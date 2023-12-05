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

@Slf4j
@Service
public class ComUtility {

    @Value("${custom.chat-gpt.api-key}")
    private String apiKey;

    public String changeToWeb(String content){
        content = content.replaceAll("\n", "<br>");
        return content;
    }

    public String getCondition(){
        String condition = " ";
        return condition;
    }

    public <T> T getObjectResponse(HttpResponse<String> response, String tag, Class<T> valueType) throws JsonProcessingException {
        int statusCode =  response.statusCode();
        String body = response.body();
        log.debug(tag + "- Response status code: " + statusCode);
        log.debug(tag + "- Response body: " + body);
        if(valueType == null){
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(body, valueType);
    }

    public HttpResponse<String> httpRequestBuilder(HttpMethod httpMethod, String apiURL,  Map<String, String> data) throws IOException, InterruptedException {
        log.debug("working method: "+httpMethod.toString());
        log.debug("working url: "+apiURL);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        if (httpMethod == HttpMethod.GET && data != null && !data.isEmpty()) {
            apiURL += "?" + data; // must be data query style
        }
        builder.uri(URI.create(apiURL));
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

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("OpenAI-Beta", " assistants=v1");
        headers.put("Content-Type", "  application/json");
        if (apiKey != null && !apiKey.isEmpty()) {
            builder.header("Authorization", "Bearer " + apiKey);
        }
        headers.forEach(builder::header);
        HttpRequest request = builder.build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }

}
