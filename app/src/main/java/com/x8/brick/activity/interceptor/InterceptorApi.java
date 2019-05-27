package com.x8.brick.activity.interceptor;

import com.x8.brick.annotation.define.method.FormUrlEncoded;
import com.x8.brick.annotation.define.method.GET;
import com.x8.brick.annotation.define.method.POST;
import com.x8.brick.annotation.define.parameter.Field;
import com.x8.brick.annotation.define.parameter.Query;
import com.x8.brick.annotation.define.type.Api;
import com.x8.brick.okhttp3.OkHttp3Task;

import okhttp3.Response;

@Api("http:192.168.31.100:8080/brick/user/")
public interface InterceptorApi {

    @GET("get")
    OkHttp3Task<Response> getUser(@Query("name") String name, @Query("age") int age);

    @FormUrlEncoded
    @POST("post")
    OkHttp3Task<Response> postUser(@Field("name") String name, @Field("age") int age);
}
