package com.kaiserkalep.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;

import androidx.annotation.NonNull;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;

/**
 * 密码输入框.数字+大小写英文字母,当 android:inputType="text"时,输入类型为可见
 *
 * @Auther: Jack
 * @Date: 2020/12/15 15:42
 * @Description:
 */
public class PasswordEditTextView extends CleanEditTextView {
    public PasswordEditTextView(Context context) {
        super(context);
        init(context);
    }

    public PasswordEditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PasswordEditTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        setTypeface(Typeface.DEFAULT);
        setKeyListener(new DigitsKeyListener() {
            @Override
            public int getInputType() {
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL;
            }

            @NonNull
            @Override
            protected char[] getAcceptedChars() {
                return MyApp.getLanguageString(context, R.string.input_type_digits).toCharArray();
            }
        });
    }
}
