package com.cyssxt.responsespringbootstarter.constant;

public enum DefaultErrorMessage implements ErrorMessage {
    SUCCESS(0,"success",200),
    PARAM_ERROR(5000000, "参数错误"),
    AUTHORIZATION_ERROR(8000000, "授权失败"),
    SHOULD_LOGIN(8000001, "请先登录"),
    USER_ID_NOT_NULL(8000002, "用户标识不存在"),
    TOKEN_NOT_VALID(8000003, "token无效"),
    SYSTEM_ERROR(9999999, "系统异常");
    private int retCode;
    private String retMsg;
    private int status;

    DefaultErrorMessage(int retCode,String retMsg) {
        this(retCode,retMsg,500);
    }

    DefaultErrorMessage(int retCode,String retMsg,int status) {
        this.retCode = retCode;
        this.retMsg = retMsg;
        this.status = status;
    }


    public void setRetCode(int retCode) {
        this.retCode = retCode;
    }

    public void setRetMsg(String retMsg) {
        this.retMsg = retMsg;
    }

    @Override
    public int getRetCode() {
        return retCode;
    }

    @Override
    public String getRetMsg() {
        return retMsg;
    }

    @Override
    public int getStatus() {
        return status;
    }
}
