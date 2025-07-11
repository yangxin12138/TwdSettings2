package com.twd.setting;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.twd.setting.module.projector.ProjectorActivity;
import com.twd.setting.utils.SystemPropertiesUtils;

/**
 * @Author:Yangxin
 * @Description:
 * @time: Create in 下午4:56 10/7/2025
 */
public class LauncherActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean useProjectorActivity = SystemPropertiesUtils.whichLauncherActivity();
        Log.i("yangxin", "onCreate: useProjectorActivity = " + useProjectorActivity);
        Class<?> targetActivity = useProjectorActivity ? ProjectorActivity.class : MainActivity.class;

        Intent intent = new Intent(this,targetActivity);
        startActivity(intent);

        finish();
    }
}
