package com.twd.setting.module.systemequipment.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.twd.setting.R;
import com.twd.setting.module.systemequipment.holder.DeviceInfosViewHolder;
import com.twd.setting.module.systemequipment.model.DeviceInfoItem;

import java.util.List;

public class DeviceInfosAdapter
        extends RecyclerView.Adapter<DeviceInfosViewHolder> {
    private List<DeviceInfoItem> dataList;

    public DeviceInfosAdapter(List<DeviceInfoItem> paramList) {
        dataList = paramList;
    }

    public int getItemCount() {
        if (dataList != null) {
            return dataList.size();
        }
        return 0;
    }

    public void onBindViewHolder(DeviceInfosViewHolder holder, int paramInt) {
        if (dataList == null) {
            return;
        }
        holder.bind( dataList.get(paramInt));
    }

    public DeviceInfosViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt) {
        return new DeviceInfosViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.layout_item_device_info, paramViewGroup, false));
    }
}

