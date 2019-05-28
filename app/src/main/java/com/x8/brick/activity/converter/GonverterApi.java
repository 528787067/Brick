package com.x8.brick.activity.converter;

import com.x8.brick.annotation.define.method.GET;
import com.x8.brick.annotation.define.parameter.Query;
import com.x8.brick.annotation.define.type.Api;
import com.x8.brick.okhttp3.OkHttp3Task;

import okhttp3.Response;

@Api("http:192.168.31.100:8080/")
public interface GonverterApi {

    @GET("brick/user/get")
    OkHttp3Task<Response> getUser(@Query("name") String name, @Query("age") int age);

    @GET("brick/user/get")
    OkHttp3Task<ConverterData> getUserBean(@Query("name") String name, @Query("age") int age);

    @GET("brick/user/get")
    OkHttp3Task<String> getUserString(@Query("name") String name, @Query("age") int age);
}
