package com.x8.brick.interceptor;

import android.support.annotation.NonNull;

import com.x8.brick.exception.HttpException;
import com.x8.brick.parameter.Request;
import com.x8.brick.parameter.Response;

import java.util.List;

public class InterceptorChain<REQUEST extends Request, RESPONSE extends Response>
        implements Interceptor.Chain<REQUEST, RESPONSE> {

    private REQUEST request;
    private Executor<REQUEST, RESPONSE> executor;
    private List<Interceptor<REQUEST, RESPONSE>> interceptors;

    public InterceptorChain(REQUEST request, @NonNull Executor<REQUEST, RESPONSE> executor,
            List<Interceptor<REQUEST, RESPONSE>> interceptors) {
        this.request = request;
        this.executor = executor;
        this.interceptors = interceptors;
    }

    @Override
    public REQUEST request() {
        return request;
    }

    @Override
    public RESPONSE proceed(REQUEST request) throws HttpException {
        if (interceptors != null && interceptors.size() > 0) {
            Interceptor<REQUEST, RESPONSE> interceptor = interceptors.get(0);
            InterceptorChain<REQUEST, RESPONSE> next = new InterceptorChain<>(
                    request, executor, interceptors.subList(1, interceptors.size()));
            return interceptor.intercept(next);
        }
        return executor.execute(request);
    }

    public interface Executor<REQUEST extends Request, RESPONSE extends Response> {
        RESPONSE execute(REQUEST request) throws HttpException;
    }
}
