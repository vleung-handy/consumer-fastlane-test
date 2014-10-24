package com.handybook.handybook;

import android.app.Application;
import android.content.Context;
import android.util.Base64;

import com.squareup.otto.Bus;

import java.util.Properties;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

@Module(injects = {
        ServiceCategoriesFragment.class,
        LoginFragment.class,
        NavigationFragment.class,
        ProfileFragment.class,
        BookingsFragment.class,
        BookingDetailFragment.class,
        BaseDataManager.class
})
final class ApplicationModule {
    private final Application application;

    ApplicationModule(final Application application) {
        this.application = application;
    }

    @Provides @Singleton final Context provideApplicationContext() {
        return application.getApplicationContext();
    }

    @Provides @Singleton final HandyRetrofitEndpoint provideHandyEnpoint() {
        return new HandyRetrofitEndpoint(application.getApplicationContext());
    }

    @Provides @Singleton final HandyRetrofitService provideHandyService(final HandyRetrofitEndpoint endpoint) {
        final Properties configs = PropertiesReader
                .getProperties(application.getApplicationContext(), "config.properties");
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
                    }
                }).build();

        if (!BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_PROD))
            restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);

        return restAdapter.create(HandyRetrofitService.class);
    }

    @Provides @Singleton final DataManager provideDataManager(final HandyRetrofitService service,
                                                              final HandyRetrofitEndpoint endpoint,
                                                              final Bus bus) {
        final BaseDataManager dataManager = new BaseDataManager(service, endpoint, bus);
        if (BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_PROD)) {
            dataManager.setEnvironment(DataManager.Environment.P);
        }
        return dataManager;
    }

    @Provides final DataManagerErrorHandler provideDataManagerErrorHandler() {
        return new BaseDataManagerErrorHandler();
    }

    @Provides @Singleton final Bus provideBus() {
        return new Bus();
    }
}
