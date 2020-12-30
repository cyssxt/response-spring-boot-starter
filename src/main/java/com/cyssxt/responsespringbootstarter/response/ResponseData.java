package com.cyssxt.responsespringbootstarter.response;

import com.cyssxt.responsespringbootstarter.constant.ErrorMessage;
import lombok.Data;

@Data
public class ResponseData<T> {
    private int retCode;
    private String retMsg;
    private T data;

    public static ResponseData fail(ErrorMessage errorMessage){
        ResponseData responseData = new ResponseData();
        responseData.setRetCode(errorMessage.getRetCode());
        responseData.setRetMsg(errorMessage.getRetMsg());
        return responseData;
    }

    public static <T> ResponseData<T> success(){
        return ResponseData.success(null);
    }
    public static <T> ResponseData<T> success(T t){
        ResponseData responseData = new ResponseData();
        responseData.setData(t);
        return responseData;
    }
}
