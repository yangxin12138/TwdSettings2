package com.twd.setting.module.bluetooth.bluetoothlib;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.twd.setting.commonlibrary.Utils.ReflectUtils;
import com.twd.setting.module.bluetooth.dialog.PairingRequestDialogView;
import com.twd.setting.module.bluetooth.dialog.PairingRequestDialogView.BluetoothPairingDialogListener;
//import com.twd.setting.utils.HLog;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

public class BluetoothPairingController
        implements PairingRequestDialogView.BluetoothPairingDialogListener {
    public static final int CONFIRMATION_DIALOG = 1;
    public static final int DISPLAY_PASSKEY_DIALOG = 2;
    public static final int INVALID_DIALOG_TYPE = -1;
    private static final int PAIRING_VARIANT_CONSENT = 3;
    private static final int PAIRING_VARIANT_DISPLAY_PASSKEY = 4;
    private static final int PAIRING_VARIANT_DISPLAY_PIN = 5;
    private static final int PAIRING_VARIANT_OOB_CONSENT = 6;
    private static final int PAIRING_VARIANT_PASSKEY = 1;
    private static final String TAG = "BtPairingController";
    public static final int USER_ENTRY_DIALOG = 0;
    private BluetoothDevice mDevice;
    private int mPasskey;
    private String mPasskeyFormatted;
    private int mType;
    private String mUserInput;
    private Context mContext;

    public BluetoothPairingController(Intent paramIntent, Context paramContext) {
        this.mContext = paramContext;
        this.mDevice = ((BluetoothDevice) paramIntent.getParcelableExtra("android.bluetooth.device.extra.DEVICE"));
        this.mType = paramIntent.getIntExtra("android.bluetooth.device.extra.PAIRING_VARIANT", Integer.MIN_VALUE);
        int i = paramIntent.getIntExtra("android.bluetooth.device.extra.PAIRING_KEY", Integer.MIN_VALUE);
        this.mPasskey = i;
        this.mPasskeyFormatted = formatKey(i);
        StringBuilder str = new StringBuilder();
        str.append("mType :");
        str.append(this.mType);
        str.append(" -mPasskey:");
        str.append(this.mPasskey);
        str.append("-mPasskeyFormatted:");
        str.append(this.mPasskeyFormatted);
        str.append("-mDeviceName:");
        str.append(getDeviceName());
        Log.d(TAG, str.toString());
    }

    public static byte[] convertPinToBytes(String paramString) {
        if (paramString == null) {
            return null;
        }
        try {
            byte[] bytes = paramString.getBytes("UTF-8");
            if (bytes.length > 0) {
                if (bytes.length > 16) {
                    return null;
                }
                return bytes;
            }
            return null;
        } catch (UnsupportedEncodingException unsupportedEncodingException) {

        }
        Log.d(TAG, "UTF-8 not supported?!?");
        return null;
    }

    private String formatKey(int paramInt) {
        int i = this.mType;
        if ((i != 2) && (i != 4)) {
            if (i != 5) {
                return null;
            }
            return String.format("%04d", new Object[]{Integer.valueOf(paramInt)});
        }
        return String.format(Locale.US, "%06d", new Object[]{Integer.valueOf(paramInt)});
    }

    public void onPair(String paramString) {
        Log.d(TAG, "配对开始");
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Log.d(TAG, "onPair: mType = " +mType);
       /* switch (mType) {
            default:
                Log.d(TAG, "error :Incorrect pairing type received");
                return;
        }
        try {
            ReflectUtils.reflect(mDevice).method("setRemoteOutOfBandData").get();
            return;
        } catch (Exception e) {
        }
        byte[] bytes = convertPinToBytes(mPasskeyFormatted);
        if (paramString != null) {
            mDevice.setPin(paramString.getBytes());
          return;
          mDevice.setPairingConfirmation(true);
          int i = Integer.parseInt(paramString);
          ReflectUtils.reflect(mDevice).method("setPasskey", new Object[] { Integer.valueOf(i) }).get();
        }*/
        switch (mType){
            case BluetoothDevice.PAIRING_VARIANT_PIN:
                mDevice.setPin(paramString.getBytes());
                break;
            case BluetoothDevice.PAIRING_VARIANT_PASSKEY_CONFIRMATION:
                mDevice.setPairingConfirmation(true);
                break;
            default:
                //do noting
                mDevice.setPairingConfirmation(true);
                Log.e(TAG, "收到其他类型");
                break;
        }

    }

    @SuppressLint("MissingPermission")
    public String getDeviceName() {
        if (mDevice == null) {
            return "";
        }
        String str1;
        if (Build.VERSION.SDK_INT >= 30) {
            str1 = mDevice.getAlias();
        } else {
            str1 = mDevice.getName();
        }
        String str2 = str1;
        if (TextUtils.isEmpty(str1)) {
            str2 = mDevice.getAddress();
        }
        return str2;
    }

    public int getDialogType() {
        switch (mType) {
            default:
                return -1;
            case 4:
            case 5:
                return 2;
            case 2:
            case 3:
            case 6:
                return 1;
        }
        //return 0;
    }

    public String getPasskeyFormatted() {
        return mPasskeyFormatted;
    }

    public void onCancel() {
        Log.d(TAG, "Pairing dialog canceled cancel paring process");
 /*   try
    {
      ReflectUtils.reflect(mDevice).method("cancelPairingUserInput").get();
      return;
    }
    catch (Exception localException) {}

  */
    }

    public void onDialogNegativeClick(PairingRequestDialogView paramPairingRequestDialogView) {
        onCancel();
    }

    public void onDialogPositiveClick(PairingRequestDialogView paramPairingRequestDialogView) {
        if (getDialogType() == 0) {
            onPair(mUserInput);
            return;
        }
        onPair(null);
    }
}
