package com.x8.brick.task.rxjava;

import com.x8.brick.parameter.Request;
import com.x8.brick.parameter.Response;
import com.x8.brick.task.Task;

import java.util.concurrent.atomic.AtomicInteger;

import rx.Producer;
import rx.Subscriber;
import rx.Subscription;
import rx.exceptions.CompositeException;
import rx.exceptions.Exceptions;
import rx.exceptions.OnCompletedFailedException;
import rx.exceptions.OnErrorFailedException;
import rx.exceptions.OnErrorNotImplementedException;
import rx.plugins.RxJavaPlugins;

public class TaskArbiter<T> extends AtomicInteger implements Subscription, Producer {

    private static final int STATE_WAITING = 0;
    private static final int STATE_REQUESTED = 1;
    private static final int STATE_HAS_RESPONSE = 2;
    private static final int STATE_TERMINATED = 3;

    private Task<? extends Request, ? extends Response, T> task;
    private Subscriber<? super T> subscriber;

    private volatile boolean unsubscribed;
    private volatile T response;

    TaskArbiter(Task<? extends Request, ? extends Response, T> task, Subscriber<? super T> subscriber) {
        super(STATE_WAITING);
        this.task = task;
        this.subscriber = subscriber;
    }

    @Override
    public void unsubscribe() {
        unsubscribed = true;
        task.cancel();
    }

    @Override
    public boolean isUnsubscribed() {
        return unsubscribed;
    }

    @Override
    public void request(long amount) {
        if (amount == 0) {
            return;
        }
        while (true) {
            int state = get();
            switch (state) {
                case STATE_WAITING:
                    if (compareAndSet(STATE_WAITING, STATE_REQUESTED)) {
                        return;
                    }
                    break;
                case STATE_HAS_RESPONSE:
                    if (compareAndSet(STATE_HAS_RESPONSE, STATE_TERMINATED)) {
                        deliverResponse(response);
                        return;
                    }
                    break;
                case STATE_REQUESTED:
                case STATE_TERMINATED:
                    return;
                default:
                    throw new IllegalStateException("Unknown state: " + state);
            }
        }
    }

    void emitResponse(T response) {
        while (true) {
            int state = get();
            switch (state) {
                case STATE_WAITING:
                    this.response = response;
                    if (compareAndSet(STATE_WAITING, STATE_HAS_RESPONSE)) {
                        return;
                    }
                    break;
                case STATE_REQUESTED:
                    if (compareAndSet(STATE_REQUESTED, STATE_TERMINATED)) {
                        deliverResponse(response);
                        return;
                    }
                    break;
                case STATE_HAS_RESPONSE:
                case STATE_TERMINATED:
                    throw new AssertionError();
                default:
                    throw new IllegalStateException("Unknown state: " + state);
            }
        }
    }

    void emitError(Throwable t) {
        set(STATE_TERMINATED);
        if (!isUnsubscribed()) {
            try {
                subscriber.onError(t);
            } catch (OnCompletedFailedException | OnErrorFailedException | OnErrorNotImplementedException e) {
                RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
            } catch (Throwable inner) {
                Exceptions.throwIfFatal(inner);
                CompositeException composite = new CompositeException(t, inner);
                RxJavaPlugins.getInstance().getErrorHandler().handleError(composite);
            }
        }
    }

    private void deliverResponse(T response) {
        try {
            if (!isUnsubscribed()) {
                subscriber.onNext(response);
            }
        } catch (OnCompletedFailedException
                | OnErrorFailedException
                | OnErrorNotImplementedException e) {
            RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
            return;
        } catch (Throwable t) {
            Exceptions.throwIfFatal(t);
            try {
                subscriber.onError(t);
            } catch (OnCompletedFailedException | OnErrorFailedException | OnErrorNotImplementedException e) {
                RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
            } catch (Throwable inner) {
                Exceptions.throwIfFatal(inner);
                CompositeException composite = new CompositeException(t, inner);
                RxJavaPlugins.getInstance().getErrorHandler().handleError(composite);
            }
            return;
        }
        try {
            if (!isUnsubscribed()) {
                subscriber.onCompleted();
            }
        } catch (OnCompletedFailedException | OnErrorFailedException | OnErrorNotImplementedException e) {
            RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
        } catch (Throwable t) {
            Exceptions.throwIfFatal(t);
            RxJavaPlugins.getInstance().getErrorHandler().handleError(t);
        }
    }
}
