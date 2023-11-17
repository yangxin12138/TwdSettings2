package com.twd.setting.module.bluetooth.bluetoothlib;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.twd.setting.utils.BluetoothUtils;

public class AlwaysDiscoverable
        extends BroadcastReceiver {
    private static final String TAG = "AlwaysDiscoverable";
    private BluetoothAdapter mBluetoothAdapter;
    private Context mContext;
    private IntentFilter mIntentFilter;
    boolean mStarted;

    public AlwaysDiscoverable(Context paramContext) {
        mContext = paramContext;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mIntentFilter = new IntentFilter();
        ;
        mIntentFilter.addAction("android.bluetooth.adapter.action.SCAN_MODE_CHANGED");
    }

    @SuppressLint("MissingPermission")
    public void onReceive(Context paramContext, Intent paramIntent) {
        if (paramIntent.getAction() != "android.bluetooth.adapter.action.SCAN_MODE_CHANGED") {
            return;
        }
        if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            BluetoothUtils.setScanMode(mBluetoothAdapter, BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE);
        }
    }

    @SuppressLint("MissingPermission")
    public void start() {
        if (mStarted) {
            return;
        }
        mContext.registerReceiver(this, mIntentFilter);
        mStarted = true;
        if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            BluetoothUtils.setScanMode(mBluetoothAdapter, BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE);
        }
    }

    public void stop() {
        if (!mStarted) {
            return;
        }
        mContext.unregisterReceiver(this);
        mStarted = false;
        BluetoothUtils.setScanMode(mBluetoothAdapter, BluetoothAdapter.SCAN_MODE_CONNECTABLE);
    }
}
