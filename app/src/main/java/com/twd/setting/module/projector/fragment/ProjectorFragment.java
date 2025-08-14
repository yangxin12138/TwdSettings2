package com.twd.setting.module.projector.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.Observer;

import com.twd.setting.R;
import com.twd.setting.base.BaseBindingVmFragment;
import com.twd.setting.databinding.FragmentProjectorBinding;
import com.twd.setting.module.projector.ProjectionActivity;
import com.twd.setting.module.projector.vm.ProjectorViewModel;
import com.twd.setting.utils.AutoFocusUtils;
import com.twd.setting.utils.SystemPropertiesUtils;
import com.twd.setting.utils.ToastUtils;
import com.twd.setting.utils.UiUtils;

public class ProjectorFragment extends BaseBindingVmFragment<FragmentProjectorBinding, ProjectorViewModel> implements View.OnFocusChangeListener {
    private static final String TAG = "ProjectorFragment";
    private int selectItem = 9;
    private AutoFocusUtils autoFocusUtils;
    private Context context;
    private static final String PREF_NAME = "MyAppPreferences";
    private static final String KEY_FIRST_OPEN = "isFirstOpen";
    private boolean vertical_focus;
    private String KYE_VERTICAL_FOCUS = "VERTICAL_FOCUS";
    private String KEY_BRIGHTNESS_ENABLE = "BRIGHTNESS_ENABLE";

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
        }else if (item == R.id.AutoProjectionInclude){//自动梯形
            //TODO:先判断自动对焦开没开
            if (vertical_focus || isAutoFocusOpen()) {
                gotoAutoProjection(binding.AutoProjectionInclude.switchAuto.isChecked());
            } else {
                ToastUtils.showCustomToast(context, context.getString(R.string.projector_auto_tip), Toast.LENGTH_SHORT);
            }
        } else if (item == R.id.AutoFocusInclude) {//自动对焦
            gotoAutoFocus(binding.AutoFocusInclude.switchAuto.isChecked());
        } else if (item == R.id.AutoOBSIclude) {//自动避障
            if (binding.AutoProjectionInclude.switchAuto.isChecked()){
                gotoAutoOBS(binding.AutoOBSIclude.switchAuto.isChecked());
            }else {
                ToastUtils.showCustomToast(context,context.getString(R.string.projector_auto_projector_tip),Toast.LENGTH_SHORT);
            }
        } else if (item == R.id.AutoFitScreenIclude) {//自动入幕
            if (isAutoFocusOpen()){
                gotoAutoFitScreen(binding.AutoFitScreenIclude.switchAuto.isChecked());
            }else {
                ToastUtils.showCustomToast(context,context.getString(R.string.projector_auto_tip),Toast.LENGTH_SHORT);
            }

        } else if (item == R.id.brightnessInclude) {
            gotoBrightness();
        }
    }

    private void gotoBrightness(){
        selectItem = 9;
        UiUtils.replaceFragmentHadBackStack(getParentFragmentManager(), 16908290, BrightnessFragment.newInstance(), "ProjectorFragment");
    }
    private boolean isAutoFocusOpen(){
        return binding.AutoFocusInclude.switchAuto.isChecked();
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
            gotoAutoOBS(true);
            if (vertical_focus) {
                autoFocusUtils.setVerticalCorrectEnable(false);
            } else {
                autoFocusUtils.setTrapezoidCorrectEnable(false);
            }
        }else {
            binding.twoPointInclude.contentTV.setTextColor(getResources().getColor(R.color.unselectable_color));
            binding.twoPointInclude.contentTVLeft.setVisibility(View.GONE);
            binding.fourPointInclude.contentTV.setTextColor(getResources().getColor(R.color.unselectable_color));
            binding.fourPointInclude.contentTVLeft.setVisibility(View.GONE);
            binding.twoPointInclude.itemRL.setFocusable(false);
            binding.fourPointInclude.itemRL.setFocusable(false);
            binding.AutoProjectionInclude.switchAuto.setChecked(true);
            if (vertical_focus) {
                autoFocusUtils.setVerticalCorrectEnable(true);
            } else {
                autoFocusUtils.setTrapezoidCorrectEnable(true);
            }
        }
    }
    private void gotoAutoFocus(boolean isChecked){//true
        selectItem = 5;
        boolean newCheckedState = !isChecked;
        if (!newCheckedState){
                binding.AutoFocusInclude.switchAuto.setChecked(false);
                binding.AutoProjectionInclude.switchAuto.setChecked(false);
                binding.AutoOBSIclude.switchAuto.setChecked(false);
                binding.AutoFitScreenIclude.switchAuto.setChecked(false);

                autoFocusUtils.setAutoFocusEnable(false);//关闭自动对焦
                gotoBootAutoFocus(true);
                gotoAutoProjection(true);
                gotoAutoOBS(true);
                gotoAutoFitScreen(true);

                binding.AutoProjectionInclude.itemRL.setVisibility(View.GONE);
                binding.AutoOBSIclude.itemRL.setVisibility(View.GONE);
                binding.AutoFitScreenIclude.itemRL.setVisibility(View.GONE);
        }else {
            binding.AutoFocusInclude.switchAuto.setChecked(true);
            autoFocusUtils.setAutoFocusEnable(true);
            gotoBootAutoFocus(false);

            binding.AutoProjectionInclude.itemRL.setVisibility(View.VISIBLE);
            binding.AutoOBSIclude.itemRL.setVisibility(View.VISIBLE);
            binding.AutoFitScreenIclude.itemRL.setVisibility(View.VISIBLE);
        }
    }

    private void gotoBootAutoFocus(boolean isChecked){
        selectItem = 6;
        boolean newCheckedState = !isChecked;
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

    public static ProjectorFragment newInstance() {
        return new ProjectorFragment();
    }

    private void setClickListener() {
        UiUtils.setOnClickListener(((FragmentProjectorBinding) this.binding).brightnessInclude.itemRL,((ProjectorViewModel) this.viewModel).getItemClickListener());
        UiUtils.setOnClickListener(((FragmentProjectorBinding) this.binding).twoPointInclude.itemRL, ((ProjectorViewModel) this.viewModel).getItemClickListener());
        UiUtils.setOnClickListener(((FragmentProjectorBinding) this.binding).fourPointInclude.itemRL, ((ProjectorViewModel) this.viewModel).getItemClickListener());
        UiUtils.setOnClickListener(((FragmentProjectorBinding) this.binding).sizeInclude.itemRL, ((ProjectorViewModel) this.viewModel).getItemClickListener());
        UiUtils.setOnClickListener(((FragmentProjectorBinding) this.binding).projectionInclude.itemRL, ((ProjectorViewModel) this.viewModel).getItemClickListener());
        UiUtils.setOnClickListener(((FragmentProjectorBinding) this.binding).AutoProjectionInclude.itemRL,((ProjectorViewModel) this.viewModel).getItemClickListener());
        UiUtils.setOnClickListener(((FragmentProjectorBinding) this.binding).AutoFocusInclude.itemRL,((ProjectorViewModel) this.viewModel).getItemClickListener());
        UiUtils.setOnClickListener(((FragmentProjectorBinding) this.binding).AutoOBSIclude.itemRL,((ProjectorViewModel) this.viewModel).getItemClickListener());
        UiUtils.setOnClickListener(((FragmentProjectorBinding) this.binding).AutoFitScreenIclude.itemRL,((ProjectorViewModel) this.viewModel).getItemClickListener());
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
        }  else if (selectItem == 7) {
            binding.AutoOBSIclude.itemRL.requestFocus();
        } else if (selectItem == 8) {
            binding.AutoFitScreenIclude.itemRL.requestFocus();
        } else if (selectItem == 9) {
            binding.brightnessInclude.itemRL.requestFocus();
        }

        initAutoSwitch();
    }

    public void onViewCreated(View paramView, Bundle paramBundle) {
        super.onViewCreated(paramView, paramBundle);
        ((FragmentProjectorBinding) this.binding).setViewModel((ProjectorViewModel) this.viewModel);
        initTitle(paramView, R.string.projector_title);
        context = requireContext();
        autoFocusUtils = new AutoFocusUtils();
        vertical_focus = Boolean.parseBoolean(SystemPropertiesUtils.readSystemProp(KYE_VERTICAL_FOCUS));
        ((ProjectorViewModel) this.viewModel).getClickItem().observe(getViewLifecycleOwner(), new Observer() {
            @Override
            public void onChanged(Object o) {
                clickItem(((Integer)o).intValue());
            }
        });
        setClickListener();
        initAutoSwitch();
        if (isFirstOpen(context)){
            ProjectionFragment.setProjectionMode(0);
        }
    }

    private void initAutoSwitch(){
        binding.brightnessInclude.itemRL.setVisibility(
                Boolean.parseBoolean(SystemPropertiesUtils.readSystemProp(KEY_BRIGHTNESS_ENABLE)) ? View.VISIBLE : View.GONE);

        binding.AutoFocusInclude.itemRL.setVisibility(vertical_focus ? View.GONE : View.VISIBLE);
        binding.AutoOBSIclude.itemRL.setVisibility(vertical_focus ? View.GONE : View.VISIBLE);
        binding.AutoFitScreenIclude.itemRL.setVisibility(vertical_focus ? View.GONE : View.VISIBLE);

        boolean isAutoFocus = autoFocusUtils.getAutoFocusStatus();
        Log.i(TAG, "initAutoSwitch: 自动对焦 ： " + isAutoFocus);
        if (!isAutoFocus){//如果自动对焦是关闭状态，那就关闭自动梯形，入幕，避障
            binding.AutoProjectionInclude.itemRL.setVisibility(View.GONE);
            binding.AutoOBSIclude.itemRL.setVisibility(View.GONE);
            binding.AutoFitScreenIclude.itemRL.setVisibility(View.GONE);

            gotoAutoProjection(true);
            gotoAutoFitScreen(true);
            gotoAutoOBS(true);

        }
        boolean isAutoProjection = vertical_focus ? autoFocusUtils.getVerticalCorrectStatus() : autoFocusUtils.getTrapezoidCorrectStatus();
        Log.i(TAG, "initAutoSwitch: 自动投影 ： " + isAutoProjection);
        boolean isAutoBootFocus = autoFocusUtils.getPowerOnAutoFocusStatus();
        Log.i(TAG, "initAutoSwitch: 开机自动对焦 ： " + isAutoBootFocus);
        boolean isAutoOBS = autoFocusUtils.getAutoObstacleAvoidanceStatus();
        Log.i(TAG, "initAutoSwitch: 自动避障 ： " + isAutoOBS);
        boolean isAutoFitScreen = autoFocusUtils.getAutoComeAdmireStatus();
        Log.i(TAG, "initAutoSwitch: 自动入幕 ： " + isAutoFitScreen);
        initCustomProjection(isAutoProjection);

        binding.AutoProjectionInclude.switchAuto.setChecked(isAutoProjection);
        if (!vertical_focus){
            binding.AutoFocusInclude.switchAuto.setChecked(isAutoFocus);
            binding.AutoOBSIclude.switchAuto.setChecked(isAutoOBS);
            binding.AutoFitScreenIclude.switchAuto.setChecked(isAutoFitScreen);
        }
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

    public static boolean isFirstOpen(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean isFirst = sharedPreferences.getBoolean(KEY_FIRST_OPEN, true);
        if (isFirst) {
            // 如果是第一次打开，将其标记为已打开
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(KEY_FIRST_OPEN, false);
            editor.apply();
        }
        return isFirst;
    }
}
