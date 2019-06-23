package com.x8.brick.multihost;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.x8.brick.R;
import com.x8.brick.exception.HttpException;
import com.x8.brick.okhttp3.HttpClient;
import com.x8.brick.okhttp3.HttpManager;
import com.x8.brick.okhttp3.HttpResponse;
import com.x8.brick.okhttp3.HttpTask;
import com.x8.brick.task.Task;

import java.io.IOException;

public class MultiHostActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener,
        View.OnClickListener, Task.Callback<HttpResponse> {

    private HttpManager httpManager;
    private MultiHostApi api;

    private TextView dataView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multi_host_activity);
        setTitle("多环境切换");

        ((RadioButton) findViewById(R.id.online)).setOnCheckedChangeListener(this);
        ((RadioButton) findViewById(R.id.sandbox)).setOnCheckedChangeListener(this);
        ((RadioButton) findViewById(R.id.test1)).setOnCheckedChangeListener(this);
        ((RadioButton) findViewById(R.id.test2)).setOnCheckedChangeListener(this);

        findViewById(R.id.get_data).setOnClickListener(this);
        findViewById(R.id.post_data).setOnClickListener(this);

        dataView = (TextView) findViewById(R.id.data_show);

        // 构造网络请求对象
        HttpClient httpClient = new HttpClient.Builder().build();
        httpManager = new HttpManager.Builder(httpClient).build();
        api = httpManager.create(MultiHostApi.class);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
        if (!checked) {
            return;
        }
        /*
         * 使用 httpManager 的 setHost 方法切换环境（对应的 host 需要有在 API 接口中配置或者在 java 代码中设置过）
         * 如果在 java 代码中设置过和 API 接口中配置一样的 host，则会优先使用 Java 代码中设置的 host
         */
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
                HttpTask<HttpResponse> getTask = api.getUser("王小明", 15);
                getTask.asyncExecute(this);
                break;
            case R.id.post_data:
                HttpTask<HttpResponse> postTask = api.getUser("李小红", 17);
                postTask.asyncExecute(this);
                break;
        }
    }

    @Override
    public void onSuccess(Task task, final HttpResponse response) {
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
