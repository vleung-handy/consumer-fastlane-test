package com.handybook.handybook.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.model.BookingCardRowViewModel;

import butterknife.Bind;

public class BookingCardRowView extends InjectedRelativeLayout
{

    @Bind(R.id.tv_card_booking_row_title)
    TextView mTitle;
    @Bind(R.id.tv_card_booking_row_title)
    TextView mSubtitle;
    @Bind(R.id.iv_card_booking_row_left_edge_indicator)
    ImageView mEdgeIndicator;

    public BookingCardRowView(Context context)
    {
        super(context);
    }

    public BookingCardRowView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public BookingCardRowView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
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
