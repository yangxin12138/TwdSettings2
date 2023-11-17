package com.twd.setting.module.bluetooth.bluetoothlib;

import com.twd.setting.module.bluetooth.model.CachedBluetoothDevice;

import java.util.List;

public abstract interface OnBluetoothResultCallback {
    public abstract void onBluetoothStateChanged(int paramInt);

    public abstract void onConnectionStateChanged(CachedBluetoothDevice paramCachedBluetoothDevice, int paramInt);

    public abstract void onDeviceBondStateChanged(CachedBluetoothDevice paramCachedBluetoothDevice, int paramInt1, int paramInt2);

    public abstract void onProfileConnectionStateChanged(CachedBluetoothDevice paramCachedBluetoothDevice, int paramInt1, int paramInt2);

    public abstract void onScanHandsetItem(CachedBluetoothDevice paramCachedBluetoothDevice);

    public abstract void onScanResultList(List<CachedBluetoothDevice> paramList);

    public abstract void onScanningStateChanged(boolean paramBoolean);
}
