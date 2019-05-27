package com.x8.brick.activity.task;

import com.x8.brick.activity.gson.ResponseBean;
import com.x8.brick.activity.gson.UserBean;
import com.x8.brick.annotation.define.method.GET;
import com.x8.brick.annotation.define.parameter.Query;
import com.x8.brick.annotation.define.type.Api;

import io.reactivex.Observable;
import com.x8.brick.activity.task.AsyncTaskConverter.AsyncTask;

@Api("http:192.168.31.100:8080/brick/user/")
public interface TaskApi {

    @GET("get")
    Observable<ResponseBean<UserBean>> rxUser(@Query("name") String name, @Query("age") int age);

    @GET("get")
    AsyncTask<ResponseBean<UserBean>> asyncUser(@Query("name") String name, @Query("age") int age);
}
