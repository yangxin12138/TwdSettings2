package com.twd.setting.module.network.wifi;

import android.app.Application;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.twd.setting.module.network.repository.WifiConnectRepository;
//import com.twd.setting.utils.HLog;
import java.util.Objects;

public class WifiViewModel
        extends AndroidViewModel {
    private final String TAG = "WifiViewModel";
    private final MutableLiveData<NetworkInfo.State> _networkStateAsLiveData = new MutableLiveData();
    private WifiConnectRepository connectRepository;

    public WifiViewModel(Application paramApplication) {
        super(paramApplication);

        Objects.requireNonNull(new MutableLiveData());
        connectRepository = new WifiConnectRepository(paramApplication, new WifiConnectRepository.INetworkStateChange() {
            @Override
            public void onNetworkStateChange(State paramState) {
                Log.d(TAG,"onNetworkStateChange");
            }
        });
    }

    public boolean addNetwork(String paramString1, String paramString2, String paramString3) {
        return connectRepository.addNetwork(paramString1, paramString2, paramString3);
    }

    public int connect(ScanResult paramScanResult, String paramString) {
        return connectRepository.connectWiFi(paramScanResult, paramString);
    }

    public void removeConfigured(String str) {
        WifiConfiguration wifiConfiguration = connectRepository.getExistConfig(str);
        boolean flag = connectRepository.removeConfigured(wifiConfiguration);
        Log.d(TAG, "remove " + str + ", success: " + flag);
    }
}
