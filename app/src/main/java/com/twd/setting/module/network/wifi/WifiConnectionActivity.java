package com.twd.setting.module.network.wifi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.twd.setting.R;
import com.twd.setting.base.BaseActivity;
import com.twd.setting.module.network.model.WifiAccessPoint;
import com.twd.setting.module.network.setup.AddStartState;
import com.twd.setting.module.network.setup.ConnectAuthFailureState;
import com.twd.setting.module.network.setup.ConnectFailedState;
import com.twd.setting.module.network.setup.ConnectRejectedByApState;
import com.twd.setting.module.network.setup.ConnectState;
import com.twd.setting.module.network.setup.ConnectTimeOutState;
import com.twd.setting.module.network.setup.EnterPasswordState;
import com.twd.setting.module.network.setup.FinishState;
import com.twd.setting.module.network.setup.KnownNetworkState;
import com.twd.setting.module.network.setup.OptionsOrConnectState;
import com.twd.setting.module.network.setup.SuccessState;
import com.twd.setting.module.network.setup.UserChoiceInfo;
import com.twd.setting.module.network.util.ReflectHelper;
import com.twd.setting.module.network.util.State;
import com.twd.setting.module.network.util.State.FragmentChangeListener;
import com.twd.setting.module.network.util.StateMachine;
import com.twd.setting.module.network.util.StateMachine.Callback;
import com.twd.setting.module.network.util.WifiConfigHelper;
import com.twd.setting.module.network.util.WifiSecurityUtil;
import com.twd.setting.utils.HLog;
import com.twd.setting.utils.UiUtils;
import com.twd.setting.widgets.ToastTools;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class WifiConnectionActivity
        extends BaseActivity
        implements State.FragmentChangeListener {
    private static final String EXTRA_WIFI_SECURITY_NAME = "wifi_security_name";
    private static final String EXTRA_WIFI_SSID = "wifi_ssid";
    static final String TAG = "WifiConnectionActivity";
    private State mAddStartState;
    private WifiConfiguration mConfiguration;
    private State mConnectAuthFailureState;
    private State mConnectFailedState;
    private State mConnectRejectedByApState;
    private State mConnectState;
    private State mConnectTimeOutState;
    private State mEnterPasswordState;
    private State mFinishState;
    private State mKnownNetworkState;
    private State mOptionsOrConnectState;
    private StateMachine mStateMachine;
    private final StateMachine.Callback mStateMachineCallback = new Callback() {
        @Override
        public void onFinish(int paramInt) {
            Log.d(TAG, "StateMachine.Callback  onFinish: "+paramInt);
            if (paramInt == 0  || paramInt == -1) {
                //UiUtils.replaceFragment(getSupportFragmentManager(), 16908290, new WifiListFragment());
                WifiListFragment.clearSelectedSSID();
                finish();

            }
            //    mStateMachine.getListener().onComplete(-1);
            //WifiListFragment.clearSelectedSSID();
            //mStateMachine.getListener().onComplete(StateMachine.SELECT_WIFI);//6
        }
    };
    private State mSuccessState;
    private int mWifiSecurity;

    public static Intent createIntent(Context paramContext, WifiConfiguration paramWifiConfiguration) {
        int i = WifiSecurityUtil.getSecurity(paramWifiConfiguration);
        String ssid ="";
        try {
            ssid = (String) WifiConfiguration.class.getDeclaredMethod("getPrintableSsid", new Class[0]).invoke(paramWifiConfiguration, new Object[0]);
        } catch (InvocationTargetException invocationTargetException) {
        } catch (IllegalAccessException illegalAccessException) {
        } catch (NoSuchMethodException noSuchMethodException) {
            noSuchMethodException.printStackTrace();
        }
        return new Intent(paramContext, WifiConnectionActivity.class).putExtra(EXTRA_WIFI_SSID, ssid).putExtra(EXTRA_WIFI_SECURITY_NAME, i);
    }

    public static Intent createIntent(Context paramContext, WifiAccessPoint paramWifiAccessPoint) {
        return createIntent(paramContext, paramWifiAccessPoint, paramWifiAccessPoint.getSecurity());
    }

    public static Intent createIntent(Context paramContext, WifiAccessPoint wifiAccessPoint, int security) {
        Log.d(TAG, "new Intent  security:" + security + ",ssid:" + wifiAccessPoint.getSsidStr());
        return new Intent(paramContext, WifiConnectionActivity.class).putExtra(EXTRA_WIFI_SSID, wifiAccessPoint.getSsidStr()).putExtra(EXTRA_WIFI_SECURITY_NAME, security);
    }

    @SuppressLint("ResourceType")
    private void updateView(Fragment fragment, boolean open) {
        Log.d(TAG, "updateView " + fragment + ", " + open);
        if (fragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            int i;
            if (open) {
                i = FragmentTransaction.TRANSIT_FRAGMENT_OPEN;//4097;
            } else {
                i = FragmentTransaction.TRANSIT_FRAGMENT_CLOSE;//8194;
            }
            fragmentTransaction.setTransition(i).replace(16908290, fragment, "WifiConnectionActivity").commit();
        }
    }

    @Override
    public void onBackPressed() {
        if ((mStateMachine.getCurrentState() instanceof ConnectState)) {
            return;
        }
        mStateMachine.back();
    }

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        Log.d(TAG, "onCreate");
        mStateMachine = (StateMachine) new ViewModelProvider(this).get(StateMachine.class);
        Log.d(TAG, "onCreate  000");
        mStateMachine.setCallback(mStateMachineCallback);
        mKnownNetworkState = new KnownNetworkState(this);
        mEnterPasswordState = new EnterPasswordState(this);
        mConnectState = new ConnectState(this);
        mConnectTimeOutState = new ConnectTimeOutState(this);
        mConnectRejectedByApState = new ConnectRejectedByApState(this);
        mConnectFailedState = new ConnectFailedState(this);
        mConnectAuthFailureState = new ConnectAuthFailureState(this);
        Log.d(TAG, "onCreate  1111");
        mSuccessState = new SuccessState(this);
        mOptionsOrConnectState = new OptionsOrConnectState(this);
        mAddStartState = new AddStartState(this);
        mFinishState = new FinishState(this);
        Log.d(TAG, "onCreate  2222");
        mStateMachine.addState(mKnownNetworkState, 0, mAddStartState);
        mStateMachine.addState(mKnownNetworkState, 6, mFinishState);
        mStateMachine.addState(mAddStartState, 7, mEnterPasswordState);
        mStateMachine.addState(mAddStartState, 5, mConnectState);
        mStateMachine.addState(mEnterPasswordState, 18, mOptionsOrConnectState);
        mStateMachine.addState(mOptionsOrConnectState, 5, mConnectState);
        mStateMachine.addState(mConnectState, 10, mConnectRejectedByApState);
        mStateMachine.addState(mConnectState, 11, mConnectFailedState);
        mStateMachine.addState(mConnectState, 12, mConnectTimeOutState);
        Log.d(TAG, "onCreate  3333");
        mStateMachine.addState(mConnectState, 13, mConnectAuthFailureState);
        mStateMachine.addState(mConnectState, 14, mSuccessState);
        mStateMachine.addState(mConnectFailedState, 6, mFinishState);
        mStateMachine.addState(mConnectTimeOutState, 6, mFinishState);
        mStateMachine.addState(mConnectRejectedByApState, 6, mFinishState);
        mStateMachine.addState(mConnectAuthFailureState, 6, mFinishState);
        Log.d(TAG, "onCreate  4444");
        mWifiSecurity = getIntent().getIntExtra(EXTRA_WIFI_SECURITY_NAME, 0);
        Log.d(TAG, "onCreate  mWifiSecurity" + mWifiSecurity);
        mConfiguration = WifiConfigHelper.getConfiguration(this, getIntent().getStringExtra(EXTRA_WIFI_SSID), mWifiSecurity);
        Log.d(TAG, "onCreate  mConfiguration" + mConfiguration);
        UserChoiceInfo userChoiceInfo = (UserChoiceInfo) new ViewModelProvider(this).get(UserChoiceInfo.class);
        userChoiceInfo.setWifiConfiguration(mConfiguration);
        userChoiceInfo.setWifiSecurity(mWifiSecurity);
        Log.d(TAG, "onCreate  555");
        if (Build.VERSION.SDK_INT >= 24) {
            Log.d(TAG, "onCreate 1111");
            Class mClass = ReflectHelper.getClass("android.net.wifi.WifiConfiguration$NetworkSelectionStatus");
            if (mClass == null) {
                return;
            }
            Log.d(TAG, "onCreate 2222");
            int i = ReflectHelper.getIntConstant("android.net.wifi.WifiConfiguration$NetworkSelectionStatus", "DISABLED_BY_WRONG_PASSWORD");
            try {
                Object localObject = WifiConfiguration.class.getDeclaredMethod("getNetworkSelectionStatus", new Class[0]).invoke(mConfiguration, new Object[0]);
                if (Integer.parseInt(String.valueOf(mClass.getDeclaredMethod("getNetworkSelectionDisableReason", new Class[0]).invoke(localObject, new Object[0]))) != i) {
                    if (WifiConfigHelper.isNetworkSaved(mConfiguration)) {
                        mStateMachine.setStartState(mKnownNetworkState);
                    } else {
                        mStateMachine.setStartState(mAddStartState);
                    }
                    mStateMachine.start(true);
                    return;
                }else {
                    ToastTools.Instance().showToast(this, getString(R.string.add_wifi_error_passwd));
                    mStateMachine.setStartState(mEnterPasswordState);
                    mStateMachine.start(true);
                    return;
                }
            } catch (IllegalAccessException illegalAccessException) {
            } catch (InvocationTargetException invocationTargetException) {
            } catch (NoSuchMethodException noSuchMethodException) {
                noSuchMethodException.printStackTrace();
            } finally {
                Log.d(TAG, "StateMachine 启动异常，原因：方法getNetworkSelectionStatus、getNetworkSelectionDisableReason可能调用异常");
            }
        } else {

            if (WifiConfigHelper.isNetworkSaved(mConfiguration)) {
                Log.d(TAG, "onCreate knownNetwork");
                mStateMachine.setStartState(mKnownNetworkState);
            } else {
                Log.d(TAG, "onCreate not knownNetwork");
                mStateMachine.setStartState(mAddStartState);
            }
            mStateMachine.start(true);
        }
        Log.d(TAG, "onCreate  end");

    }

    @Override
    public void onFragmentChange(Fragment paramFragment, boolean paramBoolean) {
        updateView(paramFragment, paramBoolean);
    }
}
