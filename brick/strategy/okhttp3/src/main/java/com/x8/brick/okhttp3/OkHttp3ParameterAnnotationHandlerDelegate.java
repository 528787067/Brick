package com.x8.brick.okhttp3;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.x8.brick.annotation.define.parameter.FieldMap;
import com.x8.brick.annotation.define.parameter.HeaderMap;
import com.x8.brick.annotation.define.parameter.PartMap;
import com.x8.brick.annotation.define.parameter.QueryMap;
import com.x8.brick.annotation.define.parameter.QueryName;
import com.x8.brick.annotation.define.parameter.Url;
import com.x8.brick.annotation.define.type.Handler;
import com.x8.brick.annotation.handler.AnnotationHandlerHelper;
import com.x8.brick.annotation.handler.AnnotationHandlerHelper.MethodModel;
import com.x8.brick.annotation.handler.ParameterAnnotationHandler;
import com.x8.brick.annotation.handler.ParameterAnnotationHandlerDelegate;
import com.x8.brick.core.RequestModel;
import com.x8.brick.utils.ConvertUtils;

import java.lang.annotation.Annotation;
import java.util.Map;

import okhttp3.MultipartBody;

public class OkHttp3ParameterAnnotationHandlerDelegate extends ParameterAnnotationHandlerDelegate {

    private boolean cacheAble;

    public OkHttp3ParameterAnnotationHandlerDelegate() {
        this(true);
    }

    public OkHttp3ParameterAnnotationHandlerDelegate(boolean cacheAble) {
        this.cacheAble = cacheAble;
    }

    @Nullable
    @Override
    public <T extends Annotation> ParameterAnnotationHandler<T> getParameterAnnotationHandler(T annotation) {
        // noinspection unchecked
        return AnnotationHandlerHelper.getInstance().getAnnotationHandler(
                annotation,
                OkHttp3ParameterAnnotationHandlerDelegate.class,
                ParameterAnnotationHandler.class,
                cacheAble ? OkHttpParamterAnnotationHandlerHolder.instance : null
        );
    }

    @SuppressWarnings("unused")
    @Handler(QueryName.class)
    public static class QueryNameHandler extends ParameterAnnotationHandler<QueryName> {
        @NonNull
        @Override
        public RequestModel handle(@NonNull QueryName queryName,
                @Nullable Object parameter, @NonNull RequestModel requestModel, @NonNull MethodModel methodModel) {
            boolean encoded = queryName.encoded();
            if (parameter instanceof Object[]) {
                for (Object query : (Object[]) parameter) {
                    requestModel.addQuery(ConvertUtils.convertString(query), null, encoded);
                }
            } else if (parameter instanceof Iterable) {
                for (Object query : (Iterable) parameter) {
                    requestModel.addQuery(ConvertUtils.convertString(query), null, encoded);
                }
            } else {
                requestModel.addQuery(ConvertUtils.convertString(parameter), null, encoded);
            }
            return requestModel;
        }
    }

    @SuppressWarnings("unused")
    @Handler(QueryMap.class)
    public static class QueryMapHandler extends ParameterAnnotationHandler<QueryMap> {
        @NonNull
        @Override
        public RequestModel handle(@NonNull QueryMap queryMap,
                @Nullable Object param, @NonNull RequestModel requestModel, @NonNull MethodModel methodModel) {
            if (param == null) {
                throw new IllegalArgumentException("@QueryMap parameter was null.");
            }
            if (!(param instanceof Map)) {
                throw new IllegalArgumentException("@QueryMap parameter type must be Map.");
            }
            boolean encoded = queryMap.encoded();
            for (Map.Entry entry : ((Map<?, ?>) param).entrySet()) {
                Object entryKey = entry.getKey();
                Object entryValue = entry.getValue();
                if (entryKey == null) {
                    throw new IllegalArgumentException("@QueryMap parameter contained null key.");
                }
                if (entryValue == null) {
                    throw new IllegalArgumentException(
                            "@QueryMap parameter contained null value for key '" + entryKey + "'.");
                }
                String key = ConvertUtils.convertString(entryKey);
                String value = ConvertUtils.convertString(entryValue);
                requestModel.addQuery(key, value, encoded);
            }
            return requestModel;
        }
    }

    @SuppressWarnings("unused")
    @Handler(FieldMap.class)
    public static class FieldMapHandler extends ParameterAnnotationHandler<FieldMap> {
        @NonNull
        @Override
        public RequestModel handle(@NonNull FieldMap fieldMap,
                @Nullable Object param, @NonNull RequestModel requestModel, @NonNull MethodModel methodModel) {
            if (param == null) {
                throw new IllegalArgumentException("@FieldMap parameter was null.");
            }
            if (!(param instanceof Map)) {
                throw new IllegalArgumentException("@FieldMap parameter type must be Map.");
            }
            boolean encoded = fieldMap.encoded();
            for (Map.Entry entry : ((Map<?, ?>) param).entrySet()) {
                Object entryKey = entry.getKey();
                Object entryValue = entry.getValue();
                if (entryKey == null) {
                    throw new IllegalArgumentException("@FieldMap parameter contained null key.");
                }
                if (entryValue == null) {
                    throw new IllegalArgumentException(
                            "@FieldMap parameter contained null value for key '" + entryKey + "'.");
                }
                String key = ConvertUtils.convertString(entryKey);
                String value = ConvertUtils.convertString(entryValue);
                requestModel.addField(key, value, encoded);
            }
            return requestModel;
        }
    }

    @SuppressWarnings("unused")
    @Handler(HeaderMap.class)
    public static class HeaderMapHandler extends ParameterAnnotationHandler<HeaderMap> {
        @NonNull
        @Override
        public RequestModel handle(@NonNull HeaderMap headerMap,
                @Nullable Object param, @NonNull RequestModel requestModel, @NonNull MethodModel methodModel) {
            if (param == null) {
                throw new IllegalArgumentException("@HeaderMap parameter was null.");
            }
            if (!(param instanceof Map)) {
                throw new IllegalArgumentException("@HeaderMap parameter type must be Map.");
            }
            for (Map.Entry entry : ((Map<?, ?>) param).entrySet()) {
                Object entryKey = entry.getKey();
                Object entryValue = entry.getValue();
                if (entryKey == null) {
                    throw new IllegalArgumentException("@HeaderMap parameter contained null key.");
                }
                if (entryValue == null) {
                    throw new IllegalArgumentException(
                            "@HeaderMap parameter contained null value for key '" + entryKey + "'.");
                }
                String key = ConvertUtils.convertString(entryKey);
                String value = ConvertUtils.convertString(entryValue);
                requestModel.addHeader(key, value);
            }
            return requestModel;
        }
    }

    @SuppressWarnings("unused")
    @Handler(PartMap.class)
    public static class PartMapHandler extends ParameterAnnotationHandler<PartMap> {
        @NonNull
        @Override
        public RequestModel handle(@NonNull PartMap partMap,
                @Nullable Object param, @NonNull RequestModel requestModel, @NonNull MethodModel methodModel) {
            if (!(param instanceof Map)) {
                throw new IllegalArgumentException("@PartMap parameter type must be Map.");
            }
            for (Map.Entry entry : ((Map<?, ?>) param).entrySet()) {
                Object entryKey = entry.getKey();
                Object entryValue = entry.getValue();
                if (entryKey == null) {
                    throw new IllegalArgumentException("@PartMap parameter contained null key.");
                }
                if (entryValue == null) {
                    throw new IllegalArgumentException(
                            "@PartMap parameter contained null value for key '" + entryKey + "'.");
                }
                if (entryValue instanceof MultipartBody.Part) {
                    throw new IllegalArgumentException("@PartMap values cannot be MultipartBody.Part. "
                            + "Use @Part List<Part> or a different value type instead.");
                }
                String key = ConvertUtils.convertString(entryKey);
                String encoding = ConvertUtils.convertString(partMap.encoding());
                requestModel.addPart(key, encoding, entryValue);
            }
            return requestModel;
        }
    }

    @SuppressWarnings("unused")
    @Handler(Url.class)
    public static class UrlHandler extends ParameterAnnotationHandler<Url> {
        @NonNull
        @Override
        public RequestModel handle(@NonNull Url url,
                @Nullable Object param, @NonNull RequestModel requestModel, @NonNull MethodModel methodModel) {
            if (requestModel.querys().size() > 0) {
                throw new IllegalArgumentException("A @Url parameter must not come after a @Query or @QueryName");
            }
            return requestModel.addUrl(ConvertUtils.convertString(param));
        }
    }

    private static class OkHttpParamterAnnotationHandlerHolder
            extends AnnotationHandlerHelper.AnnotationHandlerHolder<ParameterAnnotationHandler> {
        static OkHttpParamterAnnotationHandlerHolder instance = new OkHttpParamterAnnotationHandlerHolder();
    }
}
