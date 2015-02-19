package com.handybook.handybook.ui.fragment;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.ui.widget.LimitedEditText;
import com.handybook.handybook.util.Utils;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RateServiceConfirmDialogFragment extends BaseDialogFragment {
    static final String EXTRA_BOOKING = "com.handy.handy.EXTRA_BOOKING";
    static final String EXTRA_RATING = "com.handy.handy.EXTRA_RATING";

    private int rating;
    private int booking;

    @InjectView(R.id.service_icon) ImageView serviceIcon;
    @InjectView(R.id.service_icon_img) ImageView serviceIconImage;
    @InjectView(R.id.title_text) TextView titleText;
    @InjectView(R.id.message_text) TextView messageText;
    @InjectView(R.id.feedback_text) LimitedEditText feedbackText;
    @InjectView(R.id.submit_button) Button submitButton;
    @InjectView(R.id.submit_progress) ProgressBar submitProgress;
    @InjectView(R.id.skip_button) Button skipButton;
    @InjectView(R.id.submit_button_layout) View submitButtonLayout;

    public static RateServiceConfirmDialogFragment newInstance(final int bookingId, final int rating) {
        final RateServiceConfirmDialogFragment rateServiceConfirmDialogFragment
                = new RateServiceConfirmDialogFragment();

        final Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_BOOKING, bookingId);
        bundle.putInt(EXTRA_RATING, rating);
        rateServiceConfirmDialogFragment.setArguments(bundle);

        return rateServiceConfirmDialogFragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle args = getArguments();
        booking = args.getInt(EXTRA_BOOKING);
        rating = args.getInt(EXTRA_RATING);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        final View view = inflater.inflate(R.layout.dialog_rate_service_confirm, container, true);
        ButterKnife.inject(this, view);

        initLayout(rating);
        submitButton.setOnClickListener(submitListener);

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (rating == 4) {
            // dismiss handler automatically if rating is a 4
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismiss();
                }
            }, 1000);
        }
    }

    @Override
    protected void enableInputs() {
        super.enableInputs();
        submitButton.setClickable(true);
        skipButton.setClickable(true);
    }

    @Override
    protected void disableInputs() {
        super.disableInputs();
        submitButton.setClickable(false);
        skipButton.setClickable(false);
    }

    private void initLayout(final int rating) {
        // setup different modal layouts according to rating
        if (rating >= 4) {
            serviceIconImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart));

            serviceIcon.setColorFilter(getResources().getColor(R.color.handy_love),
                    PorterDuff.Mode.SRC_ATOP);

            titleText.setText(getResources().getString(R.string.glad_you_enjoy));

            if (rating == 4) {
                titleText.setPadding(titleText.getPaddingLeft(), titleText.getPaddingTop(),
                        titleText.getPaddingRight(), titleText.getPaddingBottom()
                                + Utils.toDP(32, getActivity()));

                messageText.setVisibility(View.GONE);
                submitButtonLayout.setVisibility(View.GONE);
            }
            else {
                messageText.setText(getResources().getString(R.string.good_vibes));
                messageText.setTextColor(getResources().getColor(R.color.black));
                feedbackText.setMaxCharacters(140);
                feedbackText.setMaxLines(3);
                feedbackText.setVisibility(View.VISIBLE);
                submitButton.setText(getResources().getString(R.string.send));
                skipButton.setVisibility(View.VISIBLE);
            }
        }
        else {
            titleText.setText(getResources().getString(R.string.thanks_for_feedback));
            messageText.setText(getResources().getString(R.string.were_sorry_feedback));
        }
    }

    private View.OnClickListener submitListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            final String positiveFeedback = feedbackText.getText().toString();
            if (positiveFeedback.length() > 0) {
                disableInputs();
                submitProgress.setVisibility(View.VISIBLE);
                submitButton.setText(null);

                dataManager.submitProRatingDetails(booking, positiveFeedback,
                        new DataManager.Callback<Void>() {
                    @Override
                    public void onSuccess(final Void response) {
                        if (!allowCallbacks) return;
                        dismiss();
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error) {
                        if (!allowCallbacks) return;
                        submitProgress.setVisibility(View.GONE);
                        submitButton.setText(R.string.send);
                        enableInputs();
                        dataManagerErrorHandler.handleError(getActivity(), error);
                    }
                });
            }
            else dismiss();
        }
    };
}
