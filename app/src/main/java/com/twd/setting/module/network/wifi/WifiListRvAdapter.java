package com.twd.setting.module.network.wifi;

import static android.content.Context.WIFI_SERVICE;
import static com.twd.setting.module.network.wifi.WifiConnectionActivity.TAG;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
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
    private Context mContext;


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
        private final Activity mActivity;
        private boolean isToastDisplayed = false;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            context = binding.getRoot().getContext();
            mActivity = (Activity) binding.getRoot().getContext();
        }
        public void showToast(String text){
            Toast toast = new Toast(context);
            LayoutInflater inflater = LayoutInflater.from(context);
            View layout = inflater.inflate(R.layout.my_toast,(ViewGroup) mActivity.findViewById(R.id.custom_toast_layout));

            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setView(layout);
            TextView Text = layout.findViewById(R.id.custom_toast_message);
            Text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
            Text.setText(text);
            toast.show();
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
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
            String ssid = getCurrentWifiSsid(wifiManager);
            if (wifiAccessPoint.getSsidStr().equals(ssid)){
                Log.d(TAG, "bind: wifiAccessPoint.getSsidStr() = " + wifiAccessPoint.getSsidStr() + ", ssid = " + ssid);
                str = context.getString(R.string.wifi_state_connected);
                /*if (!isToastDisplayed){
                    showToast(context.getResources().getString(R.string.wifi_setup_connection_success));
                    isToastDisplayed = true;
                }*/
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
            DisplayMetrics metric = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(metric);
            int width = metric.widthPixels;//屏幕宽度（单位：px）
            int height = metric.heightPixels;//屏幕高度（单位：px）
            float density = metric.density;//屏幕密度（常见的有：1.5、2.0、3.0）
            int densityDpi = metric.densityDpi;//屏幕DPI（常见的有：240、320、480）
            float densitySW = height / density;
            Log.d(TAG, "width=" + width + ",height=" + height + ",density=" + density + ",densityDpi=" + densityDpi);
            int newWidth = (int)(drawable.getIntrinsicWidth() * 0.6);  // 将宽度缩小为原来的0.5倍
            int newHeight = (int)(drawable.getIntrinsicHeight() * 0.6);  // 将高度缩小为原来的0.5倍
            if (densitySW == 720){
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            } else if (densitySW == 480) {
                drawable.setBounds(0, 0, newWidth, newHeight);
            }else {
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            }
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

