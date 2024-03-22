package com.twd.setting.module.network.setup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.twd.setting.R;
import com.twd.setting.databinding.FragmentGuideStepBinding;
import com.twd.setting.module.network.util.State;
import com.twd.setting.module.network.util.State.FragmentChangeListener;
import com.twd.setting.module.network.util.StateMachine;
import com.twd.setting.module.network.util.WifiConfigHelper;
import com.twd.setting.module.network.wifi.WifiListFragment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

public class KnownNetworkState
        implements State {
    private static final String TAG = "KnownNetworkState";
    private final FragmentActivity mActivity;
    private Fragment mFragment;

    public KnownNetworkState(FragmentActivity paramFragmentActivity) {
        mActivity = paramFragmentActivity;
    }

    public Fragment getFragment() {
        return mFragment;
    }

    public void processBackward() {
        Log.d(TAG,"processBackward");
        KnownNetworkFragment localKnownNetworkFragment = new KnownNetworkFragment(mActivity);
        mFragment = localKnownNetworkFragment;
        State.FragmentChangeListener localFragmentChangeListener = (State.FragmentChangeListener) mActivity;
        if (localFragmentChangeListener != null) {
            localFragmentChangeListener.onFragmentChange(localKnownNetworkFragment, false);
        }
    }

    public void processForward() {
        Log.d(TAG,"processForward");
        KnownNetworkFragment localKnownNetworkFragment = new KnownNetworkFragment(mActivity);
        mFragment = localKnownNetworkFragment;
        State.FragmentChangeListener localFragmentChangeListener = (State.FragmentChangeListener) mActivity;
        if (localFragmentChangeListener != null) {
            localFragmentChangeListener.onFragmentChange(localKnownNetworkFragment, true);
        }
    }

    public static class KnownNetworkFragment
            extends WifiConnectivityGuidedStepFragment {
        private StateMachine mStateMachine;
        private UserChoiceInfo mUserChoiceInfo;
        private Context mContext;
        SharedPreferences wifiInfoPreferences;
        FragmentActivity mActivity;

        public KnownNetworkFragment(FragmentActivity mActivity){
            this.mActivity = mActivity;
        }

        public void onCreate(Bundle paramBundle) {
            mContext = requireContext();
            mUserChoiceInfo = ((UserChoiceInfo) new ViewModelProvider(requireActivity()).get(UserChoiceInfo.class));
            mStateMachine = ((StateMachine) new ViewModelProvider(requireActivity()).get(StateMachine.class));
            super.onCreate(paramBundle);
        }
        public static boolean IsNullString(String str) {
            if (str != null && !TextUtils.isEmpty(str) && !TextUtils.equals("", str.trim())) {
                return false;
            } else {
                return true;
            }
        }

        @SuppressLint("MissingPermission")
        public void onViewCreated(View paramView, Bundle paramBundle) {
            Log.d("KnownNetworkFragment","onViewCreated");
            super.onViewCreated(paramView, paramBundle);
            String method_str = "";
            String ssid= "";
            String connected_ssid = "";
            boolean is_alive = false;
            try {
                ssid = (String) WifiConfiguration.class.getDeclaredMethod("getPrintableSsid", new Class[0]).invoke(mUserChoiceInfo.getWifiConfiguration(), new Object[0]);
            } catch (InvocationTargetException invocationTargetException) {
            } catch (IllegalAccessException illegalAccessException) {
            } catch (NoSuchMethodException noSuchMethodException) {
                noSuchMethodException.printStackTrace();
            }


            //WifiConfiguration mConfiguration = WifiConfigHelper.getConfiguration(requireContext(), ssid,mUserChoiceInfo.getWifiSecurity());
            WifiManager wifiManager = ((WifiManager)requireContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE));
            WifiInfo  wifiinfo = wifiManager.getConnectionInfo();
            if(!IsNullString(wifiinfo.getSSID())){
                connected_ssid = wifiinfo.getSSID();
                Log.e("lgq","---直接获取ssid:"+connected_ssid);
                if(connected_ssid.equals("\""+ssid+"\"")){
                    is_alive = true;
                    Log.e("lgq","---直接获取ssid:"+connected_ssid +", is alive:"+is_alive);
                }
            }else{
                int networkId = wifiinfo.getNetworkId();
                @SuppressLint("MissingPermission")
                List<WifiConfiguration> netConfList = wifiManager.getConfiguredNetworks();
                if(netConfList!=null){

                for (WifiConfiguration wificonf:netConfList){
                    if(wificonf.networkId == networkId){
                        Log.e("lgq","---间接获取ssid："+connected_ssid);
                    }
                }
                }
            }

            if(is_alive){
                method_str = getString(R.string.title_wifi_known_network_connect, new Object[]{ssid});
            }else {
                method_str = getString(R.string.title_wifi_known_network, new Object[]{ssid});
            }
            ((TextView) this.binding.customDialog.getRoot().findViewById(R.id.custom_dialog_title_tv_id)).setText(method_str);
            TextView tv_forget  = binding.customDialog.getRoot().findViewById(R.id.custom_dialog_cancle_tv_id);
            tv_forget .setText(getString(R.string.wifi_forget_network));
            //paramView.setOnClickListener(new KnownNetworkState.KnownNetworkFragment..ExternalSyntheticLambda0(this));
            String finalSsid = ssid;
            String wifiInfo_ssid = finalSsid;
            wifiInfoPreferences = requireContext().getSharedPreferences("wifi_info",Context.MODE_PRIVATE);
            String wifiInfo_password = wifiInfoPreferences.getString(wifiInfo_ssid,"000");
            Log.d(TAG, "onViewCreated: wifiInfo_password = " + wifiInfo_password+"wifiInfo_ssid = " + wifiInfo_ssid);
            Log.d(TAG,"SSID: "+finalSsid);
            tv_forget .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG,"ignore "+finalSsid);
                    WifiManager wifiManager = ((WifiManager)requireContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE));

                    Iterator iterator = wifiManager.getConfiguredNetworks().iterator();
                    while (iterator.hasNext()) {
                        WifiConfiguration wifiConfiguration = (WifiConfiguration) iterator.next();

                        Log.d(TAG, "removeWifiBySsid ssid=" + wifiConfiguration.SSID);
                        if (wifiConfiguration.SSID != null && wifiConfiguration.SSID.equals("\""+finalSsid+"\"")) {
                            Log.d(TAG, "removeWifiBySsid success, SSID = " + wifiConfiguration.SSID + " netId = " + wifiConfiguration.networkId);
                            //wifiManager.removeNetwork(wifiConfiguration.networkId);
                            //wifiManager.saveConfiguration();


                            try {
                                Method forget = wifiManager.getClass().getDeclaredMethod("forget", int.class, Class.forName("android.net.wifi.WifiManager$ActionListener"));
                                if (forget != null) {
                                    forget.setAccessible(true);
                                    forget.invoke(wifiManager, wifiConfiguration.networkId, null);
                                    SharedPreferences.Editor editor = wifiInfoPreferences.edit();
                                    editor.remove(wifiInfo_ssid);
                                    editor.apply();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    WifiListFragment.clearSelectedSSID();
                    mStateMachine.finish(0);

                }
            });
            TextView tv_connect  = binding.customDialog.getRoot().findViewById(R.id.custom_dialog_ok_tv_id);
            tv_connect .setText(getString(R.string.wifi_connect));
            //paramView.setOnClickListener(new KnownNetworkState.KnownNetworkFragment..ExternalSyntheticLambda1(this));
            tv_connect .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG,"connect");
                    connectToWifi(wifiInfo_ssid,wifiInfo_password);
                    WifiListFragment.clearSelectedSSID();
                    mStateMachine.getListener().onComplete(0);
                }
            });
            if(is_alive){
                tv_connect .setVisibility(View.INVISIBLE);
            }
            tv_forget.requestFocus();
        }

        private void connectToWifi(String ssid,String password){
            WifiConfiguration wifiConfiguration = new WifiConfiguration();
            wifiConfiguration.SSID = "\"" + ssid + "\"";
            wifiConfiguration.preSharedKey = "\"" + password + "\"";
            Log.d(TAG, "connectToWifi: wifiConfiguration.SSID = " + wifiConfiguration.SSID + ",wifiConfiguration.preSharedKey = " + wifiConfiguration.preSharedKey);
            WifiManager wifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            //如果wifi已启用,请禁用它以确保连接新网络
            if (wifiManager.isWifiEnabled()){
                wifiManager.setWifiEnabled(false);
            }

            //添加并启用网络配置
            int networkId = wifiManager.addNetwork(wifiConfiguration);
            wifiManager.enableNetwork(networkId,true);

            //重新启用wifi
            wifiManager.setWifiEnabled(true);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (getCurrentWifiSsid(wifiManager).equals(ssid)){
                        Log.d(TAG, "run: 连接成功4秒 getCurrentWifiSsid(wifiManager) = " + getCurrentWifiSsid(wifiManager)+ ",ssid = " + ssid);
                        showToast(mContext.getResources().getString(R.string.wifi_setup_connection_success));
                    }else {
                        showToast(mContext.getResources().getString(R.string.bluetooth_index_connect_failed));
                        Log.d(TAG, "run: 连接失败4秒 getCurrentWifiSsid(wifiManager) = " + getCurrentWifiSsid(wifiManager)+ ",ssid = " + ssid);
                    }
                    SharedPreferences.Editor editor = wifiInfoPreferences.edit();
                    editor.putString(ssid,password);
                    Log.d(TAG, "run: SharedPreferences = " + ssid + ","+password);
                    editor.apply();
                }
            },4000);
        }

        private String getCurrentWifiSsid(WifiManager wifiManager){
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String ssid;
            if (wifiInfo != null && wifiInfo.getSupplicantState() == SupplicantState.COMPLETED){
                ssid = wifiInfo.getSSID().replace("\"","");
            }else {
                ssid = "无连接";
            }
            return ssid;
        }

        private void showToast(String text){
            Toast toast = new Toast(mContext);
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View layout = inflater.inflate(R.layout.my_toast,(ViewGroup) mActivity.findViewById(R.id.custom_toast_layout));

            toast.setGravity(Gravity.CENTER_VERTICAL,0,0);
            toast.setView(layout);
            TextView Text = layout.findViewById(R.id.custom_toast_message);
            Text.setTextSize(TypedValue.COMPLEX_UNIT_SP,24);
            Text.setText(text);
            toast.show();
        }
    }
}
