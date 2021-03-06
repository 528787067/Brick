package com.x8.brick.okhttp3;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.x8.brick.annotation.define.type.Api;
import com.x8.brick.annotation.define.type.Handler;
import com.x8.brick.annotation.handler.AnnotationHandlerHelper;
import com.x8.brick.annotation.handler.AnnotationHandlerHelper.MethodModel;
import com.x8.brick.annotation.handler.TypeAnnotationHandler;
import com.x8.brick.annotation.handler.TypeAnnotationHandlerDelegate;
import com.x8.brick.core.RequestModel;
import com.x8.brick.utils.ConvertUtils;

import java.lang.annotation.Annotation;

public class OkHttp3TypeAnnotationHandlerDelegate extends TypeAnnotationHandlerDelegate {

    private boolean cacheAble;

    public OkHttp3TypeAnnotationHandlerDelegate() {
        this(true);
    }

    public OkHttp3TypeAnnotationHandlerDelegate(boolean cacheAble) {
        this.cacheAble = cacheAble;
    }

    @Nullable
    @Override
    public <T extends Annotation> TypeAnnotationHandler<T> getTypeAnnotationHandler(T annotation) {
        // noinspection unchecked
        return AnnotationHandlerHelper.getInstance().getAnnotationHandler(
                annotation,
                OkHttp3TypeAnnotationHandlerDelegate.class,
                TypeAnnotationHandler.class,
                cacheAble ? OkHttpTypeAnnotationHandlerHolder.instance : null
        );
    }

    @SuppressWarnings("unused")
    @Handler(Api.class)
    public static class ApiHandler extends TypeAnnotationHandler<Api> {
        @NonNull
        @Override
        public RequestModel handle(@NonNull Api api,
                                   @NonNull RequestModel requestModel, @NonNull MethodModel methodModel) {
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
            for (String host : api.hosts()) {
                if (host == null) {
                    throw new IllegalArgumentException("Host value could not be null.");
                }
                int colon = host.indexOf('@');
                if (colon == -1 || colon == 0 || colon == host.length() - 1) {
                    throw new IllegalArgumentException(String.format(
                            "Host value must be in the form \"Name@ Url\". Found: \"%s\"", host));
                }
                String name = host.substring(0, colon).trim();
                String url = host.substring(colon + 1).trim();
                name = ConvertUtils.convertString(name);
                url = ConvertUtils.convertString(url);
                requestModel.addHost(name, url);
            }
            return requestModel;
        }
    }

    private static class OkHttpTypeAnnotationHandlerHolder
            extends AnnotationHandlerHelper.AnnotationHandlerHolder<TypeAnnotationHandler> {
        static OkHttpTypeAnnotationHandlerHolder instance = new OkHttpTypeAnnotationHandlerHolder();
    }
}
