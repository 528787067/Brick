package com.x8.brick.task;

import android.annotation.SuppressLint;

import com.x8.brick.converter.TaskConverter;
import com.x8.brick.exception.HttpException;
import com.x8.brick.okhttp3.HttpTask;

import java.lang.reflect.Type;

/**
 * 自定义 Task 转换器需要实现 TaskConverter 接口
 * 在 {@link TaskConverter#convert(Task, Type)} 中执行类型转换，将 {@param Task} 转换成 {@param Type}
 * @param <T> Response 数据类型
 */
public class AsyncTaskConverter<T> implements TaskConverter<HttpTask<T>, AsyncTaskConverter.AsyncTask<T>> {

    /**
     * 根据 taskType 类型来决定是否有必要使用对应的转换类型
     * 如果不需要执行转换的话返回 {@param null}，将转换工作传递给下一级类型转换器
     * @param task 原始的 Task 对象
     * @param taskType 待转换的目标类型
     * @return 转换后的目标对象
     */
    @Override
    public AsyncTask<T> convert(HttpTask<T> task, Type taskType) {
        if (taskType == AsyncTask.class) {
            return new AsyncTask<>(task);
        }
        return null;
    }

    /**
     * 使用 {@link android.os.AsyncTask} 执行网络异步请求
     * @param <T> Response 数据类型
     */
    public static class AsyncTask<T> {

        private HttpTask<T> task;

        public AsyncTask(HttpTask<T> task) {
            this.task = task;
        }

        @SuppressLint("StaticFieldLeak")
        public void asyncExecute(final Task.Callback<T> callback) {
            new android.os.AsyncTask<Void, Void, T>() {

                HttpException httpException;

                @Override
                protected T doInBackground(Void... voids) {
                    try {
                        return task.execute();
                    } catch (HttpException e) {
                        httpException = e;
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(T t) {
                    if (callback != null) {
                        if (httpException != null) {
                            callback.onFailure(task, httpException);
                        } else {
                            callback.onSuccess(task, t);
                        }
                    }
                }
            }.execute();
        }
    }
}
