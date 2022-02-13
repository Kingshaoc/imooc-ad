package com.imooc.ad.advice;

import com.imooc.ad.exception.AdException;
import com.imooc.ad.vo.CommonReponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler(value = AdException.class)
    public CommonReponse<String> handlerAdException(HttpServletRequest req, AdException e) {
        CommonReponse<String> reponse = new CommonReponse<>(-1, "business error");
        reponse.setData(e.getMessage());
        return  reponse;
    }
}
