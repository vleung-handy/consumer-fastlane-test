package com.handybook.handybook.core;

import android.app.Application;
import android.content.Context;

import com.facebook.login.LoginManager;
import com.handybook.handybook.data.BaseDataManagerErrorHandler;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.data.DataManagerErrorHandler;
import com.handybook.handybook.data.HandyRetrofitEndpoint;
import com.handybook.handybook.data.HandyRetrofitService;
import com.handybook.handybook.data.Mixpanel;
import com.handybook.handybook.data.SecurePreferences;
import com.handybook.handybook.manager.AppBlockManager;
import com.handybook.handybook.manager.PrefsManager;
import com.handybook.handybook.ui.activity.BaseActivity;
import com.handybook.handybook.ui.activity.BookingDateActivity;
import com.handybook.handybook.ui.activity.BookingEditExtrasActivity;
import com.handybook.handybook.ui.activity.BookingEditFrequencyActivity;
import com.handybook.handybook.ui.activity.BookingEditHoursActivity;
import com.handybook.handybook.ui.activity.BookingLocationActivity;
import com.handybook.handybook.ui.activity.BookingOptionsActivity;
import com.handybook.handybook.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.ui.fragment.BookingDateFragment;
import com.handybook.handybook.ui.fragment.BookingDateFragmentTest;
import com.handybook.handybook.ui.fragment.BookingEditExtrasFragment;
import com.handybook.handybook.ui.fragment.BookingEditFrequencyFragment;
import com.handybook.handybook.ui.fragment.BookingEditHoursFragment;
import com.handybook.handybook.ui.fragment.BookingLocationFragment;
import com.handybook.handybook.ui.fragment.BookingLocationFragmentTest;
import com.handybook.handybook.ui.fragment.BookingOptionsFragment;
import com.handybook.handybook.ui.fragment.BookingOptionsFragmentTest;
import com.handybook.handybook.ui.fragment.NavigationFragment;
import com.handybook.handybook.ui.fragment.ServiceCategoriesFragment;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Module(injects = {
        TestBaseApplication.class,
        BaseActivity.class,
        NavigationFragment.class,
        BookingEditFrequencyActivity.class,
        BookingEditFrequencyFragment.class,
        BookingEditHoursActivity.class,
        BookingEditHoursFragment.class,
        BookingEditExtrasActivity.class,
        BookingEditExtrasFragment.class,
        ServiceCategoriesActivity.class,
        ServiceCategoriesFragment.class,
        BookingLocationActivity.class,
        BookingLocationFragment.class,
        BookingLocationFragmentTest.class,
        BookingOptionsActivity.class,
        BookingOptionsFragment.class,
        BookingOptionsFragmentTest.class,
        BookingDateActivity.class,
        BookingDateFragment.class,
        BookingDateFragmentTest.class,
}, library = true)
public class TestApplicationModule
{
    public final Context context;

    public TestApplicationModule(Context context)
    {
        this.context = context;
    }

    @Provides
    @Singleton
    final DataManagerErrorHandler provideDataManagerHandler()
    {
        return mock(BaseDataManagerErrorHandler.class);
    }

    @Provides
    @Singleton
    final EnvironmentModifier provideEnvironmentModifier()
    {
        EnvironmentModifier environmentModifier = mock(EnvironmentModifier.class);
        when(environmentModifier.getEnvironment()).thenReturn("s");
        return environmentModifier;
    }

    @Provides
    @Singleton
    final HandyRetrofitEndpoint provideHandyEndpoint()
    {
        return mock(HandyRetrofitEndpoint.class);
    }

    @Provides
    @Singleton
    final HandyRetrofitService provideHandyService()
    {
        return mock(HandyRetrofitService.class);
    }

    @Provides
    @Singleton
    final DataManager provideDataManager()
    {
        return mock(DataManager.class);
    }

    @Provides
    @Singleton
    final UserManager provideUserManager()
    {
        return mock(UserManager.class);
    }

    @Provides
    @Singleton
    final Bus provideBus()
    {
        return mock(Bus.class);
    }

    @Provides
    @Singleton
    final Application provideApplication()
    {
        return mock(Application.class);
    }

    @Provides
    @Singleton
    final com.securepreferences.SecurePreferences providePrefs()
    {
        return mock(com.securepreferences.SecurePreferences.class);
    }

    @Provides
    @Singleton
    final BookingManager provideBookingManager()
    {
        return mock(BookingManager.class);
    }

    @Provides
    @Singleton
    final LoginManager provideLoginManager()
    {
        return mock(LoginManager.class);
    }

    @Provides
    @Singleton
    final PrefsManager providePrefsManager()
    {
        return mock(PrefsManager.class);
    }


    @Provides
    @Singleton
    final Mixpanel provideMixpanel()
    {
        return mock(Mixpanel.class);
    }


    @Provides
    @Singleton
    final SecurePreferences provideSecurePreferences()
    {
        return mock(SecurePreferences.class);
    }

    @Provides
    @Singleton
    final AppBlockManager provideAppBlockManager()
    {
        return mock(AppBlockManager.class);
    }

    @Provides
    @Singleton
    final Context provideContext()
    {
        return context;
    }

    @Provides
    @Singleton
    final ReactiveLocationProvider provideReactiveLocationProvider()
    {
        return mock(ReactiveLocationProvider.class);
    }
}
