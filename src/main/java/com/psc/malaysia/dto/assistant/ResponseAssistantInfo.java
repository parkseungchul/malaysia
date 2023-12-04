package com.psc.malaysia.dto.assistant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ResponseAssistantInfo {
    private String id;
    private String object;
    private long created_at;
    private String assistant_id;
    private String thread_id;
    private String status;
    private Long started_at; // 이러한 필드들은 null이 될 수 있으므로 Long 등의 객체 타입을 사용합니다.
    private long expires_at;
    private Long cancelled_at;
    private Long failed_at;
    private Long completed_at;
    private String last_error;
    private String model;
    private String instructions;
    private List<String> tools;
    private List<String> file_ids;
    private Map<String, Object> metadata;

    public ResponseAssistantInfo(){}
}
