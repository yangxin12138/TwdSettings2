package com.twd.setting.base;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class BaseBindingVmActivity<D extends ViewDataBinding, VM extends BaseViewModel>
        extends BaseActivity {
    protected D binding;
    protected ActivityResultLauncher<Intent> launcher;
    protected VM viewModel;

    private void createViewModel() {
        if (viewModel == null) {
            Class modelClass;
            Type type = getClass().getGenericSuperclass();
            if ((type instanceof ParameterizedType)) {
                modelClass = (Class) ((ParameterizedType) type).getActualTypeArguments()[1];
            } else {
                modelClass = BaseViewModel.class;
            }
            viewModel = ((VM) createViewModel(this, (Class) modelClass));
        }
    }

    private void initViewDataBinding(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, initLayout(savedInstanceState));
        binding.setLifecycleOwner(this);
        getLifecycle().addObserver(viewModel);
    }

    public <T extends AndroidViewModel> T createViewModel(FragmentActivity activity, Class<T> paramClass) {
        return (T) new ViewModelProvider(activity, ViewModelProvider.AndroidViewModelFactory.getInstance(activity.getApplication())).get(paramClass);
    }

    public abstract int initLayout(Bundle paramBundle);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback()
        {
            @Override
            public void onActivityResult(Object result) {
                onLaunchActivityResult((ActivityResult)result);
            }
        });
        createViewModel();
        initViewDataBinding(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (binding != null) {
            binding.unbind();
        }
        getLifecycle().removeObserver(viewModel);
        launcher.unregister();
    }

    public void onLaunchActivityResult(ActivityResult paramActivityResult) {}
    //public void onActivityResult(Object result) {}
}
