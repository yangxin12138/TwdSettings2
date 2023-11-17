package com.twd.setting.module.bluetooth;

import android.os.Bundle;

import com.twd.setting.R;
import com.twd.setting.base.BaseActivity;
import com.twd.setting.module.bluetooth.bluetoothlib.LocalBluetoothManager;
import com.twd.setting.module.bluetooth.fragment.BluetoothFragment;
import com.twd.setting.utils.BluetoothUtils;
import com.twd.setting.utils.UiUtils;

public class BluetoothActivity
        extends BaseActivity {
    private LocalBluetoothManager mLocalManager;

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        if (BluetoothUtils.isSupportBluetooth()) {
            mLocalManager = LocalBluetoothManager.getInstance(this, null);
        }
        UiUtils.replaceFragment(getSupportFragmentManager(), 16908290, BluetoothFragment.newInstance());
    }

    protected void onDestroy() {
        super.onDestroy();
        if (mLocalManager != null) {
            mLocalManager.onDestroy();
        }
    }
}
