package com.handybook.handybook.core;

import android.content.Context;

import com.handybook.handybook.data.BaseDataManager;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.data.HandyEndpoint;
import com.handybook.handybook.data.HandyRetrofitService;
import com.handybook.handybook.data.MockDataManager;
import com.handybook.handybook.data.MockHandyRetrofitService;
import com.handybook.handybook.data.SecurePreferences;
import com.handybook.handybook.ui.fragment.BookingsFragment;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by jwilliams on 2/17/15.
 */
@Module (

        injects = { MockDataManager.class, BookingsFragment.class

        })

public class TestModule {

    private final Context context;

    public TestModule(final Context context) {
        this.context = context.getApplicationContext();
    }

    @Provides @Singleton final HandyEndpoint provideHandyEnpoint() {
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
        return null;
    }

    @Provides @Singleton final SecurePreferences provideMockSecurePreferences() {
        return null;
    }

    @Provides @Singleton final DataManager provideDataManager(final HandyRetrofitService service,
                                                              final HandyEndpoint endpoint,
                                                              final Bus bus,
                                                              final SecurePreferences prefs) {
        final BaseDataManager dataManager = new BaseDataManager(service, endpoint, bus, prefs);
        return dataManager;
    }
}
