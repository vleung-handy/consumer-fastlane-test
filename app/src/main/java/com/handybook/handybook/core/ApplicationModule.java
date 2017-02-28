package com.handybook.handybook.core;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.Base64;

import com.google.gson.GsonBuilder;
import com.handybook.handybook.BuildConfig;
import com.handybook.handybook.R;
import com.handybook.handybook.account.ui.AccountFragment;
import com.handybook.handybook.account.ui.ContactFragment;
import com.handybook.handybook.account.ui.EditContactInfoActivity;
import com.handybook.handybook.account.ui.EditPasswordActivity;
import com.handybook.handybook.account.ui.EditPlanAddressActivity;
import com.handybook.handybook.account.ui.EditPlanAddressFragment;
import com.handybook.handybook.account.ui.EditPlanFragment;
import com.handybook.handybook.account.ui.EditPlanFrequencyActivity;
import com.handybook.handybook.account.ui.EditPlanFrequencyFragment;
import com.handybook.handybook.account.ui.PlansFragment;
import com.handybook.handybook.account.ui.ProfileActivity;
import com.handybook.handybook.account.ui.ProfilePasswordFragment;
import com.handybook.handybook.account.ui.UpdatePaymentFragment;
import com.handybook.handybook.autocomplete.AddressAutoCompleteManager;
import com.handybook.handybook.autocomplete.AutoCompleteAddressFragment;
import com.handybook.handybook.autocomplete.PlacesService;
import com.handybook.handybook.booking.bookingedit.manager.BookingEditManager;
import com.handybook.handybook.booking.bookingedit.ui.activity.BookingEditAddressActivity;
import com.handybook.handybook.booking.bookingedit.ui.activity.BookingEditEntryInformationActivity;
import com.handybook.handybook.booking.bookingedit.ui.activity.BookingEditExtrasActivity;
import com.handybook.handybook.booking.bookingedit.ui.activity.BookingEditFrequencyActivity;
import com.handybook.handybook.booking.bookingedit.ui.activity.BookingEditHoursActivity;
import com.handybook.handybook.booking.bookingedit.ui.activity.BookingEditPreferencesActivity;
import com.handybook.handybook.booking.bookingedit.ui.fragment.BookingEditAddressFragment;
import com.handybook.handybook.booking.bookingedit.ui.fragment.BookingEditEntryInformationFragment;
import com.handybook.handybook.booking.bookingedit.ui.fragment.BookingEditExtrasFragment;
import com.handybook.handybook.booking.bookingedit.ui.fragment.BookingEditFrequencyFragment;
import com.handybook.handybook.booking.bookingedit.ui.fragment.BookingEditHoursFragment;
import com.handybook.handybook.booking.bookingedit.ui.fragment.BookingEditPreferencesFragment;
import com.handybook.handybook.booking.history.HistoryActivity;
import com.handybook.handybook.booking.history.HistoryFragment;
import com.handybook.handybook.booking.manager.BookingManager;
import com.handybook.handybook.booking.model.BookingPostInfo;
import com.handybook.handybook.booking.model.BookingQuote;
import com.handybook.handybook.booking.model.BookingRequest;
import com.handybook.handybook.booking.model.BookingTransaction;
import com.handybook.handybook.booking.rating.RateImprovementConfirmationDialogFragment;
import com.handybook.handybook.booking.rating.RateImprovementDialogFragment;
import com.handybook.handybook.booking.rating.RatingsGridFragment;
import com.handybook.handybook.booking.rating.RatingsRadioFragment;
import com.handybook.handybook.booking.reschedule.RescheduleUpcomingActivity;
import com.handybook.handybook.booking.ui.activity.BookingAddressActivity;
import com.handybook.handybook.booking.ui.activity.BookingCancelOptionsActivity;
import com.handybook.handybook.booking.ui.activity.BookingDateActivity;
import com.handybook.handybook.booking.ui.activity.BookingDetailActivity;
import com.handybook.handybook.booking.ui.activity.BookingExtrasActivity;
import com.handybook.handybook.booking.ui.activity.BookingFinalizeActivity;
import com.handybook.handybook.booking.ui.activity.BookingLocationActivity;
import com.handybook.handybook.booking.ui.activity.BookingOptionsActivity;
import com.handybook.handybook.booking.ui.activity.BookingPaymentActivity;
import com.handybook.handybook.booking.ui.activity.BookingRecurrenceActivity;
import com.handybook.handybook.booking.ui.activity.BookingRescheduleOptionsActivity;
import com.handybook.handybook.booking.ui.activity.BookingsActivity;
import com.handybook.handybook.booking.ui.activity.CancelRecurringBookingActivity;
import com.handybook.handybook.booking.ui.activity.PeakPricingActivity;
import com.handybook.handybook.booking.ui.activity.PromosActivity;
import com.handybook.handybook.booking.ui.activity.ReportIssueActivity;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.booking.ui.activity.ServicesActivity;
import com.handybook.handybook.booking.ui.activity.ZipActivity;
import com.handybook.handybook.booking.ui.fragment.ActiveBookingFragment;
import com.handybook.handybook.booking.ui.fragment.AddLaundryDialogFragment;
import com.handybook.handybook.booking.ui.fragment.BookingAddressFragment;
import com.handybook.handybook.booking.ui.fragment.BookingCancelReasonFragment;
import com.handybook.handybook.booking.ui.fragment.BookingCancelWarningFragment;
import com.handybook.handybook.booking.ui.fragment.BookingDateFragment;
import com.handybook.handybook.booking.ui.fragment.BookingDetailFragment;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragment;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentAddress;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentBookingActions;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentEntryInformation;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentExtras;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentLaundry;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentPayment;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentPreferences;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentProInformation;
import com.handybook.handybook.booking.ui.fragment.BookingEntryInfoFragment;
import com.handybook.handybook.booking.ui.fragment.BookingExtrasFragment;
import com.handybook.handybook.booking.ui.fragment.BookingHeaderFragment;
import com.handybook.handybook.booking.ui.fragment.BookingLocationFragment;
import com.handybook.handybook.booking.ui.fragment.BookingOptionsFragment;
import com.handybook.handybook.booking.ui.fragment.BookingPasswordPromptFragment;
import com.handybook.handybook.booking.ui.fragment.BookingPaymentFragment;
import com.handybook.handybook.booking.ui.fragment.BookingPreferencesFragment;
import com.handybook.handybook.booking.ui.fragment.BookingRecurrenceFragment;
import com.handybook.handybook.booking.ui.fragment.BookingRescheduleOptionsFragment;
import com.handybook.handybook.booking.ui.fragment.BookingSubscriptionFragment;
import com.handybook.handybook.booking.ui.fragment.CancelRecurringBookingFragment;
import com.handybook.handybook.booking.ui.fragment.CancelRecurringBookingSelectionFragment;
import com.handybook.handybook.booking.ui.fragment.EmailCancellationDialogFragment;
import com.handybook.handybook.booking.ui.fragment.LaundryDropOffDialogFragment;
import com.handybook.handybook.booking.ui.fragment.LaundryInfoDialogFragment;
import com.handybook.handybook.booking.ui.fragment.PeakPricingFragment;
import com.handybook.handybook.booking.ui.fragment.PeakPricingTableFragment;
import com.handybook.handybook.booking.ui.fragment.PromosFragment;
import com.handybook.handybook.booking.ui.fragment.RateProTeamFragment;
import com.handybook.handybook.booking.ui.fragment.RateServiceConfirmDialogFragment;
import com.handybook.handybook.booking.ui.fragment.RateServiceDialogFragment;
import com.handybook.handybook.booking.ui.fragment.ReferralDialogFragment;
import com.handybook.handybook.booking.ui.fragment.ReportIssueFragment;
import com.handybook.handybook.booking.ui.fragment.RescheduleDialogFragment;
import com.handybook.handybook.booking.ui.fragment.ServiceCategoriesFragment;
import com.handybook.handybook.booking.ui.fragment.ServiceCategoriesHomeFragment;
import com.handybook.handybook.booking.ui.fragment.ServicesFragment;
import com.handybook.handybook.booking.ui.fragment.TipDialogFragment;
import com.handybook.handybook.booking.ui.fragment.UpcomingBookingsFragment;
import com.handybook.handybook.booking.ui.fragment.ZipFragment;
import com.handybook.handybook.booking.ui.view.ServiceCategoriesOverlayFragment;
import com.handybook.handybook.bottomnav.BottomNavActivity;
import com.handybook.handybook.configuration.manager.ConfigurationManager;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.DataManagerErrorHandler;
import com.handybook.handybook.core.data.HandyRetrofitEndpoint;
import com.handybook.handybook.core.data.HandyRetrofitService;
import com.handybook.handybook.core.data.SecurePreferences;
import com.handybook.handybook.core.manager.AppBlockManager;
import com.handybook.handybook.core.manager.AppseeManager;
import com.handybook.handybook.core.manager.DefaultPreferencesManager;
import com.handybook.handybook.core.manager.FileManager;
import com.handybook.handybook.core.manager.SecurePreferencesManager;
import com.handybook.handybook.core.manager.ServicesManager;
import com.handybook.handybook.core.manager.SessionManager;
import com.handybook.handybook.core.manager.StripeManager;
import com.handybook.handybook.core.manager.UserDataManager;
import com.handybook.handybook.core.receiver.LayerPushReceiver;
import com.handybook.handybook.core.ui.activity.BlockingActivity;
import com.handybook.handybook.core.ui.activity.LoginActivity;
import com.handybook.handybook.core.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.core.ui.activity.OldDeeplinkSplashActivity;
import com.handybook.handybook.core.ui.activity.SplashActivity;
import com.handybook.handybook.core.ui.activity.UpdatePaymentActivity;
import com.handybook.handybook.core.ui.activity.WebViewActivity;
import com.handybook.handybook.core.ui.fragment.BlockingUpdateFragment;
import com.handybook.handybook.core.ui.fragment.LoginFragment;
import com.handybook.handybook.core.ui.fragment.NavbarWebViewDialogFragment;
import com.handybook.handybook.deeplink.DeepLinkIntentProvider;
import com.handybook.handybook.helpcenter.HelpModule;
import com.handybook.handybook.library.ui.fragment.WebViewFragment;
import com.handybook.handybook.library.util.PropertiesReader;
import com.handybook.handybook.logger.handylogger.EventLogManager;
import com.handybook.handybook.notifications.NotificationsModule;
import com.handybook.handybook.onboarding.OnboardingModule;
import com.handybook.handybook.promos.PromosModule;
import com.handybook.handybook.proteam.manager.ProTeamManager;
import com.handybook.handybook.proteam.ui.activity.ProMessagesActivity;
import com.handybook.handybook.proteam.ui.activity.ProTeamActivity;
import com.handybook.handybook.proteam.ui.activity.ProTeamEditActivity;
import com.handybook.handybook.proteam.ui.activity.ProTeamPerBookingActivity;
import com.handybook.handybook.proteam.ui.fragment.BookingProTeamConversationsFragment;
import com.handybook.handybook.proteam.ui.fragment.NewProTeamProListFragment;
import com.handybook.handybook.proteam.ui.fragment.ProTeamConversationsFragment;
import com.handybook.handybook.proteam.ui.fragment.ProTeamEditFragment;
import com.handybook.handybook.proteam.ui.fragment.ProTeamProListFragment;
import com.handybook.handybook.push.manager.UrbanAirshipManager;
import com.handybook.handybook.push.receiver.PushReceiver;
import com.handybook.handybook.ratingflow.RatingFlowModule;
import com.handybook.handybook.referral.ReferralModule;
import com.handybook.handybook.yozio.YozioMetaDataCallback;
import com.handybook.shared.core.HandyLibrary;
import com.handybook.shared.layer.LayerHelper;
import com.squareup.okhttp.CertificatePinner;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.otto.Bus;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

@Module(injects = {
        SplashActivity.class,
        ServiceCategoriesFragment.class,
        LoginFragment.class,
        UpcomingBookingsFragment.class,
        HistoryFragment.class,
        ActiveBookingFragment.class,
        AutoCompleteAddressFragment.class,
        BookingDetailFragment.class,
        ServiceCategoriesFragment.class,
        ServicesFragment.class,
        BookingLocationFragment.class,
        BookingOptionsFragment.class,
        BookingDateFragment.class,
        BookingAddressFragment.class,
        BookingHeaderFragment.class,
        BookingPaymentFragment.class,
        PeakPricingFragment.class,
        PeakPricingTableFragment.class,
        PromosFragment.class,
        BookingExtrasFragment.class,
        BookingRecurrenceFragment.class,
        DataManager.class,
        ServiceCategoriesActivity.class,
        ServicesActivity.class, ProfileActivity.class,
        PeakPricingActivity.class,
        MenuDrawerActivity.class,
        LoginActivity.class,
        BookingsActivity.class,
        HistoryActivity.class,
        BookingRecurrenceActivity.class,
        BookingPaymentActivity.class,
        BookingOptionsActivity.class,
        BookingLocationActivity.class,
        BookingExtrasActivity.class,
        BookingDetailActivity.class,
        BookingDateActivity.class,
        BookingFinalizeActivity.class,
        BookingPreferencesFragment.class,
        BookingPasswordPromptFragment.class,
        BookingEntryInfoFragment.class,
        BookingAddressActivity.class,
        PromosActivity.class,
        BaseApplication.class,
        BookingRescheduleOptionsActivity.class,
        BookingRescheduleOptionsFragment.class,
        BookingCancelOptionsActivity.class,
        BookingCancelReasonFragment.class,
        BookingCancelWarningFragment.class,
        YozioMetaDataCallback.class,
        RateServiceDialogFragment.class,
        RateServiceConfirmDialogFragment.class,
        LaundryDropOffDialogFragment.class,
        LaundryInfoDialogFragment.class,
        AddLaundryDialogFragment.class,
        OldDeeplinkSplashActivity.class,
        BookingDetailSectionFragment.class,
        BookingDetailSectionFragmentAddress.class,
        BookingDetailSectionFragmentEntryInformation.class,
        BookingDetailSectionFragmentExtras.class,
        BookingDetailSectionFragmentLaundry.class,
        BookingDetailSectionFragmentPreferences.class,
        BookingDetailSectionFragmentPayment.class,
        BookingDetailSectionFragmentProInformation.class,
        BookingDetailSectionFragmentBookingActions.class,
        BookingEditPreferencesActivity.class,
        BookingEditPreferencesFragment.class,
        BookingEditEntryInformationActivity.class,
        BookingEditEntryInformationFragment.class,
        BookingEditFrequencyActivity.class,
        BookingEditFrequencyFragment.class,
        BookingEditExtrasActivity.class,
        BookingEditExtrasFragment.class,
        BookingEditHoursActivity.class,
        BookingEditHoursFragment.class,
        BookingEditAddressActivity.class,
        BookingEditAddressFragment.class,
        BlockingActivity.class,
        BlockingUpdateFragment.class,
        TipDialogFragment.class,
        CancelRecurringBookingActivity.class,
        CancelRecurringBookingSelectionFragment.class,
        CancelRecurringBookingFragment.class,
        EmailCancellationDialogFragment.class,
        UpdatePaymentActivity.class,
        UpdatePaymentFragment.class,
        NavbarWebViewDialogFragment.class,
        ServiceCategoriesOverlayFragment.class,
        PushReceiver.class,
        ReferralDialogFragment.class,
        RateImprovementDialogFragment.class,
        RatingsGridFragment.class,
        RateProTeamFragment.class,
        RatingsRadioFragment.class,
        RateImprovementConfirmationDialogFragment.class,
        ProTeamActivity.class,
        ProMessagesActivity.class,
        ProTeamConversationsFragment.class,
        ProTeamEditFragment.class,
        ProTeamProListFragment.class,
        NewProTeamProListFragment.class,
        ReportIssueActivity.class,
        ReportIssueFragment.class,
        WebViewActivity.class,
        WebViewFragment.class,
        AccountFragment.class,
        ContactFragment.class,
        ProfilePasswordFragment.class,
        PlansFragment.class,
        EditPlanFragment.class,
        EditPlanAddressFragment.class,
        EditPlanFrequencyFragment.class,
        RescheduleUpcomingActivity.class,
        BottomNavActivity.class,
        LayerPushReceiver.class,
        BookingSubscriptionFragment.class,
        ProTeamPerBookingActivity.class,
        BookingProTeamConversationsFragment.class,
        RescheduleDialogFragment.class,
        EditContactInfoActivity.class,
        EditPasswordActivity.class,
        EditPlanFrequencyActivity.class,
        EditPlanAddressActivity.class,
        ProTeamEditActivity.class,
        ServiceCategoriesHomeFragment.class,
        SessionManager.class,
        ZipActivity.class,
        ZipFragment.class,
},
        includes = {
                HelpModule.class,
                NotificationsModule.class,
                OnboardingModule.class,
                ReferralModule.class,
                PromosModule.class,
                RatingFlowModule.class,
        }
)
public final class ApplicationModule {

    private final Context mContext;
    private final Properties mConfigs;

    public ApplicationModule(final Context context) {
        mContext = context.getApplicationContext();
        mConfigs = PropertiesReader.getProperties(context, "config.properties");
    }

    @Provides
    @Singleton
    Properties provideProperties() {
        return mConfigs;
    }

    @Provides
    @Singleton
    final EnvironmentModifier provideEnvironmentModifier(
            Bus bus,
            DefaultPreferencesManager defaultPreferencesManager
    ) {
        EnvironmentModifier environmentModifier = new EnvironmentModifier(mContext, bus,
                                                                          defaultPreferencesManager
        );
        if (BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_PROD)) {
            environmentModifier.setEnvironment(EnvironmentModifier.Environment.PRODUCTION);
        }
        return environmentModifier;
    }

    @Provides
    @Singleton
    final HandyRetrofitEndpoint provideHandyRetrofitEndpoint(EnvironmentModifier environmentModifier) {
        return new HandyRetrofitEndpoint(mContext, environmentModifier);
    }

    @Provides
    @Singleton
    final PlacesService providePlacesService() {
        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(mContext.getString(R.string.places_api_base_url))
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addQueryParam("key", mConfigs.getProperty("google_places_api_key"));
                    }
                })
                .setConverter(new GsonConverter(new GsonBuilder().create()))
                .setClient(new OkClient((new OkHttpClient())))
                .build();

        return restAdapter.create(PlacesService.class);
    }

    @Provides
    @Singleton
    final RestAdapter provideRestAdapter(
            final HandyRetrofitEndpoint endpoint,
            final UserManager userManager
    ) {
        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(60, TimeUnit.SECONDS);
        if (BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_PROD) && !BuildConfig.DEBUG) {
            okHttpClient.setCertificatePinner(new CertificatePinner.Builder()
                                                      .add(
                                                              mConfigs.getProperty("hostname"),
                                                              "sha1/tbHJQrYmt+5isj5s44sk794iYFc=",
                                                              "sha1/SXxoaOSEzPC6BgGmxAt/EAcsajw=",
                                                              "sha1/blhOM3W9V/bVQhsWAcLYwPU6n24=",
                                                              "sha1/T5x9IXmcrQ7YuQxXnxoCmeeQ84c="
                                                      )
                                                      .build());
        }

        final String username = mConfigs.getProperty("api_username");
        String password = mConfigs.getProperty("api_password_internal");
        if (BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_PROD)) {
            password = mConfigs.getProperty("api_password");
        }
        final String pwd = password;
        final RestAdapter restAdapter =
                new RestAdapter
                        .Builder()
                        .setEndpoint(endpoint)
                        .setRequestInterceptor(new RequestInterceptor() {
                            final String auth = "Basic " + Base64.encodeToString(
                                    (username + ":" + pwd).getBytes(),
                                    Base64.NO_WRAP
                            );

                            @Override
                            public void intercept(
                                    RequestFacade request
                            ) {
                                request.addHeader("Authorization", auth);
                                request.addHeader("Accept", "application/json");
                                request.addQueryParam("client", "android");
                                request.addQueryParam("app_version", BuildConfig.VERSION_NAME);
                                request.addQueryParam(
                                        "app_version_code",
                                        String.valueOf(BuildConfig.VERSION_CODE)
                                );
                                request.addQueryParam("api_sub_version", "6.0");
                                request.addQueryParam("app_device_id", getDeviceId());
                                request.addQueryParam("app_device_model", getDeviceName());
                                request.addQueryParam("app_device_os", Build.VERSION.RELEASE);
                                final User user = userManager.getCurrentUser();
                                if (user != null) {
                                    request.addQueryParam("app_user_id", user.getId());
                                    String authToken = user.getAuthToken();
                                    if (authToken != null) {
                                        request.addHeader("X-Auth-Token", authToken);
                                    }
                                }
                            }
                        })
                        .setConverter(new GsonConverter(
                                new GsonBuilder()
                                        .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                                        .setExclusionStrategies(
                                                BookingRequest.getExclusionStrategy()
                                        )
                                        .registerTypeAdapter(
                                                BookingRequest.class,
                                                new BookingRequest.BookingRequestSerializer()
                                        )
                                        .setExclusionStrategies(
                                                BookingQuote
                                                        .getExclusionStrategy())
                                        .registerTypeAdapter(
                                                BookingQuote.class,
                                                new BookingQuote.BookingQuoteSerializer()
                                        )
                                        .setExclusionStrategies(
                                                BookingPostInfo
                                                        .getExclusionStrategy())
                                        .registerTypeAdapter(
                                                BookingPostInfo.class,
                                                new BookingPostInfo.BookingPostInfoSerializer()
                                        )
                                        .setExclusionStrategies(
                                                BookingTransaction
                                                        .getExclusionStrategy())
                                        .registerTypeAdapter(
                                                BookingTransaction.class,
                                                new BookingTransaction.BookingTransactionSerializer()
                                        )
                                        .setExclusionStrategies(
                                                User.getExclusionStrategy())
                                        .registerTypeAdapter(
                                                User.class,
                                                new User.UserSerializer()
                                        )
                                        .create()))
                        .setClient(new OkClient(
                                okHttpClient))
                        .build();

        if (!BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_PROD)
            || BuildConfig.BUILD_TYPE.equals("debug")) {
            restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
        }

        return restAdapter;
    }

    @Provides
    @Singleton
    final SessionManager provideSessionCacheManager() {
        return new SessionManager();
    }

    @Provides
    @Singleton
    final HandyRetrofitService provideHandyService(
            final RestAdapter restAdapter
    ) {
        return restAdapter.create(HandyRetrofitService.class);
    }

    @Provides
    @Singleton
    final DataManager provideDataManager(
            final HandyRetrofitService service,
            final HandyRetrofitEndpoint endpoint
    ) {
        return new DataManager(service, endpoint);
    }

    @Provides
    @Singleton
    final AppseeManager provideAppseeManager(
            final ConfigurationManager configurationManager,
            final FileManager fileManager
    ) {
        String appseeApiKey = mConfigs.getProperty
                (BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_PROD) ?
                 "app_see_key"
                                                                        : "app_see_key_internal");
        return new AppseeManager(appseeApiKey, configurationManager, fileManager);
    }

    @Provides
    final DataManagerErrorHandler provideDataManagerErrorHandler() {
        return new DataManagerErrorHandler();
    }

    @Provides
    @Singleton
    final Bus provideBus() {
        return new MainBus();
    }

    @Provides
    @Singleton
    final SecurePreferences providePrefs() {
        return new SecurePreferences(
                mContext,
                null,
                mConfigs.getProperty("secure_prefs_key"),
                true
        );
    }

    @Provides
    @Singleton
    final BookingManager provideBookingManager(
            final Bus bus,
            final SecurePreferencesManager securePreferencesManager,
            final DataManager dataManager
    ) {
        return new BookingManager(bus, securePreferencesManager, dataManager);
    }

    @Provides
    @Singleton
    final FileManager provideFileManager() {
        return new FileManager(mContext);
    }

    @Provides
    @Singleton
    final AddressAutoCompleteManager provideAddressAutoCompleteManager(
            final Bus bus,
            final PlacesService service
    ) {
        return new AddressAutoCompleteManager(bus, service);
    }

    @Provides
    @Singleton
    final BookingEditManager provideBookingEditManager(
            final Bus bus,
            final DataManager dataManager
    ) {
        return new BookingEditManager(bus, dataManager);
    }

    @Provides
    @Singleton
    final UserManager provideUserManager(
            final Bus bus,
            final SecurePreferencesManager securePreferencesManager,
            final DefaultPreferencesManager defaultPreferencesManager
    ) {
        return new UserManager(mContext, bus, securePreferencesManager, defaultPreferencesManager);
    }

    @Provides
    @Singleton
    final UserDataManager provideUserDataManager(
            final UserManager userManager,
            final DataManager dataManager,
            final Bus bus
    ) {
        return new UserDataManager(userManager, dataManager, bus);
    }

    @Provides
    @Singleton
    final ServicesManager provideServicesManager(
            final DataManager dataManager,
            final Bus bus,
            final SecurePreferencesManager securePreferencesManager,
            final ConfigurationManager configurationManager,
            final SessionManager sessionManager
    ) {
        return new ServicesManager(dataManager, bus, securePreferencesManager, configurationManager,
                                   sessionManager
        );
    }

    @Provides
    @Singleton
    final DefaultPreferencesManager provideDefaultPreferencesManager() {
        return new DefaultPreferencesManager(mContext);
    }

    @Provides
    @Singleton
    final DeepLinkIntentProvider provideDeepLinkNavigationManager(
            final UserManager userManager, final ConfigurationManager configurationManager
    ) {
        return new DeepLinkIntentProvider(userManager, configurationManager);
    }

    @Provides
    final LayerHelper provideHandyLayer() {
        return HandyLibrary.getInstance().getLayerHelper();
    }

    @Provides
    @Singleton
    final NavigationManager provideNavigationManager(
            final UserManager userManager,
            final DataManager dataManager,
            final DataManagerErrorHandler dataManagerErrorHandler
    ) {
        return new NavigationManager(
                this.mContext,
                userManager,
                dataManager,
                dataManagerErrorHandler
        );
    }

    @Provides
    @Singleton
    final AppBlockManager provideAppBlockManager(
            final Bus bus,
            final DataManager dataManager,
            final SecurePreferencesManager securePreferencesManager
    ) {
        return new AppBlockManager(bus, dataManager, securePreferencesManager);
    }

    @Provides
    @Singleton
    final StripeManager provideStripeManager(
            final Bus bus
    ) {
        return new StripeManager(bus, mConfigs);
    }

    @Provides
    @Singleton
    final UrbanAirshipManager provideUrbanAirshipManager(
            final Bus bus,
            final UserManager userManager
    ) {
        return new UrbanAirshipManager(mContext, bus, userManager);
    }

    @Provides
    @Singleton
    final ConfigurationManager provideConfigurationManager(
            final Bus bus,
            final DefaultPreferencesManager defaultPreferencesManager,
            final DataManager dataManager
    ) {
        return new ConfigurationManager(bus, defaultPreferencesManager, dataManager);
    }

    @Provides
    @Singleton
    final EventLogManager provideLogEventsManager(
            final Bus bus,
            final DataManager dataManager,
            final FileManager fileManager,
            final DefaultPreferencesManager defaultPreferencesManager,
            final UserManager userManager
    ) {
        return new EventLogManager(
                mContext,
                bus,
                dataManager,
                fileManager,
                defaultPreferencesManager,
                userManager
        );
    }

    @Provides
    @Singleton
    final ProTeamManager provideProTeamManager(
            final Bus bus,
            final HandyRetrofitService service,
            final UserManager userDataManager
    ) {
        return new ProTeamManager(bus, service, userDataManager);
    }

    private String getDeviceId() {
        return Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private String getDeviceName() {
        final String manufacturer = Build.MANUFACTURER;
        final String model = Build.MODEL;

        if (model.startsWith(manufacturer)) {
            return model;
        }
        else {
            return manufacturer + " " + model;
        }
    }
}
