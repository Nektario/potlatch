package com.mocca_capstone.potlatch.utilities;

import android.text.format.DateUtils;

import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Formatters {
    private static NumberFormat nf = NumberFormat.getInstance();

    public static String formatDateTimeForLocaleIndependentDisplay(long epoch) {
        return (String) DateUtils.getRelativeTimeSpanString (epoch, System.currentTimeMillis(), 0L, DateUtils.FORMAT_ABBREV_ALL);
    }

    public static String formatISO8601Duration(String duration) {
        String minutesPattern = "(\\d+)M";
        String secondsPattern = "(\\d{1,2})S";
        Pattern minutesRegex = Pattern.compile(minutesPattern, Pattern.CASE_INSENSITIVE);
        Pattern secondsRegex = Pattern.compile(secondsPattern, Pattern.CASE_INSENSITIVE);
        String minutes = "0";
        String seconds = "00";

        Matcher matcher = minutesRegex.matcher(duration);
        if (matcher.find()) {
            minutes = matcher.group(1);
        }

        matcher = secondsRegex.matcher(duration);
        if (matcher.find()) {
            seconds = String.format("%02d", Integer.parseInt(matcher.group(1)));
        }

        return minutes+":"+seconds;
    }

    public static String formatNumber(long num) {
        return nf.format(num);
    }
}
