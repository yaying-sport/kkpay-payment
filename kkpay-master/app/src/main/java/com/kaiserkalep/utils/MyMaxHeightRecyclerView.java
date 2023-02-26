package com.kaiserkalep.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import com.kaiserkalep.R;
import com.kaiserkalep.widgets.MyRecycleView;


/**
 * @Auther: Jack
 * @Date: 2020/11/3 09:13
 * @Description:
 */
public class MyMaxHeightRecyclerView extends MyRecycleView {

    float myMaxHeight;

    public MyMaxHeightRecyclerView(Context context) {
        this(context, null);
    }

    public MyMaxHeightRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    @SuppressLint("Recycle")
    public MyMaxHeightRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MyMaxHeightRecyclerView);
        myMaxHeight = a.getDimension(R.styleable.MyMaxHeightRecyclerView_my_rv_max_height, UIUtils.dip2px(context, 200));
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            //最大高度显示为设置高度
            //此处是关键，设置控件高度（在此替换成自己需要的高度）
            heightMeasureSpec = View.MeasureSpec.makeMeasureSpec((int) myMaxHeight, View.MeasureSpec.AT_MOST);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //重新计算控件高、宽
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}