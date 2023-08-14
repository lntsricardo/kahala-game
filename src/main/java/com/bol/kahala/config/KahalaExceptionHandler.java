package com.bol.kahala.config;

import com.bol.kahala.dto.KahalaErrorDTO;
import com.bol.kahala.exception.KahalaException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class KahalaExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<?> handleIllegalArgumentException(Exception ex, WebRequest webRequest) {
        KahalaErrorDTO dto = new KahalaErrorDTO(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return new ResponseEntity<>(dto, new HttpHeaders(), dto.status());
    }

    @ExceptionHandler({KahalaException.class})
    public ResponseEntity<?> handleKahalaException(Exception ex, WebRequest webRequest) {
        KahalaErrorDTO dto = new KahalaErrorDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        return new ResponseEntity<>(dto, new HttpHeaders(), dto.status());
    }
}
