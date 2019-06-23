package com.x8.brick.task.rxjava;

import com.x8.brick.parameter.Request;
import com.x8.brick.parameter.Response;
import com.x8.brick.task.Task;

import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.exceptions.Exceptions;

public class TaskExecuteOnSubscribe<T> implements OnSubscribe<T>  {

    private Task<? extends Request, ? extends Response, T> task;

    TaskExecuteOnSubscribe(Task<? extends Request, ? extends Response, T> task) {
        this.task = task;
    }

    @Override
    public void call(Subscriber<? super T> subscriber) {
        TaskArbiter<T> arbiter = new TaskArbiter<>(task, subscriber);
        subscriber.add(arbiter);
        subscriber.setProducer(arbiter);
        T response;
        try {
            response = task.execute();
        } catch (Throwable t) {
            Exceptions.throwIfFatal(t);
            arbiter.emitError(t);
            return;
        }
        arbiter.emitResponse(response);
    }
}
