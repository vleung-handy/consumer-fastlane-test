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
import com.handybook.handybook.ui.activity.BookingAddressActivity;
import com.handybook.handybook.ui.activity.BookingCancelOptionsActivity;
import com.handybook.handybook.ui.activity.BookingConfirmationActivity;
import com.handybook.handybook.ui.activity.BookingDateActivity;
import com.handybook.handybook.ui.activity.BookingDetailActivity;
import com.handybook.handybook.ui.activity.BookingExtrasActivity;
import com.handybook.handybook.ui.activity.BookingLocationActivity;
import com.handybook.handybook.ui.activity.BookingOptionsActivity;
import com.handybook.handybook.ui.activity.BookingPaymentActivity;
import com.handybook.handybook.ui.activity.BookingRecurrenceActivity;
import com.handybook.handybook.ui.activity.BookingRescheduleOptionsActivity;
import com.handybook.handybook.ui.activity.BookingsActivity;
import com.handybook.handybook.ui.activity.LoginActivity;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.ui.activity.OnboardActivity;
import com.handybook.handybook.ui.activity.PeakPricingActivity;
import com.handybook.handybook.ui.activity.ProfileActivity;
import com.handybook.handybook.ui.activity.PromosActivity;
import com.handybook.handybook.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.ui.activity.ServicesActivity;
import com.handybook.handybook.ui.fragment.BookingAddressFragment;
import com.handybook.handybook.ui.fragment.BookingCancelOptionsFragment;
import com.handybook.handybook.ui.fragment.BookingConfirmationFragment;
import com.handybook.handybook.ui.fragment.BookingDateFragment;
import com.handybook.handybook.ui.fragment.BookingDetailFragment;
import com.handybook.handybook.ui.fragment.BookingExtrasFragment;
import com.handybook.handybook.ui.fragment.BookingHeaderFragment;
import com.handybook.handybook.ui.fragment.BookingLocationFragment;
import com.handybook.handybook.ui.fragment.BookingOptionsFragment;
import com.handybook.handybook.ui.fragment.BookingPaymentFragment;
import com.handybook.handybook.ui.fragment.BookingRecurrenceFragment;
import com.handybook.handybook.ui.fragment.BookingRescheduleOptionsFragment;
import com.handybook.handybook.ui.fragment.BookingsFragment;
import com.handybook.handybook.ui.fragment.LoginFragment;
import com.handybook.handybook.ui.fragment.NavigationFragment;
import com.handybook.handybook.ui.fragment.OnboardFragment;
import com.handybook.handybook.ui.fragment.PeakPricingFragment;
import com.handybook.handybook.ui.fragment.PeakPricingTableFragment;
import com.handybook.handybook.ui.fragment.ProfileFragment;
import com.handybook.handybook.ui.fragment.PromosFragment;
import com.handybook.handybook.ui.fragment.ServiceCategoriesFragment;
import com.handybook.handybook.ui.fragment.ServicesFragment;
import com.handybook.handybook.yozio.YozioMetaDataCallback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.otto.Bus;

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
        ServiceCategoriesFragment.class, LoginFragment.class, NavigationFragment.class,
        ProfileFragment.class, BookingsFragment.class, BookingDetailFragment.class,
        ServiceCategoriesFragment.class, ServicesFragment.class, BookingLocationFragment.class,
        BookingOptionsFragment.class, BookingDateFragment.class, BookingAddressFragment.class,
        BookingHeaderFragment.class, BookingPaymentFragment.class, PeakPricingFragment.class,
        PeakPricingTableFragment.class, BookingConfirmationFragment.class, PromosFragment.class,
        BookingExtrasFragment.class, BookingRecurrenceFragment.class, BaseDataManager.class,
        ServiceCategoriesActivity.class, ServicesActivity.class, ProfileActivity.class,
        PeakPricingActivity.class, MenuDrawerActivity.class, LoginActivity.class,
        BookingsActivity.class, BookingRecurrenceActivity.class, BookingPaymentActivity.class,
        BookingOptionsActivity.class, BookingLocationActivity.class, BookingExtrasActivity.class,
        BookingDetailActivity.class, BookingDateActivity.class, BookingConfirmationActivity.class,
        BookingAddressActivity.class, PromosActivity.class, BaseApplication.class,
        BookingRescheduleOptionsActivity.class, BookingRescheduleOptionsFragment.class,
        BookingCancelOptionsActivity.class, BookingCancelOptionsFragment.class,
        YozioMetaDataCallback.class, OnboardActivity.class, OnboardFragment.class
})
public final class ApplicationModule {
    private final Context context;
    private final Properties configs;

    public ApplicationModule(final Context context) {
        this.context = context.getApplicationContext();
        configs = PropertiesReader
                .getProperties(context, "config.properties");
    }

    @Provides @Singleton final HandyRetrofitEndpoint provideHandyEnpoint() {
        return new HandyRetrofitEndpoint(context);
    }

    @Provides @Singleton final HandyRetrofitService provideHandyService(
            final HandyRetrofitEndpoint endpoint, final UserManager userManager) {

        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(10, TimeUnit.SECONDS);

        final String username = configs.getProperty("api_username");
        String password = configs.getProperty("api_password_internal");

        if (BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_PROD))
            password = configs.getProperty("api_password");

        final String pwd = password;

        final RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(endpoint)
                .setRequestInterceptor(new RequestInterceptor() {
                    final String auth = "Basic " + Base64.encodeToString((username + ":" + pwd)
                            .getBytes(), Base64.NO_WRAP);

                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("Authorization", auth);
                        request.addHeader("Accept", "application/json");
                        request.addQueryParam("client", "android");
                        request.addQueryParam("app_version", BuildConfig.VERSION_NAME);
                        request.addQueryParam("api_sub_version", "5.0");
                        request.addQueryParam("app_device_id", getDeviceId());
                        request.addQueryParam("app_device_model", getDeviceName());
                        request.addQueryParam("app_device_os", Build.VERSION.RELEASE);

                        final User user = userManager.getCurrentUser();
                        if (user != null) request.addQueryParam("app_user_id", user.getId());
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
            restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);

        return restAdapter.create(HandyRetrofitService.class);
    }

    @Provides @Singleton final DataManager provideDataManager(final HandyRetrofitService service,
                                                              final HandyRetrofitEndpoint endpoint,
                                                              final Bus bus,
                                                              final SecurePreferences prefs) {
        final BaseDataManager dataManager = new BaseDataManager(service, endpoint, bus, prefs);

        if (BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_PROD))
            dataManager.setEnvironment(DataManager.Environment.P, false);

        return dataManager;
    }

    @Provides final DataManagerErrorHandler provideDataManagerErrorHandler() {
        return new BaseDataManagerErrorHandler();
    }

    @Provides @Singleton final Bus provideBus() {
        return new MainBus();
    }

    @Provides @Singleton final SecurePreferences providePrefs() {
        return new SecurePreferences(context, null,
                configs.getProperty("secure_prefs_key"), true);
    }

    @Provides @Singleton final BookingManager provideBookingManager(final Bus bus,
                                                                    final SecurePreferences prefs) {
        return new BookingManager(bus, prefs);
    }

    @Provides @Singleton final UserManager provideUserManager(final Bus bus,
                                                              final SecurePreferences prefs) {
        return new UserManager(bus, prefs);
    }

    @Provides final ReactiveLocationProvider provideReactiveLocationProvider() {
        return new ReactiveLocationProvider(context);
    }

    @Provides @Singleton final Mixpanel provideMixpanel(final UserManager userManager,
                                                        final BookingManager bookingManager,
                                                        final Bus bus) {
        return new Mixpanel(context, userManager, bookingManager, bus);
    }

    private String getDeviceId() {
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    private String getDeviceName() {
        final String manufacturer = Build.MANUFACTURER;
        final String model = Build.MODEL;

        if (model.startsWith(manufacturer)) return model;
        else return manufacturer + " " + model;
    }
}
