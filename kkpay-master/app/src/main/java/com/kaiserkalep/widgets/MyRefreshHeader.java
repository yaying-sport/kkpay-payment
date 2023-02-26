package com.kaiserkalep.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshInternal;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.internal.InternalAbstract;
import com.kaiserkalep.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @Auther: Jack
 * @Date: 2020/12/16 11:09
 * @Description:
 */
public class MyRefreshHeader extends InternalAbstract implements RefreshHeader {


    protected MyRefreshHeader(@NonNull View wrapped) {
        super(wrapped);
    }

    protected MyRefreshHeader(@NonNull View wrappedView, @Nullable RefreshInternal wrappedInternal) {
        super(wrappedView, wrappedInternal);
    }

    public MyRefreshHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.view_refresh_heard, this);
    }

    @NonNull
    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;
    }
}
