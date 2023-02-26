package com.kaiserkalep.utils;

import android.content.Context;

import java.io.File;

/**
 * 字体获取
 *
 * @Auther: Jack
 * @Date: 2020/5/27 14:02
 * @Description:
 */
public class FontUtils {


    /**
     * 方正汉真广标简体(播放页水印使用 {@link WatermarkView})
     *
     * @param context
     * @return
     */
    public static File getFZHZ(Context context) {
        return getFontByFile(context, "fonts/FZHZ.ttf");
    }


    /**
     * 方正正纤黑简体(启动页及关于我们appName使用)
     *
     * @param context
     * @return
     * @deprecated
     */
    public static File getFZZQ(Context context) {
        return getFontByFile(context, "fonts/FZZQ.ttf");
    }


    /**
     * 根据字体名获取字体
     *
     * @param context
     * @param fontName
     * @return
     */
    private static File getFontByFile(Context context, String fontName) {
        try {
            String path = context.getFilesDir().getPath();
            File font = new File(path, fontName);
            if (!font.exists()) {
                String zip_dir = "fonts/fonts.zip";
                ZipFilesUtils.UnZipAssetsFolder(context, zip_dir, path);
            }
            if (font.exists() && font.isFile()) {
                return font;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
