package com.twd.setting.module.network.wifi;

import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.twd.setting.base.BaseActivity;
import com.twd.setting.module.network.setup.AddWifiNetworkState;
import com.twd.setting.module.network.setup.ConnectAuthFailureState;
import com.twd.setting.module.network.setup.ConnectFailedState;
import com.twd.setting.module.network.setup.ConnectRejectedByApState;
import com.twd.setting.module.network.setup.ConnectState;
import com.twd.setting.module.network.setup.ConnectTimeOutState;
import com.twd.setting.module.network.setup.FinishState;
import com.twd.setting.module.network.setup.OptionsOrConnectState;
import com.twd.setting.module.network.setup.SuccessState;
import com.twd.setting.module.network.setup.UserChoiceInfo;
import com.twd.setting.module.network.util.State;
import com.twd.setting.module.network.util.State.FragmentChangeListener;
import com.twd.setting.module.network.util.StateMachine;
import com.twd.setting.module.network.util.StateMachine.Callback;

public class AddWifiNetworkActivity
        extends BaseActivity
        implements State.FragmentChangeListener {
    private static final String LOG_TAG = "AddWifiNetworkActivity";
    private State mAddWifiNetworkState;
    private State mConnectAuthFailureState;
    private State mConnectFailedState;
    private State mConnectRejectedByApState;
    private State mConnectState;
    private State mConnectTimeOutState;
    private State mFinishState;
    private State mOptionsOrConnectState;
    private StateMachine mStateMachine;
    private final StateMachine.Callback mStateMachineCallback = new Callback() {
        @Override
        public void onFinish(int paramInt) {
            Log.d(LOG_TAG,"onFinish "+paramInt);
            if (paramInt == 0) {
                //UiUtils.replaceFragment(getSupportFragmentManager(), 16908290, new WifiListFragment());
                WifiListFragment.clearSelectedSSID();
                //AddWifiNetworkActivity.super.onBackPressed();
                finish();
            }
        }
    };
    private State mSuccessState;

    private void updateView(Fragment paramFragment, boolean paramBoolean) {
        if (paramFragment != null) {
            FragmentTransaction localFragmentTransaction = getSupportFragmentManager().beginTransaction();
            if (paramBoolean) {
                localFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            } else {
                localFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            }
            localFragmentTransaction.replace(16908290, paramFragment, LOG_TAG);
            localFragmentTransaction.commit();
        }
    }

    public void onBackPressed() {
        mStateMachine.back();
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        mStateMachine = (StateMachine) new ViewModelProvider(this).get(StateMachine.class);
        mStateMachine.setCallback(mStateMachineCallback);
        ((UserChoiceInfo) new ViewModelProvider(this).get(UserChoiceInfo.class)).getWifiConfiguration().hiddenSSID = true;
        mAddWifiNetworkState = new AddWifiNetworkState(this);
        mConnectState = new ConnectState(this);
        mSuccessState = new SuccessState(this);
        mOptionsOrConnectState = new OptionsOrConnectState(this);
        mConnectTimeOutState = new ConnectTimeOutState(this);
        mConnectRejectedByApState = new ConnectRejectedByApState(this);
        mConnectFailedState = new ConnectFailedState(this);
        mConnectAuthFailureState = new ConnectAuthFailureState(this);
        mFinishState = new FinishState(this);
        mStateMachine.addState(mAddWifiNetworkState, 18, mOptionsOrConnectState);
        mStateMachine.addState(mAddWifiNetworkState, 1, mFinishState);
        mStateMachine.addState(mOptionsOrConnectState, 5, mConnectState);
        mStateMachine.addState(mConnectState, 10, mConnectRejectedByApState);
        mStateMachine.addState(mConnectState, 11, mConnectFailedState);
        mStateMachine.addState(mConnectState, 12, mConnectTimeOutState);
        mStateMachine.addState(mConnectState, 13, mConnectAuthFailureState);
        mStateMachine.addState(mConnectState, 14, mSuccessState);
        mStateMachine.addState(mConnectFailedState, 16, mOptionsOrConnectState);
        mStateMachine.addState(mConnectFailedState, 6, mFinishState);
        mStateMachine.addState(mConnectTimeOutState, 16, mOptionsOrConnectState);
        mStateMachine.addState(mConnectTimeOutState, 6, mFinishState);
        mStateMachine.addState(mConnectRejectedByApState, 16, mOptionsOrConnectState);
        mStateMachine.addState(mConnectRejectedByApState, 6, mFinishState);
        mStateMachine.addState(mConnectAuthFailureState, 6, mFinishState);
        mStateMachine.addState(mConnectRejectedByApState, 6, mFinishState);
        mStateMachine.setStartState(mAddWifiNetworkState);
        mStateMachine.start(true);
    }

    public void onFragmentChange(Fragment paramFragment, boolean paramBoolean) {
        updateView(paramFragment, paramBoolean);
    }
}
