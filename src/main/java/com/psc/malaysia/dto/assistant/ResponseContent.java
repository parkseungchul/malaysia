package com.psc.malaysia.dto.assistant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseContent {
    private String id;
    private String object;
    private long created_at;
    private String thread_id;
    private String role;
    private List<Content> content;
    private List<String> file_ids;
    private String assistant_id;
    private String run_id;
    private Metadata metadata;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Content {
        private String type;
        private Text text;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Text {
            private String value;
            private List<Annotation> annotations;
        }
    }


    public static class Annotation {
        // Define annotation properties
    }


    public static class Metadata {
        // Define metadata properties
    }
}
