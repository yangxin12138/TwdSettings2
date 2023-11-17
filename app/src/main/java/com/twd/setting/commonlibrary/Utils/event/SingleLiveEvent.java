package com.twd.setting.commonlibrary.Utils.event;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.concurrent.atomic.AtomicBoolean;

public class SingleLiveEvent<T>
        extends MutableLiveData<T> {
    private static final String TAG = "SingleLiveEvent";
    private final AtomicBoolean mPending = new AtomicBoolean(false);

    public void call() {
        setValue(null);
    }

    public void observe(LifecycleOwner owner, final Observer<? super T> observer) {
        if (hasActiveObservers()) {
            Log.w("SingleLiveEvent", "Multiple observers registered but only one will be notified of changes.");
        }
        super.observe(owner, new Observer<T>() {
            @Override
            public void onChanged(T t) {
                if (mPending.compareAndSet(true, false)) {
                    observer.onChanged(t);
                }
            }
        });
    }

    public void setValue(@Nullable T t) {
        mPending.set(true);
        super.setValue(t);
    }
}
