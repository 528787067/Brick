package com.x8.brick.activity.gson;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.x8.brick.R;
import com.x8.brick.exception.HttpException;
import com.x8.brick.okhttp3.OkHttp3Client;
import com.x8.brick.okhttp3.OkHttp3Manager;
import com.x8.brick.okhttp3.OkHttp3Task;
import com.x8.brick.okhttp3.converter.gson.OkHttp3GsonResponseConverter;
import com.x8.brick.task.Task;

public class GsonActivity extends AppCompatActivity implements Task.Callback<ResponseBean<UserBean>> {

    private TextView timestamp;
    private TextView name;
    private TextView age;
    private TextView method;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("使用Gson转换请求类型");
        setContentView(R.layout.gson_activity);

        timestamp = (TextView) findViewById(R.id.timestamp);
        name = (TextView) findViewById(R.id.name);
        age = (TextView) findViewById(R.id.age);
        method = (TextView) findViewById(R.id.method);

        // Gson 转换器可以实现将 Response 数据直接转换成实体对象
        OkHttp3Client http3Client = new OkHttp3Client.Builder().build();
        OkHttp3Manager http3Manager = new OkHttp3Manager.Builder(http3Client)
                .addResponseConverter(new OkHttp3GsonResponseConverter<>()) // 添加 Gson 转换器
                .build();
        final GsonApi api = http3Manager.create(GsonApi.class);

        findViewById(R.id.get_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OkHttp3Task<ResponseBean<UserBean>> task = api.getUser("江小白", 26);
                task.asyncExecute(GsonActivity.this);
            }
        });
    }

    @Override
    public void onSuccess(Task task, final ResponseBean<UserBean> data) {
        runOnUiThread(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                UserBean user = data.getData();
                timestamp.setText("时间戳: " + data.getTimestamp());
                name.setText("姓名: " + user.getName());
                age.setText("年龄: " + user.getAge());
                method.setText("请求类型: " + user.getMethod());
            }
        });
    }

    @Override
    public void onFailure(Task task, final HttpException exception) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e("GsonActivity", "onFailure --> " + exception);
            }
        });
    }
}
