package com.twd.setting.module.systemequipment;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.twd.setting.R;
//import com.twd.setting.SettingConfig;
import com.twd.setting.base.BaseBindingVmActivity;
import com.twd.setting.commonlibrary.Utils.ToastUtils;
import com.twd.setting.databinding.ActStorageDetailBinding;
import com.twd.setting.module.systemequipment.adapter.StorageDetailAdapter;
import com.twd.setting.module.systemequipment.model.StorageInfoData;
import com.twd.setting.module.systemequipment.vm.StorageDetailViewModel;
import com.twd.setting.utils.HLog;
import com.twd.setting.utils.UsbScanTool;
import com.twd.setting.widgets.DialogTools;

import java.util.List;

public class StorageDetailActivity
        extends BaseBindingVmActivity<ActStorageDetailBinding, StorageDetailViewModel> {
    private final String TAG = "StorageDetailActivity";
    private StorageDetailAdapter adapter;
    private Dialog loadingDialog;
    private BroadcastReceiver mBroadcastReceiver;

    private void hideDialog() {
        Dialog localDialog = this.loadingDialog;
        if ((localDialog != null) && (localDialog.isShowing())) {
            this.loadingDialog.dismiss();
        }
    }

    private void loadExternalData() {
        showDialog();
        ((StorageDetailViewModel) this.viewModel).loadExternalData(this);
    }

    private void registerUsbBroadcastReceiver() {
        IntentFilter localIntentFilter = new IntentFilter();
        localIntentFilter.addAction("android.intent.action.MEDIA_EJECT");
        localIntentFilter.addAction("android.intent.action.MEDIA_MOUNTED");
        localIntentFilter.addDataScheme("file");
        BroadcastReceiver local4 = new BroadcastReceiver() {
            public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent) {
                /*if (SettingConfig.IS_DEBUG) {
                    if ("android.intent.action.MEDIA_EJECT".equals(paramAnonymousIntent.getAction())) {
                        HLog.d("StorageDetailActivity", "U盘移除");
                    }
                    if ("android.intent.action.MEDIA_MOUNTED".equals(paramAnonymousIntent.getAction())) {
                        HLog.d("StorageDetailActivity", "U盘挂载");
                        if (SettingConfig.IS_DEBUG) {
                            StringBuilder localStringBuilder = new StringBuilder();
                            localStringBuilder.append("U盘挂载 路径=");
                            if (paramAnonymousIntent.getData() == null) {
                                paramAnonymousContext = "null";
                            } else {
                                paramAnonymousContext = paramAnonymousIntent.getData().getPath();
                            }
                            localStringBuilder.append(paramAnonymousContext);
                            HLog.d("StorageDetailActivity", localStringBuilder.toString());
                        }
                    }
                }

                 */
                if (UsbScanTool.getSingleton(StorageDetailActivity.this).isMountedStorageExist()) {
                    HLog.d("StorageDetailActivity", "U盘加载数据");
                    StorageDetailActivity.this.loadExternalData();
                    return;
                }
                if (StorageDetailActivity.this.adapter != null) {
                    StorageDetailActivity.this.adapter.setExternalStorageDataList(null);
                }
                StorageDetailActivity.this.hideDialog();
            }
        };
        this.mBroadcastReceiver = local4;
        registerReceiver(local4, localIntentFilter);
    }

    private void setOnKeyListener() {
        final int i = getResources().getDimensionPixelSize(R.dimen.deviceinfo_item_scroll_height);
        ((ActStorageDetailBinding) this.binding).storageDetailRcv.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View paramAnonymousView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent) {
                if (paramAnonymousKeyEvent.getAction() != 1) {
                    return false;
                }
                if ((paramAnonymousInt == 19) && (((ActStorageDetailBinding) StorageDetailActivity.this.binding).storageDetailRcv.canScrollVertically(-1))) {
                    ((ActStorageDetailBinding) StorageDetailActivity.this.binding).storageDetailRcv.scrollBy(0, -i);
                    return true;
                }
                if ((paramAnonymousInt == 20) && (((ActStorageDetailBinding) StorageDetailActivity.this.binding).storageDetailRcv.canScrollVertically(1))) {
                    ((ActStorageDetailBinding) StorageDetailActivity.this.binding).storageDetailRcv.scrollBy(0, i);
                    return true;
                }
                return false;
            }
        });
    }

    private void showDialog() {
        if (this.loadingDialog == null) {
            this.loadingDialog = DialogTools.getLoadingDialog(this, getString(R.string.str_scanning_usb_tip));
        }
        if (!this.loadingDialog.isShowing()) {
            this.loadingDialog.show();
        }
    }

    //public int initLayout(Bundle paramBundle) {
    //    return R.layout.act_storage_detail;
    //}

    @Override
    public int initLayout(Bundle paramBundle) {
        return R.layout.act_storage_detail;
    }

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        initTitle(getString(R.string.str_storage_detail));
        this.adapter = new StorageDetailAdapter();
        ((ActStorageDetailBinding) this.binding).storageDetailRcv.setAdapter(this.adapter);
        setOnKeyListener();
        ((StorageDetailViewModel) this.viewModel).getSysStorageInfo().observe(this, new Observer() {
            @Override
            public void onChanged(Object o) {
                if (o == null) {
                    ToastUtils.showShort(StorageDetailActivity.this.getString(R.string.str_storage_detail_sys_error));
                }
                HLog.d("StorageDetailActivity", "刷新sys数据  更新view了");
                if (StorageDetailActivity.this.adapter != null) {
                    StorageDetailActivity.this.adapter.setSystemStorageData((StorageInfoData)o);
                }
            }
        });
        ((StorageDetailViewModel) this.viewModel).getExternalStorageInfo().observe(this, new Observer() {
            @Override
            public void onChanged(Object o) {
                if (StorageDetailActivity.this.adapter != null) {
                    StorageDetailActivity.this.adapter.setExternalStorageDataList((List<StorageInfoData>)o);
                }
                StorageDetailActivity.this.hideDialog();
            }
        });
        ((StorageDetailViewModel) this.viewModel).loadSysData(this);
        if (UsbScanTool.getSingleton(this).isMountedStorageExist()) {
            loadExternalData();
        }
        registerUsbBroadcastReceiver();
    }

    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }
}

