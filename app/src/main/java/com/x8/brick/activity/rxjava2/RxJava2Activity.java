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
import io.reactivex.disposables.Disposable;
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

        OkHttp3Client http3Client = new OkHttp3Client.Builder().build();
        OkHttp3Manager http3Manager = new OkHttp3Manager.Builder(http3Client)
                .addResponseConverter(new OkHttp3GsonResponseConverter<>())
                .addTaskConverter(new RxJava2Converter())
                .build();
        api = http3Manager.create(RxJava2Api.class);

        findViewById(R.id.get_user).setOnClickListener(this);
        findViewById(R.id.post_user).setOnClickListener(this);

        timestamp = (TextView) findViewById(R.id.timestamp);
        name = (TextView) findViewById(R.id.name);
        age = (TextView) findViewById(R.id.age);
        method = (TextView) findViewById(R.id.method);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.get_user:
                Observable<ResponseBean<UserBean>> getObservable = api.getUser("王二狗", 22);
                Disposable getDisposable = getObservable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
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
                break;
            case R.id.post_user:
                Observable<ResponseBean<UserBean>> postObservable = api.postUser("王小华", 18);
                Disposable postDisposable = postObservable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
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
                break;
        }
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
