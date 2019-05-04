package org.jacobvv.calendar.page;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Custom ViewPager that allows swiping to be disabled.
 * <p>
 * Created by hui.yin on 2017/5/23.
 */

public class CalendarPagerView extends ViewPager {

    private boolean pagingEnabled = true;

    public CalendarPagerView(Context context) {
        super(context);
    }

    public CalendarPagerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * enable disable viewpager scroll
     *
     * @param pagingEnabled false to disable paging, true for paging (default)
     */
    public void setPagingEnabled(boolean pagingEnabled) {
        this.pagingEnabled = pagingEnabled;
    }

    /**
     * @return is this viewpager allowed to page
     */
    public boolean isPagingEnabled() {
        return pagingEnabled;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return pagingEnabled && super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return pagingEnabled && super.onTouchEvent(ev);
    }

    /**
     * disables scrolling vertically when paging disabled, fixes scrolling
     * for nested {@link android.support.v4.view.ViewPager}
     */
    @Override
    public boolean canScrollVertically(int direction) {
        return pagingEnabled && super.canScrollVertically(direction);
    }

    /**
     * disables scrolling horizontally when paging disabled, fixes scrolling
     * for nested {@link android.support.v4.view.ViewPager}
     */
    @Override
    public boolean canScrollHorizontally(int direction) {
        return pagingEnabled && super.canScrollHorizontally(direction);
    }

}
