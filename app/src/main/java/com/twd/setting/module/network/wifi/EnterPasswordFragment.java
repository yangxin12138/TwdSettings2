package com.twd.setting.module.network.wifi;

import static com.twd.setting.commonlibrary.Utils.Utils.runOnUiThread;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.twd.setting.R;
import com.twd.setting.base.BaseFragment;
import com.twd.setting.databinding.FragmentEnterPwdBinding;
import com.twd.setting.module.network.NetworkConstant;
import com.twd.setting.module.network.setup.UserChoiceInfo;
import com.twd.setting.module.network.util.StateMachine;
import com.twd.setting.utils.HLog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EnterPasswordFragment
        extends BaseFragment {
    public static final String ARG_SCAN_RESULT = "ARG_SCAN_RESULT";
    private static final int PSK_MIN_LENGTH = 8;
    private static final int WEP_MIN_LENGTH = 5;
    private final String LOG_TAG = "EnterPasswordFragment";
    private FragmentEnterPwdBinding binding;
    boolean isPasswordHidden;
    private StateMachine mStateMachine;
    private UserChoiceInfo mUserChoiceInfo;
    private ScanResult scanResult;
    private Context mContext;
    private SharedPreferences wifiInfoPreferences;

    public static EnterPasswordFragment newInstance() {
        return new EnterPasswordFragment();
    }

    private void setWifiConfigurationPassword(String paramString) {
        WifiConfiguration configuration = mUserChoiceInfo.getWifiConfiguration();
        StringBuilder localStringBuilder;
        if (mUserChoiceInfo.getWifiSecurity() == 1) {
            Log.d(TAG,"setWifiConfigurationPassword  Security is 1");
            int length = paramString.length();
            if (((length == 10) || (length == 26) || (length == 32) || (length == 58)) && (paramString.matches("[0-9A-Fa-f]*"))) {
                configuration.wepKeys[0] = paramString;
                return;
            }
            if ((length == 5) || (length == 13) || (length == 16) || (length == 29)) {
                localStringBuilder = new StringBuilder();
                localStringBuilder.append('"');
                localStringBuilder.append(paramString);
                localStringBuilder.append('"');
                configuration.wepKeys[0] = localStringBuilder.toString();
            }
        } else {
            if ((mUserChoiceInfo.getWifiSecurity() == 2) && (paramString.length() < 8)) {
                Log.d(TAG,"setWifiConfigurationPassword  Security is 2");
                return;
            }
            Log.d(TAG,"setWifiConfigurationPassword  Security is "+mUserChoiceInfo.getWifiSecurity());
            if (paramString.matches("[0-9A-Fa-f]{64}")) {
                configuration.preSharedKey = paramString;
                return;
            }
            Log.d(TAG,"setWifiConfigurationPassword  Security is,"+mUserChoiceInfo.getWifiSecurity()+" not match");
            localStringBuilder = new StringBuilder();
            localStringBuilder.append('"');
            localStringBuilder.append(paramString);
            localStringBuilder.append('"');
            configuration.preSharedKey = localStringBuilder.toString();
            Log.d(TAG,"preShareKey: "+configuration.preSharedKey);
        }
    }

    private void updateBtnConnectClickable(int paramInt) {
        if (paramInt >= 8) {
            binding.btnConnect.setEnabled(true);
            binding.btnConnect.setFocusable(true);
            binding.btnConnect.setFocusableInTouchMode(true);
        } else {
            binding.btnConnect.setEnabled(false);
            binding.btnConnect.setFocusable(false);
            binding.btnConnect.setFocusableInTouchMode(false);
        }
    }

    private void updatePasswordHidden(Boolean flag) {
        updatePasswordHiddenTip(flag);
        updatePasswordInputObfuscation(flag);
    }

    private void updatePasswordHiddenTip(Boolean flag) {
        if (flag) {
            binding.tvTipPwdVisibility.setText(R.string.wifi_enter_passwd_hide);

            Drawable drawable = getResources().getDrawable(R.mipmap.ic_pwd_hidden_black);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            binding.tvTipPwdVisibility.setCompoundDrawablePadding(16);
            binding.tvTipPwdVisibility.setCompoundDrawables(drawable, null, null, null);
        } else {
            binding.tvTipPwdVisibility.setText(R.string.wifi_enter_passwd_show);

            Drawable drawable =  getResources().getDrawable(R.mipmap.ic_pwd_visible_black);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            binding.tvTipPwdVisibility.setCompoundDrawablePadding(16);
            binding.tvTipPwdVisibility.setCompoundDrawables(drawable, null, null, null);
        }
    }

    private void updatePasswordInputObfuscation(boolean flag) {
        if (flag) {
            binding.edtEnterPwd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD|InputType.TYPE_CLASS_TEXT);
        } else {
            binding.edtEnterPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD|InputType.TYPE_CLASS_TEXT);
        }
    }

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        Log.d(TAG,"onCreate");
        mUserChoiceInfo = ((UserChoiceInfo) new ViewModelProvider(requireActivity()).get(UserChoiceInfo.class));
        mStateMachine = ((StateMachine) new ViewModelProvider(requireActivity()).get(StateMachine.class));
        wifiInfoPreferences = requireContext().getSharedPreferences("wifi_info",Context.MODE_PRIVATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle) {
        Log.d(TAG,"onCreateView");
        binding = (FragmentEnterPwdBinding) DataBindingUtil.inflate(paramLayoutInflater, R.layout.fragment_enter_pwd, paramViewGroup, false);
        return binding.getRoot();
    }

    public void onViewCreated(View paramView, Bundle paramBundle) {
        super.onViewCreated(paramView, paramBundle);
        Log.d(TAG,"onViewCreated");
        String method_name = "";
        try {
            method_name = (String) WifiConfiguration.class.getDeclaredMethod("getPrintableSsid", new Class[0]).invoke(mUserChoiceInfo.getWifiConfiguration(), new Object[0]);
        } catch (InvocationTargetException invocationTargetException) {
        } catch (IllegalAccessException illegalAccessException) {
        } catch (NoSuchMethodException noSuchMethodException) {
            noSuchMethodException.printStackTrace();
        }

        initTitle(paramView, getString(R.string.fragment_title_specified_wifi, new Object[]{method_name}));
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(NetworkConstant.NAME_SP_NETWORK, 0);
        isPasswordHidden = sharedPreferences.getBoolean(NetworkConstant.KEY_IS_PASSWORD_HIDDEN, false);
        updatePasswordHidden(isPasswordHidden);
        binding.itemSwitchPwdVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"itemSwitchPwdVisibility  onClick");
                isPasswordHidden = !isPasswordHidden;
                updatePasswordHidden(isPasswordHidden);
            }
        });
        binding.edtEnterPwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                Log.d(TAG,"edtEnterPwd  onFocusChange");
            }
        });
        binding.edtEnterPwd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                Log.d(TAG,"edtEnterPwd  onEditorAction"+keyEvent);
                return false;
            }
        });
        binding.edtEnterPwd.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
                Log.d(TAG,"edtEnterPwd  setFilters:"+charSequence+","+i+","+i1+","+spanned+","+i2+","+i3);
                return null;
            }
        }, new InputFilter.LengthFilter(32)});
        binding.edtEnterPwd.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable paramAnonymousEditable) {
                Log.d(TAG,"edtEnterPwd  afterTextChanged");
                int i = paramAnonymousEditable.toString().length();
                EnterPasswordFragment.this.binding.tvTipPwdNum.setText(String.valueOf(i));
                EnterPasswordFragment.this.updateBtnConnectClickable(i);
            }

            public void beforeTextChanged(CharSequence charSequence, int int1, int int2, int int3) {
                Log.d(TAG,"edtEnterPwd  beforeTextChanged:"+charSequence+","+int1+","+int2+","+int3);
            }

            public void onTextChanged(CharSequence charSequence, int int1, int int2, int int3) {
                Log.d(TAG,"edtEnterPwd  onTextChanged"+charSequence+","+int1+","+int2+","+int3);
                //setWifiConfigurationPassword();
            }
        });

        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("prevPassword: ");
        localStringBuilder.append(mUserChoiceInfo.getPageSummary(2));
        localStringBuilder.append(", isPasswordHidden: ");
        localStringBuilder.append(mUserChoiceInfo.isPasswordHidden());
        Log.d(TAG, localStringBuilder.toString());
        binding.btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"btnConnect onclick:"+binding.edtEnterPwd.getText().toString());
                setWifiConfigurationPassword(binding.edtEnterPwd.getText().toString());
                mStateMachine.getListener().onComplete(18);
                String ssid = binding.titleLayout.titleTV.getText().toString();
                String password = binding.edtEnterPwd.getText().toString();
                connectToWifi(ssid,password);
                //WifiListFragment.clearSelectedSSID();
            }
        });
    }

    private void showToast(String text){
        Toast toast = new Toast(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View layout = inflater.inflate(R.layout.my_toast,(ViewGroup) mActivity.findViewById(R.id.custom_toast_layout));

        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setView(layout);
        TextView Text = layout.findViewById(R.id.custom_toast_message);
        Text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        Text.setText(text);
        toast.show();
    }

    private void connectToWifi(String ssid,String password){
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = "\"" + ssid + "\"";
        wifiConfiguration.preSharedKey = "\"" + password + "\"";

        WifiManager wifiManager = (WifiManager) requireContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        //如果wifi已启用,请禁用它以确保连接新网络
        if (wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(false);
        }

        //添加并启用网络配置
        int networkId = wifiManager.addNetwork(wifiConfiguration);
        wifiManager.enableNetwork(networkId,true);

        //重新启用wifi
        wifiManager.setWifiEnabled(true);
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getCurrentWifiSsid(wifiManager).equals(ssid)){
                    Log.d(TAG, "run: 连接成功2秒 getCurrentWifiSsid(wifiManager) = " + getCurrentWifiSsid(wifiManager)+ ",ssid = " + ssid);
                    showToast("连接成功");
                }else {
                    showToast("-----------连接失败-------");
                    Log.d(TAG, "run: 连接失败2秒 getCurrentWifiSsid(wifiManager) = " + getCurrentWifiSsid(wifiManager)+ ",ssid = " + ssid);
                }
            }
        },2000);*/

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getCurrentWifiSsid(wifiManager).equals(ssid)){
                    Log.d(TAG, "run: 连接成功4秒 getCurrentWifiSsid(wifiManager) = " + getCurrentWifiSsid(wifiManager)+ ",ssid = " + ssid);
                    showToast(mContext.getResources().getString(R.string.wifi_setup_connection_success));
                }else {
                    //showToast(mContext.getResources().getString(R.string.bluetooth_index_connect_failed));
                    Log.d(TAG, "run: 连接失败4秒 getCurrentWifiSsid(wifiManager) = " + getCurrentWifiSsid(wifiManager)+ ",ssid = " + ssid);
                }
                SharedPreferences.Editor editor = wifiInfoPreferences.edit();
                editor.putString(ssid,password);
                Log.d(TAG, "run: SharedPreferences = " + ssid + ","+password);
                editor.apply();
            }
        },8000);
    }

    private String getCurrentWifiSsid(WifiManager wifiManager){
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssid;
        if (wifiInfo != null && wifiInfo.getSupplicantState() == SupplicantState.COMPLETED){
            ssid = wifiInfo.getSSID().replace("\"","");
        }else {
            ssid = "默认网络";
        }
        return ssid;
    }
    private boolean isCurrentlyConnectedToWifi(String ssid) {
        Log.d(TAG, "isCurrentlyConnectedToWifi: 111");
        WifiManager wifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        if (wifiInfo != null) {
            Log.d(TAG, "isCurrentlyConnectedToWifi: 222");
            String currentSSID = wifiInfo.getSSID().replace("\"","");
            Log.d(TAG, "isCurrentlyConnectedToWifi: currentSSID = " + currentSSID + ", ssid = " + ssid);
            if (currentSSID != null && currentSSID.equals(ssid)) {
                return true; // 当前连接的WiFi的SSID与指定的SSID相同
            }
        }
        return false; // 当前连接的WiFi的SSID与指定的SSID不同
    }

}
