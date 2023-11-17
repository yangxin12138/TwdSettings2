package com.twd.setting.utils;

import android.content.Context;
import android.util.Log;
//import com.konka.android.storage.KKStorageManager;
//import com.twd.setting.SettingConfig;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UsbScanTool
{
  public static final String[] MUSIC_TYPE = { ".flac", ".ape", ".wav", ".mp3", ".aac", ".ogg", ".wma" };
  public static final String[] PICTRUE_TYPE = { ".bmp", ".gif", ".jpeg", ".JPEG2000", ".jpg", ".tiff", ".psd", ".png", ".swf", ".svg", ".eps", ".wmf" };
  private static String TAG = "SettingsCenter/UsbTool";
  public static final String[] VEDIO_TYPE = { "avi", ".mpg ", ".mpeg ", ".mpe ", ".m1v ", ".m2v ", ".mpv2 ", ".tp", ".flv", ".f4v", ".mp2v ", ".dat", ".mp4", ".m4v", "m4p", ".3gp", ".3gpp", ".3g2", ".3gp2", ".ts", ".m2ts", "swf", ".asf", "vob", ".trp", ".wmv", ".mov", ".mkv", ".amr", ".rm", ".ram", ".rmvb", ".rpm ", ".xv" };
  private static volatile UsbScanTool instance = null;
  private WeakReference<Context> mContextReference;
  private long musicSize;
  private long musicTotalSize;
  private long picSize;
  private long picTotalSize;
  private long vedioSize;
  private long vedioTotalSize;
  
  public UsbScanTool(Context paramContext)
  {
    this.mContextReference = new WeakReference(paramContext);
  }
  
  public static ArrayList<String> getAllExternalStoragePath(Context paramContext)
  {
    String[] arrayOfString = getSingleton(paramContext).getVolumePaths();
    if (arrayOfString == null) {
      return null;
    }
    ArrayList localArrayList = new ArrayList();
    int j = arrayOfString.length;
    int i = 0;
    while (i < j)
    {
      String str1 = arrayOfString[i];
      if (!str1.contains("emulated"))
      {
        String str2 = getSingleton(paramContext).getVolumeState(str1);
        if ((str2 != null) && (str2.equals("mounted"))) {
          localArrayList.add(str1);
        }
      }
      i += 1;
    }
    return localArrayList;
  }
  
  public static String getExternalStoragePath(Context paramContext)
  {
    String[] arrayOfString = getSingleton(paramContext).getVolumePaths();
    if (arrayOfString == null) {
      return null;
    }
    int j = arrayOfString.length;
    int i = 0;
    while (i < j)
    {
      String str1 = arrayOfString[i];
      if ((!str1.contains("/emulated")) && (!str1.contains("/self")))
      {
        String str2 = getSingleton(paramContext).getVolumeState(str1);
        if ((str2 != null) && (str2.equals("mounted"))) {
          return str1;
        }
      }
      i += 1;
    }
    return null;
  }
  
  public static String getOfflineZipMountPath()
  {
   /* Object localObject1 = Pattern.compile("/mnt/media_rw\\S*");
    Object localObject2 = Pattern.compile("/mnt/\\S+\\svfat");
    try
    {
      localObject3 = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("mount").getInputStream()));
      ArrayList localArrayList = new ArrayList();
      for (;;)
      {
        String str = ((BufferedReader)localObject3).readLine();
        if (str == null) {
          break;
        }
        Object localObject4;
        if (SettingConfig.IS_DEBUG)
        {
          localObject4 = TAG;
          StringBuilder localStringBuilder = new StringBuilder();
          localStringBuilder.append(" USB分区信息=");
          localStringBuilder.append(str);
          HLog.d((String)localObject4, localStringBuilder.toString());
        }
        if ((!str.contains("proc")) && (!str.contains("tmpfs")) && (!str.contains("asec")) && (!str.contains("secure")) && (!str.contains("system")) && (!str.contains("cache")) && (!str.contains("sys")) && (!str.contains("data")) && (!str.contains("shell")) && (!str.contains("root")) && (!str.contains("acct")) && (!str.contains("misc")) && (!str.contains("obb")))
        {
          localObject4 = ((Pattern)localObject1).matcher(str);
          if (((Matcher)localObject4).find()) {
            return ((Matcher)localObject4).group();
          }
          localObject4 = ((Pattern)localObject2).matcher(str);
          if (((Matcher)localObject4).find())
          {
            localObject4 = ((Matcher)localObject4).group();
            if (localObject4 != null) {
              return localObject4.split(" ")[0];
            }
          }
          if ((!str.contains("/emulated")) && ((str.contains("fat")) || (str.contains("fuse")) || (str.contains("ntfs")) || (str.startsWith("/dev/block/vold")))) {
            localArrayList.add(str);
          }
        }
      }
      localObject1 = getOtherSdInfo(localArrayList);
      return (String)localObject1;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
      localObject2 = TAG;
      Object localObject3 = new StringBuilder();
      ((StringBuilder)localObject3).append(" 获取离线更新zip路径失败=");
      ((StringBuilder)localObject3).append(localException.getMessage());
      HLog.e((String)localObject2, ((StringBuilder)localObject3).toString());
    }

    */
    return null;
  }
  
  private static String getOtherSdInfo(List<String> paramList)
  {
    if (CollectionUtils.isEmpty(paramList)) {
      return null;
    }
    if (paramList.size() == 1)
    {
      String str = (String)paramList.get(0);
      if (str != null)
      {
        String[] strs = str.split(" ");
        if (strs.length > 1) {
          return strs[1];
        }
      }
      return null;
    }
    Iterator iterator = paramList.iterator();
    while (iterator.hasNext())
    {
      Object localObject = (String)iterator.next();
      if (localObject != null)
      {
        String[] strs = ((String)localObject).split(" ");
        if (strs.length > 1)
        {
          String str = strs[1].toLowerCase(Locale.getDefault());
          if ((str.contains("/sd")) || (str.contains("usb"))) {
            return strs[1];
          }
        }
      }
    }
    return null;
  }
  
  public static UsbScanTool getSingleton(Context paramContext)
  {
    try
    {
      if (instance == null) {
        instance = new UsbScanTool(paramContext);
      }
      return instance;
    }
    finally {}
  }
  
  public long[] getAllExterbalFile(Context paramContext)
  {
 /*   try
    {
      Thread.sleep(1200L);
    }
    catch (InterruptedException localInterruptedException)
    {
      localInterruptedException.printStackTrace();
      Thread.currentThread().interrupt();
    }
    ArrayList localArrayList = getAllExternalStoragePath(paramContext);
    paramContext = new long[3];
    Context tmp30_29 = paramContext;
    tmp30_29[0] = 0L;
    Context tmp34_30 = tmp30_29;
    tmp34_30[1] = 0L;
    Context tmp38_34 = tmp34_30;
    tmp38_34[2] = 0L;
    tmp38_34;
    if (localArrayList == null) {
      return paramContext;
    }
    int i = 0;
    while (i < localArrayList.size())
    {
      Object localObject = (String)localArrayList.get(i);
      try
      {
        localObject = scanDir((String)localObject, true);
        paramContext = (Context)localObject;
      }
      catch (IOException localIOException)
      {
        localIOException.printStackTrace();
        String str = TAG;
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Exception: ");
        localStringBuilder.append(localIOException.toString());
        Log.d(str, localStringBuilder.toString());
      }
      i += 1;
    }
    return paramContext;
  */return null;
  }
  
  public String[] getVolumePaths()
  {
 //   return KKStorageManager.getInstance((Context)this.mContextReference.get()).getVolumePaths();
      return null;
  }
  
  public String getVolumeState(String paramString)
  {
    //return KKStorageManager.getInstance((Context)this.mContextReference.get()).getVolumeState(paramString);
    return null;
  }
  
  public boolean isMountedStorageExist()
  {
    /*KKStorageManager localKKStorageManager = KKStorageManager.getInstance((Context)this.mContextReference.get());
    if (localKKStorageManager == null) {
      return false;
    }
    String[] arrayOfString = localKKStorageManager.getVolumePaths();
    int j = arrayOfString.length;
    int i = 0;
    while (i < j)
    {
      String str = arrayOfString[i];
      if ("mounted".equals(localKKStorageManager.getVolumeState(str)))
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("isMountedStorageExist   路径：");
        localStringBuilder.append(str);
        HLog.d("打印", localStringBuilder.toString());
        if ((!str.contains("/emulated")) && (!str.contains("/self"))) {
          return true;
        }
      }
      i += 1;
    }

     */
    return false;
  }
 /*
  public long[] scanDir(String paramString, boolean paramBoolean)
    throws IOException
  {
    if (paramString == null) {
      return null;
    }
    Object localObject3 = new File(paramString).listFiles();
    if (localObject3 == null) {
      return null;
    }
    paramString = new long[3];
    Object localObject2 = new ArrayList();
    Object localObject1 = new ArrayList();
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    while (i < localObject3.length)
    {
      Object localObject4 = localObject3[i];
      if ((!((File)localObject4).getName().contains("$RECYCLE.BIN")) && (!((File)localObject4).getName().contains("LOST.DIR")) && (!((File)localObject4).getName().contains(".git")) && (!localObject3[i].getName().contains(".svn")) && (!localObject3[i].getName().contains("found.000")) && (!localObject3[i].getName().contains("FOUND.000")) && (!localObject3[i].getName().contains("logs")) && (!localObject3[i].getName().contains("Logs")) && (!localObject3[i].getName().contains(".log")) && (!localObject3[i].getName().contains("cae_record")) && (!((File)localObject4).getName().contains("System Volume Information")))
      {
        Object localObject5;
        Object localObject6;
        int j;
        if ((((File)localObject4).isDirectory()) && (paramBoolean))
        {
          localObject5 = scanDir(((File)localObject4).getAbsolutePath(), paramBoolean);
          if (localObject5 == null)
          {
            localObject5 = TAG;
            localObject6 = new StringBuilder();
            ((StringBuilder)localObject6).append("scanDir: 路径非法");
            ((StringBuilder)localObject6).append(((File)localObject4).getAbsolutePath());
            Log.d((String)localObject5, ((StringBuilder)localObject6).toString());
          }
          else
          {
            j = 0;
          }
        }
        else
        {
          while (j < localObject5.length)
          {
            paramString[j] += localObject5[j];
            j += 1;
            continue;
            localObject5 = ((File)localObject4).getAbsolutePath().toLowerCase(Locale.getDefault());
            localObject6 = TAG;
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append("scanDir: strFileName");
            localStringBuilder.append((String)localObject5);
            Log.d((String)localObject6, localStringBuilder.toString());
            j = 0;
            for (;;)
            {
              localObject6 = VEDIO_TYPE;
              if (j >= localObject6.length) {
                break;
              }
              if (((String)localObject5).endsWith(localObject6[j])) {
                ((ArrayList)localObject2).add(((File)localObject4).getAbsolutePath());
              }
              j += 1;
            }
            j = 0;
            for (;;)
            {
              localObject6 = MUSIC_TYPE;
              if (j >= localObject6.length) {
                break;
              }
              if (((String)localObject5).endsWith(localObject6[j])) {
                ((ArrayList)localObject1).add(((File)localObject4).getAbsolutePath());
              }
              j += 1;
            }
            j = 0;
            for (;;)
            {
              localObject6 = PICTRUE_TYPE;
              if (j >= localObject6.length) {
                break;
              }
              if (((String)localObject5).endsWith(localObject6[j])) {
                localArrayList.add(((File)localObject4).getAbsolutePath());
              }
              j += 1;
            }
          }
        }
      }
      i += 1;
    }
    if (!((ArrayList)localObject2).isEmpty())
    {
      i = 0;
      while (i < ((ArrayList)localObject2).size())
      {
        if ((!((ArrayList)localObject2).isEmpty()) && (((ArrayList)localObject2).get(i) != null))
        {
          localObject3 = new File((String)((ArrayList)localObject2).get(i));
          if (((File)localObject3).exists())
          {
            try
            {
              this.vedioSize = RomSizeTools.getFileSize((File)localObject3);
            }
            catch (IOException localIOException)
            {
              localIOException.printStackTrace();
            }
          }
          else
          {
            ((ArrayList)localObject2).clear();
            this.vedioSize = 0L;
          }
          this.vedioTotalSize += this.vedioSize;
        }
        i += 1;
      }
    }
    if (!((ArrayList)localObject1).isEmpty())
    {
      i = 0;
      while (i < ((ArrayList)localObject1).size())
      {
        if ((!((ArrayList)localObject1).isEmpty()) && (((ArrayList)localObject1).get(i) != null))
        {
          localObject2 = new File((String)((ArrayList)localObject1).get(i));
          if (((File)localObject2).exists())
          {
            try
            {
              this.musicSize = RomSizeTools.getFileSize((File)localObject2);
            }
            catch (Exception localException2)
            {
              localException2.printStackTrace();
            }
          }
          else
          {
            ((ArrayList)localObject1).clear();
            this.musicSize = 0L;
          }
          this.musicTotalSize += this.musicSize;
        }
        i += 1;
      }
    }
    if (!localArrayList.isEmpty())
    {
      i = 0;
      while (i < localArrayList.size())
      {
        if ((!localArrayList.isEmpty()) && (localArrayList.get(i) != null))
        {
          localObject1 = new File((String)localArrayList.get(i));
          if (((File)localObject1).exists())
          {
            try
            {
              this.picSize = RomSizeTools.getFileSize((File)localObject1);
            }
            catch (Exception localException1)
            {
              localException1.printStackTrace();
            }
          }
          else
          {
            localArrayList.clear();
            this.picSize = 0L;
          }
          this.picTotalSize += this.picSize;
        }
        i += 1;
      }
    }
    paramString[0] = this.vedioTotalSize;
    paramString[1] = this.musicTotalSize;
    paramString[2] = this.picTotalSize;
    return paramString;
  }

  */
}
