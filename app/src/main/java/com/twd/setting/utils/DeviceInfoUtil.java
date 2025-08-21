package com.twd.setting.utils;

import android.content.ContentResolver;
import android.content.Context;

import java.lang.reflect.Method;

/**
 * @Author:Yangxin
 * @Description:
 * @time: Create in 下午7:11 19/8/2025
 */
public class DeviceInfoUtil {
    /**
     * 通过反射获取设备名称
     * @param context 上下文
     * @return 设备名称，获取失败返回null
     */
    public static String getDeviceName(Context context) {
        if (context == null) {
            return null;
        }

        ContentResolver resolver = context.getContentResolver();
        if (resolver == null) {
            return null;
        }

        try {
            // 获取Settings.Global类（内部类需要用$连接）
            Class<?> settingsGlobalClass = Class.forName("android.provider.Settings$Global");

            // 获取getString方法：public static String getString(ContentResolver cr, String name)
            Method getStringMethod = settingsGlobalClass.getMethod(
                    "getString",
                    ContentResolver.class,
                    String.class
            );

            // 调用方法，参数为ContentResolver和键名（注意正确的键是"device_name"）
            Object result = getStringMethod.invoke(
                    null,  // 静态方法，第一个参数为null
                    resolver,
                    "device_name"  // 正确的设备名称键
            );

            return result instanceof String ? (String) result : null;

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
