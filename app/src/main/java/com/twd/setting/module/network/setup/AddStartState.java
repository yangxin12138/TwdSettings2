package com.twd.setting.module.network.setup;

import android.net.wifi.WifiConfiguration;
import android.text.TextUtils;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.twd.setting.module.network.util.State;
import com.twd.setting.module.network.util.State.StateCompleteListener;
import com.twd.setting.module.network.util.StateMachine;
import com.twd.setting.module.network.util.WifiSecurityUtil;

public class AddStartState
        implements State {
    private static final String TAG = "AddStartState";
    private final FragmentActivity mActivity;
    private Fragment mFragment;
    private final StateMachine mStateMachine;
    private final UserChoiceInfo mUserChoiceInfo;

    public AddStartState(FragmentActivity paramFragmentActivity) {
        mActivity = paramFragmentActivity;
        mStateMachine = ((StateMachine) new ViewModelProvider(paramFragmentActivity).get(StateMachine.class));
        mUserChoiceInfo = ((UserChoiceInfo) new ViewModelProvider(paramFragmentActivity).get(UserChoiceInfo.class));
    }

    public Fragment getFragment() {
        return mFragment;
    }

    public void processBackward() {
        Log.d(TAG,"processBackward");
        mStateMachine.back();
    }

    public void processForward() {
        Log.d(TAG,"processForward");
        mFragment = null;
        int i = mUserChoiceInfo.getWifiSecurity();
        WifiConfiguration localWifiConfiguration = mUserChoiceInfo.getWifiConfiguration();
        if (((i == 1) && (TextUtils.isEmpty(localWifiConfiguration.wepKeys[0]))) || ((!WifiSecurityUtil.isOpen(i)) && (TextUtils.isEmpty(localWifiConfiguration.preSharedKey)))) {
            Log.d(TAG,"StateMachine PASSWORD");
            mStateMachine.getListener().onComplete(StateMachine.PASSWORD);//7
            return;
        }
        Log.d(TAG,"StateMachine CONNECT");
        mStateMachine.getListener().onComplete(StateMachine.CONNECT);//5
    }
}
