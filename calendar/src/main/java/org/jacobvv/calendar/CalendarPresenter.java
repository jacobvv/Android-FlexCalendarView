package org.jacobvv.calendar;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import org.jacobvv.calendar.formatter.DateFormatter;
import org.jacobvv.calendar.model.BaseLunarDataSource;
import org.jacobvv.calendar.model.CalendarInfo;
import org.jacobvv.calendar.model.LunarCalendarProvider;
import org.jacobvv.calendar.page.CalendarPage;
import org.jacobvv.calendar.page.CalendarPagerAdapter;
import org.jacobvv.calendar.page.CalendarPagerView;
import org.jacobvv.calendar.page.MonthPagerAdapter;
import org.jacobvv.calendar.page.WeekPagerAdapter;

import java.util.List;

/**
 * Calendar Presenter for CalendarWidget
 * <p>
 * Created by hui.yin on 2017/6/29.
 */
class CalendarPresenter implements CalendarContract.Presenter {

    private static final String TAG = "CalendarPresenter";
    private CalendarContract.View mView;
    private CalendarPagerAdapter<? extends CalendarPage> mAdapter;
    private CalendarState mState;
    private CalendarProfile mProfile;

    private OnPageClickListener mOnPageListener;

    private final OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            updateCurrentDate(position);
            mView.callbackOnPageSwitched(mState.getCurrentPageItem());
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }
    };

    CalendarPresenter(CalendarContract.View view) {
        mView = view;
        mView.setPresenter(this);
        mState = new CalendarState(getContext());
        mProfile = mState.getProfile();
        LunarCalendarProvider.getInstance().loadLunar(mView.getContext(), new BaseLunarDataSource.LoadLunarAdapter() {
            @Override
            public void onLunarLoaded() {
                if (mAdapter != null) {
                    mAdapter.invalidatePagesData();
                }
            }
        });
    }

    @Override
    public void onViewAttached() {
    }

    @Override
    public void onViewDetached() {
    }

    @Override
    public Context getContext() {
        return mView.getContext();
    }

    @Override
    public CalendarState getState() {
        return mState;
    }

    @Override
    public CalendarProfile getProfile() {
        return mProfile;
    }

    @Override
    public void createPagerAdapter(CalendarPagerView pager) {
        mOnPageListener = new OnPageClickListener(pager);
        CalendarPagerAdapter<? extends CalendarPage> adapter;
        if (mState.getCalendarMode() == CalendarState.MODE_MONTH) {
            adapter = new MonthPagerAdapter(this, pager);
        } else if (mState.getCalendarMode() == CalendarState.MODE_WEEK) {
            adapter = new WeekPagerAdapter(this, pager);
        } else {
            return;
        }
        if (mAdapter != null) {
            adapter.migrateStateFrom(mAdapter);
        }
        mAdapter = adapter;
        // Adapter is created while parsing the TypedArray attrs, so setup has to happen after
        mAdapter.setTitleFormatter(DateFormatter.DEFAULT_TITLE);
        CalendarInfo last = mState.getLastClickDate();
        if (last != null) {
            mState.setCurrentPageItem(last);
        }
        mAdapter.setCurrentDate(mState.getCurrentPageItem(), true);
        mView.updateTitleBar();
    }

    @Override
    public void onDateClicked(CalendarInfo info) {
        boolean isChecked = !getState().getSelected().contains(info);
        mView.callbackOnDateSelected(info, isChecked);
        final CalendarInfo currentDate = mAdapter.getCurrentItem();

        final int currentMonth = currentDate.getSolar().getMonth();
        final int selectedMonth = info.getSolar().getMonth();

        if (mState.getCalendarMode() == CalendarState.MODE_MONTH && mState.allowClickOthers()
                && currentMonth != selectedMonth) {
            if (currentDate.isAfter(info)) {
                mView.goToPrevious();
            } else if (currentDate.isBefore(info)) {
                mView.goToNext();
            }
        }
        clearSelected();
        setSelected(info, true);
    }

    @Override
    public int getPageCount() {
        return mAdapter.getCount();
    }

    @Override
    public CalendarInfo getPageItem(int position) {
        return mAdapter.getItem(position);
    }

    @Override
    public void setShowMode(int mode) {
        mState.setShowMode(mode);
        mAdapter.invalidatePages();
    }

    @Override
    public void setCalendarMode(int calendarMode) {
        mState.setCalendarMode(calendarMode);
        createPagerAdapter(mAdapter.getPager());
    }

    @Override
    public void setTitleBarVisible(boolean visible) {
        mState.setTitleBarVisible(visible);
    }

    @Override
    public void setAllowClickOther(boolean allow) {
        mState.setAllowClickOthers(allow);
    }

    @Override
    public void setPagingEnable(boolean enable) {
        mState.setPagingEnable(enable);
    }

    @Override
    public void setFirstDayOfWeek(int firstDayOfWeek) {
        mState.setFirstDayOfWeek(firstDayOfWeek);
        createPagerAdapter(mAdapter.getPager());
    }

    @Override
    public void setSelected(@Nullable CalendarInfo date, boolean selected) {
        if (date == null) {
            return;
        }
        mState.setLastClickDate(date);
        if (selected) {
            if (!mState.getSelected().contains(date)) {
                mState.addSelected(date);
                mAdapter.invalidatePages();
            }
        } else {
            if (mState.getSelected().contains(date)) {
                mState.removeSelected(date);
                mAdapter.invalidatePages();
            }
        }
    }

    @Override
    public void clearSelected() {
        List<CalendarInfo> dates = mState.getSelected();
        mState.cleanSelected();
        mAdapter.invalidatePages();
        for (CalendarInfo day : dates) {
            mView.callbackOnDateSelected(day, false);
        }
    }

    @Override
    public void setCurrentDate(@Nullable CalendarInfo day, boolean useSmoothScroll) {
        if (day == null || mState.getCurrentPageItem().equals(day)) {
            return;
        }
        mState.setCurrentPageItem(day);
        mAdapter.setCurrentDate(day, useSmoothScroll);
        mView.updateTitleBar();
    }

    @Override
    public void updateCurrentDate(int position) {
        CalendarInfo current = mAdapter.getItem(position);
        int index = mAdapter.getIndexForDay(mState.getCurrentPageItem());
        mState.setCurrentPageItem(current);
        if (position == index) {
            return;
        }
        mState.setLastClickDate(current);
        mView.updateTitleBar();
    }

    public OnPageChangeListener getOnPageChangeListener() {
        return onPageChangeListener;
    }

    @Override
    public OnPageClickListener getOnPageClickListener() {
        return mOnPageListener;
    }

}
