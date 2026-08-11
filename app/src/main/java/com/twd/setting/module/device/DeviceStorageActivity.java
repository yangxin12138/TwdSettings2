package com.twd.setting.module.device;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.twd.setting.R;
import com.twd.setting.utils.SystemPropertiesUtils;
import com.twd.setting.utils.TwdUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DeviceStorageActivity extends AppCompatActivity {
    private static final String TAG = DeviceStorageActivity.class.getName();
    private TextView storage_total ;

    //String theme_code = SystemPropertiesUtils.getPropertyColor("persist.sys.background_blue","0");
    String theme_code = "1";
    TypedArray typedArray;
    TwdUtils twdUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        switch (theme_code){
            case "0": //冰激蓝
                this.setTheme(R.style.Theme_IceBlue);
                break;
            case "1": //木棉白
                this.setTheme(R.style.Theme_KapokWhite);
                break;
            case "2": //星空蓝
                this.setTheme(R.style.Theme_StarBlue);
                break;
        }
        typedArray = obtainStyledAttributes(new int[]{
                R.attr.textColor
        });
        super.onCreate(savedInstanceState);
        twdUtils = new TwdUtils();
        twdUtils.hideSystemUI(this);
        setContentView(R.layout.activity_device_storage);
        initView();
    }

    private double InternationalReplace(String rom){
        double Rom_Long;

        if (Locale.getDefault().getLanguage().equals("fr") ||
                Locale.getDefault().getLanguage().equals("de")){
            Log.i(TAG, "InternationalReplace: 走逗号格式化");
            Rom_Long = Double.parseDouble(rom.replace(",","."));
        }else {
            Log.i(TAG, "InternationalReplace: Rom_Long = " + Double.parseDouble(rom));
            Rom_Long = Double.parseDouble(rom);
        }
        return Rom_Long;
    }

    private String formatNumberWithCommas(String number){
        Locale defaultLocale  = Locale.getDefault();
        try {
            NumberFormat numberFormat = NumberFormat.getInstance(defaultLocale);
            double romValue = numberFormat.parse(number).doubleValue();
            Log.i(TAG, "formatNumberWithCommas: romValue = " + romValue);
            return String.valueOf(romValue);
        } catch (Exception e) {
            e.printStackTrace();
            return "0.00"; // 或者您可以根据需要设置一个默认值
        }
    }

    private void initView(){
        storage_total = findViewById(R.id.storage_total);

        //总容量
        String totalRom = SystemPropertiesUtils.readSystemProp("STORAGE_SIMPLE_SYSDATA");
        Log.i(TAG, "initView: totalRom = "+totalRom);
        storage_total.setText(getString(R.string.device_storage_total)+":"+totalRom);

    }

    //RAM内存大小, 返回1GB/2GB/3GB/4GB/8G/16G
    public static String getTotalRam(){
        String path = "/proc/meminfo";
        String ramMemorySize = null;
        int totalRam = 0 ;
        try{
            FileReader fileReader = new FileReader(path);
            BufferedReader br = new BufferedReader(fileReader, 4096);
            ramMemorySize = br.readLine().split("\\s+")[1];
            br.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        if(ramMemorySize != null){
            totalRam = (int)Math.ceil((new Float(Float.valueOf(ramMemorySize) / (1024 * 1024)).doubleValue()));
        }

        return totalRam + "GB";
    }

    //ROM内存大小，返回 64G/128G/256G/512G
    /*
     * 总容量*/
    private static String getTotalRom() {
        File dataDir = Environment.getDataDirectory();
        StatFs stat = new StatFs(dataDir.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        long size = totalBlocks * blockSize;
        long GB = 1024 * 1024 * 1024;
        final long[] deviceRomMemoryMap = {2*GB, 4*GB, 8*GB, 16*GB, 32*GB, 64*GB, 128*GB, 256*GB, 512*GB, 1024*GB, 2048*GB};
        String[] displayRomSize = {"2GB","4GB","8GB","16GB","32GB","64GB","128GB","256GB","512GB","1024GB","2048GB"};
        int i;
        for(i = 0 ; i < deviceRomMemoryMap.length; i++) {
            if(size <= deviceRomMemoryMap[i]) {
                break;
            }
            if(i == deviceRomMemoryMap.length) {
                i--;
            }
        }
        return displayRomSize[i];
    }

}
