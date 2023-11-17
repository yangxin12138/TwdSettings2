package com.twd.setting.commonlibrary.Utils;

public final class StringUtils {
    private StringUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static boolean equals(CharSequence paramCharSequence1, CharSequence paramCharSequence2) {
        if (paramCharSequence1 == paramCharSequence2) {
            return true;
        }
        if ((paramCharSequence1 != null) && (paramCharSequence2 != null)) {
            int j = paramCharSequence1.length();
            if (j == paramCharSequence2.length()) {
                if (((paramCharSequence1 instanceof String)) && ((paramCharSequence2 instanceof String))) {
                    return paramCharSequence1.equals(paramCharSequence2);
                }
                int i = 0;
                while (i < j) {
                    if (paramCharSequence1.charAt(i) != paramCharSequence2.charAt(i)) {
                        return false;
                    }
                    i += 1;
                }
                return true;
            }
        }
        return false;
    }

    public static boolean equalsIgnoreCase(String paramString1, String paramString2) {
        if (paramString1 == null) {
            return paramString2 == null;
        }
        return paramString1.equalsIgnoreCase(paramString2);
    }

    public static boolean isEmpty(final String str) {
        return str == null || str.length() == 0;
    }

    public static boolean isNotEmpty(final String str) {
        return !isEmpty(str);
    }

    public static boolean isBlank(final String str) {

        if (isEmpty(str))
            return true;

        for (char c : str.toCharArray()) {
            if (!Character.isWhitespace(c))
                return false;
        }

        return true;
    }

    public static boolean isNotBlank(final String str) {
        return !isBlank(str);
    }

    public static boolean isSpace(String paramString) {
        if (paramString == null) {
            return true;
        }
        int j = paramString.length();
        int i = 0;
        while (i < j) {
            if (!Character.isWhitespace(paramString.charAt(i))) {
                return false;
            }
            i += 1;
        }
        return true;
    }

    public static boolean isTrimEmpty(String paramString) {
        return (paramString == null) || (paramString.trim().length() == 0);
    }

    public static int length(CharSequence paramCharSequence) {
        if (paramCharSequence == null) {
            return 0;
        }
        return paramCharSequence.length();
    }

    public static String lowerFirstLetter(String paramString) {
        if ((paramString != null) && (paramString.length() != 0)) {
            if (!Character.isUpperCase(paramString.charAt(0))) {
                return paramString;
            }
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append(String.valueOf((char) (paramString.charAt(0) + ' ')));
            localStringBuilder.append(paramString.substring(1));
            return localStringBuilder.toString();
        }
        return "";
    }

    public static String null2Length0(String paramString) {
        String str = paramString;
        if (paramString == null) {
            str = "";
        }
        return str;
    }

    public static String reverse(String paramString) {
/*    if (paramString == null) {
      return "";
    }
    int k = paramString.length();
    if (k <= 1) {
      return paramString;
    }
    paramString = paramString.toCharArray();
    int j = 0;
    while (j < k >> 1)
    {
      int i = paramString[j];
      int m = k - j - 1;
      paramString[j] = paramString[m];
      paramString[m] = i;
      j += 1;
    }

 */
        return new String(paramString);
    }

    public static String toDBC(String paramString) {
 /*   if ((paramString != null) && (paramString.length() != 0))
    {
      paramString = paramString.toCharArray();
      int i = 0;
      int j = paramString.length;
      while (i < j)
      {
        if (paramString[i] == '　') {
          paramString[i] = 32;
        } else if ((65281 <= paramString[i]) && (paramString[i] <= 65374)) {
          paramString[i] = ((char)(paramString[i] - 65248));
        } else {
          paramString[i] = paramString[i];
        }
        i += 1;
      }
      return new String(paramString);
    }

  */
        return "";
    }

    public static String toSBC(String paramString) {
/*    if ((paramString != null) && (paramString.length() != 0))
    {
      paramString = paramString.toCharArray();
      int i = 0;
      int j = paramString.length;
      while (i < j)
      {
        if (paramString[i] == ' ') {
          paramString[i] = 12288;
        } else if (('!' <= paramString[i]) && (paramString[i] <= '~')) {
          paramString[i] = ((char)(paramString[i] + 65248));
        } else {
          paramString[i] = paramString[i];
        }
        i += 1;
      }
      return new String(paramString);
    }

 */
        return "";
    }

    public static String upperFirstLetter(String paramString) {
 /*   if ((paramString != null) && (paramString.length() != 0))
    {
      if (!Character.isLowerCase(paramString.charAt(0))) {
        return paramString;
      }
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(String.valueOf((char)(paramString.charAt(0) - ' ')));
      localStringBuilder.append(paramString.substring(1));
      return localStringBuilder.toString();
    }

  */
        return "";
    }
}
