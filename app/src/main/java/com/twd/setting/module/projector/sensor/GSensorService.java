package com.twd.setting.module.projector.sensor;


import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Parcel;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;

import androidx.core.app.NotificationCompat;

import com.twd.setting.R;
import com.twd.setting.module.projector.vm.KeystoneViewModel;
import com.twd.setting.utils.SystemPropertiesUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Arrays;

public class GSensorService extends Service {

    public static final String TAG = GSensorService.class.getSimpleName();

    private SensorManager sensorManager;
    private Sensor gravitySensor;
    private Sensor gyroSensor;

    //zzzz
    private static final float NS2S = 1.0f / 1000000000.0f;
    private static final float DT = 0.032258f;//0.04f;//0.0625f;//0.04f;
    private long timestamp;
    private float angleDT[] = new float[3];
    private float angle[] = new float[3];
    private float gyroQuiet[] = new float[3];
    private float quietAngle[] = new float[3];
    private static int angleCount = 0;
    private static int gyroQuietCount = 0;
    private static final int  GYRO_QUIET_COUNT= 5;
    private static float gyro_anglex = 0;
    private static float gyro_angley = 0;
    private static float gyro_anglez = 0;
    private static float device_init_anglex = 0;
    private static float device_init_angley = 0;
    private static float device_init_anglez = 0;
    private static float device_anglex = 0;
    private static float device_angley = 0;
    private static float device_anglez = 0;
    //zzzz


    private int gsensor_count = 0;
    private int gsensor_index = 0;
    private int count_not_moving = 0;

    private int refress_view_cont=0;
    //private int gsensor_stable_value = 0;
    private double[] gsen_nums = new double[30];


    //zzzz
    //private double gsensor_stable_value = 0;
    private int gsensor_stable_value = 0;

    private boolean not_moving;
    private boolean moving;

    private int curDegree;
    private int lastDegree;
    private int last2Degree;
    private int ignore_degree = 0;
    public static final int IGNORE_DEGREE_COUNT = 1;

    private static boolean refress_view_flag;
    private static int autofocus_flag = 1;
    private static boolean poweron_flag = false;
    private static boolean  first_run_flag = true;
    private static int ignore_count = 2;
    public static final String PROP_LT = "persist.sys.keystone.lt";
    public static final String PROP_RT = "persist.sys.keystone.rt";
    public static final String PROP_RB = "persist.sys.keystone.rb";
    public static final String PROP_LB = "persist.sys.keystone.lb";

    public static final String PROP_LT_O = "ro.sys.keystone.lt";
    public static final String PROP_RT_O = "ro.sys.keystone.rt";
    public static final String PROP_RB_O = "ro.sys.keystone.rb";
    public static final String PROP_LB_O = "ro.sys.keystone.lb";
    public static final String PROP_KEYSTONE_UPDATE = "persist.sys.keystone.update";
    public static final String PROP_ZOOMLEVEL = "persist.sys.keystone.zoomlevel";
    public static final String PROP_ZOOMLEVEL2 = "persist.display.size";
    public static final String PROP_PROJECTION = "persist.sys.projection";
    public static final String PROP_AUTOFOCUS = "persist.sys.keystone.autofocus";
    public static final String PROP_DEGREETOROOF = "persist.sys.keystone.degreetoroof";
    public static final String PROP_POWERON = "persist.sys.keystone.poweron";
    public static final String PROP_KEYSTONE_MODEL = "persist.sys.keystone.model";

    public static final String PROP_LTX = "persist.display.keystone_ltx";
    public static final String PROP_LTY = "persist.display.keystone_lty";
    public static final String PROP_RTX = "persist.display.keystone_rtx";
    public static final String PROP_RTY = "persist.display.keystone_rty";
    public static final String PROP_LBX = "persist.display.keystone_lbx";
    public static final String PROP_LBY = "persist.display.keystone_lby";
    public static final String PROP_RBX = "persist.display.keystone_rbx";
    public static final String PROP_RBY = "persist.display.keystone_rby";

    private static final String PROPERTY_LEFT_BOTTOM_X = "persist.display.keystone_lbx";
    private static final String PROPERTY_LEFT_TOP_X = "persist.display.keystone_ltx";
    private static final String PROPERTY_RIGHT_BOTTOM_X = "persist.display.keystone_rbx";
    private static final String PROPERTY_RIGHT_TOP_X = "persist.display.keystone_rtx";
    private static final String PROPERTY_LEFT_BOTTOM_Y = "persist.display.keystone_lby";
    private static final String PROPERTY_LEFT_TOP_Y = "persist.display.keystone_lty";
    private static final String PROPERTY_RIGHT_BOTTOM_Y = "persist.display.keystone_rby";
    private static final String PROPERTY_RIGHT_TOP_Y = "persist.display.keystone_rty";

    private IBinder mSurfaceFlinger;
    private float mLeftBottomX = 0;
    private float mLeftBottomY = 0;
    private float mRightBottomX = 0;
    private float mRightBottomY = 0;
    private float mLeftTopX = 0;
    private float mLeftTopY = 0;
    private float mRightTopX = 0;
    private float mRightTopY = 0;

    private double x;
    private double y;
    private double z;
    private int mDegree=360;

    //private KeystoneViewModel vm;
    public static int degree_keystone = 0;
    private static final int  ARRAY_SIZE= 30;
    private int[] degree_arrays = new int[ARRAY_SIZE];
    public static int degree_high = 0;
    public static int degree_low = 0;
    //zzzz
    public static int degree_sum = 0;
    public static int degree_average = 0;
    public static int degree_high_last = 0;
    public static int degree_low_last = 0;
    public static int degree_sum_last = 0;
    public static int degree_average_last = 0;
    private static final int  ARRAY_SIZE_LAST= 10;

    private static final  int DEGREETOROOF = 43;
    //YK
    //private static  final int DEGREETOROOF = 50;

    private static int DEGREE_TO_ROOF = DEGREETOROOF;

		private static int lcdValidWidth_top = 1280;
		private static int lcdValidWidth_bottom = 1280;
		private static int lcdValidHeight_left = 720;
		private static int lcdValidHeight_right = 720;
		private static double zoomy_ratio=1.75;
        private static double vZoom = 0;
        private static double vZoom_old = 0;
        private static double vZoom2 = 0;
		
        protected static double mStepX = 2.7; 
        protected static double mStepY = 4.8; 

        protected static double mStepX_roof = 2.7; 
        protected static double mStepY_roof = 4.5;

    protected static float lt_x = 0;
    protected static float lt_y = 0;
    protected static float rt_x = 0;
    protected static float rt_y = 0;
    protected static float lb_x = 0;
    protected static float lb_y = 0;
    protected static float rb_x = 0;
    protected static float rb_y = 0;
    protected static int projection;

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "ForegroundServiceChannel";
    private WindowManager.LayoutParams wmParams;
    private WindowManager mWindowManager;
    private GSensorAnimationLayout mWindowView;
    private int widthPixels;
    private int heightPixels;
    private String keystone_model;


    @Override
    public void onCreate() {
        super.onCreate();
        initGravitySensor();
        createNotificationChannel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //return super.onStartCommand(intent, flags, startId);

        // 创建通知
        Notification notification = createNotification();

        // 服务
        startForeground(NOTIFICATION_ID, notification);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void startForegroundWithNotification() {
        // 创建通知
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("服务运行中")
                .setContentText("服务正在前台运行")
                .setSmallIcon(R.drawable.ic_notification) // 设置小图标
                .setPriority(NotificationCompat.PRIORITY_LOW) // 设置优先级
                .build();

        // 启动服务
        startForeground(NOTIFICATION_ID, notification);
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private Notification createNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Service")
                .setContentText("Service is running in foreground")
                .setSmallIcon(R.drawable.ic_notification)
                .build();
    }

    private void initGravitySensor(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            sensorManager =(SensorManager)getSystemService(Context.SENSOR_SERVICE);
        }
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        sensorManager.registerListener(sensorLis, gravitySensor, SensorManager.SENSOR_DELAY_UI);
        //sensorManager.registerListener(gyroSensorLis, gyroSensor, SensorManager.SENSOR_DELAY_UI);


        DEGREE_TO_ROOF = Integer.parseInt(SystemPropertiesUtils.getProperty(PROP_DEGREETOROOF,DEGREETOROOF+""));

        String value_lt = SystemPropertiesUtils.getProperty(PROP_LT_O,"0,0");
        String v_lt[] = value_lt.trim().split(",");
        if (v_lt != null) {
            lt_x = Float.parseFloat(v_lt[0]);
            lt_y = Float.parseFloat(v_lt[1]);
        }

        String value_rt =SystemPropertiesUtils.getProperty(PROP_RT_O,"0,0");
        String v_rt[] = value_rt.trim().split(",");
        if (v_rt != null) {
            rt_x = Float.parseFloat(v_rt[0]);
            rt_y = Float.parseFloat(v_rt[1]);
        }

        String value_lb = SystemPropertiesUtils.getProperty(PROP_LB_O,"0,0");
        String v_lb[] = value_lb.trim().split(",");
        if (v_lb != null) {
            lb_x = Float.parseFloat(v_lb[0]);
            lb_y = Float.parseFloat(v_lb[1]);
        }

        String value_rb = SystemPropertiesUtils.getProperty(PROP_RB_O,"0,0");
        String v_rb[] = value_rb.trim().split(",");
        if (v_rb != null) {
            rb_x = Float.parseFloat(v_rb[0]);
            rb_y = Float.parseFloat(v_rb[1]);
        }

        poweron_flag = Boolean.parseBoolean(SystemPropertiesUtils.getProperty(PROP_POWERON,"false"));
        projection = Integer.parseInt(SystemPropertiesUtils.getProperty(PROP_PROJECTION,"0"));
        keystone_model = SystemPropertiesUtils.getProperty(PROP_KEYSTONE_MODEL,"paotong");


        DisplayMetrics displayMetrics = new DisplayMetrics();
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.getDefaultDisplay().getMetrics(displayMetrics);
        widthPixels = displayMetrics.widthPixels;
        heightPixels = displayMetrics.heightPixels;

        if(heightPixels ==720){
            lcdValidWidth_top = 1280;
            lcdValidWidth_bottom = 1280;
            lcdValidHeight_left = 720;
            lcdValidHeight_right = 720;
            mStepX = 2.7;
            mStepY = 4.8;
            zoomy_ratio=1.75;
            mStepX_roof = 2.7;
            mStepY_roof = 4.5;

        }else if(heightPixels ==600){
            lcdValidWidth_top = 1024;
            lcdValidWidth_bottom = 1024;
            lcdValidHeight_left = 600;
            lcdValidHeight_right = 600;
            mStepX = 2.7;
            mStepY = 4.8;
            zoomy_ratio=1.75;
            mStepX_roof = 2.7;
            mStepY_roof = 4.5;

        }else{
            lcdValidWidth_top = 1280;
            lcdValidWidth_bottom = 1280;
            lcdValidHeight_left = 720;
            lcdValidHeight_right = 720;
            mStepX = 2.7;
            mStepY = 4.8;
            zoomy_ratio=1.75;
            mStepX_roof = 2.7;
            mStepY_roof = 4.5;
        }



        //zzzz
        //String value_rb = SystemPropertiesUtils.getProperty(PROP_RB_O,"0,0");



//
//        String value_lt = SystemPropertiesUtils.getProperty(PROP_LT_O,"0,0");
//        String v_lt[] = value_lt.trim().split(",");
//        if (v_lt != null) {
//            lt_x = (int)Float.parseFloat(v_lt[0]);
//            lt_y = (int)Float.parseFloat(v_lt[1]);
//        }
//
//        String value_rt =SystemPropertiesUtils.getProperty(PROP_RT_O,"0,0");
//        String v_rt[] = value_rt.trim().split(",");
//        if (v_rt != null) {
//            rt_x = (int)Float.parseFloat(v_rt[0]);
//            rt_y = (int)Float.parseFloat(v_rt[1]);
//        }
//
//        String value_lb = SystemPropertiesUtils.getProperty(PROP_LB_O,"0,0");
//        String v_lb[] = value_lb.trim().split(",");
//        if (v_lb != null) {
//            lb_x = (int)Float.parseFloat(v_lb[0]);
//            lb_y = (int)Float.parseFloat(v_lb[1]);
//        }
//
//        String value_rb = SystemPropertiesUtils.getProperty(PROP_RB_O,"0,0");
//        String v_rb[] = value_rb.trim().split(",");
//        if (v_rb != null) {
//            rb_x = (int)Float.parseFloat(v_rb[0]);
//            rb_y = (int)Float.parseFloat(v_rb[1]);
//        }



        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        wmParams = new WindowManager.LayoutParams();
        wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY ;
        wmParams.alpha = 0.01f;
        wmParams.format = PixelFormat.TRANSLUCENT;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.CENTER;
        wmParams.width = 1;
        wmParams.height = 1;
//      wmParams.x=2000;
//      wmParams.y=2000;
        Log.d(TAG, "---LoadMotorService ----init. ");
        mWindowView = new GSensorAnimationLayout(this);
        mWindowManager.addView(mWindowView, wmParams);
    }

    private void getKeystoneProperties() {
        mLeftBottomX = Float.parseFloat(SystemPropertiesUtils.getProperty(PROPERTY_LEFT_BOTTOM_X, "0.0"));
        mLeftTopX = Float.parseFloat(SystemPropertiesUtils.getProperty(PROPERTY_LEFT_TOP_X, "0.0"));
        mRightBottomX = Float.parseFloat(SystemPropertiesUtils.getProperty(PROPERTY_RIGHT_BOTTOM_X, "0.0"));
        mRightTopX = Float.parseFloat(SystemPropertiesUtils.getProperty(PROPERTY_RIGHT_TOP_X, "0.0"));

        mLeftBottomY = Float.parseFloat(SystemPropertiesUtils.getProperty(PROPERTY_LEFT_BOTTOM_Y, "0.0"));
        mLeftTopY = Float.parseFloat(SystemPropertiesUtils.getProperty(PROPERTY_LEFT_TOP_Y, "0.0"));
        mRightBottomY = Float.parseFloat(SystemPropertiesUtils.getProperty(PROPERTY_RIGHT_BOTTOM_Y, "0.0"));
        mRightTopY = Float.parseFloat(SystemPropertiesUtils.getProperty(PROPERTY_RIGHT_TOP_Y, "0.0"));
    }
    private void updateALL(){
        try {
            try {
                Class<?> serviceManagerClass = Class.forName("android.os.ServiceManager");
                Method getServiceMethod = serviceManagerClass.getMethod("getService", String.class);

                // 调用getService方法
                mSurfaceFlinger = (IBinder) getServiceMethod.invoke(null, "SurfaceFlinger");

                // 使用IBinder对象进行后续操作
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (mSurfaceFlinger != null) {
                getKeystoneProperties() ;
                Parcel data = Parcel.obtain();
                data.writeInterfaceToken("android.ui.ISurfaceComposer");

                data.writeFloat((float)((double)mLeftBottomX * 0.001));
                data.writeFloat((float)((double)mLeftBottomY * 0.001));
                data.writeFloat((float)((double)mLeftTopX * 0.001));
                data.writeFloat((float)((double)mLeftTopY * 0.001));
                data.writeFloat((float)((double)mRightTopX * 0.001));
                data.writeFloat((float)((double)mRightTopY * 0.001));
                data.writeFloat((float)((double)mRightBottomX * 0.001));
                data.writeFloat((float)((double)mRightBottomY * 0.001));
                mSurfaceFlinger.transact(1050, data, null, 0);
                data.recycle();

                mWindowView.showZ();

            } else {
                Log.i(TAG,"error get surfaceflinger service");
            }
        } catch (Exception ex) {
            Log.i(TAG,"error talk with surfaceflinger service");
        }
    }

    public void start_focus(){
        Intent intent_f5 = new Intent();
        ComponentName cn_f5 = new ComponentName("com.hysd.bkscreen", "com.hysd.bkscreen.CalibTestONF2");
        intent_f5.setComponent(cn_f5);
        intent_f5.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent_f5.setAction("android.intent.action.MAIN");
        startActivity(intent_f5);

    }
//
//    public void updateTopLeft(int value){
//        float zoomx = lcdValidWidth_top*vZoom/10/2/2;
//        float movex = value*mStepX*(20-vZoom)/20;
//        float x = lt_x + (zoomx + movex);
//        float zoomy = lcdValidHeight_left*vZoom/10/2/2;
//        float movey = value*mStepY*(20-vZoom)/20;
//        float y = lt_y - (zoomy + movey);
//
//        Log.e(TAG, "persist.sys.keystone.lt= "+x+","+y);
//        SystemPropertiesUtils.setProperty(PROP_LT,x+","+y);
//    }
//    public void updateTopRight(int value){
//        float zoomx = lcdValidWidth_top*vZoom/10/2/2;
//        float movex = value*mStepX*(20-vZoom)/20;
//        float x = rt_x - (zoomx + movex);
//        float zoomy = lcdValidHeight_right*vZoom/10/2/2;
//        float movey = value*mStepY*(20-vZoom)/20;
//        float y = rt_y - (zoomy + movey);
//
//        Log.e(TAG, "persist.sys.keystone.rt= "+x+","+y);
//        SystemPropertiesUtils.setProperty(PROP_RT,x+","+y);
//    }
//    public void updateBottomLeft(int value){
//        float zoomx = lcdValidWidth_bottom*vZoom/10/2/2;
//        float movex = value*mStepX*(20-vZoom)/20;
//        float x = lb_x + (zoomx + movex);
//        float zoomy = lcdValidHeight_left*vZoom/10/2/2;
//        float movey = value*mStepY*(20-vZoom)/20;
//        float y = lb_y + (zoomy + movey);
//        Log.e(TAG, "persist.sys.keystone.lb= "+x+","+y);
//        SystemPropertiesUtils.setProperty(PROP_LB,x+","+y);
//    }
//    public void updateBottomRight(int value){
//        float zoomx = lcdValidWidth_bottom*vZoom/10/2/2;
//        float movex = value*mStepX*(20-vZoom)/20;
//        float x = rb_x - (zoomx + movex);
//        float zoomy = lcdValidHeight_right*vZoom/10/2/2;
//        float movey = value*mStepY*(20-vZoom)/20;
//        float y = rb_y + (zoomy + movey);
//        Log.e(TAG, "persist.sys.keystone.rb= "+x+","+y);
//        SystemPropertiesUtils.setProperty(PROP_RB,x+","+y);
//    }

    double valueXtoFactorX(int value){
        if(keystone_model.contains("paotong")){
            return valueXtoFactorX_Paotong(value);
        }else if(keystone_model.contains("fangpao")){
            if(heightPixels ==720){
                return valueXtoFactorX_FangPao(value);
            }else if(heightPixels ==600){
                return valueXtoFactorX_FangPao_600P(value);
            }else{
                return valueXtoFactorX_FangPao(value);
            }
        }else if(keystone_model.contains("duanpao")) {
            return valueXtoFactorX_DuanPao(value);
        }else if(keystone_model.contains("cyt1")) {
            return valueXtoFactorX_Paotong_CYT1(value);
        }else{
            return valueXtoFactorX_Paotong(value);
        }
    }

    double valueYtoFactorY(int value){
        if(keystone_model.contains("paotong")){
            return valueYtoFactorY_Paotong(value);
        }else if(keystone_model.contains("fangpao")){
            if(heightPixels ==720){
                return valueYtoFactorY_FangPao(value);
            }else if(heightPixels ==600){
                return valueYtoFactorY_FangPao_600P(value);
            }else{
                return valueYtoFactorY_FangPao(value);
            }
        }else if(keystone_model.contains("duanpao")) {
            return valueYtoFactorY_DuanPao(value);
        }else if(keystone_model.contains("cyt1")) {
            return valueYtoFactorY_Paotong_CYT1(value);
        }else{
            return valueYtoFactorY_Paotong(value);
        }
    }

    double valueXtoFactorX_Roof(int value){
        if(keystone_model.contains("paotong")){
            return 1.0;
        }else if(keystone_model.contains("fangpao")){
            return 1.4;
        }else{
            return 1.0;
        }
    }

    double valueYtoFactorY_Roof(int value){
        if(keystone_model.contains("paotong")){
            return 1.0;
        }else if(keystone_model.contains("fangpao")){
            return 1.4;
        }else{
            return 1.0;
        }
    }


    double valueXtoFactorX_DuanPao(int value){
        double actvieFactorX=1;
        if(value >= 0) {
            if ((value >= 0) && (value < 6)) {
                actvieFactorX = 2;
            } else if ((value >= 6) && (value < 12)) {
                //actvieFactorX = 1.45;
                actvieFactorX = 2.04;
            } else if ((value >= 12) && (value < 18)) {
                actvieFactorX = 1.75;
            } else if ((value >= 18) && (value < 24)) {
                actvieFactorX = 1.68;
            } else if ((value >= 24) && (value < 30)) {
                actvieFactorX = 1.57;
            } else if ((value >= 30) && (value < 36)) {
                actvieFactorX = 1.44;
            } else if ((value >= 36) && (value < 42)) {
                actvieFactorX = 1.44;
            } else {
                actvieFactorX = 1.44;
            }
        }

        return actvieFactorX;
    }


    double valueXtoFactorX_FangPao(int value){

        double actvieFactorX=1;
        if(value >= 0) {
            if ((value >= 0) && (value < 6)) {
                actvieFactorX = 1;
            } else if ((value >= 6) && (value < 12)) {
                actvieFactorX = 1.3; //o
            } else if ((value >= 12) && (value < 18)) {
                //actvieFactorX = 1.05;
                //actvieFactorX = 1.5;
                actvieFactorX = 1.6; //o
            } else if ((value >= 18) && (value < 24)) {

                actvieFactorX = 1.50;
            } else if ((value >= 24) && (value < 30)) {
                actvieFactorX = 1.50; //o
            } else if ((value >= 30) && (value < 36)) {
                actvieFactorX = 1.445; //o
            } else if ((value >= 36) && (value < 42)) {
                actvieFactorX = 1.3;
            } else {
                actvieFactorX = 1.3;
            }
        }

        return actvieFactorX;
    }



    double valueXtoFactorX_FangPao_600P(int value){

        double actvieFactorX=1;
        if(value >= 0) {
            if ((value >= 0) && (value < 6)) {
                actvieFactorX = 1;
            } else if ((value >= 6) && (value < 12)) {
                //actvieFactorX = 1.2;
                actvieFactorX = 1.3;
            } else if ((value >= 12) && (value < 18)) {
                actvieFactorX = 1.35;
            } else if ((value >= 18) && (value < 24)) {
                actvieFactorX = 1.35;
            } else if ((value >= 24) && (value < 30)) {
                actvieFactorX = 1.35;
            } else if ((value >= 30) && (value < 36)) {
                actvieFactorX = 1.35;
            } else if ((value >= 36) && (value < 42)) {
                actvieFactorX = 1.3;
            } else {
                actvieFactorX = 1.3;
            }
        }

        return actvieFactorX;
    }


    double valueXtoFactorX_Paotong_CYT1(int value){

        double actvieFactorX=1;
        if(value >= 0) {
            if ((value >= 0) && (value < 6)) {
                actvieFactorX = 2.18;   //O
            } else if ((value >= 6) && (value < 12)) {
                actvieFactorX = 2.0;    //O
            } else if ((value >= 12) && (value < 18)) {
                actvieFactorX = 1.65;   //O
            } else if ((value >= 18) && (value < 24)) {
                actvieFactorX = 1.72;   //O
            } else if ((value >= 24) && (value < 30)) {
                actvieFactorX = 1.53;   //O
            } else if ((value >= 30) && (value < 36)) {
                actvieFactorX = 1.43;   //0
            } else if ((value >= 36) && (value < 42)) {
                actvieFactorX = 1.37;
            } else {
                actvieFactorX = 1.33;
            }
        }


        return actvieFactorX;
    }


    double valueXtoFactorX_Paotong(int value){

        double actvieFactorX=1;
        if(value >= 0) {
            if ((value >= 0) && (value < 6)) {
                actvieFactorX = 1;
            } else if ((value >= 6) && (value < 12)) {
                actvieFactorX = 1;
            } else if ((value >= 12) && (value < 18)) {
                //actvieFactorX = 1.05;
                actvieFactorX = 1.17;
            } else if ((value >= 18) && (value < 24)) {
                //actvieFactorX = 1.05;
                actvieFactorX = 1.17;
            } else if ((value >= 24) && (value < 30)) {
                actvieFactorX = 1.13;
            } else if ((value >= 30) && (value < 36)) {
                actvieFactorX = 1.03;
            } else if ((value >= 36) && (value < 42)) {
                actvieFactorX = 1;
            } else {
                actvieFactorX = 1;
            }
        }


        return actvieFactorX;
    }


    double valueYtoFactorY_DuanPao(int value){

        double actvieFactorY=0.84;
        if(value >= 0) {
            if ((value >= 0) && (value < 6)) {
                actvieFactorY = 0.84;
            } else if ((value >= 6) && (value < 12)) {
                actvieFactorY = 0.87;
            } else if ((value >= 12) && (value < 18)) {
                actvieFactorY = 1.4;
            } else if ((value >= 18) && (value < 24)) {
                actvieFactorY = 1.52;
            } else if ((value >= 24) && (value < 30)) {
                //actvieFactorY = 1.1;
                actvieFactorY = 1.50;
            } else if ((value >= 30) && (value < 36)) {
                actvieFactorY = 1.66;
            } else if ((value >= 36) && (value < 42)) {
                actvieFactorY = 1.5;
            } else {
                actvieFactorY = 1.5;
            }
        }

        return actvieFactorY;
    }


    double valueYtoFactorY_FangPao(int value){

        double actvieFactorY=0.84;
        if(value >= 0) {
            if ((value >= 0) && (value < 6)) {
                actvieFactorY = 0.84;
            } else if ((value >= 6) && (value < 12)) {
                actvieFactorY = 0.87;
            } else if ((value >= 12) && (value < 18)) {
                actvieFactorY = 0.91;
            } else if ((value >= 18) && (value < 24)) {
                //actvieFactorY = 0.90;
                //actvieFactorY = 0.97;
                actvieFactorY = 1.1;
            } else if ((value >= 24) && (value < 30)) {
                //actvieFactorY = 1.1;
                actvieFactorY = 1.3;
            } else if ((value >= 30) && (value < 36)) {
                actvieFactorY = 1.3;
            } else if ((value >= 36) && (value < 42)) {
                actvieFactorY = 1.5;
            } else {
                actvieFactorY = 1.5;
            }
        }

        return actvieFactorY;
    }



    double valueYtoFactorY_FangPao_600P(int value){
 
        double actvieFactorY=0.84;
        if(value >= 0) {
            if ((value >= 0) && (value < 6)) {
                //actvieFactorY = 0.84;
                actvieFactorY = 0.82;
            } else if ((value >= 6) && (value < 12)) {
                actvieFactorY = 0.87;
            } else if ((value >= 12) && (value < 18)) {
                actvieFactorY = 0.91;
            } else if ((value >= 18) && (value < 24)) {
                //actvieFactorY = 0.90;
                //actvieFactorY = 0.97;
                actvieFactorY = 1.1;
            } else if ((value >= 24) && (value < 30)) {
                //actvieFactorY = 1.1;
                actvieFactorY = 1.3;
            } else if ((value >= 30) && (value < 36)) {
                actvieFactorY = 1.3;
            } else if ((value >= 36) && (value < 42)) {
                actvieFactorY = 1.5;
            } else {
                actvieFactorY = 1.5;
            }
        }

        return actvieFactorY;
    }

    double valueYtoFactorY_Paotong_CYT1(int value){

        double actvieFactorY=0.84;
        if(value > 0) {
            if ((value > 0) && (value < 6)) {
                actvieFactorY = 0.84;
            } else if ((value >= 6) && (value < 12)) {
                actvieFactorY = 0.87;
            } else if ((value >= 12) && (value < 18)) {
                actvieFactorY = 1.28;
            } else if ((value >= 18) && (value < 24)) {
                actvieFactorY = 1.42;
            } else if ((value >= 24) && (value < 30)) {
                actvieFactorY = 1.49;
            } else if ((value >= 30) && (value < 36)) {
                actvieFactorY = 1.56;
            } else if ((value >= 36) && (value < 42)) {
                actvieFactorY = 1.62;
            } else {
                actvieFactorY = 1.62;
            }
        }

        return actvieFactorY;
    }


    double valueYtoFactorY_Paotong(int value){

        double actvieFactorY=0.84;
        if(value > 0) {
            if ((value > 0) && (value < 6)) {
                actvieFactorY = 0.84;
            } else if ((value >= 6) && (value < 12)) {
                actvieFactorY = 0.87;
            } else if ((value >= 12) && (value < 18)) {
                actvieFactorY = 0.91;
            } else if ((value >= 18) && (value < 24)) {
                actvieFactorY = 0.93;
            } else if ((value >= 24) && (value < 30)) {
                actvieFactorY = 1.1;
            } else if ((value >= 30) && (value < 36)) {
                actvieFactorY = 1.3;
            } else if ((value >= 36) && (value < 42)) {
                actvieFactorY = 1.4;
            } else {
                actvieFactorY = 1.45;
            }
        }

        return actvieFactorY;
    }

    public void updateTopLeft(int value){

        double zoomx = lcdValidWidth_top*vZoom/10/2/2;
        //double activeStepX=mStepX-((double) value)/50;

        double actvieFactorX=1;
        actvieFactorX = valueXtoFactorX(value);

        double activeStepX=mStepX*actvieFactorX;

        double movex = value*activeStepX*(20-vZoom)/20;
        //double movex = value*activeStepX*(10-vZoom)/10;
        //double movex = value*activeStepX*(10-vZoom/2)/10;
        //double movex = value*activeStepX*2*(1-vZoom/10/2);
        double x = lt_x + (zoomx + movex);
        //double x = lt_x + (zoomx + movex)-value;
        if (value>0){
            x -= value*vZoom/10;
        }
        double zoomy = lcdValidHeight_left*vZoom/10/2/2;
        double activeZoomy = (double) (zoomy * zoomy_ratio);
        //double activeStepY=mStepY+((double) value)/50;
        //double activeStepY=mStepY;

        double activeStepY=mStepY;
        double actvieFactorY=0.84;
        actvieFactorY = valueYtoFactorY(value);

        activeStepY=mStepY*actvieFactorY;

        //double activeStepY=mStepY*1.1; //1.75 缩的太多了；1.50 缩的多了一点；1.45在角度大的时候缩少了点。可能1.475好一点。
        //double activeStepY=mStepY; //1.75 缩的太多了；1.50 缩的多了一点；1.45在角度大的时候缩少了点。可能1.475好一点。
        double movey = value*activeStepY*(20-vZoom)/20;
        double y = lt_y + (activeZoomy + movey);

        SystemPropertiesUtils.setProperty(PROP_LT,x+","+y);
        SystemPropertiesUtils.setProperty(PROP_LTX,x+"");
        SystemPropertiesUtils.setProperty(PROP_LTY,y+"");
    }

    public void updateTopRight(int value){

        double zoomx = lcdValidWidth_top*vZoom/10/2/2;
        //double activeStepX=mStepX-((double) value)/50;
        //double activeStepX=mStepX;

        double actvieFactorX=1;

        actvieFactorX = valueXtoFactorX(value);

        double activeStepX=mStepX*actvieFactorX;


        //double movex = value*activeStepX*(10-vZoom/2)/10;
        //double movex = value*activeStepX*(10-vZoom)/10;
        double movex = value*activeStepX*(20-vZoom)/20;
        //double x = rt_x + (zoomx + movex);
        double x = lt_x + (zoomx + movex);
        if (value>0){
            x -= value*vZoom/10;
        }
        double zoomy = lcdValidHeight_right*vZoom/10/2/2;
        double activeZoomy = (double) (zoomy * zoomy_ratio);
        //double activeStepY=mStepY+((double) value)/50;
        //double activeStepY=mStepY;

        double activeStepY=mStepY;
        double actvieFactorY=0.84;

        actvieFactorY = valueYtoFactorY(value);


        activeStepY=mStepY*actvieFactorY;

        double movey = value*activeStepY*(20-vZoom)/20;
        double y = rt_y + (activeZoomy + movey);

        SystemPropertiesUtils.setProperty(PROP_RT,x+","+y);
        SystemPropertiesUtils.setProperty(PROP_RTX,x+"");
        SystemPropertiesUtils.setProperty(PROP_RTY,y+"");
    }

    public void updateBottomLeft(int value){

        double zoomx = lcdValidWidth_bottom*vZoom/10/2/2;
        //double activeStepX=mStepX-((double) value)/50;

        double actvieFactorX=1;
        actvieFactorX = valueXtoFactorX(value);

        double activeStepX=mStepX*actvieFactorX;

        double movex = value*activeStepX*(20-vZoom)/20;
        //double movex = value*activeStepX*(10-vZoom/2)/10;
        double x = lb_x + (zoomx + movex);
        if (value>0){
            x -= value*vZoom/10;
        }
        double zoomy = lcdValidHeight_left*vZoom/10/2/2;
        double activeZoomy = (double) (zoomy * zoomy_ratio);
        //double activeStepY=mStepY+((double) value)/50;
        //double activeStepY=mStepY;

        double activeStepY=mStepY;
        double actvieFactorY=0.84;
        actvieFactorY = valueYtoFactorY(value);

        activeStepY=mStepY*actvieFactorY;

        double movey = value*activeStepY*(20-vZoom)/20;
        double y = lb_y + (activeZoomy + movey);

        SystemPropertiesUtils.setProperty(PROP_LB,x+","+y);
        SystemPropertiesUtils.setProperty(PROP_LBX,x+"");
        SystemPropertiesUtils.setProperty(PROP_LBY,y+"");
    }
    public void updateBottomRight(int value){

        double zoomx = lcdValidWidth_bottom*vZoom/10/2/2;
        //double activeStepX=mStepX-((double) value)/50;
        //double activeStepX=mStepX;

        double actvieFactorX=1;

        actvieFactorX = valueXtoFactorX(value);

        double activeStepX=mStepX*actvieFactorX;


        //double movex = value*activeStepX*(10-vZoom/2)/10;
        double movex = value*activeStepX*(20-vZoom)/20;
        //double x = rt_x + (zoomx + movex);
        double x = lt_x + (zoomx + movex);
        if (value>0){
            x -= value*vZoom/10;
        }

        double zoomy = lcdValidHeight_right*vZoom/10/2/2;
        double activeZoomy = (double) (zoomy * zoomy_ratio);

        double activeStepY=mStepY;
        double actvieFactorY=0.84;

        actvieFactorY = valueYtoFactorY(value);


        activeStepY=mStepY*actvieFactorY;

        double movey = value*activeStepY*(20-vZoom)/20;
        double y = rb_y + (activeZoomy + movey);

        SystemPropertiesUtils.setProperty(PROP_RB,x+","+y);
        SystemPropertiesUtils.setProperty(PROP_RBX,x+"");
        SystemPropertiesUtils.setProperty(PROP_RBY,y+"");
    }



    public void updateTopLeft_roof(int value){

        double zoomx = lcdValidWidth_top*vZoom/10/2/2;
        //double activeStepX=mStepX-((double) value)/50;
        double actvieFactorX=valueXtoFactorX_Roof(value);
        double activeStepX=mStepX*actvieFactorX;
        double movex = value*activeStepX*(20-vZoom)/20;
        double x = lt_x + (zoomx + movex);
        double zoomy = lcdValidHeight_left*vZoom/10/2/2;
        double activeZoomy = (double) (zoomy * zoomy_ratio);
        //double activeStepY=mStepY+((double) value)/50;
        double activeStepY=mStepY_roof;
//        double activeStepY=mStepY*1.475; //1.75 缩的太多了；1.50 缩的多了一点；1.45在角度大的时候缩少了点。
        double movey = value*activeStepY*(20-vZoom)/20;
        double y = lt_y + (activeZoomy + movey);

        SystemPropertiesUtils.setProperty(PROP_LT,x+","+y);
        SystemPropertiesUtils.setProperty(PROP_LTX,x+"");
        SystemPropertiesUtils.setProperty(PROP_LTY,y+"");
    }

    public void updateTopRight_roof(int value){

        double zoomx = lcdValidWidth_top*vZoom/10/2/2;
        //double activeStepX=mStepX-((double) value)/50;
        double actvieFactorX=valueXtoFactorX_Roof(value);
        double activeStepX=mStepX*actvieFactorX;
        double movex = value*activeStepX*(20-vZoom)/20;
        double x = rt_x + (zoomx + movex);
        double zoomy = lcdValidHeight_right*vZoom/10/2/2;
        double activeZoomy = (double) (zoomy * zoomy_ratio);
        //double activeStepY=mStepY+((double) value)/50;
        double activeStepY=mStepY_roof;
//        double activeStepY=mStepY*1.45;
        double movey = value*activeStepY*(20-vZoom)/20;
        double y = rt_y + (activeZoomy + movey);

        SystemPropertiesUtils.setProperty(PROP_RT,x+","+y);
        SystemPropertiesUtils.setProperty(PROP_RTX,x+"");
        SystemPropertiesUtils.setProperty(PROP_RTY,y+"");
    }

    public void updateBottomLeft_roof(int value){

        double zoomx = lcdValidWidth_bottom*vZoom/10/2/2;
        //double activeStepX=mStepX-((double) value)/50;
        double actvieFactorX=valueXtoFactorX_Roof(value);
        double activeStepX=mStepX*actvieFactorX;
        double movex = value*activeStepX*(20-vZoom)/20;
        double x = lb_x + (zoomx + movex);
        double zoomy = lcdValidHeight_left*vZoom/10/2/2;
        double activeZoomy = (double) (zoomy * zoomy_ratio);
        //double activeStepY=mStepY+((double) value)/50;
        double activeStepY=mStepY_roof;
        //double activeStepY=mStepY*1.45;
        double movey = value*activeStepY*(20-vZoom)/20;
        double y = lb_y + (activeZoomy + movey);

        SystemPropertiesUtils.setProperty(PROP_LB,x+","+y);
        SystemPropertiesUtils.setProperty(PROP_LBX,x+"");
        SystemPropertiesUtils.setProperty(PROP_LBY,y+"");
    }
    public void updateBottomRight_roof(int value){

        double zoomx = lcdValidWidth_bottom*vZoom/10/2/2;
        //double activeStepX=mStepX-((double) value)/50;
        double actvieFactorX=valueXtoFactorX_Roof(value);
        double activeStepX=mStepX*actvieFactorX;
        double movex = value*activeStepX*(20-vZoom)/20;
        double x = rb_x + (zoomx + movex);
        double zoomy = lcdValidHeight_right*vZoom/10/2/2;
        double activeZoomy = (double) (zoomy * zoomy_ratio);
        //double activeStepY=mStepY+((double) value)/50;
        double activeStepY=mStepY_roof;
        //double activeStepY=mStepY*1.45;
        double movey = value*activeStepY*(20-vZoom)/20;
        double y = rb_y + (activeZoomy + movey);

        SystemPropertiesUtils.setProperty(PROP_RB,x+","+y);
        SystemPropertiesUtils.setProperty(PROP_RBX,x+"");
        SystemPropertiesUtils.setProperty(PROP_RBY,y+"");
    }


    public void topZoomOut(int value){
        //vTopLeft.doRight();
        //vTopLeft.doBottom();
        //vTopRight.doLeft();
        //vTopRight.doBottom();

        updateTopLeft(value);
        updateTopRight(value);
    }
    public void topZoomOut_roof(int value){
        //vTopLeft.doRight();
        //vTopLeft.doBottom();
        //vTopRight.doLeft();
        //vTopRight.doBottom();

        updateTopLeft_roof(value);
        updateTopRight_roof(value);
    }

    public void topZoomIn(int value){
        //vTopLeft.doLeft();
        //vTopLeft.doTop();
        //vTopRight.doRight();
        //vTopRight.doTop();

        updateTopLeft(value);
        updateTopRight(value);
    }
    public void bottomZoomOut(int value){
        //vBottomRight.doLeft();
        //vBottomRight.doTop();
        //vBottomLeft.doRight();
        //vBottomLeft.doTop();

        updateBottomLeft(value);
        updateBottomRight(value);
    }
    public void bottomZoomOut_roof(int value){
        //vBottomRight.doLeft();
        //vBottomRight.doTop();
        //vBottomLeft.doRight();
        //vBottomLeft.doTop();

        updateBottomLeft_roof(value);
        updateBottomRight_roof(value);
    }
    public void bottomZoomIn(int value){
        //vBottomRight.doRight();
        //vBottomRight.doBottom();
        //vBottomLeft.doLeft();
        //vBottomLeft.doBottom();

        updateBottomLeft(value);
        updateBottomRight(value);
    }
    public void start_keystone(int degree){
        if(degree >=0){
            topZoomOut(0);
            bottomZoomOut(degree);
        }else{
            topZoomOut(-degree);
            bottomZoomOut(0);
        }
        SystemPropertiesUtils.setProperty(PROP_KEYSTONE_UPDATE, "1");
    }

    public void reset_keystone(){
        topZoomOut(0);
        bottomZoomOut(0);

        updateALL();
        SystemPropertiesUtils.setProperty(PROP_KEYSTONE_UPDATE, "1");
    }

    public void start_keystone_aw(int degree){

        mDegree = degree;
        KeystoneViewModel vm = new KeystoneViewModel(this.getApplication());
        vm.getInitKeystone(this.getApplication());
        if(degree >=0){
            //topZoomOut(0);
            //bottomZoomOut(degree);
            vm.topZoomOutN(0);
            vm.bottomZoomOutN(degree);
        }else{
            //topZoomOut(-degree);
            vm.bottomZoomOutN(0);
            vm.topZoomOutN(degree);
            //bottomZoomOut(0);

        }

        //SystemPropertiesUtils.setProperty("persist.sys.keystone.update", "1");
        //mWindowManager.addView(mWindowView, wmParams);
//		mWindowView.show();
        //mWindowManager.removeView(mWindowView);

    }

    public void start_keystone_aw2(int degree) {

        mDegree = degree;
        refress_view_flag = true;
//        KeystoneViewModel vm = new KeystoneViewModel(this.getApplication());
//        vm.getInitKeystone(this.getApplication());
        if(degree >=0){
            topZoomOut(0);
            bottomZoomOut(degree);

            if (degree >= 50)
            {
                bottomZoomOut(0);
            }

            //vm.topZoomOutN(0);
            //vm.bottomZoomOutN(degree);
        }else{
            topZoomOut(-degree);
            bottomZoomOut(0);

            if((-degree) >= 50)
            {
                topZoomOut(0);
            }

//            vm.bottomZoomOutN(0);
//            vm.topZoomOutN(degree);
        }
        updateALL();
        SystemPropertiesUtils.setProperty(PROP_KEYSTONE_UPDATE, "1");


    }


    public void start_keystone_aw3(int degree) {

        mDegree = degree;
        refress_view_flag = true;
//        KeystoneViewModel vm = new KeystoneViewModel(this.getApplication());
//        vm.getInitKeystone(this.getApplication());
        if(degree >=0){

            //if (degree < 50) {
            if (degree < DEGREE_TO_ROOF) {
                topZoomOut(degree);
                bottomZoomOut(0);
            }
            else {
                if(z > 0){
                    topZoomOut(0);
                    //bottomZoomOut_roof((90-degree)*30/40);
                    bottomZoomOut_roof((90-degree));
                }
                else {
                    topZoomOut_roof(90-degree);
                    //bottomZoomOut_roof((90-degree)*30/40);
                    bottomZoomOut(0);
                }
            }

            //vm.topZoomOutN(0);
            //vm.bottomZoomOutN(degree);
        }else{

            if((-degree) < DEGREE_TO_ROOF)
            {
                topZoomOut(0);
                bottomZoomOut(-degree);
            }
            else {
                if(z < 0){
                    topZoomOut_roof(90+degree);
                    bottomZoomOut(0);
                }
                else {
                    topZoomOut(0);
                    bottomZoomOut_roof(90+degree);
                }
            }

//            vm.bottomZoomOutN(0);
//            vm.topZoomOutN(degree);
        }
        updateALL();
        SystemPropertiesUtils.setProperty(PROP_KEYSTONE_UPDATE, "1");

    }



    public void gyro_keystone(float left_right,float up_down,float tilt){
    /*
        if(left_right >50.0f){
            left_right = 50.0f;
        }else if(left_right <-50.0f){
            left_right = -50.0f;
        }
        if(up_down > 50.0f){
            up_down = 50.0f;
        }else if(up_down < -50.0f){
            up_down = -50.0f;
        }
        Log.e(TAG, "======= left_right: "+left_right+",up_down:"+up_down+")========");

        A[0] = -PROJECTION_WIDTH/2;
        A[1] = PROJECTION_HIGH/2;
        A[2] = 0;
        B[0] = PROJECTION_WIDTH/2;
        B[1] = PROJECTION_HIGH/2;
        B[2] = 0;
        C[0] = -PROJECTION_WIDTH/2;
        C[1] = -PROJECTION_HIGH/2;
        C[2] = 0;
        D[0] = PROJECTION_WIDTH/2;
        D[1] = -PROJECTION_HIGH/2;
        D[2] = 0;
        E[0] = 0;
        E[1] = 0;
        E[2]  = DISTANCE;

        updateABCD(up_down,left_right);
        updateTopLeft();
        updateTopRight();
        updateBottomLeft();
        updateBottomRight();

        SystemPropertiesUtils.setProperty("persist.sys.keystone.update", "1");
        //mWindowView.show(left_right,up_down,tilt);
        mWindowManager.addView(mWindowView, wmParams);
        mWindowManager.removeView(mWindowView);
        //Message delay_close = new Message();
        //delay_close.what = MSG_DELAY_CLOSE;
        //mHandler.sendMessageDelayed(delay_close,3000);

        SystemPropertiesUtils.setProperty(ANGLEX,String.valueOf(left_right));
        SystemPropertiesUtils.setProperty(ANGLEY,String.valueOf(up_down));
        SystemPropertiesUtils.setProperty(ANGLEZ,String.valueOf(tilt));*/
    }


    private static String readFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                String line = reader.readLine();

                return line;
            } catch (Exception e) {

                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
            }
        } else {

        }
        return "";
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

            } catch (Exception e) {
                flag = false;

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

        }
        return flag;
    }

    public void gyro_keystone_reset(){
        gyro_anglex = 0;
        gyro_angley = 0;
        gyro_anglez = 0;

        angle[0] = 0;
        angle[1] = 0;
        angle[2] = 0;

        device_anglex = 0;
        device_angley = 0;
        device_anglez = 0;

        device_init_anglex = 0;
        device_init_angley = 0;
        device_init_anglez = 0;

        gyro_keystone(0,0,0);
        String cali_data = readFile("/sys/bus/platform/drivers/gyroscope/cali");
        SystemPropertiesUtils.setProperty("persist.sh3201.cali",cali_data);
    }


    private SensorEventListener gyroSensorLis = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

        @Override
        public void onSensorChanged(SensorEvent event) {

            if (event.sensor.getType() != Sensor.TYPE_GYROSCOPE) {
                return;
            }

            if(timestamp != 0)
            {
                // 得到两次检测到手机旋转的时间差（纳秒），并将其转化为秒
                final float dT = (event.timestamp -timestamp) * NS2S;
//						Log.e(TAG, "gyroscope DT  time:"+String.valueOf(timestamp)+"  ,DT:"+dT);

                // 将手机在各个轴上的旋转角度相加，即可得到当前位置相对于初始位置的角度
                angleDT[0] = event.values[SensorManager.DATA_X] * dT;
                angleDT[1] = event.values[SensorManager.DATA_Y] * dT;
                angleDT[2] = event.values[SensorManager.DATA_Z] * dT;

//						angleDT[0] = event.values[SensorManager.DATA_X] /31;
//						angleDT[1] = event.values[SensorManager.DATA_Y] /31;
//						angleDT[2] = event.values[SensorManager.DATA_Z] /31;

                angle[0] += angleDT[0];
                angle[1] += angleDT[1];
                angle[2] += angleDT[2];
                angleCount++;


                gyro_anglex = angle[0];//(float) Math.toDegrees(angle[0]);
                gyro_angley = angle[1];//(float) Math.toDegrees(angle[1]);
                gyro_anglez = angle[2];//(float) Math.toDegrees(angle[2]);

//						Log.e(TAG, "gyroscope DT :("+angleDT[0]+","+angleDT[1]+","+angleDT[2]+"),  gyroscope angle: ("+gyro_anglex+","+gyro_angley+","+gyro_anglez+")");

                gyroQuiet[0] += angleDT[0];
                gyroQuiet[1] += angleDT[1];
                gyroQuiet[2] += angleDT[2];
                gyroQuietCount++;

                if(gyroQuietCount >= GYRO_QUIET_COUNT){
                    quietAngle[0] = gyroQuiet[0];//(float) Math.toDegrees(gyroQuiet[0]);
                    quietAngle[1] = gyroQuiet[1];//(float) Math.toDegrees(gyroQuiet[1]);
                    quietAngle[2] = gyroQuiet[2];//(float) Math.toDegrees(gyroQuiet[2]);
                    gyroQuietCount = 0;
//							Log.e(TAG, "quietAngle : x= "+quietAngle[0]+" y= "+quietAngle[1]+" z= "+quietAngle[2]);
//							Log.e(TAG, "-----keystone now: x= "+device_anglex+" y= "+device_angley+" z= "+device_anglez);

                    boolean  autoFocus_reset = "1".equals(SystemPropertiesUtils.getProperty("persist.ty.autofocus.reset","0"));

                    if(autoFocus_reset){
                        SystemPropertiesUtils.setProperty("persist.ty.autofocus.reset","0");
                        gyro_keystone_reset();
                    }

                    if(quietAngle[0]<0.2 && quietAngle[0]>-0.2 && quietAngle[1]<0.2 && quietAngle[1]>-0.2 &&quietAngle[2]<0.2 && quietAngle[2]>-0.2){
//								Log.e(TAG, "-----gyro_angle clean : x= "+gyro_anglex+" y= "+gyro_angley+" z= "+gyro_anglez);
                        if(moving){
                            moving = false;
                            device_anglex = gyro_anglez + device_init_anglex;
                            //device_angley = gyro_angley;
                            device_anglez = gyro_anglex +device_init_anglez;
//									Log.e(TAG, "-----keystone : x= "+device_anglex+" y= "+device_angley+" z= "+device_anglez);

                            boolean autoFocus = "1".equals(SystemPropertiesUtils.getProperty("persist.ty.autofocus","0"));
                            if(autoFocus){
                                gyro_keystone(device_anglex,device_angley,device_anglez);
                                //gyro_keystone(device_anglex,device_angley,device_anglez);
                            }
                        }
//								angle[0] = 0;
//								angle[1] = 0;
//								angle[2] = 0;

//								gyro_anglex = 0;
//								gyro_angley = 0;
//								gyro_anglez = 0;
                    }else{//moveing
                        moving = true;
                    }
                    gyroQuiet[0] = 0;
                    gyroQuiet[1] = 0;
                    gyroQuiet[2] = 0;
                }
            }
            timestamp = event.timestamp;


        }
    };

    private SensorEventListener sensorLis = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

        @Override
        public void onSensorChanged(SensorEvent event) {

            if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
                return;
            }


            int autoFocus = Integer.parseInt((SystemPropertiesUtils.getProperty(PROP_AUTOFOCUS, "1")));

            if ((autoFocus == 0 && autofocus_flag == 0) || (autoFocus == -1)) {
                Log.d(TAG, "onSensorChanged: return zzz");
                autofocus_flag = autoFocus;
                return;
            } else if (autoFocus != autofocus_flag) {
                autofocus_flag = autoFocus;
                if (autofocus_flag == 0) {
                    reset_keystone();
                    return;
                }else if(autofocus_flag == 1){
                    start_keystone_aw3(mDegree);
                }

            }


            x = event.values[SensorManager.DATA_Y];
            y = event.values[SensorManager.DATA_X];
            z = event.values[SensorManager.DATA_Z];


            //zzzz
            /*  yk
            double t;
            t = x;
            x = y;
            y = t;
            z = -z; */

            x /= 10;
            y /= 10;
            z /= 10;
            y = -y;

            

            if ((ignore_count--) > 0) {
                return;
            }

            float g_y = (float) Math.sqrt(x * x + z * z);
            double tan = y / g_y;
            double rad = Math.atan(tan);
            int degree = (int) (180 * rad / Math.PI);

             
            curDegree = degree;

            int mode = Integer.parseInt(SystemPropertiesUtils.getProperty(PROP_PROJECTION, "0"));


            if ((mode == 2) || (mode == 3)) {
                degree = -degree;
            }

            int new_vZoom2 = Integer.parseInt(SystemPropertiesUtils.getProperty(PROP_ZOOMLEVEL2, "0"));
            if (new_vZoom2!=0){
                vZoom = vZoom_old;
            }

            int new_vZoom = Integer.parseInt(SystemPropertiesUtils.getProperty(PROP_ZOOMLEVEL, "0"));
            if (new_vZoom != (int)vZoom) {
                vZoom = new_vZoom;
                //boolean autoFocus = "1".equals(SystemProperties.get("persist.ty.autofocus","0"));
                if (autoFocus == 1) {
                    start_keystone_aw3(mDegree);
                }
            }

            if (new_vZoom2!=0){
                vZoom_old = vZoom;
                vZoom = (100-(double)new_vZoom2)/10;

                if((vZoom2 != vZoom))
                {
                    vZoom2 = vZoom;
                    if ((autoFocus == 1)&&(gsensor_count >= ARRAY_SIZE)) {
                        start_keystone_aw3(mDegree);
                    }
                }
            }

            if (ignore_degree > 0) {
                ignore_degree--;
            }


            if (gsensor_count < ARRAY_SIZE) {
                degree_arrays[gsensor_count] = degree;

                gsensor_count++;
                gsensor_index = gsensor_count;

            } else {
 
                gsensor_stable_value = 0;

                for (int i = 0; i < ARRAY_SIZE - 1; i++) {
                    gsensor_stable_value += degree_arrays[i];
                    degree_arrays[i] = degree_arrays[i + 1];    
                    if (i == 0) {
                        degree_high = degree_arrays[i];
                        degree_low = degree_arrays[i];
                    } else {
                        degree_high = Math.max(degree_high, degree_arrays[i]);
                        degree_low = Math.min(degree_low, degree_arrays[i]);
                    }
                }
                gsensor_stable_value = gsensor_stable_value / (ARRAY_SIZE - 1);
                degree_arrays[ARRAY_SIZE - 1] = degree;

                degree_high = Math.max(degree_high, degree_arrays[ARRAY_SIZE - 1]);
                degree_low = Math.min(degree_low, degree_arrays[ARRAY_SIZE - 1]);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    degree_sum = Arrays.stream(degree_arrays).sum();
                    degree_sum -= degree_high;
                    degree_sum -= degree_low;
                    degree_average = degree_sum / (ARRAY_SIZE - 2);  
                }

                if (poweron_flag){
                    SystemPropertiesUtils.setProperty(PROP_POWERON, "false");
                    poweron_flag=false;
                    start_keystone_aw3(degree_average);
                }

                if (degree_average < DEGREE_TO_ROOF) {
                    adjustToWall(degree);
                } else {
                    adjustToRoof(degree);
                }


//                    if((gsensor_stable_value > (mDegree+2))  || (gsensor_stable_value < (mDegree -2))){
//                        Log.e(TAG, "sensor is change slowly,  set new :"+(int)gsensor_stable_value);
//
//                         start_keystone_aw2((int)gsensor_stable_value);
//
//                    }
//                    else if(moving){
//                        if(degree_high -degree_low <= 5){
//                            moving=false;
//                            Log.e(TAG, "sensor is stoped,  high"+degree_high+", low="+degree_low+", degree="+degree + ",gsensor_stable_value="+gsensor_stable_value);
//                            start_keystone_aw2(degree);
//                        }
//                    }


//                if(moving){
//                    if(degree_high -degree_low <= 5){
//                        moving=false;
//                        Log.e(TAG, "sensor is stoped,  high"+degree_high+", low="+degree_low+", degree="+degree + ",gsensor_stable_value="+gsensor_stable_value);
//
//                        start_keystone_aw2(degree);
//                        mWindowManager.addView(mWindowView, wmParams);
//                        //mWindowView.show();
//                        mWindowManager.removeView(mWindowView);
//
//                    }
//                }

//
//                if (refress_view_flag) {
//                    refress_view_flag = false;
//                    refress_view_cont = 0;
//                }
//
////                if (refress_view_cont<100)
////                {
////                    Log.d(TAG, "onSensorChanged: refress_view refress_view_cont="+refress_view_cont);
////                    refress_view_cont++;
////                    if(refress_view_cont==99)
////                    {
////                        Log.d(TAG, "onSensorChanged: refress_view !!! refress_view_cont="+refress_view_cont);
////                        mWindowManager.addView(mWindowView, wmParams);
////                        //mWindowView.show();
////                        mWindowManager.removeView(mWindowView);
////                    }
////
////                }
//
//
//                if (refress_view_cont<1)
//                {
//                    Log.d(TAG, "onSensorChanged: refress_view refress_view_cont="+refress_view_cont);
//                    refress_view_cont++;
//
//                    Log.d(TAG, "onSensorChanged: refress_view !!! refress_view_cont="+refress_view_cont);
//                    //mWindowManager.addView(mWindowView, wmParams);
//                    mWindowView.show();
//                    //mWindowManager.removeView(mWindowView);
//
//                }


            }
        }
    };

    private void adjustToRoof(int degree) {
        if (degree_high - degree_low > 5) {    
            if ((degree_average > (mDegree + 3)) || (degree_average < (mDegree - 3))) {    
                for (int i = ARRAY_SIZE - ARRAY_SIZE_LAST; i <= ARRAY_SIZE - 1; i++) {

                    if (i == (ARRAY_SIZE - ARRAY_SIZE_LAST)) {
                        degree_high_last = degree_arrays[i];
                        degree_low_last = degree_arrays[i];
                        degree_sum_last = degree_arrays[i];
                    } else {
                        degree_high_last = Math.max(degree_high_last, degree_arrays[i]);
                        degree_low_last = Math.min(degree_low_last, degree_arrays[i]);
                        degree_sum_last += degree_arrays[i];

                    }
                }
                degree_average_last = degree_sum_last / ARRAY_SIZE_LAST;

                if (((degree_high_last - degree_low_last) < 5) && ((degree_average_last > (mDegree + 3)) || (degree_average_last < (mDegree - 3)))) {

                    moving = true;
                } else {
                    if (!moving) {
                        return;
                    }
                }

            } else {
                if (!moving) {
                    return;
                }

            }

            if ((degree_average >= (mDegree + 3)) || (degree_average <= (mDegree - 3))) {   
                start_keystone_aw3((int) degree_average);
            }

        } else {

            if ((degree_average > (mDegree + 2)) || (degree_average < (mDegree - 3))) {


                for (int i = ARRAY_SIZE - ARRAY_SIZE_LAST; i <= ARRAY_SIZE - 1; i++) {

                    if (i == (ARRAY_SIZE - ARRAY_SIZE_LAST)) {
                        degree_high_last = degree_arrays[i];
                        degree_low_last = degree_arrays[i];
                        degree_sum_last = degree_arrays[i];
                    } else {
                        degree_high_last = Math.max(degree_high_last, degree_arrays[i]);
                        degree_low_last = Math.min(degree_low_last, degree_arrays[i]);
                        degree_sum_last += degree_arrays[i];

                    }
                }
                degree_average_last = degree_sum_last / ARRAY_SIZE_LAST;

                if (((degree_high_last - degree_low_last) < 3) && ((degree_average_last > (mDegree + 3)) || (degree_average_last < (mDegree - 3)))) {
                    start_keystone_aw3((int) degree_average_last);
                }
            }

            if (moving) {
                moving = false;
                Arrays.fill(degree_arrays, degree);
                gsensor_count = 0;
                degree_low = degree;
                degree_high = degree;
                start_keystone_aw3(degree);
            }

        }

    }

    private void adjustToWall (int degree) {
        if (degree_high - degree_low > 8) {    
            if ((degree_average > (mDegree + 5)) || (degree_average < (mDegree - 5))) {   
                for (int i = ARRAY_SIZE - ARRAY_SIZE_LAST; i <= ARRAY_SIZE - 1; i++) {

                    if (i == (ARRAY_SIZE - ARRAY_SIZE_LAST)) {
                        degree_high_last = degree_arrays[i];
                        degree_low_last = degree_arrays[i];
                        degree_sum_last = degree_arrays[i];
                    } else {
                        degree_high_last = Math.max(degree_high_last, degree_arrays[i]);
                        degree_low_last = Math.min(degree_low_last, degree_arrays[i]);
                        degree_sum_last += degree_arrays[i];

                    }
                }
                degree_average_last = degree_sum_last / ARRAY_SIZE_LAST;

                if (((degree_high_last - degree_low_last) < 6) && ((degree_average_last > (mDegree + 3)) || (degree_average_last < (mDegree - 3)))) {
                    moving = true;
                } else {
                    if (!moving) {
                        return;
                    }
                }

            } else {
                if (!moving) {
                    return;
                }

            }

            if((degree_average >= (mDegree+3))  || (degree_average <= (mDegree -3))){
                start_keystone_aw3((int) degree_average);

            }

        } else {

                if ((degree_average >= (mDegree + 5)) || (degree_average <= (mDegree - 5))) {


                for (int i = ARRAY_SIZE - ARRAY_SIZE_LAST; i <= ARRAY_SIZE - 1; i++) {

                    if (i == (ARRAY_SIZE - ARRAY_SIZE_LAST)) {
                        degree_high_last = degree_arrays[i];
                        degree_low_last = degree_arrays[i];
                        degree_sum_last = degree_arrays[i];
                    } else {
                        degree_high_last = Math.max(degree_high_last, degree_arrays[i]);
                        degree_low_last = Math.min(degree_low_last, degree_arrays[i]);
                        degree_sum_last += degree_arrays[i];

                    }
                }
                degree_average_last = degree_sum_last / ARRAY_SIZE_LAST;

                if (((degree_high_last - degree_low_last) < 3) && ((degree_average_last > (mDegree + 3)) || (degree_average_last < (mDegree - 3)))) {
                     start_keystone_aw3((int) degree_average_last);

                }
            }

            if (moving) {

                moving = false;
                Arrays.fill(degree_arrays, degree);
                gsensor_count = 0;
                degree_low = degree;
                degree_high = degree;
                start_keystone_aw3(degree);
            }

        }
    }



}
