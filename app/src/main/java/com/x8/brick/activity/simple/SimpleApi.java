package com.x8.brick.activity.simple;

import com.x8.brick.annotation.define.method.FormUrlEncoded;
import com.x8.brick.annotation.define.method.GET;
import com.x8.brick.annotation.define.method.POST;
import com.x8.brick.annotation.define.parameter.Field;
import com.x8.brick.annotation.define.parameter.Path;
import com.x8.brick.annotation.define.parameter.Query;
import com.x8.brick.annotation.define.type.Api;
import com.x8.brick.okhttp3.OkHttp3Task;

import okhttp3.Response;

/**
 * 定义网络 API 接口，需要使用 @Api 注解进行修饰
 * 接口内部定义用于访问网络请求的方法，使用方法和 Retrofit 一致
 */
@Api("http:192.168.31.100:8080/")
public interface SimpleApi {

    @GET("brick/user/{path}")
    OkHttp3Task<Response> pathUser(@Path("path") String path);

    @GET("brick/user/get")
    OkHttp3Task<Response> getUser(@Query("name") String name, @Query("age") int age);

    @FormUrlEncoded
    @POST("brick/user/post")
    OkHttp3Task<Response> postUser(@Field("name") String name, @Field("age") int age);
}
