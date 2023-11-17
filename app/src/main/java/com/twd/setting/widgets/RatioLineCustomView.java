package com.twd.setting.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;

import com.twd.setting.R;
import com.twd.setting.R.styleable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RatioLineCustomView
        extends View {
    private int contentWidth;
    private float descTextPaddingLeft;
    private float descTextPaddingRight;
    private float descTextPaddingTop;
    private float descTextSize;
    private int descTextTitleColor = -16777216;
    private boolean isRoundStyle = true;
    private List<ItemData> itemDataList;
    private final RectF itemRectF = new RectF();
    private float lineSize;
    private Paint mPaint;
    private float totalValue;

    public RatioLineCustomView(Context paramContext) {
        this(paramContext, null);
    }

    public RatioLineCustomView(Context paramContext, AttributeSet paramAttributeSet) {
        this(paramContext, paramAttributeSet, 0);
    }

    public RatioLineCustomView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        init(paramContext, paramAttributeSet);
    }

    public RatioLineCustomView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2) {
        super(paramContext, paramAttributeSet, paramInt1, paramInt2);
        init(paramContext, paramAttributeSet);
    }

    private float adjustMinWidth(ItemData paramItemData) {
        float f1 = paramItemData.valueF / totalValue * contentWidth;
        float f2 = this.lineSize;
        if (f1 < f2) {
            return f2 - f1;
        }
        return 0.0F;
    }

    private void drawDescText(Canvas paramCanvas) {
        Object localObject = itemDataList;
        if (localObject != null) {
            if (((List) localObject).size() == 0) {
                return;
            }
            float f1 = getPaddingLeft();
            int i = 0;
            while (i < itemDataList.size()) {
                localObject = (ItemData) itemDataList.get(i);
                float f2 = f1;
                if (localObject != null) {
                    f2 = f1;
                    if (((ItemData) localObject).describeTitle != null) {
                        if (((ItemData) localObject).describeTitle.trim().length() == 0) {
                            f2 = f1;
                        } else {
                            mPaint.setColor(((ItemData) localObject).colorC);
                            itemRectF.left = f1;
                            itemRectF.top = (getPaddingTop() + lineSize + descTextPaddingTop);
                            RectF localRectF = itemRectF;
                            localRectF.right = (localRectF.left + descTextSize);
                            localRectF = itemRectF;
                            localRectF.bottom = (localRectF.top + descTextSize);
                            paramCanvas.drawRect(itemRectF, mPaint);
                            f2 = descTextSize;
                            mPaint.setColor(descTextTitleColor);
                            float f3 = itemRectF.top;
                            float f4 = descTextSize;
                            float f5 = mPaint.descent() / 2.0F;
                            paramCanvas.drawText(((ItemData) localObject).describeTitle, itemRectF.left + descTextSize + descTextPaddingLeft, f3 + f4 - f5, mPaint);
                            f2 = f1 + f2 + (descTextPaddingLeft + mPaint.measureText(((ItemData) localObject).describeTitle)) + descTextPaddingRight;
                        }
                    }
                }
                i += 1;
                f1 = f2;
            }
        }
    }

    private void drawMutiRect(Canvas paramCanvas) {
        Object localObject = itemDataList;
        if (localObject != null) {
            if (((List) localObject).size() == 0) {
                return;
            }
            int i = 0;
            float f2;
            for (float f1 = 0.0F; i < itemDataList.size(); f1 = f2) {
                localObject = (ItemData) itemDataList.get(i);
                f2 = ((ItemData) localObject).valueF / totalValue;
                float f3 = contentWidth;
                mPaint.setColor(((ItemData) localObject).colorC);
                f2 = f1 + f2 * f3;
                paramCanvas.drawRect(f1, 0.0F, f2, lineSize, mPaint);
                i += 1;
            }
        }
    }

    private void drawMutiRoundRect(Canvas paramCanvas) {
        Object localObject = itemDataList;
        if (itemDataList != null) {
            if (itemDataList.size() == 0) {
                return;
            }
            float f2 = getPaddingLeft();
            int m = 0;
            float f1 = adjustMinWidth((ItemData) itemDataList.get(0));
            ItemData itemData = (ItemData) itemDataList.get(itemDataList.size() - 1);
            float f5 = f1 + adjustMinWidth(itemData);
            int j = 0;
            int k;
            for (int i = 0; j < itemDataList.size(); i = k) {
                ItemData itemData2 = (ItemData) itemDataList.get(j);
                k = i;
                if (itemData2.valueF / totalValue * contentWidth > lineSize * 2.0F) {
                    k = i + 1;
                }
                j += 1;
            }
            itemRectF.left = getPaddingLeft();
            itemRectF.top = getPaddingTop();
            itemRectF.right = (itemRectF.left + contentWidth);
            itemRectF.bottom = (itemRectF.top + lineSize);
            mPaint.setColor(itemData.colorC);
            localObject = itemRectF;
            f1 = lineSize;
            paramCanvas.drawRoundRect((RectF) itemRectF, f1, f1, mPaint);
            j = m;
            while (j < itemDataList.size() - 1) {
                ItemData ItemData3 = (ItemData) itemDataList.get(j);
                float f3 = ItemData3.valueF / totalValue * contentWidth;
                if (j == 0) {
                    float f4 = lineSize;
                    if (f3 < f4) {
                        f1 = f4;
                    } else {
                        f1 = f3;
                        if (f5 > 0.0F) {
                            f1 = f3;
                            if (f3 > f4 * 2.0F) {
                                f1 = f3 - f5 / j;
                            }
                        }
                    }
                    itemRectF.left = f2;
                    itemRectF.top = getPaddingTop();
                    itemRectF.right = (itemRectF.left + f1 + lineSize / 2.0F);
                    itemRectF.bottom = (itemRectF.top + lineSize);
                    mPaint.setColor(ItemData3.colorC);
                    f3 = lineSize;
                    paramCanvas.drawRoundRect(itemRectF, f3, f3, mPaint);
                } else {
                    f1 = f3;
                    if (f5 > 0.0F) {
                        f1 = f3;
                        if (f3 > lineSize * 2.0F) {
                            f1 = f3 - f5 / j;
                        }
                    }
                    itemRectF.left = f2;
                    itemRectF.top = getPaddingTop();
                    itemRectF.right = (itemRectF.left + f1);
                    itemRectF.bottom = (itemRectF.top + lineSize);
                    mPaint.setColor(ItemData3.colorC);
                    paramCanvas.drawRect(itemRectF, mPaint);
                }
                f2 += f1;
                j += 1;
            }
        }
    }

    private void drawSingleRoundRect(Canvas paramCanvas) {
        Object localObject = itemDataList;
        if (localObject != null) {
            if (((List) localObject).size() == 0) {
                return;
            }
            localObject = (ItemData) itemDataList.get(0);
            if (localObject == null) {
                return;
            }
            itemRectF.left = getPaddingLeft();
            itemRectF.top = getPaddingTop();
            itemRectF.right = contentWidth;
            itemRectF.bottom = lineSize;
            mPaint.setColor(((ItemData) localObject).colorC);
            localObject = itemRectF;
            float f = lineSize;
            paramCanvas.drawRoundRect((RectF) localObject, f, f, mPaint);
        }
    }

    private int getMyMeasureHeight(int paramInt) {
        int i = View.MeasureSpec.getMode(paramInt);
        paramInt = View.MeasureSpec.getSize(paramInt);
        if ((i != Integer.MIN_VALUE) && (i != 0)) {
            if (i != 1073741824) {
                return 0;
            }
            return paramInt - getPaddingTop() - getPaddingBottom();
        }
        return Math.round(lineSize + descTextPaddingTop + descTextSize * 1.2F + 0.5F) + (getPaddingTop() + getPaddingBottom());
    }

    private int getMyMeasureWidth(int paramInt) {
        int i = View.MeasureSpec.getMode(paramInt);
        int k = View.MeasureSpec.getSize(paramInt);
        int m = 0;
        int j = 0;
        int ret = i;
        if ((i != Integer.MIN_VALUE) && (i != 0)) {
            if (i != 1073741824) {
                return 0;
            }
            m = getPaddingLeft();
            int n = getPaddingRight();
            paramInt = k;
            if (itemDataList != null) {
                float f2 = 0.0F;
                float f1 = 0.0F;
                i = 0;
                paramInt = j;
                while (j < itemDataList.size()) {
                    ItemData itemData = (ItemData) itemDataList.get(j);
                    f1 = f2;
                    j = i;
                    if (itemData != null) {
                        f1 = f2;
                        j = i;
                        if (itemData.describeTitle != null) {
                            if (itemData.describeTitle.trim().length() == 0) {
                                f1 = f2;
                                j = i;
                            } else {
                                f2 += descTextSize + descTextPaddingLeft + mPaint.measureText(itemData.describeTitle);
                                f1 = f2;
                                j = i;
                                if (paramInt < itemDataList.size() - 1) {
                                    j = i + 1;
                                    f1 = f2;
                                }
                            }
                        }
                    }
                    paramInt += 1;
                    f2 = f1;
                    i = j;

                }
                if (i > 0) {
                    descTextPaddingRight = ((k - m - n - f2) / i);
                }
                f1 = descTextPaddingRight;
                f2 = descTextPaddingLeft;
                ret = k;
                if (f1 < f2) {
                    descTextPaddingRight = f2;
                    return k;
                }
            }
        } else {
            if (mPaint != null) {
                if (itemDataList != null) {
                    if (itemDataList.size() == 0) {
                        return View.MeasureSpec.getSize(paramInt);
                    }
                    i = 0;
                    paramInt = m;
                    int num = 0;
                    while (num < itemDataList.size()) {
                        ItemData itemData = (ItemData) itemDataList.get(paramInt);
                        j = i;
                        if (itemData != null) {
                            j = i;
                            if (itemData.describeTitle != null) {
                                if (itemData.describeTitle.trim().length() == 0) {
                                    j = i;
                                } else {
                                    j = (int) (i + (descTextSize + descTextPaddingLeft + descTextPaddingRight + mPaint.measureText(itemData.describeTitle)));
                                }
                            }
                        }
                        num += 1;
                        i = j;
                    }
                    paramInt = i + (getPaddingLeft() + getPaddingRight());
                }
            }
        }
        return ret;
    }

    private void init(Context context, AttributeSet paramAttributeSet) {
        if (mPaint != null) {
            return;
        }
        if (paramAttributeSet != null) {
            loadRes(context, paramAttributeSet);
        }
        if (lineSize <= 0.0F) {
            lineSize = 24.0F;
        }
        if (descTextSize <= 0.0F) {
            descTextSize = 20.0F;
        }

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setTextSize(descTextSize);
        mPaint.setStrokeWidth(1.0F);
        if (descTextPaddingRight <= 0.0F) {
            descTextPaddingRight = descTextPaddingLeft;
        }
    }

    private void loadRes(Context paramContext, AttributeSet paramAttributeSet) {
        TypedArray typedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.RatioLineCustomView);
        lineSize = typedArray.getDimension(styleable.RatioLineCustomView_lineSize, 24.0F);
        descTextSize = typedArray.getDimension(styleable.RatioLineCustomView_descTextSize, 20.0F);
        descTextPaddingTop = typedArray.getDimension(styleable.RatioLineCustomView_descTextPaddingTop, 18.0F);
        descTextPaddingLeft = typedArray.getDimension(styleable.RatioLineCustomView_descTextPaddingLeft, 18.0F);
        descTextPaddingRight = typedArray.getDimension(styleable.RatioLineCustomView_descTextPaddingRight, 18.0F);
        isRoundStyle = typedArray.getBoolean(styleable.RatioLineCustomView_isRoundStyle, true);
        descTextTitleColor = typedArray.getColor(styleable.RatioLineCustomView_descTextTitleColor, -16777216);
        typedArray.recycle();
    }

    protected void onDraw(Canvas paramCanvas) {
        if (itemDataList != null) {
            if (itemDataList.size() == 0) {
                return;
            }
            if (isRoundStyle) {
                if (itemDataList.size() == 1) {
                    drawSingleRoundRect(paramCanvas);
                } else {
                    drawMutiRoundRect(paramCanvas);
                }
            } else {
                drawMutiRect(paramCanvas);
            }
            drawDescText(paramCanvas);
        }
    }

    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
        contentWidth = (getWidth() - getPaddingLeft() - getPaddingRight());
    }

    protected void onMeasure(int paramInt1, int paramInt2) {
        setMeasuredDimension(getMyMeasureWidth(paramInt1), getMyMeasureHeight(paramInt2));
    }

    public void setAllData(List<ItemData> list) {
        if (list != null) {
            if (list.size() == 0) {
                return;
            }

            if (itemDataList == null) {
                itemDataList = new ArrayList();
            } else {
                itemDataList.clear();
            }
            totalValue = 0.0F;
            Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                ItemData itemData = (ItemData) iterator.next();
                if ((itemData != null) && (itemData.describeTitle != null) && (itemData.describeTitle.trim().length() != 0) && (itemData.valueF > 0.0F)) {
                    totalValue += itemData.valueF;
                    itemDataList.add(itemData);
                } else {
                    Log.e("LineView", "data illegel, describeTitle is null or value is 0");
                }
            }
            postInvalidate();
        }
    }

    public static class ItemData {
        private final int colorC;
        private final String describeTitle;
        private final float valueF;

        public ItemData(int paramInt, float paramFloat, String paramString) {
            colorC = paramInt;
            valueF = paramFloat;
            describeTitle = paramString;
        }
    }
}

