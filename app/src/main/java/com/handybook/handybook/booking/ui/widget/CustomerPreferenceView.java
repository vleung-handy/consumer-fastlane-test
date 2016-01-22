package com.handybook.handybook.booking.ui.widget;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.ChecklistItem;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CustomerPreferenceView extends FrameLayout
{
    @Bind(R.id.content)
    TextView mContent;

    public CustomerPreferenceView(final Context context)
    {
        super(context);
    }

    public CustomerPreferenceView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CustomerPreferenceView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    public void init(ChecklistItem checklistItem)
    {
        LayoutInflater.from(getContext()).inflate(R.layout.view_customer_preference, this);
        ButterKnife.bind(this);

        final Spanned formattedContent = Html.fromHtml("<b>" + checklistItem.getTitle() + ":</b> " +
                checklistItem.getText());
        mContent.setText(formattedContent);
    }
}
