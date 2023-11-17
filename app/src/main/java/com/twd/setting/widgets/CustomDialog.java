package com.twd.setting.widgets;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
//import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.twd.setting.R;
import com.twd.setting.commonlibrary.Utils.CommonFunction;

public class CustomDialog
        extends Dialog {
    private static final int KEYCODE_ESCAPE = 111;
    public AlertParams P;
    public Button buttonLeft;
    public Button buttonRight;
    public ImageView iconIv;
    private boolean isPositiveBtnClose = true;
    private Context mContext;
    private ViewGroup mCustomDialogLayout;
    public TextView messageTv;
    public TextView titleTv;

    public CustomDialog(Context paramContext) {
        this(paramContext, R.style.CustomDialog);
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams localLayoutParams = window.getAttributes();
            localLayoutParams.width = -1;
            localLayoutParams.height = -2;
            localLayoutParams.gravity = Gravity.CENTER;
            window.setAttributes(localLayoutParams);
        }
        mContext = paramContext;
    }

    public CustomDialog(Context context, int paramInt) {
        super(context, paramInt);
        mContext = context;
        P = new AlertParams(getContext());
        setContentView(LayoutInflater.from(context).inflate(R.layout.custom_dialog, null));
        setCanceledOnTouchOutside(false);
        findViews();
    }

    private void apply(final AlertParams paramAlertParams) {
        if ((iconIv != null) && (paramAlertParams.mIconDrawableRes >= 0)) {
            iconIv.setImageResource(paramAlertParams.mIconDrawableRes);
        }
        if (titleTv != null) {
            if (TextUtils.isEmpty(paramAlertParams.mTitle)) {
                titleTv.setVisibility(View.GONE);
            } else {
                titleTv.setText(paramAlertParams.mTitle);
                if (titleTv.getVisibility() != View.VISIBLE) {
                    titleTv.setVisibility(View.VISIBLE);
                }
            }
        }
        if (messageTv != null) {
            if (TextUtils.isEmpty(paramAlertParams.mMessage)) {
                messageTv.setVisibility(View.GONE);
            } else {
                messageTv.setText(paramAlertParams.mMessage);
                if (messageTv.getVisibility() != View.VISIBLE) {
                    messageTv.setVisibility(View.VISIBLE);
                }
            }
        }
        if (TextUtils.isEmpty(paramAlertParams.mPositiveButtonText)) {
            buttonLeft.setVisibility(View.GONE);
        } else {
            buttonLeft.setText(paramAlertParams.mPositiveButtonText);
            buttonLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View paramAnonymousView) {
                    if (isPositiveBtnClose) {
                        dismiss();
                    }
                    if (paramAlertParams.mPositiveButtonListener != null) {
                        paramAlertParams.mPositiveButtonListener.onClick(CustomDialog.this, 0);
                    }
                }
            });
        }
        if (TextUtils.isEmpty(paramAlertParams.mNegativeButtonText)) {
            buttonRight.setVisibility(View.GONE);
            return;
        }
        buttonRight.setText(paramAlertParams.mNegativeButtonText);
        buttonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View paramAnonymousView) {
                dismiss();
                if (paramAlertParams.mNegativeButtonListener != null) {
                    paramAlertParams.mNegativeButtonListener.onClick(CustomDialog.this, 0);
                }
            }
        });
    }

    private void findViews() {
        mCustomDialogLayout = ((ViewGroup) findViewById(R.id.custom_dialog_layout));
        iconIv = ((ImageView) findViewById(R.id.custom_dialog_icon_iv_id));
        titleTv = ((TextView) findViewById(R.id.custom_dialog_title_tv_id));
        messageTv = ((TextView) findViewById(R.id.custom_dialog_msg_tv_id));
        buttonLeft = ((Button) findViewById(R.id.custom_dialog_ok_tv_id));
        buttonRight = ((Button) findViewById(R.id.custom_dialog_cancle_tv_id));
    }

    public void dismiss() {
        if (CommonFunction.checkActIsActivity(mContext)) {
            super.dismiss();
        }
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        KeyEvent localKeyEvent = keyEvent;
        if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ESCAPE) {
            localKeyEvent = new KeyEvent(keyEvent.getDownTime(), keyEvent.getEventTime(), keyEvent.getAction(), KeyEvent.KEYCODE_BACK, keyEvent.getRepeatCount());
        }
        return super.dispatchKeyEvent(localKeyEvent);
    }

    public CharSequence getMessage() {
        return P.mMessage;
    }

    public CharSequence getTitle() {
        return P.mTitle;
    }

    public boolean isPositiveBtnClose() {
        return isPositiveBtnClose;
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
    }

    public boolean onSearchRequested() {
        return true;
    }

    protected void onStart() {
        super.onStart();
        apply(this.P);
    }

    public void setBackGroundResource(int paramInt) {
        mCustomDialogLayout.setBackgroundResource(paramInt);
    }

    public void setIconDrawableRes(int paramInt) {
        P.mIconDrawableRes = paramInt;
    }

    public void setMessage(int paramInt) {
        P.mMessage = P.mContext.getText(paramInt);
    }

    public void setMessage(CharSequence paramCharSequence) {
        P.mMessage = paramCharSequence;
    }

    public void setMessage(String paramString) {
        P.mMessage = paramString;
    }

    public void setNegativeButton(int paramInt, DialogInterface.OnClickListener paramOnClickListener) {
        P.mNegativeButtonText = P.mContext.getText(paramInt);
        P.mNegativeButtonListener = paramOnClickListener;
    }

    public void setNegativeButton(CharSequence paramCharSequence, DialogInterface.OnClickListener paramOnClickListener) {
        P.mNegativeButtonText = paramCharSequence;
        P.mNegativeButtonListener = paramOnClickListener;
    }

    public void setOnCancelListener(DialogInterface.OnCancelListener paramOnCancelListener) {
        P.mOnCancelListener = paramOnCancelListener;
        super.setOnCancelListener(paramOnCancelListener);
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener paramOnDismissListener) {
        P.mOnDismissListener = paramOnDismissListener;
        super.setOnDismissListener(paramOnDismissListener);
    }

    public void setOnKeyListener(DialogInterface.OnKeyListener paramOnKeyListener) {
        P.mOnKeyListener = paramOnKeyListener;
        super.setOnKeyListener(paramOnKeyListener);
    }

    public CustomDialog setPositiveBtnClose(boolean paramBoolean) {
        isPositiveBtnClose = paramBoolean;
        return this;
    }

    public void setPositiveButton(int paramInt, DialogInterface.OnClickListener paramOnClickListener) {
        P.mPositiveButtonText = P.mContext.getText(paramInt);
        P.mPositiveButtonListener = paramOnClickListener;
    }

    public void setPositiveButton(CharSequence paramCharSequence, DialogInterface.OnClickListener paramOnClickListener) {
        P.mPositiveButtonText = paramCharSequence;
        P.mPositiveButtonListener = paramOnClickListener;
    }

    public void setTitle(int paramInt) {
        P.mTitle = P.mContext.getText(paramInt);
    }

    public void setTitle(String paramString) {
        P.mTitle = paramString;
    }

    public void show() {
        if (CommonFunction.checkActIsActivity(mContext)) {
        }
        try {
            super.show();
            return;
        } catch (Exception localException) {
        }
    }

    static class AlertParams {
        public boolean isShowing = false;
        public final Context mContext;
        public int mIconDrawableRes = -1;
        public CharSequence mMessage;
        public DialogInterface.OnClickListener mNegativeButtonListener;
        public CharSequence mNegativeButtonText;
        public DialogInterface.OnCancelListener mOnCancelListener;
        public DialogInterface.OnDismissListener mOnDismissListener;
        public DialogInterface.OnKeyListener mOnKeyListener;
        public DialogInterface.OnClickListener mPositiveButtonListener;
        public CharSequence mPositiveButtonText;
        public CharSequence mTitle;

        public AlertParams(Context paramContext) {
            mContext = paramContext;
        }
    }
}
