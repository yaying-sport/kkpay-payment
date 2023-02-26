package com.kaiserkalep.utils;

import android.graphics.drawable.Drawable;

import java.util.HashMap;

/**
 * @Auther: Jack
 * @Date: 2020/5/6 20:58
 * @Description:
 */
public class TransformationUtils {

    private static HashMap<String, Integer[]> map = new HashMap<>();
    private static HashMap<String, Drawable> mapDrawable = new HashMap<>();


    public static Integer[] getSizeByUrl(String url) {
        if (CommonUtils.StringNotNull(url) && map.containsKey(url)) {
            return map.get(url);
        }
        return null;
    }

    public static void putSize(String url, int mBitmapWidth, int mBitmapHeight) {
        if (map.containsKey(url)) {
            return;
        }
        map.put(url, new Integer[]{mBitmapWidth, mBitmapHeight});
    }


    public static Drawable getDrawableByUrl(String url) {
        if (CommonUtils.StringNotNull(url) && mapDrawable.containsKey(url)) {
            return mapDrawable.get(url);
        }
        return null;
    }

    public static void putDrawable(String url, Drawable drawable) {
        mapDrawable.put(url, drawable);
    }
}
