package org.jacobvv.calendar;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.jacobvv.calendar.model.CalendarInfo;
import org.jacobvv.calendar.page.CalendarPagerView;
import org.jacobvv.calendar.util.Utils;

import java.util.Calendar;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;


/**
 * <p>
 * This class is a calendar widget for displaying and selecting dates.
 * The range of dates supported by this calendar is configurable.
 * A user can select a mDate by taping on it and can page the calendar to a desired mDate.
 * </p>
 * <p>
 * By default, the range of dates shown is from 200 years in the past to 200 years in the future.
 * This can be extended or shortened by configuring the minimum and maximum dates.
 * </p>
 * <p>
 * When selecting a mDate out of range, or when the range changes so the selection becomes outside,
 * The mDate closest to the previous selection will become selected. This will also trigger the
 * {@linkplain OnDateSelectedListener}
 * </p>
 * <p>
 * <strong>Note:</strong> if this view's size isn't divisible by 7,
 * the contents will be centered inside such that the days in the calendar are equally square.
 * For example, 600px isn't divisible by 7, so a cell size of 85 is chosen, making the calendar
 * 595px wide. The extra 5px are distributed left and right to get to 600px.
 * </p>
 */
@SuppressWarnings("unused")
public class CalendarWidget extends ViewGroup implements CalendarContract.View {

    private static final String TAG = "CalendarWidget";
    private CalendarContract.Presenter mPresenter;

    private TitleBarView mTitleBar;
    private WeekBarView mWeekBar;
    private CalendarPagerView mPager;

    private OnDateSelectedListener dateListener;
    private OnPageSwitchedListener pageListener;
    private OnRangeSelectedListener rangeListener;

    public CalendarWidget(Context context) {
        this(context, null);
    }

    public CalendarWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        // If we're on good Android versions, turn off clipping for cool effects
        setClipToPadding(false);
        setClipChildren(false);

        // TODO: Here should use dependence injection
        new CalendarPresenter(this);

        // State MUST be setup before adapter created.
        TypedArray a = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.CalendarWidget, 0, 0);
        setupState(context, a);
        setupViews();
    }

    @SuppressWarnings("ResourceType")
    private void setupState(Context context, TypedArray a) {
        Resources res = context.getResources();
        int titleHeightPx = res.getDimensionPixelSize(R.dimen.titleHeight);
        int weekHeightPx = WRAP_CONTENT;
        int cellHeightPx = WRAP_CONTENT;

        int titleTextSizePx = res.getDimensionPixelSize(R.dimen.titleTextSize);
        int weekTextSizePx = res.getDimensionPixelSize(R.dimen.weekTextSize);
        int dayTextSizePx = res.getDimensionPixelSize(R.dimen.dayTextSize);
        int subTextSizePx = res.getDimensionPixelSize(R.dimen.subTextSize);

        int arrowColor = Color.BLACK;
        int selectedColor = Utils.getThemeAccentColor(context);

        try {
            int calendarMode = a.getInteger(R.styleable.CalendarWidget_cal_calendarMode, CalendarState.MODE_MONTH);
            int showMode = a.getInteger(R.styleable.CalendarWidget_cal_showMode, CalendarState.SHOW_DEFAULT);

            boolean isTitleVisible = a.getBoolean(R.styleable.CalendarWidget_cal_titleVisible, true);
            boolean allowClickOther = a.getBoolean(R.styleable.CalendarWidget_cal_allowClickOther, true);
            boolean isPagingEnabled = a.getBoolean(R.styleable.CalendarWidget_cal_isPagingEnabled, true);
            int firstDayOfWeek = a.getInteger(R.styleable.CalendarWidget_cal_firstDayOfWeek, Calendar.getInstance().getFirstDayOfWeek());

            titleHeightPx = a.getDimensionPixelSize(R.styleable.CalendarWidget_cal_titleHeight, titleHeightPx);
            weekHeightPx = a.getDimensionPixelSize(R.styleable.CalendarWidget_cal_weekHeight, weekHeightPx);
            cellHeightPx = a.getDimensionPixelSize(R.styleable.CalendarWidget_cal_cellHeight, cellHeightPx);

            titleTextSizePx = a.getDimensionPixelSize(R.styleable.CalendarWidget_cal_titleTextSize, titleTextSizePx);
            weekTextSizePx = a.getDimensionPixelSize(R.styleable.CalendarWidget_cal_weekTextSize, weekTextSizePx);
            dayTextSizePx = a.getDimensionPixelSize(R.styleable.CalendarWidget_cal_dayTextSize, dayTextSizePx);
            subTextSizePx = a.getDimensionPixelSize(R.styleable.CalendarWidget_cal_subTextSize, subTextSizePx);

            arrowColor = a.getColor(R.styleable.CalendarWidget_cal_arrowColor, arrowColor);
            selectedColor = a.getColor(R.styleable.CalendarWidget_cal_selectedColor, selectedColor);

            ColorStateList todayTextColor = a.getColorStateList(R.styleable.CalendarWidget_cal_todayTextColor);
            ColorStateList titleTextColor = a.getColorStateList(R.styleable.CalendarWidget_cal_titleTextColor);
            ColorStateList weekTextColor = a.getColorStateList(R.styleable.CalendarWidget_cal_weekTextColor);
            ColorStateList dayTextColor = a.getColorStateList(R.styleable.CalendarWidget_cal_dayTextColor);
            ColorStateList subTextColor = a.getColorStateList(R.styleable.CalendarWidget_cal_subTextColor);

            if (todayTextColor == null) {
                todayTextColor = Utils.getDefaultColorState(false, selectedColor);
            }
            if (titleTextColor == null) {
                titleTextColor = res.getColorStateList(R.color.calendar_title_light);
            }
            if (weekTextColor == null) {
                weekTextColor = res.getColorStateList(R.color.calendar_text_date_light);
            }
            if (dayTextColor == null) {
                dayTextColor = res.getColorStateList(R.color.calendar_text_date_light);
            }
            if (subTextColor == null) {
                subTextColor = res.getColorStateList(R.color.calendar_text_date_light);
            }

            Drawable leftArrowMask = a.getDrawable(R.styleable.CalendarWidget_cal_leftArrowMask);
            if (leftArrowMask == null) {
                leftArrowMask = getResources().getDrawable(R.mipmap.arrow_previous);
            }
            Drawable rightArrowMask = a.getDrawable(R.styleable.CalendarWidget_cal_rightArrowMask);
            if (rightArrowMask == null) {
                rightArrowMask = getResources().getDrawable(R.mipmap.arrow_next);
            }

            CalendarState state = mPresenter.getState();
            CalendarProfile profile = mPresenter.getProfile();

            state.setShowMode(showMode);
            state.setCalendarMode(calendarMode);
            state.setTitleBarVisible(isTitleVisible);
            state.setAllowClickOthers(allowClickOther);
            state.setPagingEnable(isPagingEnabled);
            state.setFirstDayOfWeek(firstDayOfWeek);

            profile.setTitleHeightPx(titleHeightPx);
            profile.setWeekHeightPx(weekHeightPx);
            profile.setCellHeightPx(cellHeightPx);

            profile.setTitleTextSizePx(titleTextSizePx);
            profile.setWeekTextSizePx(weekTextSizePx);
            profile.setDayTextSizePx(dayTextSizePx);
            profile.setSubTextSizePx(subTextSizePx);

            profile.setArrowColor(arrowColor);
            profile.setSelectedColor(selectedColor);
            profile.setTodayTextColor(todayTextColor);
            profile.setTitleTextColor(titleTextColor);
            profile.setWeekTextColor(weekTextColor);
            profile.setDayTextColor(dayTextColor);
            profile.setSubTextColor(subTextColor);

            profile.setLeftArrowMask(leftArrowMask);
            profile.setRightArrowMask(rightArrowMask);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            a.recycle();
        }
    }

    private void setupViews() {
        CalendarState state = mPresenter.getState();
        CalendarProfile profile = mPresenter.getProfile();

        mWeekBar = new WeekBarView(getContext(), profile, state.getFirstDayOfWeek());
        mPager = new CalendarPagerView(getContext());
        mPager.addOnPageChangeListener(mPresenter.getOnPageChangeListener());
        mPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                position = (float) Math.sqrt(1 - Math.abs(position));
                page.setAlpha(position);
            }
        });
        mPager.setOffscreenPageLimit(1);
        mPager.setPagingEnabled(state.isPagingEnable());
        mPresenter.createPagerAdapter(mPager);
        mPresenter.setSelected(CalendarInfo.TODAY, true);

        if (state.isTitleBarVisible()) {
            setupTitleBar();
            addView(mTitleBar, new LayoutParams(profile.getTitleHeightPx()));
        }
        addView(mWeekBar, new LayoutParams(profile.getWeekHeightPx()));
        // Reset height params after mode change
        addView(mPager, generatePagerLayoutParams());
    }

    private void setupTitleBar() {
        if (mTitleBar == null) {
            CalendarState state = mPresenter.getState();
            mTitleBar = new TitleBarView(getContext(), mPresenter.getProfile(),
                    state.getCurrentPageItem());
            mTitleBar.setOnPageListener(mPresenter.getOnPageClickListener());
        }
    }

    public void updateTitleBar() {
        CalendarState state = mPresenter.getState();
        if (mTitleBar == null || !state.isTitleBarVisible()) {
            return;
        }
        mTitleBar.setDate(state.getCurrentPageItem());
        mTitleBar.setButtonPreviousEnable(canGoPrevious());
        mTitleBar.setButtonNextEnable(canGoNext());
    }

    private LayoutParams generatePagerLayoutParams() {
        CalendarProfile profile = mPresenter.getProfile();
        int height = profile.getCellHeightPx();
        if (height == WRAP_CONTENT || height == MATCH_PARENT) {
            height = CalendarProfile.AUTO_CELL_DIMENSION;
        } else {
            height = height * mPresenter.getState().getWeekCount();
        }
        return new LayoutParams(height);
    }

    @Override
    public void setPresenter(CalendarContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    private boolean canGoPrevious() {
        return mPager.getCurrentItem() > 0;
    }

    private boolean canGoNext() {
        return mPager.getCurrentItem() < (mPresenter.getPageCount() - 1);
    }

    /**
     * Go to previous month or week without using the button in {@link #mTitleBar}. Should only go to
     * previous if {@link #canGoPrevious()} is true, meaning it's possible to go to the previous month
     * or week.
     */
    public void goToPrevious() {
        if (canGoPrevious()) {
            mPager.setCurrentItem(mPager.getCurrentItem() - 1, true);
        }
    }

    /**
     * Go to next month or week without using the button in {@link #mTitleBar}. Should only go to
     * next if {@link #canGoNext()} is enabled, meaning it's possible to go to the next month or
     * week.
     */
    public void goToNext() {
        if (canGoNext()) {
            mPager.setCurrentItem(mPager.getCurrentItem() + 1, true);
        }
    }

    public void setShowMode(int mode) {
        if (mode == mPresenter.getState().getShowMode()) {
            return;
        }
        mPresenter.setShowMode(mode);
    }

    public void setCalendarMode(int mode) {
        if (mode == mPresenter.getState().getCalendarMode()) {
            return;
        }
        mPresenter.setCalendarMode(mode);
        // Reset height params after mode change
        mPager.setLayoutParams(generatePagerLayoutParams());
        requestLayout();
    }

    public void setTitleBarVisible(boolean visible) {
        if (visible == mPresenter.getState().isTitleBarVisible()) {
            return;
        }
        mPresenter.setTitleBarVisible(visible);
        if (!visible && mTitleBar != null) {
            mTitleBar.setVisibility(GONE);
        } else if (visible && mTitleBar == null) {
            setupTitleBar();
            addView(mTitleBar, 0, new LayoutParams(mPresenter.getProfile().getTitleHeightPx()));
        } else if (visible) {
            mTitleBar.setVisibility(VISIBLE);
        }
        requestLayout();
    }

    public void setAllowClickOther(boolean allow) {
        mPresenter.setAllowClickOther(allow);
    }

    /**
     * Enable or disable the ability to swipe between months.
     *
     * @param enable pass false to disable paging, true to enable (default)
     */
    public void setPagingEnable(boolean enable){
        mPresenter.setPagingEnable(enable);
        mPager.setPagingEnabled(enable);
    }

    public void setFirstDayOfWeek(int firstDayOfWeek){
        if (firstDayOfWeek == mPresenter.getState().getFirstDayOfWeek()) {
            return;
        }
        mPresenter.setFirstDayOfWeek(firstDayOfWeek);
        mWeekBar.setFirstDayOfWeek(firstDayOfWeek);
        mWeekBar.invalidate();
    }

    public List<CalendarInfo> getSelectedDates() {
        return mPresenter.getState().getSelected();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        return mPresenter.getState().saveState(super.onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Parcelable s) {
        super.onRestoreInstanceState(s);
        mPresenter.getState().restoreState(s);
        mPresenter.createPagerAdapter(mPager);
        // Reset height params after mode change
        mPager.setLayoutParams(generatePagerLayoutParams());
        requestLayout();
    }

    @Override
    protected void dispatchSaveInstanceState(@NonNull SparseArray<Parcelable> container) {
        dispatchFreezeSelfOnly(container);
    }

    @Override
    protected void dispatchRestoreInstanceState(@NonNull SparseArray<Parcelable> container) {
        dispatchThawSelfOnly(container);
    }

    /*
     * Listener/Callback Code
     */

    /**
     * Sets the listener to be notified upon selected mDate changes.
     *
     * @param listener thing to be notified
     */
    public void setOnDateSelectedListener(OnDateSelectedListener listener) {
        this.dateListener = listener;
    }

    /**
     * Sets the listener to be notified upon month changes.
     *
     * @param listener thing to be notified
     */
    public void setOnPageSwitchListener(OnPageSwitchedListener listener) {
        this.pageListener = listener;
    }

    /**
     * Sets the listener to be notified upon a range has been selected.
     *
     * @param listener thing to be notified
     */
    public void setOnRangeSelectedListener(OnRangeSelectedListener listener) {
        this.rangeListener = listener;
    }

    /**
     * Add listener to the title or null to remove it.
     *
     * @param listener Listener to be notified.
     */
    public void setOnTitleClickListener(final OnClickListener listener) {
        mTitleBar.setOnTitleClickListener(listener);
    }

    /**
     * Dispatch mDate change events to a listener, if set
     *
     * @param day      the day that was selected
     * @param selected true if the day is now currently selected, false otherwise
     */
    public void callbackOnDateSelected(final CalendarInfo day, final boolean selected) {
        if (dateListener != null) {
            dateListener.onDateSelected(this, day, selected);
        }
    }

    /**
     * Dispatch a range of days to a listener, if set. First day must be before last Day.
     *
     * @param days a range of days.
     */
    public void callbackOnRangeSelected(List<CalendarInfo> days) {
        if (rangeListener != null) {
            rangeListener.onRangeSelected(this, days);
        }
    }

    /**
     * Dispatch mDate change events to a listener, if set
     *
     * @param day first day of the new month
     */
    public void callbackOnPageSwitched(final CalendarInfo day) {
        if (pageListener != null) {
            pageListener.onPageSwitched(this, day);
        }
    }

    /*
     * Custom ViewGroup Code
     */

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        //We need to disregard padding for a while. This will be added back later
        final int specWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int desiredWidth = specWidthSize - getPaddingLeft() - getPaddingRight();

        //Calculate independent cell sizes for later
        CalendarState state = mPresenter.getState();
        final int weekCount = state.getWeekCount();
        boolean showLunar = (state.getShowMode() & CalendarState.SHOW_LUNAR) != 0;
        float heightWidthRatio = showLunar ? 0.8f : 0.7f;
        int desiredCellWidth = desiredWidth / CalendarState.DEFAULT_DAYS_IN_WEEK;

        int heightTotal = 0;
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            ViewGroup.LayoutParams lp = child.getLayoutParams();
            // Check child view is pager & the height of pager is auto measured or not.
            if (lp.height == CalendarProfile.AUTO_CELL_DIMENSION) {
                lp.height = (int) (desiredCellWidth * heightWidthRatio) * weekCount;
            }
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            heightTotal += child.getMeasuredHeight();
        }

        //Put padding back in from when we took it away
        int measuredHeight = heightTotal + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(specWidthSize, clampSize(measuredHeight, heightMeasureSpec));
    }

    /**
     * Clamp the size to the measure spec.
     *
     * @param size Size we want to be
     * @param spec Measure spec to clamp against
     * @return the appropriate size to pass to {@linkplain View#setMeasuredDimension(int, int)}
     */
    private static int clampSize(int size, int spec) {
        int specMode = MeasureSpec.getMode(spec);
        int specSize = MeasureSpec.getSize(spec);
        switch (specMode) {
            case MeasureSpec.EXACTLY: {
                return specSize;
            }
            case MeasureSpec.AT_MOST: {
                return Math.min(size, specSize);
            }
            case MeasureSpec.UNSPECIFIED:
            default: {
                return size;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int count = getChildCount();
        final int parentLeft = getPaddingLeft();
        final int parentWidth = right - left - parentLeft - getPaddingRight();

        int childTop = getPaddingTop();

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }

            final int width = child.getMeasuredWidth();
            final int height = child.getMeasuredHeight();

            int delta = (parentWidth - width) / 2;
            int childLeft = parentLeft + delta;

            child.layout(childLeft, childTop, childLeft + width, childTop + height);

            childTop += height;
        }
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(WRAP_CONTENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p.height);
    }

    /**
     * Simple layout params for MaterialCalendarView. The only variation for layout is height.
     */
    private static class LayoutParams extends FrameLayout.LayoutParams {

        /**
         * Create a layout that matches parent width, and the specified height in pixel.
         *
         * @param height view height in pixel
         */
        LayoutParams(int height) {
            super(MATCH_PARENT, height);
        }

    }

}
