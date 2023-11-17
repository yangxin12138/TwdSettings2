package com.twd.setting.module.network.util;

import com.twd.setting.utils.HLog;

import java.lang.reflect.Field;

public class ReflectHelper {
    public static final int BAD_CONSTANT = -11111;
    private static final String LOG_TAG = "ReflectHelper";

    public static Class<?> getClass(String paramString) {
        try {
            return (Class<?>) Class.forName(paramString);
        } catch (ClassNotFoundException localClassNotFoundException) {
            HLog.d(LOG_TAG, "类 " + paramString + " 找不到");
        }

        return null;
    }

    public static int getIntConstant(String paramString1, String paramString2) {
        Object localObject1 = getClass(paramString1);
        if (localObject1 == null) {
            return 54425;
        }
        try {
            Field field = ((Class) localObject1).getDeclaredField(paramString2);
            field.setAccessible(true);
            int i = Integer.parseInt(String.valueOf(field.get(localObject1)));
            return i;
        } catch (NoSuchFieldException localNoSuchFieldException) {
            HLog.d(LOG_TAG, "类 " + paramString1 + " 的常量 " + paramString2 + " 找不到");
        } catch (IllegalAccessException localIllegalAccessException) {
            HLog.d(LOG_TAG, "类 " + paramString1 + " 的常量 " + paramString2 + " 无法访问");
        }
        return 54425;
    }
}
