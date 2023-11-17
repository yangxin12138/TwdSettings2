package com.twd.setting.module.network;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.ConnectivityManager.NetworkCallback;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.NetworkRequest.Builder;
import android.os.Build;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.twd.setting.module.network.repository.ConnectivityListener;
import com.twd.setting.module.network.repository.WifiStateRepository;

import java.util.Objects;

public class NetworkViewModel
        extends AndroidViewModel {
    private final String TAG = "NetworkViewModel";
    private final MutableLiveData<String> _connectedSSIDAsLiveData = new MutableLiveData();
    private final MediatorLiveData<Boolean> _isWifiEnable = new MediatorLiveData();
    private final MutableLiveData<Integer> _wifiStateAsLiveData = new MutableLiveData();
    private ConnectivityManager connManager;
    private final LiveData<String> connectedSSIDAsLiveData = new MutableLiveData();
    private ConnectivityListener connectivityListener;
    private final LiveData<Boolean> isWifiEnable = new MediatorLiveData();
    private WifiStateRepository repository;
    private final MutableLiveData<Boolean> toggleStateAsLiveData = new MutableLiveData();

    public NetworkViewModel(Application paramApplication) {
        super(paramApplication);

        ConnectivityManager localConnectivityManager = (ConnectivityManager) paramApplication.getSystemService(Context.CONNECTIVITY_SERVICE);
        connManager = (ConnectivityManager) paramApplication.getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityListener = new ConnectivityListener(paramApplication, null);
        Objects.requireNonNull(new MutableLiveData());

        repository = new WifiStateRepository(paramApplication, new WifiStateRepository.IWifiStateChange() {
            @Override
            public void onWifiStateChange(int paramInt) {
                Log.d(TAG,"onWifiStateChange");
            }
        });
        _isWifiEnable.addSource(new MutableLiveData(), new Observer() {
            @Override
            public void onChanged(Object o) {

            }
        });

        if (Build.VERSION.SDK_INT >= 21) {
            localConnectivityManager.requestNetwork(new NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build(), new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(Network paramAnonymousNetwork) {
                    super.onAvailable(paramAnonymousNetwork);
                    String ssid = connectivityListener.getSsid();
                    if ((paramAnonymousNetwork != null) && (!TextUtils.equals("<unknown ssid>", ssid))) {
                        NetworkViewModel.this._connectedSSIDAsLiveData.postValue(ssid);
                    }
                }

                @Override
                public void onLost(Network paramAnonymousNetwork) {
                    super.onLost(paramAnonymousNetwork);
                    String ssid = connectivityListener.getSsid();
                    if ((paramAnonymousNetwork != null) && (!TextUtils.equals("<unknown ssid>", ssid))) {
                        NetworkViewModel.this._connectedSSIDAsLiveData.postValue(ssid);
                    }
                }
            });
        }

    }

    public void disableWifi() {
        repository.disableWifi();
    }

    public void enableWifi() {
        repository.enableWifi();
    }

    public ConnectivityManager getConnManager() {
        return connManager;
    }

    public LiveData<String> getConnectedSSID() {
        return connectedSSIDAsLiveData;
    }

    public MutableLiveData<Boolean> getToggleState() {
        return toggleStateAsLiveData;
    }

    public boolean isWifiConnected() {
        NetworkInfo localNetworkInfo = connManager.getActiveNetworkInfo();
        return (localNetworkInfo != null) && (localNetworkInfo.isConnected()) && (localNetworkInfo.getType() == 1);
    }

    public LiveData<Boolean> isWifiEnable() {
        return isWifiEnable;
    }

}
