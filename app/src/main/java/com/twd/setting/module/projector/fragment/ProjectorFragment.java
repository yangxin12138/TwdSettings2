package com.twd.setting.module.projector.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.Observer;

import com.twd.setting.R;
import com.twd.setting.base.BaseBindingVmFragment;
import com.twd.setting.databinding.FragmentProjectorBinding;
import com.twd.setting.module.projector.ProjectionActivity;
import com.twd.setting.module.projector.vm.ProjectorViewModel;
import com.twd.setting.utils.SystemPropertiesUtils;
import com.twd.setting.utils.UiUtils;

public class ProjectorFragment extends BaseBindingVmFragment<FragmentProjectorBinding, ProjectorViewModel> implements View.OnFocusChangeListener {
    private static final String TAG = "ProjectorFragment";
    private int selectItem = 4;
    SharedPreferences autoPreferences;
    boolean isAuto;
    public static final String PROP_AUTOFOCUS = "persist.sys.keystone.autofocus";
    private void clickItem(int item) {
        isAuto = autoPreferences.getBoolean("AutoMode",false);
        Log.i(TAG, "clickItem: isAuto = "+ isAuto);
        Log.d(TAG,"clickItem: "+item);
        if(item == R.id.twoPointInclude){//1
            gotoTwoPoint();
        }else if(item == R.id.fourPointInclude){//2
            gotoFourPoint();
        }else if(item == R.id.sizeInclude){//3
            gotoSize();
        }else if(item == R.id.projectionInclude){//4
            gotoProjection();
        }else if (item == R.id.autoInclude){//5
            gotoAuto(isAuto);
        }
    }

    private void gotoAuto(boolean Auto){
        SharedPreferences.Editor editor = autoPreferences.edit();
        selectItem = 4;
        if (Auto){//原本是选中，自动模式，点击后变成手动模式

            /*binding.twoPointInclude.itemRL.setVisibility(View.VISIBLE);
            binding.fourPointInclude.itemRL.setVisibility(View.VISIBLE);*/
            Log.i(TAG, "gotoAuto: 关闭switch,走手动模式");
            binding.twoPointInclude.contentTV.setTextColor(getResources().getColor(R.color.white));
            binding.twoPointInclude.contentTVLeft.setVisibility(View.VISIBLE);
            binding.fourPointInclude.contentTV.setTextColor(getResources().getColor(R.color.white));
            binding.fourPointInclude.contentTVLeft.setVisibility(View.VISIBLE);
            binding.twoPointInclude.itemRL.setFocusable(true);
            binding.fourPointInclude.itemRL.setFocusable(true);
            binding.autoInclude.switchAuto.setChecked(false);
            isAuto = false;
            editor.putBoolean("AutoMode",false).apply();
            //TODO:手动模式
            SystemPropertiesUtils.setProperty(PROP_AUTOFOCUS,"0");
        }else {//原本是未选中，手动模式，点击后变成自动模式

            /*binding.twoPointInclude.itemRL.setVisibility(View.GONE);
            binding.fourPointInclude.itemRL.setVisibility(View.GONE);*/
            Log.i(TAG, "gotoAuto: 开启switch,走自动模式");
            binding.twoPointInclude.contentTV.setTextColor(getResources().getColor(R.color.unselectable_color));
            binding.twoPointInclude.contentTVLeft.setVisibility(View.GONE);
            binding.fourPointInclude.contentTV.setTextColor(getResources().getColor(R.color.unselectable_color));
            binding.fourPointInclude.contentTVLeft.setVisibility(View.GONE);
            binding.twoPointInclude.itemRL.setFocusable(false);
            binding.fourPointInclude.itemRL.setFocusable(false);
            binding.autoInclude.switchAuto.setChecked(true);
            isAuto = true;
            editor.putBoolean("AutoMode",true).apply();
            //TODO:自动模式
            SystemPropertiesUtils.setProperty(PROP_AUTOFOCUS,"1");
        }

    }

    private void gotoTwoPoint() {
        selectItem = 0;
        UiUtils.replaceFragmentHadBackStack(getParentFragmentManager(), 16908290, TwoPointFragment.newInstance(), "ProjectorFragment");
    }

    private void gotoFourPoint() {
        selectItem = 1;
        UiUtils.replaceFragmentHadBackStack(getParentFragmentManager(), 16908290, SinglePointFragment.newInstance(), "ProjectorFragment");
    }
    private void gotoSize() {
        selectItem = 2;
        UiUtils.replaceFragmentHadBackStack(getParentFragmentManager(), 16908290, SizeFragment.newInstance(), "ProjectorFragment");
    }

    private void gotoProjection() {
        selectItem = 3;
        launcher.launch(new Intent(mActivity, ProjectionActivity.class));
    }

    public static ProjectorFragment newInstance() {
        return new ProjectorFragment();
    }

    private void setClickListener() {
        UiUtils.setOnClickListener(((FragmentProjectorBinding) this.binding).autoInclude.itemRL,((ProjectorViewModel) this.viewModel).getItemClickListener());
        UiUtils.setOnClickListener(((FragmentProjectorBinding) this.binding).twoPointInclude.itemRL, ((ProjectorViewModel) this.viewModel).getItemClickListener());
        UiUtils.setOnClickListener(((FragmentProjectorBinding) this.binding).fourPointInclude.itemRL, ((ProjectorViewModel) this.viewModel).getItemClickListener());
        UiUtils.setOnClickListener(((FragmentProjectorBinding) this.binding).sizeInclude.itemRL, ((ProjectorViewModel) this.viewModel).getItemClickListener());
        UiUtils.setOnClickListener(((FragmentProjectorBinding) this.binding).projectionInclude.itemRL, ((ProjectorViewModel) this.viewModel).getItemClickListener());
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

    @Override
    public void onResume() {
        super.onResume();
        if(selectItem == 0){
            binding.twoPointInclude.itemRL.requestFocus();
        } else if (selectItem ==1) {
            binding.fourPointInclude.itemRL.requestFocus();
        } else if (selectItem ==2) {
            binding.sizeInclude.itemRL.requestFocus();
        } else if (selectItem ==3) {
            binding.projectionInclude.itemRL.requestFocus();
        } else if (selectItem == 4) {
            binding.autoInclude.itemRL.requestFocus();
        }
        autoPreferences = getContext().getSharedPreferences("SaveAutoMode", Context.MODE_PRIVATE);
        initAutoMode();
    }

    public void onViewCreated(View paramView, Bundle paramBundle) {
        super.onViewCreated(paramView, paramBundle);
        ((FragmentProjectorBinding) this.binding).setViewModel((ProjectorViewModel) this.viewModel);
        initTitle(paramView, R.string.projector_title);

        ((FragmentProjectorBinding) this.binding).twoPointInclude.itemRL.requestFocus();

        ((ProjectorViewModel) this.viewModel).getClickItem().observe(getViewLifecycleOwner(), new Observer() {
            @Override
            public void onChanged(Object o) {
                clickItem(((Integer)o).intValue());
            }
        });
        setClickListener();
    }

    private void initAutoMode(){
        String AutoFocus = SystemPropertiesUtils.getProperty(PROP_AUTOFOCUS,"3");
        if (AutoFocus.equals("1")){isAuto = true; } else if (AutoFocus.equals("0")) {isAuto = false;}else {
            isAuto=false;
            Log.i(TAG, "initAutoMode: 参数不正常 = "+AutoFocus);
        }
        Log.i(TAG, "initAutoMode: AutoFocus = "+ AutoFocus);
        SharedPreferences.Editor editor = autoPreferences.edit();
        editor.putBoolean("AutoMode",isAuto);
        editor.apply();
        Log.i(TAG, "initAutoMode: 初始化 AutoMode = "+isAuto);
        if (isAuto){
            binding.twoPointInclude.contentTV.setTextColor(getResources().getColor(R.color.unselectable_color));
            binding.twoPointInclude.contentTVLeft.setVisibility(View.GONE);
            binding.fourPointInclude.contentTV.setTextColor(getResources().getColor(R.color.unselectable_color));
            binding.fourPointInclude.contentTVLeft.setVisibility(View.GONE);
            binding.twoPointInclude.itemRL.setFocusable(false);
            binding.fourPointInclude.itemRL.setFocusable(false);
            binding.autoInclude.switchAuto.setChecked(true);
            //TODO:自动模式
        }else {
            //TODO:手动模式
            if (binding.twoPointInclude.itemRL.isFocused()){
                Log.i(TAG, "initAutoMode: 初始化 twoPointInclude 选中");
                binding.twoPointInclude.contentTV.setTextColor(getResources().getColor(R.color.color_0f39e9));
            }else {
                Log.i(TAG, "initAutoMode: 初始化 twoPointInclude 未选中");
                binding.twoPointInclude.contentTV.setTextColor(getResources().getColor(R.color.white));
            }
            binding.twoPointInclude.contentTVLeft.setVisibility(View.VISIBLE);
            if (binding.fourPointInclude.itemRL.isFocused()){
                Log.i(TAG, "initAutoMode: 初始化 fourPointInclude 选中");
                binding.fourPointInclude.contentTV.setTextColor(getResources().getColor(R.color.color_0f39e9));
            }else {
                Log.i(TAG, "initAutoMode: 初始化 fourPointInclude 未选中");
                binding.fourPointInclude.contentTV.setTextColor(getResources().getColor(R.color.white));
            }
            binding.fourPointInclude.contentTVLeft.setVisibility(View.VISIBLE);
            binding.twoPointInclude.itemRL.setFocusable(true);
            binding.fourPointInclude.itemRL.setFocusable(true);
            binding.autoInclude.switchAuto.setChecked(false);
        }
        binding.twoPointInclude.itemRL.setOnFocusChangeListener(this::onFocusChange);
        binding.fourPointInclude.itemRL.setOnFocusChangeListener(this::onFocusChange);
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus){
            if (v == binding.twoPointInclude.itemRL){
                binding.twoPointInclude.contentTV.setTextColor(getResources().getColor(R.color.color_0f39e9));
            } else if (v == binding.fourPointInclude.itemRL) {
                binding.fourPointInclude.contentTV.setTextColor(getResources().getColor(R.color.color_0f39e9));
            }
        }else {
            if (v == binding.twoPointInclude.itemRL){
                binding.twoPointInclude.contentTV.setTextColor(getResources().getColor(R.color.white));
            } else if (v == binding.fourPointInclude.itemRL) {
                binding.fourPointInclude.contentTV.setTextColor(getResources().getColor(R.color.white));
            }
        }
    }
}
