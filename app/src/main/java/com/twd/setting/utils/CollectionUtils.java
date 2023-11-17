package com.twd.setting.utils;

import java.util.List;

public class CollectionUtils {
    public static boolean isEmpty(List paramList) {
        return (paramList == null) || (paramList.isEmpty());
    }
}