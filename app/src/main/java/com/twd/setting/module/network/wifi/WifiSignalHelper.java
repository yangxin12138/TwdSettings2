package com.twd.setting.module.network.wifi;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.twd.setting.R;
import com.twd.setting.module.network.model.WifiAccessPoint;

public class WifiSignalHelper {
    private static final int LEVEL_1 = 1;
    private static final int LEVEL_2 = 2;
    private static final int LEVEL_3 = 3;
    private static final int LEVEL_4 = 4;
    private static final int LEVEL_NONE = 0;
    private final String TAG = "WifiSignalHelper";

    public static Drawable getIconSignalStrength(Context paramContext, WifiAccessPoint paramWifiAccessPoint) {
        boolean security = false;
        int i = 0;
        if (paramWifiAccessPoint.getSecurity() != 0) {
            security = true;
        } else {
            security = false;
        }
        int level = paramWifiAccessPoint.getLevel();
        if (level == LEVEL_1) {
            if (security) {
                i = R.mipmap.ic_wifi_signal_1_locked;
            } else {
                i = R.mipmap.ic_wifi_signal_1;
            }
        } else if (level == LEVEL_2) {
            if (security) {
                i = R.mipmap.ic_wifi_signal_2_locked;
            } else {
                i = R.mipmap.ic_wifi_signal_2;
            }
        } else if (level == LEVEL_3) {
            if (security) {
                i = R.mipmap.ic_wifi_signal_3_locked;
            } else {
                i = R.mipmap.ic_wifi_signal_3;
            }
        } else if (level == LEVEL_4) {
            if (security) {
                i = R.mipmap.ic_wifi_signal_4_locked;
            } else {
                i = R.mipmap.ic_wifi_signal_4;
            }
        }else {
            if (security) {
                i = R.mipmap.ic_wifi_signal_1_locked;
            } else {
                i = R.mipmap.ic_wifi_signal_1;
            }
        }
    /*if (level != 2)
    {
      if (level != 3)
      {
        if (level != 4)
        {
          if (i != 0) {
            i = R.mipmap.ic_wifi_signal_1_locked;
          } else {
            i = R.mipmap.ic_wifi_signal_1;
          }
        }
        else if (i != 0) {
          i = R.mipmap.ic_wifi_signal_4_locked;
        } else {
          i = R.mipmap.ic_wifi_signal_4;
        }
      }
      else if (i != 0) {
        i = R.mipmap.ic_wifi_signal_3_locked;
      } else {
        i = R.mipmap.ic_wifi_signal_3;
      }
    }
    else if (i != 0) {
      i = R.mipmap.ic_wifi_signal_2_locked;
    } else {
      i = R.mipmap.ic_wifi_signal_2;
    }
     */
        return paramContext.getResources().getDrawable(i);
    }
}
