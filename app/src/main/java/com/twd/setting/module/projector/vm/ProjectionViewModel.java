package com.twd.setting.module.projector.vm;

import android.app.Application;
import android.app.TwdManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.twd.setting.R;
import com.twd.setting.base.BaseViewModel;
import com.twd.setting.commonlibrary.Utils.event.SingleLiveEvent;
import com.twd.setting.module.systemequipment.repository.SysEquipmentRepository;
import com.twd.setting.utils.SystemPropertiesUtils;
import com.twd.setting.utils.binding.ItemLRTextIconData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class ProjectionViewModel extends BaseViewModel<SysEquipmentRepository> {
    public static final int ITEM_POS_POS = 1;
    public static final int ITEM_POS_NEG = 2;
    public static final int ITEM_NEG_POS = 3;
    public static final int ITEM_NEG_NEG = 4;
    private static final String PATH_CONTROL_MIPI = "persist.sys.projection";

    private static final String TAG = "ProjectionViewModel";
    private final MutableLiveData<Integer> _ClickItem = new SingleLiveEvent();
    private final MutableLiveData<Integer> _ShowLoadingDialog = new SingleLiveEvent();
    private final MutableLiveData<Boolean> _ShowOfflineUpdate = new SingleLiveEvent();
    private final OnClickListener _itemClickListener = new OnClickListener() {
        public void onClick(View view) {
            Log.d(TAG,"tag:"+view.getTag()+",id:"+view.getId());
            //SysEquipmentViewModel.this._ClickItem.postValue(Integer.valueOf(((Integer) view.getTag()).intValue()));
            _ClickItem.postValue(Integer.valueOf(((Integer) view.getId()).intValue()));
        }
    };
    private ItemLRTextIconData posPosData;
    private ItemLRTextIconData posNegData;
    private ItemLRTextIconData negPosData;
    private ItemLRTextIconData negNegData;
    public ProjectionViewModel(Application paramApplication) {
        super(paramApplication);
        initData(paramApplication);
    }

    public LiveData<Integer> getClickItem() {
        return _ClickItem;
    }

    public ItemLRTextIconData getPosPosData() {
        return posPosData;
    }

    public OnClickListener getItemClickListener() {
        return _itemClickListener;
    }

    public ItemLRTextIconData getPosNegData() {
        return posNegData;
    }

    public ItemLRTextIconData getNegPosData() {
        return negPosData;
    }

    public ItemLRTextIconData getNegNegData() {
        return negNegData;
    }

    public void initData(Application paramApplication) {
        int postion = getInitIcon();
        if(postion == 0){
            posPosData = new ItemLRTextIconData(1, paramApplication.getString(R.string.projection_pos_pos), null, 0, R.drawable.icon_projection_selected_black,View.GONE,View.VISIBLE);
            posNegData = new ItemLRTextIconData(2, paramApplication.getString(R.string.projection_pos_neg), null, 0, 0,View.GONE,View.VISIBLE);
            negPosData = new ItemLRTextIconData(3, paramApplication.getString(R.string.projection_neg_pos), null, 0, 0,View.GONE,View.VISIBLE);
            negNegData = new ItemLRTextIconData(4, paramApplication.getString(R.string.projection_neg_neg), null, 0, 0,View.GONE,View.VISIBLE);
        } else if (postion == 1) {
            posPosData = new ItemLRTextIconData(1, paramApplication.getString(R.string.projection_pos_pos), null, 0, 0,View.GONE,View.VISIBLE);
            posNegData = new ItemLRTextIconData(2, paramApplication.getString(R.string.projection_pos_neg), null, 0, R.drawable.icon_projection_selected_black,View.GONE,View.VISIBLE);
            negPosData = new ItemLRTextIconData(3, paramApplication.getString(R.string.projection_neg_pos), null, 0, 0,View.GONE,View.VISIBLE);
            negNegData = new ItemLRTextIconData(4, paramApplication.getString(R.string.projection_neg_neg), null, 0, 0,View.GONE,View.VISIBLE);
        } else if (postion == 2) {
            posPosData = new ItemLRTextIconData(1, paramApplication.getString(R.string.projection_pos_pos), null, 0, 0,View.GONE,View.VISIBLE);
            posNegData = new ItemLRTextIconData(2, paramApplication.getString(R.string.projection_pos_neg), null, 0, 0,View.GONE,View.VISIBLE);
            negPosData = new ItemLRTextIconData(3, paramApplication.getString(R.string.projection_neg_pos), null, 0, R.drawable.icon_projection_selected_black,View.GONE,View.VISIBLE);
            negNegData = new ItemLRTextIconData(4, paramApplication.getString(R.string.projection_neg_neg), null, 0, 0,View.GONE,View.VISIBLE);
        } else if (postion == 3) {
            posPosData = new ItemLRTextIconData(1, paramApplication.getString(R.string.projection_pos_pos), null, 0, 0,View.GONE,View.VISIBLE);
            posNegData = new ItemLRTextIconData(2, paramApplication.getString(R.string.projection_pos_neg), null, 0, 0,View.GONE,View.VISIBLE);
            negPosData = new ItemLRTextIconData(3, paramApplication.getString(R.string.projection_neg_pos), null, 0, 0,View.GONE,View.VISIBLE);
            negNegData = new ItemLRTextIconData(4, paramApplication.getString(R.string.projection_neg_neg), null, 0, R.drawable.icon_projection_selected_black,View.GONE,View.VISIBLE);
        }else {
            posPosData = new ItemLRTextIconData(1, paramApplication.getString(R.string.projection_pos_pos), null, 0, R.drawable.icon_projection_selected_black,View.GONE,View.VISIBLE);
            posNegData = new ItemLRTextIconData(2, paramApplication.getString(R.string.projection_pos_neg), null, 0, 0,View.GONE,View.VISIBLE);
            negPosData = new ItemLRTextIconData(3, paramApplication.getString(R.string.projection_neg_pos), null, 0, 0,View.GONE,View.VISIBLE);
            negNegData = new ItemLRTextIconData(4, paramApplication.getString(R.string.projection_neg_neg), null, 0, 0,View.GONE,View.VISIBLE);
        }

    }

    public LiveData<Boolean> isShowOfflineUpdate() {
        return _ShowOfflineUpdate;
    }

    public LiveData<Integer> showLoadingDialog() {
        return _ShowLoadingDialog;
    }

    public int getInitIcon(){
        return TwdManager.getInstance().getScreenMirror();
    }
}
