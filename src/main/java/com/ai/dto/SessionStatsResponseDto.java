package com.ai.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionStatsResponseDto {
    private List<LevelStatsDto> levels;
    private List<AssessmentStatsDto> assessments;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssessmentStatsDto {
        private Timestamp updatedDatetime;
        private Integer score;
        private Integer speakingScore;
        private Integer totalScore;
        private String userLevel;
        private Boolean speakingInsufficientData;
    }
} 