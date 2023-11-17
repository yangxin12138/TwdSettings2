package com.twd.setting.module.systemequipment.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.twd.setting.R;
import com.twd.setting.base.BaseFragment;
import com.twd.setting.commonlibrary.Utils.RxTransUtils;
import com.twd.setting.commonlibrary.Utils.StringUtils;
import com.twd.setting.module.systemequipment.adapter.DeviceInfosAdapter;
import com.twd.setting.module.systemequipment.model.DeviceInfoItem;
import com.twd.setting.utils.HLog;
import com.twd.setting.utils.KkUtils;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.observers.DisposableObserver;

import java.util.ArrayList;
import java.util.List;

public class DeviceInfosFragment
        extends BaseFragment {
    private DisposableObserver<List<DeviceInfoItem>> disposableObserver;
    private RecyclerView sysInfoRV;

    private List<DeviceInfoItem> getData(Context mContext, String paramString) {
        ArrayList list = new ArrayList();
        list.add(new DeviceInfoItem(mContext.getString(R.string.str_device_info_platform), KkUtils.getPlatform(mContext)));
        list.add(new DeviceInfoItem(mContext.getString(R.string.str_device_info_series), KkUtils.getSeries()));
        list.add(new DeviceInfoItem(mContext.getString(R.string.str_device_info_model), KkUtils.getType(mContext)));
        list.add(new DeviceInfoItem(mContext.getString(R.string.str_device_info_android_version), mContext.getString(R.string.str_device_info_android, new Object[]{Build.VERSION.RELEASE})));
        list.add(new DeviceInfoItem(mContext.getString(R.string.str_device_info_software_no), KkUtils.getMainCode(mContext)));
        list.add(new DeviceInfoItem(mContext.getString(R.string.str_device_info_serial_number), KkUtils.getSerialNumber(mContext)));
        if (!StringUtils.isTrimEmpty(paramString)) {
            list.add(new DeviceInfoItem(mContext.getString(R.string.str_device_info_mac_addr), paramString));
        }

        Log.d(TAG, "有线macAddr="+paramString);
        String wifiMacAddr = KkUtils.getWifiMacAddr(mContext.getApplicationContext());
        if (!StringUtils.isTrimEmpty(wifiMacAddr)) {
            list.add(new DeviceInfoItem(mContext.getString(R.string.str_device_info_wifi_mac_addr), wifiMacAddr));
        }
        String bluetoothMacAddr = KkUtils.getBluetoothMacAddr();
        if (!StringUtils.isTrimEmpty(bluetoothMacAddr)) {
            list.add(new DeviceInfoItem(mContext.getString(R.string.str_device_info_bluetooth_mac_addr), bluetoothMacAddr));
        }
        return list;
    }

    private DisposableObserver<List<DeviceInfoItem>> getListDp() {
        return new DisposableObserver() {
            public void onComplete() {
            }

            @Override
            public void onNext(Object value) {
                if (sysInfoRV == null) {
                    return;
                }
                sysInfoRV.setAdapter(new DeviceInfosAdapter((List<DeviceInfoItem>)value));
            }

            public void onError(Throwable paramAnonymousThrowable) {
            }
        };
    }

    private void initdata() {
        stopDispose();
        disposableObserver = getListDp();
        final String str = KkUtils.getMac();
        Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter e) throws Exception {
                e.onNext(DeviceInfosFragment.this.getData(DeviceInfosFragment.this.mActivity, str));
            }
        }).compose(RxTransUtils.schedulersIoToUi()).subscribe(disposableObserver);
    }

    private void setOnKeyListener() {
        if (sysInfoRV == null) {
            return;
        }
        final int i = mActivity.getResources().getDimensionPixelSize(R.dimen.deviceinfo_item_scroll_height);
        sysInfoRV.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View paramAnonymousView, int keycode, KeyEvent keyEvent) {
                if (keyEvent.getAction() != KeyEvent.ACTION_UP) {
                    return false;
                }
                if ((keycode == KeyEvent.KEYCODE_DPAD_UP) && (DeviceInfosFragment.this.sysInfoRV.canScrollVertically(-1))) {
                    DeviceInfosFragment.this.sysInfoRV.scrollBy(0, -i);
                    return true;
                }
                if ((keycode == KeyEvent.KEYCODE_DPAD_DOWN) && (DeviceInfosFragment.this.sysInfoRV.canScrollVertically(1))) {
                    DeviceInfosFragment.this.sysInfoRV.scrollBy(0, i);
                    return true;
                }
                return false;
            }
        });
    }

    private void stopDispose() {
        if ((disposableObserver != null) && (!disposableObserver.isDisposed())) {
            disposableObserver.dispose();
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup group, Bundle bundle) {
        return layoutInflater.inflate(R.layout.fragment_device_infos, group, false);
    }

    public void onStop() {
        super.onStop();
        stopDispose();
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        initTitle(view, R.string.str_device_information);
        sysInfoRV = ((RecyclerView) view.findViewById(R.id.deviceInfoRV));
        setOnKeyListener();
        initdata();
    }
}

