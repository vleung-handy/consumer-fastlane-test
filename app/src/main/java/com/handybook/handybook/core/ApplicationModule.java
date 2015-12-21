package com.handybook.handybook.core;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.Base64;

import com.google.gson.GsonBuilder;
import com.handybook.handybook.BuildConfig;
import com.handybook.handybook.booking.manager.BookingManager;
import com.handybook.handybook.booking.model.BookingPostInfo;
import com.handybook.handybook.booking.model.BookingQuote;
import com.handybook.handybook.booking.model.BookingRequest;
import com.handybook.handybook.booking.model.BookingTransaction;
import com.handybook.handybook.booking.ui.activity.BookingAddressActivity;
import com.handybook.handybook.booking.ui.activity.BookingCancelOptionsActivity;
import com.handybook.handybook.booking.ui.activity.BookingConfirmationActivity;
import com.handybook.handybook.booking.ui.activity.BookingDateActivity;
import com.handybook.handybook.booking.ui.activity.BookingDetailActivity;
import com.handybook.handybook.booking.ui.activity.BookingEditAddressActivity;
import com.handybook.handybook.booking.ui.activity.BookingEditEntryInformationActivity;
import com.handybook.handybook.booking.ui.activity.BookingEditExtrasActivity;
import com.handybook.handybook.booking.ui.activity.BookingEditFrequencyActivity;
import com.handybook.handybook.booking.ui.activity.BookingEditHoursActivity;
import com.handybook.handybook.booking.ui.activity.BookingEditNoteToProActivity;
import com.handybook.handybook.booking.ui.activity.BookingExtrasActivity;
import com.handybook.handybook.booking.ui.activity.BookingLocationActivity;
import com.handybook.handybook.booking.ui.activity.BookingOptionsActivity;
import com.handybook.handybook.booking.ui.activity.BookingPaymentActivity;
import com.handybook.handybook.booking.ui.activity.BookingRecurrenceActivity;
import com.handybook.handybook.booking.ui.activity.BookingRescheduleOptionsActivity;
import com.handybook.handybook.booking.ui.activity.BookingsActivity;
import com.handybook.handybook.booking.ui.activity.CancelRecurringBookingActivity;
import com.handybook.handybook.booking.ui.activity.PeakPricingActivity;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.booking.ui.activity.ServicesActivity;
import com.handybook.handybook.booking.ui.fragment.AddLaundryDialogFragment;
import com.handybook.handybook.booking.ui.fragment.BookingAddressFragment;
import com.handybook.handybook.booking.ui.fragment.BookingCancelOptionsFragment;
import com.handybook.handybook.booking.ui.fragment.BookingConfirmationFragment;
import com.handybook.handybook.booking.ui.fragment.BookingDateFragment;
import com.handybook.handybook.booking.ui.fragment.BookingDetailFragment;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragment;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentAddress;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentBookingActions;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentEntryInformation;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentExtras;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentLaundry;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentNoteToPro;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentPayment;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentProInformation;
import com.handybook.handybook.booking.ui.fragment.BookingEditAddressFragment;
import com.handybook.handybook.booking.ui.fragment.BookingEditEntryInformationFragment;
import com.handybook.handybook.booking.ui.fragment.BookingEditExtrasFragment;
import com.handybook.handybook.booking.ui.fragment.BookingEditFrequencyFragment;
import com.handybook.handybook.booking.ui.fragment.BookingEditHoursFragment;
import com.handybook.handybook.booking.ui.fragment.BookingEditNoteToProFragment;
import com.handybook.handybook.booking.ui.fragment.BookingExtrasFragment;
import com.handybook.handybook.booking.ui.fragment.BookingHeaderFragment;
import com.handybook.handybook.booking.ui.fragment.BookingListFragment;
import com.handybook.handybook.booking.ui.fragment.BookingLocationFragment;
import com.handybook.handybook.booking.ui.fragment.BookingOptionsFragment;
import com.handybook.handybook.booking.ui.fragment.BookingPaymentFragment;
import com.handybook.handybook.booking.ui.fragment.BookingRecurrenceFragment;
import com.handybook.handybook.booking.ui.fragment.BookingRescheduleOptionsFragment;
import com.handybook.handybook.booking.ui.fragment.BookingsFragment;
import com.handybook.handybook.booking.ui.fragment.CancelRecurringBookingFragment;
import com.handybook.handybook.booking.ui.fragment.EmailCancellationDialogFragment;
import com.handybook.handybook.booking.ui.fragment.LaundryDropOffDialogFragment;
import com.handybook.handybook.booking.ui.fragment.LaundryInfoDialogFragment;
import com.handybook.handybook.booking.ui.fragment.PeakPricingFragment;
import com.handybook.handybook.booking.ui.fragment.PeakPricingTableFragment;
import com.handybook.handybook.booking.ui.fragment.PromosActivity;
import com.handybook.handybook.booking.ui.fragment.PromosFragment;
import com.handybook.handybook.booking.ui.fragment.RateServiceConfirmDialogFragment;
import com.handybook.handybook.booking.ui.fragment.RateServiceDialogFragment;
import com.handybook.handybook.booking.ui.fragment.ServiceCategoriesFragment;
import com.handybook.handybook.booking.ui.fragment.ServicesFragment;
import com.handybook.handybook.booking.ui.fragment.TipDialogFragment;
import com.handybook.handybook.data.BaseDataManager;
import com.handybook.handybook.data.BaseDataManagerErrorHandler;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.data.DataManagerErrorHandler;
import com.handybook.handybook.data.HandyRetrofitEndpoint;
import com.handybook.handybook.data.HandyRetrofitService;
import com.handybook.handybook.data.Mixpanel;
import com.handybook.handybook.data.PropertiesReader;
import com.handybook.handybook.data.SecurePreferences;
import com.handybook.handybook.helpcenter.helpcontact.manager.HelpContactManager;
import com.handybook.handybook.helpcenter.helpcontact.ui.activity.HelpContactActivity;
import com.handybook.handybook.helpcenter.helpcontact.ui.fragment.HelpContactFragment;
import com.handybook.handybook.helpcenter.manager.HelpManager;
import com.handybook.handybook.helpcenter.ui.activity.HelpActivity;
import com.handybook.handybook.helpcenter.ui.fragment.HelpFragment;
import com.handybook.handybook.manager.AppBlockManager;
import com.handybook.handybook.manager.PrefsManager;
import com.handybook.handybook.manager.StripeManager;
import com.handybook.handybook.manager.UserDataManager;
import com.handybook.handybook.module.notifications.manager.NotificationManager;
import com.handybook.handybook.module.notifications.view.activity.NotificationsActivity;
import com.handybook.handybook.module.notifications.view.fragment.NotificationFeedFragment;
import com.handybook.handybook.module.notifications.view.fragment.SplashPromoDialogFragment;
import com.handybook.handybook.module.push.manager.UrbanAirshipManager;
import com.handybook.handybook.ui.activity.BaseActivity;
import com.handybook.handybook.ui.activity.BlockingActivity;
import com.handybook.handybook.ui.activity.LoginActivity;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.ui.activity.OnboardActivity;
import com.handybook.handybook.ui.activity.ProfileActivity;
import com.handybook.handybook.ui.activity.SplashActivity;
import com.handybook.handybook.ui.activity.UpdatePaymentActivity;
import com.handybook.handybook.ui.fragment.BlockingUpdateFragment;
import com.handybook.handybook.ui.fragment.LoginFragment;
import com.handybook.handybook.ui.fragment.NavbarWebViewDialogFragment;
import com.handybook.handybook.ui.fragment.NavigationFragment;
import com.handybook.handybook.ui.fragment.OnboardFragment;
import com.handybook.handybook.ui.fragment.OnboardPageFragment;
import com.handybook.handybook.ui.fragment.ProfileFragment;
import com.handybook.handybook.ui.fragment.UpdatePaymentFragment;
import com.handybook.handybook.yozio.YozioMetaDataCallback;
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
        BaseActivity.class,
        ServiceCategoriesFragment.class,
        LoginFragment.class,
        NavigationFragment.class,
        ProfileFragment.class,
        BookingsFragment.class,
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
        BookingConfirmationFragment.class,
        PromosFragment.class,
        BookingExtrasFragment.class,
        BookingRecurrenceFragment.class,
        BaseDataManager.class,
        ServiceCategoriesActivity.class,
        ServicesActivity.class, ProfileActivity.class,
        PeakPricingActivity.class,
        MenuDrawerActivity.class,
        LoginActivity.class,
        BookingsActivity.class,
        BookingRecurrenceActivity.class,
        BookingPaymentActivity.class,
        BookingOptionsActivity.class,
        BookingLocationActivity.class,
        BookingExtrasActivity.class,
        BookingDetailActivity.class,
        BookingDateActivity.class,
        BookingConfirmationActivity.class,
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
        HelpActivity.class,
        HelpFragment.class,
        RateServiceDialogFragment.class,
        RateServiceConfirmDialogFragment.class,
        LaundryDropOffDialogFragment.class,
        LaundryInfoDialogFragment.class,
        AddLaundryDialogFragment.class,
        HelpContactFragment.class,
        HelpContactActivity.class,
        SplashActivity.class,
        BookingDetailSectionFragment.class,
        BookingDetailSectionFragmentAddress.class,
        BookingDetailSectionFragmentEntryInformation.class,
        BookingDetailSectionFragmentExtras.class,
        BookingDetailSectionFragmentLaundry.class,
        BookingDetailSectionFragmentNoteToPro.class,
        BookingDetailSectionFragmentPayment.class,
        BookingDetailSectionFragmentProInformation.class,
        BookingDetailSectionFragmentBookingActions.class,
        BookingEditNoteToProActivity.class,
        BookingEditNoteToProFragment.class,
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
        NotificationsActivity.class,
        BlockingUpdateFragment.class,
        TipDialogFragment.class,
        NotificationFeedFragment.class,
        CancelRecurringBookingActivity.class,
        CancelRecurringBookingFragment.class,
        EmailCancellationDialogFragment.class,
        UpdatePaymentActivity.class,
        UpdatePaymentFragment.class,
        NavbarWebViewDialogFragment.class,
        SplashPromoDialogFragment.class
        //TODO: WE NEED TO STOP MAKING NEW ACTIVITIES
})
public final class ApplicationModule
{
    private final Context mContext;
    private final Properties mConfigs;

    public ApplicationModule(final Context context)
    {
        mContext = context.getApplicationContext();
        mConfigs = PropertiesReader
                .getProperties(context, "config.properties");
    }

    @Provides
    @Singleton
    public Properties provideProperties()
    {
        return mConfigs;
    }

    @Provides
    @Singleton
    final EnvironmentModifier provideEnvironmentModifier(Bus bus, PrefsManager prefsManager)
    {
        EnvironmentModifier environmentModifier = new EnvironmentModifier(mContext, bus, prefsManager);
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
    final HandyRetrofitService provideHandyService(final HandyRetrofitEndpoint endpoint,
                                                   final UserManager userManager)
    {

        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(60, TimeUnit.SECONDS);

        final String username = mConfigs.getProperty("api_username");
        String password = mConfigs.getProperty("api_password_internal");

        if (BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_PROD))
        {
            password = mConfigs.getProperty("api_password");
        }

        final String pwd = password;

        final RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(endpoint)
                .setRequestInterceptor(new RequestInterceptor()
                {
                    final String auth = "Basic " + Base64.encodeToString((username + ":" + pwd)
                            .getBytes(), Base64.NO_WRAP);

                    @Override
                    public void intercept(RequestFacade request)
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
                            request.addQueryParam("auth_token", user.getAuthToken());
                        }
                    }
                }).setConverter(new GsonConverter(new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                        .setExclusionStrategies(BookingRequest.getExclusionStrategy())
                        .registerTypeAdapter(BookingRequest.class,
                                new BookingRequest.BookingRequestSerializer())
                        .setExclusionStrategies(BookingQuote.getExclusionStrategy())
                        .registerTypeAdapter(BookingQuote.class,
                                new BookingQuote.BookingQuoteSerializer())
                        .setExclusionStrategies(BookingPostInfo.getExclusionStrategy())
                        .registerTypeAdapter(BookingPostInfo.class,
                                new BookingPostInfo.BookingPostInfoSerializer())
                        .setExclusionStrategies(BookingTransaction.getExclusionStrategy())
                        .registerTypeAdapter(BookingTransaction.class,
                                new BookingTransaction.BookingTransactionSerializer())
                        .setExclusionStrategies(User.getExclusionStrategy())
                        .registerTypeAdapter(User.class, new User.UserSerializer())
                        .create())).setClient(new OkClient(okHttpClient)).build();

        if (!BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_PROD)
                || BuildConfig.BUILD_TYPE.equals("debug"))
        {
            restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
        }

        return restAdapter.create(HandyRetrofitService.class);
    }

    @Provides
    @Singleton
    final DataManager provideDataManager(final HandyRetrofitService service,
                                         final HandyRetrofitEndpoint endpoint,
                                         final PrefsManager prefsManager)
    {
        final BaseDataManager dataManager = new BaseDataManager(service, endpoint, prefsManager);

        return dataManager;
    }

    @Provides
    final DataManagerErrorHandler provideDataManagerErrorHandler()
    {
        return new BaseDataManagerErrorHandler();
    }

    @Provides
    @Singleton
    final Bus provideBus(final Mixpanel mixpanel)
    {
        return new MainBus(mixpanel);
    }

    @Provides
    @Singleton
    final SecurePreferences providePrefs()
    {
        return new SecurePreferences(mContext, null,
                mConfigs.getProperty("secure_prefs_key"), true);
    }

    @Provides
    @Singleton
    final BookingManager provideBookingManager(
            final Bus bus,
            final PrefsManager prefsManager,
            final DataManager dataManager
    )
    {
        return new BookingManager(bus, prefsManager, dataManager);
    }

    @Provides
    @Singleton
    final UserManager provideUserManager(
            final Bus bus,
            final PrefsManager prefsManager)
    {
        return new UserManager(bus, prefsManager);
    }

    @Provides
    @Singleton
    final UserDataManager provideUserDataManager(final UserManager userManager,
                                                 final DataManager dataManager,
                                                 final Bus bus)
    {
        return new UserDataManager(userManager, dataManager, bus);
    }

    @Provides
    @Singleton
    final Mixpanel provideMixpanel(final PrefsManager prefsManager)
    {
        return new Mixpanel(mContext, prefsManager);
    }

    @Provides
    @Singleton
    final NavigationManager provideNavigationManager(
            final UserManager userManager,
            final DataManager dataManager,
            final DataManagerErrorHandler dataManagerErrorHandler)
    {
        return new NavigationManager(this.mContext, userManager, dataManager, dataManagerErrorHandler);
    }

    @Provides
    @Singleton
    final HelpManager provideHelpManager(
            final Bus bus,
            final DataManager dataManager,
            final UserManager userManager
    )
    {
        return new HelpManager(bus, dataManager, userManager);
    }

    @Provides
    @Singleton
    final HelpContactManager provideHelpContactManager(
            final Bus bus,
            final DataManager dataManager
    )
    {
        return new HelpContactManager(bus, dataManager);
    }

    @Provides
    @Singleton
    final AppBlockManager provideAppBlockManager(
            final Bus bus,
            final DataManager dataManager,
            final PrefsManager prefsManager
    )
    {
        return new AppBlockManager(bus, dataManager, prefsManager);
    }

    @Provides
    @Singleton
    final StripeManager provideStripeManager(final Bus bus)
    {
        return new StripeManager(bus, mConfigs);
    }

    @Provides
    @Singleton
    final UrbanAirshipManager provideUrbanAirshipManager(final Bus bus,
                                                         final UserManager userManager
    )
    {
        return new UrbanAirshipManager(mContext, bus, userManager);
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

    @Provides
    @Singleton
    final NotificationManager provideNotificationManager(
            final Bus bus,
            final DataManager dataManager
    )
    {
        return new NotificationManager(bus, dataManager);
    }

}
