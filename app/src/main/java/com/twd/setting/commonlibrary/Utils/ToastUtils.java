package com.twd.setting.commonlibrary.Utils;

import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public final class ToastUtils {
    public static final int TOAST_SHORT_TIME = 2000;
    private static volatile long lastShowTime;
    private static volatile CharSequence pendingText;
    private static final Runnable showRunnable = new Runnable() {
        public void run() {
            if (SystemClock.uptimeMillis() - ToastUtils.lastShowTime > 2000L) {
                if (TextUtils.isEmpty(ToastUtils.pendingText)) {
                    return;
                }
                //    ToastUtils.access$002(SystemClock.uptimeMillis());
                try {
                    Toast.makeText(Utils.getApp(), ToastUtils.pendingText, Toast.LENGTH_SHORT).show();
                    //       ToastUtils.access$102(null);
                    return;
                } catch (Exception localException) {
                    localException.printStackTrace();
                    StringBuilder localStringBuilder = new StringBuilder();
                    localStringBuilder.append("toast显示失败 : txt=");
                    localStringBuilder.append(ToastUtils.pendingText);
                    localStringBuilder.append("  error=");
                    localStringBuilder.append(localException.getMessage());
                    Log.e("ToastUtils", localStringBuilder.toString());
                }
            }
        }
    };

    private ToastUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    private static void show(CharSequence text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        pendingText = text;
        if (SystemClock.uptimeMillis() - lastShowTime <= 2000L) {
            Utils.runOnUiThreadDelayedShortMsg(showRunnable, 2000L);
            return;
        }
        Utils.runOnUiThreadShortMsg(showRunnable);
    }

    public static void showShort(CharSequence paramCharSequence) {
        show(paramCharSequence);
    }
}
