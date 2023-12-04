package com.psc.malaysia.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MessageObject {

    public MessageObject(){}

    public MessageObject(String role, String content){
        this.role = role;
        this.content = content;
    }
    private String role;
    private String content;


}
