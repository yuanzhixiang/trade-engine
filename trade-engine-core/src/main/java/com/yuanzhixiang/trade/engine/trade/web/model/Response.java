package com.yuanzhixiang.trade.engine.trade.web.model;

/**
 * @author ZhiXiang Yuan
 * @date 2020/09/11 21:26
 */
public class Response<T> {

    /** 是否成功 */
    private boolean success;
    /** 错误编码 */
    private String errorCode;
    /** 错误信息 */
    private String errorMessage;
    /** 返回内容 */
    private T content;

    public static Response<Void> success() {
        return Response.success(null);
    }

    public static <T> Response<T> success(T content) {
        Response<T> response = new Response<>();
        response.content = content;
        response.success = true;
        return response;
    }

    public static <T> Response<T> error(String errorMessage) {
        Response<T> response = new Response<>();
        response.errorMessage = errorMessage;
        response.success = false;
        return response;
    }

    public Response<T> setContent(T content) {
        this.content = content;
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public T getContent() {
        return content;
    }
}
