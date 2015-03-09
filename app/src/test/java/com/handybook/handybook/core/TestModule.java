package com.handybook.handybook.core;

import android.content.Context;

import com.handybook.handybook.data.BaseDataManager;
import com.handybook.handybook.data.BaseDataManagerErrorHandler;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.data.DataManagerErrorHandler;
import com.handybook.handybook.data.HandyEndpoint;
import com.handybook.handybook.data.HandyRetrofitService;
import com.handybook.handybook.data.Mixpanel;
import com.handybook.handybook.data.MockSecurePreferences;
import com.handybook.handybook.data.PropertiesReader;
import com.handybook.handybook.data.SecurePreferences;
import com.handybook.handybook.ui.activity.BookingsActivity;
import com.handybook.handybook.ui.activity.BookingsActivityTest;
import com.handybook.handybook.ui.fragment.BookingsFragment;
import com.handybook.handybook.ui.fragment.NavigationFragment;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.otto.Bus;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Executor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;

/**
 * Created by jwilliams on 2/17/15.
 */
@Module (

        injects = {
                TestBaseApplication.class, BaseDataManager.class, BookingsFragment.class,
                BookingsActivityTest.class, BookingsActivity.class, NavigationFragment.class
        })

public class TestModule {

    private final Context context;
    private final Properties configs;

    public TestModule(final Context context) {
        this.context = context.getApplicationContext();
        configs = PropertiesReader
                .getProperties(context, "config.properties");
    }

    @Provides @Singleton final HandyEndpoint provideMockHandyEnpoint(final MockWebServer server) {
        return new HandyEndpoint() {
            @Override
            public Environment getEnv() {
                return null;
            }

            @Override
            public void setEnv(Environment env) {
            }

            @Override
            public String getUrl() {
                return server.getUrl("/").toString();
            }

            @Override
            public String getName() {
                return "Test";
            }
        };
    }

    @Provides @Singleton final MockWebServer provideMockWebServer() {
        MockWebServer server = new MockWebServer();
        try {
            server.play();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return server;
    }

    @Provides @Singleton final HandyRetrofitService provideMockHandyRetrofitService(HandyEndpoint endpoint) {
        return new RestAdapter.Builder().setEndpoint(endpoint)
                // Override executors so all server calls our mocked synchronously
                .setExecutors(new Executor() {
                    @Override
                    public void execute(Runnable command) {
                        command.run();
                    }
                }, new Executor() {
                    @Override
                    public void execute(Runnable command) {
                        command.run();
                    }
                }).build().create(HandyRetrofitService.class);
    }

    @Provides @Singleton final Bus provideMockBus() {
        return new MainBus();
    }

    @Provides @Singleton final SecurePreferences providePrefs() {
        return new MockSecurePreferences(context, null,
                configs.getProperty("secure_prefs_key"), true);
    }

    @Provides @Singleton final Mixpanel provideMockMixpanel(final UserManager userManager,
                                                        final BookingManager bookingManager,
                                                        final Bus bus) {
        return new Mixpanel(context, userManager, bookingManager, bus);
    }

    @Provides @Singleton final DataManager provideMockDataManager(final HandyRetrofitService service,
                                                              final HandyEndpoint endpoint,
                                                              final Bus bus,
                                                              final SecurePreferences prefs) {
        final BaseDataManager dataManager = new BaseDataManager(service, endpoint, bus, prefs);
        return dataManager;
    }

    @Provides @Singleton final UserManager provideMockUserManager(final Bus bus,
                                                              final SecurePreferences prefs) {
        return new MockUserManager(bus, prefs);
    }

    @Provides final DataManagerErrorHandler provideMockDataManagerErrorHandler() {
        return new BaseDataManagerErrorHandler();
    }
}
