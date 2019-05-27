package com.x8.brick.activity.converter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.x8.brick.R;
import com.x8.brick.converter.ResponseConverter;
import com.x8.brick.exception.HttpException;
import com.x8.brick.okhttp3.OkHttp3Client;
import com.x8.brick.okhttp3.OkHttp3Manager;
import com.x8.brick.okhttp3.OkHttp3Response;
import com.x8.brick.okhttp3.OkHttp3Task;
import com.x8.brick.okhttp3.converter.gson.OkHttp3GsonResponseConverter;
import com.x8.brick.task.Task;

import java.io.IOException;
import java.lang.reflect.Type;

public class ConverterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("自定义String类型转换器");
        setContentView(R.layout.converter_activity);

        ResponseConverter<OkHttp3Response, String> stringConverter = new ResponseConverter<OkHttp3Response, String>() {
            @Override
            public String convert(OkHttp3Response response, Type type) {
                if (type == String.class) {
                    try {
                        return response.response.body().string();
                    } catch (IOException e) {
                        return null;
                    } finally {
                        response.response.close();
                    }
                }
                return null;
            }
        };
        OkHttp3Client http3Client = new OkHttp3Client.Builder().build();
        OkHttp3Manager http3Manager = new OkHttp3Manager.Builder(http3Client)
                .addResponseConverter(stringConverter)
                .addResponseConverter(new OkHttp3GsonResponseConverter<>())
                .build();
        final GonverterApi api = http3Manager.create(GonverterApi.class);

        findViewById(R.id.get_data_bean).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OkHttp3Task<ConverterData> task = api.getUserBean("李大柱", 25);
                task.asyncExecute(new Task.Callback<ConverterData>() {
                    @Override
                    public void onSuccess(Task task, ConverterData data) {
                        showResult(data.toString());
                    }
                    @Override
                    public void onFailure(Task task, HttpException exception) {
                        showResult(exception.toString());
                    }
                });
            }
        });
        findViewById(R.id.get_data_string).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OkHttp3Task<String> task = api.getUserString("孙小花", 21);
                task.asyncExecute(new Task.Callback<String>() {
                    @Override
                    public void onSuccess(Task task, String s) {
                        showResult(s);
                    }
                    @Override
                    public void onFailure(Task task, HttpException exception) {
                        showResult(exception.toString());
                    }
                });
            }
        });
    }

    private void showResult(final String result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView dataView = (TextView) findViewById(R.id.data_show);
                dataView.setText(result);
            }
        });
    }
}
