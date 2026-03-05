package com.twd.setting.module.universal;

import android.app.AlarmManager;
import android.content.Context;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setTheme(R.style.Theme_KapokWhite);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);
        utils = new DateTimeUtils(this);
        utils.hideSystemUI(this);
        mCurrentTimeZone = TimeZone.getDefault();
        initView();
        updateTimeRunnable.run();
    }

    private Runnable updateTimeRunnable = new Runnable() {
        @Override
        public void run() {
            getSystemTime();
            //每隔5秒更新一次时间
            timerHandler.postDelayed(this,5000);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        utils.hideSystemUI(this);
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
                // 更新系统设置
                setUseNetworkTime(isChecked);
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
    private void getSystemTime(){
        //获取当前时间和日期
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(mCurrentTimeZone);
        Date currentDate = calendar.getTime();
        //设置日期的格式
        SimpleDateFormat dateFormat = getDateFormatterByTimeZone(mCurrentTimeZone);
        String formatterDate = dateFormat.format(currentDate);

        String dayOfWeek = new SimpleDateFormat("EEEE", Locale.getDefault()).format(currentDate);
        String finalFormatterDate = dayOfWeek+"\n"+formatterDate;

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
        // 判定是否为东八区（GMT+8，兼容Asia/Shanghai、GMT+8等ID）
        boolean isEast8Zone = timeZone.getRawOffset() == TimeZone.getTimeZone("Asia/Shanghai").getRawOffset();

        // 中文环境下的格式规则
        if (Locale.CHINESE.getLanguage().equals(getResources().getConfiguration().locale.getLanguage())) {
            if (isEast8Zone) {
                // 东八区：年月日（如 2024/05/20）
                sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
            } else {
                // 非东八区：日月年（如 20/05/2024）
                sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.CHINA);
            }
        } else {
            // 英文环境（保留原有逻辑）
            sdf = new SimpleDateFormat(isEast8Zone ? "yyyy/MM/dd" : "dd/MM/yyyy", Locale.US);
        }

        // 绑定时区，避免系统默认时区干扰
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

    private void  setUseNetworkTime(boolean enabled){
        //更新系统设置
        if (Settings.Global.putInt(getContentResolver(),Settings.Global.AUTO_TIME, enabled? 1:0)){
            Log.i(TAG, "setUseNetworkTime: Network time " + (enabled ? "enabled":"disabled"));
        }else {
            Log.i(TAG, "setUseNetworkTime: Failed to update setting");
        }
    }

    private void setUse24HoursTime(boolean enabled){
        //更新系统设置
        if (Settings.System.putInt(getContentResolver(),Settings.System.TIME_12_24,enabled ? 24 : 12)){
            Log.i(TAG, "setUse24HoursTime: is24Hours + " + (enabled ? "24" : "12"));
        }else {
            Log.i(TAG, "setUse24HoursTime: Failed to set 24Hours");
        }
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
                if (when / 1000 < Integer.MAX_VALUE){
                    ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).setTime(when);
                }
                SimpleDateFormat dateFormat = getDateFormatterByTimeZone(mCurrentTimeZone);
                String formattedDate = dateFormat.format(calendar.getTime());
                date_summary.setText(formattedDate);

            } else {
                // 如果日期格式不正确，抛出异常或处理错误
                throw new IllegalArgumentException("Date format should be yyyy/MM/dd");
            }
        }catch (NumberFormatException e) {
            // 如果解析整数失败，打印错误信息
            System.out.println("Error parsing date components to integers: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            // 如果日期格式不正确，打印错误信息
            System.out.println("Error: " + e.getMessage());
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
        targetCalendar.setTimeZone(mCurrentTimeZone); // 时间也绑定时区

        // 步骤1：根据当前系统的时间格式（12/24小时制），选择对应的解析格式
        SimpleDateFormat timeParser = null;
        if (switch_24Hours.isChecked()) {
            // 24小时制：解析格式为 HH:mm（如 20:25）
            timeParser = new SimpleDateFormat("HH:mm", Locale.getDefault());
        } else {
            // 12小时制：解析格式为 hh:mm a（如 08:25 PM/AM）
            timeParser = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        }

        try {
            // 步骤2：解析用户选择的时间（自动适配12/24小时制）
            Date selectedTime = timeParser.parse(time);
            if (selectedTime == null) {
                Log.e(TAG, "onTimeSelected: 时间解析失败，格式不匹配 | 选择的时间=" + time + " | 解析格式=" + timeParser.toPattern());
                return;
            }

            // 步骤3：提取解析后的小时/分钟（统一用Calendar处理，避免12/24制换算错误）
            Calendar selectedCalendar = Calendar.getInstance();
            selectedCalendar.setTime(selectedTime);
            int targetHour = selectedCalendar.get(Calendar.HOUR_OF_DAY); // 始终取24小时制小时数（关键！）
            int targetMinute = selectedCalendar.get(Calendar.MINUTE);

            Log.i(TAG, "onTimeSelected: 解析结果 → 24小时制小时=" + targetHour + ", 分钟=" + targetMinute);

            // 步骤4：保留当前日期，仅修改小时/分钟（秒和毫秒置0，避免残留）
            Calendar currentCalendar = Calendar.getInstance();
            targetCalendar.set(Calendar.YEAR, currentCalendar.get(Calendar.YEAR));
            targetCalendar.set(Calendar.MONTH, currentCalendar.get(Calendar.MONTH));
            targetCalendar.set(Calendar.DAY_OF_MONTH, currentCalendar.get(Calendar.DAY_OF_MONTH));
            targetCalendar.set(Calendar.HOUR_OF_DAY, targetHour); // 24小时制小时，兼容所有场景
            targetCalendar.set(Calendar.MINUTE, targetMinute);
            targetCalendar.set(Calendar.SECOND, 0);
            targetCalendar.set(Calendar.MILLISECOND, 0);

            // 步骤5：设置系统时间（用AlarmManager，兼容所有Android版本）
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

            // 步骤6：更新UI显示（适配当前的12/24小时制格式）
            String timeFormatString = DateTimeUtils.getTimeFormat(this); // 复用原有工具类的格式
            DateFormat displayFormat = new SimpleDateFormat(timeFormatString, Locale.getDefault());
            String formattedTime = displayFormat.format(targetCalendar.getTime());
            time_summary.setText(formattedTime);

        } catch (ParseException e) {
            Log.e(TAG, "onTimeSelected: 时间解析异常 | 选择的时间=" + time + " | 解析格式=" + (timeParser != null ? timeParser.toPattern() : "null"), e);
            // 容错：如果解析失败，尝试反向兼容（比如用户选了24小时制格式但当前是12小时制）
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
    }
}
