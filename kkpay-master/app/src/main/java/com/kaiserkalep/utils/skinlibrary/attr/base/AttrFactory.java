package com.kaiserkalep.utils.skinlibrary.attr.base;

import com.kaiserkalep.utils.skinlibrary.attr.BackgroundAttr;
import com.kaiserkalep.utils.skinlibrary.attr.EditTextColorHint;
import com.kaiserkalep.utils.skinlibrary.attr.ImageViewSrcAttr;
import com.kaiserkalep.utils.skinlibrary.attr.SeekBarAttr;
import com.kaiserkalep.utils.skinlibrary.attr.TextColorAttr;
import com.kaiserkalep.utils.skinlibrary.attr.TextViewDrawableLeft;
import com.kaiserkalep.utils.skinlibrary.attr.TextViewDrawableRight;

import java.util.HashMap;


/**
 * Created by _SOLID
 * Date:2016/4/14
 * Time:9:47
 */
public class AttrFactory {

    private static HashMap<String, SkinAttr> sSupportAttr = new HashMap<>();
    public static String background = "background";
    public static String textColor = "textColor";
    public static String src = "src";
    public static String progressDrawable = "progressDrawable";
    public static String textColorHint = "textColorHint";
    public static String drawableLeft = "drawableLeft";
    public static String drawableRight = "drawableRight";

    static {
        sSupportAttr.put(background, new BackgroundAttr());
        sSupportAttr.put(textColor, new TextColorAttr());
        sSupportAttr.put(src, new ImageViewSrcAttr());
        sSupportAttr.put(progressDrawable, new SeekBarAttr());
        sSupportAttr.put(textColorHint, new EditTextColorHint());
        sSupportAttr.put(drawableLeft, new TextViewDrawableLeft());
        sSupportAttr.put(drawableRight, new TextViewDrawableRight());
    }


    public static SkinAttr get(String attrName, int attrValueRefId, String attrValueRefName, String typeName) {
        SkinAttr mSkinAttr = sSupportAttr.get(attrName).clone();
        if (mSkinAttr == null) return null;
        mSkinAttr.attrName = attrName;
        mSkinAttr.attrValueRefId = attrValueRefId;
        mSkinAttr.attrValueRefName = attrValueRefName;
        mSkinAttr.attrValueTypeName = typeName;
        return mSkinAttr;
    }

    /**
     * check current attribute if can be support
     *
     * @param attrName attribute name
     * @return true : supported <br>
     * false: not supported
     */
    public static boolean isSupportedAttr(String attrName) {
        return sSupportAttr.containsKey(attrName);
    }

    /**
     * add support's attribute
     *
     * @param attrName attribute name
     * @param skinAttr skin attribute
     */
    public static void addSupportAttr(String attrName, SkinAttr skinAttr) {
        sSupportAttr.put(attrName, skinAttr);
    }
}
