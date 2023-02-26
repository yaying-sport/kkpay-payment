package com.kaiserkalep.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.kaiserkalep.utils.skinlibrary.utils.TypefaceUtils;

/**
 * 金额字体 DINPro-Medium
 *
 * @Auther: Jack
 * @Date: 2021/1/2 13:33
 * @Description:
 */
@SuppressLint("AppCompatCustomView")
public class MoneyTextView extends TextView {

    public MoneyTextView(Context context) {
        super(context);
    }

    public MoneyTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MoneyTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setTypeface(TypefaceUtils.getMoneyTypeface());
    }
}
