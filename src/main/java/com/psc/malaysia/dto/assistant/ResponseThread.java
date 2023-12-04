package com.psc.malaysia.dto.assistant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ResponseThread {

    private String id;
    private String object;
    private long created_at;
    private Map<String, Object> metadata;

    public ResponseThread(){

    }
}
