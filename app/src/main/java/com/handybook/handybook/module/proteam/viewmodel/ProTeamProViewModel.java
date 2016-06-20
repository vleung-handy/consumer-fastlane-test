package com.handybook.handybook.module.proteam.viewmodel;

import android.support.annotation.NonNull;

import com.handybook.handybook.module.proteam.model.ProTeamCategoryType;
import com.handybook.handybook.module.proteam.model.ProTeamPro;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProTeamProViewModel
{
    private static final String TEMPLATE_SPECIALTIES = "Specialties: %s";
    private static final String TEMPLATE_FOOTER_CLEANING = "Last cleaning on %s";
    private static final String TEMPLATE_FOOTER_HANDYMEN = "Last service on %s";

    private final ProTeamPro mProTeamPro;
    private final ProTeamCategoryType mProTeamCategoryType;
    private final String mTitle;
    private final String mSubtitle;
    private final String mFooter;
    private boolean mIsChecked;

    private ProTeamProViewModel(
            @NonNull final ProTeamPro proTeamPro,
            final ProTeamCategoryType proTeamCategoryType
    )
    {
        mProTeamPro = proTeamPro;
        mProTeamCategoryType = proTeamCategoryType;
        mTitle = proTeamPro.getName();
        mSubtitle = String.format(TEMPLATE_SPECIALTIES, proTeamPro.getDescription());
        final Date lastSeenAt = proTeamPro.getLastSeenAt();
        if (lastSeenAt == null)
        {
            mFooter = null;
        }
        else
        {
            final DateFormat df = new SimpleDateFormat("EEE, MMM d", Locale.US);
            final String template = mProTeamCategoryType == ProTeamCategoryType.CLEANING ?
                    TEMPLATE_FOOTER_CLEANING : TEMPLATE_FOOTER_HANDYMEN;
            mFooter = String.format(template, df.format(lastSeenAt));
        }
    }

    public static ProTeamProViewModel from(
            @NonNull final ProTeamPro proTeamPro,
            @NonNull final ProTeamCategoryType proTeamCategoryType
    )
    {
        return new ProTeamProViewModel(proTeamPro, proTeamCategoryType);
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

    public String getFooter()
    {
        return mFooter;
    }

    public boolean isChecked()
    {
        return mIsChecked;
    }

    public void setChecked(final boolean checked)
    {
        mIsChecked = checked;
    }

    public boolean isSubtitleVisible()
    {
        return mProTeamCategoryType == ProTeamCategoryType.HANDYMEN
                && mProTeamPro.getDescription() != null
                && !mProTeamPro.getDescription().isEmpty();
    }

    public boolean isFooterVisible()
    {
        return mProTeamPro.getLastSeenAt() != null;
    }

    public interface OnInteractionListener
    {
        void onXClicked(ProTeamPro proTeamPro);

        void onCheckedChanged(ProTeamPro proTeamPro, boolean checked);
    }
}
