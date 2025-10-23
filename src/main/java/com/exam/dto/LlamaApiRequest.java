package com.exam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LlamaApiRequest {

    private String model;
    private String prompt;
    private boolean stream = false;
    private String format;
    private Object options;
    
    public LlamaApiRequest(String model, String prompt) {
        this.model = model;
        this.prompt = prompt;
        this.stream = false;
    }
}
