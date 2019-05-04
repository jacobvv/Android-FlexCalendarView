package org.jacobvv.calendar;

import android.support.annotation.NonNull;

import org.jacobvv.calendar.model.CalendarInfo;

/**
 * The callback used to indicate a mDate has been checked or unchecked
 *
 * Created by hui.yin on 2017/5/23.
 */

public interface OnDateSelectedListener {

    /**
     * Called when a user clicks on a day.
     * There is no logic to prevent multiple calls for the same mDate and state.
     *
     * @param view   the agent of calendar view provided interface to calendar.
     * @param date    the mDate that was selected or unselected
     * @param checked true if the day is now selected, false otherwise
     */
    void onDateSelected(@NonNull CalendarWidget view, @NonNull CalendarInfo date, boolean checked);
}
