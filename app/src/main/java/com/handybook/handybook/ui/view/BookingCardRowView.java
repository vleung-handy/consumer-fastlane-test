package com.handybook.handybook.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.model.BookingCardRowViewModel;

public class BookingCardRowView extends RelativeLayout
{

    TextView vTitle;
    TextView vSubtitle;
    ImageView vEdgeInidcator;

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
        vTitle = (TextView) findViewById(R.id.tv_card_booking_row_title);
        vSubtitle = (TextView) findViewById(R.id.tv_card_booking_row_subtitle);
        vEdgeInidcator = (ImageView) findViewById(R.id.iv_card_booking_row_left_edge_indicator);
    }

    public void update(BookingCardRowViewModel model)
    {
        vTitle.setText(model.getTitle());
        vSubtitle.setText(model.getSubtitle(getContext()));
        //TODO: Add indicator, image on the left based on the model state.
        if (model.isIndicatorVisible())
        {
            vEdgeInidcator.setVisibility(VISIBLE);
        } else
        {
            vEdgeInidcator.setVisibility(GONE);
        }
    }

}
