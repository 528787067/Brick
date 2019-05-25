package com.x8.brick.exception;

public class HttpException extends Throwable {

    public HttpException() {
        super();
    }

    public HttpException(String message) {
        super(message);
    }

    public HttpException(Throwable cause) {
        super(cause);
    }

    public HttpException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
