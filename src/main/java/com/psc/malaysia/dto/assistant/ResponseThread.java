package com.psc.malaysia.dto.assistant;

import lombok.*;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseThread {
    private String id;
    private String object;
    private long created_at;
    private Map<String, Object> metadata;

}
