package com.x8.brick.activity.basicusage;

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

import java.io.IOException;

import okhttp3.Response;

public class BasicUsageActivity extends AppCompatActivity implements View.OnClickListener, Task.Callback<Response> {

    private BasicUsageApi api;
    private TextView dataView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_usage_activity);
        setTitle("基本使用");

        findViewById(R.id.path_user).setOnClickListener(this);
        findViewById(R.id.get_user).setOnClickListener(this);
        findViewById(R.id.post_user).setOnClickListener(this);
        dataView = (TextView) findViewById(R.id.data);

        OkHttp3Client http3Client = new OkHttp3Client.Builder().build();
        OkHttp3Manager http3Manager = new OkHttp3Manager.Builder(http3Client).build();
        api = http3Manager.create(BasicUsageApi.class);
    }

    @Override
    public void onClick(View view) {
        dataView.setText("数据加载中...");
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
