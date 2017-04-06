package com.handybook.handybook.referral.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.callback.FragmentSafeCallback;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.user.ReferralLog;
import com.handybook.handybook.referral.model.ReferralDescriptor;
import com.handybook.handybook.referral.model.ReferralResponse;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This is the new version of the referral fragment, such that it fetches a user's proteams.
 * If the proteam exists, then it'll display the {@link ProReferralFragment} otherwise, it'll
 * fall back to the existing {@link ReferralFragment}
 */
public class ReferralV2Fragment extends InjectedFragment {

    public static final String EXTRA_REQUEST_COMPLETED = "request-completed";

    private ReferralDescriptor mReferralDescriptor;
    private String mSource;
    private boolean mRequestCompleted = false;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    public static ReferralV2Fragment newInstance(String source) {
        Bundle args = new Bundle();
        args.putString(BundleKeys.REFERRAL_PAGE_SOURCE, source);
        ReferralV2Fragment fragment = new ReferralV2Fragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mReferralDescriptor
                    = (ReferralDescriptor) savedInstanceState.getSerializable(BundleKeys.REFERRAL_DESCRIPTOR);
            mRequestCompleted = savedInstanceState.getBoolean(EXTRA_REQUEST_COMPLETED);
        }

        if (mReferralDescriptor == null) {
            fetchData();
        }

    }

    @Nullable
    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_referral_v2, container, false);
        ButterKnife.bind(this, view);

        setupToolbar(mToolbar, getString(R.string.free_cleanings));
        mToolbar.setNavigationIcon(null);

        bus.post(new LogEvent.AddLogEvent(new ReferralLog.ReferralOpenLog()));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateDisplay();
    }

    /**
     * Determines whether to show the {@link ProReferralFragment} or just the regular
     * {@link ReferralFragment}. This method may be called before the view is inflated, so be careful
     */
    private void updateDisplay() {
        if (getView() == null || !mRequestCompleted) {
            //don't do anything if the view hasn't inflated, or the request hasn't completed.
            return;
        }

        if (mReferralDescriptor == null
            || mReferralDescriptor.getProReferralInfo() == null
            || mReferralDescriptor.getProReferralInfo().isEmpty()) {
            showLegacyReferral();
        }
        else {
            showProTeamReferral();
        }
    }

    private void showProTeamReferral() {
        if (getChildFragmentManager().findFragmentByTag(ProReferralFragment.class.getName()) ==
            null) {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.referral_v2_main,
                            ProReferralFragment.newInstance(mReferralDescriptor, mSource),
                            ProReferralFragment.class.getName()
                    )
                    .commit();
        }

    }

    private void showLegacyReferral() {
        if (getChildFragmentManager().findFragmentByTag(ReferralFragment.class.getName()) == null) {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.referral_v2_main,
                            ReferralFragment.newInstance(mReferralDescriptor, mSource, true),
                            ReferralFragment.class.getName()
                    )
                    .commit();
        }
    }

    private void fetchData() {
        progressDialog.show();
        dataManager.requestPrepareReferrals(
                true,
                new FragmentSafeCallback<ReferralResponse>(this) {
            @Override
            public void onCallbackSuccess(final ReferralResponse response) {
                progressDialog.dismiss();
                mReferralDescriptor = response.getReferralDescriptor();
                mRequestCompleted = true;
                updateDisplay();
            }

            @Override
            public void onCallbackError(final DataManager.DataManagerError error) {
                progressDialog.dismiss();
                mRequestCompleted = true;
                showLegacyReferral();
            }
        });
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(BundleKeys.REFERRAL_DESCRIPTOR, mReferralDescriptor);
        outState.putBoolean(EXTRA_REQUEST_COMPLETED, mRequestCompleted);
    }
}
