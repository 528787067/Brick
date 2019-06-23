package com.x8.brick.task;

import com.x8.brick.API;
import com.x8.brick.gson.ResponseBean;
import com.x8.brick.gson.UserBean;
import com.x8.brick.annotation.define.method.GET;
import com.x8.brick.annotation.define.parameter.Query;
import com.x8.brick.annotation.define.type.Api;

import io.reactivex.Observable;
import com.x8.brick.task.AsyncTaskConverter.AsyncTask;
import com.x8.brick.okhttp3.HttpTask;

@Api(API.BASE_RUL)
public interface TaskApi {

    @GET("brick/user/get")
    HttpTask<ResponseBean<UserBean>> getUser(@Query("name") String name, @Query("age") int age);

    @GET("brick/user/get")
    Observable<ResponseBean<UserBean>> rxUser(@Query("name") String name, @Query("age") int age);

    @GET("brick/user/get")
    AsyncTask<ResponseBean<UserBean>> asyncUser(@Query("name") String name, @Query("age") int age);
}
