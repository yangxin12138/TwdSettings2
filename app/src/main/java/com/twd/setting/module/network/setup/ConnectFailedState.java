package com.twd.setting.module.network.setup;

import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.twd.setting.R;
import com.twd.setting.module.network.util.State;
import com.twd.setting.module.network.util.State.FragmentChangeListener;
import com.twd.setting.module.network.util.State.StateCompleteListener;
import com.twd.setting.module.network.util.StateMachine;
import com.twd.setting.utils.HLog;
import com.twd.setting.widgets.ToastTools;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ConnectFailedState
        implements State {
    private static final String TAG = "ConnectFailedState";
    private final FragmentActivity mActivity;
    private Fragment mFragment;

    public ConnectFailedState(FragmentActivity paramFragmentActivity) {
        mActivity = paramFragmentActivity;
    }

    public Fragment getFragment() {
        return mFragment;
    }

    public void processBackward() {
        Log.d(TAG,"processBackward");
        ((StateMachine) new ViewModelProvider(this.mActivity).get(StateMachine.class)).back();
    }

    public void processForward() {
        Log.d(TAG,"processForward");
        mFragment = new ConnectFailedFragment();
        State.FragmentChangeListener localFragmentChangeListener = (State.FragmentChangeListener) this.mActivity;
        if (localFragmentChangeListener != null) {
            localFragmentChangeListener.onFragmentChange(mFragment, true);
        }
    }

    public static class ConnectFailedFragment
            extends WifiConnectivityGuidedStepFragment {
        private static final String TAG = "ConnectFailedFragment";
        private static final int ACTION_ID_TRY_AGAIN = 100001;
        private static final int ACTION_ID_VIEW_AVAILABLE_NETWORK = 100002;
        private StateMachine mStateMachine;
        private UserChoiceInfo mUserChoiceInfo;

        public void onCreate(Bundle paramBundle) {
            mStateMachine = ((StateMachine) new ViewModelProvider(requireActivity()).get(StateMachine.class));
            mUserChoiceInfo = ((UserChoiceInfo) new ViewModelProvider(requireActivity()).get(UserChoiceInfo.class));
            super.onCreate(paramBundle);
        }

        public void onResume() {
            super.onResume();
            //ToastTools.Instance().showToast(requireContext(), "未知错误");
            mStateMachine.getListener().onComplete(StateMachine.SELECT_WIFI);//6
        }

        @Override
        public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle) {
            return null;
        }

        public void onViewCreated(View paramView, Bundle paramBundle) {
            super.onViewCreated(paramView, paramBundle);
            String declaredMethod = "";
            try {
                declaredMethod = (String) WifiConfiguration.class.getDeclaredMethod("getPrintableSsid", new Class[0]).invoke(this.mUserChoiceInfo.getWifiConfiguration(), new Object[0]);
            } catch (InvocationTargetException InvocationTargetException) {
            } catch (IllegalAccessException IllegalAccessException) {
            } catch (NoSuchMethodException NoSuchMethodException) {
                NoSuchMethodException.printStackTrace();
            }

            String str = getString(R.string.title_wifi_could_not_connect, new Object[]{declaredMethod});
            Log.d(TAG, str);
        }
    }
}

