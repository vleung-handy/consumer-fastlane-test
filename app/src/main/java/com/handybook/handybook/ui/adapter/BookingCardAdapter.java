package com.handybook.handybook.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.handybook.handybook.R;
import com.handybook.handybook.viewmodel.BookingCardViewModel;
import com.handybook.handybook.ui.holder.BookingCardHolder;

public class BookingCardAdapter extends RecyclerView.Adapter<BookingCardHolder>
{

    private Context mContext;
    private BookingCardViewModel.List mBookingCardViewModels;
    private int lastPosition = -1;

    public BookingCardAdapter(
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
        Animation animation = AnimationUtils.loadAnimation(
                mContext,
                position > lastPosition ? R.anim.up_from_bottom : R.anim.down_from_top
        );
        //holder.itemView.startAnimation(animation);
        lastPosition = position;
    }

    @Override
    public int getItemCount()
    {
        return mBookingCardViewModels.size();
    }
}
