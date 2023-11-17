package com.twd.setting.widgets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.twd.setting.R;

public class ToastTools {
    private static volatile ToastTools instance;

    public static ToastTools Instance() {
        if (instance == null) {
            try {
                if (instance == null) {
                    instance = new ToastTools();
                }
            } finally {
            }
        }
        return instance;
    }

    public void showToast(Context paramContext, String paramString) {
        View localView = LayoutInflater.from(paramContext).inflate(R.layout.toast_layout, null);
        ((TextView) localView.findViewById(R.id.tv_toast)).setText(paramString);
        Toast toast = new Toast(paramContext);
        toast.setGravity(80, 0, 50);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(localView);
        toast.show();
    }
}
