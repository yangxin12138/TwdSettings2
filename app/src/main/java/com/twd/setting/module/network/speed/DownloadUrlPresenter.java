package com.twd.setting.module.network.speed;

import android.content.Context;
//import com.alibaba.fastjson.JSON;
//import com.konka.Signature;
//import com.twd.setting.base.BasePresenter;
import com.twd.setting.utils.HLog;
//import com.twd.setting.utils.NetRequestUtils;
import com.twd.setting.utils.ThreadPoolsUtils;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class DownloadUrlPresenter
//  extends BasePresenter
{
    private final Charset CHARSET = Charset.forName("UTF-8");
    private final String LOG_TAG = "DownloadUrlPresenter";
    private final String SALT = "SnRegister2022";
    private DownloadUrlModel bean;
    private Context context;
    private String param;

    public DownloadUrlPresenter(Context paramContext) {
        this.context = paramContext;
        requestDownloadFileUrl();
    }

    public void destroy() {
    }

    public void getDownloadFileUrl() {
        //   Observable localObservable = NetRequestUtils.postHttp("http://scs.kkapp.com/SettingCenterSnRegister/snRegister/downloadTestFile", this.param, DownloadUrlModel.class);
        //   this.compositeDisposable.add(localObservable.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.from(ThreadPoolsUtils.getExecutor())).subscribe(new DownloadUrlPresenter..ExternalSyntheticLambda0(this), new DownloadUrlPresenter..ExternalSyntheticLambda1(this)));
    }

    public String getDownloadUrl() {
        DownloadUrlModel localDownloadUrlModel = this.bean;
        if (localDownloadUrlModel == null) {
            return "";
        }
        if (localDownloadUrlModel.getData() == null) {
            return "";
        }
        return this.bean.getData();
    }

    public void requestDownloadFileUrl() {
        Object localObject1 = new HashMap();
        Object localObject2 = new DownloadUrlRequest();
        //   ((DownloadUrlRequest)localObject2).setSign(Signature.sign((Map)localObject1, "SnRegister2022", this.CHARSET));
        //   this.param = JSON.toJSONString(localObject2);
        localObject1 = this.LOG_TAG;
        localObject2 = new StringBuilder();
        ((StringBuilder) localObject2).append("----> param: ");
        ((StringBuilder) localObject2).append(this.param);
        HLog.d((String) localObject1, ((StringBuilder) localObject2).toString());
    }
}

