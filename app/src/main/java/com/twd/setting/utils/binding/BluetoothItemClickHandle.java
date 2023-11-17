package com.twd.setting.utils.binding;

import android.view.View;

import com.twd.setting.module.bluetooth.model.CachedBluetoothDevice;

public abstract interface BluetoothItemClickHandle {
    public abstract void onClick(View paramView, CachedBluetoothDevice paramCachedBluetoothDevice);
}
