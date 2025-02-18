package com.twd.setting.module.bluetooth.holder;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.twd.setting.R;
import com.twd.setting.databinding.LayoutItemBluetoothBinding;
import com.twd.setting.module.bluetooth.model.CachedBluetoothDevice;
import com.twd.setting.utils.UiUtils;

public class BluetoothViewHolder
        extends RecyclerView.ViewHolder {
    private LayoutItemBluetoothBinding binding;
    private Context mContext;

    public BluetoothViewHolder(LayoutItemBluetoothBinding paramLayoutItemBluetoothBinding, View.OnClickListener paramOnClickListener,Context context) {
        super(paramLayoutItemBluetoothBinding.getRoot());
        binding = paramLayoutItemBluetoothBinding;
        UiUtils.setOnClickListener(itemView, paramOnClickListener);
        mContext = context;
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
        TextView leftTv = binding.leftTv;
        TextView rightTv = binding.rightTv;
        Drawable drawable = leftTv.getCompoundDrawables()[0];
        Drawable rtDrawable = rightTv.getCompoundDrawables()[2];
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;//屏幕宽度（单位：px）
        int height = metric.heightPixels;//屏幕高度（单位：px）
        float density = metric.density;//屏幕密度（常见的有：1.5、2.0、3.0）
        int densityDpi = metric.densityDpi;//屏幕DPI（常见的有：240、320、480）
        float densitySW = height / density;
        int newWidth = (int)(drawable.getIntrinsicWidth() * 0.7);  // 将宽度缩小为原来的0.5倍
        int newWidthRT = (int)(rtDrawable.getIntrinsicWidth() * 0.7);  // 将宽度缩小为原来的0.5倍
        int newHeight = (int)(drawable.getIntrinsicHeight() * 0.7);  // 将高度缩小为原来的0.5倍
        int newHeightRT = (int)(rtDrawable.getIntrinsicHeight() * 0.7);  // 将高度缩小为原来的0.5倍
        if (densitySW == 720 || densitySW == 600){
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            rtDrawable.setBounds(0, 0, rtDrawable.getIntrinsicWidth(), rtDrawable.getIntrinsicHeight());
        } else if (densitySW == 480 ) {
            Log.i("yangxin", "bind: -------------densitySW == 480------------");
            drawable.setBounds(0, 0, newWidth, newHeight);
            rtDrawable.setBounds(0, 0, newWidthRT, newHeightRT);
        } else if (densitySW == 400) {
            drawable.setBounds(0, 0, (int)(drawable.getIntrinsicWidth()*0.8), (int)(drawable.getIntrinsicHeight()*0.8));
            rtDrawable.setBounds(0, 0, (int)(rtDrawable.getIntrinsicWidth()*0.8),(int)(rtDrawable.getIntrinsicHeight()*0.8));
        } else {
            Log.i("yangxin", "bind: -------------走其他默认------------");
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            rtDrawable.setBounds(0, 0, rtDrawable.getIntrinsicWidth(), rtDrawable.getIntrinsicHeight());
        }
        binding.setItemData(paramCachedBluetoothDevice);
        itemView.setTag(paramCachedBluetoothDevice);
        binding.executePendingBindings();
    }
}
