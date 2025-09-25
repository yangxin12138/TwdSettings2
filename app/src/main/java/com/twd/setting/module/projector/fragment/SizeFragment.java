package com.twd.setting.module.projector.fragment;

import android.app.TwdManager;
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
import com.twd.setting.utils.TwdUtils;

public class SizeFragment extends BaseBindingVmFragment<FragmentSizeBinding, KeystoneViewModel> {

    private static final String TAG = "SizeFragment";
    TwdUtils twdUtils;
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
        twdUtils = new TwdUtils();
        twdUtils.hideSystemUI(getActivity());
        binding =  DataBindingUtil.inflate(paramLayoutInflater, R.layout.fragment_size, paramViewGroup, false);
        return binding.getRoot();
    }
    public void onViewCreated(View paramView, Bundle paramBundle) {
        super.onViewCreated(paramView, paramBundle);
        binding.setViewModel(viewModel);
        int ZoomMin = TwdManager.getInstance().getZoomMinValue();
        int CurrentZoom = TwdManager.getInstance().getZoomValue();
        Log.d(TAG, "onViewCreated: 缩放最小值是 = " + ZoomMin+",当前缩放是 = "+CurrentZoom);

        int initialProgress = (100 - CurrentZoom) / 5;
        initialProgress = Math.max(0, Math.min(initialProgress, (100 - ZoomMin) / 5));
        Log.i(TAG, "onViewCreated: 初始化读取的progress 是"+initialProgress);
        binding.seekbarLevel.setProgress(initialProgress);
        binding.textLevel.setText("Level: " + initialProgress);


        binding.seekbarLevel.setFocusable(true);
        binding.seekbarLevel.requestFocus();
        binding.seekbarLevel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                Log.d(TAG,"onProgressChanged:"+progress+",flag:"+b);
                if(b) {
                    binding.seekbarLevel.setProgress(progress);
                    binding.textLevel.setText("Level: " + progress);
                    // 公式：缩放值 = 100 - (进度值 × 5)
                    int zoomValue = 100 - (progress * 5);
                    if (zoomValue >= ZoomMin && zoomValue <= 100) {
                        TwdManager.getInstance().setZoomValue(zoomValue);
                        Log.d(TAG, "设置缩放值: " + zoomValue);
                    }
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
