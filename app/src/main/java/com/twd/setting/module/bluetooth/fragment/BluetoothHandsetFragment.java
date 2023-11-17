package com.twd.setting.module.bluetooth.fragment;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.twd.setting.R;
import com.twd.setting.base.BaseBindingVmFragment;
import com.twd.setting.databinding.FragmentBluetoothHandsetBinding;
import com.twd.setting.module.bluetooth.adapter.BluetoothHandsetListAdapter;
import com.twd.setting.module.bluetooth.model.CachedBluetoothDevice;
import com.twd.setting.module.bluetooth.vm.BluetoothHandsetViewModel;
import com.twd.setting.utils.UiUtils;
import com.twd.setting.widgets.CustomDialog;
import com.twd.setting.widgets.DialogTools;

import java.util.List;

public class BluetoothHandsetFragment
        extends BaseBindingVmFragment<FragmentBluetoothHandsetBinding, BluetoothHandsetViewModel> {
    private BluetoothHandsetListAdapter adapter;

    private void addBluetoothHandset() {
        UiUtils.addFragment(mActivity.getSupportFragmentManager(), 16908290, new BluetoothHandsetPairFragment(), this);
    }

    private void bondedDeviceDialog(final CachedBluetoothDevice paramCachedBluetoothDevice) {
        DialogTools.Instance().getDialogForCustomView(mActivity, paramCachedBluetoothDevice.getName(), getString(R.string.str_bluetooth_dialog_bonded_msg), R.string.str_bluetooth_dialog_btn_connect, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                paramCachedBluetoothDevice.setNewStatus(1);
                adapter.updateDevicesState(paramCachedBluetoothDevice);
                viewModel.connect(paramCachedBluetoothDevice);
            }
        }, R.string.str_bluetooth_dialog_btn_ignore, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                viewModel.unPair(paramCachedBluetoothDevice);
            }
        }).show();
    }

    private void connectedDeviceDialog(final CachedBluetoothDevice paramCachedBluetoothDevice) {
        DialogTools.Instance().getDialogForCustomView(mActivity, paramCachedBluetoothDevice.getName(), getString(R.string.str_bluetooth_dialog_connected_msg), R.string.str_bluetooth_dialog_btn_unconnect, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                viewModel.disConnect(paramCachedBluetoothDevice);
            }
        }, R.string.str_bluetooth_dialog_btn_ignore, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                viewModel.unPair(paramCachedBluetoothDevice);
            }
        }).show();
    }

    public int initLayout(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle) {
        return R.layout.fragment_bluetooth_handset;
    }

    public void onHiddenChanged(boolean paramBoolean) {
        super.onHiddenChanged(paramBoolean);
        if ((!paramBoolean) && (binding != null)) {
            viewModel.loadBoundedDataList(false);
            binding.bluetoothHandsetRV.requestFocus();
        }
    }

    public void onViewCreated(View paramView, Bundle paramBundle) {
        super.onViewCreated(paramView, paramBundle);
        initTitle(paramView, R.string.str_bluetooth_remote_control);
        if (adapter == null) {
            adapter = new BluetoothHandsetListAdapter(viewModel.getClickHandle());
        }
        binding.bluetoothHandsetRV.setAdapter(adapter);
        binding.bluetoothHandsetRV.requestFocus();
        viewModel.getBluetoothBondedList().observe(getViewLifecycleOwner(), new Observer() {
            @Override
            public void onChanged(Object o) {
                adapter.setDeviceDataList((List<CachedBluetoothDevice>) o);
            }
        });
        viewModel.getClickBluetoothItemValue().observe(getViewLifecycleOwner(), new Observer() {
            @Override
            public void onChanged(Object o) {
                if (((CachedBluetoothDevice) o).isFirstBtnItem()) {
                    addBluetoothHandset();
                    return;
                }
                if (((CachedBluetoothDevice) o).isConnected()) {
                    connectedDeviceDialog(((CachedBluetoothDevice) o));
                    return;
                }
                bondedDeviceDialog(((CachedBluetoothDevice) o));
            }
        });
        viewModel.getBluetoothStateChanged().observe(getViewLifecycleOwner(), new Observer() {
            @Override
            public void onChanged(Object o) {
                adapter.updateDevicesState((CachedBluetoothDevice) o);
            }
        });
        if (viewModel.isDefaultGuidePage()) {
            addBluetoothHandset();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_bluetooth_handset;
    }
}
