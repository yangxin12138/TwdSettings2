package com.twd.setting.module.network.util;

import androidx.fragment.app.Fragment;

public abstract interface State {
    public abstract Fragment getFragment();

    public abstract void processBackward();

    public abstract void processForward();

    public static abstract interface FragmentChangeListener {
        public abstract void onFragmentChange(Fragment paramFragment, boolean paramBoolean);
    }

    public static abstract interface StateCompleteListener {
        public abstract void onComplete(int paramInt);
    }
}

