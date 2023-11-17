package com.twd.setting.module.network.speed;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.twd.setting.utils.HLog;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;

public class NetworkSpeedTester
        extends Handler {
    private static final String LOG_TAG = "NetworkSpeedTester";
    public static final int MSG_END_TEST_SPEED = 4098;
    public static final int MSG_RESULT_TEST_SPEED = 4099;
    public static final int MSG_START_TEST_SPEED = 4096;
    public static final int MSG_TESTING_SPEED = 4097;
    private final int INTERVAL_MILLISECOND_DISPLAY_RESULT = 2000;
    private final int INTERVAL_MILLISECOND_GET_INFO = 500;
    private final int MAX_TEST_TIME = 8000;
    private String averageSpeed = "";
    private final INetSpeedTest iNetSpeedTest;
    private NetSpeedInfo info;
    private boolean isStart;
    private boolean shouldSendHandlerMsg = true;
    private String urlDownload;

    public NetworkSpeedTester(Looper paramLooper, String paramString, INetSpeedTest paramINetSpeedTest) {
        super(paramLooper);
        this.urlDownload = paramString;
        this.iNetSpeedTest = paramINetSpeedTest;
    }

    private String formatByte(float paramFloat) {
        int i = 0;
        while ((paramFloat > 1024.0F) && (i < 3)) {
            paramFloat /= 1024.0F;
            i += 1;
        }
        String str = String.format(Locale.getDefault(), "%.1f", new Object[]{Float.valueOf(paramFloat)});
        if (i == 0) {
            str = str + " B";
        } else if (i == 1) {
            str = str + " KB";
        } else if (i == 2) {
            str = str + " MB";
        } else {
            str = str + " GB";
        }
        return str;
    }

    private String formatSpeed(int paramInt) {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append(String.format(Locale.getDefault(), "%.2f", new Object[]{Float.valueOf(paramInt / 1024.0F / 1024.0F)}));
        localStringBuilder.append(" MB/s");
        return localStringBuilder.toString();
    }

    public NetSpeedInfo getNetSpeedInfo() {
        if (this.info == null) {
            this.info = new NetSpeedInfo();
        }
        return this.info;
    }

    public void handleMessage(Message paramMessage) {
        if (this.iNetSpeedTest == null) {
            return;
        }
        Object localObject;
        switch (paramMessage.what) {
            default:
                break;
            case 4099:
                HLog.d(LOG_TAG, "NetSpeedTestHandler receive: MSG_RESULT_TEST_SPEED");
                this.iNetSpeedTest.onResult(this.averageSpeed);
                break;
            case 4098:
                HLog.d(LOG_TAG, "NetSpeedTestHandler receive: MSG_END_TEST_SPEED");
                this.isStart = false;
                this.iNetSpeedTest.onEnd();
                localObject = Message.obtain();
                ((Message) localObject).what = 4099;
                sendMessageDelayed((Message) localObject, 2000L);
                break;
            case 4097:
                HLog.d(LOG_TAG, "NetSpeedTestHandler receive: MSG_TESTING_SPEED");
                if (this.isStart) {
                    this.averageSpeed = formatSpeed(paramMessage.arg2);
                    localObject = this.iNetSpeedTest;
                    StringBuilder localStringBuilder = new StringBuilder();
                    localStringBuilder.append(paramMessage.obj);
                    localStringBuilder.append("%");
                    ((INetSpeedTest) localObject).onTesting(localStringBuilder.toString(), formatSpeed(paramMessage.arg1), this.averageSpeed);
                }
                break;
            case 4096:
                HLog.d(LOG_TAG, "NetSpeedTestHandler receive: MSG_START_TEST_SPEED");
                this.isStart = true;
                this.iNetSpeedTest.onStart();
                new DownloadThread().start();
                new GetInfoThread().start();
        }
        super.handleMessage(paramMessage);
    }

    public void setShouldSendHandlerMsg(boolean paramBoolean) {
        this.shouldSendHandlerMsg = paramBoolean;
    }

    public void startTestSpeed() {
        HLog.d(LOG_TAG, "start test speed");
        NetSpeedInfo localNetSpeedInfo = getNetSpeedInfo();
        localNetSpeedInfo.setHadFinishByte(0L);
        localNetSpeedInfo.setTotalByte(1024L);
        localNetSpeedInfo.setSpeed(0.0D);
        sendEmptyMessage(4096);
    }

    class DownloadThread
            extends Thread {
        DownloadThread() {
        }

        public void run() {
            try {
                HLog.d(LOG_TAG, "NetSpeedTestHandler download url: " + urlDownload);
                URLConnection urlConnection = new URL(urlDownload).openConnection();
                NetSpeedInfo netSpeedInfo = getNetSpeedInfo();
                netSpeedInfo.setTotalByte(2147483647L);

                HLog.d(LOG_TAG, "---- measure start ---- info.handFinishByte = " + netSpeedInfo.getHadFinishByte());
                InputStream inputStream = urlConnection.getInputStream();
                long l1 = System.currentTimeMillis();
                while ((inputStream.read() != -1) && (shouldSendHandlerMsg)) {
                    netSpeedInfo.setHadFinishByte(netSpeedInfo.getHadFinishByte() + 1L);
                    long l2 = System.currentTimeMillis() - l1;
                    if (l2 == 0L) {
                        netSpeedInfo.setSpeed(1000.0D);
                    } else {
                        netSpeedInfo.setSpeed(netSpeedInfo.getHadFinishByte() / l2 * 1000L);
                        if (l2 >= 8000L) {
                            HLog.d(NetworkSpeedTester.LOG_TAG, "maximum speed measurement time: 8 s.");
                        }
                    }
                }
                inputStream.close();
                netSpeedInfo.setTotalByte(netSpeedInfo.getHadFinishByte());
                HLog.d(LOG_TAG, "total bytes: " + netSpeedInfo.getHadFinishByte());
                HLog.d(LOG_TAG, "---- measure end ---- info.handFinishByte = " + netSpeedInfo.getHadFinishByte());
                return;
            } catch (Exception localException) {
                Object localObject2 = NetworkSpeedTester.LOG_TAG;
                Object localObject3 = new StringBuilder();
                ((StringBuilder) localObject3).append("---- measure exception ---- info.handFinishByte = ");
                ((StringBuilder) localObject3).append(NetworkSpeedTester.this.info.getHadFinishByte());
                HLog.d((String) localObject2, ((StringBuilder) localObject3).toString());
                localException.printStackTrace();
            }
        }
    }

    class GetInfoThread
            extends Thread {
        long percent;
        long startTime;

        GetInfoThread() {
        }

        public void run() {
            if (!NetworkSpeedTester.this.shouldSendHandlerMsg) {
                HLog.d(NetworkSpeedTester.LOG_TAG, "NetworkSpeedTester shouldSendHandlerMsg = false");
                return;
            }
            try {
                this.startTime = System.currentTimeMillis();
                NetSpeedInfo localNetSpeedInfo = NetworkSpeedTester.this.getNetSpeedInfo();
                double d2 = 0.0D;
                double d1 = 0.0D;
                while (localNetSpeedInfo.getHadFinishByte() < localNetSpeedInfo.getTotalByte()) {
                    Thread.sleep(500L);
                    long l = System.currentTimeMillis();
                    d2 += localNetSpeedInfo.getSpeed();
                    d1 += 1.0D;
                    l = (l - this.startTime) * 100L / 8000L;
                    this.percent = l;
                    if (l >= 100L) {
                        break;
                    }
                    int i = (int) localNetSpeedInfo.getSpeed();
                    int j = (int) (d2 / d1);
                    Object localObject = NetworkSpeedTester.LOG_TAG;
                    StringBuilder localStringBuilder = new StringBuilder();
                    localStringBuilder.append("~~~~ percent: ");
                    localStringBuilder.append(this.percent);
                    localStringBuilder.append("%, cur_speed: ");
                    localStringBuilder.append(NetworkSpeedTester.this.formatByte(i));
                    localStringBuilder.append("/s, ave_speed: ");
                    localStringBuilder.append(NetworkSpeedTester.this.formatByte(j));
                    localStringBuilder.append("/s");
                    HLog.d((String) localObject, localStringBuilder.toString());
                    localObject = new Message();
                    ((Message) localObject).arg1 = ((int) localNetSpeedInfo.getSpeed());
                    ((Message) localObject).arg2 = j;
                    ((Message) localObject).obj = Long.valueOf(this.percent);
                    ((Message) localObject).what = 4097;
                    NetworkSpeedTester.this.sendMessage((Message) localObject);
                }
                NetworkSpeedTester.this.sendEmptyMessage(4098);
                return;
            } catch (Exception localException) {
                localException.printStackTrace();
            }
        }
    }

    public static abstract interface INetSpeedTest {
        public abstract void onEnd();

        public abstract void onResult(String paramString);

        public abstract void onStart();

        public abstract void onTesting(String paramString1, String paramString2, String paramString3);
    }
}
