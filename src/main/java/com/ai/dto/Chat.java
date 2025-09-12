package com.ai.dto;

import lombok.Data;
import java.util.List;

@Data
public class Chat {

    private String model;
    private String prompt;
    private String language = "any";  // Default value "any"
    private String languageLevel = "any";  // Default value "any"
    private String topic = "any";  // Default value "any"
    private String tutor = "any";  // Default value "any"
    private List<MessageDto> pastDialogue = null;  // Default value is null
    // getters and setters
}
