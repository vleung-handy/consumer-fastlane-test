package com.handybook.handybook.booking.ui.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.ui.view.ImageToggleButton;
import com.handybook.handybook.module.proteam.model.ProviderMatchPreference;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This is the fragment that holds views for adding / removing / blocking a pro
 */
public class RateProTeamFragment extends Fragment
{

    private static final String RATING_VALUE = "rating-value";
    private static final String MATCH_PREFERENCE = "match-preference";
    private static final String PRO_NAME = "pro-name";

    @Bind(R.id.toggle_button)
    ImageToggleButton mToggleButton;

    /**
     * Simple container just shows title and then button
     */
    @Bind(R.id.simple_container)
    ViewGroup mSimpleContainer;

    @Bind(R.id.rate_dialog_pro_match_header_txt)
    TextView mTextTitle;

    private String mTitleAddPro;
    private String mButtonTextAddPro;

    private String mTitleBlockPro;
    private String mButtonTextBlockPro;

    private String mTitleRemovePro;
    private String mButtonTextRemovePro;

    private Drawable mActiveAddDrawable;
    private Drawable mInactiveAddDrawable;

    private Drawable mActiveBlockDrawable;
    private Drawable mInactiveBlockDrawable;

    private Drawable mActiveRemoveDrawable;
    private Drawable mInactiveRemoveDrawable;

    /**
     * complex container shows a view pager which contains 2 pages of (title & button)
     */
    @Bind(R.id.complex_container)
    ViewGroup mComplexContainer;

    private ProviderMatchPreference mInitialPreference;
    private int mRating;
    private String mProName;

    public static RateProTeamFragment newInstance(int rating, String proName, ProviderMatchPreference matchPreference)
    {
        final RateProTeamFragment fragment = new RateProTeamFragment();
        final Bundle args = new Bundle();
        args.putInt(RATING_VALUE, rating);
        args.putString(PRO_NAME, proName);
        args.putSerializable(MATCH_PREFERENCE, matchPreference);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_rate_pro_team, container, false);
        ButterKnife.bind(this, v);

        mInitialPreference = (ProviderMatchPreference) getArguments().getSerializable(MATCH_PREFERENCE);
        mRating = getArguments().getInt(RATING_VALUE);
        mProName = getArguments().getString(PRO_NAME);

        mTitleAddPro = getResources().getString(R.string.rate_dialog_pro_match_title_add);
        mButtonTextAddPro = getResources().getString(R.string.rate_dialog_pro_match_add);

        mTitleBlockPro = getResources().getString(R.string.rate_dialog_pro_match_title_block);
        mButtonTextBlockPro = getResources().getString(R.string.rate_dialog_pro_match_block, mProName);

        mTitleRemovePro = getResources().getString(R.string.rate_dialog_pro_match_title_remove);
        mButtonTextRemovePro = getResources().getString(R.string.rate_dialog_pro_match_remove);

        mActiveAddDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_rating_pro_heart_active);
        mInactiveAddDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_rating_pro_heart_inactive);

        mActiveBlockDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_rating_pro_ban_active);
        mInactiveBlockDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_rating_pro_ban_inactive);

        mActiveRemoveDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_rating_pro_remove_active);
        mInactiveRemoveDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_rating_pro_remove_inactive);

        resetLayout();
        return v;
    }

    public void setRating(final int rating)
    {
        mRating = rating;
    }

    /**
     * This is the main method that must be called to setup the display properly.
     */
    public void resetLayout()
    {
        if (mRating < 0)
        {
            //this view is not ready to initialize. Not enough data. Hide everything
            mComplexContainer.setVisibility(View.GONE);
            mSimpleContainer.setVisibility(View.GONE);
            return;
        }

        if (isProAlreadyOnTeam())
        {
            if (mRating <= 3)
            {
                //TODO: JIA: show the complex layout block where it allows user to remove and then block pro
            }
            else
            {
                //HIDE EVERYTHING!!
                mComplexContainer.setVisibility(View.GONE);
                mSimpleContainer.setVisibility(View.GONE);
            }
        }
        else
        {    //pro is not yet on team
            if (mRating <= 3)
            {
                resetViewForBlockPro();
            }
            else
            {
                //show the flow to add to pro team
                resetViewForAddPro();
            }

        }
    }

    /**
     * This updates the layout to say the things specifically for the case where we want to block this
     * pro.
     */
    private void resetViewForBlockPro()
    {
        mComplexContainer.setVisibility(View.GONE);
        mSimpleContainer.setVisibility(View.VISIBLE);
        mToggleButton.setChecked(false);
        mTextTitle.setText(mTitleBlockPro);
        mToggleButton.setCheckedText(mButtonTextBlockPro);
        mToggleButton.setUncheckedText(mButtonTextBlockPro);
        mToggleButton.setCheckedDrawable(mActiveBlockDrawable);
        mToggleButton.setUncheckedDrawable(mInactiveBlockDrawable);
        mToggleButton.updateState();
    }

    /**
     * This updates the layout to say the things specifically for the case where we want to add this
     * pro to the pro team.
     */
    private void resetViewForAddPro()
    {
        mComplexContainer.setVisibility(View.GONE);
        mSimpleContainer.setVisibility(View.VISIBLE);
        mTextTitle.setText(mTitleAddPro);
        mToggleButton.setChecked(false);
        mToggleButton.setCheckedText(mButtonTextAddPro);
        mToggleButton.setUncheckedText(mButtonTextAddPro);
        mToggleButton.setCheckedDrawable(mActiveAddDrawable);
        mToggleButton.setUncheckedDrawable(mInactiveAddDrawable);
        mToggleButton.updateState();
    }

    /**
     * As of today, a pro is considered already on the team if the match preference says "preferred"
     *
     * @return
     */
    private boolean isProAlreadyOnTeam()
    {
        return mInitialPreference == ProviderMatchPreference.PREFERRED;
    }
}
