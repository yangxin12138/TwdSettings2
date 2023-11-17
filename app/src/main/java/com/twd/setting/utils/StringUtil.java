package com.twd.setting.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    public static String matchFloat(String paramString) {
        Matcher matcher = Pattern.compile("\\d+(\\.\\d+)?").matcher(paramString);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }
}