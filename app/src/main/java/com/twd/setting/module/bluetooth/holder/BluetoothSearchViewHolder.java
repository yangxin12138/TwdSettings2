package com.twd.setting.module.bluetooth.holder;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.twd.setting.databinding.LayoutItemBluetoothResearchBinding;
import com.twd.setting.module.bluetooth.model.CachedBluetoothDevice;
import com.twd.setting.utils.UiUtils;

public class BluetoothSearchViewHolder
        extends RecyclerView.ViewHolder {
    private LayoutItemBluetoothResearchBinding binding;
    private Drawable itemBg;

    public BluetoothSearchViewHolder(LayoutItemBluetoothResearchBinding paramLayoutItemBluetoothResearchBinding, View.OnClickListener paramOnClickListener) {
        super(paramLayoutItemBluetoothResearchBinding.getRoot());
        binding = paramLayoutItemBluetoothResearchBinding;
        UiUtils.setOnClickListener(itemView, paramOnClickListener);
        itemBg = paramLayoutItemBluetoothResearchBinding.itemRL.getBackground();
    }

    public void bind(CachedBluetoothDevice paramCachedBluetoothDevice, int paramInt) {
        paramCachedBluetoothDevice.setCurrentStatus(paramCachedBluetoothDevice.getNewStatus());
        if (paramCachedBluetoothDevice.getCurrentStatus() == 100) {
            binding.btnTv.setVisibility(View.VISIBLE);
            binding.btnSearchingTv.setVisibility(View.GONE);
            binding.itemRL.setBackground(itemBg);
        } else {
            binding.btnTv.setVisibility(View.GONE);
            binding.btnSearchingTv.setVisibility(View.VISIBLE);
            binding.itemRL.setBackground(null);
        }
        binding.setItemData(paramCachedBluetoothDevice);
        itemView.setTag(paramCachedBluetoothDevice);
        binding.executePendingBindings();
    }
}
