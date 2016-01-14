package com.handybook.handybook.booking.ui.fragment;

import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.model.LocalizedMonetaryAmount;
import com.handybook.handybook.analytics.Mixpanel;
import com.handybook.handybook.analytics.MixpanelEvent;
import com.handybook.handybook.ui.fragment.BaseDialogFragment;
import com.handybook.handybook.ui.widget.HandySnackbar;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RateServiceDialogFragment extends BaseDialogFragment
{
    static final String EXTRA_BOOKING = "com.handy.handy.EXTRA_BOOKING";
    static final String EXTRA_PRO_NAME = "com.handy.handy.EXTRA_PRO_NAME";
    static final String EXTRA_RATING = "com.handy.handy.EXTRA_RATING";
    private static final String STATE_RATING = "RATING";

    private ArrayList<ImageView> mStars = new ArrayList<>();
    private int mBookingId;
    private String mProName;
    private int mRating;

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
    @Bind(R.id.tip_section)
    View mTipSection;

    public static RateServiceDialogFragment newInstance(
            final int bookingId,
            final String proName,
            final int rating,
            final ArrayList<LocalizedMonetaryAmount> defaultTipAmounts)
    {
        final RateServiceDialogFragment rateServiceDialogFragment = new RateServiceDialogFragment();
        final Bundle bundle = new Bundle();

        bundle.putInt(EXTRA_BOOKING, bookingId);
        bundle.putString(EXTRA_PRO_NAME, proName);
        bundle.putInt(EXTRA_RATING, rating);
        bundle.putParcelableArrayList(TipFragment.EXTRA_DEFAULT_TIP_AMOUNTS, defaultTipAmounts);

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

        initStars();
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

        ArrayList<LocalizedMonetaryAmount> defaultTipAmounts =
                getArguments().getParcelableArrayList(TipFragment.EXTRA_DEFAULT_TIP_AMOUNTS);
        TipFragment tipFragment = TipFragment.newInstance(defaultTipAmounts);

        if (defaultTipAmounts != null && !defaultTipAmounts.isEmpty())
        {
            mTipSection.setVisibility(View.VISIBLE);
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.tip_layout_container, tipFragment)
                    .commit();
            mBus.post(new MixpanelEvent.TrackShowTipPrompt(MixpanelEvent.TipParentFlow.RATING_FLOW));
        }

        mBus.post(new MixpanelEvent.TrackShowRatingPrompt());

        return view;
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
    public void onReceiveRateBookingSuccess(BookingEvent.ReceiveRateBookingSuccess event)
    {
        if (getTipAmount() != null)
        {
            final String message = getString(R.string.tip_success_message_formatted, mProName);
            HandySnackbar.show(getActivity(), message, HandySnackbar.TYPE_SUCCESS);
        }
        dismiss();

        final int finalRating = mRating + 1;

        mixpanel.trackEventProRate(Mixpanel.ProRateEventType.SUBMIT, mBookingId, mProName, finalRating);

        RateServiceConfirmDialogFragment.newInstance(mBookingId, finalRating).show(getActivity()
                .getSupportFragmentManager(), "RateServiceConfirmDialogFragment");
    }

    @Subscribe
    public void onReceiveRateBookingError(BookingEvent.ReceiveRateBookingError event)
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

            mBus.post(new BookingEvent.RateBookingEvent(mBookingId, finalRating, tipAmountCents));
            if (tipAmountCents != null)
            {
                mBus.post(new MixpanelEvent.TrackSubmitTip(tipAmountCents, MixpanelEvent.TipParentFlow.RATING_FLOW));
            }
        }
    };

    private Integer getTipAmount()
    {
        final TipFragment tipFragment = (TipFragment) getChildFragmentManager()
                .findFragmentById(R.id.tip_layout_container);
        return tipFragment != null ? tipFragment.getTipAmount() : null;
    }
}
