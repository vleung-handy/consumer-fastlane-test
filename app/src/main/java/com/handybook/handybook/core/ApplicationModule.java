package com.handybook.handybook.core;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.handybook.handybook.BuildConfig;
import com.handybook.handybook.R;
import com.handybook.handybook.autocomplete.AddressAutoCompleteManager;
import com.handybook.handybook.autocomplete.PlacesService;
import com.handybook.handybook.booking.bookingedit.manager.BookingEditManager;
import com.handybook.handybook.booking.manager.BookingManager;
import com.handybook.handybook.booking.model.BookingPostInfo;
import com.handybook.handybook.booking.model.BookingQuote;
import com.handybook.handybook.booking.model.BookingRequest;
import com.handybook.handybook.booking.model.BookingTransaction;
import com.handybook.handybook.configuration.manager.ConfigurationManager;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.DataManagerErrorHandler;
import com.handybook.handybook.core.data.HandyRetrofit2Service;
import com.handybook.handybook.core.data.HandyRetrofitEndpoint;
import com.handybook.handybook.core.data.HandyRetrofitService;
import com.handybook.handybook.core.data.SecurePreferences;
import com.handybook.handybook.core.manager.AppBlockManager;
import com.handybook.handybook.core.manager.AppseeManager;
import com.handybook.handybook.core.manager.DefaultPreferencesManager;
import com.handybook.handybook.core.manager.FileManager;
import com.handybook.handybook.core.manager.ReviewAppManager;
import com.handybook.handybook.core.manager.SecurePreferencesManager;
import com.handybook.handybook.core.manager.ServicesManager;
import com.handybook.handybook.core.manager.SessionManager;
import com.handybook.handybook.core.manager.StripeManager;
import com.handybook.handybook.core.manager.UserDataManager;
import com.handybook.handybook.deeplink.DeepLinkIntentProvider;
import com.handybook.handybook.helpcenter.HelpModule;
import com.handybook.handybook.library.util.PropertiesReader;
import com.handybook.handybook.logger.handylogger.EventLogManager;
import com.handybook.handybook.notifications.NotificationsModule;
import com.handybook.handybook.onboarding.OnboardingModule;
import com.handybook.handybook.promos.PromosModule;
import com.handybook.handybook.proprofiles.ProProfileModule;
import com.handybook.handybook.proteam.manager.ProTeamManager;
import com.handybook.handybook.push.manager.UrbanAirshipManager;
import com.handybook.handybook.ratingflow.RatingFlowModule;
import com.handybook.handybook.referral.ReferralModule;
import com.handybook.handybook.vegas.VegasManager;
import com.handybook.handybook.vegas.VegasModule;
import com.handybook.shared.core.HandyLibrary;
import com.handybook.shared.layer.LayerHelper;
import com.squareup.okhttp.CertificatePinner;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.otto.Bus;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(
        injects = {
                BaseApplication.class,
                // Do not add new class here. Put it into InjectionModule instead.
        },
        includes = {
                InjectionModule.class,
                HelpModule.class,
                NotificationsModule.class,
                OnboardingModule.class,
                ReferralModule.class,
                PromosModule.class,
                RatingFlowModule.class,
                VegasModule.class,
                ProProfileModule.class,
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
                                                new BookingRequest.BookingRequestApiSerializer()
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
    final HandyRetrofit2Service provideRetrofit2Service(
            final HandyRetrofitEndpoint endpoint,
            final UserManager userManager
    ) {

        final String username = mConfigs.getProperty("api_username");
        String password = mConfigs.getProperty("api_password_internal");
        if (BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_PROD)) {
            password = mConfigs.getProperty("api_password");
        }
        final String auth = "Basic " + Base64.encodeToString(
                (username + ":" + password).getBytes(),
                Base64.NO_WRAP
        );
        final User user = userManager.getCurrentUser();

        HttpLoggingInterceptor httpInterceptor = new HttpLoggingInterceptor();
        httpInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        okhttp3.OkHttpClient.Builder httpClientBuilder = new okhttp3.OkHttpClient
                .Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {

                        Request original = chain.request();
                        HttpUrl originalHttpUrl = original.url();

                        okhttp3.HttpUrl.Builder urlBuilder = originalHttpUrl
                                .newBuilder()
                                .addQueryParameter("client", "android")
                                .addQueryParameter("app_version", BuildConfig.VERSION_NAME)
                                .addQueryParameter("api_sub_version", "6.0")
                                .addQueryParameter("app_device_id", getDeviceId())
                                .addQueryParameter("app_device_model", getDeviceName())
                                .addQueryParameter("app_device_os", Build.VERSION.RELEASE)
                                .addQueryParameter(
                                        "app_version_code",
                                        String.valueOf(BuildConfig.VERSION_CODE)
                                );
                        if (user != null) {
                            urlBuilder.addQueryParameter("app_user_id", user.getId());
                        }
                        // Request customization: add request headers
                        Request.Builder requestBuilder = original
                                .newBuilder()
                                .addHeader("Authorization", auth)
                                .addHeader("Accept", "application/json")
                                .url(urlBuilder.build());
                        if (user != null && user.getAuthToken() != null) {
                            requestBuilder.addHeader("X-Auth-Token", user.getAuthToken());
                        }

                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    }
                })
                .addInterceptor(httpInterceptor);

        if (BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_PROD) && !BuildConfig.DEBUG) {
            httpClientBuilder.certificatePinner(
                    new okhttp3.CertificatePinner.Builder().add(
                            mConfigs.getProperty("hostname"),
                            "sha1/tbHJQrYmt+5isj5s44sk794iYFc=",
                            "sha1/SXxoaOSEzPC6BgGmxAt/EAcsajw=",
                            "sha1/blhOM3W9V/bVQhsWAcLYwPU6n24=",
                            "sha1/T5x9IXmcrQ7YuQxXnxoCmeeQ84c="
                    ).build());
        }

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .setExclusionStrategies(
                        BookingRequest.getExclusionStrategy()
                )
                .registerTypeAdapter(
                        BookingRequest.class,
                        new BookingRequest.BookingRequestApiSerializer()
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
                .create();

        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(endpoint.getUrl())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClientBuilder.build())
                .build();

        return retrofit.create(HandyRetrofit2Service.class);
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
            final HandyRetrofit2Service service2,
            final HandyRetrofitEndpoint endpoint
    ) {
        return new DataManager(service, service2, endpoint);
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
    @Singleton
    final ReviewAppManager provideReviewAppManager(
            final ConfigurationManager configurationManager,
            final DefaultPreferencesManager defaultPreferencesManager
    ) {
        return new ReviewAppManager(mContext, configurationManager, defaultPreferencesManager);
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
    @NonNull
    final VegasManager provideVegasManager(final HandyRetrofitService service) {
        return new VegasManager(service);
    }

    @Provides
    @Singleton
    final ProTeamManager provideProTeamManager(
            final Bus bus,
            final HandyRetrofitService service,
            final DataManager dataManager,
            final UserManager userDataManager
    ) {
        return new ProTeamManager(bus, service, dataManager, userDataManager);
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
