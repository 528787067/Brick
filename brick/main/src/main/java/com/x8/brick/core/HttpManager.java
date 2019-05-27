package com.x8.brick.core;

import android.support.annotation.NonNull;

import com.x8.brick.adapter.RequestAdapter;
import com.x8.brick.adapter.RequestConverterAdapter;
import com.x8.brick.adapter.ResponseAdapter;
import com.x8.brick.adapter.ResponseConverterAdapter;
import com.x8.brick.adapter.TaskAdapter;
import com.x8.brick.adapter.TaskConverterAdapter;
import com.x8.brick.annotation.checker.AnnotationChecker;
import com.x8.brick.annotation.checker.Checker;
import com.x8.brick.annotation.define.type.Api;
import com.x8.brick.annotation.handler.MethodAnnotationHandlerDelegate;
import com.x8.brick.annotation.handler.ParameterAnnotationHandlerDelegate;
import com.x8.brick.annotation.handler.TypeAnnotationHandlerDelegate;
import com.x8.brick.converter.RequestConverter;
import com.x8.brick.converter.ResponseConverter;
import com.x8.brick.converter.TaskConverter;
import com.x8.brick.parameter.Request;
import com.x8.brick.parameter.Response;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HttpManager<REQUEST extends Request, RESPONSE extends Response> {

    private String hostName;
    private HttpClient<REQUEST, RESPONSE> httpClient;
    private List<RequestModel.Host> hosts;
    private List<TaskConverter> taskConverters;
    private List<RequestConverter> requestConverters;
    private List<ResponseConverter> responseConverters;
    private TaskAdapter taskAdapter;
    private RequestAdapter requestAdapter;
    private ResponseAdapter responseAdapter;
    private List<Checker> annotationCheckers;
    private boolean handlerCacheAble;
    private boolean parseResultCacheAble;
    private boolean validateEagerly;
    private boolean checkAnnotationEffective;
    private TypeAnnotationHandlerDelegate typeAnnotationHandlerDelegate;
    private MethodAnnotationHandlerDelegate methodAnnotationHandlerDelegate;
    private ParameterAnnotationHandlerDelegate parameterAnnotationHandlerDelegate;

    protected HttpManager(@NonNull HttpClient<REQUEST, RESPONSE> httpClient) {
        this.httpClient = httpClient;
        this.handlerCacheAble = true;
        this.parseResultCacheAble = true;
        this.validateEagerly = false;
        this.checkAnnotationEffective = true;
    }

    public <API> API create(@NonNull Class<API> apiClass) {
        if (!apiClass.isInterface()) {
            throw new IllegalArgumentException("Api should be a interface.");
        }
        if (apiClass.getAnnotation(Api.class) == null) {
            throw new IllegalArgumentException("You must add @Api annotation to api interface.");
        }
        return new ApiProxy<API, REQUEST, RESPONSE>(this).create(apiClass);
    }

    public HttpClient<REQUEST, RESPONSE> httpClient() {
        return httpClient;
    }

    public String hostName() {
        return hostName;
    }

    public String defaultHostName() {
        return hostName == null ? RequestModel.HostName.DEFAULT : hostName;
    }

    public String baseUrl() {
        return url(RequestModel.HostName.DEFAULT);
    }

    public String url() {
        return url(defaultHostName());
    }

    public String url(String hostName) {
        if (hostName != null && hosts != null) {
            for (RequestModel.Host host : hosts) {
                if (host.name.equals(hostName)) {
                    return host.url;
                }
            }
        }
        return null;
    }

    public List<RequestModel.Host> hosts() {
        return hosts;
    }

    public List<TaskConverter> taskConverters() {
        return taskConverters;
    }

    public List<RequestConverter> requestConverters() {
        return requestConverters;
    }

    public List<ResponseConverter> responseConverters() {
        return responseConverters;
    }

    public List<Checker> annotationCheckers() {
        return annotationCheckers;
    }

    public TaskAdapter taskAdapter() {
        return taskAdapter;
    }

    public RequestAdapter requestAdapter() {
        return requestAdapter;
    }

    public ResponseAdapter responseAdapter() {
        return responseAdapter;
    }

    public boolean handlerCacheAble() {
        return handlerCacheAble;
    }

    public boolean parseResultCacheAble() {
        return parseResultCacheAble;
    }

    public boolean validateEagerly() {
        return validateEagerly;
    }

    public boolean checkAnnotationEffective() {
        return checkAnnotationEffective;
    }

    public TypeAnnotationHandlerDelegate typeAnnotationHandlerDelegate() {
        return typeAnnotationHandlerDelegate;
    }

    public MethodAnnotationHandlerDelegate methodAnnotationHandlerDelegate() {
        return methodAnnotationHandlerDelegate;
    }

    public ParameterAnnotationHandlerDelegate parameterAnnotationHandlerDelegate() {
        return parameterAnnotationHandlerDelegate;
    }

    public void setHost(String host) {
        setHostName(host);
    }

    protected void setHostName(String hostName) {
        this.hostName = hostName;
    }

    protected void setBaseUrl(String baseUrl) {
        addHost(RequestModel.HostName.DEFAULT, baseUrl);
    }

    protected void addHost(@NonNull String name, @NonNull String url) {
        if (hosts == null) {
            hosts = new LinkedList<>();
        }
        for (RequestModel.Host host : hosts) {
            if (host.name.equals(name)) {
                hosts.remove(host);
                break;
            }
        }
        hosts.add(new RequestModel.Host(name, url));
    }

    protected void addTaskConverter(@NonNull TaskConverter taskConverter) {
        if (taskConverters == null) {
            taskConverters = new LinkedList<>();
        }
        taskConverters.add(taskConverter);
    }

    protected void addRequestConverter(@NonNull RequestConverter requestConverter) {
        if (requestConverters == null) {
            requestConverters = new LinkedList<>();
        }
        requestConverters.add(requestConverter);
    }

    protected void addResponseConverter(@NonNull ResponseConverter<RESPONSE, ?> responseConverter) {
        if (responseConverters == null) {
            responseConverters = new LinkedList<>();
        }
        responseConverters.add(responseConverter);
    }

    protected void addAnnotationChecker(@NonNull Checker checker) {
        if (annotationCheckers == null) {
            annotationCheckers = new LinkedList<>();
        }
        annotationCheckers.add(checker);
    }

    protected void setTaskAdapter(TaskAdapter taskAdapter) {
        this.taskAdapter = taskAdapter;
    }

    protected void setRequestAdapter(RequestAdapter requestAdapter) {
        this.requestAdapter = requestAdapter;
    }

    protected void setResponseAdapter(ResponseAdapter<RESPONSE, ?> responseAdapter) {
        this.responseAdapter = responseAdapter;
    }

    protected void setHandlerCacheAble(boolean cacheAble) {
        this.handlerCacheAble = cacheAble;
    }

    protected void setParseResultCacheAble(boolean cacheAble) {
        this.parseResultCacheAble = cacheAble;
    }

    protected void setValidateEagerly(boolean validateEagerly) {
        this.validateEagerly = validateEagerly;
    }

    protected void setCheckAnnotationEffective(boolean checkAnnotationEffective) {
        this.checkAnnotationEffective = checkAnnotationEffective;
    }

    protected void setTypeAnnotationHandlerDelegate(TypeAnnotationHandlerDelegate delegate) {
        this.typeAnnotationHandlerDelegate = delegate;
    }

    protected void setMethodAnnotationHandlerDelegate(MethodAnnotationHandlerDelegate delegate) {
        this.methodAnnotationHandlerDelegate = delegate;
    }

    protected void setParameterAnnotationHandlerDelegate(ParameterAnnotationHandlerDelegate delegate) {
        this.parameterAnnotationHandlerDelegate = delegate;
    }

    public static class Builder<REQUEST extends Request, RESPONSE extends Response,
            MANAGER extends HttpManager<REQUEST, RESPONSE>,
            BUILDER extends Builder<REQUEST, RESPONSE, MANAGER, BUILDER>> {

        private MANAGER httpManager;

        public <CLIENT extends HttpClient<REQUEST, RESPONSE>> Builder(@NonNull CLIENT httpClient) {
            this.httpManager = createHttpManager(httpClient);
        }

        protected <CLIENT extends HttpClient<REQUEST, RESPONSE>> MANAGER createHttpManager(
                @NonNull CLIENT httpClient) {
            // noinspection unchecked
            return (MANAGER) new HttpManager<>(httpClient);
        }

        protected BUILDER builder() {
            // noinspection unchecked
            return (BUILDER) this;
        }

        protected MANAGER httpManager() {
            return this.httpManager;
        }

        public BUILDER setBaseUrl(String baseUrl) {
            httpManager.setBaseUrl(baseUrl);
            return builder();
        }

        public BUILDER setHost(String host) {
            return setHostName(host);
        }

        public BUILDER setHostName(String hostName) {
            httpManager.setHostName(hostName);
            return builder();
        }

        public BUILDER addHost(String name, String url) {
            httpManager.addHost(name, url);
            return builder();
        }

        public BUILDER addHost(@NonNull RequestModel.Host host) {
            return addHost(host.name, host.url);
        }

        public BUILDER addHosts(RequestModel.Host... hosts) {
            for (RequestModel.Host host : hosts) {
                addHost(host);
            }
            return builder();
        }

        public BUILDER addHosts(@NonNull Iterable<RequestModel.Host> hosts) {
            for (RequestModel.Host host : hosts) {
                addHost(host);
            }
            return builder();
        }

        public BUILDER addHosts(@NonNull Map<String, String> hosts) {
            for (Map.Entry<String, String> entry : hosts.entrySet()) {
                addHost(entry.getKey(), entry.getValue());
            }
            return builder();
        }

        public BUILDER addTaskConverter(@NonNull TaskConverter taskConverter) {
            httpManager.addTaskConverter(taskConverter);
            return builder();
        }

        public BUILDER addRequestConverter(@NonNull RequestConverter requestConverter) {
            httpManager.addRequestConverter(requestConverter);
            return builder();
        }

        public BUILDER addResponseConverter(@NonNull ResponseConverter<RESPONSE, ?> responseConverter) {
            httpManager.addResponseConverter(responseConverter);
            return builder();
        }

        public BUILDER addAnnotationChecker(@NonNull AnnotationChecker annotationChecker) {
            httpManager.addAnnotationChecker(annotationChecker);
            return builder();
        }

        public BUILDER setHandlerCacheAble(boolean cacheAble) {
            httpManager.setHandlerCacheAble(cacheAble);
            return builder();
        }

        public BUILDER setParseResultCacheAble(boolean cacheAble) {
            httpManager.setParseResultCacheAble(cacheAble);
            return builder();
        }

        public BUILDER setValidateEagerly(boolean validateEagerly) {
            httpManager.setValidateEagerly(validateEagerly);
            return builder();
        }

        public BUILDER setCheckAnnotationEffective(boolean checkAnnotationEffective) {
            httpManager.setCheckAnnotationEffective(checkAnnotationEffective);
            return builder();
        }

        public BUILDER setTypeAnnotationHandlerDelegate(TypeAnnotationHandlerDelegate delegate) {
            httpManager.setTypeAnnotationHandlerDelegate(delegate);
            return builder();
        }

        public BUILDER setMethodAnnotationHandlerDelegate(MethodAnnotationHandlerDelegate delegate) {
            httpManager.setMethodAnnotationHandlerDelegate(delegate);
            return builder();
        }

        public BUILDER setParameterAnnotationHandlerDelegate(ParameterAnnotationHandlerDelegate delegate) {
            httpManager.setParameterAnnotationHandlerDelegate(delegate);
            return builder();
        }

        public MANAGER build() {
            MANAGER httpManager = httpManager();
            if (httpManager.httpClient() == null) {
                throw new IllegalArgumentException("HttpClient is not allowed to be null");
            }
            httpManager.addAnnotationChecker(new AnnotationChecker());
            // noinspection unchecked
            httpManager.setTaskAdapter(new TaskConverterAdapter(httpManager.taskConverters()));
            // noinspection unchecked
            httpManager.setRequestAdapter(new RequestConverterAdapter(httpManager.requestConverters()));
            // noinspection unchecked
            httpManager.setResponseAdapter(new ResponseConverterAdapter(httpManager.responseConverters()));
            return httpManager;
        }
    }
}
