package com.twd.setting.module.projector.keystone;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;

import com.twd.setting.utils.SystemPropertiesUtils;

public class Lcd {
    private static final String TAG = "lcd_info";
    protected static Context mContext;
    protected static float mStepX_onepoint = 0.0f;
    protected static float mStepY_onepoint = 0.0f;
    protected static float mStepX_twopoint = 0.0f;
    protected static float mStepY_twopoint = 0.0f;
    protected static int lcdWidth;
    protected static int lcdHeight;
    protected static int lcdValidWidth_top ;
    protected static int lcdValidWidth_bottom ;
    protected static int lcdValidHeight_left;
    protected static int lcdValidHeight_right;

    public final int MODE_ONEPOINT = 1;
    public final int MODE_TWOPOINT = 0;
//    protected static SharedPreferences prefs;
    protected static int mode =0;
    public  Lcd(Context context){
        DisplayMetrics dm = new DisplayMetrics();
        dm =  context.getResources().getDisplayMetrics();
        lcdWidth = dm.widthPixels;
        lcdHeight = dm.heightPixels;


        mStepX_twopoint = getFloatProperty("STEPX_TWOPOINT", 5.12f);
        mStepY_twopoint = getFloatProperty("STEPY_TWOPOINT", 3.0f);
        mStepX_onepoint = getFloatProperty("STEPX_ONEPOINT", 2.56f);
        mStepY_onepoint = getFloatProperty("STEPY_ONEPOINT", 1.5f);

        Log.d(TAG, "SizeActivity mode: "+mode+", lcdWidth:"+lcdWidth+",lcdHeight:"+lcdHeight);

    }

    private float getFloatProperty(String key, float defaultValue) {
        try {
            String value = SystemPropertiesUtils.readSystemProp(key).trim();
            return Float.parseFloat(value);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }
    public float getStepX(){
        if(mode ==MODE_TWOPOINT){
            return mStepX_twopoint;
        }else if(mode == MODE_ONEPOINT){
            return mStepX_onepoint;
        }
        return mStepX_onepoint;
    }
    public float getStepY(){
        if(mode == MODE_TWOPOINT){
            return mStepY_twopoint;
        }else if(mode == MODE_ONEPOINT){
            return mStepY_onepoint;
        }
        return mStepY_onepoint;
    }
    public int getLcdWidth(){
        return lcdWidth;
    }
    public int getLcdHeight(){
        return lcdHeight;
    }
}