package com.x8.brick.interceptor;

import com.x8.brick.exception.HttpException;
import com.x8.brick.parameter.Request;
import com.x8.brick.parameter.Response;

public interface Interceptor<REQUEST extends Request, RESPONSE extends Response> {

    RESPONSE intercept(Chain<REQUEST, RESPONSE> chain) throws HttpException;

    interface Chain<REQUEST extends Request, RESPONSE extends Response> {
        REQUEST request();
        RESPONSE proceed(REQUEST request) throws HttpException;
    }
}
