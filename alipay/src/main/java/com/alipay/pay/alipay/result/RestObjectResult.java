/**
 * BSHO Engine
 */
package com.alipay.pay.alipay.result;

public class RestObjectResult<T> {

    private boolean success;

    private String  code;

    private String  message;

    private T       data;

    public RestObjectResult() {

    }
    public RestObjectResult(boolean success, String code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public RestObjectResult(T data) {
        this.success = true;
        this.data = data;
    }

    public RestObjectResult(String errorCode, String errorMessage) {
        this.success = false;
        this.code = errorCode;
        this.message = errorMessage;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
