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

// Lombok annotation for logging
@Slf4j
// Annotation to declare this class as a Spring MVC Controller
@Controller
// Base URL mapping for this controller
@RequestMapping("/assistant")
public class AssistantController {

    // Injecting values from application properties
    @Value("${custom.chat-gpt.assistant-id}")
    private String assistantId;

    @Value("${custom.chat-gpt.assistant-url}")
    private String assistantURL;

    // Dependency injection of ComUtility
    private final ComUtility comUtility;
    public AssistantController(ComUtility comUtility) {
        this.comUtility = comUtility;
    }

    // Mapping for the main page of the assistant
    @RequestMapping("/assistant")
    public String indexAssistant(HttpServletRequest request, ModelMap modelMap) throws IOException, InterruptedException {

        // Creating a new session and setting an attribute
        HttpSession httpSession = request.getSession();
        httpSession.setAttribute("assistant_thread_id", "");

        // Preparing and making an HTTP request to get a new thread ID
        HttpResponse<String> response = comUtility.httpRequestBuilder(HttpMethod.POST, assistantURL, null);
        ResponseThread responseThread = comUtility.getObjectResponse(response, "get thread", ResponseThread.class);
        String assistant_thread_id = responseThread.getId();

        // Building the URL to get assistant info and making the request
        String assistantInfoURL = assistantURL + "/" + assistant_thread_id + "/runs";
        Map<String, String> data = new HashMap<>();
        data.put("assistant_id", assistantId);

        response = comUtility.httpRequestBuilder(HttpMethod.POST, assistantInfoURL,  data);
        ResponseAssistantInfo responseAssistant = comUtility.getObjectResponse(response, "Get Assistant", ResponseAssistantInfo.class);
        log.debug(responseAssistant.toString());

        // Adding the instructions to the modelMap after converting them for web
        modelMap.addAttribute("instructions", comUtility.changeToWeb(responseAssistant.getInstructions()));

        // Finalizing the request
        comUtility.httpRequestBuilder(HttpMethod.GET, assistantInfoURL, null);
        return "assistant/index";
    }

    // Endpoint to handle sending of user input and receiving response
    @PostMapping("/send.json")
    @ResponseBody
    public ResponseObject send(HttpServletRequest request, @RequestBody String prompt) {

        HttpResponse<String> response;
        Map<String, String> data;
        ResponseObject responseObject = new ResponseObject();

        try {
            // Step 1: Creating a new thread if necessary
            HttpSession httpSession = request.getSession();
            String assistant_thread_id = (String) httpSession.getAttribute("assistant_thread_id");
            if (assistant_thread_id == null || assistant_thread_id.isEmpty()) {
                response = comUtility.httpRequestBuilder(HttpMethod.POST, assistantURL, null);
                ResponseThread responseThread = comUtility.getObjectResponse(response, "get thread", ResponseThread.class);
                assistant_thread_id = responseThread.getId();
                httpSession.setAttribute("assistant_thread_id", assistant_thread_id);
            }

            // Step 2: Sending user input to the assistant
            String threadAssistantURL = assistantURL + "/" + assistant_thread_id + "/messages";
            data = new HashMap<>();
            data.put("role", "user");
            data.put("content", prompt);
            response = comUtility.httpRequestBuilder(HttpMethod.POST, threadAssistantURL, data);
            ResponseContent responseContent = comUtility.getObjectResponse(response, "Send Data", ResponseContent.class);

            // Step 3: Requesting assistant's response
            String assistantInfoURL = assistantURL + "/" + assistant_thread_id + "/runs";
            data = new HashMap<>();
            data.put("assistant_id", assistantId);
            response = comUtility.httpRequestBuilder(HttpMethod.POST, assistantInfoURL, data);
            ResponseAssistantInfo responseAssistant = comUtility.getObjectResponse(response, "Get Assistant", ResponseAssistantInfo.class);
            String runStatusURL = assistantInfoURL + "/" + responseAssistant.getId();

            // Step 4: Checking the run status of the assistant
            boolean isQueued = false;
            while (true) {
                response = comUtility.httpRequestBuilder(HttpMethod.GET, runStatusURL, null);
                String query = "$.status";

                String status = JsonPath.read(response.body(), query);
                if ("completed".equals(status)) {
                    break;
                } else if ("in_progress".equals(status)) {
                    Thread.sleep(5000); // Wait before checking the status again
                } else {
                    isQueued = true;
                    break;
                }
            }

            // Handling if the response is queued
            if (isQueued) {
                throw new Exception("Queueing");
            }

            // Step 6: Retrieving the assistant's message
            String messageAssistantURL = assistantURL + "/" + assistant_thread_id + "/messages";
            response = comUtility.httpRequestBuilder(HttpMethod.GET, messageAssistantURL, null);
            String jsonResult = response.body();

            String messageQuery = "$.data[?(@.role == 'assistant')].content[0].text.value";
            Object result = JsonPath.read(jsonResult, messageQuery);
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

            // Finalizing the request
            comUtility.httpRequestBuilder(HttpMethod.GET, assistantInfoURL, null);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                Thread.sleep(7000); // Waiting before returning the response
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            } finally {
                // Returning an error message if an exception occurs
                return new ResponseObject("Server is busy or Refresh F5");
            }
        }
        return responseObject;
    }

}
