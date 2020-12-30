package com.cyssxt.responsespringbootstarter.exception;

import com.cyssxt.responsespringbootstarter.constant.ErrorMessage;
import lombok.Data;

import java.util.List;

@Data
public class ErrorException extends Exception{
    ErrorMessage errorMessage;
    List<String> errors;

    public ErrorException(ErrorMessage errorMessage, List<String> errors) {
        this.errorMessage = errorMessage;
        this.errors = errors;
    }

    public ErrorException(ErrorMessage errorMessage) {
        this(errorMessage,null);
    }
}
