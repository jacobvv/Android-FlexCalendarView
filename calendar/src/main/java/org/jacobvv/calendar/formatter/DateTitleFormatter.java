package org.jacobvv.calendar.formatter;

import android.content.Context;
import android.text.format.DateUtils;

import org.jacobvv.calendar.model.CalendarInfo;

/**
 * Created by hui.yin on 2017/5/23.
 */

public class DateTitleFormatter implements DateFormatter {
    @Override
    public CharSequence format(Context context, CalendarInfo day) {
        return DateUtils.formatDateTime(context, day.getDate().getTime(),
                DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_NO_MONTH_DAY);
    }
}
