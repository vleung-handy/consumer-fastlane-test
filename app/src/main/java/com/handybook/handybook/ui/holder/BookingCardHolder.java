package com.handybook.handybook.ui.holder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.model.BookingCardRowViewModel;
import com.handybook.handybook.model.BookingCardViewModel;
import com.handybook.handybook.ui.adapter.BookingCardRowAdapter;
import com.handybook.handybook.ui.view.BookingCardRowView;
import com.handybook.handybook.ui.widget.ServiceIconImageView;

import java.util.zip.Inflater;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BookingCardHolder extends RecyclerView.ViewHolder
{
    private Context mContext;
    private BookingCardViewModel mBookingCardViewModel;
    private BookingCardRowAdapter mBookingCardRowAdapter;

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
    @Bind(R.id.ll_booking_card_booking_row_container)
    LinearLayout vBookingRowContainer;


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
        vServiceTitle.setText(mBookingCardViewModel.getTitle());
        mBookingCardRowAdapter = new BookingCardRowAdapter(
                mContext,
                bookingCardViewModel.getBookingCardRowViewModels()
        );
        for (BookingCardRowViewModel bookingCardRowViewModel : bookingCardViewModel.getBookingCardRowViewModels())
        {
            BookingCardRowView bookingCardRowView = new BookingCardRowView(mContext);
            vBookingRowContainer.addView(bookingCardRowView);
            bookingCardRowView.update(bookingCardRowViewModel);
        }
        if (mBookingCardViewModel.isMultiCard())
        {
            vRecurringSubtitleContainer.setVisibility(View.VISIBLE);
            vFooter.setVisibility(View.VISIBLE);
            vRecurringText.setVisibility(View.VISIBLE);
            vRecurringText.setText(mBookingCardViewModel.getSubtitle());
        } else
        {
            vRecurringText.setVisibility(View.GONE);
            vRecurringSubtitleContainer.setVisibility(View.GONE);
            vFooter.setVisibility(View.GONE);
        }
        vServiceIcon.updateServiceIconByBooking(mBookingCardViewModel.getBookings().get(0));
    }

}
