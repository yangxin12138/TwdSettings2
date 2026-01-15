package com.twd.setting.module.network.wifi;

import static android.content.Context.WIFI_SERVICE;
import static com.twd.setting.module.network.wifi.WifiConnectionActivity.TAG;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
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
import com.twd.setting.module.network.model.AccessPoint;
import com.twd.setting.module.network.model.WifiAccessPoint;
import com.twd.setting.utils.UiUtils;

import java.net.InetAddress;
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
        public void bind(WifiAccessPoint wifiAccessPoint) {
            if (wifiAccessPoint == null) {
                return;
            }
            binding.tvSSID.setText(wifiAccessPoint.getSsidStr());
            String str = "";
            final String TAG = "WIFI_STATE_CHECK";

            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String currentSsid = wifiInfo.getSSID() != null ? wifiInfo.getSSID().replace("\"", "") : "";
            String apSsid = wifiAccessPoint.getSsidStr().replace("\"", "");

            // 核心修改：判断已连接 + 检测网络状态
            if (apSsid.equals(currentSsid)){
                Log.d(TAG, "【初始状态】当前连接WiFi：" + apSsid + "，先显示验证中");
                str = context.getString(R.string.wifi_state_verifying);
                binding.tvTip.setText(str);

                new Thread(() -> {
                    boolean isNetworkOk = false;
                    try {
                        Log.d(TAG, "【延迟检测】开始3秒内多次验证网络状态");
                        // 每500毫秒检测一次，持续3秒，取最后一次结果
                        for (int i = 0; i < 6; i++) { // 6次 × 500毫秒 = 3秒
                            Thread.sleep(500);
                            isNetworkOk = hasInternet();
                            Log.d(TAG, "【多次检测】第" + (i+1) + "次检测结果：" + isNetworkOk);
                        }
                    } catch (InterruptedException e) {
                        Log.e(TAG, "【延迟异常】等待过程被中断：" + e.getMessage());
                        Thread.currentThread().interrupt();
                    } catch (Exception e) {
                        Log.e(TAG, "【检测异常】" + e.getMessage());
                    }

                    // 3. 切回主线程更新UI + 打日志
                    boolean finalIsNetworkOk = isNetworkOk;
                    binding.tvTip.post(() -> {
                        String finalStr = finalIsNetworkOk
                                ? context.getString(R.string.wifi_state_connected)
                                : context.getString(R.string.wifi_state_connected_no_network);
                        Log.d(TAG, "【检测完成】" + apSsid + "网络可用：" + finalIsNetworkOk + "，更新状态为：" + finalStr);

                        // 更新文字
                        binding.tvTip.setText(finalStr);

                        // 强制刷新当前item（极简刷新：不用notifyItemChanged，直接更新）
                        binding.tvTip.invalidate();
                        Log.d(TAG, "【UI刷新】已刷新" + apSsid + "的状态文字");
                    });
                }).start();
            }else if (wifiAccessPoint.isSaved()){
                str = context.getString(R.string.wifi_state_saved);
            }else {
                str = "";
            }
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
            //Log.d(TAG, "width=" + width + ",height=" + height + ",density=" + density + ",densityDpi=" + densityDpi);
            int newWidth = (int)(drawable.getIntrinsicWidth() * 0.6);  // 将宽度缩小为原来的0.5倍
            int newHeight = (int)(drawable.getIntrinsicHeight() * 0.6);  // 将高度缩小为原来的0.5倍
            if (densitySW == 720 || densitySW == 600){
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            } else if (densitySW == 480 || densitySW == 400) {
                drawable.setBounds(0, 0, newWidth, newHeight);
            }else if (densitySW == 320) {
                drawable.setBounds(0, 0, (int)(drawable.getIntrinsicWidth() * 0.3), (int)(drawable.getIntrinsicHeight() * 0.3));
            }else if (densitySW == 540) {
                drawable.setBounds(0, 0, (int)(drawable.getIntrinsicWidth() * 0.7), (int)(drawable.getIntrinsicHeight() * 0.7));
            }   else {
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

        private boolean hasInternet() {
            // 获取连接管理器
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm == null) return true;

            // 获取当前活跃网络
            Network activeNetwork = cm.getActiveNetwork();
            if (activeNetwork == null) return false;

            // 获取网络能力
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(activeNetwork);
            if (capabilities == null) return false;

            // 验证：是WiFi + 系统已验证可访问公网 + 有互联网能力
            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                    && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        }

    }


}

