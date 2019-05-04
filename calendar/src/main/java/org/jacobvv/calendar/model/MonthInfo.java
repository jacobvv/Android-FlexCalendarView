package org.jacobvv.calendar.model;

/**
 * Created by hui.yin on 2017/7/3.
 */

class MonthInfo extends BaseInfo {

    public MonthInfo(int year, int month) {
        super(year, month, 0);
    }

    @Override
    public boolean isLeap() {
        return false;
    }
}
