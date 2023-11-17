package com.twd.setting.module.network;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.twd.setting.R;
import com.twd.setting.base.BaseActivity;
import com.twd.setting.module.network.wifi.WifiListFragment;
//import com.twd.setting.utils.HLog;
//import com.twd.setting.utils.KkDataUtils;
import com.twd.setting.utils.TimeUtils;
import com.twd.setting.utils.UiUtils;
import com.twd.setting.utils.manager.DataMapUtils;

public class NetworkActivity
        extends BaseActivity
        /*implements NetworkChangeReceiver.NetworkChangeListener*/ {
    private final String TAG = "NetworkActivity";
    //private NetworkChangeReceiver networkChangeReceiver;

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        UiUtils.replaceFragment(getSupportFragmentManager(), 16908290, NetworkFragment.newInstance());

    }

}
