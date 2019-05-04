package org.jacobvv.calendar.util;

import android.content.Context;

/**
 * Created by hui.yin on 2017/6/12.
 */

public interface BaseView<T extends BasePresenter> {
    Context getContext();
    void setPresenter(T presenter);
}
