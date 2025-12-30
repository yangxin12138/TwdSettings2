package com.twd.setting.module.projector.sensor;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import com.twd.setting.module.projector.sensor.FocusingView;

import com.twd.setting.R;

/**
 * 主页
 * Created by yangle on 2016/11/19.
 * <p>
 * Website：http://www.yangle.tech
 * GitHub：https://github.com/alidili
 * CSDN：http://blog.csdn.net/kong_gu_you_lan
 * JianShu：http://www.jianshu.com/u/34ece31cd6eb
 */

public class FocusActivity extends Activity {
    
//    private static final int TIME_OUT_END_FOCUSING = 3000;
//    
    private static final int MSG_EXIT = 1;
//    private static final int MSG_ZOOM_OUT = 2;
//
//    private Button mBtnZoomIn;
//    private Button mBtnZoomOut;
    
    private FocusingView mTempControl;
    
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_EXIT:
                    finish();
                    break;
            }
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus);

        mTempControl = (FocusingView)findViewById(R.id.temp_control);

//        mBtnZoomIn = (Button)findViewById(R.id.zoom_in);
//        mBtnZoomIn.setOnTouchListener(new MyOnTouchListener(true));
//        mBtnZoomOut = (Button)findViewById(R.id.zoom_out);
//        mBtnZoomOut.setOnTouchListener(new MyOnTouchListener(false));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_F2) {
            mTempControl.start(FocusingView.STATE_ZOOM_IN);
            mHandler.removeMessages(MSG_EXIT);
        } else if (keyCode == KeyEvent.KEYCODE_F3) {
            mTempControl.start(FocusingView.STATE_ZOOM_OUT);
            mHandler.removeMessages(MSG_EXIT);
        }
        return super.onKeyDown(keyCode, event);
        
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_F2) {
            mTempControl.stop(FocusingView.STATE_ZOOM_IN);
            timeOutExit();
        } else if (keyCode == KeyEvent.KEYCODE_F3) {
            mTempControl.stop(FocusingView.STATE_ZOOM_OUT);
            timeOutExit();
        }
        return super.onKeyUp(keyCode, event);
    }
    
    private void timeOutExit() {
        if (mTempControl.isIdle()) {
            mHandler.removeMessages(MSG_EXIT);
            mHandler.sendEmptyMessageDelayed(MSG_EXIT, 3000);
        }
    }
    
//    private class MyOnTouchListener implements View.OnTouchListener {
//        
//        private boolean mIsZoomIn;
//        
//        public MyOnTouchListener(boolean isZoomIn) {
//            mIsZoomIn = isZoomIn;
//        }
//
//        @Override
//        public boolean onTouch(View v, MotionEvent event) {
//            return mTempControl.touchZoom(event.getAction(), mIsZoomIn ? 1 : 2);
//        }
//        
//    }
}
