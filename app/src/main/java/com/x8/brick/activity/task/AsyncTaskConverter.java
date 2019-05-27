package com.x8.brick.activity.task;

import android.annotation.SuppressLint;

import com.x8.brick.converter.TaskConverter;
import com.x8.brick.exception.HttpException;
import com.x8.brick.okhttp3.OkHttp3Task;
import com.x8.brick.task.Task;

import java.lang.reflect.Type;

public class AsyncTaskConverter<T> implements TaskConverter<OkHttp3Task<T>, AsyncTaskConverter.AsyncTask<T>> {

    @Override
    public AsyncTask<T> convert(OkHttp3Task<T> task, Type taskType) {
        if (taskType == AsyncTask.class) {
            return new AsyncTask<>(task);
        }
        return null;
    }

    public static class AsyncTask<T> {

        private OkHttp3Task<T> task;

        public AsyncTask(OkHttp3Task<T> task) {
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
