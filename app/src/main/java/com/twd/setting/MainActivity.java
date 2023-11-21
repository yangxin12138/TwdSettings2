package com.twd.setting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.twd.setting.base.BaseActivity;
import com.twd.setting.bean.InputItem;
import com.twd.setting.databinding.FragmentMainBinding;
import com.twd.setting.module.MainFragment;
import com.twd.setting.module.bluetooth.BluetoothActivity;
import com.twd.setting.module.bluetooth.fragment.BluetoothFragment;
import com.twd.setting.utils.KkUtils;
import com.twd.setting.utils.UiUtils;

import java.util.List;

/*
public class MainActivity extends AppCompatActivity {

    private MainFragment curFragment;

    private BluetoothFragment bluetoothFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        setContentView(R.layout.fragment_main);
        //setContentView(R.layout.fragment_bluetooth);

        //FragmentDataBinding activityMainBinding= DataBindingUtil.setContentView(this,R.layout.fragment_data);

 //       FragmentMainBinding activityMainBinding= DataBindingUtil.setContentView(this,R.layout.fragment_main);

        //LayoutItemMainBinding activityMainBinding= DataBindingUtil.setContentView(this,R.layout.layout_item_main);
        //User user=new User("chenqi","123456");
        //activityMainBinding.setUserInfo(user);
        curFragment = new MainFragment();
//        bluetoothFragment = new BluetoothFragment();
        //curFragment = MainFragment.newInstance();

        UiUtils.replaceFragment(getSupportFragmentManager(), R.id.content, curFragment);
//        UiUtils.replaceFragment(getSupportFragmentManager(), R.id.content, bluetoothFragment);

        //UiUtils.replaceFragment(getSupportFragmentManager(), R.id.main_layout, curFragment,"main");
//        UiUtils.add(getSupportFragmentManager(), "layout/fragment_main_0");



    }
}*/
public class MainActivity
        extends BaseActivity {
    private final static String TAG = "MainActivity";
    private MainFragment curFragment;

    private void showMenu(Intent intent) {
        if (intent == null) {
            return;
        }
        String action = intent.getAction();
        String extra = intent.getStringExtra("menu_name");
        Log.d(TAG, "showMenu  action=" + action + "   extra=" + extra);
        if (("net_lan".equals(extra)) || ("android.settings.WIFI_SETTINGS".equals(action))
                || ("android.settings.NETWORK_SETTINGS".equals(action)) || ("android.settings.WIFI_IP_SETTINGS".equals(action))) {
            //    startActivity(new Intent(this, NetworkActivity.class));
        } else if (("bt_bluetooth".equals(extra)) || ("android.settings.BLUETOOTH_SETTINGS".equals(action))) {
            startActivity(new Intent(this, BluetoothActivity.class));
        } else if (("system_info".equals(extra)) || ("android.settings.DEVICE_INFO_SETTINGS".equals(action))
                || ("storage_ram".equals(extra)) || ("storage_sd".equals(extra))
                || ("android.settings.INTERNAL_STORAGE_SETTINGS".equals(action))
                || ("system_upgrade".equals(extra)) || ("system_reset".equals(extra))) {
            //    startActivity(new Intent(this, SysEquipmentActivity.class));
        } else if (("system_input".equals(extra)) || ("android.settings.INPUT_METHOD_SETTINGS".equals(action))) {
            //    startActivity(new Intent(this, CommonActivity.class));
        }
    }

    @Override
    protected void onCreate(Bundle paramBundle) {
        //     UiUtils.updateTheme((SettingApplication)getApplication());
        super.onCreate(paramBundle);
        SharedPreferences inputPreferences = getSharedPreferences("inputMethod", Context.MODE_PRIVATE);
        String input = inputPreferences.getString("input",null);
        if (input!=null) {
            //用户切换过输入法
            Log.i(TAG, "onCreate: 第一次走直接设置默认输入法 = " + input);
            Settings.Secure.putString(getContentResolver(),Settings.Secure.DEFAULT_INPUT_METHOD,input);
        }else {
            //第一次打开，没有切换过输入法
            //当前输入法
            PackageManager packageManager = getPackageManager();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            String selectedInputMethodId = Settings.Secure.getString(getContentResolver(),Settings.Secure.DEFAULT_INPUT_METHOD);
            //获取已启用的输入法列表
            List<InputMethodInfo> inputMethods = imm.getInputMethodList();
            for (InputMethodInfo inputMethod : inputMethods) {
                String packageName = inputMethod.getPackageName();
                String inputMethodID = inputMethod.getId();
                try {
                    CharSequence appName = packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, 0));
                    String inputMethodAppName = appName.toString();
                    Log.i(TAG, "inputMethodAppName = " + inputMethodAppName + ",inputMethodID = " + inputMethodID);
                    if (inputMethodID.equals(selectedInputMethodId)) {
                        SharedPreferences.Editor editor = inputPreferences.edit();
                        editor.putString("input",inputMethodID);
                        editor.commit();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d(TAG, "onCreate  savedInstanceState=" + paramBundle + "   intent=" + getIntent());
        curFragment = MainFragment.newInstance();
        //UiUtils.replaceFragment(getSupportFragmentManager(), R.id.content, curFragment);
        UiUtils.replaceFragment(getSupportFragmentManager(), 16908290, curFragment);
        showMenu(getIntent());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy  intent=" + getIntent());
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent keyEvent) {
        if (curFragment != null) {
            curFragment.onKeyDown(keycode, keyEvent);
        }
        return super.onKeyDown(keycode, keyEvent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent  intent=" + intent);
/*        SettingApplication application = (SettingApplication)getApplication();

        if ((application != null) && (application.getThemeId() != KkUtils.getSysThemeTypeResId()))
        {
            recreate();
            return;
        }
        showMenu(intent);

 */
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart  intent=" + getIntent());
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop  intent=" + getIntent());
    }
}