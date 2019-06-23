package com.x8.brick.core;

import android.support.annotation.NonNull;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RequestModel {

    @StringDef({
            HostName.DEFAULT,
            HostName.RELEASE,
            HostName.DEBUG,
            HostName.ONLINE,
            HostName.DEV,
            HostName.TEST,
            HostName.SANDBOX,
            HostName.PRODUCT,
            HostName.PREVIEW
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface HostName {
        String DEFAULT = "default";
        String RELEASE = "release";
        String DEBUG = "debug";
        String ONLINE = "online";
        String DEV = "dev";
        String TEST = "test";
        String SANDBOX = "sandbox";
        String PRODUCT = "product";
        String PREVIEW = "preview";
    }

    @StringDef({
            HttpMethod.GET,
            HttpMethod.POST,
            HttpMethod.PUT,
            HttpMethod.HEAD,
            HttpMethod.DELETE,
            HttpMethod.OPTIONS,
            HttpMethod.PATCH
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface HttpMethod {
        String GET = "GET";
        String POST = "POST";
        String PUT = "PUT";
        String HEAD = "HEAD";
        String DELETE = "DELETE";
        String OPTIONS = "OPTIONS";
        String PATCH = "PATCH";
    }

    private List<String> hostNames;
    private List<Host> hosts;
    private List<MethodPath> methodPaths;
    private List<Path> paths;
    private List<Query> querys;
    private List<Field> fields;
    private List<Header> headers;
    private List<Part> parts;
    private List<String> urls;
    private List<Object> bodys;
    private List<Boolean> formUrlEncodeds;
    private List<Boolean> multiparts;
    private List<Boolean> streamings;
    private Map<Object, Object> extras;

    RequestModel() {
        this(null);
    }

    RequestModel(RequestModel model) {
        hostNames = new LinkedList<>();
        hosts = new LinkedList<>();
        methodPaths = new LinkedList<>();
        paths = new LinkedList<>();
        querys = new LinkedList<>();
        fields = new LinkedList<>();
        headers = new LinkedList<>();
        parts = new LinkedList<>();
        urls = new LinkedList<>();
        bodys = new LinkedList<>();
        formUrlEncodeds = new LinkedList<>();
        multiparts = new LinkedList<>();
        streamings = new LinkedList<>();
        extras = new HashMap<>();
        addModel(model);
    }

    public RequestModel addHostName(String hostName) {
        return add(this.hostNames, hostName);
    }

    public RequestModel addHostNames(String... hostNames) {
        return addAll(this.hostNames, hostNames);
    }

    public RequestModel addHostNames(@NonNull Iterable<String> hostNames) {
        return addAll(this.hostNames, hostNames);
    }

    public RequestModel addHost(String name, String url) {
        return add(this.hosts, new Host(name, url));
    }

    public RequestModel addHosts(Host... hosts) {
        return addAll(this.hosts, hosts);
    }

    public RequestModel addHosts(@NonNull Iterable<Host> hosts) {
        return addAll(this.hosts, hosts);
    }

    public RequestModel addMethodPath(@HttpMethod String method, String path, boolean hasBody) {
        return add(this.methodPaths, new MethodPath(method, path, hasBody));
    }

    public RequestModel addMethodPaths(MethodPath... methodPaths) {
        return addAll(this.methodPaths, methodPaths);
    }

    public RequestModel addMethodPaths(@NonNull Iterable<MethodPath> methodPaths) {
        return addAll(this.methodPaths, methodPaths);
    }

    public RequestModel addPath(String name, String value, boolean encoded) {
        return add(this.paths, new Path(name, value, encoded));
    }

    public RequestModel addPaths(Path... paths) {
        return addAll(this.paths, paths);
    }

    public RequestModel addPaths(@NonNull Iterable<Path> paths) {
        return addAll(this.paths, paths);
    }

    public RequestModel addPaths(@NonNull Map<String, String> paths, boolean encoded) {
        for (Map.Entry<String, String> data : paths.entrySet()) {
            addPath(data.getKey(), data.getValue(), encoded);
        }
        return this;
    }

    public RequestModel addQuery(String name, String value, boolean encoded) {
        return add(this.querys, new Query(name, value, encoded));
    }

    public RequestModel addQuerys(Query... querys) {
        return addAll(this.querys, querys);
    }

    public RequestModel addQuerys(@NonNull Iterable<Query> querys) {
        return addAll(this.querys, querys);
    }

    public RequestModel addQuerys(@NonNull Map<String, String> querys, boolean encoded) {
        for (Map.Entry<String, String> data : querys.entrySet()) {
            addQuery(data.getKey(), data.getValue(), encoded);
        }
        return this;
    }

    public RequestModel addField(String name, String value, boolean encoded) {
        return this.add(this.fields, new Field(name, value, encoded));
    }

    public RequestModel addFields(Field... fields) {
        return addAll(this.fields, fields);
    }

    public RequestModel addFields(@NonNull Iterable<Field> fields) {
        return addAll(this.fields, fields);
    }

    public RequestModel addFields(@NonNull Map<String, String> fields, boolean encoded) {
        for (Map.Entry<String, String> data : fields.entrySet()) {
            addField(data.getKey(), data.getValue(), encoded);
        }
        return this;
    }

    public RequestModel addHeader(String name, String value) {
        return add(this.headers, new Header(name, value));
    }

    public RequestModel addHeaders(Header... headers) {
        return addAll(this.headers, headers);
    }

    public RequestModel addHeaders(@NonNull Iterable<Header> headers) {
        return addAll(this.headers, headers);
    }

    public RequestModel addHeaders(@NonNull Map<String, String> headers) {
        for (Map.Entry<String, String> data : headers.entrySet()) {
            addHeader(data.getKey(), data.getValue());
        }
        return this;
    }

    public RequestModel addPart(String name, String encoding, Object data) {
        this.parts.add(new Part(name, encoding, data));
        return this;
    }

    public RequestModel addParts(@NonNull Part... parths) {
        return addAll(this.parts, parths);
    }

    public RequestModel addParts(@NonNull Iterable<Part> parths) {
        return addAll(this.parts, parths);
    }

    public RequestModel addParts(String encoding, @NonNull Map<String, Object> parths) {
        Set<Map.Entry<String, Object>> set = parths.entrySet();
        for (Map.Entry<String, Object> data : set) {
            addPart(data.getKey(), encoding, data.getValue());
        }
        return this;
    }

    public RequestModel addUrl(String url) {
        return add(this.urls, url);
    }

    public RequestModel addUrls(String... urls) {
        return addAll(this.urls, urls);
    }

    public RequestModel addUrls(@NonNull Iterable<String> urls) {
        return addAll(this.urls, urls);
    }

    public RequestModel addBody(Object body) {
        return add(this.bodys, body);
    }

    public RequestModel addBodys(Object... bodys) {
        return addAll(this.bodys, bodys);
    }

    public RequestModel addBodys(@NonNull Iterable<Object> bodys) {
        return addAll(this.bodys, bodys);
    }

    public RequestModel addFormUrlEncoded(boolean formUrlEncoded) {
        return add(this.formUrlEncodeds, formUrlEncoded);
    }

    public RequestModel addFormUrlEncodeds(Boolean... formUrlEncodeds) {
        return addAll(this.formUrlEncodeds, formUrlEncodeds);
    }

    public RequestModel addFormUrlEncodeds(@NonNull Iterable<Boolean> formUrlEncodeds) {
        return addAll(this.formUrlEncodeds, formUrlEncodeds);
    }

    public RequestModel addMultipart(boolean multipart) {
        return add(this.multiparts, multipart);
    }

    public RequestModel addMultiparts(Boolean... multiparts) {
        return addAll(this.multiparts, multiparts);
    }

    public RequestModel addMultiparts(@NonNull Iterable<Boolean> multiparts) {
        return addAll(this.multiparts, multiparts);
    }

    public RequestModel addStreaming(boolean streaming) {
        return add(this.streamings, streaming);
    }

    public RequestModel addStreamings(Boolean... streamings) {
        return addAll(this.streamings, streamings);
    }

    public RequestModel addStreamings(@NonNull Iterable<Boolean> streamings) {
        return addAll(this.streamings, streamings);
    }

    public <K, V> RequestModel putExtra(K key, V value) {
        this.extras.put(key, value);
        return this;
    }

    public <K, V> RequestModel putExtras(@NonNull Map<K, V> extras) {
        this.extras.putAll(extras);
        return this;
    }

    public List<String> hostNames() {
        return hostNames;
    }

    public List<Host> hosts() {
        return hosts;
    }

    public List<MethodPath> methodPaths() {
        return methodPaths;
    }

    public List<Path> paths() {
        return paths;
    }

    public List<Query> querys() {
        return querys;
    }

    public List<Field> fields() {
        return fields;
    }

    public List<Header> headers() {
        return headers;
    }

    public List<Part> parts() {
        return parts;
    }

    public List<String> urls() {
        return urls;
    }

    public List<Object> bodys() {
        return bodys;
    }

    public List<Boolean> formUrlEncodeds() {
        return formUrlEncodeds;
    }

    public List<Boolean> multiparts() {
        return multiparts;
    }

    public List<Boolean> streamings() {
        return streamings;
    }

    public <K, V> V getExtra(K key) {
        // noinspection unchecked
        return (V) extras.get(key);
    }

    public Map<?, ?> extras() {
        return extras;
    }

    public boolean hasPath() {
        return paths.size() > 0;
    }

    public boolean hasQuery() {
        return querys.size() > 0;
    }

    public boolean hasField() {
        return fields.size() > 0;
    }

    public boolean hasHeader() {
        return headers.size() > 0;
    }

    public boolean hasPart() {
        return parts.size() > 0;
    }

    public boolean hasUrl() {
        return urls.size() > 0;
    }

    public boolean hasBody() {
        return bodys.size() > 0;
    }

    public boolean hasExtra() {
        return extras.size() > 0;
    }

    public boolean isFormEncoded() {
        return formUrlEncodeds.size() > 0;
    }

    public boolean isMultipart() {
        return multiparts.size() > 0;
    }

    public boolean isStreaming() {
        return streamings.size() > 0;
    }

    public RequestModel clearHostNames() {
        return clear(hostNames);
    }

    public RequestModel clearHosts() {
        return clear(hosts);
    }

    public RequestModel clearMethodPaths() {
        return clear(methodPaths);
    }

    public RequestModel clearPaths() {
        return clear(paths);
    }

    public RequestModel clearQuerys() {
        return clear(querys);
    }

    public RequestModel clearFields() {
        return clear(fields);
    }

    public RequestModel clearHeaders() {
        return clear(headers);
    }

    public RequestModel clearParts() {
        return clear(parts);
    }

    public RequestModel clearUrls() {
        return clear(urls);
    }

    public RequestModel clearBodys() {
        return clear(bodys);
    }

    public RequestModel clearFormUrlEncodeds() {
        return clear(formUrlEncodeds);
    }

    public RequestModel clearMultiparts() {
        return clear(multiparts);
    }

    public RequestModel clearStreamings() {
        return clear(streamings);
    }

    public RequestModel clearExtras() {
        extras.clear();
        return this;
    }

    public RequestModel clear() {
        return clearHostNames()
                .clearHosts()
                .clearMethodPaths()
                .clearPaths()
                .clearQuerys()
                .clearFields()
                .clearHeaders()
                .clearParts()
                .clearUrls()
                .clearBodys()
                .clearFormUrlEncodeds()
                .clearMultiparts()
                .clearStreamings()
                .clearExtras();
    }

    public RequestModel newModel() {
        return new RequestModel(this);
    }

    public RequestModel addModel(RequestModel model) {
        if (model != null) {
            addHostNames(model.hostNames)
                    .addHosts(model.hosts)
                    .addMethodPaths(model.methodPaths)
                    .addPaths(model.paths)
                    .addQuerys(model.querys)
                    .addFields(model.fields)
                    .addHeaders(model.headers)
                    .addParts(model.parts)
                    .addUrls(model.urls)
                    .addBodys(model.bodys)
                    .addFormUrlEncodeds(model.formUrlEncodeds)
                    .addMultiparts(model.multiparts)
                    .addStreamings(model.streamings)
                    .putExtras(model.extras);
        }
        return this;
    }

    @Override
    public String toString() {
        return "RequestModel{"
                + "hostNames=" + hostNames
                + ", hosts=" + hosts
                + ", methodPaths=" + methodPaths
                + ", paths=" + paths
                + ", querys=" + querys
                + ", fields=" + fields
                + ", headers=" + headers
                + ", parts=" + parts
                + ", urls=" + urls
                + ", bodys=" + bodys
                + ", formUrlEncodeds=" + formUrlEncodeds
                + ", multiparts=" + multiparts
                + ", streamings=" + streamings
                + ", extras=" + extras
                + '}';
    }

    private <T> RequestModel add(@NonNull List<T> params, T datas) {
        params.add(datas);
        return this;
    }

    private <T> RequestModel addAll(@NonNull List<T> params, T... datas) {
        params.addAll(toList(datas));
        return this;
    }

    private <T> RequestModel addAll(@NonNull List<T> params, Iterable<T> datas) {
        params.addAll(toList(datas));
        return this;
    }

    private <T> RequestModel clear(@NonNull List<T> params) {
        params.clear();
        return this;
    }

    private static <T> List<T> toList(T... datas) {
        return Arrays.asList(datas);
    }

    private static <T> List<T> toList(Iterable<T> iterable) {
        List<T> datas = new LinkedList<>();
        if (iterable != null) {
            for (T data : iterable) {
                datas.add(data);
            }
        }
        return datas;
    }

    public static class Host {

        private String name;
        private String url;

        public Host(String name, String url) {
            this.name = name;
            this.url = url;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String name() {
            return name;
        }

        public String url() {
            return url;
        }

        @Override
        public String toString() {
            return "Host{"
                    + "name='" + name + '\''
                    + ", url='" + url + '\''
                    + '}';
        }
    }

    public static class MethodPath {

        private String method;
        private String path;
        private boolean hasBody;

        public MethodPath(@NonNull @HttpMethod String method, String path, boolean hasBody) {
            this.method = method;
            this.path = path;
            this.hasBody = hasBody;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public void setHasBody(boolean hasBody) {
            this.hasBody = hasBody;
        }

        public String method() {
            return method;
        }

        public String path() {
            return path;
        }

        public boolean hasBody() {
            return hasBody;
        }

        @Override
        public String toString() {
            return "MethodPath{"
                    + "method='" + method + '\''
                    + ", path='" + path + '\''
                    + ", hasBody=" + hasBody
                    + '}';
        }
    }

    public static class Path {

        private String name;
        private String value;
        private boolean encoded;

        public Path(String name, String value, boolean encoded) {
            this.name = name;
            this.value = value;
            this.encoded = encoded;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public void setEncoded(boolean encoded) {
            this.encoded = encoded;
        }

        public String name() {
            return name;
        }

        public String value() {
            return value;
        }

        public boolean encoded() {
            return encoded;
        }

        @Override
        public String toString() {
            return "Path{" +
                    "name='" + name + '\'' +
                    ", value='" + value + '\'' +
                    ", encoded=" + encoded +
                    '}';
        }
    }

    public static class Query {

        private String name;
        private String value;
        private boolean encoded;

        public Query(String name, String value, boolean encoded) {
            this.name = name;
            this.value = value;
            this.encoded = encoded;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public void setEncoded(boolean encoded) {
            this.encoded = encoded;
        }

        public String name() {
            return name;
        }

        public String value() {
            return value;
        }

        public boolean encoded() {
            return encoded;
        }

        @Override
        public String toString() {
            return "Query{" +
                    "name='" + name + '\'' +
                    ", value='" + value + '\'' +
                    ", encoded=" + encoded +
                    '}';
        }
    }

    public static class Field {

        private String name;
        private String value;
        private boolean encoded;

        public Field(String name, String value, boolean encoded) {
            this.name = name;
            this.value = value;
            this.encoded = encoded;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public void setEncoded(boolean encoded) {
            this.encoded = encoded;
        }

        public String name() {
            return name;
        }

        public String value() {
            return value;
        }

        public boolean encoded() {
            return encoded;
        }

        @Override
        public String toString() {
            return "Field{" +
                    "name='" + name + '\'' +
                    ", value='" + value + '\'' +
                    ", encoded=" + encoded +
                    '}';
        }
    }

    public static class Header {

        private String name;
        private String value;

        public Header(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String name() {
            return name;
        }

        public String value() {
            return value;
        }

        @Override
        public String toString() {
            return "Header{" +
                    "name='" + name + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

    public static class QueryName {

        private String value;
        private boolean encoded;

        public QueryName(String value, boolean encoded) {
            this.value = value;
            this.encoded = encoded;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public void setEncoded(boolean encoded) {
            this.encoded = encoded;
        }

        public String value() {
            return value;
        }

        public boolean encoded() {
            return encoded;
        }

        @Override
        public String toString() {
            return "QueryName{" +
                    ", value='" + value + '\'' +
                    ", encoded=" + encoded +
                    '}';
        }
    }

    public static class Part {

        private String name;
        private String encoding;
        private Object data;

        public Part(String name, String encoding, Object data) {
            this.name = name;
            this.encoding = encoding;
            this.data = data;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setEncoding(String encoding) {
            this.encoding = encoding;
        }

        public void setData(Object data) {
            this.data = data;
        }

        public String name() {
            return name;
        }

        public String encoding() {
            return encoding;
        }

        public Object data() {
            return data;
        }

        @Override
        public String toString() {
            return "Part{"
                    + "name='" + name + '\''
                    + ", encoding='" + encoding + '\''
                    + ", data=" + data
                    + '}';
        }
    }
}
