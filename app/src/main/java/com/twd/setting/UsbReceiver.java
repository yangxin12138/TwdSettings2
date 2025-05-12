package com.twd.setting;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.util.Log;
import android.view.WindowManager;

import java.io.File;

/**
 * @Author:Yangxin
 * @Description:
 * @time: Create in 上午11:15 7/5/2025
 */
public class UsbReceiver extends BroadcastReceiver {

    private Context mContext;
    AlertDialog alertDialog = null;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        mContext = context;
        Log.i("settings-yangxin", "onReceive:action =  " + action);

        if (action!= null && action.equals(Intent.ACTION_MEDIA_MOUNTED)){
            Log.i("settings-yangxin", "onReceive:usb u盘插入 ");
            // 获取挂载路径
            String mountPath = intent.getData() != null ? intent.getData().getPath() : null;
            Log.i("settings-yangxin", "onReceive: 挂载路径 = " + mountPath);


            // 判断是否为真实 U 盘
            assert mountPath != null;
            if (!mountPath.contains("/storage/emulated")) {
                Log.i("settings-yangxin", "onReceive:usb 真实U盘插入 ");
                // 显示对话框
                showUsbDialog();
            } else {
                Log.i("settings-yangxin", "onReceive: 非U盘挂载，可能是系统存储或SD卡");
            }
        }else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)){
            Log.i("settings-yangxin", "onReceive:usb u盘拔出 ");
            if (alertDialog!=null && alertDialog.isShowing()){
                alertDialog.dismiss();
            }
        }
    }

    private void showUsbDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        dialogBuilder.setMessage(mContext.getString(R.string.index_usb_listener));
        dialogBuilder.setPositiveButton(mContext.getString(R.string.dialog_changeLanguage_btConfirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openFileManager();
                        dismissDialog();
                        Log.i("settings-yangxin", "onClick: usb ---- 点击确定");
                    }
                });
        dialogBuilder.setNegativeButton(mContext.getString(R.string.dialog_changeLanguage_btCancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("settings-yangxin", "onClick: usb ---- 点击取消");
                        dismissDialog();
                    }
                });

        alertDialog = dialogBuilder.create();
        try {
            // 设置系统级对话框
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
            } else {
                alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            }
            alertDialog.show();
        } catch (Exception e) {
            Log.e("settings-yangxin", "显示对话框失败: " + e.getMessage());
        }
    }

    // 打开文件管理器
    private void openFileManager() {
        try {
            Intent intent_f1 = new Intent();
            ComponentName cn_f1 = new ComponentName("com.softwinner.TvdFileManager", "com.softwinner.TvdFileManager.MainUI");
            intent_f1.setComponent(cn_f1);
            intent_f1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent_f1.setAction("android.intent.action.MAIN");
            mContext.startActivity(intent_f1);
        } catch (Exception e) {
            Log.e("settings-yangxin", "打开文件管理器失败: " + e.getMessage());
        }
    }

    // 关闭对话框
    private void dismissDialog() {
        if (alertDialog != null && alertDialog.isShowing()) {
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                Log.e("settings-yangxin", "关闭对话框失败: " + e.getMessage());
            }
        }
    }
}

