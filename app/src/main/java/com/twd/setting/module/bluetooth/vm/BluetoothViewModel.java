package com.twd.setting.module.bluetooth.vm;

import android.annotation.SuppressLint;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.os.SystemClock;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.twd.setting.R;
//import com.twd.setting.SettingConfig;
import com.twd.setting.base.observer.MyObserver;
import com.twd.setting.base.BaseModel;
import com.twd.setting.base.BaseViewModel;
import com.twd.setting.commonlibrary.Utils.StringUtils;
import com.twd.setting.commonlibrary.Utils.ToastUtils;
import com.twd.setting.commonlibrary.Utils.event.SingleLiveEvent;
import com.twd.setting.module.bluetooth.bluetoothlib.LocalBluetoothManager;
import com.twd.setting.module.bluetooth.bluetoothlib.OnBluetoothResultCallback;
import com.twd.setting.module.bluetooth.model.CachedBluetoothDevice;
//import com.twd.setting.utils.HLog;
//import com.twd.setting.utils.KkUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class BluetoothViewModel
        extends BaseViewModel<BaseModel> {
    private final MutableLiveData<List<CachedBluetoothDevice>> _BluetoothItemListValue = new SingleLiveEvent();
    private final MutableLiveData<Integer> _BluetoothSwitchValue = new SingleLiveEvent();
    private final MutableLiveData<Boolean> _ScanBtnValue = new SingleLiveEvent();
    private final String TAG = "BluetoothViewModel";
    private final OnBluetoothResultCallback callback = new OnBluetoothResultCallback() {
        public void onBluetoothStateChanged(int state) {
            Log.d(TAG,"onBluetoothStateChanged"+ state);
            if (state == 12) {
                updateBluetoothName();
            }
            _BluetoothSwitchValue.postValue(Integer.valueOf(state));
        }

        public void onConnectionStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int paramAnonymousInt) {
            if (mLocalManager != null) {
                if(cachedBluetoothDevice !=null) {
                    Log.d(TAG, "onConnectionStateChanged" + cachedBluetoothDevice.getName());
                }
                updateListView(mLocalManager.getCachedDeviceManager().getCachedDevicesCopy());
            }
        }

        public void onDeviceBondStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int paramAnonymousInt1, int paramAnonymousInt2) {
            if (mLocalManager != null) {
                if(cachedBluetoothDevice !=null) {
                    Log.d(TAG, "onConnectionStateChanged" + cachedBluetoothDevice.getName());
                }
                updateListView(mLocalManager.getCachedDeviceManager().getCachedDevicesCopy());
            }
            if ((cachedBluetoothDevice != null) && (paramAnonymousInt1 == 10) && (paramAnonymousInt2 == 11)) {
                ToastUtils.showShort(getApplication().getString(R.string.str_bluetooth_toast_bond_failed, new Object[]{cachedBluetoothDevice.getName()}));
            }
        }

        public void onProfileConnectionStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int paramAnonymousInt1, int paramAnonymousInt2) {
            if (mLocalManager != null) {
                if(cachedBluetoothDevice !=null) {
                    Log.d(TAG, "onConnectionStateChanged" + cachedBluetoothDevice.getName());
                }
                if (mLocalManager.isDiscovering(false)) {
                    return;
                }
                updateListView(mLocalManager.getCachedDeviceManager().getCachedDevicesCopy());
                if ((cachedBluetoothDevice != null) && (paramAnonymousInt1 == 0) && (paramAnonymousInt2 == 1)) {
                    ToastUtils.showShort(getApplication().getString(R.string.str_bluetooth_toast_connect_failed, new Object[]{cachedBluetoothDevice.getName()}));
                }
            }
        }

        public void onScanHandsetItem(CachedBluetoothDevice paramAnonymousCachedBluetoothDevice) {
        }

        public void onScanResultList(List<CachedBluetoothDevice> list) {
            Log.d("BluetoothViewModel", "数据来了  size=" + list.size() + "  thread=" + Thread.currentThread().getName());
            if (SystemClock.uptimeMillis() - lastUpdateListTime > 2000L) {
                //BluetoothViewModel.access$202(this, SystemClock.uptimeMillis());
                updateListView(list);
            }
        }

        public void onScanningStateChanged(boolean changed) {
            _ScanBtnValue.postValue(Boolean.valueOf(changed));
            Log.d("BluetoothViewModel", "onScanningStateChanged 扫描状态更新  started=" + changed + " thread=" + Thread.currentThread().getName());
            if ((!changed) && (mLocalManager != null)) {
                updateListView(mLocalManager.getCachedDeviceManager().getCachedDevicesCopy());
            }
        }
    };
    private long lastUpdateListTime;
    private LocalBluetoothManager mLocalManager;

    public BluetoothViewModel(Application paramApplication) {
        super(paramApplication);
        initData();
    }

    @SuppressLint("MissingPermission")
    private void updateBluetoothName() {
        final BluetoothAdapter localBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (localBluetoothAdapter != null) {
            if (!localBluetoothAdapter.isEnabled()) {
                return;
            }
            Observable.create(new ObservableOnSubscribe() {
                @Override
                public void subscribe(ObservableEmitter e) throws Exception {
                    // String str = KkUtils.getType(getApplication());
                    String str = "TEST";// KkUtils.getType(getApplication());
                    boolean bool;
                    if ((!StringUtils.isTrimEmpty(str)) && (!str.equals(localBluetoothAdapter.getName()))) {
                        bool = localBluetoothAdapter.setName(str);
                    } else {
                        bool = false;
                    }
                    e.onNext(Boolean.valueOf(bool));
                    e.onComplete();
                }
            }).subscribeOn(Schedulers.io()).subscribe(new MyObserver(this) {
                @Override
                public void onNext(Object value) {
                    Log.d("BluetoothViewModel", "本机蓝牙名称修改" + value);
                }

                @Override
                public void onError(Throwable e) {
                    Log.d("BluetoothViewModel", "本机蓝牙名称修改失败");
                }
            });
        }
    }

    private void updateListView(final List<CachedBluetoothDevice> paramList) {
        if ((paramList != null) && (paramList.size() > 1)) {
            Observable.create(new ObservableOnSubscribe() {
                @Override
                public void subscribe(ObservableEmitter e) throws Exception {
                    Iterator iterator = paramList.iterator();
                    while (iterator.hasNext()) {
                        CachedBluetoothDevice device = (CachedBluetoothDevice) iterator.next();
                        if (device != null) {
                            device.updateSortValue();
                        }
                    }
                    try {
                        Collections.sort(paramList, new Comparator() {
                            @Override
                            public int compare(Object o, Object t1) {
                                return Integer.compare(((CachedBluetoothDevice) t1).getSortValue(), ((CachedBluetoothDevice) o).getSortValue());
                            }
                        });
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        Log.e("BluetoothViewModel", "排序出错", exception);
                    }
                    _BluetoothItemListValue.postValue(paramList);
                    e.onNext(Boolean.valueOf(true));
                    e.onComplete();
                }
            }).subscribeOn(Schedulers.single()).observeOn(AndroidSchedulers.mainThread()).subscribe(new MyObserver(this) {
                @Override
                public void onNext(Object value) {

                }
            });
            return;
        }
        _BluetoothItemListValue.postValue(paramList);
    }

    public void connect(CachedBluetoothDevice device) {
        if (mLocalManager == null) {
            return;
        }
        String deviceName = device.getName();
        //判断是否是要连接语音助手，要连接语音助手则断开其他的语音助手即可，其他设备不用管。 要连接其他的设备断开其他的设备就可，语音助手不管
        if (Objects.equals(deviceName, "语音助手")){
            disConnectAllVoiceAssistant();
        }else {
            disConnectAllOtherDevice();
        }
        //disConnectAll();
        updateListView(mLocalManager.getCachedDeviceManager().getCachedDevicesCopy());

        if ((device != null) && (device.getDevice() != null)) {
            if (!device.isConnected()) {
                device.connect();
                return;
            }
            device.setNewStatus(2);
            updateListView(mLocalManager.getCachedDeviceManager().getCachedDevicesCopy());
        }
    }

    public void disConnect(CachedBluetoothDevice device) {
        if (device != null) {
            device.disconnect();
        }
    }

    public void disConnectAll(){
        List<CachedBluetoothDevice> cachedBluetoothDevices = mLocalManager.getCachedDeviceManager().getCachedDevicesCopy();
        for (CachedBluetoothDevice cachedBluetoothDevice : cachedBluetoothDevices){
            Log.i(TAG, "disConnectAll: disconnect : "+ cachedBluetoothDevice.getName());
            disConnect(cachedBluetoothDevice);
        }
    }

    public void disConnectAllOtherDevice(){
        List<CachedBluetoothDevice> cachedBluetoothDevices = mLocalManager.getCachedDeviceManager().getCachedDevicesCopy();
        for (CachedBluetoothDevice cachedBluetoothDevice : cachedBluetoothDevices){
            Log.i(TAG, "disConnectAll: disconnect : "+ cachedBluetoothDevice.getName());
            String currentName = cachedBluetoothDevice.getName();
            if (!currentName.equals("语音助手")){
                disConnect(cachedBluetoothDevice);
            }
        }
    }
    //断开所有的语音助手
    public void disConnectAllVoiceAssistant(){
        List<CachedBluetoothDevice> cachedBluetoothDevices = mLocalManager.getCachedDeviceManager().getCachedDevicesCopy();
        for (CachedBluetoothDevice cachedBluetoothDevice : cachedBluetoothDevices){
            Log.i(TAG, "disConnectAll: disconnect : "+ cachedBluetoothDevice.getName());
            String currentName = cachedBluetoothDevice.getName();
            if (currentName.equals("语音助手")){
                disConnect(cachedBluetoothDevice);
            }
        }
    }

    public boolean disableBluetooth() {
        if (mLocalManager == null) {
            return false;
        }
        return mLocalManager.disableBluetooth();
    }

    public boolean enableBluetooth() {
        if (mLocalManager == null) {
            return false;
        }
        return mLocalManager.enableBluetooth();
    }

    public LiveData<List<CachedBluetoothDevice>> getBluetoothItemList() {
        return _BluetoothItemListValue;
    }

    public LiveData<Integer> getBluetoothSwitchBtn() {
        return _BluetoothSwitchValue;
    }

    public LiveData<Boolean> getSearchBtnStatus() {
        return _ScanBtnValue;
    }

    public void initData() {
        mLocalManager = LocalBluetoothManager.getInstance(getApplication(), null);

        Log.d("BluetoothViewModel", "initData() 蓝牙初始化  isInitSuccess=" + isInitSuccess());

        if (mLocalManager == null) {
            return;
        }
        mLocalManager.registerResultCallback(callback);
        if (!mLocalManager.isAdapterEnabled()) {
            return;
        }
        startScan();
        updateBluetoothName();
    }

    public boolean isDiscovering() {
        LocalBluetoothManager localLocalBluetoothManager = mLocalManager;
        boolean ret = false;
        if (mLocalManager != null) {
            if (mLocalManager.isDiscovering(false)) {
                ret = true;
            }
        }
        return ret;
    }

    public boolean isInitSuccess() {
        return mLocalManager != null;
    }

    public void onDestroy(LifecycleOwner paramLifecycleOwner) {
        super.onDestroy(paramLifecycleOwner);
        Log.d("BluetoothViewModel", "onDestroy() 生命周期");
    }

    public void onHiddenChanged(boolean changed) {
        LocalBluetoothManager localLocalBluetoothManager = this.mLocalManager;
        if (mLocalManager == null) {
            return;
        }
        if (changed) {
            mLocalManager.unregisterResultCallback(callback);
        } else {
            mLocalManager.registerResultCallback(callback);
        }
    }

    public void onStart(LifecycleOwner paramLifecycleOwner) {
        super.onStart(paramLifecycleOwner);
        Log.e("BluetoothViewModel", "onStart() 生命周期");
        if (mLocalManager != null) {
            mLocalManager.registerResultCallback(callback);
        }
    }

    public void onStop(LifecycleOwner paramLifecycleOwner) {
        super.onStop(paramLifecycleOwner);
        Log.d("BluetoothViewModel", "onStop() 生命周期");
        stopScan();
        if (mLocalManager != null) {
            mLocalManager.unregisterResultCallback(callback);
        }
    }

    public void refreshList() {
        if (mLocalManager == null) {
            return;
        }
        Log.d("BluetoothViewModel", "refreshList() 准备刷新绑定设备数据");
        mLocalManager.getEventManager().readBondedDevices();
        updateListView(mLocalManager.getCachedDeviceManager().getCachedDevicesCopy());
    }

    public void startScan() {
        BluetoothAdapter localBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (localBluetoothAdapter == null) {
            Log.d("BluetoothViewModel", "获取BluetoothAdapter失败，无法使用蓝牙 adapter=null");
            ToastUtils.showShort("操作失败，无法获取蓝牙适配器");
            return;
        }
        if ((mLocalManager != null) && (localBluetoothAdapter.isEnabled())) {
            stopScan();
            updateListView(null);
            _ScanBtnValue.postValue(Boolean.valueOf(true));
            mLocalManager.getCachedDeviceManager().clear();
            mLocalManager.getEventManager().readBondedDevices();
            Observable.create(new ObservableOnSubscribe() {
                @Override
                public void subscribe(ObservableEmitter e) throws Exception {
                    boolean bool = mLocalManager.startScanning(false);
                    Log.d("BluetoothViewModel", "loadData()  启动扫描了  thread=" + Thread.currentThread().getName() + "  startScan=" + bool + " state=" + mLocalManager.getLocalAdapter().getState() + " time=" + System.currentTimeMillis());
                    e.onNext(Boolean.valueOf(bool));
                    e.onComplete();
                }
            }).delay(200L, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.single()).subscribe(new MyObserver(this) {
                @Override
                public void onNext(Object value) {
                    if (!((Boolean) value).booleanValue()) {
                        Log.e("BluetoothViewModel", "打开扫描失败");
                        _ScanBtnValue.postValue(Boolean.valueOf(false));
                    }
                }

                @Override
                public void onError(Throwable paramAnonymousThrowable) {
                }
            });
            return;
        }
        Log.d("BluetoothViewModel", "蓝牙开关没有打开，或者初始化蓝牙失败，无法使用蓝牙  mLocalManager=" + mLocalManager + "  isEnable=" + localBluetoothAdapter.isEnabled());
        ToastUtils.showShort("数据异常，请退出后，重新进入蓝牙页");
    }

    public void stopScan() {
        Log.d("BluetoothViewModel", "stopScan() 扫描停止了？  thread=" + Thread.currentThread());
        if (mLocalManager != null) {
            mLocalManager.stopScanning(false);
        }
        _ScanBtnValue.postValue(Boolean.valueOf(false));
    }

    public void unPair(CachedBluetoothDevice paramCachedBluetoothDevice) {
        if (paramCachedBluetoothDevice != null) {
            paramCachedBluetoothDevice.unPair();
        }
    }
}
