package com.twd.setting.module.projector.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.twd.setting.R;
import com.twd.setting.base.BaseBindingVmFragment;
import com.twd.setting.databinding.FragmentBrightnessBinding;
import com.twd.setting.module.projector.vm.BrightnessViewModel;
import com.twd.setting.utils.UiUtils;

import java.util.Objects;

/**
 * @Author:Yangxin
 * @Description:
 * @time: Create in 下午4:50 28/6/2025
 */
public class BrightnessFragment extends BaseBindingVmFragment<FragmentBrightnessBinding, BrightnessViewModel> {
    private static final String TAG = "BrightnessFragment";
    private int selectItem = 0;
    private Context context;
    private static final String PREFS_NAME = "IconSelectionPrefs";
    private static final String KEY_SELECTED_POSITION = "selectedPosition";

    private void clickItem(int item){
        Log.i(TAG, "clickItem: "+item);
        if (item == R.id.standardInclude){
            gotoStandard();
        } else if (item == R.id.highlightInclude) {
            gotoHighlight();
        } else if (item == R.id.energySaveInclude) {
            gotoEnergy();
        }else {

        }
    }

    private void gotoStandard(){
        //setBrightnessMode
        setBrightnessMode(context,204);
        setIconChange(0);
    }
    private void gotoHighlight(){
        setBrightnessMode(context,255);
        setIconChange(1);
    }
    private void gotoEnergy(){
        setBrightnessMode(context,153);
        setIconChange(2);
    }

    public static void setBrightnessMode(Context context,int brightness){
        //writeFIle
        Log.i("brightness", "setBrightnessMode:设置亮度 "+brightness);
        Settings.System.putInt(context.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS,brightness);
    }

    public void setIconChange(int position){
        Log.d(TAG, "setIconChange: "+position);
        binding.standardInclude.contentTVLeft.setImageResource(0);
        binding.highlightInclude.contentTVLeft.setImageResource(0);
        binding.energySaveInclude.contentTVLeft.setImageResource(0);
        if (position==0){
            binding.standardInclude.contentTVLeft.setImageResource(R.drawable.icon_projection_selected_black);
        } else if (position == 1) {
            binding.highlightInclude.contentTVLeft.setImageResource(R.drawable.icon_projection_selected_black);
        } else if (position == 2) {
            binding.energySaveInclude.contentTVLeft.setImageResource(R.drawable.icon_projection_selected_black);
        }

        SharedPreferences preferences = getContext().getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        preferences.edit().putInt(KEY_SELECTED_POSITION,position).apply();
    }

    private int getSelectItem(){
        SharedPreferences preferences = getActivity().getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        return preferences.getInt(KEY_SELECTED_POSITION,1);
    }

    private static boolean writeFile(String path,String content){
        return true;
    }

    public static BrightnessFragment newInstance(){
        return new BrightnessFragment();
    }
    private void setClickListener(){
        UiUtils.setOnClickListener(((FragmentBrightnessBinding)this.binding).standardInclude.itemRL,((BrightnessViewModel)this.viewModel).getItemClickListener());
        UiUtils.setOnClickListener(((FragmentBrightnessBinding)this.binding).highlightInclude.itemRL,((BrightnessViewModel)this.viewModel).getItemClickListener());
        UiUtils.setOnClickListener(((FragmentBrightnessBinding)this.binding).energySaveInclude.itemRL,((BrightnessViewModel)this.viewModel).getItemClickListener());
    }

    public int initLayout(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle){
        return R.layout.fragment_brightness;
    }

    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_brightness;
    }

    @Override
    public void onViewCreated(View paramView, Bundle paramBundle) {
        super.onViewCreated(paramView, paramBundle);
        ((FragmentBrightnessBinding)this.binding).setViewModel((BrightnessViewModel) this.viewModel);
        initTitle(paramView,R.string.projector_brightness_title);
        context = getContext();
        if (getSelectItem()==1){
            ((FragmentBrightnessBinding) this.binding).highlightInclude.itemRL.requestFocus();
        } else if (getSelectItem()==0) {
            ((FragmentBrightnessBinding) this.binding).standardInclude.itemRL.requestFocus();
        } else if (getSelectItem()==2) {
            ((FragmentBrightnessBinding) this.binding).energySaveInclude.itemRL.requestFocus();
        }


        ((BrightnessViewModel) this.viewModel).getClickItem().observe(getViewLifecycleOwner(), new Observer() {
            @Override
            public void onChanged(Object o) {
                clickItem(((Integer)o).intValue());
            }
        });
        setClickListener();
    }
}
