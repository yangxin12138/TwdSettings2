package com.twd.setting.module.network.setup;

import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.twd.setting.module.network.util.State;
import com.twd.setting.module.network.util.State.FragmentChangeListener;
import com.twd.setting.module.network.wifi.AddNetworkFragment;

public class AddWifiNetworkState
        implements State {
    private static final String TAG = "AddWifiNetworkState";
    private final FragmentActivity mActivity;
    private Fragment mFragment;

    public AddWifiNetworkState(FragmentActivity paramFragmentActivity) {
        this.mActivity = paramFragmentActivity;
    }

    public Fragment getFragment() {
        return mFragment;
    }

    public void processBackward() {
        Log.d(TAG,"processBackward");
        mFragment = new AddNetworkFragment();
        State.FragmentChangeListener localFragmentChangeListener = (State.FragmentChangeListener) this.mActivity;
        if (localFragmentChangeListener != null) {
            localFragmentChangeListener.onFragmentChange(mFragment, false);
        }
    }

    public void processForward() {
        Log.d(TAG,"processForward");
        mFragment = new AddNetworkFragment();
        State.FragmentChangeListener localFragmentChangeListener = (State.FragmentChangeListener) this.mActivity;
        if (localFragmentChangeListener != null) {
            localFragmentChangeListener.onFragmentChange(mFragment, true);
        }
    }
}
