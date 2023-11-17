package com.twd.setting.widgets;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.animation.LinearInterpolator;

import com.twd.setting.R;
import com.twd.setting.R.styleable;

import java.text.DecimalFormat;

public class CustomDialView
        extends View {
    private ObjectAnimator animator;
    private RectF arcRF;
    private float arcRadius;
    private float arcRadiusRatio;
    private float cRadius;
    private float cRadiusRatio;
    private int circleColor;
    private int[] colors;
    float curAngle = 0.0F;
    private String curTextStr;
    private float curValue;
    private float cx;
    private float cy;
    private DecimalFormat dFormat;
    private float dashLineMargin;
    private int indicatorColor;
    private float indicatorLength;
    private float indicatorSize;
    private Paint mLinePaint;
    private Paint mPaint;
    private PathEffect pathEffect;
    private float[] positions;
    private float scaleTextMargin;
    private float scaleTextMaxWidth;
    private String scaleTextStr1;
    private String scaleTextStr2;
    private String scaleTextStr3;
    private String scaleTextStr4;
    private String scaleTextStr5;
    private float scaleValue1;
    private float scaleValue2;
    private float scaleValue3;
    private float scaleValue4;
    private float scaleValue5;
    private int textColor;
    private float textSize;
    private String unitText;
    private float unitTextSize;
    private float unitTextWidth;

    public CustomDialView(Context paramContext) {
        this(paramContext, null);
    }

    public CustomDialView(Context paramContext, AttributeSet paramAttributeSet) {
        this(paramContext, paramAttributeSet, 0);
    }

    public CustomDialView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        init(paramContext, paramAttributeSet);
    }

    public CustomDialView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2) {
        super(paramContext, paramAttributeSet, paramInt1, paramInt2);
        init(paramContext, paramAttributeSet);
    }

    private float caculateCurrentAngle() {
        float f1 = curValue;
        float f3 = scaleValue5;
        if (f1 >= f3) {
            return 180.0F;
        }
        float f2 = scaleValue4;
        if (f1 > f2) {
            f1 = (f1 - f2) / (f3 - f2) * 45.0F;
        }
    /*
    for (f2 = 135.0F;; f2 = 90.0F)
    {
      return f1 + f2;
      f3 = scaleValue3;
      if (f1 <= f3) {
        break;
      }
      f1 = (f1 - f3) / (f2 - f3) * 45.0F;
    }

  */
        f2 = scaleValue2;
        if (f1 > f2) {
            return (f1 - f2) / (f3 - f2) * 45.0F + 45.0F;
        }


        return f1 / f2 * 45.0F;
    }

    private int getMyMeasureHeight(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        if ((paramInt3 != Integer.MIN_VALUE) && (paramInt3 != 0)) {
            if (paramInt3 != 1073741824) {
                return 0;
            }
        } else {
            paramInt4 = Math.round(indicatorLength + dashLineMargin * 2.0F + indicatorSize * 2.0F + scaleTextMargin + textSize + cRadius + 0.5F) + (getPaddingTop() + getPaddingBottom());
        }
        return paramInt4;
    }

    private int getMyMeasureWidth(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        if ((paramInt1 != Integer.MIN_VALUE) && (paramInt1 != 0)) {
            if (paramInt1 != 1073741824) {
                return 0;
            }
            float f = (paramInt2 - getPaddingLeft() - getPaddingRight()) * 1.0F / 2.0F - scaleTextMaxWidth - scaleTextMargin - indicatorSize * 2.0F - dashLineMargin * 2.0F;
            indicatorLength = f;
            arcRadius = (arcRadiusRatio * f);
            cRadius = (f * cRadiusRatio);
            inspactIndicatorLength();
            return paramInt2;
        }
        inspactIndicatorLength();
        paramInt1 = Math.round((indicatorLength + dashLineMargin * 2.0F + indicatorSize * 2.0F + scaleTextMargin + scaleTextMaxWidth) * 2.0F + 0.5F);
        return getPaddingLeft() + getPaddingRight() + paramInt1;
    }

    private void init(Context paramContext, AttributeSet paramAttributeSet) {
        if (mPaint != null) {
            return;
        }
        dFormat = new DecimalFormat("#.##");
        arcRF = new RectF();
        Paint localPaint = new Paint();
        mPaint = localPaint;
        localPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.BUTT);
        localPaint = new Paint();
        mLinePaint = localPaint;
        localPaint.setAntiAlias(true);
        mLinePaint.setStyle(Paint.Style.STROKE);
        if (paramAttributeSet != null) {
            loadRes(paramContext, paramAttributeSet);
        }
        if (unitText == null) {
            unitText = "";
        }
        mLinePaint.setTextSize(unitTextSize);
        unitTextWidth = mLinePaint.measureText(unitText);
        inspactIndicatorLength();
        float f = indicatorSize;
        pathEffect = new DashPathEffect(new float[]{f, f * 2.0F}, 0.0F);
        curTextStr = dFormat.format(curValue);
    }

    private void initScaleDial(Canvas paramCanvas) {
        mLinePaint.setColor(textColor);
        mLinePaint.setStrokeWidth(indicatorSize);
        mLinePaint.setStyle(Paint.Style.STROKE);
        arcRF.left = (cx - indicatorLength - dashLineMargin * 2.0F - indicatorSize);
        arcRF.top = (cy - indicatorLength - dashLineMargin * 2.0F - indicatorSize);
        arcRF.right = (cx + indicatorLength + dashLineMargin * 2.0F + indicatorSize);
        arcRF.bottom = (cy + indicatorLength + dashLineMargin * 2.0F + indicatorSize);
        mLinePaint.setPathEffect(null);
        paramCanvas.drawArc(arcRF, 180.0F, 180.0F, false, mLinePaint);
        arcRF.left = (cx - indicatorLength - dashLineMargin);
        arcRF.top = (cy - indicatorLength - dashLineMargin);
        arcRF.right = (cx + indicatorLength + dashLineMargin);
        arcRF.bottom = (cy + indicatorLength + dashLineMargin);
        mLinePaint.setPathEffect(pathEffect);
        paramCanvas.drawArc(arcRF, 180.0F, 180.0F, false, mLinePaint);
        mLinePaint.setPathEffect(null);
        mLinePaint.setStrokeWidth(0.0F);
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setTextSize(textSize);
        float f3 = indicatorLength + dashLineMargin * 2.0F + indicatorSize * 2.0F + scaleTextMargin;
        float f2 = mLinePaint.measureText(scaleTextStr1);
        float f4 = scaleTextMaxWidth;
        float f1 = f2;
        if (f2 < f4) {
            f1 = f2 + (f4 - f2) / 2.0F;
        }
        paramCanvas.drawText(scaleTextStr1, cx - f3 - f1, cy, mLinePaint);
        f4 = mLinePaint.measureText(scaleTextStr2);
        f1 = (float) Math.cos(0.7853981633974483D) * f3;
        f2 = (float) Math.sin(0.7853981633974483D) * f3;
        paramCanvas.drawText(scaleTextStr2, cx - f1 - f4, cy - f2, mLinePaint);
        f4 = mLinePaint.measureText(scaleTextStr3);
        paramCanvas.drawText(scaleTextStr3, cx - f4 / 2.0F, cy - f3, mLinePaint);
        paramCanvas.drawText(scaleTextStr4, cx + f1, cy - f2, mLinePaint);
        paramCanvas.drawText(scaleTextStr5, cx + f3, cy, mLinePaint);
    }

    private void inspactIndicatorLength() {
        if (indicatorLength <= 0.0F) {
            indicatorLength = 2.0F;
        }
        float f1 = arcRadiusRatio;
        float f2 = cRadiusRatio;
        if (f1 <= f2) {
            arcRadiusRatio = (0.1F + f2);
        }
        if (cRadius <= 0.0F) {
            cRadius = (indicatorLength * f2);
        }
        if (arcRadius <= 0.0F) {
            arcRadius = (indicatorLength * arcRadiusRatio);
        }
        f1 = arcRadius;
        f2 = cRadius;
        if (f1 <= f2) {
            arcRadius = (f2 * 1.1F);
        }
        f1 = indicatorLength;
        f2 = arcRadius;
        if (f1 < f2) {
            indicatorLength = (f2 / arcRadiusRatio);
        }
    }

    private void loadRes(Context context, AttributeSet paramAttributeSet) {
        TypedArray typedArray = context.obtainStyledAttributes(paramAttributeSet, R.styleable.CustomDialView);
        textSize = typedArray.getDimension(styleable.CustomDialView_textSize, 12.0F);
        unitTextSize = typedArray.getDimension(styleable.CustomDialView_unitTextSize, 8.0F);
        indicatorSize = typedArray.getDimension(styleable.CustomDialView_indicatorSize, 2.0F);
        indicatorLength = typedArray.getDimension(styleable.CustomDialView_indicatorLength, 50.0F);
        cRadius = typedArray.getDimension(styleable.CustomDialView_cRadius, 0.0F);
        arcRadius = typedArray.getDimension(styleable.CustomDialView_arcRadius, 0.0F);
        scaleTextMargin = typedArray.getDimension(styleable.CustomDialView_scaleTextMargin, 2.0F);
        dashLineMargin = typedArray.getDimension(styleable.CustomDialView_dashLineMargin, 10.0F);
        arcRadiusRatio = typedArray.getDimension(styleable.CustomDialView_arcRadiusRatio, 0.8F);
        cRadiusRatio = typedArray.getDimension(styleable.CustomDialView_cRadiusRatio, 0.5F);
        colors = new int[]{0, typedArray.getColor(styleable.CustomDialView_startColor, Color.BLUE), typedArray.getColor(styleable.CustomDialView_middleColor, Color.BLUE), typedArray.getColor(styleable.CustomDialView_endColor, Color.BLUE)};
        positions = new float[]{0.0F, 0.5F, 0.75F, 1.0F};
        circleColor = typedArray.getColor(styleable.CustomDialView_circleColor, Color.GRAY);
        textColor =  typedArray.getColor(styleable.CustomDialView_textColor,  Color.BLACK);
        indicatorColor = typedArray.getColor(styleable.CustomDialView_indicatorColor, Color.BLUE);
        unitText = typedArray.getString(styleable.CustomDialView_unitText);
        setScaleValue(typedArray.getFloat(styleable.CustomDialView_scaleValue1, 0.0F), typedArray.getFloat(styleable.CustomDialView_scaleValue2, 0.5F), typedArray.getFloat(styleable.CustomDialView_scaleValue3, 4.0F), typedArray.getFloat(styleable.CustomDialView_scaleValue4, 20.0F), typedArray.getFloat(styleable.CustomDialView_scaleValue5, 100.0F));
        typedArray.recycle();
    }

    private void refreshView() {
        curTextStr = dFormat.format(curValue);
        postInvalidate();
    }

    public void clear() {
        ObjectAnimator localObjectAnimator = animator;
        if (localObjectAnimator != null) {
            localObjectAnimator.cancel();
            animator.removeAllListeners();
        }
    }

    public float getCurAngle() {
        return curAngle;
    }

    protected void onDraw(Canvas paramCanvas) {
        initScaleDial(paramCanvas);
        RectF localRectF = arcRF;
        float f1 = cx;
        float f2 = arcRadius;
        localRectF.left = (f1 - f2 + (f2 - cRadius) / 2.0F);
        localRectF = arcRF;
        f1 = cy;
        f2 = arcRadius;
        localRectF.top = (f1 - f2 + (f2 - cRadius) / 2.0F);
        localRectF = arcRF;
        f1 = cx;
        f2 = arcRadius;
        localRectF.right = (f1 + f2 - (f2 - cRadius) / 2.0F);
        localRectF = arcRF;
        f1 = cy;
        f2 = arcRadius;
        localRectF.bottom = (f1 + f2 - (f2 - cRadius) / 2.0F);
        mPaint.setStrokeWidth(arcRadius - cRadius);
        paramCanvas.drawArc(arcRF, 180.0F, curAngle, false, mPaint);
        paramCanvas.save();
        paramCanvas.rotate(-(180.0F - curAngle), cx, cy);
        mLinePaint.setColor(indicatorColor);
        mLinePaint.setStrokeWidth(indicatorSize);
        f1 = cx;
        f2 = cy;
        paramCanvas.drawLine(f1, f2, indicatorLength + f1, f2, mLinePaint);
        paramCanvas.restore();
        mLinePaint.setStrokeWidth(0.0F);
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setColor(circleColor);
        paramCanvas.drawCircle(cx, cy, cRadius, mLinePaint);
        mLinePaint.setColor(textColor);
        mLinePaint.setTextSize(textSize);
        f1 = mLinePaint.measureText(curTextStr);
        f2 = cy + mLinePaint.descent();
        paramCanvas.drawText(curTextStr, cx - (unitTextWidth + f1) / 2.0F, f2, mLinePaint);
        mLinePaint.setTextSize(unitTextSize);
        paramCanvas.drawText(unitText, cx + f1 / 2.0F - unitTextWidth / 2.0F, f2, mLinePaint);
    }

    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
        float f1 = (getWidth() - getPaddingLeft() - getPaddingRight()) * 1.0F / 2.0F + getPaddingLeft();
        float f2 = getHeight() - getPaddingBottom() - cRadius;
        if ((cx != f1) || (cy != f2)) {
            cx = f1;
            cy = f2;
            mPaint.setShader(new SweepGradient(cx, cy, colors, positions));
        }
    }

    protected void onMeasure(int paramInt1, int paramInt2) {
        int i = View.MeasureSpec.getMode(paramInt1);
        paramInt1 = View.MeasureSpec.getSize(paramInt1);
        int j = View.MeasureSpec.getMode(paramInt2);
        paramInt2 = View.MeasureSpec.getSize(paramInt2);
        setMeasuredDimension(getMyMeasureWidth(i, paramInt1, j, paramInt2), getMyMeasureHeight(i, paramInt1, j, paramInt2));
    }

    public void setCurAngle(float paramFloat) {
        curAngle = paramFloat;
        invalidate();
    }

    public void setScaleValue(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5) {
        scaleValue1 = paramFloat1;
        scaleValue2 = paramFloat2;
        scaleValue3 = paramFloat3;
        scaleValue4 = paramFloat4;
        scaleValue5 = paramFloat5;
        scaleTextStr1 = dFormat.format(paramFloat1);
        scaleTextStr2 = dFormat.format(scaleValue2);
        scaleTextStr3 = dFormat.format(scaleValue3);
        scaleTextStr4 = dFormat.format(scaleValue4);
        scaleTextStr5 = dFormat.format(scaleValue5);
        mLinePaint.setTextSize(textSize);
        scaleTextMaxWidth = mLinePaint.measureText(scaleTextStr1);
        paramFloat1 = mLinePaint.measureText(scaleTextStr5);
        if (scaleTextMaxWidth < paramFloat1) {
            scaleTextMaxWidth = paramFloat1;
        }
        paramFloat1 = scaleTextMaxWidth;
        paramFloat2 = textSize;
        if (paramFloat1 < paramFloat2) {
            scaleTextMaxWidth = paramFloat2;
        }
        requestLayout();
    }

    public void start(float paramFloat) {
        if (curValue == paramFloat) {
            return;
        }
        curValue = paramFloat;
        float f = scaleValue1;
        if (paramFloat < f) {
            curValue = f;
        }
        paramFloat = caculateCurrentAngle();
        ObjectAnimator localObjectAnimator = animator;
        if (localObjectAnimator != null) {
            localObjectAnimator.setFloatValues(new float[]{paramFloat});
        } else {
            localObjectAnimator = ObjectAnimator.ofFloat(this, "curAngle", new float[]{curAngle, paramFloat});
            animator = localObjectAnimator;
            localObjectAnimator.setDuration(400L);
            animator.setInterpolator(new LinearInterpolator());
            animator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator paramAnonymousAnimator) {
                    CustomDialView.this.refreshView();
                }
            });
        }
        animator.start();
    }
}
