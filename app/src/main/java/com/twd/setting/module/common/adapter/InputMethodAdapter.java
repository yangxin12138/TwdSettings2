package com.twd.setting.module.common.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.twd.setting.R;
import com.twd.setting.databinding.LayoutItemInputMethodListBinding;
import com.twd.setting.module.common.holder.InputMethodViewHolder;
import com.twd.setting.module.common.model.InputMethodData;
import com.twd.setting.utils.binding.ItemClickHandle;

import java.util.List;

public class InputMethodAdapter
        extends RecyclerView.Adapter<InputMethodViewHolder> {
    private ItemClickHandle<InputMethodData> clickHandle;
    private List<InputMethodData> dataList;

    public InputMethodAdapter(ItemClickHandle<InputMethodData> paramItemClickHandle, List<InputMethodData> paramList) {
        this.clickHandle = paramItemClickHandle;
        this.dataList = paramList;
    }

    public int getItemCount() {
        List localList = this.dataList;
        if (localList != null) {
            return localList.size();
        }
        return 0;
    }

    public void onBindViewHolder(InputMethodViewHolder paramInputMethodViewHolder, int paramInt) {
        paramInputMethodViewHolder.bind((InputMethodData) this.dataList.get(paramInt), paramInt);
    }

    public InputMethodViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt) {
        LayoutItemInputMethodListBinding layoutItemInputMethodListBinding = (LayoutItemInputMethodListBinding) DataBindingUtil.inflate(LayoutInflater.from(paramViewGroup.getContext()), R.layout.layout_item_input_method_list, paramViewGroup, false);
        //setItemClickListener(this.clickHandle);
        return new InputMethodViewHolder(layoutItemInputMethodListBinding);
    }

    public void setDataList(List<InputMethodData> paramList) {
        this.dataList = paramList;
        notifyDataSetChanged();
    }
}
