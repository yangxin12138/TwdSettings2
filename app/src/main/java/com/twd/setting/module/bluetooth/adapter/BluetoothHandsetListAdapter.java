package com.twd.setting.module.bluetooth.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.twd.setting.R;
import com.twd.setting.databinding.LayoutItemBluetoothHandsetBinding;
import com.twd.setting.module.bluetooth.holder.BluetoothHandsetViewHolder;
import com.twd.setting.module.bluetooth.model.CachedBluetoothDevice;

import java.util.ArrayList;
import java.util.List;

public class BluetoothHandsetListAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final CachedBluetoothDevice addHandsetItem;
    private final List<CachedBluetoothDevice> dataList = new ArrayList();
    private final View.OnClickListener viewClickListener;

    public BluetoothHandsetListAdapter(View.OnClickListener paramOnClickListener) {
        viewClickListener = paramOnClickListener;
        addHandsetItem = new CachedBluetoothDevice(true);
    }

    private void notifyInsertDevice(int paramInt, CachedBluetoothDevice paramCachedBluetoothDevice) {
        dataList.add(paramInt, paramCachedBluetoothDevice);
        notifyItemInserted(paramInt);
        notifyItemRangeChanged(paramInt, dataList.size() - paramInt);
    }

    private void notifyRemoveDevice(int paramInt) {
        if (paramInt >= dataList.size()) {
            return;
        }
        dataList.remove(paramInt);
        notifyItemRemoved(paramInt);
        notifyItemRangeChanged(paramInt, dataList.size() - paramInt);
    }

    public int getItemCount() {
        return dataList.size();
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt) {
        if ((paramViewHolder instanceof BluetoothHandsetViewHolder)) {
            ((BluetoothHandsetViewHolder) paramViewHolder).bind((CachedBluetoothDevice) dataList.get(paramInt), paramInt, viewClickListener);
        }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt) {
        return new BluetoothHandsetViewHolder((LayoutItemBluetoothHandsetBinding) DataBindingUtil.inflate(LayoutInflater.from(paramViewGroup.getContext()), R.layout.layout_item_bluetooth_handset, paramViewGroup, false));
    }

    public void setDeviceDataList(List<CachedBluetoothDevice> paramList) {
        if (dataList.size() > 1) {
            dataList.clear();
        }
        if (dataList.size() == 0) {
            dataList.add(addHandsetItem);
        }
        if ((paramList != null) && (paramList.size() > 0)) {
            dataList.addAll(paramList);
        }
        notifyDataSetChanged();
    }

    public void updateDevicesState(CachedBluetoothDevice cachedBluetoothDevice) {
        if ((cachedBluetoothDevice != null) && (!TextUtils.isEmpty(cachedBluetoothDevice.getAddress()))) {
            if (!cachedBluetoothDevice.isBluetoothRc()) {
                return;
            }
            if (dataList.size() == 0) {
                dataList.add(addHandsetItem);
            }
            if (dataList.size() == 1) {
                if ((cachedBluetoothDevice.isConnected()) || (cachedBluetoothDevice.isBonded())) {
                    notifyInsertDevice(1, cachedBluetoothDevice);
                }
                return;
            }
            int k = 0;
            int i = 1;
            int j;
            for (; ; ) {
                j = k;
                if (i >= dataList.size()) {
                    break;
                }
                CachedBluetoothDevice localCachedBluetoothDevice = (CachedBluetoothDevice) dataList.get(i);
                if ((localCachedBluetoothDevice != null) && (cachedBluetoothDevice.getAddress().equals(localCachedBluetoothDevice.getAddress()))) {
                    j = i;
                    break;
                }
                i += 1;
            }
            if ((!cachedBluetoothDevice.isConnected()) && (!cachedBluetoothDevice.isBonded())) {
                if (j > 0) {
                    notifyRemoveDevice(j);
                }
            } else {
                if (j > 0) {
                    notifyItemRangeChanged(j, 1);
                    return;
                }
                notifyInsertDevice(1, cachedBluetoothDevice);
            }
        }
    }
}
