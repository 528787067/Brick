package com.x8.brick.module.task.rxjava2;

import com.x8.brick.parameter.Request;
import com.x8.brick.parameter.Response;
import com.x8.brick.task.Task;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.plugins.RxJavaPlugins;

public class RxJava2TaskExecuteObservable<T> extends Observable<T> {

    private Task<? extends Request, ? extends Response, T> task;

    RxJava2TaskExecuteObservable(Task<? extends Request, ? extends Response, T> task) {
        this.task = task;
    }

    @Override
    protected void subscribeActual(Observer<? super T> observer) {
        TaskDisposable disposable = new TaskDisposable(task);
        observer.onSubscribe(disposable);
        boolean terminated = false;
        try {
            T response = task.execute();
            if (!disposable.isDisposed()) {
                observer.onNext(response);
            }
            if (!disposable.isDisposed()) {
                terminated = true;
                observer.onComplete();
            }
        } catch (Throwable t) {
            Exceptions.throwIfFatal(t);
            if (terminated) {
                RxJavaPlugins.onError(t);
            } else if (!disposable.isDisposed()) {
                try {
                    observer.onError(t);
                } catch (Throwable inner) {
                    Exceptions.throwIfFatal(inner);
                    RxJavaPlugins.onError(new CompositeException(t, inner));
                }
            }
        }
    }

    private static class TaskDisposable implements Disposable {

        private Task<? extends Request, ? extends Response, ?> task;
        private volatile boolean disposed;

        TaskDisposable(Task<? extends Request, ? extends Response, ?> task) {
            this.task = task;
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
