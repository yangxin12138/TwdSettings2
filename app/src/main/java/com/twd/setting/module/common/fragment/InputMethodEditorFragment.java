package com.twd.setting.module.common.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.twd.setting.R;
import com.twd.setting.base.BaseBindingVmFragment;
import com.twd.setting.databinding.FragmentInputMethodEditorsBinding;
import com.twd.setting.module.common.adapter.InputMethodAdapter;
import com.twd.setting.module.common.model.InputMethodData;
import com.twd.setting.module.common.vm.CommonViewModel;
import com.twd.setting.utils.binding.ItemClickHandle;

public class InputMethodEditorFragment
        extends BaseBindingVmFragment<FragmentInputMethodEditorsBinding, CommonViewModel> {
    ItemClickHandle<InputMethodData> clickHandle = new ItemClickHandle() {
        @Override
        public void onClick(View paramView, Object paramT) {
            Bundle bundle = new Bundle();
            bundle.putString("selectedImeName", ((InputMethodData) paramT).getNameStr());
            InputMethodEditorFragment.this.getParentFragmentManager().setFragmentResult("selectedIme", bundle);
            if (InputMethodEditorFragment.this.mActivity != null) {
                ((CommonViewModel) InputMethodEditorFragment.this.viewModel).changeInputMethod(InputMethodEditorFragment.this.mActivity, ((InputMethodData) paramT).getIdStr());
                InputMethodEditorFragment.this.mActivity.onBackPressed();
            }
        }
    };

    public static InputMethodEditorFragment newInstance() {
        return new InputMethodEditorFragment();
    }

    public int initLayout(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle) {
        return R.layout.fragment_input_method_editors;
    }

    public CommonViewModel initViewModel() {
        return (CommonViewModel) new ViewModelProvider(this.mActivity).get(CommonViewModel.class);
    }

    public void onViewCreated(View paramView, Bundle paramBundle) {
        super.onViewCreated(paramView, paramBundle);
        initTitle(paramView, R.string.str_ime);
        InputMethodAdapter InputMethodAdapter = new InputMethodAdapter(this.clickHandle, ((CommonViewModel) this.viewModel).getImeList());
        ((FragmentInputMethodEditorsBinding) this.binding).imeListRcv.setAdapter(InputMethodAdapter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_input_method_editors;
    }
}
