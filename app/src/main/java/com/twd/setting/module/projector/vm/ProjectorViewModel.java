package com.twd.setting.module.projector.vm;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.twd.setting.R;
import com.twd.setting.base.BaseViewModel;
import com.twd.setting.commonlibrary.Utils.event.SingleLiveEvent;
import com.twd.setting.module.systemequipment.repository.SysEquipmentRepository;
import com.twd.setting.utils.SystemPropertiesUtils;
import com.twd.setting.utils.binding.ItemLRTextIconData;

import android.app.Application;
import android.app.TwdManager;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class ProjectorViewModel extends BaseViewModel<SysEquipmentRepository> {
    public static final int ITEM_TWO_POINT = 1;
    public static final int ITEM_SINGLE_POINT = 2;
    public static final int ITEM_SIZE = 3;
    public static final int ITEM_PROJECTION= 4;
    private static final String PATH_CONTROL_MIPI = "persist.sys.projection";
/*    private static final String PATH_DEV_PRO_INFO = "/dev/pro_info";
    private static final String PATH_DEV_PRO_INFO2 = "/dev/block/mmcblk0p1";*/

    private static final String TAG = "ProjectorViewModel";
    private final MutableLiveData<Integer> _ClickItem = new SingleLiveEvent();

    private final OnClickListener _itemClickListener = new OnClickListener() {
        public void onClick(View view) {
            Log.d(TAG,"tag:"+view.getTag()+",id:"+view.getId());
            _ClickItem.postValue(Integer.valueOf(((Integer) view.getId()).intValue()));
        }
    };
    private ItemLRTextIconData autoProjectionData;
    private ItemLRTextIconData verticalProjectionData;
    private ItemLRTextIconData autoFocusData;
    private ItemLRTextIconData bootAutoFocusData;
    private ItemLRTextIconData autoOBSData;
    private ItemLRTextIconData autoFitScreenData;
    private ItemLRTextIconData twoPointData;
    private ItemLRTextIconData fourPointData;
    private ItemLRTextIconData sizeData;
    private ItemLRTextIconData projectionData;

    public ProjectorViewModel(Application paramApplication) {
        super(paramApplication);
        initData(paramApplication);
    }

    public LiveData<Integer> getClickItem() {
        return _ClickItem;
    }

    public ItemLRTextIconData getTwoPointData() {
        return twoPointData;
    }

    public OnClickListener getItemClickListener() {
        return _itemClickListener;
    }

    public ItemLRTextIconData getFourPointData() {
        return fourPointData;
    }

    public ItemLRTextIconData getSizeData() {
        return sizeData;
    }

    public ItemLRTextIconData getProjectionData() {
        return projectionData;
    }

    public ItemLRTextIconData getAutoProjectionData() {
        return autoProjectionData;
    }

    public ItemLRTextIconData getAutoFocusData() {
        return autoFocusData;
    }

    public ItemLRTextIconData getBootAutoFocusData() {
        return bootAutoFocusData;
    }

    public ItemLRTextIconData getAutoOBSData() {
        return autoOBSData;
    }

    public ItemLRTextIconData getAutoFitScreenData() {
        return autoFitScreenData;
    }

    public ItemLRTextIconData getVerticalProjectionData() {
        return verticalProjectionData;
    }

    public void setVerticalProjectionData(ItemLRTextIconData verticalProjectionData) {
        this.verticalProjectionData = verticalProjectionData;
    }

    public void initData(Application paramApplication) {
        int postion = getProjectionItem();
        if(postion == 0){
            projectionData = new ItemLRTextIconData(4, paramApplication.getString(R.string.projector_projection_title), paramApplication.getString(R.string.projection_pos_pos), 0, R.drawable.ic_baseline_arrow_forward_ios_24,View.GONE,View.VISIBLE);
        } else if (postion == 1) {
            projectionData = new ItemLRTextIconData(4, paramApplication.getString(R.string.projector_projection_title), paramApplication.getString(R.string.projection_pos_neg), 0, R.drawable.ic_baseline_arrow_forward_ios_24,View.GONE,View.VISIBLE);
        } else if (postion == 2) {
            projectionData = new ItemLRTextIconData(4, paramApplication.getString(R.string.projector_projection_title), paramApplication.getString(R.string.projection_neg_pos), 0, R.drawable.ic_baseline_arrow_forward_ios_24,View.GONE,View.VISIBLE);
        } else if (postion == 3) {
            projectionData = new ItemLRTextIconData(4, paramApplication.getString(R.string.projector_projection_title), paramApplication.getString(R.string.projection_neg_neg), 0, R.drawable.ic_baseline_arrow_forward_ios_24,View.GONE,View.VISIBLE);
        }else {
            projectionData = new ItemLRTextIconData(4, paramApplication.getString(R.string.projector_projection_title), null, 0, R.drawable.ic_baseline_arrow_forward_ios_24,View.GONE,View.VISIBLE);
        }
        twoPointData = new ItemLRTextIconData(1, paramApplication.getString(R.string.projector_two_point_title), null, 0, R.drawable.ic_baseline_arrow_forward_ios_24,View.GONE,View.VISIBLE);
        fourPointData = new ItemLRTextIconData(2, paramApplication.getString(R.string.projector_four_point_title), null, 0, R.drawable.ic_baseline_arrow_forward_ios_24,View.GONE,View.VISIBLE);
        sizeData = new ItemLRTextIconData(3, paramApplication.getString(R.string.projector_size_title), null, 0, R.drawable.ic_baseline_arrow_forward_ios_24,View.GONE,View.VISIBLE);
        autoProjectionData = new ItemLRTextIconData(5,paramApplication.getString(R.string.projector_auto_projection_title),null,0,R.drawable.ic_baseline_arrow_forward_ios_24,View.VISIBLE,View.GONE);
        verticalProjectionData = new ItemLRTextIconData(5,paramApplication.getString(R.string.projector_auto_title),null,0,R.drawable.ic_baseline_arrow_forward_ios_24,View.VISIBLE,View.GONE);
        autoFocusData = new ItemLRTextIconData(5,paramApplication.getString(R.string.projector_auto_focus_title),null,0,R.drawable.ic_baseline_arrow_forward_ios_24,View.VISIBLE,View.GONE);
        bootAutoFocusData = new ItemLRTextIconData(5,paramApplication.getString(R.string.projector_boot_auto_focus_title),null,0,R.drawable.ic_baseline_arrow_forward_ios_24,View.VISIBLE,View.GONE);
        autoOBSData = new ItemLRTextIconData(5,paramApplication.getString(R.string.projector_auto_obs_title),null,0,R.drawable.ic_baseline_arrow_forward_ios_24,View.VISIBLE,View.GONE);
        autoFitScreenData = new ItemLRTextIconData(5,paramApplication.getString(R.string.projector_auto_fit_screen_title),null,0,R.drawable.ic_baseline_arrow_forward_ios_24,View.VISIBLE,View.GONE);

    }

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        super.onResume(owner);
        int postion = getProjectionItem();
        if(postion == 0){
            projectionData.setRightTxt(getApplication().getString(R.string.projection_pos_pos));
        } else if (postion == 1) {
            projectionData.setRightTxt(getApplication().getString(R.string.projection_pos_neg));
        } else if (postion == 2) {
            projectionData.setRightTxt(getApplication().getString(R.string.projection_neg_pos));
        } else if (postion == 3) {
            projectionData.setRightTxt(getApplication().getString(R.string.projection_neg_neg));
        }else {
            projectionData.setRightTxt(getApplication().getString(R.string.projection_pos_pos));
        }
    }

    public int getProjectionItem(){
        int ret = 0;
        ret = TwdManager.getInstance().getScreenMirror();
        return ret;
    }
}
