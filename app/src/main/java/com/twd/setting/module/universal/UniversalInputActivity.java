package com.twd.setting.module.universal;

import android.annotation.SuppressLint;
import android.app.Instrumentation;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.twd.setting.R;
import com.twd.setting.bean.InputItem;
import com.twd.setting.utils.SystemPropertiesUtils;

import java.util.ArrayList;
import java.util.List;

public class UniversalInputActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView input_listView;
    private static final String TAG = UniversalInputActivity.class.getName();
    List<InputItem>  inputItems = new ArrayList<>();
    InputItemAdapter adapter;
    private final Context context = this;
    //String theme_code = SystemPropertiesUtils.getPropertyColor("persist.sys.background_blue","0");
    String theme_code = "0";
    SharedPreferences inputPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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
        setContentView(R.layout.activity_universal_input);
        initView();
    }

    private void initView(){
        input_listView = findViewById(R.id.universal_input_listView);
         inputPreferences= getSharedPreferences("inputMethod", Context.MODE_PRIVATE);
        PackageManager packageManager = getPackageManager();
        //检测当前已安装的输入法
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        String selectedInputMethodId = Settings.Secure.getString(getContentResolver(),Settings.Secure.DEFAULT_INPUT_METHOD);
        //获取已启用的输入法列表
        List<InputMethodInfo> inputMethods = imm.getInputMethodList();
        for (InputMethodInfo inputMethod : inputMethods){
            InputItem inputItem = null;
            String packageName = inputMethod.getPackageName();
            String inputMethodID = inputMethod.getId();
            try {
                CharSequence appName = packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, 0));
                String inputMethodAppName = appName.toString();
                Log.i(TAG,"inputMethodAppName = "+inputMethodAppName+",inputMethodID = " + inputMethodID);
                inputItem = new InputItem(inputMethodAppName,inputMethodID,false);
                if (inputMethodID.equals(selectedInputMethodId)){
                    inputItem.setSelected(true);
                }
            }catch (Exception e){
                e.printStackTrace();
            }


            /*if (packageName.contains("sogou")){
                inputItem = new InputItem(getString(R.string.inputMethod_value_sougou),inputMethodID,false);
                if (inputMethodID.equals(selectedInputMethodId)){
                    inputItem.setSelected(true);
                }
            } else if (packageName.contains("inputmethod.latin")) {
                inputItem = new InputItem(getString(R.string.inputMethod_value_Aosp),inputMethodID,false);
                if (inputMethodID.equals(selectedInputMethodId)){
                    inputItem.setSelected(true);
                }
            } else if (packageName.contains("inputmethod.pinyin")) {
                inputItem = new InputItem(getString(R.string.inputMethod_value_google),inputMethodID,false);
                if (inputMethodID.equals(selectedInputMethodId)){
                    inputItem.setSelected(true);
                }
            }*/

            if (inputItem != null)inputItems.add(inputItem);
            Log.i(TAG, "initView: packageName = " + packageName + ",id = " + inputMethodID);
        }
        adapter = new InputItemAdapter(context,inputItems);
        input_listView.setAdapter(adapter);
        input_listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                adapter.setFocusedItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        input_listView.setOnItemClickListener(this::onItemClick);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*
        * 设置启用输入法需要系统app权限,第三方app无法操作这个权限
        * 设置当前选中的输入法为启用的输入法*/
        InputItem selectedInputItem = inputItems.get(position);
        Settings.Secure.putString(getContentResolver(),Settings.Secure.DEFAULT_INPUT_METHOD,selectedInputItem.getInputId());
        SharedPreferences.Editor editor = inputPreferences.edit();
        editor.putString("input", selectedInputItem.getInputId());
        editor.commit();
        simulateBackKeyPress();
        Log.i(TAG,"onItemClick: -------点击事件--------");
    }

    //模拟点击返回键
    public void simulateBackKeyPress(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Instrumentation inst = new Instrumentation();
                inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                SystemClock.sleep(100);
            }
        }).start();
    }

    public class InputItemAdapter extends ArrayAdapter<InputItem>{
        private LayoutInflater inflater;
        boolean isSelected;
        private int focusedItem = 0;
        public  InputItemAdapter(Context context,List<InputItem> inputItems){
            super(context,0,inputItems);
            inflater = LayoutInflater.from(context);
        }

        public void setFocusedItem(int position){
            focusedItem = position;
            notifyDataSetChanged();
        }

        @SuppressLint("ResourceType")
        @Override
        public View getView(int position, View convertView, ViewGroup parent ){
            View itemView = convertView;
            if (itemView == null){
                itemView = inflater.inflate(R.layout.universal_input_list_item,parent,false);
            }

            int bgResIdSel = 0; int bgResIdUnSel = 0;
            int textColorSel = 0; int textColorUnSel = 0;
            int IVSelectedSel = 0; int IVSelectedUnSel = 0;
            TextView inputNameTV = itemView.findViewById(R.id.inputName);
            ImageView inputSelectIV = itemView.findViewById(R.id.iv_inputSelect);
            switch (String.valueOf(theme_code)){
                case "0"://iceBlue
                    bgResIdSel = R.color.customWhite;bgResIdUnSel = Color.TRANSPARENT;
                    textColorSel = R.color.sel_blue;textColorUnSel = R.color.customWhite;
                    IVSelectedSel = R.drawable.input_selected_iceblue_sel; IVSelectedUnSel = R.drawable.input_selected_iceblue_unsel;
                    break;
                case "1"://kapokWhite
                    bgResIdSel = R.drawable.red_border;bgResIdUnSel = R.drawable.black_border;
                    textColorSel = R.color.text_red_new;textColorUnSel = R.color.black;
                    IVSelectedSel = R.drawable.input_selected_kapokwhite_sel;IVSelectedUnSel = R.drawable.input_selected_kapokwhite_unsel;
                    break;
                case "2":
                    bgResIdSel = R.color.text_red_new;bgResIdUnSel = Color.TRANSPARENT;
                    textColorSel = R.color.customWhite;textColorUnSel = R.color.customWhite;
                    IVSelectedSel = R.drawable.input_selected_iceblue_unsel;IVSelectedUnSel = R.drawable.input_selected_iceblue_unsel;
            }

            InputItem inputItem = inputItems.get(position);
            if (inputItem != null){
                inputNameTV.setText(inputItem.getInputName());
                isSelected = inputItem.isSelected();
                if (isSelected) {
                    inputSelectIV.setImageResource(IVSelectedSel);
                }else {
                    inputSelectIV.setImageResource(IVSelectedUnSel);
                }
            }
            //设置聚焦效果
            if (position == focusedItem){
                itemView.setBackgroundResource(bgResIdSel);
                inputNameTV.setTextColor(ContextCompat.getColor(context,textColorSel));
                if (inputItem.isSelected()){
                    inputSelectIV.setImageResource(IVSelectedSel);
                }else {
                    inputSelectIV.setImageResource(R.drawable.unselected);
                }
            }else {
                itemView.setBackgroundResource(bgResIdUnSel);
                inputNameTV.setTextColor(ContextCompat.getColor(context,textColorUnSel));
                if (inputItem.isSelected()){
                    inputSelectIV.setImageResource(IVSelectedUnSel);
                }else {
                    inputSelectIV.setImageResource(R.drawable.unselected);
                }
            }
            return itemView;
        }
    }
}
