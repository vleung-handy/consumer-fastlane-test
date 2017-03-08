package com.handybook.handybook.ratingflow.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.google.common.collect.Lists;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.BookingOption;
import com.handybook.handybook.booking.model.Provider;
import com.handybook.handybook.booking.ui.view.BookingOptionsSelectView;
import com.handybook.handybook.booking.ui.view.BookingOptionsView;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.data.HandyRetrofitService;
import com.handybook.handybook.core.data.VoidRetrofitCallback;
import com.handybook.handybook.proteam.event.ProTeamEvent;
import com.handybook.handybook.proteam.model.ProTeamEdit;
import com.handybook.handybook.proteam.model.ProTeamEditWrapper;
import com.handybook.handybook.proteam.model.ProviderMatchPreference;

import javax.inject.Inject;

import static com.handybook.handybook.proteam.model.ProviderMatchPreference.NEVER;
import static com.handybook.handybook.proteam.model.ProviderMatchPreference.PREFERRED;

public class RatingFlowMatchPreferenceFragment extends RatingFlowFeedbackChildFragment {

    @Inject
    HandyRetrofitService mService;

    private Provider mProvider;
    private int mOptionIndex;
    private ProviderMatchPreference mSelectedPreference;
    private BookingOptionsView.OnUpdatedListener mOptionUpdateListener
            = new BookingOptionsView.OnUpdatedListener() {
        @Override
        public void onUpdate(final BookingOptionsView view) {
            mOptionIndex = ((BookingOptionsSelectView) view).getCurrentIndex();
            mSelectedPreference = mOptionIndex == 0 ? PREFERRED : NEVER;
            setSubmissionEnabled(true);
        }

        @Override
        public void onShowChildren(
                final BookingOptionsView view,
                final String[] items
        ) {

        }

        @Override
        public void onHideChildren(
                final BookingOptionsView view,
                final String[] items
        ) {

        }
    };

    @NonNull
    public static RatingFlowMatchPreferenceFragment newInstance(@NonNull final Provider provider) {
        final RatingFlowMatchPreferenceFragment fragment = new RatingFlowMatchPreferenceFragment();
        final Bundle arguments = new Bundle();
        arguments.putSerializable(BundleKeys.PROVIDER, provider);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProvider = (Provider) getArguments().getSerializable(BundleKeys.PROVIDER);
        mOptionIndex = -1;
    }

    @Override
    public void onViewCreated(
            final View view, @Nullable final Bundle savedInstanceState
    ) {
        setSubmissionEnabled(false);
        mSectionContainer.removeAllViews();
        mSectionTitle.setText(getString(
                R.string.would_you_like_to_work_with_x_again,
                mProvider.getFirstName()
        ));
        final BookingOption options = new BookingOption();
        options.setType(BookingOption.TYPE_OPTION);
        options.setOptions(new String[]{getString(R.string.yes), getString(R.string.no)});
        options.setDefaultValue(String.valueOf(mOptionIndex));
        final BookingOptionsSelectView optionsView = new BookingOptionsSelectView(
                getActivity(),
                R.layout.view_rating_select_option,
                options,
                mOptionUpdateListener
        );
        optionsView.hideTitle();
        optionsView.hideSeparator();
        mSectionContainer.addView(optionsView);
    }

    @Override
    void onSubmit() {
        final ProTeamEdit proTeamEdit = new ProTeamEdit(mSelectedPreference);
        proTeamEdit.addId(
                Integer.parseInt(mProvider.getId()),
                mProvider.getCategoryType()
        );
        mService.editProTeam(
                userManager.getCurrentUser().getId(),
                new ProTeamEditWrapper(
                        Lists.newArrayList(proTeamEdit),
                        ProTeamEvent.Source.RATING_FLOW.toString()
                ),
                new VoidRetrofitCallback()
        );
        finishStep();
    }
}
