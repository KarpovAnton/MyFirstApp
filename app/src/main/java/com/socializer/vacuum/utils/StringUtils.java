package com.socializer.vacuum.utils;

public class StringUtils {

    public static String getPrice(int price) {
        return new StringBuffer(String.valueOf(price))
                .append(" \u20BD")
                .toString();
    }

    public static String getSignPrice(String price, boolean increase) {
        return new StringBuffer(getSign(increase))
                .append(price)
                .append("\u20BD")
                .toString();
    }

    public static String getSign(boolean plus) {
        return plus?"+":"-";
    }

    public static String formatPhoneNumber(String number) {
        return number.replaceFirst(".?\\+?7.?(\\d{3})(\\d{3})(\\d{2})(\\d{2})", "+7 ($1) $2-$3-$4");
    }
}
