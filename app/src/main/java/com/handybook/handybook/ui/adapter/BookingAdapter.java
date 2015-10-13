package com.handybook.handybook.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.model.BookingCardViewModel;
import com.handybook.handybook.ui.holder.BookingCardHolder;

public class BookingAdapter extends RecyclerView.Adapter<BookingCardHolder>
{

    private Context mContext;
    private BookingCardViewModel.List mBookingCardViewModels;


    public BookingAdapter(
            @NonNull final Context context,
            @NonNull final BookingCardViewModel.List bookingCardViewModels
    )
    {
        mContext = context;
        mBookingCardViewModels = bookingCardViewModels;
    }

    @Override
    public BookingCardHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        final View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.layout_card_booking, null);
        return new BookingCardHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final BookingCardHolder holder, int position)
    {
        BookingCardViewModel bookingCardViewModel = mBookingCardViewModels.get(position);
        holder.bindBookingCardViewModel(bookingCardViewModel);
    }

    @Override
    public int getItemCount()
    {
        return mBookingCardViewModels.size();
    }
}
