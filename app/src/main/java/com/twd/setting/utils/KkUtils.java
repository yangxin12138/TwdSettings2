package com.twd.setting.utils;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.RecoverySystem;
import android.provider.Settings.Global;
import android.text.TextUtils;
import android.util.Log;
//import com.konka.android.net.ethernet.EthernetDevInfo;
//import com.konka.android.net.ethernet.EthernetDevInfo.CONN_MODE;
//import com.konka.android.net.ethernet.EthernetManager;
//import com.konka.android.storage.KKStorageManager;
//import com.konka.android.system.KKConfigManager;
//import com.konka.android.system.KKConfigManager.EN_KK_SYSTEM_CONFIG_KEY_STRING;
//import com.konka.android.tv.KKAudioManager;
//import com.konka.android.tv.KKCommonManager;
//import com.konka.android.tv.KKFactoryManager;
//import com.twd.setting.SettingConfig;
import com.twd.setting.R;
import com.twd.setting.commonlibrary.Utils.ReflectUtils;
import com.twd.setting.commonlibrary.Utils.StringUtils;
import com.twd.setting.commonlibrary.Utils.ToastUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class KkUtils {
    public static final String CLASS_NAME_SYSTEM_PROPERTIES = "android.os.SystemProperties";
    public static final String KEY_SCREEN_SAVER = "persist.sys.screensaver";
    public static final String KEY_THEME_TYPE = "persist.sys.background_blue";
    public static final String KKSYS_CONFIG_FILE_PATH = "/etc/settings.ini";
    private static final int NETWORK_MOBILE = 0;
    private static final int NETWORK_NONE = -1;
    private static final int NETWORK_WIFI = 1;
    private static final String TAG = "KkUtils";

    public static boolean deleteDefaultLoginServer() {
        File localFile = new File("data/misc/konka/DefaultLoginServer.txt");
        if ((localFile.exists()) && (localFile.isFile())) {
            if (localFile.delete()) {
                return true;
            }
            Log.e("KkUtils", "失败！");
            return false;
        }
        Log.e("KkUtils", "不存在！");
        return false;
    }

    public static void execR(String paramString) {
        try {
            InputStream inputStream = Runtime.getRuntime().exec(paramString).getInputStream();
            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
            for (; ; ) {
                int i = inputStream.read();
                if (i == -1) {
                    break;
                }
                localByteArrayOutputStream.write(i);
            }
            new String(localByteArrayOutputStream.toByteArray());
            if (inputStream != null) {
                inputStream.close();
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ComponentName get4GComponentName(ConfigInfo paramConfigInfo) {
  /*  Object localObject = paramConfigInfo;
    if (paramConfigInfo == null) {
      localObject = new ConfigInfo("/etc/settings.ini");
    }
    paramConfigInfo = ((ConfigInfo)localObject).getString("4G_PACKAGE").trim();
    localObject = ((ConfigInfo)localObject).getString("4G_CLASS").trim();
    if ((!TextUtils.isEmpty(paramConfigInfo)) && (!TextUtils.isEmpty((CharSequence)localObject))) {
      return new ComponentName(paramConfigInfo, (String)localObject);
    }

   */
        return null;
    }

    public static String getBluetoothMacAddr() {
 /*   try
    {
      localObject = BluetoothAdapter.getDefaultAdapter();
      if (localObject != null) {
        localObject = ((BluetoothAdapter)localObject).getAddress();
      }
    }
    catch (Exception localException)
    {
      Object localObject;
      for (;;) {}
    }
    localObject = null;
    if (localObject == null) {
      return null;
    }
    return ((String)localObject).trim().toUpperCase(Locale.ROOT);

  */
        return "8:8:8:8:8:8";
    }

    public static String getBluetoothRemoteTip() {
        return new ConfigInfo("/etc/settings.ini").getString("REMOTE").trim();
    }

    public static ComponentName getCommonSettingComponentName(ConfigInfo paramConfigInfo) {
 /*   Object localObject = paramConfigInfo;
    if (paramConfigInfo == null) {
      localObject = new ConfigInfo("/etc/settings.ini");
    }
    paramConfigInfo = ((ConfigInfo)localObject).getString("COMMON_PACKAGE").trim();
    localObject = ((ConfigInfo)localObject).getString("COMMON_CLASS").trim();
    if ((!TextUtils.isEmpty(paramConfigInfo)) && (!TextUtils.isEmpty((CharSequence)localObject))) {
      return new ComponentName(paramConfigInfo, (String)localObject);
    }

  */
        return null;
    }

  public static List<String> getFilesAllName(String paramString)
  {
    File[] files = new File(paramString).listFiles();
    if (files == null) {
      return null;
    }
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    while (i < files.length)
    {
      localArrayList.add(files[i].getAbsolutePath());
      i += 1;
    }
    return localArrayList;
  }



    public static String getMac() {
/*    try
    {
      Object localObject = new StringBuffer(100);
      BufferedReader localBufferedReader = new BufferedReader(new FileReader("/sys/class/net/eth0/address"));
      char[] arrayOfChar = new char['Ѐ'];
      for (;;)
      {
        int i = localBufferedReader.read(arrayOfChar);
        if (i == -1) {
          break;
        }
        ((StringBuffer)localObject).append(String.valueOf(arrayOfChar, 0, i));
      }
      localBufferedReader.close();
      localObject = ((StringBuffer)localObject).toString().trim().toUpperCase(Locale.ROOT);
      return (String)localObject;
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }

 */
        return null;
    }

    public static String getMacAddr(Context paramContext) {
 /*   try
    {
      EthernetDevInfo localEthernetDevInfo = EthernetManager.getInstance(paramContext.getApplicationContext()).getConfig();
      paramContext = localEthernetDevInfo;
      if (localEthernetDevInfo == null)
      {
        paramContext = new EthernetDevInfo();
        paramContext.setConnectMode(EthernetDevInfo.CONN_MODE.CONN_MODE_DHCP);
      }
      paramContext = paramContext.getMacAddress().trim().toUpperCase(Locale.ROOT);
      return paramContext;
    }
    catch (Exception paramContext)
    {
      for (;;) {}
    }

  */
        return null;
    }

    public static String getMainCode(Context paramContext) {
/*    try
    {
      String str1 = KKConfigManager.getInstance(paramContext.getApplicationContext()).getStringConfig(KKConfigManager.EN_KK_SYSTEM_CONFIG_KEY_STRING.PANEL_SOFTWARE_ID);
      String str2 = KKConfigManager.getInstance(paramContext.getApplicationContext()).getStringConfig(KKConfigManager.EN_KK_SYSTEM_CONFIG_KEY_STRING.MAIN_SOFTWARE_ID);
      if (((str1 == null) && (str2 == null)) || (("".equals(str1)) && ("".equals(str2))))
      {
        paramContext = KKCommonManager.getInstance(paramContext.getApplicationContext()).getSoftwareID();
        return paramContext;
      }
      return str2;
    }
    catch (Exception paramContext)
    {
      for (;;) {}
    }

 */
        return null;
    }

    public static int getNetWorkState(Context paramContext) {
/*    paramContext = ((ConnectivityManager)paramContext.getSystemService("connectivity")).getActiveNetworkInfo();
    if ((paramContext != null) && (paramContext.isConnected()))
    {
      if (paramContext.getType() == 1) {
        return 1;
      }
      if (paramContext.getType() == 0) {
        return 0;
      }
    }

 */
        return -1;
    }

    public static String getOffLineInstallPath() {
/*    String str = UsbScanTool.getOfflineZipMountPath();
    if (SettingConfig.IS_DEBUG)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("getAllExternalSdcardPath 离线更新路径 ：");
      localStringBuilder.append(str);
      Log.d("KkUtils", localStringBuilder.toString());
    }
    return str;

 */
        return null;
    }

    public static String getPlatform(Context paramContext) {
/*    try
    {
      paramContext = KKCommonManager.getInstance(paramContext.getApplicationContext()).getPlatform();
      return paramContext;
    }
    catch (Exception paramContext)
    {
      for (;;) {}
    }

 */
        return "MTK6582";
    }

    public static ComponentName getProjectorComponentName(ConfigInfo paramConfigInfo) {
 /*   Object localObject = paramConfigInfo;
    if (paramConfigInfo == null) {
      localObject = new ConfigInfo("/etc/settings.ini");
    }
    paramConfigInfo = ((ConfigInfo)localObject).getString("PROJECTOR_PACKAGE").trim();
    localObject = ((ConfigInfo)localObject).getString("PROJECTOR_CLASS").trim();
    if ((!TextUtils.isEmpty(paramConfigInfo)) && (!TextUtils.isEmpty((CharSequence)localObject))) {
      return new ComponentName(paramConfigInfo, (String)localObject);
    }

  */
        return null;
    }

    public static String getSerialNumber(Context paramContext) {
/*    try
    {
      paramContext = KKFactoryManager.getInstance(paramContext.getApplicationContext()).getSerialNumber();
      if (paramContext == null) {
        break label57;
      }
      paramContext = new String(paramContext, StandardCharsets.UTF_8);
      return paramContext;
    }
    catch (NoClassDefFoundError paramContext)
    {
      for (;;) {}
    }
    catch (RuntimeException paramContext)
    {
      for (;;) {}
    }
    catch (Exception paramContext)
    {
      label57:
      for (;;) {}
    }
    Log.e("KkUtils", "get serial num error 3333!");
    return null;
    Log.e("KkUtils", "get serial num error 2222!");
    return null;
    Log.e("KkUtils", "get serial num error 1111!"); */
        return null;

    }

    public static byte[] getSerialNumberByte(Context paramContext) {
/*    try
    {
      paramContext = KKFactoryManager.getInstance(paramContext.getApplicationContext()).getSerialNumber();
      return paramContext;
    }
    catch (NoClassDefFoundError paramContext)
    {
      for (;;) {}
    }
    catch (RuntimeException paramContext)
    {
      for (;;) {}
    }
    catch (Exception paramContext)
    {
      label43:
      for (;;) {}
    }
    Log.e("KkUtils", "get serial byte num error 3333!");
    break label43;
    Log.e("KkUtils", "get serial byte num error 2222!");
    break label43;
    Log.e("KkUtils", "get serial byte num error 1111!");

 */
        return null;
    }

    public static String getSeries() {
        //return new ConfigInfo("/etc/main.ini").getString("SERIES").trim();
        return "1234567890000";
    }

    public static ComponentName getSourceComponentName(ConfigInfo paramConfigInfo) {
/*    Object localObject = paramConfigInfo;
    if (paramConfigInfo == null) {
      localObject = new ConfigInfo("/etc/settings.ini");
    }
    paramConfigInfo = ((ConfigInfo)localObject).getString("SOURCE_PACKAGE").trim();
    localObject = ((ConfigInfo)localObject).getString("SOURCE_CLASS").trim();
    if ((!TextUtils.isEmpty(paramConfigInfo)) && (!TextUtils.isEmpty((CharSequence)localObject))) {
      return new ComponentName(paramConfigInfo, (String)localObject);
    }

 */
        return null;
    }

    public static String getSysThemeType() {
        return getSystemProperties("persist.sys.background_blue", "");
    }

    public static int getSysThemeTypeResId() {
        String str = getSysThemeType();
        str.hashCode();
        if (!str.equals("1")) {
            if (!str.equals("2")) {
                Log.d("KkUtils", "主题 THEME_ICE_CREAM_BLUE");
                return R.style.Theme_TVSettings2_IceCreamBlue;
            }
            Log.d("KkUtils", "主题 THEME_STARRY_SKY_BLUE");
            return R.style.Theme_TVSettings2_StarrySkyBlue;
        }
        Log.d("KkUtils", "主题 THEME_WHITE");
        return R.style.Theme_TVSettings2_White;

    }

    public static String getSysVersion(Context paramContext) {
 /*   try
    {
      paramContext = KKCommonManager.getInstance(paramContext.getApplicationContext()).getVersion();
      return paramContext;
    }
    catch (Exception paramContext)
    {
      for (;;) {}
    }

  */
        return null;
    }

    public static String getSystemProperties(String paramString1, String paramString2) {
        Object localObject1 = null;
        try {
            Object localObject2 = Class.forName("android.os.SystemProperties");
            Method localMethod = ((Class) localObject2).getMethod("get", new Class[]{String.class});
            localMethod.setAccessible(true);
            localObject2 = localMethod.invoke(localObject2, new Object[]{paramString1});
            //if (SettingConfig.IS_DEBUG)
            {
                StringBuilder str = new StringBuilder();
                str.append("getSystemProperties  value=");
                str.append(localObject2);
                Log.d("KkUtils", paramString1.toString());
            }
            paramString1 = (String) localObject1;
            if ((localObject2 instanceof String)) {
                paramString1 = (String) localObject2;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (StringUtils.isTrimEmpty(paramString1)) {
            return paramString2;
        }
        return paramString1;
    }

    public static String getType(Context paramContext) {
/*    try
    {
      paramContext = KKCommonManager.getInstance(paramContext.getApplicationContext()).getType();
      return paramContext;
    }
    catch (Exception paramContext)
    {
      for (;;) {}
    }

 */
        return "TWD";
    }

    public static String getUSBUpgradeFilePath(Context paramContext) {
/*    try
    {
      paramContext = KKCommonManager.getInstance(paramContext.getApplicationContext()).getUSBUpgradeFilePath();
      return paramContext;
    }
    catch (Exception paramContext)
    {
      for (;;) {}
    }

 */
        return null;
    }

    public static String getUsbPath(Context paramContext) {
/*    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append(KKStorageManager.getInstance(paramContext).getVolumePaths().length);
    ((StringBuilder)localObject).append("");
    HLog.d("KkUtils", ((StringBuilder)localObject).toString());
    localObject = KKStorageManager.getInstance(paramContext).getVolumePaths();
    if (localObject.length > 0)
    {
      localObject = localObject[0];
      if (KKStorageManager.getInstance(paramContext).getVolumeState((String)localObject).equals("mounted")) {
        return (String)localObject;
      }
    }

 */
        return null;
    }

    public static String getWifiMacAddr(Context paramContext) {
 /*   try
    {
      paramContext = (WifiManager)paramContext.getApplicationContext().getSystemService("wifi");
      if ((paramContext != null) && (paramContext.isWifiEnabled()))
      {
        paramContext = paramContext.getConnectionInfo().getMacAddress();
        break label107;
      }
      localStringBuilder = new StringBuilder();
      localStringBuilder.append("getWifiMacAddr 网络不可用 wifiManager=");
      localStringBuilder.append(paramContext);
      HLog.e("KkUtils", localStringBuilder.toString());
    }
    catch (Exception paramContext)
    {
      localStringBuilder = new StringBuilder();
      localStringBuilder.append("getWifiMacAddr error wifiMacStr=");
      localStringBuilder.append(paramContext.getMessage());
      HLog.e("KkUtils", localStringBuilder.toString());
    }
    paramContext = null;
    label107:
    if ((!StringUtils.isTrimEmpty(paramContext)) && (!"02:00:00:00:00:00".equals(paramContext))) {
      return paramContext.trim().toUpperCase(Locale.ROOT);
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("getWifiMacAddr wifiMacStr=");
    localStringBuilder.append(paramContext);
    Log.e("KkUtils", localStringBuilder.toString());
    return null;

  */
        return "8:8:8:8:8:6";
    }

    @SuppressLint("MissingPermission")
    public static void installPackage(Context paramContext, File paramFile)
            throws IOException {
        RecoverySystem.installPackage(paramContext, paramFile);
    }

    public static boolean isDebugEnable(Context paramContext) {
        //return Settings.Global.getInt(paramContext.getContentResolver(), "adb_enabled", 1) == 1;
        return false;
    }

    public static boolean isEthernetEnable(Context paramContext) {
/*    try
    {
      paramContext = ReflectUtils.reflect(EthernetManager.getInstance(paramContext)).method("isConfigured").get();
      localStringBuilder = new StringBuilder();
      localStringBuilder.append("KkUtils.isEthernetEnable result=");
      localStringBuilder.append(paramContext);
      HLog.d("KkUtils", localStringBuilder.toString());
      if (paramContext != null)
      {
        boolean bool = ((Boolean)paramContext).booleanValue();
        return bool;
      }
    }
    catch (Exception paramContext)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("KkUtils.isEthernetEnable error");
      localStringBuilder.append(paramContext.getMessage());
      Log.e("KkUtils", localStringBuilder.toString());
    }
    return true;

 */
        return false;
    }

    public static int isFormatMAC(String paramString) {
        if ((paramString != null) && (!paramString.equals(""))) {
            if (paramString.length() != 12) {
                return 1;
            }
            if (!paramString.matches("-?[0-9a-fA-F]+")) {
                return 2;
            }
            return 3;
        }
        return 0;
    }

    public static int isFormatSN(String paramString) {
        if ((paramString != null) && (!paramString.equals(""))) {
            if (paramString.length() != 20) {
                return 1;
            }
            if (!paramString.matches("-?[0-9a-zA-Z]+")) {
                return 2;
            }
            return 3;
        }
        return 0;
    }

    public static boolean isIncusAudioEnable(Context paramContext) {
  /*  try
    {
      boolean bool = KKAudioManager.getInstance(paramContext).isIncusAudioEnable();
      return bool;
    }
    catch (Exception paramContext)
    {
      for (;;) {}
    }

   */
        return false;
    }

    public static boolean isLan() {
        try {
            ConfigInfo localConfigInfo = new ConfigInfo("/etc/settings.ini");
            if (localConfigInfo.getString("LAN") != null) {
                boolean bool = localConfigInfo.getString("LAN").trim().equals("true");
                return bool;
            }
            return true;
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return false;
    }

    public static boolean isNetMac() {
        ConfigInfo localConfigInfo = new ConfigInfo("/etc/settings.ini");
        if (localConfigInfo.getString("NETMAC") != null) {
            return localConfigInfo.getString("NETMAC").trim().equals("true");
        }
        return false;
    }

    public static boolean isScreenSaverEnable(Context paramContext) {
        return "1".equals(getSystemProperties("persist.sys.screensaver", "1"));
    }

    public static boolean isTestSwitchEnable() {
        File localFile = new File("data/misc/konka/DefaultLoginServer.txt");
        return (localFile.exists()) && (localFile.isFile());
    }

    public static boolean isUnSupportBluetooth(ConfigInfo paramConfigInfo) {
 /*   Object localObject = paramConfigInfo;
    if (paramConfigInfo == null) {
      localObject = new ConfigInfo("/etc/settings.ini");
    }
    localObject = ((ConfigInfo)localObject).getString("BLUETOOTH");
    paramConfigInfo = (ConfigInfo)localObject;
    if (localObject != null) {
      paramConfigInfo = ((String)localObject).trim();
    }
    return "false".equals(paramConfigInfo);

  */
        return false;
    }

    public static boolean isUnSupportCommonSetting(ConfigInfo paramConfigInfo) {
 /*   Object localObject = paramConfigInfo;
    if (paramConfigInfo == null) {
      localObject = new ConfigInfo("/etc/settings.ini");
    }
    localObject = ((ConfigInfo)localObject).getString("COMMON_SETTINGS");
    paramConfigInfo = (ConfigInfo)localObject;
    if (localObject != null) {
      paramConfigInfo = ((String)localObject).trim();
    }
    return "false".equals(paramConfigInfo);

  */
        return false;
    }

    public static boolean isUnSupportProjector(ConfigInfo paramConfigInfo) {
 /*   Object localObject = paramConfigInfo;
    if (paramConfigInfo == null) {
      localObject = new ConfigInfo("/etc/settings.ini");
    }
    localObject = ((ConfigInfo)localObject).getString("PROJECTOR");
    paramConfigInfo = (ConfigInfo)localObject;
    if (localObject != null) {
      paramConfigInfo = ((String)localObject).trim();
    }
    return "false".equals(paramConfigInfo);

  */
        return false;
    }

    public static boolean isUnSupportSource(ConfigInfo paramConfigInfo) {
/*    Object localObject = paramConfigInfo;
    if (paramConfigInfo == null) {
      localObject = new ConfigInfo("/etc/settings.ini");
    }
    localObject = ((ConfigInfo)localObject).getString("SOURCE");
    paramConfigInfo = (ConfigInfo)localObject;
    if (localObject != null) {
      paramConfigInfo = ((String)localObject).trim();
    }
    return "false".equals(paramConfigInfo);

 */
        return false;
    }

    public static boolean isWifiMac() {
        ConfigInfo localConfigInfo = new ConfigInfo("/etc/settings.ini");
        if (localConfigInfo.getString("WIFIMAC") != null) {
            return localConfigInfo.getString("WIFIMAC").trim().equals("true");
        }
        return false;
    }


    public static void resetSystem(Context paramContext) {
        try {
        //    KKCommonManager.getInstance(paramContext).userReset();
            return;
        } catch (NoSuchMethodError NoSuchMethodError) {
            NoSuchMethodError.printStackTrace();
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append("resetSystem error ：");
            localStringBuilder.append(NoSuchMethodError.getMessage());
            Log.e("KkUtils", localStringBuilder.toString());
            ToastUtils.showShort("操作失败");
        }
    }

    public static String searchUsb(String paramString1, String paramString2) {
        Object localObject = new ArrayList();
        try {
            List localList = getFilesAllName(paramString1);
            localObject = localList;
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        if (localObject != null) {
            Iterator iterator = ((List) localObject).iterator();
            while (iterator.hasNext()) {
                String str = (String) iterator.next();
                HLog.d("KkUtils", str);
                StringBuilder localStringBuilder = new StringBuilder();
                localStringBuilder.append(paramString1);
                localStringBuilder.append(paramString2);
                if (str.startsWith(localStringBuilder.toString())) {
                    String[] strs = str.split(paramString2);
                    String[] splits = strs[(strs.length - 1)].toUpperCase(Locale.ROOT).split(".TXT");

                    return strs[0];
                }
            }
        }
        return null;
    }

    public static void setDebugEnable(Context paramContext, boolean paramBoolean) {
   //     Settings.Global.putInt(paramContext.getContentResolver(), "adb_enabled", paramBoolean ^ true);
    }


    public static boolean setScreenSaverEnable(Context paramContext, boolean paramBoolean) {
        String str;
        if (paramBoolean) {
            str = "1";
        } else {
            str = "0";
        }
        return setSystemProperties("persist.sys.screensaver", str);
    }

    public static boolean setSystemProperties(String paramString1, String paramString2) {
        try {
            Class localClass = Class.forName("android.os.SystemProperties");
            Method localMethod = localClass.getMethod("set", new Class[]{String.class, String.class});
            localMethod.setAccessible(true);
            localMethod.invoke(localClass, new Object[]{paramString1, paramString2});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean writeDefaultLoginServer()
            throws IOException, URISyntaxException {
        if (Environment.getExternalStorageState().equals("mounted")) {
            FileOutputStream localFileOutputStream = new FileOutputStream(new File("data/misc/konka/DefaultLoginServer.txt"));
            OutputStreamWriter localOutputStreamWriter = new OutputStreamWriter(localFileOutputStream, "utf-8");
            localOutputStreamWriter.write("DEFAULT_LOGIN_SERVER = test.kkapp.com:6612");
            localOutputStreamWriter.close();
            localFileOutputStream.close();
            execR("chmod 777 data/misc/konka/DefaultLoginServer.txt");
            return true;
        }
        return false;
    }
}