package com.twd.setting.module.systemequipment.holder;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.twd.setting.R;
import com.twd.setting.module.systemequipment.model.DeviceInfoItem;

public class DeviceInfosViewHolder
        extends RecyclerView.ViewHolder {
    TextView leftTv;
    TextView rightTv;

    public DeviceInfosViewHolder(View paramView) {
        super(paramView);
        leftTv = ((TextView) paramView.findViewById(R.id.leftTv));
        rightTv = ((TextView) paramView.findViewById(R.id.rightTv));
    }

    public void bind(DeviceInfoItem infoItem) {
        if (infoItem == null) {
            return;
        }
        leftTv.setText(infoItem.leftStr);
        rightTv.setText(infoItem.rightStr);
    }
}

