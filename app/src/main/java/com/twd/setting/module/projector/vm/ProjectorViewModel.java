package com.twd.setting.module.projector.vm;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.twd.setting.R;
import com.twd.setting.base.BaseViewModel;
import com.twd.setting.commonlibrary.Utils.event.SingleLiveEvent;
import com.twd.setting.module.systemequipment.repository.SysEquipmentRepository;
import com.twd.setting.utils.binding.ItemLRTextIconData;

import android.app.Application;
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
    private static final String PATH_CONTROL_MIPI = "/sys/ir/control_mipi";
    private static final String PATH_DEV_PRO_INFO = "/dev/pro_info";
    private static final String PATH_DEV_PRO_INFO2 = "/dev/block/mmcblk0p1";

    private static final String TAG = "ProjectorViewModel";
    private final MutableLiveData<Integer> _ClickItem = new SingleLiveEvent();

    private final OnClickListener _itemClickListener = new OnClickListener() {
        public void onClick(View view) {
            Log.d(TAG,"tag:"+view.getTag()+",id:"+view.getId());
            _ClickItem.postValue(Integer.valueOf(((Integer) view.getId()).intValue()));
        }
    };
    private ItemLRTextIconData twoPointData;
    private ItemLRTextIconData fourPointData;
    private ItemLRTextIconData sizeData;
    private ItemLRTextIconData projectionData;

    //private int progress;

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

    /*
    public String getProgress(){
        return "Level: "+progress;
    }

    public void setProgress(int i){
        progress = i;
    }*/

    public void initData(Application paramApplication) {
        int postion = getProjectionItem();
        if(postion == 0){
            projectionData = new ItemLRTextIconData(4, paramApplication.getString(R.string.projector_projection_title), paramApplication.getString(R.string.projection_pos_pos), 0, R.drawable.ic_baseline_arrow_forward_ios_24);
        } else if (postion == 1) {
            projectionData = new ItemLRTextIconData(4, paramApplication.getString(R.string.projector_projection_title), paramApplication.getString(R.string.projection_pos_neg), 0, R.drawable.ic_baseline_arrow_forward_ios_24);
        } else if (postion == 2) {
            projectionData = new ItemLRTextIconData(4, paramApplication.getString(R.string.projector_projection_title), paramApplication.getString(R.string.projection_neg_pos), 0, R.drawable.ic_baseline_arrow_forward_ios_24);
        } else if (postion == 3) {
            projectionData = new ItemLRTextIconData(4, paramApplication.getString(R.string.projector_projection_title), paramApplication.getString(R.string.projection_neg_neg), 0, R.drawable.ic_baseline_arrow_forward_ios_24);
        }else {
            projectionData = new ItemLRTextIconData(4, paramApplication.getString(R.string.projector_projection_title), null, 0, R.drawable.ic_baseline_arrow_forward_ios_24);
        }
        twoPointData = new ItemLRTextIconData(1, paramApplication.getString(R.string.projector_two_point_title), null, 0, R.drawable.ic_baseline_arrow_forward_ios_24);
        fourPointData = new ItemLRTextIconData(2, paramApplication.getString(R.string.projector_four_point_title), null, 0, R.drawable.ic_baseline_arrow_forward_ios_24);
        sizeData = new ItemLRTextIconData(3, paramApplication.getString(R.string.projector_size_title), null, 0, R.drawable.ic_baseline_arrow_forward_ios_24);


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
            projectionData.setRightTxt(null);
        }
    }

    public int getProjectionItem(){
        int ret = 0;
        ret = readProjectionValue(PATH_CONTROL_MIPI);//readProjectionValue(PATH_DEV_PRO_INFO2);
        if(ret == 0){
            if(Build.HARDWARE.equals("mt6735")){
                ret = readProjectionValue(PATH_DEV_PRO_INFO2);
            }else {
                ret = readProjectionValue(PATH_DEV_PRO_INFO);
            }
        }
        return ret;
    }

    private static int readProjectionValue(String path) {
        File file = new File(path);
        if (file.exists()) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                int read = reader.read();
                Log.d(TAG, "read " + path + ": " + read);
                if (read != -1) {
                    return read - '0';
                }
            } catch (Exception e) {
                Log.e(TAG, "Read " + path + ": error", e);
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
            }
        } else {
            Log.w(TAG, path + " is not exist");
        }
        Log.i(TAG, "read " + path + ": defalut 0");
        return 0;
    }
}
