package com.twd.setting.module;

import android.content.ComponentName;

public class MainItemVisible {
    private boolean bluetoothVisible = true;
    private ComponentName projectorCname;
    private boolean projectorVisible = true;
    private ComponentName sourceCname;
    private boolean sourceVisible = true;

    public ComponentName getProjectorCname() {
        return projectorCname;
    }

    public ComponentName getSourceCname() {
        return sourceCname;
    }

    public boolean isBluetoothVisible() {
        return bluetoothVisible;
    }

    public boolean isProjectorVisible() {
        return projectorVisible;
    }

    public boolean isSourceVisible() {
        return sourceVisible;
    }

    public void setBluetoothVisible(boolean visible) {
        bluetoothVisible = visible;
    }

    public void setProjectorCname(ComponentName name) {
        projectorCname = name;
    }

    public void setProjectorVisible(boolean visible) {
        projectorVisible = visible;
    }

    public void setSourceCname(ComponentName name) {
        sourceCname = name;
    }

    public void setSourceVisible(boolean visible) {
        sourceVisible = visible;
    }
}
