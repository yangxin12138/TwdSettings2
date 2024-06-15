package com.twd.setting.module.projector;

import android.os.Bundle;
import android.view.KeyEvent;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.twd.setting.R;
import com.twd.setting.base.BaseActivity;
import com.twd.setting.module.projector.fragment.ProjectorFragment;
import com.twd.setting.utils.SystemPropertiesUtils;
import com.twd.setting.utils.UiUtils;

public class ProjectorActivity extends BaseActivity {
    public static int mode = 0;
    String theme_code = SystemPropertiesUtils.getPropertyColor("persist.sys.background_blue","0");
    protected void onCreate(Bundle paramBundle){
//        switch (theme_code){
//            case "0": //冰激蓝
//                this.setTheme(R.style.Theme_TVSettings2_IceCreamBlue);
//                break;
//            case "1": //木棉白
//                this.setTheme(R.style.Theme_TVSettings2_White);
//                break;
//            case "2": //星空蓝
//                this.setTheme(R.style.Theme_TVSettings2_StarrySkyBlue);
//                break;
//        }
        super.onCreate(paramBundle);
        UiUtils.replaceFragment(getSupportFragmentManager(),16908290, ProjectorFragment.newInstance());
    }

    FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment Fragment = fragmentManager.findFragmentById(R.id.id_content);
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (Fragment instanceof  ProjectorFragment){
                // 调用系统的退出函数
                finish();
                return true;
            }
            return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }
}
