package com.twd.setting.module.projector.sensor;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.ImageView;
import android.graphics.drawable.AnimationDrawable;

import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import com.twd.setting.R;

public class MotorAnimationLayout extends RelativeLayout {
	private static final String TAG = MotorAnimationLayout.class.getSimpleName();
    private FocusingView mTempControl;

	
    private AnimationDrawable animationDrawable;
	public static int step = 0;
	private static final Uri WARNING_SOUND_URI = Uri
            .parse("file:///product/media/audio/ui/VideoRecord.ogg");
	private Ringtone mRingtone;
	private Context mContext;
	
	int [] images = new int[]{ 
            R.mipmap.motor0,
            R.mipmap.motor1,
            R.mipmap.motor2,
			R.mipmap.motor3,
            R.mipmap.motor4,
            R.mipmap.motor16
    } ;
    
    public MotorAnimationLayout(Context context) {
        super(context);
        Log.d(TAG, "AlertDialogLayout");
		mContext = context;
        LayoutInflater.from(context).inflate(R.layout.activity_focus, this);
        
        mTempControl = (FocusingView)findViewById(R.id.temp_control);
        //Log.d(TAG, "===========MotorAnimationLayout:" );
        //animationDrawable=(AnimationDrawable) getResources().getDrawable(R.drawable.frame_animation);
        
        //view.setBackgroundDrawable(animationDrawable);
    }
	
	protected void show(int step,int direction) {

	}
	

    protected void forward() {
		//Log.d(TAG, "===========forward step:"+step );
		mTempControl.start(FocusingView.STATE_ZOOM_OUT);

    }
	
	protected void backward() {
		//Log.d(TAG, "===========backward step:"+step );
		mTempControl.start(FocusingView.STATE_ZOOM_IN);

    }
 
    protected void forward_stop() {
		//Log.d(TAG, "==========stop" );
        mTempControl.stop(FocusingView.STATE_ZOOM_OUT);
    }
	protected void backward_stop() {
		//Log.d(TAG, "==========stop" );
        mTempControl.stop(FocusingView.STATE_ZOOM_IN);
    }
	
	protected void playAlertSound() {

    	mRingtone = RingtoneManager.getRingtone(mContext, WARNING_SOUND_URI);
        if (mRingtone != null) {
        	mRingtone.setStreamType(AudioManager.STREAM_SYSTEM);
            mRingtone.play();
        }
    }

    protected void stopRingtone() {
        if (mRingtone != null) {
            mRingtone.stop();
        }
    }
    
}
