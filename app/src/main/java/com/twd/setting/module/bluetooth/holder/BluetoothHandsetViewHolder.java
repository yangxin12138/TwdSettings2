package com.twd.setting.module.bluetooth.holder;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.twd.setting.R;
import com.twd.setting.databinding.LayoutItemBluetoothHandsetBinding;
import com.twd.setting.module.bluetooth.model.CachedBluetoothDevice;
import com.twd.setting.utils.UiUtils;

public class BluetoothHandsetViewHolder
        extends RecyclerView.ViewHolder {
    private LayoutItemBluetoothHandsetBinding binding;

    public BluetoothHandsetViewHolder(LayoutItemBluetoothHandsetBinding paramLayoutItemBluetoothHandsetBinding) {
        super(paramLayoutItemBluetoothHandsetBinding.getRoot());
        binding = paramLayoutItemBluetoothHandsetBinding;
    }

    public void bind(CachedBluetoothDevice device, int paramInt, View.OnClickListener paramOnClickListener) {
        int state = device.getNewStatus();
    /*if (paramInt != 1)
    {
      if (paramInt != 2)
      {
        if (paramInt != 12) {
          binding.handsetStateTv.setText(null);
        } else {
          binding.handsetStateTv.setText(R.string.str_bluetooth_bonded);
        }
      }else {
        binding.handsetStateTv.setText(R.string.str_bluetooth_connected);
      }
    }else {
      binding.handsetStateTv.setText(R.string.str_bluetooth_connecting);
    }*/
        Log.d("BtHandsetViewHolder","state change "+state);
        if (state == 1) {
            binding.handsetStateTv.setText(R.string.str_bluetooth_connecting);
        } else if (state == 2) {
            binding.handsetStateTv.setText(R.string.str_bluetooth_connected);
        } else if (state == 12) {
            binding.handsetStateTv.setText(R.string.str_bluetooth_bonded);
        } else {
            binding.handsetStateTv.setText(null);
        }
        binding.setItemData(device);
        itemView.setTag(device);
        UiUtils.setOnClickListener(binding.getRoot(), paramOnClickListener);
    }
}
