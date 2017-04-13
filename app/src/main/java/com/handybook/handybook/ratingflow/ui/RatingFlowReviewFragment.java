package com.handybook.handybook.ratingflow.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;

import com.google.common.base.Strings;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.rating.ReviewProRequest;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.data.VoidDataManagerCallback;
import com.handybook.handybook.library.util.TextUtils;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.booking.EventType;
import com.handybook.handybook.ratingflow.RatingFlowLog;

import butterknife.BindColor;
import butterknife.BindDimen;

public class RatingFlowReviewFragment extends RatingFlowFeedbackChildFragment {

    private EditText mReviewTextField;
    @BindColor(R.color.dark_grey_pressed)
    int mHintColor;
    @BindColor(R.color.black)
    int mBlackColor;
    @BindDimen(R.dimen.text_size_medium)
    float mMediumTextSize;
    @BindDimen(R.dimen.default_padding)
    int mDefaultPadding;

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

        bus.post(new LogEvent.AddLogEvent(new RatingFlowLog.ProfileReviewLog(
                EventType.EVENT_TYPE_SHOWN,
                Integer.parseInt(mBooking.getProvider().getId())
        )));
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        setSubmissionEnabled(true);
        mSectionTitle.setText(getString(
                R.string.write_a_review_formatted,
                mBooking.getProvider().getFirstName()
        ));
        mSectionContainer.removeAllViews();
        mSectionContainer.setBackgroundResource(R.drawable.border_grey_top);
        setHelperText(getString(R.string.rating_flow_review_note));
        mReviewTextField = createReviewTextField();
        mSectionContainer.addView(mReviewTextField);
    }

    @NonNull
    private EditText createReviewTextField() {
        final EditText editText = new EditText(getActivity());
        editText.setHint(R.string.rating_flow_review_hint);
        editText.setHintTextColor(mHintColor);
        editText.setBackground(null);
        editText.setTextColor(mBlackColor);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mMediumTextSize);
        editText.setTypeface(TextUtils.get(
                getContext(),
                com.handybook.handybook.library.util.TextUtils.Fonts.CIRCULAR_BOOK
        ));
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(140)});
        editText.setLines(5);
        editText.setGravity(Gravity.TOP);
        editText.setPadding(mDefaultPadding, mDefaultPadding, mDefaultPadding, mDefaultPadding);
        return editText;
    }

    @Override
    void onSubmit() {
        final String review = mReviewTextField.getText().toString();
        if (!Strings.isNullOrEmpty(review)) {
            dataManager.submitProRatingDetails(
                    Integer.parseInt(mBooking.getId()),
                    new ReviewProRequest(review, null),
                    new VoidDataManagerCallback()
            );
        }

        bus.post(new LogEvent.AddLogEvent(new RatingFlowLog.ProfileReviewLog(
                EventType.EVENT_TYPE_SUBMITTED,
                Integer.parseInt(mBooking.getProvider().getId())
        )));

        finishStep();
    }

    @Override
    void onSkip() {
        bus.post(new LogEvent.AddLogEvent(new RatingFlowLog.ProfileReviewLog(
                EventType.EVENT_TYPE_SKIPPED,
                Integer.parseInt(mBooking.getProvider().getId())
        )));
    }
}
