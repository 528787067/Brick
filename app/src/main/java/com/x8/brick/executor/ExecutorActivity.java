package com.x8.brick.executor;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.x8.brick.R;
import com.x8.brick.exception.HttpException;
import com.x8.brick.okhttp3.HttpClient;
import com.x8.brick.okhttp3.HttpExecutorFactory;
import com.x8.brick.okhttp3.HttpManager;
import com.x8.brick.okhttp3.HttpRequest;
import com.x8.brick.okhttp3.HttpResponse;
import com.x8.brick.okhttp3.HttpTask;
import com.x8.brick.okhttp3.executor.android.AssetThreadPoolExecutorFactory;
import com.x8.brick.okhttp3.executor.file.FileThreadPoolExecutorFactory;
import com.x8.brick.task.Task;
import com.x8.brick.task.TaskModel;

public class ExecutorActivity extends AppCompatActivity implements View.OnClickListener, Task.Callback<HttpResponse> {

    private RadioButton okhttpExecutor;
    private RadioButton assetExecutor;
    private RadioButton fileExecutor;
    private RadioButton stringExecutor;
    private TextView dataShow;

    private ExecutorApi api;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("动态切换执行器");
        setContentView(R.layout.executor_activity);

        okhttpExecutor = (RadioButton) findViewById(R.id.okhttp);
        assetExecutor = (RadioButton) findViewById(R.id.asset);
        fileExecutor = (RadioButton) findViewById(R.id.file);
        stringExecutor = (RadioButton) findViewById(R.id.string);
        dataShow = (TextView) findViewById(R.id.data_show);
        findViewById(R.id.get_data).setOnClickListener(this);
        findViewById(R.id.post_data).setOnClickListener(this);

        final HttpExecutorFactory httpFactory = new HttpExecutorFactory();
        final AssetThreadPoolExecutorFactory assetFactory = new AssetThreadPoolExecutorFactory(this);
        final FileThreadPoolExecutorFactory fileFactory = new FileThreadPoolExecutorFactory.Builder()
                .setHost(getExternalFilesDir("api").getAbsolutePath())
                .setMediaType("application/json; charset=UTF-8")
                .build();
        final StringExecutorFactory stringFactory = new StringExecutorFactory();

        ExecutorFactory<HttpRequest, HttpResponse, ?> executorFactory = new ExecutorFactory<HttpRequest, HttpResponse, Object>() {
            @Override
            public Executor<HttpRequest, HttpResponse, Object> create(@NonNull TaskModel<HttpRequest, HttpResponse> taskModel) {
                if (okhttpExecutor.isChecked()) {
                    return httpFactory.create(taskModel);
                } else if (assetExecutor.isChecked()) {
                    return assetFactory.create(taskModel);
                } else if (fileExecutor.isChecked()) {
                    return fileFactory.create(taskModel);
                } else {
                    return stringFactory.create(taskModel);
                }
            }
        };

        HttpClient httpClient = new HttpClient.Builder()
                .setExecutorFactory(executorFactory)
                .build();
        HttpManager httpManager = new HttpManager.Builder(httpClient).build();
        api = httpManager.create(ExecutorApi.class);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.get_data:
                getData();
                break;
            case R.id.post_data:
                postData();
                break;
        }
    }

    private void getData() {
        HttpTask<HttpResponse> task = api.getUser("王小二", 16);
        task.asyncExecute(this);
    }

    private void postData() {
        HttpTask<HttpResponse> task = api.postUser("李大头", 19);
        task.asyncExecute(this);
    }

    @Override
    public void onSuccess(Task task, final HttpResponse response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    dataShow.setText(response.body().string());
                } catch (Exception e) {
                    dataShow.setText(e.toString());
                } finally {
                    response.close();
                }
            }
        });
    }

    @Override
    public void onFailure(Task task, final HttpException e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dataShow.setText(e.toString());
            }
        });
    }
}


