package org.jacobvv.calendar.formatter;

import android.content.Context;

import org.jacobvv.calendar.model.CalendarInfo;

/**
 * Created by hui.yin on 2017/5/23.
 */

public class DayFirstFormatter implements DateFormatter {

    @Override
    public CharSequence format(Context context, CalendarInfo day) {
        return String.valueOf(day.getSolar().getDay());
    }

}
