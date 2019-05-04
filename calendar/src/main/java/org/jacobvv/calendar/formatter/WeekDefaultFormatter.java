package org.jacobvv.calendar.formatter;

import android.content.Context;

import org.jacobvv.calendar.util.Utils;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by hui.yin on 2017/6/1.
 */

public class WeekDefaultFormatter implements WeekFormatter {

    @Override
    public CharSequence format(Context context, int dayOfWeek) {
        Calendar c = Utils.getWeekCalendar(dayOfWeek);
        return c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
    }
}
