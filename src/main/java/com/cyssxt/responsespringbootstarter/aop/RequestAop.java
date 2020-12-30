package com.cyssxt.responsespringbootstarter.aop;

import com.cyssxt.responsespringbootstarter.annotations.Alias;
import com.cyssxt.responsespringbootstarter.annotations.Authorization;
import com.cyssxt.responsespringbootstarter.constant.DefaultErrorMessage;
import com.cyssxt.responsespringbootstarter.exception.ErrorException;
import com.cyssxt.responsespringbootstarter.utils.ErrorUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Aspect
@Component
@Slf4j
public class RequestAop {

    private static final String USER_TYPE = "userType";
    private static final String USER_ID = "userId";
    private static final String USER = "user";
    public static ThreadLocal<String> CURRENT_USER = new ThreadLocal<>();
//    private static final String SIGN_KEY = "signKey";


    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)||@annotation(org.springframework.web.bind.annotation.GetMapping)||@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void pointCut() {
    }

    @Resource
    UserHandler userHandler;

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint pjp) throws ErrorException {
        CURRENT_USER.set(null);//防止线程重用 导致线程数据错乱
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Object[] objects = pjp.getArgs();
        Method method = methodSignature.getMethod();
        Class parent = method.getDeclaringClass();
        Object object = pjp.getTarget();
        boolean flag = false;
        Authorization  authorization = object.getClass().getAnnotation(Authorization.class);
        //使用类上加注解代表所有类需要授权

        log.debug("params={},method={}", objects, method.getName());
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        Byte userType = null;
        String userId;
        String token = null;
        UserInfo userInfo;
        if (requestAttributes != null) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            token = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (!StringUtils.isEmpty(token)) {
                if (!token.startsWith("Bearer ")) {
                    token=null;
                } else {
                    token = token.replace("Bearer ", "").trim();
                    if (StringUtils.isEmpty(token)) {
                        throw new ErrorException(DefaultErrorMessage.AUTHORIZATION_ERROR);
                    }
                }
            }
        }
        log.debug("token={}", token);
        if (authorization == null) {
            authorization = method.getDeclaredAnnotation(Authorization.class);
        }

        Parameter[] parameters = method.getParameters();
        int length = parameters.length;
        for (int i = 0; i < length; i++) {
            Object param = objects[i];
            if (param instanceof BaseReq && !StringUtils.isEmpty(token)) {
                ((BaseReq) param).setToken(token);
            }
            if (param instanceof BindingResult) {
                ErrorUtils.checkErrors((BindingResult)param);
                break;
            }
        }
        if (authorization != null ) {
            if (StringUtils.isEmpty(token)) {
                throw new ErrorException(DefaultErrorMessage.SHOULD_LOGIN);
            }
            userId = userHandler.getUserId(token);
            if (StringUtils.isEmpty(userId) ) {
                throw new ErrorException(DefaultErrorMessage.USER_ID_NOT_NULL);
            }
            CURRENT_USER.set(userId);
            userInfo = userHandler.findById(userId);
            for (int j = 0; j < length; j++) {
                Parameter parameter = parameters[j];
                String name = parameter.getName();
                Alias alias = parameter.getAnnotation(Alias.class);
                if (alias != null) {
                    name = alias.value();
                }
                if (USER_TYPE.equals(name)) {
                    objects[j] = userType;
                } else if (USER_ID.equals(name)) {
                    objects[j] = userId;
                } else if (USER.equals(name)) {
                    objects[j] = userInfo;
                }
            }
            if (userInfo == null) {
                throw new ErrorException(DefaultErrorMessage.TOKEN_NOT_VALID);
            }
        }
        try {
            return pjp.proceed(objects);
        } catch (Throwable throwable) {
            log.error("proceed={}", throwable);
            if (throwable instanceof ErrorException) {
                throw (ErrorException) throwable;
            }
            throw new ErrorException(DefaultErrorMessage.SYSTEM_ERROR);
        }
    }

}
