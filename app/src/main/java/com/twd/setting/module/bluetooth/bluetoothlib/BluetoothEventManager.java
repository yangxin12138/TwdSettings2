package com.twd.setting.module.bluetooth.bluetoothlib;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.UserHandle;
import android.util.Log;
//import com.twd.setting.SettingConfig;
import com.twd.setting.module.bluetooth.model.CachedBluetoothDevice;
//import com.twd.setting.utils.HLog;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class BluetoothEventManager {
    public static final String A2DP_ACTION_ACTIVE_DEVICE_CHANGED = "android.bluetooth.a2dp.profile.action.ACTIVE_DEVICE_CHANGED";
    private static final String TAG = "BluetoothEventManager";
    private boolean isRegisteredProfileReceiver;
    private boolean isRegisteredReceiver;
    private final LeScanCallbackHandler leScanCallbackHandler = new LeScanCallbackHandler();
    private final Object leScanCallbackHandler21 = initLeScanCallbackHandler21();
    private final IntentFilter mAdapterIntentFilter;
    private final BroadcastReceiver mBroadcastReceiver = new BluetoothBroadcastReceiver();
    private final Collection<BluetoothCallback> mCallbacks = new CopyOnWriteArrayList();
    private final Context mContext;
    private final CachedBluetoothDeviceManager mDeviceManager;
    private final Map<String, Handler> mHandlerMap;
    private final BluetoothAdapter mLocalAdapter;
    private final BroadcastReceiver mProfileBroadcastReceiver = new BluetoothBroadcastReceiver();
    private final IntentFilter mProfileIntentFilter;

    BluetoothEventManager(BluetoothAdapter paramBluetoothAdapter, CachedBluetoothDeviceManager paramCachedBluetoothDeviceManager, Context paramContext, Handler paramHandler, UserHandle paramUserHandle) {
        mLocalAdapter = paramBluetoothAdapter;
        mDeviceManager = paramCachedBluetoothDeviceManager;
        mAdapterIntentFilter = new IntentFilter();
        mProfileIntentFilter = new IntentFilter();
        mHandlerMap = new HashMap(16, 0.75F);
        mContext = paramContext;
        addHandler("android.bluetooth.adapter.action.STATE_CHANGED", new AdapterStateChangedHandler());
        addHandler("android.bluetooth.adapter.action.CONNECTION_STATE_CHANGED", new ConnectionStateChangedHandler());
        addHandler("android.bluetooth.adapter.action.DISCOVERY_STARTED", new ScanningStateChangedHandler(true));
        addHandler("android.bluetooth.adapter.action.DISCOVERY_FINISHED", new ScanningStateChangedHandler(false));
        addHandler("android.bluetooth.device.action.FOUND", new DeviceFoundHandler());
        addHandler("android.bluetooth.device.action.BOND_STATE_CHANGED", new BondStateChangedHandler());
        addHandler("android.bluetooth.device.action.CLASS_CHANGED", new ClassChangedHandler());
        addHandler("android.bluetooth.device.action.UUID", new UuidChangedHandler());
        addHandler("android.bluetooth.a2dp.profile.action.ACTIVE_DEVICE_CHANGED", new ActiveDeviceChangedHandler());
        addHandler("android.bluetooth.headset.profile.action.AUDIO_STATE_CHANGED", new AudioModeChangedHandler());
        addHandler("android.bluetooth.device.action.ACL_CONNECTED", new AclStateChangedHandler());
        addHandler("android.bluetooth.device.action.ACL_DISCONNECTED", new AclStateChangedHandler());
        registerAdapterIntentReceiver();
    }

    private void dispatchAclStateChanged(CachedBluetoothDevice paramCachedBluetoothDevice, int paramInt) {
        Iterator localIterator = mCallbacks.iterator();
        while (localIterator.hasNext()) {
            ((BluetoothCallback) localIterator.next()).onAclConnectionStateChanged(paramCachedBluetoothDevice, paramInt);
        }
    }

    private void dispatchAudioModeChanged() {
        Iterator localIterator = mCallbacks.iterator();
        while (localIterator.hasNext()) {
            ((BluetoothCallback) localIterator.next()).onAudioModeChanged();
        }
    }

    private void dispatchConnectionStateChanged(CachedBluetoothDevice paramCachedBluetoothDevice, int paramInt) {
        Iterator localIterator = mCallbacks.iterator();
        while (localIterator.hasNext()) {
            ((BluetoothCallback) localIterator.next()).onConnectionStateChanged(paramCachedBluetoothDevice, paramInt);
        }
    }

    private void dispatchDeviceAdded(BluetoothDevice paramBluetoothDevice, byte[] paramArrayOfByte, int paramInt) {
        Iterator localIterator = mCallbacks.iterator();
        while (localIterator.hasNext()) {
            ((BluetoothCallback) localIterator.next()).onDeviceAdded(paramBluetoothDevice, paramArrayOfByte, paramInt);
        }
    }

    private Object initLeScanCallbackHandler21() {
        if (Build.VERSION.SDK_INT < 21) {
            return null;
        }
        return new ScanCallback() {
            public void onBatchScanResults(List<ScanResult> paramAnonymousList) {
            }

            public void onScanFailed(int state) {
                //if (SettingConfig.IS_DEBUG)
                {
                    Log.d(TAG, "onScanFailed 扫描报错了 errorCode=" + state + " thread=" + Thread.currentThread());
                }
            }

            @SuppressLint("MissingPermission")
            public void onScanResult(int paramAnonymousInt, ScanResult paramAnonymousScanResult) {
                if (paramAnonymousScanResult == null) {
                    return;
                }
                //boolean bool = SettingConfig.IS_DEBUG;
                Object localObject2 = null;
                String name;
                //if (bool)
                {
                    StringBuilder str = new StringBuilder();
                    str.append("onScanResult 扫描来了 callbackType=");
                    str.append(paramAnonymousInt);
                    str.append("  name=");
                    if (paramAnonymousScanResult.getDevice() != null) {
                        name = paramAnonymousScanResult.getDevice().getName();
                    } else {
                        name = null;
                    }
                    str.append(name);
                    str.append(" result=");
                    str.append(paramAnonymousScanResult);
                    str.append(" thread=");
                    str.append(Thread.currentThread());
                    Log.d(TAG, str.toString());
                }
                Object localObject1 = null;
                BluetoothDevice localBluetoothDevice = paramAnonymousScanResult.getDevice();
                if (paramAnonymousScanResult.getScanRecord() == null) {
                    localObject1 = localObject2;
                } else {
                    localObject1 = paramAnonymousScanResult.getScanRecord().getBytes();
                }
                dispatchDeviceAdded(localBluetoothDevice, (byte[]) localObject1, paramAnonymousScanResult.getRssi());
            }
        };
    }

    private void registerIntentReceiver(BroadcastReceiver paramBroadcastReceiver, IntentFilter paramIntentFilter) {
        mContext.registerReceiver(paramBroadcastReceiver, paramIntentFilter);
    }

    void addHandler(String paramString, Handler paramHandler) {
        mHandlerMap.put(paramString, paramHandler);
        mAdapterIntentFilter.addAction(paramString);
    }

    void addProfileHandler(String paramString, Handler paramHandler) {
        mHandlerMap.put(paramString, paramHandler);
        mProfileIntentFilter.addAction(paramString);
    }

    public void clear() {
        mCallbacks.clear();
        unRegisterAdapterIntentReceiver();
        unRegisterProfileIntentReceiver();
    }

    void dispatchActiveDeviceChanged(CachedBluetoothDevice paramCachedBluetoothDevice, int paramInt) {
        Iterator localIterator = mCallbacks.iterator();
        while (localIterator.hasNext()) {
            ((BluetoothCallback) localIterator.next()).onActiveDeviceChanged(paramCachedBluetoothDevice, paramInt);
        }
    }

    void dispatchDeviceAdded(BluetoothDevice paramBluetoothDevice, Intent paramIntent) {
        Iterator localIterator = mCallbacks.iterator();
        while (localIterator.hasNext()) {
            ((BluetoothCallback) localIterator.next()).onDeviceAdded(paramBluetoothDevice, paramIntent);
        }
    }

    void dispatchDeviceAdded(CachedBluetoothDevice paramCachedBluetoothDevice) {
        Iterator localIterator = mCallbacks.iterator();
        while (localIterator.hasNext()) {
            ((BluetoothCallback) localIterator.next()).onDeviceAdded(paramCachedBluetoothDevice);
        }
    }

    void dispatchDeviceRemoved(CachedBluetoothDevice paramCachedBluetoothDevice) {
        Iterator localIterator = mCallbacks.iterator();
        while (localIterator.hasNext()) {
            ((BluetoothCallback) localIterator.next()).onDeviceDeleted(paramCachedBluetoothDevice);
        }
    }

    void dispatchProfileConnectionStateChanged(CachedBluetoothDevice paramCachedBluetoothDevice, int paramInt1, int paramInt2, int paramInt3) {
        Iterator localIterator = mCallbacks.iterator();
        while (localIterator.hasNext()) {
            ((BluetoothCallback) localIterator.next()).onProfileConnectionStateChanged(paramCachedBluetoothDevice, paramInt1, paramInt2, paramInt3);
        }
    }

    public LeScanCallbackHandler getLeScanCallbackHandler() {
        return leScanCallbackHandler;
    }

    public ScanCallback getLeScanCallbackHandler21() {
        Object localObject = leScanCallbackHandler21;
        if (localObject == null) {
            return null;
        }
        return (ScanCallback) localObject;
    }

    @SuppressLint("MissingPermission")
    public List<CachedBluetoothDevice> readBondedDevices() {
        Object localObject = mLocalAdapter.getBondedDevices();
        if (localObject == null) {
            return null;
        }
        ArrayList localArrayList = new ArrayList();
        Iterator localIterator = ((Set) localObject).iterator();
        while (localIterator.hasNext()) {
            BluetoothDevice localBluetoothDevice = (BluetoothDevice) localIterator.next();
            CachedBluetoothDevice localCachedBluetoothDevice = mDeviceManager.findDevice(localBluetoothDevice);
            localObject = localCachedBluetoothDevice;
            if (localCachedBluetoothDevice == null) {
                localObject = mDeviceManager.addDevice(localBluetoothDevice);
            }
            ((CachedBluetoothDevice) localObject).refresh();
            ((CachedBluetoothDevice) localObject).setJustDiscovered(false);
            localArrayList.add(localObject);
        }
        return localArrayList;
    }

    public void registerAdapterIntentReceiver() {
        isRegisteredReceiver = true;
        registerIntentReceiver(mBroadcastReceiver, mAdapterIntentFilter);
    }

    public void registerCallback(BluetoothCallback paramBluetoothCallback) {
        mCallbacks.add(paramBluetoothCallback);
    }

    public void registerProfileIntentReceiver() {
        isRegisteredProfileReceiver = true;
        registerIntentReceiver(mProfileBroadcastReceiver, mProfileIntentFilter);
    }

    public void unRegisterAdapterIntentReceiver() {
        if ((mContext != null) && (isRegisteredReceiver)) {
            mContext.unregisterReceiver(mBroadcastReceiver);
            isRegisteredReceiver = false;
        }
    }

    public void unRegisterProfileIntentReceiver() {
        if ((mContext != null) && (isRegisteredProfileReceiver)) {
            mContext.unregisterReceiver(mProfileBroadcastReceiver);
            isRegisteredProfileReceiver = false;
        }
    }

    public void unregisterCallback(BluetoothCallback paramBluetoothCallback) {
        mCallbacks.remove(paramBluetoothCallback);
    }

    private class AclStateChangedHandler
            implements BluetoothEventManager.Handler {
        private AclStateChangedHandler() {
        }

        public void onReceive(Context paramContext, Intent paramIntent, BluetoothDevice paramBluetoothDevice) {
            if (paramBluetoothDevice == null) {
                Log.w(TAG, "AclStateChangedHandler: device is null");
                return;
            }
            String action = paramIntent.getAction();
            if (action == null) {
                Log.w(TAG, "AclStateChangedHandler: action is null");
                return;
            }
            CachedBluetoothDevice cachedBluetoothDevice = mDeviceManager.findDevice(paramBluetoothDevice);
            if (cachedBluetoothDevice == null) {
                Log.w(TAG, "AclStateChangedHandler: activeDevice is null");
                return;
            }
            cachedBluetoothDevice.hashCode();
            int i;
            if (!action.equals("android.bluetooth.device.action.ACL_CONNECTED")) {
                if (!action.equals("android.bluetooth.device.action.ACL_DISCONNECTED")) {
                    Log.w(TAG, "ActiveDeviceChangedHandler: unknown action " + action);
                    return;
                }
                i = 0;
            } else {
                i = 2;
            }
            dispatchAclStateChanged(cachedBluetoothDevice, i);
        }
    }

    private class ActiveDeviceChangedHandler
            implements BluetoothEventManager.Handler {
        private ActiveDeviceChangedHandler() {
        }

        public void onReceive(Context paramContext, Intent paramIntent, BluetoothDevice paramBluetoothDevice) {
            String action = paramIntent.getAction();
            if (action == null) {
                Log.w(TAG, "ActiveDeviceChangedHandler: action is null");
                return;
            }
            CachedBluetoothDevice cachedBluetoothDevice = mDeviceManager.findDevice(paramBluetoothDevice);
            if (Objects.equals(action, "android.bluetooth.a2dp.profile.action.ACTIVE_DEVICE_CHANGED")) {
                dispatchActiveDeviceChanged(cachedBluetoothDevice, 2);
                return;
            }
            Log.w(TAG, "ActiveDeviceChangedHandler: unknown action " + paramContext);
        }
    }

    private class AdapterStateChangedHandler
            implements BluetoothEventManager.Handler {
        private AdapterStateChangedHandler() {
        }

        public void onReceive(Context paramContext, Intent paramIntent, BluetoothDevice paramBluetoothDevice) {
            int i = paramIntent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE);
            Iterator iterator = mCallbacks.iterator();
            while (iterator.hasNext()) {
                ((BluetoothCallback) iterator.next()).onBluetoothStateChanged(i);
            }
            mDeviceManager.onBluetoothStateChanged(i);
        }
    }

    private class AudioModeChangedHandler
            implements BluetoothEventManager.Handler {
        private AudioModeChangedHandler() {
        }

        public void onReceive(Context paramContext, Intent paramIntent, BluetoothDevice paramBluetoothDevice) {
            if (paramIntent.getAction() == null) {
                Log.w(TAG, "AudioModeChangedHandler() action is null");
                return;
            }
            dispatchAudioModeChanged();
        }
    }

    private class BluetoothBroadcastReceiver
            extends BroadcastReceiver {
        private BluetoothBroadcastReceiver() {
        }

        public void onReceive(Context paramContext, Intent paramIntent) {
            Object localObject = paramIntent.getAction();
            BluetoothDevice localBluetoothDevice = (BluetoothDevice) paramIntent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
            localObject = (BluetoothEventManager.Handler) BluetoothEventManager.this.mHandlerMap.get(localObject);
            if (localObject != null) {
                ((BluetoothEventManager.Handler) localObject).onReceive(paramContext, paramIntent, localBluetoothDevice);
            }
        }
    }

    private class BondStateChangedHandler
            implements BluetoothEventManager.Handler {
        private BondStateChangedHandler() {
        }

        public void onReceive(Context paramContext, Intent paramIntent, BluetoothDevice paramBluetoothDevice) {
            if (paramBluetoothDevice == null) {
                Log.e(TAG, "ACTION_BOND_STATE_CHANGED with no EXTRA_DEVICE");
                return;
            }
            int i = paramIntent.getIntExtra("android.bluetooth.device.extra.BOND_STATE", Integer.MIN_VALUE);
            int j = paramIntent.getIntExtra("android.bluetooth.device.extra.PREVIOUS_BOND_STATE", 10);
            CachedBluetoothDevice cachedBluetoothDevice = mDeviceManager.findDevice(paramBluetoothDevice);

            if (cachedBluetoothDevice == null) {
                Log.d(TAG, "Got bonding state changed for " + paramBluetoothDevice + ", but we have no record of that device.");
                cachedBluetoothDevice = mDeviceManager.addDevice(paramBluetoothDevice);
            }
            cachedBluetoothDevice.onBondingStateChanged(i, j);
            Iterator iterator = mCallbacks.iterator();
            while (iterator.hasNext()) {
                ((BluetoothCallback) iterator.next()).onDeviceBondStateChanged(cachedBluetoothDevice, i, j);
            }
        }
    }

    private class ClassChangedHandler
            implements BluetoothEventManager.Handler {
        private ClassChangedHandler() {
        }

        public void onReceive(Context paramContext, Intent paramIntent, BluetoothDevice paramBluetoothDevice) {
            CachedBluetoothDevice cachedBluetoothDevice = mDeviceManager.findDevice(paramBluetoothDevice);
            BluetoothClass bluetoothClass = (BluetoothClass) paramIntent.getParcelableExtra("android.bluetooth.device.extra.CLASS");
            if (cachedBluetoothDevice != null) {
                cachedBluetoothDevice.updateProfileByClass(bluetoothClass);
                cachedBluetoothDevice.refresh();
            }
        }
    }

    private class ConnectionStateChangedHandler
            implements BluetoothEventManager.Handler {
        private ConnectionStateChangedHandler() {
        }

        public void onReceive(Context paramContext, Intent paramIntent, BluetoothDevice paramBluetoothDevice) {
            int i = paramIntent.getIntExtra("android.bluetooth.adapter.extra.CONNECTION_STATE", Integer.MIN_VALUE);
            int j = paramIntent.getIntExtra("android.bluetooth.adapter.extra.PREVIOUS_CONNECTION_STATE", Integer.MIN_VALUE);
            CachedBluetoothDevice cachedBluetoothDevice = mDeviceManager.findDevice(paramBluetoothDevice);

            if (cachedBluetoothDevice == null) {
                Log.d(TAG, "Got connecting state changed for " + paramBluetoothDevice + ", but we have no record of that device.");
                cachedBluetoothDevice = mDeviceManager.addDevice(paramBluetoothDevice);
            }
            cachedBluetoothDevice.onConnectingStateChanged(i, j);
            dispatchConnectionStateChanged(cachedBluetoothDevice, i);
        }
    }

    private class DeviceFoundHandler
            implements BluetoothEventManager.Handler {
        private DeviceFoundHandler() {
        }

        public void onReceive(Context paramContext, Intent paramIntent, BluetoothDevice paramBluetoothDevice) {
            BluetoothEventManager.this.dispatchDeviceAdded(paramBluetoothDevice, paramIntent);
        }
    }

    static abstract interface Handler {
        public abstract void onReceive(Context paramContext, Intent paramIntent, BluetoothDevice paramBluetoothDevice);
    }

    private class LeScanCallbackHandler
            implements BluetoothAdapter.LeScanCallback {
        private LeScanCallbackHandler() {
        }

        public void onLeScan(BluetoothDevice paramBluetoothDevice, int paramInt, byte[] paramArrayOfByte) {
            //if (SettingConfig.IS_DEBUG)
            {
                StringBuilder localStringBuilder = new StringBuilder();
                localStringBuilder.append("onLeScan 扫描来了 scanRecord=");
                localStringBuilder.append(new String(paramArrayOfByte, StandardCharsets.UTF_8));
                localStringBuilder.append("  device=");
                localStringBuilder.append(paramBluetoothDevice);
                localStringBuilder.append(" thread=");
                localStringBuilder.append(Thread.currentThread());
                Log.d(TAG, localStringBuilder.toString());
            }
            dispatchDeviceAdded(paramBluetoothDevice, paramArrayOfByte, paramInt);
        }
    }

    private class ScanningStateChangedHandler
            implements BluetoothEventManager.Handler {
        private final boolean mStarted;

        ScanningStateChangedHandler(boolean paramBoolean) {
            mStarted = paramBoolean;
        }

        public void onReceive(Context paramContext, Intent paramIntent, BluetoothDevice paramBluetoothDevice) {
            Iterator iterator = mCallbacks.iterator();
            while (iterator.hasNext()) {
                ((BluetoothCallback) iterator.next()).onScanningStateChanged(mStarted);
            }
        }
    }

    private class UuidChangedHandler
            implements BluetoothEventManager.Handler {
        private UuidChangedHandler() {
        }

        public void onReceive(Context paramContext, Intent paramIntent, BluetoothDevice paramBluetoothDevice) {
            mDeviceManager.onUuidChanged(paramBluetoothDevice);
        }
    }
}
