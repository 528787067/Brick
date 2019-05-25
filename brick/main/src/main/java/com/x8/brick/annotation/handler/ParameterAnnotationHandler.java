package com.x8.brick.annotation.handler;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.x8.brick.annotation.define.parameter.Body;
import com.x8.brick.annotation.define.parameter.Field;
import com.x8.brick.annotation.define.parameter.FieldMap;
import com.x8.brick.annotation.define.parameter.Header;
import com.x8.brick.annotation.define.parameter.HeaderMap;
import com.x8.brick.annotation.define.parameter.Part;
import com.x8.brick.annotation.define.parameter.PartMap;
import com.x8.brick.annotation.define.parameter.Path;
import com.x8.brick.annotation.define.parameter.Query;
import com.x8.brick.annotation.define.parameter.QueryMap;
import com.x8.brick.annotation.define.parameter.QueryName;
import com.x8.brick.annotation.define.parameter.Url;
import com.x8.brick.annotation.define.type.Handler;
import com.x8.brick.annotation.handler.AnnotationHandlerHelper.MethodModel;
import com.x8.brick.core.RequestModel;
import com.x8.brick.utils.ConvertUtils;

import java.lang.annotation.Annotation;
import java.util.Map;

public abstract class ParameterAnnotationHandler<T extends Annotation> {

    @NonNull
    public abstract RequestModel handle(@NonNull T annotation,
            @Nullable Object parameter, @NonNull RequestModel requestModel, @NonNull MethodModel methodModel);

    @SuppressWarnings("unused")
    @Handler(Path.class)
    public static class PathHandler extends ParameterAnnotationHandler<Path> {
        @NonNull
        @Override
        public RequestModel handle(@NonNull Path path,
                @Nullable Object parameter, @NonNull RequestModel requestModel, @NonNull MethodModel methodModel) {
            String key = ConvertUtils.convertString(path.value());
            String value = ConvertUtils.convertString(parameter);
            boolean encoded = path.encoded();
            return requestModel.addPath(key, value, encoded);
        }
    }

    @SuppressWarnings("unused")
    @Handler(Query.class)
    public static class QueryHandler extends ParameterAnnotationHandler<Query> {
        @NonNull
        @Override
        public RequestModel handle(@NonNull Query query,
                @Nullable Object parameter, @NonNull RequestModel requestModel, @NonNull MethodModel methodModel) {
            boolean encoded = query.encoded();
            String key = ConvertUtils.convertString(query.value());
            if (parameter instanceof Object[]) {
                for (Object data : (Object[]) parameter) {
                    requestModel.addQuery(key, ConvertUtils.convertString(data), encoded);
                }
            } else if (parameter instanceof Iterable) {
                for (Object data : (Iterable) parameter) {
                    requestModel.addQuery(key, ConvertUtils.convertString(data), encoded);
                }
            } else {
                requestModel.addQuery(key, ConvertUtils.convertString(parameter), encoded);
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
                @Nullable Object parameter, @NonNull RequestModel requestModel, @NonNull MethodModel methodModel) {
            if (parameter instanceof Map) {
                // noinspection unchecked
                return requestModel.addQuerys((Map) parameter, queryMap.encoded());
            }
            return requestModel;
        }
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
                    requestModel.addQuery(ConvertUtils.convertString(query), "", encoded);
                }
            } else if (parameter instanceof Iterable) {
                for (Object query : (Iterable) parameter) {
                    requestModel.addQuery(ConvertUtils.convertString(query), "", encoded);
                }
            } else {
                requestModel.addQuery(ConvertUtils.convertString(parameter), "", encoded);
            }
            return requestModel;
        }
    }

    @SuppressWarnings("unused")
    @Handler(Field.class)
    public static class FieldHandler extends ParameterAnnotationHandler<Field> {
        @NonNull
        @Override
        public RequestModel handle(@NonNull Field field,
                @Nullable Object parameter, @NonNull RequestModel requestModel, @NonNull MethodModel methodModel) {
            boolean encoded = field.encoded();
            String key = ConvertUtils.convertString(field.value());
            if (parameter instanceof Object[]) {
                for (Object data : (Object[]) parameter) {
                    requestModel.addField(key, ConvertUtils.convertString(data), encoded);
                }
            } else if (parameter instanceof Iterable) {
                for (Object data : (Iterable) parameter) {
                    requestModel.addField(key, ConvertUtils.convertString(data), encoded);
                }
            } else {
                requestModel.addField(key, ConvertUtils.convertString(parameter), encoded);
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
                @Nullable Object parameter, @NonNull RequestModel requestModel, @NonNull MethodModel methodModel) {
            if (parameter instanceof Map) {
                // noinspection unchecked
                return requestModel.addFields((Map) parameter, fieldMap.encoded());
            }
            return requestModel;
        }
    }

    @SuppressWarnings("unused")
    @Handler(Header.class)
    public static class HeaderHandler extends ParameterAnnotationHandler<Header> {
        @NonNull
        @Override
        public RequestModel handle(@NonNull Header header,
                @Nullable Object parameter, @NonNull RequestModel requestModel, @NonNull MethodModel methodModel) {
            return requestModel.addHeader(ConvertUtils.convertString(header.value()),
                    ConvertUtils.convertString(parameter));
        }
    }

    @SuppressWarnings("unused")
    @Handler(HeaderMap.class)
    public static class HeaderMapHandler extends ParameterAnnotationHandler<HeaderMap> {
        @NonNull
        @Override
        public RequestModel handle(@NonNull HeaderMap headerMap,
                @Nullable Object parameter, @NonNull RequestModel requestModel, @NonNull MethodModel methodModel) {
            if (parameter instanceof Map) {
                // noinspection unchecked
                return requestModel.addHeaders((Map) parameter);
            }
            return requestModel;
        }
    }

    @SuppressWarnings("unused")
    @Handler(Part.class)
    public static class PartHandler extends ParameterAnnotationHandler<Part> {
        @NonNull
        @Override
        public RequestModel handle(@NonNull Part part,
                @Nullable Object parameter, @NonNull RequestModel requestModel, @NonNull MethodModel methodModel) {
            String key = ConvertUtils.convertString(part.value());
            String encoding = ConvertUtils.convertString(part.encoding());
            if (parameter instanceof Object[]) {
                for (Object data : (Object[]) parameter) {
                    requestModel.addPart(key, encoding, data);
                }
            } else if (parameter instanceof Iterable) {
                for (Object data : (Iterable) parameter) {
                    requestModel.addPart(key, encoding, data);
                }
            } else {
                requestModel.addPart(key, encoding, parameter);
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
                @Nullable Object parameter, @NonNull RequestModel requestModel, @NonNull MethodModel methodModel) {
            if (parameter instanceof Map) {
                // noinspection unchecked
                return requestModel.addParts(ConvertUtils.convertString(partMap.encoding()), (Map) parameter);
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
                @Nullable Object parameter, @NonNull RequestModel requestModel, @NonNull MethodModel methodModel) {
            return requestModel.addUrl(ConvertUtils.convertString(parameter));
        }
    }

    @SuppressWarnings("unused")
    @Handler(Body.class)
    public static class BodyHandler extends ParameterAnnotationHandler<Body> {
        @NonNull
        @Override
        public RequestModel handle(@NonNull Body body,
                @Nullable Object parameter, @NonNull RequestModel requestModel, @NonNull MethodModel methodModel) {
            return requestModel.addBody(parameter);
        }
    }

    @SuppressWarnings("unused")
    @Handler(Annotation.class)
    public static class AnnotationHandler extends ParameterAnnotationHandler<Annotation> {
        @NonNull
        @Override
        public RequestModel handle(@NonNull Annotation annotation,
                @Nullable Object parameter, @NonNull RequestModel requestModel, @NonNull MethodModel methodModel) {
            return requestModel;
        }
    }
}
