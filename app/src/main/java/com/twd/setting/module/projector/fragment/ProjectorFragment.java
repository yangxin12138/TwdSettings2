package com.twd.setting.module.projector.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private int selectItem = 0;
    private AutoFocusUtils autoFocusUtils;
    private Context context;
    private static final String PREF_NAME = "MyAppPreferences";
    private static final String KEY_FIRST_OPEN = "isFirstOpen";
    private boolean vertical_focus;
    private String KYE_VERTICAL_FOCUS = "VERTICAL_FOCUS";


    private void clickItem(int item) {
        if(item == R.id.twoPointInclude){//1
            gotoTwoPoint();
        }else if(item == R.id.fourPointInclude){//2
            gotoFourPoint();
        }else if(item == R.id.sizeInclude){//3
            gotoSize();
        }else if(item == R.id.projectionInclude){//4
            gotoProjection();
        }else if (item == R.id.AutoProjectionInclude){//自动梯形矫正
            //TODO:先先判断自动对焦开没开
            if (vertical_focus || isAutoFocusOpen()){
                gotoAutoProjection(binding.AutoProjectionInclude.switchAuto.isChecked());
            }else {
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
        } else if (item == R.id.AutoFitScreenIclude) {//自动入幕  打开自动入幕会隐藏自动梯形和自动避障，并且把这两个设置false，关闭自动入幕会显示这两个菜单
            if (isAutoFocusOpen()){
                gotoAutoFitScreen(binding.AutoFitScreenIclude.switchAuto.isChecked());
            }else {
                ToastUtils.showCustomToast(context,context.getString(R.string.projector_auto_tip),Toast.LENGTH_SHORT);
            }
        }
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
    private void gotoAutoFocus(boolean isChecked){
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

    private void gotoAutoProjection(boolean isChecked){//false
        selectItem = 4;
        if (isChecked){//原本是选中，自动模式，点击后变成手动模式
            binding.twoPointInclude.itemRL.setVisibility(View.VISIBLE);
            binding.fourPointInclude.itemRL.setVisibility(View.VISIBLE);
            binding.AutoProjectionInclude.switchAuto.setChecked(false);
            Log.d(TAG, "gotoAutoProjection: 关闭自动梯形 ，走手动模式");
            gotoAutoOBS(true);
            if (vertical_focus){
                Log.d(TAG, "gotoAutoProjection: 手动模式 -1");
                autoFocusUtils.setVerticalCorrectEnable(false);
            } else {
                Log.d(TAG, "gotoAutoProjection: 手动模式 -2");
                autoFocusUtils.setTrapezoidCorrectEnable(false);
            }
        }else {//原本是未选中，手动模式，点击后变成自动模式
            Log.i(TAG, "gotoAuto: 开启switch,走自动模式");
            binding.twoPointInclude.itemRL.setVisibility(View.GONE);
            binding.fourPointInclude.itemRL.setVisibility(View.GONE);
            binding.AutoProjectionInclude.switchAuto.setChecked(true);
            if (vertical_focus) {
                autoFocusUtils.setVerticalCorrectEnable(true);
            } else {
                autoFocusUtils.setTrapezoidCorrectEnable(true);
            }
        }

    }

    private void gotoBootAutoFocus(boolean isChecked){
        selectItem = 6;
        boolean newCheckedState = !isChecked;
        autoFocusUtils.setPowerOnAutoFocusEnable(newCheckedState);
    }

    private void gotoAutoOBS(boolean isChecked){//true
        selectItem = 7;
        boolean newCheckedState = !isChecked;
        binding.AutoOBSIclude.switchAuto.setChecked(newCheckedState);
        autoFocusUtils.setAutoObstacleAvoidanceEnable(newCheckedState);
    }

    private void gotoAutoFitScreen(boolean isChecked){//true
        selectItem = 8;
        boolean newCheckedState = !isChecked;
        binding.AutoFitScreenIclude.switchAuto.setChecked(newCheckedState);
        if (newCheckedState){
            gotoAutoProjection(true);
            gotoAutoOBS(true);
            binding.AutoProjectionInclude.itemRL.setVisibility(View.GONE);
            binding.AutoOBSIclude.itemRL.setVisibility(View.GONE);
        }else {
            binding.AutoProjectionInclude.itemRL.setVisibility(View.VISIBLE);
            binding.AutoOBSIclude.itemRL.setVisibility(View.VISIBLE);
        }
        autoFocusUtils.setAutoComeAdmireEnable(newCheckedState);
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
        initAutoSwitch();
        if(selectItem == 0){
            if(binding.twoPointInclude.contentTVLeft.getVisibility() == View.VISIBLE){
                binding.twoPointInclude.itemRL.requestFocus();
            }else {
                binding.sizeInclude.itemRL.requestFocus();
            }
        } else if (selectItem ==1) {
            binding.fourPointInclude.itemRL.requestFocus();
        } else if (selectItem ==2) {
            binding.sizeInclude.itemRL.requestFocus();
        } else if (selectItem ==3) {
            binding.projectionInclude.itemRL.requestFocus();
        }else if (selectItem == 4) {
            binding.AutoProjectionInclude.itemRL.requestFocus();
        } else if (selectItem == 5) {
            binding.AutoFocusInclude.itemRL.requestFocus();
        } else if (selectItem == 7) {
            binding.AutoOBSIclude.itemRL.requestFocus();
        } else if (selectItem == 8) {
            binding.AutoFitScreenIclude.itemRL.requestFocus();
        }
    }

    public void onViewCreated(View paramView, Bundle paramBundle) {
        super.onViewCreated(paramView, paramBundle);
        ((FragmentProjectorBinding) this.binding).setViewModel((ProjectorViewModel) this.viewModel);
        initTitle(paramView, R.string.projector_title);
        context = requireContext();
        autoFocusUtils = new AutoFocusUtils();
        vertical_focus = Boolean.parseBoolean(SystemPropertiesUtils.readSystemProp(KYE_VERTICAL_FOCUS));
        Log.d(TAG, "onViewCreated: vertical_focus = " +vertical_focus);
        ((FragmentProjectorBinding) this.binding).twoPointInclude.itemRL.requestFocus();

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
        binding.AutoFocusInclude.itemRL.setVisibility(vertical_focus ? View.GONE : View.VISIBLE);
        binding.AutoOBSIclude.itemRL.setVisibility(vertical_focus ? View.GONE : View.VISIBLE);
        binding.AutoFitScreenIclude.itemRL.setVisibility(vertical_focus ? View.GONE : View.VISIBLE);

        boolean isAutoFocus = autoFocusUtils.getAutoFocusStatus();
        Log.i(TAG, "initAutoSwitch: 自动对焦 ： " + isAutoFocus);
        boolean isAutoFitScreen = autoFocusUtils.getAutoComeAdmireStatus();
        Log.i(TAG, "initAutoSwitch: 自动入幕 ： " + isAutoFitScreen);
        if (!isAutoFocus){//如果自动对焦是关闭状态，那就关闭自动梯形，入幕，避障
            binding.AutoProjectionInclude.itemRL.setVisibility(View.GONE);
            binding.AutoOBSIclude.itemRL.setVisibility(View.GONE);
            binding.AutoFitScreenIclude.itemRL.setVisibility(View.GONE);

            Log.d(TAG, "initAutoSwitch: 关闭自动梯形，入幕，避障");
            gotoAutoProjection(true);
            autoFocusUtils.setAutoComeAdmireEnable(false);

        } else if (isAutoFitScreen) {
            binding.AutoProjectionInclude.itemRL.setVisibility(View.GONE);
            binding.AutoOBSIclude.itemRL.setVisibility(View.GONE);
        }
        boolean isAutoProjection = vertical_focus ? autoFocusUtils.getVerticalCorrectStatus() : autoFocusUtils.getTrapezoidCorrectStatus();
        Log.i(TAG, "initAutoSwitch: 自动投影 ： " + isAutoProjection);
        boolean isAutoBootFocus = autoFocusUtils.getPowerOnAutoFocusStatus();
        Log.i(TAG, "initAutoSwitch: 开机自动对焦 ： " + isAutoBootFocus);
        boolean isAutoOBS = autoFocusUtils.getAutoObstacleAvoidanceStatus();
        Log.i(TAG, "initAutoSwitch: 自动避障 ： " + isAutoOBS);

        initCustomProjection(isAutoProjection);
        binding.AutoProjectionInclude.switchAuto.setChecked(isAutoProjection);
        if (!vertical_focus){
            binding.AutoFocusInclude.switchAuto.setChecked(isAutoFocus);
            binding.AutoOBSIclude.switchAuto.setChecked(isAutoOBS);
            binding.AutoFitScreenIclude.switchAuto.setChecked(isAutoFitScreen);
        }
        binding.projectionInclude.itemRL.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                binding.projectionInclude.rightTV.setSelected(hasFocus);
            }
        });
    }

    private void initCustomProjection(boolean isAutoProjection){
        if (isAutoProjection){
                binding.twoPointInclude.itemRL.setVisibility(View.GONE);
                binding.fourPointInclude.itemRL.setVisibility(View.GONE);
        }else {
                binding.twoPointInclude.itemRL.setVisibility(View.VISIBLE);
                binding.fourPointInclude.itemRL.setVisibility(View.VISIBLE);
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
