package com.handybook.handybook.ui.widget;

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

public class ChecklistView extends FrameLayout
{
    @Bind(R.id.content)
    TextView mContent;

    public ChecklistView(final Context context)
    {
        super(context);
    }

    public ChecklistView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ChecklistView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    public void init(ChecklistItem checklistItem)
    {
        LayoutInflater.from(getContext()).inflate(R.layout.view_checklist, this);
        ButterKnife.bind(this);

        final Spanned formattedContent = Html.fromHtml("<b>" + checklistItem.getTitle() + ":</b> " +
                checklistItem.getText());
        mContent.setText(formattedContent);
    }
}
