package com.x8.brick.core;

import android.support.annotation.NonNull;

import com.x8.brick.annotation.checker.Checker;
import com.x8.brick.annotation.handler.HandlerHelper;
import com.x8.brick.parameter.Request;
import com.x8.brick.parameter.Response;
import com.x8.brick.task.Task;
import com.x8.brick.task.TaskFactory;
import com.x8.brick.task.TaskModel;
import com.x8.brick.task.TaskModelFactory;
import com.x8.brick.utils.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class ApiProxy<API, REQUEST extends Request, RESPONSE extends Response> implements InvocationHandler {

    private HttpManager<REQUEST, RESPONSE> httpManager;
    private TaskFactory<REQUEST, RESPONSE, ?> taskFactory;
    private HandlerHelper handlerHelper;

    private volatile RequestModel typeRequestModelCache;
    private volatile Map<Method, RequestModel> methodRequestModelCache;

    ApiProxy(@NonNull HttpManager<REQUEST, RESPONSE> httpManager) {
        this.httpManager = httpManager;
        this.handlerHelper = new HandlerHelper(this.httpManager);
        this.taskFactory = this.httpManager.httpClient().taskFactory();
    }

    API create(@NonNull Class<API> apiClass) {
        ClassLoader classLoader = apiClass.getClassLoader();
        if (classLoader == null) {
            throw new RuntimeException("Unable to get class loader for " + apiClass);
        }
        // noinspection unchecked
        API api = (API) Proxy.newProxyInstance(classLoader, new Class[]{ apiClass }, this);
        if (httpManager.validateEagerly()) {
            eagerlyValidateMethods(api, apiClass);
        }
        return api;
    }

    @Override
    public Object invoke(Object object, Method method, Object[] parameters) throws Throwable {
        RequestModel requestModel = parseAnnotation(object, method, parameters);
        Task<REQUEST, RESPONSE, ?> task = createTask(requestModel, method.getGenericReturnType());
        return convertTask(task, method.getReturnType());
    }

    private void eagerlyValidateMethods(API api, Class<API> apiClass) {
        for (Method method : apiClass.getDeclaredMethods()) {
            parseAnnotation(api, method, null);
        }
    }

    private RequestModel parseAnnotation(Object object, Method method, Object[] parameters) {
        RequestModel requestModel = new RequestModel();
        MethodModel methodModel = new MethodModel(object, method, parameters);
        if (httpManager.parseResultCacheAble()) {
            if (methodRequestModelCache != null && methodRequestModelCache.containsKey(method)) {
                requestModel = requestModel.addModel(methodRequestModelCache.get(method));
            } else {
                boolean checkEffective = checkAnnotationEffective();
                List<Checker> checkers = httpManager.annotationCheckers();
                if (typeRequestModelCache == null
                        && (!checkEffective || AnnotationUtils.hasTypeAnnotation(method, checkers))) {
                    synchronized (this) {
                        if (typeRequestModelCache == null) {
                            typeRequestModelCache = parseTypeAnnotation(methodModel, new RequestModel());
                        }
                    }
                }
                if (!checkEffective || AnnotationUtils.hasMethodAnnotation(method, checkers)) {
                    RequestModel model = parseMethodAnnotation(methodModel, new RequestModel(typeRequestModelCache));
                    if (methodRequestModelCache == null) {
                        synchronized (this) {
                            if (methodRequestModelCache == null) {
                                methodRequestModelCache = new ConcurrentHashMap<>();
                            }
                        }
                    }
                    methodRequestModelCache.put(method, model);
                    requestModel = requestModel.addModel(model);
                }
            }
        } else {
            requestModel = parseTypeAnnotation(methodModel, requestModel);
            requestModel = parseMethodAnnotation(methodModel, requestModel);
        }
        requestModel = parseParameterAnnotation(methodModel, requestModel);
        if (requestModel == null) {
            throw new IllegalArgumentException("Request model is null, please check your annotation handler.");
        }
        return requestModel;
    }

    private RequestModel parseTypeAnnotation(MethodModel methodModel, RequestModel requestModel) {
        Method method = methodModel.method();
        boolean checkEffective = checkAnnotationEffective();
        List<Checker> checkers = httpManager.annotationCheckers();
        if (!checkEffective || AnnotationUtils.hasTypeAnnotation(method, checkers)) {
            Annotation[] typeAnnotations = method.getDeclaringClass().getAnnotations();
            for (Annotation annotation : typeAnnotations) {
                if (!checkEffective || AnnotationUtils.isTypeAnnotation(annotation, checkers)) {
                    requestModel = handlerHelper.handleTypeAnnotation(annotation, requestModel, methodModel);
                }
            }
        }
        return requestModel;
    }

    private RequestModel parseMethodAnnotation(MethodModel methodModel, RequestModel requestModel) {
        Method method = methodModel.method();
        boolean checkEffective = checkAnnotationEffective();
        List<Checker> checkers = httpManager.annotationCheckers();
        if (!checkEffective || AnnotationUtils.hasMethodAnnotation(method, checkers)) {
            Annotation[] methodAnnotations = method.getAnnotations();
            for (Annotation annotation : methodAnnotations) {
                if (!checkEffective || AnnotationUtils.isMethodAnnotation(annotation, checkers)) {
                    requestModel = handlerHelper.handleMethodAnnotation(annotation, requestModel, methodModel);
                }
            }
        }
        return requestModel;
    }

    private RequestModel parseParameterAnnotation(MethodModel methodModel, RequestModel requestModel) {
        Method method = methodModel.method();
        Object[] parameters = methodModel.parameters();
        boolean checkEffective = checkAnnotationEffective();
        List<Checker> checkers = httpManager.annotationCheckers();
        if (parameters != null && (!checkEffective || AnnotationUtils.hasParameterAnnotation(method, checkers))) {
            Annotation[][] parameterAnnotationsArray = method.getParameterAnnotations();
            for (int i = 0; i < parameters.length; i++) {
                Object parameter = parameters[i];
                Annotation[] parameterAnnotations = parameterAnnotationsArray[i];
                for (Annotation annotation : parameterAnnotations) {
                    if (!checkEffective || AnnotationUtils.isParameterAnnotation(annotation, checkers)) {
                        requestModel = handlerHelper.handleParameterAnnotation(
                                annotation, parameter, requestModel, methodModel);
                    }
                }
            }
        }
        return requestModel;
    }

    private boolean checkAnnotationEffective() {
        return httpManager.checkAnnotationEffective()
                && httpManager.annotationCheckers() != null
                && httpManager.annotationCheckers().size() > 0;
    }

    private <RESULT> Task<REQUEST, RESPONSE, RESULT> createTask(RequestModel requestModel, Type taskType) {
        HttpClient<REQUEST, RESPONSE> httpClient = httpManager.httpClient();
        TaskModelFactory<REQUEST, RESPONSE> taskModelFactory = httpClient.taskModelFactory();
        if (taskModelFactory == null) {
            throw new IllegalArgumentException("You must first set up an TaskModelFactory.");
        }
        TaskModel<REQUEST, RESPONSE> taskModel = taskModelFactory.create(httpManager, requestModel, taskType);
        // noinspection unchecked
        return (Task<REQUEST, RESPONSE, RESULT>) taskFactory.create(taskModel);
    }

    private <RESULT> Object convertTask(Task<REQUEST, RESPONSE, RESULT> task, Type taskType) {
        // noinspection unchecked
        return httpManager.taskAdapter().adapt(task, taskType);
    }
}
