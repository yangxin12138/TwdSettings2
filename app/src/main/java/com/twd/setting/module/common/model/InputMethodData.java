package com.twd.setting.module.common.model;

import com.twd.setting.R;

public class InputMethodData {
    private String idStr;
    private String nameStr;
    private boolean selected;

    public int getDrawableRightResId() {
        if (this.selected) {
            return R.drawable.ic_radiobutton_selected;
        }
        return R.drawable.ic_radiobutton_unselected;
    }

    public String getIdStr() {
        return this.idStr;
    }

    public String getNameStr() {
        return this.nameStr;
    }

    public void setIdStr(String paramString) {
        this.idStr = paramString;
    }

    public void setNameStr(String paramString) {
        this.nameStr = paramString;
    }

    public void setSelected(boolean paramBoolean) {
        this.selected = paramBoolean;
    }
}
