package com.handybook.handybook.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.model.BookingCardRowViewModel;
import com.handybook.handybook.ui.holder.BookingCardRowHolder;

public class BookingRowAdapter extends RecyclerView.Adapter<BookingCardRowHolder>
{

    private Context mContext;
    private BookingCardRowViewModel.List mBookingCardRowViewModels;

    public BookingRowAdapter(
            @NonNull final Context context,
            @NonNull final BookingCardRowViewModel.List bookingCardRowViewModels
    )
    {
        mContext = context;
        this.mBookingCardRowViewModels = bookingCardRowViewModels;
    }

    @Override
    public BookingCardRowHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        final View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.layout_card_booking_row, null);
        return new BookingCardRowHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BookingCardRowHolder holder, int position)
    {
        BookingCardRowViewModel bookingCardRowViewModel = mBookingCardRowViewModels.get(position);
        holder.bindBookingCardRowViewModel(bookingCardRowViewModel);
    }

    @Override
    public int getItemCount()
    {
        return 0;
    }
}
