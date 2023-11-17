package com.twd.setting.module.common.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.twd.setting.R;
import com.twd.setting.base.BaseBindingVmFragment;
import com.twd.setting.commonlibrary.Utils.ToastUtils;
import com.twd.setting.databinding.FragmentCommonBinding;
import com.twd.setting.databinding.LayoutItemSystemEquipmentBinding;
import com.twd.setting.module.common.model.CommonSettingVisible;
import com.twd.setting.module.common.vm.CommonViewModel;
import com.twd.setting.utils.HLog;
//import com.twd.setting.utils.KkDataUtils;
import com.twd.setting.utils.KkUtils;
import com.twd.setting.utils.UiUtils;
import com.twd.setting.utils.binding.ItemLRTextIconData;
import com.twd.setting.widgets.SwitchLayoutView;
import com.twd.setting.widgets.SwitchLayoutView.OnCheckedListener;

public class CommonFragment
        extends BaseBindingVmFragment<FragmentCommonBinding, CommonViewModel> {
    private final static String TAG = "CommonFragment";
    private final View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Log.d(TAG, "getTag:" + view.getTag());
            if (view.getId() == R.id.imeLayout) {
                //if (((Integer) view.getTag()).intValue() == 0) {
                UiUtils.addFragment(getParentFragmentManager(), 16908290, InputMethodEditorFragment.newInstance(), CommonFragment.this);
                return;
            }
            CommonSettingVisible commonSettingVisible = viewModel.getSettingVisible();
            if ((commonSettingVisible != null) && (commonSettingVisible.getSettingCname() != null)) {
                Intent localIntent = new Intent();
                localIntent.setComponent(commonSettingVisible.getSettingCname());
                startActivity(localIntent);
            }
            ToastUtils.showShort("操作失败");
        }
    };
    private final FragmentResultListener resultListener = new FragmentResultListener() {
        public void onFragmentResult(String paramAnonymousString, Bundle bundle) {
            String ime = bundle.getString("selectedImeName");
            Log.d(TAG, "选择了输入法：" + ime);
            ((CommonViewModel) viewModel).getImeData().setRightTxt(ime);
        }
    };

    private void setClickListener() {
        UiUtils.setOnClickListener(((FragmentCommonBinding) this.binding).imeLayout.itemRL, clickListener);
        UiUtils.setOnClickListener(((FragmentCommonBinding) this.binding).settingsLayout.itemRL, clickListener);
    }

    private void setSettingVisible(CommonSettingVisible paramCommonSettingVisible) {
        if ((paramCommonSettingVisible != null) && (paramCommonSettingVisible.getSettingCname() != null)) {
            ((FragmentCommonBinding) this.binding).settingsLayout.itemRL.setVisibility(View.VISIBLE);
            return;
        }
//        ((FragmentCommonBinding) this.binding).settingsLayout.itemRL.setVisibility(View.GONE);
    }

    public int initLayout(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle) {
        return R.layout.fragment_common;
    }

    public CommonViewModel initViewModel() {
        return (CommonViewModel) new ViewModelProvider(this.mActivity).get(CommonViewModel.class);
    }

    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        getParentFragmentManager().setFragmentResultListener("selectedIme", this, this.resultListener);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_common;
    }

    public void onViewCreated(View paramView, Bundle paramBundle) {
        super.onViewCreated(paramView, paramBundle);
        Log.d(TAG, "onViewCreated");
        ((FragmentCommonBinding) this.binding).setViewModel((CommonViewModel) this.viewModel);
        initTitle(paramView, R.string.str_common);
        setClickListener();
        ((FragmentCommonBinding) this.binding).screenSaverSwitchRL.setChecked(KkUtils.isScreenSaverEnable(getContext().getApplicationContext()));
        ((FragmentCommonBinding) this.binding).screenSaverSwitchRL.setCheckedListener(new SwitchLayoutView.OnCheckedListener() {
            @Override
            public void onCheckedChanged(boolean paramAnonymousBoolean) {
        /*        boolean bool = KkUtils.setScreenSaverEnable(CommonFragment.this.getContext().getApplicationContext(), paramAnonymousBoolean);
                String str = "打开";
                if (bool) {
                    if (!paramAnonymousBoolean) {
                        str = "关闭";
                    }
                    //        KkDataUtils.sentEventActive("屏幕保护开关切换", str);
                    return;
                }
                if (!paramAnonymousBoolean) {
                    str = "关闭";
                }
                //       KkDataUtils.sentEventError("屏幕保护开关切换", str);

         */
            }
        });
        ((CommonViewModel) this.viewModel).getImeBarValue().observe(getViewLifecycleOwner(), new Observer() {
            @Override
            public void onChanged(Object o) {
                ((FragmentCommonBinding) CommonFragment.this.binding).imeLayout.setItemData((ItemLRTextIconData) o);
            }

            //public void onChanged(ItemLRTextIconData paramAnonymousItemLRTextIconData) {
            //    ((FragmentCommonBinding) CommonFragment.this.binding).imeLayout.setItemData(paramAnonymousItemLRTextIconData);
            //}
        });
        ((CommonViewModel) this.viewModel).getSettingsValue().observe(getViewLifecycleOwner(), new Observer() {
            @Override
            public void onChanged(Object o) {
                CommonFragment.this.setSettingVisible((CommonSettingVisible) o);
            }

            //public void onChanged(CommonSettingVisible paramAnonymousCommonSettingVisible) {
            //     CommonFragment.this.setSettingVisible(paramAnonymousCommonSettingVisible);
            //}
        });
        if (((CommonViewModel) this.viewModel).getSettingVisible() != null) {
            setSettingVisible(((CommonViewModel) this.viewModel).getSettingVisible());
        }
        ((FragmentCommonBinding) this.binding).imeLayout.itemRL.requestFocus();
    }
}
