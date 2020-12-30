package com.cyssxt.responsespringbootstarter.aop;

public interface UserHandler {
    String getUserId(String token);

    UserInfo findById(String userId);
}
