package com.imooc.ad.search.vo.media;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Device {

    private String deviceCode;

    private String mac;

    private String ip;

    private String model;

    private String displaySize;

    private String screenSize;

    private String serialName;


}
