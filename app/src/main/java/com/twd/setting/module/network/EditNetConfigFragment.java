package com.twd.setting.module.network;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;

import com.twd.setting.R;
import com.twd.setting.base.BaseFragment;
import com.twd.setting.databinding.FragmentEditNetConfigBinding;
import com.twd.setting.utils.UiUtils;

import java.io.PrintStream;

public class EditNetConfigFragment
        extends BaseFragment
        implements View.OnFocusChangeListener, View.OnKeyListener {
    private static final String TAG = "EditNetConfigFragment";
    protected FragmentEditNetConfigBinding binding;
    private boolean isKeyboardShown = false;

    private float dp2Px(float paramFloat) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, paramFloat, requireContext().getResources().getDisplayMetrics());
    }

    private boolean isKeyboardShown() {
        return isKeyboardShown;
    }

    private void observeKeyboardShow() {
        binding.rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private int preHeight = 0;

            public void onGlobalLayout() {
                int i = binding.rootLayout.getRootView().getHeight() - binding.rootLayout.getHeight();
                Log.d(TAG, "height differ = " + i);
                if (preHeight == i) {
                    return;
                }
                preHeight = i;

                if (i > dp2Px(100.0F)) {
                    setKeyboardShown(true);
                } else {
                    setKeyboardShown(false);
                }
            }
        });
    }

    private void setKeyboardShown(boolean paramBoolean) {
        isKeyboardShown = paramBoolean;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup paramViewGroup, Bundle paramBundle) {
        binding = (FragmentEditNetConfigBinding) DataBindingUtil.inflate(inflater, R.layout.fragment_edit_net_config, paramViewGroup, false);
        return binding.getRoot();
    }

    public void onFocusChange(View view, boolean changed) {
        if (!(view instanceof EditText)) {
            return;
        }
        EditText editText = (EditText) view;
        if (changed) {
            editText.setSelection(editText.getText().length());
            ColorStateList colorStateList = getResources().getColorStateList(UiUtils.getThemeResId(requireActivity().getTheme(), R.attr.selectedTextColor));
            if (view.getId() == binding.edtIpAddress.getId()) {
                binding.tvTitleIpAddress.setTextColor(colorStateList);
                return;
            }
            if (view.getId() == binding.edtSubnetMask.getId()) {
                binding.tvTitleSubnetMask.setTextColor(colorStateList);
                return;
            }
            if (view.getId() == binding.edtGateway.getId()) {
                binding.tvTitleGateway.setTextColor(colorStateList);
                return;
            }
            if (view.getId() == binding.edtDNS.getId()) {
                binding.tvTitleDNS.setTextColor(colorStateList);
            }
        } else {
            ColorStateList colorStateList = getResources().getColorStateList(UiUtils.getThemeResId(requireActivity().getTheme(), R.attr.mainPageItemColor));
            if (view.getId() == binding.edtIpAddress.getId()) {
                binding.tvTitleIpAddress.setTextColor(colorStateList);
                return;
            }
            if (view.getId() == binding.edtSubnetMask.getId()) {
                binding.tvTitleSubnetMask.setTextColor(colorStateList);
                return;
            }
            if (view.getId() == binding.edtGateway.getId()) {
                binding.tvTitleGateway.setTextColor(colorStateList);
                return;
            }
            if (view.getId() == binding.edtDNS.getId()) {
                binding.tvTitleDNS.setTextColor(colorStateList);
            }
        }
    }

    public boolean onKey(View view, int paramInt, KeyEvent keyEvent) {
        if (isKeyboardShown()) {
            return false;
        }
        if (((view instanceof EditText)) && (view.hasFocus())) {
            if (paramInt != 19) {
                return false;
            }
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                view.setFocusable(false);
                if (view.getId() == binding.edtIpAddress.getId()) {
                    binding.itemSwitchConfigEdit.requestFocus();
                } else if (view.getId() == binding.edtSubnetMask.getId()) {
                    binding.edtIpAddress.requestFocus();
                } else if (view.getId() == binding.edtGateway.getId()) {
                    binding.edtSubnetMask.requestFocus();
                } else if (view.getId() == binding.edtDNS.getId()) {
                    binding.edtGateway.requestFocus();
                }
                view.setFocusable(true);
                return true;
            }
        }
        return false;
    }

    public void onViewCreated(View view, Bundle paramBundle) {
        super.onViewCreated(view, paramBundle);
        observeKeyboardShow();
        InputFilter[] inputFilter = new InputFilter[1];
        //inputFilter[0] = EditNetConfigFragment..ExternalSyntheticLambda0.INSTANCE;
        inputFilter[0] = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
                return null;
            }
        };
        binding.edtIpAddress.setFilters(inputFilter);
        binding.edtSubnetMask.setFilters(inputFilter);
        binding.edtGateway.setFilters(inputFilter);
        binding.edtDNS.setFilters(inputFilter);
        binding.itemSwitchConfigEdit.requestFocus();
        //binding.itemSwitchConfigEdit.setOnClickListener(new EditNetConfigFragment..ExternalSyntheticLambda1(this));
        binding.itemSwitchConfigEdit.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {

                                                            }
                                                        }
        );
        binding.edtIpAddress.setOnFocusChangeListener(this);
        binding.edtSubnetMask.setOnFocusChangeListener(this);
        binding.edtGateway.setOnFocusChangeListener(this);
        binding.edtDNS.setOnFocusChangeListener(this);
        binding.edtIpAddress.setOnKeyListener(this);
        binding.edtSubnetMask.setOnKeyListener(this);
        binding.edtGateway.setOnKeyListener(this);
        binding.edtDNS.setOnKeyListener(this);
    }
}
