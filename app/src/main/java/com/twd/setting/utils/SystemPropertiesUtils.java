package com.twd.setting.utils;

import java.lang.reflect.Method;

public class SystemPropertiesUtils {
    private static final String CLASS_NAME = "android.os.SystemProperties";
    public static <T> T getProperty(String key, T defaultValue){

        T value = defaultValue;
        try{
            // 将 defaultValue 转换为 String 类型
            String defaultStringValue = defaultValue != null ? defaultValue.toString() : null;
            Class<?> c = Class.forName(CLASS_NAME);
            Method get = c.getMethod("get",String.class, String.class);
            String result = (String) get.invoke(null, key, defaultStringValue);

            if (defaultValue instanceof Boolean) {
                // 如果 defaultValue 是 Boolean 类型，将结果转换为 Boolean
                value = (T) Boolean.valueOf(result);
            } else {
                value = (T) result;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return value;
        }
    }

    public static void setProperty(String key,String value){
        try{
            Class<?> c = Class.forName(CLASS_NAME);
            Method set = c.getMethod("set",String.class,String.class);
            set.invoke(c,key,value);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String getPropertyColor(String key,String defaultValue){
        String value = defaultValue;
        try{
            Class<?> c = Class.forName(CLASS_NAME);
            Method get = c.getMethod("get",String.class,String.class);
            value = (String) (get.invoke(c,key,defaultValue));
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return value;
        }
    }
}
