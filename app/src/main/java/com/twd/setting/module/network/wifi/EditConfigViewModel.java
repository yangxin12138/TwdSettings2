package com.twd.setting.module.network.wifi;

import android.app.Application;
import android.text.TextUtils;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.twd.setting.module.network.model.NetConfigInfo;
import com.twd.setting.module.network.repository.NetConfigsRepository;
import com.twd.setting.widgets.ToastTools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditConfigViewModel
        extends AndroidViewModel {
    private final String LOG_TAG = "EditConfigViewModel";
    private final MutableLiveData<NetConfigInfo> _netConfigInfoAsLiveData;
    private NetConfigInfo info;
    private final LiveData<NetConfigInfo> netConfigInfoAsLiveData;
    private NetConfigsRepository repository;

    public EditConfigViewModel(Application paramApplication) {
        super(paramApplication);
        MutableLiveData localMutableLiveData = new MutableLiveData();
        _netConfigInfoAsLiveData = localMutableLiveData;
        netConfigInfoAsLiveData = localMutableLiveData;
        repository = new NetConfigsRepository(paramApplication);
    }

    public boolean checkAllNetworkConfigs(String paramString1, String paramString2, String paramString3, String paramString4) {
        StringBuilder localStringBuilder = new StringBuilder("");
        if (!isCorrectIp(paramString1)) {
            localStringBuilder.append("IP 地址");
        }
        if (!isCorrectIp(paramString2)) {
            if (!TextUtils.isEmpty(localStringBuilder)) {
                localStringBuilder.append(",");
            }
            localStringBuilder.append("子网掩码");
        }
        if (!isCorrectIp(paramString3)) {
            if (!TextUtils.isEmpty(localStringBuilder)) {
                localStringBuilder.append(",");
            }
            localStringBuilder.append("网关");
        }
        if (!isCorrectIp(paramString4)) {
            if (!TextUtils.isEmpty(localStringBuilder)) {
                localStringBuilder.append(",");
            }
            localStringBuilder.append("DNS");
        }
        if (!TextUtils.isEmpty(localStringBuilder)) {
            localStringBuilder.append("输入错误，请重新输入");
            ToastTools.Instance().showToast(getApplication(), localStringBuilder.toString());
            return false;
        }
        return true;
    }

    public NetConfigInfo getNetConfigInfo() {
        NetConfigInfo localNetConfigInfo = new NetConfigInfo();
        info = localNetConfigInfo;
        localNetConfigInfo.setIpAddress(repository.getIpAddress());
        info.setSubnetMask(repository.getSubnetMask());
        info.setGateway(repository.getGateway());
        info.setDns1(repository.getDNS1());
        info.setDns2(repository.getDNS2());
        return info;
    }

    public LiveData<NetConfigInfo> getNetConfigInfoAsLiveData() {
        return netConfigInfoAsLiveData;
    }

    public void ignoreNetwork(String paramString) {
        if ((paramString.startsWith("\")")) && (paramString.endsWith("\""))) {
            repository.removeWifiBySsid(paramString);
            return;
        }

        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("\"");
        localStringBuilder.append(paramString);
        localStringBuilder.append("\"");
        repository.removeWifiBySsid(localStringBuilder.toString());
    }

    public boolean isCorrectIp(String paramString) {
        return Pattern.compile("([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}").matcher(paramString).matches();
    }

    public void updateCurrentConnectedConfig(NetConfigInfo paramNetConfigInfo) {
        if (!checkAllNetworkConfigs(paramNetConfigInfo.getIpAddress(), paramNetConfigInfo.getSubnetMask(), paramNetConfigInfo.getDns1(), paramNetConfigInfo.getGateway())) {
            return;
        }
        repository.changeWifiConfiguration(false, paramNetConfigInfo.getIpAddress(), repository.countPrefixLength(paramNetConfigInfo.getSubnetMask()), paramNetConfigInfo.getDns1(), paramNetConfigInfo.getDns2(), paramNetConfigInfo.getGateway());
    }
}

