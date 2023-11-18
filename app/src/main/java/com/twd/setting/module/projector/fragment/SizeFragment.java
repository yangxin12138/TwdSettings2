package com.twd.setting.module.projector.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.databinding.DataBindingUtil;

import com.twd.setting.R;
import com.twd.setting.base.BaseBindingVmFragment;
import com.twd.setting.databinding.FragmentSizeBinding;
import com.twd.setting.module.projector.vm.KeystoneViewModel;

public class SizeFragment extends BaseBindingVmFragment<FragmentSizeBinding, KeystoneViewModel> {

    private static final String TAG = "SizeFragment";
    public static SizeFragment newInstance() {
        return new SizeFragment();
    }
    public int initLayout(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle) {
        return R.layout.fragment_projector;
    }

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
    }
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_projector;
    }

    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle) {
        Log.d(TAG,"onCreateView");
        binding =  DataBindingUtil.inflate(paramLayoutInflater, R.layout.fragment_size, paramViewGroup, false);
        return binding.getRoot();
    }
    public void onViewCreated(View paramView, Bundle paramBundle) {
        super.onViewCreated(paramView, paramBundle);
        binding.setViewModel(viewModel);
        binding.seekbarLevel.setFocusable(true);
        binding.seekbarLevel.requestFocus();
        binding.seekbarLevel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.d(TAG,"onProgressChanged:"+i+",flag:"+b);
                if(b) {
                    viewModel.setZoom(i);
                    binding.textLevel.setText("Level: " + i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }
}
