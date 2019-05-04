package org.jacobvv.calendar.page;

import android.support.annotation.NonNull;

import org.jacobvv.calendar.CalendarContract;
import org.jacobvv.calendar.CalendarState;
import org.jacobvv.calendar.model.CalendarInfo;

/**
 * Created by jacob on 17-7-19.
 */

public class WeekPage extends CalendarPage {

    public WeekPage(@NonNull CalendarContract.Presenter callback, CalendarInfo date, CalendarInfo firstDay) {
        super(callback, date, firstDay);
    }

    @Override
    protected int getRows() {
        return 1;
    }

    @Override
    protected int atPage(CalendarInfo info) {
        return 0;
    }

    @Override
    protected void initShowMode() {
        mIsShowSub = (mCallback.getState().getShowMode() & CalendarState.SHOW_LUNAR) != 0;
        mIsShowOther = true;
    }
}
