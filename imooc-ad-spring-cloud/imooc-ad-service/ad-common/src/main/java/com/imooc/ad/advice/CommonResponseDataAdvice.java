package com.imooc.ad.advice;

import com.imooc.ad.annotation.IngoreResponseAdvice;
import com.imooc.ad.vo.CommonReponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class CommonResponseDataAdvice implements ResponseBodyAdvice {
    @Override
    @SuppressWarnings("all")
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        //如果类被IngoreResponseAdvice所标识
        if (methodParameter.getDeclaringClass().isAnnotationPresent(IngoreResponseAdvice.class)) {
            return false;
        }
        //如果方法被IngoreResponseAdvice所标识
        if (methodParameter.getMethod().isAnnotationPresent(IngoreResponseAdvice.class)) {
            return false;
        }
        return true;
    }

    @Override
    @SuppressWarnings("all")
    public Object beforeBodyWrite(Object o,
                                  MethodParameter methodParameter,
                                  MediaType mediaType, Class aClass,
                                  ServerHttpRequest serverHttpRequest,
                                  ServerHttpResponse serverHttpResponse) {
        CommonReponse<Object> response = new CommonReponse<>(0, "ok");
        if (o == null) {
            return response;
        }else if (o instanceof CommonReponse) {
            response = (CommonReponse<Object>)o;
        }else {
            response.setData(o);
        }
        return response;
    }
}
