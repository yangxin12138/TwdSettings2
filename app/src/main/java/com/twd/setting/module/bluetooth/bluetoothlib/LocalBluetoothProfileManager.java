package com.twd.setting.module.bluetooth.bluetoothlib;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
//import com.twd.setting.SettingConfig;
import com.twd.setting.module.bluetooth.model.CachedBluetoothDevice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class LocalBluetoothProfileManager {
    private static final String TAG = "LocalBtProfileManager";
    private A2dpProfile mA2dpProfile;
    private final Context mContext;
    private final CachedBluetoothDeviceManager mDeviceManager;
    private final BluetoothEventManager mEventManager;
    private HidProfile mHidProfile;
    private final Map<String, LocalBluetoothProfile> mProfileNameMap = new HashMap(16, 0.75F);
    private final Collection<ServiceListener> mServiceListeners = new CopyOnWriteArrayList();

    LocalBluetoothProfileManager(Context paramContext, BluetoothEventManager paramBluetoothEventManager, CachedBluetoothDeviceManager paramCachedBluetoothDeviceManager) {
        this.mContext = paramContext;
        this.mDeviceManager = paramCachedBluetoothDeviceManager;
        this.mEventManager = paramBluetoothEventManager;
        //if (SettingConfig.IS_DEBUG) {
        Log.d("LocalBtProfileManager", "LocalBluetoothProfileManager construction complete");
        //}
    }

    private void addProfile(LocalBluetoothProfile paramLocalBluetoothProfile, String paramString1, String paramString2) {
        this.mEventManager.addProfileHandler(paramString2, new StateChangedHandler(paramLocalBluetoothProfile));
        this.mProfileNameMap.put(paramString1, paramLocalBluetoothProfile);
    }

    public void addServiceListener(ServiceListener paramServiceListener) {
        this.mServiceListeners.add(paramServiceListener);
    }

    void callServiceConnectedListeners() {
        Iterator localIterator = new ArrayList(this.mServiceListeners).iterator();
        while (localIterator.hasNext()) {
            ((ServiceListener) localIterator.next()).onServiceConnected();
        }
    }

    public A2dpProfile getA2dpProfile() {
        return this.mA2dpProfile;
    }

    public HidProfile getHidProfile() {
        return this.mHidProfile;
    }

    public LocalBluetoothProfile getProfileByName(String paramString) {
        return (LocalBluetoothProfile) this.mProfileNameMap.get(paramString);
    }

    public boolean isManagerReady() {
        try {
            A2dpProfile localA2dpProfile = this.mA2dpProfile;
            if (localA2dpProfile != null) {
                boolean bool = localA2dpProfile.isProfileReady();
                return bool;
            }
            return false;
        } finally {
        }
    }

    public void removeServiceListener(ServiceListener paramServiceListener) {
        this.mServiceListeners.remove(paramServiceListener);
    }

    public void setBluetoothStateOn() {
        updateLocalProfiles();
    }

    void updateLocalProfiles() {
        Object localObject;
        if (this.mA2dpProfile == null) {
            //if (SettingConfig.IS_DEBUG) {
            Log.d("LocalBtProfileManager", "Adding local A2DP profile");
            //}
            localObject = new A2dpProfile(this.mContext, this.mDeviceManager, this);
            this.mA2dpProfile = ((A2dpProfile) localObject);
            addProfile((LocalBluetoothProfile) localObject, "A2DP", "android.bluetooth.a2dp.profile.action.CONNECTION_STATE_CHANGED");
        }
        if (this.mHidProfile == null) {
            //if (SettingConfig.IS_DEBUG) {
            Log.d("LocalBtProfileManager", "Adding local HID_HOST profile");
            //}
            localObject = new HidProfile(this.mContext, this.mDeviceManager, this);
            this.mHidProfile = ((HidProfile) localObject);
            addProfile((LocalBluetoothProfile) localObject, "HID", "android.bluetooth.input.profile.action.CONNECTION_STATE_CHANGED");
        }
        this.mEventManager.registerProfileIntentReceiver();
    }

    public static abstract interface ServiceListener {
        public abstract void onServiceConnected();
    }

    private class StateChangedHandler
            implements BluetoothEventManager.Handler {
        final LocalBluetoothProfile mProfile;

        StateChangedHandler(LocalBluetoothProfile paramLocalBluetoothProfile) {
            this.mProfile = paramLocalBluetoothProfile;
        }

        public void onReceive(Context paramContext, Intent paramIntent, BluetoothDevice paramBluetoothDevice) {
            CachedBluetoothDevice localCachedBluetoothDevice = mDeviceManager.findDevice(paramBluetoothDevice);
            if (localCachedBluetoothDevice == null) {
                localCachedBluetoothDevice = mDeviceManager.addDevice(paramBluetoothDevice, mProfile);
            }
            onReceiveInternal(paramIntent, localCachedBluetoothDevice);
        }

        protected void onReceiveInternal(Intent paramIntent, CachedBluetoothDevice paramCachedBluetoothDevice) {
            int i = paramIntent.getIntExtra("android.bluetooth.profile.extra.STATE", 0);
            int j = paramIntent.getIntExtra("android.bluetooth.profile.extra.PREVIOUS_STATE", 0);
            StringBuilder str = new StringBuilder();
            str.append("onReceiveInternal() ");
            str.append(this.mProfile);
            str.append(" device, newState=");
            str.append(i);
            str.append(" oldState=");
            str.append(j);
            Log.i("LocalBtProfileManager", str.toString());
            if (paramCachedBluetoothDevice != null) {
                paramCachedBluetoothDevice.onProfileStateChanged(this.mProfile, i);
            }
            LocalBluetoothProfileManager.this.mEventManager.dispatchProfileConnectionStateChanged(paramCachedBluetoothDevice, i, j, this.mProfile.getProfileId());
        }
    }
}
