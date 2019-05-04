package org.jacobvv.calendar.formatter;

import android.content.Context;
import android.text.SpannableStringBuilder;

import org.jacobvv.calendar.model.CalendarInfo;

/**
 * Created by hui.yin on 2017/6/2.
 */

public class DateTitleArrayFormatter implements DateFormatter {

    private final CharSequence[] monthLabels;

    /**
     * Format using an array of month labels
     *
     * @param monthLabels an array of 12 labels to use for months, starting with January
     */
    public DateTitleArrayFormatter(CharSequence[] monthLabels) {
        if (monthLabels == null) {
            throw new IllegalArgumentException("Label array cannot be null");
        }
        if (monthLabels.length < 12) {
            throw new IllegalArgumentException("Label array is too short");
        }
        this.monthLabels = monthLabels;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CharSequence format(Context context, CalendarInfo day) {
        return new SpannableStringBuilder()
                .append(monthLabels[day.getSolar().getMonth() - 1])
                .append(" ")
                .append(String.valueOf(day.getSolar().getYear())).toString();
    }
}
