package com.x8.brick.activity.gson;

import com.x8.brick.annotation.define.method.GET;
import com.x8.brick.annotation.define.parameter.Query;
import com.x8.brick.annotation.define.type.Api;
import com.x8.brick.okhttp3.OkHttp3Task;

@Api("http:192.168.31.100:8080/")
public interface GsonApi {

    @GET("brick/user/get")
    OkHttp3Task<ResponseBean<UserBean>> getUser(@Query("name") String name, @Query("age") int age);
}
