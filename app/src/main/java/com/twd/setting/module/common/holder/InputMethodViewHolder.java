package com.twd.setting.module.common.holder;

import androidx.recyclerview.widget.RecyclerView;

import com.twd.setting.databinding.LayoutItemInputMethodListBinding;
import com.twd.setting.module.common.model.InputMethodData;

public class InputMethodViewHolder
        extends RecyclerView.ViewHolder {
    private LayoutItemInputMethodListBinding binding;

    public InputMethodViewHolder(LayoutItemInputMethodListBinding paramLayoutItemInputMethodListBinding) {
        super(paramLayoutItemInputMethodListBinding.getRoot());
        this.binding = paramLayoutItemInputMethodListBinding;
    }

    public void bind(InputMethodData paramInputMethodData, int paramInt) {
        this.binding.setItemData(paramInputMethodData);
        this.binding.executePendingBindings();
    }
}
