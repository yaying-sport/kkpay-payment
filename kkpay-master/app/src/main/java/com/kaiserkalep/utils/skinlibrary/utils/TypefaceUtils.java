package com.kaiserkalep.utils.skinlibrary.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;

import com.kaiserkalep.MyApp;
import com.kaiserkalep.utils.skinlibrary.SkinConfig;

import java.io.File;
import java.util.HashMap;


/**
 * Created by _SOLID
 * Date:2016/7/13
 * Time:10:50
 */
public class TypefaceUtils {

    public static Typeface CURRENT_TYPEFACE;
    private static HashMap<String, Typeface> faceMap = new HashMap<>();

    public static Typeface createTypeface(Context context, String fontName) {
        Typeface tf;
        if (!TextUtils.isEmpty(fontName)) {
            tf = Typeface.createFromAsset(context.getAssets(), getFontPath(fontName));
            SkinPreferencesUtils.putString(context, SkinConfig.PREF_FONT_PATH, getFontPath(fontName));
        } else {
            tf = Typeface.DEFAULT;
            SkinPreferencesUtils.putString(context, SkinConfig.PREF_FONT_PATH, "");
        }
        TypefaceUtils.CURRENT_TYPEFACE = tf;
        return tf;
    }

    public static Typeface getTypeface(Context context) {
        String fontPath = SkinPreferencesUtils.getString(context, SkinConfig.PREF_FONT_PATH);
        Typeface tf;
        if (!TextUtils.isEmpty(fontPath)) {
            tf = Typeface.createFromAsset(context.getAssets(), fontPath);
        } else {
            tf = Typeface.DEFAULT;
            SkinPreferencesUtils.putString(context, SkinConfig.PREF_FONT_PATH, "");
        }
        return tf;
    }

    private static String getFontPath(String fontName) {
        return SkinConfig.FONT_DIR_NAME + File.separator + fontName;
    }

    public static Typeface getMoneyTypeface(){
        return getTypeface("fonts/a.ttf");
    }

    public static Typeface getBetNumberTypeface(){
        return getTypeface("fonts/ab.ttf");
    }

    public static Typeface getTXTypeface(){
        return getTypeface("fonts/tx.ttf");
    }

    public static Typeface getDCBTypeface(){
        return getTypeface("fonts/dcb.ttf");
    }

    private static Typeface getTypeface(String name){
        if (faceMap.containsKey(name)) {
            return faceMap.get(name);
        }
        Typeface fromAsset = Typeface.createFromAsset(MyApp.getContext().getAssets(), name);
        faceMap.put(name, fromAsset);
        return fromAsset;
    }
}
