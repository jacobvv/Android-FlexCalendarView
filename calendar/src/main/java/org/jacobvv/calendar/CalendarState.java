package org.jacobvv.calendar;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import org.jacobvv.calendar.model.CalendarInfo;
import org.jacobvv.calendar.util.Utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * State for CalendarWidget
 *
 * Created by hui.yin on 2017/6/2.
 */

public final class CalendarState {

    public static final int DEFAULT_DAYS_IN_WEEK = 7;
    public static final int DEFAULT_MAX_WEEKS = 6;

    /**
     * Show dates of a month per page.
     */
    public static final int MODE_MONTH = 0;

    /**
     * Show dates of a week per page.
     */
    public static final int MODE_WEEK = 1;

    /**
     * Show dates from the proceeding and successive months, in a secondary state.
     */
    public static final int SHOW_OTHER = 1;

    public static final int SHOW_LUNAR = 2;

    /**
     * TODO: not achieve yet.
     */
    public static final int SHOW_HOLIDAY = 4;

    public static final int SHOW_DEFAULT = SHOW_OTHER | SHOW_LUNAR | SHOW_HOLIDAY;

    public static final int SHOW_SUB_MASK = 6;

    private static WeakReference<Context> sContext;

    private CalendarProfile profile;

    private int showMode;
    private int calendarMode;

    private boolean isTitleBarVisible;
    private boolean allowClickOthers;
    private boolean isPagingEnable;
    private int firstDayOfWeek;

    private CalendarInfo minDate;
    private CalendarInfo maxDate;
    private CalendarInfo currentPageItem;
    private CalendarInfo lastClickDate;
    private List<CalendarInfo> selectedDates = new ArrayList<>();

    @SuppressWarnings("WrongConstant")
    CalendarState(Context context) {
        profile = new CalendarProfile(context);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, Utils.getYear(c) - 200);
        this.minDate = CalendarInfo.from(c);
        c.set(Calendar.YEAR, Utils.getYear(c) + 400);
        this.maxDate = CalendarInfo.from(c);
        currentPageItem = CalendarInfo.TODAY;
    }

    void setShowMode(int showMode) {
        this.showMode = showMode;
    }

    void setCalendarMode(int calendarMode) {
        this.calendarMode = calendarMode;
    }

    void setTitleBarVisible(boolean titleBarVisible) {
        this.isTitleBarVisible = titleBarVisible;
    }

    void setAllowClickOthers(boolean allowClickOthers) {
        this.allowClickOthers = allowClickOthers;
    }

    void setPagingEnable(boolean pagingEnable) {
        isPagingEnable = pagingEnable;
    }

    void setFirstDayOfWeek(int firstDayOfWeek) {
        this.firstDayOfWeek = firstDayOfWeek;
    }

    void setCurrentPageItem(CalendarInfo currentPageItem) {
        this.currentPageItem = currentPageItem;
    }

    void setLastClickDate(CalendarInfo info) {
        this.lastClickDate = info;
    }

    void addSelected(CalendarInfo date) {
        if (!selectedDates.contains(date)) {
            selectedDates.add(date);
        }
    }

    void removeSelected(CalendarInfo date) {
        if (selectedDates.contains(date)) {
            selectedDates.remove(date);
        }
    }

    void cleanSelected() {
        this.selectedDates.clear();
    }

    public CalendarProfile getProfile() {
        return profile;
    }

    public int getShowMode() {
        return showMode;
    }

    public int getCalendarMode() {
        return calendarMode;
    }

    public boolean isTitleBarVisible() {
        return isTitleBarVisible;
    }

    public boolean allowClickOthers() {
        return allowClickOthers;
    }

    public boolean isPagingEnable() {
        return isPagingEnable;
    }

    public int getFirstDayOfWeek() {
        return firstDayOfWeek;
    }

    public CalendarInfo getCurrentPageItem() {
        return currentPageItem;
    }

    public CalendarInfo getLastClickDate() {
        return lastClickDate;
    }

    public List<CalendarInfo> getSelected() {
        return Collections.unmodifiableList(selectedDates);
    }

    public CalendarInfo getMinDate() {
        return minDate;
    }

    public CalendarInfo getMaxDate() {
        return maxDate;
    }

    public int getWeekCount() {
        int weekCount = 0;
        switch (calendarMode) {
            case MODE_MONTH:
                weekCount = DEFAULT_MAX_WEEKS;
                break;
            case MODE_WEEK:
                weekCount = 1;
                break;
        }
        return weekCount;
    }

    // Apply state from saved state restored by view.
    private void applyFrom(SavedState state) {
        this.profile = state.profile;
        this.showMode = state.showMode;
        this.calendarMode = state.calendarMode;
        this.isTitleBarVisible = state.titleBarVisible;
        this.allowClickOthers = state.allowClickOthers;
        this.isPagingEnable = state.isPagingEnable;
        this.firstDayOfWeek = state.firstDayOfWeek;
        this.minDate = state.minDate;
        this.maxDate = state.maxDate;
        this.currentPageItem = state.currentPageItem;
        this.lastClickDate = state.lastClickDate;
        this.selectedDates = state.selectedDates;
    }

    /**
     * Only called by {@link CalendarWidget#onSaveInstanceState()} generate saved state to save.
     *
     * @param baseState state of view
     * @return saved state of calendar
     */
    Parcelable saveState(Parcelable baseState) {
        return new SavedState(baseState, this);
    }

    /**
     * Only called by {@link CalendarWidget#onRestoreInstanceState(Parcelable)} restore saved state
     * to restore.
     *
     * @param state state to restore
     */
    void restoreState(Parcelable state) {
        if (state instanceof SavedState) {
            SavedState ss = (SavedState) state;
            applyFrom(ss);
        }
    }

    private static class SavedState extends View.BaseSavedState {

        final private CalendarProfile profile;

        final private int showMode;
        final private int calendarMode;

        final private boolean titleBarVisible;
        final private boolean allowClickOthers;
        final private boolean isPagingEnable;
        final private int firstDayOfWeek;

        final private CalendarInfo minDate;
        final private CalendarInfo maxDate;
        final private CalendarInfo currentPageItem;
        final private CalendarInfo lastClickDate;
        final private List<CalendarInfo> selectedDates;

        SavedState(Parcelable in, CalendarState state) {
            super(in);
            this.profile = state.profile;
            this.showMode = state.showMode;
            this.calendarMode = state.calendarMode;
            this.titleBarVisible = state.isTitleBarVisible;
            this.allowClickOthers = state.allowClickOthers;
            this.isPagingEnable = state.isPagingEnable;
            this.firstDayOfWeek = state.firstDayOfWeek;
            this.minDate = state.minDate;
            this.maxDate = state.maxDate;
            this.currentPageItem = state.currentPageItem;
            this.lastClickDate = state.lastClickDate;
            this.selectedDates = state.selectedDates;
        }

        @SuppressWarnings("WrongConstant")
        SavedState(Parcel in) {
            super(in);
            profile = in.readParcelable(CalendarProfile.class.getClassLoader());
            showMode = in.readInt();
            calendarMode = in.readInt();
            titleBarVisible = in.readByte() != 0;
            allowClickOthers = in.readByte() != 0;
            isPagingEnable = in.readByte() != 0;
            firstDayOfWeek = in.readInt();
            minDate = in.readParcelable(CalendarInfo.class.getClassLoader());
            maxDate = in.readParcelable(CalendarInfo.class.getClassLoader());
            currentPageItem = in.readParcelable(CalendarInfo.class.getClassLoader());
            lastClickDate = in.readParcelable(CalendarInfo.class.getClassLoader());
            selectedDates = in.createTypedArrayList(CalendarInfo.CREATOR);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeParcelable(profile, flags);
            dest.writeInt(showMode);
            dest.writeInt(calendarMode);
            dest.writeByte((byte) (titleBarVisible ? 1 : 0));
            dest.writeByte((byte) (allowClickOthers ? 1 : 0));
            dest.writeByte((byte) (isPagingEnable ? 1 : 0));
            dest.writeInt(firstDayOfWeek);
            dest.writeParcelable(minDate, flags);
            dest.writeParcelable(maxDate, flags);
            dest.writeParcelable(currentPageItem, flags);
            dest.writeParcelable(lastClickDate, flags);
            dest.writeTypedList(selectedDates);
        }
    }

}
