package com.kaiserkalep.utils;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MoneyUtils {

    /**
     * @param str
     * @param rule
     * @return
     */
    public static String mDecimalFormat(String str, String rule) {
        BigDecimal b = new BigDecimal(str);
        DecimalFormat d = new DecimalFormat(rule);
        return d.format(b);
    }

    /**
     * @param rule
     * @return
     */
    public static String mDecimalFormat(long val, String rule) {
        DecimalFormat d = new DecimalFormat(rule);
        return d.format(val);
    }

    /**
     * @param rule
     * @return
     */
    public static String mDecimalFormat(double val, String rule) {
        DecimalFormat d = new DecimalFormat(rule);
        return d.format(val);
    }


    public static String getIntMoneyText(String money) {
        if (money == null) {
            return "";
        }
        String s = money;
        if (s.indexOf(".") > 0) {
            // 正则表达
            s = s.replaceAll("0+?$", "");// 去掉后面无用的零;
            s = s.replaceAll("[.]$", "");// 如小数点后面全是零则去
        }
        return s;
    }

    public static String getIntMoneyText(double money) {
        return getIntMoneyText(CommonUtils.rahToStr(money) + "");
    }

    public static String addComma(String str) {
        return mDecimalFormat(str, "#,##0.####;(#)");
    }

    /**
     * @param tempValStr
     * @param rule
     * @return
     */
    public static String formatRahToStr(String tempValStr, String rule) {
        double value = 0;
        String valStr = tempValStr;
        try {
            value = Double.valueOf(tempValStr);
            if (value == 0) {
                valStr = "0.00";
            } else {
                valStr = mDecimalFormat(value, rule);
            }
        } catch (Exception e) {
            valStr = "0.00";
        }
        return valStr;
    }

    public static String removeZero(String money) {
        if (money == null) {
            return "";
        }
        String s = money;
        if (s.indexOf(".") > 0) {
            // 正则表达
            s = s.replaceAll("0+?$", "");// 去掉后面无用的零;
            s = s.replaceAll("[.]$", "");// 如小数点后面全是零则去
        }
        return s;
    }

    public static String rahToStr(double val) {
        String tempValStr = CommonUtils.rahToStr(val);
        return formatRahToStr(tempValStr, ",##0.00");
    }

    public static String rahToStrNo(double val) {
        String tempValStr = CommonUtils.rahToStr(val);
        return formatRahToStr(tempValStr, "0.00");
    }

    //千位符和不保留小数
    public static String rahToIntStr(double val) {
        String tempValStr = CommonUtils.rahToStr(val);
        return formatRahToStr(tempValStr, ",##0.##");
    }

    //千位符和不保留小数
    public static String rahToIntStrNodot(double val) {
        String tempValStr = CommonUtils.rahToStr(val);
        return formatRahToStr(tempValStr, ",##0");
    }

    //返回整数金额并不带千分位
    public static String rahToIntStrWithouthousands(double val) {
        String tempValStr = CommonUtils.rahToStr(val);
        return formatRahToStr(tempValStr, "#0");
    }

    //重载   对String进行2位小数和千位符
    public static String rahToStr(String val) {
        return formatRahToStr(val, ",##0.00");
    }

    public static String rahToStrNo(String val) {
        return formatRahToStr(val, "0.00");
    }

    /**
     * 转换万元单位，保留2位小数
     *
     * @param val
     * @return
     */
    public static String rahToStr_Wan(double val) {
        String tempValStr = CommonUtils.rahToStr(val);
        double value = 0;
        String valStr = tempValStr;
        try {
            value = Double.valueOf(tempValStr);
            if (value == 0) {
                valStr = "0.00";
            } else {
                DecimalFormat decimalFormat = new DecimalFormat(",##0.00");
                if (tempValStr.length() > 8) {
                    valStr = decimalFormat.format(value * 0.0001) + "万";
                } else {
                    valStr = decimalFormat.format(value);
                }

            }

        } catch (Exception e) {
            valStr = "0.00";
        }
        return valStr;
    }

    /**
     * 最多保留2位小数，舍0
     *
     * @param val
     * @return
     */
    public static String rahToStrNtelligent(double val) {
        if (!Double.isNaN(val) && val != Double.NEGATIVE_INFINITY && val != Double.POSITIVE_INFINITY) {
            BigDecimal bd = new BigDecimal(val);
            val = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            DecimalFormat decimalFormat = new DecimalFormat("0.##");
            return decimalFormat.format(val);
        }
        return "";
    }


    public static int quzheng(String val) {

        if (CommonUtils.StringNotNull(val) && val.contains(".")) {
            String[] str = val.split("\\.", 2);
            if (str.length > 0)
                return Integer.parseInt(str[0]);
            else
                return Integer.parseInt(val);
        }

        return 0;
    }

    /**
     * 四舍五入 两位
     *
     * @param val
     * @return
     */
    public static double formatBigDecimalMoney(double val) {
        return new BigDecimal(val).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    public static String showWan(String str, String appendStr) {
        str = str.replace(",", "");
        if (TextUtils.isEmpty(str)) {
            str = "0";
        }
        BigDecimal b = new BigDecimal(str);
        if (b.compareTo(new BigDecimal(10000)) > 0) {
            b = b.divide(new BigDecimal(10000));
        } else {
            appendStr = "";
        }
        return addComma(b.toPlainString()) + appendStr;
    }

    public static String moneyToString(double moneyEarn) {

        DecimalFormat moneyFormat = new DecimalFormat("######0.00");

        String tempMoney = "";
        if (moneyEarn < 10000) {
            tempMoney = moneyFormat.format(moneyEarn);
        } else {
            tempMoney = moneyFormat.format(moneyEarn / 10000) + "万";
        }

        return tempMoney;
    }


    public static String wanToString(String money) {
        if (NumberUtils.isNumeric(money)) {
            return wanToString(NumberUtils.stringToInt(money));
        }
        return money;
    }

    public static String wanToString(double money) {
        String temp = "";
        if (Math.abs(money) < 10000) {
            temp = CommonUtils.radixToStr(money);
        } else if (10000 <= Math.abs(money) && Math.abs(money) < 100000000) {
            temp = CommonUtils.radixToStr(money / 10000) + "万";
        } else {
            temp = CommonUtils.radixToStr(money / 100000000) + "亿";
        }
        return temp;
    }

    /**
     * 水位显示
     *
     * @param odds
     * @return
     */
    public static String wanOddsToString2(String odds) {
        return wanOddsToString2(NumberUtils.stringToDouble(odds));
    }

    /**
     * 水位显示
     *
     * @param odds
     * @return
     */
    public static String wanOddsToString2(double odds) {
        String temp = "";
        if (Math.abs(odds) < Math.pow(10, 8)) {
            temp = CommonUtils.radixOdds2(odds, false);
        } else if (Math.pow(10, 8) <= Math.abs(odds) && Math.abs(odds) < Math.pow(10, 12)) {
            temp = CommonUtils.radixOdds2(odds / Math.pow(10, 8), true) + "亿";
        } else if (Math.pow(10, 12) <= Math.abs(odds) && Math.abs(odds) < Math.pow(10, 16)) {
            temp = CommonUtils.radixOdds2(odds / Math.pow(10, 12), true) + "兆";
        } else if (Math.pow(10, 16) <= Math.abs(odds) && Math.abs(odds) < Math.pow(10, 20)) {
            temp = CommonUtils.radixOdds2(odds / Math.pow(10, 16), true) + "京";
        } else if (Math.pow(10, 20) <= Math.abs(odds) && Math.abs(odds) < Math.pow(10, 24)) {
            temp = CommonUtils.radixOdds2(odds / Math.pow(10, 20), true) + "垓";
        } else if (Math.pow(10, 24) <= Math.abs(odds) && Math.abs(odds) < Math.pow(10, 28)) {
            temp = CommonUtils.radixOdds2(odds / Math.pow(10, 24), true) + "秭";
        } else if (Math.pow(10, 28) <= Math.abs(odds) && Math.abs(odds) < Math.pow(10, 32)) {
            temp = CommonUtils.radixOdds2(odds / Math.pow(10, 28), true) + "穰";
        } else if (Math.pow(10, 32) <= Math.abs(odds) && Math.abs(odds) < Math.pow(10, 36)) {
            temp = CommonUtils.radixOdds2(odds / Math.pow(10, 32), true) + "沟";
        } else if (Math.pow(10, 36) <= Math.abs(odds) && Math.abs(odds) < Math.pow(10, 40)) {
            temp = CommonUtils.radixOdds2(odds / Math.pow(10, 36), true) + "涧";
        } else if (Math.pow(10, 40) <= Math.abs(odds) && Math.abs(odds) < Math.pow(10, 34)) {
            temp = CommonUtils.radixOdds2(odds / Math.pow(10, 40), true) + "正";
        } else if (Math.pow(10, 44) <= Math.abs(odds) && Math.abs(odds) < Math.pow(10, 48)) {
            temp = CommonUtils.radixOdds2(odds / Math.pow(10, 44), true) + "载";
        } else {
            temp = CommonUtils.radixOdds2(odds / Math.pow(10, 48), true) + "极";
        }
        return temp;
    }


    /**
     * 金额显示保留2位小数
     *
     * @param odds
     * @return
     */
    public static String wanMoneyToString2(String odds) {
        return wanMoneyToString2(NumberUtils.stringToDouble(odds));
    }

    /**
     * 金额显示保留2位小数
     *
     * @param money
     * @return
     */
    public static String wanMoneyToString2(double money) {
        String temp = "";
        if (Math.abs(money) < Math.pow(10, 8)) {
            temp = CommonUtils.radixMoney2(money);
        } else if (Math.pow(10, 8) <= Math.abs(money) && Math.abs(money) < Math.pow(10, 12)) {
            temp = CommonUtils.radixMoney2(money / Math.pow(10, 8)) + "亿";
        } else if (Math.pow(10, 12) <= Math.abs(money) && Math.abs(money) < Math.pow(10, 16)) {
            temp = CommonUtils.radixMoney2(money / Math.pow(10, 12)) + "兆";
        } else if (Math.pow(10, 16) <= Math.abs(money) && Math.abs(money) < Math.pow(10, 20)) {
            temp = CommonUtils.radixMoney2(money / Math.pow(10, 16)) + "京";
        } else if (Math.pow(10, 20) <= Math.abs(money) && Math.abs(money) < Math.pow(10, 24)) {
            temp = CommonUtils.radixMoney2(money / Math.pow(10, 20)) + "垓";
        } else if (Math.pow(10, 24) <= Math.abs(money) && Math.abs(money) < Math.pow(10, 28)) {
            temp = CommonUtils.radixMoney2(money / Math.pow(10, 24)) + "秭";
        } else if (Math.pow(10, 28) <= Math.abs(money) && Math.abs(money) < Math.pow(10, 32)) {
            temp = CommonUtils.radixMoney2(money / Math.pow(10, 28)) + "穰";
        } else if (Math.pow(10, 32) <= Math.abs(money) && Math.abs(money) < Math.pow(10, 36)) {
            temp = CommonUtils.radixMoney2(money / Math.pow(10, 32)) + "沟";
        } else if (Math.pow(10, 36) <= Math.abs(money) && Math.abs(money) < Math.pow(10, 40)) {
            temp = CommonUtils.radixMoney2(money / Math.pow(10, 36)) + "涧";
        } else if (Math.pow(10, 40) <= Math.abs(money) && Math.abs(money) < Math.pow(10, 34)) {
            temp = CommonUtils.radixMoney2(money / Math.pow(10, 40)) + "正";
        } else if (Math.pow(10, 44) <= Math.abs(money) && Math.abs(money) < Math.pow(10, 48)) {
            temp = CommonUtils.radixMoney2(money / Math.pow(10, 44)) + "载";
        } else {
            temp = CommonUtils.radixMoney2(money / Math.pow(10, 48)) + "极";
        }
        return temp;
    }

    /**
     * 将一个数字转换为有千分位的格式
     *
     * @return
     */
    public static String rahToStrNum(double money) {
        return mDecimalFormat(money, ",###");
    }

    /**
     * 将一个数字转换为有千分位的格式
     *
     * @return
     */
    public static String rahToStrNum(long money) {
        return mDecimalFormat(money, ",###");
    }

    /**
     * 金额验证
     *
     * @param str
     * @return
     */
    public static boolean isMoney(String str) {
        Matcher match = null;
        try {
            Pattern pattern = Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$"); // 判断小数点后2位的数字的正则表达式
            match = pattern.matcher(str);
            return match.matches();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 大于万位四舍五入加万,小于万位四舍五入加元
     *
     * @param val
     * @return
     */
    public static String rahToStrWanYuan(BigDecimal val) {
        if (val == null) {
            return "";
        }
        DecimalFormat df = new DecimalFormat("#");
        String formatText = df.format(val);
        if (formatText.length() > 4) {
            formatText = val.multiply(new BigDecimal("0.0001")).setScale(0, BigDecimal.ROUND_HALF_UP) + "万";
        } else {
            formatText = formatText + "元";
        }
        return formatText;
    }
}
