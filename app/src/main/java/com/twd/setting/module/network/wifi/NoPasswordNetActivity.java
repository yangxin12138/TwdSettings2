package com.twd.setting.module.network.wifi;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.twd.setting.R;
import com.twd.setting.utils.TwdUtils;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class NoPasswordNetActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView network_ssid;
    private TextView bt_connect;
    String ssid;
    private ProgressDialog progressDialog; // 进度条Dialog
    TwdUtils twdUtils;
    private Handler mHandler = new Handler();
    private WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_password_net);
        twdUtils = new TwdUtils();
        twdUtils.hideSystemUI(this);
        network_ssid = findViewById(R.id.tv_network_ssid);
        bt_connect = findViewById(R.id.btn_connect);
        ssid = getIntent().getStringExtra("net_ssid");
        network_ssid.setText(String.format("%s%s", getString(R.string.wifi_no_passwd_info), ssid));
        bt_connect.setOnClickListener(this::onClick);
        bt_connect.requestFocus();
        // 初始化全局WifiManager
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (Objects.equals(ssid, getCurrentWifiSsid())) {
            showForgetDialog();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        twdUtils.hideSystemUI(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    private void showForgetDialog() {
        LayoutInflater inflater = LayoutInflater.from(this); // 使用当前上下文
        View dialogView = inflater.inflate(R.layout.layout_nowifi_forgetdialog, null);
        Dialog dialog = new Dialog(this, R.style.Theme_AppCompat_Light);
        dialog.setContentView(dialogView);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        //添加监听
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    Log.i("yangxin", "点击返回键被我抓到了");
                    finish();
                    return true;
                }
                return false;
            }
        });
        TextView tvMessage = dialogView.findViewById(R.id.tv_dialog_message);
        TextView btnAction = dialogView.findViewById(R.id.btn_dialog_action);

        tvMessage.setText(getString(R.string.title_wifi_known_network_connect, new Object[]{ssid}));
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //处理点击事件
                forgetNetwork();
                finish();
            }
        });

        dialog.show();
    }

    private void forgetNetwork() {
        WifiManager wifiManager = ((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Iterator iterator = wifiManager.getConfiguredNetworks().iterator();
        while (iterator.hasNext()) {
            WifiConfiguration wifiConfiguration = (WifiConfiguration) iterator.next();
            if (wifiConfiguration.SSID != null && wifiConfiguration.SSID.equals("\"" + ssid + "\"")) {
                try {
                    Method forget = wifiManager.getClass().getDeclaredMethod("forget", int.class, Class.forName("android.net.wifi.WifiManager$ActionListener"));
                    if (forget != null) {
                        forget.setAccessible(true);
                        forget.invoke(wifiManager, wifiConfiguration.networkId, null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        showProgressDialog(); // 显示进度条Dialog
        connectToWifi(ssid);
    }

    private void connectToWifi(String ssid) {
        // 1. 先移除已存在的相同SSID配置（避免冲突）
        removeExistingNetwork(ssid);

        // 2. 配置开放网络参数（完整清除加密相关配置）
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = "\"" + ssid + "\""; // 开放网络SSID必须加引号
        wifiConfiguration.status = WifiConfiguration.Status.ENABLED; // 启用配置
        wifiConfiguration.priority = 100; // 提高优先级（优先连接）

        wifiConfiguration.allowedKeyManagement.clear();
        wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE); // 无密钥管理
        wifiConfiguration.allowedAuthAlgorithms.clear(); // 清除认证算法（开放网络无需）
        wifiConfiguration.allowedPairwiseCiphers.clear(); // 清除成对加密
        wifiConfiguration.allowedGroupCiphers.clear(); // 清除组加密
        wifiConfiguration.allowedProtocols.clear(); // 清除协议限制（兼容所有802.11协议）

        // 3. 确保WiFi已启用（无需关闭再开启）
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }

        // 4. 添加并启用网络（系统级应用无需等待WiFi就绪，直接操作）
        int networkId = wifiManager.addNetwork(wifiConfiguration);
        if (networkId != -1) {
            wifiManager.enableNetwork(networkId, true); // 禁用其他网络，优先连接当前
            wifiManager.saveConfiguration(); // 保存配置到系统
        } else {
            dismissProgressDialog();
            showToast(getResources().getString(R.string.net_wifiList_connecttoWifi_isConnected_false));
            return;
        }
        // 5. 延长超时判断（开放网络连接可能需要更久，设为10秒）
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismissProgressDialog();
                if (getCurrentWifiSsid().equals(ssid)) {
                    Log.d("yangxin", "run: AC连接成功10秒 getCurrentWifiSsid(wifiManager) = " + getCurrentWifiSsid() + ",ssid = " + ssid);
                    showToast(getResources().getString(R.string.wifi_setup_connection_success));
                    finish();
                } else {
                    Log.d("yangxin", "run: AC连接失败10秒 getCurrentWifiSsid(wifiManager) = " + getCurrentWifiSsid() + ",ssid = " + ssid);
                    showToast(getResources().getString(R.string.net_wifiList_connecttoWifi_isConnected_false));
                }
            }
        }, 10000);
    }

    // 辅助方法：移除已存在的相同SSID网络配置
    private void removeExistingNetwork(String ssid) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
        if (configuredNetworks == null) return;

        for (WifiConfiguration config : configuredNetworks) {
            if (config.SSID != null && config.SSID.equals("\"" + ssid + "\"")) {
                wifiManager.removeNetwork(config.networkId);
                wifiManager.saveConfiguration(); // 保存移除操作
                Log.d("yangxin", "已移除旧配置：" + config.SSID);
                break;
            }
        }
    }
    private String getCurrentWifiSsid(){
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo == null) return "NoConnect";

        // 仅当连接完成时才返回真实SSID，否则视为未连接
        if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
            String ssid = wifiInfo.getSSID();
            return ssid.replace("\"", ""); // 去除引号
        } else {
            return "NoConnect";
        }
    }

    private void showToast(String text){
        Toast toast = new Toast(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout = inflater.inflate(R.layout.my_toast,(ViewGroup) findViewById(R.id.custom_toast_layout));

        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setView(layout);
        TextView Text = layout.findViewById(R.id.custom_toast_message);
        Text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        Text.setText(text);
        toast.show();
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.net_wifiList_connecttoWifi_title_tv));
        progressDialog.setCanceledOnTouchOutside(false); // 不允许通过点击外部取消
        progressDialog.show();
    }
    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}