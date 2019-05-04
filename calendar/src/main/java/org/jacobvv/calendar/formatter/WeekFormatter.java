package org.jacobvv.calendar.formatter;

import android.content.Context;

/**
 * Created by hui.yin on 2017/5/23.
 */

public interface WeekFormatter {

    public CharSequence format(Context context, int dayOfWeek);

    public static WeekFormatter DEFAULT = new WeekDefaultFormatter();
}
