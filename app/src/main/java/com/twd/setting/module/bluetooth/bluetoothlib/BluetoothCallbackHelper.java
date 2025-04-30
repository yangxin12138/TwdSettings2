package com.twd.setting.module.bluetooth.bluetoothlib;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
//import com.twd.setting.SettingConfig;
import com.twd.setting.module.bluetooth.model.CachedBluetoothDevice;
//import com.twd.setting.utils.HLog;
//import com.twd.setting.utils.KkDataUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BluetoothCallbackHelper {
    private static final int ID_REFRESH_LIST = 100;
    private static final String _TAG = "BluetoothCallbackHelper";
    private final BluetoothCallback bluetoothCallback = new BluetoothCallback() {
        public void onAclConnectionStateChanged(CachedBluetoothDevice device, int state) {
            if (device == null) {
                Log.d(_TAG, "onAclConnectionStateChanged  cachedDevice null");
                return;
            }
            Log.d(_TAG, "onAclConnectionStateChanged  name=" + device.getName() + "  state=" + state);
        }

        public void onActiveDeviceChanged(CachedBluetoothDevice device, int state) {
            if (device == null) {
                Log.d(_TAG, "onActiveDeviceChanged  cachedDevice null");
                return;
            }
            Log.d(_TAG, "onDeviceDeleted  name=" + device.getName() + "  bluetoothProfile=" + state);
        }

        public void onAudioModeChanged() {
            Log.d(_TAG, "onAudioModeChanged  ");
        }

        public void onBluetoothStateChanged(int state) {
            Log.d(_TAG, "onBluetoothStateChanged  bluetoothState=" + state);
            if ((state == 12) && (mProfileManager != null)) {
                mProfileManager.setBluetoothStateOn();
            }
            Iterator iterator = resultCallbackList.iterator();
            while (iterator.hasNext()) {
                ((OnBluetoothResultCallback) iterator.next()).onBluetoothStateChanged(state);
            }
        }

        public void onConnectionStateChanged(CachedBluetoothDevice device, int state) {
            if (device == null) {
                Log.d(_TAG, "scanlist onConnectionStateChanged  cachedDevice null");
                return;
            }
            Log.d(_TAG, "onConnectionStateChanged  name=" + device.getName() + "  state=" + state + "  thread=" + Thread.currentThread().getName());
            Iterator iterator = resultCallbackList.iterator();
            while (iterator.hasNext()) {
                ((OnBluetoothResultCallback) iterator.next()).onConnectionStateChanged(device, state);
            }
        }

        public void onDeviceAdded(BluetoothDevice device, Intent state) {
            if (device == null) {
                Log.d(_TAG, "onDeviceAdded  device = null");
                return;
            }
            addScanDevices(device, null, 0, state);
        }

        public void onDeviceAdded(BluetoothDevice device, byte[] bytes, int state) {
            if (device == null) {
                Log.d(_TAG, "onDeviceAdded 2  device = null");
                return;
            }
            addScanDevices(device, bytes, state, null);
        }

        public void onDeviceAdded(CachedBluetoothDevice device) {
            if (device == null) {
                return;
            }
            device.refresh();
            sendData(device);
        }

        public void onDeviceBondStateChanged(CachedBluetoothDevice device, int state1, int state2) {
            if (device == null) {
                Log.d(_TAG, "scanlist onDeviceBondStateChanged  cachedDevice null");
                return;
            }
            Log.d(_TAG, "onDeviceBondStateChanged  name=" + device.getName() + "  bondState=" + state1 + "  thread=" + Thread.currentThread().getName());
            if ((state1 == 10) && (state2 == 11)) {
                String str;
                if (device.isBluetoothRc()) {
                    str = "蓝牙遥控器连接";
                } else {
                    str = "蓝牙连接";
                }
                //       KkDataUtils.sentEventError((String)localObject, null);
            }
            Iterator iterator = resultCallbackList.iterator();
            while (iterator.hasNext()) {
                ((OnBluetoothResultCallback) iterator.next()).onDeviceBondStateChanged(device, state1, state2);
            }
        }

        public void onDeviceDeleted(CachedBluetoothDevice device) {
            if (device == null) {
                Log.d(_TAG, "onDeviceDeleted  cachedDevice null");
                return;
            }
            Log.d(_TAG, "onDeviceDeleted  name=" + device.getName());
        }

        public void onProfileConnectionStateChanged(CachedBluetoothDevice device, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {
            if (device == null) {
                Log.d(_TAG, "onProfileConnectionStateChanged  cachedDevice null");
                return;
            }
            Log.d(_TAG, "onProfileConnectionStateChanged  name=" + device.getName());
            Iterator iterator = resultCallbackList.iterator();
            while (iterator.hasNext()) {
                ((OnBluetoothResultCallback) iterator.next()).onProfileConnectionStateChanged(device, paramAnonymousInt1, paramAnonymousInt2);
            }
            String str = "蓝牙遥控器连接";
            if (paramAnonymousInt1 == 2) {
                if ((paramAnonymousInt2 == 1) || (paramAnonymousInt2 == 0)) {
                    if (!device.isBluetoothRc()) {
                        str = "蓝牙连接";
                    }
                    Log.d(_TAG, "onProfileConnectionStateChanged  " + str);
                    //         KkDataUtils.sentEventActive((String)localObject, null);
                }
                return;
            }
            if ((paramAnonymousInt1 == 0) && (paramAnonymousInt2 == 1)) {
                if (!device.isBluetoothRc()) {
                    str = "蓝牙连接";
                }
                Log.d(_TAG, "onProfileConnectionStateChanged  " + str);
//        KkDataUtils.sentEventError((String)localObject, null);
            }
        }

        public void onScanningStateChanged(boolean state) {
            Log.d(_TAG, "onScanningStateChanged  started=" + state);
            scanningStateChanged(state);
        }
    };
    private final CachedBluetoothDeviceManager mCachedDeviceManager;
    private final LocalBluetoothProfileManager mProfileManager;
    private final HandlerThread mScanResultHT;
    private final Handler mScanResultHandler;
    private final LocalBluetoothProfileManager.ServiceListener profileServiceListener = new LocalBluetoothProfileManager.ServiceListener() {
        @Override
        public void onServiceConnected() {
            Iterator localIterator = BluetoothCallbackHelper.this.resultCallbackList.iterator();
            while (localIterator.hasNext()) {
                ((OnBluetoothResultCallback) localIterator.next()).onProfileConnectionStateChanged(null, 0, 0);
            }
        }
    };
    private final List<OnBluetoothResultCallback> resultCallbackList = new ArrayList();

    public BluetoothCallbackHelper(LocalBluetoothProfileManager paramLocalBluetoothProfileManager, CachedBluetoothDeviceManager paramCachedBluetoothDeviceManager) {
        mProfileManager = paramLocalBluetoothProfileManager;
        mCachedDeviceManager = paramCachedBluetoothDeviceManager;
        mScanResultHT = new HandlerThread("scan_result");
        mScanResultHT.start();
        mScanResultHandler = new LeScanResultHandler(mScanResultHT.getLooper(), null);
    }

    private void addScanDevices(final BluetoothDevice device, final byte[] paramArrayOfByte, final int paramInt, final Intent paramIntent) {
        Handler localHandler = mScanResultHandler;
        if (localHandler == null) {
            return;
        }
        localHandler.post(new Runnable() {
            public void run() {
                int i = paramInt;
                Object localObject = paramIntent;
                if (localObject != null) {
                    localObject = (BluetoothClass) ((Intent) localObject).getParcelableExtra("android.bluetooth.device.extra.CLASS");
                    i = paramIntent.getShortExtra("android.bluetooth.device.extra.RSSI", (short) Short.MIN_VALUE);
                } else {
                    localObject = null;
                }
                CachedBluetoothDevice nextDevice = mCachedDeviceManager.findDevice(device);
                byte[] arrayOfByte;
                if (nextDevice == null) {
                    nextDevice = mCachedDeviceManager.addDevice(device, true, paramArrayOfByte, i, false);
                    if (nextDevice.getLocalBtProfile() == null) {
                        arrayOfByte = paramArrayOfByte;
                        if (arrayOfByte != null) {
                            nextDevice.updateProfileByScanRecord(arrayOfByte, i);
                        } else {
                            nextDevice.updateProfileByClass((BluetoothClass) localObject);
                        }
                    }
                    nextDevice.setRssi(i);
                    sendData(nextDevice);
                    //if (SettingConfig.IS_DEBUG)
                    {
                        Log.d("Doubao", "setBtScanResult DeviceFoundHandler 发现设备：name=" + nextDevice.getName() + " addr=" + nextDevice.getAddress() + " thread=" + Thread.currentThread());
                    }
                    return;
                }
                if (nextDevice.getLocalBtProfile() == null) {
                    arrayOfByte = paramArrayOfByte;
                    if (arrayOfByte != null) {
                        nextDevice.updateProfileByScanRecord(arrayOfByte, i);
                    } else {
                        nextDevice.updateProfileByClass((BluetoothClass) localObject);
                    }
                }
                nextDevice.setJustDiscovered(true);
                nextDevice.setRssi(i);
                if ((nextDevice.getBondState() == 12) && (!nextDevice.isConnected())) {
                    sendData(nextDevice);
                }
            }
        });
    }

    private void sendData(CachedBluetoothDevice device) {
        if (mScanResultHandler == null) {
            return;
        }
        if (!mScanResultHandler.hasMessages(100)) {
            mScanResultHandler.sendEmptyMessageDelayed(100, 1000L);
        }
        if ((device != null) && (device.isBluetoothRc())) {
            Iterator iterator = resultCallbackList.iterator();
            while (iterator.hasNext()) {
                ((OnBluetoothResultCallback) (iterator.next())).onScanHandsetItem(device);
            }
        }
    }

    public void clear() {
        resultCallbackList.clear();

        if (mScanResultHT != null) {
            mScanResultHT.quit();
        }
    }

    public BluetoothCallback getBluetoothCallback() {
        return bluetoothCallback;
    }

    public LocalBluetoothProfileManager.ServiceListener getProfileServiceListener() {
        return profileServiceListener;
    }

    public void registerResultCallback(OnBluetoothResultCallback callback) {
        if (callback != null) {
            if (resultCallbackList.contains(callback)) {
                return;
            }
            resultCallbackList.add(callback);
        }
    }

    public void scanningStateChanged(boolean paramBoolean) {
        Iterator localIterator = resultCallbackList.iterator();
        while (localIterator.hasNext()) {
            ((OnBluetoothResultCallback) localIterator.next()).onScanningStateChanged(paramBoolean);
        }
    }

    public void unregisterResultCallback(OnBluetoothResultCallback callback) {
        resultCallbackList.remove(callback);
    }

    private class LeScanResultHandler
            extends Handler {
        public LeScanResultHandler(Looper paramLooper, Handler.Callback paramCallback) {
            super(paramCallback);
        }

        public void handleMessage(Message paramMessage) {
            if (paramMessage.what != 100) {
                return;
            }
            removeMessages(100);
            List deviceList = mCachedDeviceManager.getCachedDevicesCopy();
            for(int i = 0;i<deviceList.size();i++){
                Log.d("BluetoothCallbackHelper","name:"+deviceList.get(i).toString());
            }
            Iterator localIterator = resultCallbackList.iterator();
            while (localIterator.hasNext()) {
                ((OnBluetoothResultCallback) localIterator.next()).onScanResultList(deviceList);
            }
        }
    }
}
