package com.twd.setting.module.systemequipment.vm;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

//import com.twd.setting.SettingConfig;
import com.twd.setting.R;
import com.twd.setting.base.observer.MyObserver;
import com.twd.setting.base.BaseViewModel;
import com.twd.setting.base.IDispose;
import com.twd.setting.commonlibrary.Utils.StringUtils;
import com.twd.setting.commonlibrary.Utils.ToastUtils;
import com.twd.setting.commonlibrary.Utils.event.SingleLiveEvent;
import com.twd.setting.module.systemequipment.repository.SysEquipmentRepository;
import com.twd.setting.utils.HLog;
import com.twd.setting.utils.KkUtils;
import com.twd.setting.utils.binding.ItemLRTextIconData;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

import java.io.File;

public class SysEquipmentViewModel
        extends BaseViewModel<SysEquipmentRepository> {
    public static final int ITEM_DEVICE_INFORMATION = 3;
    public static final int ITEM_OFFLINE_UPDATE = 2;
    public static final int ITEM_ONLINE_UPDATE = 1;
    public static final int ITEM_RESET_SYSTEM = 5;
    public static final int ITEM_STORAGE_DETAIL = 4;
    private static final String TAG = "SysEquipmentViewModel";
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
    private ItemLRTextIconData deviceInformationData;
    private ItemLRTextIconData offLineUpdateData;
    private ItemLRTextIconData onLineUpdateData;
    private ItemLRTextIconData resetSystemData;
    private ItemLRTextIconData storageDetailData;

    public SysEquipmentViewModel(Application paramApplication) {
        super(paramApplication);
        initData(paramApplication);
    }

    public LiveData<Integer> getClickItem() {
        return _ClickItem;
    }

    public ItemLRTextIconData getDeviceInformationData() {
        return deviceInformationData;
    }

    public View.OnClickListener getItemClickListener() {
        return _itemClickListener;
    }

    public ItemLRTextIconData getOffLineUpdateData() {
        return offLineUpdateData;
    }

    public ItemLRTextIconData getOnLineUpdateData() {
        return onLineUpdateData;
    }

    public ItemLRTextIconData getResetSystemData() {
        return resetSystemData;
    }

    public ItemLRTextIconData getStorageDetailData() {
        return storageDetailData;
    }


    public void initData(Application paramApplication) {
        //if (onLineUpdateData != null) {
        //    return;
        //}
        Log.d(TAG,"initData");
        String str = KkUtils.getSysVersion(paramApplication);
        Object localObject = str;
        if (!StringUtils.isTrimEmpty(str)) {
            localObject = new StringBuilder();
            ((StringBuilder) localObject).append("YIUI OS ");
            ((StringBuilder) localObject).append(str);
            localObject = ((StringBuilder) localObject).toString();
        }
        onLineUpdateData = new ItemLRTextIconData(1, paramApplication.getString(R.string.str_online_update), (String) localObject, 0, R.drawable.ic_baseline_arrow_forward_ios_24);
        offLineUpdateData = new ItemLRTextIconData(2, paramApplication.getString(R.string.str_offline_update), null, 0, R.drawable.ic_baseline_arrow_forward_ios_24);
        deviceInformationData = new ItemLRTextIconData(3, paramApplication.getString(R.string.str_device_information), null, 0, R.drawable.ic_baseline_arrow_forward_ios_24);
        storageDetailData = new ItemLRTextIconData(4, paramApplication.getString(R.string.str_storage_detail), null, 0, R.drawable.ic_baseline_arrow_forward_ios_24);
        resetSystemData = new ItemLRTextIconData(5, paramApplication.getString(R.string.str_reset_system), null, 0, R.drawable.ic_baseline_arrow_forward_ios_24);
        if (model != null) {
            ((SysEquipmentRepository) model).getSupportOffLineUpdateStatus(paramApplication).subscribe(new MyObserver(this) {
                @Override
                public void onNext(Object value) {
                    SysEquipmentViewModel.this._ShowOfflineUpdate.postValue((Boolean)value);
                }

                public void onError(Throwable paramAnonymousThrowable) {
                    SysEquipmentViewModel.this._ShowOfflineUpdate.postValue(Boolean.valueOf(false));
                }
            });
        }
    }

    public LiveData<Boolean> isShowOfflineUpdate() {
        return _ShowOfflineUpdate;
    }

    public void offLineUpdateSystem() {
        /*
        Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter e) throws Exception {
                try {
                    String str = KkUtils.getUSBUpgradeFilePath(this.val$ctx);
                    if (str == null) {
                        HLog.e("SysEquipmentViewModel", "offLineUpdateSystem error filePath = null");
                        ToastUtils.showShort("读取离线安装文件失败");
                        e.onNext(Boolean.valueOf(false));
                        e.onComplete();
                        return;
                    }
                    KkUtils.installPackage(this.val$ctx, new File(str));
                    e.onNext(Boolean.valueOf(true));
                    e.onComplete();
                    return;
                } catch (Exception localException) {
                    StringBuilder localStringBuilder = new StringBuilder();
                    localStringBuilder.append("offLineUpdateSystem installPackage error ：");
                    localStringBuilder.append(localException.getMessage());
                    HLog.e("SysEquipmentViewModel", localStringBuilder.toString());
                    e.onError(localException);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe(new MyObserver(this) {
            @Override
            public void onNext(Object value) {
                SysEquipmentViewModel.this._ShowLoadingDialog.postValue(Integer.valueOf(-1));
                //if (SettingConfig.IS_DEBUG) {
                    HLog.d("SysEquipmentViewModel", "offLineUpdateSystem Observer离线更新流程结束 ");
                //}
            }

            public void onError(Throwable paramAnonymousThrowable) {
                //if (SettingConfig.IS_DEBUG) {
                    HLog.d("SysEquipmentViewModel", "offLineUpdateSystem Observer离线更新失败 ");
                //}
                SysEquipmentViewModel.this._ShowLoadingDialog.postValue(Integer.valueOf(-1));
                ToastUtils.showShort("操作失败，请检查升级包是否可用");
            }

            public void onNext(Boolean paramAnonymousBoolean) {

            }
        });

         */
    }

    public LiveData<Integer> showLoadingDialog() {
        return _ShowLoadingDialog;
    }
}

