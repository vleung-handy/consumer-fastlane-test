package com.handybook.handybook.booking.ui.view;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.ui.view.InjectedRelativeLayout;

import butterknife.Bind;

/**
 * Created by cdavis on 9/1/15.
 */
public class BookingDetailSectionImageItemView extends InjectedRelativeLayout
{

    @Bind(R.id.extra_title)
    public TextView extraTitle;
    @Bind(R.id.extra_image)
    public ImageView extraImage;


    public enum TextStyle
    {
        BOLD, ITALICS
    }

    public BookingDetailSectionImageItemView(final Context context)
    {
        super(context);
    }

    public BookingDetailSectionImageItemView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public BookingDetailSectionImageItemView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public void updateDisplay(int imageResource, int imageVisibility, String title, TextStyle textStyle, String text)
    {
        if (imageResource != 0)
        {
            extraImage.setImageResource(imageResource);
            extraImage.setVisibility(imageVisibility);
        }
        else
        {
            extraImage.setVisibility(INVISIBLE);
        }

        //if there is a title specified, try formatting it.
        if (!android.text.TextUtils.isEmpty(title))
        {
            switch (textStyle)
            {
                case BOLD:
                    extraTitle.setText(Html.fromHtml("<b>" + title + ":</b> " + text));
                    break;
                case ITALICS:
                    extraTitle.setText(Html.fromHtml("<i>" + title + ":</i> &nbsp;" + text));
                    break;
                default:
                    extraTitle.setText(Html.fromHtml(title + ": " + text));
            }
        }
        else
        {
            extraTitle.setText(Html.fromHtml(text));
        }
    }

}
