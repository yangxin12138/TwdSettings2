package com.twd.setting.module.projector.application;

import android.app.Application;

public class ProjectionApp extends Application {
    // 全局布尔变量，默认false
    private boolean Vertical_Reset = false;

    // 提供get/set方法
    public boolean isVertical_Reset() {
        return Vertical_Reset;
    }

    public void setVertical_Reset(boolean vertical_Reset) {
        Vertical_Reset = vertical_Reset;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化默认值
        Vertical_Reset = false;
    }
}
