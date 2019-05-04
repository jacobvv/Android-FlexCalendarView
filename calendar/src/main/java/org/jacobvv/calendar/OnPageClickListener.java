package org.jacobvv.calendar;

import android.support.v4.view.ViewPager;

/**
 * Created by jacob on 17-7-18.
 */

public class OnPageClickListener {
    private ViewPager mPager;

    OnPageClickListener(ViewPager pager) {
        mPager = pager;
    }

    void onPrevious() {
        mPager.setCurrentItem(mPager.getCurrentItem() - 1, true);
    }

    void onNext() {
        mPager.setCurrentItem(mPager.getCurrentItem() + 1, true);
    }
}
