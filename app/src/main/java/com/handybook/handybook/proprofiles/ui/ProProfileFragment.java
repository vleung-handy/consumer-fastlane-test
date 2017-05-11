package com.handybook.handybook.proprofiles.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.BookingRequest;
import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.booking.ui.activity.BookingLocationActivity;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.core.constant.ActivityResult;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.callback.FragmentSafeCallback;
import com.handybook.handybook.core.manager.ServicesManager;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.library.util.TextUtils;
import com.handybook.handybook.library.util.Utils;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.constants.SourcePage;
import com.handybook.handybook.logger.handylogger.model.ProProfileLog;
import com.handybook.handybook.proprofiles.ProProfileManager;
import com.handybook.handybook.proprofiles.model.ProProfile;
import com.handybook.handybook.proprofiles.reviews.model.ProReviews;
import com.handybook.handybook.proprofiles.reviews.model.ProReviewsRequest;
import com.handybook.handybook.proteam.callback.ConversationCallback;
import com.handybook.handybook.proteam.callback.ConversationCallbackWrapper;
import com.handybook.handybook.proteam.ui.activity.ProMessagesActivity;
import com.handybook.handybook.proteam.viewmodel.ProMessagesViewModel;
import com.handybook.handybook.referral.util.ReferralIntentUtil;
import com.handybook.shared.core.HandyLibrary;
import com.handybook.shared.layer.LayerConstants;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * has a header view and a tab layout view.
 * the header view collapses as the tab layout is scrolled
 *
 * todo make this class smaller?
 * possibly put the action buttons and handlers into their own fragment?
 */
public class ProProfileFragment extends InjectedFragment implements
        ProProfileDetailsTabLayout.RequestReviewsListener, ConversationCallback {

    public final static String TAG = ProProfileFragment.class.getName();

    /**
     * for now, we ONLY want to show 5-star ratings
     */
    private static final float PRO_REVIEWS_MIN_RATING_TO_DISPLAY = 5.0f;
    private static final int PRO_REVIEWS_PAGE_SIZE = 10;

    @Bind(R.id.pro_profile_details_tab_container)
    ProProfileDetailsTabLayout mProProfileDetailsTabLayout;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.pro_profile_header_layout)
    ProProfileHeaderView mProProfileHeaderView;

    @Bind(R.id.pro_profile_loading_layout)
    View mLoadingLayout;

    @Bind(R.id.loading_error_layout)
    View mLoadingErrorLayout;

    @Inject
    ProProfileManager mProProfileManager;

    @Inject
    ServicesManager mServicesManager;

    /**
     * used for making requests for pro profile and reviews
     */
    private String mProviderId;

    private ProProfile mProProfile;
    /**
     * prevent reviews request while one is in progress
     */
    private boolean mRequestingMoreReviews = false;

    public static ProProfileFragment newInstance(
            @NonNull String providerId,
            @SourcePage.HandyLoggerSourcePage String sourcePage
    ) {
        Bundle args = new Bundle();
        args.putString(BundleKeys.PROVIDER_ID, providerId);
        args.putString(BundleKeys.PAGE_SOURCE, sourcePage);
        ProProfileFragment fragment = new ProProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProviderId = getArguments().getString(BundleKeys.PROVIDER_ID);
        @SourcePage.HandyLoggerSourcePage String sourcePage
                = getArguments().getString(BundleKeys.PAGE_SOURCE, null);
        bus.post(new LogEvent.AddLogEvent(new ProProfileLog.Shown(
                mProviderId,
                sourcePage
        )));
    }

    @Override
    public void onDestroy() {
        bus.post(new LogEvent.AddLogEvent(new ProProfileLog.PageClosed(mProviderId)));
        super.onDestroy();
    }

    private ProReviewsRequest buildReviewsRequest(@Nullable final String currentPageLastReviewId) {
        return new ProReviewsRequest(
                currentPageLastReviewId,
                PRO_REVIEWS_PAGE_SIZE,
                ProReviewsRequest.SortOrder.DESCENDING,
                PRO_REVIEWS_MIN_RATING_TO_DISPLAY
        );
    }

    @Override
    public void onRequestMoreReviews(@Nullable final String mCurrentPageLastReviewId) {
        if (mRequestingMoreReviews) { return; }
        mRequestingMoreReviews = true; //don't request if already requesting
        /*
        TODO is it ever possible to get multiple requests for the same id (or id for older page)
        even with the help of this boolean? investigate and handle
         */
        mProProfileManager.getProviderReviews(
                mProviderId,
                buildReviewsRequest(mCurrentPageLastReviewId),
                new FragmentSafeCallback<ProReviews>(this) {
                    @Override
                    public void onCallbackSuccess(final ProReviews response) {
                        updateViewsWithAdditionalProReviews(
                                response);
                        mRequestingMoreReviews = false;
                    }

                    @Override
                    public void onCallbackError(final DataManager.DataManagerError error) {
                        mProProfileDetailsTabLayout.getReviewsContainer()
                                                   .showErrorView();
                        mRequestingMoreReviews = false;

                    }
                }
        );
    }

    private void initActionButtonClickListeners() {
        mProProfileHeaderView.setBookActionButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onBookButtonClicked();
            }
        });

        mProProfileHeaderView.setMessageActionButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onMessageButtonClicked();
            }
        });

        mProProfileHeaderView.setRecommendActionButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onRecommendButtonClicked();
            }
        });
    }

    private void onMessageButtonClicked() {
        bus.post(new LogEvent.AddLogEvent(new ProProfileLog.ActionButtonClicked(
                mProviderId, ProProfileLog.ActionButtonClicked.ACTION_BUTTON_TYPE_MESSAGE
        )));
        HandyLibrary.getInstance()
                    .getHandyService()
                    .createConversation(
                            mProviderId,
                            userManager.getCurrentUser().getAuthToken(),
                            "",
                            new ConversationCallbackWrapper(ProProfileFragment.this)
                    );

    }

    private void onBookButtonClicked() {
        bus.post(new LogEvent.AddLogEvent(new ProProfileLog.ActionButtonClicked(
                mProviderId, ProProfileLog.ActionButtonClicked.ACTION_BUTTON_TYPE_BOOK
        )));

        //doing what ProMessagesActivity does
        startBookingFlow(
                mServicesManager.getCachedService(Booking.SERVICE_HOME_CLEANING),
                mProviderId
        );
    }

    /**
     * copied from {@link com.handybook.handybook.proteam.ui.activity.ProMessagesActivity}.
     * it is definitely not ideal, but refactoring it is out of scope for this feature
     */
    private void startBookingFlow(@Nullable Service service, String providerId) {
        if (service == null) {
            startActivity(new Intent(
                    getActivity(),
                    ServiceCategoriesActivity.class
            ));
            return;
        }
        final BookingRequest request = new BookingRequest();
        request.setServiceId(service.getId());
        request.setUniq(service.getUniq());

        if (userManager.getCurrentUser() != null) {
            //todo possible to change it?
            request.setEmail(userManager.getCurrentUser().getEmail());
        }
        request.setCoupon(bookingManager.getPromoTabCoupon());
        request.setProviderId(providerId);

        bookingManager.clear();
        bookingManager.setCurrentRequest(request);
        final Intent intent;

        /*
        note that this does not account for the consolidated booking flow experiment,
        but this is what ProMessagesActivity is already doing.

        we cannot directly launch BookingGetQuoteActivity from here
        because it requires an options map as a bundle argument
         */
        intent = new Intent(getActivity(), BookingLocationActivity.class);
        startActivity(intent);
    }

    private void onRecommendButtonClicked() {
        bus.post(new LogEvent.AddLogEvent(new ProProfileLog.ActionButtonClicked(
                mProviderId, ProProfileLog.ActionButtonClicked.ACTION_BUTTON_TYPE_RECOMMEND
        )));
        launchGenericShareIntent();
    }

    //TODO consolidate with the logic in BaseReferralFragment
    protected void launchGenericShareIntent() {
        final Intent dummyIntent = new Intent();
        dummyIntent.setAction(Intent.ACTION_SEND);
        dummyIntent.setType(ReferralIntentUtil.MIME_TYPE_PLAIN_TEXT);

        final Intent activityPickerIntent = new Intent();
        activityPickerIntent.setAction(Intent.ACTION_PICK_ACTIVITY);
        activityPickerIntent.putExtra(Intent.EXTRA_TITLE, getString(R.string.share_using));
        activityPickerIntent.putExtra(Intent.EXTRA_INTENT, dummyIntent);
        startActivityForResult(activityPickerIntent, ActivityResult.PICK_ACTIVITY);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        if (requestCode == ActivityResult.PICK_ACTIVITY && resultCode == Activity.RESULT_OK &&
            intent != null) {
            final String resolvedChannel =
                    ReferralIntentUtil.addReferralIntentExtras(
                            getActivity(),
                            intent,
                            mProProfile.getReferralInfo().getReferralInfo()
                    );
            final String extraText = intent.getStringExtra(Intent.EXTRA_TEXT);
            if (TextUtils.isBlank(extraText)) {
                intent.putExtra(
                        Intent.EXTRA_TEXT,
                        mProProfile.getProviderInformation().getReferralCode()
                );
            }
            Utils.safeLaunchIntent(intent, getActivity());
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState
    ) {
        final View view = inflater.inflate(R.layout.fragment_pro_profile, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    private void hideAllOverlays() {
        mLoadingLayout.setVisibility(View.GONE);
        mLoadingErrorLayout.setVisibility(View.GONE);
    }

    private void showLoadingLayout() {
        mLoadingLayout.setVisibility(View.VISIBLE);
        mLoadingErrorLayout.setVisibility(View.GONE);
    }

    private void showErrorLayout() {
        mLoadingLayout.setVisibility(View.GONE);
        mLoadingErrorLayout.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.loading_error_try_again_button)
    public void onLoadingErrorTryAgainButtonClicked() {
        loadProProfileAndReviews();
        /*
        even though the error layout only shows when unable to get the ProProfile model,
        also re-fetching the reviews because if we don't it would be a strange
        experience - reviews error layout would still show after profile loaded,
        if error was due to network connectivity
         */
    }

    private void loadProviderProfile() {
        showLoadingLayout();
        mProProfileManager.getProviderProfile(
                mProviderId,
                new FragmentSafeCallback<ProProfile>(this) {
                    @Override
                    public void onCallbackSuccess(final ProProfile response) {
                        updateViewsWithProviderProfile(response);
                        hideAllOverlays();
                    }

                    @Override
                    public void onCallbackError(final DataManager.DataManagerError error) {
                        showErrorLayout();
                    }
                }
        );
    }

    private void loadInitialProviderReviews() {
        mProProfileDetailsTabLayout.getReviewsContainer().clearReviews();
        mProProfileDetailsTabLayout.getReviewsContainer().showLoadingLayout();
        onRequestMoreReviews(null);
    }

    private void loadProProfileAndReviews() {
        loadProviderProfile();
        loadInitialProviderReviews();
    }

    @Override
    public void onViewCreated(
            final View view, @Nullable final Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);
        setupToolbar(mToolbar, null);

        loadProProfileAndReviews();

        mProProfileDetailsTabLayout.setRequestReviewsListener(this);

        //this is only for logging purposes
        mProProfileDetailsTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(final TabLayout.Tab tab) {
                @ProProfileLog.TabPageToggled.ProProfileTabPageType String tabType = null;
                if (tab.getPosition() == mProProfileDetailsTabLayout.getAboutTabPosition()) {
                    tabType = ProProfileLog.TabPageToggled.TAB_PAGE_ABOUT;
                }
                else if (tab.getPosition() ==
                         mProProfileDetailsTabLayout.getReviewsTabPosition()) {
                    tabType = ProProfileLog.TabPageToggled.TAB_PAGE_FIVE_STAR_REVIEWS;
                }
                bus.post(new LogEvent.AddLogEvent(new ProProfileLog.TabPageToggled(
                        mProviderId,
                        tabType
                )));
            }

            @Override
            public void onTabUnselected(final TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(final TabLayout.Tab tab) {
            }
        });
        initActionButtonClickListeners();
    }

    private void updateViewsWithAdditionalProReviews(ProReviews proReviews) {
        mProProfileDetailsTabLayout.updateForAdditionalProReviews(proReviews);
    }

    private void updateViewsWithProviderProfile(ProProfile proProfile) {
        ProProfile.ProviderInformation providerInformation
                = proProfile.getProviderInformation();
        setupToolbar(
                mToolbar,
                getResources().getString(
                        R.string.pro_profile_toolbar_title_formatted,
                        providerInformation.getFirstName()
                )
        );

        mProProfileHeaderView.updateWithModel(proProfile);

        mProProfileDetailsTabLayout.updateForProProfile(proProfile);
        mProProfile = proProfile;
    }

    /**
     * assumes that pro profile is already received
     * @param conversationId
     */
    @Override
    public void onCreateConversationSuccess(final String conversationId) {
        ProProfile.ProviderInformation providerInformation
                = mProProfile.getProviderInformation();
        Intent intent = new Intent(getActivity(), ProMessagesActivity.class);
        intent.putExtra(LayerConstants.LAYER_CONVERSATION_KEY, Uri.parse(conversationId));
        intent.putExtra(
                BundleKeys.PRO_MESSAGES_VIEW_MODEL,
                new ProMessagesViewModel(
                        providerInformation.getId(),
                        providerInformation.getDisplayName(),
                        providerInformation.getFirstName(),
                        providerInformation.getProfilePhotoUrl(),
                        providerInformation.getProTeamCategoryType(),
                        providerInformation.isCustomerFavorite() != null,
                        true
                )
        );
        startActivity(intent);
    }

    @Override
    public void onCreateConversationError() {
        Toast.makeText(getContext(), R.string.default_error_string, Toast.LENGTH_SHORT).show();
        Crashlytics.logException(new Exception("Unable to create layer conversation"));
    }
}
