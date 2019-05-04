package org.jacobvv.calendar.formatter;

import android.content.Context;

import org.jacobvv.calendar.model.CalendarInfo;

/**
 * Created by hui.yin on 2017/5/23.
 */

public interface DateFormatter {
    CharSequence format(Context context, CalendarInfo day);
    DateFormatter DEFAULT_DAY_FIRST = new DayFirstFormatter();
    DateFormatter DEFAULT_DAY_SECOND = new DaySecondaryFormatter();
    DateFormatter DEFAULT_TITLE = new DateTitleFormatter();
}
