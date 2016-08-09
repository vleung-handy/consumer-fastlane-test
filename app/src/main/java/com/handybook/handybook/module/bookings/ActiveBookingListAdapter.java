package com.handybook.handybook.module.bookings;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.RecurringBooking;

import java.util.List;

/**
 * A sophisticated list adapter that not only displays a list of bookings, it has concepts of
 * displaying special titles and "active" bookings.
 */
public class ActiveBookingListAdapter extends SimpleBookingListAdapater
{
    private static final int TITLE = 0;
    private static final int ACTIVE = 1;
    private static final int REGULAR = 2;
    private static final String TAG = ActiveBookingListAdapter.class.getName();

    private String mActivePlanCountTitle;
    protected List<RecurringBooking> mRecurringBookings;

    public ActiveBookingListAdapter(
            FragmentManager fragmentManager,
            @NonNull final List<Booking> bookings,
            @NonNull final List<RecurringBooking> recurringBookings,
            final String activePlanCountTitle,
            final View.OnClickListener clickListener
    )
    {
        super(fragmentManager, bookings, clickListener);
        mActivePlanCountTitle = activePlanCountTitle;
        mRecurringBookings = recurringBookings;
        updateWithActivePlan();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView;

        if (viewType == TITLE)
        {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_cleaning_plan, parent, false);

            return new CleaningPlanHolder(itemView);
        }
        else if (viewType == REGULAR)
        {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_booking_list_item, parent, false);

            return new BookingCardHolder(itemView, mOnClickListener);

        }
        else
        {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.active_booking_container, parent, false);

            return new ActiveBookingHolder(itemView);
        }

    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position)
    {
        if (holder.getItemViewType() == TITLE)
        {
            ((CleaningPlanHolder) holder).bind(mRecurringBookings, mActivePlanCountTitle);
        }
        else if (holder.getItemViewType() == ACTIVE)
        {
            //FIXME: do this only if the booking is considered "active" Not always at position 0
            ((ActiveBookingHolder) holder).bindActiveBooking(mFragmentManager, mBookings.get(position));
            Log.d(TAG, "onBindViewHolder: " + mBookings.get(position).getServiceName());
        }
        else
        {   //REGULAR
            ((BookingCardHolder) holder).bindToBooking(mBookings.get(position));
        }
    }


    @Override
    public int getItemViewType(final int position)
    {
        if (position == 0)
        {
            if (!TextUtils.isEmpty(mActivePlanCountTitle))
            {
                return TITLE;
            }
            else
            {
                //FIXME: do this only if the booking is considered "active" Not always at position 0
                return ACTIVE;
            }
        }
        else if (position == 1)
        {
            if (!TextUtils.isEmpty(mActivePlanCountTitle))
            {
                return ACTIVE;
            }
            else
            {
                //FIXME: do this only if the booking is considered "active" Not always at position 0
                return REGULAR;
            }
        }
        else
        {
            return REGULAR;
        }
    }

    private void updateWithActivePlan()
    {
        if (mBookings != null && !mBookings.isEmpty())
        {
            if (!TextUtils.isEmpty(mActivePlanCountTitle) && mBookings.get(0) != null)
            {
                //if there is a title, then the first item needs to be null
                //(to represent the header element)
                mBookings.add(0, null);
                notifyDataSetChanged();
            }
            else if (TextUtils.isEmpty(mActivePlanCountTitle) && mBookings.get(0) == null)
            {
                //if there are no title, then remove the null at the header
                mBookings.remove(0);
                notifyDataSetChanged();
            }
        }
    }
}
