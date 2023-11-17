package com.twd.setting.commonlibrary.Utils;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RxTransUtils {
    public static <T> ObservableTransformer<T, T> schedulersIoToUi() {
        return new ObservableTransformer() {
            @Override
            public ObservableSource apply(Observable upstream) {
                return (ObservableSource) upstream.subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
                //return null;
            }
        };
    }
}