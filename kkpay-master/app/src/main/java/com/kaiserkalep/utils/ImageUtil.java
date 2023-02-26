package com.kaiserkalep.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.core.content.FileProvider;

import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.king.zxing.util.CodeUtils;
import com.kaiserkalep.MyApp;
import com.kaiserkalep.R;
import com.kaiserkalep.base.ZZActivity;
import com.kaiserkalep.widgets.glide.GlideUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 工藤
 * @email gougou@16fan.com
 * cc.shinichi.drawlongpicturedemo.util
 * create at 2018/8/28  10:46
 * description:
 */
public class ImageUtil {

    private static final String TAG = "ImageUtil";

    public static Bitmap getImageBitmap(String srcPath, float maxWidth, float maxHeight) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

        newOpts.inJustDecodeBounds = false;
        int originalWidth = newOpts.outWidth;
        int originalHeight = newOpts.outHeight;

        float be = 1;
        if (originalWidth > originalHeight && originalWidth > maxWidth) {
            be = originalWidth / maxWidth;
        } else if (originalWidth < originalHeight && originalHeight > maxHeight) {
            be = newOpts.outHeight / maxHeight;
        }
        if (be <= 0) {
            be = 1;
        }

        newOpts.inSampleSize = (int) be;
        newOpts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        newOpts.inDither = false;
        newOpts.inPurgeable = true;
        newOpts.inInputShareable = true;

        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }

        try {
            bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        } catch (OutOfMemoryError e) {
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
            Runtime.getRuntime().gc();
        } catch (Exception e) {
            Runtime.getRuntime().gc();
        }

        if (bitmap != null) {
            bitmap = rotateBitmapByDegree(bitmap, getBitmapDegree(srcPath));
        }
        return bitmap;
    }

    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation =
                    exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                default:
                    degree = 0;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }

    public static Bitmap resizeImage(Bitmap origin, int newWidth, int newHeight) {
        if (origin == null) {
            return null;
        }
        int height = origin.getHeight();
        int width = origin.getWidth();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (!origin.isRecycled()) {
            origin.recycle();
        }
        return newBM;
    }

    public static String saveBitmapBackPath(Bitmap bm) throws IOException {
        String path = Environment.getExternalStorageDirectory() + "/ShareLongPicture/.temp/";
        File targetDir = new File(path);
        if (!targetDir.exists()) {
            try {
                targetDir.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String fileName = "temp_LongPictureShare_" + System.currentTimeMillis() + ".jpeg";
        File savedFile = new File(path + fileName);
        if (!savedFile.exists()) {
            savedFile.createNewFile();
        }
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(savedFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        bos.flush();
        bos.close();
        return savedFile.getAbsolutePath();
    }

    public static int getOrientation(String imagePath) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(imagePath);
            int orientation =
                    exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
                default:
                    return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int[] getWidthHeight(String imagePath) {
        if (imagePath.isEmpty()) {
            return new int[]{0, 0};
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            Bitmap originBitmap = BitmapFactory.decodeFile(imagePath, options);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 使用第一种方式获取原始图片的宽高
        int srcWidth = options.outWidth;
        int srcHeight = options.outHeight;

        // 使用第二种方式获取原始图片的宽高
        if (srcHeight == -1 || srcWidth == -1) {
            try {
                ExifInterface exifInterface = new ExifInterface(imagePath);
                srcHeight =
                        exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, ExifInterface.ORIENTATION_NORMAL);
                srcWidth =
                        exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, ExifInterface.ORIENTATION_NORMAL);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 使用第三种方式获取原始图片的宽高
        if (srcWidth <= 0 || srcHeight <= 0) {
            Bitmap bitmap2 = BitmapFactory.decodeFile(imagePath);
            if (bitmap2 != null) {
                srcWidth = bitmap2.getWidth();
                srcHeight = bitmap2.getHeight();
                try {
                    if (!bitmap2.isRecycled()) {
                        bitmap2.recycle();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        int orient = getOrientation(imagePath);
        if (orient == 90 || orient == 270) {
            return new int[]{srcHeight, srcWidth};
        }
        return new int[]{srcWidth, srcHeight};
    }

    public static Bitmap getImageBitmap(String srcPath, int degree) {
        boolean isOOM = false;
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        newOpts.inJustDecodeBounds = false;
        float be = 1;
        newOpts.inSampleSize = (int) be;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        newOpts.inDither = false;
        newOpts.inPurgeable = true;
        newOpts.inInputShareable = true;
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        try {
            bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        } catch (OutOfMemoryError e) {
            isOOM = true;
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
            Runtime.getRuntime().gc();
        } catch (Exception e) {
            isOOM = true;
            Runtime.getRuntime().gc();
        }
        if (isOOM) {
            try {
                bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
            } catch (Exception e) {
                newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
                bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
            }
        }
        if (bitmap != null) {
            if (degree == 90) {
                degree += 180;
            }
            bitmap = rotateBitmapByDegree(bitmap, degree);
            int ttHeight = (1080 * bitmap.getHeight() / bitmap.getWidth());
            if (bitmap.getWidth() >= 1080) {
                bitmap = zoomBitmap(bitmap, 1080, ttHeight);
            }
        }
        return bitmap;
    }

    private static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    }

    public static String getImageTypeWithMime(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        String type = options.outMimeType;
        // ”image/png”、”image/jpeg”、”image/gif”
        if (TextUtils.isEmpty(type)) {
            type = "";
        } else {
            type = type.substring(6, type.length());
        }
        return type;
    }

    public static boolean isGifImageWithMime(String path) {
        return "gif".equalsIgnoreCase(getImageTypeWithMime(path));
    }

    public static boolean isBmpImageWithMime(String path) {
        return "bmp".equalsIgnoreCase(getImageTypeWithMime(path));
    }

    public static boolean isGifImageWithUrl(String url) {
        return url.toLowerCase().endsWith("gif");
    }

    /**
     * 根据给定的高进行裁剪,宽为屏幕宽,等比缩放后裁剪
     *
     * @param context
     * @param origin     原图
     * @param clipHeight 需要裁剪的高
     * @return new Bitmap
     */
    public static Bitmap scaleBitmap(Context context, Bitmap origin, int clipHeight) {
        if (origin == null) {
            return null;
        }
        int height = origin.getHeight();
        int width = origin.getWidth();
        int screenWidth = UIUtils.getScreenWidth(context);
        Matrix matrix = null;
        float imageHeight = -1;
        if (width != 0) {
            float scale = screenWidth * 1.0f / width * 1.0f;
            imageHeight = height * 1.0f * scale;
            float scaleWidth = ((float) screenWidth) / width;
            float scaleHeight =
                    (imageHeight > 0 && imageHeight < UIUtils.dip2px(context, clipHeight)) ?
                            (UIUtils.dip2px(context, clipHeight) * 1.0f / height * 1.0f)
                            : (imageHeight) / height * 1.0f;
            matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
        }
        Bitmap newBM = null;
        try {
            newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
            if (imageHeight > 0 && imageHeight < UIUtils.dip2px(context, clipHeight)) {//等比缩放,图片宽大于屏幕宽但是图片高小于所需裁剪高
                return newBM;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Bitmap newbm2 = null;
        try {
            if (newBM != null) {
                newbm2 = Bitmap.createBitmap(newBM, 0, 0, screenWidth, UIUtils.dip2px(context, clipHeight));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newbm2;
    }


    /**
     * 根据给定的高进行裁剪,等比缩放后裁剪
     *
     * @param origin     原图
     * @param clipWidth  需要裁剪的宽
     * @param clipHeight 需要裁剪的高
     * @return new Bitmap
     */
    public static Bitmap scaleBitmap(Bitmap origin, int clipWidth, int clipHeight) {
        if (origin == null) {
            return null;
        }
        int height = origin.getHeight();
        int width = origin.getWidth();
        Matrix matrix = null;
        float imageHeight = -1;
        if (width != 0) {
            float scale = clipWidth * 1.0f / width * 1.0f;
            imageHeight = height * 1.0f * scale;
            float scaleWidth = ((float) clipWidth) / width;
            float scaleHeight =
                    (imageHeight > 0 && imageHeight < clipHeight) ? (clipHeight * 1.0f / height * 1.0f)
                            : (imageHeight) / height * 1.0f;
            matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
        }
        Bitmap newBM = null;
        try {
            newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
            if (imageHeight > 0 && imageHeight < clipHeight) {//等比缩放,图片宽大于屏幕宽但是图片高小于所需裁剪高
                return newBM;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Bitmap newbm2 = null;
        try {
            if (newBM != null) {
                newbm2 = Bitmap.createBitmap(newBM, 0, 0, clipWidth, clipHeight);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newbm2;
    }

    /**
     * 根据给定的宽和高进行拉伸
     *
     * @param origin    原图
     * @param newWidth  新图的宽
     * @param newHeight 新图的高
     * @return new Bitmap
     */
    public static Bitmap scaleBitmap2(Bitmap origin, int newWidth, int newHeight) {
        if (origin == null) {
            return null;
        }
        int height = origin.getHeight();
        int width = origin.getWidth();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newBM = null;
        try {
            newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newBM != null ? newBM : origin;
    }


    /**
     * 保存Bitmap
     */
    private static void saveBitmap(Bitmap bm, String path, String url) {
        try {
            url = URLEncoder.encode(url, "utf-8");
            String cachePath = path + File.separator + url;
            if (CommonUtils.StringNotNull(cachePath)) {
                File file = new File(cachePath);
                if (file.exists()) {
                    file.delete();
                }
                FileOutputStream out = new FileOutputStream(file);
                bm.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.flush();
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取Bitmap
     *
     * @param url
     * @return
     */
    private static Bitmap getBitmap(String path, String url) {
        try {
            url = URLEncoder.encode(url, "utf-8");
            String cachePath = path + File.separator + url;
            if (CommonUtils.StringNotNull(cachePath)) {
                File file = new File(cachePath);
                if (file.exists()) {
                    return BitmapFactory.decodeStream(new FileInputStream(file));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 保存广告图片
     *
     * @param context
     * @param resourceFile
     * @param url
     */
    public static void saveAdGuideFile(Context context, File resourceFile, String url) {
        File dir = new File(context.getCacheDir(), context.getString(R.string.ZZImageAdCache));
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            url = URLEncoder.encode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        File targetFile = new File(dir.getPath() + File.separator + url);
        if (targetFile.exists()) {
            targetFile.delete();
        }
        try {
            targetFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileChannel resourceChannel = null;
        FileChannel targetChannel = null;
        try {
            resourceChannel = new FileInputStream(resourceFile).getChannel();
            targetChannel = new FileOutputStream(targetFile).getChannel();
            resourceChannel.transferTo(0, resourceChannel.size(), targetChannel);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            resourceChannel.close();
            targetChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取广告图片
     *
     * @param url
     * @return
     */
    public static File getAdGuideFile(Context context, String url) {
        try {
            url = URLEncoder.encode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String cachePath = new File(context.getCacheDir(),
                context.getString(R.string.ZZImageAdCache)).getPath() + File.separator + url;
        if (CommonUtils.StringNotNull(cachePath)) {
            File file = new File(cachePath);
            if (file.exists() && file.isFile()) {
                return file;
            }
        }
        return null;
    }


    /**
     * 保存图片
     *
     * @param url 二维码网络下载地址
     */
    public static void savePhoneByNetUrl(ZZActivity zzActivity, String url) {
        if (zzActivity == null) {
            return;
        }
        if (!CommonUtils.StringNotNull(url)) {
            return;
        }
        url = CommonUtils.getImgURL(url);
        String finalUrl = url;
        MyDataManager.setPermission(zzActivity, o -> {
            if (o != null && o) {
                zzActivity.showDialog();
                new Thread(() -> {
                    Bitmap load = GlideUtil.load(MyApp.getContext(), finalUrl);
                    if (load != null) {
                        try {
                            saveBitmap(zzActivity, load);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (zzActivity != null) {
                        zzActivity.runOnUiThread(() -> {
                            if (zzActivity != null) {
                                zzActivity.closeDialog();
                                zzActivity.toast("保存成功");
                            }
                        });
                    }
                }).start();
            }
        });
    }


    /**
     * 保存图片
     *
     * @param url 链接地址
     */
    public static void savePhoneByLinkUrl(ZZActivity zzActivity, String url) {
        if (zzActivity == null) {
            return;
        }
        if (!CommonUtils.StringNotNull(url)) {
            return;
        }
        url = CommonUtils.getImgURL(url);
        String finalUrl = url;
        MyDataManager.setPermission(zzActivity, o -> {
            if (o != null && o) {
                zzActivity.showDialog();
                new Thread(() -> {
                    Bitmap load = getQRBitmapByUrl(finalUrl);
                    if (load != null) {
                        try {
                            saveBitmap(zzActivity, load);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (zzActivity != null) {
                        zzActivity.runOnUiThread(() -> {
                            if (zzActivity != null) {
                                zzActivity.closeDialog();
                                zzActivity.toast("保存成功");
                            }
                        });
                    }
                }).start();
            }
        });
    }

    /**
     * url生成二维码
     *
     * @param url
     * @return
     */
    private static Bitmap getQRBitmapByUrl(String url) {
        Bitmap qrCode = null;
        if (CommonUtils.StringNotNull(url)) {
            //配置参数
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            //容错级别
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            //设置空白边距的宽度
            hints.put(EncodeHintType.MARGIN, 0); //default is 4
            qrCode = CodeUtils.createQRCode(url, UIUtils.dip2px(250), null, 0, hints);
        }
        return qrCode;
    }

    /**
     * 保存图片并刷新图库
     *
     * @param context
     * @param bitmap
     * @throws IOException
     */
    public static void saveBitmap(Context context, Bitmap bitmap) throws IOException {
        File eFile = Environment.getExternalStorageDirectory();
        File mDirectory = new File(eFile.toString() + File.separator + context.getPackageName());
        if (!mDirectory.exists()) {
            mDirectory.mkdirs();
        }
        File imageFile = new File(mDirectory, "photo_" + System.currentTimeMillis() + ".jpg");
        FileOutputStream out;
        try {
            out = new FileOutputStream(imageFile);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)) {
                out.flush();
                out.close();
            }
            updatePhotoAlbum(context, imageFile);//更新图库
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 兼容android 10
     * 更新图库
     *
     * @param mContext
     * @param file
     */
    public static void updatePhotoAlbum(Context mContext, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, file.getName());
            values.put(MediaStore.MediaColumns.MIME_TYPE, getMimeType(file));
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM);
            ContentResolver contentResolver = mContext.getContentResolver();
            Uri uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri == null) {
                return;
            }
            try {
                OutputStream out = contentResolver.openOutputStream(uri);
                FileInputStream fis = new FileInputStream(file);
                assert out != null;
                android.os.FileUtils.copy(fis, out);
                fis.close();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            MediaScannerConnection.scanFile(mContext.getApplicationContext(), new String[]{file.getAbsolutePath()},
                    new String[]{"image/jpeg"}, null);
        }
    }

    public static String getMimeType(File file) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        return fileNameMap.getContentTypeFor(file.getName());
    }


    public static Uri toUri(Context context, String filePath) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context, context.getApplicationInfo().packageName + ".fileprovider", new File(filePath));
        }
        return Uri.fromFile(new File(filePath));
    }
}