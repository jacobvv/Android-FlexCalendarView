package org.jacobvv.calendar;

import org.jacobvv.calendar.model.CalendarInfo;

import java.util.List;

/**
 * Created by hui.yin on 2017/6/2.
 */

interface OnRangeSelectedListener {
    void onRangeSelected(CalendarWidget view, List<CalendarInfo> days);
}
