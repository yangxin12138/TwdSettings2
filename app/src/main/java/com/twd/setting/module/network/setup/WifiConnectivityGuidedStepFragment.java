package com.twd.setting.module.network.setup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.twd.setting.R;
import com.twd.setting.databinding.FragmentGuideStepBinding;

public class WifiConnectivityGuidedStepFragment
        extends DialogFragment {
    protected FragmentGuideStepBinding binding;

    public static WifiConnectivityGuidedStepFragment newInstance() {
        return new WifiConnectivityGuidedStepFragment();
    }

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
    }

    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle) {
        binding = (FragmentGuideStepBinding) DataBindingUtil.inflate(paramLayoutInflater, R.layout.fragment_guide_step, paramViewGroup, false);
        return binding.getRoot();
    }

    public void onViewCreated(View paramView, Bundle paramBundle) {
        super.onViewCreated(paramView, paramBundle);
    }
}

