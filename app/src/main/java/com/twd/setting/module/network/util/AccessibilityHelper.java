package com.twd.setting.module.network.util;

import android.content.Context;
import android.view.accessibility.AccessibilityManager;

public class AccessibilityHelper {
    public static boolean forceFocusableViews(Context paramContext) {
        return ((AccessibilityManager) paramContext.getSystemService(Context.ACCESSIBILITY_SERVICE)).isEnabled();
    }
}
