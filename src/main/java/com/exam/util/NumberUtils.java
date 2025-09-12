package com.exam.util;

public class NumberUtils
{
    public static boolean isNullOrZero(final Number number){
        return number == null ||
                (
                        number instanceof Integer ? number.intValue() == 0 :
                                number instanceof Long    ? number.longValue() == 0 :
                                        number instanceof Double  ? number.doubleValue() == 0 :
                                                number instanceof Short   ? number.shortValue() == 0 :
                                                        number.floatValue() == 0
                );
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
