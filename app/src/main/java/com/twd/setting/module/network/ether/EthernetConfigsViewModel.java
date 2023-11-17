package com.twd.setting.module.network.ether;

import android.app.Application;
import android.net.Network;

import androidx.lifecycle.AndroidViewModel;

import com.twd.setting.module.network.model.NetConfigInfo;
import com.twd.setting.utils.HLog;
import com.twd.setting.widgets.ToastTools;

public class EthernetConfigsViewModel
        extends AndroidViewModel {
    private String LOG_TAG = "EthernetConfigsViewModel";
    private EthernetConfigsRepository repository;

    public EthernetConfigsViewModel(Application paramApplication) {
        super(paramApplication);
        repository = new EthernetConfigsRepository(paramApplication);
    }

    public void changeEtherConfig(String paramString1, String paramString2, String paramString3, String paramString4) {
        if (repository.isEtherConnected()) {
            boolean bool = repository.setEthernetConfigs(true, paramString1, paramString2, paramString4, paramString3);
            HLog.d(LOG_TAG, "success ---> " + bool);
            return;
        }
        ToastTools.Instance().showToast(getApplication(), "有线网络未插入");
    }

    public NetConfigInfo getNetConfigInfo(Network paramNetwork) {
        if (repository.isEtherConnected()) {
            NetConfigInfo localNetConfigInfo = new NetConfigInfo();
            localNetConfigInfo.setIpAddress(repository.getIpAddress(paramNetwork));
            localNetConfigInfo.setSubnetMask(repository.getSubnetMask(paramNetwork));
            localNetConfigInfo.setGateway(repository.getGateway(paramNetwork));
            localNetConfigInfo.setDns1(repository.getDNS1(paramNetwork).replace("/", ""));
            localNetConfigInfo.setDns2(repository.getDNS2(paramNetwork).replace("/", ""));
            return localNetConfigInfo;
        }
        return null;
    }
}
