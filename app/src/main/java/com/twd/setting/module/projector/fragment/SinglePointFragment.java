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
import com.twd.setting.databinding.FragmentSinglePointBinding;
import com.twd.setting.module.projector.vm.KeystoneViewModel;
import com.twd.setting.utils.SystemPropertiesUtils;
import com.twd.setting.utils.TwdUtils;

public class SinglePointFragment extends BaseBindingVmFragment<FragmentSinglePointBinding, KeystoneViewModel> {
    private static final String TAG = "SinglePointFragment";
    private int nowPoint = 0;
    public final String ORIGIN_POINT = "0,0";
    private static SharedPreferences prefs;
    public final int MODE_ONEPOINT = 1;
    public final int MODE_TWOPOINT = 0;
    public final int MODE_UNKOWN = -1;
    private String projectorMode;
    TwdUtils twdUtils;
    public static SinglePointFragment newInstance() {
        return new SinglePointFragment();
    }
    public int initLayout(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle) {
        return R.layout.fragment_single_point;
    }
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
    }
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_single_point;
    }

    public void onViewCreated(View paramView, Bundle paramBundle) {
        super.onViewCreated(paramView, paramBundle);
        binding.setViewModel(viewModel);
        twdUtils = new TwdUtils();
        twdUtils.hideSystemUI(getActivity());
        prefs = getActivity().getSharedPreferences("ty_keystone", Context.MODE_PRIVATE);
        int mode = prefs.getInt("mode",MODE_UNKOWN);
        Log.d("SinglePoint", "TrapezoidalSinglePointActivity mode: "+mode);
        if(mode == MODE_TWOPOINT || mode == MODE_UNKOWN){
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("mode",MODE_ONEPOINT);
            editor.apply();

            viewModel.restoreKeystone();
            resetView();
        }
        viewModel.setKeystoneMode(MODE_ONEPOINT);
        if(viewModel.isVertical()){
            nowPoint = 3;
        }else {
            nowPoint = 0;
        }

        projectorMode = SystemPropertiesUtils.getProperty("persist.sys.projection","0");
        Log.d(TAG, "onViewCreated: projectorMode = " + projectorMode);
        if (projectorMode.equals("1")){
            nowPoint = 1;
        }else if (projectorMode.equals("0")){
            nowPoint = 0;
        } else if (projectorMode.equals("2")) {
            nowPoint = 2;
        } else if (projectorMode.equals("3")) {
            nowPoint = 3;
        }
        binding.ivSingleLeftUp.setFocusable(true);
        binding.ivSingleLeftUp.requestFocus();
        binding.ivSingleLeftUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: 切换到右上");
                binding.ivSingleRightUp.requestFocus();
                binding.ivSingleRightUp.setVisibility(View.VISIBLE);
                binding.ivSingleLeftUp.setVisibility(View.INVISIBLE);
                if (projectorMode.equals("0") ){
                    nowPoint = 1;
                } else if (projectorMode.equals("1")) {
                    nowPoint = 0;
                } else if (projectorMode.equals("2")) {
                    nowPoint = 3;
                } else if (projectorMode.equals("3")) {
                    nowPoint = 2;
                }
            }
        });
        binding.ivSingleLeftUp.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                dealKey(i,keyEvent);
                return false;
            }
        });

        binding.ivSingleRightUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("function:onKey()","切换到右下");
                binding.ivSingleRightDown.requestFocus();
                binding.ivSingleRightDown.setVisibility(View.VISIBLE);
                binding.ivSingleRightUp.setVisibility(View.INVISIBLE);
                if (projectorMode.equals("0")){
                    nowPoint = 2;
                } else if (projectorMode.equals("1")) {
                    nowPoint = 3;
                } else if (projectorMode.equals("2")) {
                    nowPoint = 0;
                } else if (projectorMode.equals("3")) {
                    nowPoint = 1;
                }
            }
        });
        binding.ivSingleRightUp.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                dealKey(i,keyEvent);
                return false;
            }
        });
        binding.ivSingleRightDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("function:onKey()","切换到左下");
                binding.ivSingleLeftDown.requestFocus();
                binding.ivSingleLeftDown.setVisibility(View.VISIBLE);
                binding.ivSingleRightDown.setVisibility(View.INVISIBLE);
                if (projectorMode.equals("0")){
                    nowPoint = 3;
                } else if (projectorMode.equals("1")) {
                    nowPoint = 2;
                } else if (projectorMode.equals("2")) {
                    nowPoint = 1;
                } else if (projectorMode.equals("3")) {
                    nowPoint = 0;
                }
            }
        });
        binding.ivSingleRightDown.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                dealKey(i,keyEvent);
                return false;
            }
        });
        binding.ivSingleLeftDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("function:onKey()","切换到左上");
                binding.ivSingleLeftUp.requestFocus();
                binding.ivSingleLeftUp.setVisibility(View.VISIBLE);
                binding.ivSingleLeftDown.setVisibility(View.INVISIBLE);
                if (projectorMode.equals("0")){
                    nowPoint = 0;
                } else if (projectorMode.equals("1")) {
                    nowPoint = 1;
                } else if (projectorMode.equals("2")) {
                    nowPoint = 2;
                } else if (projectorMode.equals("3")) {
                    nowPoint = 3;
                }
            }
        });
        binding.ivSingleLeftDown.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                dealKey(i,keyEvent);
                return false;
            }
        });
        binding.ivSingleLeftUp.setVisibility(View.VISIBLE);
        binding.ivSingleRightUp.setVisibility(View.INVISIBLE);
        binding.ivSingleRightDown.setVisibility(View.INVISIBLE);
        binding.ivSingleLeftDown.setVisibility(View.INVISIBLE);
    }

    private void resetView(){
        binding.tvSingleLeftUp.setText(ORIGIN_POINT);
        binding.tvSingleLeftDown.setText(ORIGIN_POINT);
        binding.tvSingleRightUp.setText(ORIGIN_POINT);
        binding.tvSingleRightDown.setText(ORIGIN_POINT);

    }
    private void updateText(){
        binding.tvSingleLeftUp.setText(viewModel.getOnePointInfo(0));
        binding.tvSingleLeftDown.setText(viewModel.getOnePointInfo(3));
        binding.tvSingleRightUp.setText(viewModel.getOnePointInfo(1));
        binding.tvSingleRightDown.setText(viewModel.getOnePointInfo(2));
    }
    public boolean dealKey( int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN){
            switch (keyCode){
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if(viewModel.isVertical()){
                        viewModel.oneBottom(nowPoint);
                    }else {
                        viewModel.oneLeft(nowPoint);
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if(viewModel.isVertical()){
                        viewModel.oneTop(nowPoint);
                    }else {
                        viewModel.oneRight(nowPoint);
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    if(viewModel.isVertical()){
                        viewModel.oneLeft(nowPoint);
                    }else{
                        viewModel.oneTop(nowPoint);
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if(viewModel.isVertical()){
                        viewModel.oneRight(nowPoint);
                    }else{
                        viewModel.oneBottom(nowPoint);
                    }
                    break;
                case KeyEvent.KEYCODE_MENU:
                    viewModel.restoreKeystone();
                    resetView();
                    break;
            }
            updateText();
        }
        return false;
    }
}
