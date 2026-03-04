package com.twd.setting.module.universal.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.twd.setting.R;
import com.twd.setting.module.universal.DateTimeUtils;
import com.twd.setting.module.universal.TimeDateActivity;

import com.twd.setting.module.universal.interfaces.TimeSelectedInterface;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * @Author:Yangxin
 * @Description:
 * @time: Create in 15:10 2024/6/21
 */
public class TimePickerDialog extends Dialog implements View.OnClickListener, View.OnFocusChangeListener{
    String selectTime;
    private TimePicker timePicker;
    private Context mContext;
    private final static String TAG = "TimePickerDialog";
    private TimeSelectedInterface timeSelectedInterface;
    private DateTimeUtils utils;

    public TimePickerDialog(@NonNull Context context, TimeSelectedInterface timeSelectedInterface) {
        super(context);
        mContext = context;
        this.timeSelectedInterface = timeSelectedInterface;
    }
    public TimePickerDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    protected TimePickerDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_time_picker);
        timePicker = findViewById(R.id.timePicker);
        utils = new DateTimeUtils(mContext);
        timePicker.setIs24HourView(utils.is24HoursEnabled());
        if (utils.is24HoursEnabled()){
            timePicker.setCurrentHour(new GregorianCalendar().get(Calendar.HOUR_OF_DAY));
        }else {
            timePicker.setCurrentHour(new GregorianCalendar().get(Calendar.HOUR));
            timePicker.setCurrentMinute(new GregorianCalendar().get(Calendar.MINUTE));
        }
        View hourView = timePicker.findViewById(Resources.getSystem().getIdentifier("hour", "id", "android"));
        View minuteView = timePicker.findViewById(Resources.getSystem().getIdentifier("minute", "id", "android"));
        View amPmView = timePicker.findViewById(Resources.getSystem().getIdentifier("amPm", "id", "android"));
        timePicker.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        selectTime = timePicker.is24HourView() ? String.format("%02d:%02d", timePicker.getCurrentHour(), timePicker.getCurrentMinute())
                : String.format("%02d:%02d AM", timePicker.getCurrentHour() % 12 == 0 ? 12 : timePicker.getCurrentHour() % 12, timePicker.getCurrentMinute());
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                selectTime = String.format("%02d:%02d", hourOfDay, minute);
                if(view.is24HourView()){
                    //24小时制
                    Log.i(TAG, "onTimeChanged: 24-hour format time is " + selectTime);
                    selectTime = String.format("%02d:%02d", hourOfDay, minute);
                }else {
                    // 12小时制，需要获取AM/PM
                    int amPm = view.getCurrentHour() >= 12 ? Calendar.PM : Calendar.AM;
                    selectTime = String.format("%02d:%02d %s", hourOfDay % 12 == 0 ? 12 : hourOfDay % 12, minute, amPm == Calendar.PM ? "PM" : "AM");
                    Log.i(TAG, "onTimeChanged: 12-hour format time is " + selectTime);
                }
            }
        });
        timePicker.post(new Runnable() {
            @Override
            public void run() {
                timePicker.requestFocus();
            }
        });
        hourView.setOnClickListener(this); minuteView.setOnClickListener(this); amPmView.setOnClickListener(this);
        hourView.setOnFocusChangeListener(this); minuteView.setOnFocusChangeListener(this); amPmView.setOnFocusChangeListener(this);
    }
    @Override
    public void onClick(View v) {
        Log.i(TAG, "onClick: selectTime = " + selectTime);
        if (timeSelectedInterface != null) {
            timeSelectedInterface.onTimeSelected(selectTime);
        }
        dismiss();
        Intent restartIntent = new Intent(mContext.getApplicationContext(), TimeDateActivity.class);
        restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mContext.startActivity(restartIntent);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus){
            v.setBackgroundResource(R.drawable.dialog_item_background);
        }else {
            v.setBackgroundResource(0);
        }
    }
}
