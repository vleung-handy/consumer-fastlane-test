package com.handybook.handybook.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.User;
import com.handybook.handybook.ui.view.InjectedRelativeLayout;

import butterknife.InjectView;

/**
 * Created by cdavis on 9/1/15.
 */
public class BookingDetailSectionView extends InjectedRelativeLayout
{
    @InjectView(R.id.entry_title)
    public TextView entryTitle;
    @InjectView(R.id.entry_text)
    public TextView entryText;
    @InjectView(R.id.entry_action_text)
    public TextView entryActionText;

//    public int getLayoutResourceId()
//    {
//        return R.layout.element_booking_section;
//    }

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


    public void updateDisplay(Booking booking, User user)
    {
        System.out.println("This should be overwritten by a child class!");


        //section title
        //section text
        //gfx
        //edit action text

        //edit action
        //fragment will be unique for this edit action or will be capable of processing action off of a list of them

        //fragment needs to know what edit window/activity to bring up and set the transition back to correctly edit the booking (this shouold be through server not local)

        //edit action will have a linkage to another fragment, will need to get the data back though, all edit actions could be server calls the send back an updated booking?

        //


        //subclasses will use the proper display code


    }





}
