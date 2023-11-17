package com.twd.setting.module.systemequipment;

import android.os.Bundle;

import com.twd.setting.base.BaseActivity;
import com.twd.setting.module.systemequipment.fragment.SysEquipmentFragment;
import com.twd.setting.utils.UiUtils;

public class SysEquipmentActivity
        extends BaseActivity {
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        UiUtils.replaceFragment(getSupportFragmentManager(), 16908290, SysEquipmentFragment.newInstance());
    }
}
