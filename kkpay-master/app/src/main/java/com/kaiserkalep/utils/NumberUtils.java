package com.kaiserkalep.utils;

import java.util.regex.Pattern;

/**
 * String转number
 */
public class NumberUtils {


    /**
     * String转long
     *
     * @param s
     * @return
     */
    public static long stringToLong(String s) {
        long l = 0;
        if (CommonUtils.StringNotNull(s)) {
            try {
                l = Long.parseLong(s);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return l;
    }


    /**
     * String转int
     *
     * @param s
     * @return
     */
    public static int stringToInt(String s) {
        int i = 0;
        if (CommonUtils.StringNotNull(s)) {
            try {
                i = Integer.parseInt(s);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return i;
    }


    /**
     * String转double
     *
     * @param s
     * @return
     */
    public static double stringToDouble(String s) {
        double d = 0;
        if (CommonUtils.StringNotNull(s)) {
            try {
                d = Double.parseDouble(s);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return d;
    }


    public static boolean isNumeric(String str) {
        boolean isNum = false;
        if (CommonUtils.StringNotNull(str)) {
            try {
                Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
                isNum = pattern.matcher(str).matches();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return isNum;
    }

    /**
     * 强转int
     *
     * @param f
     * @return
     */
    public static int CreatureSwap(float f) {
        int i = 0;
        try {
            i = (int) f;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i;
    }
}
