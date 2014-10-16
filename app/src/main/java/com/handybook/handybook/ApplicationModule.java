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
        BaseDataManager.class
})
final class ApplicationModule {
    private final Application application;

    ApplicationModule(final Application application) {
        this.application = application;
    }

    @Provides @Singleton final Context provideApplicationContext() {
        Properties config = PropertiesReader.getProperties(application.getApplicationContext(), "config.properties");
        config.getProperty("api_username");
        return application.getApplicationContext();
    }

//    @Provides @Singleton final HandyEndpoint provideHandyEnpoint() {
//        return new HandyEndpoint(application.getApplicationContext());
//    }

    @Provides @Singleton final HandyRetrofitService provideHandyService(final HandyRetrofitEndpoint endpoint) {
        final RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(endpoint)
                .setRequestInterceptor(new RequestInterceptor() {
                    Properties configs = PropertiesReader
                            .getProperties(application.getApplicationContext(), "config.properties");

                    final String auth = "Basic " + Base64.encodeToString((configs.getProperty("api_username")
                            + ":" + configs.getProperty("api_password")).getBytes(), Base64.NO_WRAP);

                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("Authorization", auth);
                        request.addHeader("Accept", "application/json");
                    }
                }).build();
        return restAdapter.create(HandyRetrofitService.class);
    }

    @Provides @Singleton final DataManager provideDataManager(final HandyRetrofitService service,
                                                              final HandyRetrofitEndpoint endpoint) {
        final BaseDataManager dataManager = new BaseDataManager(service, endpoint);
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
