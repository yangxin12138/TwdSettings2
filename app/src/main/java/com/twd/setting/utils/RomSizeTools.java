package com.twd.setting.utils;

import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.StatFs;
import android.text.format.Formatter;
//import com.konka.android.storage.KKStorageManager;

public class RomSizeTools
{
  private static final long M0GSize = 67108864L;
  private static final long M128GSize = 137438953472L;
  private static final long M16GSize = 17179869184L;
  private static final long M256GSize = 274877906944L;
  private static final long M2GSize = 2147483648L;
  private static final long M32GSize = 34359738368L;
  private static final long M4GSize = 4294967296L;
  private static final long M64GSize = 68719476736L;
  private static final long M8GSize = 8589934592L;
  
  public static String formatFileSize(long paramLong)
  {
    double d;
    if (paramLong >= 1073741824L)
    {
      d = paramLong;
      Double.isNaN(d);
      return String.format("%.2f GB", new Object[] { Double.valueOf(d / 1.073741824E9D) });
    }
    if (paramLong >= 1048576L)
    {
      d = paramLong;
      Double.isNaN(d);
      return String.format("%.2f MB", new Object[] { Double.valueOf(d / 1048576.0D) });
    }
    if (paramLong >= 1024L)
    {
      d = paramLong;
      Double.isNaN(d);
      return String.format("%.2f KB", new Object[] { Double.valueOf(d / 1024.0D) });
    }
    return String.format("%d B", new Object[] { Long.valueOf(paramLong) });
  }
  
  public static String formatFileSize(Context paramContext, long paramLong)
  {
    if (Build.VERSION.SDK_INT >= 26) {
      return formatFileSize(paramLong);
    }
    return Formatter.formatFileSize(paramContext, paramLong);
  }
  /*
  public static long[] getExtraStorageStateSize(Context paramContext)
  {
    paramContext = KKStorageManager.getInstance(paramContext);
    String[] arrayOfString = paramContext.getVolumePaths();
    int j = arrayOfString.length;
    long l3 = 0L;
    long l1 = 0L;
    int i = 0;
    while (i < j)
    {
      String str1 = arrayOfString[i];
      long l4;
      long l2;
      if (str1.contains("emulated"))
      {
        l4 = l3;
        l2 = l1;
      }
      else
      {
        String str2 = paramContext.getVolumeState(str1);
        l4 = l3;
        l2 = l1;
        if (str2 != null) {
          if (!str2.equals("mounted"))
          {
            l4 = l3;
            l2 = l1;
          }
          else
          {
            l4 = l3 + KKStorageManager.getTotalMemorySize(str1);
            l2 = l1 + KKStorageManager.getAvailableMemorySize(str1);
          }
        }
      }
      i += 1;
      l3 = l4;
      l1 = l2;
    }
    return new long[] { l3, l1 };
  }


   */
  /* Error */
  static long getFileSize(java.io.File paramFile)
    throws java.io.IOException
  {
    return 0;
    // Byte code:
    //   0: lconst_0
    //   1: lstore 5
    //   3: aconst_null
    //   4: astore 4
    //   6: aconst_null
    //   7: astore_3
    //   8: aconst_null
    //   9: astore_2
    //   10: aload 4
    //   12: astore_1
    //   13: aload_0
    //   14: invokevirtual 138	java/io/File:exists	()Z
    //   17: ifeq +49 -> 66
    //   20: aload 4
    //   22: astore_1
    //   23: new 140	java/io/FileInputStream
    //   26: dup
    //   27: aload_0
    //   28: invokespecial 143	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   31: astore_0
    //   32: aload_0
    //   33: invokevirtual 147	java/io/FileInputStream:available	()I
    //   36: i2l
    //   37: lstore 7
    //   39: lload 7
    //   41: lstore 5
    //   43: aload_0
    //   44: invokevirtual 150	java/io/FileInputStream:close	()V
    //   47: lload 7
    //   49: lstore 5
    //   51: goto +28 -> 79
    //   54: astore_2
    //   55: aload_0
    //   56: astore_1
    //   57: aload_2
    //   58: astore_0
    //   59: goto +98 -> 157
    //   62: astore_2
    //   63: goto +38 -> 101
    //   66: aload 4
    //   68: astore_1
    //   69: ldc -104
    //   71: ldc -102
    //   73: invokestatic 160	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   76: pop
    //   77: aload_2
    //   78: astore_0
    //   79: lload 5
    //   81: lstore 7
    //   83: aload_0
    //   84: ifnull +70 -> 154
    //   87: aload_0
    //   88: invokevirtual 150	java/io/FileInputStream:close	()V
    //   91: lload 5
    //   93: lreturn
    //   94: astore_0
    //   95: goto +62 -> 157
    //   98: astore_2
    //   99: aload_3
    //   100: astore_0
    //   101: aload_0
    //   102: astore_1
    //   103: new 162	java/lang/StringBuilder
    //   106: dup
    //   107: invokespecial 163	java/lang/StringBuilder:<init>	()V
    //   110: astore_3
    //   111: aload_0
    //   112: astore_1
    //   113: aload_3
    //   114: ldc -91
    //   116: invokevirtual 169	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   119: pop
    //   120: aload_0
    //   121: astore_1
    //   122: aload_3
    //   123: aload_2
    //   124: invokevirtual 173	java/io/IOException:getMessage	()Ljava/lang/String;
    //   127: invokevirtual 169	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   130: pop
    //   131: aload_0
    //   132: astore_1
    //   133: ldc -104
    //   135: aload_3
    //   136: invokevirtual 176	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   139: invokestatic 160	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   142: pop
    //   143: lload 5
    //   145: lstore 7
    //   147: aload_0
    //   148: ifnull +6 -> 154
    //   151: goto -64 -> 87
    //   154: lload 7
    //   156: lreturn
    //   157: aload_1
    //   158: ifnull +7 -> 165
    //   161: aload_1
    //   162: invokevirtual 150	java/io/FileInputStream:close	()V
    //   165: goto +5 -> 170
    //   168: aload_0
    //   169: athrow
    //   170: goto -2 -> 168
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	173	0	paramFile	java.io.File
    //   12	150	1	localObject1	Object
    //   9	1	2	localObject2	Object
    //   54	4	2	localObject3	Object
    //   62	16	2	localIOException1	java.io.IOException
    //   98	26	2	localIOException2	java.io.IOException
    //   7	129	3	localStringBuilder	StringBuilder
    //   4	63	4	localObject4	Object
    //   1	143	5	l1	long
    //   37	118	7	l2	long
    // Exception table:
    //   from	to	target	type
    //   32	39	54	finally
    //   43	47	54	finally
    //   32	39	62	java/io/IOException
    //   43	47	62	java/io/IOException
    //   13	20	94	finally
    //   23	32	94	finally
    //   69	77	94	finally
    //   103	111	94	finally
    //   113	120	94	finally
    //   122	131	94	finally
    //   133	143	94	finally
    //   13	20	98	java/io/IOException
    //   23	32	98	java/io/IOException
    //   69	77	98	java/io/IOException
  }
  
  public static long[] getRomStateSize(String[] paramArrayOfString)
  {
    int j = paramArrayOfString.length;
    long l1 = 0L;
    long l2 = 0L;
    long l3 = 0L;
    int i = 0;
    while (i < j)
    {
      String str = paramArrayOfString[i];
      StatFs localStatFs = new StatFs(str);
      localStatFs.restat(str);
      long l4;
      if (Build.VERSION.SDK_INT >= 18)
      {
        l1 = l3 + localStatFs.getAvailableBlocksLong() * localStatFs.getBlockSizeLong();
        l4 = localStatFs.getBlockCountLong();
        l3 = localStatFs.getBlockSizeLong();
      }
      else
      {
        l1 = l3 + localStatFs.getAvailableBlocks() * localStatFs.getBlockSize();
        l4 = localStatFs.getBlockCount();
        l3 = localStatFs.getBlockSize();
      }
      l2 += l4 * l3;
      i += 1;
      l3 = l1;
    }
    l1 = l2;
    if (paramArrayOfString.length > 1) {
      if (l2 > 137438953472L) {
        l1 = 274877906944L;
      } else if (l2 > 68719476736L) {
        l1 = 137438953472L;
      } else if (l2 > 34359738368L) {
        l1 = 68719476736L;
      } else if (l2 > 17179869184L) {
        l1 = 34359738368L;
      } else if (l2 > 8589934592L) {
        l1 = 17179869184L;
      } else if (l2 > 4294967296L) {
        l1 = 8589934592L;
      } else if (l2 > 2147483648L) {
        l1 = 4294967296L;
      } else if (l2 > 67108864L) {
        l1 = 2147483648L;
      } else {
        l1 = 67108864L;
      }
    }
    return new long[] { l1, l3 };
  }
  
  public static String getRomStateSizeString(long paramLong)
  {
    String str = "4GB";
    if (paramLong == 274877906944L) {
      return "256GB";
    }
    if (paramLong == 137438953472L) {
      return "128GB";
    }
    if (paramLong == 68719476736L) {
      return "64GB";
    }
    if (paramLong == 34359738368L) {
      return "32GB";
    }
    if (paramLong == 17179869184L) {
      return "16GB";
    }
    if (paramLong == 8589934592L) {
      return "8GB";
    }
    if (paramLong == 4294967296L) {
      return "4GB";
    }
    if (paramLong == 2147483648L) {
      return "2GB";
    }
    if (paramLong == 67108864L) {
      str = "0GB";
    }
    return str;
  }
}

