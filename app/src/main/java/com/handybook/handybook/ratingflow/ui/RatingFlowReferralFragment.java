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
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.common.collect.Lists;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.Provider;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.HandyRetrofitCallback;
import com.handybook.handybook.core.data.HandyRetrofitService;
import com.handybook.handybook.core.data.callback.FragmentSafeCallback;
import com.handybook.handybook.library.ui.fragment.ProgressSpinnerFragment;
import com.handybook.handybook.library.ui.view.proteamcarousel.CarouselPagerAdapter;
import com.handybook.handybook.library.ui.view.proteamcarousel.ProCarouselVM;
import com.handybook.handybook.library.ui.view.proteamcarousel.ProTeamCarouselView;
import com.handybook.handybook.library.util.FragmentUtils;
import com.handybook.handybook.library.util.StringUtils;
import com.handybook.handybook.library.util.TextUtils;
import com.handybook.handybook.library.util.Utils;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.constants.EventType;
import com.handybook.handybook.proteam.event.ProTeamEvent;
import com.handybook.handybook.proteam.model.ProTeamEdit;
import com.handybook.handybook.proteam.model.ProTeamEditWrapper;
import com.handybook.handybook.proteam.model.ProviderMatchPreference;
import com.handybook.handybook.proteam.model.RecommendedProvidersWrapper;
import com.handybook.handybook.ratingflow.RatingFlowLog;
import com.handybook.handybook.referral.event.ReferralsEvent;
import com.handybook.handybook.referral.model.ReferralChannels;
import com.handybook.handybook.referral.model.ReferralDescriptor;
import com.handybook.handybook.referral.model.ReferralInfo;
import com.handybook.handybook.referral.util.ReferralIntentUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.BindInt;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RatingFlowReferralFragment extends ProgressSpinnerFragment {

    @Inject
    HandyRetrofitService mService;

    @Nullable
    private ReferralDescriptor mReferralDescriptor;
    @Nullable
    private ReferralChannels mReferralChannels;
    @Nullable
    private ArrayList<Provider> mRecommendedProviders;
    private List<ProCarouselVM> mProTeamCarouselViewModels;

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
    @Bind(R.id.rating_flow_referral_header_divider)
    View mHeaderDivider;
    @Bind(R.id.rating_flow_referral_content)
    View mReferralContent;
    @Bind(R.id.rating_flow_review_section)
    View mReviewSection;
    @Bind(R.id.rating_flow_referral_help_button)
    View mHelpButton;
    @Bind(R.id.referral_flow_pro_team_section)
    ViewGroup mProTeamSection;
    @Bind(R.id.referral_flow_pro_team_carousel)
    ProTeamCarouselView mProTeamCarousel;

    @BindInt(R.integer.anim_duration_medium)
    int mMediumDuration;

    private static final String EXTRA_MODE = "mode";
    private Mode mMode;
    private Booking mBooking;
    private RatingFlowReviewFragment mReviewFragment;


    enum Mode {
        REFERRAL, FEEDBACK, REVIEW
    }

    @NonNull
    public static RatingFlowReferralFragment newInstance(
            @NonNull final Booking booking,
            @NonNull final Mode mode,
            @Nullable final ReferralDescriptor referralDescriptor,
            @Nullable final ArrayList<Provider> recommendedProviders
    ) {
        final RatingFlowReferralFragment fragment = new RatingFlowReferralFragment();
        final Bundle arguments = new Bundle();
        arguments.putParcelable(BundleKeys.BOOKING, booking);
        arguments.putSerializable(EXTRA_MODE, mode);
        arguments.putSerializable(BundleKeys.REFERRAL_DESCRIPTOR, referralDescriptor);
        arguments.putSerializable(BundleKeys.RECOMMENDED_PROVIDERS, recommendedProviders);
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
        if (mReferralDescriptor != null) {
            mReferralChannels = mReferralDescriptor
                    .getReferralChannelsForSource(ReferralDescriptor.SOURCE_HIGH_RATING_MODAL);
        }
        mRecommendedProviders = (ArrayList<Provider>) getArguments()
                .getSerializable(BundleKeys.RECOMMENDED_PROVIDERS);
    }

    @Nullable
    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState
    ) {
        ViewGroup view = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        view.addView(inflater.inflate(R.layout.fragment_rating_flow_referral, container, false));

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        initReferralContent();
        initProTeamCarousel();
        startAnimations();
    }

    private void initReferralContent() {
        if (mReferralDescriptor == null) { return; }
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
    }

    private void initProTeamCarousel() {
        if (mRecommendedProviders == null && mMode == Mode.FEEDBACK) {
            showProgressSpinner(true);
            dataManager.getRecommendedProviders(
                    userManager.getCurrentUser().getId(),
                    mBooking.getService().getId(),
                    new FragmentSafeCallback<RecommendedProvidersWrapper>(this) {
                        @Override
                        public void onCallbackSuccess(final RecommendedProvidersWrapper response) {
                            mRecommendedProviders = response.getRecommendedProviders();
                            initProTeamCarousel();
                            hideProgressSpinner();
                        }

                        @Override
                        public void onCallbackError(final DataManager.DataManagerError error) {
                            hideProgressSpinner();
                        }
                    }
            );
        }

        if (mRecommendedProviders == null || mRecommendedProviders.isEmpty()) { return; }

        bus.post(new LogEvent.AddLogEvent(new RatingFlowLog.RecommendedProvidersShown(
                mRecommendedProviders,
                userManager.getCurrentUser().getId(),
                mBooking.getId()
        )));

        mProTeamSection.setVisibility(View.VISIBLE);

        mProTeamCarouselViewModels = new ArrayList<>();
        for (final Provider provider : mRecommendedProviders) {
            final ProCarouselVM viewModel = ProCarouselVM.fromProvider(
                    provider,
                    getString(R.string.add_to_pro_team)
            );
            viewModel.setIsProTeam(false);
            mProTeamCarouselViewModels.add(viewModel);
        }
        mProTeamCarousel.bind(
                mProTeamCarouselViewModels,
                new CarouselPagerAdapter.ActionListener() {
                    @Override
                    public void onPrimaryButtonClick(final ProCarouselVM pro, final View button) {
                        addToProTeam(pro, (Button) button);
                    }
                }
        );
    }

    private void addToProTeam(final ProCarouselVM viewModel, final Button button) {
        final int index = mProTeamCarouselViewModels.indexOf(viewModel);
        final Provider provider = mRecommendedProviders.get(index);

        button.setText(R.string.adding);
        button.setClickable(false);

        final ProTeamEdit proTeamEdit = new ProTeamEdit(ProviderMatchPreference.PREFERRED);
        proTeamEdit.addId(
                Integer.parseInt(provider.getId()),
                provider.getCategoryType()
        );
        final FragmentSafeCallback<Void> editProTeamCallback =
                new FragmentSafeCallback<Void>(this) {
                    @Override
                    public void onCallbackSuccess(final Void response) {
                        viewModel.setActionable(false);
                        viewModel.setButtonText(getString(R.string.added));
                        button.setText(R.string.added);
                        button.setEnabled(false);
                        bus.post(new LogEvent.AddLogEvent(new RatingFlowLog.ProviderAdded(
                                userManager.getCurrentUser().getId(),
                                provider.getId()
                        )));
                    }

                    @Override
                    public void onCallbackError(final DataManager.DataManagerError error) {
                        button.setText(R.string.add_to_pro_team);
                        button.setClickable(true);
                    }
                };
        mService.editProTeam(
                userManager.getCurrentUser().getId(),
                new ProTeamEditWrapper(
                        Lists.newArrayList(proTeamEdit),
                        ProTeamEvent.Source.LOW_RATING_FLOW.toString()
                ),
                new HandyRetrofitCallback(editProTeamCallback) {
                    @Override
                    protected void success(final JSONObject response) {
                        editProTeamCallback.onSuccess(null);
                    }
                }
        );
    }

    private void startAnimations() {
        if (mMode == Mode.FEEDBACK) {
            mHelpButton.setVisibility(View.INVISIBLE);
            mHeaderDivider.setVisibility(View.INVISIBLE);
        }

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
                        if (shouldShowReferralContent()) {
                            animateContentIn(mReferralContent);
                            bus.post(new LogEvent.AddLogEvent(new RatingFlowLog.ReferralPageLog(
                                    EventType.EVENT_TYPE_SHOWN,
                                    null
                            )));
                        }
                        else {
                            animateContentIn(mReviewSection);
                        }
                    }

                    @Override
                    public void onAnimationRepeat(final Animation animation) {

                    }
                });
                mHeaderIcon.startAnimation(fadeInAnimation);
                mHeaderText.startAnimation(fadeInAnimation);
                if (mMode == Mode.FEEDBACK) {
                    mHelpButton.startAnimation(fadeInAnimation);
                    mHeaderDivider.startAnimation(fadeInAnimation);
                }
            }

            @Override
            public void onAnimationRepeat(final Animation animation) {

            }
        });
        mHeader.startAnimation(slideDownAnimation);
    }

    private boolean shouldShowReferralContent() {
        return mMode == Mode.REFERRAL && mReferralDescriptor != null;
    }

    private void animateContentIn(View view) {
        view.setVisibility(View.INVISIBLE);
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
        view.startAnimation(slideInAnimation);
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
            sendShareMethodSelectedLog(emailReferralInfo.getGuid(), ReferralChannels.CHANNEL_EMAIL);
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
            sendShareMethodSelectedLog(smsReferralInfo.getGuid(), ReferralChannels.CHANNEL_SMS);
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

        if (shouldShowReferralContent()) {
            //if the referral content is being displayed, then we want to log accordingly.
            bus.post(new LogEvent.AddLogEvent(new RatingFlowLog.ReferralPageLog(
                    EventType.EVENT_TYPE_SUBMITTED,
                    null
                     ))
            );
        }

        if (getActivity() instanceof RatingFlowActivity) {

            if (mMode == Mode.REVIEW && mReviewFragment != null) {
                //save the review
                mReviewFragment.onSubmit();
            }

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

    private void sendShareMethodSelectedLog(final String guid, final String referralMedium) {
        if (mReferralDescriptor != null) {
            String couponCode = StringUtils.replaceWithEmptyIfNull(
                    mReferralDescriptor.getCouponCode());
            String identifier = StringUtils.replaceWithEmptyIfNull(guid);
            bus.post(new LogEvent.AddLogEvent(new RatingFlowLog.ShareMethodSelected(
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
