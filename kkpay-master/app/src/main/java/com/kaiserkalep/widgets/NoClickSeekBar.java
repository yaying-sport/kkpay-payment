package com.kaiserkalep.widgets;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

import androidx.appcompat.widget.AppCompatSeekBar;

/**
 * @Auther: Jack
 * @Date: 2020/12/21 18:45
 * @Description:
 */
public class NoClickSeekBar extends AppCompatSeekBar implements SeekBar.OnSeekBarChangeListener {

    public interface ValidateSeekBarCallBack {
        void onProgressChangedCallBack(SeekBar seekBar, int progress, boolean fromUser);

        void onStartTrackingTouchCallBack(SeekBar seekbar);

        void onStopTrackingTouchCallBack(SeekBar seekbar);
    }

    private ValidateSeekBarCallBack callback;

    public void setProgressChangedCallback(ValidateSeekBarCallBack callback) {
        this.callback = callback;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!isTouchInThumb(event, getThumb().getBounds())) {
                return false;
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * 判断MotionEvent事件是否位于thumb上
     *
     * @param event
     * @param thumbBounds
     * @return
     */
    private boolean isTouchInThumb(MotionEvent event, Rect thumbBounds) {
        float x = event.getX();
        float y = event.getY();
        //根据偏移量和左边距确定thumb位置
        int left = thumbBounds.left - getThumbOffset() + getPaddingLeft();
        int right = left + thumbBounds.width();
        if (x >= left && x <= right
                && y >= thumbBounds.top && y <= thumbBounds.bottom)
            return true;
        return false;
    }


    public NoClickSeekBar(Context context) {
        super(context);
        setOnSeekBarChangeListener(this);
    }

    public NoClickSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnSeekBarChangeListener(this);
    }

    public NoClickSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnSeekBarChangeListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        seekBar.setProgress(progress);
        if (this.callback != null) {
            this.callback.onProgressChangedCallBack(seekBar, progress, fromUser);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (this.callback != null) {
            this.callback.onStartTrackingTouchCallBack(seekBar);
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (this.callback != null) {
            this.callback.onStopTrackingTouchCallBack(seekBar);
        }
    }
}