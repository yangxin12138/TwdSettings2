package com.twd.setting.module.network;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
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
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
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
import com.twd.setting.module.network.wifi.NoPasswordNetActivity;
import com.twd.setting.module.network.wifi.WifiConfigFragment;
import com.twd.setting.module.network.wifi.WifiConnectionActivity;
import com.twd.setting.module.network.wifi.WifiListFragment;
import com.twd.setting.module.network.wifi.WifiListRvAdapter;
import com.twd.setting.utils.KkUtils;
import com.twd.setting.utils.TwdUtils;
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
        implements  ConnectivityListener.Listener, WifiListRvAdapter.IWifiItemClickListener
        ,WifiAccessPoint.AccessPointListener, ConnectivityListener.WifiNetworkListener{
    private final String TAG = "NetworkFragment";
    private FragmentNetworkBinding binding;
    private ConnectivityListener mConnectivityListener;
    TwdUtils twdUtils;
    public static String selectedBSSID;
    public static String selectedSSID;
    private WifiManager wifiManager;
    private List<WifiAccessPoint> wifiAccessPoints;
    private WifiListRvAdapter adapter;
    private long mNoWifiUpdateBeforeMillis;
    private final Handler mHandler = new Handler();
    private final Runnable mInitialUpdateWifiListRunnable = new Runnable() {
        @Override
        public void run() {
            updateWifiList();
        }
    };
    public static NetworkFragment newInstance() {
        return new NetworkFragment();
    }

    private void updateWifi() {
        if (mConnectivityListener.isWifiEnabledOrEnabling()) {
            binding.switchWifi.setChecked(true);        // 确保WiFi列表可见
            binding.networkWifiList.setVisibility(View.VISIBLE);
            binding.itemWifiAvailable.setVisibility(View.VISIBLE);

            updateWifiList();
            return;
        }
        binding.switchWifi.setChecked(false);
        binding.networkWifiList.setVisibility(binding.switchWifi.isChecked()?View.VISIBLE:View.INVISIBLE);
        Log.i(TAG, "updateWifi: networklist = "+binding.networkWifiList.getVisibility()+",switch = "+binding.switchWifi.isChecked());
    }


    @Override
    public void onConnectivityChange() {
        updateConnectivity();
    }

    private void updateConnectivity(){
        if (mConnectivityListener.isWifiEnabledOrEnabling()){
            Log.i(TAG, "updateConnectivity: ------resume-----");
            updateWifi();
        }
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

    private void updateWifiList(){
        Log.i(TAG, "updateWifiList: ");
        if (!mConnectivityListener.isWifiEnabledOrEnabling()){
            Log.i(TAG, "updateWifiList: 网络没打开");
            wifiAccessPoints.clear();
            adapter.clearAll();
            mNoWifiUpdateBeforeMillis = 0L;
            return;
        }
        wifiAccessPoints.clear();
        adapter.clearAll();
        long l = SystemClock.elapsedRealtime();
        Log.d(TAG, "updateWifiList  2222  l:"+l+", mNoWifiUpdateBeforeMillis:"+mNoWifiUpdateBeforeMillis);
        if (mNoWifiUpdateBeforeMillis > l){
            mHandler.removeCallbacks(mInitialUpdateWifiListRunnable);
            mHandler.postDelayed(mInitialUpdateWifiListRunnable,mNoWifiUpdateBeforeMillis -l);
            return;
        }
        Log.d(TAG, "updateWifiList  3333");
        int j = wifiAccessPoints.size();
        HashSet localHashSet = new HashSet(j);
        int i = 0;
        while ( i < j){
            localHashSet.add((WifiAccessPoint) wifiAccessPoints.get(i));
            i += 1;
        }

        List<WifiAccessPoint> wifiAccessPoint_list = mConnectivityListener.getAvailableNetworks();
        Log.d(TAG, "updateWifiList: wifiAccessPoint_list is " + wifiAccessPoint_list.isEmpty());
        wifiAccessPoints.clear();
        Iterator iterator = wifiAccessPoint_list.iterator();
        while (iterator.hasNext()){
            WifiAccessPoint wifiAccessPoint = (WifiAccessPoint) iterator.next();
            wifiAccessPoint.setListener(this);
            if (wifiAccessPoint.getTag() == null){
                wifiAccessPoint.setTag(wifiAccessPoint);
            }else {
                localHashSet.remove((WifiAccessPoint) wifiAccessPoint.getTag());
            }
            Log.d(TAG,"list:"+wifiAccessPoint.getSsid()+",state:"+((wifiAccessPoint.getNetworkInfo()==null)?"null": wifiAccessPoint.getNetworkInfo().getState()));
            wifiAccessPoints.add(wifiAccessPoint);
        }
        Iterator iterator_hashset = localHashSet.iterator();
        while (iterator_hashset.hasNext()){
            wifiAccessPoints.remove(iterator_hashset.next());
        }

        if ((wifiAccessPoints != null) && (wifiAccessPoints.size() != 0)){
            adapter.notifyWifiAccessPoints();
        }
    }

    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle) {
        binding = (FragmentNetworkBinding) DataBindingUtil.inflate(paramLayoutInflater, R.layout.fragment_network, paramViewGroup, false);
        initWifiList();
        twdUtils = new TwdUtils();
        twdUtils.hideSystemUI(getActivity());
        return binding.getRoot();
    }

    private void initWifiList(){
        wifiAccessPoints = new ArrayList<WifiAccessPoint>();
        binding.networkWifiList.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new WifiListRvAdapter();
        adapter.setWifiAccessPoints(wifiAccessPoints);
        adapter.setItemClickListener(this);
        adapter.notifyWifiAccessPoints();
        binding.networkWifiList.setItemAnimator(null);
        binding.networkWifiList.setAdapter(adapter);
        binding.networkWifiList.addItemDecoration(new MarginTopItemDecoration(getResources().getDimensionPixelSize(R.dimen.net_rc_item_margin_t)));
    }

    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            requireContext().unregisterReceiver(wifiReceiver);
        } catch (IllegalArgumentException e) {
            // 接收器可能未注册，忽略异常
        }
    }

    public void onResume() {
        super.onResume();
        twdUtils.hideSystemUI(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION); // 添加WiFi状态变化监听
        requireContext().registerReceiver(wifiReceiver, intentFilter);
    }
    @Override
    public void onStart() {
        super.onStart();
        mNoWifiUpdateBeforeMillis = (SystemClock.elapsedRealtime() + 500L);
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
                    binding.networkWifiList.setVisibility(View.INVISIBLE);
                }else {
                    wifiManager.setWifiEnabled(true);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.itemWifiAvailable.setVisibility(View.VISIBLE);
                            binding.networkWifiList.setVisibility(View.VISIBLE);
                            updateWifiList();
                        }
                    },4000);
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

    // 新增：获取当前已连接WiFi的BSSID（用于精准匹配，避免SSID重复问题）
    private String getCurrentWifiBssid() {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo != null && wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
            return wifiInfo.getBSSID();
        }
        return null;
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

    @Override
    public void onFocusRequest(View paramView, int paramInt) {

    }

    @Override
    public void onItemClick(WifiAccessPoint paramWifiAccessPoint) {
        if (paramWifiAccessPoint == null){
            selectedSSID = "Other";
            selectedBSSID = "add a new network";
            startActivity(new Intent(getContext(), AddWifiNetworkActivity.class));
        } else {
            // 1. 获取当前已连接WiFi的BSSID和点击的WiFi的BSSID
            String currentConnectedBssid = getCurrentWifiBssid();
            String clickedBssid = paramWifiAccessPoint.getBssid();

            // 2. 判断是否为当前已连接的WiFi（用BSSID匹配，比SSID更精准）
            if (currentConnectedBssid != null && currentConnectedBssid.equals(clickedBssid)) {
                Log.d("yangxin", "onItemClick: --------------判断为已连接的wifi");
                // 3. 显示自定义Dialog：包含“断开并忘记密码”按钮
                showDisconnectAndForgetDialog(paramWifiAccessPoint);
                return; // 直接返回，不执行后续连接逻辑
            }


            selectedSSID = paramWifiAccessPoint.getSsidStr();
            selectedBSSID = paramWifiAccessPoint.getBssid();
            Log.i("yangxin", "onItemClick: NetworkFragment ----onItemClick成功读取参数 selectedSSID = " + selectedSSID);
            int security = paramWifiAccessPoint.getSecurity();
            if (security == 0){
                Log.i("yangxin", "onItemClick: NetworkFragment --------没密码");
                startActivity(new Intent(getContext(), NoPasswordNetActivity.class).putExtra("net_ssid",paramWifiAccessPoint.getSsidStr()));
            }else {
                startActivity(WifiConnectionActivity.createIntent(getContext(),paramWifiAccessPoint));
            }

        }
    }

    // 新增：自定义“断开并忘记密码”Dialog
    private void showDisconnectAndForgetDialog(WifiAccessPoint wifiAccessPoint) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.dialog_title_wifi_connected) // 需要在strings.xml中添加该字符串："当前已连接该WiFi"
                .setMessage(R.string.dialog_msg_disconnect_forget) // 字符串："是否断开连接并忘记该WiFi密码？"
                .setPositiveButton(R.string.dialog_btn_confirm, (dialog, which) -> {
                    // 4. 执行“断开并忘记密码”逻辑
                    Log.d("yangxin", "showDisconnectAndForgetDialog:忘记密码--- 点击确定按钮");
                    disconnectAndForgetWifi(wifiAccessPoint);
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.dialog_btn_cancel, (dialog, which) -> {
                    Log.d("yangxin", "showDisconnectAndForgetDialog:忘记密码--- 点击取消按钮");
                    dialog.dismiss();
                })
                .setCancelable(true) // 点击外部不关闭Dialog
                .show();
    }

    // 新增：断开并忘记WiFi密码的核心逻辑
    private void disconnectAndForgetWifi(WifiAccessPoint wifiAccessPoint) {
        try {

            String targetSsid = wifiAccessPoint.getSsidStr(); // 目标WiFi的SSID
            String targetBssid = wifiAccessPoint.getBssid(); // 目标WiFi的BSSID（唯一标识）
            Log.d("yangxin", "disconnectAndForgetWifi: targetSsid = "+targetSsid+",targetBssid = "+targetBssid);
            // 步骤1：获取设备上所有已保存的WiFi网络配置
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            List<WifiConfiguration> savedConfigs = wifiManager.getConfiguredNetworks();
            if (savedConfigs == null || savedConfigs.isEmpty()) {
                Log.w("yangxin", "disconnectAndForgetWifi: 无已保存的WiFi配置");
                return;
            }
            // 1. 首先断开当前连接
            wifiManager.disconnect();
            // 2. 清除所有匹配的配置（可能有多个相同SSID的配置）
            boolean removedAny = false;
            for (WifiConfiguration config : savedConfigs) {
                String savedSsid = config.SSID != null ? config.SSID.replace("\"", "") : "";
                // 更宽松的匹配逻辑：只要SSID匹配就删除
                if (savedSsid.equals(targetSsid)) {
                    boolean success = wifiManager.removeNetwork(config.networkId);
                    if (success) {
                        removedAny = true;
                        Log.d("yangxin", "成功删除网络配置: " + savedSsid + ", networkId: " + config.networkId);
                    }
                }
            }
            if (removedAny) {
                // 保存配置并重启WiFi以确保状态重置
                wifiManager.saveConfiguration();
                // 立即更新UI显示
                binding.itemWifiAvailableName.setText(getCurrentWifiSsid(wifiManager));
                // 延迟更新WiFi列表
                mHandler.postDelayed(() -> {
                    updateWifiList();
                }, 500);
                // 可选：轻量级重启WiFi（不关闭再打开）
                restartWifiSoftly();
            }
        } catch (Exception e) {
            Log.e("yangxin", "disconnectAndForgetWifi: 异常", e);
        }
    }

    // 新增：软重启WiFi方法（更温和）
    private void restartWifiSoftly(){
        Log.d("yangxin", "restartWifiSoftly: 软重启WiFi");

        // 1. 先记录当前WiFi状态
        boolean wasWifiEnabled = wifiManager.isWifiEnabled();

        // 2. 重新扫描
        wifiManager.startScan();

        // 3. 延迟更新列表
        mHandler.postDelayed(() -> {
            updateWifiList();

            // 确保WiFi列表可见
            if (wifiManager.isWifiEnabled()) {
                binding.networkWifiList.setVisibility(View.VISIBLE);
            }

            Log.d("yangxin", "WiFi软重启完成");
        }, 1500);
    }

    @Override
    public void onAccessPointChanged(WifiAccessPoint wifiAccessPoint) {
        WifiAccessPoint accessPoint = (WifiAccessPoint) wifiAccessPoint.getTag();
        if (accessPoint != null){
            int position = adapter.getWifiAccessPoints().indexOf(accessPoint);
            Log.d(TAG,"onAccessPointChanged  position = "+position+", WifiAccessPoint"+accessPoint);
            adapter.notifyItemChanged(position,accessPoint);
        }
    }

    @Override
    public void onLevelChanged(WifiAccessPoint paramWifiAccessPoint) {
        paramWifiAccessPoint = (WifiAccessPoint) paramWifiAccessPoint.getTag();
        if (paramWifiAccessPoint !=null){
            WifiListRvAdapter localWifiListRvAdapter = adapter;
            localWifiListRvAdapter.notifyItemChanged(localWifiListRvAdapter.getWifiAccessPoints().indexOf(paramWifiAccessPoint),paramWifiAccessPoint);
        }
    }

    @Override
    public void onWifiListChanged() {
        updateWifiList();
    }

    // 添加BroadcastReceiver来监听WiFi状态变化
    private final BroadcastReceiver wifiStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);

                switch (wifiState) {
                    case WifiManager.WIFI_STATE_ENABLED:
                        Log.d("yangxin", "WiFi已启用");
                        // 确保UI显示正确
                        binding.networkWifiList.setVisibility(View.VISIBLE);
                        binding.itemWifiAvailable.setVisibility(View.VISIBLE);

                        // 延迟一点更新列表（给系统一点时间初始化）
                        mHandler.postDelayed(() -> {
                            updateWifiList();
                        }, 1000);
                        break;

                    case WifiManager.WIFI_STATE_DISABLED:
                        Log.d("yangxin", "WiFi已禁用");
                        binding.networkWifiList.setVisibility(View.INVISIBLE);
                        binding.itemWifiAvailable.setVisibility(View.GONE);
                        break;

                    case WifiManager.WIFI_STATE_ENABLING:
                        Log.d("yangxin", "WiFi正在启用");
                        break;

                    case WifiManager.WIFI_STATE_DISABLING:
                        Log.d("yangxin", "WiFi正在禁用");
                        break;
                }
            }
        }
    };
}