package com.psc.malaysia.controller;

import com.psc.malaysia.comn.ComUtility;
import com.psc.malaysia.dto.assistant.ResponseAssistantInfo;
import com.psc.malaysia.dto.assistant.ResponseContent;
import com.psc.malaysia.dto.assistant.ResponseObject;
import com.psc.malaysia.dto.assistant.ResponseThread;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import com.jayway.jsonpath.JsonPath;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/assistant")
public class AssistantController {

    @Value("${custom.chat-gpt.assistant-id}")
    private String assistantId;

    @Value("${custom.chat-gpt.assistant-url}")
    private String assistantURL;

    private final ComUtility comUtility;
    public AssistantController(ComUtility comUtility) {
        this.comUtility = comUtility;
    }

    @RequestMapping("/assistant")
    public String indexAssistant(HttpServletRequest request, ModelMap modelMap) throws IOException, InterruptedException {

        HttpSession  httpSession =  request.getSession();
        httpSession.setAttribute("assistant_thread_id", "");


        Map<String, String> data = null;
        HttpResponse<String> response = comUtility.httpRequestBuilder(HttpMethod.POST, assistantURL, null);
        ResponseThread responseThread = comUtility.getObjectResponse(response, "get thread", ResponseThread.class);
        String assistant_thread_id = responseThread.getId();

        String assistantInfoURL = assistantURL + "/" + assistant_thread_id + "/runs";
        data = new HashMap<String, String>();
        data.put("assistant_id", assistantId);

        response = comUtility.httpRequestBuilder(HttpMethod.POST, assistantInfoURL,  data);
        ResponseAssistantInfo responseAssistant = comUtility.getObjectResponse(response, "Get Assistant", ResponseAssistantInfo.class);
        log.debug(responseAssistant.toString());

        modelMap.addAttribute("instructions", comUtility.changeToWeb(responseAssistant.getInstructions()));

        // finished
        comUtility.httpRequestBuilder(HttpMethod.GET, assistantInfoURL, null);
        return "assistant/index";
    }

    @PostMapping("/send.json")
    @ResponseBody
    public ResponseObject send(HttpServletRequest request, @RequestBody String prompt)  {

        HttpResponse<String> response = null;
        Map<String, String> data = null;
        ResponseObject responseObject = new ResponseObject();

        try{
            // 1. create Thread
            HttpSession  httpSession =  request.getSession();
            String assistant_thread_id = (String)httpSession.getAttribute("assistant_thread_id");
            if(assistant_thread_id == null|| assistant_thread_id.equals("")){
                response = comUtility.httpRequestBuilder(HttpMethod.POST, assistantURL, null);
                ResponseThread responseThread = comUtility.getObjectResponse(response, "get thread", ResponseThread.class);
                assistant_thread_id = responseThread.getId();
                httpSession.setAttribute("assistant_thread_id", assistant_thread_id);
            }

            // 2. Sent user data
            String thread_assistantURL = assistantURL +"/" + assistant_thread_id +"/messages";
            data = new HashMap<String, String>();
            data.put("role", "user");
            data.put("content", prompt);
            response = comUtility.httpRequestBuilder(HttpMethod.POST, thread_assistantURL,  data );
            ResponseContent responseContent = comUtility.getObjectResponse(response, "Send Data", ResponseContent.class);
            String messageId = responseContent.getId();

            // 3. send assistant info
            String assistantInfoURL = assistantURL + "/" + assistant_thread_id + "/runs"; // /v1/threads/thread_4DQN8y3zcRlwOxUds0DgrRZp/runs
            data = new HashMap<String, String>();
            data.put("assistant_id", assistantId);
            response = comUtility.httpRequestBuilder(HttpMethod.POST, assistantInfoURL,  data );
            ResponseAssistantInfo responseAssistant = comUtility.getObjectResponse(response, "Get Assistant", ResponseAssistantInfo.class);
            String runStatusURL = assistantInfoURL + "/" + responseAssistant.getId(); //  /v1/threads/thread_4DQN8y3zcRlwOxUds0DgrRZp/runs/run_JhX5VwL7Hrx6SmsTvIjs6JGc

            // 4. Run status
            boolean isQueued = false;
            while(true){
                response = comUtility.httpRequestBuilder(HttpMethod.GET, runStatusURL,  null );
                String query = "$.status";

                String status = JsonPath.read(response.body(), query);
                log.debug("=====> Status: " + status);
                if(status.equals("completed")){
                    break;
                }else if(status.equals("in_progress")){
                    Thread.sleep(5000);
                }else{
                    isQueued = true;
                    break;
                }
            }

            if(isQueued){
                return new ResponseObject("Server is busy or need to refresh F5");
            }

            // 6. Get Message
            String message_assistantURL = assistantURL +"/" + assistant_thread_id +"/messages";
            response = comUtility.httpRequestBuilder(HttpMethod.GET, message_assistantURL,  null );
            String jsonResult = response.body();

            String query = "$.data[?(@.role == 'assistant')].content[0].text.value";
            Object result = JsonPath.read(jsonResult, query);
            if (result instanceof JSONArray) {
                JSONArray resultArray = (JSONArray) result;
                if (!resultArray.isEmpty()) {
                    String value = resultArray.get(0).toString();
                    responseObject.setData(comUtility.changeToWeb(value));
                }
            } else if (result instanceof String) {
                String value = (String) result;
                responseObject.setData(comUtility.changeToWeb(value));
            }

            // finished
            comUtility.httpRequestBuilder(HttpMethod.GET, assistantInfoURL, null);
        }catch(Exception e){
            e.printStackTrace();
            try {
                Thread.sleep(7000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }finally {
                return new ResponseObject("Server is busy or Refresh F5");
            }
        }



        return responseObject;
    }

}
