package com.twd.setting.module.bluetooth.vm;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
//import com.twd.setting.SettingConfig;
import com.twd.setting.base.observer.MyDisposableObserver;
import com.twd.setting.base.observer.MyObserver;
import com.twd.setting.base.BaseModel;
import com.twd.setting.base.BaseViewModel;
import com.twd.setting.commonlibrary.Utils.RxTransUtils;
import com.twd.setting.commonlibrary.Utils.ToastUtils;
import com.twd.setting.commonlibrary.Utils.event.SingleLiveEvent;
import com.twd.setting.module.bluetooth.bluetoothlib.LocalBluetoothManager;
import com.twd.setting.module.bluetooth.bluetoothlib.OnBluetoothResultCallback;
import com.twd.setting.module.bluetooth.model.CachedBluetoothDevice;
import com.twd.setting.utils.BluetoothUtils;
//import com.twd.setting.utils.HLog;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class BluetoothHandsetPairViewModel
        extends BaseViewModel<BaseModel> {
    private final int TIMEOUT_CONNECT = 15;
    private final int TIMEOUT_OPEN_SCAN = 5;
    private final int TIMEOUT_PENDING_CONNECT = 4;
    private final String TAG = "BtHandsetPairViewModel";
    private final MutableLiveData<Integer> _UpdateAddDialogStatus = new SingleLiveEvent();
    private final OnBluetoothResultCallback callback = new OnBluetoothResultCallback() {
        @Override
        public void onBluetoothStateChanged(int paramInt) {

        }

        @Override
        public void onConnectionStateChanged(CachedBluetoothDevice paramCachedBluetoothDevice, int paramInt) {
            if ((paramCachedBluetoothDevice != null) && (curSelectedDeviceAddr != null) && (paramCachedBluetoothDevice.getDevice() != null)) {
                if (!curSelectedDeviceAddr.equals(paramCachedBluetoothDevice.getDevice().getAddress())) {
                    return;
                }
                if (paramInt == 2) {
                    if (dialogCurrentState == 3) {
                        return;
                    }
                    dialogCurrentState = 3;
                    //        BluetoothHandsetPairViewModel.access$302(this, 3);
                    //         BluetoothHandsetPairViewModel.access$502(this, null);
                    updateDialogStatus();
                    return;
                }
                if (paramInt != 1) {
                    Log.d(TAG, "onConnectionStateChanged  不是已连接、也不是连接中状态，则表示连接失败  name=" + paramCachedBluetoothDevice.getName() + " state=" + paramInt);
                    dialogCurrentState = 4;
                    //      BluetoothHandsetPairViewModel.access$302(this, 4);
                    //      BluetoothHandsetPairViewModel.access$502(this, null);
                    updateDialogStatus();
                }
            }
        }

        @Override
        public void onDeviceBondStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int paramInt1, int paramInt2) {
            if (cachedBluetoothDevice != null) {
                if (cachedBluetoothDevice.getDevice() == null) {
                    return;
                }
                Log.d(TAG,"onDeviceBondStateChanged "+ paramInt1 +", "+paramInt2);
                if (paramInt1 != 10) {
                    return;
                }
                StringBuilder localStringBuilder;
                if ((dialogCurrentState == 1) && (pendingConnectDeviceAddr != null) && (pendingConnectDeviceAddr.equals(cachedBluetoothDevice.getAddress()))) {
                    Log.d(TAG, "onDeviceBondStateChanged  取消绑定了 开始连接设备  name=" + cachedBluetoothDevice.getName() + " bondState=" + paramInt1);
                    startConnect(cachedBluetoothDevice);
                    return;
                }
                if ((curSelectedDeviceAddr != null) && (cachedBluetoothDevice.getDevice() != null) && (curSelectedDeviceAddr.equals(cachedBluetoothDevice.getDevice().getAddress()))) {
                    Log.d(TAG, "onDeviceBondStateChanged  绑定失败了 此次配对失败  name=" + cachedBluetoothDevice.getName() + " bondState=" + paramInt1);
                    dialogCurrentState = 4;
                    //         BluetoothHandsetPairViewModel.access$302(this, 4);
                    //         BluetoothHandsetPairViewModel.access$502(this, null);

                    updateDialogStatus();
                }
            }
        }

        @Override
        public void onProfileConnectionStateChanged(CachedBluetoothDevice paramCachedBluetoothDevice, int paramInt1, int paramInt2) {
            Log.d(TAG,"onProfileConnectionStateChanged "+ paramInt1 +", "+paramInt2);
            if (paramCachedBluetoothDevice == null) {
                return;
            }
            if ((dialogCurrentState == 2) && (curSelectedDeviceAddr != null) && (curSelectedDeviceAddr.equals(paramCachedBluetoothDevice.getAddress())) && (paramCachedBluetoothDevice.isConnected())) {
                dialogCurrentState = 3;
                //       BluetoothHandsetPairViewModel.access$302(this, 3);
                //       BluetoothHandsetPairViewModel.access$502(this, null);
                updateDialogStatus();
            }
        }

        @Override
        public void onScanHandsetItem(CachedBluetoothDevice cachedBluetoothDevice) {
            if ((!isPairing.get()) && (cachedBluetoothDevice != null) && (cachedBluetoothDevice.getDevice() != null) && (cachedBluetoothDevice.isJustDiscovered())) {
                if (!cachedBluetoothDevice.isBluetoothRc()) {
                    return;
                }
                isPairing.set(true);
                stopScan();

                Log.d(TAG, "onScanResultItem 扫描到遥控设备 已配对=" + cachedBluetoothDevice.isBonded() + "  已连接=" + cachedBluetoothDevice.isConnected() + "  name=" + cachedBluetoothDevice.getName() + "  thread=" + Thread.currentThread());

                Observable.create(new ObservableOnSubscribe() {
                    @Override
                    public void subscribe(ObservableEmitter e) throws Exception {
                        Log.d(TAG, "222 onScanResultItem 扫描到遥控设备 已配对=" + cachedBluetoothDevice.isBonded() + "  已连接=" + cachedBluetoothDevice.isConnected() + "  name=" + cachedBluetoothDevice.getName() + "  thread=" + Thread.currentThread());
                        if ((!cachedBluetoothDevice.isBonded()) && (!cachedBluetoothDevice.isConnected())) {
                            if (dialogCurrentState != 2) {
                                startConnect(cachedBluetoothDevice);
                            }
                            e.onNext(new Object());
                            e.onComplete();
                            return;
                        }
                        //           BluetoothHandsetPairViewModel.access$202(this, paramCachedBluetoothDevice.getAddress());
                        stopScan();
                        cachedBluetoothDevice.unPair();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new MyObserver(BluetoothHandsetPairViewModel.this) {
                    @Override
                    public void onNext(Object paramAnonymous2Object) {
                    }
                });
            }
        }

        @Override
        public void onScanResultList(List<CachedBluetoothDevice> paramList) {

        }

        @Override
        public void onScanningStateChanged(boolean paramBoolean) {
            Log.d(TAG,"onScanningStateChanged :"+paramBoolean);
            if (paramBoolean) {
                //      BluetoothHandsetPairViewModel.access$702(BluetoothHandsetPairViewModel.this, true);
                return;
            }
            Log.d(TAG, "onScanningStateChanged 扫描遥控结束了 重试吗？ isConnecting=" + dialogCurrentState + "  pendingConnectDeviceAddr=" + pendingConnectDeviceAddr + "  isScanStarted=" + isScanStarted + "  isDiscoverring=" + mLocalManager.isDiscovering(true));
            if (!isPairing.get()) {
                if (dialogCurrentState != 1) {
                    return;
                }
                if (pendingConnectDeviceAddr != null) {
                    Log.d(TAG,"onScanningStateChanged  startTimeOutCheck");
                    startTimeOutCheck(4);
                    return;
                }
                //if ((isScanStarted) && ((mLocalManager == null) || (!mLocalManager.isDiscovering(true)))) {
                if ( ((mLocalManager == null) || (!mLocalManager.isDiscovering(true)))) {
                    Log.d(TAG,"onScanningStateChanged  failed 4");
                    dialogCurrentState = 4;
                    //        BluetoothHandsetPairViewModel.access$302(BluetoothHandsetPairViewModel.this, 4);
                    updateDialogStatus();
                    //         BluetoothHandsetPairViewModel.access$702(BluetoothHandsetPairViewModel.this, false);
                }
            }else{
                if(dialogCurrentState == 1){
                    Log.d(TAG,"onScanningStateChanged  timeout 4");
                    dialogCurrentState = 4;
                    updateDialogStatus();
                }
            }


        }

    };
    private String curSelectedDeviceAddr;
    private int dialogCurrentState = 1;
    private AtomicBoolean isPairing = new AtomicBoolean(false);
    private boolean isScanStarted = false;
    private LocalBluetoothManager mLocalManager;
    private String pendingConnectDeviceAddr;
    private DisposableObserver<Object> scanTimeoutDobserver;
    private DisposableObserver<Integer> timeOutObserver;

    public BluetoothHandsetPairViewModel(Application paramApplication) {
        super(paramApplication);
        initData();
    }

    private void cancelScanTimeout() {
        if ((scanTimeoutDobserver != null) && (!scanTimeoutDobserver.isDisposed())) {
            scanTimeoutDobserver.dispose();
        }
    }

    private DisposableObserver<Object> getScanTimeoutObserver() {
        return new MyDisposableObserver() {
            @Override
            public void onNext(Integer paramAnonymousInteger) {
                Log.d(TAG, "getScanTimeoutObserver()  dialogCurrentState=" + dialogCurrentState+", "+paramAnonymousInteger);
                if ((mLocalManager != null) && (mLocalManager.isDiscovering(true)) && (dialogCurrentState == 1)) {
                    stopScan();
                }
            }
        };
    }

    private DisposableObserver<Integer> getTimeOutObserver() {
        return new MyDisposableObserver() {

            @Override
            public void onNext(Integer paramAnonymousInteger) {
                StringBuilder str = new StringBuilder();
                str.append("getTimeOutObserver() onNext() 操作超时了。。。。。。time=");
                str.append(paramAnonymousInteger);
                str.append("  dialogCurrentState=");
                str.append(dialogCurrentState);
                str.append("  isdiscovering=");
                boolean bool;
                if ((mLocalManager != null) && (mLocalManager.isDiscovering(true))) {
                    bool = true;
                } else {
                    bool = false;
                }
                str.append(bool);
                str.append("  pendingConnectDeviceAddr=");
                str.append(pendingConnectDeviceAddr);
                Log.d(TAG, str.toString());
                if ((paramAnonymousInteger.intValue() == 5) && (mLocalManager != null) && (!mLocalManager.isDiscovering(true)) && (dialogCurrentState == 1) && (pendingConnectDeviceAddr == null)) {
                    dialogCurrentState = 4;
                    //       BluetoothHandsetPairViewModel.access$302(BluetoothHandsetPairViewModel.this, 4);
                    updateDialogStatus();
                    return;
                }
                if ((paramAnonymousInteger.intValue() == 15) && (dialogCurrentState == 2)) {
                    dialogCurrentState = 4;
                    //        BluetoothHandsetPairViewModel.access$302(BluetoothHandsetPairViewModel.this, 4);
                    updateDialogStatus();
                    return;
                }
                if ((paramAnonymousInteger.intValue() == 4) && (dialogCurrentState == 1) && (pendingConnectDeviceAddr != null)) {
                    dialogCurrentState = 4;
                    //         BluetoothHandsetPairViewModel.access$202(BluetoothHandsetPairViewModel.this, null);
                    //         BluetoothHandsetPairViewModel.access$302(BluetoothHandsetPairViewModel.this, 4);
                    updateDialogStatus();
                }
            }
        };
    }

    private void startConnect(CachedBluetoothDevice paramCachedBluetoothDevice) {
        StringBuilder str = new StringBuilder();
        str.append("onScanResultItem 扫描到遥控设备 开启连接  threadname=");
        str.append(Thread.currentThread().getName());
        Log.d(TAG, str.toString());
        pendingConnectDeviceAddr = null;
        connect(paramCachedBluetoothDevice);
        updateDialogStatus();
        if (dialogCurrentState == 2) {
            startTimeOutCheck(TIMEOUT_CONNECT);
        }
    }

    private void startTimeOutCheck(int paramInt) {
        stopTimeOutCheck();
        StringBuilder str = new StringBuilder();
        str.append("startTimeOutCheck() 超时启动了 dialogCurrentState=");
        str.append(dialogCurrentState);
        str.append(" time=");
        str.append(paramInt);
        Log.d(TAG, str.toString());
        timeOutObserver = getTimeOutObserver();
        Observable.just(Integer.valueOf(paramInt)).delay(paramInt, TimeUnit.SECONDS).compose(RxTransUtils.schedulersIoToUi()).subscribe(timeOutObserver);
    }

    private boolean stopScan() {
        cancelScanTimeout();
        StringBuilder str = new StringBuilder();
        str.append("stopScan() 准备关闭扫描 isDiscovering=");
        boolean bool;
        if ((mLocalManager != null) && (mLocalManager.isDiscovering(true))) {
            bool = true;
        } else {
            bool = false;
        }
        str.append(bool);
        Log.d(TAG, str.toString());
        if (mLocalManager != null) {
            mLocalManager.stopScanning(true);
            return true;
        }
        return false;
    }

    private void stopTimeOutCheck() {
        StringBuilder str = new StringBuilder();
        str.append("stopTimeOutCheck() 超时被停止了 dialogCurrentState=");
        str.append(dialogCurrentState);
        str.append(" disposed=");
        Object localObject = this.timeOutObserver;
        if (timeOutObserver != null) {
            str.append(timeOutObserver.isDisposed());
        }
        Log.d(TAG, str.toString());

        if ((timeOutObserver != null) && (!timeOutObserver.isDisposed())) {
            timeOutObserver.dispose();
            timeOutObserver = null;
        }
    }

    private void updateDialogStatus() {
        _UpdateAddDialogStatus.postValue(Integer.valueOf(dialogCurrentState));
    }

    public void connect(final CachedBluetoothDevice paramCachedBluetoothDevice) {
        if ((paramCachedBluetoothDevice != null) && (paramCachedBluetoothDevice.getDevice() != null)) {
            StringBuilder str = new StringBuilder();
            str.append("connect() 停止扫描遥控，准备连接设备 name=");
            str.append(paramCachedBluetoothDevice.getName());
            Log.d(TAG, str.toString());
            dialogCurrentState = 2;
            stopScan();
            if (!paramCachedBluetoothDevice.isConnected()) {
                curSelectedDeviceAddr = paramCachedBluetoothDevice.getDevice().getAddress();
                Observable.just(Integer.valueOf(1)).delay(1L, TimeUnit.SECONDS).subscribe(new MyObserver(this) {
                    @Override
                    public void onNext(Object value) {
                        Log.d(TAG, "connect() 停止扫描遥控，正在连接设备 name=" + paramCachedBluetoothDevice.getName() + "  rssi=" + paramCachedBluetoothDevice.getRssi());
                        paramCachedBluetoothDevice.connect();
                    }
                });
                return;
            }
            dialogCurrentState = 3;
            updateDialogStatus();
        }
    }

    public LiveData<Integer> getUpdateAddDialogStatus() {
        return _UpdateAddDialogStatus;
    }

    public void initData() {
        mLocalManager = LocalBluetoothManager.getInstance(getApplication(), null);

        if (mLocalManager == null) {
            return;
        }
        mLocalManager.registerResultCallback(callback);
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
            stopScan();
            mLocalManager.unregisterResultCallback(callback);
        }
        stopTimeOutCheck();
    }

    public void startAddDevice() {
        if (mLocalManager == null) {
            ToastUtils.showShort("获取蓝牙适配器异常");
            Log.d(TAG, "startAddDevice()启动扫描。。。。。。mLocalManager=null");
            return;
        }
        isPairing.set(false);
        curSelectedDeviceAddr = null;
        pendingConnectDeviceAddr = null;
        dialogCurrentState = 1;
        stopScan();
        Log.d(TAG, "startAddDevice()启动扫描。。。。。。");
        mLocalManager.getCachedDeviceManager().clear();
        mLocalManager.getEventManager().readBondedDevices();
        isScanStarted = false;
        Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter e) throws Exception {
                boolean bool = mLocalManager.startScanning(true);
                StringBuilder str = new StringBuilder();
                str.append("loadData()  启动扫描了  thread=");
                str.append(Thread.currentThread().getName());
                str.append("  startScan=");
                str.append(bool);
                str.append(" state=");
                str.append(BluetoothHandsetPairViewModel.this.mLocalManager.getLocalAdapter().getState());
                str.append(" time=");
                str.append(System.currentTimeMillis());
                Log.d(TAG, str.toString());
                e.onNext(Boolean.valueOf(bool));
                e.onComplete();
            }
        }).delay(1000L, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.single()).subscribe(new MyObserver(this) {
            @Override
            public void onNext(Object value) {
                if (!((Boolean) value).booleanValue()) {
                    Log.e(TAG, "打开扫描失败");
                }
            }

            public void onError(Throwable paramAnonymousThrowable) {
            }
        });
        startTimeOutCheck(5);
        if (BluetoothUtils.isSupportBluetoothLE(getApplication())) {
            cancelScanTimeout();
            scanTimeoutDobserver = getScanTimeoutObserver();
            Observable.timer(14L, TimeUnit.SECONDS).subscribeOn(AndroidSchedulers.mainThread()).subscribe(scanTimeoutDobserver);
        }
    }
}