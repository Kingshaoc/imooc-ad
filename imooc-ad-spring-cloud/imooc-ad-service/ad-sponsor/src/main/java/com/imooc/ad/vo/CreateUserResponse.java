package com.imooc.ad.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserResponse {

    private long userId;

    private String username;

    private String token;

    private Date createTime;

    private Date updateTime;


}
