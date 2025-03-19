package com.twd.setting.module.common.vm;

import android.app.Application;
import android.content.Context;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.twd.setting.R;
import com.twd.setting.base.observer.MyObserver;
import com.twd.setting.base.BaseModel;
import com.twd.setting.base.BaseViewModel;
import com.twd.setting.base.IDispose;
import com.twd.setting.commonlibrary.Utils.RxTransUtils;
import com.twd.setting.commonlibrary.Utils.StringUtils;
import com.twd.setting.commonlibrary.Utils.event.SingleLiveEvent;
import com.twd.setting.module.common.model.CommonSettingVisible;
import com.twd.setting.module.common.model.InputMethodData;
import com.twd.setting.utils.CollectionUtils;
import com.twd.setting.utils.ConfigInfo;
import com.twd.setting.utils.HLog;
//import com.twd.setting.utils.KkDataUtils;
import com.twd.setting.utils.KkUtils;
import com.twd.setting.utils.binding.ItemLRTextIconData;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CommonViewModel
        extends BaseViewModel<BaseModel> {
    private final String TAG = "InputMethodEditorViewModel";
    private final MutableLiveData<ItemLRTextIconData> _ImeBarValue = new SingleLiveEvent();
    private final MutableLiveData<CommonSettingVisible> _SettingsValue = new SingleLiveEvent();
    private ItemLRTextIconData imeData;
    private List<InputMethodData> imeList;
    private volatile CommonSettingVisible settingVisible;
    private ItemLRTextIconData settingsData;

    public CommonViewModel(Application paramApplication) {
        super(paramApplication);
        initData(paramApplication);
    }

    private String getDefaultImeName(Context paramContext) {
        List localObject3 = ((InputMethodManager) paramContext.getSystemService(Context.INPUT_METHOD_SERVICE)).getInputMethodList();
        String NameStr = null;
        String NameStr_select = null;
        if (localObject3 != null) {
            if (localObject3.size() <= 0) {
                return null;
            }
            this.imeList = new ArrayList();
            String str = Settings.Secure.getString(paramContext.getContentResolver(), "default_input_method");
            Iterator iterator = localObject3.iterator();
            while (iterator.hasNext()) {
                InputMethodInfo localInputMethodInfo = (InputMethodInfo) iterator.next();
                if ((!StringUtils.isTrimEmpty(localInputMethodInfo.getId())) && (!localInputMethodInfo.getId().contains("com.konka.kkmultiscreen"))) {
                    InputMethodData localInputMethodData = new InputMethodData();
                    localInputMethodData.setIdStr(localInputMethodInfo.getId());
                    CharSequence localCharSequence = localInputMethodInfo.loadLabel(paramContext.getPackageManager());
                    if (localCharSequence == null) {
                        NameStr = localInputMethodInfo.getPackageName();
                    } else {
                        NameStr = localCharSequence.toString();
                    }
                    localInputMethodData.setNameStr(NameStr);
                    if (NameStr != null) {
                        if (NameStr.equalsIgnoreCase(localInputMethodInfo.getId())) {
                            localInputMethodData.setSelected(true);
                            if (localCharSequence == null) {
                                NameStr_select = "";
                            } else {
                                NameStr_select = localCharSequence.toString();
                            }
                        }
                    }
                    this.imeList.add(localInputMethodData);
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("输入法 PackageName:");
                    stringBuilder.append(localInputMethodInfo.getPackageName());
                    stringBuilder.append(" serverName=");
                    stringBuilder.append(localInputMethodInfo.getServiceName());
                    stringBuilder.append("  id=");
                    stringBuilder.append(localInputMethodInfo.getId());
                    stringBuilder.append(" label=");
                    stringBuilder.append(localInputMethodInfo.loadLabel(paramContext.getPackageManager()));
                    HLog.d("InputMethodEditorViewModel", stringBuilder.toString());

                }
            }
        }
        return NameStr_select;
    }

    private void loadImeData(final Context paramContext) {
        Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter e) throws Exception {
                e.onNext(CommonViewModel.this.getDefaultImeName(paramContext));
            }
        }).compose(RxTransUtils.schedulersIoToUi()).subscribe(new MyObserver(this) {
            @Override
            public void onNext(Object value) {
                CommonViewModel.this.imeData.setRightTxt((String) value);
                CommonViewModel.this._ImeBarValue.postValue(CommonViewModel.this.imeData);
                if (TextUtils.isEmpty((String) value)) {
                    //       KkDataUtils.sentEventError("输入法", null);
                    return;
                }
                //    KkDataUtils.sentEventActive("输入法", paramAnonymousString);
            }

            public void onError(Throwable paramAnonymousThrowable) {
                //    KkDataUtils.sentEventError("输入法", null);
            }

        });
    }

    public boolean changeInputMethod(Context paramContext, String paramString) {
        if (StringUtils.isTrimEmpty(paramString)) {
            HLog.e("InputMethodEditorViewModel", "输入法 切换失败: inputMethodId=" + paramString);
            return false;
        }
        boolean bool = Settings.Secure.putString(paramContext.getContentResolver(), "default_input_method", paramString);
        Settings.Secure.putInt(paramContext.getContentResolver(), "selected_input_method_subtype", -1);
        if (!CollectionUtils.isEmpty(this.imeList)) {
            Iterator iterator = this.imeList.iterator();
            while (iterator.hasNext()) {
                InputMethodData localInputMethodData = (InputMethodData) iterator.next();
                if (localInputMethodData != null) {
                    localInputMethodData.setSelected(paramString.equalsIgnoreCase(localInputMethodData.getIdStr()));
                }
            }
        }
        if (!bool) {
            HLog.e("InputMethodEditorViewModel", "输入法 设置失败: inputMethodId=" + paramString);
        }
        return bool;
    }

    public LiveData<ItemLRTextIconData> getImeBarValue() {
        return this._ImeBarValue;
    }

    public ItemLRTextIconData getImeData() {
        return this.imeData;
    }

    public List<InputMethodData> getImeList() {
        return this.imeList;
    }

    public CommonSettingVisible getSettingVisible() {
        return this.settingVisible;
    }

    public ItemLRTextIconData getSettingsData() {
        return this.settingsData;
    }

    public LiveData<CommonSettingVisible> getSettingsValue() {
        return this._SettingsValue;
    }

    public void initData(Context paramContext) {
        this.imeData = new ItemLRTextIconData(0, paramContext.getString(R.string.str_ime), null, 0, R.drawable.ic_baseline_arrow_forward_ios_24, View.GONE,View.VISIBLE);
        this.settingsData = new ItemLRTextIconData(1, paramContext.getString(R.string.str_common_settings), null, 0, R.drawable.ic_baseline_arrow_forward_ios_24,View.GONE,View.VISIBLE);
        loadImeData(paramContext);
        initSettingItemVisible();
    }

    public void initSettingItemVisible() {
        Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter e) throws Exception {
                CommonSettingVisible localCommonSettingVisible = new CommonSettingVisible();
                ConfigInfo localConfigInfo = new ConfigInfo("/etc/settings.ini");
                if (KkUtils.isUnSupportCommonSetting(localConfigInfo)) {
                    localCommonSettingVisible.setSettingVisible(true);
                } else {
                    localCommonSettingVisible.setSettingCname(KkUtils.getCommonSettingComponentName(localConfigInfo));
                }
                //CommonViewModel.access$102(CommonViewModel.this, localCommonSettingVisible);
                e.onNext(localCommonSettingVisible);
                e.onComplete();
            }
        }).compose(RxTransUtils.schedulersIoToUi()).subscribe(new MyObserver(this) {
            @Override
            public void onNext(Object value) {
                CommonViewModel.this._SettingsValue.postValue((CommonSettingVisible) value);
            }

            public void onError(Throwable paramAnonymousThrowable) {
                CommonViewModel.this._SettingsValue.postValue(null);
            }

        });
    }
}
