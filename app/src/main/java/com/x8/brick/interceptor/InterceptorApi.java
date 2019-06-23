package com.x8.brick.interceptor;

import com.x8.brick.API;
import com.x8.brick.annotation.define.method.FormUrlEncoded;
import com.x8.brick.annotation.define.method.GET;
import com.x8.brick.annotation.define.method.POST;
import com.x8.brick.annotation.define.parameter.Field;
import com.x8.brick.annotation.define.parameter.Query;
import com.x8.brick.annotation.define.type.Api;
import com.x8.brick.okhttp3.HttpResponse;
import com.x8.brick.okhttp3.HttpTask;

@Api(API.BASE_RUL)
public interface InterceptorApi {

    @GET("brick/user/get")
    HttpTask<HttpResponse> getUser(@Query("name") String name, @Query("age") int age);

    @FormUrlEncoded
    @POST("brick/user/post")
    HttpTask<HttpResponse> postUser(@Field("name") String name, @Field("age") int age);
}
