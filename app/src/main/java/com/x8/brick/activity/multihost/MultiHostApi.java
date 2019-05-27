package com.x8.brick.activity.multihost;

import com.x8.brick.annotation.define.method.GET;
import com.x8.brick.annotation.define.method.POST;
import com.x8.brick.annotation.define.parameter.Field;
import com.x8.brick.annotation.define.parameter.Query;
import com.x8.brick.annotation.define.type.Api;
import com.x8.brick.okhttp3.OkHttp3Task;

import okhttp3.Response;

/**
 * 网络请求接口需要使用 @Api 注解进行修饰
 * {@param hostName} 用于配置当前默认使用的环境名称
 * Api 注解中默认预设了 8 个常用的 host 可供直接使用
 * 如果预设的 host 无法满足需求，则可使用 {@param hosts} 配置自定义的 host，具体 host 定义规则： "hostName@url"
 */
@Api(
        hostName = "online",
        online = "http:192.168.31.100:8080/brick/online/",
        sandbox = "http:192.168.31.100:8080/brick/sandbox/",
        hosts = {
                "dev1@http:192.168.31.100:8080/brick/dev1/",
                "dev2@http:192.168.31.100:8080/brick/dev2/"
        }
)
public interface MultiHostApi {

    @GET("user/get")
    OkHttp3Task<Response> getUser(@Query("name") String name, @Query("age") int age);

    @POST("user/post")
    OkHttp3Task<Response> postUser(@Field("name") String name, @Field("age") int age);
}
