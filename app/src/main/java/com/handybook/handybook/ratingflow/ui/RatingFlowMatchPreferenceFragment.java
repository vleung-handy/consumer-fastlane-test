package com.handybook.handybook.ratingflow.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.BookingOption;
import com.handybook.handybook.booking.model.Provider;
import com.handybook.handybook.booking.ui.view.BookingOptionsSelectView;
import com.handybook.handybook.booking.ui.view.BookingOptionsView;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.HandyRetrofitCallback;
import com.handybook.handybook.core.data.HandyRetrofitService;
import com.handybook.handybook.core.data.callback.FragmentSafeCallback;
import com.handybook.handybook.proteam.event.ProTeamEvent;
import com.handybook.handybook.proteam.model.ProTeamEdit;
import com.handybook.handybook.proteam.model.ProTeamEditWrapper;
import com.handybook.handybook.proteam.model.ProTeamWrapper;
import com.handybook.handybook.proteam.model.ProviderMatchPreference;

import org.json.JSONObject;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.handybook.handybook.proteam.model.ProviderMatchPreference.NEVER;
import static com.handybook.handybook.proteam.model.ProviderMatchPreference.PREFERRED;

public class RatingFlowMatchPreferenceFragment extends RatingFlowFeedbackChildFragment {

    @Inject
    HandyRetrofitService mService;

    @Bind(R.id.rating_flow_section_title)
    TextView mSectionTitle;
    @Bind(R.id.rating_flow_section_container)
    ViewGroup mSectionContainer;

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

    @Nullable
    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState
    ) {

        final View view = inflater.inflate(
                R.layout.fragment_rating_flow_generic,
                container,
                false
        );
        ButterKnife.bind(this, view);
        return view;
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
        options.setTitle(getString(
                R.string.would_you_like_to_work_with_x_again,
                mProvider.getFirstName()
        ));
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
        showUiBlockers();
        final DataManager.Callback<ProTeamWrapper> cb =
                new FragmentSafeCallback<ProTeamWrapper>(this) {
                    @Override
                    public void onCallbackSuccess(final ProTeamWrapper proTeamWrapper) {
                        removeUiBlockers();
                        finishStep();
                    }

                    @Override
                    public void onCallbackError(final DataManager.DataManagerError error) {
                        removeUiBlockers();
                        showToast(R.string.default_error_string);
                    }
                };
        final ProTeamEdit proTeamEdit = new ProTeamEdit(mSelectedPreference);
        proTeamEdit.addId(
                Integer.parseInt(mProvider.getId()),
                mProvider.getCategoryType()
        );
        mService.editProTeam(
                userManager.getCurrentUser().getId(),
                new ProTeamEditWrapper(
                        Lists.newArrayList(proTeamEdit),
                        ProTeamEvent.Source.PRO_MANAGEMENT.toString()
                ),
                new HandyRetrofitCallback(cb) {
                    @Override
                    protected void success(final JSONObject response) {
                        cb.onSuccess(ProTeamWrapper.fromJson(response.toString()));
                    }
                }
        );
    }
}
