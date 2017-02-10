package com.handybook.handybook.booking.ui.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.core.BaseApplication;
import com.handybook.handybook.library.ui.view.ImageToggleButton;
import com.handybook.handybook.proteam.model.ProviderMatchPreference;

import butterknife.Bind;
import butterknife.BindDrawable;
import butterknife.ButterKnife;

/**
 * This is the fragment that holds views for adding / removing / blocking a pro
 */
public class RateProTeamFragment extends Fragment
{

    private static final String KEY_MATCH_PREFERENCE = "match-preference";

    @Bind(R.id.rate_pro_team_button_yes)
    ImageToggleButton mButtonYes;
    @Bind(R.id.rate_pro_team_button_no)
    ImageToggleButton mButtonNo;

    @Bind(R.id.rate_pro_team_container)
    ViewGroup mRootContainer;

    @BindDrawable(R.drawable.ic_checkbox_heart_checked)
    Drawable mActiveAddDrawable;

    @BindDrawable(R.drawable.ic_checkbox_heart_unchecked)
    Drawable mInactiveAddDrawable;

    @BindDrawable(R.drawable.ic_rating_pro_ban_active)
    Drawable mActiveBlockDrawable;

    @BindDrawable(R.drawable.ic_rating_pro_ban_inactive)
    Drawable mInactiveBlockDrawable;

    private ProviderMatchPreference mInitialMatchPreference;

    private boolean mHasUserClickedYesOrNoButton = false;
    private ProviderMatchPreference mCurrentProviderMatchPreference;

    public static RateProTeamFragment newInstance(ProviderMatchPreference matchPreference)
    {
        final RateProTeamFragment fragment = new RateProTeamFragment();
        final Bundle args = new Bundle();
        args.putSerializable(KEY_MATCH_PREFERENCE, matchPreference);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ((BaseApplication) getActivity().getApplication()).inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState
    )
    {
        View v = inflater.inflate(R.layout.fragment_rate_pro_team, container, false);
        ButterKnife.bind(this, v);
        if (getArguments() != null)
        {
            mInitialMatchPreference = (ProviderMatchPreference) getArguments()
                    .getSerializable(KEY_MATCH_PREFERENCE);
        }
        mCurrentProviderMatchPreference = mInitialMatchPreference;
        initUI();
        return v;
    }

    private void initUI()
    {
        // Yes
        mButtonYes.setChecked(isProAlreadyOnTeam());
        mButtonYes.setCheckedText(getString(R.string.yes));
        mButtonYes.setUncheckedText(getString(R.string.yes));
        mButtonYes.setCheckedDrawable(mActiveAddDrawable);
        mButtonYes.setUncheckedDrawable(mInactiveAddDrawable);
        mButtonYes.updateState();
        mButtonYes.setListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                mButtonYes.setChecked(true);
                mButtonYes.updateState();
                if (mCurrentProviderMatchPreference != ProviderMatchPreference.PREFERRED)
                {
                    mButtonNo.setChecked(false);
                    mButtonNo.updateState();
                    mCurrentProviderMatchPreference = ProviderMatchPreference.PREFERRED;
                }
                mHasUserClickedYesOrNoButton = true;
            }
        });
        // No
        mButtonNo.setChecked(!isProAlreadyOnTeam());
        mButtonNo.setCheckedText(getString(R.string.no));
        mButtonNo.setUncheckedText(getString(R.string.no));
        mButtonNo.setCheckedDrawable(mActiveBlockDrawable);
        mButtonNo.setUncheckedDrawable(mInactiveBlockDrawable);
        mButtonNo.updateState();
        mButtonNo.setListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                mButtonNo.setChecked(true);
                mButtonNo.updateState();
                if (mCurrentProviderMatchPreference != ProviderMatchPreference.NEVER)
                {
                    mButtonYes.setChecked(false);
                    mButtonYes.updateState();
                    mCurrentProviderMatchPreference = ProviderMatchPreference.NEVER;
                }
                mHasUserClickedYesOrNoButton = true;
            }
        });
    }

    public void setProviderMatchPreference(final ProviderMatchPreference pmp)
    {
        if (mHasUserClickedYesOrNoButton) { return; }
        mCurrentProviderMatchPreference = pmp;
        switch (pmp)
        {
            case PREFERRED:
                mButtonYes.setChecked(true);
                mButtonNo.setChecked(false);
                mButtonYes.updateState();
                mButtonNo.updateState();
                break;
            case NEVER:
                mButtonYes.setChecked(false);
                mButtonNo.setChecked(true);
                mButtonYes.updateState();
                mButtonNo.updateState();
                break;
        }
    }

    /**
     * This is the method that should be called to retrieve the user's final decision on what he's
     * selected through the possible combinations of buttons
     *
     * @return
     */
    @NonNull
    public ProviderMatchPreference getNewProviderMatchPreference()
    {
        if (mButtonYes.isChecked())
        {
            return ProviderMatchPreference.PREFERRED;
        }
        else if (mButtonNo.isChecked())
        {
            return ProviderMatchPreference.NEVER;
        }
        //In theory this should never happen :)
        return ProviderMatchPreference.INDIFFERENT;
    }

    /**
     * As of today, a pro is considered already on the team if the match preference says
     * "preferred"
     *
     * @return
     */
    private boolean isProAlreadyOnTeam()
    {
        return mInitialMatchPreference == ProviderMatchPreference.PREFERRED;
    }

}
