package com.exam.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PassedLessonDto {
    private Integer id;
    private String name;
    private String description;
    private Integer score;
} 
