package com.handybook.handybook.ratingflow.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.View;

import com.google.common.base.Strings;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.data.VoidDataManagerCallback;
import com.handybook.handybook.library.ui.view.LimitedEditText;
import com.handybook.handybook.library.util.TextUtils;

import butterknife.BindColor;
import butterknife.BindDimen;

public class RatingFlowReviewFragment extends RatingFlowFeedbackChildFragment {

    private LimitedEditText mReviewTextField;
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
            dataManager.submitProRatingDetails(
                    Integer.parseInt(mBooking.getId()),
                    review,
                    new VoidDataManagerCallback()
            );
        }
        finishStep();
    }
}
