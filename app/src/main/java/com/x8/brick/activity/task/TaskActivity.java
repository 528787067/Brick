package com.x8.brick.activity.task;

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
import com.x8.brick.exception.HttpException;
import com.x8.brick.okhttp3.OkHttp3Client;
import com.x8.brick.okhttp3.OkHttp3Manager;
import com.x8.brick.okhttp3.converter.gson.OkHttp3GsonResponseConverter;
import com.x8.brick.task.Task;
import com.x8.brick.task.rxjava2.RxJava2Converter;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import com.x8.brick.activity.task.AsyncTaskConverter.AsyncTask;

public class TaskActivity extends AppCompatActivity {

    private TextView timestamp;
    private TextView name;
    private TextView age;
    private TextView method;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("自定义Task转换器");
        setContentView(R.layout.task_activity);

        timestamp = (TextView) findViewById(R.id.timestamp);
        name = (TextView) findViewById(R.id.name);
        age = (TextView) findViewById(R.id.age);
        method = (TextView) findViewById(R.id.method);

        OkHttp3Client http3Client = new OkHttp3Client.Builder().build();
        OkHttp3Manager http3Manager = new OkHttp3Manager.Builder(http3Client)
                .addResponseConverter(new OkHttp3GsonResponseConverter<>())
                .addTaskConverter(new AsyncTaskConverter())
                .addTaskConverter(new RxJava2Converter())
                .build();
        final TaskApi api = http3Manager.create(TaskApi.class);

        findViewById(R.id.rxjava2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Observable<ResponseBean<UserBean>> getObservable = api.rxUser("李小二", 18);
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
                                Log.e("TaskActivity", "onError --> " + throwable);
                            }
                        });
            }
        });
        findViewById(R.id.async_task).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AsyncTask<ResponseBean<UserBean>> task = api.asyncUser("王小红", 27);
                task.asyncExecute(new Task.Callback<ResponseBean<UserBean>>() {
                    @Override
                    public void onSuccess(Task task, ResponseBean<UserBean> data) {
                        showResult(data);
                    }
                    @Override
                    public void onFailure(Task task, HttpException exception) {
                        Log.e("TaskActivity", "onFailure --> " + exception);
                    }
                });
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
