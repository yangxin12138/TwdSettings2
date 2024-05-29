package com.twd.setting.utils.binding;

import android.util.Log;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.twd.setting.BR;

public class ItemLRTextIconData extends BaseObservable {
    private final static String TAG = "ItemLRTextIconData";
    private int itemId;
    private int leftIconRes;
    private String leftTxt;
    private int rightIconRes;
    private String rightTxt;
    private int isVisible;
    private int rightIconVisible;

    public ItemLRTextIconData(int m_itemid, String m_leftTxt, String m_rightTxt, int m_leftIconRes, int m_rightIconRes,int visible,int rightVisible) {
        Log.d(TAG, "ItemLRTextIconData: id:" + m_itemid + ", leftTxt:" + m_leftTxt + ",rightTxt" + m_rightTxt + ",leftIcon:" + m_leftIconRes + ",rightIcon:" + m_rightIconRes);
        itemId = m_itemid;
        leftTxt = m_leftTxt;
        rightTxt = m_rightTxt;
        leftIconRes = m_leftIconRes;
        rightIconRes = m_rightIconRes;
        isVisible = visible;
        rightIconVisible = rightVisible;
    }

    public int getRightIconVisible() {
        return rightIconVisible;
    }

    public void setRightIconVisible(int rightIconVisible) {
        this.rightIconVisible = rightIconVisible;
    }

    public int getItemId() {
        return itemId;
    }

    public int getLeftIconRes() {
        return leftIconRes;
    }

    public String getLeftTxt() {
        return leftTxt;
    }

    public int getRightIconRes() {
        return rightIconRes;
    }

    public int isVisible() {
        return isVisible;
    }

    public void setVisible(int visible) {
        isVisible = visible;
    }

    @Bindable
    public String getRightTxt() {
        return rightTxt;
    }

    public void setItemId(int paramInt) {
        itemId = paramInt;
    }

    public void setLeftIconRes(int paramInt) {
        leftIconRes = paramInt;
    }

    public void setLeftTxt(String paramString) {
        Log.d(TAG, "setLeftText:" + paramString);
        leftTxt = paramString;
    }

    public void setRightIconRes(int paramInt) {
        rightIconRes = paramInt;
    }

    public void setRightTxt(String paramString) {
        rightTxt = paramString;
        notifyPropertyChanged(BR.rightTxt);
    }
}
