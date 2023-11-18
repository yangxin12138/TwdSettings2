package com.twd.setting.module.projector;

import android.os.Bundle;

import com.twd.setting.base.BaseActivity;
import com.twd.setting.module.projector.fragment.ProjectorFragment;
import com.twd.setting.utils.UiUtils;

public class ProjectorActivity extends BaseActivity {
    public static int mode = 0;
    protected void onCreate(Bundle paramBundle){
        super.onCreate(paramBundle);
        UiUtils.replaceFragment(getSupportFragmentManager(),16908290, ProjectorFragment.newInstance());
    }
}
