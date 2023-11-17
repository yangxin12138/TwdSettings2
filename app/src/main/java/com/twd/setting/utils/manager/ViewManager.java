package com.twd.setting.utils.manager;

import static android.view.WindowManager.LayoutParams.TYPE_PHONE;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
//import com.twd.setting.utils.HLog;
import com.twd.setting.widgets.BaseView;
import com.twd.setting.widgets.DialogView;

import java.lang.ref.WeakReference;
import java.util.Stack;

public class ViewManager {
    private static final String TAG = "ViewManager";
    private static final int VIEW_AUTO_EXIT_TIME = 30000;
    private static ViewManager sViewManager;
    private boolean mAutoExitTimerEnable = true;
    private WeakReference<Context> mContextWeakRef;
    private WindowManager.LayoutParams mLayoutParams = new WindowManager.LayoutParams();
    private Handler mRemoveHandler = new Handler();
    private Runnable mRemoveRunnable = new Runnable() {
        public void run() {
            Log.d(TAG, "移除所有view" + mAutoExitTimerEnable);
            if (mAutoExitTimerEnable) {
                clearViews();
                return;
            }
            mRemoveHandler.postDelayed(this, VIEW_AUTO_EXIT_TIME);
        }
    };
    private Stack<BaseView> mViews = new Stack();
    private WindowManager mWindowManager;

    private ViewManager(Context paramContext) {
        mWindowManager = ((WindowManager) paramContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE));
        mLayoutParams.gravity = Gravity.CENTER;
        mLayoutParams.type = LayoutParams.TYPE_PHONE;//2002;
        mLayoutParams.flags = LayoutParams.FLAG_HARDWARE_ACCELERATED | LayoutParams.FLAG_ALT_FOCUSABLE_IM;//16908288;
        mLayoutParams.format = 1;
    }

    public static ViewManager getInstance(Context paramContext) {
        if (sViewManager == null) {
            try {
                if (sViewManager == null) {
                    sViewManager = new ViewManager(paramContext);
                }
            } finally {
            }
        }
        sViewManager.init(paramContext);
        return sViewManager;
    }

    private void init(Context paramContext) {
        this.mContextWeakRef = new WeakReference(paramContext);
    }

    public void addDialogView(DialogView paramDialogView) {
        addView(paramDialogView, paramDialogView.mLayoutParams);
    }

    public void addView(BaseView paramBaseView) {
        addView(paramBaseView, mLayoutParams);
    }

    public void addView(BaseView paramBaseView, WindowManager.LayoutParams paramLayoutParams) {
        if (paramBaseView.getParent() != null) {
            return;
        }
        WindowManager.LayoutParams localLayoutParams = paramLayoutParams;
        if (paramLayoutParams == null) {
            localLayoutParams = mLayoutParams;
        }
        mWindowManager.addView(paramBaseView, localLayoutParams);

        Log.d(TAG, "addView mViews.size=" + mViews.size());
        mViews.add(paramBaseView);
        paramBaseView.onViewAdd();
    }

    public void clearViews() {
        stopTimer();
        while (!mViews.empty()) {
            BaseView localBaseView = (BaseView)this.mViews.peek();
            localBaseView.onViewRemove();
            remove(localBaseView);
        }
    }

    public boolean getAutoExitTimerEnable() {
        return mAutoExitTimerEnable;
    }

    public View getTopView() {
        if (!mViews.isEmpty()) {
            return mViews.peek();
        }
        return null;
    }

    public void remove(View paramView) {
        if (mViews.empty()) {
            Log.d("ViewManager", paramView.getClass().getName() + "的remove逻辑有问题");
        }
        try {
            if (mViews.peek() == paramView) {
                mWindowManager.removeView(mViews.pop());
            } else {
                mWindowManager.removeView(paramView);
            }
        } catch (IllegalArgumentException e) {
            Log.d("ViewManager", paramView.getClass().getName() + "的remove失败");
        }


    }

    public void removeTopView() {
        if (!mViews.isEmpty()) {
            remove(mViews.peek());
        }
    }

    public void resetTimer() {
        stopTimer();
        mRemoveHandler.postDelayed(mRemoveRunnable, VIEW_AUTO_EXIT_TIME);
    }

    public void setAutoExitTimerEnable(boolean paramBoolean) {
        mAutoExitTimerEnable = paramBoolean;
    }

    public void stopTimer() {
        if (mRemoveRunnable != null) {
            mRemoveHandler.removeCallbacks(mRemoveRunnable);
        }
    }
}