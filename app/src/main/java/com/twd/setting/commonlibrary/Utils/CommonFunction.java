package com.twd.setting.commonlibrary.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

public class CommonFunction {
    public static boolean checkActIsActivity(Context paramContext) {
        if (paramContext == null) {
            Log.i("打印", "checkActIsActivity ctx false 了 ");
            return false;
        }
        if ((paramContext instanceof FragmentActivity)) {
            FragmentActivity localFragmentActivity = (FragmentActivity) paramContext;
            if ((localFragmentActivity.isFinishing()) || (localFragmentActivity.isDestroyed())) {
                Log.i("打印", "checkActIsActivity FragmentActivity false 了 ");
                return false;
            }
        }
        if ((paramContext instanceof Activity)) {
            Activity activity = (Activity) paramContext;
            if ((activity.isFinishing()) || (activity.isDestroyed())) {
                Log.i("打印", "checkActIsActivity Activity false 了 ");
                return false;
            }
        }
        return true;
    }

    public static int dpToPx(Context paramContext, float paramFloat) {
        return Math.round(paramContext.getResources().getDisplayMetrics().density * paramFloat);
    }
}
