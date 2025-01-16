package com.twd.setting.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

public class SystemPropertiesUtils {
    private static final String CLASS_NAME = "android.os.SystemProperties";
    public static String getProperty(String key, String defaultValue){
        String value = defaultValue;
        try{
            Class<?> c = Class.forName(CLASS_NAME);
            Method get = c.getMethod("get",String.class, String.class);
            value = (String)(get.invoke(c,key,defaultValue));
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

    public static String readSystemFile(String path){
        StringBuilder content = new StringBuilder();
        File file = new File(path);
        try{
            FileInputStream fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line;
            while ((line = reader.readLine()) != null){
                content.append(line).append("\n");
            }
            reader.close();
            fis.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return content.toString();
    }
}
