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

import okhttp3.Response;

public class ConverterActivity extends AppCompatActivity implements View.OnClickListener {

    private GonverterApi api;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("自定义String类型转换器");
        setContentView(R.layout.converter_activity);

        findViewById(R.id.get_data).setOnClickListener(this);
        findViewById(R.id.get_data_bean).setOnClickListener(this);
        findViewById(R.id.get_data_string).setOnClickListener(this);

        /**
         * 自定义Response转换器需要实现 {@link ResponseConverter} 接口
         * 在 converter 方法中根据 type 类型来判断是否需要进行对应的类型转换
         * 如果不需要进行转换的话直接返回 null 交给下一级转换器进行处理
         */
        ResponseConverter<OkHttp3Response, String> stringConverter = new ResponseConverter<OkHttp3Response, String>() {
            @Override
            public String convert(OkHttp3Response response, Type type) {
                if (type == String.class) {
                    return responseToString(response.response);
                }
                return null;
            }
        };
        /**
         * 重写 OkHttp3GsonResponseConverter，如果碰到数据类型是 Response 类型的则不进行转换
         */
        OkHttp3GsonResponseConverter<?> gsonConverter = new OkHttp3GsonResponseConverter<Object>() {
            @Override
            public Object convert(OkHttp3Response response, Type type) {
                if (type == Response.class) {
                    return null;
                }
                return super.convert(response, type);
            }
        };

        OkHttp3Client http3Client = new OkHttp3Client.Builder().build();
        OkHttp3Manager http3Manager = new OkHttp3Manager.Builder(http3Client)
                .addResponseConverter(stringConverter) // 添加自定义的 String 转换器将 Response 转换成 String
                .addResponseConverter(gsonConverter) // 添加 Gson 转换器将 Response 转成成实体对象
                .build();
        api = http3Manager.create(GonverterApi.class);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.get_data:
                getData();
                break;
            case R.id.get_data_bean:
                getDataBean();
                break;
            case R.id.get_data_string:
                getDataString();
                break;
        }
    }

    /**
     * 直接获取数据不使用特定的转换器，响应数据类型为 Response
     */
    private void getData() {
        OkHttp3Task<Response> task = api.getUser("王大春", 29);
        task.asyncExecute(new Task.Callback<Response>() {
            @Override
            public void onSuccess(Task task, Response response) {
                showResult(responseToString(response));
            }
            @Override
            public void onFailure(Task task, HttpException exception) {
                showResult(exception.toString());
            }
        });
    }

    /**
     * 获取数据并使用 Gson 转换器将响应数据直接转换成实体对象
     */
    private void getDataBean() {
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

    /**
     * 获取数据并使用自定义的 String 转化器将响应数据转换成 String
     */
    private void getDataString() {
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

    private void showResult(final String result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView dataView = (TextView) findViewById(R.id.data_show);
                dataView.setText(result);
            }
        });
    }

    private String responseToString(Response response) {
        try {
            return response.body().string();
        } catch (IOException e) {
            return null;
        } finally {
            response.close();
        }
    }
}
