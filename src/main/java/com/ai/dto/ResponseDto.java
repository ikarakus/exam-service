package com.ai.dto;

import java.io.Serializable;
import java.util.List;

public class ResponseDto<T> implements Serializable {
	private int resultCode = ResultCodes.OK;
	private String errorMessage;
	private String responseId;
    private List<T> responseBody;

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

	public List<T> getResponseBody() {
		return responseBody;
	}

	public void setResponseBody(List<T> responseBody) {
		this.responseBody = responseBody;
	}
}
