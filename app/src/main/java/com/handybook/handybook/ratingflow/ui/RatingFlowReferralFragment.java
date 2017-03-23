package com.handybook.handybook.ratingflow.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.common.base.Strings;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.rating.ReviewProRequest;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.data.VoidDataManagerCallback;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.library.util.FragmentUtils;
import com.handybook.handybook.library.util.StringUtils;
import com.handybook.handybook.library.util.TextUtils;
import com.handybook.handybook.library.util.Utils;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.user.ShareModalLog;
import com.handybook.handybook.ratingflow.RatingFlowLog;
import com.handybook.handybook.referral.event.ReferralsEvent;
import com.handybook.handybook.referral.model.ReferralChannels;
import com.handybook.handybook.referral.model.ReferralDescriptor;
import com.handybook.handybook.referral.model.ReferralInfo;
import com.handybook.handybook.referral.util.ReferralIntentUtil;

import butterknife.Bind;
import butterknife.BindInt;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RatingFlowReferralFragment extends InjectedFragment {

    private ReferralDescriptor mReferralDescriptor;
    private ReferralChannels mReferralChannels;

    @Bind(R.id.rating_flow_referral_scroll_view)
    ScrollView mScrollView;
    @Bind(R.id.rating_flow_referral_subtitle)
    TextView mSubtitle;
    @Bind(R.id.rating_flow_referral_header)
    View mHeader;
    @Bind(R.id.rating_flow_referral_header_icon)
    View mHeaderIcon;
    @Bind(R.id.rating_flow_referral_header_text)
    View mHeaderText;
    @Bind(R.id.rating_flow_referral_content)
    View mReferralContent;
    @Bind(R.id.rating_flow_referral_feedback_section)
    View mFeedbackSection;
    @BindInt(R.integer.anim_duration_medium)
    int mMediumDuration;

    private static final String EXTRA_MODE = "mode";
    private Mode mMode;
    private Booking mBooking;


    enum Mode {
        REFERRAL, FEEDBACK
    }

    @NonNull
    public static RatingFlowReferralFragment newInstance(
            @NonNull final Booking booking,
            @NonNull final Mode mode,
            @NonNull final ReferralDescriptor referralDescriptor
    ) {
        final RatingFlowReferralFragment fragment = new RatingFlowReferralFragment();
        final Bundle arguments = new Bundle();
        arguments.putParcelable(BundleKeys.BOOKING, booking);
        arguments.putSerializable(EXTRA_MODE, mode);
        arguments.putSerializable(BundleKeys.REFERRAL_DESCRIPTOR, referralDescriptor);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBooking = getArguments().getParcelable(BundleKeys.BOOKING);
        mMode = (Mode) getArguments().getSerializable(EXTRA_MODE);
        mReferralDescriptor = (ReferralDescriptor) getArguments()
                .getSerializable(BundleKeys.REFERRAL_DESCRIPTOR);
        mReferralChannels = mReferralDescriptor
                .getReferralChannelsForSource(ReferralDescriptor.SOURCE_HIGH_RATING_MODAL);
    }

    @Nullable
    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState
    ) {
        final View view = inflater.inflate(
                R.layout.fragment_rating_flow_referral,
                container,
                false
        );
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        final String currencyChar = userManager.getCurrentUser().getCurrencyChar();
        final String formattedReceiverCouponAmount = TextUtils.formatPrice(
                mReferralDescriptor.getReceiverCouponAmount(),
                currencyChar,
                null
        );
        final String formattedSenderCreditAmount = TextUtils.formatPrice(
                mReferralDescriptor.getSenderCreditAmount(),
                currencyChar,
                null
        );
        mSubtitle.setText(getString(R.string.referral_dialog_subtitle_formatted,
                                    formattedReceiverCouponAmount, formattedSenderCreditAmount
        ));
        startAnimations();
    }

    private void startAnimations() {
        final Animation slideDownAnimation
                = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down_from_top);
        slideDownAnimation.setDuration(mMediumDuration);
        slideDownAnimation.setInterpolator(getActivity(), android.R.anim.decelerate_interpolator);
        slideDownAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(final Animation animation) {

            }

            @Override
            public void onAnimationEnd(final Animation animation) {
                final Animation fadeInAnimation = AnimationUtils.loadAnimation(
                        getActivity(),
                        R.anim.fade_in
                );
                fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(final Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(final Animation animation) {
                        if (mMode == Mode.REFERRAL) {
                            mReferralContent.setVisibility(View.INVISIBLE);
                            final Animation slideInAnimation = AnimationUtils.loadAnimation(
                                    getActivity(),
                                    R.anim.slide_in_right
                            );
                            slideInAnimation.setInterpolator(
                                    getActivity(),
                                    android.R.anim.overshoot_interpolator
                            );
                            slideInAnimation.setFillAfter(true);
                            slideInAnimation.setFillEnabled(true);
                            slideInAnimation.setDuration(mMediumDuration);
                            mReferralContent.startAnimation(slideInAnimation);
                        }
                    }

                    @Override
                    public void onAnimationRepeat(final Animation animation) {

                    }
                });
                mHeaderIcon.startAnimation(fadeInAnimation);
                mHeaderText.startAnimation(fadeInAnimation);
                if (mMode == Mode.FEEDBACK) {
                    mFeedbackSection.setVisibility(View.INVISIBLE);
                    mFeedbackSection.startAnimation(fadeInAnimation);
                }
            }

            @Override
            public void onAnimationRepeat(final Animation animation) {

            }
        });
        mHeader.startAnimation(slideDownAnimation);
    }

    @OnClick(R.id.rating_flow_referral_email_button)
    public void onEmailButtonClicked() {
        final ReferralInfo emailReferralInfo =
                mReferralChannels.getReferralInfoForChannel(ReferralChannels.CHANNEL_EMAIL);
        if (emailReferralInfo != null) {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("plain/text");
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, emailReferralInfo.getSubject());
            emailIntent.putExtra(Intent.EXTRA_TEXT, emailReferralInfo.getMessage());
            emailIntent.putExtra(
                    Intent.EXTRA_BCC,
                    getResources().getStringArray(R.array.referral_email_bcc_array)
            );
            launchShareIntent(emailIntent, ReferralChannels.CHANNEL_EMAIL);
            sendShareButtonTappedLog(emailReferralInfo.getGuid(), ReferralChannels.CHANNEL_EMAIL);
        }
        else {
            Crashlytics.logException(new Exception("Email referral info is null"));
        }
    }

    @OnClick(R.id.rating_flow_referral_text_button)
    public void onTextButtonClicked() {
        final ReferralInfo smsReferralInfo =
                mReferralChannels.getReferralInfoForChannel(ReferralChannels.CHANNEL_SMS);
        if (smsReferralInfo != null) {
            final Intent smsReferralIntent = ReferralIntentUtil.getSmsReferralIntent(
                    getActivity(),
                    smsReferralInfo
            );
            launchShareIntent(smsReferralIntent, ReferralChannels.CHANNEL_SMS);
            sendShareButtonTappedLog(smsReferralInfo.getGuid(), ReferralChannels.CHANNEL_SMS);
        }
        else {
            Crashlytics.logException(new Exception("SMS referral info is null"));
        }
    }

    @OnClick(R.id.rating_flow_done_button)
    public void onDoneClicked() {
        bus.post(new LogEvent.AddLogEvent(new RatingFlowLog.ConfirmationSubmitted(
                         Integer.parseInt(mBooking.getId()),
                         Integer.parseInt(mBooking.getProvider().getId())
                 ))
        );
        if (getActivity() instanceof RatingFlowActivity) {
            ((RatingFlowActivity) getActivity()).finishStep();
        }
    }

    private void launchShareIntent(
            final Intent intent,
            @Nullable @ReferralChannels.Channel final String channel
    ) {
        if (channel != null) {
            final ReferralInfo referralInfo = mReferralChannels.getReferralInfoForChannel(channel);
            if (referralInfo != null) {
                bus.post(new ReferralsEvent.RequestConfirmReferral(referralInfo.getGuid()));
            }
        }
        Utils.safeLaunchIntent(intent, getActivity());
    }

    private void sendShareButtonTappedLog(final String guid, final String referralMedium) {
        if (mReferralDescriptor != null) {
            String couponCode = StringUtils.replaceWithEmptyIfNull(
                    mReferralDescriptor.getCouponCode());
            String identifier = StringUtils.replaceWithEmptyIfNull(guid);
            bus.post(new LogEvent.AddLogEvent(new ShareModalLog.PostRatingShareButtonTappedLog(
                    referralMedium,
                    identifier,
                    couponCode,
                    null,
                    mReferralDescriptor.getSenderCreditAmount(),
                    mReferralDescriptor.getReceiverCouponAmount()
            )));
        }
    }

    @OnClick(R.id.rating_flow_referral_help_button)
    public void onHelpClicked() {
        if (getFragmentManager().findFragmentByTag(RatingFlowHelpDialogFragment.TAG) == null) {
            FragmentUtils.safeLaunchDialogFragment(
                    RatingFlowHelpDialogFragment.newInstance(mBooking),
                    this,
                    RatingFlowHelpDialogFragment.TAG
            );
        }
    }
}
