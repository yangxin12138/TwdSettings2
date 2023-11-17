package com.twd.setting.module.systemequipment.repository;

import android.content.Context;
import androidx.core.content.ContextCompat;
//import com.konka.android.storage.KKStorageManager;
import com.twd.setting.R;
import com.twd.setting.base.BaseModel;
import com.twd.setting.commonlibrary.Utils.StringUtils;
import com.twd.setting.module.systemequipment.model.StorageInfoData;
import com.twd.setting.utils.CollectionUtils;
import com.twd.setting.utils.HLog;
//import com.twd.setting.utils.KkDataUtils;
import com.twd.setting.utils.RomSizeTools;
import com.twd.setting.utils.UsbScanTool;
import com.twd.setting.widgets.RatioLineCustomView.ItemData;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class StorageDetailRepository
  extends BaseModel
{
  private final String _TAG = "StorageDetailRepository";
  
  private StorageInfoData getSysStorageData(Context paramContext)
  {
    long l1 = RomSizeTools.getRomStateSize(new String[] { "/data", "/system", "/cache" })[0];
    long[] localObject1 = RomSizeTools.getRomStateSize(new String[] { "/data" });
    long l2 = localObject1[1];
    long l3 = localObject1[0] - localObject1[1];
    long l4 = RomSizeTools.getRomStateSize(new String[] { "/system" })[0];
    Object localObject2 = new StringBuilder();
    ((StringBuilder)localObject2).append("系统总容量：romTotalSize: ");
    ((StringBuilder)localObject2).append(l1);
    HLog.d("StorageDetailRepository", ((StringBuilder)localObject2).toString());
    localObject2 = new StringBuilder();
    ((StringBuilder)localObject2).append("系统data已用：romSizeData[0]: ");
    ((StringBuilder)localObject2).append(localObject1[0]);
    HLog.d("StorageDetailRepository", ((StringBuilder)localObject2).toString());
    localObject2 = new StringBuilder();
    ((StringBuilder)localObject2).append("系统system已用：systemUsedSize: ");
    ((StringBuilder)localObject2).append(l4);
    HLog.d("StorageDetailRepository", ((StringBuilder)localObject2).toString());
    long l5 = l1 - localObject1[0] - l4;
    ArrayList arrayList = new ArrayList();
    localObject2 = RomSizeTools.getRomStateSizeString(l1);
    if (l4 > 0L) {
 //     arrayList.add(new RatioLineCustomView.ItemData(ContextCompat.getColor(paramContext, R.color.color_636fc6), (float)l4, paramContext.getString(R.string.str_storage_sys_used_prefix, new Object[] { RomSizeTools.formatFileSize(paramContext, l4) })));
    }
    if (l3 > 0L) {
 //     arrayList.add(new RatioLineCustomView.ItemData(ContextCompat.getColor(paramContext, R.color.color_f29c4a), (float)l3, paramContext.getString(R.string.str_storage_app_used_prefix, new Object[] { RomSizeTools.formatFileSize(paramContext, l3) })));
    }
    if (l5 > 0L) {
 //     arrayList.add(new RatioLineCustomView.ItemData(ContextCompat.getColor(paramContext, R.color.color_475261), (float)l5, paramContext.getString(R.string.str_storage_other_used_prefix, new Object[] { RomSizeTools.formatFileSize(paramContext, l5) })));
    }
    String str = RomSizeTools.formatFileSize(paramContext, l2);
 //   arrayList.add(new RatioLineCustomView.ItemData(ContextCompat.getColor(paramContext, R.color.color_b7bbc1), (float)l2, paramContext.getString(R.string.str_storage_free_prefix, new Object[] { str })));
   // KkDataUtils.sentEventActive("剩余内存", str);
    return new StorageInfoData(true, paramContext.getString(R.string.str_storage_system), R.drawable.ic_sys_storage, paramContext.getString(R.string.str_storage_size, new Object[] { localObject2 }), paramContext.getString(R.string.str_storage_freesize, new Object[] { str, localObject2 }), (List)arrayList);
  }
  
  private StorageInfoData getUsbItemData(int paramInt, Context paramContext, String paramString1, String paramString2)
  {
  /*  long l5 = KKStorageManager.getTotalMemorySize(paramString1);
    long l6 = KKStorageManager.getAvailableMemorySize(paramString1);
    Object localObject2 = new UsbScanTool(paramContext);
    Object localObject1 = new long[3];
    Object tmp29_27 = localObject1;
    tmp29_27[0] = 0L;
    Object tmp33_29 = tmp29_27;
    tmp33_29[1] = 0L;
    Object tmp37_33 = tmp33_29;
    tmp37_33[2] = 0L;
    tmp37_33;
    try
    {
      paramString1 = ((UsbScanTool)localObject2).scanDir(paramString1, true);
    }
    catch (IOException paramString1)
    {
      paramString1.printStackTrace();
      paramString1 = (String)localObject1;
    }
    long l1;
    long l2;
    long l3;
    long l4;
    if (paramString1 != null)
    {
      l1 = paramString1[0];
      l2 = paramString1[1];
      l3 = paramString1[2];
      paramString1 = new StringBuilder();
      paramString1.append("USB filesize usbVedioSize=");
      paramString1.append(l1);
      paramString1.append("  usbMusicSize=");
      paramString1.append(l2);
      paramString1.append("  usbPictrueSize=");
      paramString1.append(l3);
      HLog.d("StorageDetailRepository", paramString1.toString());
      l4 = tmp37_33 - l6 - l1 - l2 - l3;
    }
    else
    {
      l3 = 0L;
      l1 = l3;
      l2 = l1;
      l4 = l2;
    }
    paramString1 = new ArrayList();
    localObject1 = RomSizeTools.formatFileSize(paramContext, tmp37_33);
    if (l1 > 0L) {
      paramString1.add(new RatioLineCustomView.ItemData(ContextCompat.getColor(paramContext, 2131099732), (float)l1, paramContext.getString(2131755248, new Object[] { RomSizeTools.formatFileSize(paramContext, l1) })));
    }
    if (l2 > 0L) {
      paramString1.add(new RatioLineCustomView.ItemData(ContextCompat.getColor(paramContext, 2131099725), (float)l2, paramContext.getString(2131755242, new Object[] { RomSizeTools.formatFileSize(paramContext, l2) })));
    }
    if (l3 > 0L) {
      paramString1.add(new RatioLineCustomView.ItemData(ContextCompat.getColor(paramContext, 2131099709), (float)l3, paramContext.getString(2131755244, new Object[] { RomSizeTools.formatFileSize(paramContext, l3) })));
    }
    if (l4 > 0L) {
      paramString1.add(new RatioLineCustomView.ItemData(ContextCompat.getColor(paramContext, 2131099708), (float)l4, paramContext.getString(2131755243, new Object[] { RomSizeTools.formatFileSize(paramContext, l4) })));
    }
    localObject2 = RomSizeTools.formatFileSize(paramContext, l6);
    paramString1.add(new RatioLineCustomView.ItemData(ContextCompat.getColor(paramContext, 2131099724), (float)l6, paramContext.getString(2131755240, new Object[] { localObject2 })));
    if (StringUtils.isTrimEmpty(paramString2))
    {
      paramString2 = new StringBuilder();
      paramString2.append(paramContext.getString(2131755239));
      paramString2.append(paramInt);
      paramString2.append(1);
      paramString2 = paramString2.toString();
    }
    return new StorageInfoData(false, paramString2, 2131230857, paramContext.getString(2131755245, new Object[] { localObject1 }), paramContext.getString(2131755241, new Object[] { localObject2, localObject1 }), paramString1);


   */
    return  null;
  }

  
  public Observable<List<StorageInfoData>> getExternalListData(final Context paramContext)
  {
    return Observable.create(new ObservableOnSubscribe()
    {
      @Override
      public void subscribe(ObservableEmitter e) throws Exception {
        ArrayList localArrayList1 = new ArrayList();
     //   KKStorageManager localKKStorageManager = KKStorageManager.getInstance(paramContext);
        ArrayList localArrayList2 = UsbScanTool.getAllExternalStoragePath(paramContext);
        if (CollectionUtils.isEmpty(localArrayList2))
        {
          e.onNext(localArrayList1);
          e.onComplete();
          return;
        }
        int i = 0;
        while (i < localArrayList2.size())
        {
          String str = (String)localArrayList2.get(i);
          StringBuilder localStringBuilder = new StringBuilder();
          localStringBuilder.append("挂载磁盘 volume = ");
          localStringBuilder.append(str);
          HLog.d("StorageDetailRepository", localStringBuilder.toString());
          if ((!StringUtils.isTrimEmpty(str)) && (!str.contains("/emulated")) && (!str.contains("/self"))) {
          //  localArrayList1.add(StorageDetailRepository.this.getUsbItemData(i, paramContext, str, localKKStorageManager.getVolumeLabel(str)));
          }
          i += 1;
        }
        e.onNext(localArrayList1);
        e.onComplete();
      }
    }).delay(1200L, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io());
  }
  
  public Observable<StorageInfoData> getSysData(final Context paramContext)
  {
    return Observable.create(new ObservableOnSubscribe()
    {
      @Override
      public void subscribe(ObservableEmitter e) throws Exception {
        e.onNext(StorageDetailRepository.this.getSysStorageData(paramContext));
        e.onComplete();
      }
    }).subscribeOn(Schedulers.io());
  }
}

