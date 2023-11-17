package com.twd.setting.module.network.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Process;
import android.util.Log;
import android.util.LruCache;

import androidx.core.util.Preconditions;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class WifiNetworkScoreCache {
    private static final boolean DBG = Log.isLoggable("WifiNetworkScoreCache", Log.DEBUG);
    private static final int DEFAULT_MAX_CACHE_SIZE = 100;
    public static final int INVALID_NETWORK_SCORE = -128;
    private static final String TAG = "WifiNetworkScoreCache";
    private final LruCache<String, ScoredNetwork> mCache;
    private final Context mContext;
    private CacheListener mListener;
    private final Object mLock = new Object();

    public WifiNetworkScoreCache(Context paramContext) {
        this(paramContext, null);
    }

    public WifiNetworkScoreCache(Context paramContext, CacheListener paramCacheListener) {
        this(paramContext, paramCacheListener, 100);
    }

    public WifiNetworkScoreCache(Context paramContext, CacheListener paramCacheListener, int paramInt) {
        mContext = paramContext.getApplicationContext();
        mListener = paramCacheListener;
        mCache = new LruCache(paramInt);
    }

    private String buildNetworkKey(ScanResult paramScanResult) {
        if ((paramScanResult != null) && (paramScanResult.SSID != null)) {
            StringBuilder localStringBuilder = new StringBuilder("\"");
            localStringBuilder.append(paramScanResult.SSID);
            localStringBuilder.append("\"");
            if (paramScanResult.BSSID != null) {
                localStringBuilder.append(paramScanResult.BSSID);
            }
            return localStringBuilder.toString();
        }
        return null;
    }

    private String buildNetworkKey(NetworkKey paramNetworkKey) {
        if (paramNetworkKey == null) {
            return null;
        }
        if (paramNetworkKey.wifiKey == null) {
            return null;
        }
        if (paramNetworkKey.type == 1) {
            String str = paramNetworkKey.wifiKey.ssid;
            if (str == null) {
                return null;
            }
            Object localObject = str;
            if (paramNetworkKey.wifiKey.bssid != null) {
                localObject = new StringBuilder();
                ((StringBuilder) localObject).append(str);
                ((StringBuilder) localObject).append(paramNetworkKey.wifiKey.bssid);
                localObject = ((StringBuilder) localObject).toString();
            }
            return (String) localObject;
        }
        return null;
    }

    private String buildNetworkKey(ScoredNetwork paramScoredNetwork) {
        if (paramScoredNetwork == null) {
            return null;
        }
        return buildNetworkKey(paramScoredNetwork.networkKey);
    }

    public final void clearScores() {
        synchronized (this.mLock) {
            this.mCache.evictAll();
            return;
        }
    }

    protected final void dump(FileDescriptor arg1, PrintWriter paramPrintWriter, String[] paramArrayOfString) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.DUMP", "WifiNetworkScoreCache");
        paramPrintWriter.println(String.format("WifiNetworkScoreCache (%s/%d)", new Object[]{this.mContext.getPackageName(), Integer.valueOf(Process.myUid())}));
        paramPrintWriter.println("  All score curves:");
        synchronized (this.mLock) {
            Iterator iterator = this.mCache.snapshot().values().iterator();
            Object localObject;
            StringBuilder localStringBuilder;
            while (iterator.hasNext()) {
                localObject = (ScoredNetwork) iterator.next();
                localStringBuilder = new StringBuilder();
                localStringBuilder.append("    ");
                localStringBuilder.append(localObject);
                paramPrintWriter.println(localStringBuilder.toString());
            }
            paramPrintWriter.println("  Network scores for latest ScanResults:");
            Iterator iterator2 = ((WifiManager) this.mContext.getSystemService(Context.WIFI_SERVICE)).getScanResults().iterator();
            while (iterator2.hasNext()) {
                localObject = (ScanResult) iterator2.next();
                localStringBuilder = new StringBuilder();
                localStringBuilder.append("    ");
                localStringBuilder.append(buildNetworkKey((ScanResult) localObject));
                localStringBuilder.append(": ");
                localStringBuilder.append(getNetworkScore((ScanResult) localObject));
                paramPrintWriter.println(localStringBuilder.toString());
            }
            return;
        }

    }

    public int getNetworkScore(ScanResult paramScanResult) {
        ScoredNetwork localScoredNetwork = getScoredNetwork(paramScanResult);
        int i;
        if ((localScoredNetwork != null) && (localScoredNetwork.rssiCurve != null)) {
            int j = localScoredNetwork.rssiCurve.lookupScore(paramScanResult.level);
            i = j;
            if (DBG) {
                StringBuilder localStringBuilder = new StringBuilder();
                localStringBuilder.append("getNetworkScore found scored network ");
                localStringBuilder.append(localScoredNetwork.networkKey);
                localStringBuilder.append(" score ");
                localStringBuilder.append(Integer.toString(j));
                localStringBuilder.append(" RSSI ");
                localStringBuilder.append(paramScanResult.level);
                Log.d("WifiNetworkScoreCache", localStringBuilder.toString());
                return j;
            }
        } else {
            i = -128;
        }
        return i;
    }

    public int getNetworkScore(ScanResult paramScanResult, boolean paramBoolean) {
        ScoredNetwork localScoredNetwork = getScoredNetwork(paramScanResult);
        int i;
        if ((localScoredNetwork != null) && (localScoredNetwork.rssiCurve != null)) {
            int j = localScoredNetwork.rssiCurve.lookupScore(paramScanResult.level, paramBoolean);
            i = j;
            if (DBG) {
                StringBuilder localStringBuilder = new StringBuilder();
                localStringBuilder.append("getNetworkScore found scored network ");
                localStringBuilder.append(localScoredNetwork.networkKey);
                localStringBuilder.append(" score ");
                localStringBuilder.append(Integer.toString(j));
                localStringBuilder.append(" RSSI ");
                localStringBuilder.append(paramScanResult.level);
                localStringBuilder.append(" isActiveNetwork ");
                localStringBuilder.append(paramBoolean);
                Log.d("WifiNetworkScoreCache", localStringBuilder.toString());
                return j;
            }
        } else {
            i = -128;
        }
        return i;
    }

    public ScoredNetwork getScoredNetwork(ScanResult arg1) {
        String localObject1 = buildNetworkKey(arg1);
        if (localObject1 == null) {
            return null;
        }
        synchronized (this.mLock) {
            return mCache.get(localObject1);
        }
    }

    public ScoredNetwork getScoredNetwork(NetworkKey arg1) {
        String localObject1 = buildNetworkKey(arg1);
        if (localObject1 == null) {
            if (DBG) {
                Log.d("WifiNetworkScoreCache", "Could not build key string for Network Key: " + arg1);
            }
            return null;
        }
        synchronized (this.mLock) {
            return mCache.get(localObject1);
        }
    }

    public boolean hasScoreCurve(ScanResult paramScanResult) {
        ScoredNetwork scoredNetwork = getScoredNetwork(paramScanResult);
        return (scoredNetwork != null) && (scoredNetwork.rssiCurve != null);
    }

    public boolean isScoredNetwork(ScanResult paramScanResult) {
        return getScoredNetwork(paramScanResult) != null;
    }

    public void registerListener(CacheListener paramCacheListener) {
        synchronized (this.mLock) {
            this.mListener = paramCacheListener;
            return;
        }
    }

    public void unregisterListener() {
        synchronized (this.mLock) {
            this.mListener = null;
            return;
        }
    }

    public final void updateScores(List<ScoredNetwork> paramList) {
        if (paramList != null) {
            if (paramList.isEmpty()) {
                return;
            }
            if (DBG) {
                Log.d("WifiNetworkScoreCache", "updateScores list size=" + paramList.size());
            }
            int i = 0;
            synchronized (this.mLock) {
                Object localObject2 = paramList.iterator();
                while (((Iterator) localObject2).hasNext()) {
                    ScoredNetwork localScoredNetwork = (ScoredNetwork) ((Iterator) localObject2).next();
                    String localObject3 = buildNetworkKey(localScoredNetwork);
                    if (localObject3 == null) {
                        if (DBG) {
                            Log.d("WifiNetworkScoreCache", "Failed to build network key for ScoredNetwork" + localScoredNetwork);
                        }
                    } else {
                        this.mCache.put(localObject3, localScoredNetwork);
                        i = 1;
                    }
                }
                localObject2 = this.mListener;
                if ((localObject2 != null) && (i != 0)) {
                    ((CacheListener) localObject2).post(paramList);
                }
                return;
            }
        }
    }

    public static abstract class CacheListener {
        private Handler mHandler;

        @SuppressLint("RestrictedApi")
        public CacheListener(Handler paramHandler) {
            Preconditions.checkNotNull(paramHandler);
            this.mHandler = paramHandler;
        }

        public abstract void networkCacheUpdated(List<ScoredNetwork> paramList);

        void post(final List<ScoredNetwork> paramList) {
            this.mHandler.post(new Runnable() {
                public void run() {
                    WifiNetworkScoreCache.CacheListener.this.networkCacheUpdated(paramList);
                }
            });
        }
    }
}
