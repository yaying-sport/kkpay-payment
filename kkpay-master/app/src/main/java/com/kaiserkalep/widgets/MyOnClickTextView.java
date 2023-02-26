package com.kaiserkalep.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.kaiserkalep.utils.CommonUtils;
import java.util.ArrayList;

import androidx.annotation.Nullable;

/**
 * 点击多回调
 *
 * @Auther: Jack
 * @Date: 2019/10/14 14:51
 * @Description:
 */
@SuppressLint("AppCompatCustomView")
public class MyOnClickTextView extends TextView implements View.OnClickListener {

    public MyOnClickTextView(Context context) {
        super(context);
    }

    public MyOnClickTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyOnClickTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    ArrayList<OnClickListener> listeners = new ArrayList<>();


    public void addOnClickListener(@Nullable OnClickListener listener) {
        super.setOnClickListener(this);
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
        if (listener != null) {
            listeners.add(listener);
        }
    }

    @Override
    public void onClick(View v) {
        if (CommonUtils.ListNotNull(listeners)) {
            for (OnClickListener listener : listeners) {
                if (listener != null) {
                    listener.onClick(v);
                }
            }
        }
    }
}
