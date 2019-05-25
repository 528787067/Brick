package com.x8.brick.annotation.handler;

import android.support.annotation.NonNull;

import com.x8.brick.annotation.define.type.Api;
import com.x8.brick.annotation.define.type.Handler;
import com.x8.brick.annotation.handler.AnnotationHandlerHelper.MethodModel;
import com.x8.brick.core.RequestModel;
import com.x8.brick.utils.ConvertUtils;

import java.lang.annotation.Annotation;

public abstract class TypeAnnotationHandler<T extends Annotation> {

    @NonNull
    public abstract RequestModel handle(@NonNull T annotation,
            @NonNull RequestModel requestModel, @NonNull MethodModel methodModel);

    @SuppressWarnings("unused")
    @Handler(Api.class)
    public static class ApiHandler extends TypeAnnotationHandler<Api> {
        @NonNull
        @Override
        public RequestModel handle(@NonNull Api api,
                @NonNull RequestModel requestModel, @NonNull MethodModel methodData) {
            requestModel.addHostName(ConvertUtils.convertString(api.hostName()));
            requestModel.addHost(RequestModel.HostName.DEFAULT, ConvertUtils.convertString(api.value()));
            requestModel.addHost(RequestModel.HostName.RELEASE, ConvertUtils.convertString(api.release()));
            requestModel.addHost(RequestModel.HostName.DEBUG, ConvertUtils.convertString(api.debug()));
            requestModel.addHost(RequestModel.HostName.ONLINE, ConvertUtils.convertString(api.online()));
            requestModel.addHost(RequestModel.HostName.DEV, ConvertUtils.convertString(api.dev()));
            requestModel.addHost(RequestModel.HostName.TEST, ConvertUtils.convertString(api.test()));
            requestModel.addHost(RequestModel.HostName.SANDBOX, ConvertUtils.convertString(api.sandbox()));
            requestModel.addHost(RequestModel.HostName.PRODUCT, ConvertUtils.convertString(api.product()));
            requestModel.addHost(RequestModel.HostName.PREVIEW, ConvertUtils.convertString(api.preview()));
            String[] hosts = api.hosts();
            for (String host : hosts) {
                host = ConvertUtils.convertString(host);
                String name;
                String url;
                int colon = host.indexOf('@');
                if (host.equals("@")) {
                    name = RequestModel.HostName.DEFAULT;
                    url = "";
                } else if (colon == -1) {
                    name = RequestModel.HostName.DEFAULT;
                    url = host.trim();
                } else if (colon == 0) {
                    name = RequestModel.HostName.DEFAULT;
                    url = host.substring(1).trim();
                } else if (colon == host.length() - 1) {
                    name = host.substring(0, host.length() - 1).trim();
                    url = "";
                } else {
                    name = host.substring(0, colon).trim();
                    url = host.substring(colon + 1).trim();
                }
                name = ConvertUtils.convertString(name);
                url = ConvertUtils.convertString(url);
                requestModel.addHost(name, url);
            }
            return requestModel;
        }
    }

    @SuppressWarnings("unused")
    @Handler(Annotation.class)
    public static class AnnotationHandler extends TypeAnnotationHandler {
        @NonNull
        @Override
        public RequestModel handle(@NonNull Annotation annotation,
                @NonNull RequestModel requestModel, @NonNull MethodModel methodModel) {
            return requestModel;
        }
    }
}
