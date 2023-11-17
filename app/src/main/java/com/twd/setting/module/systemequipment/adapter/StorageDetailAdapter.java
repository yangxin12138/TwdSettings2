package com.twd.setting.module.systemequipment.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.twd.setting.R;
import com.twd.setting.module.systemequipment.holder.StorageDetailViewHolder;
import com.twd.setting.module.systemequipment.model.StorageInfoData;
import com.twd.setting.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class StorageDetailAdapter
        extends RecyclerView.Adapter<StorageDetailViewHolder> {
    private List<StorageInfoData> dataList = new ArrayList();

    public int getItemCount() {
        return dataList.size();
    }

    public void onBindViewHolder(StorageDetailViewHolder paramStorageDetailViewHolder, int paramInt) {
        paramStorageDetailViewHolder.bind((StorageInfoData) dataList.get(paramInt));
    }

    public StorageDetailViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt) {
        return new StorageDetailViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.layout_item_storage_detail, paramViewGroup, false));
    }

    public void setExternalStorageDataList(List<StorageInfoData> paramList) {
        StorageInfoData localStorageInfoData;
        if (dataList.size() > 0) {
            localStorageInfoData = (StorageInfoData) dataList.get(0);
        } else {
            localStorageInfoData = null;
        }
        dataList.clear();
        if ((localStorageInfoData != null) && (localStorageInfoData.isSystemData)) {
            dataList.add(localStorageInfoData);
        }
        if (!CollectionUtils.isEmpty(paramList)) {
            dataList.addAll(paramList);
        }
        notifyDataSetChanged();
    }

    public void setSystemStorageData(StorageInfoData paramStorageInfoData) {
        StorageInfoData localStorageInfoData;
        if (dataList.size() > 0) {
            localStorageInfoData = (StorageInfoData) dataList.get(0);
        } else {
            localStorageInfoData = null;
        }
        if ((localStorageInfoData != null) && (localStorageInfoData.isSystemData)) {
            dataList.remove(0);
        }
        if (paramStorageInfoData != null) {
            dataList.add(0, paramStorageInfoData);
        }
        notifyDataSetChanged();
    }
}

