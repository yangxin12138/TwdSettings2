package com.twd.setting.module.systemequipment.repository;

import android.content.Context;
import com.twd.setting.base.BaseModel;
import com.twd.setting.utils.KkUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

public class SysEquipmentRepository
  extends BaseModel
{
  public Observable<Boolean> getSupportOffLineUpdateStatus(final Context paramContext)
  {
    return Observable.create(new ObservableOnSubscribe()
    {
      @Override
      public void subscribe(ObservableEmitter e) throws Exception {

      }
    }).subscribeOn(Schedulers.io());
  }
}

