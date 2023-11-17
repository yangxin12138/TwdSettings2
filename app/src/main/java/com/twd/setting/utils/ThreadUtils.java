package com.twd.setting.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadUtils {
    private static volatile Thread sMainThread;
    private static volatile Handler sMainThreadHandler;
    private static volatile ExecutorService sSingleThreadExecutor;

    public static void ensureMainThread() {
        if (isMainThread()) {
            return;
        }
        throw new RuntimeException("Must be called on the UI thread");
    }

    public static Handler getUiThreadHandler() {
        if (sMainThreadHandler == null) {
            sMainThreadHandler = new Handler(Looper.getMainLooper());
        }
        return sMainThreadHandler;
    }

    public static boolean isMainThread() {
        if (sMainThread == null) {
            sMainThread = Looper.getMainLooper().getThread();
        }
        return Thread.currentThread() == sMainThread;
    }

    public static void postOnBackgroundThread(Runnable paramRunnable) {
        if (sSingleThreadExecutor == null) {
            sSingleThreadExecutor = Executors.newSingleThreadExecutor();
        }
        sSingleThreadExecutor.execute(paramRunnable);
    }

    public static void postOnMainThread(Runnable paramRunnable) {
        getUiThreadHandler().post(paramRunnable);
    }
}