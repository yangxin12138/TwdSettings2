package com.twd.setting.module.projector.vm;

import android.app.Application;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.twd.setting.R;
import com.twd.setting.base.BaseViewModel;
import com.twd.setting.commonlibrary.Utils.event.SingleLiveEvent;
import com.twd.setting.module.systemequipment.repository.SysEquipmentRepository;
import com.twd.setting.utils.binding.ItemLRTextIconData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * @Author:Yangxin
 * @Description:
 * @time: Create in 下午5:01 28/6/2025
 */
public class BrightnessViewModel extends BaseViewModel<SysEquipmentRepository> {
    public static final int ITEM_STANDARD = 1;
    public static final int ITEM_HIGHLIGHT = 2;
    public static final int ITEM_ENERGY = 3;

    private static final String TAG = "BrightnessViewModel";
    private final MutableLiveData<Integer> _ClickItem = new SingleLiveEvent();
    private final MutableLiveData<Integer> _ShowLoadingDialog = new SingleLiveEvent();
    private final MutableLiveData<Boolean> _ShowOfflineUpdate = new SingleLiveEvent();
    private final View.OnClickListener _itemClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            Log.d(TAG,"tag:"+view.getTag()+",id:"+view.getId());
            //SysEquipmentViewModel.this._ClickItem.postValue(Integer.valueOf(((Integer) view.getTag()).intValue()));
            _ClickItem.postValue(Integer.valueOf(((Integer) view.getId()).intValue()));
        }
    };

    private ItemLRTextIconData standardData;
    private ItemLRTextIconData highlightData;
    private ItemLRTextIconData energyData;
    public BrightnessViewModel(@NonNull Application application) {
        super(application);
        initData(application);
    }
    public LiveData<Integer> getClickItem() {
        return _ClickItem;
    }
    public View.OnClickListener getItemClickListener() {
        return _itemClickListener;
    }
    public ItemLRTextIconData getStandardData() {
        return standardData;
    }

    public ItemLRTextIconData getHighlightData() {
        return highlightData;
    }

    public ItemLRTextIconData getEnergyData() {
        return energyData;
    }

    public void initData(Application paramApplication){
        int position = getInitIcon(paramApplication);
        if (position == 0){
            standardData = new ItemLRTextIconData(1,paramApplication.getString(R.string.brightness_standard_title),null,0,R.drawable.icon_projection_selected_black,View.GONE,View.VISIBLE);
            highlightData = new ItemLRTextIconData(2,paramApplication.getString(R.string.brightness_Highlight_title),null,0,0,View.GONE,View.VISIBLE);
            energyData = new ItemLRTextIconData(3,paramApplication.getString(R.string.brightness_energy_title),null,0,0,View.GONE,View.VISIBLE);
        } else if (position == 1) {
            standardData = new ItemLRTextIconData(1,paramApplication.getString(R.string.brightness_standard_title),null,0,0,View.GONE,View.VISIBLE);
            highlightData = new ItemLRTextIconData(2,paramApplication.getString(R.string.brightness_Highlight_title),null,0,R.drawable.icon_projection_selected_black,View.GONE,View.VISIBLE);
            energyData = new ItemLRTextIconData(3,paramApplication.getString(R.string.brightness_energy_title),null,0,0,View.GONE,View.VISIBLE);
        } else if (position == 2) {
            standardData = new ItemLRTextIconData(1,paramApplication.getString(R.string.brightness_standard_title),null,0,0,View.GONE,View.VISIBLE);
            highlightData = new ItemLRTextIconData(2,paramApplication.getString(R.string.brightness_Highlight_title),null,0,0,View.GONE,View.VISIBLE);
            energyData = new ItemLRTextIconData(3,paramApplication.getString(R.string.brightness_energy_title),null,0,R.drawable.icon_projection_selected_black,View.GONE,View.VISIBLE);
        }
    }

    public int getInitIcon(Application application){
        //获取亮度当前设置
        int current_brightness = getScreenBrightness(application.getApplicationContext());
        Log.i("brightness", "getInitIcon: 当前的亮度是 "+current_brightness);
        if (current_brightness <= 153){
            //节能模式
            return 2;
        } else if (current_brightness <= 204) {
            //标准模式
            return 0;
        } else {
            //高亮模式
            return 1;
        }
    }

    private int getScreenBrightness(Context context){
        try{
            return Settings.System.getInt(context.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS);
        }catch (Settings.SettingNotFoundException e){
            e.printStackTrace();
            return -1;
        }
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
