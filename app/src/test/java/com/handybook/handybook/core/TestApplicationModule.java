package com.handybook.handybook.core;

import android.app.Application;
import android.content.Context;

import com.facebook.login.LoginManager;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.data.HandyRetrofitEndpoint;
import com.handybook.handybook.data.HandyRetrofitService;
import com.handybook.handybook.data.Mixpanel;
import com.handybook.handybook.data.SecurePreferences;
import com.handybook.handybook.manager.AppBlockManager;
import com.handybook.handybook.manager.PrefsManager;
import com.squareup.otto.Bus;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by jwilliams on 2/17/15.
 */
@Module(injects = {
        TestBaseApplication.class,


}, library = true)
public class TestApplicationModule
{

    public final Context context;
    public TestApplicationModule(Context context)
    {
        this.context = context;
    }

    @Provides
    final EnvironmentModifier provideEnvironmentModifier()
    {
        EnvironmentModifier environmentModifier = mock(EnvironmentModifier.class);
        when(environmentModifier.getEnvironment()).thenReturn("s");
        return environmentModifier;
    }

    @Provides
    final HandyRetrofitEndpoint provideHandyEndpoint()
    {
        return mock(HandyRetrofitEndpoint.class);
    }

    @Provides
    final HandyRetrofitService provideHandyService()
    {
        return mock(HandyRetrofitService.class);
    }

    @Provides
    final DataManager provideDataManager()
    {
        return mock(DataManager.class);
    }

    @Provides
    final Bus provideBus()
    {
        return mock(Bus.class);
    }

    @Provides
    final Application provideApplication()
    {
        return mock(Application.class);
    }

    @Provides
    final com.securepreferences.SecurePreferences providePrefs()
    {
        return mock(com.securepreferences.SecurePreferences.class);
    }

    @Provides
    final BookingManager provideBookingManager()
    {
        return mock(BookingManager.class);
    }

    @Provides
    final LoginManager provideLoginManager()
    {
        return mock(LoginManager.class);
    }

    @Provides
    final PrefsManager providePrefsManager()
    {
        return mock(PrefsManager.class);
    }


    @Provides
    final Mixpanel provideMixpanel()
    {
        return mock(Mixpanel.class);
    }


    @Provides
    final SecurePreferences provideSecurePreferences()
    {
        return mock(SecurePreferences.class);
    }

    @Provides
    final AppBlockManager provideAppBlockManager()
    {
        return mock(AppBlockManager.class);
    }

    @Provides
    final Context provideContext()
    {
        return context;
    }
}
