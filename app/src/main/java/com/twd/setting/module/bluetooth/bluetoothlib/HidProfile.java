package com.twd.setting.module.bluetooth.bluetoothlib;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHidDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
//import android.bluetooth.BluetoothUuid;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;

import com.twd.setting.module.bluetooth.model.CachedBluetoothDevice;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class HidProfile
        implements LocalBluetoothProfile {
    private static final int ID_PROFILE_HID = 4;
    public static final String NAME = "HID";
    //public static final ParcelUuid[] SINK_UUIDS = { BluetoothUuid.Hid, BluetoothUuid.Hogp };
    public static final ParcelUuid[] SINK_UUIDS = {};
    private static final String TAG = "HidProfile";
    private final CachedBluetoothDeviceManager mDeviceManager;
    private boolean mIsProfileReady;
    private BluetoothProfile mService;

    private final class HidHostServiceListener
            implements BluetoothProfile.ServiceListener {
        private HidHostServiceListener() {
        }

        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            HidProfile.this.mService = (BluetoothProfile) proxy;
            //HidProfile.access$002(HidProfile.this, paramBluetoothProfile);
            List<BluetoothDevice> deviceList = mService.getConnectedDevices();
            while (!deviceList.isEmpty()) {
                BluetoothDevice nextDevice = deviceList.remove(0);
                CachedBluetoothDevice device = mDeviceManager.findDevice(nextDevice);

                if (device == null) {

                    Log.w(TAG, "HidProfile found new device: " + nextDevice);
                    device = mDeviceManager.addDevice(nextDevice);
                }
                device.onProfileStateChanged(HidProfile.this, BluetoothProfile.STATE_CONNECTED);
            }
            // HidProfile.access$202(HidProfile.this, true);
            HidProfile.this.mIsProfileReady = true;
        }

        public void onServiceDisconnected(int paramInt) {
            HidProfile.this.mIsProfileReady = false;
            // HidProfile.access$202(HidProfile.this, false);
        }
    }

    HidProfile(Context paramContext, CachedBluetoothDeviceManager paramCachedBluetoothDeviceManager, LocalBluetoothProfileManager paramLocalBluetoothProfileManager) {
        mDeviceManager = paramCachedBluetoothDeviceManager;
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.getProfileProxy(paramContext, new HidHostServiceListener(), /*BluetoothProfile.STATE_DISCONNECTING */4);
        }
    }

    public boolean connect(BluetoothDevice device) {
        if (mService == null)
            return false;
        if ((mService != null) && (device != null)) {
            try {
                Method method = mService.getClass().getDeclaredMethod("connect", new Class[]{BluetoothDevice.class});
                method.setAccessible(true);
                method.invoke(mService, new Object[]{device});
                return true;
            } catch (IllegalAccessException illegalAccessException) {

            } catch (InvocationTargetException invocationTargetException) {

            } catch (NoSuchMethodException noSuchMethodException) {
                noSuchMethodException.printStackTrace();
            }

        }
        return false;
    }

    public boolean disconnect(BluetoothDevice device) {
        if (mService == null) return false;
        if ((mService != null) && (device != null)) {
            try {
                Method method = mService.getClass().getDeclaredMethod("disconnect", new Class[]{BluetoothDevice.class});
                method.setAccessible(true);
                method.invoke(mService, new Object[]{device});
                return true;
            } catch (IllegalAccessException illegalAccessException) {

            } catch (InvocationTargetException invocationTargetException) {

            } catch (NoSuchMethodException noSuchMethodException) {
                noSuchMethodException.printStackTrace();
            }

        }
        return false;
    }

    protected void finalize() {
        Log.d("HidProfile", "finalize()");
        if (mService != null) {
            try {
                BluetoothAdapter.getDefaultAdapter().closeProfileProxy(4, mService);
                mService = null;
            } catch (Throwable t) {
                Log.w("HidProfile", "Error cleaning up HID proxy");
            }
        }
    }

    public int getConnectionStatus(BluetoothDevice device) {
        if ((mService != null) && (device != null)) {
            return mService.getConnectionState(device);
        }
        return 0;
    }

    public int getProfileId() {
        return 4;
    }

    public String getProfileTypeName() {
        return "HID";
    }

    public boolean isAutoConnectable() {
        return true;
    }

    public boolean isProfileReady() {
        return mIsProfileReady;
    }

    public String toString() {
        return "HID";
    }


}
