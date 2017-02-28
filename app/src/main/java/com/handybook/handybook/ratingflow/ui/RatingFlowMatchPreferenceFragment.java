package com.handybook.handybook.ratingflow.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.BookingOption;
import com.handybook.handybook.booking.model.Provider;
import com.handybook.handybook.booking.ui.view.BookingOptionsSelectView;
import com.handybook.handybook.booking.ui.view.BookingOptionsView;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.proteam.model.ProviderMatchPreference;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.handybook.handybook.proteam.model.ProviderMatchPreference.NEVER;
import static com.handybook.handybook.proteam.model.ProviderMatchPreference.PREFERRED;

public class RatingFlowMatchPreferenceFragment extends InjectedFragment {

    @Bind(R.id.rating_flow_section_title)
    TextView mSectionTitle;
    @Bind(R.id.rating_flow_section_container)
    ViewGroup mSectionContainer;

    private Provider mProvider;
    private int mOptionIndex;
    private BookingOptionsView.OnUpdatedListener mOptionUpdateListener
            = new BookingOptionsView.OnUpdatedListener() {
        @Override
        public void onUpdate(final BookingOptionsView view) {
            mOptionIndex = ((BookingOptionsSelectView) view).getCurrentIndex();
            final Fragment targetFragment = getTargetFragment();
            final ProviderMatchPreference selectedPreference =
                    mOptionIndex == 0 ? PREFERRED : NEVER;
            if (targetFragment != null) {
                targetFragment.onActivityResult(
                        getTargetRequestCode(),
                        Activity.RESULT_OK,
                        new Intent().putExtra(BundleKeys.MATCH_PREFERENCE, selectedPreference)
                );
            }
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
}
