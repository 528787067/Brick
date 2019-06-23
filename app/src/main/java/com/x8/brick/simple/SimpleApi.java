package com.x8.brick.simple;

import com.x8.brick.API;
import com.x8.brick.annotation.define.method.FormUrlEncoded;
import com.x8.brick.annotation.define.method.GET;
import com.x8.brick.annotation.define.method.POST;
import com.x8.brick.annotation.define.parameter.Field;
import com.x8.brick.annotation.define.parameter.Path;
import com.x8.brick.annotation.define.parameter.Query;
import com.x8.brick.annotation.define.type.Api;
import com.x8.brick.okhttp3.HttpResponse;
import com.x8.brick.okhttp3.HttpTask;

/**
 * 定义网络 API 接口，需要使用 @API 注解进行修饰
 * @Api("http:192.168.31.100:8080/") 可以指定 BASE_URL
 * 接口内部定义用于访问网络请求的方法，使用方法和 Retrofit 一致
 */
@Api(API.BASE_RUL)
public interface SimpleApi {

    @GET("brick/user/{path}")
    HttpTask<HttpResponse> pathUser(@Path("path") String path);

    @GET("brick/user/get")
    HttpTask<HttpResponse> getUser(@Query("name") String name, @Query("age") int age);

    @FormUrlEncoded
    @POST("brick/user/post")
    HttpTask<HttpResponse> postUser(@Field("name") String name, @Field("age") int age);
}
