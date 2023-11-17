package com.twd.setting.module.network.ether;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.ConnectivityManager.NetworkCallback;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.NetworkRequest.Builder;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.twd.setting.R;
import com.twd.setting.databinding.FragmentEditNetConfigBinding;
import com.twd.setting.module.network.EditNetConfigFragment;
import com.twd.setting.module.network.model.NetConfigInfo;
import com.twd.setting.utils.HLog;

public class EthernetConfigsFragment
        extends EditNetConfigFragment {
    private final String LOG_TAG = "EthernetConfigsFragment";
    private ConnectivityManager connMgr;
    private NetConfigInfo info;
    private ConnectivityManager.NetworkCallback networkCallback;
    private EthernetConfigsViewModel viewModel;

    public static EthernetConfigsFragment newInstance() {
        return new EthernetConfigsFragment();
    }

    private void registerEthernetConnect() {
        NetworkRequest localNetworkRequest = new NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET).addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build();
        ConnectivityManager.NetworkCallback local1 = new ConnectivityManager.NetworkCallback() {
            public void onAvailable(Network paramAnonymousNetwork) {
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }

            public void onCapabilitiesChanged(Network paramAnonymousNetwork, NetworkCapabilities paramAnonymousNetworkCapabilities) {
                HLog.d(LOG_TAG, "The network changed capabilities: " + paramAnonymousNetworkCapabilities);
            }

            public void onLinkPropertiesChanged(Network paramAnonymousNetwork, LinkProperties paramAnonymousLinkProperties) {
                HLog.d(LOG_TAG, "The network changed link properties: " + paramAnonymousLinkProperties);
                // paramAnonymousLinkProperties = EthernetConfigsFragment.this;
                // EthernetConfigsFragment.access$102(this, this.viewModel.getNetConfigInfo(paramAnonymousNetwork));
                if (info != null) {
                    StringBuilder str = new StringBuilder();
                    str.append("ip: ");
                    str.append(info.getIpAddress());
                    str.append(", subnetMask: ");
                    str.append(info.getSubnetMask());
                    str.append(", gateway: ");
                    str.append(info.getGateway());
                    str.append(", dns1: ");
                    str.append(info.getDns1());
                    str.append(", dns2: ");
                    str.append(info.getDns2());
                    HLog.d(LOG_TAG, str.toString());
                }
            }

            public void onLost(Network paramAnonymousNetwork) {
                String str = EthernetConfigsFragment.this.LOG_TAG;
                StringBuilder localStringBuilder = new StringBuilder();
                localStringBuilder.append("The application no longer has a network. The last network was ");
                localStringBuilder.append(paramAnonymousNetwork);
                HLog.d(str, localStringBuilder.toString());
            }
        };
        this.networkCallback = local1;
        this.connMgr.registerNetworkCallback(localNetworkRequest, local1);
    }

    private void unregisterEthernetConnect() {
        if (Build.VERSION.SDK_INT >= 21) {
            this.connMgr.unregisterNetworkCallback(this.networkCallback);
        }
    }

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        this.viewModel = ((EthernetConfigsViewModel) new ViewModelProvider(requireActivity()).get(EthernetConfigsViewModel.class));
        this.connMgr = ((ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE));
        if (Build.VERSION.SDK_INT >= 21) {
            registerEthernetConnect();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterEthernetConnect();
    }

    public void onViewCreated(View paramView, Bundle paramBundle) {
        super.onViewCreated(paramView, paramBundle);
        initTitle(paramView, R.string.fragment_title_ethernet_configs);
        binding.btnIgnoreNetwork.setVisibility(View.GONE);
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HLog.d(LOG_TAG, "onViewCreated  btnSave onClick");
            }
        });
    }
}
