package com.handybook.handybook.ui.fragment;

import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.core.LocalizedMonetaryAmount;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.data.Mixpanel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RateServiceDialogFragment extends BaseDialogFragment {
    static final String EXTRA_BOOKING = "com.handy.handy.EXTRA_BOOKING";
    static final String EXTRA_PRO_NAME = "com.handy.handy.EXTRA_PRO_NAME";
    static final String EXTRA_RATING = "com.handy.handy.EXTRA_RATING";
    static final String EXTRA_DEFAULT_TIP_AMOUNTS = "com.handy.handy.EXTRA_DEFAULT_TIP_AMOUNTS";
    private static final String STATE_RATING = "RATING";

    private ArrayList<ImageView> stars = new ArrayList<>();
    private int booking;
    private String proName;
    private int rating;
    private int tipAmount;
    private boolean sendTipAmount = false;
    private boolean customTipSelected = false;
    private ArrayList<LocalizedMonetaryAmount> defaultTipAmounts;
    private Map tipMapping = new HashMap();

    @Inject DataManager dataManager;

    @Bind(R.id.service_icon)
    ImageView serviceIcon;
    @Bind(R.id.title_text)
    TextView titleText;
    @Bind(R.id.message_text)
    TextView messageText;
    @Bind(R.id.submit_button)
    Button submitButton;
    @Bind(R.id.skip_button)
    Button skipButton;
    @Bind(R.id.submit_progress)
    ProgressBar submitProgress;
    @Bind(R.id.ratings_layout)
    LinearLayout ratingsLayout;
    @Bind(R.id.submit_button_layout)
    View submitButtonLayout;
    @Bind(R.id.star_1)
    ImageView star1;
    @Bind(R.id.star_2)
    ImageView star2;
    @Bind(R.id.star_3)
    ImageView star3;
    @Bind(R.id.star_4)
    ImageView star4;
    @Bind(R.id.star_5)
    ImageView star5;
    @Bind(R.id.tip_amount_radio_group)
    RadioGroup tipAmountRadioGroup;
    @Bind(R.id.tip_layout)
    FrameLayout tipLayout;
    @Bind(R.id.custom_tip_amount_wrapper)
    LinearLayout customTipAmountWrapper;
    @Bind(R.id.custom_tip_amount)
    EditText customTipAmount;

    public static RateServiceDialogFragment newInstance(final int bookingId, final String proName,
                                                        final int rating, final ArrayList<LocalizedMonetaryAmount> defaultTipAmounts) {
        final RateServiceDialogFragment rateServiceDialogFragment = new RateServiceDialogFragment();
        final Bundle bundle = new Bundle();

        bundle.putInt(EXTRA_BOOKING, bookingId);
        bundle.putString(EXTRA_PRO_NAME, proName);
        bundle.putInt(EXTRA_RATING, rating);
        bundle.putParcelableArrayList(EXTRA_DEFAULT_TIP_AMOUNTS, defaultTipAmounts);

        rateServiceDialogFragment.setArguments(bundle);
        return rateServiceDialogFragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final View view = inflater.inflate(R.layout.dialog_rate_service, container, true);
        ButterKnife.bind(this, view);

        final Bundle args = getArguments();
        booking = args.getInt(EXTRA_BOOKING);
        proName = args.getString(EXTRA_PRO_NAME);
        defaultTipAmounts = args.getParcelableArrayList(EXTRA_DEFAULT_TIP_AMOUNTS);
        updateTipAmountDisplay(defaultTipAmounts);

        if (savedInstanceState != null) rating = savedInstanceState.getInt(STATE_RATING , -1);
        else rating = args.getInt(EXTRA_RATING, -1);

        initStars();
        initTipListeners();
        setRating(rating);

        serviceIcon.setColorFilter(getResources().getColor(R.color.handy_green),
                PorterDuff.Mode.SRC_ATOP);

        titleText.setText(getResources().getString(R.string.how_was_last_service));
        messageText.setText(getResources().getString(R.string.please_rate_pro));

        submitButton.setOnClickListener(submitListener);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                dismiss();
            }
        });

        return view;
    }

    private void updateTipAmountDisplay(final ArrayList<LocalizedMonetaryAmount> defaultTipAmounts) {
        if (defaultTipAmounts.isEmpty()) {
            tipLayout.setVisibility(View.GONE);
        }
        else {
            int maxEntriesToDisplay = Math.min(defaultTipAmounts.size(), 3); // MAKE IT CONSTANT! PRAISE BE!

            for (int i = 0; i < maxEntriesToDisplay; i++) {
                int radioButtonGroupIndex = i + 1;
                if (tipAmountRadioGroup.getChildCount() > radioButtonGroupIndex) {
                    RadioButton childRadioButton = (RadioButton) tipAmountRadioGroup.getChildAt(radioButtonGroupIndex);
                    childRadioButton.setText(defaultTipAmounts.get(i).getDisplayAmount());

                    // Create a mapping of the child radio button to the tip amount
                    tipMapping.put(childRadioButton, defaultTipAmounts.get(i).getAmountInCents());
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_RATING, rating);
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

    private void initStars() {
        stars.add(star1);
        stars.add(star2);
        stars.add(star3);
        stars.add(star4);
        stars.add(star5);

        // init all stars to empty
        for (final ImageView star : stars) {
            star.setColorFilter(getResources().getColor(R.color.light_grey),
                    PorterDuff.Mode.SRC_ATOP);
        }

        // fill stars when dragging across them
        ratingsLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, final MotionEvent event) {
                for (int i = 0; i < ratingsLayout.getChildCount(); i++) {
                    final RelativeLayout layout = (RelativeLayout) ratingsLayout.getChildAt(i);
                    final Rect outRect = new Rect(layout.getLeft(), layout.getTop(),
                            layout.getRight(), layout.getBottom());

                    if (outRect.contains((int) event.getX(), (int) event.getY())) {
                        final int starsIndex = stars.indexOf((ImageView) layout.getChildAt(0));
                        setRating(starsIndex);
                        break;
                    }
                }

                return true;
            }
        });
    }

    private void initTipListeners() {
        tipAmountRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup rGroup, int checkedId) {
                RadioButton checkedRadioButton = (RadioButton) rGroup.findViewById(checkedId);

                if (tipMapping.containsKey(checkedRadioButton)) {
                    setTipAmount((int) tipMapping.get(checkedRadioButton));
                    setSendTipAmount(true);
                    setCustomTipSelected(false);
                    customTipAmountWrapper.setVisibility(View.GONE);
                } else if (pickedOtherAmount(checkedRadioButton, rGroup)) {
                    setTipAmount(0);
                    setSendTipAmount(true);
                    setCustomTipSelected(true);
                    customTipAmountWrapper.setVisibility(View.VISIBLE);
                    customTipAmountWrapper.requestFocus();
                } else {
                    setCustomTipSelected(false);
                    setSendTipAmount(false);
                    customTipAmountWrapper.setVisibility(View.GONE);
                }
            }

            private boolean pickedOtherAmount(final RadioButton checkedRadioButton, final RadioGroup radioGroup) {
                return checkedRadioButton.getId() == radioGroup.getChildAt(radioGroup.getChildCount() - 1).getId();
            }
        });
    }

    private void setTipAmount(final int tipAmount) {
        this.tipAmount = tipAmount;
    }

    private void setSendTipAmount(final boolean sendTipAmount) {
        this.sendTipAmount = sendTipAmount;
    }
    private void setCustomTipSelected(final boolean customTipSelected) {
        this.customTipSelected = customTipSelected;
    }

    private void setRating(final int rating) {
        this.rating = rating;

        for (int j = 0; j < stars.size(); j++) {
            final ImageView star = stars.get(j);

            if (j <= rating) star.clearColorFilter();
            else star.setColorFilter(getResources().getColor(R.color.light_grey),
                    PorterDuff.Mode.SRC_ATOP);
        }

        if (rating >= 0) submitButtonLayout.setVisibility(View.VISIBLE);
    }

    private View.OnClickListener submitListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            disableInputs();
            submitProgress.setVisibility(View.VISIBLE);
            submitButton.setText(null);

            final int finalRating = rating + 1;
            final Integer postTipAmount = getTipAmount();

            dataManager.ratePro(booking, finalRating, postTipAmount, new DataManager.Callback<Void>() {
                @Override
                public void onSuccess(final Void response) {
                    if (!allowCallbacks) return;
                    dismiss();

                    mixpanel.trackEventProRate(Mixpanel.ProRateEventType.SUBMIT, booking,
                            proName, finalRating);

                    RateServiceConfirmDialogFragment.newInstance(booking, finalRating).show(getActivity()
                                .getSupportFragmentManager(), "RateServiceConfirmDialogFragment");
                }

                @Override
                public void onError(final DataManager.DataManagerError error) {
                    if (!allowCallbacks) return;
                    submitProgress.setVisibility(View.GONE);
                    submitButton.setText(R.string.submit);
                    skipButton.setVisibility(View.VISIBLE);
                    enableInputs();
                    dataManagerErrorHandler.handleError(getActivity(), error);
                }
            });
        }

        private Integer getTipAmount() {
            if (sendTipAmount) {
                return customTipSelected ? getCustomTipAmount() : tipAmount;
            }
            else {
                return null;
            }
        }

        private Integer getCustomTipAmount() {
            return Integer.parseInt("" + customTipAmount.getText()) * 100;
        }
    };
}
