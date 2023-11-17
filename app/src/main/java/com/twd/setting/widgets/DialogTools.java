package com.twd.setting.widgets;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;

import com.twd.setting.R;

public class DialogTools {
    private static volatile DialogTools instance;

    public static DialogTools Instance() {
        if (instance == null) {
            try {
                if (instance == null) {
                    instance = new DialogTools();
                }
            } finally {
            }
        }
        return instance;
    }

    public static AlertDialog getLoadingDialog(Context context) {
        AlertDialog dialog = new AlertDialog.Builder(context, R.style.CustomDialog).setCancelable(true).setView(R.layout.layout_loading_dialog).create();
        return dialog;
    }

    public static AlertDialog getLoadingDialog(Context paramContext, String str) {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(paramContext, R.style.CustomDialog);
        localBuilder.setCancelable(true);
        View view = LayoutInflater.from(paramContext).inflate(R.layout.layout_loading_dialog, null);
        TextView localTextView = (TextView) view.findViewById(R.id.msgTv);
        localTextView.setText(str);
        localTextView.setVisibility(View.VISIBLE);
        localBuilder.setView(view);
        return localBuilder.create();
    }

    public CustomDialog getDialogForCustomView(Context paramContext, int paramInt1, String paramString1, String paramString2, int paramInt2, DialogInterface.OnClickListener paramOnClickListener1, int paramInt3, DialogInterface.OnClickListener paramOnClickListener2) {
        CustomDialog localCustomDialog = new CustomDialog(paramContext);
        localCustomDialog.setCanceledOnTouchOutside(false);
        localCustomDialog.setIconDrawableRes(paramInt1);
        localCustomDialog.setTitle(paramString1);
        localCustomDialog.setMessage(paramString2);
        if ((paramInt3 != -1) && (paramInt3 != 0)) {
            localCustomDialog.setNegativeButton(paramInt3, paramOnClickListener2);
        } else {
            localCustomDialog.setNegativeButton(paramContext.getText(R.string.str_cancel), paramOnClickListener2);
        }
        if ((paramInt2 != -1) && (paramInt2 != 0)) {
            localCustomDialog.setPositiveButton(paramInt2, paramOnClickListener1);
            return localCustomDialog;
        }
        localCustomDialog.setPositiveButton(paramContext.getText(R.string.str_confirm), paramOnClickListener1);
        return localCustomDialog;
    }

    public CustomDialog getDialogForCustomView(Context paramContext, String paramString1, String paramString2, int paramInt1, DialogInterface.OnClickListener paramOnClickListener1, int paramInt2, DialogInterface.OnClickListener paramOnClickListener2) {
        CustomDialog localCustomDialog = new CustomDialog(paramContext);
        localCustomDialog.setCancelable(true);
        localCustomDialog.setCanceledOnTouchOutside(true);
        localCustomDialog.setTitle(paramString1);
        localCustomDialog.setMessage(paramString2);
        if ((paramInt2 != -1) && (paramInt2 != 0)) {
            localCustomDialog.setNegativeButton(paramInt2, paramOnClickListener2);
        } else {
            localCustomDialog.setNegativeButton(paramContext.getText(R.string.str_cancel), paramOnClickListener2);
        }
        if ((paramInt1 != -1) && (paramInt1 != 0)) {
            localCustomDialog.setPositiveButton(paramInt1, paramOnClickListener1);
            return localCustomDialog;
        }
        localCustomDialog.setPositiveButton(paramContext.getText(R.string.str_confirm), paramOnClickListener1);
        return localCustomDialog;
    }

    public CustomDialog getDialogForCustomView(Context paramContext, String paramString1, String paramString2, int paramInt1, DialogInterface.OnClickListener paramOnClickListener1, int paramInt2, DialogInterface.OnClickListener paramOnClickListener2, DialogInterface.OnCancelListener paramOnCancelListener, boolean paramBoolean) {
        CustomDialog localCustomDialog = new CustomDialog(paramContext);
        localCustomDialog.setCancelable(paramBoolean);
        localCustomDialog.setCanceledOnTouchOutside(paramBoolean);
        localCustomDialog.setTitle(paramString1);
        localCustomDialog.setMessage(paramString2);
        if (paramBoolean) {
            localCustomDialog.setOnCancelListener(paramOnCancelListener);
        }
        if ((paramInt2 != -1) && (paramInt2 != 0)) {
            localCustomDialog.setNegativeButton(paramInt2, paramOnClickListener2);
        } else {
            localCustomDialog.setNegativeButton(paramContext.getText(R.string.str_cancel), paramOnClickListener2);
        }
        if ((paramInt1 != -1) && (paramInt1 != 0)) {
            localCustomDialog.setPositiveButton(paramInt1, paramOnClickListener1);
            return localCustomDialog;
        }
        localCustomDialog.setPositiveButton(paramContext.getText(R.string.str_confirm), paramOnClickListener1);
        return localCustomDialog;
    }

    public CustomDialog getDialogForCustomViewUnKeyBack(Context paramContext, String paramString1, String paramString2, int paramInt1, DialogInterface.OnClickListener paramOnClickListener1, int paramInt2, DialogInterface.OnClickListener paramOnClickListener2, DialogInterface.OnKeyListener paramOnKeyListener) {
        CustomDialog localCustomDialog = new CustomDialog(paramContext);
        localCustomDialog.setCanceledOnTouchOutside(false);
        localCustomDialog.setTitle(paramString1);
        localCustomDialog.setMessage(paramString2);
        DialogInterface.OnKeyListener onKeyListener = paramOnKeyListener;
        if (onKeyListener == null) {
            onKeyListener = new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent) {
                    return (paramAnonymousKeyEvent.getAction() == 0) && (paramAnonymousInt == 4);
                }
            };
        }
        localCustomDialog.setOnKeyListener(onKeyListener);
        if ((paramInt1 != -1) && (paramInt1 != 0)) {
            localCustomDialog.setNegativeButton(paramInt1, paramOnClickListener1);
        } else {
            localCustomDialog.setNegativeButton(paramContext.getText(R.string.str_cancel), paramOnClickListener1);
        }
        if ((paramInt2 != -1) && (paramInt2 != 0)) {
            localCustomDialog.setPositiveButton(paramInt2, paramOnClickListener2);
            return localCustomDialog;
        }
        localCustomDialog.setPositiveButton(paramContext.getText(R.string.str_confirm), paramOnClickListener2);
        return localCustomDialog;
    }
}
