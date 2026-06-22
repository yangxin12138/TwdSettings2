package com.twd.setting.module.device;

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
    private LinearLayout LL_Screen;
    private TextView tv_info;
    private TextView tv_storage;
    private TextView tv_factory;
    private TextView tv_update;
    private ImageView arrow_info;
    private ImageView arrow_storage;
    private ImageView arrow_factory;
    private ImageView arrow_update;
    private Context context;
    String theme_code = SystemPropertiesUtils.getPropertyColor("persist.sys.background_blue","0");
   //String theme_code = "1";

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
        LL_Screen = findViewById(R.id.devices_LL_screen);
        tv_info = findViewById(R.id.devices_tv_Info);
        tv_storage = findViewById(R.id.devices_tv_storage);
        tv_factory = findViewById(R.id.devices_tv_factory);
        tv_update = findViewById(R.id.devices_tv_update);
        arrow_info = findViewById(R.id.arrow_info);
        arrow_storage = findViewById(R.id.arrow_storage);
        arrow_factory = findViewById(R.id.arrow_factory);
        arrow_update = findViewById(R.id.arrow_update);

        LL_info.setOnClickListener(this::onClick);
        LL_storage.setOnClickListener(this::onClick);
        LL_factory.setOnClickListener(this::onClick);
        LL_update.setOnClickListener(this::onClick);
        LL_Screen.setOnClickListener(this::onClick);

        LL_info.requestFocus();
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
            intent.setComponent(new ComponentName("com.yunos.osupdate","com.yunos.osupdate.front.UpdateActivity"));
            startActivity(intent);
        } else if (view.getId() == R.id.devices_LL_screen) {
            showScreenTimeoutDialog();
        } else {
            showDialog();
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

    private void showScreenTimeoutDialog() {
        Dialog screenDialog = new Dialog(this, R.style.DialogStyle);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.screen_timeout_dialog, null);
        screenDialog.setContentView(dialogView);

        // 获取三个条目与勾选图标
        LinearLayout item5 = dialogView.findViewById(R.id.item_5min);
        LinearLayout item15 = dialogView.findViewById(R.id.item_15min);
        LinearLayout itemOff = dialogView.findViewById(R.id.item_off);

        ImageView iv5 = dialogView.findViewById(R.id.iv_check_5min);
        ImageView iv15 = dialogView.findViewById(R.id.iv_check_15min);
        ImageView ivOff = dialogView.findViewById(R.id.iv_check_off);

        // 先全部隐藏勾选
        iv5.setVisibility(View.INVISIBLE);
        iv15.setVisibility(View.INVISIBLE);
        ivOff.setVisibility(View.INVISIBLE);

        // 读取系统当前屏幕超时值 单位：毫秒
        int currentTimeout;
        try {
            currentTimeout = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
        } catch (Settings.SettingNotFoundException e) {
            currentTimeout = 0;
        }

        // 根据当前值显示对应勾选图标
        if (currentTimeout == 5 * 60 * 1000) {
            iv5.setVisibility(View.VISIBLE);
        } else if (currentTimeout == 15 * 60 * 1000) {
            iv15.setVisibility(View.VISIBLE);
        } else if (currentTimeout == Integer.MAX_VALUE || currentTimeout == 0) {
            // 永不息屏/关闭
            ivOff.setVisibility(View.VISIBLE);
        }

        // 5分钟点击
        item5.setOnClickListener(v -> {
            setScreenTimeout(5 * 60 * 1000);
            screenDialog.dismiss();
        });
        //15分钟点击
        item15.setOnClickListener(v -> {
            setScreenTimeout(15 * 60 * 1000);
            screenDialog.dismiss();
        });
        //关闭息屏（永不休眠）
        itemOff.setOnClickListener(v -> {
            setScreenTimeout(Integer.MAX_VALUE);
            screenDialog.dismiss();
        });

        screenDialog.show();
    }

    private void setScreenTimeout(int ms) {
        try {
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, ms);
        } catch (Exception e) {
            Log.e(TAG, "set screen timeout failed", e);
        }
    }
}
