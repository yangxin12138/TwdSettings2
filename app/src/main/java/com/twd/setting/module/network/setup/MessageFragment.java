package com.twd.setting.module.network.setup;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.twd.setting.R;
import com.twd.setting.databinding.FragmentWifiConnectivityLoadingBinding;
import com.twd.setting.module.network.util.AccessibilityHelper;

public class MessageFragment
        extends DialogFragment {
    private static final String TAG = "MessageFragment";
    private static final String EXTRA_SHOW_PROGRESS_INDICATOR = "show_progress_indicator";
    private static final String EXTRA_TITLE = "title";
    protected FragmentWifiConnectivityLoadingBinding binding;

    public static void addArguments(Bundle paramBundle, String paramString, boolean paramBoolean) {
        Log.d(TAG,"addArguments  title:"+paramString+", show_progress_indicator:"+paramBoolean);
        paramBundle.putString(EXTRA_TITLE, paramString);
        paramBundle.putBoolean(EXTRA_SHOW_PROGRESS_INDICATOR, paramBoolean);
    }

    public static MessageFragment newInstance(String paramString, boolean paramBoolean) {
        Log.d(TAG,"newInstance");
        MessageFragment localMessageFragment = new MessageFragment();
        Bundle localBundle = new Bundle();
        addArguments(localBundle, paramString, paramBoolean);
        localMessageFragment.setArguments(localBundle);
        return localMessageFragment;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle paramBundle) {
        Log.d(TAG,"onCreateView:");
		binding =  DataBindingUtil.inflate(layoutInflater, R.layout.fragment_wifi_connectivity_loading, viewGroup, false);

        Bundle localBundle = getArguments();
        String str = "";
        if (localBundle != null) {
            str = localBundle.getString(EXTRA_TITLE);
            Log.d(TAG,"onCreateView:  EXTRA_TITLE:"+str);
        } else {
            str = "";
        }
        int i;
        if ((localBundle != null) && (localBundle.getBoolean(EXTRA_SHOW_PROGRESS_INDICATOR))) {
            i = 1;
            Log.d(TAG,"onCreateView:  EXTRA_SHOW_PROGRESS_INDICATOR");
        } else {
            i = 0;
        }
        if (layoutInflater != null) {
            Log.d(TAG,"newInstance  layoutInflater is not null");
            binding.layoutProgressbar.msgTv.setText(str);
            binding.layoutProgressbar.msgTv.setVisibility(View.VISIBLE);

            if (AccessibilityHelper.forceFocusableViews(getActivity())) {
                binding.layoutProgressbar.msgTv.setFocusable(true);
                binding.layoutProgressbar.msgTv.setFocusableInTouchMode(true);

            }
        } else {
            Log.d(TAG,"newInstance  layoutInflater is null");
            binding.layoutProgressbar.msgTv.setVisibility(View.GONE);
        }
        if (i != 0) {
            binding.layoutProgressbar.bluetoothListProgress.setVisibility(View.VISIBLE);
            return binding.getRoot();
        }else {
            binding.layoutProgressbar.bluetoothListProgress.setVisibility(View.GONE);
            return binding.getRoot();
        }
    }

    public void onResume() {
        super.onResume();
        if (AccessibilityHelper.forceFocusableViews(requireActivity())) {
            ((TextView) requireView().findViewById(R.id.layout_progressbar).findViewById(R.id.msgTv)).requestFocus();
        }
    }

    public void onViewCreated(View paramView, Bundle paramBundle) {
        super.onViewCreated(paramView, paramBundle);
        setCancelable(false);
    }
}

