package com.handybook.handybook.ratingflow.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.BookingOption;
import com.handybook.handybook.booking.model.LocalizedMonetaryAmount;
import com.handybook.handybook.booking.ui.view.BookingOptionsSpinnerView;
import com.handybook.handybook.booking.ui.view.BookingOptionsView;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.callback.FragmentSafeCallback;
import com.handybook.handybook.library.ui.fragment.ProgressSpinnerFragment;
import com.handybook.handybook.library.util.TextWatcherAdapter;
import com.handybook.handybook.library.util.Utils;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.constants.EventType;
import com.handybook.handybook.proteam.model.ProTeamCategoryType;
import com.handybook.handybook.proteam.model.ProviderMatchPreference;
import com.handybook.handybook.ratingflow.RatingFlowLog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RatingFlowRateAndTipFragment extends ProgressSpinnerFragment {

    public static final String CUSTOM_TIP_AMOUNT_PATTERN = "^\\$?([0-9]+(?:\\.[0-9]{1,2})?)$";
    private static final int MATCH_PREFERENCE_YES_INDEX = 0;

    @BindView(R.id.rating_flow_pro_image)
    ImageView mProImage;
    @BindView(R.id.rating_flow_rate_prompt)
    TextView mRatePrompt;
    @BindView(R.id.rating_flow_stars)
    RatingFlowFiveStarsView mStars;
    @BindView(R.id.rating_flow_match_preference_section)
    ViewGroup mMatchPreferenceSection;
    @BindView(R.id.rating_flow_tip_section)
    ViewGroup mTipSection;
    @BindView(R.id.rating_flow_tip_options)
    ViewGroup mTipOptions;
    @BindView(R.id.rating_flow_custom_tip)
    EditText mCustomTip;
    @BindView(R.id.rating_flow_next_button)
    Button mNextButton;
    @BindString(R.string.other)
    String mOther;

    private Booking mBooking;
    private List<Integer> mTipAmountsInCentsByIndex;
    private int mSelectedRating;
    private String mCurrencyChar;
    private BookingOptionsSpinnerView mMatchPreferenceSpinner;
    private BookingOptionsSpinnerView mTipOptionsSpinner;
    private BookingOptionsView.OnUpdatedListener mTipSelectionUpdateListener
            = new BookingOptionsView.OnUpdatedListener() {
        @Override
        public void onUpdate(final BookingOptionsView view) {
            if (mOther.equalsIgnoreCase(view.getCurrentValue())) {
                mCustomTip.setText(null);
                mCustomTip.setGravity(Gravity.LEFT);
                mCustomTip.setVisibility(View.VISIBLE);
                mCustomTip.clearFocus();
            }
            else {
                mCustomTip.setVisibility(View.INVISIBLE);
                Utils.hideSoftKeyboard(getActivity(), mCustomTip);
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
    private RatingFlowFiveStarsView.RatingSelectionListener mRatingSelectionListener
            = new RatingFlowFiveStarsView.RatingSelectionListener() {
        @Override
        public void onRatingSelected(final int rating) {
            mSelectedRating = rating;
            mNextButton.setEnabled(true);
        }
    };
    private TextWatcherAdapter mCustomTipTextWatcher = new TextWatcherAdapter() {
        @Override
        public void afterTextChanged(final Editable s) {
            super.afterTextChanged(s);
            final String value = mCustomTip.getText().toString();
            mCustomTip.setGravity(TextUtils.isEmpty(value) ? Gravity.LEFT : Gravity.CENTER);
            if (value.matches(CUSTOM_TIP_AMOUNT_PATTERN) && !value.startsWith(mCurrencyChar)) {
                mCustomTip.setText(mCurrencyChar + value);
                mCustomTip.setSelection(mCustomTip.getText().length());
            }
        }
    };

    @OnClick(R.id.rating_flow_next_button)
    void onNextButtonClicked() {
        final Integer tipAmountCents = getTipAmountCents();
        if (tipAmountCents == null) {
            showToast(R.string.invalid_tip_amount);
            return;
        }
        Utils.hideSoftKeyboard(getActivity(), mCustomTip);
        showProgressSpinner(true);
        final ProviderMatchPreference selectedMatchPreference =
                mMatchPreferenceSpinner.getCurrentIndex() == MATCH_PREFERENCE_YES_INDEX
                ? ProviderMatchPreference.PREFERRED
                : ProviderMatchPreference.NEVER;
        dataManager.ratePro(
                Integer.parseInt(mBooking.getId()),
                mSelectedRating,
                tipAmountCents == 0 ? null : tipAmountCents,
                selectedMatchPreference,
                new FragmentSafeCallback<Void>(this) {
                    @Override
                    public void onCallbackSuccess(final Void response) {
                        hideProgressSpinner();
                        if (getActivity() instanceof RatingFlowActivity) {
                            ((RatingFlowActivity) getActivity())
                                    .finishStepWithProRating(mSelectedRating);
                        }
                        bus.post(new LogEvent.AddLogEvent(new RatingFlowLog.RatingSuccess(
                                mSelectedRating,
                                tipAmountCents,
                                Integer.parseInt(mBooking.getId()),
                                Integer.parseInt(mBooking.getProvider().getId())
                        )));
                    }

                    @Override
                    public void onCallbackError(final DataManager.DataManagerError error) {
                        hideProgressSpinner();
                        showToast(R.string.default_error_string);
                        bus.post(new LogEvent.AddLogEvent(new RatingFlowLog.RatingError(
                                mSelectedRating,
                                tipAmountCents,
                                Integer.parseInt(mBooking.getId()),
                                Integer.parseInt(mBooking.getProvider().getId())
                        )));
                    }
                }
        );
        bus.post(new LogEvent.AddLogEvent(new RatingFlowLog.RatingAndTipLog(
                EventType.EVENT_TYPE_SUBMITTED,
                Integer.parseInt(mBooking.getProvider().getId())
        )));
    }

    @NonNull
    public static RatingFlowRateAndTipFragment newInstance(@NonNull final Booking booking) {
        final RatingFlowRateAndTipFragment fragment = new RatingFlowRateAndTipFragment();
        final Bundle arguments = new Bundle();
        arguments.putParcelable(BundleKeys.BOOKING, booking);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBooking = getArguments().getParcelable(BundleKeys.BOOKING);

        bus.post(new LogEvent.AddLogEvent(new RatingFlowLog.RatingAndTipLog(
                EventType.EVENT_TYPE_SHOWN,
                Integer.parseInt(mBooking.getProvider().getId())
        )));
    }

    @Nullable
    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState
    ) {
        ViewGroup view = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        view.addView(inflater.inflate(
                R.layout.fragment_rating_flow_rate_and_tip,
                container,
                false
        ));

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        initTipSection();
        initMatchPreferenceSection();
        initRateSection();
    }

    private void initRateSection() {
        Picasso.with(getContext())
               .load(mBooking.getProvider().getImageUrl())
               .placeholder(R.drawable.img_pro_placeholder)
               .noFade()
               .into(mProImage);
        int stringResId = R.string.rate_your_booking_prompt_formatted;
        if (mBooking.getProvider().getCategoryType() == ProTeamCategoryType.CLEANING) {
            stringResId = R.string.rate_your_cleaning_prompt_formatted;
        }
        mRatePrompt.setText(getString(
                stringResId,
                mBooking.getProvider().getFirstName()
        ));
        mStars.setRatingSelectionListener(mRatingSelectionListener);
    }

    private void initMatchPreferenceSection() {
        mMatchPreferenceSection.removeAllViews();
        final BookingOption matchPreferenceOptions = new BookingOption();
        matchPreferenceOptions.setType(BookingOption.TYPE_OPTION_PICKER);
        matchPreferenceOptions.setDefaultValue("0");
        matchPreferenceOptions.setOptions(new String[]{
                getString(R.string.yes),
                getString(R.string.no)
        });
        matchPreferenceOptions.setTitle(getString(
                R.string.would_you_like_to_work_with_x_again,
                mBooking.getProvider().getFirstName()
        ));
        mMatchPreferenceSpinner = new BookingOptionsSpinnerView(
                getActivity(),
                matchPreferenceOptions,
                null
        );
        mMatchPreferenceSpinner.findViewById(R.id.rel_layout).setBackground(null);
        mMatchPreferenceSection.addView(mMatchPreferenceSpinner);
    }

    private void initTipSection() {
        final User currentUser = userManager.getCurrentUser();
        if (mBooking.canLeaveTip()
            && currentUser != null
            && currentUser.getDefaultTipAmounts() != null
            && !currentUser.getDefaultTipAmounts().isEmpty()) {

            mTipOptions.removeAllViews();
            mTipSection.setVisibility(View.VISIBLE);

            mTipOptionsSpinner = new BookingOptionsSpinnerView(
                    getActivity(),
                    initAndGetTipOptions(currentUser.getDefaultTipAmounts()),
                    mTipSelectionUpdateListener
            );
            mTipOptionsSpinner.findViewById(R.id.rel_layout).setBackground(null);

            mTipOptions.addView(mTipOptionsSpinner);

            mCurrencyChar = currentUser.getCurrencyChar();
            mCustomTip.addTextChangedListener(mCustomTipTextWatcher);
        }
        else {
            mTipSection.setVisibility(View.GONE);
        }
    }

    @NonNull
    private BookingOption initAndGetTipOptions(
            final ArrayList<LocalizedMonetaryAmount> amounts
    ) {
        final ArrayList<String> options = new ArrayList<>();
        mTipAmountsInCentsByIndex = new ArrayList<>();

        options.add(getString(R.string.none));
        mTipAmountsInCentsByIndex.add(0);

        for (final LocalizedMonetaryAmount amount : amounts) {
            options.add(amount.getDisplayAmount());
            mTipAmountsInCentsByIndex.add(amount.getAmountInCents());
        }
        options.add(getString(R.string.other));

        final BookingOption tipOptions = new BookingOption();
        tipOptions.setType(BookingOption.TYPE_OPTION_PICKER);
        tipOptions.setDefaultValue("0");
        tipOptions.setOptions(options.toArray(new String[options.size()]));
        tipOptions.setTitle(getString(R.string.leave_a_tip_prompt));
        return tipOptions;
    }

    @Nullable
    private Integer getTipAmountCents() {
        if (mTipSection.getVisibility() != View.VISIBLE) {
            return 0;
        }
        Integer tipAmount = null;
        if (mOther.equalsIgnoreCase(mTipOptionsSpinner.getCurrentValue())) {
            tipAmount = getCustomTipAmountCents();
        }
        else {
            final int selectedTipOptionIndex = mTipOptionsSpinner.getCurrentIndex();
            if (selectedTipOptionIndex < mTipAmountsInCentsByIndex.size()) {
                tipAmount = mTipAmountsInCentsByIndex.get(selectedTipOptionIndex);
            }
        }
        return tipAmount;
    }

    @Nullable
    private Integer getCustomTipAmountCents() {
        try {
            final String customTipAmount =
                    mCustomTip.getText().toString().replaceFirst(CUSTOM_TIP_AMOUNT_PATTERN, "$1");
            return Utils.convertToCents(Float.parseFloat(customTipAmount));
        }
        catch (Exception e) {
            Crashlytics.logException(e);
            return null;
        }
    }
}
