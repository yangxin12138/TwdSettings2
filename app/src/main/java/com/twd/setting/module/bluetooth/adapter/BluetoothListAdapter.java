package com.twd.setting.module.bluetooth.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.DiffUtil.ItemCallback;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.twd.setting.R;
import com.twd.setting.commonlibrary.Utils.StringUtils;
import com.twd.setting.databinding.LayoutItemBluetoothBinding;
import com.twd.setting.databinding.LayoutItemBluetoothResearchBinding;
import com.twd.setting.module.bluetooth.holder.BluetoothSearchViewHolder;
import com.twd.setting.module.bluetooth.holder.BluetoothViewHolder;
import com.twd.setting.module.bluetooth.model.CachedBluetoothDevice;

import java.util.ArrayList;
import java.util.List;

public class BluetoothListAdapter
        extends ListAdapter<CachedBluetoothDevice, RecyclerView.ViewHolder> {
    private static final int ITEM_TYPE_CONTENT = 1;
    private static final int ITEM_TYPE_HEADER = 0;
    private final View.OnClickListener clickListener;
    private final CachedBluetoothDevice searchBtnItem;
    private Context mContext;

    public BluetoothListAdapter(View.OnClickListener paramOnClickListener,Context context) {
        super(new DiffUtil.ItemCallback() {
            @Override
            public boolean areContentsTheSame(@NonNull Object oldItem, @NonNull Object newItem) {
                int i = ((CachedBluetoothDevice) newItem).getCurrentStatus();
                if ((i == 0) && (!StringUtils.isTrimEmpty(((CachedBluetoothDevice) oldItem).getStatusStr()))) {
                    return false;
                }
                if (((CachedBluetoothDevice) oldItem).getCurrentStatus() == ((CachedBluetoothDevice) newItem).getNewStatus()) {
                    return true;
                }
                return false;
            }

            @Override
            public boolean areItemsTheSame(@NonNull Object oldItem, @NonNull Object newItem) {
                if (oldItem == newItem) {
                    return true;
                }
                if (((CachedBluetoothDevice) oldItem).getDevice() != null) {
                    if (((CachedBluetoothDevice) newItem).getDevice() == null) {
                        return false;
                    }
                    return (!TextUtils.isEmpty(((CachedBluetoothDevice) oldItem).getDevice().getAddress())) && (((CachedBluetoothDevice) oldItem).getDevice().getAddress().equals(((CachedBluetoothDevice) newItem).getDevice().getAddress()));
                }
                return false;
            }

        });
        clickListener = paramOnClickListener;
        searchBtnItem = new CachedBluetoothDevice(true);
        searchBtnItem.setCurrentStatus(100);
        mContext = context;
    }

    private List<CachedBluetoothDevice> addFirstBtn(List<CachedBluetoothDevice> paramList) {
        ArrayList localArrayList = new ArrayList();
        localArrayList.add(searchBtnItem);
        if ((paramList != null) && (paramList.size() > 0)) {
            Log.d("BluetoothListAdapter","paramList:"+paramList);
            localArrayList.addAll(paramList);
        }
        return localArrayList;
    }

    public int getItemCount() {
        return super.getItemCount();
    }

    public int getItemViewType(int paramInt) {
        if (isHeaderView(paramInt)) {
            return 0;
        }
        return 1;
    }

    public boolean isHeaderView(int paramInt) {
        return paramInt == 0;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt) {
        if (isHeaderView(paramInt)) {
            ((BluetoothSearchViewHolder) paramViewHolder).bind((CachedBluetoothDevice) getItem(paramInt), paramInt);
            return;
        }
        ((BluetoothViewHolder) paramViewHolder).bind((CachedBluetoothDevice) getItem(paramInt), paramInt);
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt) {
        if (paramInt == 0) {
            return new BluetoothSearchViewHolder((LayoutItemBluetoothResearchBinding) DataBindingUtil.inflate(LayoutInflater.from(paramViewGroup.getContext()), R.layout.layout_item_bluetooth_research, paramViewGroup, false), clickListener);
        }
        return new BluetoothViewHolder((LayoutItemBluetoothBinding) DataBindingUtil.inflate(LayoutInflater.from(paramViewGroup.getContext()), R.layout.layout_item_bluetooth, paramViewGroup, false), clickListener,mContext);
    }

    public void submitList(List<CachedBluetoothDevice> paramList) {
        super.submitList(addFirstBtn(paramList));
    }

    public void submitList(List<CachedBluetoothDevice> paramList, Runnable paramRunnable) {
        super.submitList(addFirstBtn(paramList), paramRunnable);
    }

    public void updateFirstBtn(boolean paramBoolean) {
        int i;
        if (paramBoolean) {
            i = 101;
        } else {
            i = 100;
        }
        searchBtnItem.setNewStatus(i);
        notifyItemChanged(0);
    }
}