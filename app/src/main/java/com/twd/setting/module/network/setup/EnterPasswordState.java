package com.twd.setting.module.network.setup;

import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.twd.setting.module.network.util.State;
import com.twd.setting.module.network.util.State.FragmentChangeListener;
import com.twd.setting.module.network.wifi.EnterPasswordFragment;

public class EnterPasswordState
        implements State {
    private static final String TAG = "EnterPasswordState";
    private final FragmentActivity mActivity;
    private Fragment mFragment;

    public EnterPasswordState(FragmentActivity paramFragmentActivity) {
        mActivity = paramFragmentActivity;
    }

    public Fragment getFragment() {
        return mFragment;
    }

    public void processBackward() {
        Log.d(TAG,"processBackward");
        mFragment = new EnterPasswordFragment();
        ((State.FragmentChangeListener) mActivity).onFragmentChange(mFragment, false);
    }

    public void processForward() {
        Log.d(TAG,"processForward");
        mFragment = new EnterPasswordFragment();
        ((State.FragmentChangeListener) mActivity).onFragmentChange(mFragment, true);
    }
}
