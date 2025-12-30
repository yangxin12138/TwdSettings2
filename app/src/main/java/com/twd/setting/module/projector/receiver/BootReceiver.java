package com.twd.setting.module.projector.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.twd.setting.module.projector.sensor.GSensorService;
import com.twd.setting.utils.SystemPropertiesUtils;

public class BootReceiver extends BroadcastReceiver {
    public static final String PROP_POWERON = "persist.sys.keystone.poweron";
    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            SystemPropertiesUtils.setProperty(PROP_POWERON, "true");
            Intent serviceIntent = new Intent(context, GSensorService.class);
            //context.startService(serviceIntent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent);
            }
        }else if("com.konka.systemsetting.action.SHOW_MENU".equals(intent.getAction())){
            String name = intent.getStringExtra("menu_name");
        }
    }
}
