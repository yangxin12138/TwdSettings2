package com.twd.setting.module.projector.fragment;


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
    private static SharedPreferences prefs;
    public final int MODE_ONEPOINT = 1;
    public final int MODE_TWOPOINT = 0;
    public final int MODE_UNKOWN = -1;
    TwdUtils twdUtils;

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
        binding.tvVertical.setText(viewModel.getTwoPointYString());
        binding.tvHorizontal.setText(viewModel.getTwoPointXString());
    }

    public void onViewCreated(View paramView, Bundle paramBundle) {
        super.onViewCreated(paramView, paramBundle);
        binding.setViewModel(viewModel);
        twdUtils = new TwdUtils();
        twdUtils.hideSystemUI(getActivity());
        prefs = getActivity().getSharedPreferences("ty_keystone", Context.MODE_PRIVATE);
        int mode = prefs.getInt("mode",MODE_UNKOWN);
        Log.d("TwoPoint", "TrapezoidalActivity mode: "+mode);
        if(mode == MODE_ONEPOINT || mode == MODE_UNKOWN){
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("mode",MODE_TWOPOINT);
            editor.apply();

            viewModel.resetKeystone();
            resetView();
        }
        viewModel.setKeystoneMode(MODE_TWOPOINT);

        binding.ivTrapezoidal01.setFocusable(true);
        binding.ivTrapezoidal01.requestFocus();
        binding.ivTrapezoidal01.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN){
                    switch (keyCode){
                        case KeyEvent.KEYCODE_DPAD_UP:
                            if(viewModel.isVertical()){
                                viewModel.twoBottom();
                            }else {
                                viewModel.twoTop();
                            }
                            break;
                        case KeyEvent.KEYCODE_DPAD_DOWN:
                            if(viewModel.isVertical()){
                                viewModel.twoTop();
                            }else {
                                viewModel.twoBottom();
                            }
                            break;
                        case KeyEvent.KEYCODE_DPAD_LEFT:
                            if(viewModel.isVertical()){
                                viewModel.twoRight();
                            }else {
                                viewModel.twoLeft();
                            }
                            break;
                        case KeyEvent.KEYCODE_DPAD_RIGHT:
                            if(viewModel.isVertical()){
                                viewModel.twoLeft();
                            }else {
                                viewModel.twoRight();
                            }
                            break;
                        case KeyEvent.KEYCODE_MENU:
                            viewModel.resetKeystone();
                            resetView();
                            break;
                    }
                    updateText();

                }
                return false;
            }
        });

    }
}
