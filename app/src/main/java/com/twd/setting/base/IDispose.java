package com.twd.setting.base;

import io.reactivex.disposables.Disposable;

public abstract interface IDispose {
    public abstract void addSubscribe(Disposable paramDisposable);
}
