package com.twd.setting.module.bluetooth.bluetoothlib;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.twd.setting.module.bluetooth.model.CachedBluetoothDevice;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CachedBluetoothDeviceManager {
    private final static String TAG = "CachedBtDeviceManager";
    private final LocalBluetoothManager mBtManager;
    final List<CachedBluetoothDevice> mCachedDevices = new ArrayList();

    CachedBluetoothDeviceManager(LocalBluetoothManager paramLocalBluetoothManager) {
        mBtManager = paramLocalBluetoothManager;
    }

    public CachedBluetoothDevice addDevice(BluetoothDevice paramBluetoothDevice) {
        return addDevice(paramBluetoothDevice, false, null, 0, true);
    }

    public CachedBluetoothDevice addDevice(BluetoothDevice paramBluetoothDevice, LocalBluetoothProfile paramLocalBluetoothProfile) {
        try {
            CachedBluetoothDevice device = findDevice(paramBluetoothDevice);
            if (device == null) {
                device = new CachedBluetoothDevice(paramLocalBluetoothProfile, paramBluetoothDevice);
                mCachedDevices.add(device);
                mBtManager.getEventManager().dispatchDeviceAdded(device);
            }
            return device;
        } catch (Exception e) {
            Log.d(TAG, "addDevice fail" + e);
        } finally {

        }
        return null;
    }

    public CachedBluetoothDevice addDevice(BluetoothDevice paramBluetoothDevice, boolean paramBoolean1, byte[] paramArrayOfByte, int paramInt, boolean paramBoolean2) {
        LocalBluetoothProfileManager localLocalBluetoothProfileManager = mBtManager.getProfileManager();
        try {
            CachedBluetoothDevice localCachedBluetoothDevice = findDevice(paramBluetoothDevice);

            if (localCachedBluetoothDevice == null) {
                CachedBluetoothDevice device = new CachedBluetoothDevice(localLocalBluetoothProfileManager, paramBluetoothDevice);
                if (device.getLocalBtProfile() == null) {
                    device.updateProfileByScanRecord(paramArrayOfByte, paramInt);
                }
                device.setJustDiscovered(paramBoolean1);
                mCachedDevices.add(device);

                if (paramBoolean2) {
                    mBtManager.getEventManager().dispatchDeviceAdded(device);
                }
                return device;
            }
            return localCachedBluetoothDevice;
        } finally {
        }
    }

    public void clear() {
        if (mCachedDevices.size() > 0) {
            mCachedDevices.clear();
        }
    }

    public void clearNonBondedDevices() {
        try {
            Iterator iterator = mCachedDevices.iterator();
            while (iterator.hasNext()) {
                if (((CachedBluetoothDevice) iterator.next()).getBondState() == 10) {
                    iterator.remove();
                }
            }
            return;
        } catch (Exception e) {
            Log.d(TAG, "clearNonBondedDevices faild");
        } finally {
        }

    }

    public CachedBluetoothDevice findDevice(BluetoothDevice paramBluetoothDevice) {
        try {
            Iterator iterator = mCachedDevices.iterator();
            while (iterator.hasNext()) {
                CachedBluetoothDevice localCachedBluetoothDevice = (CachedBluetoothDevice) iterator.next();
                boolean bool = localCachedBluetoothDevice.getDevice().equals(paramBluetoothDevice);
                if (bool) {
                    return localCachedBluetoothDevice;
                }
            }
            return null;
        } catch (Exception e) {
            Log.d(TAG, "findDevice faild");
        } finally {
        }
        return null;
    }

    public List<CachedBluetoothDevice> getCachedDevicesCopy() {
        try {
            ArrayList localArrayList = new ArrayList(mCachedDevices);
            return localArrayList;
        } catch (Exception e) {
            Log.d(TAG, "getCachedDevicesCopy faild");
        } finally {

        }
        return null;
    }

    public String getName(BluetoothDevice paramBluetoothDevice) {
        CachedBluetoothDevice localCachedBluetoothDevice = findDevice(paramBluetoothDevice);
        if ((localCachedBluetoothDevice != null) && (localCachedBluetoothDevice.getName() != null)) {
            return localCachedBluetoothDevice.getName();
        }
        return paramBluetoothDevice.getAddress();
    }

    /* Error */
    public void onBluetoothStateChanged(int paramInt) {
        // Byte code:
        //   0: aload_0
        //   1: monitorenter
        //   2: iload_1
        //   3: bipush 13
        //   5: if_icmpne +32 -> 37
        //   8: aload_0
        //   9: getfield 19	com/konka/settingcenter/module/bluetooth/bluetoothlib/CachedBluetoothDeviceManager:mCachedDevices	Ljava/util/List;
        //   12: invokeinterface 78 1 0
        //   17: ifle +20 -> 37
        //   20: aload_0
        //   21: getfield 19	com/konka/settingcenter/module/bluetooth/bluetoothlib/CachedBluetoothDeviceManager:mCachedDevices	Ljava/util/List;
        //   24: invokeinterface 80 1 0
        //   29: goto +8 -> 37
        //   32: astore_2
        //   33: aload_0
        //   34: monitorexit
        //   35: aload_2
        //   36: athrow
        //   37: aload_0
        //   38: monitorexit
        //   39: return
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	40	0	this	CachedBluetoothDeviceManager
        //   0	40	1	paramInt	int
        //   32	4	2	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   8	29	32	finally
    }

    public void onScanningStateChanged(boolean paramBoolean) {
    }

    public void onUuidChanged(BluetoothDevice paramBluetoothDevice) {
        try {
            CachedBluetoothDevice device = findDevice(paramBluetoothDevice);
            if (device != null) {
                device.onUuidChanged();
            }
            return;
        } finally {
        }
    }
}

