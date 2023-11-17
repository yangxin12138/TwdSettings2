package com.twd.setting.module.bluetooth.dialog;

import android.content.Context;

import com.twd.setting.R;
import com.twd.setting.module.bluetooth.bluetoothlib.BluetoothPairingController;
import com.twd.setting.utils.manager.ViewManager;
import com.twd.setting.widgets.DialogView;

public class PairingRequestDialogView
        extends DialogView {
    private static final String TAG = "PairingRequestView";
    public static boolean isShow = false;
    private BluetoothPairingController mBluetoothPairingController;

    public PairingRequestDialogView(Context paramContext, BluetoothPairingController paramBluetoothPairingController) {
        super(paramContext);
        this.mBluetoothPairingController = paramBluetoothPairingController;
        setTitle(getTitleStr(paramContext, paramBluetoothPairingController.getDeviceName(), this.mBluetoothPairingController.getPasskeyFormatted()));
        setMsg(paramContext.getString(R.string.str_bluetooth_paring_request_content));
    }

    private String getTitleStr(Context paramContext, String paramString1, String paramString2) {
        if ((paramString2 != null) && (paramString2.trim().length() != 0)) {
            return paramContext.getString(R.string.str_bluetooth_paring_request_title_passkey, new Object[]{paramString1, paramString2});
        }
        return paramContext.getString(R.string.str_bluetooth_paring_request_title, new Object[]{paramString1});
    }

    private void removeDialogView() {
        isShow = false;
        ViewManager.getInstance(this.mContext).remove(this);
    }

    public void onKeyBack() {
        super.onKeyBack();
        isShow = false;
        this.mBluetoothPairingController.onDialogNegativeClick(this);
    }

    protected void onNegative() {
        this.mBluetoothPairingController.onDialogNegativeClick(this);
        removeDialogView();
    }

    protected void onPositive() {
        this.mBluetoothPairingController.onDialogPositiveClick(this);
        removeDialogView();
    }

    public void onWindowFocusChanged(boolean paramBoolean) {
        super.onWindowFocusChanged(paramBoolean);
        isShow = true;
    }

    public static abstract interface BluetoothPairingDialogListener {
        public abstract void onDialogNegativeClick(PairingRequestDialogView paramPairingRequestDialogView);

        public abstract void onDialogPositiveClick(PairingRequestDialogView paramPairingRequestDialogView);
    }
}
