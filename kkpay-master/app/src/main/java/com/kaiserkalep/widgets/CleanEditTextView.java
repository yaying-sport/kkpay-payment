package com.kaiserkalep.widgets;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.widget.AppCompatEditText;

import com.kaiserkalep.R;
import com.kaiserkalep.utils.CommonUtils;
import com.kaiserkalep.utils.UIUtils;


/**
 * Created on 2017/4/24 16:31
 */
public class CleanEditTextView extends AppCompatEditText {

    private Drawable guanbiDrawable;
    private int padding;

    public CleanEditTextView(Context context) {
        super(context);
        init(context);
    }

    public CleanEditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CleanEditTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        guanbiDrawable = context.getResources().getDrawable(R.drawable.icon_login_clear);
        padding = UIUtils.dip2px(10);

        addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setDrawable(!setEditText);
                setEditText = false;
            }
        });

        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (focusChangeListener != null) {
                    focusChangeListener.onFocusChange(v, hasFocus);
                }
                if (hasFocus) {
                    setDrawable(true);
                } else {
                    isFocus = false;
                    setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                }
            }
        });
        setDrawable(false);
    }


    private MyFocusChangeListener focusChangeListener;

    public void setMyFocusChangeListener(MyFocusChangeListener listener) {
        this.focusChangeListener = listener;
    }

    public interface MyFocusChangeListener {
        void onFocusChange(View v, boolean hasFocus);
    }

    //设置删除图片
    private void setDrawable(boolean isFocus) {
        this.isFocus = isFocus;
        if (length() > 0) {
            setCompoundDrawablesWithIntrinsicBounds(null, null, guanbiDrawable, null);
            setCompoundDrawablePadding(padding);
        } else {
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
    }

    int Dx = 0;
    int Dy = 0;
    Rect rect1 = new Rect();
    boolean isFocus;
    boolean setEditText;

    // 处理删除事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Dx = (int) event.getRawX();
            Dy = (int) event.getRawY();
            getGlobalVisibleRect(rect1);
            rect1.left = rect1.right - 40 - guanbiDrawable.getIntrinsicWidth();
        }

        if (guanbiDrawable != null && event.getAction() == MotionEvent.ACTION_UP) {
            int eventX = (int) event.getRawX();
            int eventY = (int) event.getRawY();
            Rect rect = new Rect();
            getGlobalVisibleRect(rect);

            rect.left = rect.right - 40 - guanbiDrawable.getIntrinsicWidth();

            if (rect.contains(eventX, eventY) && rect1.contains(Dx, Dy) && isFocus) {
                Editable text = getText();
                if (null != text && clickCleanListener != null && CommonUtils.StringNotNull(text.toString().trim())) {
                    clickCleanListener.onClickClose();
                }
                setText("");
            }
        }
        return super.onTouchEvent(event);
    }

    private onClickCleanListener clickCleanListener;


    public interface onClickCleanListener {
        void onClickClose();
    }

    public void setClickCleanListener(onClickCleanListener clickCleanListener) {
        this.clickCleanListener = clickCleanListener;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    /**
     * 获取输入文本
     *
     * @return
     */
    private String getEditText() {
        Editable text = getText();
        if (null != text) {
            return text.toString().trim();
        }
        return "";
    }

    /**
     * 设置输入文本
     *
     * @param text
     */
    public void setEditText(CharSequence text) {
        setEditText = true;
        setText(text);
        setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    }

    /**
     * 设置输入文本
     *
     * @param text
     */
    public void setEditText(CharSequence text, boolean isVisible) {
        setText(text);
        if (isVisible) {
            setCompoundDrawablesWithIntrinsicBounds(null, null, guanbiDrawable, null);
        } else {
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
    }

    /**
     * 设置图片和textview间距
     *
     * @param padding
     */
    public void setPadding(int padding) {
        this.padding = padding;
        setCompoundDrawablePadding(padding);
    }

    public void setCleanDrawable(Drawable cleanDrawable) {
        this.guanbiDrawable = cleanDrawable;
    }
}
