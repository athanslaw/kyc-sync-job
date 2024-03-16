package com.seamfix.bioweb.microservices.syncjob.pojos;

public enum  ResponseCodeEnum {
    SUCCESS(1, "SENT"),
    FAILED(2, "FAILED"),
    ERROR(-1, "ERROR"),
    ;

    private int code;
    private String description;
    ResponseCodeEnum(int code, String status) {
        this.code = code;
        this.description = status;

    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
