package com.imooc.ad.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonReponse<T> implements Serializable {

    private int code;

    private String msg;

    private T data;


    public CommonReponse(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }


}
