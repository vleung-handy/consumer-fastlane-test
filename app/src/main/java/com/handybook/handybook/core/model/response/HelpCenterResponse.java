package com.handybook.handybook.core.model.response;


import android.support.annotation.DrawableRes;

import com.google.common.base.Strings;
import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;

import java.util.ArrayList;
import java.util.Observable;

public class HelpCenterResponse extends Observable
{
    @SerializedName("booking")
    private Booking mBooking;
    @SerializedName("links")
    private ArrayList<Link> mLinks;

    public Booking getBooking()
    {
        return mBooking;
    }

    public ArrayList<Link> getLinks()
    {
        return mLinks;
    }

    public static class Link
    {
        @SerializedName("text")
        private String mText;
        @SerializedName("subtext")
        private String mSubtext;
        @SerializedName("link")
        private String mLink;
        @SerializedName("type")
        private String mType;

        private static final String ICON_TYPE_PAST_BOOKINGS = "past_bookings";
        private static final String ICON_TYPE_UPCOMING_BOOKINGS = "upcoming_bookings";
        private static final String ICON_TYPE_ACCOUNT = "account";
        private static final String ICON_TYPE_PRO_TEAM = "pro_team";

        public String getText()
        {
            return mText;
        }

        public String getSubtext()
        {
            return mSubtext;
        }

        public String getLink()
        {
            return mLink;
        }

        @DrawableRes
        public int getIcon()
        {
            if (Strings.isNullOrEmpty(mType))
            { return R.drawable.menu_help; }

            switch (mType)
            {
                case ICON_TYPE_PAST_BOOKINGS:
                    return R.drawable.ic_help_past_booking;
                case ICON_TYPE_UPCOMING_BOOKINGS:
                    return R.drawable.ic_help_future_booking;
                case ICON_TYPE_ACCOUNT:
                    return R.drawable.ic_edit_cleaning_plan;
                case ICON_TYPE_PRO_TEAM:
                    return R.drawable.ic_pro_team_options;
                default:
                    return R.drawable.menu_help;
            }
        }
    }
}
