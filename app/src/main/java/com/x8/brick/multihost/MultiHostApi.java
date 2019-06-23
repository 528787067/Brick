package com.x8.brick.multihost;

import com.x8.brick.API;
import com.x8.brick.annotation.define.method.GET;
import com.x8.brick.annotation.define.method.POST;
import com.x8.brick.annotation.define.parameter.Field;
import com.x8.brick.annotation.define.parameter.Query;
import com.x8.brick.annotation.define.type.Api;
import com.x8.brick.okhttp3.HttpResponse;
import com.x8.brick.okhttp3.HttpTask;

/**
 * 网络请求接口需要使用 @API 注解进行修饰
 * {@param hostName} 用于配置当前默认使用的环境名称
 * API 注解中默认预设了 8 个常用的 host 可供直接使用
 * 如果预设的 host 无法满足需求，则可使用 {@param hosts} 配置自定义的 host，具体 host 定义规则： "hostName@url"
 */
@Api(
        hostName = "online",
        online = API.BASE_RUL + "brick/online/",
        sandbox = API.BASE_RUL + "brick/sandbox/",
        hosts = {
                "dev1@" + API.BASE_RUL + "brick/dev1/",
                "dev2@" + API.BASE_RUL + "brick/dev2/"
        }
)
public interface MultiHostApi {

    @GET("user/get")
    HttpTask<HttpResponse> getUser(@Query("name") String name, @Query("age") int age);

    @POST("user/post")
    HttpTask<HttpResponse> postUser(@Field("name") String name, @Field("age") int age);
}
