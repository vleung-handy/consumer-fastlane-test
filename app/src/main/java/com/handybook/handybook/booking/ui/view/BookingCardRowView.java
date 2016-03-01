package com.handybook.handybook.booking.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.viewmodel.BookingCardRowViewModel;

public class BookingCardRowView extends RelativeLayout
{

    TextView mTitle;
    TextView mSubtitle;
    ImageView mEdgeIndicator;

    public BookingCardRowView(Context context)
    {
        super(context);
        init();
    }

    public BookingCardRowView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public BookingCardRowView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        inflate(getContext(), R.layout.layout_card_booking_row, this);
        //TODO: Inflate with ButterKnife if possible
        mTitle = (TextView) findViewById(R.id.tv_card_booking_row_title);
        mSubtitle = (TextView) findViewById(R.id.tv_card_booking_row_subtitle);
        mEdgeIndicator = (ImageView) findViewById(R.id.iv_card_booking_row_left_edge_indicator);
    }

    public void update(BookingCardRowViewModel model)
    {
        mTitle.setText(model.getTitle());
        mSubtitle.setText(model.getSubtitle(getContext()));
        //TODO: Add indicator, image on the left based on the model state.
        if (model.isIndicatorVisible())
        {
            mEdgeIndicator.setVisibility(VISIBLE);
        } else
        {
            mEdgeIndicator.setVisibility(GONE);
        }
    }

}
