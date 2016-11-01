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
import com.handybook.handybook.account.ui.EditPlanAddressFragment;
import com.handybook.handybook.account.ui.EditPlanFragment;
import com.handybook.handybook.account.ui.EditPlanFrequencyFragment;
import com.handybook.handybook.account.ui.PlansFragment;
import com.handybook.handybook.account.ui.ProfileActivity;
import com.handybook.handybook.account.ui.ProfileFragment;
import com.handybook.handybook.account.ui.ProfilePasswordFragment;
import com.handybook.handybook.account.ui.UpdatePaymentFragment;
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
import com.handybook.handybook.booking.manager.BookingManager;
import com.handybook.handybook.booking.model.BookingPostInfo;
import com.handybook.handybook.booking.model.BookingQuote;
import com.handybook.handybook.booking.model.BookingRequest;
import com.handybook.handybook.booking.model.BookingTransaction;
import com.handybook.handybook.booking.rating.RateImprovementConfirmationDialogFragment;
import com.handybook.handybook.booking.rating.RateImprovementDialogFragment;
import com.handybook.handybook.booking.rating.RatingsGridFragment;
import com.handybook.handybook.booking.rating.RatingsRadioFragment;
import com.handybook.handybook.booking.ui.activity.BookingAddressActivity;
import com.handybook.handybook.booking.ui.activity.BookingCancelOptionsActivity;
import com.handybook.handybook.booking.ui.activity.BookingDateActivity;
import com.handybook.handybook.booking.ui.activity.BookingDetailActivity;
import com.handybook.handybook.booking.ui.activity.BookingExtrasActivity;
import com.handybook.handybook.booking.ui.activity.BookingFinalizeActivity;
import com.handybook.handybook.booking.ui.activity.BookingLocationActivity;
import com.handybook.handybook.booking.ui.activity.BookingOptionsActivity;
import com.handybook.handybook.booking.ui.activity.BookingPaymentActivity;
import com.handybook.handybook.booking.ui.activity.BookingProTeamActivity;
import com.handybook.handybook.booking.ui.activity.BookingRecurrenceActivity;
import com.handybook.handybook.booking.ui.activity.BookingRescheduleOptionsActivity;
import com.handybook.handybook.booking.ui.activity.BookingsActivity;
import com.handybook.handybook.booking.ui.activity.CancelRecurringBookingActivity;
import com.handybook.handybook.booking.ui.activity.PeakPricingActivity;
import com.handybook.handybook.booking.ui.activity.PromosActivity;
import com.handybook.handybook.booking.ui.activity.ReportIssueActivity;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.booking.ui.activity.ServicesActivity;
import com.handybook.handybook.booking.ui.fragment.AddLaundryDialogFragment;
import com.handybook.handybook.booking.ui.fragment.BookingAddressFragment;
import com.handybook.handybook.booking.ui.fragment.BookingCancelOptionsFragment;
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
import com.handybook.handybook.booking.ui.fragment.BookingListFragment;
import com.handybook.handybook.booking.ui.fragment.BookingLocationFragment;
import com.handybook.handybook.booking.ui.fragment.BookingOptionsFragment;
import com.handybook.handybook.booking.ui.fragment.BookingPasswordPromptFragment;
import com.handybook.handybook.booking.ui.fragment.BookingPaymentFragment;
import com.handybook.handybook.booking.ui.fragment.BookingPreferencesFragment;
import com.handybook.handybook.booking.ui.fragment.BookingProTeamFragment;
import com.handybook.handybook.booking.ui.fragment.BookingRecurrenceFragment;
import com.handybook.handybook.booking.ui.fragment.BookingRescheduleOptionsFragment;
import com.handybook.handybook.booking.ui.fragment.BookingsFragment;
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
import com.handybook.handybook.booking.ui.fragment.ServiceCategoriesFragment;
import com.handybook.handybook.booking.ui.fragment.ServicesFragment;
import com.handybook.handybook.booking.ui.fragment.TipDialogFragment;
import com.handybook.handybook.booking.ui.view.ServiceCategoriesOverlayFragment;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.data.DataManagerErrorHandler;
import com.handybook.handybook.data.HandyRetrofitEndpoint;
import com.handybook.handybook.data.HandyRetrofitService;
import com.handybook.handybook.data.SecurePreferences;
import com.handybook.handybook.deeplink.DeepLinkIntentProvider;
import com.handybook.handybook.helpcenter.HelpModule;
import com.handybook.handybook.library.ui.fragment.WebViewFragment;
import com.handybook.handybook.library.util.PropertiesReader;
import com.handybook.handybook.logger.handylogger.EventLogManager;
import com.handybook.handybook.manager.AppBlockManager;
import com.handybook.handybook.manager.AppseeManager;
import com.handybook.handybook.manager.DefaultPreferencesManager;
import com.handybook.handybook.manager.FileManager;
import com.handybook.handybook.manager.SecurePreferencesManager;
import com.handybook.handybook.manager.ServicesManager;
import com.handybook.handybook.manager.StripeManager;
import com.handybook.handybook.manager.UserDataManager;
import com.handybook.handybook.module.autocomplete.AddressAutoCompleteManager;
import com.handybook.handybook.module.autocomplete.AutoCompleteAddressFragment;
import com.handybook.handybook.module.autocomplete.PlacesService;
import com.handybook.handybook.module.bookings.ActiveBookingFragment;
import com.handybook.handybook.module.bookings.HistoryActivity;
import com.handybook.handybook.module.bookings.HistoryFragment;
import com.handybook.handybook.module.bookings.UpcomingBookingsFragment;
import com.handybook.handybook.module.configuration.manager.ConfigurationManager;
import com.handybook.handybook.module.notifications.NotificationsModule;
import com.handybook.handybook.module.proteam.manager.ProTeamManager;
import com.handybook.handybook.module.proteam.ui.activity.ProTeamActivity;
import com.handybook.handybook.module.proteam.ui.fragment.ProTeamFragment;
import com.handybook.handybook.module.proteam.ui.fragment.ProTeamProListFragment;
import com.handybook.handybook.module.proteam.ui.fragment.RemoveProDialogFragment;
import com.handybook.handybook.module.push.manager.UrbanAirshipManager;
import com.handybook.handybook.module.push.receiver.PushReceiver;
import com.handybook.handybook.module.referral.manager.ReferralsManager;
import com.handybook.handybook.module.referral.ui.RedemptionActivity;
import com.handybook.handybook.module.referral.ui.RedemptionEmailSignUpFragment;
import com.handybook.handybook.module.referral.ui.RedemptionFragment;
import com.handybook.handybook.module.referral.ui.RedemptionSignUpFragment;
import com.handybook.handybook.module.referral.ui.ReferralActivity;
import com.handybook.handybook.module.referral.ui.ReferralFragment;
import com.handybook.handybook.ui.activity.BlockingActivity;
import com.handybook.handybook.ui.activity.LoginActivity;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.ui.activity.OnboardActivity;
import com.handybook.handybook.ui.activity.SplashActivity;
import com.handybook.handybook.ui.activity.UpdatePaymentActivity;
import com.handybook.handybook.ui.activity.WebViewActivity;
import com.handybook.handybook.ui.fragment.BlockingUpdateFragment;
import com.handybook.handybook.ui.fragment.LoginFragment;
import com.handybook.handybook.ui.fragment.NavbarWebViewDialogFragment;
import com.handybook.handybook.ui.fragment.OnboardFragment;
import com.handybook.handybook.ui.fragment.OnboardPageFragment;
import com.handybook.handybook.yozio.YozioMetaDataCallback;
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
        ServiceCategoriesFragment.class,
        LoginFragment.class,
        ProfileFragment.class,
        BookingsFragment.class,
        UpcomingBookingsFragment.class,
        HistoryFragment.class,
        ActiveBookingFragment.class,
        AutoCompleteAddressFragment.class,
        BookingListFragment.class,
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
        BookingCancelOptionsFragment.class,
        YozioMetaDataCallback.class,
        OnboardActivity.class,
        OnboardFragment.class,
        OnboardPageFragment.class,
        RateServiceDialogFragment.class,
        RateServiceConfirmDialogFragment.class,
        LaundryDropOffDialogFragment.class,
        LaundryInfoDialogFragment.class,
        AddLaundryDialogFragment.class,
        SplashActivity.class,
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
        ReferralActivity.class,
        ReferralFragment.class,
        ReferralDialogFragment.class,
        RedemptionActivity.class,
        RedemptionFragment.class,
        RedemptionSignUpFragment.class,
        RedemptionEmailSignUpFragment.class,
        RateImprovementDialogFragment.class,
        RatingsGridFragment.class,
        RateProTeamFragment.class,
        RatingsRadioFragment.class,
        RateImprovementConfirmationDialogFragment.class,
        ProTeamActivity.class,
        ProTeamFragment.class,
        ProTeamProListFragment.class,
        BookingProTeamActivity.class,
        BookingProTeamFragment.class,
        RemoveProDialogFragment.class,
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
        //TODO: WE NEED TO STOP MAKING NEW ACTIVITIES
},
        includes = {
                HelpModule.class,
                NotificationsModule.class,
                //FIXME add more
        }
)
public final class ApplicationModule
{
    private final Context mContext;
    private final Properties mConfigs;

    public ApplicationModule(final Context context)
    {
        mContext = context.getApplicationContext();
        mConfigs = PropertiesReader.getProperties(context, "config.properties");
    }

    @Provides
    @Singleton
    Properties provideProperties()
    {
        return mConfigs;
    }

    @Provides
    @Singleton
    final EnvironmentModifier provideEnvironmentModifier(
            Bus bus,
            DefaultPreferencesManager defaultPreferencesManager
    )
    {
        EnvironmentModifier environmentModifier = new EnvironmentModifier(mContext, bus,
                                                                          defaultPreferencesManager
        );
        if (BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_PROD))
        {
            environmentModifier.setEnvironment(EnvironmentModifier.Environment.PRODUCTION);
        }
        return environmentModifier;
    }

    @Provides
    @Singleton
    final HandyRetrofitEndpoint provideHandyRetrofitEndpoint(EnvironmentModifier environmentModifier)
    {
        return new HandyRetrofitEndpoint(mContext, environmentModifier);
    }

    @Provides
    @Singleton
    final PlacesService providesPlacesService()
    {
        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(mContext.getString(R.string.places_api_base_url))
                .setRequestInterceptor(new RequestInterceptor()
                {
                    @Override
                    public void intercept(RequestFacade request)
                    {
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
    final HandyRetrofitService provideHandyService(
            final HandyRetrofitEndpoint endpoint,
            final UserManager userManager
    )
    {
        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(60, TimeUnit.SECONDS);
        if (BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_PROD) && !BuildConfig.DEBUG)
        {
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
        if (BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_PROD))
        {
            password = mConfigs.getProperty("api_password");
        }
        final String pwd = password;
        final RestAdapter restAdapter =
                new RestAdapter
                        .Builder()
                        .setEndpoint(endpoint)
                        .setRequestInterceptor(new RequestInterceptor()
                        {
                            final String auth = "Basic " + Base64.encodeToString(
                                    (username + ":" + pwd).getBytes(),
                                    Base64.NO_WRAP
                            );

                            @Override
                            public void intercept(
                                    RequestFacade request
                            )
                            {
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
                                if (user != null)
                                {
                                    request.addQueryParam("app_user_id", user.getId());
                                    String authToken = user.getAuthToken();
                                    if (authToken != null)
                                    {
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
                || BuildConfig.BUILD_TYPE.equals("debug"))
        {
            restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
        }
        return restAdapter.create(HandyRetrofitService.class);
    }

    @Provides
    @Singleton
    final DataManager provideDataManager(
            final HandyRetrofitService service,
            final HandyRetrofitEndpoint endpoint,
            final SecurePreferencesManager securePreferencesManager
    )
    {
        return new DataManager(service, endpoint, securePreferencesManager);
    }

    @Provides
    @Singleton
    final AppseeManager provideAppseeManager(
            final ConfigurationManager configurationManager,
            final FileManager fileManager
    )
    {
        String appseeApiKey = mConfigs.getProperty
                (BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_PROD) ?
                         "app_see_key"
                         : "app_see_key_internal");
        return new AppseeManager(appseeApiKey, configurationManager, fileManager);
    }

    @Provides
    final DataManagerErrorHandler provideDataManagerErrorHandler()
    {
        return new DataManagerErrorHandler();
    }

    @Provides
    @Singleton
    final Bus provideBus()
    {
        return new MainBus();
    }

    @Provides
    @Singleton
    final SecurePreferences providePrefs()
    {
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
    )
    {
        return new BookingManager(bus, securePreferencesManager, dataManager);
    }


    @Provides
    @Singleton
    final FileManager provideFileManager()
    {
        return new FileManager();
    }

    @Provides
    @Singleton
    final AddressAutoCompleteManager provideAddressAutoCompleteManager(
            final Bus bus,
            final PlacesService service
    )
    {
        return new AddressAutoCompleteManager(bus, service);
    }

    @Provides
    @Singleton
    final BookingEditManager provideBookingEditManager(
            final Bus bus,
            final DataManager dataManager
    )
    {
        return new BookingEditManager(bus, dataManager);
    }

    @Provides
    @Singleton
    final UserManager provideUserManager(
            final Bus bus,
            final SecurePreferencesManager securePreferencesManager
    )
    {
        return new UserManager(mContext, bus, securePreferencesManager);
    }

    @Provides
    @Singleton
    final UserDataManager provideUserDataManager(
            final UserManager userManager,
            final DataManager dataManager,
            final Bus bus
    )
    {
        return new UserDataManager(userManager, dataManager, bus);
    }

    @Provides
    @Singleton
    final ServicesManager provideServicesManager(
            final DataManager dataManager,
            final Bus bus
    )
    {
        return new ServicesManager(dataManager, bus);
    }

    @Provides
    @Singleton
    final DefaultPreferencesManager provideDefaultPreferencesManager()
    {
        return new DefaultPreferencesManager(mContext);
    }

    @Provides
    @Singleton
    final DeepLinkIntentProvider provideDeepLinkNavigationManager(
            final UserManager userManager
    )
    {
        return new DeepLinkIntentProvider(userManager);
    }

    @Provides
    @Singleton
    final NavigationManager provideNavigationManager(
            final UserManager userManager,
            final DataManager dataManager,
            final DataManagerErrorHandler dataManagerErrorHandler
    )
    {
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
    )
    {
        return new AppBlockManager(bus, dataManager, securePreferencesManager);
    }

    @Provides
    @Singleton
    final StripeManager provideStripeManager(
            final Bus bus
    )
    {
        return new StripeManager(bus, mConfigs);
    }

    @Provides
    @Singleton
    final UrbanAirshipManager provideUrbanAirshipManager(
            final Bus bus,
            final UserManager userManager
    )
    {
        return new UrbanAirshipManager(mContext, bus, userManager);
    }

    @Provides
    @Singleton
    final ReferralsManager provideReferralsManager(
            final Bus bus,
            final DataManager dataManager,
            final DefaultPreferencesManager defaultPreferencesManager
    )
    {
        return new ReferralsManager(bus, dataManager, defaultPreferencesManager);
    }

    @Provides
    @Singleton
    final ConfigurationManager provideConfigurationManager(
            final Bus bus,
            final DefaultPreferencesManager defaultPreferencesManager,
            final DataManager dataManager
    )
    {
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
    )
    {
        return new EventLogManager(bus, dataManager, fileManager, defaultPreferencesManager, userManager);
    }

    @Provides
    @Singleton
    final ProTeamManager provideProTeamManager(
            final Bus bus,
            final HandyRetrofitService service,
            final UserManager userDataManager
    )
    {
        return new ProTeamManager(bus, service, userDataManager);
    }


    private String getDeviceId()
    {
        return Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private String getDeviceName()
    {
        final String manufacturer = Build.MANUFACTURER;
        final String model = Build.MODEL;

        if (model.startsWith(manufacturer))
        {
            return model;
        }
        else
        {
            return manufacturer + " " + model;
        }
    }
}
