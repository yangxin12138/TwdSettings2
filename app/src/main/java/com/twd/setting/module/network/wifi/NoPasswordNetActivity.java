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

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Objects;

public class NoPasswordNetActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView network_ssid;
    private TextView bt_connect;
    String ssid;
    private ProgressDialog progressDialog; // 进度条Dialog

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_password_net);

        network_ssid = findViewById(R.id.tv_network_ssid);
        bt_connect = findViewById(R.id.btn_connect);
        ssid = getIntent().getStringExtra("net_ssid");
        network_ssid.setText(String.format("%s%s", getString(R.string.wifi_no_passwd_info), ssid));
        bt_connect.setOnClickListener(this::onClick);
        bt_connect.requestFocus();
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (Objects.equals(ssid, getCurrentWifiSsid(wifiManager))) {
            showForgetDialog();
        }
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
                if (keyCode == KeyEvent.KEYCODE_BACK){
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
            if (wifiConfiguration.SSID != null && wifiConfiguration.SSID.equals("\""+ssid+"\"")) {
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

    private void connectToWifi(String ssid){
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = "\"" + ssid + "\""; // 无需引号包围SSID

        wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //如果wifi已启用,请禁用它以确保连接新网络
        if (wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(false);
        }

        // 添加网络配置
        int networkId = wifiManager.addNetwork(wifiConfiguration);
        wifiManager.enableNetwork(networkId, true);

        //重新启用wifi
        wifiManager.setWifiEnabled(true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismissProgressDialog();
                if (getCurrentWifiSsid(wifiManager).equals(ssid)){
                    Log.d("yangxin", "run: 连接成功4秒 getCurrentWifiSsid(wifiManager) = " + getCurrentWifiSsid(wifiManager)+ ",ssid = " + ssid);
                    showToast(getResources().getString(R.string.wifi_setup_connection_success));
                    finish();
                }else {
                    Log.d("yangxin", "run: 连接失败4秒 getCurrentWifiSsid(wifiManager) = " + getCurrentWifiSsid(wifiManager)+ ",ssid = " + ssid);
                    showToast(getResources().getString(R.string.net_wifiList_connecttoWifi_isConnected_false));
                }
            }
        },8000);
    }

    private String getCurrentWifiSsid(WifiManager wifiManager){
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssid;
        if (wifiInfo != null && wifiInfo.getSupplicantState() == SupplicantState.COMPLETED){
            ssid = wifiInfo.getSSID().replace("\"","");
        }else {
            ssid = "NoConnect";
        }
        return ssid;
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