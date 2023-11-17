package com.twd.setting.module.bluetooth.bluetoothlib;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.text.TextUtils;
import android.util.Log;
//import com.twd.setting.SettingConfig;
//import com.twd.setting.utils.HLog;
import java.nio.charset.StandardCharsets;

public class BluetoothHandsetFilter {
    private static final int COMPLETE_NAME_FLAG = 9;
    private static final int HID_UUID16 = 4388;
    private static final int HOGP_UUID16 = 6162;
    public static String REMOTE_CONTROL_NAME;
    private static final int UUID16_SERVICE_FLAG_COMPLETE = 3;
    private static final int UUID16_SERVICE_FLAG_MORE = 2;

    public static boolean containHogpUUID(byte[] paramArrayOfByte) {
        int n = paramArrayOfByte.length;
        int i = 0;
        while (i < n - 2) {
            int i1 = paramArrayOfByte[i];
            int j = paramArrayOfByte[(i + 1)];
            if ((j == 2) || (j == 3)) {
                j = 0;
                while (j < i1 - 1) {
                    int k = i + j;
                    int m = paramArrayOfByte[(k + 2)];
                    int i2 = k + 3;
                    k = m;
                    if (i2 < n) {
                        k = m + (paramArrayOfByte[i2] << 8);
                    }
                    if (k != 6162) {
                        if (k == 4388) {
                            return true;
                        }
                        j = j + 1 + 1;
                    } else {
                        return true;
                    }
                }
            }
            i += i1 + 1;
        }
        return false;
    }

    @SuppressLint("MissingPermission")
    public static boolean isGoodMatchRc(BluetoothDevice paramBluetoothDevice, int paramInt, byte[] paramArrayOfByte) {
        if (paramArrayOfByte == null) {
            return false;
        }
        boolean bool = containHogpUUID(paramArrayOfByte);
        // if (SettingConfig.IS_DEBUG)
        {
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append("scanRecord 匹配hid结果：");
            localStringBuilder.append(bool);
            localStringBuilder.append("  name=");
            if (paramBluetoothDevice == null) {
                localStringBuilder.append("");
            } else {
                localStringBuilder.append(paramBluetoothDevice.getName());
            }
            localStringBuilder.append("   scanRecord=");
            localStringBuilder.append(new String(paramArrayOfByte, StandardCharsets.UTF_8));
            Log.d("BluetoothHandsetFilter", localStringBuilder.toString());
        }
        return bool;
    }

    public static boolean isNameMatchExName(String paramString1, String paramString2) {
        return TextUtils.equals(paramString1, paramString2);
    }

    public static boolean isNameMatchExName(String paramString, byte[] paramArrayOfByte) {
        int j = paramArrayOfByte.length;
        byte[] arrayOfByte = new byte[50];
        int i = 0;
        while (i < j - 2) {
            int k = paramArrayOfByte[i];
            if (paramArrayOfByte[(i + 1)] == 9) {
                System.arraycopy(paramArrayOfByte, i + 2, arrayOfByte, 0, k - 1);
                if (new String(arrayOfByte, StandardCharsets.UTF_8).equals(paramString)) {
                    return true;
                }
            }
            i += k + 1;
        }
        return false;
    }
}
