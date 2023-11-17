package com.twd.setting.module.network.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.Arrays;
import java.util.Locale;

public class WifiSsid
        implements Parcelable {
    public static final Parcelable.Creator<WifiSsid> CREATOR = new Parcelable.Creator() {
        public WifiSsid createFromParcel(Parcel source) {
            WifiSsid localWifiSsid = new WifiSsid();
            int i = source.readInt();
            byte[] arrayOfByte = new byte[i];
            source.readByteArray(arrayOfByte);
            localWifiSsid.octets.write(arrayOfByte, 0, i);
            return localWifiSsid;
        }

        public WifiSsid[] newArray(int size) {
            return new WifiSsid[size];
        }
    };
    private static final int HEX_RADIX = 16;
    public static final String NONE = "<unknown ssid>";
    private static final String TAG = "WifiSsid";
    public final ByteArrayOutputStream octets = new ByteArrayOutputStream(32);

    private void convertToBytes(String paramString) {
        Log.d(TAG,"=====================convertToBytes========================");
        int i = 0;
        if (i < paramString.length()) {
            int j = paramString.charAt(i);
            if (j != 92) {
                this.octets.write(j);
            }
            for (; ; ) {
                i += 1;
                //  break;
                i += 1;
                j = paramString.charAt(i);
                int k = 0;
                int m;
                if (j != 34) {
                    if (j != 92) {
                        if (j != 101) {
                            if (j != 110) {
                                if (j != 114) {
                                    if (j != 116) {
                                        if (j != 120) {
                                            switch (j) {
                                                default:
                                                    break;
                                                case 48:
                                                case 49:
                                                case 50:
                                                case 51:
                                                case 52:
                                                case 53:
                                                case 54:
                                                case 55:
                                                    k = paramString.charAt(i) - '0';
                                                    m = i + 1;
                                                    i = m;
                                                    j = k;
                                                    if (paramString.charAt(m) >= '0') {
                                                        i = m;
                                                        j = k;
                                                        if (paramString.charAt(m) <= '7') {
                                                            j = k * 8 + paramString.charAt(m) - 48;
                                                            i = m + 1;
                                                        }
                                                    }
                                                    k = i;
                                                    m = j;
                                                    if (paramString.charAt(i) >= '0') {
                                                        k = i;
                                                        m = j;
                                                        if (paramString.charAt(i) <= '7') {
                                                            m = j * 8 + paramString.charAt(i) - 48;
                                                            k = i + 1;
                                                        }
                                                    }
                                                    this.octets.write(m);
                                                    i = k;
                                                    break;
                                            }
                                        }
                                        i += 1;
                                        k = i + 2;
                                        j = -1;
                                    }
                                }
                            }
                        }
                    }
                }
                try {
                    m = Integer.parseInt(paramString.substring(i, k), 16);
                    j = m;
                } catch (NumberFormatException |
                         StringIndexOutOfBoundsException localNumberFormatException) {
                    for (; ; ) {
                    }
                }
                if (j < 0) {
                    j = Character.digit(paramString.charAt(i), 16);
                    if (j < 0) {
                        break;
                    }
                    this.octets.write(j);
                    continue;
                }
                this.octets.write(j);
                i = k;
                //     break;
                //     this.octets.write(9);
                //     continue;
                //     this.octets.write(13);
                //     continue;
                //     this.octets.write(10);
                //      continue;
                //      this.octets.write(27);
                //      continue;
                //      this.octets.write(92);
                //      continue;
                this.octets.write(34);
            }
        }
    }

    public static WifiSsid createFromAsciiEncoded(String paramString) {
        WifiSsid localWifiSsid = new WifiSsid();
        localWifiSsid.convertToBytes(paramString);
        return localWifiSsid;
    }

    public static WifiSsid createFromByteArray(byte[] paramArrayOfByte) {
        WifiSsid localWifiSsid = new WifiSsid();
        if (paramArrayOfByte != null) {
            localWifiSsid.octets.write(paramArrayOfByte, 0, paramArrayOfByte.length);
        }
        return localWifiSsid;
    }

    public static WifiSsid createFromHex(String paramString) {
        Log.d(TAG,"===================createFromHex=========================");
        WifiSsid localWifiSsid = new WifiSsid();
        if (paramString == null) {
            return localWifiSsid;
        }
        String str;
        if (!paramString.startsWith("0x")) {
            str = paramString;
            if (!paramString.startsWith("0X")) {
            }
        } else {
            str = paramString.substring(2);
        }
        int j;
        for (int i = 0; i < str.length() - 1; i = j) {
            j = i + 2;
            try {
                i = Integer.parseInt(str.substring(i, j), 16);
            } catch (NumberFormatException numberFormatException) {
                for (; ; ) {
                }
            }
            i = 0;
            localWifiSsid.octets.write(i);
        }
        return localWifiSsid;
    }

    private boolean isArrayAllZeroes(byte[] paramArrayOfByte) {
        int i = 0;
        while (i < paramArrayOfByte.length) {
            if (paramArrayOfByte[i] != 0) {
                return false;
            }
            i += 1;
        }
        return true;
    }

    public int describeContents() {
        return 0;
    }

    public boolean equals(Object paramObject) {
        if (this == paramObject) {
            return true;
        }
        if (!(paramObject instanceof WifiSsid)) {
            return false;
        }
        paramObject = (WifiSsid) paramObject;
        return Arrays.equals(octets.toByteArray(), ((WifiSsid) paramObject).octets.toByteArray());
    }

    public String getHexString() {
        byte[] arrayOfByte = getOctets();
        String str = "0x";
        int i = 0;
        while (i < this.octets.size()) {
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append(str);
            localStringBuilder.append(String.format(Locale.US, "%02x", new Object[]{Byte.valueOf(arrayOfByte[i])}));
            str = localStringBuilder.toString();
            i += 1;
        }
        if (this.octets.size() > 0) {
            return str;
        }
        return null;
    }

    public byte[] getOctets() {
        return octets.toByteArray();
    }

    public int hashCode() {
        return Arrays.hashCode(octets.toByteArray());
    }

    public boolean isHidden() {
        return isArrayAllZeroes(octets.toByteArray());
    }

    public String toString() {
        Object localObject = octets.toByteArray();
        if ((octets.size() > 0) && (!isArrayAllZeroes((byte[]) localObject))) {
            CharsetDecoder localCharsetDecoder = Charset.forName("UTF-8").newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
            CharBuffer localCharBuffer = CharBuffer.allocate(32);
            CoderResult coderResult = localCharsetDecoder.decode(ByteBuffer.wrap((byte[]) localObject), localCharBuffer, true);
            localCharBuffer.flip();
            if (coderResult.isError()) {
                return "<unknown ssid>";
            }
            return localCharBuffer.toString();
        }
        return "";
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(octets.size());
        dest.writeByteArray(octets.toByteArray());
    }
}
