package com.twd.setting.module.projector.sensor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
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

import androidx.core.app.NotificationCompat;

import com.twd.setting.R;

public class MotorService extends Service {
    public static final String TAG = MotorService.class.getSimpleName();
    public static final String ACTION_FORWARD_MOTOR= "com.tw.projector.action.FORWARD_MOTOR";
	public static final String ACTION_FORWARD_STOP= "com.tw.projector.action.FORWARD_STOP";
	public static final String ACTION_BACKWARD_MOTOR= "com.tw.projector.action.BACKWARD_MOTOR";
	public static final String ACTION_BACKWARD_STOP= "com.tw.projector.action.BACKWARD_STOP";
    public static final String ACTION_STOP_MOTOR= "com.tw.projector.action.STOP_MOTOR";

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
    private MotorAnimationLayout mWindowView;
	private Timer timer = null;
	private TimerTask timerAnimationTask = null;
	private TimerTask timerStopTask = null;
	
	public static int step = 0;
	public static boolean showAnimation = false;
	private static final int all_step = 12;
	private static boolean  fastUI_mode = false;
	private static long  keyTimeMillis;
	private static int  key_direction;//0 forward,  1 backward
	//public static final int showAnimation_delaytime= 500;
	public static final int showAnimation_delaytime= 200;
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
	private static final int NOTIFICATION_ID = 1;
    
    private PowerManager.WakeLock mWakelock = null;

	//private IStepMotorService mStepMotorService;
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
		//startForegroundWithNotification();
        init();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null ){
			String action = intent.getAction();
			if (action != null) {
				Log.d(TAG, "onStartCommand - " + action);
				if (ACTION_FORWARD_MOTOR.equals(action)) {
					forwardMotor();
				} else if (ACTION_BACKWARD_MOTOR.equals(action)) {
					backwardMotor();
				} else if (ACTION_FORWARD_STOP.equals(action)) {
					forwardStop();
				} else if (ACTION_BACKWARD_STOP.equals(action)) {
					backwardStop();
				} else if (ACTION_STOP_MOTOR.equals(action)) {
					stopMotor();
				}
			}
		}else {
			Log.d(TAG, "onStartCommand: intent="+intent);
		}


        return super.onStartCommand(intent, flags, startId);
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

		// 启动前台服务
		startForeground(NOTIFICATION_ID, notification);
	}

    private void init() {
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        wmParams = new WindowManager.LayoutParams();
        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        wmParams.format = PixelFormat.TRANSLUCENT;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.CENTER;
        wmParams.width = 300;
        wmParams.height = 300;
        Log.d(TAG, "---LoadMotorService ----init. ");
        mWindowView = new MotorAnimationLayout(this);
//		mWindowManager.addView(mWindowView, wmParams);
		timer = new Timer();
//		if (mStepMotorService == null) {
//        	mStepMotorService = IStepMotorService.Stub.asInterface(ServiceManager.getService("stepmotor"));
//        }
		
    }
	
	private void motorForward(){
/*		try {
			 if (mStepMotorService != null) {
			 	mStepMotorService.forward(100);
//				Log.d(TAG, "---------forwardMotor:------" );
				stop = false;
			 }else{
			 	mStepMotorService = IStepMotorService.Stub.asInterface(ServiceManager.getService("stepmotor"));
				mStepMotorService.forward(100);
//				Log.d(TAG, "---------forwardMotor: 222------" );
				stop = false;
			 }
		 }catch (RemoteException e) {
            Log.d(TAG, "RemoteException when set stepMotor");
            // re-acquire status bar service next time it is needed.
            mStepMotorService = null;
			stop = true;
        }*/
	}
	
	private void motorBackward(){
		/*try {
			 if (mStepMotorService != null) {
			 	mStepMotorService.backward(100);
//				Log.d(TAG, "---------backwardMotor:------" );
				stop = false;
			 }else{
			 	mStepMotorService = IStepMotorService.Stub.asInterface(ServiceManager.getService("stepmotor"));
				mStepMotorService.backward(100);
//				Log.d(TAG, "---------backwardMotor: 222------" );
				stop = false;
			 }
		 }catch (RemoteException e) {
            Log.d(TAG, "RemoteException when set stepMotor");
            // re-acquire status bar service next time it is needed.
            mStepMotorService = null;
			stop = true;
        }*/
	}
	
	private void motorForwardone(){
/*		try {
			 if (mStepMotorService != null) {
			 	mStepMotorService.forwardone();
//				Log.d(TAG, "---------forwardMotor:------" );
				stop = false;
			 }else{
			 	//mStepMotorService = IStepMotorService.Stub.asInterface(ServiceManager.getService("stepmotor"));
				//mStepMotorService.forwardone();
				 mStepMotorService = null;
//				Log.d(TAG, "---------forwardMotor: 222------" );
				stop = false;
			 }
		 }catch (RemoteException e) {
            Log.d(TAG, "RemoteException when set stepMotor");
            // re-acquire status bar service next time it is needed.
            mStepMotorService = null;
			stop = true;
        }*/
	}
	
	private void motorBackwardone(){
/*		try {
			 if (mStepMotorService != null) {
			 	mStepMotorService.backwardone();
//				Log.d(TAG, "---------backwardMotor:------" );
				stop = false;
			 }else{
			 	mStepMotorService = IStepMotorService.Stub.asInterface(ServiceManager.getService("stepmotor"));
				mStepMotorService.backwardone();
//				Log.d(TAG, "---------backwardMotor: 222------" );
				stop = false;
			 }
		 }catch (RemoteException e) {
            Log.d(TAG, "RemoteException when set stepMotor");
            // re-acquire status bar service next time it is needed.
            mStepMotorService = null;
			stop = true;
        }*/
	}
	
	private void motorStop(){
/*		try {
			 if (mStepMotorService != null) {
			 	mStepMotorService.stop();
//				Log.d(TAG, "---------stopMotor:------" );
				stop = true;
			 }else{
			 	mStepMotorService = IStepMotorService.Stub.asInterface(ServiceManager.getService("stepmotor"));
				mStepMotorService.stop();
//				Log.d(TAG, "---------stopMotor: 222------" );
				stop = true;
			 }
		 }catch (RemoteException e) {
            Log.d(TAG, "RemoteException when set stepmotor");
            // re-acquire status bar service next time it is needed.
            mStepMotorService = null;
        }*/
	}
	private int motorGet(){
	/*	try {
			 if (mStepMotorService != null) {
			 	return mStepMotorService.getpoint();
			 }else{
			 	mStepMotorService = IStepMotorService.Stub.asInterface(ServiceManager.getService("stepmotor"));
				return mStepMotorService.getpoint();
			 }
		 }catch (RemoteException e) {
            Log.d(TAG, "RemoteException when set stepMotor");
            // re-acquire status bar service next time it is needed.
            mStepMotorService = null;
        }
		return -1;*/
		return 0;
	}
    private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg)
		{
			if(msg.what == MSG_UPDATE_UI)
			{
				/*point = motorGet();
				Log.d(TAG, ".......point:"+point );
				if(point ==9){
					mWindowView.show(4);
				}else if(point == 1){
					mWindowView.show(3);
				}else{
				mWindowView.show(msg.what);
				}*/

				mHandler.removeMessages(MSG_CLOSE_ANIMATION);
				Log.d(TAG, ".......MSG_UPDATE_UI:"+direction+" ,"+ double_direction);
				if((direction == 0) || (double_direction == 0)){
					motorForwardone();
				}else if((direction == 1) || (double_direction == 1)){
					motorBackwardone();
				}
			}else if(msg.what == MSG_UI_FORWARD_STOP )
			{
				//Log.d(TAG, "======= forward stop ======" );
				mWindowView.forward_stop();
			}else if(msg.what == MSG_UI_BACKWARD_STOP )
			{
				//Log.d(TAG, "======= backward stop ======" );
				mWindowView.backward_stop();
			}else if(msg.what == MSG_FORWARD_ONE_STEP )
			{
				if(step != all_step){
					motorForwardone();
				}
//				mWindowView.forward();
				
			}else if(msg.what == MSG_BACKWARD_ONE_STEP )
			{
				if(step != all_step){
					motorBackwardone();
				}
				
//				mWindowView.backward();
			}else if(msg.what == MSG_ANIMATION_UI_FAST )
			{
				Log.d(TAG, ".......MSG_ANIMATION_UI_FAST:"+direction);
				if(direction == 0){
					motorForward();
				}else if(direction == 1){
					motorBackward();
				}
				//time_lock = false;
				animation_fast();
			}
			else if(msg.what == MSG_CLOSE_ANIMATION )
			{
				Log.d(TAG, "-------CLOSE ANIMATION------" );
				if(showAnimation){
					mWindowManager.removeView(mWindowView);
				}
				showAnimation = false;
			}
		}
	};
	
	private void updateAnimation(){
		point = motorGet();
//		Log.d(TAG, ".......point:"+point );
		if((direction == 0) || (double_direction == 0)){
			if(point ==9){
				step = all_step;
//				mWindowView.backward_stop();
				Log.d(TAG, ".......MSG_UI_FORWARD_STOP:" );
				Message msg = new Message();  
        		msg.what = MSG_UI_FORWARD_STOP;  
	            mHandler.sendMessage(msg); 
				
				//if(timerAnimationTask!=null){
				//	timerAnimationTask.cancel();
		        //	timerAnimationTask = null;
				//}
				//mWindowView.playAlertSound();
			}else{
				step++;
				if(step >= all_step)
					step = 0;
			}
		}else if((direction == 1) || (double_direction == 1)){
			if(point == 1){
				step = all_step;
//				mWindowView.forward_stop();
				Log.d(TAG, ".......MSG_UI_BACKWARD_STOP:" );
				Message msg = new Message();  
        		msg.what = MSG_UI_BACKWARD_STOP;  
	            mHandler.sendMessage(msg); 
				
				//if(timerAnimationTask!=null){
				//	timerAnimationTask.cancel();
		        //	timerAnimationTask = null;
				//}
				//mWindowView.playAlertSound();
			}else{
				step--;
				if(step < 0)
					step = all_step -1;
			}
		}

	}
	
	private void animation_fast(){
		if(timerAnimationTask!=null){
			timerAnimationTask.cancel();
        	timerAnimationTask = null;
		}
		
		timerAnimationTask = new TimerTask() {
             @Override
             public void run() {
//				 	Log.d(TAG, "---------animation_fast :run ------"+double_click +" ,"+key_down);
					mHandler.removeMessages(MSG_CLOSE_ANIMATION);
					if (key_down){//long press
						
						updateAnimation();
						
					}else{//up
						timerAnimationTask.cancel();
        				timerAnimationTask = null;
						
						motorStop();
					}
					Message msg = new Message();  
        			msg.what = MSG_CLOSE_ANIMATION; 
					mHandler.sendMessageDelayed(msg,showAnimation_delaytime);
             }
         }; 
		timer.schedule(timerAnimationTask,5,50);
	}
	
	private void animation_normal(){
		timerAnimationTask = new TimerTask() {
             @Override
             public void run() {
//			 		Log.d(TAG, "---------animation_normal :run ------"+double_click +" ,"+key_down);
					mHandler.removeMessages(MSG_CLOSE_ANIMATION);
					
					if (double_click){//double click
						updateAnimation();
						  
						if (double_direction == 0){
							Message msg = new Message();
							msg.what = MSG_FORWARD_ONE_STEP;  
	            			mHandler.sendMessage(msg); 
						}else if(double_direction == 1){
							Message msg = new Message();
							msg.what = MSG_BACKWARD_ONE_STEP;  
	            			mHandler.sendMessage(msg); 
	            		}
					}else{
						if(key_down){//long press
							updateAnimation();
							Message msg = new Message();  
		            		msg.what = MSG_ANIMATION_UI_FAST;  
		            		mHandler.sendMessage(msg); 
						}else{//up
							timerAnimationTask.cancel();
	        				timerAnimationTask = null;
						}
					}
					double_click = false;
					double_direction = -1;
					Message msg = new Message();  
        			msg.what = MSG_CLOSE_ANIMATION; 
					mHandler.sendMessageDelayed(msg,showAnimation_delaytime);
             }
         };
		 
		timer.schedule(timerAnimationTask,5,100);
	
	}
	
	
    private void forwardMotor() {
		if (!key_down) {
			Log.d(TAG, ">>>>>>> forwardMotor >>>>>>>>" );
			if(!showAnimation){
				mWindowManager.addView(mWindowView, wmParams);
				Log.d(TAG, ">>>>>>> forwardMotor 222 >>>>>>>>" );
				showAnimation = true;
			}

			direction = 0;
			key_down = true;
			//speed_mode = 1;
			if(key_direction == 1){
				//Log.d(TAG, ">>>>>>> forwardMotor stop >>>>>>>>  stop backward" );
				mWindowView.backward_stop();
				mWindowView.forward();
			}else{
				//Log.d(TAG, ">>>>>>> forwardMotor stop >>>>>>>>  remove delay" );
				mHandler.removeMessages(MSG_UI_FORWARD_STOP);
			}
//			mHandler.removeMessages(MSG_UI_FORWARD_STOP);
	//		long currentTimeMillis = System.currentTimeMillis();
	//		long midTime = currentTimeMillis - keyTimeMillis;
	//		Log.d(TAG, ">>>>>>> forwardMotor >>>>>>>>" +midTime);
		//	if((midTime <= 500 ) &&(key_direction == 1)){
		//		mWindowView.backward_stop();
		//	}else{
	//		mHandler.removeMessages(MSG_UI_FORWARD_STOP);
	//		}
	//		keyTimeMillis = currentTimeMillis;
			key_direction = 0;
			
			
			if(timerAnimationTask != null){//stop
//				Log.d(TAG, ">>>>>>> forwardMotor 11>>>>>>>>" );	
				double_click = true;
				double_direction = 0;
			}else{
				updateAnimation();

				mWindowView.forward();
			
				animation_normal();
				motorForwardone();
			}
		}
    }
	private void forwardStop() {
		//zzzz
//		Log.d(TAG, ">>>>>>> forwardMotor up >>>>>>>>" );
		if(key_down){//stop
			Log.d(TAG, ">>>>>>> forwardMotor stop >>>>>>>>" );
			key_down = false;
//			motorStop();
			direction = -1;
//			long currentTimeMillis = System.currentTimeMillis();
//			long midTime = currentTimeMillis - keyTimeMillis;
//			key_direction = 0;
//			Log.d(TAG, ">>>>>>> forwardMotor stop >>>>>>>>" +midTime);
//			if(midTime > 500 ){
//				mWindowView.forward_stop();
//			}else{
			
//			}
//			keyTimeMillis = currentTimeMillis;
			
			//mWindowView.forward_stop();
			Message msg = new Message();  
        	msg.what = MSG_UI_FORWARD_STOP;  
	        mHandler.sendMessageDelayed(msg,showAnimation_delaytime);
		}
	}
	
    private void backwardMotor() {
//		Log.d(TAG, "<<<<<<<<< backwardMotor <<<<<<<<<" );
		if (!key_down) {
			Log.d(TAG, "<<<<<<<<< backwardMotor <<<<<<<<<" );
	    	if(!showAnimation){
				mWindowManager.addView(mWindowView, wmParams);
				showAnimation = true;
				Log.d(TAG, ">>>>>>> backwardMotor 222 >>>>>>>>" );
			}
			direction  = 1;
			key_down = true;
			
			if(key_direction == 0){
				//Log.d(TAG, "<<<<<<<<< backwardMotor <<<<<<<<<  stop forward" );
				mWindowView.forward_stop();
				mWindowView.backward();
			}else{
				//Log.d(TAG, "<<<<<<<<< backwardMotor <<<<<<<<<   remove delay" );
				mHandler.removeMessages(MSG_UI_BACKWARD_STOP);
			}
//			mHandler.removeMessages(MSG_UI_BACKWARD_STOP);
//			long currentTimeMillis = System.currentTimeMillis();
//			long midTime = currentTimeMillis - keyTimeMillis;
//			Log.d(TAG, "<<<<<<<<< backwardMotor <<<<<<<<<" +midTime);
//			if((midTime <= 500 ) &&(key_direction == 0)){
//				mWindowView.forward_stop();
//			}else{
//			mHandler.removeMessages(MSG_UI_BACKWARD_STOP);
//			}
//			keyTimeMillis = currentTimeMillis;
			key_direction = 1;
			
			
			if(timerAnimationTask != null){//stop
			
//				Log.d(TAG, "<<<<<<<<< backwardMotor 11<<<<<<<<<" );
				double_click = true;
				double_direction = 1;
			}else{
//				Log.d(TAG, "<<<<<<<<< backwardMotor 22<<<<<<<<<" );

				updateAnimation();

				mWindowView.backward();
		

				animation_normal();
		
				motorBackwardone();
			}
		}
    }
	private void backwardStop() {
		if(key_down){//stop
			//zzzz
//			Log.d(TAG, "<<<<<<<<< backwardMotor stop <<<<<<<<<" );
//			motorStop();
			direction = -1;
			key_down = false;
			
//			long currentTimeMillis = System.currentTimeMillis();
//			long midTime = currentTimeMillis - keyTimeMillis;
//			key_direction = 1;
//			Log.d(TAG, "<<<<<<<<< backwardMotor stop <<<<<<<<<" +midTime);
//			if(midTime > 500 ){
//				mWindowView.backward_stop();
//			}else{
			
//			}
//			keyTimeMillis = currentTimeMillis;
			
			Message msg = new Message();  
        	msg.what = MSG_UI_BACKWARD_STOP;  
	        mHandler.sendMessageDelayed(msg,showAnimation_delaytime);
			//mWindowView.backward_stop();
		}
	}
	
	private void stopMotor() {
        Log.d(TAG, "stopMotor:" );
		direction = -1;
//		timerTask.cancel();
//        timerTask = null;
		stop = true;
		
		//Timer timer = new Timer();
/*		TimerTask timerTask2 = new TimerTask() {
             @Override
             public void run() {
                 mWindowView.stop();
        		 mWindowManager.removeView(mWindowView);
				 show = false;
             }
         };
		 timer.schedule(timerTask2,4000);*/
        //mWindowView.stop();
        //mWindowManager.removeView(mWindowView);
		Message msg = new Message();  
        msg.what = MSG_CLOSE_ANIMATION; 
		//mHandler.sendMessageDelayed(msg,showAnimation_delaytime);
		mHandler.sendMessageDelayed(msg,showAnimation_delaytime);
    }

}
