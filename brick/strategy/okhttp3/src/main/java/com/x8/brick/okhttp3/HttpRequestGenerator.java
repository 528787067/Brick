package com.x8.brick.okhttp3;

import android.support.annotation.NonNull;

import com.x8.brick.core.HttpManager;
import com.x8.brick.core.RequestModel;
import com.x8.brick.task.TaskModel;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;

public class HttpRequestGenerator implements TaskModel.RequestGenerator<HttpRequest, HttpResponse> {

    @Override
    public HttpRequest generateRequest(HttpManager<HttpRequest, HttpResponse> httpManager, RequestModel requestModel) {
        return HttpRequestGeneratorDelegator.getInstance().generateRequest(httpManager, requestModel);
    }

    private static class HttpRequestGeneratorDelegator implements RequestBuilderHandler,
            Comparator<RequestBuilderHandler> {

        private static HttpRequestGeneratorDelegator instance = new HttpRequestGeneratorDelegator();

        static HttpRequestGeneratorDelegator getInstance() {
            return instance;
        }

        private volatile boolean sorted;
        private List<RequestBuilderHandler> handlers;

        private HttpRequestGeneratorDelegator() {
            handlers = Arrays.asList(
                    RequestBuilder.getInstance(),
                    MethodPath.getInstance(),
                    BaseUrl.getInstance(),
                    RelativeUrl.getInstance(),
                    Path.getInstance(),
                    Query.getInstance(),
                    MediaType.getInstance(),
                    Header.getInstance(),
                    Body.getInstance(),
                    FormEncoded.getInstance(),
                    Field.getInstance(),
                    Multipart.getInstance(),
                    Part.getInstance(),
                    Streaming.getInstance()
            );
        }

        @Override
        public int priority() {
            return 0;
        }

        @Override
        public void apply(@NonNull HttpManager<HttpRequest, HttpResponse> httpManager,
                          @NonNull RequestModel requestModel,
                          @NonNull HttpRequestBuilder builder) {
            if (!sorted) {
                synchronized (this) {
                    Collections.sort(handlers, this);
                    sorted = true;
                }
            }
            for (RequestBuilderHandler handler : handlers) {
                handler.apply(httpManager, requestModel, builder);
            }
        }

        @Override
        public int compare(RequestBuilderHandler o1, RequestBuilderHandler o2) {
            return Integer.compare(o1 == null ? -1 : o1.priority(), o2 == null ? -1 : o2.priority());
        }

        public HttpRequest generateRequest(@NonNull HttpManager<HttpRequest, HttpResponse> httpManager,
                                           @NonNull RequestModel requestModel) {
            HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
            this.apply(httpManager, requestModel, requestBuilder);
            return requestBuilder.build();
        }
    }

    public static class HttpRequestBuilder {

        private HttpRequest.Builder requestBuilder;
        private String method;
        private boolean hasBody;
        private HttpUrl baseUrl;
        private String relativeUrl;
        private HttpUrl.Builder urlBuilder;
        private MediaType contentType;
        private boolean isStreaming;
        private RequestBody body;
        private boolean isFormEncoded;
        private FormBody.Builder formBuilder;
        private boolean isMultipart;
        private MultipartBody.Builder multipartBuilder;

        public HttpRequest.Builder requestBuilder() {
            return requestBuilder;
        }

        public String method() {
            return method;
        }

        public boolean hasBody() {
            return hasBody;
        }

        public HttpUrl baseUrl() {
            return baseUrl;
        }

        public String relativeUrl() {
            return relativeUrl;
        }

        public HttpUrl.Builder urlBuilder() {
            return urlBuilder;
        }

        public MediaType contentType() {
            return contentType;
        }

        public boolean isStreaming() {
            return isStreaming;
        }

        public RequestBody getBody() {
            return body;
        }

        public boolean isFormEncoded() {
            return isFormEncoded;
        }

        public FormBody.Builder formBuilder() {
            return formBuilder;
        }

        public boolean isMultipart() {
            return isMultipart;
        }

        public MultipartBody.Builder getMultipartBuilder() {
            return multipartBuilder;
        }

        public HttpRequestBuilder setRequestBuilder(HttpRequest.Builder requestBuilder) {
            this.requestBuilder = requestBuilder;
            return this;
        }

        public HttpRequestBuilder setMethod(String method) {
            this.method = method;
            return this;
        }

        public HttpRequestBuilder setHasBody(boolean hasBody) {
            this.hasBody = hasBody;
            return this;
        }

        public HttpRequestBuilder setBaseUrl(HttpUrl baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public HttpRequestBuilder setRelativeUrl(String relativeUrl) {
            this.relativeUrl = relativeUrl;
            return this;
        }

        public HttpRequestBuilder setUrlBuilder(HttpUrl.Builder urlBuilder) {
            this.urlBuilder = urlBuilder;
            return this;
        }

        public HttpRequestBuilder setContentType(MediaType contentType) {
            this.contentType = contentType;
            return this;
        }

        public HttpRequestBuilder setStreaming(boolean streaming) {
            this.isStreaming = streaming;
            return this;
        }

        public HttpRequestBuilder setBody(RequestBody body) {
            this.body = body;
            return this;
        }

        public HttpRequestBuilder setFormEncoded(boolean formEncoded) {
            this.isFormEncoded = formEncoded;
            return this;
        }

        public HttpRequestBuilder setFormBuilder(FormBody.Builder formBuilder) {
            this.formBuilder = formBuilder;
            return this;
        }

        public HttpRequestBuilder setMultipart(boolean multipart) {
            this.isMultipart = multipart;
            return this;
        }

        public HttpRequestBuilder setMultipartBuilder(MultipartBody.Builder multipartBuilder) {
            this.multipartBuilder = multipartBuilder;
            return this;
        }

        public HttpRequest build() {
            if (requestBuilder == null) {
                throw new IllegalArgumentException("requestBuilder == null");
            }
            HttpUrl url;
            if (urlBuilder != null) {
                url = urlBuilder.build();
            } else {
                if (baseUrl == null) {
                    throw new IllegalArgumentException("baseUrl == null");
                }
                url = baseUrl.resolve(relativeUrl);
                if (url == null) {
                    throw new IllegalArgumentException(
                            "Malformed URL. Base: " + baseUrl + ", Relative: " + relativeUrl);
                }
            }
            if (body == null) {
                if (formBuilder != null) {
                    body = formBuilder.build();
                } else if (multipartBuilder != null) {
                    body = multipartBuilder.build();
                } else if (hasBody) {
                    body = RequestBody.create(null, new byte[0]);
                }
            }
            if (contentType != null) {
                if (body != null) {
                    body = new ContentTypeOverridingRequestBody(body, contentType);
                } else {
                    requestBuilder.addHeader("Content-Type", contentType.toString());
                }
            }
            return requestBuilder
                    .url(url)
                    .method(method, body)
                    .isStreaming(isStreaming)
                    .build();
        }
    }

    public interface RequestBuilderHandler {

        int priority();

        void apply(@NonNull HttpManager<HttpRequest, HttpResponse> httpManager,
                   @NonNull RequestModel requestModel,
                   @NonNull HttpRequestBuilder builder);

        class RequestBuilder implements RequestBuilderHandler {

            private static RequestBuilder instance = new RequestBuilder();

            public static RequestBuilder getInstance() {
                return instance;
            }

            protected RequestBuilder() {
            }

            @Override
            public int priority() {
                return 1;
            }

            @Override
            public void apply(@NonNull HttpManager<HttpRequest, HttpResponse> httpManager,
                              @NonNull RequestModel requestModel,
                              @NonNull HttpRequestBuilder builder) {
                builder.setRequestBuilder(new HttpRequest.Builder());
            }
        }

        class MethodPath implements RequestBuilderHandler {

            private static MethodPath instance = new MethodPath();

            public static MethodPath getInstance() {
                return instance;
            }

            protected MethodPath() {
            }

            @Override
            public int priority() {
                return 2;
            }

            @Override
            public void apply(@NonNull HttpManager<HttpRequest, HttpResponse> httpManager,
                              @NonNull RequestModel requestModel,
                              @NonNull HttpRequestBuilder builder) {
                RequestModel.MethodPath methodPath = requestModel.methodPaths().get(0);
                builder.setMethod(methodPath.method());
                builder.setHasBody(methodPath.hasBody());
            }
        }

        class BaseUrl implements RequestBuilderHandler {

            private static BaseUrl instance = new BaseUrl();

            public static BaseUrl getInstance() {
                return instance;
            }

            protected BaseUrl() {
            }

            @Override
            public int priority() {
                return 3;
            }

            @Override
            public void apply(@NonNull HttpManager<HttpRequest, HttpResponse> httpManager,
                              @NonNull RequestModel requestModel,
                              @NonNull HttpRequestBuilder builder) {
                String hostName = httpManager.hostName();
                if (hostName == null && requestModel.hostNames().size() > 0) {
                    hostName = requestModel.hostNames().get(0);
                }
                if (hostName == null) {
                    hostName = RequestModel.HostName.DEFAULT;
                }
                String baseUrl = null;
                if (httpManager.hosts() != null) {
                    for (RequestModel.Host host : httpManager.hosts()) {
                        if (hostName.equals(host.name())) {
                            baseUrl = host.url();
                            break;
                        }
                    }
                }
                if (baseUrl == null) {
                    for (RequestModel.Host host : requestModel.hosts()) {
                        if (hostName.equals(host.name())) {
                            baseUrl = host.url();
                            break;
                        }
                    }
                }
                if (baseUrl == null) {
                    throw new IllegalArgumentException("The host name \"" + hostName + "\" is undefine.");
                }
                if (baseUrl.isEmpty()) {
                    throw new IllegalArgumentException("Base url is empty.");
                }
                HttpUrl httpUrl = HttpUrl.parse(baseUrl);
                if (httpUrl == null) {
                    throw new IllegalArgumentException("Illegal URL: " + baseUrl);
                }
                List<String> pathSegments = httpUrl.pathSegments();
                if (!"".equals(pathSegments.get(pathSegments.size() - 1))) {
                    throw new IllegalArgumentException("baseUrl must end in /: " + baseUrl);
                }
                builder.setBaseUrl(httpUrl);
            }
        }

        class RelativeUrl implements RequestBuilderHandler {

            private static RelativeUrl instance = new RelativeUrl();

            public static RelativeUrl getInstance() {
                return instance;
            }

            protected RelativeUrl() {
            }

            @Override
            public int priority() {
                return 4;
            }

            @Override
            public void apply(@NonNull HttpManager<HttpRequest, HttpResponse> httpManager,
                              @NonNull RequestModel requestModel,
                              @NonNull HttpRequestBuilder builder) {
                String relativeUrl = requestModel.hasUrl()
                        ? requestModel.urls().get(0)
                        : requestModel.methodPaths().get(0).path();
                builder.setRelativeUrl(relativeUrl);
            }
        }

        class Path implements RequestBuilderHandler {

            private static Path instance = new Path();

            public static Path getInstance() {
                return instance;
            }

            protected Path() {
            }

            @Override
            public int priority() {
                return 5;
            }

            @Override
            public void apply(@NonNull HttpManager<HttpRequest, HttpResponse> httpManager,
                              @NonNull RequestModel requestModel,
                              @NonNull HttpRequestBuilder builder) {
                String relativeUrl = builder.relativeUrl();
                for (RequestModel.Path path : requestModel.paths()) {
                    if (path.value() == null) {
                        throw new IllegalArgumentException(
                                "Path parameter \"" + path.name() + "\" value must not be null.");
                    }
                    if (relativeUrl == null) {
                        throw new AssertionError();
                    }
                    String pathValue = Encoder.canonicalizeForPath(path.value(), path.encoded());
                    relativeUrl = relativeUrl.replace("{" + path.name() + "}", pathValue);
                }
                builder.setRelativeUrl(relativeUrl);
            }
        }

        class Query implements RequestBuilderHandler {

            private static Query instance = new Query();

            public static Query getInstance() {
                return instance;
            }

            protected Query() {
            }

            @Override
            public int priority() {
                return 6;
            }

            @Override
            public void apply(@NonNull HttpManager<HttpRequest, HttpResponse> httpManager,
                              @NonNull RequestModel requestModel,
                              @NonNull HttpRequestBuilder builder) {
                if (requestModel.hasQuery()) {
                    HttpUrl baseUrl = builder.baseUrl();
                    String relativeUrl = builder.relativeUrl();
                    if (baseUrl == null) {
                        throw new AssertionError();
                    }
                    HttpUrl.Builder urlBuilder = baseUrl.newBuilder(relativeUrl);
                    if (urlBuilder == null) {
                        throw new IllegalArgumentException(
                                "Malformed URL. Base: " + baseUrl + ", Relative: " + relativeUrl);
                    }
                    for (RequestModel.Query query : requestModel.querys()) {
                        if (query.encoded()) {
                            urlBuilder.addEncodedQueryParameter(query.name(), query.value());
                        } else {
                            urlBuilder.addQueryParameter(query.name(), query.value());
                        }
                    }
                    builder.setUrlBuilder(urlBuilder);
                }
            }
        }

        class MediaType implements RequestBuilderHandler {

            private static MediaType instance = new MediaType();

            public static MediaType getInstance() {
                return instance;
            }

            protected MediaType() {
            }

            @Override
            public int priority() {
                return 7;
            }

            @Override
            public void apply(@NonNull HttpManager<HttpRequest, HttpResponse> httpManager,
                              @NonNull RequestModel requestModel,
                              @NonNull HttpRequestBuilder builder) {
                for (RequestModel.Header header : requestModel.headers()) {
                    if ("Content-Type".equalsIgnoreCase(header.name())) {
                        builder.setContentType(okhttp3.MediaType.parse(header.value()));
                    }
                }
            }
        }

        class Header implements RequestBuilderHandler {

            private static Header instance = new Header();

            public static Header getInstance() {
                return instance;
            }

            protected Header() {
            }

            @Override
            public int priority() {
                return 8;
            }

            @Override
            public void apply(@NonNull HttpManager<HttpRequest, HttpResponse> httpManager,
                              @NonNull RequestModel requestModel,
                              @NonNull HttpRequestBuilder builder) {
                HttpRequest.Builder requestBuilder = builder.requestBuilder();
                if (requestBuilder == null) {
                    throw new AssertionError();
                }
                for (RequestModel.Header header : requestModel.headers()) {
                    requestBuilder.addHeader(header.name(), header.value());
                }
            }
        }

        class Body implements RequestBuilderHandler {

            private static Body instance = new Body();

            public static Body getInstance() {
                return instance;
            }

            protected Body() {
            }

            @Override
            public int priority() {
                return 9;
            }

            @Override
            public void apply(@NonNull HttpManager<HttpRequest, HttpResponse> httpManager,
                              @NonNull RequestModel requestModel,
                              @NonNull HttpRequestBuilder builder) {
                if (requestModel.hasBody()) {
                    builder.setBody((RequestBody) requestModel.bodys().get(0));
                }
            }
        }

        class Field implements RequestBuilderHandler {

            private static Field instance = new Field();

            public static Field getInstance() {
                return instance;
            }

            protected Field() {
            }

            @Override
            public int priority() {
                return 10;
            }

            @Override
            public void apply(@NonNull HttpManager<HttpRequest, HttpResponse> httpManager,
                              @NonNull RequestModel requestModel,
                              @NonNull HttpRequestBuilder builder) {
                if (builder.isFormEncoded()) {
                    FormBody.Builder formBuilder = new FormBody.Builder();
                    for (RequestModel.Field field : requestModel.fields()) {
                        if (field.encoded()) {
                            formBuilder.addEncoded(field.name(), field.value());
                        } else {
                            formBuilder.add(field.name(), field.value());
                        }
                    }
                    builder.setFormBuilder(formBuilder);
                }
            }
        }

        class Multipart implements RequestBuilderHandler {

            private static Multipart instance = new Multipart();

            public static Multipart getInstance() {
                return instance;
            }

            protected Multipart() {
            }

            @Override
            public int priority() {
                return 11;
            }

            @Override
            public void apply(@NonNull HttpManager<HttpRequest, HttpResponse> httpManager,
                              @NonNull RequestModel requestModel,
                              @NonNull HttpRequestBuilder builder) {
                builder.setMultipart(requestModel.isMultipart());
            }
        }

        class Part implements RequestBuilderHandler {

            private static Part instance = new Part();

            public static Part getInstance() {
                return instance;
            }

            protected Part() {
            }

            @Override
            public int priority() {
                return 12;
            }

            @Override
            public void apply(@NonNull HttpManager<HttpRequest, HttpResponse> httpManager,
                              @NonNull RequestModel requestModel,
                              @NonNull HttpRequestBuilder builder) {
                if (builder.isMultipart()) {
                    MultipartBody.Builder multipartBuilder = new MultipartBody.Builder();
                    multipartBuilder.setType(MultipartBody.FORM);
                    for (RequestModel.Part part : requestModel.parts()) {
                        if (part.name().isEmpty()) {
                            multipartBuilder.addPart((MultipartBody.Part) part.data());
                        } else {
                            Headers headers = Headers.of("Content-Disposition",
                                    "form-data; name=\"" + part.name() + "\"",
                                    "Content-Transfer-Encoding",
                                    part.encoding());
                            multipartBuilder.addPart(headers, (RequestBody) part.data());
                        }
                    }
                    builder.setMultipartBuilder(multipartBuilder);
                }
            }
        }

        class FormEncoded implements RequestBuilderHandler {

            private static FormEncoded instance = new FormEncoded();

            public static FormEncoded getInstance() {
                return instance;
            }

            protected FormEncoded() {
            }

            @Override
            public int priority() {
                return 13;
            }

            @Override
            public void apply(@NonNull HttpManager<HttpRequest, HttpResponse> httpManager,
                              @NonNull RequestModel requestModel,
                              @NonNull HttpRequestBuilder builder) {
                builder.setFormEncoded(requestModel.isFormEncoded());
            }
        }

        class Streaming implements RequestBuilderHandler {

            private static Streaming instance = new Streaming();

            public static Streaming getInstance() {
                return instance;
            }

            protected Streaming() {
            }

            @Override
            public int priority() {
                return 14;
            }

            @Override
            public void apply(@NonNull HttpManager<HttpRequest, HttpResponse> httpManager,
                              @NonNull RequestModel requestModel,
                              @NonNull HttpRequestBuilder builder) {
                builder.setStreaming(requestModel.isStreaming());
            }
        }
    }

    private static class ContentTypeOverridingRequestBody extends RequestBody {

        private final RequestBody delegate;
        private final MediaType contentType;

        ContentTypeOverridingRequestBody(RequestBody delegate, MediaType contentType) {
            this.delegate = delegate;
            this.contentType = contentType;
        }

        @Override public MediaType contentType() {
            return contentType;
        }

        @Override public long contentLength() throws IOException {
            return delegate.contentLength();
        }

        @Override public void writeTo(BufferedSink sink) throws IOException {
            delegate.writeTo(sink);
        }
    }

    private static final class Encoder {

        private static final char[] HEX_DIGITS = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        private static final String PATH_SEGMENT_ALWAYS_ENCODE_SET = " \"<>^`{}|\\?#";

        Encoder() {
            throw new UnsupportedOperationException("u can't instantiate me...");
        }

        static String urlEncode(String url) {
            return urlEncode(url, "UTF-8");
        }

        static String urlEncode(String url, String charsetName) {
            if (url == null) {
                url = "";
            }
            try {
                url = URLEncoder.encode(url, charsetName);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return url;
        }

        static String canonicalizeForPath(String input, boolean alreadyEncoded) {
            int codePoint;
            for (int i = 0, limit = input.length(); i < limit; i += Character.charCount(codePoint)) {
                codePoint = input.codePointAt(i);
                if (codePoint < 0x20 || codePoint >= 0x7f
                        || PATH_SEGMENT_ALWAYS_ENCODE_SET.indexOf(codePoint) != -1
                        || (!alreadyEncoded && (codePoint == '/' || codePoint == '%'))) {
                    Buffer out = new Buffer();
                    out.writeUtf8(input, 0, i);
                    canonicalizeForPath(out, input, i, limit, alreadyEncoded);
                    return out.readUtf8();
                }
            }
            return input;
        }

        static void canonicalizeForPath(Buffer out, String input, int pos, int limit, boolean alreadyEncoded) {
            Buffer utf8Buffer = null;
            int codePoint;
            for (int i = pos; i < limit; i += Character.charCount(codePoint)) {
                codePoint = input.codePointAt(i);
                if (alreadyEncoded && (codePoint == '\t' || codePoint == '\n' || codePoint == '\f' || codePoint == '\r')) {
                    // noinspection UnnecessaryContinue
                    continue;
                } else if (codePoint < 0x20 || codePoint >= 0x7f
                        || PATH_SEGMENT_ALWAYS_ENCODE_SET.indexOf(codePoint) != -1
                        || (!alreadyEncoded && (codePoint == '/' || codePoint == '%'))) {
                    if (utf8Buffer == null) {
                        utf8Buffer = new Buffer();
                    }
                    utf8Buffer.writeUtf8CodePoint(codePoint);
                    while (!utf8Buffer.exhausted()) {
                        int b = utf8Buffer.readByte() & 0xff;
                        out.writeByte('%');
                        out.writeByte(HEX_DIGITS[(b >> 4) & 0xf]);
                        out.writeByte(HEX_DIGITS[b & 0xf]);
                    }
                } else {
                    out.writeUtf8CodePoint(codePoint);
                }
            }
        }
    }
}
