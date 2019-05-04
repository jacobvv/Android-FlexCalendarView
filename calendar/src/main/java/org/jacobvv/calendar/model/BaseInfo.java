package org.jacobvv.calendar.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base mDate info for each calendar.
 *
 * Created by hui.yin on 2017/6/20.
 */

public class BaseInfo implements Parcelable {
    protected final int year;
    protected final int month;
    protected final int day;
    protected final boolean isLeap;
    protected final ArrayList<String> mHoliday;

    public BaseInfo(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.isLeap = false;
        this.mHoliday = new ArrayList<>();
    }

    public BaseInfo(int year, int month, int day, boolean isLeap) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.isLeap = isLeap;
        this.mHoliday = new ArrayList<>();
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public boolean isLeap(){
        return isLeap;
    }

    public List<String> getHoliday() {
        return Collections.unmodifiableList(mHoliday);
    }

    public boolean isBefore(BaseInfo other) {
        if (other == null) {
            return false;
        }
        if (year != other.year) {
            return year < other.year;
        } else if (month != other.month) {
            return month < other.month;
        } else {
            return day < other.day;
        }
    }

    public boolean isAfter(BaseInfo other) {
        if (other == null) {
            return true;
        }
        if (year != other.year) {
            return year > other.year;
        } else if (month != other.month) {
            return month > other.month;
        } else {
            return day > other.day;
        }
    }

    public boolean inRange(@Nullable BaseInfo minDate, @Nullable BaseInfo maxDate) {
        return (minDate == null || minDate.isBefore(this)) &&
                (maxDate == null || maxDate.isAfter(this));
    }

    public int toDateCode() {
        return year * 10000 + month * 100 + day;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseInfo baseInfo = (BaseInfo) o;

        return year == baseInfo.year && month == baseInfo.month && day == baseInfo.day;

    }

    @Override
    public int hashCode() {
        int result = year;
        result = 31 * result + month;
        result = 31 * result + day;
        return result;
    }

    @Override
    public String toString() {
        return "BaseInfo{" +
                "year=" + year +
                ", month=" + month +
                ", day=" + day +
                '}';
    }

    private BaseInfo(Parcel in) {
        year = in.readInt();
        month = in.readInt();
        day = in.readInt();
        isLeap = in.readByte() != 0;
        mHoliday = in.createStringArrayList();
    }

    public static final Creator<BaseInfo> CREATOR = new Creator<BaseInfo>() {
        @Override
        public BaseInfo createFromParcel(Parcel in) {
            return new BaseInfo(in);
        }

        @Override
        public BaseInfo[] newArray(int size) {
            return new BaseInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(year);
        dest.writeInt(month);
        dest.writeInt(day);
        dest.writeByte((byte) (isLeap ? 1 : 0));
        dest.writeStringList(mHoliday);
    }
}
