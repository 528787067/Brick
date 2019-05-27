package com.x8.brick.activity.task;

import com.x8.brick.activity.gson.ResponseBean;
import com.x8.brick.activity.gson.UserBean;
import com.x8.brick.annotation.define.method.GET;
import com.x8.brick.annotation.define.parameter.Query;
import com.x8.brick.annotation.define.type.Api;

import io.reactivex.Observable;
import com.x8.brick.activity.task.AsyncTaskConverter.AsyncTask;
import com.x8.brick.okhttp3.OkHttp3Task;

@Api("http:192.168.31.100:8080/")
public interface TaskApi {

    @GET("brick/user/get")
    OkHttp3Task<ResponseBean<UserBean>> getUser(@Query("name") String name, @Query("age") int age);

    @GET("brick/user/get")
    Observable<ResponseBean<UserBean>> rxUser(@Query("name") String name, @Query("age") int age);

    @GET("brick/user/get")
    AsyncTask<ResponseBean<UserBean>> asyncUser(@Query("name") String name, @Query("age") int age);
}
