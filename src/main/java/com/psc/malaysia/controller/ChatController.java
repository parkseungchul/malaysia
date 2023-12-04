package com.psc.malaysia.controller;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.psc.malaysia.dto.ChatRequestObject;
import com.psc.malaysia.comn.ComUtility;
import com.psc.malaysia.dto.MessageObject;
import com.psc.malaysia.dto.MyResponseObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/chat")
public class ChatController {

    @Value("${custom.chat-gpt.api-key}")
    private String apiKey;

    @Value("${custom.chat-gpt.chat-url}")
    private String chatURL;

    private final ComUtility comUtility;
    public ChatController(ComUtility comUtility) {
        this.comUtility = comUtility;
    }

    @RequestMapping("/chat")
    public String testChat(ModelMap modelMap){

        return "chat/chat";
    }

    @RequestMapping("/ajax/list")
    @ResponseBody
    public List<MessageObject> ajaxList(ModelMap modelMap){
        ChatRequestObject chatRequestObject = ChatRequestObject.getInstance();
        log.debug(chatRequestObject.getMessages().toString());
        return chatRequestObject.getMessages();
    }

    @GetMapping("/ajax")
    @ResponseBody
    public void removeHistory() {
        ChatRequestObject chatRequestObject = ChatRequestObject.initInstance();
        log.debug(chatRequestObject.toString());
    }

    @PostMapping("/ajax")
    @ResponseBody
    public MyResponseObject myMethod(@RequestBody String prompt) {

        // singleton pattern
        ChatRequestObject chatRequestObject = ChatRequestObject.getInstance();
        chatRequestObject.getMessages();

        chatRequestObject.getMessages().add(new MessageObject("user",prompt));
        log.debug("=====> Request: " + chatRequestObject.toString());

        try {
            // Create an instance of ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();
            // Convert an object to a JSON string
            String jsonString = objectMapper.writeValueAsString(chatRequestObject);

            log.debug(jsonString);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(chatURL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(BodyPublishers.ofString(jsonString))
                    .build();

            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            int statusCode =  response.statusCode();
            String body = response.body();

            log.debug("Response status code: " + statusCode);
            log.debug("Response body: " + body);

            objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(body);

            MyResponseObject responseObject = new MyResponseObject();
            if(rootNode.has("error")){
                responseObject.setData("I am busy");
                return responseObject;
            }else{

                JsonNode choicesNode = rootNode.path("choices");
                JsonNode firstChoice = choicesNode.get(0);
                JsonNode messageNode = firstChoice.path("message");
                String content = messageNode.path("content").asText();

                // For Keeping History !!!
                chatRequestObject.getMessages().add(new MessageObject("assistant",content));

                long useCount = chatRequestObject.getMessages().stream().filter(s -> s.getRole().equals("user")).count();

                content = comUtility.changeToWeb(content);

                responseObject.setData(content);
                responseObject.setUserCount(useCount);
                //log.debug("Web: " + content);

                return responseObject;
            }
        } catch (Exception e) {
            e.printStackTrace();
            MyResponseObject responseObject = new MyResponseObject();
            responseObject.setData("I am busy");
            return responseObject;
        }
    }
}

