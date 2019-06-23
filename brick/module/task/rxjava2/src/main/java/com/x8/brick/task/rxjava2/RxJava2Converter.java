package com.x8.brick.task.rxjava2;

import com.x8.brick.converter.TaskConverter;
import com.x8.brick.parameter.Request;
import com.x8.brick.parameter.Response;
import com.x8.brick.task.Task;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;

public class RxJava2Converter implements TaskConverter<Task<? extends Request, ? extends Response, ?>, Object> {

    private Scheduler scheduler;
    private boolean isAsync;

    public RxJava2Converter() {
        this(null, false);
    }

    public RxJava2Converter(boolean isAsync) {
        this(null, isAsync);
    }

    public RxJava2Converter(Scheduler scheduler) {
        this(scheduler, false);
    }

    public RxJava2Converter(Scheduler scheduler, boolean isAsync) {
        this.scheduler = scheduler;
        this.isAsync = isAsync;
    }

    @Override
    public Object convert(Task<? extends Request, ? extends Response, ?> task, Type type) {
        Type rawType = type;
        if (type instanceof ParameterizedType) {
            rawType = ((ParameterizedType) type).getRawType();
        }
        if (!(rawType instanceof Class)) {
            return null;
        }
        Observable<?> observable = isAsync
                ? new TaskAsyncObservable<>(task) : new TaskExecuteObservable<>(task);
        if (scheduler != null) {
            observable = observable.subscribeOn(scheduler);
        }
        if (rawType == Flowable.class) {
            return observable.toFlowable(BackpressureStrategy.LATEST);
        }
        if (rawType == Single.class) {
            return observable.singleOrError();
        }
        if (rawType == Maybe.class) {
            return observable.singleElement();
        }
        if (rawType == Completable.class) {
            return observable.ignoreElements();
        }
        if (rawType == Observable.class) {
            return observable;
        }
        return null;
    }
}
