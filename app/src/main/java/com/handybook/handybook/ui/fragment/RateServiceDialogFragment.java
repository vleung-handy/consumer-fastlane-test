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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.core.LocalizedMonetaryAmount;
import com.handybook.handybook.data.Mixpanel;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.util.Utils;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RateServiceDialogFragment extends BaseDialogFragment
{
    static final String EXTRA_BOOKING = "com.handy.handy.EXTRA_BOOKING";
    static final String EXTRA_PRO_NAME = "com.handy.handy.EXTRA_PRO_NAME";
    static final String EXTRA_RATING = "com.handy.handy.EXTRA_RATING";
    static final String EXTRA_DEFAULT_TIP_AMOUNTS = "com.handy.handy.EXTRA_DEFAULT_TIP_AMOUNTS";
    private static final String STATE_RATING = "RATING";

    static final int MAX_CUSTOM_TIP_VALUES = 3; //our UI currently supports None, Custom, and 3 pre-defined tip amounts coming from the server

    private ArrayList<ImageView> mStars = new ArrayList<>();
    private int mBookingId;
    private String mProName;
    private int mRating;
    private int mTipAmount;
    private Map<RadioButton, Integer> mRadioButtonToTipAmount = new HashMap<>();

    private boolean mSendTipAmount = false;
    private boolean mCustomTipSelected = false;

    @Bind(R.id.service_icon)
    ImageView mServiceIcon;
    @Bind(R.id.title_text)
    TextView mTitleText;
    @Bind(R.id.message_text)
    TextView mMessageText;
    @Bind(R.id.submit_button)
    Button mSubmitButton;
    @Bind(R.id.skip_button)
    Button mSkipButton;
    @Bind(R.id.submit_progress)
    ProgressBar mSubmitProgress;
    @Bind(R.id.ratings_layout)
    LinearLayout mRatingsLayout;
    @Bind(R.id.submit_button_layout)
    View mSubmitButtonLayout;
    @Bind(R.id.star_1)
    ImageView mStar1;
    @Bind(R.id.star_2)
    ImageView mStar2;
    @Bind(R.id.star_3)
    ImageView mStar3;
    @Bind(R.id.star_4)
    ImageView mStar4;
    @Bind(R.id.star_5)
    ImageView mStar5;
    @Bind(R.id.tip_amount_radio_group)
    RadioGroup mTipAmountRadioGroup;
    @Bind(R.id.tip_layout)
    LinearLayout mTipLayout;
    @Bind(R.id.custom_tip_amount_wrapper)
    LinearLayout mCustomTipAmountWrapperLayout;
    @Bind(R.id.custom_tip_amount)
    EditText mCustomTipAmountText;

    public static RateServiceDialogFragment newInstance(final int bookingId, final String proName,
                                                        final int rating, final ArrayList<LocalizedMonetaryAmount> defaultTipAmounts)
    {
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
                             final Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        final View view = inflater.inflate(R.layout.dialog_rate_service, container, true);
        ButterKnife.bind(this, view);

        final Bundle args = getArguments();

        if (savedInstanceState != null)
        {
            mRating = savedInstanceState.getInt(STATE_RATING, -1);
        }
        else
        {
            mRating = args.getInt(EXTRA_RATING, -1);
        }

        mBookingId = args.getInt(EXTRA_BOOKING);
        mProName = args.getString(EXTRA_PRO_NAME);

        ArrayList<LocalizedMonetaryAmount> defaultTipAmounts = args.getParcelableArrayList(EXTRA_DEFAULT_TIP_AMOUNTS);
        updateTipAmountDisplay(defaultTipAmounts);

        initStars();
        initTipListeners();
        setRating(mRating);

        mServiceIcon.setColorFilter(getResources().getColor(R.color.handy_green), PorterDuff.Mode.SRC_ATOP);

        mTitleText.setText(getResources().getString(R.string.how_was_last_service));
        mMessageText.setText(getResources().getString(R.string.please_rate_pro));

        mSubmitButton.setOnClickListener(mSubmitListener);
        mSkipButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                dismiss();
            }
        });

        return view;
    }

    private void updateTipAmountDisplay(final ArrayList<LocalizedMonetaryAmount> defaultTipAmounts)
    {
        if (defaultTipAmounts != null && !defaultTipAmounts.isEmpty())
        {
            mTipLayout.setVisibility(View.VISIBLE);
            int maxEntriesToDisplay = Math.min(defaultTipAmounts.size(), MAX_CUSTOM_TIP_VALUES);

            for (int i = 0; i < maxEntriesToDisplay; i++)
            {
                int radioButtonGroupIndex = i + 1;
                if (mTipAmountRadioGroup.getChildCount() > radioButtonGroupIndex)
                {
                    RadioButton childRadioButton = (RadioButton) mTipAmountRadioGroup.getChildAt(radioButtonGroupIndex);
                    childRadioButton.setText(defaultTipAmounts.get(i).getDisplayAmount());

                    // Create a mapping of the child radio button to the tip amount
                    mRadioButtonToTipAmount.put(childRadioButton, defaultTipAmounts.get(i).getAmountInCents());
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_RATING, mRating);
    }

    @Override
    protected void enableInputs()
    {
        super.enableInputs();
        mSubmitButton.setClickable(true);
        mSkipButton.setClickable(true);
    }

    @Override
    protected void disableInputs()
    {
        super.disableInputs();
        mSubmitButton.setClickable(false);
        mSkipButton.setClickable(false);
    }

    private void initStars()
    {
        mStars.add(mStar1);
        mStars.add(mStar2);
        mStars.add(mStar3);
        mStars.add(mStar4);
        mStars.add(mStar5);

        // init all stars to empty
        for (final ImageView star : mStars)
        {
            star.setColorFilter(getResources().getColor(R.color.light_grey), PorterDuff.Mode.SRC_ATOP);
        }

        // fill mStars when dragging across them
        mRatingsLayout.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(final View v, final MotionEvent event)
            {
                for (int i = 0; i < mRatingsLayout.getChildCount(); i++)
                {
                    final RelativeLayout starWrapperLayout = (RelativeLayout) mRatingsLayout.getChildAt(i);
                    final Rect outRect = new Rect(starWrapperLayout.getLeft(), starWrapperLayout.getTop(),
                            starWrapperLayout.getRight(), starWrapperLayout.getBottom());

                    if (outRect.contains((int) event.getX(), (int) event.getY()))
                    {
                        final int starsIndex = mStars.indexOf((ImageView) starWrapperLayout.getChildAt(0));
                        setRating(starsIndex);
                        break;
                    }
                }
                return true;
            }
        });
    }

    private void initTipListeners()
    {
        mTipAmountRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup rGroup, int checkedId)
            {
                RadioButton checkedRadioButton = (RadioButton) rGroup.findViewById(checkedId);

                if (mRadioButtonToTipAmount.containsKey(checkedRadioButton))
                {
                    setTipAmount(mRadioButtonToTipAmount.get(checkedRadioButton));
                    setSendTipAmount(true);
                    setCustomTipSelected(false);
                    mCustomTipAmountWrapperLayout.setVisibility(View.GONE);
                } else if (pickedOtherAmount(checkedRadioButton, rGroup))
                {
                    setTipAmount(0);
                    setSendTipAmount(true);
                    setCustomTipSelected(true);
                    mCustomTipAmountWrapperLayout.setVisibility(View.VISIBLE);
                    mCustomTipAmountWrapperLayout.requestFocus();
                } else
                {
                    setCustomTipSelected(false);
                    setSendTipAmount(false);
                    mCustomTipAmountWrapperLayout.setVisibility(View.GONE);
                }
            }

            private boolean pickedOtherAmount(final RadioButton checkedRadioButton, final RadioGroup radioGroup)
            {
                return checkedRadioButton.getId() == radioGroup.getChildAt(radioGroup.getChildCount() - 1).getId();
            }
        });

        mCustomTipAmountText.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (hasFocus)
                {
                    Utils.showSoftKeyboard(getActivity(), v);
                } else
                {
                    Utils.hideSoftKeyboard(getActivity(), v);
                }
            }
        });
    }

    private void setTipAmount(final int tipAmount)
    {
        mTipAmount = tipAmount;
    }

    private void setSendTipAmount(final boolean sendTipAmount)
    {
        mSendTipAmount = sendTipAmount;
    }

    private void setCustomTipSelected(final boolean customTipSelected)
    {
        mCustomTipSelected = customTipSelected;
    }

    private void setRating(final int rating)
    {
        mRating = rating;

        for (int j = 0; j < mStars.size(); j++)
        {
            final ImageView star = mStars.get(j);

            if (j <= mRating)
            {
                star.clearColorFilter();
            }
            else
            {
                star.setColorFilter(getResources().getColor(R.color.light_grey),
                        PorterDuff.Mode.SRC_ATOP);
            }
        }

        if (mRating >= 0)
        {
            mSubmitButtonLayout.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe
    public void onReceiveRateBookingSuccess(HandyEvent.ReceiveRateBookingSuccess event)
    {
        dismiss();

        final int finalRating = mRating + 1;

        mixpanel.trackEventProRate(Mixpanel.ProRateEventType.SUBMIT, mBookingId, mProName, finalRating);

        RateServiceConfirmDialogFragment.newInstance(mBookingId, finalRating).show(getActivity()
                .getSupportFragmentManager(), "RateServiceConfirmDialogFragment");
    }

    @Subscribe
    public void onReceiveRateBookingError(HandyEvent.ReceiveRateBookingError event)
    {
        mSubmitProgress.setVisibility(View.GONE);
        mSubmitButton.setText(R.string.submit);
        mSkipButton.setVisibility(View.VISIBLE);
        enableInputs();
        dataManagerErrorHandler.handleError(getActivity(), event.error);
    }

    private View.OnClickListener mSubmitListener = new View.OnClickListener()
    {
        @Override
        public void onClick(final View v)
        {
            disableInputs();
            mSubmitProgress.setVisibility(View.VISIBLE);
            mSubmitButton.setText(null);

            final int finalRating = mRating + 1;
            final Integer tipAmountCents = getTipAmount();

            mBus.post(new HandyEvent.RateBookingEvent(mBookingId, finalRating, tipAmountCents));
        }

        private Integer getTipAmount()
        {
            if (mSendTipAmount)
            {
                return mCustomTipSelected ? getCustomTipAmount() : mTipAmount;
            }
            else
            {
                return null;
            }
        }

        private Integer getCustomTipAmount()
        {
            return Integer.parseInt("" + mCustomTipAmountText.getText()) * 100;
        }
    };
}
