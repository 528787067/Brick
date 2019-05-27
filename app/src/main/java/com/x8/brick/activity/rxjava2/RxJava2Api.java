package com.x8.brick.activity.rxjava2;

import com.x8.brick.activity.gson.ResponseBean;
import com.x8.brick.activity.gson.UserBean;
import com.x8.brick.annotation.define.method.GET;
import com.x8.brick.annotation.define.method.POST;
import com.x8.brick.annotation.define.parameter.Query;
import com.x8.brick.annotation.define.type.Api;

import io.reactivex.Observable;

@Api("http:192.168.31.100:8080/brick/user/")
public interface RxJava2Api {

    @GET("get")
    Observable<ResponseBean<UserBean>> getUser(@Query("name") String name, @Query("age") int age);

    @POST("post")
    Observable<ResponseBean<UserBean>> postUser(@Query("name") String name, @Query("age") int age);
}
