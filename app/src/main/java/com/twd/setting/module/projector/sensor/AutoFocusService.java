package com.twd.setting.module.projector.sensor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcel;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.os.UserHandle;

import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;
//import android.os.IStepMotorService;
//import android.os.ServiceManager;
import android.os.RemoteException;

import com.twd.setting.utils.SystemPropertiesUtils;

public class AutoFocusService extends Service {
    public static final String TAG = AutoFocusService.class.getSimpleName();


    public static final String ACTION_FORWARD_MOTOR= "com.tw.projector.action.FORWARD_MOTOR";
	public static final String ACTION_FORWARD_STOP= "com.tw.projector.action.FORWARD_STOP";
	public static final String ACTION_BACKWARD_MOTOR= "com.tw.projector.action.BACKWARD_MOTOR";
	public static final String ACTION_BACKWARD_STOP= "com.tw.projector.action.BACKWARD_STOP";
    public static final String ACTION_STOP_MOTOR= "com.tw.projector.action.STOP_MOTOR";

	public static final String ACTION_CASTMODE_FIXEDFRONT = "com.tw.projector.action.CASTMODE_FIXEDFRONT";
	public static final String ACTION_CASTMODE_FIXEDBACK= "com.tw.projector.action.CASTMODE_FIXEDBACK";
	public static final String ACTION_CASTMODE_HANGINGFRONT= "com.tw.projector.action.CASTMODE_HANGINGFRONT";
	public static final String ACTION_CASTMODE_HANGINGBACK= "com.tw.projector.action.CASTMODE_HANGINGBACK";
	public static final String ACTION_SET_KEY= "com.tw.projector.action.SET_KEY";

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

	static public final String CTRL = "/sys/devices/platform/motor0/motor_ctrl";
	private static final String PATH_CONTROL_MIPI = "persist.sys.projection";
    public static final int MSG_UPDATE_UI= 1;
	public static final int MSG_UPDATE_UI_FAST = 2;

	public static final int MSG_ANIMATION_UI_FAST = 4;
	public static final int MSG_CLOSE_ANIMATION= 100;
	
	public static final int MSG_FORWARD_ONE_STEP= 5;
	public static final int MSG_BACKWARD_ONE_STEP= 6;
	
	public static final int MSG_UI_FORWARD_STOP = 7;
	public static final int MSG_UI_BACKWARD_STOP = 8;
    
    private WindowManager.LayoutParams wmParams;
    private WindowManager mWindowManager;
    private MotorAnimationLayout mMotorWindowView;
	private GSensorAnimationLayout mAnimationWindowView;
	private Timer timer = null;
	private TimerTask timerAnimationTask = null;
	private TimerTask timerStopTask = null;
	FileWriter Writer = null;

	private IBinder mSurfaceFlinger;
	private float mLeftBottomX = 0;
	private float mLeftBottomY = 0;
	private float mRightBottomX = 0;
	private float mRightBottomY = 0;
	private float mLeftTopX = 0;
	private float mLeftTopY = 0;
	private float mRightTopX = 0;
	private float mRightTopY = 0;

	public static int step = 0;
	public static boolean showAnimation = false;
	private static final int all_step = 12;
	private static boolean  fastUI_mode = false;
	private static long  keyTimeMillis;
	private static int  key_direction;//0 forward,  1 backward
	public static final int showAnimation_delaytime= 500;
	private static boolean stop = true;
	
	private static boolean forward_stop = true;
	private static boolean backward_stop = true;
	private static boolean time_lock = false;
	
	private static int point = -1;
	private static int direction = -1;
	private static int key_status = 0;
	private static int speed_mode = 0;
	
	private static boolean double_click = false;
	private static int double_direction = -1;
	private static boolean key_down = false;
    
    private PowerManager.WakeLock mWakelock = null;

    
    @Override
    public void onCreate() {
        super.onCreate();

        init();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null ){
			String action = intent.getAction();
			if (action != null) {

				if (ACTION_FORWARD_MOTOR.equals(action)) {
					forwardMotor();
				}else if (ACTION_BACKWARD_MOTOR.equals(action)) {
					backwardMotor();
				}else if (ACTION_FORWARD_STOP.equals(action)) {
					forwardStop();
				}else if (ACTION_BACKWARD_STOP.equals(action)) {
					backwardStop();
				}else if (ACTION_STOP_MOTOR.equals(action)) {
					stopMotor();
				}else if (ACTION_CASTMODE_FIXEDFRONT.equals(action)) {
					castMode(0);
				}else if (ACTION_CASTMODE_FIXEDBACK.equals(action)) {
					castMode(1);
				}else if (ACTION_CASTMODE_HANGINGFRONT.equals(action)) {
					castMode(2);
				}else if (ACTION_CASTMODE_HANGINGBACK.equals(action)) {
					castMode(3);
				}else if (ACTION_SET_KEY.equals(action)) {
					setKey();
				}else {
					Log.d(TAG, "onStartCommand - else");
				}
			}
		}
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    private void init() {

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
		mAnimationWindowView = new GSensorAnimationLayout(this);
		mWindowManager.addView(mAnimationWindowView, wmParams);

    }
	
	private void motorForward(){

	}
	
	private void motorBackward(){

	}
	
	private void motorForwardone(){

	}
	
	private void motorBackwardone(){

	}
	
	private void motorStop(){

	}
	private int motorGet(){

		return 0;
	}

	
	
    private void forwardMotor() {
        try {
            Writer.write("1,99999999");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
	private void forwardStop() {
		try {
			Writer.write("1,0");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
	
    private void backwardMotor() {
		try {
			Writer.write("2,99999999");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
    }
	private void backwardStop() {
		try {
			Writer.write("2,0");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void stopMotor() {

		try {
			Writer.write("1,0");
			Writer.write("2,0");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

    }

	private void castMode(int mode) {

		SystemPropertiesUtils.setProperty(PATH_CONTROL_MIPI,String.valueOf(mode));
	}

	private void setKey() {
		updateALL();
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

				mAnimationWindowView.showZ();

			} else {

			}
		} catch (Exception ex) {

		}
	}

}
