package com.twd.setting.module.bluetooth.bluetoothlib;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;

import com.twd.setting.module.bluetooth.model.CachedBluetoothDevice;

public abstract interface BluetoothCallback {
    public abstract void onAclConnectionStateChanged(CachedBluetoothDevice paramCachedBluetoothDevice, int paramInt);

    public abstract void onActiveDeviceChanged(CachedBluetoothDevice paramCachedBluetoothDevice, int paramInt);

    public abstract void onAudioModeChanged();

    public abstract void onBluetoothStateChanged(int paramInt);

    public abstract void onConnectionStateChanged(CachedBluetoothDevice paramCachedBluetoothDevice, int paramInt);

    public abstract void onDeviceAdded(BluetoothDevice paramBluetoothDevice, Intent paramIntent);

    public abstract void onDeviceAdded(BluetoothDevice paramBluetoothDevice, byte[] paramArrayOfByte, int paramInt);

    public abstract void onDeviceAdded(CachedBluetoothDevice paramCachedBluetoothDevice);

    public abstract void onDeviceBondStateChanged(CachedBluetoothDevice paramCachedBluetoothDevice, int paramInt1, int paramInt2);

    public abstract void onDeviceDeleted(CachedBluetoothDevice paramCachedBluetoothDevice);

    public abstract void onProfileConnectionStateChanged(CachedBluetoothDevice paramCachedBluetoothDevice, int paramInt1, int paramInt2, int paramInt3);

    public abstract void onScanningStateChanged(boolean paramBoolean);
}
