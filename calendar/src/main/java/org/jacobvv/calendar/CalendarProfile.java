package org.jacobvv.calendar;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Profile for CalendarWidget
 * <p>
 * Created by hui.yin on 2017/5/26.
 */
public final class CalendarProfile implements Parcelable {

    static final int AUTO_CELL_DIMENSION = -10;

    private int titleHeightPx;
    private int weekHeightPx;
    private int cellHeightPx;

    private int titleTextSizePx;
    private int weekTextSizePx;
    private int dayTextSizePx;
    private int subTextSizePx;

    private int selectedColor;
    private int arrowColor;
    private ColorStateList titleTextColor;
    private ColorStateList weekTextColor;
    private ColorStateList dayTextColor;
    private ColorStateList subTextColor;
    private ColorStateList todayTextColor;

    private Drawable leftArrowMask;
    private Drawable rightArrowMask;

    CalendarProfile(Context context) {
    }

    void setTitleHeightPx(int titleHeightPx) {
        this.titleHeightPx = titleHeightPx;
    }

    void setWeekHeightPx(int weekHeightPx) {
        this.weekHeightPx = weekHeightPx;
    }

    void setCellHeightPx(int cellHeightPx) {
        this.cellHeightPx = cellHeightPx;
    }

    void setTitleTextSizePx(int titleTextSizePx) {
        this.titleTextSizePx = titleTextSizePx;
    }

    void setWeekTextSizePx(int weekTextSizePx) {
        this.weekTextSizePx = weekTextSizePx;
    }

    void setDayTextSizePx(int dayTextSizePx) {
        this.dayTextSizePx = dayTextSizePx;
    }

    void setSubTextSizePx(int subTextSizePx) {
        this.subTextSizePx = subTextSizePx;
    }

    void setSelectedColor(int selectedColor) {
        this.selectedColor = selectedColor;
    }

    void setArrowColor(int arrowColor) {
        this.arrowColor = arrowColor;
    }

    void setTitleTextColor(ColorStateList titleTextColor) {
        this.titleTextColor = titleTextColor;
    }

    void setWeekTextColor(ColorStateList weekTextColor) {
        this.weekTextColor = weekTextColor;
    }

    void setDayTextColor(ColorStateList dayTextColor) {
        this.dayTextColor = dayTextColor;
    }

    void setSubTextColor(ColorStateList subTextColor) {
        this.subTextColor = subTextColor;
    }

    void setTodayTextColor(ColorStateList todayTextColor) {
        this.todayTextColor = todayTextColor;
    }

    public int getTitleHeightPx() {
        return titleHeightPx;
    }

    public int getWeekHeightPx() {
        return weekHeightPx;
    }

    public int getCellHeightPx() {
        return cellHeightPx;
    }

    public int getTitleTextSizePx() {
        return titleTextSizePx;
    }

    public int getWeekTextSizePx() {
        return weekTextSizePx;
    }

    public int getDayTextSizePx() {
        return dayTextSizePx;
    }

    public int getSubTextSizePx() {
        return subTextSizePx;
    }

    public int getSelectedColor() {
        return selectedColor;
    }

    public int getArrowColor() {
        return arrowColor;
    }

    public ColorStateList getTitleTextColor() {
        return titleTextColor;
    }

    public ColorStateList getWeekTextColor() {
        return weekTextColor;
    }

    public ColorStateList getDayTextColor() {
        return dayTextColor;
    }

    public ColorStateList getSubTextColor() {
        return subTextColor;
    }

    public ColorStateList getTodayTextColor() {
        return todayTextColor;
    }

    void setLeftArrowMask(Drawable d) {
        leftArrowMask = d;
    }

    void setRightArrowMask(Drawable d) {
        rightArrowMask = d;
    }

    public Drawable getLeftArrowMask() {
        return leftArrowMask;
    }

    public Drawable getRightArrowMask() {
        return rightArrowMask;
    }

    private CalendarProfile(Parcel in) {
        titleHeightPx = in.readInt();
        weekHeightPx = in.readInt();
        cellHeightPx = in.readInt();
        titleTextSizePx = in.readInt();
        weekTextSizePx = in.readInt();
        dayTextSizePx = in.readInt();
        subTextSizePx = in.readInt();
        selectedColor = in.readInt();
        arrowColor = in.readInt();
        titleTextColor = in.readParcelable(ColorStateList.class.getClassLoader());
        weekTextColor = in.readParcelable(ColorStateList.class.getClassLoader());
        dayTextColor = in.readParcelable(ColorStateList.class.getClassLoader());
        subTextColor = in.readParcelable(ColorStateList.class.getClassLoader());
        todayTextColor = in.readParcelable(ColorStateList.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(titleHeightPx);
        dest.writeInt(weekHeightPx);
        dest.writeInt(cellHeightPx);
        dest.writeInt(titleTextSizePx);
        dest.writeInt(weekTextSizePx);
        dest.writeInt(dayTextSizePx);
        dest.writeInt(subTextSizePx);
        dest.writeInt(selectedColor);
        dest.writeInt(arrowColor);
        dest.writeParcelable(titleTextColor, flags);
        dest.writeParcelable(weekTextColor, flags);
        dest.writeParcelable(dayTextColor, flags);
        dest.writeParcelable(subTextColor, flags);
        dest.writeParcelable(todayTextColor, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CalendarProfile> CREATOR = new Creator<CalendarProfile>() {
        @Override
        public CalendarProfile createFromParcel(Parcel in) {
            return new CalendarProfile(in);
        }

        @Override
        public CalendarProfile[] newArray(int size) {
            return new CalendarProfile[size];
        }
    };

}
