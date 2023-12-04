package com.psc.malaysia.comn;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@Slf4j
@Service
public class ComUtility {

    public String changeToWeb(String content){
        content = content.replaceAll("\n", "<br>");
        //content = "<p>" + content + "</p>";

        log.debug("==================>"+content);
        return content;
    }

    public String getCondition(){
        String condition = " ";
        return condition;
    }

    public HttpResponse<String> httpRequestBuilder(HttpMethod httpMethod, String apiURL, String apiKey, Map<String, String> headers,  Map<String, String> data) throws IOException, InterruptedException {

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
        if (apiKey != null && !apiKey.isEmpty()) {
            builder.header("Authorization", "Bearer " + apiKey);
        }
        headers.forEach(builder::header);
        HttpRequest request = builder.build();


        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }

}
