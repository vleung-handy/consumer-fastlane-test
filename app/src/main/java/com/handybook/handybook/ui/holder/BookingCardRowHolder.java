package com.handybook.handybook.ui.holder;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.handybook.handybook.R;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.model.BookingCardRowViewModel;
import com.handybook.handybook.ui.activity.BookingDetailActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BookingCardRowHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    private Context mContext;
    private View mRoot;
    private BookingCardRowViewModel mBookingCardRowViewModel;

    @Bind(R.id.tv_card_booking_row_title)
    TextView vTitle;
    @Bind(R.id.tv_card_booking_row_subtitle)
    TextView vSubtitle;
    @Bind(R.id.iv_card_booking_row_left_edge_indicator)
    ImageView vLeftEdgeIndicator;

    public BookingCardRowHolder(final View itemView)
    {
        super(itemView);
        mRoot = itemView;
        mContext = mRoot.getContext();
        ButterKnife.bind(this.itemView);
        itemView.setOnClickListener(this);
    }

    public void bindBookingCardRowViewModel(@NonNull final BookingCardRowViewModel bookingCardRowViewModel)
    {
        mBookingCardRowViewModel = bookingCardRowViewModel;
        vTitle.setText(bookingCardRowViewModel.getTitle());
        vSubtitle.setText(bookingCardRowViewModel.getSubtitle(mContext));
        //TODO: Set other chrome like, the left edge indicator if it's in progress and such
    }

    @Override
    public void onClick(View v)
    {
        final Intent intent = new Intent(mContext, BookingDetailActivity.class);
        //intent.putExtra(BundleKeys.BOOKING, mBookingCardViewModel);
        //startActivityForResult(intent, ActivityResult.RESULT_BOOKING_UPDATED);
        Toast.makeText(mContext,"Clicked on :" + v.toString(),Toast.LENGTH_LONG).show();
    }

}
