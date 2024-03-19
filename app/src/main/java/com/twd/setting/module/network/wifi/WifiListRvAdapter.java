package com.twd.setting.module.network.wifi;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.twd.setting.R;
import com.twd.setting.databinding.LayoutItemWifiListBinding;
import com.twd.setting.module.network.model.WifiAccessPoint;
import com.twd.setting.utils.UiUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WifiListRvAdapter
        extends RecyclerView.Adapter<ViewHolder> {
    public static final String LOG_TAG = "WifiListRvAdapter";
    public IWifiItemClickListener itemClickListener;
    private List<WifiAccessPoint> wifiAccessPoints = new ArrayList<WifiAccessPoint>();

    public void clearAll() {
        List localList = getWifiAccessPoints();
        if (localList != null) {
            localList.clear();
            notifyItemRangeChanged(0, localList.size() + 1);
        }
    }

    @Override
    public int getItemCount() {
        List localList = getWifiAccessPoints();
        if (localList != null) {
            return localList.size() + 1;
        }
        return 1;
    }

    public List<WifiAccessPoint> getWifiAccessPoints() {
        return wifiAccessPoints;
    }

    public void notifyWifiAccessPoints() {
        List localList = getWifiAccessPoints();
        notifyItemRangeChanged(0, localList.size() + 1);
        if (/*(TextUtils.equals(WifiListFragment.selectedSSID,context.getString(R.string.wifi_ssid_other))) &&*/ (TextUtils.equals(WifiListFragment.selectedBSSID, "add a new network"))) {
            itemClickListener.onFocusRequest(null, localList.size());
            return;
        }
        if (!TextUtils.isEmpty(WifiListFragment.selectedSSID)) {
            Iterator localIterator = localList.iterator();
            while (localIterator.hasNext()) {
                WifiAccessPoint localWifiAccessPoint = (WifiAccessPoint) localIterator.next();
                if ((TextUtils.equals(WifiListFragment.selectedSSID, localWifiAccessPoint.getSsidStr())) && (TextUtils.equals(WifiListFragment.selectedBSSID, localWifiAccessPoint.getBssid()))) {
                    itemClickListener.onFocusRequest(null, localList.indexOf(localWifiAccessPoint));
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Log.d("WifiListRvAdapter","position:"+position);
        if(wifiAccessPoints == null){
            Log.d("WifiListRvAdapter","new wifiAccessPoints:");
            wifiAccessPoints = new ArrayList<WifiAccessPoint>();
        }
        if (position == getWifiAccessPoints().size()) {

            ((ViewHolder) holder).binding.tvSSID.setText(R.string.selected_ssid_add_new_network);
            ((ViewHolder) holder).binding.tvTip.setVisibility(View.INVISIBLE);

            if (TextUtils.equals(WifiListFragment.selectedSSID, holder.itemView.getContext().getString(R.string.selected_ssid_add_new_network))) {

                if (TextUtils.equals(WifiListFragment.selectedBSSID, holder.itemView.getContext().getString(R.string.selected_bssid_add_new_network))) {
                    itemClickListener.onFocusRequest(holder.itemView, getItemCount() - 1);

                }
            }
        } else {
            WifiAccessPoint wifiAccessPoint =  getWifiAccessPoints().get(position);
            ((ViewHolder) holder).bind(wifiAccessPoint);
            Log.d(LOG_TAG,"onBindViewHolder position:"+position+",ssid:"+wifiAccessPoint.getSsidStr()+",selectedSSID:"+WifiListFragment.selectedSSID);
            if ((TextUtils.equals(wifiAccessPoint.getSsidStr(), WifiListFragment.selectedSSID)) && (TextUtils.equals(wifiAccessPoint.getBssid(), WifiListFragment.selectedBSSID))) {
                itemClickListener.onFocusRequest(holder.itemView, position);
            }
        }
        UiUtils.setOnClickListener(((ViewHolder) holder).binding.getRoot(), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG,"onBindViewHolder onClick");
                if(position>= getWifiAccessPoints().size()){
                    itemClickListener.onItemClick(null);
                }else {
                    WifiAccessPoint wifiAccessPoint = (WifiAccessPoint) getWifiAccessPoints().get(position);
                    itemClickListener.onItemClick(wifiAccessPoint);
                }
            }
        });
    }
  /*
  public void onBindViewHolder(ViewHolder paramViewHolder, int paramInt)
  {
    Object localObject1 = getWifiAccessPoints();
    if (paramInt == getWifiAccessPoints().size())
    {

      paramViewHolder.binding.tvSSID.setText("其他");
      paramViewHolder.binding.tvTip.setVisibility(View.INVISIBLE);

      if (TextUtils.equals(WifiListFragment.selectedSSID, paramViewHolder.itemView.getContext().getString(R.string.selected_ssid_add_new_network)))
      {

        if (TextUtils.equals(WifiListFragment.selectedBSSID, paramViewHolder.itemView.getContext().getString(R.string.selected_bssid_add_new_network)))
        {
          itemClickListener.onFocusRequest(paramViewHolder.itemView, getItemCount() - 1);

        }
      }
    }
    else
    {
      WifiAccessPoint wifiAccessPoint = (WifiAccessPoint)getWifiAccessPoints().get(paramInt);
      paramViewHolder.bind(wifiAccessPoint);
      if ((TextUtils.equals(wifiAccessPoint.getSsidStr(), WifiListFragment.selectedSSID)) && (TextUtils.equals(wifiAccessPoint.getBssid(), WifiListFragment.selectedBSSID))) {
        itemClickListener.onFocusRequest(paramViewHolder.itemView, paramInt);
      }
    }
    UiUtils.setOnClickListener(paramViewHolder.binding.getRoot(), new View.OnClickListener() {
      @Override
      public void onClick(View view) {

      }
    });
  }

   */

    public void onBindViewHolder(ViewHolder paramViewHolder, int paramInt, List<Object> paramList) {

        if (paramList.isEmpty()) {
            super.onBindViewHolder(paramViewHolder, paramInt, paramList);
            return;
        }
        List localList = getWifiAccessPoints();
        Log.d(LOG_TAG,"onBindViewHolder paramInt:"+paramInt+", list.size: "+localList.size());
        if (paramInt < localList.size()) {
            localList.set(paramInt, (WifiAccessPoint) paramList.get(0));
        }
        onBindViewHolder(paramViewHolder, paramInt);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt) {
        //return new ViewHolder((LayoutItemWifiListBinding)DataBindingUtil.inflate(LayoutInflater.from(paramViewGroup.getContext()), R.layout.layout_item_wifi_list, paramViewGroup, false));
        View view = LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.layout_item_wifi_list, paramViewGroup, false);
        return new ViewHolder(view);
    }

    public void setItemClickListener(IWifiItemClickListener paramIWifiItemClickListener) {
        itemClickListener = paramIWifiItemClickListener;
    }

    public void setWifiAccessPoints(List<WifiAccessPoint> paramList) {
        wifiAccessPoints = paramList;
    }

    public interface IWifiItemClickListener {
        void onFocusRequest(View paramView, int paramInt);

        void onItemClick(WifiAccessPoint paramWifiAccessPoint);
    }

    static class ViewHolder
            extends RecyclerView.ViewHolder {
        private final LayoutItemWifiListBinding binding;
        private final Context context;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            context = binding.getRoot().getContext();
        }

        /*
        public ViewHolder(LayoutItemWifiListBinding layoutItemWifiListBinding)
        {
          super();
          //super();
          binding = layoutItemWifiListBinding;
          context = layoutItemWifiListBinding.getRoot().getContext();
        }
        */
        public void bind(WifiAccessPoint wifiAccessPoint) {
            if (wifiAccessPoint == null) {
                return;
            }
            binding.tvSSID.setText(wifiAccessPoint.getSsidStr());
            String str = null;
            Log.d("WifiListAdapter","=========WifiAccessPoint:"+wifiAccessPoint);

            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            String ssid = getCurrentWifiSsid(wifiManager);
            if (wifiAccessPoint.getSsidStr().equals(ssid)){
                str = context.getString(R.string.wifi_state_connected);
            }else if (wifiAccessPoint.isSaved()){
                str = context.getString(R.string.wifi_state_saved);
            }else {
                str = "";
            }
            /*if (wifiAccessPoint.isActive()) {
                str = context.getString(R.string.wifi_state_connected);
            } else if (wifiAccessPoint.isSaved()) {
                str = context.getString(R.string.wifi_state_saved);
            } else {
                str = "";
            }*/

            binding.tvTip.setText(str);
            Drawable drawable = WifiSignalHelper.getIconSignalStrength(context, wifiAccessPoint);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            binding.tvTip.setCompoundDrawablePadding(60);
            binding.tvTip.setCompoundDrawables(null, null, drawable, null);
            binding.tvTip.setVisibility(View.VISIBLE);
        }

        private String getCurrentWifiSsid(WifiManager wifiManager){
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String ssid;
            if (wifiInfo != null && wifiInfo.getSupplicantState() == SupplicantState.COMPLETED){
                ssid = wifiInfo.getSSID().replace("\"","");
            }else {
                ssid = "默认网络";
            }
            return ssid;
        }
    }
}

