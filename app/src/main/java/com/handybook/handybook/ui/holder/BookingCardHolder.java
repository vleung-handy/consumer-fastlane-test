package com.handybook.handybook.ui.holder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.viewmodel.BookingCardRowViewModel;
import com.handybook.handybook.viewmodel.BookingCardViewModel;
import com.handybook.handybook.ui.activity.BookingDetailActivity;
import com.handybook.handybook.ui.activity.BookingEditFrequencyActivity;
import com.handybook.handybook.ui.view.BookingCardRowView;
import com.handybook.handybook.ui.widget.ServiceOutlineIcon;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BookingCardHolder extends RecyclerView.ViewHolder
{
    private Context mContext;
    private BookingCardViewModel mBookingCardViewModel;
    private View mRoot;
    @Bind(R.id.iv_booking_card_service_icon)
    ServiceOutlineIcon vServiceIcon;
    @Bind(R.id.tv_booking_card_service_title)
    TextView mServiceTitle;
    @Bind(R.id.ll_booking_card_recurring_layout)
    LinearLayout mRecurringSubtitleContainer;
    @Bind(R.id.tv_booking_card_recurring_text)
    TextView mRecurringText;
    @Bind(R.id.rl_booking_card_footer)
    RelativeLayout mFooter;
    @Bind(R.id.ll_booking_card_booking_row_container)
    LinearLayout mBookingRowContainer;
    @Bind(R.id.tv_edit_booking_card)
    TextView mEditBookingCard;

    public BookingCardHolder(View itemView)
    {
        super(itemView);
        mContext = itemView.getContext();
        mRoot = itemView;
        ButterKnife.bind(this, itemView);
    }

    public void bindBookingCardViewModel(@NonNull final BookingCardViewModel bookingCardViewModel)
    {
        mBookingCardViewModel = bookingCardViewModel;
        mServiceTitle.setText(mBookingCardViewModel.getTitle());
        mBookingRowContainer.removeAllViews();
        for (final BookingCardRowViewModel model : bookingCardViewModel.getBookingCardRowViewModels())
        {
            BookingCardRowView bookingCardRowView = new BookingCardRowView(mContext);
            bookingCardRowView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Activity activity = (Activity) mContext;
                    final Intent intent = new Intent(mContext, BookingDetailActivity.class);
                    intent.putExtra(BundleKeys.BOOKING, model.getBooking());
                    activity.startActivityForResult(intent, ActivityResult.RESULT_BOOKING_UPDATED);

                }
            });
            mBookingRowContainer.addView(bookingCardRowView);
            bookingCardRowView.update(model);
        }

        Booking masterBooking = getMasterBookingFromCardViewModel();
        if(masterBooking == null)
        {
            Crashlytics.logException(new Exception(this.getClass().getCanonicalName()
                            + ": model does not contain any bookings")
            );
        }
        else
        {
            if (masterBooking.getCanEditFrequency() && !masterBooking.isPast())
            {
                mEditBookingCard.setVisibility(View.VISIBLE);
                mEditBookingCard.setOnClickListener(onEditClickListener);
            }
            else
            {
                mEditBookingCard.setVisibility(View.GONE);
                mEditBookingCard.setOnClickListener(null);
            }

            if (masterBooking.isRecurring())
            {
                mRecurringSubtitleContainer.setVisibility(View.VISIBLE);
                mFooter.setVisibility(View.VISIBLE);
                mRecurringText.setVisibility(View.VISIBLE);
                mRecurringText.setText(mBookingCardViewModel.getSubtitle());
            }
            else
            {
                mRecurringText.setVisibility(View.GONE);
                mRecurringSubtitleContainer.setVisibility(View.GONE);
                mFooter.setVisibility(View.GONE);
            }
            vServiceIcon.updateServiceIconByBooking(masterBooking);
        }

    }

    private Booking getMasterBookingFromCardViewModel()
    {
        ArrayList<Booking> bookings = mBookingCardViewModel.getBookings();
        return (bookings == null || bookings.size() == 0) ? null : bookings.get(0);
        //this is to account for confusing api payload response - e.g. only the first booking of a
        // recurring series will have recurring > 0
        //TODO: safer to loop through bookings list to find best booking to use
    }

    private View.OnClickListener onEditClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            final Intent intent = new Intent(mContext, BookingEditFrequencyActivity.class);
            intent.putExtra(BundleKeys.BOOKING, getMasterBookingFromCardViewModel());
            Activity activity = (Activity) mContext;
            activity.startActivity(intent);
        }
    };

    @OnClick(R.id.rl_booking_card_footer)
    void onInfoRowClicked()
    {
        Snackbar
                .make(mRoot, R.string.snackbar_recurring_will_be_generated, Snackbar.LENGTH_LONG)
                .show();
    }

}
