package com.twd.setting.module.projector;

import android.os.Bundle;

import com.twd.setting.base.BaseActivity;
import com.twd.setting.module.projector.fragment.ProjectionFragment;
import com.twd.setting.utils.UiUtils;

public class ProjectionActivity extends BaseActivity {
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        UiUtils.replaceFragment(getSupportFragmentManager(), 16908290, ProjectionFragment.newInstance());
    }
}
