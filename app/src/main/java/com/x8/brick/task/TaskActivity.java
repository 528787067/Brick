package com.x8.brick.task;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.x8.brick.R;
import com.x8.brick.gson.ResponseBean;
import com.x8.brick.gson.UserBean;
import com.x8.brick.exception.HttpException;
import com.x8.brick.okhttp3.HttpClient;
import com.x8.brick.okhttp3.HttpManager;
import com.x8.brick.okhttp3.HttpTask;
import com.x8.brick.okhttp3.converter.gson.HttpGsonResponseConverter;
import com.x8.brick.task.rxjava2.RxJava2Converter;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Callback;
import okhttp3.Dispatcher;

import com.x8.brick.task.AsyncTaskConverter.AsyncTask;

public class TaskActivity extends AppCompatActivity implements View.OnClickListener {

    private TaskApi taskApi;

    private TextView timestamp;
    private TextView name;
    private TextView age;
    private TextView method;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("自定义Task转换器");
        setContentView(R.layout.task_activity);

        this.timestamp = (TextView) findViewById(R.id.timestamp);
        this.name = (TextView) findViewById(R.id.name);
        this.age = (TextView) findViewById(R.id.age);
        this.method = (TextView) findViewById(R.id.method);

        findViewById(R.id.get_user).setOnClickListener(this);
        findViewById(R.id.rxjava2).setOnClickListener(this);
        findViewById(R.id.async_task).setOnClickListener(this);

        HttpClient httpClient = new HttpClient.Builder()
                .setOkhttpEnqueueStrategy(false) // 不使用 okhttp 的 enqueue 作为异步策略
                .build();
        HttpManager httpManager = new HttpManager.Builder(httpClient)
                .addResponseConverter(new HttpGsonResponseConverter<>()) // 指定使用 Gson 转换器将响应转换成实体对象
                .addTaskConverter(new AsyncTaskConverter()) // 指定 AsyncTaskConverter 转换器将 Task 转换成 AsyncTask
                .addTaskConverter(new RxJava2Converter()) // 指定 RxJava2Converter 转换器将 Task 转换成 Observable
                .build();
        this.taskApi = httpManager.create(TaskApi.class);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.get_user:
                getRequest();       // 执行默认网络请求
                break;
            case R.id.rxjava2:
                rxjava2Request();   // 使用 RxJava2 执行网络请求
                break;
            case R.id.async_task:
                asyncTaskRequest(); // 使用 AsyncTask 执行网络请求
                break;
        }
    }

    /**
     * 使用默认的 {@link HttpTask} 执行网络请求
     * 默认使用的是 OkHttp 线程池执行网络请求 {@link Dispatcher#executorService()}
     * 构建 {@link HttpClient} 的时候可以使用 {@link HttpClient.Builder#okhttpEnqueueStrategy()}
     * 设置是否使用 {@link okhttp3.Call#enqueue(Callback)} 方法作为异步策略，默认为 {@param false}
     * 注意：此回调不在 UI 线程，更新 UI 需要切换到 UI 线程进行更新
     */
    private void getRequest() {
        HttpTask<ResponseBean<UserBean>> task = taskApi.getUser("江小白", 15);
        task.asyncExecute(new Task.Callback<ResponseBean<UserBean>>() {
            @Override
            public void onSuccess(Task task, final ResponseBean<UserBean> data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showResult(data);
                    }
                });
            }
            @Override
            public void onFailure(Task task, HttpException exception) {
                Log.e("TaskActivity", "onFailure --> " + exception);
            }
        });
    }

    /**
     * 使用 RxJava2 执行网络请求
     * {@link RxJava2Converter} 转换器会将 Task 转换成 RxJava2 对应的类型（{@link Observable}）
     * 网络请求执行线程为 IO 线程（{@link Schedulers#io()}）
     * 网络请求结果回调线程为 UI 线程（{@link AndroidSchedulers#mainThread()}）
     */
    @SuppressLint("CheckResult")
    private void rxjava2Request() {
        Observable<ResponseBean<UserBean>> observable = taskApi.rxUser("李小二", 18);
        observable.subscribeOn(Schedulers.io()) // 设置网络请求线程为 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 设置请求回调线程为 UI 线程
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

    /**
     * 使用自定义的 {@link AsyncTaskConverter.AsyncTask} 执行网络请求
     * {@link AsyncTaskConverter} 转化器会将 Task 转换成 {@link AsyncTaskConverter.AsyncTask}
     * {@link AsyncTaskConverter.AsyncTask} 使用的是 {@link android.os.AsyncTask} 作为异步策略
     */
    private void asyncTaskRequest() {
        AsyncTask<ResponseBean<UserBean>> task = taskApi.asyncUser("王小红", 27);
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

    @SuppressLint("SetTextI18n")
    private void showResult(ResponseBean<UserBean> data) {
        UserBean user = data.getData();
        timestamp.setText("时间戳: " + data.getTimestamp());
        name.setText("姓名: " + user.getName());
        age.setText("年龄: " + user.getAge());
        method.setText("请求类型: " + user.getMethod());
    }
}
