package com.twd.setting.utils.binding;

import android.view.View;

public abstract interface ItemClickHandle<T> {
    public abstract void onClick(View paramView, T paramT);
}