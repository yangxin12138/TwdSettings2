package com.twd.setting.module.bluetooth.vm;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.twd.setting.R;
import com.twd.setting.base.BaseModel;
import com.twd.setting.base.BaseViewModel;
import com.twd.setting.commonlibrary.Utils.ToastUtils;
import com.twd.setting.commonlibrary.Utils.event.SingleLiveEvent;
import com.twd.setting.module.bluetooth.bluetoothlib.BluetoothEventManager;
import com.twd.setting.module.bluetooth.bluetoothlib.LocalBluetoothManager;
import com.twd.setting.module.bluetooth.bluetoothlib.OnBluetoothResultCallback;
import com.twd.setting.module.bluetooth.model.CachedBluetoothDevice;
//import com.twd.setting.utils.HLog;
import io.reactivex.observers.DisposableObserver;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BluetoothHandsetViewModel
        extends BaseViewModel<BaseModel> {
    private final MutableLiveData<List<CachedBluetoothDevice>> _BluetoothBondedList = new SingleLiveEvent();
    private final MutableLiveData<CachedBluetoothDevice> _BluetoothStateChanged = new SingleLiveEvent();
    private final MutableLiveData<CachedBluetoothDevice> _ClickBluetoothItemValue = new SingleLiveEvent();
    private final String TAG = "BtHandsetViewModel";
    private final OnBluetoothResultCallback callback = new OnBluetoothResultCallback() {
        @Override
        public void onBluetoothStateChanged(int paramAnonymousInt) {
        }

        @Override
        public void onConnectionStateChanged(CachedBluetoothDevice device, int i) {
            if ((device != null) && (device.getDevice() != null) && (device.isBluetoothRc())) {
                if (TextUtils.isEmpty(device.getDevice().getAddress())) {
                    return;
                }
                Log.d(TAG, "onConnectionStateChanged 刷新连接、绑定状态 state=" + i);
                _BluetoothStateChanged.postValue(device);
            }
        }

        @Override
        public void onDeviceBondStateChanged(CachedBluetoothDevice device, int i, int i2) {
            if ((device != null) && (device.getDevice() != null)) {
                if (!device.isBluetoothRc()) {
                    return;
                }
                Log.d(TAG, "onDeviceBondStateChanged 刷新连接、绑定状态 state=" + i);
                _BluetoothStateChanged.postValue(device);
            }
        }

        @Override
        public void onProfileConnectionStateChanged(CachedBluetoothDevice device, int paramAnonymousInt1, int paramAnonymousInt2) {
            if (device != null) {
                if (!device.isBluetoothRc()) {
                    return;
                }
                _BluetoothStateChanged.postValue(device);
                if ((paramAnonymousInt1 == 0) && (paramAnonymousInt2 == 1)) {
                    ToastUtils.showShort(getApplication().getString(R.string.str_bluetooth_toast_connect_failed, new Object[]{device.getName()}));
                }
            }
        }

        @Override
        public void onScanHandsetItem(CachedBluetoothDevice paramAnonymousCachedBluetoothDevice) {
        }

        @Override
        public void onScanResultList(List<CachedBluetoothDevice> paramAnonymousList) {
        }

        @Override
        public void onScanningStateChanged(boolean paramAnonymousBoolean) {
        }
    };
    private final View.OnClickListener clickHandle = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            CachedBluetoothDevice device = (CachedBluetoothDevice) view.getTag();
            Log.d(TAG, "选中了 " + device.getName());
            _ClickBluetoothItemValue.postValue(device);
        }
    };
    private boolean isDefaultGuidePage = false;
    private LocalBluetoothManager mLocalManager;
    private DisposableObserver<Integer> timeOutObserver;

    public BluetoothHandsetViewModel(Application paramApplication) {
        super(paramApplication);
        initData();
    }

    private boolean stopScan() {
        Log.d(TAG, "stopScan() 扫描停止了？");
        if ((mLocalManager != null) && (mLocalManager.isDiscovering(false))) {
            mLocalManager.stopScanning(false);
            return true;
        }
        return false;
    }

    private void stopTimeOutCheck() {
        Log.e(TAG, "stopTimeOutCheck() 超时被停止了  disposed=" + timeOutObserver.isDisposed());
        if ((timeOutObserver != null) && (!timeOutObserver.isDisposed())) {
            timeOutObserver.dispose();
            timeOutObserver = null;
        }
    }

    public void connect(CachedBluetoothDevice device) {
        if ((device != null) && (device.getDevice() != null)) {
            stopScan();
            if (!device.isConnected()) {
                device.connect();
                return;
            }
            loadBoundedDataList(false);
        }
    }

    public void disConnect(CachedBluetoothDevice device) {
        if (device != null) {
            device.disconnect();
        }
    }

    public LiveData<List<CachedBluetoothDevice>> getBluetoothBondedList() {
        return _BluetoothBondedList;
    }

    public LiveData<CachedBluetoothDevice> getBluetoothStateChanged() {
        return _BluetoothStateChanged;
    }

    public LiveData<CachedBluetoothDevice> getClickBluetoothItemValue() {
        return _ClickBluetoothItemValue;
    }

    public View.OnClickListener getClickHandle() {
        return clickHandle;
    }

    public void initData() {
        mLocalManager = LocalBluetoothManager.getInstance(getApplication(), null);
        if (mLocalManager == null) {
            return;
        }
        loadBoundedDataList(true);
    }

    public boolean isDefaultGuidePage() {
        return isDefaultGuidePage;
    }

    public void loadBoundedDataList(boolean paramBoolean) {
        if (mLocalManager == null) {
            return;
        }
        Object bondedDevices = mLocalManager.getEventManager().readBondedDevices();
        List al = new ArrayList();
        if ((bondedDevices != null) && (((List) bondedDevices).size() > 0)) {
            Iterator iterator = ((List) bondedDevices).iterator();
            while (iterator.hasNext()) {
                CachedBluetoothDevice device = (CachedBluetoothDevice) iterator.next();
                if ((device != null) && (device.isBluetoothRc())) {
                    Log.d(TAG, "绑定的遥控器设备  name=" + device.getName());
                    al.add(device);
                }
            }
        }
        if ((paramBoolean) && (al.size() == 0)) {
            paramBoolean = true;
        } else {
            paramBoolean = false;
        }
        isDefaultGuidePage = paramBoolean;
        _BluetoothBondedList.postValue(al);
    }

    public void onDestroy(LifecycleOwner paramLifecycleOwner) {
        super.onDestroy(paramLifecycleOwner);
        Log.d(TAG, "onDestroy() 生命周期");
    }

    public void onStart(LifecycleOwner paramLifecycleOwner) {
        super.onStart(paramLifecycleOwner);
        Log.d(TAG, "onStart() 生命周期");
        if (mLocalManager != null) {
            mLocalManager.registerResultCallback(callback);
        }
    }

    public void onStop(LifecycleOwner paramLifecycleOwner) {
        super.onStop(paramLifecycleOwner);
        Log.d(TAG, "onStop() 生命周期");
        if (mLocalManager != null) {
            mLocalManager.unregisterResultCallback(callback);
        }
        stopTimeOutCheck();
    }

    public void unPair(CachedBluetoothDevice device) {
        if ((device != null) && (device.getDevice() != null)) {
            device.unPair();
        }
    }
}