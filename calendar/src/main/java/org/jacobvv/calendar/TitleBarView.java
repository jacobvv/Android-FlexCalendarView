package org.jacobvv.calendar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import org.jacobvv.calendar.formatter.DateFormatter;
import org.jacobvv.calendar.model.CalendarInfo;

/**
 * Created by jacob on 17-7-14.
 */

public class TitleBarView extends LinearLayout {

    private TextView mTitle;
    private DirectionButton mButtonPrevious;
    private DirectionButton mButtonNext;
    private DateFormatter mFormatter = DateFormatter.DEFAULT_TITLE;

    private Drawable mLeftArrowMask;
    private Drawable mRightArrowMask;

    private OnPageClickListener mPageListener;
    private OnClickListener mTitleListener;

    private CalendarInfo mDate;

    public TitleBarView(Context context, CalendarProfile profile, CalendarInfo date) {
        super(context);
        mLeftArrowMask = profile.getLeftArrowMask();
        mRightArrowMask = profile.getRightArrowMask();
        mDate = date;

        mButtonPrevious = new DirectionButton(getContext());
        mButtonPrevious.setContentDescription(getContext().getString(R.string.previous));
        mButtonPrevious.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        mButtonPrevious.setImageDrawable(mLeftArrowMask);
        mButtonPrevious.setColor(profile.getArrowColor());

        mButtonNext = new DirectionButton(getContext());
        mButtonNext.setContentDescription(getContext().getString(R.string.next));
        mButtonNext.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        mButtonNext.setImageDrawable(mRightArrowMask);
        mButtonNext.setColor(profile.getArrowColor());

        mTitle = new TextView(getContext());
        mTitle.setClickable(false);
        mTitle.setGravity(Gravity.CENTER);
        mTitle.setTextColor(profile.getTitleTextColor());
        mTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, profile.getTitleTextSizePx());

        setOrientation(LinearLayout.HORIZONTAL);
        setClipChildren(false);
        setClipToPadding(false);

        addView(mButtonPrevious, new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1));
        addView(mTitle, new LinearLayout.LayoutParams(
                0, LayoutParams.MATCH_PARENT, CalendarState.DEFAULT_DAYS_IN_WEEK - 2
        ));
        addView(mButtonNext, new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1));
        updateText();
    }

    public void setOnPageListener(OnPageClickListener l) {
        mPageListener = l;
        mButtonPrevious.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPageListener.onPrevious();
            }
        });
        mButtonNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPageListener.onNext();
            }
        });
    }

    public void setOnTitleClickListener(OnClickListener l) {
        mTitleListener = l;
        mTitle.setOnClickListener(l);
    }

    public void setButtonPreviousEnable(boolean enable) {
        mButtonPrevious.setEnabled(enable);
    }

    public void setButtonNextEnable(boolean enable) {
        mButtonNext.setEnabled(enable);
    }

    public void setDate(CalendarInfo date) {
        mDate = date;
        updateText();
    }

    public void setFormatter(DateFormatter f) {
        mFormatter = f;
        updateText();
    }

    private void updateText() {
        mTitle.setText(mFormatter.format(getContext(), mDate));
    }
}
