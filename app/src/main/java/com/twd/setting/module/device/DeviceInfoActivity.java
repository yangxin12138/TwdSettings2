package com.twd.setting.module.device;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.twd.setting.R;
import com.twd.setting.utils.DeviceInfoUtil;
import com.twd.setting.utils.SystemPropertiesUtils;

public class DeviceInfoActivity extends AppCompatActivity {
    private TextView tv_MachineNO_value;
    private TextView tv_androidVersion_value;
    private TextView tv_softwareNO_value;
    private TextView tv_macAddressWifi_value;
    private TextView tv_macAddressBluetooth_value;
    private TextView tv_sn_value;
    private TextView tv_deviceid_value;
    private TextView tv_ip_value;
    private static final String TAG = DeviceInfoActivity.class.getName();
    private String PROP_FX_DEVICE = "ro.build.aoc.fx.device";//设备型号
    private String PROP_FX_DATE = "ro.build.aoc.fx.date";//编译时间
    private String PROP_FX_VERSION = "ro.build.aoc.fx.version";//获取安卓版本号
    private String PROP_SEQ_ID = "persist.sys.hwconfig.seq_id";//序列号
    private String PROP_STB_ID = "persist.sys.hwconfig.stb_id";//deviceid
    private Context context = this;
    //String theme_code = SystemPropertiesUtils.getPropertyColor("persist.sys.background_blue","0");
    String theme_code = "1";

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
        setSN();
        setDeviceID();
        setCurrentIp(this);
    }

    private void initView(){
        tv_MachineNO_value = findViewById(R.id.devices_tv_machineNO_value);//型号
        tv_androidVersion_value = findViewById(R.id.devices_tv_androidVersion_value);//Android版本
        tv_softwareNO_value = findViewById(R.id.devices_tv_softwareNO_value);//系统编译时间
        tv_macAddressWifi_value = findViewById(R.id.devices_tv_macAddressWifi_value);
        tv_macAddressBluetooth_value = findViewById(R.id.devices_tv_macAddressBluetooth_value);
        tv_sn_value = findViewById(R.id.devices_tv_sn_value);
        tv_deviceid_value = findViewById(R.id.devices_tv_deviceid_value);
        tv_ip_value = findViewById(R.id.devices_tv_ip_value);
    }

    /*
    * 获取序列号*/
    private void setSN(){
        String SN_number = SystemPropertiesUtils.getProperty(PROP_SEQ_ID,"0");
        tv_sn_value.setText(SN_number);
        Log.i(TAG, "setSN: --------setSN = " + SN_number);
    }

    /*
    * 获取deviceID*/
    private void setDeviceID(){
        String deviceId_number = SystemPropertiesUtils.getProperty(PROP_STB_ID,"0");
        tv_deviceid_value.setText(deviceId_number);
        Log.i(TAG, "setDeviceID: --------setDeviceID = " + deviceId_number);
    }

    private void setCurrentIp(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null && wifiManager.getConnectionInfo() != null) {
            int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
            tv_ip_value.setText(Formatter.formatIpAddress(ipAddress));
            return;
        }
        tv_ip_value.setText("");
    }
    /*
    * 获取设备号*/
    private void setMachineNo(){
        String machineNo = DeviceInfoUtil.getDeviceName(this);
        tv_MachineNO_value.setText(machineNo);
        Log.i(TAG, "getMachineNO: --------machineNo = " + machineNo);
    }

    /*
    * 获取安卓版本号*/
    private void setAndroidVersion(){
        String version = SystemPropertiesUtils.getProperty(PROP_FX_VERSION,"9.0");
        tv_androidVersion_value.setText(version);
        Log.i(TAG, "setAndroidVersion: ----------version = " + version);
    }

    /*
    * 获取编译时间*/
    private void setSoftwareNo(){
        String softwareNo = SystemPropertiesUtils.getProperty(PROP_FX_DATE,"2024.01.01");
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
