package com.twd.setting.module.universal;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.twd.setting.R;
import com.twd.setting.module.universal.dialog.DatePickerDialog;
import com.twd.setting.module.universal.dialog.TimePickerDialog;
import com.twd.setting.module.universal.dialog.TimeZoneDialog;
import com.twd.setting.module.universal.interfaces.DateSelectedInterface;
import com.twd.setting.module.universal.interfaces.OnTimeZoneSelectedListener;
import com.twd.setting.module.universal.interfaces.TimeSelectedInterface;

import java.io.DataOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeDateActivity extends AppCompatActivity implements View.OnClickListener ,
        TimeSelectedInterface, DateSelectedInterface
        , OnTimeZoneSelectedListener, View.OnFocusChangeListener {
    private final static String TAG = TimeDateActivity.class.getSimpleName();

    private DateTimeUtils utils;
    private Context context = this;
    private LinearLayout LL_Time;
    private LinearLayout LL_Date;
    private LinearLayout LL_TimeSwitch;
    private LinearLayout LL_is24HoursSwitch;
    private LinearLayout LL_timeZone;
    private TextView time_summary;
    private TextView date_summary;
    private TextView timeZone_summary;
    private TextView time_title;
    private TextView date_title;
    private TextView timeZone_title;
    private SwitchCompat switch_time;
    private SwitchCompat switch_24Hours;
    private Handler timerHandler = new Handler();

    private TimeZone mCurrentTimeZone;
    private boolean mIsNetworkTimeEnabled = false;
    // 核心：手动设置的时间戳（用于锁定时间，防止系统覆盖）
    private long mManualSetTimeMillis = 0;
    // 时间同步广播接收器（拦截系统时间更新）
    private BroadcastReceiver mTimeChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setTheme(R.style.Theme_KapokWhite);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);
        utils = new DateTimeUtils(this);
        utils.hideSystemUI(this);
        mCurrentTimeZone = TimeZone.getDefault();

        mIsNetworkTimeEnabled = utils.isNetworkTimeEnabled();
        // 注册时间变化广播（拦截系统自动同步）
        registerTimeChangeReceiver();

        initView();
        updateTimeRunnable.run();
    }

    // ========== 注册时间变化广播，拦截系统自动同步 ==========
    private void registerTimeChangeReceiver() {
        mTimeChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                // 拦截系统时间/日期/时区变化广播
                if (Intent.ACTION_TIME_CHANGED.equals(action)
                        || Intent.ACTION_DATE_CHANGED.equals(action)
                        || Intent.ACTION_TIMEZONE_CHANGED.equals(action)) {
                    // 仅在关闭网络时间且有手动设置的时间时，恢复手动时间
                    if (!mIsNetworkTimeEnabled && mManualSetTimeMillis > 0) {
                        Log.i(TAG, "拦截到系统时间变化，恢复手动设置的时间");
                        resetToManualTime();
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_DATE_CHANGED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        registerReceiver(mTimeChangeReceiver, filter);
    }

    // ========== 恢复手动设置的时间（拦截系统同步后调用） ==========
    private void resetToManualTime() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null && mManualSetTimeMillis > 0) {
            try {
                alarmManager.setTime(mManualSetTimeMillis);
                Log.i(TAG, "resetToManualTime: 恢复手动时间成功，时间戳=" + mManualSetTimeMillis);
                // 立即更新UI
                getSystemTime();
            } catch (SecurityException e) {
                Log.e(TAG, "resetToManualTime: 恢复时间失败", e);
            }
        }
    }
    private Runnable updateTimeRunnable = new Runnable() {
        @Override
        public void run() {
            getSystemTime();
            //每隔1秒更新一次时间
            timerHandler.postDelayed(this,1000);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        utils.hideSystemUI(this);
        syncNetworkTimeSetting();
    }

    // ========== 同步自动时间设置（双重保险） ==========
    private void syncNetworkTimeSetting() {
        boolean currentAutoTime = Settings.Global.getInt(getContentResolver(), Settings.Global.AUTO_TIME, 0) == 1;
        if (currentAutoTime != mIsNetworkTimeEnabled) {
            mIsNetworkTimeEnabled = currentAutoTime;
            switch_time.setChecked(mIsNetworkTimeEnabled);
            refreshSwitch();
        }
        // 关闭自动时间时，禁用自动时区
        if (!mIsNetworkTimeEnabled) {
            setUseAutoTimeZone(false);
        }
    }

    private void initView(){
        //LL
        LL_Time = findViewById(R.id.LL_Time);
        LL_Date = findViewById(R.id.LL_Date);
        LL_TimeSwitch = findViewById(R.id.LL_TimeSwitch);
        LL_is24HoursSwitch = findViewById(R.id.LL_is24Hours);
        LL_timeZone = findViewById(R.id.LL_TImeZone);
        //summary
        time_summary = findViewById(R.id.time_summary);date_summary = findViewById(R.id.date_summary);timeZone_summary = findViewById(R.id.timeZone_summary);
        //title
        time_title = findViewById(R.id.time_title);date_title = findViewById(R.id.date_title);timeZone_title = findViewById(R.id.timeZone_summary);
        //switch
        switch_time = findViewById(R.id.switch_auto);switch_24Hours = findViewById(R.id.switch_24Hours);
        // 初始化开关状态
        switch_time.setChecked(utils.isNetworkTimeEnabled());
        switch_24Hours.setChecked(utils.is24HoursEnabled());
        //clicklistener
        LL_Time.setOnClickListener(this);
        LL_TimeSwitch.setOnClickListener(this);
        LL_is24HoursSwitch.setOnClickListener(this);
        LL_Date.setOnClickListener(this);
        LL_timeZone.setOnClickListener(this);
        LL_TimeSwitch.requestFocus();

        refreshSwitch();

        switch_time.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 更新状态记录
                mIsNetworkTimeEnabled = isChecked;
                // 1. 更新系统设置（核心）
                setUseNetworkTime(isChecked);
                // 2. 同步自动时区
                setUseAutoTimeZone(isChecked);
                // 3. 开启自动时间时，清空手动时间锁定
                if (isChecked) {
                    mManualSetTimeMillis = 0;
                }
                // 4. 更新UI
                refreshSwitch();
            }
        });

        switch_24Hours.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //更改系统设置
                setUse24HoursTime(isChecked);
            }
        });
    }

    // 禁用/启用自动时区（跟随网络时间开关）
    private void setUseAutoTimeZone(boolean enabled) {
        try {
            Settings.Global.putInt(getContentResolver(), Settings.Global.AUTO_TIME_ZONE, enabled ? 1 : 0);
            Settings.Global.putString(getContentResolver(), "timezone.auto", enabled ? "1" : "0");
            Log.i(TAG, "setUseAutoTimeZone: 自动时区" + (enabled ? "启用" : "禁用"));
        } catch (SecurityException e) {
            Log.e(TAG, "setUseAutoTimeZone: 权限异常", e);
        }
    }
    private void getSystemTime(){
        //获取当前时间和日期
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(mCurrentTimeZone);
        Date currentDate = calendar.getTime();
        //设置日期的格式
        SimpleDateFormat dateFormat = getDateFormatterByTimeZone(mCurrentTimeZone);
        String formatterDate = dateFormat.format(currentDate);



        String timeFormatString = DateTimeUtils.getTimeFormat(this);
        //设置时间的格式
        DateFormat timeFormat = new SimpleDateFormat(timeFormatString);
        timeFormat.setTimeZone(mCurrentTimeZone); // 时间也绑定时区
        String formatterTime = timeFormat.format(currentDate);

        // 获取当前时区的完整名称
        String timeZoneDisplayName = utils.getTimeZoneList().get(mCurrentTimeZone.getID());

        // 计算时区偏移量，并格式化为"+HH:mm"的形式
        String timeZoneInfo = timeZoneDisplayName +"\n"+ " GMT " +
                String.format("%s%02d:%02d",
                        mCurrentTimeZone.getRawOffset() >= 0 ? "+" : "-",
                        Math.abs(mCurrentTimeZone.getRawOffset()) / 3600000,
                        Math.abs(mCurrentTimeZone.getRawOffset() % 3600000) / 60000);
        //在TextView上更新日期和时间
        time_summary.setText(formatterTime);
        date_summary.setText(formatterDate);
        timeZone_summary.setText(timeZoneInfo);
    }

    private SimpleDateFormat getDateFormatterByTimeZone(TimeZone timeZone) {
        SimpleDateFormat sdf;
        boolean isEast8Zone = timeZone.getRawOffset() == TimeZone.getTimeZone("Asia/Shanghai").getRawOffset();

        if (Locale.CHINESE.getLanguage().equals(getResources().getConfiguration().locale.getLanguage())) {
            sdf = new SimpleDateFormat(isEast8Zone ? "yyyy/MM/dd" : "dd/MM/yyyy", Locale.CHINA);
        } else {
            sdf = new SimpleDateFormat(isEast8Zone ? "yyyy/MM/dd" : "dd/MM/yyyy", Locale.US);
        }
        sdf.setTimeZone(timeZone);
        return sdf;
    }
    private void refreshSwitch(){
        if (switch_time.isChecked()){
            time_title.setTextColor(getResources().getColor(R.color.auto_time_checked)); time_summary.setTextColor(getResources().getColor(R.color.auto_time_checked));
            date_title.setTextColor(getResources().getColor(R.color.auto_time_checked));  date_summary.setTextColor(getResources().getColor(R.color.auto_time_checked));
            LL_Time.setFocusable(false); LL_Date.setFocusable(false);
            LL_Time.setOnFocusChangeListener(null); LL_Date.setOnFocusChangeListener(null);
        }else {
            time_title.setTextColor(getResources().getColor(R.color.black)); time_summary.setTextColor(getResources().getColor(R.color.black));
            date_title.setTextColor(getResources().getColor(R.color.black));  date_summary.setTextColor(getResources().getColor(R.color.black));
            LL_Time.setFocusable(true); LL_Date.setFocusable(true);
            LL_Time.setOnFocusChangeListener(this); LL_Date.setOnFocusChangeListener(this);
        }
    }

    private void setUseNetworkTime(boolean enabled){
        // 强制设置自动时间开关（系统应用有权限）
        Settings.Global.putInt(getContentResolver(),Settings.Global.AUTO_TIME, enabled? 1:0);
        Settings.Global.putString(getContentResolver(), "auto_time", enabled ? "1" : "0");
        Log.i(TAG, "setUseNetworkTime: 网络时间" + (enabled ? "启用" : "禁用"));
    }

    private void setUse24HoursTime(boolean enabled){
        Settings.System.putInt(getContentResolver(),Settings.System.TIME_12_24,enabled ? 24 : 12);
        Log.i(TAG, "setUse24HoursTime: 24小时制" + (enabled ? "启用" : "禁用"));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.LL_Time){
            showTimeDialog();
        }else if (v.getId() == R.id.LL_TimeSwitch){
            switch_time.setChecked(!switch_time.isChecked());
            refreshSwitch();
        } else if (v.getId() == R.id.LL_is24Hours) {
            switch_24Hours.setChecked(!switch_24Hours.isChecked());
        } else if (v.getId() == R.id.LL_Date) {
            showDateDialog();
        } else if (v.getId() == R.id.LL_TImeZone) {
            showTimeZoneDialog();
        }
    }

    private void showTimeDialog(){
        TimePickerDialog dialog = new TimePickerDialog(this,this);
        dialog.show();
    }

    private void showDateDialog(){
        DatePickerDialog dialog = new DatePickerDialog(this,this);
        dialog.show();
    }

    private void showTimeZoneDialog(){
        TimeZoneDialog dialog = new TimeZoneDialog(this,this);
        dialog.show();
    }
    @Override
    public void onDateSelected(String date) {
        try {
            // 使用"/"分割日期字符串
            String[] parts = date.split("/");
            if (parts.length == 3) {
                // 分别解析年、月、日
                int year = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                int day = Integer.parseInt(parts[2]);
                Calendar calendar =  Calendar.getInstance();
                calendar.setTimeZone(mCurrentTimeZone);
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month-1);
                calendar.set(Calendar.DAY_OF_MONTH,day);

                long when = calendar.getTimeInMillis();
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                if (alarmManager != null) {
                    alarmManager.setTime(when);
                    Log.i(TAG, "onDateSelected: 手动设置日期成功 → " + date);
                }
                // 2. 缓存手动设置的时间戳（核心！用于拦截后恢复）
                mManualSetTimeMillis = when;

                // 3. 立即更新UI
                SimpleDateFormat dateFormat = getDateFormatterByTimeZone(mCurrentTimeZone);
                String formattedDate = dateFormat.format(calendar.getTime());
                date_summary.setText(formattedDate);

            } else {
                // 如果日期格式不正确，抛出异常或处理错误
                throw new IllegalArgumentException("Date format should be yyyy/MM/dd");
            }
        }catch (Exception e) {
            Log.e(TAG, "onDateSelected: 设置日期异常", e);
        }
    }
    @Override
    public void onTimeZoneSelected(String timeZoneId) {
        mCurrentTimeZone = TimeZone.getTimeZone(timeZoneId);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(mCurrentTimeZone);
        // 获取当前时区的完整名称
        String timeZoneDisplayName = utils.getTimeZoneList().get(timeZoneId);

        // 计算时区偏移量，并格式化为"+HH:mm"的形式
        String timeZoneInfo = timeZoneDisplayName +"\n"+ " GMT " +
                String.format("%s%02d:%02d",
                        mCurrentTimeZone.getRawOffset() >= 0 ? "+" : "-",
                        Math.abs(mCurrentTimeZone.getRawOffset()) / 3600000,
                        Math.abs(mCurrentTimeZone.getRawOffset() % 3600000) / 60000);
        timeZone_summary.setText(timeZoneInfo);

        getSystemTime();
    }
    @Override
    public void onTimeSelected(String time) {
        Log.i(TAG, "onTimeSelected: 选择的时间=" + time + " | 当前是否24小时制=" + switch_24Hours.isChecked());
        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.setTimeZone(mCurrentTimeZone);

        SimpleDateFormat timeParser = switch_24Hours.isChecked()
                ? new SimpleDateFormat("HH:mm", Locale.getDefault())
                : new SimpleDateFormat("hh:mm a", Locale.getDefault());

        try {
            Date selectedTime = timeParser.parse(time);
            if (selectedTime == null) {
                Log.e(TAG, "onTimeSelected: 时间解析失败，格式不匹配 | 选择的时间=" + time + " | 解析格式=" + timeParser.toPattern());
                return;
            }

            Calendar selectedCalendar = Calendar.getInstance();
            selectedCalendar.setTime(selectedTime);
            int targetHour = selectedCalendar.get(Calendar.HOUR_OF_DAY);
            int targetMinute = selectedCalendar.get(Calendar.MINUTE);

            Log.i(TAG, "onTimeSelected: 解析结果 → 24小时制小时=" + targetHour + ", 分钟=" + targetMinute);

            Calendar currentCalendar = Calendar.getInstance();
            targetCalendar.set(Calendar.YEAR, currentCalendar.get(Calendar.YEAR));
            targetCalendar.set(Calendar.MONTH, currentCalendar.get(Calendar.MONTH));
            targetCalendar.set(Calendar.DAY_OF_MONTH, currentCalendar.get(Calendar.DAY_OF_MONTH));
            targetCalendar.set(Calendar.HOUR_OF_DAY, targetHour);
            targetCalendar.set(Calendar.MINUTE, targetMinute);
            targetCalendar.set(Calendar.SECOND, 0);
            targetCalendar.set(Calendar.MILLISECOND, 0);

            long newTimeInMillis = targetCalendar.getTimeInMillis();
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                try {
                    alarmManager.setTime(newTimeInMillis);
                    Log.i(TAG, "onTimeSelected: 系统时间设置成功 | 时间戳=" + newTimeInMillis);
                } catch (SecurityException e) {
                    Log.e(TAG, "onTimeSelected: 设置系统时间失败！缺少SET_TIME权限或非系统应用", e);
                    return;
                }
            } else {
                Log.e(TAG, "onTimeSelected: 获取AlarmManager失败");
                return;
            }
// 2. 缓存手动设置的时间戳（核心！用于拦截后恢复）
            mManualSetTimeMillis = newTimeInMillis;
            // 步骤6：更新UI显示（适配当前的12/24小时制格式）
            String timeFormatString = DateTimeUtils.getTimeFormat(this); // 复用原有工具类的格式
            DateFormat displayFormat = new SimpleDateFormat(timeFormatString, Locale.getDefault());
            displayFormat.setTimeZone(mCurrentTimeZone);
            time_summary.setText(displayFormat.format(targetCalendar.getTime()));

        } catch (ParseException e) {
            Log.e(TAG, "onTimeSelected: 解析异常", e);
        }
    }
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int color = hasFocus ? R.color.text_red_new : R.color.black;
        setTextViewColor(v, color);
    }

    private void setTextViewColor(View view, int color) {
        if (view.getId() == R.id.LL_Time) {
            time_title.setTextColor(getResources().getColor(color));
            time_summary.setTextColor(getResources().getColor(color));
        } else if (view.getId() == R.id.LL_Date) {
            date_title.setTextColor(getResources().getColor(color));
            date_summary.setTextColor(getResources().getColor(color));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacks(updateTimeRunnable);
        // 注销广播接收器
        if (mTimeChangeReceiver != null) {
            unregisterReceiver(mTimeChangeReceiver);
        }
    }
}
