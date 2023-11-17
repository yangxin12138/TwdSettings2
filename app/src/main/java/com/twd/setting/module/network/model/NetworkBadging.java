package com.twd.setting.module.network.model;

import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.drawable.Drawable;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class NetworkBadging {
    public static final int BADGING_4K = 30;
    public static final int BADGING_HD = 20;
    public static final int BADGING_NONE = 0;
    public static final int BADGING_SD = 10;

    public static Drawable getWifiIcon(int paramInt1, int paramInt2, Resources.Theme paramTheme) {
        return Resources.getSystem().getDrawable(getWifiSignalResource(paramInt1), paramTheme);
    }

    private static int getWifiSignalResource(int paramInt) {
        return -1;
    }

    @Retention(RetentionPolicy.SOURCE)
    public static @interface Badging {
    }
}
