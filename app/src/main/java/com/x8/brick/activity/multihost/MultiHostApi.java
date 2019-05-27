package com.x8.brick.activity.multihost;

import com.x8.brick.annotation.define.method.GET;
import com.x8.brick.annotation.define.method.POST;
import com.x8.brick.annotation.define.parameter.Field;
import com.x8.brick.annotation.define.parameter.Query;
import com.x8.brick.annotation.define.type.Api;
import com.x8.brick.okhttp3.OkHttp3Task;

import okhttp3.Response;

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
