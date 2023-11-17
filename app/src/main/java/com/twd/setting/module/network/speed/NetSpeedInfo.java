package com.twd.setting.module.network.speed;

public class NetSpeedInfo {
    private long hadFinishByte;
    private double speed;
    private long totalByte;

    public long getHadFinishByte() {
        return this.hadFinishByte;
    }

    public double getSpeed() {
        return this.speed;
    }

    public long getTotalByte() {
        return this.totalByte;
    }

    public void setHadFinishByte(long paramLong) {
        this.hadFinishByte = paramLong;
    }

    public void setSpeed(double paramDouble) {
        this.speed = paramDouble;
    }

    public void setTotalByte(long paramLong) {
        this.totalByte = paramLong;
    }
}

