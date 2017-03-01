package com.handybook.handybook.ratingflow.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.callback.FragmentSafeCallback;
import com.handybook.handybook.library.ui.view.LimitedEditText;
import com.handybook.handybook.library.util.TextUtils;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.ButterKnife;

public class RatingFlowReviewFragment extends RatingFlowFeedbackChildFragment {

    private LimitedEditText mReviewTextField;
    @Bind(R.id.rating_flow_section_title)
    TextView mSectionTitle;
    @Bind(R.id.rating_flow_section_container)
    ViewGroup mSectionContainer;
    @BindColor(R.color.dark_grey_pressed)
    int mHintColor;
    @BindColor(R.color.black)
    int mBlackColor;
    @BindDimen(R.dimen.text_size_medium)
    float mMediumTextSize;

    private Booking mBooking;

    @NonNull
    public static RatingFlowReviewFragment newInstance(@NonNull final Booking booking) {
        final RatingFlowReviewFragment fragment = new RatingFlowReviewFragment();
        final Bundle arguments = new Bundle();
        arguments.putParcelable(BundleKeys.BOOKING, booking);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBooking = getArguments().getParcelable(BundleKeys.BOOKING);
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
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        setSubmissionEnabled(true);
        mSectionTitle.setText(getString(
                R.string.write_a_review_formatted,
                mBooking.getProvider().getFirstName()
        ));
        mSectionContainer.removeAllViews();
        mReviewTextField = createReviewTextField();
        mSectionContainer.addView(mReviewTextField);
    }

    @Override
    public void onResume() {
        super.onResume();
        mReviewTextField.requestFocus();
    }

    @NonNull
    private LimitedEditText createReviewTextField() {
        final LimitedEditText editText = new LimitedEditText(getActivity());
        editText.setHint(R.string.rating_flow_feedback_hint);
        editText.setHintTextColor(mHintColor);
        editText.setBackground(null);
        editText.setTextColor(mBlackColor);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mMediumTextSize);
        editText.setTypeface(TextUtils.get(
                getContext(),
                com.handybook.handybook.library.util.TextUtils.Fonts.CIRCULAR_BOOK
        ));
        editText.setMaxCharacters(140);
        editText.setMaxLines(3);
        return editText;
    }

    @Override
    void onSubmit() {
        final String review = mReviewTextField.getText().toString();
        if (!Strings.isNullOrEmpty(review)) {
            showUiBlockers();
            dataManager.submitProRatingDetails(
                    Integer.parseInt(mBooking.getId()),
                    review,
                    new FragmentSafeCallback<Void>(this) {
                        @Override
                        public void onCallbackSuccess(final Void response) {
                            removeUiBlockers();
                            finishStep();
                        }

                        @Override
                        public void onCallbackError(DataManager.DataManagerError error) {
                            removeUiBlockers();
                            showToast(R.string.default_error_string);
                        }
                    }
            );
        }
        else {
            showToast(R.string.default_error_string);
        }
    }
}
