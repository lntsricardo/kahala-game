package com.bol.kahala.exception;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@JsonIgnoreProperties({"cause", "stackTrace", "localizedMessage","suppressed"})
@Getter
public class KahalaException extends RuntimeException {

    private String message;

    public KahalaException(String message){
        super(message);
    }

}
