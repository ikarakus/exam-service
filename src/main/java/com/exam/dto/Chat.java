package com.exam.dto;

import lombok.Data;
import java.util.List;

@Data
public class Chat {

    private String model;
    private String prompt;
    private String language = "any";  // Default value "any" - should be full language names like "English", "Spanish"
    private String languageLevel = "any";  // Default value "any" - should be CEFR levels like "A1", "B2", "C1"
    private String topic = "any";  // Default value "any"
    private String tutor = "any";  // Default value "any"
    private List<MessageDto> pastDialogue = null;  // Default value is null
    private Boolean firstMessage = false;  // Default value false
    private Long userId = null;  // Default value null
    private Long tutorId = null;  // Default value null
    private Long lessonId = null;  // Default value null
    private Long levelId = null;  // Default value null
    // getters and setters
}
