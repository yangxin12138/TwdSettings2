package com.twd.setting.module.systemequipment.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.twd.setting.R;
import com.twd.setting.module.systemequipment.model.StorageInfoData;
import com.twd.setting.widgets.RatioLineCustomView;

public class StorageDetailViewHolder
        extends RecyclerView.ViewHolder {
    private TextView itemFreeSizeTv;
    private TextView itemSizeTv;
    private ImageView itemStorageIv;
    private TextView itemStorageNameTv;
    private RatioLineCustomView itemStorageRlcv;

    public StorageDetailViewHolder(View paramView) {
        super(paramView);
        itemStorageIv = ((ImageView) paramView.findViewById(R.id.itemStorageIv));
        itemStorageRlcv = ((RatioLineCustomView) paramView.findViewById(R.id.itemStorageRlcv));
        itemStorageNameTv = ((TextView) paramView.findViewById(R.id.itemStorageNameTv));
        itemSizeTv = ((TextView) paramView.findViewById(R.id.itemSizeTv));
        itemFreeSizeTv = ((TextView) paramView.findViewById(R.id.itemFreeSizeTv));
    }

    public void bind(StorageInfoData infoData) {
        if (infoData == null) {
            return;
        }
        itemStorageIv.setImageResource(infoData.iconRes);
        itemStorageRlcv.setAllData(infoData.itemDataList);
        itemStorageNameTv.setText(infoData.nameStr);
        itemSizeTv.setText(infoData.totalSizeStr);
        itemFreeSizeTv.setText(infoData.freeSizeStr);
    }
}

