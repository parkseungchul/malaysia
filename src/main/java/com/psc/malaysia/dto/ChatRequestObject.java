package com.psc.malaysia.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ChatRequestObject {
    private static ChatRequestObject instance;
    private ChatRequestObject() {
    }
    public static ChatRequestObject initInstance(){
        instance = new ChatRequestObject();
        instance.setModel("gpt-4");
        instance.setTemperature(1);
        instance.setMax_tokens(1024);
        instance.setTop_p(1.0);
        instance.setFrequency_penalty(0);
        instance.setPresence_penalty(0);
        instance.messages = new ArrayList<MessageObject>();
        return instance;
    }
    public static ChatRequestObject getInstance() {
        if (instance == null) {
            instance = new ChatRequestObject();
            instance.setModel("gpt-3.5-turbo");
            instance.setTemperature(1);
            instance.setMax_tokens(1024);
            instance.setTop_p(1.0);
            instance.setFrequency_penalty(0);
            instance.setPresence_penalty(0);
            instance.messages = new ArrayList<MessageObject>();
        }
        return instance;
    }
    private String model;
    private List<MessageObject> messages;
    private double temperature;
    private int max_tokens;
    private double top_p;
    private double frequency_penalty;
    private double presence_penalty;


    @Override
    public String toString() {
        return "ChatRequestObject{" +
                "model='" + model + '\'' +
                ", messages =" + messages.toString()+
                ", temperature=" + temperature +
                ", max_tokens=" + max_tokens +
                ", top_p=" + top_p +
                ", frequency_penalty=" + frequency_penalty +
                ", presence_penalty=" + presence_penalty +
                '}';
    }
}
