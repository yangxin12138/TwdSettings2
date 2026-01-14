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
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.twd.setting.R;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    private static final String TAG = "NetworkListActivity";
    public static String selectedBSSID;
    public static String selectedSSID;

    //UI
    private RecyclerView rvWifiList;
    private WifiListRvAdapter adapter;
    private TextView tvEmptyTip; // 新增：空列表提示文本

    //数据相关
    private List<WifiAccessPoint> wifiAccessPoints;

    //Wifi管理相关
    private WifiManager wifiManager;
    private ConnectivityListener mConnectivityListener;

    //列表更新控制
    private long mNoWifiUpdateBeforeMillis;
    private final Handler mMainHandler = new Handler(Looper.getMainLooper());
    private final Runnable mInitialUpdateWifiListRunnable = this::safeUpdateWifiList;

    // 异步线程池（避免UI线程阻塞）
    private ExecutorService mWifiUpdateExecutor;

    // 广播接收器
    private BroadcastReceiver wifiCombinedReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_network_list);

        // 初始化异步线程池（核心：列表更新移到子线程）
        mWifiUpdateExecutor = Executors.newSingleThreadExecutor();

        // 初始化WiFi管理器
        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        // 初始化数据集合（初始化时创建空列表，避免空指针）
        wifiAccessPoints = new ArrayList<>();

        // 初始化ConnectivityListener
        initConnectivityListener();

        // 初始化UI（包含空列表提示）
        initWifiRecyclerView();

        // 注册广播接收器
        registerWifiBroadcastReceiver();
    }

    /**
     * 初始化UI：添加空列表提示，适配布局
     */
    private void initWifiRecyclerView() {
        // 绑定列表和空提示（需要在布局中添加tv_empty_tip）
        rvWifiList = findViewById(R.id.rv_wifi_list);
        tvEmptyTip = findViewById(R.id.tv_empty_tip); // 新增空提示TextView

        // 设置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvWifiList.setLayoutManager(layoutManager);

        // 初始化适配器（确保适配器永远有非空列表）
        adapter = new WifiListRvAdapter();
        adapter.setWifiAccessPoints(wifiAccessPoints);
        adapter.setItemClickListener(this);

        rvWifiList.setAdapter(adapter);
        rvWifiList.setItemAnimator(null);
        rvWifiList.addItemDecoration(new MarginTopItemDecoration(
                getResources().getDimensionPixelSize(R.dimen.net_rc_item_margin_t)
        ));
        rvWifiList.requestFocus();

        // 初始隐藏空提示
        updateEmptyTipVisibility();
    }

    /**
     * 更新空列表提示的显示/隐藏
     */
    private void updateEmptyTipVisibility() {
        boolean isEmpty = wifiAccessPoints == null || wifiAccessPoints.isEmpty();
        tvEmptyTip.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        rvWifiList.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        if (isEmpty) {
            tvEmptyTip.setText(R.string.wifi_list_empty_tip); // 字符串："未搜索到可用WiFi"
        }
    }

    /**
     * 安全更新WiFi列表（核心修复：子线程处理+异常捕获+空值保护）
     */
    private void safeUpdateWifiList() {
        // 1. 切换到子线程处理，避免UI线程阻塞
        mWifiUpdateExecutor.execute(() -> {
            try {
                Log.i(TAG, "safeUpdateWifiList: 开始更新WiFi列表（子线程）");

                // 2. 基础空值/状态检查
                if (wifiManager == null || !mConnectivityListener.isWifiEnabledOrEnabling()) {
                    Log.w(TAG, "safeUpdateWifiList: WiFi未开启或管理器为空，清空列表");
                    mMainHandler.post(() -> {
                        wifiAccessPoints.clear();
                        adapter.clearAll();
                        updateEmptyTipVisibility();
                    });
                    return;
                }

                // 3. 防抖控制
                long currentTime = SystemClock.elapsedRealtime();
                if (mNoWifiUpdateBeforeMillis > currentTime) {
                    mMainHandler.removeCallbacks(mInitialUpdateWifiListRunnable);
                    mMainHandler.postDelayed(mInitialUpdateWifiListRunnable, mNoWifiUpdateBeforeMillis - currentTime);
                    return;
                }

                // 4. 获取可用WiFi列表（核心：添加空值保护）
                List<WifiAccessPoint> newAPList = mConnectivityListener.getAvailableNetworks();
                if (newAPList == null) {
                    newAPList = new ArrayList<>(); // 空值替换为空列表
                    Log.e(TAG, "safeUpdateWifiList: 系统返回WiFi列表为null，替换为空列表");
                }
                Log.d(TAG, "safeUpdateWifiList: 系统返回WiFi数量=" + newAPList.size());

                // 5. 处理WiFi列表数据（子线程中完成，避免UI阻塞）
                HashSet<WifiAccessPoint> existingAPs = new HashSet<>();
                if (wifiAccessPoints != null) {
                    existingAPs.addAll(wifiAccessPoints);
                }

                ArrayList<WifiAccessPoint> tempList = new ArrayList<>();
                Iterator<WifiAccessPoint> iterator = newAPList.iterator();
                while (iterator.hasNext()) {
                    WifiAccessPoint ap = iterator.next();
                    if (ap == null) continue; // 跳过null项

                    ap.setListener(this);
                    if (ap.getTag() == null) {
                        ap.setTag(ap);
                    } else {
                        existingAPs.remove(ap.getTag());
                    }
                    tempList.add(ap);
                }

                // 6. 切回主线程更新UI（核心：仅UI操作在主线程）
                mMainHandler.post(() -> {
                    try {
                        // 清空旧列表，添加新数据
                        wifiAccessPoints.clear();
                        wifiAccessPoints.addAll(tempList);

                        // 更新适配器（添加异常捕获）
                        adapter.setWifiAccessPoints(wifiAccessPoints);
                        adapter.notifyWifiAccessPoints();

                        // 更新空提示
                        updateEmptyTipVisibility();

                        Log.d(TAG, "safeUpdateWifiList: UI更新完成，当前列表数量=" + wifiAccessPoints.size());
                    } catch (Exception e) {
                        Log.e(TAG, "safeUpdateWifiList: UI更新异常", e);
                        Toast.makeText(this, R.string.wifi_list_update_fail, Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                // 捕获所有异常，避免闪退
                Log.e(TAG, "safeUpdateWifiList: 列表更新异常", e);
                mMainHandler.post(() -> {
                    wifiAccessPoints.clear();
                    adapter.clearAll();
                    updateEmptyTipVisibility();
                    Toast.makeText(this, R.string.wifi_list_load_error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    /**
     * 处理WiFi连接状态变化（添加空值保护）
     */
    private void handleNetworkStateChanged(Intent intent) {
        try {
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (networkInfo != null) {
                Log.d(TAG, "WiFi连接状态变化: " + networkInfo.getState());
            }
            // 延迟更新，避免频繁刷新
            mMainHandler.postDelayed(this::safeUpdateWifiList, 300);
        } catch (Exception e) {
            Log.e(TAG, "handleNetworkStateChanged: 异常", e);
        }
    }

    /**
     * 断开并忘记WiFi（添加双重异常捕获）
     */
    private void disconnectAndForgetWifi(WifiAccessPoint wifiAccessPoint) {
        try {
            if (wifiAccessPoint == null) {
                Toast.makeText(this, R.string.wifi_info_error, Toast.LENGTH_SHORT).show();
                return;
            }

            String targetSsid = wifiAccessPoint.getSsidStr();
            if (TextUtils.isEmpty(targetSsid)) {
                Toast.makeText(this, R.string.wifi_ssid_empty, Toast.LENGTH_SHORT).show();
                return;
            }

            // 权限检查
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, R.string.permission_location_denied, Toast.LENGTH_SHORT).show();
                return;
            }

            // 子线程执行耗时操作
            mWifiUpdateExecutor.execute(() -> {
                try {
                    // 断开连接
                    wifiManager.disconnect();

                    // 删除配置（添加空值保护）
                    List<WifiConfiguration> savedConfigs = wifiManager.getConfiguredNetworks();
                    if (savedConfigs == null) savedConfigs = new ArrayList<>();

                    boolean removed = false;
                    for (WifiConfiguration config : savedConfigs) {
                        String savedSsid = config.SSID != null ? config.SSID.replace("\"", "") : "";
                        if (savedSsid.equals(targetSsid)) {
                            removed = wifiManager.removeNetwork(config.networkId);
                            if (removed) wifiManager.saveConfiguration();
                        }
                    }

                    // 主线程更新UI和提示
                    boolean finalRemoved = removed;
                    mMainHandler.post(() -> {
                        if (finalRemoved) {
                            Toast.makeText(this, getString(R.string.wifi_forget_success, targetSsid), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, R.string.wifi_forget_fail, Toast.LENGTH_SHORT).show();
                        }
                        // 安全更新列表
                        safeUpdateWifiList();
                    });

                } catch (Exception e) {
                    Log.e(TAG, "disconnectAndForgetWifi: 异常", e);
                    mMainHandler.post(() ->
                            Toast.makeText(this, R.string.wifi_operate_error, Toast.LENGTH_SHORT).show()
                    );
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "disconnectAndForgetWifi: 外层异常", e);
            Toast.makeText(this, R.string.wifi_operate_error, Toast.LENGTH_SHORT).show();
        }
    }

    // ======================== 其他核心方法（已添加空值/异常保护） ========================
    private void initConnectivityListener() {
        try {
            mConnectivityListener = new ConnectivityListener(
                    this,
                    this::onConnectivityChange,
                    getLifecycle()
            );
        } catch (Exception e) {
            Log.e(TAG, "initConnectivityListener: 异常", e);
        }
    }

    private void registerWifiBroadcastReceiver() {
        try {
            wifiCombinedReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    try {
                        String action = intent.getAction();
                        if (action == null) return;

                        switch (action) {
                            case WifiManager.WIFI_STATE_CHANGED_ACTION:
                            case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                                safeUpdateWifiList();
                                break;
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "BroadcastReceiver onReceive: 异常", e);
                    }
                }
            };

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            registerReceiver(wifiCombinedReceiver, intentFilter);
        } catch (Exception e) {
            Log.e(TAG, "registerWifiBroadcastReceiver: 异常", e);
        }
    }

    @Override
    public void onItemClick(WifiAccessPoint paramWifiAccessPoint) {
        try {
            if (paramWifiAccessPoint == null) {
                startActivity(new Intent(this, AddWifiNetworkActivity.class));
                return;
            }

            String currentBssid = getCurrentWifiBssid();
            String clickedBssid = paramWifiAccessPoint.getBssid();
            if (currentBssid != null && currentBssid.equals(clickedBssid)) {
                showDisconnectAndForgetDialog(paramWifiAccessPoint);
                return;
            }

            int security = paramWifiAccessPoint.getSecurity();
            if (security == 0) {
                startActivity(new Intent(this, NoPasswordNetActivity.class)
                        .putExtra("net_ssid", paramWifiAccessPoint.getSsidStr()));
            } else {
                startActivity(WifiConnectionActivity.createIntent(this, paramWifiAccessPoint));
            }
        } catch (Exception e) {
            Log.e(TAG, "onItemClick: 异常", e);
            Toast.makeText(this, R.string.wifi_click_error, Toast.LENGTH_SHORT).show();
        }
    }

    // ======================== 生命周期 & 资源释放 ========================
    @Override
    protected void onStart() {
        super.onStart();
        mNoWifiUpdateBeforeMillis = SystemClock.elapsedRealtime() + 500L;
        // 延迟更新，避免启动时卡顿
        mMainHandler.postDelayed(this::safeUpdateWifiList, 500);
    }

    @Override
    protected void onResume() {
        super.onResume();
        safeUpdateWifiList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(wifiCombinedReceiver);
        } catch (Exception e) {
            Log.w(TAG, "unregisterReceiver: 异常", e);
        }
        mMainHandler.removeCallbacks(mInitialUpdateWifiListRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 关闭线程池，避免内存泄漏
        if (mWifiUpdateExecutor != null && !mWifiUpdateExecutor.isShutdown()) {
            mWifiUpdateExecutor.shutdownNow();
        }
        mMainHandler.removeCallbacksAndMessages(null);
    }

    // ======================== 工具方法（添加空值保护） ========================
    private String getCurrentWifiSsid() {
        try {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo == null || wifiInfo.getSupplicantState() != SupplicantState.COMPLETED) {
                return "";
            }
            return Objects.requireNonNull(wifiInfo.getSSID()).replace("\"", "");
        } catch (Exception e) {
            Log.e(TAG, "getCurrentWifiSsid: 异常", e);
            return "";
        }
    }

    private String getCurrentWifiBssid() {
        try {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo == null || wifiInfo.getSupplicantState() != SupplicantState.COMPLETED) {
                return null;
            }
            return wifiInfo.getBSSID();
        } catch (Exception e) {
            Log.e(TAG, "getCurrentWifiBssid: 异常", e);
            return null;
        }
    }

    private void showDisconnectAndForgetDialog(WifiAccessPoint wifiAccessPoint) {
        try {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_title_wifi_connected)
                    .setMessage(R.string.dialog_msg_disconnect_forget)
                    .setPositiveButton(R.string.dialog_btn_confirm, (dialog, which) -> {
                        disconnectAndForgetWifi(wifiAccessPoint);
                        dialog.dismiss();
                    })
                    .setNegativeButton(R.string.dialog_btn_cancel, (dialog, which) -> dialog.dismiss())
                    .setCancelable(true)
                    .show();
        } catch (Exception e) {
            Log.e(TAG, "showDisconnectAndForgetDialog: 异常", e);
        }
    }

    @Override
    public void onAccessPointChanged(WifiAccessPoint wifiAccessPoint) { /* 空值保护 */ }
    @Override
    public void onLevelChanged(WifiAccessPoint paramWifiAccessPoint) { /* 空值保护 */ }
    @Override
    public void onConnectivityChange() { safeUpdateWifiList(); }
    @Override
    public void onWifiListChanged() { safeUpdateWifiList(); }
    @Override
    public void onFocusRequest(View paramView, int paramInt) {}
}