package com.twd.setting.module.device;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.twd.setting.R;
import com.twd.setting.utils.SystemPropertiesUtils;
import com.twd.setting.utils.TwdUtils;

import java.util.Objects;

public class DeviceInfoActivity extends AppCompatActivity {
    private TextView tv_MachineNO_value;
    private TextView tv_androidVersion_value;
    private TextView tv_softwareNO_value;
    private TextView tv_macAddressWifi_value;
    private TextView tv_macAddressBluetooth_value;
    private static final String TAG = DeviceInfoActivity.class.getName();
    private Context context = this;
    //String theme_code = SystemPropertiesUtils.getPropertyColor("persist.sys.background_blue","0");
    String theme_code = "0";
    TwdUtils twdUtils;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        switch (theme_code){
            case "0": //冰激蓝
                this.setTheme(R.style.Theme_IceBlue);
                break;
            case "1": //木棉白
                this.setTheme(R.style.Theme_KapokWhite);
                break;
            case "2": //星空蓝
                this.setTheme(R.style.Theme_StarBlue);
                break;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);
        twdUtils = new TwdUtils();
        twdUtils.hideSystemUI(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
        setMachineNo();
        setAndroidVersion();
        setSoftwareNo();
        setMACAddressWifi();
        setMACAddressBluetooth();
        twdUtils.hideSystemUI(this);
    }

    private void initView(){
        tv_MachineNO_value = findViewById(R.id.devices_tv_machineNO_value);
        tv_androidVersion_value = findViewById(R.id.devices_tv_androidVersion_value);
        tv_softwareNO_value = findViewById(R.id.devices_tv_softwareNO_value);
        tv_macAddressWifi_value = findViewById(R.id.devices_tv_macAddressWifi_value);
        tv_macAddressBluetooth_value = findViewById(R.id.devices_tv_macAddressBluetooth_value);
    }

    /*
    * 获取设备号*/
    private void setMachineNo(){
        String machineNo = Build.MODEL;
        tv_MachineNO_value.setText(machineNo);
        Log.i(TAG, "getMachineNO: --------machineNo = " + machineNo);
    }

    /*
    * 获取安卓版本号*/
    private void setAndroidVersion(){
        String version;
        if (!Objects.equals(SystemPropertiesUtils.readSystemVersion(), " ")){
            Log.i(TAG, "setAndroidVersion: 读到自定义版本号 = " + SystemPropertiesUtils.readSystemVersion());
            version = SystemPropertiesUtils.readSystemVersion();
        }else {
            Log.i(TAG, "setAndroidVersion: 没读到版本号 = Build.VERSION.RELEASE " + Build.VERSION.RELEASE);
            version = Build.VERSION.RELEASE;
        }
        tv_androidVersion_value.setText(version);
        Log.i(TAG, "setAndroidVersion: ----------version = " + version);
    }

    /*
    * 获取软件号*/
    private void setSoftwareNo(){
        String softwareNo = Build.VERSION.INCREMENTAL;
        tv_softwareNO_value.setText(softwareNo);
        Log.i(TAG, "setSoftwareNO: -----software = " + softwareNo);
    }

    /*
    * 获取wifi的mac地址*/
    private void setMACAddressWifi(){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String macAddress = wifiInfo.getMacAddress();
        if (macAddress != null){
            macAddress = macAddress.toUpperCase();
        }
        tv_macAddressWifi_value.setText(macAddress);
        Log.i(TAG, "setMACAddressWifi: ---------macAddress_wifi = " + macAddress);
    }

    /*
    * 获取蓝牙的mac地址*/
    private void setMACAddressBluetooth(){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null){
            String macAddress = bluetoothAdapter.getAddress();
            if (macAddress != null){
                macAddress = macAddress.toUpperCase();
                Log.i(TAG, "setMACAddressBluetooth: --------bluetooth = " + macAddress);
            }
            tv_macAddressBluetooth_value.setText(macAddress);
        }
    }
}
