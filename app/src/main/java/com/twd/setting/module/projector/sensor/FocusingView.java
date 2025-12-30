package com.twd.setting.module.projector.sensor;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.twd.setting.R;

/**
 * 温度控制
 * Created by yangle on 2016/11/29.
 */
public class FocusingView extends View {

    public static final int STATE_IDLE = 0;
    
    public static final int STATE_ZOOM_IN = 1;
    
    public static final int STATE_ZOOM_OUT = 2;
    
    public static final int MSG_UPDATE = 1;
    
    // 控件宽
    private int mWidth;
    // 控件高
    private int mHeight;
    // 旋转按钮画笔
    private Paint mRotatePaint;
    
    private BitmapFactory.Options options = new BitmapFactory.Options();
    // 按钮图片
    private Bitmap mBmpTipLogo;
    // 按钮图片阴影
    private Bitmap mBmpTip;
    
    private Bitmap mBmpTipAnim;
    
    // 抗锯齿
    private PaintFlagsDrawFilter paintFlagsDrawFilter;

    private float mRotateTipLogoAngle;
    private float mRotateTipAnimAngle;
    
    private float mCurTipLogoAngle;
    private float mCurTipAnimAngle;
    
    private float mTipLogoFactor = 0.06f;
    private float mTipAnimFactor = 0.09f;

    private long mLastTime;
    
    private int mState;
    
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE:
                    update();
                    mHandler.sendEmptyMessageDelayed(MSG_UPDATE, 100);
                    break;
            }
        }
    };
    
    public FocusingView(Context context) {
        this(context, null);
    }

    public FocusingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FocusingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //options.inSampleSize = 2;
        mBmpTipLogo = BitmapFactory.decodeResource(getResources(),
                R.drawable.manual_tip_logo, options);
        // 按钮图片阴影
        mBmpTip = BitmapFactory.decodeResource(getResources(),
                R.drawable.manual_tip, options);
        
        mBmpTipAnim = BitmapFactory.decodeResource(getResources(),
                R.drawable.manual_tip_anim, options);
        
        mRotatePaint = new Paint();
		mRotatePaint.setAntiAlias(true);  
		//函数是用来对位图进行滤波处理
		mRotatePaint.setFilterBitmap(true);

        paintFlagsDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 控件宽、高
        mWidth = mHeight = Math.min(h, w);
        /*
        if (mBmpTipLogo == null) {
            mBmpTipLogo = scale(BitmapFactory.decodeResource(getResources(), R.drawable.manual_tip_logo, options), mWidth, mHeight);
        }
      
        if (mBmpTip == null) {
            mBmpTip = scale(BitmapFactory.decodeResource(getResources(), R.drawable.manual_tip, options), mWidth, mHeight);
        }
      
        if (mBmpTipAnim == null) {
            mBmpTipAnim = scale(BitmapFactory.decodeResource(getResources(), R.drawable.manual_tip_anim, options), mWidth, mHeight);
        }*/
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawButton(canvas);
    }

    private void drawButton(Canvas canvas) {
        
        // 按钮宽高
        int bmpTipLogoWidth = mBmpTipLogo.getWidth();
        int bmpTipLogoHeight = mBmpTipLogo.getHeight();
//        Log.e("luhuiyi", "mBmpTipLogo:[ " + bmpTipLogoWidth + ", " + bmpTipLogoHeight + " ]");
        
        // 按钮阴影宽高
        int bmpTipWidth = mBmpTip.getWidth();
        int bmpTipHeight = mBmpTip.getHeight();
//        Log.e("luhuiyi", "mBmpTip:[ " + bmpTipWidth + ", " + bmpTipHeight + " ]");
        
        // 按钮阴影宽高
        int bmpTipAnimWidth = mBmpTipAnim.getWidth() ;
        int bmpTipAnimHeight = mBmpTipAnim.getHeight() ;
//        Log.e("luhuiyi", "mBmpTipAnim:[ " + bmpTipAnimWidth + ", " + bmpTipAnimHeight + " ]");

//        Log.e("luhuiyi", "View:[ " + mWidth + ", " + mHeight + " ]");
		canvas.setDrawFilter(paintFlagsDrawFilter);
        // 绘制按钮阴影
        canvas.drawBitmap(mBmpTip, (mWidth - bmpTipWidth) / 2, (mHeight - bmpTipHeight) / 2, mRotatePaint);
/*		Matrix matrix_tip = new Matrix();
        matrix_tip.setTranslate((mWidth - bmpTipLogoWidth) / 2, (mHeight - bmpTipLogoHeight) / 2);
		//matrix_tip.setTranslate(0, 0);
		matrix_tip.postScale(0.6f,0.6f);
		canvas.drawBitmap(mBmpTip, matrix_tip, mRotatePaint);*/ 

        Matrix matrix = new Matrix();
        matrix.setTranslate((mWidth - bmpTipLogoWidth) / 2, (mHeight - bmpTipLogoHeight) / 2);
        matrix.postRotate(mRotateTipLogoAngle, mWidth / 2, mHeight / 2);
		//matrix.postScale(0.6f,0.6f);
        //canvas.setDrawFilter(paintFlagsDrawFilter);
        canvas.drawBitmap(mBmpTipLogo, matrix, mRotatePaint);
        
        
        Matrix matrixTipAnim = new Matrix();
        // 设置按钮位置，移动到控件中心
        matrixTipAnim.setTranslate((mWidth - bmpTipAnimWidth) / 2, (mHeight - bmpTipAnimHeight) / 2);
        // 设置旋转角度，旋转中心为控件中心，当前也是按钮中心
        matrixTipAnim.postRotate(mRotateTipAnimAngle, mWidth / 2, mHeight / 2);
		//matrixTipAnim.postScale(0.6f,0.6f);
        
//        Log.d("luhuiyi", "mRotateTipAnimAngle: " + mRotateTipAnimAngle);
        
        //设置抗锯齿
        //canvas.setDrawFilter(paintFlagsDrawFilter);
        canvas.drawBitmap(mBmpTipAnim, matrixTipAnim, mRotatePaint);
    }
    
    public void start(int state) {
//        Log.d("luhuiyi", "start: [" + mState + " - " + state + " ]");
        if (mState != STATE_IDLE) {
            return;
        }
        mLastTime = System.currentTimeMillis();
        mState = state;
        mHandler.sendEmptyMessageDelayed(MSG_UPDATE, 50);
    }
    
    private void update() {
        long curTime = System.currentTimeMillis();
        long gapTime = curTime - mLastTime;
        mLastTime = curTime;
        
        int direction = 1;
        if (mState == STATE_ZOOM_IN) {
            direction = -1;
        }
        
        mRotateTipAnimAngle = mCurTipAnimAngle + direction * gapTime * mTipAnimFactor;
        mRotateTipLogoAngle = mCurTipLogoAngle - direction * gapTime * mTipLogoFactor;
        
        mCurTipAnimAngle = mRotateTipAnimAngle;
        mCurTipLogoAngle = mRotateTipLogoAngle;
        invalidate();
    }
    
    public void stop(int state) {
        Log.d("luhuiyi", "stop: [" + mState + " - " + state + " ]");
        if (mState == STATE_IDLE || mState != state) {
            return;
        }
        mHandler.removeMessages(MSG_UPDATE);
        update();
        mState = STATE_IDLE;
        mLastTime = -1;
        Log.d("luhuiyi", "STATE_IDLE");
    }
    
    public boolean isIdle() {
        return mState == STATE_IDLE;
    }

    public int dp2px(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
    
    public Bitmap scale(final Bitmap src, final int newWidth, final int newHeight) {
        if (isEmptyBitmap(src))
            return null;
        Bitmap ret = Bitmap.createScaledBitmap(src, newWidth, newHeight, true);
        if (!src.isRecycled() && ret != src)
            src.recycle();
        return ret;
    }
    
    private static boolean isEmptyBitmap(final Bitmap src) {
        return src == null || src.getWidth() == 0 || src.getHeight() == 0;
    }
    
    public Bitmap getBitmap(final int resId, final int maxWidth, final int maxHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        final Resources resources = getContext().getResources();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, resId, options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(resources, resId, options);
    }

    private static int calculateInSampleSize(
            final BitmapFactory.Options options, final int maxWidth,
            final int maxHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        while (height > maxHeight || width > maxWidth) {
            height >>= 1;
            width >>= 1;
            inSampleSize <<= 1;
        }
        return inSampleSize;
    }
}
