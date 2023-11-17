package com.twd.setting.module.bluetooth.bluetoothlib;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
//import com.konka.android.system.KKConfigManager;
//import com.konka.android.system.KKConfigManager.EN_KK_SYSTEM_CONFIG_KEY_BOOLEAN;
import com.twd.setting.commonlibrary.Utils.ToastUtils;
import com.twd.setting.utils.BluetoothUtils;
//import com.twd.setting.utils.HLog;
//import com.twd.setting.utils.KkUtils;
import com.twd.setting.utils.UiUtils;

import java.util.HashMap;

public class BluetoothPairingReceiver
        extends BroadcastReceiver {
    public static final String ACTION_PAIRING_CANCEL = "android.bluetooth.device.action.PAIRING_CANCEL";
    private static final String KONKA_INPUT_DEVICE_CHANGED_ACTION = "konka.inputdevice.action.CONNECT_STATE_CHANGED";
    public static final String RC_VERSION = "konka_rc_version";
    public static final String RC_VERSION_PREFERENCE = "konka_rc_version_preference";
    private static final String STATE_CONNECTED = "CONNECT";
    private static final String STATE_DISCONNECTED = "DISCONNECT";
    private static final String TAG = "BtPairingReceiver";
    private Context mContext;
    private HashMap<String, DealInterface> mDealMap;

    public BluetoothPairingReceiver() {
        mDealMap = new HashMap(16, 0.75F);
        mDealMap.put("android.bluetooth.device.action.PAIRING_REQUEST", new dealPairingRequest());
        mDealMap.put("android.bluetooth.device.action.PAIRING_CANCEL", new dealPairingCancel());
        mDealMap.put("android.bluetooth.device.action.BOND_STATE_CHANGED", new DealBondState());
        mDealMap.put("android.bluetooth.a2dp.profile.action.CONNECTION_STATE_CHANGED", new DealA2dpState());
    }

    private boolean isIflytekBluetooth(int paramInt1, int paramInt2) {
        boolean bool2 = false;
        boolean bool1 = bool2;
        if (paramInt1 == 1046) {
            bool1 = bool2;
            if (paramInt2 >= 768) {
                bool1 = bool2;
                if (paramInt2 <= 847) {
                    bool1 = true;
                }
            }
        }
        return bool1;
    }

    private boolean isWeinaBluetooth(int paramInt1, int paramInt2) {
        boolean bool2 = false;
        boolean bool1 = bool2;
        if (paramInt1 == 1047) {
            bool1 = bool2;
            if (paramInt2 >= 48) {
                bool1 = bool2;
                if (paramInt2 <= 95) {
                    bool1 = true;
                }
            }
        }
        return bool1;
    }

    private boolean isXimiBluetooth(int paramInt1, int paramInt2) {
        boolean bool2 = false;
        boolean bool1 = bool2;
        if (paramInt1 == 1048) {
            bool1 = bool2;
            if (paramInt2 >= 64) {
                bool1 = bool2;
                if (paramInt2 <= 95) {
                    bool1 = true;
                }
            }
        }
        return bool1;
    }

    public void onReceive(Context paramContext, Intent paramIntent) {
        this.mContext = paramContext;
        Object localObject = new StringBuilder();
        ((StringBuilder) localObject).append("settings-bt : onReceive action ");
        ((StringBuilder) localObject).append(paramIntent.getAction());
        Log.d(TAG, ((StringBuilder) localObject).toString());
        localObject = (DealInterface) mDealMap.get(paramIntent.getAction());
        if (localObject != null) {
            ((DealInterface) localObject).deal(paramContext, paramIntent);
        }
    }

    private class DealA2dpState
            implements BluetoothPairingReceiver.DealInterface {
        private DealA2dpState() {
        }

        public void deal(Context paramContext, Intent paramIntent) {
            BluetoothDevice localBluetoothDevice = (BluetoothDevice) paramIntent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
            int i = paramIntent.getIntExtra("android.bluetooth.profile.extra.STATE", 0);
            int j = paramIntent.getIntExtra("android.bluetooth.profile.extra.PREVIOUS_STATE", 0);
            StringBuilder str = new StringBuilder();
            str.append("settings-bt : deal A2dpState device: ");
            str.append(localBluetoothDevice);
            str.append(" a2dpState: ");
            str.append(i);
            str.append(" preState:");
            str.append(j);
            Log.d(TAG, paramIntent.toString());
      /*
      if (2 == i) {
        try
        {
          if (KKConfigManager.getInstance(paramContext).getBooleanConfig(KKConfigManager.EN_KK_SYSTEM_CONFIG_KEY_BOOLEAN.SUPPORT_INCUS_AUDIO))
          {
            Log.d(TAG, "settings-bt : deal A2dpState device:  支持助听");
            if (KkUtils.isIncusAudioEnable(paramContext))
            {
              Log.d(TAG, "settings-bt : deal A2dpState device:  弹助听 提示");
              ToastUtils.showShort("当前处于声音助听模式");
              return;
            }
          }
        }
        catch (NoSuchFieldError e)
        {
          Log.d(TAG, "settings-bt : deal A2dpState device:  不支持 助听 功能");

        }
      }

       */
        }
    }

    private class DealBondState
            implements BluetoothPairingReceiver.DealInterface {
        private DealBondState() {
        }

        public void deal(Context paramContext, Intent paramIntent) {
            BluetoothDevice localBluetoothDevice = (BluetoothDevice) paramIntent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
            int i = paramIntent.getIntExtra("android.bluetooth.device.extra.BOND_STATE", 10);
            int j = paramIntent.getIntExtra("android.bluetooth.device.extra.PREVIOUS_BOND_STATE", 10);
            StringBuilder str = new StringBuilder();
            str.append("settings-bt : deal BondState device: ");
            str.append(localBluetoothDevice);
            str.append(" bondState: ");
            str.append(i);
            str.append(" preState: ");
            str.append(j);
            Log.d(TAG, paramIntent.toString());
            if ((i == 12) || (i == 10)) {
                Log.d(TAG, "settings-bt : deal BondState device bonded or bond none, remove view bondState");
                BluetoothUtils.removeDialogView(paramContext);
            }
        }
    }

    private static abstract interface DealInterface {
        public abstract void deal(Context paramContext, Intent paramIntent);
    }

    public static abstract interface RcVersionCallBack {
        public abstract void onVersionGet(String paramString);
    }

    private class dealPairingCancel
            implements BluetoothPairingReceiver.DealInterface {
        private dealPairingCancel() {
        }

        public void deal(Context paramContext, Intent paramIntent) {
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append("dealPairingCancel 取消配对 settings-bt : onReceive action ");
            localStringBuilder.append(paramIntent.getAction());
            Log.d(TAG, localStringBuilder.toString());
            BluetoothUtils.removeDialogView(paramContext);
        }
    }

    private class dealPairingRequest
            implements BluetoothPairingReceiver.DealInterface {
        private dealPairingRequest() {
        }

        public void deal(Context paramContext, Intent paramIntent) {
            if (!UiUtils.isIdle(paramContext)) {
                BluetoothUtils.showPairingRequestDialogView(paramIntent, paramContext);
            }
        }
    }
}
