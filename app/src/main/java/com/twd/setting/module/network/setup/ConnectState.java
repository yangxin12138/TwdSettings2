package com.twd.setting.module.network.setup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.IpConfiguration;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
//import com.twd.setting.SettingConfig;
import com.twd.setting.R;
import com.twd.setting.module.network.model.WifiAccessPoint;
import com.twd.setting.module.network.repository.ConnectivityListener;
import com.twd.setting.module.network.repository.ConnectivityListener.WifiNetworkListener;
import com.twd.setting.module.network.util.ReflectHelper;
import com.twd.setting.module.network.util.State;
import com.twd.setting.module.network.util.State.FragmentChangeListener;
import com.twd.setting.module.network.util.State.StateCompleteListener;
import com.twd.setting.module.network.util.StateMachine;
import com.twd.setting.module.network.util.WifiConfigHelper;
import com.twd.setting.utils.HLog;
import com.twd.setting.widgets.ToastTools;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class ConnectState
        implements State {
    private static final String TAG = "ConnectState";
    private final FragmentActivity mActivity;
    private Fragment mFragment;

    public ConnectState(FragmentActivity paramFragmentActivity) {
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
        WifiConfiguration localWifiConfiguration = ((UserChoiceInfo) new ViewModelProvider(mActivity).get(UserChoiceInfo.class)).getWifiConfiguration();
        String title = "";
        try {
            title = (String) WifiConfiguration.class.getDeclaredMethod("getPrintableSsid", new Class[0]).invoke(localWifiConfiguration, new Object[0]);
            Log.d(TAG,"getPrintableSsid: "+title);
        } catch (InvocationTargetException invocationTargetException1) {
        } catch (IllegalAccessException illegalAccessException1) {
        } catch (NoSuchMethodException noSuchMethodException1) {
            noSuchMethodException1.printStackTrace();
            title = "";
        }


        Log.d(TAG,"new ConnectToWifiFragment");
        mFragment = ConnectToWifiFragment.newInstance(mActivity.getString(R.string.wifi_connecting, title), true);
        if (!WifiConfigHelper.isNetworkSaved(localWifiConfiguration)) {
            Log.d(TAG,"isNetworkSaved: false ,localWifiConfiguration:"+localWifiConfiguration);
            Log.d(TAG,"new AdvancedOptionsFlowInfo");
            AdvancedOptionsFlowInfo advancedOptionsFlowInfo =  new ViewModelProvider(mActivity).get(AdvancedOptionsFlowInfo.class);
            if (advancedOptionsFlowInfo.getIpConfiguration() != null) {
                try {
                    Log.d(TAG,"AdvancedOptionsFlowInfo setIpConfiguration");
                    if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        WifiConfiguration.class.getDeclaredMethod("setIpConfiguration", new Class[]{IpConfiguration.class}).invoke(localWifiConfiguration, new Object[]{advancedOptionsFlowInfo.getIpConfiguration()});
                    }
                } catch (InvocationTargetException invocationTargetException) {
                } catch (IllegalAccessException illegalAccessException) {
                } catch (NoSuchMethodException noSuchMethodException) {
                    noSuchMethodException.printStackTrace();
                }

            }
        }
        State.FragmentChangeListener localFragmentChangeListener = (State.FragmentChangeListener) this.mActivity;
        if (localFragmentChangeListener != null) {
            Log.d(TAG,"go ConnectToWifiFragment");
            localFragmentChangeListener.onFragmentChange(mFragment, true);
        }
    }

    public static class ConnectToWifiFragment
            extends MessageFragment
            implements ConnectivityListener.WifiNetworkListener {
        static final int CONNECTION_TIMEOUT = 15000;
        private static final boolean DEBUG = true;//SettingConfig.IS_DEBUG;
        private static final String TAG = "ConnectToWifiFragment";
        static final int MSG_TIMEOUT = 1;
        private boolean mConnected;
        private ConnectivityListener mConnectivityListener;
        Handler mHandler;
        private BroadcastReceiver mReceiver;
        StateMachine mStateMachine;
        private boolean mWasAssociated;
        private boolean mWasAssociating;
        private boolean mWasHandshaking;
        WifiConfiguration mWifiConfiguration;
        WifiManager mWifiManager;

        private void connect() {
            Log.d(TAG,"connect");
            if (isNetworkConnected()) {
                mConnected = true;
                notifyListener(StateMachine.RESULT_SUCCESS);//14
                return;
            }
            Log.d(TAG,"addNetwork  SSID:"+mWifiConfiguration.SSID+", preSharedKey: "+mWifiConfiguration.preSharedKey);
            Log.d(TAG,"addNetwork "+((mWifiConfiguration==null)?"null":mWifiConfiguration));
            int i = mWifiManager.addNetwork(mWifiConfiguration);
            Log.d(TAG,"addNetwork ret:"+i);
            if (i == -1) {
                if (DEBUG) {
                    Log.d(TAG, "Failed to add network!");
                }
                notifyListener(StateMachine.RESULT_UNKNOWN_ERROR);//11
                return;
            }
            if (!mWifiManager.enableNetwork(i, true)) {
                if (DEBUG) {
                    Log.d(TAG, "Failed to enable network id " + i);
                }
                notifyListener(StateMachine.RESULT_UNKNOWN_ERROR);//11
                return;
            }
            if (!mWifiManager.reconnect()) {
                if (DEBUG) {
                    Log.d(TAG, "Failed to reconnect!");
                }
                notifyListener(StateMachine.RESULT_UNKNOWN_ERROR);//11
            }
        }

        private void inferConnectionStatus(WifiAccessPoint paramWifiAccessPoint) {
            Log.d(TAG, "inferConnectionStatus" + paramWifiAccessPoint);
            WifiConfiguration wifiConfiguration = paramWifiAccessPoint.getConfig();
            if (wifiConfiguration == null) {
                return;
            }
            Class localClass = ReflectHelper.getClass("android.net.wifi.WifiConfiguration$NetworkSelectionStatus");
            if (localClass == null) {
                return;
            }
            int authentication_failure = ReflectHelper.getIntConstant("android.net.wifi.WifiConfiguration$NetworkSelectionStatus", "DISABLED_AUTHENTICATION_FAILURE");
            int wrong_password = ReflectHelper.getIntConstant("android.net.wifi.WifiConfiguration$NetworkSelectionStatus", "DISABLED_BY_WRONG_PASSWORD");
            int dhcp_failure = ReflectHelper.getIntConstant("android.net.wifi.WifiConfiguration$NetworkSelectionStatus", "DISABLED_DHCP_FAILURE");
            int dns_failure = ReflectHelper.getIntConstant("android.net.wifi.WifiConfiguration$NetworkSelectionStatus", "DISABLED_DNS_FAILURE");
            int association_rejection = ReflectHelper.getIntConstant("android.net.wifi.WifiConfiguration$NetworkSelectionStatus", "DISABLED_ASSOCIATION_REJECTION");

            int i1;
            try {
                Object objects = WifiConfiguration.class.getDeclaredMethod("getNetworkSelectionStatus", new Class[0]).invoke(wifiConfiguration, new Object[0]);
                if (Objects.equals(localClass.getDeclaredMethod("isNetworkEnabled", new Class[0]).invoke(objects, new Object[0]), Boolean.valueOf(true))) {
                    if (!isNetworkConnected()) {
                        return;
                    }
                    notifyListener(StateMachine.RESULT_SUCCESS);//14
                    return;
                }
                i1 = Integer.parseInt(String.valueOf(localClass.getDeclaredMethod("getNetworkSelectionDisableReason", new Class[0]).invoke(objects, new Object[0])));
                if (i1 == authentication_failure) {
                    notifyListener(StateMachine.RESULT_BAD_AUTH);//13
                } else if (i1 == wrong_password) {
                    notifyListener(StateMachine.RESULT_UNKNOWN_ERROR);//11
                } else if (i1 == dhcp_failure) {
                    notifyListener(StateMachine.RESULT_UNKNOWN_ERROR);//11
                } else if (i1 == association_rejection) {
                    notifyListener(StateMachine.RESULT_REJECTED_BY_AP);//10
                }

                paramWifiAccessPoint.clearConfig();

            } catch (IllegalAccessException illegalAccessException) {
            } catch (InvocationTargetException invocationTargetException) {
            } catch (NoSuchMethodException noSuchMethodException) {
                noSuchMethodException.printStackTrace();
            }


        }

        private boolean isNetworkConnected() {
            NetworkInfo networkInfo = ((ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            if (networkInfo == null) {
                if (DEBUG) {
                    Log.d(TAG, "NetworkInfo is null; network is not connected");
                }
                return false;
            }
            Log.d(TAG, "NetworkInfo: " + networkInfo.toString());

            if ((networkInfo.isConnected()) && (networkInfo.getType() == 1)) {
                WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
                if (wifiInfo == null) {
                    Log.d(TAG, "Connected to nothing");
                } else {
                    Log.d(TAG, "Connected to " + wifiInfo.getSSID());
                }

                if ((wifiInfo != null) && (wifiInfo.getSSID().equals(mWifiConfiguration.SSID))) {
                    return true;
                }
            } else {
                Log.d(TAG, "Network is not connected");
            }
            return false;
        }

        public static ConnectToWifiFragment newInstance(String paramString, boolean paramBoolean) {
            Log.d(TAG,"newInstance :title:" +paramString+",show_progress_indicator:"+paramBoolean);
            ConnectToWifiFragment connectToWifiFragment = new ConnectToWifiFragment();
            Bundle localBundle = new Bundle();
            addArguments(localBundle, paramString, paramBoolean);
            connectToWifiFragment.setArguments(localBundle);
            return connectToWifiFragment;
        }

        private void notifyListener(int paramInt) {
            if ((mStateMachine.getCurrentState() instanceof ConnectState)) {
                Log.d(TAG,"notifyListener: "+paramInt);
                mStateMachine.getListener().onComplete(paramInt);
            }
        }

        private void postTimeout() {
            mHandler.removeMessages(1);
            mHandler.sendEmptyMessageDelayed(1, 15000L);
        }

        private void registerConnectResult() {
            Log.d(TAG, "registerConnectResult");
            IntentFilter localIntentFilter = new IntentFilter();
            localIntentFilter.addAction("android.net.wifi.supplicant.STATE_CHANGE");
            mReceiver = new BroadcastReceiver() {
                public void onReceive(Context mContext, Intent intent) {
                    if ("android.net.wifi.supplicant.STATE_CHANGE".equals(intent.getAction())) {
                        SupplicantState supplicantState =  intent.getParcelableExtra("newState");

                        Log.d(TAG, "Got supplicant state: " + supplicantState.name()+", ordinal:"+supplicantState.ordinal());

                        int j = 10;
                        //            int j = ConnectState
                        //            .1.$SwitchMap$android$net$wifi$SupplicantState[supplicantState.ordinal()];
                        int state = supplicantState.ordinal();
                        int i = StateMachine.RESULT_UNKNOWN_ERROR;//11;
                        Log.d(TAG, "registerConnectResult state: "+state);
                        switch (state) {
                            default:
                                return;
                            case 10:
                                if ((!mWasAssociating) || (mWasAssociated)) {
                                    return;
                                }
                                ConnectState.ConnectToWifiFragment.this.notifyListener(StateMachine.RESULT_REJECTED_BY_AP);//10
                                break;
                            case 8:
                            case 9:
                                //ConnectState.ConnectToWifiFragment.access$502(ConnectState.ConnectToWifiFragment.this, true);
                                break;
                            case 6:
                            case 7:
                                ConnectState.ConnectToWifiFragment.this.notifyListener(StateMachine.RESULT_UNKNOWN_ERROR);//11
                                break;
                            case 4:
                            case 5:
                                if ((mWasAssociated) || (mWasHandshaking)) {

                                    if (mWasHandshaking) {
                                        i = StateMachine.RESULT_BAD_AUTH;//13;
                                    }
                                    ConnectState.ConnectToWifiFragment.this.notifyListener(i);
                                }
                                break;
                            case 2:
                                // ConnectState.ConnectToWifiFragment.access$402(ConnectState.ConnectToWifiFragment.this, true);
                                break;
                            case 1:
                                // ConnectState.ConnectToWifiFragment.access$302(ConnectState.ConnectToWifiFragment.this, true);
                                break;
                        }
                        mHandler.removeMessages(1);
                        mHandler.sendEmptyMessageDelayed(1, 15000L);
                    }
                }
            };
            requireActivity().registerReceiver(mReceiver, localIntentFilter);
        }

        private void unregisterConnectResult() {
            if (mReceiver != null) {
                requireActivity().unregisterReceiver(mReceiver);
            }
        }

        public void onCreate(Bundle paramBundle) {
            super.onCreate(paramBundle);
            Log.d(TAG,"onCreate");
            if (Build.VERSION.SDK_INT < 21) {
                ToastTools.Instance().showToast(requireContext(), "安卓版本低于5.0");
                requireActivity().finish();
                return;
            }
            if (Build.VERSION.SDK_INT < 24) {
                //this.mConnectivityListener = new ConnectivityListener(getActivity(), new ConnectState.ConnectToWifiFragment..ExternalSyntheticLambda0(this));
                mConnectivityListener = new ConnectivityListener(getActivity(), new ConnectivityListener.Listener() {
                    @Override
                    public void onConnectivityChange() {
                        Log.d(TAG, "onCreate onConnectivityChange ");
                        //connect();
                        //inferConnectionStatus();
                        proceedDependOnNetworkState();
                    }
                });
                registerConnectResult();
            } else {
                if (Build.VERSION.SDK_INT > 29) {
                    ToastTools.Instance().showToast(requireContext(), "安卓版本高于10.0");
					requireActivity().finish();
					return;
                }
                mConnectivityListener = new ConnectivityListener(getActivity(),  new ConnectivityListener.Listener() {
                    @Override
                    public void onConnectivityChange() {
                        Log.d(TAG, "onCreate onConnectivityChange ");
                        onWifiListChanged();
                    }
                });

            }
            mConnectivityListener.start();
            mWifiConfiguration = ((UserChoiceInfo) new ViewModelProvider(requireActivity()).get(UserChoiceInfo.class)).getWifiConfiguration();
            mStateMachine = ((StateMachine) new ViewModelProvider(requireActivity()).get(StateMachine.class));
            mWifiManager = ((WifiManager) requireActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE));
            mHandler = new MessageHandler(this);
            mConnectivityListener.setWifiListener(this);

        }

        public void onDestroy() {
            if (!isNetworkConnected()) {
                mWifiManager.disconnect();
            }
            mConnectivityListener.stop();
            mConnectivityListener.destroy();
            mHandler.removeMessages(1);
            if ((Build.VERSION.SDK_INT >= 21) && (Build.VERSION.SDK_INT <= 24)) {
                unregisterConnectResult();
            }
            super.onDestroy();
        }


        public void onResume() {
            super.onResume();
            postTimeout();
            proceedDependOnNetworkState();
        }

        public void onWifiListChanged() {
            Log.d(TAG,"onWifiListChanged");
            if (Build.VERSION.SDK_INT >= 24) {
                if (Build.VERSION.SDK_INT > 28) {
                    return;
                }
                Object localObject = mConnectivityListener.getAvailableNetworks();
                if (localObject != null) {
                    localObject = ((List) localObject).iterator();
                    while (((Iterator) localObject).hasNext()) {
                        WifiAccessPoint localWifiAccessPoint = (WifiAccessPoint) ((Iterator) localObject).next();
                        if ((localWifiAccessPoint != null) && (WifiAccessPoint.convertToQuotedString(localWifiAccessPoint.getSsidStr()).equals(this.mWifiConfiguration.SSID))) {
                            inferConnectionStatus(localWifiAccessPoint);
                        }
                    }
                }
            }
        }

        void proceedDependOnNetworkState() {
            Log.d(TAG,"proceedDependOnNetworkState");
            if (Build.VERSION.SDK_INT < 21) {
                ToastTools.Instance().showToast(requireContext(), "系统版本低于 5.0");
                return;
            }
            if (Build.VERSION.SDK_INT < 24) {
                connect();
                return;
            }
            if (Build.VERSION.SDK_INT <= 29) {
                Log.d("ConnectToWifiFragment", "--> ConnectState$proceedDependOnNetworkState connect to wifi");
                if (isNetworkConnected()) {
                    mWifiManager.disconnect();
                }
                mWifiManager.addNetwork(mWifiConfiguration);
                try {
                    WifiManager.class.getDeclaredMethod("connect", new Class[]{WifiConfiguration.class, Class.forName("android.net.wifi.WifiManager$ActionListener")}).invoke(this.mWifiManager, new Object[]{this.mWifiConfiguration, null});

                } catch (InvocationTargetException localInvocationTargetException) {
                } catch (ClassNotFoundException localClassNotFoundException) {
                } catch (NoSuchMethodException localNoSuchMethodException) {
                } catch (IllegalAccessException localIllegalAccessException) {
                    ToastTools.Instance().showToast(requireContext(), "连接失败");
                    Log.d("ConnectToWifiFragment", "method WifiManager.connect(WifiConfiguration, WifiManager.ActionListener) exception");
                    localIllegalAccessException.printStackTrace();
                }

            }
        }

        private static class MessageHandler
                extends Handler {
            private final WeakReference<ConnectState.ConnectToWifiFragment> mFragmentRef;

            MessageHandler(ConnectState.ConnectToWifiFragment paramConnectToWifiFragment) {
                mFragmentRef = new WeakReference(paramConnectToWifiFragment);
            }

            public void handleMessage(Message paramMessage) {
                if (ConnectState.ConnectToWifiFragment.DEBUG) {
                    Log.d("ConnectToWifiFragment", "Timeout waiting on supplicant state change");
                }
                ConnectToWifiFragment connectToWifiFragment = (ConnectState.ConnectToWifiFragment) this.mFragmentRef.get();
                if (connectToWifiFragment == null) {
                    return;
                }
                if (connectToWifiFragment.isNetworkConnected()) {
                    if (ConnectState.ConnectToWifiFragment.DEBUG) {
                        Log.d("ConnectToWifiFragment", "Fake timeout; we're actually connected");
                    }
                    connectToWifiFragment.notifyListener(StateMachine.RESULT_SUCCESS);
                } else {
                    if (ConnectState.ConnectToWifiFragment.DEBUG) {
                        Log.d("ConnectToWifiFragment", "Timeout is real; telling the listener");
                    }
                    connectToWifiFragment.notifyListener(StateMachine.RESULT_TIMEOUT);
                }
                connectToWifiFragment.notifyListener(StateMachine.RESULT_TIMEOUT);
            }
        }
    }
}
