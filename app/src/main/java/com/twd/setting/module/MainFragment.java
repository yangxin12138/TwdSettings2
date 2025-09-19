package com.twd.setting.module;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;

import com.twd.setting.R;
import com.twd.setting.base.BaseBindingVmFragment;
import com.twd.setting.base.FragmentOnKeyListener;
import com.twd.setting.databinding.FragmentMainBinding;
import com.twd.setting.module.bluetooth.BluetoothActivity;
import com.twd.setting.module.common.CommonActivity;
import com.twd.setting.module.device.DeviceActivity;
import com.twd.setting.module.network.NetworkActivity;
import com.twd.setting.module.projector.ProjectorActivity;
import com.twd.setting.module.systemequipment.SysEquipmentActivity;
import com.twd.setting.module.universal.UniversalActivity;
import com.twd.setting.utils.UiUtils;
import com.twd.setting.utils.binding.ItemLRTextIconData;


public class MainFragment extends BaseBindingVmFragment<FragmentMainBinding, MainViewModel> implements FragmentOnKeyListener {
    private static String TAG = "MainFragment";
    private int iconWidth;
    private boolean initItemVisibleFinish;

    private final View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            //if(view.getTag() != null) {

            if (view.getId() == R.id.networkItem){
                binding.networkItem.leftIv.setImageResource(hasFocus ? R.drawable.ic_network_red : R.drawable.ic_network_v);
            } else if (view.getId() == R.id.bluetoothItem) {
                binding.bluetoothItem.leftIv.setImageResource(hasFocus ? R.drawable.ic_bluetooth_red : R.drawable.ic_bluetooth_v);
            } else if (view.getId() == R.id.commonItem) {
                binding.commonItem.leftIv.setImageResource(hasFocus ? R.drawable.ic_common_red : R.drawable.ic_common_v);
            } else if (view.getId() == R.id.systemEquipmentItem) {
                binding.systemEquipmentItem.leftIv.setImageResource(hasFocus ? R.drawable.ic_sys_equipment_red : R.drawable.ic_sys_equipment_v);
            } else if (view.getId() == R.id.projectorItem) {
                binding.projectorItem.leftIv.setImageResource(hasFocus ? R.drawable.ic_projector_v : R.drawable.ic_projector_black);
            }
            updateBigIconText(view.getId());
        }
    };

    private void gotoActivity(int id) {
        MainItemVisible localMainItemVisible = null;
        Intent intent = null;
        if (id == R.id.debugMenuItem) {
            //intent = new Intent(mActivity, DebugMenuActivity.class);
        } else if (id == R.id.systemEquipmentItem) {
            intent = new Intent(mActivity, DeviceActivity.class);
        } else if (id == R.id.commonItem) {
            intent = new Intent(mActivity, UniversalActivity.class);
        } else if (id == R.id.bluetoothItem) {
            intent = new Intent(mActivity, BluetoothActivity.class);
        } else if (id == R.id.networkItem) {
            intent = new Intent(mActivity, NetworkActivity.class);
        } else if (id == R.id.signalSourceItem) {
            localMainItemVisible = viewModel.getMainItemLayoutVisibleData();
            if (localMainItemVisible != null)
            {
                if (localMainItemVisible.getSourceCname() != null)
                {
                    intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setComponent(localMainItemVisible.getSourceCname());
                }
            }
        } else if (id == R.id.projectorItem) {
            intent = new Intent(mActivity, ProjectorActivity.class);
        }

        if (intent != null) {
            try {
                launcher.launch(intent);
            } catch (Exception localException) {
                localException.printStackTrace();

            }
        }
    }

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    private void setBigIconText(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        Drawable localDrawable = AppCompatResources.getDrawable(getContext(), paramInt1);
        if (localDrawable != null) {
            localDrawable.setBounds(0, 0, paramInt3, paramInt4);
            binding.rightBigIconTv.setCompoundDrawables(null, localDrawable, null, null);
        }
        binding.rightBigIconTv.setText(paramInt2);
    }

    private void setClickListener() {
        UiUtils.setOnClickListener(binding.projectorItem.itemRL, viewModel.getItemClickListener());
        UiUtils.setOnClickListener(binding.signalSourceItem.itemRL, viewModel.getItemClickListener());
        UiUtils.setOnClickListener(binding.networkItem.itemRL, viewModel.getItemClickListener());
        UiUtils.setOnClickListener(binding.bluetoothItem.itemRL, viewModel.getItemClickListener());
        UiUtils.setOnClickListener(binding.commonItem.itemRL, viewModel.getItemClickListener());
        UiUtils.setOnClickListener(binding.systemEquipmentItem.itemRL, viewModel.getItemClickListener());
        UiUtils.setOnClickListener(binding.debugMenuItem.itemRL, viewModel.getItemClickListener());
    }

    private void setFocusChangeListener() {
        binding.projectorItem.itemRL.setOnFocusChangeListener(focusChangeListener);
        binding.signalSourceItem.itemRL.setOnFocusChangeListener(focusChangeListener);
        binding.networkItem.itemRL.setOnFocusChangeListener(focusChangeListener);
        binding.bluetoothItem.itemRL.setOnFocusChangeListener(focusChangeListener);
        binding.commonItem.itemRL.setOnFocusChangeListener(focusChangeListener);
        binding.systemEquipmentItem.itemRL.setOnFocusChangeListener(focusChangeListener);
        binding.debugMenuItem.itemRL.setOnFocusChangeListener(focusChangeListener);
    }

    private void setItemFocusAndHeight(boolean paramBoolean1, boolean paramBoolean2, int paramInt) {
        if (paramBoolean1) {
            binding.projectorItem.itemRL.requestFocus();
        } else if (paramBoolean2) {
            binding.signalSourceItem.itemRL.requestFocus();
        } else {
            binding.networkItem.itemRL.requestFocus();
        }
        if (paramInt <= 0) {
            return;
        }
        ConstraintLayout.LayoutParams localLayoutParams1 = (ConstraintLayout.LayoutParams) binding.topSpace.getLayoutParams();
        ConstraintLayout.LayoutParams localLayoutParams2 = (ConstraintLayout.LayoutParams) binding.bottomSpace.getLayoutParams();
        if (paramInt > 2) {
            localLayoutParams1.verticalWeight = 0.8F;
            localLayoutParams2.verticalWeight = 1.5F;
        } else if (paramInt == 2) {
            localLayoutParams1.verticalWeight = 0.7F;
            localLayoutParams2.verticalWeight = 0.5F;
        } else {
            localLayoutParams1.verticalWeight = 0.4F;
            localLayoutParams2.verticalWeight = 0.3F;
        }
        binding.topSpace.setLayoutParams(localLayoutParams1);
        binding.bottomSpace.setLayoutParams(localLayoutParams2);
    }

    private void setMainItemVisible(MainItemVisible paramMainItemVisible) {
        if (paramMainItemVisible != null) {
            if (initItemVisibleFinish) {
                return;
            }
            initItemVisibleFinish = true;
            int i = 3;
            Log.d(TAG, "setMainItemVisible  ......");
//            if (paramMainItemVisible.isProjectorVisible())
//            {
//                binding.projectorItem.itemRL.setVisibility(View.VISIBLE);
//                i = 2;
//            }
//            else
//            {
            //binding.projectorItem.itemRL.setVisibility(View.GONE);
            binding.projectorItem.itemRL.setVisibility(View.VISIBLE);
//            }
//            if (paramMainItemVisible.isSourceVisible())
//            {
//                binding.signalSourceItem.itemRL.setVisibility(View.VISIBLE);
//                i -= 1;
//            }
//            else
//            {
            binding.signalSourceItem.itemRL.setVisibility(View.GONE);
            //binding.signalSourceItem.itemRL.setVisibility(View.VISIBLE);
//            }
//            if (paramMainItemVisible.isBluetoothVisible())
//            {
//                binding.bluetoothItem.itemRL.setVisibility(View.VISIBLE);
//                i -= 1;
//            }
//            else
//            {
//                binding.bluetoothItem.itemRL.setVisibility(View.GONE);
//            }
            //setItemFocusAndHeight(paramMainItemVisible.isProjectorVisible(), paramMainItemVisible.isSourceVisible(), i);
            setItemFocusAndHeight(true, true, 5);
        }
    }

    private void setVmObserve() {
        viewModel.getClickItem().observe(getViewLifecycleOwner(), new Observer() {
            @Override
            public void onChanged(Object o) {
                Log.d(TAG, "setVmObserve  getClickItem  ......");
                gotoActivity(((Integer) o).intValue());
            }
        });
        viewModel.getDebugMenuItemShow().observe(getViewLifecycleOwner(), new Observer() {
            @Override
            public void onChanged(Object o) {
                Log.d(TAG, "setVmObserve  getDebugMenuItemShow  ......");
                RelativeLayout localRelativeLayout = binding.debugMenuItem.itemRL;
                int i;
                if (((Boolean) o).booleanValue()) {
                    i = View.VISIBLE;
                } else {
                    i = View.GONE;
                }
                localRelativeLayout.setVisibility(i);
            }
        });
        viewModel.getItemVisible().observe(getViewLifecycleOwner(), new Observer() {
            @Override
            public void onChanged(Object o) {
                Log.d(TAG, "setVmObserve  getItemVisible  ......");
                setMainItemVisible((MainItemVisible) o);
            }
        });
    }

    private void updateBigIconText(int id) {
        if (id == R.id.debugMenuItem) {
            setBigIconText(R.drawable.ic_debug_menu_v, R.string.str_debug_menu, iconWidth, iconWidth);
        } else if (id == R.id.systemEquipmentItem) {
            setBigIconText(R.drawable.about_black, R.string.str_system_equipment, iconWidth, (int) (iconWidth * 0.619F));
        } else if (id == R.id.commonItem) {
            setBigIconText(R.drawable.setup_black, R.string.str_common, iconWidth, iconWidth);
        } else if (id == R.id.bluetoothItem) {
            setBigIconText(R.drawable.bluetooth_black, R.string.str_bluetooth, (int) (iconWidth * 0.6F), iconWidth);
        } else if (id == R.id.networkItem) {
            setBigIconText(R.drawable.wifi_black, R.string.str_network, iconWidth, (int) (iconWidth * 0.725F));
        } else if (id == R.id.signalSourceItem) {
            setBigIconText(R.drawable.ic_signal_source_v, R.string.str_signal_source, iconWidth, (int) (iconWidth * 0.5476F));
        } else if (id == R.id.projectorItem) {
            setBigIconText(R.drawable.projection_black, R.string.str_projector_setting, iconWidth, iconWidth);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        iconWidth = ((int) (dm.widthPixels * 0.219F));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((event.getAction() == 0) && (viewModel != null)) {
            return viewModel.keyDown(keyCode);
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!initItemVisibleFinish) {
            setMainItemVisible(viewModel.getMainItemLayoutVisibleData());
        }
    }

    @Override
    public void onViewCreated(View paramView, Bundle paramBundle) {
        super.onViewCreated(paramView, paramBundle);
        binding.setViewModel(viewModel);

        initItemVisibleFinish = false;
        setVmObserve();
        setFocusChangeListener();
        setClickListener();
        binding.projectorItem.itemRL.requestFocus();
    }

}
