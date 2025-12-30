package com.twd.setting.module.projector.sensor;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ImageView;
import android.graphics.drawable.AnimationDrawable;

import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import android.graphics.Matrix;

import com.twd.setting.R;



public class GSensorAnimationLayout extends RelativeLayout {
	private static final String TAG = GSensorAnimationLayout.class.getSimpleName();
    private ImageView view;

	
	private RotateAnimation animation;
	private Interpolator  interpolator;
    private AnimationDrawable animationDrawable;
	public static int step = 0;
	private static final Uri WARNING_SOUND_URI = Uri
            .parse("file:///system/media/audio/ui/VideoRecord.ogg");
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
    
    public GSensorAnimationLayout(Context context) {
        super(context);
        //Log.d(TAG, "AlertDialogLayout");
		mContext = context;
        LayoutInflater.from(context).inflate(R.layout.gsensor_layout, this);
        
        view = (ImageView)findViewById(R.id.view_gsensor);
		interpolator = new LinearInterpolator();

    }
	
	protected void showZ() {
		animation = new RotateAnimation(step*30, step*30-30, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);//new RotateAnimation(0,360);
		animation.setDuration(0);

		animation.setInterpolator(interpolator);
		animation.setRepeatCount(0);
		animation.setFillEnabled(true);
		animation.setFillBefore(false);
		animation.setFillAfter(true);

		//view.setVisibility(View.VISIBLE);
		view.startAnimation(animation);
		//view.setVisibility(View.INVISIBLE);

	}

	protected void hideZ() {
		view.setVisibility(View.INVISIBLE);
	}

    
}
