package com.twd.setting.module.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.twd.setting.R;
import com.twd.setting.base.BaseFragment;
import com.twd.setting.databinding.FragmentNetworkBinding;
import com.twd.setting.module.network.model.WifiAccessPoint;
import com.twd.setting.module.network.model.WifiAccessPoint.AccessPointListener;
import com.twd.setting.module.network.repository.ConnectivityListener;
import com.twd.setting.module.network.repository.ConnectivityListener.Listener;
import com.twd.setting.module.network.repository.ConnectivityListener.WifiNetworkListener;
import com.twd.setting.module.network.repository.ConnectivityListener;
import com.twd.setting.module.network.speed.DownloadUrlPresenter;
import com.twd.setting.module.network.speed.NetworkSpeedFragment;
import com.twd.setting.module.network.wifi.AddWifiNetworkActivity;
import com.twd.setting.module.network.wifi.WifiConfigFragment;
import com.twd.setting.module.network.wifi.WifiConnectionActivity;
import com.twd.setting.module.network.wifi.WifiListFragment;
import com.twd.setting.module.network.wifi.WifiListRvAdapter;
import com.twd.setting.utils.KkUtils;
import com.twd.setting.utils.UiUtils;
import com.twd.setting.widgets.DialogTools;
import com.twd.setting.widgets.MarginTopItemDecoration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
public class NetworkFragment
        extends BaseFragment
        implements  ConnectivityListener.Listener{
    private final String TAG = "NetworkFragment";
    private FragmentNetworkBinding binding;
    private ConnectivityListener mConnectivityListener;

    private WifiManager wifiManager;
    public static NetworkFragment newInstance() {
        return new NetworkFragment();
    }

    private void updateWifi() {
        if (mConnectivityListener.isWifiEnabledOrEnabling()) {
            binding.switchWifi.setChecked(true);
            return;
        }
        binding.switchWifi.setChecked(false);
    }




    @Override
    public void onConnectivityChange() {
    }

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);

        mConnectivityListener = new ConnectivityListener(requireContext(), new ConnectivityListener.Listener() {
            @Override
            public void onConnectivityChange() {
                updateWifi();
            }
        }, getLifecycle());
        wifiManager = ((WifiManager)requireContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE));
    }

    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle) {
        binding = (FragmentNetworkBinding) DataBindingUtil.inflate(paramLayoutInflater, R.layout.fragment_network, paramViewGroup, false);
        return binding.getRoot();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        requireContext().unregisterReceiver(wifiReceiver);
    }
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        requireContext().registerReceiver(wifiReceiver, intentFilter);
    }
    @Override
    public void onStart() {
        super.onStart();
    }
    @Override
    public void onViewCreated(View paramView, Bundle paramBundle) {
        super.onViewCreated(paramView, paramBundle);
        initTitle(paramView, R.string.str_network);
        binding.itemWifiSwitch.requestFocus();
        binding.itemWifiAvailable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), NetworkListActivity.class));
            }
        });
        binding.itemWifiSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "itemWifiSwitch onClick");
                if(binding.switchWifi.isChecked()){
                    wifiManager.setWifiEnabled(false);
                    binding.itemWifiAvailable.setVisibility(View.GONE);
                }else {
                    wifiManager.setWifiEnabled(true);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.itemWifiAvailable.setVisibility(View.VISIBLE);
                        }
                    },1000);
                }
            }
        });

        if (wifiManager.isWifiEnabled()){
            binding.itemWifiAvailable.setVisibility(View.VISIBLE);
        }else {
            binding.itemWifiAvailable.setVisibility(View.GONE);
        }

    }
    private String getCurrentWifiSsid(WifiManager wifiManager){
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssid;
        if (wifiInfo != null && wifiInfo.getSupplicantState() == SupplicantState.COMPLETED){
            ssid = wifiInfo.getSSID().replace("\"","");
        }else {
            ssid = "";
        }
        return ssid;
    }

    private final BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (networkInfo != null && networkInfo.isConnected()) {
                    // WiFi已连接
                    // 处理连接WiFi的逻辑
                    binding.itemWifiAvailableName.setText(getCurrentWifiSsid(wifiManager));
                } else {
                    // WiFi断开连接
                    // 处理断开WiFi的逻辑
                    binding.itemWifiAvailableName.setText(getCurrentWifiSsid(wifiManager));
                }
            }
        }
    };
}