package com.twd.setting.module;

import android.app.Application;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.twd.setting.R;
import com.twd.setting.base.observer.MyObserver;
import com.twd.setting.base.BaseModel;
import com.twd.setting.base.BaseViewModel;
import com.twd.setting.base.IDispose;
import com.twd.setting.commonlibrary.Utils.RxTransUtils;
import com.twd.setting.commonlibrary.Utils.event.SingleLiveEvent;
import com.twd.setting.utils.binding.ItemLRTextIconData;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class MainViewModel extends BaseViewModel<BaseModel> {
    private static final String TAG = "MainViewModel";
    public static final int ITEM_PROJECTOR = 1;
    public static final int ITEM_SIGNAL_SOURCE = 2;
    public static final int ITEM_NETWORK = 3;
    public static final int ITEM_BLUETOOTH = 4;
    public static final int ITEM_COMMON = 5;
    public static final int ITEM_SYSTEM_EQUIPMENT = 6;
    public static final int ITEM_DEBUG_MENU = 7;
    private final int SHOW_DEBUG_MENU_KEY_COUNT = 10;

    private final MutableLiveData<Integer> _ClickItem = new SingleLiveEvent();
    private final MutableLiveData<Boolean> _DebugMenuItemShow = new SingleLiveEvent();
    private final MutableLiveData<MainItemVisible> _ItemVisible = new SingleLiveEvent();
    private final View.OnClickListener _itemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d(TAG, "onClick   tag:" + view.getTag() + ",id:" + view.getId());
            //_ClickItem.setValue(Integer.valueOf((Integer)view.getTag()).intValue());
            _ClickItem.setValue(view.getId());
        }
    };

    public ItemLRTextIconData bluetoothData;
    public ItemLRTextIconData commonData;
    private int curLeftKeyCount;
    public ItemLRTextIconData debugMenuData;
    private long lastLeftKeyTime;
    private volatile MainItemVisible mainItemLayoutVisibleData;
    public ItemLRTextIconData networkData;
    public ItemLRTextIconData projectorData;
    public ItemLRTextIconData signalSourceData;
    public ItemLRTextIconData systemEquipmentData;

    public MainViewModel(@NonNull Application application) {
        super(application);
        initData(application);
    }

    public LiveData<Integer> getClickItem() {
        return _ClickItem;
    }

    public LiveData<Boolean> getDebugMenuItemShow() {
        return _DebugMenuItemShow;
    }

    public View.OnClickListener getItemClickListener() {
        return _itemClickListener;
    }

    public LiveData<MainItemVisible> getItemVisible() {
        return _ItemVisible;
    }

    public MainItemVisible getMainItemLayoutVisibleData() {
        return mainItemLayoutVisibleData;
    }

    public void initData(Application application) {
        projectorData = new ItemLRTextIconData(ITEM_PROJECTOR, application.getString(R.string.str_projector_setting), null, R.drawable.ic_projector_v, R.drawable.ic_baseline_arrow_forward_ios_24);
        signalSourceData = new ItemLRTextIconData(ITEM_SIGNAL_SOURCE, application.getString(R.string.str_signal_source), null, R.drawable.ic_signal_source_v, R.drawable.ic_baseline_arrow_forward_ios_24);
        networkData = new ItemLRTextIconData(ITEM_NETWORK, application.getString(R.string.str_network), null, R.drawable.ic_network_v, R.drawable.ic_baseline_arrow_forward_ios_24);
        bluetoothData = new ItemLRTextIconData(ITEM_BLUETOOTH, application.getString(R.string.str_bluetooth), null, R.drawable.ic_bluetooth_v, R.drawable.ic_baseline_arrow_forward_ios_24);
        commonData = new ItemLRTextIconData(ITEM_COMMON, application.getString(R.string.str_common), null, R.drawable.ic_common_v, R.drawable.ic_baseline_arrow_forward_ios_24);
        systemEquipmentData = new ItemLRTextIconData(ITEM_SYSTEM_EQUIPMENT, application.getString(R.string.str_system_equipment), null, R.drawable.ic_sys_equipment_v, R.drawable.ic_baseline_arrow_forward_ios_24);
        debugMenuData = new ItemLRTextIconData(ITEM_DEBUG_MENU, application.getString(R.string.str_debug_menu), null, R.drawable.ic_debug_menu_v, R.drawable.ic_baseline_arrow_forward_ios_24);
        initItemVisible();
    }

    public void initItemVisible() {
        Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter e) throws Exception {

                //}

                //public void subscribe(ObservableEmitter<MainItemVisible> paramAnonymousObservableEmitter)
                //        throws Exception
                //{
                MainItemVisible mainItemVisible = new MainItemVisible();
//                ConfigInfo localConfigInfo = new ConfigInfo("/etc/settings.ini");
//                if (KkUtils.isUnSupportProjector(localConfigInfo)) {
                //mainItemVisible.setProjectorVisible(false);
                mainItemVisible.setProjectorVisible(true);
//                } else {
//                    mainItemVisible.setProjectorCname(KkUtils.getProjectorComponentName(localConfigInfo));
//                }
//                if (KkUtils.isUnSupportSource(localConfigInfo)) {
                //mainItemVisible.setSourceVisible(false);
                mainItemVisible.setSourceVisible(true);
//                } else {
//                    mainItemVisible.setSourceCname(KkUtils.getSourceComponentName(localConfigInfo));
//                }
//                if (KkUtils.isUnSupportBluetooth(localConfigInfo)) {
//                    mainItemVisible.setBluetoothVisible(false);
//                }
                // MainViewModel.access$202(this, mainItemVisible);
                e.onNext(mainItemVisible);
                e.onComplete();
            }
        }).compose(RxTransUtils.schedulersIoToUi()).subscribe(new MyObserver(this) {
            @Override
            public void onNext(Object value) {
                _ItemVisible.postValue((MainItemVisible) value);
            }

            @Override
            public void onError(Throwable paramAnonymousThrowable) {
                _ItemVisible.postValue(null);
            }
        });


    }

    public boolean keyDown(int keyCode) {
        /*if ((keyCode == KeyEvent.KEYCODE_DPAD_LEFT) && ((curLeftKeyCount <= 0) || (SystemClock.uptimeMillis() - lastLeftKeyTime <= 1000L))) {
            lastLeftKeyTime = SystemClock.uptimeMillis();
            curLeftKeyCount = curLeftKeyCount + 1;
            if (curLeftKeyCount >= 10) {
                _DebugMenuItemShow.postValue(Boolean.valueOf(true));
                curLeftKeyCount = 0;
            } else {
                curLeftKeyCount = 0;
            }
        }
        curLeftKeyCount = 0;*/
        return false;
    }
}
