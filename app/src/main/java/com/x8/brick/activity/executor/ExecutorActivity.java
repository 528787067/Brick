package com.x8.brick.activity.executor;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.x8.brick.R;
import com.x8.brick.exception.HttpException;
import com.x8.brick.executor.Executor;
import com.x8.brick.executor.ExecutorFacotry;
import com.x8.brick.okhttp3.OkHttp3Client;
import com.x8.brick.okhttp3.OkHttp3ExecutorFactory;
import com.x8.brick.okhttp3.OkHttp3Manager;
import com.x8.brick.okhttp3.OkHttp3Request;
import com.x8.brick.okhttp3.OkHttp3Response;
import com.x8.brick.okhttp3.OkHttp3Task;
import com.x8.brick.okhttp3.executor.android.OkHttp3AssetThreadPoolExecutorFactory;
import com.x8.brick.okhttp3.executor.file.OkHttp3FileThreadPoolExecutorFactory;
import com.x8.brick.task.Task;
import com.x8.brick.task.TaskModel;

import okhttp3.Response;

public class ExecutorActivity extends AppCompatActivity implements View.OnClickListener, Task.Callback<Response> {

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

        final OkHttp3ExecutorFactory httpFactory = new OkHttp3ExecutorFactory();
        final OkHttp3AssetThreadPoolExecutorFactory assetFactory = new OkHttp3AssetThreadPoolExecutorFactory(this);
        final OkHttp3FileThreadPoolExecutorFactory fileFactory = new OkHttp3FileThreadPoolExecutorFactory.Builder()
                .setHost(getExternalFilesDir("api").getAbsolutePath())
                .setMediaType("application/json; charset=UTF-8")
                .build();
        final StringExecutorFactory stringFactory = new StringExecutorFactory();

        ExecutorFacotry<OkHttp3Request, OkHttp3Response> executorFacotry = new ExecutorFacotry<OkHttp3Request, OkHttp3Response>() {
            @Override
            public <RESULT> Executor<OkHttp3Request, OkHttp3Response, RESULT> create(@NonNull TaskModel<OkHttp3Request, OkHttp3Response> taskModel) {
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

        OkHttp3Client http3Client = new OkHttp3Client.Builder()
                .setExecutorFacotry(executorFacotry)
                .build();
        OkHttp3Manager http3Manager = new OkHttp3Manager.Builder(http3Client).build();
        api = http3Manager.create(ExecutorApi.class);
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
        OkHttp3Task<Response> task = api.getUser("王小二", 16);
        task.asyncExecute(this);
    }

    private void postData() {
        OkHttp3Task<Response> task = api.postUser("李大头", 19);
        task.asyncExecute(this);
    }

    @Override
    public void onSuccess(Task task, final Response response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    dataShow.setText(response.body().string());
                } catch (Exception e) {
                    dataShow.setText(e.toString());
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


