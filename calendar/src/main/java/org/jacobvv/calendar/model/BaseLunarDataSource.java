package org.jacobvv.calendar.model;

import android.content.Context;
import android.support.annotation.MainThread;

/**
 * Main entry point for accessing lunar calendar data.
 * <p>
 * Created by hui.yin on 2017/7/4.
 */

public interface BaseLunarDataSource {

    interface LoadLunarCallback {

        void onLunarLoaded();

        void onLunarComplete();

        void onHuangLiLoaded();

        void onHuangLiComplete();

        void onLunarLoadFailed(String msg);
    }

    class LoadLunarAdapter implements LoadLunarCallback {

        @Override
        public void onLunarLoaded() {
        }

        @Override
        public void onLunarComplete() {
        }

        @Override
        public void onHuangLiLoaded() {
        }

        @Override
        public void onHuangLiComplete() {
        }

        @Override
        public void onLunarLoadFailed(String msg) {
        }
    }

    interface GetLunarCallback {

        void onLunarLoaded(LunarInfo info);

        void onDataNotAvailable();
    }

    interface GetHuangLiCallback {

        void onLunarLoaded(HuangLiInfo info);

        void onDataNotAvailable();
    }

    @MainThread
    void loadLunar(Context context, LoadLunarCallback callback);

    void getLunar(BaseInfo solar, GetLunarCallback callback);

    void getHuangLi(BaseInfo solar, GetHuangLiCallback callback);

}
