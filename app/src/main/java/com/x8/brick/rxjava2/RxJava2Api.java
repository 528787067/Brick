package com.x8.brick.rxjava2;

import com.x8.brick.API;
import com.x8.brick.gson.ResponseBean;
import com.x8.brick.gson.UserBean;
import com.x8.brick.annotation.define.method.GET;
import com.x8.brick.annotation.define.method.POST;
import com.x8.brick.annotation.define.parameter.Query;
import com.x8.brick.annotation.define.type.Api;

import io.reactivex.Observable;

@Api(API.BASE_RUL)
public interface RxJava2Api {

    @GET("brick/user/get")
    Observable<ResponseBean<UserBean>> getUser(@Query("name") String name, @Query("age") int age);

    @POST("brick/user/post")
    Observable<ResponseBean<UserBean>> postUser(@Query("name") String name, @Query("age") int age);
}
