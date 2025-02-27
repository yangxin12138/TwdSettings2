package com.twd.setting.module.universal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.twd.setting.R;
import com.twd.setting.bean.LanguageBean;
import com.twd.setting.utils.SystemPropertiesUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UniversalLanguageActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = UniversalLanguageActivity.class.getName();

    private ListView language_listView;
    List<LanguageBean> languageBeans = new ArrayList<>();
    private final Context context = this;

    LanguageItemAdapter languageItemAdapter ;
    //String theme_code = SystemPropertiesUtils.getPropertyColor("persist.sys.background_blue","0");
    String theme_code = "0";
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_universal_language);
        initView();
        Locale currentLocale = getResources().getConfiguration().locale;
        String currentLanguageCode = currentLocale.getLanguage()+"_"+currentLocale.getCountry();
        String currentLanguageName = currentLocale.getDisplayName();
        Log.i(TAG, "onCreate: currentLanguageCode = " + currentLanguageCode + "Name = " + currentLanguageName);
    }

    private void initView(){
        language_listView = findViewById(R.id.universal_language_listView);

        //TODO:检测当前系统支持的所有语言
        Locale[] availableLocales = Locale.getAvailableLocales();
        List<String> languageList = new ArrayList<>();

        //获取当前的语言code
        Locale currentLocale = getResources().getConfiguration().locale;
        String currentLanguageCode = currentLocale.getLanguage()+"_"+currentLocale.getCountry();
        Log.i(TAG, "initView: 当前语言是：" + currentLanguageCode);
        //initView: 当前语言是：en_US
        for (Locale locale : availableLocales) {
            String language =locale.getDisplayName(locale)+"_"+locale.getLanguage()+"_"+locale.getCountry();
            String languageCode = locale.getLanguage()+"_"+locale.getCountry();
            if (!language.isEmpty() && !languageList.contains(language)) {
                Log.i(TAG, "initView: " + language);
                languageList.add(language);
            }
            LanguageBean languageBean = null;
            Map<String,String> languageMap = new HashMap<>();
            languageMap.put("zh_CN","简体中文"); //简体中文
            languageMap.put("zh_TW","繁體中文"); //繁体中文
            languageMap.put("en_US","English"); //英语
            languageMap.put("ja_JP","日本語"); //日语
            languageMap.put("ko_KR","한국어"); //韩语-韩国
            languageMap.put("th_TH","ไทย"); //泰语-泰国
            languageMap.put("hi_IN","हिन्दी"); //印地语-印度
            languageMap.put("fr_FR","Français"); //法语-法国
            languageMap.put("de_DE","Deutsch"); //德语-德国
            languageMap.put("it_IT","Italiano"); //意大利语-意大利
            languageMap.put("ru_RU","Pусский"); //俄语-俄罗斯
            languageMap.put("es_ES","Español"); //西班牙语-西班牙
            languageMap.put("pt_PT","Português"); //葡萄牙语-葡萄牙
            languageMap.put("ar_SA","العربية");//阿拉伯语-沙特阿拉伯
            languageMap.put("fa_IR","فارسی");//波斯语-伊朗
            languageMap.put("tr_TR","Türkçe");//土耳其语-土耳其
            languageMap.put("pl_PL","Polski");//波兰语-波兰------
            languageMap.put("af_ZA","Afrikaans");//南非荷兰语
            languageMap.put("cs_CZ","Čeština");//捷克语
            languageMap.put("da_DK","Dansk");//丹麦语
            languageMap.put("fil_PH","Filipino");//菲律宾语
            languageMap.put("hr_HR","Hrvatski");//克罗地亚语
            languageMap.put("id_ID","Indonesia");//印度尼西亚语
            languageMap.put("zu_ZA","IsiZulu");//祖鲁语
            languageMap.put("sw_TZ","Kiswahili");//斯瓦西里语
            languageMap.put("lv_LV","Latviešu");//拉脱维亚语
            languageMap.put("lt_LT","Lietuvių");//立陶宛
            languageMap.put("hu_HU","Magyar");//匈牙利语
            languageMap.put("ms_MY","Melayu");//马来西亚语
            languageMap.put("nl_NL","Nederlands");//荷兰语
            languageMap.put("nb_NO","Bokmål");//挪威语
            languageMap.put("ro_RO","Română");//罗马尼亚语
            languageMap.put("sk_SK","Slovenčina");//斯洛伐克语
            languageMap.put("sl_SI","Slovenščina");//斯洛文尼亚语
            languageMap.put("fi_FI","Suomi");//芬兰语
            languageMap.put("sv_SE","Svenska");//瑞典语
            languageMap.put("vi_VN","Tiếng Việt");//越南语
            languageMap.put("el_GR","Ελληνικά");//希腊语
            languageMap.put("bg_BG","Български");//保加利亚语
            languageMap.put("uk_UA","Українська");//乌克兰语
            languageMap.put("he_IL","עברית");//希伯来语

            List<String> supportedLanguages = Arrays.asList("zh_CN","zh_TW","en_US","ja_JP"
                                                    ,"ko_KR","th_TH","hi_IN","fr_FR","de_DE","it_IT"
                                                    ,"ru_RU","es_ES","pt_PT","ar_SA","fa_IR","tr_TR","pl_PL","af-ZA","cs_CZ",
                    "da_DK","fil_PH","hr_HR","id_ID","zu_ZA","sw_TZ","lv_LV","hu_HU","ms_MY","nl_NL","nb_NO","ro_RO",
                    "sk_SK","sl_SI","sv_SE","vi_VN","el_GR","bg_BG","uk_UA","he_IL");
            for (String language_sup : supportedLanguages){
                if (language_sup.equals(languageCode) && !language.equals("English (United States,Computer)_en_US")){
                    String languageName = languageMap.get(language_sup);
                    Log.i("yangxin","------支持的语言-："+languageName);
                    languageBean = new LanguageBean(languageName,languageCode,false);
                    if (languageCode.equals(currentLanguageCode)){
                        languageBean.setSelect(true);
                    }
                    languageBeans.add(languageBean);
                }
            }
        }
        languageItemAdapter = new LanguageItemAdapter(context,languageBeans);
        language_listView.setAdapter(languageItemAdapter);

        language_listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                languageItemAdapter.setFocusedItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        language_listView.setOnItemClickListener(this::onItemClick);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //创建提示框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.dialog_changeLanguage_title));
        builder.setMessage(getResources().getString(R.string.dialog_changeLanguage_message));

        builder.setPositiveButton(getResources().getString(R.string.dialog_changeLanguage_btConfirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               //进行切换和重启
                LanguageBean languageBean = languageBeans.get(position);
                String indexLanguage = languageBean.getLanguageCode(); // en_US ; zh_CN ; zh_TW
                //showToast(indexLanguage);
                /*切换语言方法*/
                if (indexLanguage.equals("zh_CN") ){
                    changeSystemLanguage(Locale.SIMPLIFIED_CHINESE);
                } else if (indexLanguage.equals("zh_TW") ) {
                    changeSystemLanguage(Locale.TRADITIONAL_CHINESE);
                } else if (indexLanguage.equals("en_US")) {
                    changeSystemLanguage(new Locale("en","US"));
                } else if (indexLanguage.equals("ja_JP")) {
                    changeSystemLanguage(Locale.JAPAN);
                } else if (indexLanguage.equals("ko_KR")) {
                    changeSystemLanguage(Locale.KOREA);
                } else if (indexLanguage.equals("th_TH")) {
                    changeSystemLanguage(new Locale("th","TH"));
                } else if (indexLanguage.equals("hi_IN")) {
                    changeSystemLanguage(new Locale("hi","IN"));
                } else if (indexLanguage.equals("fr_FR")) {
                    changeSystemLanguage(Locale.FRANCE);
                } else if (indexLanguage.equals("de_DE")) {
                    changeSystemLanguage(Locale.GERMANY);
                } else if (indexLanguage.equals("it_IT")) {
                    changeSystemLanguage(Locale.ITALY);
                } else if (indexLanguage.equals("ru_RU")) {
                    changeSystemLanguage(new Locale("ru","RU"));
                } else if (indexLanguage.equals("es_ES")) {
                    changeSystemLanguage(new Locale("es","ES"));
                } else if (indexLanguage.equals("pt_PT")) {
                    changeSystemLanguage(new Locale("pt","PT"));
                } else if (indexLanguage.equals("ar_SA")) {
                    changeSystemLanguage(new Locale("ar","SA"));
                } else if (indexLanguage.equals("fa_IR")) {
                    changeSystemLanguage(new Locale("fa","IR"));
                } else if (indexLanguage.equals("tr_TR")) {
                    changeSystemLanguage(new Locale("tr","TR"));
                } else if (indexLanguage.equals("pl_PL")) {
                    changeSystemLanguage(new Locale("pl","PL"));
                } else if (indexLanguage.equals("af_ZA")) {
                    changeSystemLanguage(new Locale("af","ZA"));
                } else if (indexLanguage.equals("cs_CZ")) {
                    changeSystemLanguage(new Locale("cs","CZ"));
                } else if (indexLanguage.equals("da_DK")) {
                    changeSystemLanguage(new Locale("da","DK"));
                } else if (indexLanguage.equals("fil_PH")) {
                    changeSystemLanguage(new Locale("fil","PH"));
                } else if (indexLanguage.equals("hr_HR")) {
                    changeSystemLanguage(new Locale("hr","HR"));
                } else if (indexLanguage.equals("id_ID")) {
                    changeSystemLanguage(new Locale("id","ID"));
                } else if (indexLanguage.equals("zu_ZA")) {
                    changeSystemLanguage(new Locale("zu","ZA"));
                } else if (indexLanguage.equals("sw_TZ")) {
                    changeSystemLanguage(new Locale("sw","TZ"));
                } else if (indexLanguage.equals("lv_LV")) {
                    changeSystemLanguage(new Locale("lv","LV"));
                } else if (indexLanguage.equals("lt_LT")) {
                    changeSystemLanguage(new Locale("lt","LT"));
                } else if (indexLanguage.equals("hu_HU")) {
                    changeSystemLanguage(new Locale("hu","HU"));
                } else if (indexLanguage.equals("ms_MY")) {
                    changeSystemLanguage(new Locale("ms","MY"));
                } else if (indexLanguage.equals("nl_NL")) {
                    changeSystemLanguage(new Locale("nl","NL"));
                }else if (indexLanguage.equals("nb_NO")) {
                    changeSystemLanguage(new Locale("nb","NO"));
                }else if (indexLanguage.equals("ro_RO")) {
                    changeSystemLanguage(new Locale("ro","RO"));
                }else if (indexLanguage.equals("sk_SK")) {
                    changeSystemLanguage(new Locale("sk","SK"));
                }else if (indexLanguage.equals("sl_SI")) {
                    changeSystemLanguage(new Locale("sl","SI"));
                }else if (indexLanguage.equals("fi_FI")) {
                    changeSystemLanguage(new Locale("fi","FI"));
                }else if (indexLanguage.equals("sv_SE")) {
                    changeSystemLanguage(new Locale("sv","SE"));
                }else if (indexLanguage.equals("vi_VN")) {
                    changeSystemLanguage(new Locale("vi","VN"));
                }else if (indexLanguage.equals("el_GR")) {
                    changeSystemLanguage(new Locale("el","GR"));
                }else if (indexLanguage.equals("bg_BG")) {
                    changeSystemLanguage(new Locale("bg","BG"));
                }else if (indexLanguage.equals("uk_UA")) {
                    changeSystemLanguage(new Locale("uk","UA"));
                }else if (indexLanguage.equals("he_IL")) {
                    changeSystemLanguage(new Locale("he","IL"));
                }

                /*// 关闭当前应用程序
                finish();*/

                // 重新启动应用程序
                Intent restartIntent = new Intent(getApplicationContext(), UniversalLanguageActivity.class);
                restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(restartIntent);

            }
        });

        builder.setNegativeButton(getResources().getString(R.string.dialog_changeLanguage_btCancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // 创建并显示对话框
        AlertDialog dialog = builder.create();
        dialog.show();

    }
    /*
     * 修改系统语言的方法
     * 需要设置系统app获取系统权限才能执行*/
    public void changeSystemLanguage(Locale locale){
        if (locale != null){
            try {
                Object objIActMag;
                Class clzIActMag = Class.forName("android.app.IActivityManager");
                Class clzActMagNative = Class
                        .forName("android.app.ActivityManagerNative");
                //amn = ActivityManagerNative.getDefault();
                Method mtdActMagNative$getDefault = clzActMagNative
                        .getDeclaredMethod("getDefault");
                objIActMag = mtdActMagNative$getDefault.invoke(clzActMagNative);
                // objIActMag = amn.getConfiguration();
                Method mtdIActMag$getConfiguration = clzIActMag
                        .getDeclaredMethod("getConfiguration");
                Configuration config = (Configuration) mtdIActMag$getConfiguration
                        .invoke(objIActMag);
                // set the locale to the new value
                config.locale = locale;
                //持久化  config.userSetLocale = true;
                Class clzConfig = Class
                        .forName("android.content.res.Configuration");
                java.lang.reflect.Field userSetLocale = clzConfig
                        .getField("userSetLocale");
                userSetLocale.set(config, true);
                // 此处需要声明权限:android.permission.CHANGE_CONFIGURATION
                // 会重新调用 onCreate();
                Class[] clzParams = {Configuration.class};
                // objIActMag.updateConfiguration(config);
                Method mtdIActMag$updateConfiguration = clzIActMag
                        .getDeclaredMethod("updateConfiguration", clzParams);
                mtdIActMag$updateConfiguration.invoke(objIActMag, config);
                BackupManager.dataChanged("com.android.providers.settings");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public class LanguageItemAdapter extends ArrayAdapter<LanguageBean> {

        private LayoutInflater inflater;

        boolean isSelected;

        private int focusedItem = 0;
        public LanguageItemAdapter(@NonNull Context context, List<LanguageBean> languageBeans) {
            super(context, 0,languageBeans);
            inflater = LayoutInflater.from(context);
        }

        public void setFocusedItem(int position){
            focusedItem = position;
            notifyDataSetChanged();
        }

        @SuppressLint("ResourceType")
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null){
                //加载自定义item的布局
                itemView = inflater.inflate(R.layout.universal_language_list_item,parent,false);
            }

            //绑定自定义布局中的控件
            TextView languageNameTV = itemView.findViewById(R.id.languageName);
            ImageView languageSelectIV = itemView.findViewById(R.id.iv_languageSelect);
            int bgResIdSel = 0; int bgResIdUnSel = 0;
            int nameTvSel = 0; int nameTvUnSel = 0;
            int IVSelectSel = 0; int IVSelectUnSel = 0;
            switch (theme_code){
                case "0":
                    bgResIdSel = R.color.customWhite; bgResIdUnSel = Color.TRANSPARENT;
                    nameTvSel = R.color.sel_blue; nameTvUnSel = R.color.customWhite;
                    IVSelectSel = R.drawable.input_selected_iceblue_sel;IVSelectUnSel = R.drawable.input_selected_iceblue_unsel;
                    break;
                case "1":
                    bgResIdSel = R.drawable.red_border; bgResIdUnSel = R.drawable.black_border;
                    nameTvSel = R.color.text_red_new; nameTvUnSel = R.color.black;
                    IVSelectSel = R.drawable.input_selected_kapokwhite_sel;IVSelectUnSel = R.drawable.input_selected_kapokwhite_unsel;
                    break;
                case "2":
                    bgResIdSel = R.color.text_red_new;bgResIdUnSel = Color.TRANSPARENT;
                    nameTvSel = R.color.customWhite;nameTvUnSel = R.color.customWhite;
                    IVSelectSel = R.drawable.input_selected_iceblue_unsel;IVSelectUnSel = R.drawable.input_selected_iceblue_unsel;
                    break;
            }
            //给控件赋值
            LanguageBean languageBean = getItem(position);
            if (languageBean != null){
                languageNameTV.setText(languageBean.getLanguageName());
                isSelected = languageBean.isSelect();
                if (isSelected){
                    languageSelectIV.setImageResource(IVSelectSel);
                }else {
                    languageSelectIV.setImageResource(R.drawable.unselected);
                }
            }

            //设置聚焦效果
            if (position == focusedItem){
                itemView.setBackgroundResource(bgResIdSel);
                languageNameTV.setTextColor(ContextCompat.getColor(context,nameTvSel));
                if (languageBean.isSelect()){
                    languageSelectIV.setImageResource(IVSelectSel);
                }else {
                    languageSelectIV.setImageResource(R.drawable.unselected);
                }
            }else {
                itemView.setBackgroundResource(bgResIdUnSel);
                languageNameTV.setTextColor(ContextCompat.getColor(context,nameTvUnSel));
                if (languageBean.isSelect()){
                    languageSelectIV.setImageResource(IVSelectUnSel);
                }else {
                    languageSelectIV.setImageResource(R.drawable.unselected);
                }
            }
            return itemView;
        }
    }

    private void showToast(String text) {
        LayoutInflater inflater = getLayoutInflater();
        View customToastView = inflater.inflate(R.layout.custom_toast, null);
        TextView textView = customToastView.findViewById(R.id.toast_text);
        textView.setText(text);
        Toast customToast = new Toast(getApplicationContext());
        customToast.setView(customToastView);
        customToast.setDuration(Toast.LENGTH_SHORT);
        customToast.show();
    }
}
