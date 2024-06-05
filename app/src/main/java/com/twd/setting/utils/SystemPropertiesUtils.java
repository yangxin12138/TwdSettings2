package com.twd.setting.utils;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.lang.reflect.Method;

public class SystemPropertiesUtils {
    private static final String TAG = "SystemPropertiesUtils";
    private static final String CLASS_NAME = "android.os.SystemProperties";
    public static final String ACTION_DEVICE_NAME_UPDATE =
            "com.twd.setting.utils.SystemPropertiesUtils.DEVICE_NAME_UPDATE";

    public static String getProperty(String key, String defaultValue) {
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

    public static String getDeviceName(Context context) {
        return Settings.Global.getString(context.getContentResolver(), Settings.Global.DEVICE_NAME);
    }

    public static void setDeviceName(Context context, String name) {
        Settings.Global.putString(context.getContentResolver(), Settings.Global.DEVICE_NAME, name);
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter != null) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            btAdapter.setName(name);
        }else {
            Log.v(TAG, "Bluetooth adapter is null. Running on device without bluetooth?");
        }
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(ACTION_DEVICE_NAME_UPDATE));
    }
}
