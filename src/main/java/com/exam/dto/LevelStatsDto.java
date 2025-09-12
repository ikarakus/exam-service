package com.exam.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LevelStatsDto {
    private Integer id;
    private String name;
    private String code;
    private Integer duration;
    private Integer percentCompleted;
    private Integer averageScore;
    private Integer lessonCount;
    private Integer passedCount;
    private Boolean passed;
    private Integer scoreThreshold;
} 
