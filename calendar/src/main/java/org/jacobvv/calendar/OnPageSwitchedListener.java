package org.jacobvv.calendar;

import org.jacobvv.calendar.model.CalendarInfo;

/**
 * Created by hui.yin on 2017/5/23.
 */

public interface OnPageSwitchedListener {
    void onPageSwitched(CalendarWidget view, CalendarInfo month);
}
