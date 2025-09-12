package com.exam.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class CoursePackageDto {
    private Integer mins;
    private Integer days;
    private Integer month;
    private Boolean active;
    private Integer packageId;
    private List<PriceValue> values;

    @Data
    public static class PriceValue {
        @JsonProperty("key")
        private String key;
        
        @JsonProperty("amount")
        private Double amount;
        
        @JsonProperty("exchangeRate")
        private Double exchangeRate;
        
        @JsonProperty("discountPercent")
        private Integer discountPercent;
    }
} 
