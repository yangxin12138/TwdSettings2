package com.twd.setting.module.common;

import android.os.Bundle;

import com.twd.setting.base.BaseActivity;
import com.twd.setting.module.common.fragment.CommonFragment;
import com.twd.setting.utils.UiUtils;

public class CommonActivity
        extends BaseActivity {
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        UiUtils.replaceFragment(getSupportFragmentManager(), 16908290, new CommonFragment());
    }
}
