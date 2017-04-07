package com.handybook.handybook.ratingflow.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.BookingOption;
import com.handybook.handybook.booking.model.Provider;
import com.handybook.handybook.booking.ui.view.BookingOptionsSelectView;
import com.handybook.handybook.booking.ui.view.BookingOptionsView;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.data.HandyRetrofitService;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.proteam.model.ProTeamEdit;
import com.handybook.handybook.proteam.model.ProviderMatchPreference;
import com.handybook.handybook.ratingflow.RatingFlowLog;

import javax.inject.Inject;

import static com.handybook.handybook.proteam.model.ProviderMatchPreference.NEVER;
import static com.handybook.handybook.proteam.model.ProviderMatchPreference.PREFERRED;

public class RatingFlowMatchPreferenceFragment extends RatingFlowFeedbackChildFragment {

    private static final int NO_PREFERENCE_INDEX = -1;
    private static final int POSITIVE_PREFERENCE_INDEX = 0;
    private static final int NEGATIVE_PREFERENCE_INDEX = 1;
    @Inject
    HandyRetrofitService mService;

    private Booking mBooking;
    private Provider mProvider;
    private int mOptionIndex;
    private ProviderMatchPreference mSelectedPreference;
    private BookingOptionsView.OnUpdatedListener mOptionUpdateListener
            = new BookingOptionsView.OnUpdatedListener() {
        @Override
        public void onUpdate(final BookingOptionsView view) {
            mOptionIndex = ((BookingOptionsSelectView) view).getCurrentIndex();
            mSelectedPreference = mOptionIndex == POSITIVE_PREFERENCE_INDEX ? PREFERRED : NEVER;
            setSubmissionEnabled(true);
            updateHelperText();
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
    public static RatingFlowMatchPreferenceFragment newInstance(
            @NonNull final Booking booking,
            @NonNull final ProviderMatchPreference defaultPreference
    ) {
        final RatingFlowMatchPreferenceFragment fragment = new RatingFlowMatchPreferenceFragment();
        final Bundle arguments = new Bundle();
        arguments.putParcelable(BundleKeys.BOOKING, booking);
        arguments.putSerializable(BundleKeys.PRO_TEAM_PRO_PREFERENCE, defaultPreference);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBooking = getArguments().getParcelable(BundleKeys.BOOKING);
        mProvider = mBooking.getProvider();
        final ProviderMatchPreference defaultPreference = (ProviderMatchPreference)
                getArguments().getSerializable(BundleKeys.PRO_TEAM_PRO_PREFERENCE);
        mOptionIndex = NO_PREFERENCE_INDEX;
        if (defaultPreference != null) {
            mSelectedPreference = defaultPreference;
            switch (defaultPreference) {
                case PREFERRED:
                    mOptionIndex = POSITIVE_PREFERENCE_INDEX;
                    break;
                case NEVER:
                    mOptionIndex = NEGATIVE_PREFERENCE_INDEX;
                    break;
            }
        }
    }

    @Override
    public void onViewCreated(
            final View view, @Nullable final Bundle savedInstanceState
    ) {
        setSubmissionEnabled(mOptionIndex != NO_PREFERENCE_INDEX);
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
        updateHelperText();
    }

    @Override
    void onSubmit() {
        final ProTeamEdit proTeamEdit = new ProTeamEdit(mSelectedPreference);
        proTeamEdit.addId(
                Integer.parseInt(mProvider.getId()),
                mProvider.getCategoryType()
        );
        finishStepWithProTeamEditRequest(proTeamEdit);
        bus.post(new LogEvent.AddLogEvent(new RatingFlowLog.ProPreferenceSubmitted(
                mSelectedPreference == PREFERRED,
                Integer.parseInt(mBooking.getId()),
                Integer.parseInt(mProvider.getId())
        )));
    }

    public ProviderMatchPreference getSelectedPreference() {
        return mSelectedPreference;
    }

    private void updateHelperText() {
        if (mOptionIndex == POSITIVE_PREFERENCE_INDEX
                && mProvider.getMatchPreference() != PREFERRED) {
            setHelperText(getString(
                    R.string.rating_flow_pro_team_addition_note_formatted,
                    mProvider.getFirstName()
            ));
        }
        else if (mOptionIndex == NEGATIVE_PREFERENCE_INDEX) {
            setHelperText(getString(
                    R.string.rating_flow_pro_team_block_note_formatted,
                    mProvider.getFirstName()
            ));
        }
        else {
            setHelperText(null);
        }
    }
}
