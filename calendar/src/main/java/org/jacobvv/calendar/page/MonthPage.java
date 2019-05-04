package org.jacobvv.calendar.page;

import android.support.annotation.NonNull;

import org.jacobvv.calendar.CalendarContract;
import org.jacobvv.calendar.CalendarState;
import org.jacobvv.calendar.model.CalendarInfo;

/**
 * Created by jacob on 17-7-19.
 */

public class MonthPage extends CalendarPage {

    public MonthPage(@NonNull CalendarContract.Presenter callback, CalendarInfo date, CalendarInfo firstDay) {
        super(callback, date, firstDay);
    }

    @Override
    protected int getRows() {
        return CalendarState.DEFAULT_MAX_WEEKS;
    }

    @Override
    protected int atPage(CalendarInfo info) {
        if (mDate.getSolar().getYear() == info.getSolar().getYear() &&
                mDate.getSolar().getMonth() == info.getSolar().getMonth()) {
            return 0;
        } else if (mDate.isAfter(info)) {
            return -1;
        } else {
            return 1;
        }
    }

    @Override
    protected void initShowMode() {
        mIsShowOther = (mCallback.getState().getShowMode() & CalendarState.SHOW_OTHER) != 0;
        mIsShowSub = (mCallback.getState().getShowMode() & CalendarState.SHOW_LUNAR) != 0;
    }

}
