package com.twd.setting.base.observer;

import com.twd.setting.base.IDispose;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public abstract class MyObserver<T>
        implements Observer<T> {
    IDispose cDispose;

    public MyObserver(IDispose paramIDispose) {
        cDispose = paramIDispose;
    }

    public void onComplete() {
    }

    public void onError(Throwable paramThrowable) {
    }

    public void onSubscribe(Disposable paramDisposable) {
        if (paramDisposable != null) {
            if (cDispose != null) {
                cDispose.addSubscribe(paramDisposable);
            }
        }
    }
}