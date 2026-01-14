package com.twd.setting.module.network.setup;

import android.net.wifi.WifiConfiguration;
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ConnectTimeOutState
        implements State {
    private static final String TAG = "ConnectTimeOutState";
    private final FragmentActivity mActivity;
    private Fragment mFragment;

    public ConnectTimeOutState(FragmentActivity paramFragmentActivity) {
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
        Log.d("yangxin", "processForward: 超时的执行前");
        Object localObject3 = (AdvancedOptionsFlowInfo) new ViewModelProvider(mActivity).get(AdvancedOptionsFlowInfo.class);
        Object localObject1 = (UserChoiceInfo) new ViewModelProvider(mActivity).get(UserChoiceInfo.class);
        ((AdvancedOptionsFlowInfo) localObject3).setCanStart(true);
        try {
            localObject1 = (String) WifiConfiguration.class.getDeclaredMethod("getPrintableSsid", new Class[0]).invoke(((UserChoiceInfo) localObject1).getWifiConfiguration(), new Object[0]);
        } catch (InvocationTargetException invocationTargetException) {
        } catch (IllegalAccessException illegalAccessException) {
        } catch (NoSuchMethodException noSuchMethodException) {
            noSuchMethodException.printStackTrace();
        }

        Object localObject2 = "";
        ((AdvancedOptionsFlowInfo) localObject3).setPrintableSsid((String) localObject2);
        localObject2 = new ConnectTimeOutFragment();
        this.mFragment = ((Fragment) localObject2);
        State.FragmentChangeListener  state_listener = (State.FragmentChangeListener) mActivity;
        if (state_listener != null) {
            state_listener.onFragmentChange((Fragment) localObject2, true);
        }
    }

    public static class ConnectTimeOutFragment
            extends Fragment {
        private StateMachine mStateMachine;

        public void onCreate(Bundle paramBundle) {
            this.mStateMachine = ((StateMachine) new ViewModelProvider(requireActivity()).get(StateMachine.class));
            super.onCreate(paramBundle);
        }

        public void onResume() {
            super.onResume();
            Log.d("yangxin", "processForward: 超时的执行前——————这里没显示Toast");
            ToastTools.Instance().showToast(requireContext(),getString(R.string.net_wifiList_connecttoWifi_isConnected_false));
            this.mStateMachine.getListener().onComplete(6);
        }
    }
}
