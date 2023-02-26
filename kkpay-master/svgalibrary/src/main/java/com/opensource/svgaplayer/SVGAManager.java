package com.opensource.svgaplayer;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * svga动画加载,自己维护缓存
 *
 * @Auther: Jack
 * @Date: 2021/5/17 20:18
 * @Description:
 */
public class SVGAManager {

    private SVGAManager() {
    }

    public static SVGAManager getManager() {
        return SVGAManager.MyDialogManagerHolder.INSTANCE;
    }

    private static class MyDialogManagerHolder {
        private static final SVGAManager INSTANCE = new SVGAManager();
    }

    private HashMap<String, SVGAVideoEntity> svgaMap = new HashMap<>();
    private final String LOADING_SVG_PATH = "s/loading.svga";

    public void release() {
        if (svgaMap != null && svgaMap.size() > 0) {
            svgaMap.clear();
        }
    }

    private HashMap<String, SVGAVideoEntity> getSvgaMap() {
        if (svgaMap == null) {
            svgaMap = new HashMap<>();
        }
        return svgaMap;
    }


    /**
     * 获取加载中动画
     *
     * @param listener
     */
    public void getLoadingEntity(final LoadingSVGAListener listener) {
        getSVGAVideoEntity(LOADING_SVG_PATH, listener);
    }


    /**
     * 获取asset动画文件
     *
     * @param url
     * @param listener
     */
    public void getSVGAVideoEntity(final String url, final LoadingSVGAListener listener) {
        SVGAVideoEntity svgaVideoEntity = getSvgaMap().get(url);
        if (svgaVideoEntity != null) {
            if (listener != null) {
                listener.onCallBack(svgaVideoEntity);
            }
        } else {
            SVGAParser svgaParser = SVGAParser.Companion.shareParser();
            try {
                svgaParser.decodeFromAssets(url, new SVGAParser.ParseCompletion() {
                    @Override
                    public void onComplete(@NotNull SVGAVideoEntity videoItem) {
                        getSvgaMap().put(url, videoItem);
                        if (listener != null) {
                            listener.onCallBack(videoItem);
                        }
                    }

                    @Override
                    public void onError() {
                        if (listener != null) {
                            listener.onCallBack(null);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                if (listener != null) {
                    listener.onCallBack(null);
                }
            }
        }
    }

    public interface LoadingSVGAListener {
        void onCallBack(SVGAVideoEntity o);
    }
}
