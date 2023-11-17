package com.twd.setting.module.systemequipment.vm;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.twd.setting.base.observer.MyObserver;
import com.twd.setting.base.BaseViewModel;
import com.twd.setting.base.IDispose;
import com.twd.setting.commonlibrary.Utils.event.SingleLiveEvent;
import com.twd.setting.module.systemequipment.model.StorageInfoData;
import com.twd.setting.module.systemequipment.repository.StorageDetailRepository;
import com.twd.setting.utils.CollectionUtils;
//import com.twd.setting.utils.KkDataUtils;

import io.reactivex.Observable;

import java.util.List;

public class StorageDetailViewModel
        extends BaseViewModel<StorageDetailRepository> {
    private final MutableLiveData<List<StorageInfoData>> externalStorageInfo = new SingleLiveEvent();
    private final MutableLiveData<StorageInfoData> sysStorageInfo = new SingleLiveEvent();

    public StorageDetailViewModel(Application paramApplication) {
        super(paramApplication);
    }

    public LiveData<List<StorageInfoData>> getExternalStorageInfo() {
        return this.externalStorageInfo;
    }

    public LiveData<StorageInfoData> getSysStorageInfo() {
        return this.sysStorageInfo;
    }

    public void loadExternalData(Context paramContext) {
        ((StorageDetailRepository) this.model).getExternalListData(paramContext.getApplicationContext()).subscribe(new MyObserver(this) {
            @Override
            public void onNext(Object value) {
                StorageDetailViewModel.this.externalStorageInfo.postValue((List<StorageInfoData>)value);
            }

            public void onError(Throwable paramAnonymousThrowable) {
                StorageDetailViewModel.this.externalStorageInfo.postValue(null);
            }
        });
    }

    public void loadSysData(Context paramContext) {
        ((StorageDetailRepository) this.model).getSysData(paramContext.getApplicationContext()).subscribe(new MyObserver(this) {
            @Override
            public void onNext(Object value) {
                StorageDetailViewModel.this.sysStorageInfo.postValue((StorageInfoData)value);
                if ((value == null) || (CollectionUtils.isEmpty(((StorageInfoData)value).itemDataList))) {
                    //            KkDataUtils.sentEventError("剩余内存", null);
                }
            }

            public void onError(Throwable paramAnonymousThrowable) {
                StorageDetailViewModel.this.sysStorageInfo.postValue(null);
        //        KkDataUtils.sentEventError("剩余内存", null);
            }
        });
    }
}

