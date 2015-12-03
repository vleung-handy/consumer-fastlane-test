package com.handybook.handybook.core;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.Base64;

import com.google.gson.GsonBuilder;
import com.handybook.handybook.BuildConfig;
import com.handybook.handybook.data.BaseDataManager;
import com.handybook.handybook.data.BaseDataManagerErrorHandler;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.data.DataManagerErrorHandler;
import com.handybook.handybook.data.HandyRetrofitEndpoint;
import com.handybook.handybook.data.HandyRetrofitService;
import com.handybook.handybook.data.Mixpanel;
import com.handybook.handybook.data.PropertiesReader;
import com.handybook.handybook.data.SecurePreferences;
import com.handybook.handybook.manager.AppBlockManager;
import com.handybook.handybook.manager.HelpContactManager;
import com.handybook.handybook.manager.HelpManager;
import com.handybook.handybook.manager.PrefsManager;
import com.handybook.handybook.ui.activity.BlockingActivity;
import com.handybook.handybook.ui.activity.BookingAddressActivity;
import com.handybook.handybook.ui.activity.BookingCancelOptionsActivity;
import com.handybook.handybook.ui.activity.BookingConfirmationActivity;
import com.handybook.handybook.ui.activity.BookingDateActivity;
import com.handybook.handybook.ui.activity.BookingDetailActivity;
import com.handybook.handybook.ui.activity.BookingEditEntryInformationActivity;
import com.handybook.handybook.ui.activity.BookingEditExtrasActivity;
import com.handybook.handybook.ui.activity.BookingEditFrequencyActivity;
import com.handybook.handybook.ui.activity.BookingEditHoursActivity;
import com.handybook.handybook.ui.activity.BookingEditNoteToProActivity;
import com.handybook.handybook.ui.activity.BookingExtrasActivity;
import com.handybook.handybook.ui.activity.BookingLocationActivity;
import com.handybook.handybook.ui.activity.BookingOptionsActivity;
import com.handybook.handybook.ui.activity.BookingPaymentActivity;
import com.handybook.handybook.ui.activity.BookingRecurrenceActivity;
import com.handybook.handybook.ui.activity.BookingRescheduleOptionsActivity;
import com.handybook.handybook.ui.activity.BookingsActivity;
import com.handybook.handybook.ui.activity.HelpActivity;
import com.handybook.handybook.ui.activity.HelpContactActivity;
import com.handybook.handybook.ui.activity.LoginActivity;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.ui.activity.OnboardActivity;
import com.handybook.handybook.ui.activity.PeakPricingActivity;
import com.handybook.handybook.ui.activity.ProfileActivity;
import com.handybook.handybook.ui.activity.PromosActivity;
import com.handybook.handybook.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.ui.activity.ServicesActivity;
import com.handybook.handybook.ui.activity.SplashActivity;
import com.handybook.handybook.ui.fragment.AddLaundryDialogFragment;
import com.handybook.handybook.ui.fragment.BlockingUpdateFragment;
import com.handybook.handybook.ui.fragment.BookingAddressFragment;
import com.handybook.handybook.ui.fragment.BookingCancelOptionsFragment;
import com.handybook.handybook.ui.fragment.BookingConfirmationFragment;
import com.handybook.handybook.ui.fragment.BookingDateFragment;
import com.handybook.handybook.ui.fragment.BookingDetailFragment;
import com.handybook.handybook.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragment;
import com.handybook.handybook.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentAddress;
import com.handybook.handybook.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentBookingActions;
import com.handybook.handybook.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentEntryInformation;
import com.handybook.handybook.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentExtras;
import com.handybook.handybook.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentLaundry;
import com.handybook.handybook.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentNoteToPro;
import com.handybook.handybook.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentPayment;
import com.handybook.handybook.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentProInformation;
import com.handybook.handybook.ui.fragment.BookingEditEntryInformationFragment;
import com.handybook.handybook.ui.fragment.BookingEditExtrasFragment;
import com.handybook.handybook.ui.fragment.BookingEditFrequencyFragment;
import com.handybook.handybook.ui.fragment.BookingEditHoursFragment;
import com.handybook.handybook.ui.fragment.BookingEditNoteToProFragment;
import com.handybook.handybook.ui.fragment.BookingExtrasFragment;
import com.handybook.handybook.ui.fragment.BookingHeaderFragment;
import com.handybook.handybook.ui.fragment.BookingListFragment;
import com.handybook.handybook.ui.fragment.BookingLocationFragment;
import com.handybook.handybook.ui.fragment.BookingOptionsFragment;
import com.handybook.handybook.ui.fragment.BookingPaymentFragment;
import com.handybook.handybook.ui.fragment.BookingRecurrenceFragment;
import com.handybook.handybook.ui.fragment.BookingRescheduleOptionsFragment;
import com.handybook.handybook.ui.fragment.BookingsFragment;
import com.handybook.handybook.ui.fragment.HelpContactFragment;
import com.handybook.handybook.ui.fragment.HelpFragment;
import com.handybook.handybook.ui.fragment.LaundryDropOffDialogFragment;
import com.handybook.handybook.ui.fragment.LaundryInfoDialogFragment;
import com.handybook.handybook.ui.fragment.LoginFragment;
import com.handybook.handybook.ui.fragment.NavigationFragment;
import com.handybook.handybook.ui.fragment.OnboardFragment;
import com.handybook.handybook.ui.fragment.OnboardPageFragment;
import com.handybook.handybook.ui.fragment.PeakPricingFragment;
import com.handybook.handybook.ui.fragment.PeakPricingTableFragment;
import com.handybook.handybook.ui.fragment.ProfileFragment;
import com.handybook.handybook.ui.fragment.PromosFragment;
import com.handybook.handybook.ui.fragment.RateServiceConfirmDialogFragment;
import com.handybook.handybook.ui.fragment.RateServiceDialogFragment;
import com.handybook.handybook.ui.fragment.ServiceCategoriesFragment;
import com.handybook.handybook.ui.fragment.ServicesFragment;
import com.handybook.handybook.ui.fragment.TipDialogFragment;
import com.handybook.handybook.ui.fragment.NavbarWebViewDialogFragment;
import com.handybook.handybook.yozio.YozioMetaDataCallback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.otto.Bus;
import com.stripe.android.Stripe;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

@Module(injects = {
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
        BlockingActivity.class,
        BlockingUpdateFragment.class,
        TipDialogFragment.class,
        NavbarWebViewDialogFragment.class
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
    final BookingManager provideBookingManager(final Bus bus,
                                               final PrefsManager prefsManager,
                                               final DataManager dataManager
    )
    {
        return new BookingManager(bus, prefsManager, dataManager);
    }

    @Provides
    @Singleton
    final UserManager provideUserManager(final Bus bus,
                                         final PrefsManager prefsManager)
    {
        return new UserManager(bus, prefsManager);
    }

    @Provides
    final ReactiveLocationProvider provideReactiveLocationProvider()
    {
        return new ReactiveLocationProvider(mContext);
    }

    @Provides
    @Singleton
    final Mixpanel provideMixpanel(final PrefsManager prefsManager)
    {
        return new Mixpanel(mContext, prefsManager);
    }

    @Provides
    @Singleton
    final NavigationManager provideNavigationManager(final UserManager userManager,
                                                     final DataManager dataManager,
                                                     final DataManagerErrorHandler dataManagerErrorHandler)
    {
        return new NavigationManager(this.mContext, userManager, dataManager, dataManagerErrorHandler);
    }

    @Provides
    @Singleton
    final HelpManager provideHelpManager(final Bus bus,
                                         final DataManager dataManager,
                                         final UserManager userManager
    )
    {
        return new HelpManager(bus, dataManager, userManager);
    }

    @Provides
    @Singleton
    final HelpContactManager provideHelpContactManager(final Bus bus,
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
    final Stripe provideStripe()
    {
        return new Stripe();
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
