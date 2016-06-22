package com.handybook.handybook.booking.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.handybook.handybook.R;

import butterknife.Bind;


/**
 * Created by cdavis on 9/1/15.
 */
public class BookingDetailSectionProInfoView extends BookingDetailSectionView
{
    @Bind(R.id.action_buttons_layout_slot_1)
    public LinearLayout actionButtonsLayoutSlot1;

    @Bind(R.id.action_buttons_layout_slot_2)
    public LinearLayout actionButtonsLayoutSlot2;

    @Bind(R.id.no_pro_view)
    public View noProView;

    @Bind(R.id.button_pro_team)
    public Button buttonProTeam;

    public BookingDetailSectionProInfoView(final Context context)
    {
        super(context);
    }

    public BookingDetailSectionProInfoView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public BookingDetailSectionProInfoView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
    }
}
