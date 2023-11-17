package com.twd.setting.base.observer;

import io.reactivex.observers.DisposableObserver;

public abstract class MyDisposableObserver<T>
        extends DisposableObserver<T> {
    public void onComplete() {
    }

    public void onError(Throwable paramThrowable) {
    }

    public void onNext(T paramT) {
    }

    public abstract void onNext(Integer paramAnonymousInteger);
}