package com.imooc.ad.index;

public enum CommonStatus {

    VALID(1, "有效状态"),
    INVALID(0, "无效状态");

    private int status;

    private String desc;

    CommonStatus(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
