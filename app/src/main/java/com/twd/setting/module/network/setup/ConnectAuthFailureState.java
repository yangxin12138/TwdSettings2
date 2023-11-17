package com.twd.setting.module.network.setup;

import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.twd.setting.R;
import com.twd.setting.module.network.util.State;
import com.twd.setting.module.network.util.State.FragmentChangeListener;
import com.twd.setting.module.network.util.State.StateCompleteListener;
import com.twd.setting.module.network.util.StateMachine;
import com.twd.setting.widgets.ToastTools;

public class ConnectAuthFailureState
        implements State {
    private static final String TAG = "ConnectAuthFailureState";
    private final FragmentActivity mActivity;
    private Fragment mFragment;

    public ConnectAuthFailureState(FragmentActivity paramFragmentActivity) {
        mActivity = paramFragmentActivity;
    }

    public Fragment getFragment() {
        return mFragment;
    }

    public void processBackward() {
        Log.d(TAG,"processBackward");
        ConnectAuthFailureFragment localConnectAuthFailureFragment = new ConnectAuthFailureFragment();
        mFragment = new ConnectAuthFailureFragment();
        State.FragmentChangeListener localFragmentChangeListener = (State.FragmentChangeListener) this.mActivity;
        if (localFragmentChangeListener != null) {
            localFragmentChangeListener.onFragmentChange(localConnectAuthFailureFragment, false);
        }
    }

    public void processForward() {
        Log.d(TAG,"processForward");
        ConnectAuthFailureFragment localConnectAuthFailureFragment = new ConnectAuthFailureFragment();
        this.mFragment = localConnectAuthFailureFragment;
        State.FragmentChangeListener localFragmentChangeListener = (State.FragmentChangeListener) this.mActivity;
        if (localFragmentChangeListener != null) {
            localFragmentChangeListener.onFragmentChange(localConnectAuthFailureFragment, true);
        }
    }

    public static class ConnectAuthFailureFragment
            extends Fragment {
        private StateMachine mStateMachine;
        private UserChoiceInfo mUserChoiceInfo;

        public void onCreate(Bundle paramBundle) {
            mStateMachine = ((StateMachine) new ViewModelProvider(requireActivity()).get(StateMachine.class));
            mUserChoiceInfo = ((UserChoiceInfo) new ViewModelProvider(requireActivity()).get(UserChoiceInfo.class));
            super.onCreate(paramBundle);
        }

        public void onResume() {
            super.onResume();
            String str = getString(R.string.title_wifi_could_not_connect_authentication_failure);
            ToastTools.Instance().showToast(requireContext(), str);
            mUserChoiceInfo.removePageSummary(1);
            mUserChoiceInfo.removePageSummary(2);
            mUserChoiceInfo.removePageSummary(3);
            mStateMachine.getListener().onComplete(6);
        }
    }
}