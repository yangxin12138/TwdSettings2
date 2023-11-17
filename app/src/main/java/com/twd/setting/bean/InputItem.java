package com.twd.setting.bean;

public class InputItem {
    private String inputName;
    private String inputId;
    private boolean isSelected;

    public InputItem(String inputName, String inputId, boolean isSelected) {
        this.inputName = inputName;
        this.inputId = inputId;
        this.isSelected = isSelected;
    }

    public String getInputName() {
        return inputName;
    }

    public void setInputName(String inputName) {
        this.inputName = inputName;
    }

    public String getInputId() {
        return inputId;
    }

    public void setInputId(String inputId) {
        this.inputId = inputId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
