package com.twd.setting;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.view.WindowManager;

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
            //TODO:弹出确定弹窗
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
            dialogBuilder.setMessage(mContext.getString(R.string.index_usb_listener));
            dialogBuilder.setPositiveButton(mContext.getString(R.string.dialog_changeLanguage_btConfirm), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent_f1 = new Intent();
                    ComponentName cn_f1 = new ComponentName("com.softwinner.TvdFileManager","com.softwinner.TvdFileManager.MainUI");
                    intent_f1.setComponent(cn_f1);
                    intent_f1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent_f1.setAction("android.intent.action.MAIN");
                    mContext.startActivity(intent_f1);
                    alertDialog.dismiss();
                    Log.i("settings-yangxin", "onClick: usb ---- 点击确定");
                }
            });
            dialogBuilder.setNegativeButton(mContext.getString(R.string.dialog_changeLanguage_btCancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.i("settings-yangxin", "onClick: usb ---- 点击取消");
                    alertDialog.dismiss();
                }
            });
            alertDialog = dialogBuilder.create();
            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            alertDialog.show();
        }else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)){
            Log.i("settings-yangxin", "onReceive:usb u盘拔出 ");
            if (alertDialog!=null && alertDialog.isShowing()){
                alertDialog.dismiss();
            }
        }
    }
}
