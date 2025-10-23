package com.exam.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimplePdfProcessingRequestDto {
    
    @NotBlank(message = "Course language is required")
    private String courseLang;
    
    @NotNull(message = "Level ID is required")
    private Integer levelId;
    
    @NotNull(message = "Lesson ID is required")
    private Integer lessonId;
}
