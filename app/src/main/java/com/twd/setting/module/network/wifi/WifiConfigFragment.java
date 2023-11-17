package com.twd.setting.module.network.wifi;

import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.twd.setting.R;
import com.twd.setting.databinding.FragmentEditNetConfigBinding;
import com.twd.setting.module.network.EditNetConfigFragment;
import com.twd.setting.module.network.model.NetConfigInfo;
import com.twd.setting.module.network.model.WifiAccessPoint;
import com.twd.setting.module.network.util.WifiConfigHelper;
import com.twd.setting.utils.UiUtils;

public class WifiConfigFragment
        extends EditNetConfigFragment {
    private static final String ARG_WIFI_SECURITY_NAME = "ARG_WIFI_SECURITY_NAME";
    private static final String ARG_WIFI_SSID = "ARG_WIFI_SSID";
    private final String TAG = "WifiConfigFragment";
    private String SSID;
    private NetConfigInfo info;
    private WifiConfiguration mWifiConfiguration;
    private int mWifiSecurity;
    private EditConfigViewModel viewModel;

    public static WifiConfigFragment newInstance(WifiAccessPoint paramWifiAccessPoint) {
        WifiConfigFragment localWifiConfigFragment = new WifiConfigFragment();
        Bundle localBundle = new Bundle();
        localBundle.putString("ARG_WIFI_SSID", paramWifiAccessPoint.getSsidStr());
        localBundle.putInt("ARG_WIFI_SECURITY_NAME", paramWifiAccessPoint.getSecurity());
        localWifiConfigFragment.setArguments(localBundle);
        return localWifiConfigFragment;
    }

    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        Log.d(TAG, "onCreate");
        if (getArguments() != null) {
            SSID = ((String) getArguments().get("ARG_WIFI_SSID"));
            mWifiSecurity = ((Integer) getArguments().get("ARG_WIFI_SECURITY_NAME")).intValue();
            mWifiConfiguration = WifiConfigHelper.getConfiguration(requireContext(), SSID, mWifiSecurity);
        }
    }

    @Override
    public void onViewCreated(View paramView, Bundle paramBundle) {
        super.onViewCreated(paramView, paramBundle);
        Log.d(TAG, "onViewCreated ");
        initTitle(paramView, getString(R.string.fragment_title_specified_wifi, new Object[]{this.SSID}));
        viewModel =  new ViewModelProvider(requireActivity()).get(EditConfigViewModel.class);

        info = viewModel.getNetConfigInfo();
        binding.edtIpAddress.setText(info.getIpAddress());
        binding.edtSubnetMask.setText(info.getSubnetMask());
        binding.edtGateway.setText(info.getGateway());
        binding.edtDNS.setText(info.getDns1());
        binding.btnIgnoreNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onViewCreated  btnIgnoreNetwork onClick");
                viewModel.ignoreNetwork(SSID);
                //UiUtils.replaceFragmentHadBackStack(getParentFragmentManager(), 16908290, WifiListFragment.newInstance(), "WifiConfigFragment");
                WifiListFragment.clearSelectedSSID();
                UiUtils.popBackStack(getParentFragmentManager(),"WifiConfigFragment");
            }
        });
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onViewCreated  btnSave onClick");
            }
        });
    }
}
