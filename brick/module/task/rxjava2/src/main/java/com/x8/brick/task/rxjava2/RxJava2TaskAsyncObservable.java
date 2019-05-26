package com.x8.brick.task.rxjava2;

import com.x8.brick.exception.HttpException;
import com.x8.brick.parameter.Request;
import com.x8.brick.parameter.Response;
import com.x8.brick.task.Task;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.plugins.RxJavaPlugins;

public class RxJava2TaskAsyncObservable<T> extends Observable<T> {

    private Task<? extends Request, ? extends Response, T> task;

    RxJava2TaskAsyncObservable(Task<? extends Request, ? extends Response, T> task) {
        this.task = task;
    }

    @Override
    protected void subscribeActual(Observer<? super T> observer) {
        TaskDisposable<T> disposable = new TaskDisposable<>(task, observer);
        observer.onSubscribe(disposable);
        task.asyncExecute(disposable);
    }

    private static class TaskDisposable<T> implements Disposable, Task.Callback<T> {

        private Task<? extends Request, ? extends Response, ?> task;
        private Observer<? super T> observer;
        private volatile boolean disposed;
        private boolean terminated;

        TaskDisposable(Task<? extends Request, ? extends Response, ?> task, Observer<? super T> observer) {
            this.task = task;
            this.observer = observer;
            this.terminated = false;
        }

        @Override
        public void onSuccess(Task task, T response) {
            if (disposed) {
                return;
            }
            try {
                observer.onNext(response);
                if (!disposed) {
                    terminated = true;
                    observer.onComplete();
                }
            } catch (Throwable t) {
                if (terminated) {
                    RxJavaPlugins.onError(t);
                } else if (!disposed) {
                    try {
                        observer.onError(t);
                    } catch (Throwable inner) {
                        Exceptions.throwIfFatal(inner);
                        RxJavaPlugins.onError(new CompositeException(t, inner));
                    }
                }
            }
        }

        @Override
        public void onFailure(Task task, HttpException exception) {
            if (task.isCanceled()) {
                return;
            }
            try {
                observer.onError(exception);
            } catch (Throwable inner) {
                Exceptions.throwIfFatal(inner);
                RxJavaPlugins.onError(new CompositeException(exception, inner));
            }
        }

        @Override
        public void dispose() {
            disposed = true;
            task.cancel();
        }

        @Override
        public boolean isDisposed() {
            return disposed;
        }
    }
}
