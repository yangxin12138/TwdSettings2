package com.twd.setting.module.projector.fragment;

import android.app.TwdManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.twd.setting.R;
import com.twd.setting.base.BaseBindingVmFragment;
import com.twd.setting.databinding.FragmentSinglePointBinding;
import com.twd.setting.module.projector.vm.KeystoneViewModel;
import com.twd.setting.utils.SystemPropertiesUtils;
import com.twd.setting.utils.TwdUtils;

import java.security.PublicKey;

public class SinglePointFragment extends BaseBindingVmFragment<FragmentSinglePointBinding, KeystoneViewModel> {
    private static final String TAG = "SinglePointFragment";
    private int nowPoint = 0;
    public final String ORIGIN_POINT = "0,0";
    public int MAX_STEP = 50;
    public int MIN_STEP = 0;
    private int projectorMode;
    TwdUtils twdUtils;
    // 保留本地坐标数组，用于缓存当前值
    private float[] leftUp_Offset = new float[2];
    private float[] rightUp_Offset = new float[2];
    private float[] rightDown_Offset = new float[2];
    private float[] leftDown_Offset = new float[2];
    private SharedPreferences sp;
    public static SinglePointFragment newInstance() {
        return new SinglePointFragment();
    }
    public int initLayout(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle) {
        return R.layout.fragment_single_point;
    }
    public void onCreate(Bundle paramBundle) {super.onCreate(paramBundle);}
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_single_point;
    }

    public void onViewCreated(View paramView, Bundle paramBundle) {
        super.onViewCreated(paramView, paramBundle);
        twdUtils = new TwdUtils();
        twdUtils.hideSystemUI(getActivity());
        leftUp_Offset = TwdManager.getInstance().getLeftTopOffset();
        rightUp_Offset = TwdManager.getInstance().getRightTopOffset();
        rightDown_Offset = TwdManager.getInstance().getRightBottomOffset();
        leftDown_Offset = TwdManager.getInstance().getLeftBottomOffset();
        Log.d(TAG, "onViewCreated: leftUp_Offset = " + leftUp_Offset+",rightUp_Offset = "+rightUp_Offset
        +",rightDown_Offset = "+rightDown_Offset+",leftDown_Offset = "+leftDown_Offset);
        initPointText();
        if(viewModel.isVertical()){
            nowPoint = 3;
        }else {
            nowPoint = 0;
        }

        projectorMode = TwdManager.getInstance().getScreenMirror();
        Log.d(TAG, "onViewCreated: projectorMode = " + projectorMode);
        if (projectorMode==1){
            nowPoint = 1;
        }else if (projectorMode==0){
            nowPoint = 0;
        } else if (projectorMode==2) {
            nowPoint = 2;
        } else if (projectorMode==3) {
            nowPoint = 3;
        }
        binding.ivSingleLeftUp.setFocusable(true);
        binding.ivSingleLeftUp.requestFocus();
        binding.ivSingleLeftUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: 切换到右上");
                binding.ivSingleRightUp.requestFocus();
                binding.ivSingleRightUp.setVisibility(View.VISIBLE);
                binding.ivSingleLeftUp.setVisibility(View.INVISIBLE);
                if (projectorMode==0 ){
                    nowPoint = 1;
                } else if (projectorMode==1) {
                    nowPoint = 0;
                } else if (projectorMode==2) {
                    nowPoint = 3;
                } else if (projectorMode==3) {
                    nowPoint = 2;
                }
            }
        });
        binding.ivSingleLeftUp.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                Log.i(TAG, "onKey: ======执行 ivSingleLeftUp 的按键监听");
                dealKey( view,i,keyEvent);
                return false;
            }
        });
        binding.ivSingleRightUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("function:onKey()","切换到右下");
                binding.ivSingleRightDown.requestFocus();
                binding.ivSingleRightDown.setVisibility(View.VISIBLE);
                binding.ivSingleRightUp.setVisibility(View.INVISIBLE);
                if (projectorMode==0){
                    nowPoint = 2;
                } else if (projectorMode==1) {
                    nowPoint = 3;
                } else if (projectorMode==2) {
                    nowPoint = 0;
                } else if (projectorMode==3) {
                    nowPoint = 1;
                }
            }
        });
        binding.ivSingleRightUp.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                dealKey(view,i,keyEvent);
                return false;
            }
        });
        binding.ivSingleRightDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("function:onKey()","切换到左下");
                binding.ivSingleLeftDown.requestFocus();
                binding.ivSingleLeftDown.setVisibility(View.VISIBLE);
                binding.ivSingleRightDown.setVisibility(View.INVISIBLE);
                if (projectorMode==0){
                    nowPoint = 3;
                } else if (projectorMode==1) {
                    nowPoint = 2;
                } else if (projectorMode==2) {
                    nowPoint = 1;
                } else if (projectorMode==3) {
                    nowPoint = 0;
                }
            }
        });
        binding.ivSingleRightDown.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                dealKey(view,i,keyEvent);
                return false;
            }
        });
        binding.ivSingleLeftDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("function:onKey()","切换到左上");
                binding.ivSingleLeftUp.requestFocus();
                binding.ivSingleLeftUp.setVisibility(View.VISIBLE);
                binding.ivSingleLeftDown.setVisibility(View.INVISIBLE);
                if (projectorMode==0){
                    nowPoint = 0;
                } else if (projectorMode==1) {
                    nowPoint = 1;
                } else if (projectorMode==2) {
                    nowPoint = 2;
                } else if (projectorMode==3) {
                    nowPoint = 3;
                }
            }
        });
        binding.ivSingleLeftDown.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                dealKey(view,i,keyEvent);
                return false;
            }
        });
        binding.ivSingleLeftUp.setVisibility(View.VISIBLE);
        binding.ivSingleRightUp.setVisibility(View.INVISIBLE);
        binding.ivSingleRightDown.setVisibility(View.INVISIBLE);
        binding.ivSingleLeftDown.setVisibility(View.INVISIBLE);
    }

    private void initPointText(){
        binding.tvSingleLeftUp.setText(leftUp_Offset[0]+","+leftUp_Offset[1]);
        Log.i(TAG, "initPointText: leftUp = "+ leftUp_Offset[0]+","+leftUp_Offset[1]);
        binding.tvSingleLeftDown.setText(leftDown_Offset[0]+","+leftDown_Offset[1]);
        binding.tvSingleRightUp.setText(rightUp_Offset[0]+","+rightUp_Offset[1]);
        binding.tvSingleRightDown.setText(rightDown_Offset[0]+","+rightDown_Offset[1]);
    }
    private void resetView(){
        binding.tvSingleLeftUp.setText(ORIGIN_POINT);
        binding.tvSingleLeftDown.setText(ORIGIN_POINT);
        binding.tvSingleRightUp.setText(ORIGIN_POINT);
        binding.tvSingleRightDown.setText(ORIGIN_POINT);
    }
    private void updateOffset(){
        leftUp_Offset = TwdManager.getInstance().getLeftTopOffset();
        rightUp_Offset = TwdManager.getInstance().getRightTopOffset();
        rightDown_Offset = TwdManager.getInstance().getRightBottomOffset();
        leftDown_Offset = TwdManager.getInstance().getLeftBottomOffset();
    }
    public boolean dealKey(View view, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN){
            Log.i(TAG, "dealKey: ======进入");
            switch (keyCode){
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    Log.i(TAG, "dealKey: ======往左");
                    doOffset(view,0);
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    Log.i(TAG, "dealKey: ======往右");
                    doOffset(view,1);
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    Log.i(TAG, "dealKey: ======往上");
                    doOffset(view,2);
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    Log.i(TAG, "dealKey: ======往下");
                    doOffset(view,3);
                    break;
                case KeyEvent.KEYCODE_MENU:
                    Log.i(TAG, "dealKey: ======重置");
                    TwdManager.getInstance().resetTrapezoid();
                    resetView();
                    break;
            }
            updateOffset();
        }
        return false;
    }

    private void doOffset(View view,int target){
        Log.i(TAG, "doOffset: view id = "+view.getId());
        float currentX = 0f;
        float currentY = 0f;
        float newX = 0f;
        float newY = 0f;
        if (view == binding.ivSingleLeftUp){
            currentX = leftUp_Offset[0];
            currentY = leftUp_Offset[1];
            switch (target){
                case 0:// 左（X轴减1，需 >= MIN_STEP）
                    newX = currentX - 1;
                    // 确保 newX 不小于最小值 0
                    newX = Math.max(MIN_STEP, newX);
                    newY = currentY;
                    break;
                case 1:// 右（X轴加1，需 <= MAX_STEP）
                    newX = currentX + 1;
                    // 确保 newX 不大于最大值 50
                    newX = Math.min(MAX_STEP, newX);
                    newY = currentY;
                    break;
                case 2:// 上（Y轴减1，需 >= MIN_STEP）
                    newY = currentY - 1;
                    // 确保 newY 不小于最小值 0
                    newY = Math.max(MIN_STEP, newY);
                    newX = currentX;
                    break;
                case 3:// 下（Y轴加1，需 <= MAX_STEP）
                    newY = currentY + 1;
                    // 确保 newY 不大于最大值 50
                    newY = Math.min(MAX_STEP, newY);
                    newX = currentX;
                    break;
            }
            if (newX != currentX || newY != currentY) {
                // 更新 TextView 显示
                binding.tvSingleLeftUp.setText(newX + "," + newY);
                Log.d(TAG, "doOffset: tvSingleLeftUp = " + target + " = " + newX + "," + newY);
                // 调用 TwdManager 设置新偏移量
                TwdManager.getInstance().setLeftTopOffset(newX, newY);
                // 更新本地缓存的偏移量数组（同步最新值）
                updateOffset();
            } else {
                // 超出范围时打印日志（可选，方便调试）
                Log.d(TAG, "doOffset: 偏移量已达边界（" + MIN_STEP + "-" + MAX_STEP + "），无需修改");
            }
            Log.d(TAG, "doOffset: 修改完之后值是 setLeftTopOffset = " + TwdManager.getInstance().getLeftTopOffset());
        } else if (view == binding.ivSingleRightUp) {
            currentX = rightUp_Offset[0];
            currentY = rightUp_Offset[1];
            switch (target){
                case 0:
                    newX = currentX + 1;
                    newX = Math.min(MAX_STEP, newX);
                    newY = currentY;
                    break;
                case 1:
                    newX = currentX - 1;
                    newX = Math.max(MIN_STEP, newX);
                    newY = currentY;
                    break;
                case 2:
                    newY = currentY - 1;
                    newY = Math.max(MIN_STEP, newY);
                    newX = currentX;
                    break;
                case 3:
                    newY = currentY + 1;
                    newY = Math.min(MAX_STEP, newY);
                    newX = currentX;
                    break;
            }
            if (newX != currentX || newY != currentY) {
                binding.tvSingleRightUp.setText(newX + "," + newY);
                Log.d(TAG, "doOffset: tvSingleLeftUp = " + target + " = " + newX + "," + newY);
                TwdManager.getInstance().setRightTopOffset(newX, newY);
                updateOffset();
            } else {
                Log.d(TAG, "doOffset: 偏移量已达边界（" + MIN_STEP + "-" + MAX_STEP + "），无需修改");
            }
        }else if (view == binding.ivSingleRightDown) {
            currentX = rightDown_Offset[0];
            currentY = rightDown_Offset[1];
            switch (target){
                case 0:
                    newX = currentX + 1;
                    newX = Math.min(MAX_STEP, newX);
                    newY = currentY;
                    break;
                case 1:
                    newX = currentX - 1;
                    newX = Math.max(MIN_STEP, newX);
                    newY = currentY;
                    break;
                case 2:
                    newY = currentY + 1;
                    newY = Math.min(MAX_STEP, newY);
                    newX = currentX;
                    break;
                case 3:
                    newY = currentY - 1;
                    newY = Math.max(MIN_STEP, newY);
                    newX = currentX;
                    break;
            }
            if (newX != currentX || newY != currentY) {
                binding.tvSingleRightDown.setText(newX + "," + newY);
                Log.d(TAG, "doOffset: tvSingleLeftUp = " + target + " = " + newX + "," + newY);
                TwdManager.getInstance().setRightBottomOffset(newX, newY);
                updateOffset();
            } else {
                Log.d(TAG, "doOffset: 偏移量已达边界（" + MIN_STEP + "-" + MAX_STEP + "），无需修改");
            }
        }else if (view == binding.ivSingleLeftDown) {
            currentX = leftDown_Offset[0];
            currentY = leftDown_Offset[1];
            switch (target){
                case 0:
                    newX = currentX - 1;
                    newX = Math.max(MIN_STEP, newX);
                    newY = currentY;
                    break;
                case 1:
                    newX = currentX + 1;
                    newX = Math.min(MAX_STEP, newX);
                    newY = currentY;
                    break;
                case 2:
                    newY = currentY + 1;
                    newY = Math.min(MAX_STEP, newY);
                    newX = currentX;
                    break;
                case 3:
                    newY = currentY - 1;
                    newY = Math.max(MIN_STEP, newY);
                    newX = currentX;
                    break;
            }
            if (newX != currentX || newY != currentY) {
                binding.tvSingleLeftDown.setText(newX + "," + newY);
                Log.d(TAG, "doOffset: tvSingleLeftUp = " + target + " = " + newX + "," + newY);
                TwdManager.getInstance().setLeftBottomOffset(newX, newY);
                updateOffset();
            } else {
                Log.d(TAG, "doOffset: 偏移量已达边界（" + MIN_STEP + "-" + MAX_STEP + "），无需修改");
            }
        }
    }
}
