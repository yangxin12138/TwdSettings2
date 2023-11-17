package com.twd.setting.widgets;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.twd.setting.R;
import com.twd.setting.SettingApplication;
import com.twd.setting.utils.KkUtils;
//import com.twd.setting.utils.HLog;


public abstract class DialogView
        extends BaseView
        implements View.OnClickListener {
    private static final int CHECKBOX_VISIBLE = 1;
    protected Context mContext;
    public WindowManager.LayoutParams mLayoutParams;
    private TextView msgTv;
    private Button negativeButton;
    private Button positiveButton;
    private TextView titleTv;

    public DialogView(Context paramContext) {
        this(paramContext, null);
    }

    public DialogView(Context paramContext, AttributeSet paramAttributeSet) {
        this(paramContext, paramAttributeSet, 0);
    }

    public DialogView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        this.mContext = paramContext;
        init(paramContext, paramInt);
    }

    private void init(Context paramContext, int paramInt) {
        updateTheme(paramContext);
        LayoutInflater.from(paramContext).inflate(R.layout.layout_custom_dialog_view, this, true);
        titleTv = ((TextView) findViewById(R.id.custom_dialog_title_tv_id));
        msgTv = ((TextView) findViewById(R.id.custom_dialog_msg_tv_id));
        positiveButton = ((Button) findViewById(R.id.custom_dialog_ok_tv_id));
        negativeButton = ((Button) findViewById(R.id.custom_dialog_cancle_tv_id));
        positiveButton.setOnClickListener(this);
        negativeButton.setOnClickListener(this);
        initLayoutParams();
        negativeButton.requestFocus();
    }

    private void initLayoutParams() {
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        mLayoutParams.flags |= WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        mLayoutParams.format = PixelFormat.TRANSPARENT;
        mLayoutParams.gravity = Gravity.CENTER;
        mLayoutParams.windowAnimations = R.style.DialogWindowAnim;
    }

    private void setAppTheme(SettingApplication paramSettingApplication, Context paramContext) {
        int i = KkUtils.getSysThemeTypeResId();
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("setAppTheme SettingApplication 主题不同吗？=");
        boolean bool;
        if (paramSettingApplication.getThemeId() != i) {
            bool = true;
        } else {
            bool = false;
        }
        localStringBuilder.append(bool);
        Log.d("DialogView", localStringBuilder.toString());
        if (paramSettingApplication.getThemeId() != i) {
            paramSettingApplication.changeTheme(i);
        }
        if (paramContext != null) {
            paramContext.setTheme(i);
        }
    }

    private void updateTheme(Context paramContext) {
 /*       if ((paramContext instanceof SettingApplication)) {
            Log.d("DialogView", "updateTheme 是 SettingApplication");
            setAppTheme((SettingApplication) paramContext, null);
            return;
        }
        Log.d("DialogView", "updateTheme 不是 SettingApplication ctx="+paramContext);
        Object localObject = paramContext.getApplicationContext();
        if ((localObject instanceof SettingApplication)) {
            Log.d("DialogView", "updateTheme 上级ctx是 SettingApplication");
            setAppTheme((SettingApplication) localObject, paramContext);
        }*/
    }

    public void onClick(View paramView) {
        if (paramView.getId() == R.id.custom_dialog_ok_tv_id) {
            onPositive();
            return;
        }
        if (paramView.getId() == R.id.custom_dialog_cancle_tv_id) {
            onNegative();
        }
    }

    protected abstract void onNegative();

    protected abstract void onPositive();

    public void setMsg(String paramString) {
        msgTv.setText(paramString);
    }

    public void setNegativeTextButton(String paramString) {
        negativeButton.setText(paramString);
    }

    public void setPositiveButton(String paramString) {
        positiveButton.setText(paramString);
    }

    public void setTitle(String paramString) {
        titleTv.setText(paramString);
    }
}