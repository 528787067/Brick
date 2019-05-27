package com.x8.brick.activity.converter;

import com.x8.brick.annotation.define.method.GET;
import com.x8.brick.annotation.define.parameter.Query;
import com.x8.brick.annotation.define.type.Api;
import com.x8.brick.okhttp3.OkHttp3Task;

@Api("http:192.168.31.100:8080/brick/user/")
public interface GonverterApi {

    @GET("get")
    OkHttp3Task<ConverterData> getUserBean(@Query("name") String name, @Query("age") int age);

    @GET("get")
    OkHttp3Task<String> getUserString(@Query("name") String name, @Query("age") int age);
}
