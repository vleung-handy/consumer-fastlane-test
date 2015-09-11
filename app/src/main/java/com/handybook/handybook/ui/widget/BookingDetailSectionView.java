package com.handybook.handybook.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.ui.view.InjectedRelativeLayout;

import butterknife.Bind;


/**
 * Created by cdavis on 9/1/15.
 */
public class BookingDetailSectionView extends InjectedRelativeLayout
{
    @Bind(R.id.entry_title)
    public TextView entryTitle;
    @Bind(R.id.entry_text)
    public TextView entryText;
    @Bind(R.id.entry_action_text)
    public TextView entryActionText;

    public BookingDetailSectionView(final Context context)
    {
        super(context);
    }

    public BookingDetailSectionView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public BookingDetailSectionView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
    }
}
