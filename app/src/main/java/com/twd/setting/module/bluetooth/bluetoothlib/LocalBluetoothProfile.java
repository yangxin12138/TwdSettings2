package com.twd.setting.module.bluetooth.bluetoothlib;

import android.bluetooth.BluetoothDevice;

public abstract interface LocalBluetoothProfile {
    public abstract boolean connect(BluetoothDevice paramBluetoothDevice);

    public abstract boolean disconnect(BluetoothDevice paramBluetoothDevice);

    public abstract int getConnectionStatus(BluetoothDevice paramBluetoothDevice);

    public abstract int getProfileId();

    public abstract String getProfileTypeName();

    public abstract boolean isAutoConnectable();

    public abstract boolean isProfileReady();
}
