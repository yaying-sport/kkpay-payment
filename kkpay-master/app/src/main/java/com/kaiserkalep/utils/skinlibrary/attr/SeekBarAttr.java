package com.kaiserkalep.utils.skinlibrary.attr;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import com.kaiserkalep.utils.skinlibrary.attr.base.SkinAttr;
import com.kaiserkalep.utils.skinlibrary.utils.SkinResourcesUtils;

/**
 * @Auther: Jack
 * @Date: 2020/4/29 16:07
 * @Description:
 */
public class SeekBarAttr extends SkinAttr {

    @Override
    protected void applySkin(View view) {
        if (view instanceof SeekBar) {
            SeekBar v = (SeekBar) view;
            if (isDrawable()) {
                v.setProgressDrawable(SkinResourcesUtils.getDrawable(attrValueRefId));
            }
        } else if (view instanceof ProgressBar) {
            ProgressBar v = (ProgressBar) view;
            if (isDrawable()) {
                v.setProgressDrawable(SkinResourcesUtils.getDrawable(attrValueRefId));
            }
        }
    }
}
