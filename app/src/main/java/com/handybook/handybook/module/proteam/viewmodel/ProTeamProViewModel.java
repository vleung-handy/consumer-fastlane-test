package com.handybook.handybook.module.proteam.viewmodel;

import android.support.annotation.NonNull;

import com.handybook.handybook.module.proteam.model.ProTeamCategoryType;
import com.handybook.handybook.module.proteam.model.ProTeamPro;
import com.handybook.handybook.module.proteam.model.ProviderMatchPreference;
import com.handybook.handybook.library.util.DateTimeUtils;

import java.util.Date;
import java.util.Locale;

public class ProTeamProViewModel
{
    private static final String TEMPLATE_SPECIALTIES = "Specialties: %s";
    private static final String TEMPLATE_LAST_SEEN_FOOTER = "Last on %s";
    private static final String TEMPLATE_BOOKINGS_PLURAL_FOOTER = "%d bookings ";
    private static final String TEMPLATE_BOOKINGS_FOOTER = "%d booking";

    private final ProTeamPro mProTeamPro;
    private final ProTeamCategoryType mProTeamCategoryType;
    private final ProviderMatchPreference mProviderMatchPreference;
    private final String mTitle;
    private final String mSubtitle;
    private final String mLastSeenFooter;
    private final Float mAverageRating;
    private final String mBookingFooter;
    private boolean mIsChecked;
    private String mImageUrl;

    private ProTeamProViewModel(
            @NonNull final ProTeamPro proTeamPro,
            @NonNull ProTeamCategoryType proTeamCategoryType,
            @NonNull ProviderMatchPreference providerMatchPreference
    )
    {
        mProTeamPro = proTeamPro;
        mProTeamCategoryType = proTeamCategoryType;
        mProviderMatchPreference = providerMatchPreference;
        mIsChecked = mProviderMatchPreference == ProviderMatchPreference.PREFERRED;
        mTitle = proTeamPro.getName();
        mSubtitle = String.format(TEMPLATE_SPECIALTIES, proTeamPro.getDescription());
        final Date lastSeenAt = proTeamPro.getLastSeenAt();
        if (lastSeenAt == null)
        {
            mLastSeenFooter = null;
        }
        else
        {
            mLastSeenFooter = String.format(TEMPLATE_LAST_SEEN_FOOTER,
                    DateTimeUtils.MONTH_AND_DAY_FORMATTER.format(lastSeenAt));
        }
        mAverageRating = proTeamPro.getAverageRating();
        final int bookingCount = proTeamPro.getBookingCount();
        if (bookingCount == 1)
        { mBookingFooter = String.format(Locale.getDefault(), TEMPLATE_BOOKINGS_FOOTER, bookingCount); }
        else
        { mBookingFooter = String.format(Locale.getDefault(), TEMPLATE_BOOKINGS_PLURAL_FOOTER, bookingCount); }
        mImageUrl = proTeamPro.getImageUrl();
    }

    public static ProTeamProViewModel from(
            @NonNull final ProTeamPro proTeamPro,
            @NonNull final ProTeamCategoryType proTeamCategoryType,
            @NonNull final ProviderMatchPreference providerMatchPreference
    )
    {
        return new ProTeamProViewModel(proTeamPro, proTeamCategoryType, providerMatchPreference);
    }

    public ProviderMatchPreference getProviderMatchPreference()
    {
        return mProviderMatchPreference;
    }

    public ProTeamPro getProTeamPro()
    {
        return mProTeamPro;
    }

    public String getTitle()
    {
        return mTitle;
    }

    public String getSubtitle()
    {
        return mSubtitle;
    }

    public String getLastSeenFooter()
    {
        return mLastSeenFooter;
    }

    public Float getAverageRating()
    {
        return mAverageRating;
    }

    public String getBookingFooter()
    {
        return mBookingFooter;
    }

    public boolean isChecked()
    {
        return mIsChecked;
    }

    public void setChecked(final boolean checked)
    {
        mIsChecked = checked;
    }

    public boolean isFooterVisible()
    {
        return mProTeamPro.getLastSeenAt() != null;
    }

    public String getImageUrl()
    {
        return mImageUrl;
    }

    public interface OnInteractionListener
    {
        void onXClicked(ProTeamPro proTeamPro, ProviderMatchPreference providerMatchPreference);

        void onCheckedChanged(ProTeamPro proTeamPro, boolean checked);
    }
}
