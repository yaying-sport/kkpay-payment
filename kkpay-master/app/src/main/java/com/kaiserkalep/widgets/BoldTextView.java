package com.kaiserkalep.widgets;

import android.content.Context;
import android.text.TextPaint;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * 粗体,比textStyle="bold"细
 *
 * @Auther: Jack
 * @Date: 2019/12/31 19:52
 * @Description:
 */
public class BoldTextView extends AppCompatTextView {

    private TextPaint paint;

    public BoldTextView(Context context) {
        super(context);
        setTextBold();
    }

    public BoldTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setTextBold();
    }

    public BoldTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTextBold();
    }

    public void setTextBold() {
        setTextBold(true);
    }

    public void setTextBold(boolean fakeBoldText) {
        paint = super.getPaint();
        if (paint != null) {
            paint.setFakeBoldText(fakeBoldText);
        }
    }

}
