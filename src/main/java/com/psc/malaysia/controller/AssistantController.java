package com.psc.malaysia.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.psc.malaysia.comn.ComUtility;
import com.psc.malaysia.dto.MyResponseObject;
import com.psc.malaysia.dto.assistant.ResponseAssistant;
import com.psc.malaysia.dto.assistant.ResponseAssistantInfo;
import com.psc.malaysia.dto.assistant.ResponseThread;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/assistant")
public class AssistantController {

    @Value("${custom.chat-gpt.api-key}")
    private String apiKey;

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
/**
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("OpenAI-Beta", " assistants=v1");
        headers.put("Content-Type", "  application/json");

        HttpSession session = request.getSession();
        String assistant_thread_id = (String) session.getAttribute("assistant_thread_id");
        if(assistant_thread_id == null || assistant_thread_id.equals("")){
            HttpResponse<String> response = comUtility.httpRequestBuilder(HttpMethod.POST, assistantURL, apiKey, headers, null);
            int statusCode =  response.statusCode();
            String body = response.body();
            log.debug("0 - Response status code: " + statusCode);
            log.debug("0 - Response body: " + body);
            ObjectMapper mapper = new ObjectMapper();
            ResponseThread responseThread = mapper.readValue(body, ResponseThread.class);
            assistant_thread_id = responseThread.getId();
            session.setAttribute("assistant_thread_id", assistant_thread_id);
        }
        String assistantInfoURL = assistantURL + "/" + assistant_thread_id + "/runs";
        Map<String, String> data = new HashMap<String, String>();
        data.put("assistant_id", assistantId);

        HttpResponse<String> response = comUtility.httpRequestBuilder(HttpMethod.POST, assistantInfoURL, apiKey, headers,  data );
        int statusCode =  response.statusCode();
        String body = response.body();
        log.debug("1 -Response status code: " + statusCode);
        log.debug("1 -Response body: " + body);

        ObjectMapper mapper = new ObjectMapper();
        ResponseAssistantInfo responseAssistant =  mapper.readValue(body, ResponseAssistantInfo.class);
        log.debug(responseAssistant.toString());

        modelMap.addAttribute("instructions", comUtility.changeToWeb(responseAssistant.getInstructions()));
**/
        return "assistant/index";
    }

    @PostMapping("/send.json")
    @ResponseBody
    public ResponseAssistant send(HttpServletRequest request, @RequestBody String prompt) throws IOException, InterruptedException {







        Map<String, String> headers = new HashMap<String, String>();
        headers.put("OpenAI-Beta", " assistants=v1");
        headers.put("Content-Type", "  application/json");





        HttpResponse<String> response = comUtility.httpRequestBuilder(HttpMethod.POST, assistantURL, apiKey, headers, null);
        int statusCode =  response.statusCode();
        String body = response.body();
        log.debug("0 - Response status code: " + statusCode);
        log.debug("0 - Response body: " + body);
        ObjectMapper mapper = new ObjectMapper();
        ResponseThread responseThread = mapper.readValue(body, ResponseThread.class);
        String assistant_thread_id = responseThread.getId();















        ///HttpSession session = request.getSession();
       // String assistant_thread_id = (String) session.getAttribute("assistant_thread_id");

        String thread_assistantURL = assistantURL +"/" + assistant_thread_id +"/messages";
        Map<String, String> data = new HashMap<String, String>();
        data.put("role", "user");
        data.put("content", prompt);
        response = comUtility.httpRequestBuilder(HttpMethod.POST, thread_assistantURL, apiKey, headers,  data );
        statusCode =  response.statusCode();
        body = response.body();
        log.debug("2 -Response status code: " + statusCode);
        log.debug("2 -Response body: " + body);



        // send again a
        String assistantInfoURL = assistantURL + "/" + assistant_thread_id + "/runs";
        data = new HashMap<String, String>();
        data.put("assistant_id", assistantId);

         response = comUtility.httpRequestBuilder(HttpMethod.POST, assistantInfoURL, apiKey, headers,  data );
        statusCode =  response.statusCode();
        body = response.body();
        log.debug("3-Response status code: " + statusCode);
        log.debug("3-Response body: " + body);




        comUtility.httpRequestBuilder(HttpMethod.GET, assistantInfoURL, apiKey, headers,  null );




        comUtility.httpRequestBuilder(HttpMethod.GET, thread_assistantURL, apiKey, headers,  null );
        statusCode =  response.statusCode();
        body = response.body();
        log.debug("4 -Response status code: " + statusCode);
        log.debug("4 -Response body: " + body);

        return null;

    }
}
