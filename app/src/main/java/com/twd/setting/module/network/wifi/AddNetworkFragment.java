package com.twd.setting.module.network.wifi;

import android.content.Context;
import android.content.res.Resources;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.twd.setting.R;
import com.twd.setting.base.BaseFragment;
import com.twd.setting.databinding.FragmentAddWifiNetworkBinding;
import com.twd.setting.module.network.setup.UserChoiceInfo;
import com.twd.setting.module.network.util.StateMachine;

import java.util.Arrays;
import java.util.List;

public class AddNetworkFragment
        extends BaseFragment {
    private static final int PSK_MIN_LENGTH = 8;
    private static final int WEP_MIN_LENGTH = 5;
    private final String LOG_TAG = "AddNetworkFragment";
    private FragmentAddWifiNetworkBinding binding;
    private StateMachine mStateMachine;
    private UserChoiceInfo mUserChoiceInfo;
    InputFilter typeFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
            return null;
        }
    };

    private void initChooseSecurity() {

        List localList = Arrays.asList(getResources().getStringArray(R.array.wifi_encrypt_type));
        int[] arrayOfInt = new int[4];
        arrayOfInt[0] = 0;
        binding.selectorEncryptType.setText((CharSequence) localList.get(arrayOfInt[0]));
        mUserChoiceInfo.setWifiSecurity(0);
        binding.itemEnterPwd.setVisibility(View.GONE);
        String str1 = getResources().getString(R.string.wifi_security_type_none);
        String str2 = getResources().getString(R.string.wifi_security_type_wep);
        String str3 = getResources().getString(R.string.wifi_security_type_wpa);
        String str4 = getResources().getString(R.string.wifi_security_type_eap);
        binding.itemEncryptType.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keycode, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                    String encrytype = binding.selectorEncryptType.getText().toString();
                    int position = 0;
                    if(encrytype.equals(str1)){
                        position=0;
                    } else if (encrytype.equals(str2)) {
                        position=1;
                    } else if (encrytype.equals(str3)) {
                        position=2;
                    } else if (encrytype.equals(str4)) {
                        position=3;
                    }
                    if(keycode == KeyEvent.KEYCODE_DPAD_LEFT){
                        position = position < 1 ? 3:position-1;
                    } else if (keycode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        position = position > 2 ? 0:position+1;
                    }
                    binding.selectorEncryptType.setText((CharSequence) localList.get(position));
                    mUserChoiceInfo.setWifiSecurity(position);
                    if(position == 0) {
                        binding.itemEnterPwd.setVisibility(View.GONE);
                        binding.edtEnterPwd.setText("");
                        //mUserChoiceInfo.setWifiSecurity(0);
                    }else {
                        binding.itemEnterPwd.setVisibility(View.VISIBLE);
                        //mUserChoiceInfo.setWifiSecurity(1);
                    }
                }
                return false;
            }
        });
    }

    private void initEnterPassword() {
        binding.edtEnterPwd.setFilters(new InputFilter[]{typeFilter, new InputFilter.LengthFilter(32)});
        binding.itemEnterPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"itemEnterPwd  onClick");
                binding.edtEnterPwd.setFocusable(true);
                binding.edtEnterPwd.requestFocus();
                binding.edtEnterPwd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD|InputType.TYPE_CLASS_TEXT);
                InputMethodManager inputManager = (InputMethodManager)requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(binding.edtEnterPwd, 0);
            }
        });
        binding.edtEnterPwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                Log.d(TAG,"edtEnterPwd  onFocusChange");
            }
        });
        binding.edtEnterPwd.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable editable) {
                updateBtnConnectEnabled(mUserChoiceInfo.getWifiSecurity(), editable.length());
            }

            public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {
            }

            public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {
            }
        });
    }

    private void initNetworkName() {
        binding.itemNetworkName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"itemNetworkName  onClick");
                binding.edtNetworkName.setFocusable(true);
                binding.edtNetworkName.requestFocus();
                InputMethodManager inputManager = (InputMethodManager)requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(binding.edtNetworkName, 0);
            }
        });
        binding.edtNetworkName.setInputType(InputType.TYPE_CLASS_TEXT);
        binding.edtNetworkName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                Log.d(TAG,"edtNetworkName  onFocusChange");

            }
        });
    }

    public static AddNetworkFragment newInstance() {
        return new AddNetworkFragment();
    }

    private void setWifiConfigurationPassword(String paramString) {
        int security = mUserChoiceInfo.getWifiSecurity();
        WifiConfiguration configuration = mUserChoiceInfo.getWifiConfiguration();
        StringBuilder localStringBuilder;
        configuration.SSID = "\""+binding.edtNetworkName.getText().toString()+"\"";
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
        /*
        if(security == 0){
            wifiConfiguration.wepKeys[0] = "";
        }else if (security == 1) {
            int i = paramString.length();
            if (((i == 10) || (i == 26) || (i == 32) || (i == 58)) && (paramString.matches("[0-9A-Fa-f]*"))) {
                wifiConfiguration.wepKeys[0] = paramString;
                return;
            }
            if ((i == 5) || (i == 13) || (i == 16) || (i == 29)) {
                localStringBuilder = new StringBuilder();
                localStringBuilder.append("\"");
                localStringBuilder.append(paramString);
                localStringBuilder.append("\"");
                wifiConfiguration.wepKeys[0] = localStringBuilder.toString();
            }
        } else {
            if (paramString.matches("[0-9A-Fa-f]{64}")) {
                wifiConfiguration.preSharedKey = paramString;
                return;
            }
            localStringBuilder = new StringBuilder();
            localStringBuilder.append("\"");
            localStringBuilder.append(paramString);
            localStringBuilder.append("\"");
            wifiConfiguration.preSharedKey = localStringBuilder.toString();
        }*/
    }

    private void updateBtnConnectEnabled(int paramInt1, int paramInt2) {
        if (paramInt1 == 0) {
            binding.btnConnect.setEnabled(true);
            binding.btnConnect.setFocusable(true);
            binding.btnConnect.setFocusableInTouchMode(true);
            return;
        }
        if (paramInt2 >= 8) {
            binding.btnConnect.setEnabled(true);
            binding.btnConnect.setFocusable(true);
            binding.btnConnect.setFocusableInTouchMode(true);
        } else {
            binding.btnConnect.setEnabled(false);
            binding.btnConnect.setFocusable(false);
            binding.btnConnect.setFocusableInTouchMode(false);
        }
    }

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup paramViewGroup, Bundle paramBundle) {
        mUserChoiceInfo =  new ViewModelProvider(requireActivity()).get(UserChoiceInfo.class);
        mStateMachine =  new ViewModelProvider(requireActivity()).get(StateMachine.class);
        binding =  DataBindingUtil.inflate(inflater, R.layout.fragment_add_wifi_network, paramViewGroup, false);
        return binding.getRoot();
    }

    public void onViewCreated(View paramView, Bundle paramBundle) {
        super.onViewCreated(paramView, paramBundle);
        initTitle(paramView, R.string.fragment_add_network);
        initNetworkName();
        initEnterPassword();
        initChooseSecurity();
        updateBtnConnectEnabled(mUserChoiceInfo.getWifiSecurity(), binding.edtEnterPwd.length());
        binding.btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WifiListFragment.clearSelectedSSID();
                setWifiConfigurationPassword(binding.edtEnterPwd.getText().toString());
Log.d(TAG,"config:"+mUserChoiceInfo.getWifiConfiguration());
                mStateMachine.getListener().onComplete(18);
            }
        });
        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WifiListFragment.clearSelectedSSID();
                mStateMachine.back();
            }
        });
    }
}
