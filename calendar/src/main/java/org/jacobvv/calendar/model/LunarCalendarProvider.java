package org.jacobvv.calendar.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.LruCache;
import android.util.SparseArray;
import android.util.SparseIntArray;


import org.jacobvv.calendar.R;
import org.jacobvv.calendar.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Chinese lunar calendar provider.
 * <p>
 * Created by hui.yin on 2017/6/19.
 */

public final class LunarCalendarProvider implements BaseLunarDataSource {

    private final static String TAG = "LunarCalendarProvider";

    public static String LUNAR_YEAR;
    public static String[] LUNAR_YEAR_NUM;
    public static String LUNAR_MONTH;
    public static String[] LUNAR_MONTH_NUM;
    public static String LUNAR_DAY;
    public static String[] LUNAR_DAY_NUM;
    public static String LUNAR_LEAP;

    private static String HOLIDAY_CHUXI;
    private static String HOLIDAY_CHUNJIE;
    private static String HOLIDAY_YUANXIAO;
    private static String HOLIDAY_LONGTAITOU;
    private static String HOLIDAY_HANSHI;
    private static String HOLIDAY_QINGMING;
    private static String HOLIDAY_DUANWU;
    private static String HOLIDAY_QIXI;
    private static String HOLIDAY_ZHONGYUAN;
    private static String HOLIDAY_ZHONGQIU;
    private static String HOLIDAY_CHONGYANG;
    private static String HOLIDAY_XIAYUAN;
    private static String HOLIDAY_LABA;

    public static String[] LUNAR_GAN;
    public static String[] LUNAR_ZHI;
    public static String[] LUNAR_ZODIAC;
    private static String[] LUNAR_JIEQI;

    private final static String SP_LUNAR_LOADED = "sp_lunar_loaded";
    private final static String SP_HUANGLI_LOADED = "sp_huangli_loaded";

    private final static int STATE_NONE = 0;
    private final static int STATE_LOADED = 2;
    private final static int STATE_COMPLETE = 3;

    private LruCache<Integer, LunarInfo> mLunarCache = new LruCache<>(1024);
    private LruCache<Integer, HuangLiInfo> mLunarHuangLiCache = new LruCache<>(1024);

    private Handler mHandler = new Handler();
    private ArrayList<Runnable> mLunarOnLoadedCallbacks = new ArrayList<>();
    private ArrayList<Runnable> mHuangLiOnLoadedCallbacks = new ArrayList<>();

    private SparseIntArray mLunarData = new SparseIntArray();
    private SparseIntArray mLunarJieQi = new SparseIntArray();
    private SparseIntArray mLunarJieQiYear = new SparseIntArray();
    private SparseIntArray mLunarJieQiMonth = new SparseIntArray();

    private SparseArray<HuangLiInfo> mLunarHuangLi = new SparseArray<>();
    private int mStateLunar = STATE_NONE;
    private int mStateHuangLi = STATE_NONE;

    private LunarCalendarProvider() {
    }

    private static class Holder {
        private static LunarCalendarProvider INSTANCE = new LunarCalendarProvider();
    }

    public static LunarCalendarProvider getInstance() {
        return Holder.INSTANCE;
    }

    private void initData(Context context) {
        Resources res = context.getResources();
        LUNAR_YEAR = res.getString(R.string.lunar_year);
        LUNAR_YEAR_NUM = res.getStringArray(R.array.lunar_year_num);
        LUNAR_MONTH = res.getString(R.string.lunar_month);
        LUNAR_MONTH_NUM = res.getStringArray(R.array.lunar_month_num);
        LUNAR_DAY = res.getString(R.string.lunar_day);
        LUNAR_DAY_NUM = res.getStringArray(R.array.lunar_day_num);
        LUNAR_LEAP = res.getString(R.string.lunar_leap);

        LUNAR_JIEQI = res.getStringArray(R.array.lunar_jieqi);
        LUNAR_GAN = res.getStringArray(R.array.lunar_gan);
        LUNAR_ZHI = res.getStringArray(R.array.lunar_zhi);
        LUNAR_ZODIAC = res.getStringArray(R.array.lunar_zodiac);

        HOLIDAY_CHUXI = res.getString(R.string.lunar_holiday_chuxi);
        HOLIDAY_CHUNJIE = res.getString(R.string.lunar_holiday_chunjie);
        HOLIDAY_YUANXIAO = res.getString(R.string.lunar_holiday_yuanxiao);
        HOLIDAY_LONGTAITOU = res.getString(R.string.lunar_holiday_longtaitou);
        HOLIDAY_HANSHI = res.getString(R.string.lunar_holiday_hanshi);
        HOLIDAY_QINGMING = res.getString(R.string.lunar_holiday_qingming);
        HOLIDAY_DUANWU = res.getString(R.string.lunar_holiday_duanwu);
        HOLIDAY_QIXI = res.getString(R.string.lunar_holiday_qixi);
        HOLIDAY_ZHONGYUAN = res.getString(R.string.lunar_holiday_zhongyuan);
        HOLIDAY_ZHONGQIU = res.getString(R.string.lunar_holiday_zhongqiu);
        HOLIDAY_CHONGYANG = res.getString(R.string.lunar_holiday_chongyang);
        HOLIDAY_XIAYUAN = res.getString(R.string.lunar_holiday_xiayuan);
        HOLIDAY_LABA = res.getString(R.string.lunar_holiday_laba);
    }

    @Override
    @MainThread
    public void loadLunar(final Context context, final LoadLunarCallback callback) {
        initData(context);
        if (mStateLunar != STATE_NONE && mStateHuangLi != STATE_NONE) {
            return;
        }
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        mStateLunar = sp.getBoolean(SP_LUNAR_LOADED, false) ? STATE_COMPLETE : STATE_NONE;
        mStateHuangLi = sp.getBoolean(SP_HUANGLI_LOADED, false) ? STATE_COMPLETE : STATE_NONE;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mStateLunar == STATE_NONE) {
                    long time = System.currentTimeMillis();
                    loadLunarFile(context, callback);
                    Log.d(TAG, "Time of load lunar file: " + (System.currentTimeMillis() - time) + " ms");
                } else if (mStateLunar == STATE_COMPLETE) {
                    mLunarData.clear();
                    mLunarJieQi.clear();
                }
                if (mStateHuangLi == STATE_NONE) {
                    long time = System.currentTimeMillis();
                    loadHuangLiFile(context);
                    Log.d(TAG, "Time of load huangli file: " + (System.currentTimeMillis() - time) + " ms");
                } else if (mStateHuangLi == STATE_COMPLETE) {
                    mLunarHuangLi.clear();
                }
                if (mStateLunar == STATE_LOADED) {
                    saveLunar(context);
                }
                if (mStateHuangLi == STATE_LOADED) {
                    saveHuangLi(context);
                }
            }
        }).start();
    }

    @Override
    @MainThread
    public void getLunar(final BaseInfo solar,
                         final GetLunarCallback callback) {
        Runnable r = new Runnable() {
            public void run() {
                getLunar(solar, callback);
            }
        };
        if (waitUntilLunarLoaded(r)) {
            return;
        }
        // Get JieQi of given mDate.
        int jieqi = mLunarJieQi.get(solar.toDateCode(), -1);
        int jieqiYear = mLunarJieQiYear.get(solar.toDateCode() / 10000);
        int jieqiMonth = mLunarJieQiMonth.get(solar.toDateCode() / 100);
        Calendar target = Utils.fromDate(solar);
        Calendar c = (Calendar) target.clone();
        c.add(Calendar.DATE, 1);
        int jieqiNext = mLunarJieQi.get(Utils.date2int(c), -1);

        // Get raw info of given lunar year.
        int year = solar.getYear();
        int lunarBit = mLunarData.get(year);
        if (lunarBit == 0) {
            callback.onDataNotAvailable();
            return;
        }
        int leapMonth = (lunarBit & 0x78000000) >> 27;
        int monthBit = (lunarBit & 0x7FFC000) >> 14;
        int solarMon = (lunarBit & 0x3000) >> 12;
        int solarDay = (lunarBit & 0xF80) >> 7;
        Calendar springSolar = Utils.fromDate(year, solarMon, solarDay);
        if (target.before(springSolar)) {
            year = year - 1;
            lunarBit = mLunarData.get(year);
            if (lunarBit == 0) {
                callback.onDataNotAvailable();
                return;
            }
            leapMonth = (lunarBit & 0x78000000) >> 27;
            monthBit = (lunarBit & 0x7FFC000) >> 14;
            solarMon = (lunarBit & 0x3000) >> 12;
            solarDay = (lunarBit & 0xF80) >> 7;
            springSolar = Utils.fromDate(year, solarMon, solarDay);
        }
        // Calculate interval between target mDate & solar calendar mDate of last spring festival.
        int interval = Utils.intervalBetween(springSolar, target);
        // Calculate lunar calendar mDate by interval.
        int month = 1;
        boolean isLeap = false;
        boolean isChuXi = false;
        for (int i = 0; i < 13 && interval >= 0; i++) {
            boolean isBigMonth = ((monthBit >> (12 - i)) & 0x01) == 1;
            int dayOfMonth = isBigMonth ? 30 : 29;
            if (interval < dayOfMonth) {
                if (interval + 1 == dayOfMonth && month == 12) {
                    isChuXi = true;
                }
                break;
            }
            interval -= dayOfMonth;
            month++;
            if (month - 1 == leapMonth && !isLeap) {
                isLeap = true;
                month--;
            } else {
                isLeap = false;
            }
        }
        LunarInfo info = LunarInfo.from(year, month, interval + 1, isLeap, solar, jieqi,
                jieqiYear, jieqiMonth,
                getHolidays(month, interval + 1, isLeap, jieqi, jieqiNext, isChuXi));
        callback.onLunarLoaded(info);
    }

    @Override
    @MainThread
    public void getHuangLi(@NonNull final BaseInfo solar, @NonNull final GetHuangLiCallback callback) {
        Runnable r = new Runnable() {
            public void run() {
                getHuangLi(solar, callback);
            }
        };
        if (waitUntilHuangLiLoaded(r)) {
            return;
        }
        callback.onLunarLoaded(mLunarHuangLi.get(solar.toDateCode()));
    }

    private void loadLunarFile(Context context, final LoadLunarCallback callback) {
        DataInputStream dis = null;
        try {
            dis = new DataInputStream(context.getAssets().open("cal"));
            // Read lunar data.
            int size = dis.readUnsignedShort();
            byte[] buf = new byte[size];
            int len = dis.read(buf);
            if (size != len) {
                Log.e(TAG, "Data file lunar data has broken!");
                return;
            }
            loadLunarData(buf);
            loadJieQiData(dis);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mStateLunar = STATE_LOADED;
                    for (Runnable r : mLunarOnLoadedCallbacks) {
                        r.run();
                    }
                    mLunarOnLoadedCallbacks.clear();
                    callback.onLunarLoaded();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            callback.onLunarLoadFailed(e.getMessage());
        } finally {
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadHuangLiFile(Context context) {
        ZipInputStream zis = null;
        try {
            zis = new ZipInputStream(context.getAssets().open("huangli"));
            ZipEntry zipEntry;
            mLunarHuangLi.clear();
            while ((zipEntry = zis.getNextEntry()) != null) {
                if ("meta".equals(zipEntry.getName())) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(zis, "utf-8"));
                    String meta = reader.readLine();
                    loadHuangLiMeta(meta);
                } else if (zipEntry.getName().startsWith("hl")) {
                    BufferedInputStream bis = new BufferedInputStream(zis);
                    int size = Utils.readInt32(bis);
                    byte[] buf = new byte[size];
                    int len = bis.read(buf);
                    if (len != size) {
                        Log.e(TAG, "Data file of HuangLi has broken! code:hl" + zipEntry.getName() + "s2");
                        return;
                    }
                    loadHuangLiData(buf);
                }
            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mStateHuangLi = STATE_LOADED;
                    for (Runnable r : mHuangLiOnLoadedCallbacks) {
                        r.run();
                    }
                    mHuangLiOnLoadedCallbacks.clear();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zis != null) {
                try {
                    zis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void saveLunar(Context context) {

    }

    private void saveHuangLi(Context context) {

    }

    private void loadLunarData(byte[] buf) {
        mLunarData.clear();
        int len = buf.length;
        for (int i = 0; i < len; i += 6) {
            int year = Utils.byte2uint16(buf, i);
            int lunar = Utils.byte2int32(buf, i + 2);
            mLunarData.append(year, lunar);
        }
    }

    private void loadJieQiData(InputStream is) throws IOException {
        mLunarJieQi.clear();
        byte[] buf = new byte[256 * 6];
        for (int i = 0; i < 24; i++) {
            int size = Utils.readUint16(is);
            int len = is.read(buf, 0, size);
            if (size != len) {
                Log.e(TAG, "Data file JieQi part has broken!");
                return;
            }
            int index = Utils.byte2uint16(buf);
            for (int x = 2; x < len; x += 4) {
                int date = Utils.byte2int32(buf, x);
                mLunarJieQi.put(date, index);
                if (index % 2 == 0) {
                    mLunarJieQiMonth.put(date / 100, date % 100);
                }
                if (index == 0) {
                    mLunarJieQiYear.put(date / 10000, date % 10000);
                }
            }
        }
    }

    private void loadHuangLiMeta(String json) {
        HuangLiInfo.clearMeta();
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                HuangLiInfo.addMeta(array.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadHuangLiData(byte[] b) {
        int len = b.length;
        int off = 0;
        while (off < len) {
            int date = Utils.byte2int32(b, off);
            int lenYi = Utils.byte2uint8(b[off + 4]);
            int lenJi = Utils.byte2uint8(b[off + 5]);
            ArrayList<Byte> yi = new ArrayList<>();
            ArrayList<Byte> ji = new ArrayList<>();
            for (int i = off + 6; i < off + 6 + lenYi; i++) {
                yi.add(b[i]);
            }
            for (int i = off + 6 + lenYi; i < off + 6 + lenYi + lenJi; i++) {
                ji.add(b[i]);
            }
            mLunarHuangLi.put(date, new HuangLiInfo(yi, ji));
            off = off + lenYi + lenJi + 7;
        }
    }

    /**
     * If the activity is currently paused, signal that we need to run the passed Runnable
     * in onResume.
     * <p>
     * This needs to be called from incoming places where resources might have been loaded
     * while the activity is paused. That is because the Configuration (e.g., rotation)  might be
     * wrong when we're not running, and if the activity comes back to what the configuration was
     * when we were paused, activity is not restarted.
     * <p>
     * Implementation of the method from LauncherModel.Callbacks.
     *
     * @return {@code true} if we are currently paused. The caller might be able to skip some work
     */
    private boolean waitUntilLunarLoaded(Runnable run) {
        if (mStateLunar == STATE_NONE) {
            mLunarOnLoadedCallbacks.add(run);
            return true;
        } else {
            return false;
        }
    }

    private boolean waitUntilHuangLiLoaded(Runnable run) {
        if (mStateHuangLi == STATE_NONE) {
            mHuangLiOnLoadedCallbacks.add(run);
            return true;
        } else {
            return false;
        }
    }

    private ArrayList<String> getHolidays(int month, int day, boolean isLeap,
                                          int jieqi, int jieqiNext, boolean isChuXi) {
        ArrayList<String> holidays = new ArrayList<>();
        if (jieqi != -1) {
            holidays.add(LUNAR_JIEQI[jieqi]);
        }
        int code = isLeap ? 0 : month * 100 + day;
        if (isChuXi) {
            code = -1; // ChuXi
        } else if (jieqiNext == 4) {
            code = -2; // HanShi
        } else if (jieqi == 4) {
            code = -3; // QingMing
        }

        switch (code) {
            case -1:
                holidays.add(0, HOLIDAY_CHUXI);
                break;
            case 101:
                holidays.add(0, HOLIDAY_CHUNJIE);
                break;
            case 115:
                holidays.add(0, HOLIDAY_YUANXIAO);
                break;
            case 202:
                holidays.add(0, HOLIDAY_LONGTAITOU);
                break;
            case -2:
                holidays.add(0, HOLIDAY_HANSHI);
                break;
            case -3:
                holidays.add(0, HOLIDAY_QINGMING);
                break;
            case 505:
                holidays.add(0, HOLIDAY_DUANWU);
                break;
            case 707:
                holidays.add(0, HOLIDAY_QIXI);
                break;
            case 715:
                holidays.add(0, HOLIDAY_ZHONGYUAN);
                break;
            case 815:
                holidays.add(0, HOLIDAY_ZHONGQIU);
                break;
            case 909:
                holidays.add(0, HOLIDAY_CHONGYANG);
                break;
            case 1015:
                holidays.add(0, HOLIDAY_XIAYUAN);
                break;
            case 1208:
                holidays.add(0, HOLIDAY_LABA);
                break;
        }
        return holidays;
    }

}
