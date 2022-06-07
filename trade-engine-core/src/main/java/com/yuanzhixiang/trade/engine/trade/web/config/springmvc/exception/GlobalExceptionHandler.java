package com.yuanzhixiang.trade.engine.trade.web.config.springmvc.exception;

import com.yuanzhixiang.trade.engine.trade.web.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

/**
 * @author zhixiang.yuan
 * @since 2021/02/15 12:41:32
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({WebExchangeBindException.class})
    public Response<String> validatorException(WebExchangeBindException exception) {
        log.error("", exception);
        return Response.error(exception.getBindingResult().getFieldError().getDefaultMessage());
    }

    @ExceptionHandler({BindException.class})
    public Response<String> validatorException(BindException exception) {
        String errorMessage = exception.getBindingResult().getFieldError().getDefaultMessage();
        log.error(errorMessage);
        return Response.error(errorMessage);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public Response<String> validatorException(MethodArgumentNotValidException exception) {
        String errorMessage = exception.getBindingResult().getFieldError().getDefaultMessage();
        log.error(errorMessage);
        return Response.error(errorMessage);
    }

    @ExceptionHandler(Exception.class)
    public Response<String> exception(Exception exception) {
        log.error("", exception);
        return Response.error("系统出现异常：[" + exception.getMessage() + "]");
    }
}
