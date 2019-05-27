package com.x8.brick.activity.multihost;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.x8.brick.R;
import com.x8.brick.exception.HttpException;
import com.x8.brick.okhttp3.OkHttp3Client;
import com.x8.brick.okhttp3.OkHttp3Manager;
import com.x8.brick.okhttp3.OkHttp3Task;
import com.x8.brick.task.Task;

import java.io.IOException;

import okhttp3.Response;

public class MultiHostActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, Task.Callback<Response> {

    private OkHttp3Manager httpManager;
    private MultiHostApi api;
    private TextView dataView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multi_host_activity);
        setTitle("多环境切换");

        OkHttp3Client http3Client = new OkHttp3Client.Builder().build();
        httpManager = new OkHttp3Manager.Builder(http3Client).build();
        api = httpManager.create(MultiHostApi.class);

        ((RadioButton) findViewById(R.id.online)).setOnCheckedChangeListener(this);
        ((RadioButton) findViewById(R.id.sandbox)).setOnCheckedChangeListener(this);
        ((RadioButton) findViewById(R.id.test1)).setOnCheckedChangeListener(this);
        ((RadioButton) findViewById(R.id.test2)).setOnCheckedChangeListener(this);

        findViewById(R.id.get_data).setOnClickListener(this);
        findViewById(R.id.post_data).setOnClickListener(this);

        dataView = (TextView) findViewById(R.id.data_show);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
        if (!checked) {
            return;
        }
        switch (compoundButton.getId()) {
            case R.id.online:
                httpManager.setHost("online");
                break;
            case R.id.sandbox:
                httpManager.setHost("sandbox");
                break;
            case R.id.test1:
                httpManager.setHost("dev1");
                break;
            case R.id.test2:
                httpManager.setHost("dev2");
                break;
        }
    }

    @Override
    public void onClick(View view) {
        dataView.setText("数据加载中...");
        switch (view.getId()) {
            case R.id.get_data:
                OkHttp3Task<Response> getTask = api.getUser("王小明", 15);
                getTask.asyncExecute(this);
                break;
            case R.id.post_data:
                OkHttp3Task<Response> postTask = api.getUser("李小红", 17);
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
                } catch (IOException e) {
                    dataView.setText(e.toString());
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
