package com.x8.brick.task;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.x8.brick.adapter.RequestAdapter;
import com.x8.brick.adapter.ResponseAdapter;
import com.x8.brick.core.HttpManager;
import com.x8.brick.core.RequestModel;
import com.x8.brick.exception.HttpException;
import com.x8.brick.filter.FilterChain;
import com.x8.brick.filter.RequestFilter;
import com.x8.brick.filter.ResponseFilter;
import com.x8.brick.interceptor.Interceptor;
import com.x8.brick.interceptor.InterceptorChain;
import com.x8.brick.parameter.Request;
import com.x8.brick.parameter.Response;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public abstract class TaskModel<REQUEST extends Request, RESPONSE extends Response> {

    private HttpManager<REQUEST, RESPONSE> httpManager;
    private RequestModel requestModel;
    private Type taskType;
    private REQUEST request;

    protected TaskModel(HttpManager<REQUEST, RESPONSE> httpManager, RequestModel requestModel, Type taskType) {
        this.httpManager = httpManager;
        this.requestModel = requestModel;
        this.taskType = taskType;
    }

    public HttpManager<REQUEST, RESPONSE> httpManager() {
        return httpManager;
    }

    public RequestModel requestModel() {
        return requestModel;
    }

    public Type taskType() {
        return taskType;
    }

    public Type responseType() {
        if (taskType instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) taskType).getRawType();
            Type[] types = ((ParameterizedType) taskType).getActualTypeArguments();
            if (rawType == Task.class && types.length == 3) {
                return types[2];
            }
            if (rawType != Task.class && types.length == 1) {
                return types[0];
            }
        }
        return Object.class;
    }

    public synchronized REQUEST request() {
        if (request == null) {
            RequestAdapter requestAdapter = httpManager.requestAdapter();
            if (requestModel != null && requestAdapter != null) {
                List<?> requestBodys = requestModel.bodys();
                if (requestBodys.size() > 0) {
                    List<?> bodys = new ArrayList<>(requestBodys);
                    requestBodys.clear();
                    for (Object body : bodys) {
                        Type bodyType = body.getClass();
                        // noinspection unchecked
                        Object requestBody = requestAdapter.adapt(body, bodyType);
                        requestModel.addBody(requestBody);
                    }
                }
                List<RequestModel.Part> parts = requestModel.parts();
                if (parts.size() > 0) {
                    List<RequestModel.Part> partDatas = new ArrayList<>(parts);
                    parts.clear();
                    for (RequestModel.Part partData : partDatas) {
                        Type partDataType = partData.getClass();
                        // noinspection unchecked
                        Object part = requestAdapter.adapt(partData.data, partDataType);
                        requestModel.addPart(partData.name, partData.encoding, part);
                    }
                }
            }
            RequestModelChecker requestModelChecker = requestModelChecker();
            if (requestModelChecker != null) {
                requestModel = requestModelChecker.checkRequestModel(requestModel);
            }
            RequestGenerator<REQUEST> requestGenerator = requestGenerator();
            // noinspection ConstantConditions
            if (requestGenerator == null) {
                throw new IllegalArgumentException("RequestGenerator cannot to be null.");
            }
            request = requestGenerator.generateRequest(requestModel);
        }
        return request;
    }

    public REQUEST filterRequest(REQUEST request) {
        List<RequestFilter<REQUEST>> requestFilters = httpManager.httpClient().requestFilters();
        FilterChain<RequestFilter<REQUEST>, REQUEST> filterChain = new FilterChain<>(requestFilters);
        return filterChain.doFilter(request);
    }

    public RESPONSE doInterceptor(REQUEST request, InterceptorChain.Executor<REQUEST, RESPONSE> executor)
            throws HttpException {
        List<Interceptor<REQUEST, RESPONSE>> interceptors = httpManager.httpClient().interceptors();
        InterceptorChain<REQUEST, RESPONSE> interceptorChain = new InterceptorChain<>(request, executor, interceptors);
        return interceptorChain.proceed(request);
    }

    public RESPONSE filterResponse(RESPONSE response) {
        List<ResponseFilter<RESPONSE>> responseFilters = httpManager.httpClient().responseFilters();
        FilterChain<ResponseFilter<RESPONSE>, RESPONSE> filterChain = new FilterChain<>(responseFilters);
        return filterChain.doFilter(response);
    }

    public <RESULT> RESULT adaptResponse(RESPONSE response) {
        // noinspection unchecked
        ResponseAdapter<RESPONSE, RESULT> responseAdapter = httpManager.responseAdapter();
        if (responseAdapter == null) {
            // noinspection unchecked
            return (RESULT) response;
        }
        Type responseType = responseType();
        return responseAdapter.adapt(response, responseType);
    }

    @Nullable
    protected RequestModelChecker requestModelChecker() {
        return null;
    }

    @NonNull
    protected abstract RequestGenerator<REQUEST> requestGenerator();

    public interface RequestModelChecker {
        RequestModel checkRequestModel(RequestModel requestModel);
    }

    public interface RequestGenerator<REQUEST extends Request> {
        REQUEST generateRequest(RequestModel requestModel);
    }
}
