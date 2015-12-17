package com.handybook.handybook.booking.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.ui.view.InjectedRelativeLayout;

import butterknife.Bind;

/**
 * Created by cdavis on 9/1/15.
 */
public class BookingDetailSectionExtrasEntryView extends InjectedRelativeLayout
{

    @Bind(R.id.extra_title)
    public TextView extraTitle;
    @Bind(R.id.extra_image)
    public ImageView extraImage;

    public BookingDetailSectionExtrasEntryView(final Context context)
    {
        super(context);
    }

    public BookingDetailSectionExtrasEntryView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public BookingDetailSectionExtrasEntryView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public void updateDisplay(Booking.ExtraInfo extraInfo)
    {
        if(extraInfo.getImageResource() != 0)
        {
            extraImage.setImageResource(extraInfo.getImageResource());
        }
        else
        {
            extraImage.setVisibility(INVISIBLE);
        }

        extraTitle.setText(extraInfo.getLabel());
    }

}
