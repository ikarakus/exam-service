package com.exam.dto;

import lombok.Data;
import java.util.List;

@Data
public class SessionTestQuestionDto {
    private String questionText;
    private List<Option> options;
    private String correctLabel;
    private String selectedLabel;
    private Integer questionIndex;
    private String explanation;
    private Long id;

    @Data
    public static class Option {
        private String label;
        private String text;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
} 
