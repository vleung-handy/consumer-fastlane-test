package com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.bookingedit.ui.activity.BookingEditExtrasActivity;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.ui.view.BookingDetailSectionExtrasView;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.constant.ActivityResult;
import com.handybook.handybook.core.constant.BundleKeys;

import butterknife.BindView;

public class BookingDetailSectionFragmentExtras extends BookingDetailSectionFragment {

    @BindView(R.id.booking_detail_section_view)
    protected BookingDetailSectionExtrasView view;

    @Override
    protected int getFragmentResourceId() {
        return R.layout.fragment_booking_detail_section_extras;
    }

    @Override
    protected int getEntryTitleTextResourceId(Booking booking) {
        return R.string.extras;
    }

    @Override
    public void updateDisplay(Booking booking, User user) {
        super.updateDisplay(booking, user);
        view.updateExtrasDisplay(booking);
    }

    @Override
    protected void updateActionTextView(
            @NonNull final Booking booking,
            @NonNull final TextView actionTextView
    ) {
        if (!booking.canEditExtras() || booking.isPast()) {
            actionTextView.setVisibility(View.GONE);
            return;
        }
        actionTextView.setVisibility(View.VISIBLE);
        actionTextView.setText(R.string.edit);
        actionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onActionClick();
            }
        });
    }

    protected void onActionClick() {
        final Intent intent = new Intent(getActivity(), BookingEditExtrasActivity.class);
        intent.putExtra(BundleKeys.BOOKING, this.booking);
        getParentFragment().startActivityForResult(intent, ActivityResult.BOOKING_UPDATED);
    }
}
