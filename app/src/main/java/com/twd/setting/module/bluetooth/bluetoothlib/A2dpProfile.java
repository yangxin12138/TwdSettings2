package com.twd.setting.module.bluetooth.bluetoothlib;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
//import android.bluetooth.BluetoothUuid;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;

import com.twd.setting.module.bluetooth.model.CachedBluetoothDevice;
//import com.twd.setting.utils.HLog;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

public class A2dpProfile
        implements LocalBluetoothProfile {
    public static final String NAME = "A2DP";
    //public static final ParcelUuid[] SINK_UUIDS = { BluetoothUuid.AudioSink, BluetoothUuid.AdvAudioDist };
    public static final ParcelUuid[] SINK_UUIDS = {};
    private static final String TAG = "A2dpProfile";
    private final CachedBluetoothDeviceManager mDeviceManager;
    private boolean mIsProfileReady;
    private final LocalBluetoothProfileManager mProfileManager;
    private BluetoothA2dp mService;

    A2dpProfile(Context paramContext, CachedBluetoothDeviceManager paramCachedBluetoothDeviceManager, LocalBluetoothProfileManager paramLocalBluetoothProfileManager) {
        mDeviceManager = paramCachedBluetoothDeviceManager;
        mProfileManager = paramLocalBluetoothProfileManager;
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.getProfileProxy(paramContext, new A2dpServiceListener(), BluetoothProfile.STATE_CONNECTED);
        }
    }

    public boolean connect(BluetoothDevice device) {
        if (mService == null)
            return false;
        if ((mService != null) && (device != null)) {
            try {
                Method method = mService.getClass().getDeclaredMethod("connect", new Class[]{BluetoothDevice.class});
                method.setAccessible(true);
                Object b = method.invoke(mService, new Object[]{device});
                if (!(b instanceof Boolean)) {
                    return false;
                }
                boolean bool = ((Boolean) b).booleanValue();
                return bool;
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
                Object b = method.invoke(mService, new Object[]{device});
                if (!(b instanceof Boolean)) {
                    return false;
                }
                boolean bool = ((Boolean) b).booleanValue();
                return bool;
            } catch (IllegalAccessException illegalAccessException) {

            } catch (InvocationTargetException invocationTargetException) {

            } catch (NoSuchMethodException noSuchMethodException) {
                noSuchMethodException.printStackTrace();
            }

        }

        return false;
    }

    protected void finalize() {
        Log.d("A2dpProfile", "finalize()");
        if (mService != null) {
            try {
                BluetoothAdapter.getDefaultAdapter().closeProfileProxy(BluetoothProfile.A2DP, mService);
                mService = null;
            } catch (Throwable t) {
                Log.e("A2dpProfile", "Error cleaning up A2DP proxy", t);
            }
        }
    }

    @SuppressLint("MissingPermission")
    public int getConnectionStatus(BluetoothDevice device) {
        if (mService == null) {
            return BluetoothProfile.STATE_DISCONNECTED;
        }
        //if ((mService != null) && (paramBluetoothDevice != null)) {
        return mService.getConnectionState(device);
        //}
        //return 0;
    }

    public int getProfileId() {
        return 2;
    }

    public String getProfileTypeName() {
        return "A2DP";
    }

    @SuppressLint("MissingPermission")
    boolean isA2dpPlaying() {
        if (mService == null) return false;
        List<BluetoothDevice> sinks = mService.getConnectedDevices();
        if (!sinks.isEmpty()) {
            if (mService.isA2dpPlaying(sinks.get(0))) {
                return true;
            }
        }
        return false;
    /*Iterator iterator = ((BluetoothA2dp)mService).getConnectedDevices().iterator();
    while (iterator.hasNext())
    {
      BluetoothDevice localBluetoothDevice = (BluetoothDevice)iterator.next();
      if (mService.isA2dpPlaying(localBluetoothDevice)) {
        return true;
      }
    }
    return false;
     */
    }

    public boolean isAutoConnectable() {
        return true;
    }

    public boolean isProfileReady() {
        return mIsProfileReady;
    }

    public String toString() {
        return "A2DP";
    }

    private final class A2dpServiceListener
            implements BluetoothProfile.ServiceListener {
        private A2dpServiceListener() {
        }

        @SuppressLint("MissingPermission")
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            A2dpProfile.this.mService = (BluetoothA2dp) proxy;
            // A2dpProfile.access$002(A2dpProfile.this, (BluetoothA2dp)paramBluetoothProfile);
            List<BluetoothDevice> deviceList = mService.getConnectedDevices();
            while (!deviceList.isEmpty()) {
                BluetoothDevice nextDevice = deviceList.remove(0);
                CachedBluetoothDevice device = mDeviceManager.findDevice(nextDevice);
                if (device == null) {
                    Log.w(TAG, "A2dpProfile found new device: " + nextDevice);
                    device = mDeviceManager.addDevice(nextDevice);
                }
                device.onProfileStateChanged(A2dpProfile.this, BluetoothProfile.STATE_CONNECTED);
            }
            //     A2dpProfile.access$202(A2dpProfile.this, true);
            A2dpProfile.this.mIsProfileReady = true;
            mProfileManager.callServiceConnectedListeners();
        }

        public void onServiceDisconnected(int profile) {
            A2dpProfile.this.mIsProfileReady = false;
            //A2dpProfile.access$202(A2dpProfile.this, false);
        }
    }
}
