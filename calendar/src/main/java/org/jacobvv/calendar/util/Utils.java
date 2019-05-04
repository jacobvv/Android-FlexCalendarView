package org.jacobvv.calendar.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.SparseArray;
import android.util.TypedValue;

import org.jacobvv.calendar.CalendarState;
import org.jacobvv.calendar.model.BaseInfo;
import org.jacobvv.calendar.model.CalendarInfo;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

import static java.util.Calendar.DATE;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

/**
 * Utilities for Calendar
 */
@SuppressWarnings("unused")
public class Utils {

    private static SparseArray<Calendar> weekCalendar = new SparseArray<>();

    private static byte buf[] = new byte[8];

    public static int getThemeAccentColor(Context context) {
        int colorAttr;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            colorAttr = android.R.attr.colorAccent;
        } else {
            //Get colorAccent defined for AppCompat
            colorAttr = context.getResources().getIdentifier("colorAccent", "attr", context.getPackageName());
        }
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(colorAttr, outValue, true);
        return outValue.data;
    }

    public static ColorStateList getDefaultColorState(boolean isDark, int color) {
        int[] colors;
        if (isDark) {
            colors = new int[]{Color.BLACK, Color.BLACK, Color.GRAY, color};
        } else {
            colors = new int[]{Color.WHITE, Color.WHITE, Color.GRAY, color};
        }
        int[][] states = new int[4][];
        states[0] = new int[]{android.R.attr.state_checked};
        states[1] = new int[]{android.R.attr.state_pressed};
        states[2] = new int[]{-android.R.attr.state_enabled};
        states[3] = new int[]{};
        return new ColorStateList(states, colors);
    }

    /**
     * @param date {@linkplain Date} to pull mDate information from
     * @return a new Calendar instance with the mDate set to the provided mDate. Time set to zero.
     */
    public static Calendar fromDate(@NonNull Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static Calendar fromDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, month - 1, day);
        return calendar;
    }

    public static Calendar fromDate(BaseInfo date) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(date.getYear(), date.getMonth() - 1, date.getDay());
        return calendar;
    }

    /**
     * Set the provided calendar to the first day of the month. Also clears all time information.
     *
     * @param calendar {@linkplain Calendar} to modify to be at the first fay of the month
     */
    public static void setToFirstDay(Calendar calendar) {
        int year = getYear(calendar);
        int month = getMonth(calendar);
        calendar.clear();
        calendar.set(year, month - 1, 1);
    }

    /**
     * Clean up information besides mDate.
     *
     * @param c calendar to clean up
     */
    public static Calendar refreshDate(Calendar c) {
        int year = getYear(c);
        int month = getMonth(c);
        int day = getDay(c);
        c.clear();
        c.set(year, month - 1, day);
        return c;
    }

    public static int getYear(Calendar calendar) {
        return calendar.get(YEAR);
    }

    public static int getMonth(Calendar calendar) {
        return calendar.get(MONTH) + 1;
    }

    public static int getDay(Calendar calendar) {
        return calendar.get(DATE);
    }

    public static int getDayOfWeek(Calendar calendar) {
        return calendar.get(DAY_OF_WEEK);
    }

    public static Calendar getWeekCalendar(int dayOfWeek) {
        Calendar c = weekCalendar.get(dayOfWeek);
        if (c == null) {
            c = Calendar.getInstance();
            c.clear();
            c.set(DAY_OF_WEEK, dayOfWeek);
            weekCalendar.put(dayOfWeek, c);
        }
        return c;
    }

    public static
    @NonNull
    Calendar getFirstDay(@NonNull CalendarInfo month,
                         int firstDayOfWeek, boolean showOtherMonth) {
        Calendar c = month.getCalendar(null);
        c.setFirstDayOfWeek(firstDayOfWeek);
        int dow = getDayOfWeek(c);
        int delta = firstDayOfWeek - dow;
        //If the delta is positive, we want to remove a week
        boolean removeRow = showOtherMonth ? delta >= 0 : delta > 0;
        if (removeRow) {
            delta -= CalendarState.DEFAULT_DAYS_IN_WEEK;
        }
        c.add(DATE, delta);
        return c;
    }

    public static int dp2Px(Context context, int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics()
        );
    }

    public static int sp2Px(Context context, int sp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics()
        );
    }

    public static int date2int(Calendar date) {
        return getYear(date) * 10000 + getMonth(date) * 100 + getDay(date);
    }

    public static short byte2uint8(byte b) {
        return (short) (b & 0xff);
    }

    public static int byte2uint16(byte[] b) {
        return ((b[0] & 0xff) << 8) + (b[1] & 0xff);
    }

    public static int byte2uint16(byte[] b, int off) {
        return ((b[off] & 0xff) << 8) + (b[off + 1] & 0xff);
    }

    public static int byte2int32(byte[] b) {
        int result = b[0];
        for (int i = 1; i < 4; i++) {
            result = (result << 8) + (b[i] & 0xff);
        }
        return result;
    }

    public static int byte2int32(byte[] b, int off) {
        int result = b[off];
        for (int i = 1 + off; i < 4 + off; i++) {
            result = (result << 8) + (b[i] & 0xff);
        }
        return result;
    }

    public static short readUint8(InputStream is) throws IOException {
        int count = is.read(buf, 0, 1);
        if (count < 1) {
            throw new EOFException();
        }
        return (short) (buf[0] & 0xff);
    }

    public static int readUint16(InputStream is) throws IOException {
        int count = is.read(buf, 0, 2);
        if (count < 2) {
            throw new EOFException();
        }
        return ((buf[0] & 0xff) << 8) + (buf[1] & 0xff);
    }

    public static long readUint32(InputStream is) throws IOException {
        int count = is.read(buf, 0, 4);
        if (count < 4) {
            throw new EOFException();
        }
        long result = 0;
        for (int i = 0; i < 4; i++) {
            result = (result << 8) + (buf[i] & 0xff);
        }
        return result;
    }

    public static int readInt32(InputStream is) throws IOException {
        int count = is.read(buf, 0, 4);
        if (count < 4) {
            throw new EOFException();
        }
        int result = buf[0];
        for (int i = 1; i < 4; i++) {
            result = (result << 8) + (buf[i] & 0xff);
        }
        return result;
    }

    public static int intervalBetween(Calendar start, Calendar end) {
        end = refreshDate(end);
        start = refreshDate(start);
        return (int) (Math.abs(end.getTimeInMillis() - start.getTimeInMillis()) / 1000 / 3600 / 24);
    }

}
