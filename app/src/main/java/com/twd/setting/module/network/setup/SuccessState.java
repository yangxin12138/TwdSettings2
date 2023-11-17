package com.twd.setting.module.network.setup;

import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.twd.setting.R;
import com.twd.setting.module.network.util.State;
import com.twd.setting.module.network.util.State.FragmentChangeListener;
import com.twd.setting.module.network.util.StateMachine;
import com.twd.setting.widgets.ToastTools;

public class SuccessState
        implements State {
    private static final String TAG = "SuccessState";
    private final FragmentActivity mActivity;
    private Fragment mFragment;

    public SuccessState(FragmentActivity paramFragmentActivity) {
        mActivity = paramFragmentActivity;
    }

    public Fragment getFragment() {
        return mFragment;
    }

    public void processBackward() {
        Log.d(TAG,"processBackward");
        ((StateMachine) new ViewModelProvider(mActivity).get(StateMachine.class)).back();
    }

    public void processForward() {
        Log.d(TAG,"processForward");
        mFragment = SuccessFragment.newInstance(mActivity.getString(R.string.wifi_setup_connection_success));
        State.FragmentChangeListener localFragmentChangeListener = (State.FragmentChangeListener) mActivity;
        if (localFragmentChangeListener != null) {
            localFragmentChangeListener.onFragmentChange(mFragment, true);
        }

    }

    public static class SuccessFragment
            extends Fragment {
        private static final String EXTRA_TITLE = "title";
        private StateMachine stateMachine;

        public static SuccessFragment newInstance(String paramString) {
            SuccessFragment localSuccessFragment = new SuccessFragment();
            Bundle localBundle = new Bundle();
            localBundle.putString(EXTRA_TITLE, paramString);
            localSuccessFragment.setArguments(localBundle);
            return localSuccessFragment;
        }

        public void onCreate(Bundle paramBundle) {
            stateMachine = ((StateMachine) new ViewModelProvider(requireActivity()).get(StateMachine.class));
            super.onCreate(paramBundle);
            //stateMachine.finish(0);
        }

        public void onPause() {
            super.onPause();
        }

        public void onResume() {
            super.onResume();
            Object localObject = getArguments();
            if (localObject != null) {
                localObject = ((Bundle) localObject).getString(EXTRA_TITLE);
                ToastTools.Instance().showToast(requireContext(), (String) localObject);
            }
            stateMachine.finish(-1);
        }
    }
}

