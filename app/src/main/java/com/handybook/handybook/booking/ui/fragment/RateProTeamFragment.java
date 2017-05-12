package com.handybook.handybook.booking.ui.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.core.BaseApplication;
import com.handybook.handybook.library.ui.view.ImageToggleButton;
import com.handybook.handybook.proteam.model.ProviderMatchPreference;
import com.handybook.handybook.proteam.util.ProTeamUtils;

import butterknife.Bind;
import butterknife.BindDrawable;
import butterknife.ButterKnife;

/**
 * This is the fragment that holds views for adding / removing / blocking a pro
 */
public class RateProTeamFragment extends Fragment {

    private static final String KEY_MATCH_PREFERENCE = "match-preference";
    private static final String KEY_PROVIDER_NAME = "provider-name";

    @Bind(R.id.rate_pro_team_buttons_title)
    TextView mButtonsTitle;
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

    private boolean mHasUserTouchedYesOrNoButtons = false;

    private String mProName;

    @NonNull
    public static RateProTeamFragment newInstance(
            @NonNull ProviderMatchPreference matchPreference,
            @NonNull String providerName
    ) {
        final RateProTeamFragment fragment = new RateProTeamFragment();
        final Bundle args = new Bundle();
        args.putSerializable(KEY_MATCH_PREFERENCE, matchPreference);
        args.putSerializable(KEY_PROVIDER_NAME, providerName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BaseApplication) getActivity().getApplication()).inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState
    ) {
        View v = inflater.inflate(R.layout.fragment_rate_pro_team, container, false);
        ButterKnife.bind(this, v);
        if (getArguments() != null) {
            mInitialMatchPreference = (ProviderMatchPreference) getArguments()
                    .getSerializable(KEY_MATCH_PREFERENCE);
            mProName = getArguments().getString(KEY_PROVIDER_NAME);
        }
        initUI();
        return v;
    }

    private void initUI() {
        // Title
        mButtonsTitle.setText(
                String.format(getString(R.string.would_you_like_to_work_with_x_again), mProName)
        );

        // Yes
        mButtonYes.setChecked(ProTeamUtils.isProOnProTeam(mInitialMatchPreference));
        mButtonYes.setCheckedText(getString(R.string.yes));
        mButtonYes.setUncheckedText(getString(R.string.yes));
        mButtonYes.setCheckedDrawable(mActiveAddDrawable);
        mButtonYes.setUncheckedDrawable(mInactiveAddDrawable);
        mButtonYes.updateState();
        mButtonYes.setListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                updateUI(ProviderMatchPreference.PREFERRED);
                mHasUserTouchedYesOrNoButtons = true;
            }
        });
        // No
        mButtonNo.setChecked(!ProTeamUtils.isProOnProTeam(mInitialMatchPreference));
        mButtonNo.setCheckedText(getString(R.string.no));
        mButtonNo.setUncheckedText(getString(R.string.no));
        mButtonNo.setCheckedDrawable(mActiveBlockDrawable);
        mButtonNo.setUncheckedDrawable(mInactiveBlockDrawable);
        mButtonNo.updateState();
        mButtonNo.setListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                updateUI(ProviderMatchPreference.NEVER);
                mHasUserTouchedYesOrNoButtons = true;
            }
        });
    }

    /**
     * If user hasn't interacted with the Y/N buttons it updates the UI, otherwise, nothing.
     * @param providerMatchPreference update UI to reflect this match preference
     */
    public void setProviderMatchPreference(final ProviderMatchPreference providerMatchPreference) {
        if (mHasUserTouchedYesOrNoButtons) { return; }
        updateUI(providerMatchPreference);
    }

    /**
     * Configures the UI to reflect the provided match preference
     * @param providerMatchPreference update UI to reflect this match preference
     */
    private void updateUI(final ProviderMatchPreference providerMatchPreference) {
        switch (providerMatchPreference) {
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
     */
    @NonNull
    public ProviderMatchPreference getProviderMatchPreference() {
        if (mButtonYes.isChecked()) {
            return ProviderMatchPreference.PREFERRED;
        }
        else if (mButtonNo.isChecked()) {
            return ProviderMatchPreference.NEVER;
        }
        //In theory this should never happen :)
        return ProviderMatchPreference.INDIFFERENT;
    }
}
