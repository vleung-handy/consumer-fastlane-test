package com.handybook.handybook.booking.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handybook.handybook.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BookingDetailSectionView extends FrameLayout {

    @Bind(R.id.entry_sep)
    View mSeparator;
    @Bind(R.id.entry_title)
    TextView mEntryTitle;
    @Bind(R.id.entry_text)
    TextView mEntryText;
    @Bind(R.id.entry_action_text)
    TextView mEntryActionText;
    @Bind(R.id.action_buttons_layout)
    LinearLayout mActionButtonsLayout;

    public BookingDetailSectionView(final Context context) {
        super(context);
        init();
    }

    public BookingDetailSectionView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BookingDetailSectionView(
            final Context context, final AttributeSet attrs, final int defStyleAttr
    ) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BookingDetailSectionView(
            final Context context,
            final AttributeSet attrs,
            final int defStyleAttr,
            final int defStyleRes
    ) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    protected void init() {
        inflate(getContext(), R.layout.element_booking_detail_section, this);
        ButterKnife.bind(this);
    }

    public void showSeparator(boolean show) {
        mSeparator.setVisibility(show ? VISIBLE : GONE);
    }

    public TextView getEntryTitle() {
        return mEntryTitle;
    }

    public TextView getEntryText() {
        return mEntryText;
    }

    public TextView getEntryActionText() {
        return mEntryActionText;
    }

    public LinearLayout getActionButtonsLayout() {
        return mActionButtonsLayout;
    }
}
