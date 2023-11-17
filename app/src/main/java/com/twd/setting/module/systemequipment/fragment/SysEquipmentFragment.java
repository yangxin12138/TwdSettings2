package com.twd.setting.module.systemequipment.fragment;

import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.RelativeLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.databinding.ViewStubProxy;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.twd.setting.R;
import com.twd.setting.base.BaseBindingVmFragment;
import com.twd.setting.databinding.FragmentSysEquipmentBinding;
import com.twd.setting.databinding.LayoutItemSystemEquipmentBinding;
import com.twd.setting.module.systemequipment.DeviceInfosActivity;
import com.twd.setting.module.systemequipment.StorageDetailActivity;
import com.twd.setting.module.systemequipment.vm.SysEquipmentViewModel;
import com.twd.setting.utils.HLog;
//import com.twd.setting.utils.KkDataUtils;
import com.twd.setting.utils.KkUtils;
import com.twd.setting.utils.UiUtils;
import com.twd.setting.utils.binding.ItemLRTextIconData;
import com.twd.setting.widgets.CustomDialog;
import com.twd.setting.widgets.DialogTools;

public class SysEquipmentFragment
        extends BaseBindingVmFragment<FragmentSysEquipmentBinding, SysEquipmentViewModel> {
    private static final String TAG = "SysEquipmentFrag";
    private boolean isHideOnLineUpdateItem = false;
    private Dialog loadingDialog;

    private void clickItem(int item) {
        Log.d(TAG,"clickItem: "+item);
        String str;
        if(item == R.id.onLineUpdateInclude){//1
            gotoOnLineUpdate();
            str = "在线升级";
        }else if(item == R.id.offLineUpdateViewStub){//2
            offLineUpdate();
            str = "离线升级";
        }else if(item == R.id.deviceInformationInclude){//3
            gotoDeviceInfo();
            str = "本机信息";
        }else if(item == R.id.storageDetailInclude){//4
            gotoStorageDetail();
            str = "存储详情";
        }else if(item == R.id.resetSystemInclude){//5
            resetSys();
            str = "恢复出厂设置";
        }else{
            str = null;
        }
      //  KkDataUtils.sentEventActive("系统与设备页面点击", str);
    }

    private void gotoDeviceInfo() {
        launcher.launch(new Intent(mActivity, DeviceInfosActivity.class));
    }

    private void gotoStorageDetail() {
        launcher.launch(new Intent(mActivity, StorageDetailActivity.class));
    }

    public static SysEquipmentFragment newInstance() {
        return new SysEquipmentFragment();
    }

    private void offLineUpdate() {
        DialogTools.Instance().getDialogForCustomView(mActivity, mActivity.getString(R.string.str_offline_update), mActivity.getString(R.string.str_offline_update_tip), R.string.str_confirm, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                SysEquipmentFragment.this.showDialog(true);
                ((SysEquipmentViewModel) SysEquipmentFragment.this.viewModel).offLineUpdateSystem();
            }
        }, R.string.str_cancel, null).show();
    }

    private void resetSys() {
        final Application localApplication = mActivity.getApplication();
        DialogTools.Instance().getDialogForCustomView(mActivity, mActivity.getString(R.string.str_reset_system_title), mActivity.getString(R.string.str_reset_system_tip), R.string.str_confirm, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                KkUtils.resetSystem(localApplication);
            }
        }, R.string.str_cancel, null).show();
    }

    private void setClickListener() {
        UiUtils.setOnClickListener(((FragmentSysEquipmentBinding) this.binding).onLineUpdateInclude.itemRL, ((SysEquipmentViewModel) this.viewModel).getItemClickListener());
        UiUtils.setOnClickListener(((FragmentSysEquipmentBinding) this.binding).deviceInformationInclude.itemRL, ((SysEquipmentViewModel) this.viewModel).getItemClickListener());
        UiUtils.setOnClickListener(((FragmentSysEquipmentBinding) this.binding).storageDetailInclude.itemRL, ((SysEquipmentViewModel) this.viewModel).getItemClickListener());
        UiUtils.setOnClickListener(((FragmentSysEquipmentBinding) this.binding).resetSystemInclude.itemRL, ((SysEquipmentViewModel) this.viewModel).getItemClickListener());
    }

    private void showDialog(boolean paramBoolean) {
        if (!paramBoolean) {
            Dialog localDialog = this.loadingDialog;
            if ((localDialog != null) && (localDialog.isShowing())) {
                this.loadingDialog.dismiss();
            }
            return;
        }
        if (this.loadingDialog == null) {
            this.loadingDialog = DialogTools.getLoadingDialog(this.mActivity);
        }
        if (!this.loadingDialog.isShowing()) {
            HLog.d("打印", "弹窗，检查升级文件中。。。。");
            this.loadingDialog.show();
        }
    }

    private void showOffLineItemView(boolean paramBoolean) {
        if (paramBoolean) {
            if (((FragmentSysEquipmentBinding) this.binding).offLineUpdateViewStub.getRoot() != null) {
                ((FragmentSysEquipmentBinding) this.binding).offLineUpdateViewStub.getRoot().setVisibility(View.VISIBLE);
                return;
            }
            Object localObject = ((FragmentSysEquipmentBinding) this.binding).offLineUpdateViewStub.getViewStub();
            if ((!((FragmentSysEquipmentBinding) this.binding).offLineUpdateViewStub.isInflated()) && (localObject != null)) {
                localObject = ((ViewStub) localObject).inflate();
                UiUtils.setOnClickListener((View) localObject, ((SysEquipmentViewModel) this.viewModel).getItemClickListener());
                if ((this.isHideOnLineUpdateItem) && (localObject != null)) {
                    ((View) localObject).requestFocus();
                }
            }
        } else if (((FragmentSysEquipmentBinding) this.binding).offLineUpdateViewStub.getRoot() != null) {
            ((FragmentSysEquipmentBinding) this.binding).offLineUpdateViewStub.getRoot().setVisibility(View.GONE);
        }
    }

    public void gotoOnLineUpdate() {
        Intent localIntent = new Intent("com.konka.action.upgrade.MAIN");
        localIntent.setPackage("com.konka.upgrade");
        this.launcher.launch(localIntent);
    }

    public int initLayout(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle) {
        return R.layout.fragment_sys_equipment;
    }

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sys_equipment;
    }

    public void onViewCreated(View paramView, Bundle paramBundle) {
        super.onViewCreated(paramView, paramBundle);
        ((FragmentSysEquipmentBinding) this.binding).setViewModel((SysEquipmentViewModel) this.viewModel);
        initTitle(paramView, R.string.str_system_equipment);
        boolean bool = TextUtils.isEmpty(KkUtils.getSerialNumber(this.mActivity.getApplicationContext()));
        this.isHideOnLineUpdateItem = bool;
        if (bool) {
            ((FragmentSysEquipmentBinding) this.binding).onLineUpdateInclude.itemRL.setVisibility(View.GONE);
            ((FragmentSysEquipmentBinding) this.binding).deviceInformationInclude.itemRL.requestFocus();
        } else {
            ((FragmentSysEquipmentBinding) this.binding).onLineUpdateInclude.itemRL.setVisibility(View.VISIBLE);
            ((FragmentSysEquipmentBinding) this.binding).onLineUpdateInclude.itemRL.requestFocus();
        }
        ((SysEquipmentViewModel) this.viewModel).isShowOfflineUpdate().observe(getViewLifecycleOwner(), new Observer() {
            @Override
            public void onChanged(Object o) {
                SysEquipmentFragment.this.showOffLineItemView((Boolean)o);
            }
        });
        ((SysEquipmentViewModel) this.viewModel).getClickItem().observe(getViewLifecycleOwner(), new Observer() {
            @Override
            public void onChanged(Object o) {
                SysEquipmentFragment.this.clickItem(((Integer)o).intValue());
            }
        });
        ((SysEquipmentViewModel) this.viewModel).showLoadingDialog().observe(getViewLifecycleOwner(), new Observer() {
            @Override
            public void onChanged(Object o) {
                showDialog(false);
            }
        });
        setClickListener();
    }
}
