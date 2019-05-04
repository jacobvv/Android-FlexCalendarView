package org.jacobvv.calendar.model;

import android.support.annotation.NonNull;

import org.jacobvv.calendar.util.Utils;

import java.util.ArrayList;
import java.util.Calendar;

import static org.jacobvv.calendar.model.LunarCalendarProvider.LUNAR_DAY;
import static org.jacobvv.calendar.model.LunarCalendarProvider.LUNAR_DAY_NUM;
import static org.jacobvv.calendar.model.LunarCalendarProvider.LUNAR_GAN;
import static org.jacobvv.calendar.model.LunarCalendarProvider.LUNAR_MONTH;
import static org.jacobvv.calendar.model.LunarCalendarProvider.LUNAR_MONTH_NUM;
import static org.jacobvv.calendar.model.LunarCalendarProvider.LUNAR_YEAR;
import static org.jacobvv.calendar.model.LunarCalendarProvider.LUNAR_YEAR_NUM;
import static org.jacobvv.calendar.model.LunarCalendarProvider.LUNAR_ZHI;
import static org.jacobvv.calendar.model.LunarCalendarProvider.LUNAR_ZODIAC;

/**
 * Created by hui.yin on 2017/6/20.
 */

public class LunarInfo extends BaseInfo {

    private static final String TAG = "LunarInfo";

    private static final Calendar TEMP = Calendar.getInstance();

    static {
        TEMP.clear();
        TEMP.set(1900, 1, 1);
    }

    @NonNull
    public static LunarInfo from(int year, int month, int day, boolean isLeap, BaseInfo solar,
                                 int jieqi, int jieqiYear, int jieqiMonth,
                                 ArrayList<String> holidays) {
        return new LunarInfo(year, month, day, isLeap, solar, jieqi, jieqiYear, jieqiMonth,
                holidays);
    }

    final int mJieQi;

    private String mGanZhiYear;
    private String mGanZhiMonth;
    private String mGanZhiDay;
    private String mGanZhiTime;
    private String mZodiac;

    private LunarInfo(int year, int month, int day, boolean isLeap, BaseInfo solar, int jieqi,
                      int jieqiYear, int jieqiMonth, ArrayList<String> holidays) {
        super(year, month, day, isLeap);
        mJieQi = jieqi;
        mHoliday.addAll(holidays);
        int solarYear = solar.year;
        int solarMonth = solar.month;
        int solarDay = solar.day;
        int monthIndex = solarYear * 12 + solarMonth;
        int dayIndex = Utils.intervalBetween(TEMP, Utils.fromDate(solar));
        if (solarMonth * 100 + solarDay < jieqiYear) {
            solarYear--;
        }
        if (solarDay < jieqiMonth) {
            monthIndex--;
        }
        mGanZhiYear = LUNAR_GAN[(solarYear + 6) % 10] + LUNAR_ZHI[(solarYear + 8) % 12];
        mGanZhiMonth = LUNAR_GAN[(monthIndex + 2) % 10] + LUNAR_ZHI[monthIndex % 12];
        mGanZhiDay = LUNAR_GAN[(dayIndex + 1) % 10] + LUNAR_ZHI[(dayIndex + 5) % 12];
        mZodiac = LUNAR_ZODIAC[(year - 4) % 12];
    }

    public String getDateString() {
        StringBuilder sb = new StringBuilder();
        int year = getYear();
        while (year > 0) {
            sb.insert(0, LUNAR_YEAR_NUM[year % 10]);
            year = year / 10;
        }
        sb.append(LUNAR_YEAR).append(" ");
        sb.append(LUNAR_MONTH_NUM[getMonth() - 1]).append(LUNAR_MONTH).append(" ")
                .append(LUNAR_DAY_NUM[getDay() - 1]).append(LUNAR_DAY);
        return sb.toString();
    }

    public String getBriefDateString() {
        return LUNAR_MONTH_NUM[getMonth() - 1] + LUNAR_MONTH + LUNAR_DAY_NUM[getDay() - 1];
    }

    public String getZodiacString() {
        return mZodiac + LUNAR_YEAR;
    }

    public String getGanZhiString() {
        return mGanZhiYear + LUNAR_YEAR + " " +
                mGanZhiMonth + LUNAR_MONTH + " " +
                mGanZhiDay + LUNAR_DAY;
    }
}
