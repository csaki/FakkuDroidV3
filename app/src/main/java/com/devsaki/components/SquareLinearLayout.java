package com.devsaki.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by neko on 14/05/2015.
 */
public class SquareLinearLayout extends LinearLayout
{
    public SquareLinearLayout(Context context)
    {
        super(context);
    }

    public SquareLinearLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SquareLinearLayout(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int finalMeasureSpec = makeSquareMeasureSpec(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(finalMeasureSpec, finalMeasureSpec);
    }

    private int makeSquareMeasureSpec(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);

        int size;
        if(widthMode == View.MeasureSpec.EXACTLY && widthSize == 0){
            size = widthSize;
        }
        else if(heightMode == View.MeasureSpec.EXACTLY && heightSize == 0){
            size = heightSize;
        }
        else{
            size = widthSize < heightSize ? widthSize : heightSize;
        }

        return View.MeasureSpec.makeMeasureSpec(size, View.MeasureSpec.EXACTLY);
    }
}