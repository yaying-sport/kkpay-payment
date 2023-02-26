package com.kaiserkalep.utils.skinlibrary.attr;

import android.view.View;
import android.widget.EditText;

import com.kaiserkalep.utils.skinlibrary.attr.base.SkinAttr;
import com.kaiserkalep.utils.skinlibrary.utils.SkinResourcesUtils;

/**
 * @Auther: Jack
 * @Date: 2020/4/30 09:07
 * @Description:
 */
public class EditTextColorHint extends SkinAttr {

    @Override
    protected void applySkin(View view) {
        if (view instanceof EditText) {
            EditText v = (EditText) view;
            if (isColor()) {
                v.setHintTextColor(SkinResourcesUtils.getColor(attrValueRefId));
            }
        }
    }
}
