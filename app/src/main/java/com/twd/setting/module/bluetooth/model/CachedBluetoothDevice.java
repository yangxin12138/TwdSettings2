package com.twd.setting.module.bluetooth.model;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothUuid;
import java.util.UUID;

import android.os.Build;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.Log;
//import com.twd.setting.SettingConfig;
import com.twd.setting.commonlibrary.Utils.ReflectUtils;
import com.twd.setting.module.bluetooth.bluetoothlib.A2dpProfile;
import com.twd.setting.module.bluetooth.bluetoothlib.BluetoothHandsetFilter;
import com.twd.setting.module.bluetooth.bluetoothlib.HidProfile;
import com.twd.setting.module.bluetooth.bluetoothlib.LocalBluetoothProfile;
import com.twd.setting.module.bluetooth.bluetoothlib.LocalBluetoothProfileManager;
//import com.twd.setting.utils.HLog;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class CachedBluetoothDevice
        implements Comparable<CachedBluetoothDevice> {
    private static final String TAG = "CachedBluetoothDevice";
    private int currentStatus;
    private boolean firstBtnItem;
    private BluetoothDevice mDevice;
    private volatile boolean mJustDiscovered;
    private BluetoothAdapter mLocalAdapter;
    private final Object mProfileLock;
    private LocalBluetoothProfileManager mProfileManager;
    private final List<LocalBluetoothProfile> mProfiles;
    int mRssi;
    private int newStatus;
    private volatile int sortValue;
    private String statusStr;

    public CachedBluetoothDevice(LocalBluetoothProfile localBluetoothProfile, BluetoothDevice paramBluetoothDevice) {
        mProfiles = new ArrayList<LocalBluetoothProfile>();
        mProfileLock = new Object();
        currentStatus = 0;
        newStatus = 0;
        firstBtnItem = false;
        mJustDiscovered = false;
        mLocalAdapter = BluetoothAdapter.getDefaultAdapter();
        if (localBluetoothProfile != null) {
            mProfiles.add(localBluetoothProfile);
        }
        mDevice = paramBluetoothDevice;
    }

    public CachedBluetoothDevice(LocalBluetoothProfileManager localBluetoothProfileManager, BluetoothDevice paramBluetoothDevice) {
        mProfiles = new ArrayList<LocalBluetoothProfile>();
        mProfileLock = new Object();
        currentStatus = 0;
        newStatus = 0;
        firstBtnItem = false;
        mJustDiscovered = false;
        mLocalAdapter = BluetoothAdapter.getDefaultAdapter();
        mProfileManager = localBluetoothProfileManager;
        mDevice = paramBluetoothDevice;
        LocalBluetoothProfile profile = localBluetoothProfileManager.getProfileByName(getDeviceProfileName());
        if (profile != null) {
            mProfiles.add(profile);
        }
    }

    public CachedBluetoothDevice(boolean paramBoolean) {
        mProfiles = new ArrayList();
        mProfileLock = new Object();
        currentStatus = 0;
        newStatus = 0;
        firstBtnItem = false;
        mJustDiscovered = false;
        firstBtnItem = paramBoolean;
    }

    private void connectWithoutResettingTimer() {
        try {
            if (mProfiles.isEmpty()) {
                Log.d(TAG, "No profiles. Maybe we will connect later");
                mProfiles.add(mProfileManager.getProfileByName("A2DP"));
                mProfiles.add(mProfileManager.getProfileByName("HID"));
            }
            int i = 0;
            Iterator iterator = mProfiles.iterator();
            while (iterator.hasNext()) {
                LocalBluetoothProfile localLocalBluetoothProfile = (LocalBluetoothProfile) iterator.next();
                if (localLocalBluetoothProfile.isAutoConnectable()) {
                    i += 1;
                    connectInt(localLocalBluetoothProfile);
                }
            }
            Log.d("CachedBluetoothDevice", "Preferred profiles = " + i);
            return;
        } catch (Exception e) {

        } finally {
        }

    }

    private String describe(LocalBluetoothProfile localBluetoothProfile) {
        if (mDevice == null) {
            return "";
        }
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Address:");
        localStringBuilder.append(mDevice);
        if (localBluetoothProfile != null) {
            localStringBuilder.append(" Profile:");
            localStringBuilder.append(localBluetoothProfile);
        }
        return localStringBuilder.toString();
    }

    private boolean ensurePaired() {
        if (getBondState() == 10) {
            startPairing();
            return false;
        }
        return true;
    }

    @SuppressLint("MissingPermission")
    private String getDeviceProfileName() {
        if (mDevice == null) {
            return null;
        }
        if (BluetoothHandsetFilter.isNameMatchExName(BluetoothHandsetFilter.REMOTE_CONTROL_NAME, getName())) {
            return "HID";
        }
        BluetoothClass localBluetoothClass = mDevice.getBluetoothClass();
        if (localBluetoothClass != null) {
            if (localBluetoothClass.getMajorDeviceClass() == 1280) {
                return "HID";
            }
            if (isA2dpDevice(localBluetoothClass)) {
                return "A2DP";
            }
        }
/*    if (BluetoothUuid.containsAnyUuid(mDevice.getUuids(), HidProfile.SINK_UUIDS)) {
      return "HID";
    }
    if (BluetoothUuid.containsAnyUuid(mDevice.getUuids(), A2dpProfile.SINK_UUIDS)) {
      return "A2DP";
    }*/
        //   if (SettingConfig.IS_DEBUG)
        {
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append("getDeviceProfileName() profile 配置文件匹配失败 getDeviceClass");
            Integer localInteger;
            if (localBluetoothClass == null) {
                localInteger = null;
            } else {
                localInteger = Integer.valueOf(localBluetoothClass.getDeviceClass());
            }
            localStringBuilder.append(localInteger);
            localStringBuilder.append("  getMajorDeviceClass");
            if (localBluetoothClass == null) {
                localInteger = null;
            } else {
                localInteger = Integer.valueOf(localBluetoothClass.getMajorDeviceClass());
            }
            localStringBuilder.append(localInteger);
            localStringBuilder.append("  name=");
            localStringBuilder.append(getName());
            localStringBuilder.append("  uuid=");
            localStringBuilder.append(Arrays.toString(mDevice.getUuids()));
            Log.d("CachedBluetoothDevice", localStringBuilder.toString());
        }
        return null;
    }

    private boolean isA2dpDevice(BluetoothClass paramBluetoothClass) {
        if (paramBluetoothClass.hasService(262144)) {
            return true;
        }
        int i = paramBluetoothClass.getDeviceClass();
        return (i == 1044) || (i == 1048) || (i == 1056) || (i == 1064);
    }

    private String updateName(String paramString) {
        if ("KK302".equals(paramString)) {
            return "KW-YF302";
        }
        if ("KK304".equals(paramString)) {
            return "KW-YF304";
        }
        String str = paramString;
        if ("KK305".equals(paramString)) {
            str = "KW-YF305";
        }
        return str;
    }

    public boolean cancelBondProcess() {
    try
    {
      boolean bool = ((Boolean)ReflectUtils.reflect(this).method("cancelBondProcess").get()).booleanValue();
      return bool;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
        return false;
    }

    public int compareTo(CachedBluetoothDevice paramCachedBluetoothDevice) {
        //   throw new Runtime("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\r\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\r\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\r\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.provideAs(TypeTransformer.java:780)\r\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.enexpr(TypeTransformer.java:659)\r\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:719)\r\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:703)\r\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.e2expr(TypeTransformer.java:629)\r\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:716)\r\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:703)\r\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.s2stmt(TypeTransformer.java:820)\r\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.sxStmt(TypeTransformer.java:843)\r\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:206)\r\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\r\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\r\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\r\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\r\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\r\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\r\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\r\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\r\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\r\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\r\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\r\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\r\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\r\n");
        return 0;
    }

    public void connect() {
        if (!ensurePaired()) {
            return;
        }
        if (mDevice == null) {
            return;
        }
        connectWithoutResettingTimer();
    }

    void connectInt(LocalBluetoothProfile paramLocalBluetoothProfile) {
        try {
            boolean bool = ensurePaired();
            if (!bool) {
                return;
            }
            if (paramLocalBluetoothProfile.connect(mDevice)) {
                //if (SettingConfig.IS_DEBUG)
                {
                    StringBuilder localStringBuilder = new StringBuilder();
                    localStringBuilder.append("Command sent successfully:CONNECT ");
                    localStringBuilder.append(describe(paramLocalBluetoothProfile));
                    Log.d("CachedBluetoothDevice", localStringBuilder.toString());
                }
                return;
            }
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append("Failed to connect ");
            localStringBuilder.append(paramLocalBluetoothProfile.toString());
            localStringBuilder.append(" to ");
            localStringBuilder.append(getName());
            Log.d("CachedBluetoothDevice", localStringBuilder.toString());
            return;
        } finally {
        }
    }

    public void disconnect() {
        if (mProfiles.isEmpty()) {
            return;
        }
        Iterator iterator = mProfiles.iterator();
        while (iterator.hasNext()) {
            LocalBluetoothProfile localLocalBluetoothProfile = (LocalBluetoothProfile) iterator.next();
            if (localLocalBluetoothProfile != null) {
                BluetoothDevice localBluetoothDevice = mDevice;
                if (localBluetoothDevice != null) {
                    localLocalBluetoothProfile.disconnect(localBluetoothDevice);
                }
            }
        }
    }

    public boolean equals(Object paramObject) {
        boolean bool3 = paramObject instanceof CachedBluetoothDevice;
        boolean bool2 = false;
        boolean bool1 = bool2;
        if (bool3) {
            if (this != paramObject) {
                BluetoothDevice localBluetoothDevice = mDevice;
                bool1 = bool2;
                if (localBluetoothDevice != null) {
                    bool1 = bool2;
                    if (!localBluetoothDevice.equals(((CachedBluetoothDevice) paramObject).mDevice)) {
                    }
                }
            } else {
                bool1 = true;
            }
        }
        return bool1;
    }

    public String getAddress() {
        if (mDevice == null) {
            return "";
        }
        return mDevice.getAddress();
    }

    @SuppressLint("MissingPermission")
    public int getBondState() {
        BluetoothDevice localBluetoothDevice = mDevice;
        if (mDevice == null) {
            return 10;
        }
        return localBluetoothDevice.getBondState();
    }

    public int getCurrentStatus() {
        return currentStatus;
    }

    public BluetoothDevice getDevice() {
        return mDevice;
    }

    public LocalBluetoothProfile getLocalBtProfile() {
        if (mProfiles.isEmpty()) {
            return null;
        }
        Iterator iterator = mProfiles.iterator();
        while (iterator.hasNext()) {
            LocalBluetoothProfile localLocalBluetoothProfile = (LocalBluetoothProfile) iterator.next();
            if (localLocalBluetoothProfile != null) {
                return localLocalBluetoothProfile;
            }
        }
        return null;
    }

    @SuppressLint("MissingPermission")
    public String getName() {
        if (mDevice == null) {
            return null;
        }
        String version = "";
        if (Build.VERSION.SDK_INT >= 30) {
            version = mDevice.getAlias();
        } else {
            version = mDevice.getName();
        }
        String str = updateName(version);
        if (TextUtils.isEmpty(str)) {
            str = getAddress();
        }
        return str;
    }

    public String getNameRssi() {
        String str = getName() + "  " + mRssi;
        return str;
    }

    public int getNewStatus() {
        return newStatus;
    }

    public int getProfileConnectionState(LocalBluetoothProfile paramLocalBluetoothProfile) {
        if (paramLocalBluetoothProfile != null) {
            if (mDevice != null) {
                return paramLocalBluetoothProfile.getConnectionStatus(mDevice);
            }
        }
        return 0;
    }

    public int getRssi() {
        return mRssi;
    }

    public int getSortValue() {
        return sortValue;
    }

    public String getStatusStr() {
        return statusStr;
    }

    public int hashCode() {
        if (mDevice == null) {
            return 0;
        }
        return mDevice.hashCode();
    }

    @SuppressLint("MissingPermission")
    public boolean isBluetoothRc() {
        try {
            if (mDevice == null) {
                return false;
            }
            BluetoothClass bluetoothClass = mDevice.getBluetoothClass();
            String name = mDevice.getName();
            if ((name == null) || (!name.startsWith("KW-YF"))) {
                if (bluetoothClass != null) {
                    int i = bluetoothClass.getDeviceClass();
                    if (i != 1292) {
                    }
                }
            } else {
                return true;
            }
            if (!mProfiles.isEmpty()) {
                Iterator iterator = mProfiles.iterator();
                while (iterator.hasNext()) {
                    LocalBluetoothProfile profile = (LocalBluetoothProfile) iterator.next();
                    if (profile != null) {
                        boolean bool = "HID".equals(profile.getProfileTypeName());
                        if (bool) {
                            return true;
                        }
                    }
                }
            }
            return false;
        } catch (Exception e) {
        } finally {
        }
        return false;
    }

    public boolean isBonded() {
        synchronized (mProfileLock) {
            if (getBondState() == BluetoothItemState.ITEM_PAIRED) {
                return true;
            } else {
                return false;
            }
        }

    }

    public boolean isConnected() {
        synchronized (mProfileLock) {
            if (mProfiles.isEmpty()) {
                return false;
            }
            Iterator iterator = mProfiles.iterator();
            while (iterator.hasNext()) {
                if (getProfileConnectionState((LocalBluetoothProfile) iterator.next()) == BluetoothItemState.ITEM_CONNECTED) {
                    return true;
                }
            }
            return false;
        }
    }

    public boolean isFirstBtnItem() {
        return firstBtnItem;
    }

    public boolean isJustDiscovered() {
        return mJustDiscovered;
    }

    public boolean isProfileReady() {
        if (mProfiles.isEmpty()) {
            return false;
        }
        Iterator iterator = mProfiles.iterator();
        while (iterator.hasNext()) {
            LocalBluetoothProfile profile = (LocalBluetoothProfile) iterator.next();
            if ((profile != null) && (profile.isProfileReady())) {
                return true;
            }
        }
        return false;
    }

    public void onBondingStateChanged(int paramInt1, int paramInt2) {
        Object localObject;
        if (paramInt1 != 11) {
            if (paramInt1 != 12) {
                setNewStatus(0);
            } else {
                if (mProfiles.isEmpty()) {
                    LocalBluetoothProfile profile = mProfileManager.getProfileByName(getDeviceProfileName());
                    if (profile != null) {
                        mProfiles.add(profile);
                    }
                }
                if (isConnected()) {
                    setNewStatus(2);
                } else {
                    setNewStatus(12);
                }
            }
        } else {
            setNewStatus(11);
        }
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("onBondingStateChanged() 绑定状态更新 bondState=");
        localStringBuilder.append(paramInt1);
        localStringBuilder.append("  如果是已配对状态，则继续connect()连接设备，配置文件=");
        if (getLocalBtProfile() == null) {
            localObject = null;
        } else {
            localObject = getLocalBtProfile().getProfileTypeName();
        }
        localStringBuilder.append((String) localObject);
        Log.d("CachedBluetoothDevice", localStringBuilder.toString());
        if ((paramInt1 == 12) && (paramInt2 == 11) && (!isConnected())) {
            connect();
        }
    }

    public void onConnectingStateChanged(int state, int paramInt2) {
    /*if (state != 1)
    {
      if (state != 2)
      {
        if (isBonded())
        {
          setNewStatus(BluetoothItemState.ITEM_PAIRED);//12
          return;
        }
        setNewStatus(BluetoothItemState.ITEM_SCAN);//0
        return;
      }
      if (mProfiles.isEmpty())
      {
        LocalBluetoothProfile localLocalBluetoothProfile = mProfileManager.getProfileByName(getDeviceProfileName());
        if (localLocalBluetoothProfile != null) {
          mProfiles.add(localLocalBluetoothProfile);
        }
      }
      setNewStatus(BluetoothItemState.ITEM_CONNECTED);//2
      return;
    }
    setNewStatus(BluetoothItemState.ITEM_CONNECTING);//1
    */
        if (state == 1) {
            setNewStatus(BluetoothItemState.ITEM_CONNECTING);
        } else if (state == 2) {
            if (mProfiles.isEmpty()) {
                LocalBluetoothProfile profile = mProfileManager.getProfileByName(getDeviceProfileName());
                if (profile != null) {
                    mProfiles.add(profile);
                }
            }
            setNewStatus(BluetoothItemState.ITEM_CONNECTED);//2
        } else {
            if (isBonded()) {
                setNewStatus(BluetoothItemState.ITEM_PAIRED);//12
            } else {
                setNewStatus(BluetoothItemState.ITEM_SCAN);//0
            }
        }
    }

    public void onProfileStateChanged(LocalBluetoothProfile paramLocalBluetoothProfile, int paramInt) {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("onProfileStateChanged: profile ");
        localStringBuilder.append(paramLocalBluetoothProfile);
        localStringBuilder.append(", device ");
        localStringBuilder.append(getName());
        localStringBuilder.append(", newProfileState=");
        localStringBuilder.append(paramInt);
        localStringBuilder.append("   oldstate=");
        localStringBuilder.append(getCurrentStatus());
        Log.d("CachedBluetoothDevice", localStringBuilder.toString());
        if (paramInt == 1) {
            newStatus = BluetoothItemState.ITEM_CONNECTING;// 1;
        } else if (paramInt == 2) {
            newStatus = BluetoothItemState.ITEM_CONNECTED;// 2;
            if ((!mProfiles.isEmpty()) && (!mProfiles.contains(paramLocalBluetoothProfile))) {
                mProfiles.clear();
                mProfiles.add(paramLocalBluetoothProfile);
            }
        } else if (isBonded()) {
            newStatus = BluetoothItemState.ITEM_PAIRED;// 12;
        } else {
            setNewStatus(0);
            localStringBuilder = new StringBuilder();
            localStringBuilder.append("onProfileStateChanged() 状态更新 status=");
            localStringBuilder.append(getCurrentStatus());
            localStringBuilder.append("  newState=");
            localStringBuilder.append(getNewStatus());
            localStringBuilder.append(" name=");
            localStringBuilder.append(getName());
            Log.d("CachedBluetoothDevice", localStringBuilder.toString());
        }
        if (mProfiles.isEmpty()) {
            mProfiles.add(paramLocalBluetoothProfile);
        }
    }

    public void onUuidChanged() {
        Object localObject;
        if (mProfiles.isEmpty()) {
            LocalBluetoothProfile profile = mProfileManager.getProfileByName(getDeviceProfileName());
            if ((profile != null) && (!mProfiles.contains(profile))) {
                mProfiles.add(profile);
            }
        }
        //if (SettingConfig.IS_DEBUG)
        {
            @SuppressLint("MissingPermission") BluetoothClass bluetoothClass = mDevice.getBluetoothClass();
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append("onUuidChanged() updating profiles for ");
            localStringBuilder.append(getName());
            localStringBuilder.append("  Class:");
            if (bluetoothClass == null) {
                localObject = null;
            } else {
                localObject = bluetoothClass.toString();
            }
            localStringBuilder.append((String) localObject);
            Log.d("CachedBluetoothDevice", localStringBuilder.toString());
        }
    }

    public void refresh() {
        if (isConnected()) {
            setNewStatus(2);
            return;
        }
        if (isBonded()) {
            setNewStatus(12);
            return;
        }
        setNewStatus(0);
    }

    public void setCurrentStatus(int paramInt) {
        currentStatus = paramInt;
    }

    public void setFirstBtnItem(boolean paramBoolean) {
        firstBtnItem = paramBoolean;
    }

    public void setJustDiscovered(boolean paramBoolean) {
        mJustDiscovered = paramBoolean;
    }

    public void setNewStatus(int paramInt) {
        newStatus = paramInt;
    }

    public void setRssi(int paramInt) {
        if (mRssi != paramInt) {
            mRssi = paramInt;
        }
    }

    public void setStatusStr(String paramString) {
        statusStr = paramString;
    }

    @SuppressLint("MissingPermission")
    public boolean startPairing() {
        if (mLocalAdapter.isDiscovering()) {
            mLocalAdapter.cancelDiscovery();
        }
        BluetoothDevice localBluetoothDevice = mDevice;
        return (localBluetoothDevice != null) && (localBluetoothDevice.createBond());
    }

    public void unPair() {
        if (mDevice != null) {
            try {
                Boolean localBoolean = (Boolean) BluetoothDevice.class.getMethod("removeBond", new Class[0]).invoke(mDevice, new Object[0]);
                return;
            } catch (IllegalAccessException localIllegalAccessException) {

            } catch (InvocationTargetException localInvocationTargetException) {

            } catch (NoSuchMethodException localNoSuchMethodException) {
                localNoSuchMethodException.printStackTrace();
            }

        }
    }

    public void updateProfileByClass(BluetoothClass paramBluetoothClass) {
        if (paramBluetoothClass != null) {
            try {
                if (getLocalBtProfile() == null) {
                    LocalBluetoothProfile localLocalBluetoothProfile = null;
                    if (paramBluetoothClass.getMajorDeviceClass() == 1280) {
                        localLocalBluetoothProfile = mProfileManager.getProfileByName("HID");
                    } else if (isA2dpDevice(paramBluetoothClass)) {
                        localLocalBluetoothProfile = mProfileManager.getProfileByName("A2DP");
                    }
                    if (localLocalBluetoothProfile == null) {
                        return;
                    }
                    if (!mProfiles.isEmpty()) {
                        mProfiles.clear();
                    }
                    mProfiles.add(localLocalBluetoothProfile);
                    return;
                }
            } finally {
            }
        }
    }

    public void updateProfileByScanRecord(byte[] paramArrayOfByte, int paramInt) {
        if (paramArrayOfByte == null) {
            Log.e(TAG, "updateProfile: 更新profile失败   scanRecord=null  name=" + getName());
            return;
        }
        LocalBluetoothProfile profile = getLocalBtProfile();
        if (profile != null) {
            Log.e(TAG, "updateProfile: 更新profile失败 ++++profile != null+++++  scanRecord=null  name=" + getName());
            return;
        }
        boolean bool2 = BluetoothHandsetFilter.isGoodMatchRc(mDevice, paramInt, paramArrayOfByte);
        boolean bool1 = bool2;
        if (!bool2) {
            try {
                bool1 = BluetoothHandsetFilter.isNameMatchExName(BluetoothHandsetFilter.REMOTE_CONTROL_NAME, paramArrayOfByte);
            } catch (Exception e) {
                e.printStackTrace();
                bool1 = bool2;
            }
        }
        if (bool1) {
            LocalBluetoothProfile profile2 = mProfileManager.getProfileByName("HID");
            if (profile2 == null) {
                return;
            }
            if (!mProfiles.isEmpty()) {
                mProfiles.clear();
            }
            mProfiles.add(profile2);
        }
    }

    public void updateSortValue() {
        if (isConnected()) {
            sortValue = Integer.MAX_VALUE;
            return;
        }
        if (isBonded()) {
            sortValue = 2147483646;
            return;
        }
        int i = mRssi;
        if (i < 2147483646) {
            sortValue = i;
            return;
        }
        sortValue = 2147483644;
    }
}