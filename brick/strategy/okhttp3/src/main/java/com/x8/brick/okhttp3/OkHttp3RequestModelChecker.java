package com.x8.brick.okhttp3;

import com.x8.brick.core.RequestModel;
import com.x8.brick.task.TaskModel;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class OkHttp3RequestModelChecker implements TaskModel.RequestModelChecker {

    private static final String PARAM = "[a-zA-Z][a-zA-Z0-9_-]*";
    private static final Pattern PARAM_URL_REGEX = Pattern.compile("\\{(" + PARAM + ")\\}");
    private static final Pattern PARAM_NAME_REGEX = Pattern.compile(PARAM);

    @Override
    public RequestModel checkRequestModel(RequestModel requestModel) {

        if (requestModel.hostNames().size() > 1) {
            throw new IllegalArgumentException(
                    "Found multiple host names, only one host name is allowed.");
        }
        if (requestModel.hosts().size() > 0) {
            Map<String, String> hosts = new HashMap<>();
            for (RequestModel.Host host : requestModel.hosts()) {
                String url = hosts.get(host.name);
                if (url != null && !url.equals(host.url)) {
                    throw new IllegalArgumentException(
                            "It is not allowed to define different URLs with the same host name.");
                }
                hosts.put(host.name, host.url);
            }
        }

        boolean hasField = requestModel.hasField();
        boolean hasPart = requestModel.hasPart();
        boolean hasBody = requestModel.hasBody();
        boolean hasPath = requestModel.hasPath();
        boolean hasQuery = requestModel.hasQuery();
        boolean hasUrl = requestModel.hasUrl();
        boolean isFormEncoded = requestModel.isFormEncoded();
        boolean isMultipart = requestModel.isMultipart();

        List<RequestModel.MethodPath> methodPaths = requestModel.methodPaths();
        if (methodPaths.size() < 1) {
            throw new IllegalArgumentException(
                    "HTTP method annotation is required (e.g., @GET, @POST, etc.).");
        }
        if (methodPaths.size() > 1) {
            throw new IllegalArgumentException(
                    "Found multiple HTTP methods, only one HTTP method is allowed.");
        }

        RequestModel.MethodPath methodPath = methodPaths.get(0);
        String httpMethod = methodPath.method;
        String httpPath = methodPath.path;
        boolean hasHttpBody = methodPath.hasBody;
        boolean hasHttpPath = httpPath != null && !httpPath.isEmpty();

        int question = hasHttpPath ? httpPath.indexOf('?') : -1;
        if (question != -1 && question < httpPath.length() - 1) {
            String queryParams = httpPath.substring(question + 1);
            Matcher queryParamMatcher = PARAM_URL_REGEX.matcher(queryParams);
            if (queryParamMatcher.find()) {
                throw new IllegalArgumentException(String.format(
                        "URL query string \"%s\" must not have replace block. "
                                + "For dynamic query parameters use @Query.", queryParams));
            }
        }

        if (!hasHttpBody) {
            if (isMultipart) {
                throw new IllegalArgumentException(
                        "Multipart can only be specified on HTTP methods with request body (e.g., @POST).");
            }
            if (isFormEncoded) {
                throw new IllegalArgumentException(
                        "FormUrlEncoded can only be specified on HTTP methods with request body (e.g., @POST).");
            }
        }
        if (isMultipart && isFormEncoded) {
            throw new IllegalArgumentException("Only one encoding annotation is allowed.");
        }
        if (hasUrl) {
            if (requestModel.urls().size() > 1) {
                throw new IllegalArgumentException("Multiple @Url method annotations found.");
            }
            if (hasPath) {
                throw new IllegalArgumentException("@Path parameters may not be used with @Url.");
            }
            if (hasHttpPath) {
                throw new IllegalArgumentException(String.format(
                        "@Url cannot be used with @%s URL", httpMethod));
            }
        }
        if (!hasUrl && !hasHttpPath) {
            throw new IllegalArgumentException(String.format(
                    "Missing either @%s URL or @Url parameter.", httpMethod));
        }
        if (hasPath) {
            if (hasQuery) {
                throw new IllegalArgumentException("A @Path parameter must not come after a @Query.");
            }
            if (hasUrl) {
                throw new IllegalArgumentException("@Path parameters may not be used with @Url.");
            }
            if (!hasHttpPath) {
                throw new IllegalArgumentException(String.format(
                        "@Path can only be used with relative url on @%s", httpMethod));
            }
            Matcher matcher = PARAM_URL_REGEX.matcher(httpPath);
            Set<String> patterns = new LinkedHashSet<>();
            while (matcher.find()) {
                patterns.add(matcher.group(1));
            }
            for (RequestModel.Path path : requestModel.paths()) {
                if (!PARAM_NAME_REGEX.matcher(path.name).matches()) {
                    throw new IllegalArgumentException(String.format(
                            "@Path parameter name must match %s. Found: %s", PARAM_URL_REGEX.pattern(), path.name));
                }
                if (!patterns.contains(path.name)) {
                    throw new IllegalArgumentException(String.format(
                            "URL \"%s\" does not contain \"{%s}\".", httpPath, path.name));
                }
            }
        }
        if (!isFormEncoded && !isMultipart && !hasHttpBody && hasBody) {
            throw new IllegalArgumentException("Non-body HTTP method cannot contain @Body.");
        }
        if (hasField && !isFormEncoded) {
            throw new IllegalArgumentException(
                    "@Field or @FieldMap parameters can only be used with form encoding.");
        }
        if (isFormEncoded && !hasField) {
            throw new IllegalArgumentException("Form-encoded method must contain at least one @Field.");
        }
        if (isMultipart && !hasPart) {
            throw new IllegalArgumentException("Multipart method must contain at least one @Part.");
        }
        if (hasPart && !isMultipart) {
            throw new IllegalArgumentException(
                    "@Part or @PartMap parameters can only be used with multipart encoding.");
        }
        if (hasBody) {
            if (requestModel.bodys().size() > 1) {
                throw new IllegalArgumentException("Multiple @Body method annotations found.");
            }
            if (isFormEncoded || isMultipart) {
                throw new IllegalArgumentException(
                        "@Body parameters cannot be used with form or multi-part encoding.");
            }
            for (Object body : requestModel.bodys()) {
                if (body == null) {
                    throw new IllegalArgumentException("Body parameter value must not be null.");
                }
                if (!(body instanceof RequestBody)) {
                    throw new IllegalArgumentException("Unable to convert " + body + " to RequestBody");
                }
            }
        }
        for (RequestModel.Header header : requestModel.headers()) {
            if ("Content-Type".equalsIgnoreCase(header.name) && MediaType.parse(header.value) == null) {
                throw new IllegalArgumentException(String.format("Malformed content type: %s", header.value));
            }
        }
        for (RequestModel.Part part : requestModel.parts()) {
            if (part.data == null) {
                throw new IllegalArgumentException("@Part or @PartMap parameter value must not be null.");
            }
            if (part.name.isEmpty()) {
                if (!(part.data instanceof MultipartBody.Part)) {
                    throw new IllegalArgumentException(
                            "@Part or @PartMap annotation must use MultipartBody.Part parameter type.");
                }
            } else {
                if (part.data instanceof MultipartBody.Part) {
                    throw new IllegalArgumentException(
                            "@Part or @PartMap parameters using the MultipartBody.Part must not "
                                    + "include a part name in the annotation.");
                }
                if (!(part.data instanceof RequestBody)) {
                    throw new IllegalArgumentException("Unable to convert " + part.data + " to RequestBody");
                }
            }
        }

        return requestModel;
    }
}
