package com.twd.setting.module.network.wifi;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.twd.setting.R;
import com.twd.setting.SettingApplication;
import com.twd.setting.base.BaseFragment;
import com.twd.setting.databinding.FragmentWifiListBinding;
import com.twd.setting.module.network.model.WifiAccessPoint;
import com.twd.setting.module.network.model.WifiAccessPoint.AccessPointListener;
import com.twd.setting.module.network.repository.ConnectivityListener;
import com.twd.setting.module.network.repository.ConnectivityListener.Listener;
import com.twd.setting.module.network.repository.ConnectivityListener.WifiNetworkListener;
import com.twd.setting.utils.UiUtils;
import com.twd.setting.widgets.DialogTools;
import com.twd.setting.widgets.MarginTopItemDecoration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
//import org.apache.http.util.TextUtils;

public class WifiListFragment
        extends BaseFragment
        implements WifiListRvAdapter.IWifiItemClickListener, ConnectivityListener.WifiNetworkListener, ConnectivityListener.Listener, WifiAccessPoint.AccessPointListener {
    private static final int INITIAL_UPDATE_DELAY = 500;
    private static final String TAG = "WifiListFragment";
    public static String selectedBSSID;
    public static String selectedSSID;
    private WifiListRvAdapter adapter;
    private FragmentWifiListBinding binding;
    private AlertDialog loadingDialog;
    private ConnectivityListener mConnectivityListener;
    private final Handler mHandler = new Handler();
    private final Runnable mInitialUpdateWifiListRunnable = new Runnable() {
        @Override
        public void run() {
            //WifiListFragment.access$002(WifiListFragment.this, 0L);
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

    private void hideNoWifiTip() {
        binding.tvTipNoWifi.setVisibility(View.GONE);
        binding.rvWifiList.setVisibility(View.VISIBLE);
    }

    public static WifiListFragment newInstance() {
        return new WifiListFragment();
    }

    private void showNoWifiTip() {
        return;
    /*    int i = ((SettingApplication) requireActivity().getApplication()).getThemeId();
        Drawable localDrawable;
        if (i == R.style.Theme_TVSettings2_White) {
            localDrawable = getResources().getDrawable(R.mipmap.ic_no_wifi_black);
        } else {
            if ((i != R.style.Theme_TVSettings2_IceCreamBlue) && (i != R.style.Theme_TVSettings2_StarrySkyBlue)) {
                return;
            }
            localDrawable = getResources().getDrawable(R.mipmap.ic_no_wifi_white);
        }
        localDrawable.setBounds(0, 0, localDrawable.getIntrinsicWidth(), localDrawable.getIntrinsicHeight());
        binding.tvTipNoWifi.setCompoundDrawablePadding(16);
        binding.tvTipNoWifi.setCompoundDrawables(localDrawable, null, null, null);
        binding.tvTipNoWifi.setVisibility(View.VISIBLE);
        binding.rvWifiList.setVisibility(View.INVISIBLE);

     */
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
        Log.d(TAG, "updateWifiList  2222");
        long l = SystemClock.elapsedRealtime();
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
            Log.d(TAG,"list:"+wifiAccessPoint.getSsid()+",state"+((wifiAccessPoint.getNetworkInfo()==null)?"null": wifiAccessPoint.getNetworkInfo().getState()));
            wifiAccessPoints.add(wifiAccessPoint);
        }
        Iterator iterator_hashset = localHashSet.iterator();
        while (iterator_hashset.hasNext()) {
            wifiAccessPoints.remove(iterator_hashset.next());
        }

        if ((wifiAccessPoints != null) && (wifiAccessPoints.size() != 0)) {
            adapter.notifyWifiAccessPoints();
            if (binding.tvTipNoWifi.getVisibility() == View.VISIBLE) {
                hideNoWifiTip();
            }
        } else {
            showNoWifiTip();
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

    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        wifiAccessPoints = new ArrayList();
        mConnectivityListener = new ConnectivityListener(requireContext(), this, getLifecycle());
    }

    @Override
    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle) {
        binding = (FragmentWifiListBinding) DataBindingUtil.inflate(paramLayoutInflater, R.layout.fragment_wifi_list, paramViewGroup, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clearSelectedSSID();
    }

    @Override
    public void onFocusRequest(View view, int paramInt) {
        Log.d(TAG, "onFocusRequest view: "+view+",int:"+paramInt);
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
        }
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

    //        if (paramWifiAccessPoint.isActive()) {
    //            Log.d(TAG, "goto WifiConfigFragment");
    //            UiUtils.replaceFragmentHadBackStack(getParentFragmentManager(), 16908290, WifiConfigFragment.newInstance(paramWifiAccessPoint), "WifiConfigFragment");
    //        }else {
                Log.d(TAG, "goto WifiConnectionActivity");
                startActivity(WifiConnectionActivity.createIntent(getActivity(), paramWifiAccessPoint));
    //        }
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

    @Override
    public void onResume() {
        super.onResume();
        updateConnectivity();

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
        initTitle(paramView, R.string.fragment_title_wifi_list);
        binding.rvWifiList.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new WifiListRvAdapter();
        adapter.setWifiAccessPoints(wifiAccessPoints);
        adapter.setItemClickListener(this);
        binding.rvWifiList.setItemAnimator(null);
        binding.rvWifiList.setAdapter(adapter);
        binding.rvWifiList.addItemDecoration(new MarginTopItemDecoration(getResources().getDimensionPixelSize(R.dimen.net_rc_item_margin_t)));
        loadingDialog = DialogTools.getLoadingDialog(requireContext(),getString(R.string.wifi_state_scan)  /*"正在扫描 WIFI ..."*/);
        loadingDialog.show();
    }

    @Override
    public void onWifiListChanged() {
        updateWifiList();
    }
}
