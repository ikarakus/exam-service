package com.exam.dto;

import java.io.Serializable;

public class LanguageLevelEvaluationResponse implements Serializable {
    private int resultCode;
    private String errorMessage;
    private String responseId;
    private LanguageLevelResponseDto responseBody;

    // Getters and setters
    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getResponseId() {
        return responseId;
    }

    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }

    public LanguageLevelResponseDto getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(LanguageLevelResponseDto responseBody) {
        this.responseBody = responseBody;
    }
}
