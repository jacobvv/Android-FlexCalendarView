package org.jacobvv.calendar.page;

import android.support.annotation.NonNull;
import android.util.SparseArray;

import org.jacobvv.calendar.CalendarContract;
import org.jacobvv.calendar.CalendarState;
import org.jacobvv.calendar.model.CalendarInfo;
import org.jacobvv.calendar.util.Utils;

import java.util.Calendar;

/**
 * Created by hui.yin on 2017/5/31.
 */
public class MonthPagerAdapter extends CalendarPagerAdapter<MonthPage> {

    public MonthPagerAdapter(CalendarContract.Presenter callback, CalendarPagerView pager) {
        super(callback, pager);
    }

    @Override
    MonthPage createView(int position) {
        CalendarInfo date = getItem(position);
        return new MonthPage(mPresenter, date, getFirstDay(date));
    }

    @Override
    CalendarInfo getFirstDay(CalendarInfo date) {
        int firstDayOfWeek = mPresenter.getState().getFirstDayOfWeek();
        boolean showOther = (mPresenter.getState().getShowMode() & CalendarState.SHOW_OTHER) != 0;
        Calendar c = date.getCalendar(null);
        //noinspection WrongConstant
        c.setFirstDayOfWeek(firstDayOfWeek);
        int dow = Utils.getDayOfWeek(c);
        int delta = firstDayOfWeek - dow;
        //If the delta is positive, we want to remove a week
        boolean removeRow = showOther ? delta >= 0 : delta > 0;
        if (removeRow) {
            delta -= CalendarState.DEFAULT_DAYS_IN_WEEK;
        }
        c.add(Calendar.DATE, delta);
        return CalendarInfo.from(c);
    }

    @Override
    DateRangeIndex createRangeIndex(CalendarInfo min, CalendarInfo max) {
        return new Monthly(min, max);
    }

    private static class Monthly implements DateRangeIndex {

        private final CalendarInfo min;
        private final int count;

        private SparseArray<CalendarInfo> dayCache = new SparseArray<>();

        Monthly(@NonNull CalendarInfo min, @NonNull CalendarInfo max) {
            this.min = CalendarInfo.from(min.getSolar().getYear(), min.getSolar().getMonth(), 1);
            max = CalendarInfo.from(max.getSolar().getYear(), max.getSolar().getMonth(), 1);
            this.count = indexOf(max) + 1;
        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public int getDefaultIndex() {
            return 0;
        }

        @Override
        public int indexOf(CalendarInfo day) {
            int yDiff = day.getSolar().getYear() - min.getSolar().getYear();
            int mDiff = day.getSolar().getMonth() - min.getSolar().getMonth();

            return (yDiff * 12) + mDiff;
        }

        @Override
        public CalendarInfo getItem(int position) {

            CalendarInfo re = dayCache.get(position);
            if (re != null) {
                return re;
            }

            int numY = position / 12;
            int numM = position % 12;

            int year = min.getSolar().getYear() + numY;
            int month = min.getSolar().getMonth() + numM;
            if (month > 12) {
                year += 1;
                month -= 12;
            }

            re = CalendarInfo.from(year, month, 1);
            dayCache.put(position, re);
            return re;
        }
    }
}
