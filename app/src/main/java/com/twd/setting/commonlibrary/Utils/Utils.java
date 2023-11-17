package com.twd.setting.commonlibrary.Utils;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

public final class Utils {
    private static final int HANDLER_ID_TOAST_DELAY = 100;
    private static final Handler UTIL_HANDLER = new Handler(Looper.getMainLooper());
    private static Application sApplication;

    private Utils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static Application getApp() {
        return sApplication;
    }

    public static void init(Application paramApplication) {
        sApplication = paramApplication;
    }

    public static void runOnUiThread(Runnable paramRunnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            paramRunnable.run();
            return;
        }
        UTIL_HANDLER.post(paramRunnable);
    }

    public static void runOnUiThreadDelayed(Runnable paramRunnable, long paramLong) {
        UTIL_HANDLER.postDelayed(paramRunnable, paramLong);
    }

    public static void runOnUiThreadDelayedShortMsg(Runnable paramRunnable, long paramLong) {
        Handler localHandler = UTIL_HANDLER;
        if (localHandler.hasMessages(100)) {
            return;
        }
        localHandler.sendEmptyMessageDelayed(100, paramLong);
        localHandler.postDelayed(paramRunnable, paramLong);
    }

    public static void runOnUiThreadShortMsg(Runnable paramRunnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            paramRunnable.run();
            return;
        }
        Handler localHandler = UTIL_HANDLER;
        if (localHandler.hasMessages(100)) {
            return;
        }
        localHandler.sendEmptyMessageDelayed(100, 1000L);
        localHandler.post(paramRunnable);
    }
}
