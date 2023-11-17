package com.twd.setting.utils.manager;

import java.util.HashMap;
import java.util.Map;

public class DataMapUtils {
    private final Map<String, String> dataMap = new HashMap(16, 0.75F);

    public static DataMapUtils getInstance() {
        return new DataMapUtils();
    }

    public static DataMapUtils getInstance(String paramString1, String paramString2) {
        return new DataMapUtils().putData(paramString1, paramString2);
    }

    public Map<String, String> getDataMap() {
        return dataMap;
    }

    public DataMapUtils putData(String paramString1, String paramString2) {
        dataMap.put(paramString1, paramString2);
        return this;
    }
}