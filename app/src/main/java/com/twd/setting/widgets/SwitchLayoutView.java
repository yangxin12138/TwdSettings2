package com.twd.setting.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;

import com.twd.setting.R;
import com.twd.setting.R.styleable;

public class SwitchLayoutView
        extends RelativeLayout {
    private OnCheckedListener checkedListener;
    private TextView descTv;
    private SwitchCompat switchBtn;
    private boolean switchEnable = true;
    private TextView titleTv;

    public SwitchLayoutView(Context paramContext) {
        this(paramContext, null);
    }

    public SwitchLayoutView(Context paramContext, AttributeSet paramAttributeSet) {
        this(paramContext, paramAttributeSet, 0);
    }

    public SwitchLayoutView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        init(paramContext, paramAttributeSet);
    }

    public SwitchLayoutView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2) {
        super(paramContext, paramAttributeSet, paramInt1, paramInt2);
        init(paramContext, paramAttributeSet);
    }

    private void changeSwitch() {
        if (!switchEnable) {
            return;
        }
        switchBtn.setChecked(switchBtn.isChecked() ^ true);
        if (checkedListener != null) {
            checkedListener.onCheckedChanged(switchBtn.isChecked());
        }
    }

    private void init(Context mContext, AttributeSet attrs) {
        if (switchBtn != null) {
            return;
        }
        inflate(mContext, R.layout.layout_switch, this);
        switchBtn = ((SwitchCompat) findViewById(R.id.switchBtn));
        titleTv = ((TextView) findViewById(R.id.switchTitle));
        descTv = ((TextView) findViewById(R.id.switchDesc));
        setListener();
        if (attrs != null) {
            TypedArray array = mContext.obtainStyledAttributes(attrs, R.styleable.SwitchLayoutView);
            String title = array.getString(styleable.SwitchLayoutView_titleText);
            String desc = array.getString(styleable.SwitchLayoutView_descText);
            boolean bool = array.getBoolean(styleable.SwitchLayoutView_descVisible, true);
            array.recycle();
            if (!TextUtils.isEmpty(title)) {
                titleTv.setText(title);
            }
            if (!TextUtils.isEmpty(desc)) {
                descTv.setText(desc);
            }
            if (!bool) {
                descTv.setVisibility(View.GONE);
            }


        }
    }

    private void setListener() {
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View paramAnonymousView) {
                changeSwitch();
            }
        });
        setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int paramAnonymousInt, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if ((paramAnonymousInt == KeyEvent.KEYCODE_DPAD_LEFT) && (switchBtn.isChecked())) {
                        changeSwitch();
                        return true;
                    }
                    if ((paramAnonymousInt == KeyEvent.KEYCODE_DPAD_RIGHT) && (!switchBtn.isChecked())) {
                        changeSwitch();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public OnCheckedListener getCheckedListener() {
        return checkedListener;
    }

    public boolean isChecked() {
        return (switchBtn != null) && (switchBtn.isChecked());
    }

    public boolean isSwitchEnable() {
        return switchEnable;
    }

    public void setChecked(boolean checked) {
        if (switchBtn != null) {
            switchBtn.setChecked(checked);
        }
    }

    public void setCheckedListener(OnCheckedListener onCheckedListener) {
        checkedListener = onCheckedListener;
    }

    public void setDescStr(String str) {
        if (descTv != null) {
            descTv.setText(str);
        }
    }

    public void setSwitchEnable(boolean enable) {
        switchEnable = enable;
        titleTv.setEnabled(enable);
        descTv.setEnabled(enable);
    }

    public void setTitleStr(String str) {
        if (titleTv != null) {
            titleTv.setText(str);
        }
    }

    public static abstract interface OnCheckedListener {
        public abstract void onCheckedChanged(boolean paramBoolean);
    }
}
