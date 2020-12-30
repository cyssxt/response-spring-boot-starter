package com.cyssxt.responsespringbootstarter.handlers;

import com.cyssxt.responsespringbootstarter.constant.DefaultErrorMessage;
import com.cyssxt.responsespringbootstarter.constant.ErrorMessage;
import com.cyssxt.responsespringbootstarter.exception.ErrorException;
import com.cyssxt.responsespringbootstarter.response.ResponseData;
import com.cyssxt.responsespringbootstarter.utils.ErrorUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler(ErrorException.class)
    @ResponseBody
    public ResponseEntity valid(ErrorException e) {
        ErrorMessage errorMessage = e.getErrorMessage();
        return ResponseEntity.status(errorMessage.getStatus()).body(ResponseData.fail(errorMessage));
    }

    //参数异常校验
    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseData paramError(MethodArgumentNotValidException e) {
        ResponseData responseData = ResponseData.fail(DefaultErrorMessage.PARAM_ERROR);
        responseData.setData(ErrorUtils.parseErrors(e.getBindingResult().getFieldErrors()));
        return responseData;
    }
}
