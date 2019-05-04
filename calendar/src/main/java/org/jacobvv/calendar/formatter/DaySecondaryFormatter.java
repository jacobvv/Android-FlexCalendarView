package org.jacobvv.calendar.formatter;

import android.content.Context;
import android.text.TextUtils;
import android.util.LruCache;

import org.jacobvv.calendar.model.BaseInfo;
import org.jacobvv.calendar.model.CalendarInfo;

import static org.jacobvv.calendar.model.LunarCalendarProvider.LUNAR_DAY_NUM;
import static org.jacobvv.calendar.model.LunarCalendarProvider.LUNAR_LEAP;
import static org.jacobvv.calendar.model.LunarCalendarProvider.LUNAR_MONTH;
import static org.jacobvv.calendar.model.LunarCalendarProvider.LUNAR_MONTH_NUM;


/**
 * Created by hui.yin on 2017/5/23.
 */

public class DaySecondaryFormatter implements DateFormatter {

    private static final LruCache<Integer, String> cache = new LruCache<>(1024);

    @Override
    public CharSequence format(Context context, CalendarInfo day) {
        String text = cache.get(day.getSolar().toDateCode());
        if (!TextUtils.isEmpty(text)) {
            return text;
        }
        BaseInfo lunar = day.getLunar();
        if (lunar == null) {
            return "";
        }
        if (lunar.getHoliday().size() > 0) {
            text = lunar.getHoliday().get(0);
        } else if (day.getSolar().getHoliday().size() > 0) {
            text = day.getSolar().getHoliday().get(0);
        } else if (lunar.getDay() == 1) {
            text = getMonthName(lunar);
        } else {
            text = getDayName(lunar);
        }
        cache.put(day.getSolar().toDateCode(), text);
        return text;
    }

    private String getMonthName(BaseInfo lunar) {
        if (lunar.isLeap()) {
            return LUNAR_LEAP + LUNAR_MONTH_NUM[lunar.getMonth() - 1] + LUNAR_MONTH;
        } else {
            return LUNAR_MONTH_NUM[lunar.getMonth() - 1] + LUNAR_MONTH;
        }
    }

    private String getDayName(BaseInfo lunar) {
        return LUNAR_DAY_NUM[lunar.getDay() - 1];
    }

}
