package com.x8.brick.activity.simple;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.x8.brick.R;
import com.x8.brick.exception.HttpException;
import com.x8.brick.okhttp3.OkHttp3Client;
import com.x8.brick.okhttp3.OkHttp3Manager;
import com.x8.brick.okhttp3.OkHttp3Task;
import com.x8.brick.task.Task;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Response;

public class SimpleActivity extends AppCompatActivity implements View.OnClickListener, Task.Callback<Response> {

    /**
     * 使用的基本步骤：
     * 1、定义 API 接口（需要使用 {@link @Api} 注解），在接口中定义请求方法，其他注解使用方法与 Retrofit 完全一致
     * 2、构造 {@link OkHttp3Client} 对象（可以传入 {@link OkHttpClient} 对象进行网络请求配置）
     * 3、使用 {@link OkHttp3Client} 构造 {@link OkHttp3Manager}
     * 4、将定义好的 API 接口传入 {@link OkHttp3Manager#create(Class)} 中生成 API 对象，直接调用该对象的方法即可
     */

    private SimpleApi api;
    private TextView dataView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_activity);
        setTitle("基本使用");

        findViewById(R.id.path_user).setOnClickListener(this);
        findViewById(R.id.get_user).setOnClickListener(this);
        findViewById(R.id.post_user).setOnClickListener(this);
        dataView = (TextView) findViewById(R.id.data_show);

        // 1、创建 OkHttpClient 作为进行网络请求配置
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.SECONDS) // 配置读超时时间为 5 秒
                .writeTimeout(5, TimeUnit.SECONDS) // 配置写超时时间为 5 秒
                .build();
        // 2、构造 OkHttp3Client 对象
        OkHttp3Client http3Client = new OkHttp3Client.Builder()
                .setOkHttpClient(httpClient)
                .build();
        // 3、使用 OkHttp3Client 对象构造 OkHttp3Manager 对象
        OkHttp3Manager http3Manager = new OkHttp3Manager.Builder(http3Client).build();
        // 4、使用 OkHttp3Manager 对象创建出 API 接口对象，可直接调用该接口对象定义的方法进行网络请求
        api = http3Manager.create(SimpleApi.class);
    }

    @Override
    public void onClick(View view) {
        dataView.setText("数据加载中...");
        // 执行使用 API 接口对象中定义好的方法进行网络请求
        switch (view.getId()) {
            case R.id.path_user:
                OkHttp3Task<Response> pathTask = api.pathUser("path");
                pathTask.asyncExecute(this);
                break;
            case R.id.get_user:
                OkHttp3Task<Response> getTask = api.getUser("王小明", 20);
                getTask.asyncExecute(this);
                break;
            case R.id.post_user:
                OkHttp3Task<Response> postTask = api.postUser("李小红", 18);
                postTask.asyncExecute(this);
                break;
        }
    }

    @Override
    public void onSuccess(Task task, final Response response) {
        // 网络请求成功回调，因为默认回调不在主线程，此处更新 UI 需要发送到 UI 线程进行更新
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    dataView.setText(response.body().string());
                } catch (Exception e) {
                    dataView.setText(e.toString());
                } finally {
                    response.close();
                }
            }
        });
    }

    @Override
    public void onFailure(Task task, final HttpException exception) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dataView.setText(exception.toString());
            }
        });
    }
}
