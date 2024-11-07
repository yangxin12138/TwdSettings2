package com.twd.setting.module.projector.fragment;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.Observer;

import com.twd.setting.R;
import com.twd.setting.base.BaseBindingVmFragment;
import com.twd.setting.databinding.FragmentProjectionBinding;
import com.twd.setting.module.projector.vm.ProjectionViewModel;
import com.twd.setting.utils.SystemPropertiesUtils;
import com.twd.setting.utils.UiUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class ProjectionFragment extends BaseBindingVmFragment<FragmentProjectionBinding, ProjectionViewModel> {

    private static final String TAG = "ProjectorFragment";
    private static final String PATH_CONTROL_MIPI = "persist.sys.projection";
 /*   private static final String PATH_DEV_PRO_INFO = "/dev/pro_info";
    private static final String PATH_DEV_PRO_INFO2 = "/dev/block/mmcblk0p1";*/
    private static final String PATH_CONTROL_X = "persist.sys.keystone.mirror_x";
    private static final String PATH_CONTROL_Y = "persist.sys.keystone.mirror_y";
    public static final String  PATH_CONTROL_UPDATE = "persist.sys.keystone.update";
    private static final int VALUE_POSITIVE_DRESS = 0;
    private static final int VALUE_DRESSING_REAR = 2;
    private static final int VALUE_HOISTING_FRONT = 3;
    private static final int VALUE_HOISTING_REAR = 1;

    private void clickItem(int item) {
        Log.d(TAG,"clickItem: "+item);
        if(item == R.id.posPosInclude){//1
            gotoPosPos();
        }else if(item == R.id.posNegInclude){//2
            gotoPosNeg();
        }else if(item == R.id.negPosInclude){//3
            gotoNegPos();
        }else if(item == R.id.negNegInclude){//4
            gotoNegNeg();
        }else{

        }
    }

    private void gotoPosPos() {
        setProjectionMode(0);
        setIconChange(0);
    }

    private void gotoPosNeg() {
        setProjectionMode(1);
        setIconChange(1);
    }
    private void gotoNegPos() {
        setProjectionMode(2);
        setIconChange(2);
    }

    private void gotoNegNeg() {
        setProjectionMode(3);
        setIconChange(3);
    }

    public static void setProjectionMode(int mode) {
        //writeFile(PATH_CONTROL_MIPI, String.valueOf(mode));

        /*if(Build.HARDWARE.equals("mt6735")){
            writeFile(PATH_DEV_PRO_INFO2, String.valueOf(mode));
        }else{
            writeFile(PATH_DEV_PRO_INFO, String.valueOf(mode));
        }*/

        SystemPropertiesUtils.setProperty(PATH_CONTROL_MIPI,String.valueOf(mode));
        switch(mode){
            case 0:
                SystemPropertiesUtils.setProperty(PATH_CONTROL_X,"0");
                SystemPropertiesUtils.setProperty(PATH_CONTROL_Y,"0");
                break;
            case 1:
                SystemPropertiesUtils.setProperty(PATH_CONTROL_X,"1");
                SystemPropertiesUtils.setProperty(PATH_CONTROL_Y,"0");
                break;
            case 2:
                SystemPropertiesUtils.setProperty(PATH_CONTROL_X,"1");
                SystemPropertiesUtils.setProperty(PATH_CONTROL_Y,"1");
                break;
            case 3:
                SystemPropertiesUtils.setProperty(PATH_CONTROL_X,"0");
                SystemPropertiesUtils.setProperty(PATH_CONTROL_Y,"1");
                break;

            default:
                SystemPropertiesUtils.setProperty(PATH_CONTROL_X,"0");
                SystemPropertiesUtils.setProperty(PATH_CONTROL_Y,"0");
                break;

        }

        SystemPropertiesUtils.setProperty(PATH_CONTROL_UPDATE,"1");

    }

    public void setIconChange(int postion){
        Log.d(TAG,"setIconChange :"+postion);
        binding.posPosInclude.contentTVLeft.setImageResource(0);
        binding.posNegInclude.contentTVLeft.setImageResource(0);
        binding.negPosInclude.contentTVLeft.setImageResource(0);
        binding.negNegInclude.contentTVLeft.setImageResource(0);
        if(postion == 0){
            binding.posPosInclude.contentTVLeft.setImageResource(R.drawable.icon_projection_selected_black);
        } else if (postion == 1) {
            binding.posNegInclude.contentTVLeft.setImageResource(R.drawable.icon_projection_selected_black);
        } else if (postion == 2) {
            binding.negPosInclude.contentTVLeft.setImageResource(R.drawable.icon_projection_selected_black);
        } else if (postion== 3) {
            binding.negNegInclude.contentTVLeft.setImageResource(R.drawable.icon_projection_selected_black);
        }
        //binding.posPosInclude.getItemData().setRightIconRes(R.drawable.ic_baseline_arrow_forward_ios_24);icon_projection_selected_black
    }

    private static boolean writeFile(String path, String content) {
        boolean flag = true;
        FileOutputStream out = null;
        PrintStream p = null;
        File file = new File(path);
        if (file.exists()) {
            try {
                out = new FileOutputStream(path);
                p = new PrintStream(out);
                p.print(content);
                Log.i(TAG, "Write " + path + ": " + content);
            } catch (Exception e) {
                flag = false;
                Log.e(TAG, "Write " + path + ": error", e);
                e.printStackTrace();
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                if (p != null) {
                    try {
                        p.close();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
            }
        } else {
            Log.w(TAG, path + " is not exist");
        }
        return flag;
    }

    public static ProjectionFragment newInstance() {
        return new ProjectionFragment();
    }
    private void setClickListener() {
        UiUtils.setOnClickListener(((FragmentProjectionBinding) this.binding).posPosInclude.itemRL, ((ProjectionViewModel) this.viewModel).getItemClickListener());
        UiUtils.setOnClickListener(((FragmentProjectionBinding) this.binding).posNegInclude.itemRL, ((ProjectionViewModel) this.viewModel).getItemClickListener());
        UiUtils.setOnClickListener(((FragmentProjectionBinding) this.binding).negPosInclude.itemRL, ((ProjectionViewModel) this.viewModel).getItemClickListener());
        UiUtils.setOnClickListener(((FragmentProjectionBinding) this.binding).negNegInclude.itemRL, ((ProjectionViewModel) this.viewModel).getItemClickListener());
    }

    public int initLayout(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle) {
        return R.layout.fragment_projection;
    }

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_projection;
    }

    public void onViewCreated(View paramView, Bundle paramBundle) {
        super.onViewCreated(paramView, paramBundle);
        ((FragmentProjectionBinding) this.binding).setViewModel((ProjectionViewModel) this.viewModel);
        initTitle(paramView, R.string.projector_projection_title);

        ((FragmentProjectionBinding) this.binding).posPosInclude.itemRL.requestFocus();

        ((ProjectionViewModel) this.viewModel).getClickItem().observe(getViewLifecycleOwner(), new Observer() {
            @Override
            public void onChanged(Object o) {
                clickItem(((Integer)o).intValue());
            }
        });
        setClickListener();

    }
}
