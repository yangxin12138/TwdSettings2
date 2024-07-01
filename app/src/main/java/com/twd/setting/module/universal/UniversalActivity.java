package com.twd.setting.module.universal;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.twd.setting.R;
import com.twd.setting.utils.SystemPropertiesUtils;

import java.util.Locale;

public class UniversalActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "UniversalActivity";
    private LinearLayout LL_input;
    private LinearLayout LL_language;
    private LinearLayout LL_device;
    private LinearLayout LL_screensaver;
    private LinearLayout LL_access;
    private LinearLayout LL_time;
    private TextView tv_input;
    private TextView tv_language;
    private TextView tv_inputCurrent;
    private TextView tv_languageCurrent;
    private TextView tv_deviceNameCurrent;
    private TextView tv_screensaverTime;

    private ImageView arrow_input;
    private ImageView arrow_language;
    /*String theme_code = SystemPropertiesUtils.getPropertyColor("persist.sys.background_blue","0");*/
    String theme_code = "0";
    private int selectItem ;

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
        setContentView(R.layout.activity_universal);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
        if(selectItem == 0){
            LL_input.requestFocus();
        } else if (selectItem ==1) {
            LL_language.requestFocus();
        } else if (selectItem ==2) {
            LL_device.requestFocus();
        } else if (selectItem ==3) {
            LL_screensaver.requestFocus();
        } else if (selectItem == 4) {
            LL_access.requestFocus();
        } else if (selectItem == 5) {
            LL_time.requestFocus();
        }
        //当前输入法
        PackageManager packageManager = getPackageManager();
        String selectedInputMethodId = Settings.Secure.getString(getContentResolver(),Settings.Secure.DEFAULT_INPUT_METHOD);
        Log.i("yangxin", "onResume: 当前输入法是  = " + selectedInputMethodId);
        ComponentName componentName = ComponentName.unflattenFromString(selectedInputMethodId);
        ApplicationInfo applicationInfo;
        if (componentName == null){
            Log.i("yangxin", "onResume: componentName  = null" );
        }else {
            Log.i("yangxin", "onResume: componentName  不为空" );
            try {
                applicationInfo = packageManager.getApplicationInfo(componentName.getPackageName(), 0);
                CharSequence appName = packageManager.getApplicationLabel(applicationInfo);
                Log.i("yangxin", "onResume: appName = " + appName );
            } catch (PackageManager.NameNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            applicationInfo = packageManager.getApplicationInfo(componentName.getPackageName(), 0);
            CharSequence appName = packageManager.getApplicationLabel(applicationInfo);
            // 使用appName变量，这是当前输入法应用的名称
            String currentInputMethodAppName = appName.toString();
            tv_inputCurrent.setText(currentInputMethodAppName);
            // 在这里进行进一步的处理
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        /*if (selectedInputMethodId.contains("sogou")){
            tv_inputCurrent.setText(getString(R.string.inputMethod_value_sougou));
        } else if (selectedInputMethodId.contains("inputmethod.pinyin")) {
            tv_inputCurrent.setText(getString(R.string.inputMethod_value_google));
        } else if (selectedInputMethodId.contains("inputmethod.latin")) {
            tv_inputCurrent.setText(getString(R.string.inputMethod_value_Aosp));
        }*/

        //当前语言
        Locale currentLocal  = getResources().getConfiguration().locale;
        Configuration configuration = new Configuration();
        configuration.setLocale(currentLocal);
        String name = configuration.locale.getDisplayName(currentLocal);
        String currentLanguage = currentLocal.getLanguage() + "_" + currentLocal.getCountry();
        Log.i("Language", "onResume: currentLanguage = " + currentLanguage);
        //currentLanguage = en_US
        if (currentLanguage.contains("zh_CN")){
            tv_languageCurrent.setText("简体中文");
        } else if (currentLanguage.contains("zh_TW")) {
            tv_languageCurrent.setText("繁體中文");
        } else  if(currentLanguage.equals("en_US")){
            tv_languageCurrent.setText("English");
        } else if (currentLanguage.contains("ja_JP")) {
            tv_languageCurrent.setText("日本語");
        } else if (currentLanguage.contains("ko_KR")) {
            tv_languageCurrent.setText("한국어");
        } else if (currentLanguage.contains("th_TH")) {
            tv_languageCurrent.setText("ไทย (ไทย)");
        } else if (currentLanguage.contains("hi_IN")) {
            tv_languageCurrent.setText("हिन्दी (भारत)");
        } else if (currentLanguage.contains("fr_FR")) {
            tv_languageCurrent.setText("français (France)");
        } else if (currentLanguage.contains("de_DE")) {
            tv_languageCurrent.setText("Deutsch (Deutschland)");
        } else if (currentLanguage.contains("it_IT")) {
            tv_languageCurrent.setText("italiano (Italia)");
        } else if (currentLanguage.contains("ru_RU")) {
            tv_languageCurrent.setText("русский (Россия)");
        } else if (currentLanguage.contains("es_ES")) {
            tv_languageCurrent.setText("español (España)");
        } else if (currentLanguage.contains("pt_PT")) {
            tv_languageCurrent.setText("português (Portugal)");
        } else if (currentLanguage.contains("ar_SA")) {
            tv_languageCurrent.setText("العربية (المملكة العربية السعودية)");
        } else if (currentLanguage.contains("fa_IR")) {
            tv_languageCurrent.setText("فارسی (ایران)");
        } else if (currentLanguage.contains("tr_TR")) {
            tv_languageCurrent.setText("Türkçe (Türkiye)");
        }

        //设备名称
        tv_deviceNameCurrent.setText(SystemPropertiesUtils.getDeviceName(this));
        Log.i(TAG, "onResume: getDeviceName = "+SystemPropertiesUtils.getDeviceName(this));
    }

    private void initView(){
        LL_input = findViewById(R.id.universal_LL_input);
        LL_language = findViewById(R.id.universal_LL_language);
        LL_device = findViewById(R.id.universal_LL_deviceName);
        LL_screensaver = findViewById(R.id.universal_LL_Screensaver);
        LL_access = findViewById(R.id.universal_LL_access);
        LL_time = findViewById(R.id.universal_LL_time);
        tv_input = findViewById(R.id.universal_tv_input);
        tv_language = findViewById(R.id.universal_tv_language);
        tv_inputCurrent = findViewById(R.id.universal_tv_inputcurrent);
        tv_languageCurrent = findViewById(R.id.universal_tv_languagecurrent);
        tv_deviceNameCurrent = findViewById(R.id.universal_tv_namecurrent);
        tv_screensaverTime = findViewById(R.id.universal_tv_screensaverTime);
        arrow_input = findViewById(R.id.arrow_input);
        arrow_language = findViewById(R.id.arrow_language);

        LL_input.setOnClickListener(this);
        LL_language.setOnClickListener(this);
        LL_device.setOnClickListener(this);
        LL_screensaver.setOnClickListener(this);
        LL_access.setOnClickListener(this);
        LL_time.setOnClickListener(this);

        LL_input.requestFocus();
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        if (view.getId() == R.id.universal_LL_input){
            selectItem = 0;
            intent = new Intent(this,UniversalInputActivity.class);
            startActivity(intent);
        }else if (view.getId() == R.id.universal_LL_language){
            selectItem = 1;
            intent = new Intent(this,UniversalLanguageActivity.class);
            startActivity(intent);
        }else if (view.getId() == R.id.universal_LL_deviceName){
            selectItem = 2;
            showDialog();
        } else if (view.getId() == R.id.universal_LL_Screensaver) {
            selectItem = 3;
            intent = new Intent();
            intent.setComponent(new ComponentName("com.android.tv.settings","com.android.tv.settings.device.display.daydream.DaydreamActivity"));
            startActivity(intent);
        }else if (view.getId() == R.id.universal_LL_access){
            selectItem = 4;
            intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
            if (intent.resolveActivity(getPackageManager())!=null){
                startActivity(intent);
            } else {
                Log.i(TAG, "onCreate: 不可以解析");
            }
        }else {
            selectItem = 5;
            intent = new Intent();
            intent.setComponent(new ComponentName("com.twd.timedate2","com.twd.timedate2.MainActivity"));
            //com.twd.timedate2/com.twd.timedate2.MainActivity
            startActivity(intent);
        }
    }

    private void showDialog(){
        Dialog NameDialog = new Dialog(this,R.style.DialogStyle);

        //加载自定义布局文件
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_devicename,null);
        NameDialog.setContentView(dialogView);
        dialogView.setPadding(100,0,100,50);

        final EditText deviceNameEdit = dialogView.findViewById(R.id.deviceName_edit);
        final LinearLayout okBT = dialogView.findViewById(R.id.deviceName_ok_bt);
        final LinearLayout cancelBT = dialogView.findViewById(R.id.deviceName_cancel_bt);

        NameDialog.show();
        okBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deviceName = deviceNameEdit.getText().toString();
                if (!deviceName.isEmpty()){
                    //TODO
                    SystemPropertiesUtils.setDeviceName(UniversalActivity.this,deviceName);
                    NameDialog.dismiss();

                    Intent restartIntent = new Intent(getApplicationContext(), UniversalActivity.class);
                    restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(restartIntent);
                }else {
                    Toast.makeText(UniversalActivity.this, "输入的设备名不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NameDialog.dismiss();
            }
        });
    }
}
