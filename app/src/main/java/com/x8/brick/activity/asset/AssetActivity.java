package com.x8.brick.activity.asset;

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
import com.x8.brick.okhttp3.executor.android.OkHttp3AssetAsyncTaskExecutorFactory;
import com.x8.brick.task.Task;

import java.io.IOException;

import okhttp3.Response;

public class AssetActivity extends AppCompatActivity implements View.OnClickListener, Task.Callback<Response> {

    private AssetApi api;
    private TextView dataView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("使用Asset下的数据进行模拟");
        setContentView(R.layout.asset_activity);

        findViewById(R.id.get_user).setOnClickListener(this);
        findViewById(R.id.post_user).setOnClickListener(this);
        dataView = (TextView) findViewById(R.id.data_show);

        /*
         * 添加 asset 模拟执行器工厂，可以读取 asset 目录下的文件来模拟网络请求
         * OkHttp3AssetAsyncTaskExecutorFactory 执行器工厂使用 OkHttp3AssetAsyncTaskExecutor 执行器进行处理
         * OkHttp3AssetAsyncTaskExecutor 执行器会根据请求的 URL 的相对地址读取 asset 目录下的对应文件来模拟网络请求
         * OkHttp3AssetAsyncTaskExecutor 执行器使用的是 AsyncTask 进行异步处理，因此可以直接回调到主线程
         */
        OkHttp3Client http3Client = new OkHttp3Client.Builder()
                .setExecutorFacotry(new OkHttp3AssetAsyncTaskExecutorFactory(this))
                .build();
        OkHttp3Manager http3Manager = new OkHttp3Manager.Builder(http3Client).build();
        api = http3Manager.create(AssetApi.class);
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
     * 使用 asset/brick/user/get 文件模拟 http:192.168.31.100:8080/brick/user/get 接口
     */
    private void getUser() {
        OkHttp3Task<Response> task = api.getUser("李小二", 17);
        task.asyncExecute(this);
    }

    /**
     * 使用 asset/brick/user/post 文件模拟 http:192.168.31.100:8080/brick/user/post 接口
     */
    private void postUser() {
        OkHttp3Task<Response> task = api.postUser("王大锤", 23);
        task.asyncExecute(this);
    }

    @Override
    public void onSuccess(Task task, Response response) {
        try {
            if (response.isSuccessful()) {
                String data = response.body().string();
                dataView.setText(data);
            } else {
                dataView.setText(response.message());
            }
        } catch (IOException e) {
            dataView.setText(e.toString());
        } finally {
            try {
                response.close();
            } catch (Exception e) {
                dataView.setText(e.toString());
            }
        }
    }

    @Override
    public void onFailure(Task task, HttpException exception) {
        dataView.setText(exception.toString());
    }
}
