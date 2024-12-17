package com.ptit.e_commerce_website_be.do_an_nhom.models.responses;

import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.BrandDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.enums.ResultCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonResult<T> {
    private Long code;

    private String message;

    private T data;

    protected  CommonResult(){
    }

    protected CommonResult(long code, String message, T data){
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<T>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }
//    public static <T> CommonResult<T> success(String message) {
//        return new CommonResult<T>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null);
//    }

    public static <T> CommonResult<T> success(T data, String message) {
        return new CommonResult<T>(ResultCode.SUCCESS.getCode(), message, data);
    }

    public static <T> CommonResult<T> failed(String message) {
        return new CommonResult<T>(ResultCode.FAILED.getCode(), message, null);
    }

    public static <T> CommonResult<T> validateFailed(String message) {
        return new CommonResult<T>(ResultCode.VALIDATE_FAILED.getCode(), message, null);
    }

    public static <T> CommonResult<T> unauthorized(T data) {
        return new CommonResult<T>(ResultCode.UNAUTHORIZED.getCode(), ResultCode.UNAUTHORIZED.getMessage(), data);
    }

    public static <T> CommonResult<T> forbidden(T data) {
        return new CommonResult<T>(ResultCode.FORBIDDEN.getCode(), ResultCode.FORBIDDEN.getMessage(), data);
    }

    public static <T> CommonResult<T> forbidden(T data, String message) {
        return new CommonResult<T>(ResultCode.FORBIDDEN.getCode(), message, data);
    }

    public static <T> CommonResult<T> tooManyRequests(String message) {
        return new CommonResult<T>(ResultCode.TOO_MANY_REQUESTS.getCode(), message, null);
    }

    public static <T> CommonResult<T> error(int code, String message) {
        return new CommonResult<>(code, message, null);
    }
}