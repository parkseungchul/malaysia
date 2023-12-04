package com.psc.malaysia.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class ChatResponseObject {
    private String id;
    private String object;
    private long created;
    private String model;
    private List<Choice> choices;
    private Usage usage;

    // 생성자, getter 및 setter 메서드

    public static class Choice {
        private int index;
        private Message message;
        private String finish_reason;

        // Choice 클래스의 생성자, getter 및 setter 메서드

        public static class Message {
            private String role;
            private String content;

            // Message 클래스의 생성자, getter 및 setter 메서드
        }
    }

    public static class Usage {
        private int prompt_tokens;
        private int completion_tokens;
        private int total_tokens;

        // Usage 클래스의 생성자, getter 및 setter 메서드
    }
}