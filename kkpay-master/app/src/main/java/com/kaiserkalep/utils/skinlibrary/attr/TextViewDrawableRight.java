package com.kaiserkalep.utils.skinlibrary.attr;

import android.view.View;
import android.widget.TextView;

import com.kaiserkalep.utils.UIUtils;
import com.kaiserkalep.utils.skinlibrary.attr.base.SkinAttr;

/**
 * @Auther: Jack
 * @Date: 2020/5/4 14:49
 * @Description:
 */
public class TextViewDrawableRight extends SkinAttr {

    @Override
    protected void applySkin(View view) {
        if (view instanceof TextView) {
            TextView v = (TextView) view;
            if (isDrawable()) {
                UIUtils.setRightDrawable(view.getContext(), v, attrValueRefId);
            }
        }
    }
}
