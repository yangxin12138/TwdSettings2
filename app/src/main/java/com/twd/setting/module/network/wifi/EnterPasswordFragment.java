package com.twd.setting.module.network.wifi;

import static com.twd.setting.commonlibrary.Utils.Utils.runOnUiThread;

import android.Manifest;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
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
import com.twd.setting.utils.TwdUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class EnterPasswordFragment
        extends BaseFragment implements View.OnFocusChangeListener {
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
    private int connectMode = 1;
    TwdUtils twdUtils;
    WifiManager wifiManager;

    public static EnterPasswordFragment newInstance() {
        return new EnterPasswordFragment();
    }

    private void setWifiConfigurationPassword(String paramString) {
        WifiConfiguration configuration = mUserChoiceInfo.getWifiConfiguration();
        // 清空原有安全配置，避免冲突
        configuration.allowedKeyManagement.clear();
        configuration.allowedProtocols.clear();
        configuration.allowedAuthAlgorithms.clear();
        configuration.allowedPairwiseCiphers.clear();

        int securityType = mUserChoiceInfo.getWifiSecurity();
        StringBuilder localStringBuilder;

        if (securityType == 1) { // WEP加密
            Log.d(TAG, "Configuring WEP security");
            // 设置WEP关键参数
            configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            configuration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            configuration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            configuration.allowedPairwiseCiphers.clear();
            configuration.allowedGroupCiphers.clear();

            // 处理WEP密码
            int length = paramString.length();
            if (((length == 10) || (length == 26) || (length == 58)) && paramString.matches("[0-9A-Fa-f]*")) {
                configuration.wepKeys[0] = paramString; // 十六进制密钥（不加引号）
            } else if ((length == 5) || (length == 13)) {
                localStringBuilder = new StringBuilder();
                localStringBuilder.append('"').append(paramString).append('"');
                configuration.wepKeys[0] = localStringBuilder.toString(); // ASCII密钥（加引号）
            }
        } else if (securityType == 2) { // WPA/WPA2加密
            Log.d(TAG, "Configuring WPA/WPA2 security");
            // 设置WPA关键参数
            configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            configuration.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            configuration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);

            // 处理WPA密码
            if (paramString.matches("[0-9A-Fa-f]{64}")) {
                configuration.preSharedKey = paramString; // 十六进制PSK（不加引号）
            } else {
                localStringBuilder = new StringBuilder();
                localStringBuilder.append('"').append(paramString).append('"');
                configuration.preSharedKey = localStringBuilder.toString(); // 明文密码（加引号）
            }
        } else { // 开放网络（无密码）
            Log.d(TAG, "Configuring open network");
            configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
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
        twdUtils = new TwdUtils();
        twdUtils.hideSystemUI(requireActivity());
        mUserChoiceInfo = ((UserChoiceInfo) new ViewModelProvider(requireActivity()).get(UserChoiceInfo.class));
        mStateMachine = ((StateMachine) new ViewModelProvider(requireActivity()).get(StateMachine.class));
        wifiInfoPreferences = requireContext().getSharedPreferences("wifi_info",Context.MODE_PRIVATE);
        wifiManager = (WifiManager) requireContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
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

    @Override
    public void onResume() {
        twdUtils.hideSystemUI(requireActivity());
        super.onResume();
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
        binding.edtEnterPwd.setImeOptions(EditorInfo.IME_ACTION_SEND);
        binding.edtEnterPwd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    //处理事件
                    Log.i(TAG, "onEditorAction: 回车键回调触发");
                    //收起软键盘
                    InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    connectMode = 0;
                    binding.btnConnect.requestFocus();
                }
                return false;
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
                if(connectMode == 1){
                    Log.d(TAG,"btnConnect onclick:"+binding.edtEnterPwd.getText().toString());
                    setWifiConfigurationPassword(binding.edtEnterPwd.getText().toString());
                    mStateMachine.getListener().onComplete(18);
                    String ssid = binding.titleLayout.titleTV.getText().toString();
                    String password = binding.edtEnterPwd.getText().toString();
                    connectToWifi(ssid,password);
                    //WifiListFragment.clearSelectedSSID();
                }else {
                    connectMode = 1;
                    Log.i(TAG, "onClick: 连接按键获得焦点 btnConnect.requestFocus();");
                }

            }
        });
        binding.edtEnterPwd.setOnFocusChangeListener(this);
    }

    private void showToast(String text){
        // 前置判断：上下文无效则不显示
        if (!isFragmentAlive()) return;

        Toast toast = new Toast(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
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

        //如果wifi已启用,请禁用它以确保连接新网络
        // 先检查WiFi状态
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
            new Handler().postDelayed(() -> attemptConnect(ssid, password, 0), 2000);
        } else {
            attemptConnect(ssid, password, 0);
        }

        //添加并启用网络配置
        int networkId = wifiManager.addNetwork(wifiConfiguration);
        wifiManager.enableNetwork(networkId,true);

        //重新启用wifi
        wifiManager.setWifiEnabled(true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 修复：先判断Fragment是否存活
                if (!isFragmentAlive()) return;
                if (getCurrentWifiSsid(wifiManager).equals(ssid)){
                    Log.d("yangxin", "run: 连接成功4秒 getCurrentWifiSsid(wifiManager) = " + getCurrentWifiSsid(wifiManager)+ ",ssid = " + ssid);
                    showToast(mContext.getResources().getString(R.string.wifi_setup_connection_success));
                }else {
                    //showToast(mContext.getResources().getString(R.string.bluetooth_index_connect_failed));
                    Log.d("yangxin", "run: 连接失败4秒 getCurrentWifiSsid(wifiManager) = " + getCurrentWifiSsid(wifiManager)+ ",ssid = " + ssid);
                }
                // 保存SP前判断上下文
                if (isFragmentAlive()) {
                    SharedPreferences.Editor editor = wifiInfoPreferences.edit();
                    editor.putString(ssid,password);
                    editor.apply();
                }
            }
        },8000);
    }

    private String getCurrentWifiSsid(WifiManager wifiManager){
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssid;
        if (wifiInfo != null && wifiInfo.getSupplicantState() == SupplicantState.COMPLETED){
            ssid = wifiInfo.getSSID().replace("\"","");
        }else {
            ssid = "NoConnect";
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

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus){
            if (v.getId() == R.id.edt_enter_pwd){
                binding.tvTipPwdNum.setTextColor(getResources().getColor(R.color.color_0f39e9));
            }
        }else {
            if (v.getId() == R.id.edt_enter_pwd){
                binding.tvTipPwdNum.setTextColor(getResources().getColor(R.color.white));
            }
        }
    }

    private void attemptConnect(String ssid, String password, int retryCount) {
        if (retryCount >= 3) {
            // 修复：先判断Fragment是否存活，再显示Toast
            if (isFragmentAlive()) {
                showToast("连接失败请重试"); // 替换为你的失败文案
            }
            return;
        }

        Log.d(TAG, "attemptConnect: 第 " + (retryCount + 1) + " 次尝试连接");

        try {
            // 前置判断：Fragment不存活则直接终止
            if (!isFragmentAlive()) return;
            // 1. 先移除所有相同SSID的配置
            removeAllNetworksBySsid(ssid);

            // 2. 检查WiFi是否启用
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
                new Handler().postDelayed(() -> {
                    attemptConnect(ssid, password, retryCount);
                }, 2000);
                return;
            }
            // 2. 创建新配置
            WifiConfiguration wifiConfig = createWifiConfig(ssid, password);

            // 3. 添加并连接
            int networkId = wifiManager.addNetwork(wifiConfig);
            if (networkId == -1) {
                Log.e(TAG, "添加网络失败");
                // 尝试更新现有配置
                networkId = updateExistingNetwork(ssid, password);
                if (networkId == -1) {
                    retryConnect(ssid, password, retryCount);
                    return;
                }
            }

            // 5. 启用网络（禁用其他网络）
            boolean reconnect = wifiManager.reconnect();
            Log.d(TAG, "reconnect调用结果: " + reconnect);

            boolean enableSuccess = wifiManager.enableNetwork(networkId, true);
            Log.d(TAG, "enableNetwork调用结果: " + enableSuccess);

            wifiManager.saveConfiguration();

            // 6. 检查连接状态
            new Handler().postDelayed(() -> {
                // 修复：延迟任务执行前，先判断Fragment是否存活
                if (!isFragmentAlive()) return;
                if (isConnectedToSsid(ssid)) {
                    Log.d(TAG, "连接成功: " + ssid);
                    //showToast(getString(R.string.wifi_setup_connection_success));

                    // 保存到SharedPreferences
                    SharedPreferences.Editor editor = wifiInfoPreferences.edit();
                    editor.putString(ssid, password);
                    editor.apply();
                    // 关闭当前界面前，判断Activity是否存活
                    if (getActivity() != null && !getActivity().isFinishing() && !getActivity().isDestroyed()) {
                        getActivity().finish();
                    }
                } else {
                    Log.d(TAG, "连接未成功，重试");
                    retryConnect(ssid, password, retryCount);
                }
            }, 8000);

        } catch (Exception e) {
            Log.e(TAG, "attemptConnect: 异常", e);
            retryConnect(ssid, password, retryCount);
        }
    }
    // 辅助方法：更新现有网络配置
    private int updateExistingNetwork(String ssid, String password) {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return -1;
        }

        List<WifiConfiguration> configs = wifiManager.getConfiguredNetworks();
        if (configs == null) return -1;

        for (WifiConfiguration config : configs) {
            if (config.SSID != null && config.SSID.equals("\"" + ssid + "\"")) {
                // 更新密码
                int securityType = mUserChoiceInfo != null ? mUserChoiceInfo.getWifiSecurity() : 2;

                if (securityType == 0) { // 开放网络
                    // 不需要密码
                } else if (securityType == 1) { // WEP
                    int length = password.length();
                    if ((length == 10 || length == 26 || length == 58) &&
                            password.matches("[0-9A-Fa-f]*")) {
                        config.wepKeys[0] = password;
                    } else {
                        config.wepKeys[0] = "\"" + password + "\"";
                    }
                } else { // WPA/WPA2
                    if (password.matches("[0-9A-Fa-f]{64}")) {
                        config.preSharedKey = password;
                    } else {
                        config.preSharedKey = "\"" + password + "\"";
                    }
                }

                wifiManager.updateNetwork(config);
                wifiManager.saveConfiguration();
                return config.networkId;
            }
        }
        return -1;
    }
    private void retryConnect(String ssid, String password, int retryCount) {
        new Handler().postDelayed(() -> {
            attemptConnect(ssid, password, retryCount + 1);
        }, 2000);
    }

    private void removeAllNetworksBySsid(String ssid) {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        List<WifiConfiguration> configs = wifiManager.getConfiguredNetworks();
        if (configs == null) return;

        for (WifiConfiguration config : configs) {
            if (config.SSID != null && config.SSID.equals("\"" + ssid + "\"")) {
                wifiManager.removeNetwork(config.networkId);
                Log.d(TAG, "移除旧配置: " + config.SSID);
            }
        }
        wifiManager.saveConfiguration();
    }

    private boolean isConnectedToSsid(String ssid) {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo == null) return false;

        if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
            String connectedSsid = wifiInfo.getSSID().replace("\"", "");
            return connectedSsid.equals(ssid);
        }
        return false;
    }

    private WifiConfiguration createWifiConfig(String ssid, String password) {
        WifiConfiguration config = new WifiConfiguration();

        // 设置SSID（必须加引号）
        config.SSID = "\"" + ssid + "\"";
        config.status = WifiConfiguration.Status.ENABLED;
        config.priority = 40; // 设置较高优先级

        // 根据安全类型设置不同的配置
        int securityType = mUserChoiceInfo != null ? mUserChoiceInfo.getWifiSecurity() : 2; // 默认为WPA2

        // 清空所有安全设置
        config.allowedKeyManagement.clear();
        config.allowedProtocols.clear();
        config.allowedAuthAlgorithms.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedGroupCiphers.clear();

        if (securityType == 0) { // 开放网络（无密码）
            Log.d(TAG, "创建开放网络配置");
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

        } else if (securityType == 1) { // WEP加密
            Log.d(TAG, "创建WEP网络配置");
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);

            // WEP密码处理
            int length = password.length();
            if ((length == 10 || length == 26 || length == 58) &&
                    password.matches("[0-9A-Fa-f]*")) {
                config.wepKeys[0] = password; // 十六进制
            } else if (length == 5 || length == 13) {
                config.wepKeys[0] = "\"" + password + "\""; // ASCII
            } else {
                config.wepKeys[0] = "\"" + password + "\"";
            }
            config.wepTxKeyIndex = 0;

        } else { // WPA/WPA2加密（最常见）
            Log.d(TAG, "创建WPA/WPA2网络配置");
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);

            // WPA密码处理
            if (password.matches("[0-9A-Fa-f]{64}")) {
                config.preSharedKey = password; // 64位十六进制PSK
            } else {
                config.preSharedKey = "\"" + password + "\"";
            }
        }

        return config;
    }

    private boolean isFragmentAlive() {
        // isAdded：Fragment是否附加到Activity；isResumed：是否处于前台；getContext()：上下文是否有效
        return isAdded() && !isDetached() && !isRemoving() && getContext() != null;
    }
}
