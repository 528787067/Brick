package com.x8.brick.task.rxjava;

import com.x8.brick.converter.TaskConverter;
import com.x8.brick.parameter.Request;
import com.x8.brick.parameter.Response;
import com.x8.brick.task.Task;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import rx.Completable;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Scheduler;
import rx.Single;

public class RxJavaConverter implements TaskConverter<Task<? extends Request, ? extends Response, ?>, Object> {

    private Scheduler scheduler;
    private boolean isAsync;

    public RxJavaConverter() {
        this(null, false);
    }

    public RxJavaConverter(boolean isAsync) {
        this(null, isAsync);
    }

    public RxJavaConverter(Scheduler scheduler) {
        this(scheduler, false);
    }

    public RxJavaConverter(Scheduler scheduler, boolean isAsync) {
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
        OnSubscribe<?> onSubscribe = isAsync
                ? new RxJavaTaskAsyncOnSubscribe<>(task) : new RxJavaTaskExecuteOnSubscribe<>(task);
        Observable<?> observable = Observable.create(onSubscribe);
        if (scheduler != null) {
            observable = observable.subscribeOn(scheduler);
        }
        if (rawType == Single.class) {
            return observable.toSingle();
        }
        if (rawType == Completable.class) {
            return observable.toCompletable();
        }
        if (rawType == Observable.class) {
            return observable;
        }
        return null;
    }
}
