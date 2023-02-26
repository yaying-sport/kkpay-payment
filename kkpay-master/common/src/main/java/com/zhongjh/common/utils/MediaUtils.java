package com.zhongjh.common.utils;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import androidx.exifinterface.media.ExifInterface;

import com.zhongjh.common.entity.MediaExtraInfo;

import java.io.IOException;
import java.io.InputStream;

/**
 * 多媒体的工具类，获取宽高、视频音频长度等
 *
 * @author zhongjh
 * @date 2022/2/8
 */
public class MediaUtils {

    /**
     * 90度
     */
    private static final String ORIENTATION_ROTATE_90 = "90";
    /**
     * 270度
     */
    private static final String ORIENTATION_ROTATE_270 = "270";

    /**
     * 获取图片的宽高
     *
     * @param context 上下文
     * @param path    path
     * @return 根据图片path获取相关参数
     */
    public static MediaExtraInfo getImageSize(Context context, String path) {
        MediaExtraInfo mediaExtraInfo = new MediaExtraInfo();
        ExifInterface exifInterface;
        InputStream inputStream = null;
        try {
            if (MimeTypeUtils.isContent(path)) {
                inputStream = context.getContentResolver().openInputStream(Uri.parse(path));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    exifInterface = new ExifInterface(inputStream);
                } else {
                    exifInterface = new ExifInterface(path);
                }
            } else {
                exifInterface = new ExifInterface(path);
            }
            mediaExtraInfo.setWidth(exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, ExifInterface.ORIENTATION_NORMAL));
            mediaExtraInfo.setHeight(exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, ExifInterface.ORIENTATION_NORMAL));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mediaExtraInfo;
    }

    /**
     * 获取视频的宽高、时长
     *
     * @param context 上下文
     * @param path    path
     * @return 根据视频path获取相关参数
     */
    public static MediaExtraInfo getVideoSize(Context context, String path) {
        MediaExtraInfo mediaExtraInfo = new MediaExtraInfo();
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            if (MimeTypeUtils.isContent(path)) {
                retriever.setDataSource(context, Uri.parse(path));
            } else {
                retriever.setDataSource(path);
            }
            String orientation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
            int width, height;
            if (TextUtils.equals(ORIENTATION_ROTATE_90, orientation) || TextUtils.equals(ORIENTATION_ROTATE_270, orientation)) {
                height = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
                width = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            } else {
                width = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
                height = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            }
            mediaExtraInfo.setWidth(width);
            mediaExtraInfo.setHeight(height);
            mediaExtraInfo.setDuration(Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }
        return mediaExtraInfo;
    }

    /**
     * 获取音频的宽高、时长
     *
     * @param context 上下文
     * @param path    path
     * @return 根据音频path获取相关参数
     */
    public static MediaExtraInfo getAudioSize(Context context, String path) {
        MediaExtraInfo mediaExtraInfo = new MediaExtraInfo();
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            if (MimeTypeUtils.isContent(path)) {
                retriever.setDataSource(context, Uri.parse(path));
            } else {
                retriever.setDataSource(path);
            }
            mediaExtraInfo.setDuration(Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }
        return mediaExtraInfo;
    }


    /**
     * 获取图片的宽和高度
     *
     * @param pathName 图片文件地址
     * @return 宽高合成的数组
     */
    public static int[] getImageWidthAndHeight(String pathName) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        // 只请求图片宽高，不解析图片像素(请求图片属性但不申请内存，解析bitmap对象，该对象不占内存)
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, opts);
        return new int[]{opts.outWidth, opts.outHeight};
    }

}
