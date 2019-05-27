package com.x8.brick.activity.interceptor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.x8.brick.R;
import com.x8.brick.exception.HttpException;
import com.x8.brick.filter.RequestFilter;
import com.x8.brick.filter.ResponseFilter;
import com.x8.brick.interceptor.Interceptor;
import com.x8.brick.okhttp3.OkHttp3Client;
import com.x8.brick.okhttp3.OkHttp3Manager;
import com.x8.brick.okhttp3.OkHttp3Request;
import com.x8.brick.okhttp3.OkHttp3Response;
import com.x8.brick.okhttp3.OkHttp3Task;
import com.x8.brick.task.Task;

import okhttp3.Request;
import okhttp3.Response;

public class InterceptorActivity extends AppCompatActivity implements View.OnClickListener, Task.Callback<Response> {

    /**
     * 支持定义多级过滤器和拦截器
     * 过滤器分请求过滤器和响应过滤器，控制粒度比拦截器要小
     * 自定义请求过滤器需要实现 {@link RequestFilter} 接口，用于在请求前处理请求参数
     * 自定义响应过滤器需要实现 {@link ResponseFilter} 接口，用于在响应后处理响应数据
     * 自定义拦截器需要实现 {@link Interceptor} 接口，用于拦截处理网络请求过程
     * 过滤器和拦截器都支持多级处理，即上一级处理完成后会交由下一级进行处理，执行顺序如下：
     * RequestFilter1 --> RequestFilter2 --> Interceptor1(request) --> Interceptor2(request) -->
     * Interceptor2(response) --> Interceptor1(response) --> ResponseFilter1 --> ResponseFilter2
     */

    private InterceptorApi api;
    private TextView dataView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("过滤器/拦截器");
        setContentView(R.layout.interceptor_activity);

        findViewById(R.id.get_user).setOnClickListener(this);
        findViewById(R.id.post_user).setOnClickListener(this);
        dataView = (TextView) findViewById(R.id.data_show);

        RequestFilter<OkHttp3Request> requestFilter = new RequestFilter<OkHttp3Request>() {
            @Override
            public OkHttp3Request doFilter(OkHttp3Request request, Chain<OkHttp3Request> chain) {
                Request rawRequest = request.request;
                long timestamp = System.currentTimeMillis();
                String method = rawRequest.method();
                String url = rawRequest.url().toString();
                Log.i("RequestFilter", "开始请求 ------------------------------------>"
                        + "\ntimestamp: " + timestamp
                        + "\nmethod： " + method
                        + "\nurl: " + url
                        + "\n<------------------------------------");
                return chain.doFilter(request);
            }
        };
        ResponseFilter<OkHttp3Response> responseFilter = new ResponseFilter<OkHttp3Response>() {
            @Override
            public OkHttp3Response doFilter(OkHttp3Response response, Chain<OkHttp3Response> chain) {
                Log.i("ResponseFilter", "结束请求 ------------------------------------>"
                        + "\ntimestamp： " + System.currentTimeMillis()
                        + "\nresponse: " + response.response.toString()
                        + "\n<------------------------------------");
                return chain.doFilter(response);
            }
        };
        Interceptor<OkHttp3Request, OkHttp3Response> interceptor = new Interceptor<OkHttp3Request, OkHttp3Response>() {
            @Override
            public OkHttp3Response intercept(Chain<OkHttp3Request, OkHttp3Response> chain) throws HttpException {
                long startTime = System.currentTimeMillis();
                Log.i("Interceptor", "开始请求 --> timestamp： " + startTime);
                OkHttp3Response response = chain.proceed(chain.request());
                long endTime = System.currentTimeMillis();
                Log.i("Interceptor", "结束请求 --> timestamp： " + endTime);
                Log.i("Interceptor", "请求耗时：" + (endTime - startTime) + "ms");
                return response;
            }
        };

        OkHttp3Client http3Client = new OkHttp3Client.Builder()
                .addRequestFilter(requestFilter) // 添加自定义的请求过滤器
                .addResponseFilter(responseFilter) // 添加自定义响应过滤器
                .addIntercptor(interceptor) // 添加拦截器
                .build();
        OkHttp3Manager http3Manager = new OkHttp3Manager.Builder(http3Client).build();
        api = http3Manager.create(InterceptorApi.class);
    }

    @Override
    public void onClick(View view) {
        dataView.setText("数据加载中...");
        switch (view.getId()) {
            case R.id.get_user:
                OkHttp3Task<Response> getTask = api.getUser("王小二", 22);
                getTask.asyncExecute(this);
                break;
            case R.id.post_user:
                OkHttp3Task<Response> postTask = api.postUser("王小二", 22);
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
