package com.handybook.handybook.booking.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookingDetailSectionExtrasView extends BookingDetailSectionView {

    @BindView(R.id.extras_section)
    public LinearLayout extrasSection;

    public BookingDetailSectionExtrasView(final Context context) {
        super(context);
    }

    public BookingDetailSectionExtrasView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public BookingDetailSectionExtrasView(
            final Context context,
            final AttributeSet attrs,
            final int defStyle
    ) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        inflate(getContext(), R.layout.view_booking_detail_section_extras, this);
        ButterKnife.bind(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        getEntryActionText().setId(R.id.edit_booking_extras_button);
        //need this here so butterknife has finished inflating this injected view
    }

    public void updateExtrasDisplay(final Booking booking) {
        final ArrayList<Booking.ExtraInfo> extras = booking.getExtrasInfo();
        extrasSection.removeAllViews();
        if (extras != null && extras.size() > 0) {
            for (int i = 0; i < extras.size(); i++) {
                final Booking.ExtraInfo info = extras.get(i);
                BookingDetailSectionImageItemView itemView = (BookingDetailSectionImageItemView)
                        inflate(getContext(), R.layout.layout_section_image_item, null);
                extrasSection.addView(itemView);
                itemView.updateDisplay(
                        info.getImageResource(),
                        VISIBLE,
                        null,
                        null,
                        info.getLabel()
                );
            }
        }
    }

}
