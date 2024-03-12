package com.twd.setting.module.network;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.twd.setting.R;
import com.twd.setting.base.BaseFragment;
import com.twd.setting.module.network.model.WifiAccessPoint;
import com.twd.setting.module.network.repository.ConnectivityListener;
import com.twd.setting.module.network.wifi.AddWifiNetworkActivity;
import com.twd.setting.module.network.wifi.WifiConnectionActivity;
import com.twd.setting.module.network.wifi.WifiListRvAdapter;
import com.twd.setting.widgets.MarginTopItemDecoration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * @Author:Yangxin
 * @Description:
 * @time: Create in 17:33 2024/3/5
 */
public class NetworkListActivity extends AppCompatActivity
            implements WifiListRvAdapter.IWifiItemClickListener, ConnectivityListener.WifiNetworkListener,
        ConnectivityListener.Listener,WifiAccessPoint.AccessPointListener {

    private final String TAG = "NetworkListActivity";
    public static String selectedBSSID;
    public static String selectedSSID;
    private WifiListRvAdapter adapter;
    private RecyclerView rvWifiList;

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


    private void updateWifi(){
        if (mConnectivityListener.isWifiEnabledOrEnabling()){
            updateWifiList();
            return;
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_network_list);
        wifiAccessPoints = new ArrayList<WifiAccessPoint>();

        mConnectivityListener = new ConnectivityListener(this, new ConnectivityListener.Listener() {
            @Override
            public void onConnectivityChange() {
                updateWifi();
            }
        },getLifecycle());
        wifiManager = (WifiManager) this.getApplicationContext().getSystemService(WIFI_SERVICE);
        rvWifiList = (RecyclerView) findViewById(R.id.rv_wifi_list);
        rvWifiList.setLayoutManager(new LinearLayoutManager(this));

        adapter = new WifiListRvAdapter();
        adapter.setWifiAccessPoints(wifiAccessPoints);
        adapter.setItemClickListener(this);
        adapter.notifyWifiAccessPoints();
        rvWifiList.requestFocus();
        rvWifiList.setItemAnimator(null);
        rvWifiList.setAdapter(adapter);
        rvWifiList.addItemDecoration(new MarginTopItemDecoration(getResources().getDimensionPixelSize(R.dimen.net_rc_item_margin_t)));
    }

    private void updateWifiList(){
        Log.d(TAG, "updateWifiList: ");
        if (!mConnectivityListener.isWifiEnabledOrEnabling()){
            wifiAccessPoints.clear();
            adapter.clearAll();
            mNoWifiUpdateBeforeMillis = 0L;
            return;
        }

        long l = SystemClock.elapsedRealtime();
        Log.d(TAG, "updateWifiList  2222  l:"+l+", mNoWifiUpdateBeforeMillis:"+mNoWifiUpdateBeforeMillis);
        if (mNoWifiUpdateBeforeMillis > l){
            mHandler .removeCallbacks(mInitialUpdateWifiListRunnable);
            mHandler.postDelayed(mInitialUpdateWifiListRunnable,mNoWifiUpdateBeforeMillis - l);
            return;
        }
        Log.d(TAG, "updateWifiList  3333");
        /*if (loadingDialog.isShowing()){
            loadingDialog.cancel();
        }*/
        int j = wifiAccessPoints.size();
        HashSet localHashSet = new HashSet(j);
        int i = 0;
        while (i < j){
            localHashSet.add((WifiAccessPoint) wifiAccessPoints.get(i));
            i += 1;
        }
        List<WifiAccessPoint> wifiAccessPoint_list = mConnectivityListener.getAvailableNetworks();
        Log.d(TAG, "updateWifiList: wifiAccessPoint_list is " + wifiAccessPoint_list.isEmpty());
        wifiAccessPoints.clear();
        Iterator iterator = wifiAccessPoint_list.iterator();
        WifiAccessPoint localWifiAccessPoint;
        while (iterator.hasNext()){
            WifiAccessPoint wifiAccessPoint = (WifiAccessPoint) iterator.next();
            wifiAccessPoint.setListener(this);
            if (wifiAccessPoint.getTag() == null){
                wifiAccessPoint.setTag(wifiAccessPoint);
            }else {
                localHashSet.remove((WifiAccessPoint) wifiAccessPoint.getTag());
            }
            Log.d(TAG,"list:"+wifiAccessPoint.getSsid()+",state:"+((wifiAccessPoint.getNetworkInfo()==null)?"null": wifiAccessPoint.getNetworkInfo().getState()));
            wifiAccessPoints.add(wifiAccessPoint);
        }
        Iterator iterator_hashset = localHashSet.iterator();
        while (iterator_hashset.hasNext()){
            wifiAccessPoints.remove(iterator_hashset.next());
        }

        if ((wifiAccessPoints != null) && (wifiAccessPoints.size() != 0)){
            adapter.notifyWifiAccessPoints();
        }
    }

    @Override
    public void onAccessPointChanged(WifiAccessPoint wifiAccessPoint) {
        WifiAccessPoint accessPoint = (WifiAccessPoint) wifiAccessPoint.getTag();
        if (accessPoint != null){
            int position = adapter.getWifiAccessPoints().indexOf(accessPoint);
            Log.d(TAG,"onAccessPointChanged  position = "+position+", WifiAccessPoint"+accessPoint);
            adapter.notifyItemChanged(position,accessPoint);
        }
    }

    @Override
    public void onLevelChanged(WifiAccessPoint paramWifiAccessPoint) {
        paramWifiAccessPoint = (WifiAccessPoint) paramWifiAccessPoint.getTag();
        if (paramWifiAccessPoint != null){
            WifiListRvAdapter localWifiListRvAdapter = adapter;
            localWifiListRvAdapter.notifyItemChanged(localWifiListRvAdapter.getWifiAccessPoints().indexOf(paramWifiAccessPoint),paramWifiAccessPoint);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mNoWifiUpdateBeforeMillis = (SystemClock.elapsedRealtime() + 500L);
    }

    @Override
    public void onConnectivityChange() {
        updateConnectivity();
    }

    private void updateConnectivity() {
        if (mConnectivityListener.isWifiEnabledOrEnabling()) {
            updateWifiList();
        }
    }

    @Override
    public void onWifiListChanged() {
        updateWifiList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateConnectivity();
    }

    @Override
    public void onFocusRequest(View paramView, int paramInt) {

    }

    @Override
    public void onItemClick(WifiAccessPoint paramWifiAccessPoint) {
        if (paramWifiAccessPoint == null){
            selectedSSID = "Other";
            selectedBSSID = "add a new network";
            startActivity(new Intent(this, AddWifiNetworkActivity.class));
        }else {
            selectedSSID = paramWifiAccessPoint.getSsidStr();
            selectedBSSID = paramWifiAccessPoint.getBssid();
            startActivity(WifiConnectionActivity.createIntent(this,paramWifiAccessPoint));
        }
    }
}
