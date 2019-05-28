package com.x8.brick.activity.rxjava2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.x8.brick.R;
import com.x8.brick.activity.gson.ResponseBean;
import com.x8.brick.activity.gson.UserBean;
import com.x8.brick.okhttp3.OkHttp3Client;
import com.x8.brick.okhttp3.OkHttp3Manager;
import com.x8.brick.okhttp3.converter.gson.OkHttp3GsonResponseConverter;
import com.x8.brick.task.rxjava2.RxJava2Converter;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class RxJava2Activity extends AppCompatActivity implements View.OnClickListener {

    private RxJava2Api api;

    private TextView timestamp;
    private TextView name;
    private TextView age;
    private TextView method;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("RxJava2转换器");
        setContentView(R.layout.rxjava2_activity);

        findViewById(R.id.get_user).setOnClickListener(this);
        findViewById(R.id.post_user).setOnClickListener(this);

        timestamp = (TextView) findViewById(R.id.timestamp);
        name = (TextView) findViewById(R.id.name);
        age = (TextView) findViewById(R.id.age);
        method = (TextView) findViewById(R.id.method);

        OkHttp3Client http3Client = new OkHttp3Client.Builder().build();
        OkHttp3Manager http3Manager = new OkHttp3Manager.Builder(http3Client)
                .addResponseConverter(new OkHttp3GsonResponseConverter<>()) // 添加 Gson 转化器，将响应对象转换成实体对象
                .addTaskConverter(new RxJava2Converter()) // 添加 RxJava2 转换器，将 task 转换成 Observable
                .build();
        api = http3Manager.create(RxJava2Api.class);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.get_user:
                getUser();
                break;
            case R.id.post_user:
                postUser();
                break;
        }
    }

    /**
     * 使用 RxJava2 执行 get 请求
     * {@link RxJava2Converter} 转换器会将 Task 转换成 RxJava2 对应的类型（{@link Observable}）
     * 网络请求执行线程为 IO 线程（{@link Schedulers#io()}）
     * 网络请求结果回调线程为 UI 线程（{@link AndroidSchedulers#mainThread()}）
     */
    @SuppressLint("CheckResult")
    private void getUser() {
        Observable<ResponseBean<UserBean>> getObservable = api.getUser("王二狗", 22);
        getObservable.subscribeOn(Schedulers.io()) // 设置网络请求线程为 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 设置请求回调线程为 UI 线程
                .subscribe(new Consumer<ResponseBean<UserBean>>() {
                    @Override
                    public void accept(ResponseBean<UserBean> data) throws Exception {
                        showResult(data);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("RxJava2Activity", "onError --> " + throwable);
                    }
                });
    }


    /**
     * 使用 RxJava2 执行 post 请求
     * {@link RxJava2Converter} 转换器会将 Task 转换成 RxJava2 对应的类型（{@link Observable}）
     * 网络请求执行线程为 IO 线程（{@link Schedulers#io()}）
     * 网络请求结果回调线程为 UI 线程（{@link AndroidSchedulers#mainThread()}）
     */
    @SuppressLint("CheckResult")
    private void postUser() {
        Observable<ResponseBean<UserBean>> postObservable = api.postUser("王小华", 18);
        postObservable.subscribeOn(Schedulers.io()) // 设置网络请求线程为 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 设置请求回调线程为 UI 线程
                .subscribe(new Consumer<ResponseBean<UserBean>>() {
                    @Override
                    public void accept(ResponseBean<UserBean> data) throws Exception {
                        showResult(data);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("RxJava2Activity", "onError --> " + throwable);
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    private void showResult(ResponseBean<UserBean> data) {
        UserBean user = data.getData();
        timestamp.setText("时间戳: " + data.getTimestamp());
        name.setText("姓名: " + user.getName());
        age.setText("年龄: " + user.getAge());
        method.setText("请求类型: " + user.getMethod());
    }
}
