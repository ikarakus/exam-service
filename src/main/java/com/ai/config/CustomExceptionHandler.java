package com.ai.config;

import com.ai.dto.ResponseDto;
import com.ai.util.Helper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    public final ResponseEntity<?> Exception(Exception ex)
    {
        ResponseDto<?> responseDto = new ResponseDto<>();
        Helper.handleException(responseDto, ex);
        return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
    }

}
