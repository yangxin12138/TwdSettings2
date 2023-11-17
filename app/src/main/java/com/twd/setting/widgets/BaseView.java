package com.twd.setting.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import com.twd.setting.utils.manager.ViewManager;

public class BaseView
        extends FrameLayout {
    public Context mContext;

    public BaseView(Context context) {
        this(context, null);
    }

    public BaseView(Context context, AttributeSet paramAttributeSet) {
        this(context, paramAttributeSet, 0);
    }

    public BaseView(Context context, AttributeSet paramAttributeSet, int paramInt) {
        super(context, paramAttributeSet, paramInt);
        mContext = context;
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
            ViewManager.getInstance(mContext).resetTimer();
            if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                onKeyBack();
            }
        }
        return super.dispatchKeyEvent(keyEvent);
    }

    public void onKeyBack() {
        onViewRemove();
        ViewManager.getInstance(mContext).remove(this);
    }

    public void onViewAdd() {
    }

    public void onViewRemove() {
    }
}
