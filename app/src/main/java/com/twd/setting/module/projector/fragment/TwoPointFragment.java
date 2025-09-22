package com.twd.setting.module.projector.fragment;


import android.app.TwdManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.twd.setting.R;
import com.twd.setting.base.BaseBindingVmFragment;
import com.twd.setting.databinding.FragmentTwoPointBinding;
import com.twd.setting.module.projector.vm.KeystoneViewModel;
import com.twd.setting.utils.TwdUtils;

public class TwoPointFragment extends BaseBindingVmFragment<FragmentTwoPointBinding, KeystoneViewModel> {
    private static final String TAG = "TwoPointFragment";
    public final String ORIGIN_POINT = "(0)";
    TwdUtils twdUtils;
    int horizontalValue;
    int verticalValue;
    int MAX_VALUE = 50;
    int MIN_VALUE = -50;
    private TwdManager twdManager;
    public static TwoPointFragment newInstance() {
        return new TwoPointFragment();
    }
    public int initLayout(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle) {
        return R.layout.fragment_two_point;
    }

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_two_point;
    }
    private void resetView(){
        binding.tvVertical.setText(ORIGIN_POINT);
        binding.tvHorizontal.setText(ORIGIN_POINT);
    }
    private void updateText(){
        horizontalValue =(int) twdManager.getHorizontalDegree();
        verticalValue = (int) twdManager.getVertivalDegree();
        binding.tvHorizontal.setText("("+horizontalValue+")");
        binding.tvVertical.setText("("+verticalValue+")");
    }

    public void onViewCreated(View paramView, Bundle paramBundle) {
        super.onViewCreated(paramView, paramBundle);
        twdUtils = new TwdUtils();
        twdUtils.hideSystemUI(getActivity());
        twdManager = TwdManager.getInstance();
        horizontalValue =(int) twdManager.getHorizontalDegree();
        verticalValue = (int) twdManager.getVertivalDegree();
        binding.tvHorizontal.setText("("+horizontalValue+")");
        binding.tvVertical.setText("("+verticalValue+")");
        binding.ivTrapezoidal01.setFocusable(true);
        binding.ivTrapezoidal01.requestFocus();
        binding.ivTrapezoidal01.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                int Horizontal = horizontalValue;
                int Vertical = verticalValue;
                int newH = Horizontal;
                int newV = Vertical;
                if (event.getAction() == KeyEvent.ACTION_DOWN){
                    switch (keyCode){
                        case KeyEvent.KEYCODE_DPAD_UP:
                            newV = newV+1;
                            newV = Math.min(MAX_VALUE,newV);
                            twdManager.setVerticalDegree(newV);
                            break;
                        case KeyEvent.KEYCODE_DPAD_DOWN:
                            newV = newV-1;
                            newV = Math.max(MIN_VALUE,newV);
                            twdManager.setVerticalDegree(newV);
                            break;
                        case KeyEvent.KEYCODE_DPAD_LEFT:
                            newH = newH+1;
                            newH = Math.min(MAX_VALUE,newH);
                            twdManager.setHorizontalDegree(newH);
                            break;
                        case KeyEvent.KEYCODE_DPAD_RIGHT:
                            newH = newH-1;
                            newH = Math.max(MIN_VALUE,newH);
                            twdManager.setHorizontalDegree(newH);
                            break;
                        case KeyEvent.KEYCODE_MENU:
                            twdManager.resetDegree();
                            twdManager.setVerticalDegree(0);
                            twdManager.setHorizontalDegree(0);
                            break;
                    }
                    updateText();
                }
                return false;
            }
        });

    }
}
