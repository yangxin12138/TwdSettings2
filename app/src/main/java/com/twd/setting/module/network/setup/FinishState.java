package com.twd.setting.module.network.setup;

import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.twd.setting.module.network.util.State;
import com.twd.setting.module.network.util.State.StateCompleteListener;
import com.twd.setting.module.network.util.StateMachine;

public class FinishState
        implements State {
    private final FragmentActivity mActivity;
    private Fragment mFragment;

    public FinishState(FragmentActivity paramFragmentActivity) {
        mActivity = paramFragmentActivity;
    }

    public Fragment getFragment() {
        return mFragment;
    }

    public void processBackward() {
        Log.d("FinishState","processBackward");
        mFragment = null;
        ((StateMachine) new ViewModelProvider(mActivity).get(StateMachine.class)).back();
    }

    public void processForward() {
        Log.d("FinishState","processForward");
        mFragment = null;
        ((StateMachine) new ViewModelProvider(mActivity).get(StateMachine.class)).getListener().onComplete(StateMachine.CONTINUE);//
    }
}
