package com.twd.setting.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.ParcelUuid;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.twd.setting.commonlibrary.Utils.ReflectUtils;
import com.twd.setting.module.bluetooth.bluetoothlib.BluetoothPairingController;
import com.twd.setting.module.bluetooth.dialog.PairingRequestDialogView;
import com.twd.setting.utils.manager.ViewManager;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;

public class BluetoothUtils {
    public static final ParcelUuid A2DP_SINK = ParcelUuid.fromString("0000110B-0000-1000-8000-00805F9B34FB");
    public static final ParcelUuid ADV_AUDIO_DIST = ParcelUuid.fromString("0000110D-0000-1000-8000-00805F9B34FB");
    private static final String TAG = "BluetoothUtils";

    public static void closeDiscoverableTimeout() {
        BluetoothAdapter localBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        try {
            Method localMethod1 = BluetoothAdapter.class.getMethod("setDiscoverableTimeout", new Class[]{Integer.TYPE});
            localMethod1.setAccessible(true);
            Method localMethod2 = BluetoothAdapter.class.getMethod("setScanMode", new Class[]{Integer.TYPE, Integer.TYPE});
            localMethod2.setAccessible(true);
            localMethod1.invoke(localBluetoothAdapter, new Object[]{Integer.valueOf(1)});
            localMethod2.invoke(localBluetoothAdapter, new Object[]{Integer.valueOf(21), Integer.valueOf(1)});
            return;
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    public static boolean containsAnyUuid(ParcelUuid[] uuid1, ParcelUuid[] uuid2) {
        if ((uuid1 == null) && (uuid2 == null)) {
            return true;
        }
        if (uuid1 == null) {
            return uuid2.length == 0;
        }
        if (uuid2 == null) {
            return uuid1.length == 0;
        }
        HashSet hs_uuid1 = new HashSet(Arrays.asList(uuid1));
        int j = uuid2.length;
        int i = 0;
        while (i < j) {
            if (hs_uuid1.contains(uuid2[i])) {
                return true;
            }
            i += 1;
        }
        return false;
    }

    public static boolean isSupportBluetooth() {
        return BluetoothAdapter.getDefaultAdapter() != null;
    }

    public static boolean isSupportBluetoothLE(Context paramContext) {
        if (paramContext == null) {
            return false;
        }
        return paramContext.getPackageManager().hasSystemFeature("android.hardware.bluetooth_le");
    }

    @SuppressLint("MissingPermission")
    public static void openDiscovered(Context paramContext, int paramInt) {
    /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
      // TODO: Consider calling
      //    ActivityCompat#requestPermissions
      // here to request the missing permissions, and then overriding
      //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
      //                                          int[] grantResults)
      // to handle the case where the user grants the permission. See the documentation
      // for ActivityCompat#requestPermissions for more details.
      return;
    }*/
        if (BluetoothAdapter.getDefaultAdapter().getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Log.d("BluetoothFragment", "openDiscovered  不可以被扫描到  现在开启");
            Intent localIntent = new Intent("android.bluetooth.adapter.action.REQUEST_DISCOVERABLE");
            localIntent.putExtra("android.bluetooth.adapter.extra.DISCOVERABLE_DURATION", paramInt);
            paramContext.startActivity(localIntent);
            return;
        }
        Log.d("BluetoothFragment", "openDiscovered  可以被扫描到  无需重复开启");
    }

    public static void removeDialogView(Context paramContext) {
        try {
            Log.d("BluetoothUtils", "settings-bt : remove mPairingRequestDialogView");
            if (!PairingRequestDialogView.isShow) {
                Log.d("BluetoothUtils", "settings-bt : mPairingRequestDialogView view is already removed,return");
                return;
            }
            Log.d("BluetoothUtils", "settings-bt : remove mPairingRequestDialogView view isShow");
            PairingRequestDialogView.isShow = false;
            ViewManager.getInstance(paramContext).removeTopView();
            return;
        } finally {
        }
    }

    public static void setDiscoverableTimeout() {
        BluetoothAdapter localBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        try {
            Method method = BluetoothAdapter.class.getMethod("setDiscoverableTimeout", new Class[]{Integer.TYPE});
            method.setAccessible(true);
            Method localMethod = BluetoothAdapter.class.getMethod("setScanMode", new Class[]{Integer.TYPE, Integer.TYPE});
            localMethod.setAccessible(true);
            method.invoke(localBluetoothAdapter, new Object[]{Integer.valueOf(0)});
            localMethod.invoke(localBluetoothAdapter, new Object[]{Integer.valueOf(23), Integer.valueOf(0)});
            return;
        } catch (Exception localException) {
            localException.printStackTrace();
            Object localObject = new StringBuilder();
            ((StringBuilder) localObject).append("setDiscoverableTimeout failure:");
            ((StringBuilder) localObject).append(localException.getMessage());
            Log.e("Bluetooth", ((StringBuilder) localObject).toString());
        }
    }

    public static boolean setScanMode(BluetoothAdapter paramBluetoothAdapter, int paramInt) {
        try {
            boolean bool = ((Boolean) ReflectUtils.reflect(paramBluetoothAdapter).method("setScanMode", new Object[]{Integer.valueOf(paramInt)}).get()).booleanValue();
            return bool;
        } catch (Exception e) {
        }
        return false;
    }

    public static void showPairingRequestDialogView(Intent paramIntent, Context paramContext) {
        try {
            if (PairingRequestDialogView.isShow) {
                Log.d("BluetoothUtils", "settings-bt : mPairingRequestDialogView is already showed,return");
                return;
            }
            Object localObject = new BluetoothPairingController(paramIntent, paramContext);
            PairingRequestDialogView view = new PairingRequestDialogView(paramContext, (BluetoothPairingController) localObject);
            int i = ((BluetoothPairingController) localObject).getDialogType();

            Log.d("BluetoothUtils", "settings-bt : showPairingRequestDialogView paring type:" + i);
            if (i == 1) {
                Log.d("BluetoothUtils", "settings-bt : showPairingRequestDialogView CONFIRMATION_DIALOG");
                ViewManager.getInstance(paramContext).addView(view);
            } else if (i == 2) {
                Log.d("BluetoothUtils", "settings-bt : showPairingRequestDialogView paring DISPLAY_PASSKEY_DIALOG");
                ViewManager.getInstance(paramContext).addView(view);
            } else if (i == 0) {
                Log.d("BluetoothUtils", "settings-bt : showPairingRequestDialogView paring USER_ENTRY_DIALOG");
            }
            return;
        } catch (Exception e) {
            Log.d(TAG, "showPairingRequestDialogView  faild");
        } finally {
        }
    }
}
