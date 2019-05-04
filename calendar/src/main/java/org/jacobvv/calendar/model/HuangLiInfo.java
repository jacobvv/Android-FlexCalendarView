package org.jacobvv.calendar.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hui.yin on 2017/7/6.
 */

public class HuangLiInfo {
    private static final List<String> mLunarHuangLiMeta = new ArrayList<>();
    private final List<Byte> mYi = new ArrayList<>();
    private final List<Byte> mJi = new ArrayList<>();

    public static void clearMeta() {
        mLunarHuangLiMeta.clear();
    }

    public static void addMeta(String meta) {
        mLunarHuangLiMeta.add(meta);
    }

    public HuangLiInfo(List<Byte> yi, List<Byte> ji) {
        if (yi != null) {
            mYi.addAll(yi);
        }
        if (ji != null) {
            mJi.addAll(ji);
        }
    }

    public List<String> getYi() {
        List<String> yi = new ArrayList<>();
        for (Byte i : mYi) {
            yi.add(mLunarHuangLiMeta.get(i));
        }
        return yi;
    }

    public List<String> getJi() {
        List<String> ji = new ArrayList<>();
        for (Byte i : mJi) {
            ji.add(mLunarHuangLiMeta.get(i));
        }
        return ji;
    }

    public String yiToString() {
        StringBuilder sb = new StringBuilder();
        for (Byte i : mYi) {
            sb.append(mLunarHuangLiMeta.get(i)).append(" ");
        }
        return sb.toString().trim();
    }

    public String jiToString() {
        StringBuilder sb = new StringBuilder();
        for (Byte i : mJi) {
            sb.append(mLunarHuangLiMeta.get(i)).append(" ");
        }
        return sb.toString().trim();
    }
}
