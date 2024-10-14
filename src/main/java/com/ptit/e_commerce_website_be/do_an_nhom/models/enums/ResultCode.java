package com.ptit.e_commerce_website_be.do_an_nhom.models.enums;


public enum ResultCode {
    SUCCESS(200, "Success"),
    FAILED(500, "Server Error"),
    VALIDATE_FAILED(404, "Not found"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    TOO_MANY_REQUESTS(429, "Too many requests");

    private Long code;

    private String message;

    private ResultCode(long code, String message) {
        this.code = code;
        this.message = message;
    }

    public Long getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
