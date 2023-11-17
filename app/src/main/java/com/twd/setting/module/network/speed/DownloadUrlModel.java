package com.twd.setting.module.network.speed;

public class DownloadUrlModel {
    private int code;
    private String data;
    private String msg;
    private String reqId;

    public int getCode() {
        return this.code;
    }

    public String getData() {
        return this.data;
    }

    public String getMsg() {
        return this.msg;
    }

    public String getReqId() {
        return this.reqId;
    }

    public void setCode(int paramInt) {
        this.code = paramInt;
    }

    public void setData(String paramString) {
        this.data = paramString;
    }

    public void setMsg(String paramString) {
        this.msg = paramString;
    }

    public void setReqId(String paramString) {
        this.reqId = paramString;
    }
}

