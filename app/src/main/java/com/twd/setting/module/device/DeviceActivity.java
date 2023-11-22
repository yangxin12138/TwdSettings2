package com.twd.setting.module.device;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
    private TextView tv_info;
    private TextView tv_storage;
    private TextView tv_factory;
    private ImageView arrow_info;
    private ImageView arrow_storage;
    private ImageView arrow_factory;
    private Context context;
    String theme_code = SystemPropertiesUtils.getPropertyColor("persist.sys.background_blue","0");

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
        tv_info = findViewById(R.id.devices_tv_Info);
        tv_storage = findViewById(R.id.devices_tv_storage);
        tv_factory = findViewById(R.id.devices_tv_factory);
        arrow_info = findViewById(R.id.arrow_info);
        arrow_storage = findViewById(R.id.arrow_storage);
        arrow_factory = findViewById(R.id.arrow_factory);

        LL_info.setOnClickListener(this::onClick);
        LL_storage.setOnClickListener(this::onClick);
        LL_factory.setOnClickListener(this::onClick);

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
        }else {
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
        factoryTitle.setText("恢复出厂设置会重启");
        FactoryDialog.show();

        okBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: factory ok");
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
}
