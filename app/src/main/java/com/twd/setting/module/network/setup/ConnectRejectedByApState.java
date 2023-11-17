package com.twd.setting.module.network.setup;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.twd.setting.R;
import com.twd.setting.module.network.util.State;
import com.twd.setting.module.network.util.State.FragmentChangeListener;
import com.twd.setting.module.network.util.State.StateCompleteListener;
import com.twd.setting.module.network.util.StateMachine;
//import com.twd.setting.utils.HLog;
import com.twd.setting.widgets.ToastTools;

public class ConnectRejectedByApState
        implements State {
    private static final String TAG = "RejectedByApState";
    private final FragmentActivity mActivity;
    private Fragment mFragment;

    public ConnectRejectedByApState(FragmentActivity paramFragmentActivity) {
        mActivity = paramFragmentActivity;
    }

    public Fragment getFragment() {
        return mFragment;
    }

    public void processBackward() {
        Log.d(TAG,"processBackward");
        mFragment = new ConnectRejectedByApFragment();
        State.FragmentChangeListener localFragmentChangeListener = (State.FragmentChangeListener) mActivity;
        if (localFragmentChangeListener != null) {
            localFragmentChangeListener.onFragmentChange(mFragment, false);
        }
    }

    public void processForward() {
        Log.d(TAG,"processForward");
        mFragment = new ConnectRejectedByApFragment();;
        State.FragmentChangeListener localFragmentChangeListener = (State.FragmentChangeListener) mActivity;
        if (localFragmentChangeListener != null) {
            localFragmentChangeListener.onFragmentChange(mFragment, true);
        }
    }

    public static class ConnectRejectedByApFragment
            extends Fragment {
        private static final String TAG = "ConnectRejectedByApFragment";
        private StateMachine mStateMachine;
        private UserChoiceInfo mUserChoiceInfo;

        public void onCreate(Bundle paramBundle) {
            mStateMachine = ((StateMachine) new ViewModelProvider(requireActivity()).get(StateMachine.class));
            mUserChoiceInfo = ((UserChoiceInfo) new ViewModelProvider(requireActivity()).get(UserChoiceInfo.class));
            super.onCreate(paramBundle);
        }

        public void onResume() {
            super.onResume();
            ToastTools.Instance().showToast(requireContext(), "被拒绝连接");
            mUserChoiceInfo.removePageSummary(1);
            mUserChoiceInfo.removePageSummary(2);
            mUserChoiceInfo.removePageSummary(3);
            mStateMachine.getListener().onComplete(6);
        }

        public void onViewCreated(View paramView, Bundle paramBundle) {
            super.onViewCreated(paramView, paramBundle);
            String str = getString(R.string.title_wifi_could_not_connect_ap_reject);
            Log.d("ConnectReject", str);
        }
    }
}
