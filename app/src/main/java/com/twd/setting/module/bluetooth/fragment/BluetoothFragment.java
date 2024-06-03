package com.twd.setting.module.bluetooth.fragment;

import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
//import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.twd.setting.R;
import com.twd.setting.base.BaseBindingVmFragment;
import com.twd.setting.commonlibrary.Utils.ToastUtils;
import com.twd.setting.databinding.FragmentBluetoothBinding;
import com.twd.setting.module.bluetooth.adapter.BluetoothListAdapter;
import com.twd.setting.module.bluetooth.model.CachedBluetoothDevice;
import com.twd.setting.module.bluetooth.vm.BluetoothViewModel;
import com.twd.setting.utils.BluetoothUtils;
//import com.twd.setting.utils.HLog;
//import com.twd.setting.utils.KkDataUtils;
import com.twd.setting.utils.UiUtils;
import com.twd.setting.widgets.CustomDialog;
import com.twd.setting.widgets.DialogTools;
import com.twd.setting.widgets.SwitchLayoutView;
import com.twd.setting.widgets.SwitchLayoutView.OnCheckedListener;

import java.util.List;

public class BluetoothFragment
        extends BaseBindingVmFragment<FragmentBluetoothBinding, BluetoothViewModel> {
    private static final String TAG = "BluetoothFragment";
    private BluetoothListAdapter adapter;
    private boolean isShowRemoteControlFragment = false;

    private void bondedDeviceDialog(final CachedBluetoothDevice device) {
        DialogTools.Instance().getDialogForCustomView(mActivity, device.getName(), getString(R.string.str_bluetooth_dialog_bonded_msg),
                R.string.str_bluetooth_dialog_btn_connect, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        viewModel.connect(device);
                    }
                }, R.string.str_bluetooth_dialog_btn_ignore, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        viewModel.unPair(device);
                    }
                }).show();
    }

    private void connectedDeviceDialog(final CachedBluetoothDevice device) {
        DialogTools.Instance().getDialogForCustomView(mActivity, device.getName(), getString(R.string.str_bluetooth_dialog_connected_msg),
                R.string.str_bluetooth_dialog_btn_unconnect, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        viewModel.disConnect(device);
                    }
                }, R.string.str_bluetooth_dialog_btn_ignore, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        viewModel.unPair(device);
                    }
                }).show();
    }

    private View.OnClickListener getItemClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CachedBluetoothDevice device = (CachedBluetoothDevice) view.getTag();
                Log.d(TAG, "onClick 打印item id=" + device.getName());
                if (!viewModel.isInitSuccess()) {
                    return;
                }
                if (device.isFirstBtnItem()) {
                    if (viewModel.isDiscovering()) {
                        Log.d(TAG, "onClick  已经在扫描搜索了");
                        return;
                    }
                    viewModel.startScan();
                    return;
                }
                if (device.getDevice() == null) {
                    ToastUtils.showShort("操作失败，数据无效");
                    return;
                }
                viewModel.stopScan();
                if (device.isConnected()) {
                    connectedDeviceDialog(device);
                    return;
                }
                if (device.isBonded()) {
                    Log.d(TAG, "device.isBonded,  do unBondDeviceDialog");
                    bondedDeviceDialog(device);
                    //unBondDeviceDialog(device);
                } else {
                    Log.d(TAG, "device.is not Bonded,  do BondDeviceDialog");
                    //bondedDeviceDialog(device);
                    unBondDeviceDialog(device);
                }


            }
        };
    }

    public static BluetoothFragment newInstance() {
        return new BluetoothFragment();
    }

    private void setClickListener() {
        binding.bluetoothSwitchRL.setCheckedListener(new SwitchLayoutView.OnCheckedListener() {
            @Override
            public void onCheckedChanged(boolean changed) {
                binding.bluetoothSwitchRL.setSwitchEnable(false);
                boolean bool;
                if (changed) {
                    bool = viewModel.enableBluetooth() ^ true;
                } else {
                    viewModel.stopScan();
                    bool = viewModel.disableBluetooth() ^ true;
                    updateViewEnable(false);
                }

                Log.d(TAG, "蓝牙开关操作：" + changed + "  开关函数结果=" + bool);
                //localObject = "打开";
                if (bool) {
                    ToastUtils.showShort(mActivity.getString(R.string.str_error_tip));
                    if (changed) {
                        binding.bluetoothSwitchRL.setChecked(false);
                    }
                    binding.bluetoothSwitchRL.setSwitchEnable(true);
                    //if (!changed) {
                    //  localObject = "关闭";
                    //}
                    // KkDataUtils.sentEventError("蓝牙开关切换", (String)localObject);
                    return;
                }
                //if (!changed) {
                //  localObject = "关闭";
                //}
                //KkDataUtils.sentEventActive("蓝牙开关切换", (String)localObject);
            }
        });
        UiUtils.setOnClickListener(binding.bluetoothRemoteControlTV, new View.OnClickListener() {
            @Override
            public void onClick(View paramAnonymousView) {
                viewModel.stopScan();

                //UiUtils.addFragment(mActivity.getSupportFragmentManager(), 16908290, new BluetoothHandsetFragment(), BluetoothFragment.this);
                UiUtils.addFragment(mActivity.getSupportFragmentManager(), 16908290, new BluetoothHandsetPairFragment(), BluetoothFragment.this);
                //BluetoothFragment.access$2502(this, true);

            }
        });
    }

    private void setDataObserve() {
        viewModel.getBluetoothSwitchBtn().observe(getViewLifecycleOwner(), new Observer() {
            @Override
            public void onChanged(Object o) {
                if (((Integer) o).intValue() == 10) {
                    Log.d(TAG, "蓝牙状态：已关闭，开关：" + binding.bluetoothSwitchRL.isChecked());
                    binding.bluetoothSwitchRL.setSwitchEnable(true);
                    if (binding.bluetoothSwitchRL.isChecked()) {
                        binding.bluetoothSwitchRL.setChecked(false);
                    }
                }
                if (((Integer) o).intValue() == 12) {
                    Log.d("BluetoothFragment", "蓝牙状态：已打开");
                    BluetoothUtils.setScanMode(BluetoothAdapter.getDefaultAdapter(), 23);
                    binding.bluetoothSwitchRL.setSwitchEnable(true);
                    updateViewEnable(true);
                    viewModel.startScan();
                }
            }
        });
        viewModel.getBluetoothItemList().observe(getViewLifecycleOwner(), new Observer() {
            @Override
            public void onChanged(Object o) {
                if (adapter != null) {
                    int i;
                    if (o == null) {
                        i = 0;
                    } else {
                        i = ((List<CachedBluetoothDevice>) o).size();
                    }
                    Log.d(TAG, "submitList  刷新数据了 size=" + i);
                    for(int j=0;j<i;j++){
                        Log.d(TAG,""+((List<CachedBluetoothDevice>) o).get(j).getName());
                    }
                    adapter.submitList((List<CachedBluetoothDevice>) o);
                }
            }
        });
        viewModel.getSearchBtnStatus().observe(getViewLifecycleOwner(), new Observer() {
            @Override
            public void onChanged(Object o) {
                updateScanStatus(((Boolean) o).booleanValue());
                if (adapter != null) {
                    adapter.updateFirstBtn(((Boolean) o).booleanValue());
                }
            }
        });
    }

    private void unBondDeviceDialog(final CachedBluetoothDevice paramCachedBluetoothDevice) {
        DialogTools.Instance().getDialogForCustomView(mActivity, getString(R.string.str_bluetooth_dialog_unbond_title, new Object[]{paramCachedBluetoothDevice.getName()}), getString(R.string.str_bluetooth_dialog_unbond_msg),
                R.string.str_bluetooth_dialog_btn_pair, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                        viewModel.connect(paramCachedBluetoothDevice);
                    }
                },
                R.string.str_cancel, null).show();
    }

    private void updateScanStatus(boolean update) {
        if (update) {
            binding.bluetoothListProgress.setVisibility(View.VISIBLE);
        } else {
            binding.bluetoothListProgress.setVisibility(View.GONE);
        }
    }

    private void updateViewEnable(boolean paramBoolean) {
        binding.bluetoothRemoteControlTitleTV.setEnabled(paramBoolean);
        binding.bluetoothRemoteControlTV.setEnabled(paramBoolean);
        binding.bluetoothRemoteControlTV.setFocusable(paramBoolean);
        binding.bluetoothListTitleTV.setEnabled(paramBoolean);
        if (paramBoolean) {
            binding.bluetoothRV.setVisibility(View.VISIBLE);
            return;
        }
        binding.bluetoothRV.setVisibility(View.INVISIBLE);
    }

    //public int initLayout(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
    //{
    //  return 2131492911;
    //}
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_bluetooth;
    }

    public void onHiddenChanged(boolean paramBoolean) {
        super.onHiddenChanged(paramBoolean);
        if (viewModel != null) {
            viewModel.onHiddenChanged(paramBoolean);
        }
        if ((isShowRemoteControlFragment) && (!paramBoolean) && (binding != null)) {
            binding.bluetoothRemoteControlTV.requestFocus();
            isShowRemoteControlFragment = false;
            viewModel.refreshList();
        }
    }

    public void onViewCreated(View paramView, Bundle paramBundle) {
        super.onViewCreated(paramView, paramBundle);
        binding.setViewModel(viewModel);
        initTitle(paramView, R.string.str_bluetooth);
        if (adapter == null) {
            adapter = new BluetoothListAdapter(getItemClick());
        }
        binding.bluetoothRV.setAdapter(adapter);
        setClickListener();
        setDataObserve();
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        binding.bluetoothSwitchRL.requestFocus();
        if (BluetoothUtils.isSupportBluetooth()) {
            //Log.d(TAG, "蓝牙设备名："+mBluetoothAdapter.getName());
            /*binding.bluetoothSwitchRL.setEnabled(true);
            binding.bluetoothSwitchRL.setSwitchEnable(true);
            updateViewEnable(paramView.isEnabled());
            binding.bluetoothSwitchRL.setChecked(paramView.isEnabled());
            if (paramView.isEnabled()) {
                BluetoothUtils.setScanMode(mBluetoothAdapter, 23);
            }*/
            Log.d(TAG, "onViewCreated: 蓝牙支持");
            if (mBluetoothAdapter.isEnabled()){
                Log.d(TAG, "onViewCreated: 蓝牙已打开");
                binding.bluetoothSwitchRL.setEnabled(true);
                binding.bluetoothSwitchRL.setSwitchEnable(true);
                updateViewEnable(paramView.isEnabled());
                binding.bluetoothSwitchRL.setChecked(paramView.isEnabled());
                if (paramView.isEnabled()){
                    BluetoothUtils.setScanMode(mBluetoothAdapter,23);
                }
            }else {
                Log.d(TAG, "onViewCreated: 蓝牙已关闭");
                binding.bluetoothSwitchRL.setEnabled(true);
                binding.bluetoothSwitchRL.setSwitchEnable(true);
                updateViewEnable(false);
                binding.bluetoothSwitchRL.setChecked(false);
                binding.bluetoothSwitchRL.requestFocus();
            }
        } else {
            Log.d(TAG, "onViewCreated: 设备不支持蓝牙");
            ToastUtils.showShort(mActivity.getString(R.string.str_unsupport_bluetooth_tip));
            binding.bluetoothSwitchRL.setEnabled(false);
            binding.bluetoothSwitchRL.setSwitchEnable(false);
            updateViewEnable(false);
        }
    }


}
