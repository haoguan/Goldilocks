package com.haowu.goldilocks.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class ExtendedLayout extends LinearLayout
{
    private Context myContext;

    public ExtendedLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        myContext = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = getMeasuredHeight();
        int width = getMeasuredWidth();
        super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height*2, MeasureSpec.EXACTLY));
    }
    
}