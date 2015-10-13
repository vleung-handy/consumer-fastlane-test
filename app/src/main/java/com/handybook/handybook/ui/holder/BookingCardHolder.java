package com.handybook.handybook.ui.holder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.model.BookingCardViewModel;
import com.handybook.handybook.ui.adapter.BookingRowAdapter;
import com.handybook.handybook.ui.widget.ServiceIconImageView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BookingCardHolder extends RecyclerView.ViewHolder
{
    private BookingCardViewModel mBookingCardViewModel;
    private Context mContext;
    private View mRoot;

    @Bind(R.id.iv_booking_card_service_icon)
    ServiceIconImageView vServiceIcon;
    @Bind(R.id.tv_booking_card_service_title)
    TextView vServiceTitle;
    @Bind(R.id.ll_booking_card_recurring_layout)
    LinearLayout vRecurringSubtitleContainer;
    @Bind(R.id.tv_booking_card_recurring_text)
    TextView vRecurringText;
    @Bind(R.id.rl_booking_card_footer)
    RelativeLayout vFooter;
    @Bind(R.id.rv_booking_card_booking_recycler_view)
    RecyclerView vBookingRowRecycler;


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
        //TODO:set all other properties
        vServiceTitle.setText(mBookingCardViewModel.getTitle());
        if (mBookingCardViewModel.isMultiCard())
        {
            vRecurringSubtitleContainer.setVisibility(View.VISIBLE);
            vFooter.setVisibility(View.GONE);
            vRecurringText.setText(mBookingCardViewModel.getSubtitle());
        } else
        {
            vRecurringSubtitleContainer.setVisibility(View.GONE);
            vFooter.setVisibility(View.GONE);
        }
        vBookingRowRecycler.setAdapter(new BookingRowAdapter(mContext, bookingCardViewModel.getBookingCardRowViewModels()));

        vServiceIcon.updateServiceIconByBooking(mBookingCardViewModel.getBookings().get(0));
    }

}
