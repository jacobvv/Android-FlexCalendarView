package org.jacobvv.calendar;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import org.jacobvv.calendar.formatter.WeekFormatter;

/**
 * Created by jacob on 17-7-17.
 */

public class WeekBarView extends View {

    private WeekFormatter mFormatter = WeekFormatter.DEFAULT;

    private String[] mWeekText = new String[7];
    private int mTextSize;
    private ColorStateList mTextColor;

    private Paint mPaint;

    public WeekBarView(Context context, CalendarProfile profile, int firstDayOfWeek) {
        super(context);
        mTextSize = profile.getWeekTextSizePx();
        mTextColor = profile.getWeekTextColor();
        mPaint = new Paint();
        mPaint.setColor(mTextColor.getDefaultColor());
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(mTextSize);
        setFirstDayOfWeek(firstDayOfWeek);
    }

    public void setFirstDayOfWeek(int firstDayOfWeek) {
        int index = firstDayOfWeek - 1; // 0 indicate SUNDAY
        for (int i = 0; i < 7; i++) {
            mWeekText[i] = mFormatter.format(getContext(), (index % 7) + 1).toString();
            index++;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height;

        if (heightMode == MeasureSpec.EXACTLY) {
            // Parent has told us how big to be. So be it.
            height = heightSize;
        } else {
            Paint.FontMetricsInt fm = mPaint.getFontMetricsInt();
            height = fm.bottom - fm.top;
            if (heightMode == MeasureSpec.AT_MOST) {
                height = Math.min(height, heightSize);
            }
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        int columnWidth = width / 7;
        for (int i = 0; i < 7; i++) {
            int fontWidth = (int) mPaint.measureText(mWeekText[i]);
            int startX = columnWidth * i + (columnWidth - fontWidth) / 2;
            int startY = (int) (height / 2 - (mPaint.ascent() + mPaint.descent()) / 2);
            canvas.drawText(mWeekText[i], startX, startY, mPaint);
        }
    }

}
