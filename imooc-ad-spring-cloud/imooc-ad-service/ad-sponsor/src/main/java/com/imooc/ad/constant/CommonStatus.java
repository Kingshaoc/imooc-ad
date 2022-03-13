package com.imooc.ad.constant;

public enum CommonStatus {

    VALID(1, "有效状态"),
    INVALID(1, "无效状态");

    private Integer status;

    private String desc;

    CommonStatus(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
