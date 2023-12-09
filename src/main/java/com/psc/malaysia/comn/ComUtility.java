package com.psc.malaysia.comn;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

// Lombok annotation for logging
@Slf4j
// Indicates that this class is a Spring service
@Service
public class ComUtility {

    // Injects the API key from the application properties
    @Value("${custom.chat-gpt.api-key}")
    private String apiKey;

    // Method to replace newline characters with HTML line breaks for web display
    public String changeToWeb(String content){
        content = content.replaceAll("\n", "<br>");
        return content;
    }

    // Method to get a condition string, currently returns a blank space
    public String getCondition(){
        String condition = " ";
        return condition;
    }

    // Generic method to convert JSON response to an object of specified type
    public <T> T getObjectResponse(HttpResponse<String> response, String tag, Class<T> valueType) throws JsonProcessingException {
        int statusCode =  response.statusCode();
        String body = response.body();
        // Logging the status code and body of the response
        log.debug(tag + "- Response status code: " + statusCode);
        log.debug(tag + "- Response body: " + body);
        if(valueType == null){
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(body, valueType);
    }

    // Method to build and execute an HTTP request
    public HttpResponse<String> httpRequestBuilder(HttpMethod httpMethod, String apiURL,  Map<String, String> data) throws IOException, InterruptedException {
        // Logging the HTTP method and URL
        log.debug("working method: "+httpMethod.toString());
        log.debug("working url: "+apiURL);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        // Appending query parameters for GET requests
        if (httpMethod == HttpMethod.GET && data != null && !data.isEmpty()) {
            apiURL += "?" + data; // Data should be formatted as a query string
        }
        builder.uri(URI.create(apiURL));
        // Handling POST requests
        if(httpMethod == HttpMethod.POST){
            if(data != null && !data.isEmpty()){
                ObjectMapper mapper = new ObjectMapper();
                String jsonString = mapper.writeValueAsString(data);
                builder.POST(HttpRequest.BodyPublishers.ofString(jsonString));
            }else{
                builder.POST(HttpRequest.BodyPublishers.noBody());
            }
        } else {
            builder.GET();
        }

        // Default headers for the request
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("OpenAI-Beta", " assistants=v1");
        headers.put("Content-Type", "  application/json");
        // Adds Authorization header if API key is available
        if (apiKey != null && !apiKey.isEmpty()) {
            builder.header("Authorization", "Bearer " + apiKey);
        }
        headers.forEach(builder::header);
        HttpRequest request = builder.build();

        // Sending the request and getting the response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }

}
