package org.jacobvv.calendar.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.jacobvv.calendar.model.BaseLunarDataSource.GetLunarCallback;
import org.jacobvv.calendar.util.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * An imputable representation of a day on a calendar
 * <p>
 * Created by hui.yin on 2017/5/25.
 */

public class CalendarInfo implements Parcelable {

    public static CalendarInfo TODAY = CalendarInfo.today();

    /**
     * Get a new instance set to getCurrentDate
     *
     * @return CalendarDay set to getCurrentDate's mDate
     */
    @NonNull
    private static CalendarInfo today() {
        return from(Calendar.getInstance());
    }

    /**
     * Get a new instance set to the specified day
     *
     * @param year  new instance's year
     * @param month new instance's month as range of 1~12
     * @param day   new instance's day of month
     * @return CalendarDay set to the specified mDate
     */
    @NonNull
    public static CalendarInfo from(int year, int month, int day) {
        return new CalendarInfo(year, month, day);
    }

    /**
     * Get a new instance set to the specified day
     *
     * @param calendar {@linkplain Calendar} to pull mDate information from. Passing null will return null
     * @return CalendarDay set to the specified mDate
     */
    public static CalendarInfo from(@NonNull Calendar calendar) {
        return new CalendarInfo(
                Utils.getYear(calendar),
                Utils.getMonth(calendar),
                Utils.getDay(calendar)
        );
    }

    /**
     * Get a new instance set to the specified day
     *
     * @param date {@linkplain Date} to pull mDate information from. Passing null will return null.
     * @return CalendarDay set to the specified mDate
     */
    public static CalendarInfo from(@NonNull Date date) {
        return from(Utils.fromDate(date));
    }

    @NonNull
    private final BaseInfo mSolar;
    private LunarInfo mLunar;
    private int flag = 0;

    private List<GetLunarCallback> mCallbacks = new ArrayList<>();

    private GetLunarCallback Callback = new GetLunarCallback() {
        @Override
        public void onLunarLoaded(LunarInfo info) {
            mLunar = info;
            for (GetLunarCallback callback : mCallbacks) {
                callback.onLunarLoaded(info);
            }
            mCallbacks.clear();
        }

        @Override
        public void onDataNotAvailable() {
            for (GetLunarCallback callback : mCallbacks) {
                callback.onDataNotAvailable();
            }
            mCallbacks.clear();
        }
    };

    /**
     * Cache for calls to {@linkplain #getCalendar(Calendar)}
     */
    private transient Calendar _calendar;

    /**
     * Cache for calls to {@linkplain #getDate()}
     */
    private transient Date _date;

    private CalendarInfo(int year, int month, int day) {
        mSolar = new BaseInfo(year, month, day);
        LunarCalendarProvider.getInstance().getLunar(mSolar, Callback);
    }

    @NonNull
    public BaseInfo getSolar() {
        return mSolar;
    }

    public LunarInfo getLunar() {
        return mLunar;
    }

    public void getLunar(GetLunarCallback callback) {
        if (mLunar != null) {
            callback.onLunarLoaded(mLunar);
        } else {
            mCallbacks.add(callback);
        }
    }

    public int getFlag() {
        return 0;
    }

    public List<String> getHolidays() {
        return null;
    }

    @NonNull
    public Calendar getCalendar(Calendar cache) {
        if (cache == null) {
            if (_calendar != null) {
                cache = _calendar;
            } else {
                cache = Calendar.getInstance();
                _calendar = cache;
            }
        }
        cache.clear();
        cache.set(mSolar.getYear(), mSolar.getMonth() - 1, mSolar.getDay());
        return cache;
    }

    @NonNull
    public Date getDate() {
        if (_date == null) {
            _date = getCalendar(null).getTime();
        }
        return _date;
    }

    public boolean isBefore(CalendarInfo other) {
        return mSolar.isBefore(other.getSolar());
    }

    public boolean isAfter(CalendarInfo other) {
        return mSolar.isAfter(other.getSolar());
    }

    public boolean inRange(@NonNull CalendarInfo minDate, @NonNull CalendarInfo maxDate) {
        return mSolar.inRange(minDate.getSolar(), maxDate.getSolar());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CalendarInfo that = (CalendarInfo) o;

        return mSolar.equals(that.mSolar);

    }

    @Override
    public int hashCode() {
        return mSolar.hashCode();
    }

    protected CalendarInfo(Parcel in) {
        mSolar = in.readParcelable(BaseInfo.class.getClassLoader());
        mLunar = in.readParcelable(BaseInfo.class.getClassLoader());
        flag = in.readInt();
        if (mLunar == null) {
            LunarCalendarProvider.getInstance().getLunar(mSolar, Callback);
        }
    }

    public static final Creator<CalendarInfo> CREATOR = new Creator<CalendarInfo>() {
        @Override
        public CalendarInfo createFromParcel(Parcel in) {
            return new CalendarInfo(in);
        }

        @Override
        public CalendarInfo[] newArray(int size) {
            return new CalendarInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mSolar, flags);
        dest.writeParcelable(mLunar, flags);
        dest.writeInt(flag);
    }

}
