package com.kaiserkalep.utils;

import android.util.Log;

import com.kaiserkalep.BuildConfig;

import java.util.Arrays;


/**
 * @Auther: Administrator
 * @Date: 2019/3/14 0014 20:59
 * @Description:
 */
public class LogUtils {
    public static boolean IS_DEBUG = BuildConfig.DEBUG;

    /**
     * Priority constant for the println method; use Log.v.
     */
    public static final int VERBOSE = 2;
    /**
     * Priority constant for the println method; use Log.d.
     */
    public static final int DEBUG = 3;
    /**
     * Priority constant for the println method; use Log.i.
     */
    public static final int INFO = 4;
    /**
     * Priority constant for the println method; use Log.w.
     */
    public static final int WARN = 5;
    /**
     * Priority constant for the println method; use Log.e.
     */
    public static final int ERROR = 6;
    static final String TAG = "debug";

    public static void setDebugMode(boolean isDebug) {
        IS_DEBUG = isDebug;
    }

    public static void v(Object... msg) {
        print(VERBOSE, msg);
    }

    public static void d(Object... msg) {
        print(DEBUG, msg);
    }

    public static void i(Object... msg) {
        print(INFO, msg);
    }

    public static void w(Object... msg) {
        print(WARN, msg);
    }

    public static void e(Object... msg) {
        print(ERROR, msg);
    }

    private static void print(int type, Object... msg) {
        if (!IS_DEBUG) {
            return;
        }

        StringBuilder str = new StringBuilder();

        if (msg != null) {
            for (Object obj : msg) {
                str.append("★").append(obj);
            }
            if (str.length() > 0) {
                str.deleteCharAt(0);// 移除第一个五角星
            }
        } else {
            str.append("null");
        }
        try {
            StackTraceElement[] sts = Thread.currentThread().getStackTrace();
            StackTraceElement st = null;
            String tag = null;
            if (sts != null && sts.length > 4) {
                st = sts[4];
                if (st != null) {
                    String fileName = st.getFileName();
                    tag = (fileName == null) ? "Unkown" : fileName.replace(".java", "");
                    str.insert(0, "【" + tag + "." + st.getMethodName() + "() line " + st.getLineNumber() + "】\n>>>[")
                            .append("]");
                }
            }

            // use logcat log
            while (str.length() > 0) {
                switch (type) {
                    case VERBOSE:
                        Log.v(tag == null ? TAG : (TAG + "_" + tag), str.substring(0, Math.min(2000, str.length())));
                        break;
                    case DEBUG:
                        Log.d(tag == null ? TAG : (TAG + "_" + tag), str.substring(0, Math.min(2000, str.length())));
                        break;
                    case INFO:
                        Log.i(tag == null ? TAG : (TAG + "_" + tag), str.substring(0, Math.min(2000, str.length())));
                        break;
                    case WARN:
                        Log.w(tag == null ? TAG : (TAG + "_" + tag), str.substring(0, Math.min(2000, str.length())));
                        break;
                    case ERROR:
                        Log.e(tag == null ? TAG : (TAG + "_" + tag), str.substring(0, Math.min(2000, str.length())));
                        break;
                    default:
                        break;
                }
                str.delete(0, 2000);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void printStackTrace() {
        if (!IS_DEBUG) {
            return;
        }
        try {
            StackTraceElement[] sts = Thread.currentThread().getStackTrace();
            for (StackTraceElement stackTraceElement : sts) {
                Log.e("Log_trace", stackTraceElement.toString());
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * 打印日志到控制台（解决Android控制台丢失长日志记录）
     *
     * @param priority
     * @param tag
     * @param content
     */
    public static void print(int priority, String tag, String content) {
        // 1. 测试控制台最多打印4062个字节，不同情况稍有出入（注意：这里是字节，不是字符！！）
        // 2. 字符串默认字符集编码是utf-8，它是变长编码一个字符用1~4个字节表示
        // 3. 这里字符长度小于1000，即字节长度小于4000，则直接打印，避免执行后续流程，提高性能哈
        if (content.length() < 1000) {
            Log.println(priority, tag, content);
            return;
        }
        // 一次打印的最大字节数
        int maxByteNum = 4000;
        // 字符串转字节数组
        byte[] bytes = content.getBytes();
        // 超出范围直接打印
        if (maxByteNum >= bytes.length) {
            Log.println(priority, tag, content);
            return;
        }
        // 分段打印计数
        int count = 1;
        // 在数组范围内，则循环分段
        while (maxByteNum < bytes.length) {
            // 按字节长度截取字符串
            String subStr = cutStr(bytes, maxByteNum);
            // 打印日志
            String desc = String.format("分段打印(%s):%s", count++, subStr);
            Log.println(priority, tag, desc);
            // 截取出尚未打印字节数组
            bytes = Arrays.copyOfRange(bytes, subStr.getBytes().length, bytes.length);
        }
        // 打印剩余部分
        Log.println(priority, tag, String.format("分段打印(%s):%s", count, new String(bytes)));
    }


    /**
     * 按字节长度截取字节数组为字符串
     *
     * @param bytes
     * @param subLength
     * @return
     */
    private static String cutStr(byte[] bytes, int subLength) {
        // 边界判断
        if (bytes == null || subLength < 1) {
            return null;
        }
        // 超出范围直接返回
        if (subLength >= bytes.length) {
            return new String(bytes);
        }
        // 复制出定长字节数组，转为字符串
        String subStr = new String(Arrays.copyOf(bytes, subLength));
        // 避免末尾字符是被拆分的，这里减1使字符串保持完整
        return subStr.substring(0, subStr.length() - 1);
    }

}

