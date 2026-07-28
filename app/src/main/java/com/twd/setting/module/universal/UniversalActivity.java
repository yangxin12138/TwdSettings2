package com.twd.setting.module.universal;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.twd.setting.R;
import com.twd.setting.utils.SystemPropertiesUtils;

import java.util.Locale;

public class UniversalActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout LL_input;
    private LinearLayout LL_language;
    private LinearLayout LL_device;
    private TextView tv_input;
    private TextView tv_language;
    private TextView tv_inputCurrent;
    private TextView tv_languageCurrent;
    private TextView tv_deviceNameCurrent;

    private ImageView arrow_input;
    private ImageView arrow_language;
    /*String theme_code = SystemPropertiesUtils.getPropertyColor("persist.sys.background_blue","0");*/
    String theme_code = "1";

    int connectMode = 1;
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
        tv_deviceNameCurrent.setText(SystemPropertiesUtils.getDeviceName(this));
        Log.i("yangxin", "onResume: getDeviceName = "+SystemPropertiesUtils.getDeviceName(this));
    }

    private void initView(){
        LL_input = findViewById(R.id.universal_LL_input);
        LL_language = findViewById(R.id.universal_LL_language);
        LL_device = findViewById(R.id.universal_LL_deviceName);
        tv_input = findViewById(R.id.universal_tv_input);
        tv_language = findViewById(R.id.universal_tv_language);
        tv_inputCurrent = findViewById(R.id.universal_tv_inputcurrent);
        tv_languageCurrent = findViewById(R.id.universal_tv_languagecurrent);
        tv_deviceNameCurrent = findViewById(R.id.universal_tv_namecurrent);
        arrow_input = findViewById(R.id.arrow_input);
        arrow_language = findViewById(R.id.arrow_language);

        LL_input.setOnClickListener(this);
        LL_language.setOnClickListener(this);
        LL_device.setOnClickListener(this);

        LL_input.requestFocus();
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        if (view.getId() == R.id.universal_LL_input){
            intent = new Intent(this,UniversalInputActivity.class);
            startActivity(intent);
        }else if (view.getId() == R.id.universal_LL_language){
            intent = new Intent(this,UniversalLanguageActivity.class);
            startActivity(intent);
        }else {
            showDialog();
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

        deviceNameEdit.setImeOptions(EditorInfo.IME_ACTION_SEND);
        deviceNameEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    connectMode = 0;
                    okBT.requestFocus();
                }
                return false;
            }
        });
        NameDialog.show();
        okBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deviceName = deviceNameEdit.getText().toString();
                if (connectMode == 1){
                    if (!deviceName.isEmpty()){

                        SystemPropertiesUtils.setDeviceName(UniversalActivity.this,deviceName);
                        NameDialog.dismiss();

                        Intent restartIntent = new Intent(getApplicationContext(), UniversalActivity.class);
                        restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(restartIntent);
                    }else {
                        Toast.makeText(UniversalActivity.this, "输入的设备名不能为空", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    connectMode = 1;
                }

            }
        });
        // 设置返回键监听
        NameDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                // 拦截返回键事件
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                    // 执行与取消按钮相同的逻辑
                    dialog.dismiss();
                    Intent restartIntent = new Intent(getApplicationContext(), UniversalActivity.class);
                    restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(restartIntent);
                    return true; // 消费事件，不再传递
                }
                return false; // 继续传递其他按键事件
            }
        });
        cancelBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NameDialog.dismiss();
                Intent restartIntent = new Intent(getApplicationContext(), UniversalActivity.class);
                restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(restartIntent);
            }
        });
    }
}
