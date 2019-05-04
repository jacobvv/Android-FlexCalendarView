package org.jacobvv.calendar.page;

import android.support.annotation.NonNull;

import org.jacobvv.calendar.CalendarContract;
import org.jacobvv.calendar.CalendarState;
import org.jacobvv.calendar.model.CalendarInfo;
import org.jacobvv.calendar.util.Utils;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.jacobvv.calendar.CalendarState.DEFAULT_DAYS_IN_WEEK;

/**
 * Created by hui.yin on 2017/5/31.
 */
public class WeekPagerAdapter extends CalendarPagerAdapter<WeekPage> {

    public WeekPagerAdapter(CalendarContract.Presenter callback, CalendarPagerView pager) {
        super(callback, pager);
    }

    @Override
    WeekPage createView(int position) {
        CalendarInfo date = getItem(position);
        return new WeekPage(mPresenter, date, getFirstDay(date));
    }

    @Override
    CalendarInfo getFirstDay(CalendarInfo date) {
        int firstDayOfWeek = mPresenter.getState().getFirstDayOfWeek();
        Calendar c = date.getCalendar(null);
        //noinspection WrongConstant
        c.setFirstDayOfWeek(firstDayOfWeek);
        int dow = Utils.getDayOfWeek(c);
        int delta = firstDayOfWeek - dow;
        if (delta > 0) {
            delta -= CalendarState.DEFAULT_DAYS_IN_WEEK;
        }
        c.add(Calendar.DATE, delta);
        return CalendarInfo.from(c);
    }

    @Override
    DateRangeIndex createRangeIndex(CalendarInfo min, CalendarInfo max) {
        return new Weekly(this, min, max);
    }

    private static class Weekly implements DateRangeIndex {

        private final CalendarInfo min;
        private final int count;

        Weekly(CalendarPagerAdapter adapter, @NonNull CalendarInfo min, @NonNull CalendarInfo max) {
            this.min = adapter.getFirstDay(min);
            int days = Utils.intervalBetween(this.min.getCalendar(null), max.getCalendar(null));
            this.count = days / DEFAULT_DAYS_IN_WEEK + 1;
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
            int days = Utils.intervalBetween(this.min.getCalendar(null), day.getCalendar(null));
            return days / DEFAULT_DAYS_IN_WEEK;
        }

        @Override
        public CalendarInfo getItem(int position) {
            long minMillis = min.getDate().getTime();
            long millisOffset = TimeUnit.MILLISECONDS.convert(
                    position * DEFAULT_DAYS_IN_WEEK,
                    TimeUnit.DAYS);
            long positionMillis = minMillis + millisOffset;
            return CalendarInfo.from(new Date(positionMillis));
        }

    }
}
