package com.twd.setting.module.common.model;

import android.content.ComponentName;

public class CommonSettingVisible {
    private ComponentName settingCname;
    private boolean settingVisible = true;

    public ComponentName getSettingCname() {
        return this.settingCname;
    }

    public boolean isSettingVisible() {
        return this.settingVisible;
    }

    public void setSettingCname(ComponentName paramComponentName) {
        this.settingCname = paramComponentName;
    }

    public void setSettingVisible(boolean paramBoolean) {
        this.settingVisible = paramBoolean;
    }
}
