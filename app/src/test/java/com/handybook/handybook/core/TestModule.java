package com.handybook.handybook.core;

import android.content.Context;

import com.handybook.handybook.data.BaseDataManagerErrorHandler;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.data.DataManagerErrorHandler;
import com.handybook.handybook.data.HandyEndpoint;
import com.handybook.handybook.data.HandyRetrofitService;
import com.handybook.handybook.data.Mixpanel;
import com.handybook.handybook.data.MockDataManager;
import com.handybook.handybook.data.MockHandyRetrofitService;
import com.handybook.handybook.data.MockSecurePreferences;
import com.handybook.handybook.data.PropertiesReader;
import com.handybook.handybook.data.SecurePreferences;
import com.handybook.handybook.ui.activity.BookingsActivity;
import com.handybook.handybook.ui.activity.BookingsActivityTest;
import com.handybook.handybook.ui.fragment.BookingsFragment;
import com.handybook.handybook.ui.fragment.NavigationFragment;
import com.squareup.otto.Bus;

import java.util.Properties;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by jwilliams on 2/17/15.
 */
@Module (

        injects = {
                TestBaseApplication.class, MockDataManager.class, BookingsFragment.class,
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

    @Provides @Singleton final HandyEndpoint provideMockHandyEnpoint() {
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
                return null;
            }

            @Override
            public String getName() {
                return null;
            }
        };
    }

    @Provides @Singleton final HandyRetrofitService provideMockHandyRetrofitService() {
        return new MockHandyRetrofitService();
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
                                                              final Bus bus) {
        final MockDataManager dataManager = new MockDataManager(service, endpoint, bus);
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
