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
    protected void onCreate(Bundle paramBundle){
        super.onCreate(paramBundle);
        UiUtils.replaceFragment(getSupportFragmentManager(),16908290, ProjectorFragment.newInstance());
    }
}
