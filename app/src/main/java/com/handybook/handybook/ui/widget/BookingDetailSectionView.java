package com.handybook.handybook.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.ui.view.InjectedRelativeLayout;

import butterknife.Bind;


public class BookingDetailSectionView extends InjectedRelativeLayout
{
    @Bind(R.id.entry_title)
    TextView mEntryTitle;
    @Bind(R.id.entry_text)
    TextView mEntryText;
    @Bind(R.id.entry_action_text)
    TextView mEntryActionText;
    @Bind(R.id.action_buttons_layout)
    LinearLayout mActionButtonsLayout;

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

    public TextView getEntryTitle()
    {
        return mEntryTitle;
    }

    public TextView getEntryText()
    {
        return mEntryText;
    }

    public TextView getEntryActionText()
    {
        return mEntryActionText;
    }

    public LinearLayout getActionButtonsLayout()
    {
        return mActionButtonsLayout;
    }
}
