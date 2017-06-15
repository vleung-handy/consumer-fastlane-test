package com.handybook.handybook.proprofiles.ui;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.handybook.handybook.R;
import com.handybook.handybook.library.util.TextUtils;
import com.handybook.handybook.proprofiles.model.ProProfile;
import com.handybook.handybook.proteam.model.ProTeamCategoryType;
import com.handybook.handybook.proteam.util.ProTeamUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * the header view for the pro profiles page
 * that contains the mini pro profile layout (with profile image, rating and job count info)
 * and profile action buttons.
 *
 * currently, this is everything in pro profiles that is not in a tab layout.
 * this collapses as the tab layout is scrolled.
 *
 * rendered using the {@link ProProfile model}
 */
public class ProProfileHeaderView extends FrameLayout{

    @BindView(R.id.pro_profile_summary_layout)
    ProProfileMiniProProfile mMiniProProfile;

    @BindView(R.id.pro_profile_action_buttons_layout)
    ViewGroup mActionButtonsLayout;

    @BindView(R.id.pro_profile_message_action_button)
    View mMessageActionButton;

    @BindView(R.id.pro_profile_book_action_button)
    View mBookActionButton;

    @BindView(R.id.pro_profile_recommend_action_button)
    View mRecommendActionButton;

    public ProProfileHeaderView(@NonNull final Context context) {
        super(context);
        init();
    }

    public ProProfileHeaderView(
            @NonNull final Context context,
            @Nullable final AttributeSet attrs
    ) {
        super(context, attrs);
        init();
    }

    public ProProfileHeaderView(
            @NonNull final Context context,
            @Nullable final AttributeSet attrs,
            @AttrRes final int defStyleAttr
    ) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_pro_profile_header, this);
        ButterKnife.bind(this);
        mActionButtonsLayout.setVisibility(GONE);
    }

    public void updateWithModel(@NonNull ProProfile proProfile)
    {
        ProProfile.ProviderInformation providerInformation
                = proProfile.getProviderInformation();
        mMiniProProfile.setTitle(providerInformation.getDisplayName());

        //update the profile image
        String profileImageUrl = null;
        if(providerInformation.getProfileImages()!= null
           && providerInformation.getProfileImages().length > 0)
        {
            //try to get profile image url from profile images array
            ProProfile.ProviderInformation.ProfileImage profileImage =
                    providerInformation.getProfileImage(ProProfile.ProviderInformation.ProfileImage.Type.THUMBNAIL);
            if(profileImage == null)
            {
                //if no THUMBNAIL, fallback to ORIGINAL type. just using the same logic as the pro app
                profileImage =
                        providerInformation.getProfileImage(ProProfile.ProviderInformation.ProfileImage.Type.ORIGINAL);
            }
            if(profileImage != null)
            {
                profileImageUrl = profileImage.getUrl();
            }
        }
        if(TextUtils.isBlank(profileImageUrl))
        {
            //i don't know what this url represents compared to the photos array
            profileImageUrl = providerInformation.getProfilePhotoUrl();
        }
        mMiniProProfile.setImage(profileImageUrl);

        //set up rating/jobs count view and new-to-handy badge
        mMiniProProfile.setRatingAndJobsCount(providerInformation.getAverageRating(),
                                              providerInformation.getBookingCount());

        //pro team indicator
        boolean isProOnProTeam = ProTeamUtils.isProOnProTeam(providerInformation.getMatchPreference());
        mMiniProProfile.setProTeamIndicatorEnabled(isProOnProTeam);
        mMiniProProfile.setIsProTeam(isProOnProTeam);
        mMiniProProfile.setIsProTeamFavorite(providerInformation.isCustomerFavorite() != null
                                             && providerInformation.isCustomerFavorite());

        mMiniProProfile.setHandymanIndicatorEnabled(providerInformation.getProTeamCategoryType() == ProTeamCategoryType.HANDYMEN);

        //don't show allow user to message pro if pro is not on pro team
        mMessageActionButton.setEnabled(isProOnProTeam);

        //profile fully loaded; now we can show the profile actions
        mActionButtonsLayout.setVisibility(VISIBLE);
    }

    public void setMessageActionButtonClickListener(OnClickListener onClickListener)
    {
        mMessageActionButton.setOnClickListener(onClickListener);
    }

    public void setBookActionButtonClickListener(OnClickListener onClickListener)
    {
        mBookActionButton.setOnClickListener(onClickListener);
    }

    public void setRecommendActionButtonClickListener(OnClickListener onClickListener)
    {
        mRecommendActionButton.setOnClickListener(onClickListener);
    }
}
