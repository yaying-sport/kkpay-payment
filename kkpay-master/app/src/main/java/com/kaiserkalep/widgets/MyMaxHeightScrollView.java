package com.kaiserkalep.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.kaiserkalep.R;
import com.kaiserkalep.utils.UIUtils;

/**
 *  最大高度scrollView,超过设定高度即可滑动
 *
 * @Auther: Jack
 * @Date: 2019/10/13 18:47
 * @Description:
 */
public class MyMaxHeightScrollView extends ScrollView {

    float myMaxHeight;

    public MyMaxHeightScrollView(Context context) {
        this(context, null);
    }

    public MyMaxHeightScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    @SuppressLint("Recycle")
    public MyMaxHeightScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MyMaxHeightScrollView);

        myMaxHeight = a.getDimension(R.styleable.MyMaxHeightScrollView_my_max_height, UIUtils.dip2px(context, 180));
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            //最大高度显示为设置高度
            //此处是关键，设置控件高度（在此替换成自己需要的高度）
            heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) myMaxHeight, MeasureSpec.AT_MOST);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //重新计算控件高、宽
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}