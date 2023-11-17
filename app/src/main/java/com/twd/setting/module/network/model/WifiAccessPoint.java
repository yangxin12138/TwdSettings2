package com.twd.setting.module.network.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.NetworkInfo.State;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TtsSpan;
import android.text.style.TtsSpan.TelephoneBuilder;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.collection.ArraySet;

import com.twd.setting.R;
import com.twd.setting.utils.ThreadUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class WifiAccessPoint
        implements Comparable<WifiAccessPoint> {
    public static int INVALID_NETWORK_ID = 0;
    public static int INVALID_RSSI = 0;
    static final String KEY_CARRIER_AP_EAP_TYPE = "key_carrier_ap_eap_type";
    static final String KEY_CARRIER_NAME = "key_carrier_name";
    static final String KEY_CONFIG = "key_config";
    static final String KEY_FQDN = "key_fqdn";
    static final String KEY_IS_CARRIER_AP = "key_is_carrier_ap";
    static final String KEY_NETWORKINFO = "key_networkinfo";
    static final String KEY_PROVIDER_FRIENDLY_NAME = "key_provider_friendly_name";
    static final String KEY_PSKTYPE = "key_psktype";
    static final String KEY_SCANRESULTS = "key_scanresults";
    static final String KEY_SCOREDNETWORKCACHE = "key_scorednetworkcache";
    static final String KEY_SECURITY = "key_security";
    static final String KEY_SPEED = "key_speed";
    static final String KEY_SSID = "key_ssid";
    static final String KEY_WIFIINFO = "key_wifiinfo";
    private static final String TAG = "WifiAccessPoint";
    public static final int HIGHER_FREQ_24GHZ = 2500;
    public static final int HIGHER_FREQ_5GHZ = 5900;
    public static final int LOWER_FREQ_24GHZ = 2400;
    public static final int LOWER_FREQ_5GHZ = 4900;
    private static final int PSK_UNKNOWN = 0;
    private static final int PSK_WPA = 1;
    private static final int PSK_WPA2 = 2;
    private static final int PSK_WPA_WPA2 = 3;

    public static final int SECURITY_NONE = 0;
    public static final int SECURITY_WEP = 1;
    public static final int SECURITY_PSK = 2;
    public static final int SECURITY_EAP = 3;
    public static final int SECURITY_OWE = 4;
    public static final int SECURITY_OWE_TRANSITION = 8;

    public static final int SECURITY_PSK_SAE_TRANSITION = 7;
    public static final int SECURITY_SAE = 5;

    public static final int SIGNAL_LEVELS = 5;
    public static final int UNREACHABLE_RSSI = Integer.MIN_VALUE;
    static final AtomicInteger sLastId = new AtomicInteger(0);
    private String bssid;
    AccessPointListener mAccessPointListener;
    private int mCarrierApEapType;
    private String mCarrierName;
    private WifiConfiguration mConfig;
    private Context mContext;
    private String mFqdn;
    int mId;
    private WifiInfo mInfo;
    private boolean mIsCarrierAp;
    private boolean mIsScoredNetworkMetered;
    private String mKey;
    private NetworkInfo mNetworkInfo;
    private String mProviderFriendlyName;
    private int mRssi;
    private final ArraySet<ScanResult> mScanResults = new ArraySet();
    private final Map<String, TimestampedScoredNetwork> mScoredNetworkCache = new HashMap();
    private int mSpeed;
    private Object mTag;
    private int networkId;
    private int pskType;
    private int security;
    private String ssid;

    public WifiAccessPoint(Context context, WifiConfiguration config) {
        Log.d(TAG,"WifiAccessPoint(Context, WifiConfiguration)");
        pskType = 0;
        try {
            INVALID_RSSI = ((Integer) WifiInfo.class.getField("INVALID_RSSI").get(WifiInfo.class)).intValue();
        } catch (IllegalAccessException localIllegalAccessException1) {

        } catch (NoSuchFieldException noSuchFieldException) {
            INVALID_RSSI = -127;
            noSuchFieldException.printStackTrace();
        }

        try {
            INVALID_NETWORK_ID = ((Integer) WifiConfiguration.class.getField("INVALID_NETWORK_ID").get(WifiConfiguration.class)).intValue();
        } catch (IllegalAccessException illegalAccessException) {
        } catch (NoSuchFieldException noSuchFieldException) {
        } catch (NullPointerException nullPointerException) {
            INVALID_NETWORK_ID = -1;
            nullPointerException.printStackTrace();
        }

        networkId = INVALID_NETWORK_ID;
        mRssi = Integer.MIN_VALUE;
        mSpeed = 0;
        mIsScoredNetworkMetered = false;
        mIsCarrierAp = false;
        mCarrierApEapType = -1;
        mCarrierName = null;
        mContext = context;
        loadConfig(config);
        mId = sLastId.incrementAndGet();
    }

    public WifiAccessPoint(Context context, Bundle savedState) {
        Log.d(TAG,"WifiAccessPoint(Context, Bundle)");
        int i = 0;
        pskType = 0;
        try {
            INVALID_RSSI = ((Integer) WifiInfo.class.getField("INVALID_RSSI").get(WifiInfo.class)).intValue();
        } catch (IllegalAccessException illegalAccessException) {
        } catch (NoSuchFieldException noSuchFieldException) {
            INVALID_RSSI = -127;
            noSuchFieldException.printStackTrace();
        }

        try {
            INVALID_NETWORK_ID = ((Integer) WifiConfiguration.class.getField("INVALID_NETWORK_ID").get(WifiConfiguration.class)).intValue();
        } catch (IllegalAccessException illegalAccessException) {
        } catch (NoSuchFieldException noSuchFieldException) {
        } catch (NullPointerException nullPointerException) {
            INVALID_NETWORK_ID = -1;
            nullPointerException.printStackTrace();
        }

        networkId = INVALID_NETWORK_ID;
        mRssi = Integer.MIN_VALUE;
        mSpeed = 0;
        mIsScoredNetworkMetered = false;
        mIsCarrierAp = false;
        mCarrierApEapType = -1;
        mCarrierName = null;
        mContext = context;
        if (savedState.containsKey(KEY_CONFIG)) {
            mConfig =  savedState.getParcelable(KEY_CONFIG);
        }
        if (mConfig != null) {
            loadConfig(mConfig);
        }
        if (savedState.containsKey(KEY_SSID)) {
            ssid = savedState.getString(KEY_SSID);
        }
        if (savedState.containsKey(KEY_SECURITY)) {
            security = savedState.getInt(KEY_SECURITY);
        }
        if (savedState.containsKey(KEY_SPEED)) {
            mSpeed = savedState.getInt(KEY_SPEED);
        }
        if (savedState.containsKey(KEY_PSKTYPE)) {
            pskType = savedState.getInt(KEY_PSKTYPE);
        }
        mInfo =  savedState.getParcelable(KEY_WIFIINFO);
        if (savedState.containsKey(KEY_NETWORKINFO)) {
            mNetworkInfo =  savedState.getParcelable(KEY_NETWORKINFO);
        }

        if (savedState.containsKey(KEY_SCANRESULTS)) {
            Parcelable[] scanResults = savedState.getParcelableArray(KEY_SCANRESULTS);
            mScanResults.clear();
            for(Parcelable result : scanResults){
                mScanResults.add((ScanResult) result);
            }
        }
        if (savedState.containsKey(KEY_SCOREDNETWORKCACHE)) {
            ArrayList<TimestampedScoredNetwork> scoredNetworkArrayList =
                    savedState.getParcelableArrayList(KEY_SCOREDNETWORKCACHE);
            for (TimestampedScoredNetwork timedScore : scoredNetworkArrayList) {
                mScoredNetworkCache.put(timedScore.getScore().networkKey.wifiKey.bssid, timedScore);
            }
        }
        if (savedState.containsKey(KEY_FQDN)) {
            mFqdn = savedState.getString(KEY_FQDN);
        }
        if (savedState.containsKey(KEY_PROVIDER_FRIENDLY_NAME)) {
            mProviderFriendlyName = savedState.getString(KEY_PROVIDER_FRIENDLY_NAME);
        }
        if (savedState.containsKey(KEY_IS_CARRIER_AP)) {
            mIsCarrierAp = savedState.getBoolean(KEY_IS_CARRIER_AP);
        }
        if (savedState.containsKey(KEY_CARRIER_AP_EAP_TYPE)) {
            mCarrierApEapType = savedState.getInt(KEY_CARRIER_AP_EAP_TYPE);
        }
        if (savedState.containsKey(KEY_CARRIER_NAME)) {
            mCarrierName = savedState.getString(KEY_CARRIER_NAME);
        }
        update(mConfig, mInfo, mNetworkInfo);
        updateKey();
        updateRssi();
        mId = sLastId.incrementAndGet();
    }

    public WifiAccessPoint(Context context, Collection<ScanResult> results) {
//        Log.d(TAG,"WifiAccessPoint(Context, Collection)");
        pskType = 0;
        try {
            INVALID_RSSI = ((Integer) WifiInfo.class.getField("INVALID_RSSI").get(WifiInfo.class)).intValue();
        } catch (IllegalAccessException illegalAccessException) {
        } catch (NoSuchFieldException noSuchFieldException) {
            INVALID_RSSI = -127;
            noSuchFieldException.printStackTrace();
        }

        try {
            INVALID_NETWORK_ID = ((Integer) WifiConfiguration.class.getField("INVALID_NETWORK_ID").get(WifiConfiguration.class)).intValue();
        } catch (IllegalAccessException illegalAccessException) {
        } catch (NoSuchFieldException noSuchFieldException) {
        } catch (NullPointerException nullPointerException) {
            INVALID_NETWORK_ID = -1;
            nullPointerException.printStackTrace();
        }

        networkId = INVALID_NETWORK_ID;
        mRssi = Integer.MIN_VALUE;
        mSpeed = 0;
        mIsScoredNetworkMetered = false;
        mIsCarrierAp = false;
        mCarrierApEapType = -1;
        mCarrierName = null;
        mContext = context;
        if (results.isEmpty()) {
            throw new IllegalArgumentException("Cannot construct with an empty ScanResult list");
        }
        mScanResults.addAll(results);

        ScanResult firstResult = results.iterator().next();
        ssid = firstResult.SSID;
        bssid = firstResult.BSSID;
        security = getSecurity(firstResult);
        if (security == SECURITY_PSK) {
            pskType = getPskType(firstResult);
        }
        updateKey();
        updateRssi();

        mId = sLastId.incrementAndGet();
    }

    public static String convertToQuotedString(String string) {
        return "\"" + string + "\"";
    }

    private int generateAverageSpeedForSsid() {
        Log.d(TAG,"generateAverageSpeedForSsid :");
        if (mScoredNetworkCache.isEmpty()) {
            return Speed.NONE;
        }

        Log.d(TAG, String.format("Generating fallbackspeed for %s using cache: %s", getSsidStr(), mScoredNetworkCache));
        int count = 0;
        int totalSpeed = 0;
        for (TimestampedScoredNetwork timedScore : mScoredNetworkCache.values()) {
            int speed = timedScore.getScore().calculateBadge(mRssi);
            if (speed != Speed.NONE) {
                count++;
                totalSpeed += speed;
            }
        }
        int speed = count == 0 ? Speed.NONE : totalSpeed / count;
        if (isVerboseLoggingEnabled()) {
            Log.i(TAG, String.format("%s generated fallback speed is: %d", getSsidStr(), speed));
        }
        return roundToClosestSpeedEnum(speed);
    }

    public static String getKey(ScanResult result) {
        StringBuilder builder = new StringBuilder();

        if (TextUtils.isEmpty(result.SSID)) {
            builder.append(result.BSSID);
        } else {
            builder.append(result.SSID);
        }

        builder.append(',').append(getSecurity(result));
        return builder.toString();
    }

    public static String getKey(WifiConfiguration config) {
        StringBuilder builder = new StringBuilder();

        if (TextUtils.isEmpty(config.SSID)) {
            builder.append(config.BSSID);
        } else {
            builder.append(removeDoubleQuotes(config.SSID));
        }

        builder.append(',').append(getSecurity(config));
        return builder.toString();
    }

    private static int getPskType(ScanResult result) {
        boolean wpa = result.capabilities.contains("WPA-PSK");
        boolean wpa2 = result.capabilities.contains("WPA2-PSK");
        if (wpa2 && wpa) {
            return PSK_WPA_WPA2;
        } else if (wpa2) {
            return PSK_WPA2;
        } else if (wpa) {
            return PSK_WPA;
        } else {
            Log.w(TAG, "Received abnormal flag string: " + result.capabilities);
            return PSK_UNKNOWN;
        }
    }

    private static int getSecurity(ScanResult result) {
        if (result.capabilities.contains("WEP")) {
            return SECURITY_WEP;
        } else if (result.capabilities.contains("PSK")) {
            return SECURITY_PSK;
        } else if (result.capabilities.contains("EAP")) {
            return SECURITY_EAP;
        }
        return SECURITY_NONE;
    }

    static int getSecurity(WifiConfiguration config) {
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK)) {
            return SECURITY_PSK;
        }
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_EAP) ||
                config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.IEEE8021X)) {
            return SECURITY_EAP;
        }
        return (config.wepKeys[0] != null) ? SECURITY_WEP : SECURITY_NONE;
    }

    private static String getSpeedLabel(Context context, int speed) {
        switch (speed) {
            case Speed.VERY_FAST:
                return context.getString(R.string.speed_label_very_fast);
            case Speed.FAST:
                return context.getString(R.string.speed_label_fast);
            case Speed.MODERATE:
                return context.getString(R.string.speed_label_okay);
            case Speed.SLOW:
                return context.getString(R.string.speed_label_slow);
            case Speed.NONE:
            default:
                return null;
        }
    }

    public static String getSpeedLabel(Context context, ScoredNetwork scoredNetwork, int rssi) {
        return getSpeedLabel(context, roundToClosestSpeedEnum(scoredNetwork.calculateBadge(rssi)));
    }

    private boolean isInfoForThisAccessPoint(WifiConfiguration config, WifiInfo info) {
        if (isPasspoint() == false && networkId != INVALID_NETWORK_ID) {
            return networkId == info.getNetworkId();
        } else if (config != null) {
            return matches(config);
        }
        else {
            // Might be an ephemeral connection with no WifiConfiguration. Try matching on SSID.
            // (Note that we only do this if the WifiConfiguration explicitly equals INVALID).
            // TODO: Handle hex string SSIDs.
            return ssid.equals(removeDoubleQuotes(info.getSSID()));
        }
    }

    private static boolean isVerboseLoggingEnabled() {
        return true;
    }

    public static String removeDoubleQuotes(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        int length = string.length();
        if ((length > 1) && (string.charAt(0) == '"')
                && (string.charAt(length - 1) == '"')) {
            return string.substring(1, length - 1);
        }
        return string;
    }

    private static int roundToClosestSpeedEnum(int speed) {
        if (speed < Speed.SLOW) {
            return Speed.NONE;
        } else if (speed < (Speed.SLOW + Speed.MODERATE) / 2) {
            return Speed.SLOW;
        } else if (speed < (Speed.MODERATE + Speed.FAST) / 2) {
            return Speed.MODERATE;
        } else if (speed < (Speed.FAST + Speed.VERY_FAST) / 2) {
            return Speed.FAST;
        } else {
            return Speed.VERY_FAST;
        }
    }

    public static String securityToString(int security, int pskType) {
        if (security == SECURITY_WEP) {
            return "WEP";
        } else if (security == SECURITY_PSK) {
            if (pskType == PSK_WPA) {
                return "WPA";
            } else if (pskType == PSK_WPA2) {
                return "WPA2";
            } else if (pskType == PSK_WPA_WPA2) {
                return "WPA_WPA2";
            }
            return "PSK";
        } else if (security == SECURITY_EAP) {
            return "EAP";
        }
        return "NONE";

    }

    private void updateKey() {
//        Log.d(TAG,"updateKey");
        StringBuilder builder = new StringBuilder();
        if (TextUtils.isEmpty(getSsidStr())) {
            builder.append(getBssid());
        } else {
            builder.append(getSsidStr());
        }
        builder.append(',').append(getSecurity());
        mKey = builder.toString();
    }

    private boolean updateMetered(WifiNetworkScoreCache scoreCache) {
        Log.d(TAG,"updateMetered");
        boolean oldMetering = mIsScoredNetworkMetered;
        mIsScoredNetworkMetered = false;

        if (isActive() && mInfo != null) {
            NetworkKey key = NetworkKey.createFromWifiInfo(mInfo);
            ScoredNetwork score = scoreCache.getScoredNetwork(key);
            if (score != null) {
                mIsScoredNetworkMetered |= score.meteredHint;
            }
        } else {
            for (ScanResult result : mScanResults) {
                ScoredNetwork score = scoreCache.getScoredNetwork(result);
                if (score == null) {
                    continue;
                }
                mIsScoredNetworkMetered |= score.meteredHint;
            }
        }
        return oldMetering == mIsScoredNetworkMetered;
    }

    private void updateRssi() {
//        Log.d(TAG,"updateRssi");
        if (this.isActive()) {
            return;
        }

        int rssi = UNREACHABLE_RSSI;
        for (ScanResult result : mScanResults) {
            if (result.level > rssi) {
                rssi = result.level;
            }
        }

        if (rssi != UNREACHABLE_RSSI && mRssi != UNREACHABLE_RSSI) {
            mRssi = (mRssi + rssi) / 2; // half-life previous value
        } else {
            mRssi = rssi;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private boolean updateScores(WifiNetworkScoreCache scoreCache, long maxScoreCacheAgeMillis) {
        Log.d(TAG,"updateScores");
        long nowMillis = SystemClock.elapsedRealtime();
        for (ScanResult result : mScanResults) {
            ScoredNetwork score = scoreCache.getScoredNetwork(result);
            if (score == null) {
                continue;
            }
            TimestampedScoredNetwork timedScore = mScoredNetworkCache.get(result.BSSID);
            if (timedScore == null) {
                mScoredNetworkCache.put(
                        result.BSSID, new TimestampedScoredNetwork(score, nowMillis));
            } else {
                // Update data since the has been seen in the score cache
                timedScore.update(score, nowMillis);
            }
        }

        // Remove old cached networks
        long evictionCutoff = nowMillis - maxScoreCacheAgeMillis;
        Iterator<TimestampedScoredNetwork> iterator = mScoredNetworkCache.values().iterator();
        iterator.forEachRemaining(timestampedScoredNetwork -> {
            if (timestampedScoredNetwork.getUpdatedTimestampMillis() < evictionCutoff) {
                iterator.remove();
            }
        });

        return updateSpeed();
    }

    private boolean updateSpeed() {
//        Log.d(TAG,"updateSpeed");
        int oldSpeed = mSpeed;
        mSpeed = generateAverageSpeedForSsid();

        boolean changed = oldSpeed != mSpeed;
        if(isVerboseLoggingEnabled() && changed) {
            Log.i(TAG, String.format("%s: Set speed to %d", ssid, mSpeed));
        }
        return changed;
    }

    public void clearConfig() {
        mConfig = null;
        networkId = INVALID_NETWORK_ID;
    }

    public int compareTo(WifiAccessPoint other) {
        // Active one goes first.
        if (isActive() && !other.isActive()) return -1;
        if (!isActive() && other.isActive()) return 1;

        // Reachable one goes before unreachable one.
        if (isReachable() && !other.isReachable()) return -1;
        if (!isReachable() && other.isReachable()) return 1;

        // Configured (saved) one goes before unconfigured one.
        if (isSaved() && !other.isSaved()) return -1;
        if (!isSaved() && other.isSaved()) return 1;

        // Faster speeds go before slower speeds - but only if visible change in speed label
        if (getSpeed() != other.getSpeed()) {
            return other.getSpeed() - getSpeed();
        }
        // Sort by signal strength, bucketed by level
        int difference = WifiManager.calculateSignalLevel(other.mRssi, SIGNAL_LEVELS)
                - WifiManager.calculateSignalLevel(mRssi, SIGNAL_LEVELS);
        if (difference != 0) {
            return difference;
        }

        // Sort by ssid.
        difference = getSsidStr().compareToIgnoreCase(other.getSsidStr());
        if (difference != 0) {
            return difference;
        }

        // Do a case sensitive comparison to distinguish SSIDs that differ in case only
        return getSsidStr().compareTo(other.getSsidStr());
    }

    public boolean equals(Object other) {
        if (!(other instanceof WifiAccessPoint)) return false;
        return (this.compareTo((WifiAccessPoint) other) == 0);

    }

    public void generateOpenNetworkConfig() {
        if (security != SECURITY_NONE)
            throw new IllegalStateException();
        if (mConfig != null)
            return;
        mConfig = new WifiConfiguration();
        mConfig.SSID = WifiAccessPoint.convertToQuotedString(ssid);
        mConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
    }

    public String getBssid() {
        return bssid;
    }

    public int getCarrierApEapType() {
        return mCarrierApEapType;
    }

    public String getCarrierName() {
        return mCarrierName;
    }

    public WifiConfiguration getConfig() {
        return mConfig;
    }

    @SuppressLint("NewApi")
    public String getConfigName() {
        if (mConfig != null && mConfig.isPasspoint()) {
            return mConfig.providerFriendlyName;
        } else if (mFqdn != null) {
            return mProviderFriendlyName;
        }else {
            return ssid;
        }
    }

    public NetworkInfo.DetailedState getDetailedState() {
        return mNetworkInfo != null ? mNetworkInfo.getDetailedState() : null;
    }

    public WifiInfo getInfo() {
        return mInfo;
    }

    public String getKey() {
        return mKey;
    }

    public int getLevel() {
        return WifiManager.calculateSignalLevel(mRssi, SIGNAL_LEVELS);
    }

    public NetworkInfo getNetworkInfo() {
        return mNetworkInfo;
    }

    public String getPasspointFqdn() {
        return mFqdn;
    }

    public int getRssi() {
        return mRssi;
    }

    public Set<ScanResult> getScanResults() {
        return mScanResults;
    }

    public Map<String, TimestampedScoredNetwork> getScoredNetworkCache() {
        return mScoredNetworkCache;
    }

    public int getSecurity() {
        return security;
    }

    @SuppressLint("NewApi")
    public String getSecurityString(boolean concise) {
        Context context = mContext;
        /// @}
        if (mConfig != null && mConfig.isPasspoint()) {
            return concise ? context.getString(R.string.wifi_security_short_eap) :
                    context.getString(R.string.wifi_security_eap);
        }
        switch(security) {
            case SECURITY_EAP:
                return concise ? context.getString(R.string.wifi_security_short_eap) :
                        context.getString(R.string.wifi_security_eap);
            case SECURITY_PSK:
                switch (pskType) {
                    case PSK_WPA:
                        return concise ? context.getString(R.string.wifi_security_short_wpa) :
                                context.getString(R.string.wifi_security_wpa);
                    case PSK_WPA2:
                        return concise ? context.getString(R.string.wifi_security_short_wpa2) :
                                context.getString(R.string.wifi_security_wpa2);
                    case PSK_WPA_WPA2:
                        return concise ? context.getString(R.string.wifi_security_short_wpa_wpa2) :
                                context.getString(R.string.wifi_security_wpa_wpa2);
                    case PSK_UNKNOWN:
                    default:
                        return concise ? context.getString(R.string.wifi_security_short_psk_generic)
                                : context.getString(R.string.wifi_security_psk_generic);
                }
            case SECURITY_WEP:
                return concise ? context.getString(R.string.wifi_security_short_wep) :
                        context.getString(R.string.wifi_security_wep);
            case SECURITY_NONE:
            default:
                return concise ? "" : context.getString(R.string.wifi_security_none);
        }

    }

    int getSpeed() {
        return mSpeed;
    }

    String getSpeedLabel() {
        return getSpeedLabel(mSpeed);
    }

    String getSpeedLabel(int speed) {
        return getSpeedLabel(mContext, speed);
    }

    public CharSequence getSsid() {
        final SpannableString str = new SpannableString(ssid);
        str.setSpan(new TtsSpan.TelephoneBuilder(ssid).build(), 0, ssid.length(),
                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return str;
    }

    public String getSsidStr() {
        return ssid;
    }

    public Object getTag() {
        return mTag;
    }

    public int hashCode() {
        int result = 0;
        if (mInfo != null) result += 13 * mInfo.hashCode();
        result += 19 * mRssi;
        result += 23 * networkId;
        result += 29 * ssid.hashCode();
        return result;
    }

    public boolean isActive() {
        if(mNetworkInfo ==null) {
            //Log.d(TAG,"isActive  is null");
            return false;
        }else{
            return ((networkId != INVALID_NETWORK_ID) || (mNetworkInfo.getState() != NetworkInfo.State.DISCONNECTED));
        }

    }

    public boolean isCarrierAp() {
        return mIsCarrierAp;
    }

    public boolean isConnectable() {
        return (getLevel() != -1) && (getDetailedState() == null);
    }

    public boolean isMetered() {

        boolean isMetered = false;

        try {
            isMetered = ((Boolean) WifiConfiguration.class.getDeclaredMethod("isMetered", new Class[0]).invoke(WifiConfiguration.class, new Object[]{this.mConfig, this.mInfo})).booleanValue();
        } catch (InvocationTargetException localInvocationTargetException) {
        } catch (NoSuchMethodException localNoSuchMethodException) {
        } catch (IllegalAccessException illegalAccessException) {
            illegalAccessException.printStackTrace();
        }

        return mIsScoredNetworkMetered || isMetered;

    }



    public boolean isPasspoint() {
        //return mConfig != null && mConfig.isPasspoint();

        int i = Build.VERSION.SDK_INT;
        if (i < 21) {
            return false;
        }
        if (Build.VERSION.SDK_INT < 23) {
            return false;
        }
        if (Build.VERSION.SDK_INT < 29) {
            return mConfig != null && mConfig.isPasspoint();
        }

        return false;
    }

    public boolean isPasspointConfig() {
        return mFqdn != null;
    }

    public boolean isReachable() {
        return mRssi != Integer.MIN_VALUE;
    }

    public boolean isSaved() {
        return networkId != INVALID_NETWORK_ID;
    }

    void loadConfig(WifiConfiguration config) {
        if (config.SSID == null) {
            ssid = "";
        } else {
            ssid = removeDoubleQuotes(config.SSID);
        }
        bssid = config.BSSID;
        security = getSecurity(config);
        updateKey();
        networkId = config.networkId;
        mConfig = config;
    }


    public boolean matches(WifiConfiguration config) {
        if (VERSION.SDK_INT == 29) {
            if (config.isPasspoint()) {
                return (isPasspoint()) && (config.FQDN.equals(this.mConfig.FQDN));
            }
        }
        if (VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (config.isPasspoint() && mConfig != null && mConfig.isPasspoint()) {
                return config.FQDN.equals(mConfig.providerFriendlyName);
            } else {
                boolean field_flag = false;
                if(mConfig != null) {
                    try {
                        Field field = WifiConfiguration.class.getField("shared");
                        try {
                            Object localObject1 = field.get(mConfig);
                            Object localObject2 = field.get(config);
                            if (localObject1 != localObject2) {
                                field_flag = false;
                            } else {
                                field_flag = true;
                            }
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }

                    } catch (NoSuchFieldException e) {
                        throw new RuntimeException(e);
                    }
                }
                return ssid.equals(removeDoubleQuotes(config.SSID))
                        && security == getSecurity(config)
                        && (mConfig == null || field_flag //mConfig.shared == config.shared
            );
            }
        }else{
            boolean field_flag = false;

            return ssid.equals(removeDoubleQuotes(config.SSID))
                    && security == getSecurity(config)
                    && (mConfig == null /*|| field_flag*/ );

        }


/*
        int sdk = Build.VERSION.SDK_INT;
        boolean bool2 = true;
        boolean bool1 = true;
        if (sdk == 29) {
            if (config.isPasspoint()) {
                return (isPasspoint()) && (config.FQDN.equals(this.mConfig.FQDN));
            }
        }
        int field_flag = 0;
        try {
            Field field = WifiConfiguration.class.getField("shared");
            Object localObject1 = field.get(this.mConfig);
            Object localObject2 = field.get(config);
            if (localObject1 != localObject2) {
                field_flag = 1;
            }
        } catch (Exception localException1) {
            Object localObject2;
            Object localObject1;
            int j;
        }

        if (this.ssid.equals(removeDoubleQuotes(config.SSID))) {
            if ((this.mConfig != null) && (field_flag != 0)) {
                return false;
            }
            i = getSecurity(config);
            WifiManager wifiManager = (WifiManager) this.mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (security != 7) {
                if (security != 8) {
                    return security == i;
                }
                if (i != 0) {
                    if ((wifiManager.isEnhancedOpenSupported()) && (i == 4)) {
                        return true;
                    }
                    bool1 = false;
                }
                return bool1;
            }
            bool1 = bool2;
            if (i != 2) {
                if ((wifiManager.isWpa3SaeSupported()) && (i == 5)) {
                    return true;
                }
                bool1 = false;
            }
            return bool1;
        }
        //return false;
        if ((this.ssid.equals(removeDoubleQuotes(config.SSID))) && (this.security == getSecurity(config)) && (this.mConfig == null)) {
            bool1 = true;
        } else {
            bool1 = false;
        }

        if ((Build.VERSION.SDK_INT >= 23) && (Build.VERSION.SDK_INT <= 28)) {
            if (config.isPasspoint()) {
                if ((mConfig != null) && (mConfig.isPasspoint())) {
                    return (this.ssid.equals(removeDoubleQuotes(config.SSID))) && (config.FQDN.equals(this.mConfig.FQDN));
                }
            }
            if (Build.VERSION.SDK_INT < 24) {
            }
        }
        try {
            Field field = WifiConfiguration.class.getField("shared");
            Object localObject1 = field.get(this.mConfig);
            Object localObject2 = field.get(config);
            if (localObject1 == localObject2) {
                field_flag = 1;
            }
        } catch (Exception localException2) {

        }
        i = 0;
        if ((ssid.equals(removeDoubleQuotes(config.SSID))) && (security == getSecurity(config))) {
            if (mConfig == null) {
                if ((1 | i) != 0)
                    return true;
            } else {
                if ((0 | i) != 0)
                    return true;
            }
        }
        return false;
        // return bool1;

 */

    }

    public void saveWifiState(Bundle savedState) {
        if (ssid != null) {
            savedState.putString(KEY_SSID, getSsidStr());
        }
        savedState.putInt(KEY_SECURITY, security);
        savedState.putInt(KEY_SPEED, mSpeed);
        savedState.putInt(KEY_PSKTYPE, pskType);

        if (mConfig != null) {
            savedState.putParcelable(KEY_CONFIG,  mConfig);
        }
        savedState.putParcelable(KEY_WIFIINFO, mInfo);

        savedState.putParcelableArray(KEY_SCANRESULTS,   mScanResults.toArray(new Parcelable[( mScanResults).size()]));
        savedState.putParcelableArrayList(KEY_SCOREDNETWORKCACHE, new ArrayList(this.mScoredNetworkCache.values()));

        if (mNetworkInfo != null) {
            savedState.putParcelable(KEY_NETWORKINFO,  mNetworkInfo);
        }

        if (mFqdn != null) {
            savedState.putString(KEY_FQDN,  mFqdn);
        }

        if (mProviderFriendlyName != null) {
            savedState.putString(KEY_PROVIDER_FRIENDLY_NAME,  mProviderFriendlyName);
        }
        savedState.putBoolean(KEY_IS_CARRIER_AP, mIsCarrierAp);
        savedState.putInt(KEY_CARRIER_AP_EAP_TYPE, mCarrierApEapType);
        savedState.putString(KEY_CARRIER_NAME, mCarrierName);
    }

    public void setListener(AccessPointListener listener) {
        mAccessPointListener = listener;
    }

    void setRssi(int paramInt) {
        mRssi = paramInt;
    }

    public void setScanResults(Collection<ScanResult> scanResults) {
//        Log.d(TAG,"setScanResults");
        String key = getKey();
        for (ScanResult result : scanResults) {
            String scanResultKey = WifiAccessPoint.getKey(result);
            if (!mKey.equals(scanResultKey)) {
                throw new IllegalArgumentException(
                        String.format("ScanResult %s\nkey of %s did not match current AP key %s",
                                      result, scanResultKey, key));
            }
        }
        int oldLevel = getLevel();
        mScanResults.clear();
        mScanResults.addAll(scanResults);
        updateRssi();
        int newLevel = getLevel();

        // If newLevel is 0, there will be no displayed Preference since the AP is unreachable
        if (newLevel > 0 && newLevel != oldLevel) {
            // Only update labels on visible rssi changes
            updateSpeed();
            ThreadUtils.postOnMainThread(() -> {
                if (mAccessPointListener != null) {
                    mAccessPointListener.onLevelChanged(this);
                }
            });

        }

        ThreadUtils.postOnMainThread(() -> {
            if (mAccessPointListener != null) {
                mAccessPointListener.onAccessPointChanged(this);
            }
        });

        if (!scanResults.isEmpty()) {
            ScanResult result = scanResults.iterator().next();

            // This flag only comes from scans, is not easily saved in config
            if (security == SECURITY_PSK) {
                pskType = getPskType(result);
            }
        }
    }

    public void setTag(Object tag) {
        mTag = tag;
    }

    void setUnreachable() {
        setRssi(WifiAccessPoint.UNREACHABLE_RSSI);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder().append("AccessPoint(")
                .append(ssid);
        if (bssid != null) {
            builder.append(":").append(bssid);;
        }
        if (isSaved()) {
            builder.append(',').append("saved");
        }
        if (isActive()) {
            builder.append(',').append("active");
        }
        builder.append(",level=").append(getLevel());
        if (mSpeed != 0) {
            builder.append(",speed=").append(this.mSpeed);
        }
        if (isVerboseLoggingEnabled()) {
            builder.append(",rssi=").append(this.mRssi).append(",scan cache size=").append(this.mScanResults.size());
        }
        builder.append(')');
        return builder.toString();
    }

    public void update(WifiConfiguration config) {
        mConfig = config;
        if (mConfig != null) {
            networkId = mConfig.networkId;
        } else {
            networkId = INVALID_NETWORK_ID;
        }
        ThreadUtils.postOnMainThread(() -> {
            if (mAccessPointListener != null) {
                mAccessPointListener.onAccessPointChanged(this);
            }
        });
    }

    public boolean update(WifiConfiguration config, WifiInfo info, NetworkInfo networkInfo) {
//        Log.d(TAG,"update :");
        boolean updated = false;
        final int oldLevel = getLevel();
        if (info != null && isInfoForThisAccessPoint(config, info)) {
            updated = (mInfo == null);
            if (mConfig != config) {
                // We do not set updated = true as we do not want to increase the amount of sorting
                // and copying performed in WifiTracker at this time. If issues involving refresh
                // are still seen, we will investigate further.
                update(config); // Notifies the AccessPointListener of the change
            }
            if (mRssi != info.getRssi() && info.getRssi() != INVALID_RSSI) {
                mRssi = info.getRssi();
                updated = true;
            } else if (mNetworkInfo != null && networkInfo != null
                    && mNetworkInfo.getDetailedState() != networkInfo.getDetailedState()) {
                updated = true;
            }
            mInfo = info;
            mNetworkInfo = networkInfo;
        } else if (mInfo != null) {
            updated = true;
            mInfo = null;
            mNetworkInfo = null;
        }
        if (updated && mAccessPointListener != null) {
            ThreadUtils.postOnMainThread(() -> {
                if (mAccessPointListener != null) {
                    mAccessPointListener.onAccessPointChanged(this);
                }
            });

            if (oldLevel != getLevel() /* current level */) {
                ThreadUtils.postOnMainThread(() -> {
                    if (mAccessPointListener != null) {
                        mAccessPointListener.onLevelChanged(this);
                    }
                });
            }
        }

        return updated;
/*
        int i = getLevel();
        boolean bool2 = false;
        boolean bool1 = false;
        if ((paramWifiInfo != null) && (isInfoForThisAccessPoint(paramWifiConfiguration, paramWifiInfo))) {
            if (mInfo == null) {
                bool1 = true;
            }
            if (mConfig != paramWifiConfiguration) {
                update(paramWifiConfiguration);
            }
            if ((mRssi != paramWifiInfo.getRssi()) && (paramWifiInfo.getRssi() != INVALID_RSSI)) {
                mRssi = paramWifiInfo.getRssi();
            }
            do {
                bool2 = true;
                //     break;

                bool2 = bool1;
                if (mNetworkInfo == null) {
                    break;
                }
                bool2 = bool1;
                if (paramNetworkInfo == null) {
                    break;
                }
                bool2 = bool1;
            } while (mNetworkInfo.getDetailedState() != paramNetworkInfo.getDetailedState());
            mInfo = paramWifiInfo;
            mNetworkInfo = paramNetworkInfo;
        } else if (mInfo != null) {
            mInfo = null;
            mNetworkInfo = null;
            bool2 = true;
        }
        if ((bool2) && (this.mAccessPointListener != null)) {
            //ThreadUtils.postOnMainThread(new WifiAccessPoint..ExternalSyntheticLambda2(this));
            ThreadUtils.postOnMainThread(new Runnable() {
                @Override
                public void run() {

                }
            });
            if (i != getLevel()) {
                //ThreadUtils.postOnMainThread(new WifiAccessPoint..ExternalSyntheticLambda3(this));
                ThreadUtils.postOnMainThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        }
        return bool2;

 */
    }

    public  interface AccessPointListener {
        void onAccessPointChanged(WifiAccessPoint paramWifiAccessPoint);

        void onLevelChanged(WifiAccessPoint paramWifiAccessPoint);
    }

    public  @interface Speed {
        int NONE = 0;
        int SLOW = 5;
        int MODERATE = 10;
        int FAST = 20;
        int VERY_FAST = 30;
    }
}
