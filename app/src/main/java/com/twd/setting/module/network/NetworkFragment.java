package com.twd.setting.module.network;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.twd.setting.R;
import com.twd.setting.base.BaseFragment;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.twd.setting.databinding.FragmentNetworkBinding;
import com.twd.setting.module.network.model.WifiAccessPoint;
import com.twd.setting.module.network.model.WifiAccessPoint.AccessPointListener;
import com.twd.setting.module.network.repository.ConnectivityListener;
import com.twd.setting.module.network.repository.ConnectivityListener.Listener;
import com.twd.setting.module.network.repository.ConnectivityListener.WifiNetworkListener;
import com.twd.setting.module.network.repository.ConnectivityListener;
import com.twd.setting.module.network.speed.DownloadUrlPresenter;
import com.twd.setting.module.network.speed.NetworkSpeedFragment;
import com.twd.setting.module.network.wifi.AddWifiNetworkActivity;
import com.twd.setting.module.network.wifi.WifiConfigFragment;
import com.twd.setting.module.network.wifi.WifiConnectionActivity;
import com.twd.setting.module.network.wifi.WifiListFragment;
import com.twd.setting.module.network.wifi.WifiListRvAdapter;
import com.twd.setting.utils.KkUtils;
import com.twd.setting.utils.UiUtils;
import com.twd.setting.widgets.DialogTools;
import com.twd.setting.widgets.MarginTopItemDecoration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
public class NetworkFragment
        extends BaseFragment
        implements WifiListRvAdapter.IWifiItemClickListener, ConnectivityListener.WifiNetworkListener, ConnectivityListener.Listener, WifiAccessPoint.AccessPointListener{
    private final int ITEM_ID_AVAILABLE_NETWORK = 1;
    private final int ITEM_ID_ETHERNET = 2;
    private final int ITEM_ID_NETWORK_SPEED_TEST = 3;
    private final int ITEM_ID_WIFI_SWITCH = 0;
    private final String TAG = "NetworkFragment";
    public static String selectedBSSID;
    public static String selectedSSID;
    private WifiListRvAdapter adapter;
    private FragmentNetworkBinding binding;
	private AlertDialog loadingDialog;

    private ConnectivityListener mConnectivityListener;

    private WifiManager wifiManager;
    private final Handler mHandler = new Handler();
    private final Runnable mInitialUpdateWifiListRunnable = new Runnable() {
        @Override
        public void run() {
            updateWifiList();
        }
    };
    private long mNoWifiUpdateBeforeMillis;
    private List<WifiAccessPoint> wifiAccessPoints;
	
    public static void clearSelectedSSID() {
        if ((TextUtils.isEmpty(selectedSSID)) && (TextUtils.isEmpty(selectedBSSID))) {
            return;
        }
        selectedSSID = "";
        selectedBSSID = "";
    }
    public static NetworkFragment newInstance() {
        return new NetworkFragment();
    }

    private void updateWifi() {
        if (mConnectivityListener.isWifiEnabledOrEnabling()) {
            binding.switchWifi.setChecked(true);
            updateWifiList();
            return;
        }
        binding.switchWifi.setChecked(false);
    }


	private void updateConnectivity() {
        if (!isAdded()) {
            return;
        }
        if (mConnectivityListener.isWifiEnabledOrEnabling()) {
            updateWifiList();
        }
    }

    private void updateWifiList() {
        Log.d(TAG, "updateWifiList");
        if (!isAdded()) {
            return;
        }
        Log.d(TAG, "updateWifiList  111");
        if (!mConnectivityListener.isWifiEnabledOrEnabling()) {
            wifiAccessPoints.clear();
            adapter.clearAll();
            mNoWifiUpdateBeforeMillis = 0L;
            return;
        }

        long l = SystemClock.elapsedRealtime();
        Log.d(TAG, "updateWifiList  2222  l:"+l+", mNoWifiUpdateBeforeMillis:"+mNoWifiUpdateBeforeMillis);
        if (mNoWifiUpdateBeforeMillis > l) {
            mHandler.removeCallbacks(mInitialUpdateWifiListRunnable);
            mHandler.postDelayed(mInitialUpdateWifiListRunnable, mNoWifiUpdateBeforeMillis - l);
            return;
        }
        Log.d(TAG, "updateWifiList  3333");
        if (loadingDialog.isShowing()) {
            loadingDialog.cancel();
        }
        int j = wifiAccessPoints.size();
        HashSet localHashSet = new HashSet(j);
        int i = 0;
        while (i < j) {
            localHashSet.add((WifiAccessPoint) wifiAccessPoints.get(i));
            i += 1;
        }
        List<WifiAccessPoint> wifiAccessPoint_list = mConnectivityListener.getAvailableNetworks();
        wifiAccessPoints.clear();
        Iterator iterator = wifiAccessPoint_list.iterator();
        WifiAccessPoint localWifiAccessPoint;
        while (iterator.hasNext()) {
            WifiAccessPoint wifiAccessPoint = (WifiAccessPoint) iterator.next();
            wifiAccessPoint.setListener(this);
            if(wifiAccessPoint.getTag() == null){
                wifiAccessPoint.setTag(wifiAccessPoint);
            }else{
                localHashSet.remove((WifiAccessPoint)wifiAccessPoint.getTag());
            }
        //    Log.d(TAG,"list:"+wifiAccessPoint.getSsid()+",state:"+((wifiAccessPoint.getNetworkInfo()==null)?"null": wifiAccessPoint.getNetworkInfo().getState()));
            wifiAccessPoints.add(wifiAccessPoint);
        }
        Iterator iterator_hashset = localHashSet.iterator();
        while (iterator_hashset.hasNext()) {
            wifiAccessPoints.remove(iterator_hashset.next());
        }

        if ((wifiAccessPoints != null) && (wifiAccessPoints.size() != 0)) {
            adapter.notifyWifiAccessPoints();
        }
    }

    @Override
    public void onAccessPointChanged(WifiAccessPoint wifiAccessPoint) {
        WifiAccessPoint  accessPoint = (WifiAccessPoint) wifiAccessPoint.getTag();
        if (accessPoint != null) {
            int position = adapter.getWifiAccessPoints().indexOf(accessPoint);
            Log.d(TAG,"onAccessPointChanged  position = "+position+", WifiAccessPoint"+accessPoint);
            adapter.notifyItemChanged(position, accessPoint);
        }
    }



    @Override
    public void onConnectivityChange() {
		updateConnectivity();
    }

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
		wifiAccessPoints = new ArrayList<WifiAccessPoint>();

        mConnectivityListener = new ConnectivityListener(requireContext(), new ConnectivityListener.Listener() {
            @Override
            public void onConnectivityChange() {
                updateWifi();
            }
        }, getLifecycle());
        wifiManager = ((WifiManager)requireContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE));
    }

    private boolean isChecked(){
        return binding.switchWifi.isChecked();
    }

    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle) {
        binding = (FragmentNetworkBinding) DataBindingUtil.inflate(paramLayoutInflater, R.layout.fragment_network, paramViewGroup, false);
        return binding.getRoot();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        requireContext().unregisterReceiver(wifiReceiver);
    }

    @Override
    public void onFocusRequest(View view, int paramInt) {
        /*Log.d(TAG, "onFocusRequest view: "+view+",int:"+paramInt);
        LinearLayoutManager localLinearLayoutManager = (LinearLayoutManager) binding.rvWifiList.getLayoutManager();
        if (view != null) {
            view.requestFocus();
        } else if (localLinearLayoutManager != null) {
            view = localLinearLayoutManager.findViewByPosition(paramInt);
            if (view != null) {
                view.requestFocus();
            }
        }
        if (localLinearLayoutManager != null) {
            localLinearLayoutManager.scrollToPosition(paramInt);
        }*/
    }

    @Override
    public void onItemClick(WifiAccessPoint paramWifiAccessPoint) {
		Log.d(TAG, "onItemClick" + paramWifiAccessPoint);

        if (paramWifiAccessPoint == null) {
            selectedSSID = getString(R.string.selected_ssid_add_new_network);
            selectedBSSID = getString(R.string.selected_bssid_add_new_network);
            Log.d(TAG, "WifiAccessPoint is null addWifiNetwork, ssid:"+selectedSSID+", bssid"+selectedBSSID);
            startActivity(new Intent(getActivity(), AddWifiNetworkActivity.class));
        } else {
            selectedSSID = paramWifiAccessPoint.getSsidStr();
            selectedBSSID = paramWifiAccessPoint.getBssid();
            Log.d(TAG, "onItemClick" + paramWifiAccessPoint+", ssid:"+selectedSSID+", bssid"+selectedBSSID);

            //if (paramWifiAccessPoint.isActive()) {
            //    Log.d(TAG, "goto WifiConfigFragment");
            //    UiUtils.replaceFragmentHadBackStack(getParentFragmentManager(), 16908290, WifiConfigFragment.newInstance(paramWifiAccessPoint), "WifiConfigFragment");
            //}else {
                Log.d(TAG, "goto WifiConnectionActivity");
                startActivity(WifiConnectionActivity.createIntent(getActivity(), paramWifiAccessPoint));
            //}
        }
    }

    @Override
    public void onLevelChanged(WifiAccessPoint paramWifiAccessPoint) {
        Log.d(TAG, "onLevelChanged");
        paramWifiAccessPoint = (WifiAccessPoint) paramWifiAccessPoint.getTag();
        if (paramWifiAccessPoint != null) {
            WifiListRvAdapter localWifiListRvAdapter = adapter;
            localWifiListRvAdapter.notifyItemChanged(localWifiListRvAdapter.getWifiAccessPoints().indexOf(paramWifiAccessPoint), paramWifiAccessPoint);
        }
    }
    public void onResume() {
        super.onResume();
		updateConnectivity();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        requireContext().registerReceiver(wifiReceiver, intentFilter);
    }
    @Override
    public void onStart() {
        super.onStart();
        mConnectivityListener.setWifiListener(this);
        mNoWifiUpdateBeforeMillis = (SystemClock.elapsedRealtime() + 500L);
        updateWifiList();
    }
    @Override
    public void onViewCreated(View paramView, Bundle paramBundle) {
        super.onViewCreated(paramView, paramBundle);
        initTitle(paramView, R.string.str_network);
        //binding.rvWifiList.setLayoutManager(new LinearLayoutManager(requireContext()));
		adapter = new WifiListRvAdapter();
        adapter.setWifiAccessPoints(wifiAccessPoints);
        adapter.setItemClickListener(this);
        binding.itemWifiSwitch.requestFocus();
        binding.itemWifiAvailable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), NetworkListActivity.class));
            }
        });
        binding.itemWifiSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "itemWifiSwitch onClick");
                if(binding.switchWifi.isChecked()){
                    wifiManager.setWifiEnabled(false);
                    /*binding.rvWifiList.setVisibility(View.GONE);
                    wifiAccessPoints.clear();*/
                    adapter.clearAll();
                    binding.itemWifiAvailable.setVisibility(View.GONE);
                    //adapter.notifyWifiAccessPoints();
                }else {
                    wifiManager.setWifiEnabled(true);
                    /*binding.rvWifiList.setVisibility(View.VISIBLE);
                    loadingDialog.show();*/
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.itemWifiAvailable.setVisibility(View.VISIBLE);
                        }
                    },8000);
                }
            }
        });
        if (wifiManager.isWifiEnabled()){
            binding.itemWifiAvailable.setVisibility(View.VISIBLE);
        }else {
            binding.itemWifiAvailable.setVisibility(View.GONE);
        }
		/*binding.rvWifiList.setItemAnimator(null);
        binding.rvWifiList.setAdapter(adapter);
        binding.rvWifiList.addItemDecoration(new MarginTopItemDecoration(getResources().getDimensionPixelSize(R.dimen.net_rc_item_margin_t)));*/
        loadingDialog = DialogTools.getLoadingDialog(requireContext(),getString(R.string.wifi_state_scan)  /*"正在扫描 WIFI ..."*/);
        //loadingDialog.show();

    }
    public void onWifiListChanged() {
        updateWifiList();
    }

    private String getCurrentWifiSsid(WifiManager wifiManager){
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssid;
        if (wifiInfo != null && wifiInfo.getSupplicantState() == SupplicantState.COMPLETED){
            ssid = wifiInfo.getSSID().replace("\"","");
        }else {
            ssid = "无连接";
        }
        return ssid;
    }

    private final BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (networkInfo != null && networkInfo.isConnected()) {
                    // WiFi已连接
                    // 处理连接WiFi的逻辑
                    binding.itemWifiAvailableName.setText(getCurrentWifiSsid(wifiManager));
                } else {
                    // WiFi断开连接
                    // 处理断开WiFi的逻辑
                    binding.itemWifiAvailableName.setText(getCurrentWifiSsid(wifiManager));
                }
            }
        }
    };
}