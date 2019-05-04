package org.jacobvv.calendar.page;

import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import org.jacobvv.calendar.CalendarContract;
import org.jacobvv.calendar.formatter.DateFormatter;
import org.jacobvv.calendar.formatter.WeekFormatter;
import org.jacobvv.calendar.model.CalendarInfo;

import java.util.ArrayList;

/**
 * View pager adapter for calendar.
 * <p>
 * Created by hui.yin on 2017/5/23.
 */

public abstract class CalendarPagerAdapter<V extends CalendarPage> extends PagerAdapter {

    private static final String TAG = "CalendarPagerAdapter";

    protected CalendarContract.Presenter mPresenter;
    protected CalendarPagerView pager;

    private final ArrayList<V> calendarPages;
    private DateRangeIndex dateRangeIndex;

    private boolean dateClickable = true;

    private DateFormatter titleFormatter;
    private WeekFormatter weekFormatter;
    private DateFormatter dateFormatter;
    private DateFormatter dateSecondFormatter;

    CalendarPagerAdapter(CalendarContract.Presenter callback, CalendarPagerView pager) {
        mPresenter = callback;
        calendarPages = new ArrayList<>();
        dateRangeIndex = createRangeIndex(mPresenter.getState().getMinDate(), mPresenter.getState().getMaxDate());
        this.pager = pager;
        this.pager.setAdapter(this);
    }

    @Override
    public int getCount() {
        return dateRangeIndex.getCount();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        V page = createView(position);
        page.setAlpha(0);

        container.addView(page);
        calendarPages.add(page);
        return page;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        V page = (V) object;
        calendarPages.remove(page);
        container.removeView(page);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleFormatter == null ? "" : titleFormatter.format(mPresenter.getContext(), getItem(position));
    }

    @Override
    public int getItemPosition(Object object) {
        if (!(object instanceof CalendarPage)) {
            return POSITION_NONE;
        }
        CalendarPage page = (CalendarPage) object;
        CalendarInfo date = page.getDate();
        if (date == null) {
            return POSITION_NONE;
        }
        int index = dateRangeIndex.indexOf(date);
        if (index < 0) {
            return POSITION_NONE;
        }
        return index;
    }

    public CalendarPagerView getPager() {
        return pager;
    }

    public CalendarInfo getItem(int position) {
        return dateRangeIndex.getItem(position);
    }

    public CalendarInfo getCurrentItem() {
        return getItem(pager.getCurrentItem());
    }

    public int getIndexForDay(CalendarInfo day) {
        if (day == null) {
            return dateRangeIndex.getDefaultIndex();
        }
        if (mPresenter.getState().getMinDate().isAfter(day)) {
            return 0;
        }
        if (mPresenter.getState().getMaxDate().isBefore(day)) {
            return getCount() - 1;
        }
        return dateRangeIndex.indexOf(day);
    }

    public void setCurrentDate(@Nullable CalendarInfo day, boolean useSmoothScroll) {
        int index = getIndexForDay(day);
        if (pager.getCurrentItem() != index) {
            pager.setCurrentItem(index, useSmoothScroll);
        }
    }

    public void setTitleFormatter(DateFormatter formatter) {
        if (formatter == null) {
            return;
        }
        this.titleFormatter = formatter;
    }

    public void invalidatePages() {
        for (V page : calendarPages) {
            page.invalidate();
        }
    }

    public void invalidatePagesData() {
        for (V page : calendarPages) {
            page.initDay();
            page.invalidate();
        }
    }

    public void migrateStateFrom(CalendarPagerAdapter<?> oldAdapter) {
        this.dateClickable = oldAdapter.dateClickable;

        this.titleFormatter = oldAdapter.titleFormatter;
        this.weekFormatter = oldAdapter.weekFormatter;
        this.dateFormatter = oldAdapter.dateFormatter;
        this.dateSecondFormatter = oldAdapter.dateSecondFormatter;
    }

    public void reindexDateRange() {
        dateRangeIndex = createRangeIndex(mPresenter.getState().getMinDate(),
                mPresenter.getState().getMaxDate());
        notifyDataSetChanged();
    }

    abstract V createView(int position);

    abstract CalendarInfo getFirstDay(CalendarInfo date);

    abstract DateRangeIndex createRangeIndex(CalendarInfo min, CalendarInfo max);

    /**
     * Index manager of whole mDate range used by paged calendar
     */
    interface DateRangeIndex {
        int getCount();

        int getDefaultIndex();

        int indexOf(CalendarInfo day);

        CalendarInfo getItem(int position);
    }
}
