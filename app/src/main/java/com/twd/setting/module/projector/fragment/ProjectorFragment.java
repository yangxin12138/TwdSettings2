package com.twd.setting.module.projector.fragment;

import android.content.Intent;
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
import com.twd.setting.utils.AutoFocusUtils;
import com.twd.setting.utils.SystemPropertiesUtils;
import com.twd.setting.utils.UiUtils;

public class ProjectorFragment extends BaseBindingVmFragment<FragmentProjectorBinding, ProjectorViewModel> implements View.OnFocusChangeListener {
    private static final String TAG = "ProjectorFragment";
    private int selectItem = 0;
    private AutoFocusUtils autoFocusUtils;

    private void clickItem(int item) {
        Log.d(TAG,"clickItem: "+item);
        if(item == R.id.twoPointInclude){//1
            gotoTwoPoint();
        }else if(item == R.id.fourPointInclude){//2
            gotoFourPoint();
        }else if(item == R.id.sizeInclude){//3
            gotoSize();
        }else if(item == R.id.projectionInclude){//4
            gotoProjection();
        }else if (item == R.id.AutoProjectionInclude){
            gotoAutoProjection(binding.AutoProjectionInclude.switchAuto.isChecked());
        } else if (item == R.id.AutoFocusInclude) {
            gotoAutoFocus(binding.AutoFocusInclude.switchAuto.isChecked());
        } else if (item == R.id.BootAutoFocusIclude) {
            gotoBootAutoFocus(binding.BootAutoFocusIclude.switchAuto.isChecked());
        } else if (item == R.id.AutoOBSIclude) {
            gotoAutoOBS(binding.AutoOBSIclude.switchAuto.isChecked());
        } else if (item == R.id.AutoFitScreenIclude) {
            gotoAutoFitScreen(binding.AutoFitScreenIclude.switchAuto.isChecked());
        }else if (item == R.id.VerticalProjectionInclude) {
            gotoVerticalProjection(binding.VerticalProjectionInclude.switchAuto.isChecked());
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

    private void gotoAutoProjection(boolean isChecked){
        selectItem = 4;
        if (isChecked){
            if (binding.twoPointInclude.itemRL.isFocused()){
                binding.twoPointInclude.contentTV.setTextColor(getResources().getColor(R.color.text_red_new));
            }else {
                binding.twoPointInclude.contentTV.setTextColor(getResources().getColor(R.color.black));
            }
            binding.twoPointInclude.contentTVLeft.setVisibility(View.VISIBLE);
            if (binding.fourPointInclude.itemRL.isFocused()){
                binding.fourPointInclude.contentTV.setTextColor(getResources().getColor(R.color.text_red_new));
            }else {
                binding.fourPointInclude.contentTV.setTextColor(getResources().getColor(R.color.black));
            }
            binding.fourPointInclude.contentTVLeft.setVisibility(View.VISIBLE);
            binding.twoPointInclude.itemRL.setFocusable(true);
            binding.fourPointInclude.itemRL.setFocusable(true);
            binding.AutoProjectionInclude.switchAuto.setChecked(false);
            autoFocusUtils.setTrapezoidCorrectEnable(false);
        }else {
            binding.twoPointInclude.contentTV.setTextColor(getResources().getColor(R.color.unselectable_color));
            binding.twoPointInclude.contentTVLeft.setVisibility(View.GONE);
            binding.fourPointInclude.contentTV.setTextColor(getResources().getColor(R.color.unselectable_color));
            binding.fourPointInclude.contentTVLeft.setVisibility(View.GONE);
            binding.twoPointInclude.itemRL.setFocusable(false);
            binding.fourPointInclude.itemRL.setFocusable(false);
            binding.AutoProjectionInclude.switchAuto.setChecked(true);
            autoFocusUtils.setTrapezoidCorrectEnable(true);
        }
    }
    private void gotoAutoFocus(boolean isChecked){
        selectItem = 5;
        boolean newCheckedState = !isChecked;
        binding.AutoFocusInclude.switchAuto.setChecked(newCheckedState);
        autoFocusUtils.setAutoFocusEnable(newCheckedState);
    }

    private void gotoBootAutoFocus(boolean isChecked){
        selectItem = 6;
        boolean newCheckedState = !isChecked;
        binding.BootAutoFocusIclude.switchAuto.setChecked(newCheckedState);
        autoFocusUtils.setPowerOnAutoFocusEnable(newCheckedState);
    }

    private void gotoAutoOBS(boolean isChecked){
        selectItem = 7;
        boolean newCheckedState = !isChecked;
        binding.AutoOBSIclude.switchAuto.setChecked(newCheckedState);
        autoFocusUtils.setAutoObstacleAvoidanceEnable(newCheckedState);
    }

    private void gotoAutoFitScreen(boolean isChecked){
        selectItem = 8;
        boolean newCheckedState = !isChecked;
        binding.AutoFitScreenIclude.switchAuto.setChecked(newCheckedState);
        autoFocusUtils.setAutoComeAdmireEnable(newCheckedState);
    }

    private void gotoVerticalProjection(boolean isChecked){
        selectItem = 9;
        if (isChecked){
            if (binding.twoPointInclude.itemRL.isFocused()){
                binding.twoPointInclude.contentTV.setTextColor(getResources().getColor(R.color.text_red_new));
            }else {
                binding.twoPointInclude.contentTV.setTextColor(getResources().getColor(R.color.black));
            }
            binding.twoPointInclude.contentTVLeft.setVisibility(View.VISIBLE);
            if (binding.fourPointInclude.itemRL.isFocused()){
                binding.fourPointInclude.contentTV.setTextColor(getResources().getColor(R.color.text_red_new));
            }else {
                binding.fourPointInclude.contentTV.setTextColor(getResources().getColor(R.color.black));
            }
            binding.fourPointInclude.contentTVLeft.setVisibility(View.VISIBLE);
            binding.twoPointInclude.itemRL.setFocusable(true);
            binding.fourPointInclude.itemRL.setFocusable(true);
            binding.VerticalProjectionInclude.switchAuto.setChecked(false);
            autoFocusUtils.setVerticalCorrectEnable(false);
        }else {
            binding.twoPointInclude.contentTV.setTextColor(getResources().getColor(R.color.unselectable_color));
            binding.twoPointInclude.contentTVLeft.setVisibility(View.GONE);
            binding.fourPointInclude.contentTV.setTextColor(getResources().getColor(R.color.unselectable_color));
            binding.fourPointInclude.contentTVLeft.setVisibility(View.GONE);
            binding.twoPointInclude.itemRL.setFocusable(false);
            binding.fourPointInclude.itemRL.setFocusable(false);
            binding.VerticalProjectionInclude.switchAuto.setChecked(true);
            autoFocusUtils.setVerticalCorrectEnable(true);
        }
    }

    public static ProjectorFragment newInstance() {
        return new ProjectorFragment();
    }

    private void setClickListener() {
        UiUtils.setOnClickListener(((FragmentProjectorBinding) this.binding).twoPointInclude.itemRL, ((ProjectorViewModel) this.viewModel).getItemClickListener());
        UiUtils.setOnClickListener(((FragmentProjectorBinding) this.binding).fourPointInclude.itemRL, ((ProjectorViewModel) this.viewModel).getItemClickListener());
        UiUtils.setOnClickListener(((FragmentProjectorBinding) this.binding).sizeInclude.itemRL, ((ProjectorViewModel) this.viewModel).getItemClickListener());
        UiUtils.setOnClickListener(((FragmentProjectorBinding) this.binding).projectionInclude.itemRL, ((ProjectorViewModel) this.viewModel).getItemClickListener());
        UiUtils.setOnClickListener(((FragmentProjectorBinding) this.binding).AutoProjectionInclude.itemRL,((ProjectorViewModel) this.viewModel).getItemClickListener());
        UiUtils.setOnClickListener(((FragmentProjectorBinding) this.binding).AutoFocusInclude.itemRL,((ProjectorViewModel) this.viewModel).getItemClickListener());
        UiUtils.setOnClickListener(((FragmentProjectorBinding) this.binding).BootAutoFocusIclude.itemRL,((ProjectorViewModel) this.viewModel).getItemClickListener());
        UiUtils.setOnClickListener(((FragmentProjectorBinding) this.binding).AutoOBSIclude.itemRL,((ProjectorViewModel) this.viewModel).getItemClickListener());
        UiUtils.setOnClickListener(((FragmentProjectorBinding) this.binding).AutoFitScreenIclude.itemRL,((ProjectorViewModel) this.viewModel).getItemClickListener());
        UiUtils.setOnClickListener(((FragmentProjectorBinding) this.binding).VerticalProjectionInclude.itemRL,((ProjectorViewModel) this.viewModel).getItemClickListener());
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
            binding.AutoProjectionInclude.itemRL.requestFocus();
        } else if (selectItem == 5) {
            binding.AutoFocusInclude.itemRL.requestFocus();
        } else if (selectItem == 6) {
            binding.BootAutoFocusIclude.itemRL.requestFocus();
        } else if (selectItem == 7) {
            binding.AutoOBSIclude.itemRL.requestFocus();
        } else if (selectItem == 8) {
            binding.AutoFitScreenIclude.itemRL.requestFocus();
        }else if (selectItem == 9) {
            binding.VerticalProjectionInclude.itemRL.requestFocus();
        }

        initAutoSwitch();
        initVerticalProjection();
        initSwitch();
    }

    private void initSwitch(){
        String VERTICALORAUTO = SystemPropertiesUtils.readSystemProp("VERTICALORAUTO");
        boolean verticalOrAuto = VERTICALORAUTO.equals("true");
        binding.AutoProjectionInclude.itemRL.setVisibility(verticalOrAuto ? View.GONE : View.VISIBLE);
        binding.AutoFocusInclude.itemRL.setVisibility(verticalOrAuto ? View.GONE : View.VISIBLE);
        binding.BootAutoFocusIclude.itemRL.setVisibility(verticalOrAuto ? View.GONE : View.VISIBLE);
        binding.AutoOBSIclude.itemRL.setVisibility(verticalOrAuto ? View.GONE : View.VISIBLE);
        binding.AutoFitScreenIclude.itemRL.setVisibility(verticalOrAuto ? View.GONE : View.VISIBLE);
        binding.VerticalProjectionInclude.itemRL.setVisibility(verticalOrAuto ? View.VISIBLE : View.GONE);
    }

    public void onViewCreated(View paramView, Bundle paramBundle) {
        super.onViewCreated(paramView, paramBundle);
        ((FragmentProjectorBinding) this.binding).setViewModel((ProjectorViewModel) this.viewModel);
        initTitle(paramView, R.string.projector_title);
        autoFocusUtils = new AutoFocusUtils();
        ((FragmentProjectorBinding) this.binding).twoPointInclude.itemRL.requestFocus();

        ((ProjectorViewModel) this.viewModel).getClickItem().observe(getViewLifecycleOwner(), new Observer() {
            @Override
            public void onChanged(Object o) {
                clickItem(((Integer)o).intValue());
            }
        });
        setClickListener();
        initAutoSwitch();
        initVerticalProjection();
        initSwitch();
    }

    private void initVerticalProjection() {
        boolean enableVertical = autoFocusUtils.getVerticalCorrectStatus();
        binding.VerticalProjectionInclude.switchAuto.setChecked(enableVertical);
        initCustomProjection(enableVertical);
    }
    private void initAutoSwitch(){
        boolean isAutoProjection = autoFocusUtils.getTrapezoidCorrectStatus();
        Log.i(TAG, "initAutoSwitch: 自动投影 ： " + isAutoProjection);
        boolean isAutoFocus = autoFocusUtils.getAutoFocusStatus();
        Log.i(TAG, "initAutoSwitch: 自动对焦 ： " + isAutoFocus);
        boolean isAutoBootFocus = autoFocusUtils.getPowerOnAutoFocusStatus();
        Log.i(TAG, "initAutoSwitch: 开机自动对焦 ： " + isAutoBootFocus);
        boolean isAutoOBS = autoFocusUtils.getAutoObstacleAvoidanceStatus();
        Log.i(TAG, "initAutoSwitch: 自动避障 ： " + isAutoOBS);
        boolean isAutoFitScreen = autoFocusUtils.getAutoComeAdmireStatus();
        Log.i(TAG, "initAutoSwitch: 自动入幕 ： " + isAutoFitScreen);
        initCustomProjection(isAutoProjection);

        binding.AutoProjectionInclude.switchAuto.setChecked(isAutoProjection);
        binding.AutoFocusInclude.switchAuto.setChecked(isAutoFocus);
        binding.BootAutoFocusIclude.switchAuto.setChecked(isAutoBootFocus);
        binding.AutoOBSIclude.switchAuto.setChecked(isAutoOBS);
        binding.AutoFitScreenIclude.switchAuto.setChecked(isAutoFitScreen);
    }

    private void initCustomProjection(boolean isAutoProjection){
        if (isAutoProjection){
            binding.twoPointInclude.contentTV.setTextColor(getResources().getColor(R.color.unselectable_color));
            binding.twoPointInclude.contentTVLeft.setVisibility(View.GONE);
            binding.fourPointInclude.contentTV.setTextColor(getResources().getColor(R.color.unselectable_color));
            binding.fourPointInclude.contentTVLeft.setVisibility(View.GONE);
            binding.twoPointInclude.itemRL.setFocusable(false);
            binding.fourPointInclude.itemRL.setFocusable(false);
        }else {
            if (binding.twoPointInclude.itemRL.isFocused()){
                binding.twoPointInclude.contentTV.setTextColor(getResources().getColor(R.color.text_red_new));
            }else {
                binding.twoPointInclude.contentTV.setTextColor(getResources().getColor(R.color.black));
            }
            binding.twoPointInclude.contentTVLeft.setVisibility(View.VISIBLE);
            if (binding.fourPointInclude.itemRL.isFocused()){
                binding.fourPointInclude.contentTV.setTextColor(getResources().getColor(R.color.text_red_new));
            }else {
                binding.fourPointInclude.contentTV.setTextColor(getResources().getColor(R.color.black));
            }
            binding.fourPointInclude.contentTVLeft.setVisibility(View.VISIBLE);
            binding.twoPointInclude.itemRL.setFocusable(true);
            binding.fourPointInclude.itemRL.setFocusable(true);
        }
        binding.twoPointInclude.itemRL.setOnFocusChangeListener(this::onFocusChange);
        binding.fourPointInclude.itemRL.setOnFocusChangeListener(this::onFocusChange);
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus){
            if (v == binding.twoPointInclude.itemRL){
                binding.twoPointInclude.contentTV.setTextColor(getResources().getColor(R.color.text_red_new));
            } else if (v == binding.fourPointInclude.itemRL) {
                binding.fourPointInclude.contentTV.setTextColor(getResources().getColor(R.color.text_red_new));
            }
        }else {
            if (v == binding.twoPointInclude.itemRL){
                binding.twoPointInclude.contentTV.setTextColor(getResources().getColor(R.color.black));
            } else if (v == binding.fourPointInclude.itemRL) {
                binding.fourPointInclude.contentTV.setTextColor(getResources().getColor(R.color.black));
            }
        }
    }
}
