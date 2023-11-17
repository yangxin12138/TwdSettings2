package com.twd.setting.module.bluetooth.holder;

import android.content.res.Resources;
import android.view.View;
import android.view.View.OnClickListener;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.twd.setting.R;
import com.twd.setting.databinding.LayoutItemBluetoothBinding;
import com.twd.setting.module.bluetooth.model.CachedBluetoothDevice;
import com.twd.setting.utils.UiUtils;

public class BluetoothViewHolder
        extends RecyclerView.ViewHolder {
    private LayoutItemBluetoothBinding binding;

    public BluetoothViewHolder(LayoutItemBluetoothBinding paramLayoutItemBluetoothBinding, View.OnClickListener paramOnClickListener) {
        super(paramLayoutItemBluetoothBinding.getRoot());
        binding = paramLayoutItemBluetoothBinding;
        UiUtils.setOnClickListener(itemView, paramOnClickListener);
    }

    public void bind(CachedBluetoothDevice paramCachedBluetoothDevice, int paramInt) {
        paramCachedBluetoothDevice.setCurrentStatus(paramCachedBluetoothDevice.getNewStatus());
        paramInt = paramCachedBluetoothDevice.getCurrentStatus();
        if(paramInt==1){
            paramCachedBluetoothDevice.setStatusStr(binding.getRoot().getResources().getString(R.string.str_bluetooth_connecting));
        } else if (paramInt == 2) {
            paramCachedBluetoothDevice.setStatusStr(binding.getRoot().getResources().getString(R.string.str_bluetooth_connected));
        } else if (paramInt==11) {
            paramCachedBluetoothDevice.setStatusStr(binding.getRoot().getResources().getString(R.string.str_bluetooth_bonding));
        } else if (paramInt==12) {
            paramCachedBluetoothDevice.setStatusStr(binding.getRoot().getResources().getString(R.string.str_bluetooth_bonded));
        }else {
            paramCachedBluetoothDevice.setStatusStr(null);
        }

        binding.setItemData(paramCachedBluetoothDevice);
        itemView.setTag(paramCachedBluetoothDevice);
        binding.executePendingBindings();
    }
}
