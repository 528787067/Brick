package com.x8.brick.converter;

import com.x8.brick.API;
import com.x8.brick.annotation.define.method.GET;
import com.x8.brick.annotation.define.parameter.Query;
import com.x8.brick.annotation.define.type.Api;
import com.x8.brick.okhttp3.HttpResponse;
import com.x8.brick.okhttp3.HttpTask;

@Api(API.BASE_RUL)
public interface GonverterApi {

    @GET("brick/user/get")
    HttpTask<HttpResponse> getUser(@Query("name") String name, @Query("age") int age);

    @GET("brick/user/get")
    HttpTask<ConverterData> getUserBean(@Query("name") String name, @Query("age") int age);

    @GET("brick/user/get")
    HttpTask<String> getUserString(@Query("name") String name, @Query("age") int age);
}
