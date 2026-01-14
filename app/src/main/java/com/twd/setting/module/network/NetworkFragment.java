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
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.twd.setting.R;
import com.twd.setting.base.BaseFragment;
import com.twd.setting.databinding.FragmentNetworkBinding;
import com.twd.setting.module.network.repository.ConnectivityListener;
import com.twd.setting.utils.TwdUtils;

import java.util.Objects;

public class NetworkFragment extends BaseFragment {
    private final String TAG = "NetworkFragment";
    private FragmentNetworkBinding binding;
    private ConnectivityListener mConnectivityListener;
    TwdUtils twdUtils;
    private WifiManager wifiManager;
    private final Handler mMainHandler=new Handler(Looper.getMainLooper());
    // 核心：防抖动Runnable，仅执行最后一次Switch状态对应的WiFi操作
    private Runnable mDebounceWifiToggleRunnable;
    private static final long WIFI_TOGGLE_DEBOUNCE_DELAY=2000; // 2秒防抖动

    public static NetworkFragment newInstance() {
        return new NetworkFragment();
    }

    // 仅更新WiFi名称，不修改任何Switch状态和可用项显示
    private void updateWifiInfoOnly() {
        binding.itemWifiAvailableName.setText(getCurrentWifiSsid(wifiManager));
        Log.i(TAG, "updateWifiInfoOnly: 仅更新WiFi名称，Switch状态保持不变");
    }

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        // 回调仅更新WiFi名称，不触碰Switch
        mConnectivityListener=new ConnectivityListener(requireContext(), this::updateWifiInfoOnly, getLifecycle());
        wifiManager=(WifiManager) requireContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle) {
        binding=DataBindingUtil.inflate(paramLayoutInflater, R.layout.fragment_network, paramViewGroup, false);
        twdUtils=new TwdUtils();
        twdUtils.hideSystemUI(getActivity());
        return binding.getRoot();
    }
    private void syncWifiStateWithSwitch() {
        boolean currentSwitchState = binding.switchWifi.isChecked();
        try {
            boolean result = wifiManager.setWifiEnabled(currentSwitchState);
            Log.d(TAG, "同步WiFi状态至Switch状态 | Switch状态=" + currentSwitchState + " | 执行结果=" + result);
            if (!result) {
                Log.e(TAG, "WiFi状态同步失败，目标状态：" + currentSwitchState);
            }
        } catch (Exception e) {
            Log.e(TAG, "WiFi状态同步抛出异常", e);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        try {
            requireContext().unregisterReceiver(wifiCombinedReceiver);
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "广播接收器未注册，无需反注册", e);
        }
        // 清空防抖动任务
        if (mDebounceWifiToggleRunnable != null) {
            mMainHandler.removeCallbacks(mDebounceWifiToggleRunnable);
        }

        syncWifiStateWithSwitch();
    }

    public void onResume() {
        super.onResume();
        twdUtils.hideSystemUI(getActivity());

        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        requireContext().registerReceiver(wifiCombinedReceiver, intentFilter);

        boolean initWifiState = wifiManager.isWifiEnabled();
        binding.switchWifi.setChecked(initWifiState);
        binding.itemWifiAvailable.setVisibility(initWifiState ? View.VISIBLE : View.GONE);
        updateWifiInfoOnly();
    }

    @Override
    public void onViewCreated(View paramView, Bundle paramBundle) {
        super.onViewCreated(paramView, paramBundle);
        initTitle(paramView, R.string.str_network);
        binding.itemWifiSwitch.requestFocus();

        binding.itemWifiAvailable.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), NetworkListActivity.class))
        );

        // 核心点击逻辑：纯Switch控制，防抖动执行WiFi操作
        binding.itemWifiSwitch.setOnClickListener(view -> {
            // 1. 立即反转Switch状态（用户视觉优先）
            boolean targetSwitchState = !binding.switchWifi.isChecked();
            binding.switchWifi.setChecked(targetSwitchState);
            // 同步控制可用项显示（仅UI，不关联系统WiFi）
            binding.itemWifiAvailable.setVisibility(targetSwitchState ? View.VISIBLE : View.GONE);
            Log.d(TAG, "Switch状态已立即更新为: " + targetSwitchState);

            // 2. 移除之前的防抖动任务，重置倒计时
            if (mDebounceWifiToggleRunnable != null) {
                mMainHandler.removeCallbacks(mDebounceWifiToggleRunnable);
                Log.d(TAG, "移除旧的防抖动任务，重置2秒倒计时");
            }

            // 3. 创建新的防抖动任务：使用final固化当前Switch状态
            final boolean finalTargetState=targetSwitchState;
            mDebounceWifiToggleRunnable=() -> {
                try {
                    // 仅此时才执行系统WiFi开关操作，WiFi状态跟随Switch最终状态
                    boolean result=wifiManager.setWifiEnabled(finalTargetState);
                    Log.d(TAG, "2秒无操作，执行WiFi开关操作 | 目标状态=" + finalTargetState + " | 执行结果=" + result);
                    if (!result) {
                        Log.e(TAG, "WiFi开关操作失败，目标状态：" + finalTargetState);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "WiFi开关操作抛出异常", e);
                }
            };

            // 4. 提交2秒防抖动任务
            mMainHandler.postDelayed(mDebounceWifiToggleRunnable, WIFI_TOGGLE_DEBOUNCE_DELAY);
            Log.d(TAG, "已提交防抖动任务，2秒无操作将执行WiFi开关");
        });
    }

    private String getCurrentWifiSsid(WifiManager wifiManager) {
        WifiInfo wifiInfo=wifiManager.getConnectionInfo();
        String ssid;
        if (wifiInfo != null && wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
            ssid=Objects.requireNonNull(wifiInfo.getSSID()).replace("\"", "");
        } else {
            ssid = "";
        }
        return ssid;
    }

    // 广播接收器：仅更新WiFi名称，绝不修改Switch状态
    private final BroadcastReceiver wifiCombinedReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            if (action == null) return;

            switch (action) {
                case WifiManager.WIFI_STATE_CHANGED_ACTION:
                    handleWifiStateChanged(intent);
                    break;
                case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                    handleWifiNetworkChanged(intent);
                    break;
            }
        }

        private void handleWifiStateChanged(Intent intent) {
            int wifiState=intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
            switch (wifiState) {
                case WifiManager.WIFI_STATE_ENABLED:
                    Log.d(TAG, "系统WiFi已启用");
                    break;
                case WifiManager.WIFI_STATE_DISABLED:
                    Log.d(TAG, "系统WiFi已禁用");
                    break;
                case WifiManager.WIFI_STATE_ENABLING:
                    Log.d(TAG, "WiFi正在启用");
                    break;
                case WifiManager.WIFI_STATE_DISABLING:
                    Log.d(TAG, "WiFi正在禁用");
                    break;
            }
            // 仅更新名称，不碰Switch
            updateWifiInfoOnly();
        }

        private void handleWifiNetworkChanged(Intent intent) {
            NetworkInfo networkInfo=intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (networkInfo != null) {
                Log.d(TAG, "WiFi连接状态变化: " + networkInfo.getState());
                // 仅更新名称，不碰Switch
                updateWifiInfoOnly();
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDebounceWifiToggleRunnable != null) {
            mMainHandler.removeCallbacks(mDebounceWifiToggleRunnable);
        }
    }
}