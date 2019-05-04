package org.jacobvv.calendar.page;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import org.jacobvv.calendar.CalendarContract;
import org.jacobvv.calendar.CalendarProfile;
import org.jacobvv.calendar.CalendarState;
import org.jacobvv.calendar.formatter.DateFormatter;
import org.jacobvv.calendar.model.CalendarInfo;

import java.util.Calendar;
import java.util.List;

/**
 * Page view in view pager of calendar.
 * <p>
 * Created by hui.yin on 2017/5/24.
 */
public abstract class CalendarPage extends View {

    private static final String TAG = "CalendarPage";
    private static final Calendar _calendar = Calendar.getInstance();
    private static final int[] COLOR_STATE_DISABLE = new int[]{-android.R.attr.state_enabled};
    private static final int[] COLOR_STATE_PRESSED = new int[]{android.R.attr.state_pressed};

    protected CalendarContract.Presenter mCallback;

    protected CalendarInfo mFirstDay;
    protected CalendarInfo mDate;
    protected CalendarInfo[][] mDays;
    protected String[][] mDaysText;
    protected String[][] mSubText;

    protected DateFormatter mDayFormatter = DateFormatter.DEFAULT_DAY_FIRST;
    protected DateFormatter mLunarFormatter = DateFormatter.DEFAULT_DAY_SECOND;

    protected boolean mIsShowSub;
    protected boolean mIsShowOther;

    private GestureDetectorCompat mGesture = new GestureDetectorCompat(getContext(),
            new GestureListener());

    private ColorStateList mDayTextColor;
    private ColorStateList mSubTextColor;
    private ColorStateList mTodayTextColor;
    private int mSelectedColor;

    private int mDayTextSizePx;
    private int mSubTextSizePx;
    private int mCellWidthPx;
    private int mCellHeightPx;
    private int mSelectedCircleRadius;

    private Paint mDayPaint;
    private Paint mSubPaint;
    private Paint mBgPaint;

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            int x = (int) e.getX();
            int y = (int) e.getY();
            int col = Math.min(x / mCellWidthPx, CalendarState.DEFAULT_DAYS_IN_WEEK);
            int row = Math.min(y / mCellHeightPx, getRows());
            CalendarInfo info = mDays[row][col];
            mCallback.onDateClicked(info);
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

    }

    public CalendarPage(@NonNull CalendarContract.Presenter callback, CalendarInfo date,
                        CalendarInfo firstDay) {
        super(callback.getContext());
        mCallback = callback;
        mFirstDay = firstDay;
        mDate = date;
        initShowMode();

        initAttr();
        initPaint();
        initDay();
    }

    private void initAttr() {
        CalendarProfile profile = mCallback.getProfile();
        mDayTextColor = profile.getDayTextColor();
        mSubTextColor = profile.getSubTextColor();
        mTodayTextColor = profile.getTodayTextColor();
        mSelectedColor = profile.getSelectedColor();

        mDayTextSizePx = profile.getDayTextSizePx();
        mSubTextSizePx = profile.getSubTextSizePx();
    }

    private void initPaint() {
        mDayPaint = new Paint();
        mDayPaint.setAntiAlias(true);
        mDayPaint.setTextSize(mDayTextSizePx);

        mSubPaint = new Paint();
        mSubPaint.setAntiAlias(true);
        mSubPaint.setTextSize(mSubTextSizePx);

        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);
        mBgPaint.setColor(mSelectedColor);
    }

    private void initSize(int width, int height) {
        mCellWidthPx = width / CalendarState.DEFAULT_DAYS_IN_WEEK;
        mCellHeightPx = height / getRows();
        mSelectedCircleRadius = Math.min(mCellWidthPx, mCellHeightPx) / 2;
    }

    public void initDay() {
        mFirstDay.getCalendar(_calendar);
        int row = getRows();
        mDays = new CalendarInfo[row][CalendarState.DEFAULT_DAYS_IN_WEEK];
        mDaysText = new String[row][CalendarState.DEFAULT_DAYS_IN_WEEK];
        mSubText = new String[row][CalendarState.DEFAULT_DAYS_IN_WEEK];
        for (int r = 0; r < row; r++) {
            for (int c = 0; c < CalendarState.DEFAULT_DAYS_IN_WEEK; c++) {
                CalendarInfo day = CalendarInfo.from(_calendar);
                mDays[r][c] = day;
                mDaysText[r][c] = mDayFormatter.format(getContext(), day).toString();
                mSubText[r][c] = mLunarFormatter.format(getContext(), day).toString();
                _calendar.add(Calendar.DATE, 1);
            }
        }
    }

    protected CalendarInfo getDate() {
        return mDate;
    }

    /**
     * Return the number of rows to display per page
     *
     * @return Rows per page
     */
    protected abstract int getRows();

    protected abstract int atPage(CalendarInfo info);

    protected abstract void initShowMode();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        Log.d(TAG, "x=" + x + ", y=" + y);
        return mGesture.onTouchEvent(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        initSize(getWidth(), getHeight());
        int row = getRows();
        List<CalendarInfo> selected = mCallback.getState().getSelected();
        for (int r = 0; r < row; r++) {
            for (int c = 0; c < CalendarState.DEFAULT_DAYS_IN_WEEK; c++) {
                CalendarInfo info = mDays[r][c];
                // Draw day text or not.
                if (!mIsShowOther && atPage(info) != 0) {
                    continue;
                }
                // Set paint color.
                if (info.equals(CalendarInfo.TODAY)) {
                    mDayPaint.setColor(mTodayTextColor.getDefaultColor());
                    mSubPaint.setColor(mTodayTextColor.getDefaultColor());
                    mDayPaint.setFakeBoldText(true);
                    mSubPaint.setFakeBoldText(true);
                } else if (atPage(info) == 0) {
                    mDayPaint.setColor(mDayTextColor.getDefaultColor());
                    mSubPaint.setColor(mSubTextColor.getDefaultColor());
                    mDayPaint.setFakeBoldText(false);
                    mSubPaint.setFakeBoldText(false);
                } else if (mIsShowOther) {
                    mDayPaint.setColor(mDayTextColor.getColorForState(COLOR_STATE_DISABLE,
                            Color.GRAY));
                    mSubPaint.setColor(mSubTextColor.getColorForState(COLOR_STATE_DISABLE,
                            Color.GRAY));
                    mDayPaint.setFakeBoldText(false);
                    mSubPaint.setFakeBoldText(false);
                }
                // Calculate coordinate of cell.
                int startCellX = mCellWidthPx * c;
                int startCellY = mCellHeightPx * r;
                int endCellX = startCellX + mCellWidthPx;
                int endCellY = startCellY + mCellHeightPx;
                // Draw circle background.
                if (selected.contains(info)) {
                    canvas.drawCircle((startCellX + endCellX) / 2, (startCellY + endCellY) / 2,
                            mSelectedCircleRadius, mBgPaint);
                    if (info.equals(CalendarInfo.TODAY)) {
                        mDayPaint.setColor(mTodayTextColor.getColorForState(COLOR_STATE_PRESSED,
                                Color.WHITE));
                        mSubPaint.setColor(mTodayTextColor.getColorForState(COLOR_STATE_PRESSED,
                                Color.WHITE));
                    } else {
                        mDayPaint.setColor(mDayTextColor.getColorForState(COLOR_STATE_PRESSED,
                                Color.WHITE));
                        mSubPaint.setColor(mSubTextColor.getColorForState(COLOR_STATE_PRESSED,
                                Color.WHITE));
                    }
                }
                // Draw day text.
                float offsetRatio = mIsShowSub ? 0.35f : 0.5f;
                String dayText = mDaysText[r][c];
                int offsetX = (int) ((mCellWidthPx - mDayPaint.measureText(dayText)) / 2);
                int offsetY = (int) (mCellHeightPx * offsetRatio -
                        (mDayPaint.ascent() + mDayPaint.descent()) / 2);
                canvas.drawText(dayText, startCellX + offsetX, startCellY + offsetY, mDayPaint);
                // Draw lunar day text.
                if (mIsShowSub) {
                    String lunarText = mSubText[r][c];
                    offsetX = (int) ((mCellWidthPx - mSubPaint.measureText(lunarText)) / 2);
                    offsetY = (int) (mCellHeightPx * 0.7 -
                            (mSubPaint.ascent() + mSubPaint.descent()) / 2);
                    canvas.drawText(lunarText, startCellX + offsetX, startCellY + offsetY, mSubPaint);
                }
            }
        }
    }
}
