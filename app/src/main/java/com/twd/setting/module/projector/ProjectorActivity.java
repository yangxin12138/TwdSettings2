package com.twd.setting.module.projector;

import android.os.Bundle;
import android.view.KeyEvent;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.twd.setting.R;
import com.twd.setting.base.BaseActivity;
import com.twd.setting.module.projector.fragment.ProjectorFragment;
import com.twd.setting.module.projector.fragment.SinglePointFragment;
import com.twd.setting.utils.SystemPropertiesUtils;
import com.twd.setting.utils.UiUtils;

public class ProjectorActivity extends BaseActivity {
    public static final String EXTRA_TARGET_FRAGMENT = "target_fragment";

    public static final int FRAG_PROJECTOR = 0;
    public static final int FRAG_SINGLE_POINT = 1;


    protected void onCreate(Bundle paramBundle){
        super.onCreate(paramBundle);
        int targetFragment = getIntent().getIntExtra(EXTRA_TARGET_FRAGMENT, FRAG_PROJECTOR);
        Fragment target;
        if (targetFragment == FRAG_SINGLE_POINT) {
            // 外部请求：打开单点梯形校正 SinglePointFragment
            target = SinglePointFragment.newInstance();
        } else {
            // 默认页面
            target = ProjectorFragment.newInstance();
        }
        UiUtils.replaceFragment(getSupportFragmentManager(), 16908290, target);
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
