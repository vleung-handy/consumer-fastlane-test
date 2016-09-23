package com.handybook.handybook.booking.ui.fragment;

import android.accounts.NetworkErrorException;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.model.LocalizedMonetaryAmount;
import com.handybook.handybook.booking.rating.PrerateProInfo;
import com.handybook.handybook.booking.rating.RateImprovementDialogFragment;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.RatingDialogLog;
import com.handybook.handybook.module.configuration.event.ConfigurationEvent;
import com.handybook.handybook.module.configuration.model.Configuration;
import com.handybook.handybook.module.proteam.model.ProviderMatchPreference;
import com.handybook.handybook.library.ui.fragment.BaseDialogFragment;
import com.handybook.handybook.ui.widget.HandySnackbar;
import com.handybook.handybook.library.util.FragmentUtils;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RateServiceDialogFragment extends BaseDialogFragment
{
    private static final String EXTRA_BOOKING = "com.handy.handy.EXTRA_BOOKING";
    private static final String EXTRA_PRO_NAME = "com.handy.handy.EXTRA_PRO_NAME";
    private static final String EXTRA_RATING = "com.handy.handy.EXTRA_RATING";
    private static final String STATE_RATING = "RATING";
    private static final int GOOD_RATING = 4; //threshold for what is considered a good rating.
    private static final int RAW_RATING_THRESHOLD = 3; //raw threshold for what is considered a good rating.
    private static final String RATE_SERVICE_CONFIRM_DIALOG_FRAGMENT = "RateServiceConfirmDialogFragment";

    @Bind(R.id.rate_dialog_service_icon)
    ImageView mServiceIcon;
    @Bind(R.id.rate_dialog_title_text)
    TextView mTitleText;
    @Bind(R.id.rate_dialog_pro_team_member)
    TextView mTextProTeamMember;
    @Bind(R.id.rate_dialog_submit_button)
    Button mSubmitButton;
    @Bind(R.id.rate_dialog_skip_button)
    Button mSkipButton;
    @Bind(R.id.rate_dialog_submit_progress)
    ProgressBar mSubmitProgress;
    @Bind(R.id.rate_dialog_ratings_layout)
    LinearLayout mRatingsLayout;

    /**
     * This layout also contains the pro team layout
     */
    @Bind(R.id.rate_dialog_submit_button_layout)
    View mSubmitButtonLayout;
    @Bind(R.id.rate_dialog_star_1)
    ImageView mStar1;
    @Bind(R.id.rate_dialog_star_2)
    ImageView mStar2;
    @Bind(R.id.rate_dialog_star_3)
    ImageView mStar3;
    @Bind(R.id.rate_dialog_star_4)
    ImageView mStar4;
    @Bind(R.id.rate_dialog_star_5)
    ImageView mStar5;
    @Bind(R.id.rate_dialog_tip_section)
    View mTipSection;
    @Bind(R.id.rate_dialog_pro_team_section)
    ViewGroup mProTeamSection;
    @Bind(R.id.rate_dialog_scrollview)
    ScrollView mScroll;
    @Bind(R.id.rate_pro_team_tip_divider)
    View mTipDivider;

    private Configuration mConfiguration;
    private RateProTeamFragment mRateProTeamFragment;

    private int mBookingId;
    private int mRating;
    private String mProName;
    private PrerateProInfo mPrerateProInfo;
    private ArrayList<ImageView> mStars = new ArrayList<>();
    private View.OnClickListener mSubmitListener;

    {
        mSubmitListener = new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                disableInputs();
                showProgress();
                mSubmitButton.setText(null);
                final int finalRating = mRating + 1;
                final Integer tipAmountCents = getTipAmount();

                ProviderMatchPreference matchPreference;

                if (mRateProTeamFragment != null)
                {
                    matchPreference = mRateProTeamFragment.getNewProviderMatchPreference();
                }
                else
                {
                    matchPreference = mPrerateProInfo.getProviderMatchPreference();
                }

                mBus.post(new LogEvent.AddLogEvent(new RatingDialogLog.Submitted(
                        finalRating,
                        matchPreference,
                        tipAmountCents
                )));

                mBus.post(new BookingEvent.RateBookingEvent(
                        mBookingId,
                        finalRating,
                        tipAmountCents,
                        matchPreference
                ));
            }
        };
    }

    public static RateServiceDialogFragment newInstance(
            final int bookingId,
            final String proName,
            final int rating,
            final ArrayList<LocalizedMonetaryAmount> defaultTipAmounts,
            final String currencyPrefixSymbol
    )
    {
        final RateServiceDialogFragment rateServiceDialogFragment = new RateServiceDialogFragment();
        final Bundle bundle = new Bundle();

        bundle.putInt(EXTRA_BOOKING, bookingId);
        bundle.putString(EXTRA_PRO_NAME, proName);
        bundle.putInt(EXTRA_RATING, rating);
        bundle.putParcelableArrayList(TipFragment.EXTRA_DEFAULT_TIP_AMOUNTS, defaultTipAmounts);
        bundle.putString(TipFragment.EXTRA_CURRENCY_CHAR, currencyPrefixSymbol);

        rateServiceDialogFragment.setArguments(bundle);
        return rateServiceDialogFragment;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (mPrerateProInfo == null)
        {
            mBus.post(new BookingEvent.RequestPrerateProInfo(String.valueOf(mBookingId)));
            disableInputs();
            showProgress();
        }

        if (mConfiguration == null)
        {
            mBus.post(new ConfigurationEvent.RequestConfiguration());
        }

    }

    @Override
    protected void disableInputs()
    {
        super.disableInputs();
        mSubmitButton.setClickable(false);
        mSkipButton.setClickable(false);
    }

    @Override
    protected void enableInputs()
    {
        super.enableInputs();
        mSubmitButton.setClickable(true);
        mSkipButton.setClickable(true);
    }

    @Override
    public View onCreateView(
            final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState
    )
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

        if (TextUtils.isEmpty(mProName))
        {
            mTitleText.setText(getResources().getString(R.string.how_was_last_service));
        }
        else
        {
            mTitleText.setText(String.format(getString(R.string.how_was_last_service_with), mProName));
        }
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
        String currency = getArguments().getString(TipFragment.EXTRA_CURRENCY_CHAR);
        TipFragment tipFragment = TipFragment.newInstance(defaultTipAmounts, currency);
        if (defaultTipAmounts != null && !defaultTipAmounts.isEmpty())
        {
            mTipDivider.setVisibility(View.VISIBLE);
            mTipSection.setVisibility(View.VISIBLE);
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.rate_dialog_tip_layout_container, tipFragment)
                    .commit();
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(final Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_RATING, mRating);
    }

    @Subscribe
    public void onReceivePrerateInfoSuccess(
            BookingEvent.ReceivePrerateProInfoSuccess receivePrerateProInfoSuccess
    )
    {
        mPrerateProInfo = receivePrerateProInfoSuccess.getPrerateProInfo();

        if (mPrerateProInfo.getProviderMatchPreference() == ProviderMatchPreference.PREFERRED)
        {
            mTextProTeamMember.setVisibility(View.VISIBLE);
        }
        else
        {
            mTextProTeamMember.setVisibility(View.GONE);
        }

        initProTeamSection();
        hideProgress();
        enableInputs();

    }

    @Subscribe
    public void onReceivePrerateInfoSuccess(
            BookingEvent.ReceivePrerateProInfoError receivePrerateProInfoSuccess
    )
    {
        Crashlytics.logException(new NetworkErrorException("Could not get prerate_pro_info"));
        dismiss();
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
        if (finalRating < GOOD_RATING)
        {
            FragmentUtils.safeLaunchDialogFragment(
                    RateImprovementDialogFragment.newInstance(
                            String.valueOf(mBookingId),
                            mPrerateProInfo
                    ),
                    getActivity(),
                    RateImprovementDialogFragment.class.getSimpleName()
            );
        }
        else
        {
            FragmentUtils.safeLaunchDialogFragment(
                    RateServiceConfirmDialogFragment.newInstance(mBookingId, finalRating),
                    getActivity(),
                    RATE_SERVICE_CONFIRM_DIALOG_FRAGMENT);
        }
    }

    @Subscribe
    public void onReceiveConfigurationSuccess(
            final ConfigurationEvent.ReceiveConfigurationSuccess event
    )
    {
        if (event != null)
        {
            mConfiguration = event.getConfiguration();
            if (isProTeamEnabled())
            {
                initProTeamSection();
            }
        }
    }

    private boolean isProTeamEnabled()
    {
        return mConfiguration != null && mConfiguration.isMyProTeamEnabled();
    }

    @Subscribe
    public void onReceiveRateBookingError(BookingEvent.ReceiveRateBookingError event)
    {
        hideProgress();
        mSubmitButton.setText(R.string.submit);
        mSkipButton.setVisibility(View.VISIBLE);
        enableInputs();
        dataManagerErrorHandler.handleError(getActivity(), event.error);
    }

    /**
     * Sets the rating field and fills the stars yellow in the layout, also shows the submit button
     *
     * @param rating Zero indexed rating
     */
    private void setRating(final int rating)
    {
        mRating = rating;
        for (int starIndex = 0; starIndex < mStars.size(); starIndex++)
        {
            final ImageView star = mStars.get(starIndex);

            if (starIndex <= mRating)
            {
                star.clearColorFilter();
            }
            else
            {
                star.setColorFilter(
                        ContextCompat.getColor(getContext(), R.color.light_grey),
                        PorterDuff.Mode.SRC_ATOP
                );
            }
        }
        if (mRating >= 0)
        {
            mSubmitButtonLayout.setVisibility(View.VISIBLE);

            //this is zero indexed, so this means 4 stars or higher
            if (!isProTeamEnabled() || (rating >= RAW_RATING_THRESHOLD && mPrerateProInfo != null &&
                    mPrerateProInfo.getProviderMatchPreference() == ProviderMatchPreference.PREFERRED))
            {
                mProTeamSection.setVisibility(View.GONE);
            }
            else
            {
                mProTeamSection.setVisibility(View.VISIBLE);
            }

            //must make this call to update with new rating, even if the above sets the layout to
            //GONE. This is needed for tracking previous rating.
            if (mRateProTeamFragment != null)
            {
                mRateProTeamFragment.updateWithNewRating(mRating);
            }
        }
    }

    private Integer getTipAmount()
    {
        final TipFragment tipFragment = (TipFragment) getChildFragmentManager()
                .findFragmentById(R.id.rate_dialog_tip_layout_container);
        return tipFragment != null ? tipFragment.getTipAmount() : null;
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
            star.setColorFilter(ContextCompat.getColor(getContext(), R.color.light_grey), PorterDuff.Mode.SRC_ATOP);
        }
        // fill mStars when dragging across them
        mRatingsLayout.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(final View v, final MotionEvent event)
            {
                for (int i = 0; i < mRatingsLayout.getChildCount(); i++)
                {
                    final ImageView imageView = (ImageView) mRatingsLayout.getChildAt(i);
                    final Rect outRect = new Rect(
                            imageView.getLeft(),
                            imageView.getTop(),
                            imageView.getRight(),
                            imageView.getBottom()
                    );
                    if (outRect.contains((int) event.getX(), (int) event.getY()))
                    {
                        final int starsIndex = mStars.indexOf(imageView);
                        setRating(starsIndex);
                        break;
                    }
                }
                scrollToBottom();
                return true;
            }
        });
    }

    private void initProTeamSection()
    {
        if (isProTeamEnabled() && mPrerateProInfo != null)
        {
            mRateProTeamFragment = (RateProTeamFragment) getChildFragmentManager().findFragmentByTag(RateProTeamFragment.class.getSimpleName());
            if (mRateProTeamFragment == null)
            {
                mRateProTeamFragment = RateProTeamFragment.newInstance(mRating, mProName, mPrerateProInfo.getProviderMatchPreference());
                getChildFragmentManager()
                        .beginTransaction()
                        .add(R.id.rate_pro_team_container, mRateProTeamFragment)
                        .commit();
                scrollToBottom();
            }
        }
    }

    private void scrollToBottom()
    {
        new Handler().postDelayed(new Runnable()
        { // Scroll to the bottom
            @Override
            public void run()
            {
                mScroll.fullScroll(View.FOCUS_DOWN);
            }
        }, 100);
    }

    private void showProgress()
    {
        mSubmitProgress.setVisibility(View.VISIBLE);
    }

    private void hideProgress()
    {
        mSubmitProgress.setVisibility(View.GONE);
    }
}
