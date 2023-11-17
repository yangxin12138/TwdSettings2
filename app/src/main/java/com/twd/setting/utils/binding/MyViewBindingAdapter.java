package com.twd.setting.utils.binding;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;

public class MyViewBindingAdapter {
    private static void setIntrinsicBounds(Drawable paramDrawable) {
        if (paramDrawable != null) {
            paramDrawable.setBounds(0, 0, paramDrawable.getIntrinsicWidth(), paramDrawable.getIntrinsicHeight());
        }
    }

    public static void setTextViewDrawableLeft(TextView paramTextView, int paramInt) {
        Drawable[] arrayOfDrawable = paramTextView.getCompoundDrawables();
        if (paramInt == 0) {
            paramTextView.setCompoundDrawables(null, arrayOfDrawable[1], arrayOfDrawable[2], arrayOfDrawable[3]);
            return;
        }
        Drawable localDrawable = AppCompatResources.getDrawable(paramTextView.getContext(), paramInt);
        setIntrinsicBounds(localDrawable);
        paramTextView.setCompoundDrawables(localDrawable, arrayOfDrawable[1], arrayOfDrawable[2], arrayOfDrawable[3]);
    }

    public static void setTextViewDrawableRight(TextView paramTextView, int paramInt) {
        Drawable[] arrayOfDrawable = paramTextView.getCompoundDrawables();
        if (paramInt == 0) {
            paramTextView.setCompoundDrawables(arrayOfDrawable[0], arrayOfDrawable[1], null, arrayOfDrawable[3]);
            return;
        }
        Drawable localDrawable = AppCompatResources.getDrawable(paramTextView.getContext(), paramInt);
        setIntrinsicBounds(localDrawable);
        paramTextView.setCompoundDrawables(arrayOfDrawable[0], arrayOfDrawable[1], localDrawable, arrayOfDrawable[3]);
    }

    public static void srcCompat(ImageView paramImageView, int paramInt) {
        paramImageView.setImageResource(paramInt);
    }
}