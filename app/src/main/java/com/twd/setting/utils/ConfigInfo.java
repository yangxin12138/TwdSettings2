package com.twd.setting.utils;

import java.util.Properties;

public class ConfigInfo {
    private Properties properties;

    public ConfigInfo(String paramString) {
        readSystemProperties(paramString);
    }

    public String getString(String paramString) {
        Properties localProperties = properties;
        if (localProperties == null) {
            return "";
        }
        if (!localProperties.containsKey(paramString)) {
            return "";
        }
        return String.valueOf(properties.get(paramString));
    }

    /* Error */
    public Properties readSystemProperties(String paramString) {
        return properties;
        // Byte code:
        //   0: aconst_null
        //   1: astore 4
        //   3: aconst_null
        //   4: astore_3
        //   5: aload_1
        //   6: ifnonnull +5 -> 11
        //   9: aconst_null
        //   10: areturn
        //   11: aload_1
        //   12: astore_2
        //   13: aload_1
        //   14: ldc 44
        //   16: invokevirtual 48	java/lang/String:startsWith	(Ljava/lang/String;)Z
        //   19: ifne +29 -> 48
        //   22: new 50	java/lang/StringBuilder
        //   25: dup
        //   26: invokespecial 51	java/lang/StringBuilder:<init>	()V
        //   29: astore_2
        //   30: aload_2
        //   31: ldc 44
        //   33: invokevirtual 55	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   36: pop
        //   37: aload_2
        //   38: aload_1
        //   39: invokevirtual 55	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   42: pop
        //   43: aload_2
        //   44: invokevirtual 59	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   47: astore_2
        //   48: aload_3
        //   49: astore_1
        //   50: new 50	java/lang/StringBuilder
        //   53: dup
        //   54: invokespecial 51	java/lang/StringBuilder:<init>	()V
        //   57: astore 5
        //   59: aload_3
        //   60: astore_1
        //   61: aload 5
        //   63: invokestatic 65	android/os/Environment:getRootDirectory	()Ljava/io/File;
        //   66: invokevirtual 70	java/io/File:getCanonicalPath	()Ljava/lang/String;
        //   69: invokevirtual 55	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   72: pop
        //   73: aload_3
        //   74: astore_1
        //   75: aload 5
        //   77: aload_2
        //   78: invokevirtual 55	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   81: pop
        //   82: aload_3
        //   83: astore_1
        //   84: aload 5
        //   86: invokevirtual 59	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   89: astore_2
        //   90: aload_3
        //   91: astore_1
        //   92: new 50	java/lang/StringBuilder
        //   95: dup
        //   96: invokespecial 51	java/lang/StringBuilder:<init>	()V
        //   99: astore 5
        //   101: aload_3
        //   102: astore_1
        //   103: aload 5
        //   105: ldc 72
        //   107: invokevirtual 55	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   110: pop
        //   111: aload_3
        //   112: astore_1
        //   113: aload 5
        //   115: aload_2
        //   116: invokevirtual 55	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   119: pop
        //   120: aload_3
        //   121: astore_1
        //   122: ldc 74
        //   124: aload 5
        //   126: invokevirtual 59	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   129: invokestatic 80	com/konka/settingcenter/utils/HLog:d	(Ljava/lang/String;Ljava/lang/String;)V
        //   132: aload_3
        //   133: astore_1
        //   134: aload_0
        //   135: new 24	java/util/Properties
        //   138: dup
        //   139: invokespecial 81	java/util/Properties:<init>	()V
        //   142: putfield 20	com/konka/settingcenter/utils/ConfigInfo:properties	Ljava/util/Properties;
        //   145: aload_3
        //   146: astore_1
        //   147: new 83	java/io/BufferedInputStream
        //   150: dup
        //   151: new 85	java/io/FileInputStream
        //   154: dup
        //   155: aload_2
        //   156: invokespecial 87	java/io/FileInputStream:<init>	(Ljava/lang/String;)V
        //   159: invokespecial 90	java/io/BufferedInputStream:<init>	(Ljava/io/InputStream;)V
        //   162: astore_2
        //   163: aload_0
        //   164: getfield 20	com/konka/settingcenter/utils/ConfigInfo:properties	Ljava/util/Properties;
        //   167: new 92	java/io/InputStreamReader
        //   170: dup
        //   171: aload_2
        //   172: getstatic 98	java/nio/charset/StandardCharsets:UTF_8	Ljava/nio/charset/Charset;
        //   175: invokespecial 101	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;Ljava/nio/charset/Charset;)V
        //   178: invokevirtual 105	java/util/Properties:load	(Ljava/io/Reader;)V
        //   181: aload_2
        //   182: invokevirtual 110	java/io/InputStream:close	()V
        //   185: goto +45 -> 230
        //   188: astore_3
        //   189: aload_2
        //   190: astore_1
        //   191: aload_3
        //   192: astore_2
        //   193: goto +42 -> 235
        //   196: astore_3
        //   197: goto +11 -> 208
        //   200: astore_2
        //   201: goto +34 -> 235
        //   204: astore_3
        //   205: aload 4
        //   207: astore_2
        //   208: aload_2
        //   209: astore_1
        //   210: aload_3
        //   211: invokevirtual 113	java/lang/Exception:printStackTrace	()V
        //   214: aload_2
        //   215: ifnull +15 -> 230
        //   218: aload_2
        //   219: invokevirtual 110	java/io/InputStream:close	()V
        //   222: goto +8 -> 230
        //   225: astore_1
        //   226: aload_1
        //   227: invokevirtual 114	java/io/IOException:printStackTrace	()V
        //   230: aload_0
        //   231: getfield 20	com/konka/settingcenter/utils/ConfigInfo:properties	Ljava/util/Properties;
        //   234: areturn
        //   235: aload_1
        //   236: ifnull +15 -> 251
        //   239: aload_1
        //   240: invokevirtual 110	java/io/InputStream:close	()V
        //   243: goto +8 -> 251
        //   246: astore_1
        //   247: aload_1
        //   248: invokevirtual 114	java/io/IOException:printStackTrace	()V
        //   251: aload_2
        //   252: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	253	0	this	ConfigInfo
        //   0	253	1	paramString	String
        //   12	181	2	localObject1	Object
        //   200	1	2	localObject2	Object
        //   207	45	2	localObject3	Object
        //   4	142	3	localObject4	Object
        //   188	4	3	localObject5	Object
        //   196	1	3	localException1	Exception
        //   204	7	3	localException2	Exception
        //   1	205	4	localObject6	Object
        //   57	68	5	localStringBuilder	StringBuilder
        // Exception table:
        //   from	to	target	type
        //   163	181	188	finally
        //   163	181	196	java/lang/Exception
        //   50	59	200	finally
        //   61	73	200	finally
        //   75	82	200	finally
        //   84	90	200	finally
        //   92	101	200	finally
        //   103	111	200	finally
        //   113	120	200	finally
        //   122	132	200	finally
        //   134	145	200	finally
        //   147	163	200	finally
        //   210	214	200	finally
        //   50	59	204	java/lang/Exception
        //   61	73	204	java/lang/Exception
        //   75	82	204	java/lang/Exception
        //   84	90	204	java/lang/Exception
        //   92	101	204	java/lang/Exception
        //   103	111	204	java/lang/Exception
        //   113	120	204	java/lang/Exception
        //   122	132	204	java/lang/Exception
        //   134	145	204	java/lang/Exception
        //   147	163	204	java/lang/Exception
        //   181	185	225	java/io/IOException
        //   218	222	225	java/io/IOException
        //   239	243	246	java/io/IOException
    }
}