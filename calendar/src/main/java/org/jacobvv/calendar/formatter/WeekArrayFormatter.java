package org.jacobvv.calendar.formatter;

import android.content.Context;

/**
 * Use an array given to supply week day labels
 *
 * Created by hui.yin on 2017/6/2.
 */

public class WeekArrayFormatter implements WeekFormatter {

    private final CharSequence[] weekDayLabels;

    /**
     * @param weekDayLabels an array of 7 labels, starting with Sunday
     */
    public WeekArrayFormatter(CharSequence[] weekDayLabels) {
        if (weekDayLabels == null) {
            throw new IllegalArgumentException("Cannot be null");
        }
        if (weekDayLabels.length != 7) {
            throw new IllegalArgumentException("Array must contain exactly 7 elements");
        }
        this.weekDayLabels = weekDayLabels;
    }

    @Override
    public CharSequence format(Context context, int dayOfWeek) {
        return weekDayLabels[dayOfWeek - 1];
    }
}
