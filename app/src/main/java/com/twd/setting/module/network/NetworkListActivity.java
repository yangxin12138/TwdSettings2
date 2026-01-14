package com.twd.setting.module.network;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.twd.setting.R;
import com.twd.setting.base.BaseFragment;
import com.twd.setting.module.network.model.WifiAccessPoint;
import com.twd.setting.module.network.repository.ConnectivityListener;
import com.twd.setting.module.network.wifi.AddWifiNetworkActivity;
import com.twd.setting.module.network.wifi.NoPasswordNetActivity;
import com.twd.setting.module.network.wifi.WifiConnectionActivity;
import com.twd.setting.module.network.wifi.WifiListRvAdapter;
import com.twd.setting.widgets.MarginTopItemDecoration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * @Author:Yangxin
 * @Description:
 * @time: Create in 17:33 2024/3/5
 */
public class NetworkListActivity extends AppCompatActivity
        implements WifiListRvAdapter.IWifiItemClickListener,
        ConnectivityListener.Listener,
        ConnectivityListener.WifiNetworkListener,
        WifiAccessPoint.AccessPointListener{

    //日志TAG
    private static final String TAG = "NetworkListActivity";

    //静态变量(保持原逻辑)
    public static String selectedBSSID;
    public static String selectedSSID;

    //UI
    private RecyclerView rvWifiList;
    private WifiListRvAdapter adapter;

    //数据相关
    private List<WifiAccessPoint> wifiAccessPoints;

    //Wifi管理相关
    private WifiManager wifiManager;
    private ConnectivityListener mConnectivityListener;

    //列表更新控制
    private long mNoWifiUpdateBeforeMillis;
    private final Handler mHandler = new Handler();
    private final Runnable mInitialUpdateWifiListRunnable = this::updateWifiList;

    //广播接收器
    private BroadcastReceiver wifiCombinedReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_network_list);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        wifiAccessPoints = new ArrayList<>();
        initConnectivityListener();
        initWifiRecyclerView();
        registerWifiBroadcastReceiver();
    }

    private void initConnectivityListener(){
        mConnectivityListener = new ConnectivityListener(this,this::onConnectivityChange,getLifecycle());
    }

    private void initWifiRecyclerView(){
        rvWifiList = findViewById(R.id.rv_wifi_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvWifiList.setLayoutManager(layoutManager);

        adapter = new WifiListRvAdapter();
        adapter.setWifiAccessPoints(wifiAccessPoints);
        adapter.setItemClickListener(this);

        rvWifiList.setAdapter(adapter);
        rvWifiList.setItemAnimator(null);
        rvWifiList.addItemDecoration(new MarginTopItemDecoration(
                getResources().getDimensionPixelSize(R.dimen.net_rc_item_margin_t)
        ));
        rvWifiList.requestFocus();
    }

    /*
    * 注册wifi广播接收器*
    */
    private void registerWifiBroadcastReceiver(){
        wifiCombinedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action == null)return;
                switch (action){
                    case WifiManager.WIFI_STATE_CHANGED_ACTION:
                        updateWifiList();
                        break;
                    case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                        handleNetworkStateChanged(intent);
                        break;
                }
            }
        };
        //注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(wifiCombinedReceiver,intentFilter);
    }

    /*
    * 处理wifi网络连接状态变化*/
    private void handleNetworkStateChanged(Intent intent){
        NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        if (networkInfo != null){
            Log.d(TAG, "WiFi连接状态变化: " + networkInfo.getState());
            updateWifiList();
        }
    }

    /*
    * 更新Wifi列表*/
    private void updateWifiList(){
        Log.i(TAG, "updateWifiList: 开始更新WiFi列表");

        //Wifi未开启，清空列表
        if (!mConnectivityListener.isWifiEnabledOrEnabling()){
            Log.i(TAG, "updateWifiList: WiFi未开启，清空列表");
            wifiAccessPoints.clear();
            adapter.clearAll();
            mNoWifiUpdateBeforeMillis = 0L;
            rvWifiList.setVisibility(View.GONE);
            return;
        }

        //wifi已开启,显示列表
        rvWifiList.setVisibility(View.VISIBLE);

        //防抖
        long currentTime = SystemClock.elapsedRealtime();
        if (mNoWifiUpdateBeforeMillis > currentTime) {
            mHandler.removeCallbacks(mInitialUpdateWifiListRunnable);
            mHandler.postDelayed(mInitialUpdateWifiListRunnable, mNoWifiUpdateBeforeMillis - currentTime);
            return;
        }

        // 保存现有列表用于对比
        int oldSize = wifiAccessPoints.size();
        HashSet<WifiAccessPoint> existingAPs = new HashSet<>(oldSize);
        for (int i = 0; i < oldSize; i++) {
            existingAPs.add(wifiAccessPoints.get(i));
        }

        // 获取可用WiFi列表
        List<WifiAccessPoint> newAPList = mConnectivityListener.getAvailableNetworks();
        Log.d(TAG, "updateWifiList: 获取到可用WiFi数量=" + newAPList.size());

        // 清空旧列表，重新填充
        wifiAccessPoints.clear();
        Iterator<WifiAccessPoint> iterator = newAPList.iterator();
        while (iterator.hasNext()) {
            WifiAccessPoint ap = iterator.next();
            ap.setListener(this);

            // 标记处理（保持原逻辑）
            if (ap.getTag() == null) {
                ap.setTag(ap);
            } else {
                existingAPs.remove(ap.getTag());
            }

            wifiAccessPoints.add(ap);
            Log.d(TAG, "添加WiFi: " + ap.getSsid() + " | BSSID: " + ap.getBssid());
        }

        // 移除不存在的AP
        Iterator<WifiAccessPoint> removeIterator = existingAPs.iterator();
        while (removeIterator.hasNext()) {
            wifiAccessPoints.remove(removeIterator.next());
        }

        // 更新适配器
        if (!wifiAccessPoints.isEmpty()) {
            adapter.notifyWifiAccessPoints();
        }
    }

    /**
     * 获取当前已连接WiFi的SSID（去除引号）
     */
    private String getCurrentWifiSsid() {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssid = "";
        if (wifiInfo != null && wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
            ssid = Objects.requireNonNull(wifiInfo.getSSID()).replace("\"", "");
        }
        return ssid;
    }

    /**
     * 获取当前已连接WiFi的BSSID（精准匹配）
     */
    private String getCurrentWifiBssid() {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo != null && wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
            return wifiInfo.getBSSID();
        }
        return null;
    }

    /**
     * 显示“断开并忘记WiFi”对话框（迁移自NetworkFragment）
     */
    private void showDisconnectAndForgetDialog(WifiAccessPoint wifiAccessPoint) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_title_wifi_connected) // 需在strings.xml添加：当前已连接该WiFi
                .setMessage(R.string.dialog_msg_disconnect_forget) // 需添加：是否断开连接并忘记该WiFi密码？
                .setPositiveButton(R.string.dialog_btn_confirm, (dialog, which) -> {
                    // 执行断开并忘记逻辑
                    disconnectAndForgetWifi(wifiAccessPoint);
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.dialog_btn_cancel, (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }

    /**
     * 断开并忘记WiFi密码（核心逻辑迁移）
     */
    private void disconnectAndForgetWifi(WifiAccessPoint wifiAccessPoint) {
        try {
            String targetSsid = wifiAccessPoint.getSsidStr();
            String targetBssid = wifiAccessPoint.getBssid();
            Log.d(TAG, "disconnectAndForgetWifi: 处理WiFi -> SSID: " + targetSsid + ", BSSID: " + targetBssid);

            // 权限检查（定位权限）
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG, "缺少ACCESS_FINE_LOCATION权限，无法操作WiFi配置");
                return;
            }

            // 步骤1：断开当前连接
            wifiManager.disconnect();

            // 步骤2：删除已保存的WiFi配置
            List<WifiConfiguration> savedConfigs = wifiManager.getConfiguredNetworks();
            if (savedConfigs == null || savedConfigs.isEmpty()) {
                Log.w(TAG, "无已保存的WiFi配置");
                return;
            }

            boolean removed = false;
            for (WifiConfiguration config : savedConfigs) {
                String savedSsid = config.SSID != null ? config.SSID.replace("\"", "") : "";
                if (savedSsid.equals(targetSsid)) {
                    boolean removeSuccess = wifiManager.removeNetwork(config.networkId);
                    if (removeSuccess) {
                        removed = true;
                        Log.d(TAG, "成功删除WiFi配置: " + savedSsid + " (networkId: " + config.networkId + ")");
                    }
                }
            }

            // 步骤3：保存配置并更新列表
            if (removed) {
                wifiManager.saveConfiguration();
                // 延迟更新列表（确保系统处理完成）
                mHandler.postDelayed(this::updateWifiList, 500);
                // 软重启WiFi扫描
                restartWifiSoftly();
            }

        } catch (Exception e) {
            Log.e(TAG, "断开并忘记WiFi时发生异常", e);
        }
    }

    /**
     * 软重启WiFi（温和方式，不关闭WiFi）
     */
    private void restartWifiSoftly() {
        Log.d(TAG, "restartWifiSoftly: 执行WiFi软重启");
        boolean wasEnabled = wifiManager.isWifiEnabled();

        // 重新扫描WiFi
        wifiManager.startScan();

        // 延迟更新列表
        mHandler.postDelayed(() -> {
            updateWifiList();
            if (wasEnabled) {
                rvWifiList.setVisibility(View.VISIBLE);
            }
        }, 1500);
    }

    @Override
    public void onAccessPointChanged(WifiAccessPoint wifiAccessPoint) {
        WifiAccessPoint ap = (WifiAccessPoint) wifiAccessPoint.getTag();
        if (ap != null) {
            int position = adapter.getWifiAccessPoints().indexOf(ap);
            if (position >= 0) {
                adapter.notifyItemChanged(position, ap);
            }
        }
    }

    @Override
    public void onLevelChanged(WifiAccessPoint paramWifiAccessPoint) {
        WifiAccessPoint ap = (WifiAccessPoint) paramWifiAccessPoint.getTag();
        if (ap != null) {
            int position = adapter.getWifiAccessPoints().indexOf(ap);
            if (position >= 0) {
                adapter.notifyItemChanged(position, ap);
            }
        }
    }

    @Override
    public void onConnectivityChange() {
        // WiFi状态变化时更新列表
        updateWifiList();
    }

    @Override
    public void onWifiListChanged() {
        // WiFi列表变化回调
        updateWifiList();
    }

    @Override
    public void onFocusRequest(View paramView, int paramInt) {
        // 空实现（保持接口兼容）
    }

    @Override
    public void onItemClick(WifiAccessPoint paramWifiAccessPoint) {
        if (paramWifiAccessPoint == null) {
            // 点击"添加新网络"
            selectedSSID = "Other";
            selectedBSSID = "add a new network";
            startActivity(new Intent(this, AddWifiNetworkActivity.class));
        } else {
            // 点击具体WiFi
            String currentBssid = getCurrentWifiBssid();
            String clickedBssid = paramWifiAccessPoint.getBssid();

            // 判断是否是当前已连接的WiFi
            if (currentBssid != null && currentBssid.equals(clickedBssid)) {
                Log.d(TAG, "点击了已连接的WiFi，显示断开忘记对话框");
                showDisconnectAndForgetDialog(paramWifiAccessPoint);
                return;
            }

            // 未连接的WiFi，执行连接逻辑
            selectedSSID = paramWifiAccessPoint.getSsidStr();
            selectedBSSID = paramWifiAccessPoint.getBssid();
            Log.i(TAG, "选择WiFi: " + selectedSSID + " (BSSID: " + selectedBSSID + ")");

            // 判断是否有密码
            int security = paramWifiAccessPoint.getSecurity();
            if (security == 0) {
                // 无密码WiFi
                startActivity(new Intent(this, NoPasswordNetActivity.class)
                        .putExtra("net_ssid", paramWifiAccessPoint.getSsidStr()));
            } else {
                // 有密码WiFi
                startActivity(WifiConnectionActivity.createIntent(this, paramWifiAccessPoint));
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 延迟500ms后更新列表（保持原逻辑）
        mNoWifiUpdateBeforeMillis = SystemClock.elapsedRealtime() + 500L;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 页面恢复时更新WiFi列表
        updateWifiList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 反注册广播接收器
        try {
            unregisterReceiver(wifiCombinedReceiver);
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "广播接收器未注册，无需反注册", e);
        }
        // 移除未执行的更新任务
        mHandler.removeCallbacks(mInitialUpdateWifiListRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 清空Handler任务
        mHandler.removeCallbacksAndMessages(null);
    }
}
