package com.twd.setting.module.bluetooth.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.twd.setting.R;
import com.twd.setting.base.BaseBindingVmFragment;
import com.twd.setting.commonlibrary.Utils.RxTransUtils;
import com.twd.setting.commonlibrary.Utils.StringUtils;
import com.twd.setting.commonlibrary.Utils.ToastUtils;
import com.twd.setting.databinding.FragmentBluetoothHandsetPairBinding;
import com.twd.setting.module.bluetooth.vm.BluetoothHandsetPairViewModel;
//import com.twd.setting.utils.HLog;
//import com.twd.setting.utils.KkUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.observers.DisposableObserver;

public class BluetoothHandsetPairFragment
        extends BaseBindingVmFragment<FragmentBluetoothHandsetPairBinding, BluetoothHandsetPairViewModel> {
    private final String TAG = "BluetoothHandsetPairFragment";
    private final int TIP_ERROR_VALUE = 4;
    private int currentStatus = 1;
    private int errorCount = 0;
    private String guideTipString;
    private DisposableObserver<String> tipObserver;
    private Animation transLeftAnim;
    private Animation transRightAnim;

    private DisposableObserver<String> getTipObserver() {
        return new DisposableObserver() {
            @Override
            public void onNext(Object value) {
       /* String str = BluetoothHandsetPairFragment.this.TAG;
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("读取到遥控操作提示文案了：");
        localStringBuilder.append(paramAnonymousString);
        Log.d(TAG, localStringBuilder.toString());
        if (!StringUtils.isTrimEmpty(paramAnonymousString))
        {
          BluetoothHandsetPairFragment.access$502(BluetoothHandsetPairFragment.this, paramAnonymousString);
          if ((currentStatus == 1) && (binding != null) && (binding.pairTipTv != null)) {
            binding.pairTipTv.setText(guideTipString);
          }
        }*/
                if ((currentStatus == 1) && (binding != null) && (binding.pairTipTv != null)) {
                    binding.pairTipTv.setText(guideTipString);
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }

        };
    }

    private void showGuidePage() {
        stopAnim();
        binding.pairResultIv.setVisibility(View.GONE);
        binding.pairResultTitleTv.setVisibility(View.GONE);
        binding.pairResultDescTv.setVisibility(View.GONE);
        binding.pairResultBtnLL.setVisibility(View.GONE);
        binding.pairTipLeftIv.setVisibility(View.GONE);
        binding.pairTipRightIv.setVisibility(View.GONE);
        binding.pairTipTv.setVisibility(View.VISIBLE);
        binding.pairTipHandsetIv.setVisibility(View.VISIBLE);
        binding.pairTipTv.setText(this.guideTipString);
    }

    private void showPage(int state) {
        if(state == 1){
            showGuidePage();
        } else if (state ==2) {
            showPairingPage();
        } else if (state ==3) {
            showPairSuccessPage();
        } else if (state ==4) {
            showPairFailPage();
        }
    }

    private void showPairFailPage() {
        stopAnim();
        binding.pairTipLeftIv.setVisibility(View.GONE);
        binding.pairTipRightIv.setVisibility(View.GONE);
        binding.pairTipTv.setVisibility(View.GONE);
        binding.pairTipHandsetIv.setVisibility(View.GONE);
        binding.pairResultIv.setVisibility(View.VISIBLE);
        binding.pairResultTitleTv.setVisibility(View.VISIBLE);
        binding.pairResultDescTv.setVisibility(View.VISIBLE);
        binding.pairResultBtnLL.setVisibility(View.VISIBLE);
        binding.btnCancel.setText(R.string.str_cancel);
        binding.btnConfirm.setVisibility(View.VISIBLE);
        binding.btnConfirm.setText(R.string.str_retry);
        binding.btnConfirm.requestFocus();
        binding.pairResultTitleTv.setText(R.string.str_bluetooth_pair_fail);
        binding.pairResultDescTv.setText(R.string.str_bluetooth_handset_pair_retry_desc);
        int i = errorCount;
        if (i <= 4) {
            errorCount = (i + 1);
        }
        i = errorCount;
        if (i == 4) {
            errorCount = (i + 1);
            ToastUtils.showShort("请重启蓝牙或移除已配对设备后再试");
        }
    }

    private void showPairSuccessPage() {
        stopAnim();
        binding.pairTipLeftIv.setVisibility(View.GONE);
        binding.pairTipRightIv.setVisibility(View.GONE);
        binding.pairTipTv.setVisibility(View.GONE);
        binding.pairTipHandsetIv.setVisibility(View.GONE);
        binding.pairResultIv.setVisibility(View.VISIBLE);
        binding.pairResultTitleTv.setVisibility(View.VISIBLE);
        binding.pairResultDescTv.setVisibility(View.VISIBLE);
        binding.pairResultBtnLL.setVisibility(View.VISIBLE);
        binding.btnConfirm.setVisibility(View.GONE);
        binding.btnCancel.setText(R.string.str_complete);
        binding.btnCancel.requestFocus();
        binding.pairResultTitleTv.setText(R.string.str_bluetooth_pair_success);
        binding.pairResultDescTv.setText(R.string.str_bluetooth_handset_pair_desc);
        errorCount = 0;
    }

    private void showPairingPage() {
        stopAnim();
        binding.pairResultIv.setVisibility(View.GONE);
        binding.pairResultTitleTv.setVisibility(View.GONE);
        binding.pairResultDescTv.setVisibility(View.GONE);
        binding.pairResultBtnLL.setVisibility(View.GONE);
        binding.pairTipLeftIv.setVisibility(View.VISIBLE);
        binding.pairTipRightIv.setVisibility(View.VISIBLE);
        binding.pairTipTv.setVisibility(View.VISIBLE);
        binding.pairTipHandsetIv.setVisibility(View.VISIBLE);
        binding.pairTipTv.setText(R.string.str_bluetooth_handset_pairing);
        if (transLeftAnim == null) {
            transLeftAnim = AnimationUtils.loadAnimation(getContext(), R.anim.anim_trans_left);
        }
        if (transRightAnim == null) {
            transRightAnim = AnimationUtils.loadAnimation(getContext(), R.anim.anim_trans_right);
        }
        binding.pairTipLeftIv.startAnimation(transLeftAnim);
        binding.pairTipRightIv.startAnimation(transRightAnim);
    }

    private void stopAnim() {
        if ((binding.pairTipLeftIv != null) && (binding.pairTipLeftIv.getAnimation() != null)) {
            binding.pairTipLeftIv.getAnimation().cancel();
            binding.pairTipLeftIv.clearAnimation();
        }
        if ((binding.pairTipRightIv != null) && (binding.pairTipRightIv.getAnimation() != null)) {
            binding.pairTipRightIv.getAnimation().cancel();
            binding.pairTipRightIv.clearAnimation();
        }
    }

    public int initLayout(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle) {
        return R.layout.fragment_bluetooth_handset_pair;
    }

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        if (TextUtils.isEmpty(guideTipString)) {
            guideTipString = getString(R.string.str_bluetooth_handset_pair_guide);
        }
        tipObserver = getTipObserver();
        Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter e) throws Exception {
                //String str = KkUtils.getBluetoothRemoteTip();
                //if(str == null)str="";
                String str = "";
                e.onNext(str);
            }
        }).compose(RxTransUtils.schedulersIoToUi()).subscribe(tipObserver);
    }

    public void onDestroy() {
        super.onDestroy();
        if ((tipObserver != null) && (!tipObserver.isDisposed())) {
            tipObserver.dispose();
            tipObserver = null;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_bluetooth_handset_pair;
    }

    public void onStop() {
        super.onStop();
        stopAnim();
    }

    public void onViewCreated(View paramView, Bundle paramBundle) {
        super.onViewCreated(paramView, paramBundle);
        initTitle(paramView, R.string.str_bluetooth_remote_control);
        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View paramAnonymousView) {
                if (mActivity != null) {
                    mActivity.onBackPressed();
                }
            }
        });
        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View paramAnonymousView) {
                showPage(1);
                viewModel.startAddDevice();
            }
        });
        viewModel.getUpdateAddDialogStatus().observe(getViewLifecycleOwner(), new Observer() {
            @Override
            public void onChanged(Object o) {
                Log.d("BluetoothHandsetPairFrg","onChanged "+((Integer) o).intValue());
                showPage(((Integer) o).intValue());
            }
        });
        showPage(1);
        viewModel.startAddDevice();
    }

    public void setGuideTipString(String str) {
        guideTipString = str;
        if (currentStatus == 1) {
            binding.pairTipTv.setText(str);
        }
    }
}
