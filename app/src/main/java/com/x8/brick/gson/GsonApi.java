package com.x8.brick.gson;

import com.x8.brick.API;
import com.x8.brick.annotation.define.method.GET;
import com.x8.brick.annotation.define.parameter.Query;
import com.x8.brick.annotation.define.type.Api;
import com.x8.brick.okhttp3.HttpTask;

@Api(API.BASE_RUL)
public interface GsonApi {

    @GET("brick/user/get")
    HttpTask<ResponseBean<UserBean>> getUser(@Query("name") String name, @Query("age") int age);
}
