package com.twd.setting.module.device;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.twd.setting.R;
import com.twd.setting.utils.SystemPropertiesUtils;

public class DeviceActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = DeviceActivity.class.getName();
    private LinearLayout LL_info;
    private LinearLayout LL_storage;
    private LinearLayout LL_factory;
    private LinearLayout LL_update;
    private LinearLayout LL_signal;
    private TextView tv_info;
    private TextView tv_storage;
    private TextView tv_factory;
    private TextView tv_update;
    private TextView tv_signal;
    private TextView tv_signal_source;
    private Context context;
    //String theme_code = SystemPropertiesUtils.getPropertyColor("persist.sys.background_blue","0");
    String theme_code = "0";
    public static final String SYS_BOOT_APP ="persist.sys.boot.app";
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
        setContentView(R.layout.activity_device);
        context = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    private void initView(){
        LL_info = findViewById(R.id.devices_LL_Info);
        LL_storage = findViewById(R.id.devices_LL_storage);
        LL_factory = findViewById(R.id.devices_LL_factory);
        LL_update = findViewById(R.id.devices_LL_update);
        LL_signal = findViewById(R.id.devices_LL_signal);
        tv_info = findViewById(R.id.devices_tv_Info);
        tv_storage = findViewById(R.id.devices_tv_storage);
        tv_factory = findViewById(R.id.devices_tv_factory);
        tv_update = findViewById(R.id.devices_tv_update);
        tv_signal_source = findViewById(R.id.devices_tv_signal_source);

        LL_info.setOnClickListener(this::onClick);
        LL_storage.setOnClickListener(this::onClick);
        LL_factory.setOnClickListener(this::onClick);
        LL_update.setOnClickListener(this::onClick);
        LL_signal.setOnClickListener(this::onClick);

        LL_info.requestFocus();
        String sys_boot_app = SystemPropertiesUtils.getProperty(SYS_BOOT_APP,"0");
        switch (sys_boot_app){
            case "0":
                tv_signal_source.setText(R.string.device_signal_home);
                break;
            case "1":
                tv_signal_source.setText(R.string.device_signal_hdmi);
                break;
            case "-1":
                LL_signal.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        if(view.getId() == R.id.devices_LL_Info){
            intent = new Intent(this,DeviceInfoActivity.class);
            startActivity(intent);
        }else if (view.getId() == R.id.devices_LL_storage){
            intent = new Intent(this,DeviceStorageActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.devices_LL_update) {
            intent = new Intent();
            intent.setComponent(new ComponentName("com.vsoontech.mos.ota","com.linkin.ota.activity.DownloadActivity"));
            startActivity(intent);
        } else if (view.getId() == R.id.devices_LL_signal) {
            showSignalSourceDialog();
        }else {
            showDialog();
        }
    }

    private void showSignalSourceDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_signal, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false); // 点击外部不关闭对话框

        // 获取选项控件
        LinearLayout option_launcher = dialogView.findViewById(R.id.LL_option1);
        LinearLayout option_hdmi = dialogView.findViewById(R.id.LL_option2);
        TextView launcher_tv = dialogView.findViewById(R.id.option1);
        TextView hdmi_tv = dialogView.findViewById(R.id.option2);
        ImageView launcher_iv = dialogView.findViewById(R.id.icon_option1);
        ImageView hdmi_iv = dialogView.findViewById(R.id.icon_option2);

        // 设置选项文本（根据实际需求修改）
        launcher_tv.setText(R.string.device_signal_home);
        hdmi_tv.setText(R.string.device_signal_hdmi);
        String sys_boot_app = SystemPropertiesUtils.getProperty(SYS_BOOT_APP,"0");
        switch (sys_boot_app){
            case "0":
                launcher_iv.setVisibility(View.VISIBLE);
                break;
            case "1":
                hdmi_iv.setVisibility(View.VISIBLE);
                break;
        }
        // 默认选中第一个选项
        option_launcher.setSelected(true);
        final LinearLayout[] selectedOption = {option_launcher};

        // 为选项设置焦点变化监听，处理遥控器导航
        View.OnFocusChangeListener focusChangeListener = (view, hasFocus) -> {
            if (hasFocus) {
                // 取消之前选中项的选中状态
                selectedOption[0].setSelected(false);
                // 设置当前焦点项为选中状态
                view.setSelected(true);
                selectedOption[0] = (LinearLayout) view;
            }
        };

        option_launcher.setOnFocusChangeListener(focusChangeListener);
        option_hdmi.setOnFocusChangeListener(focusChangeListener);

        // 设置选项点击事件
        View.OnClickListener optionClickListener = view -> {
            // 将选中的文本设置到目标TextView
            if (view.getId() == R.id.LL_option1){
                //TODO:设置系统属性
                tv_signal_source.setText(launcher_tv.getText());
                SystemPropertiesUtils.setProperty(SYS_BOOT_APP,"0");
                launcher_iv.setVisibility(View.VISIBLE);
                hdmi_iv.setVisibility(View.INVISIBLE);
            } else if (view.getId() == R.id.LL_option2) {
                tv_signal_source.setText(hdmi_tv.getText());
                SystemPropertiesUtils.setProperty(SYS_BOOT_APP,"1");
                launcher_iv.setVisibility(View.INVISIBLE);
                hdmi_iv.setVisibility(View.VISIBLE);
            }
            dialog.dismiss(); // 关闭对话框
        };

        option_launcher.setOnClickListener(optionClickListener);
        option_hdmi.setOnClickListener(optionClickListener);

        dialog.show();

        // 设置对话框宽度
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.6);
            window.setAttributes(params);
        }
    }
    private void showDialog(){
        Dialog FactoryDialog = new Dialog(this,R.style.DialogStyle);

        //加载自定义布局文件
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.factory_dialog, null);
        FactoryDialog.setContentView(dialogView);
        dialogView.setPadding(100,0,100,50);

        final TextView factoryTitle = dialogView.findViewById(R.id.factory_title);
        final LinearLayout okBT = dialogView.findViewById(R.id.factory_ok_bt);
        final LinearLayout cancelBT = dialogView.findViewById(R.id.factory_cancel_bt);
        factoryTitle.setText(getString(R.string.factory_dialog_title));
        FactoryDialog.show();

        okBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: factory ok");
                try {
                    startFactoryDefault(context);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        cancelBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: factory cancel");
                FactoryDialog.dismiss();
            }
        });
    }

    public static void startFactoryDefault(Context context) throws Exception {
        if (Build.VERSION.SDK_INT < 26) {
            context.sendBroadcast(new Intent("android.intent.action.MASTER_CLEAR"));
        } else {
            Intent intent = new Intent("android.intent.action.FACTORY_RESET");
            intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            intent.setPackage("android");
            context.sendBroadcast(intent);
        }
    }

}
