package com.x8.brick.interceptor;

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
import com.x8.brick.okhttp3.HttpClient;
import com.x8.brick.okhttp3.HttpManager;
import com.x8.brick.okhttp3.HttpRequest;
import com.x8.brick.okhttp3.HttpResponse;
import com.x8.brick.okhttp3.HttpTask;
import com.x8.brick.task.Task;

public class InterceptorActivity extends AppCompatActivity implements View.OnClickListener, Task.Callback<HttpResponse> {

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

        RequestFilter<HttpRequest> requestFilter = new RequestFilter<HttpRequest>() {
            @Override
            public HttpRequest doFilter(HttpRequest request, Chain<HttpRequest> chain) {
                long timestamp = System.currentTimeMillis();
                String method = request.method();
                String url = request.url().toString();
                Log.i("RequestFilter", "开始请求 ------------------------------------>"
                        + "\ntimestamp: " + timestamp
                        + "\nmethod： " + method
                        + "\nurl: " + url
                        + "\n<------------------------------------");
                return chain.doFilter(request);
            }
        };
        ResponseFilter<HttpResponse> responseFilter = new ResponseFilter<HttpResponse>() {
            @Override
            public HttpResponse doFilter(HttpResponse response, Chain<HttpResponse> chain) {
                Log.i("ResponseFilter", "结束请求 ------------------------------------>"
                        + "\ntimestamp： " + System.currentTimeMillis()
                        + "\nresponse: " + response.toString()
                        + "\n<------------------------------------");
                return chain.doFilter(response);
            }
        };
        Interceptor<HttpRequest, HttpResponse> interceptor = new Interceptor<HttpRequest, HttpResponse>() {
            @Override
            public HttpResponse intercept(Chain<HttpRequest, HttpResponse> chain) throws HttpException {
                long startTime = System.currentTimeMillis();
                Log.i("Interceptor", "开始请求 --> timestamp： " + startTime);
                HttpResponse response = chain.proceed(chain.request());
                long endTime = System.currentTimeMillis();
                Log.i("Interceptor", "结束请求 --> timestamp： " + endTime);
                Log.i("Interceptor", "请求耗时：" + (endTime - startTime) + "ms");
                return response;
            }
        };

        HttpClient httpClient = new HttpClient.Builder()
                .addRequestFilter(requestFilter) // 添加自定义的请求过滤器
                .addResponseFilter(responseFilter) // 添加自定义响应过滤器
                .addIntercptor(interceptor) // 添加拦截器
                .build();
        HttpManager httpManager = new HttpManager.Builder(httpClient).build();
        api = httpManager.create(InterceptorApi.class);
    }

    @Override
    public void onClick(View view) {
        dataView.setText("数据加载中...");
        switch (view.getId()) {
            case R.id.get_user:
                HttpTask<HttpResponse> getTask = api.getUser("王小二", 22);
                getTask.asyncExecute(this);
                break;
            case R.id.post_user:
                HttpTask<HttpResponse> postTask = api.postUser("王小二", 22);
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
                    String data = response.body().string();
                    dataView.setText(data);
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
