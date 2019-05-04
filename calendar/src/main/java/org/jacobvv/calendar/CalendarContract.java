package org.jacobvv.calendar;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;

import org.jacobvv.calendar.model.CalendarInfo;
import org.jacobvv.calendar.page.CalendarPagerView;
import org.jacobvv.calendar.util.BasePresenter;
import org.jacobvv.calendar.util.BaseView;

import java.util.List;

/**
 * This specifies the contract between the CalendarWidget and the CalendarPresenter.
 * <p>
 * Created by hui.yin on 2017/6/28.
 */
public interface CalendarContract {
    interface View extends BaseView<Presenter> {

        void goToPrevious();

        void goToNext();

        void updateTitleBar();

        void requestLayout();

        void callbackOnDateSelected(final CalendarInfo day, final boolean selected);

        void callbackOnRangeSelected(List<CalendarInfo> days);

        void callbackOnPageSwitched(final CalendarInfo day);

    }

    interface Presenter extends BasePresenter {

        Context getContext();

        CalendarState getState();

        CalendarProfile getProfile();

        void createPagerAdapter(CalendarPagerView pager);

        void onDateClicked(CalendarInfo info);

        int getPageCount();

        CalendarInfo getPageItem(int position);

        void setShowMode(int mode);

        void setCalendarMode(int calendarMode);

        void setTitleBarVisible(boolean visible);

        void setAllowClickOther(boolean allow);

        void setPagingEnable(boolean enable);

        void setFirstDayOfWeek(int firstDayOfWeek);

        void setSelected(@Nullable CalendarInfo date, boolean selected);

        void clearSelected();

        void setCurrentDate(@Nullable CalendarInfo day, boolean useSmoothScroll);

        void updateCurrentDate(int position);

        ViewPager.OnPageChangeListener getOnPageChangeListener();

        OnPageClickListener getOnPageClickListener();
    }
}
