package com.handybook.handybook.core;

import android.app.Application;
import android.content.Context;

import com.facebook.login.LoginManager;
import com.handybook.handybook.booking.bookingedit.ui.activity.BookingEditExtrasActivity;
import com.handybook.handybook.booking.bookingedit.ui.activity.BookingEditFrequencyActivity;
import com.handybook.handybook.booking.bookingedit.ui.activity.BookingEditHoursActivity;
import com.handybook.handybook.booking.bookingedit.ui.fragment.BookingEditAddressFragment;
import com.handybook.handybook.booking.bookingedit.ui.fragment.BookingEditExtrasFragment;
import com.handybook.handybook.booking.bookingedit.ui.fragment.BookingEditFrequencyFragment;
import com.handybook.handybook.booking.bookingedit.ui.fragment.BookingEditHoursFragment;
import com.handybook.handybook.booking.manager.BookingManager;
import com.handybook.handybook.booking.ui.activity.BookingAddressActivity;
import com.handybook.handybook.booking.ui.activity.BookingDateActivity;
import com.handybook.handybook.booking.ui.activity.BookingExtrasActivity;
import com.handybook.handybook.booking.ui.activity.BookingLocationActivity;
import com.handybook.handybook.booking.ui.activity.BookingOptionsActivity;
import com.handybook.handybook.booking.ui.activity.BookingPaymentActivity;
import com.handybook.handybook.booking.ui.activity.BookingRecurrenceActivity;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.booking.ui.fragment.BookingAddressFragment;
import com.handybook.handybook.booking.ui.fragment.BookingAddressFragmentTest;
import com.handybook.handybook.booking.ui.fragment.BookingDateFragment;
import com.handybook.handybook.booking.ui.fragment.BookingDateFragmentTest;
import com.handybook.handybook.booking.ui.fragment.BookingExtrasFragment;
import com.handybook.handybook.booking.ui.fragment.BookingExtrasFragmentTest;
import com.handybook.handybook.booking.ui.fragment.BookingHeaderFragment;
import com.handybook.handybook.booking.ui.fragment.BookingLocationFragment;
import com.handybook.handybook.booking.ui.fragment.BookingLocationFragmentTest;
import com.handybook.handybook.booking.ui.fragment.BookingOptionsFragment;
import com.handybook.handybook.booking.ui.fragment.BookingOptionsFragmentTest;
import com.handybook.handybook.booking.ui.fragment.BookingPaymentFragment;
import com.handybook.handybook.booking.ui.fragment.BookingPaymentFragmentTest;
import com.handybook.handybook.booking.ui.fragment.BookingRecurrenceFragment;
import com.handybook.handybook.booking.ui.fragment.BookingRecurrenceFragmentTest;
import com.handybook.handybook.booking.ui.fragment.CancelRecurringBookingSelectionFragment;
import com.handybook.handybook.booking.ui.fragment.ServiceCategoriesFragment;
import com.handybook.handybook.booking.ui.fragment.ServiceCategoriesFragmentTest;
import com.handybook.handybook.data.BaseDataManagerErrorHandler;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.data.DataManagerErrorHandler;
import com.handybook.handybook.data.HandyRetrofitEndpoint;
import com.handybook.handybook.data.HandyRetrofitService;
import com.handybook.handybook.data.SecurePreferences;
import com.handybook.handybook.logger.mixpanel.Mixpanel;
import com.handybook.handybook.manager.AppBlockManager;
import com.handybook.handybook.manager.PrefsManager;
import com.handybook.handybook.manager.StripeManager;
import com.handybook.handybook.module.push.manager.UrbanAirshipManager;
import com.handybook.handybook.module.referral.ui.RedemptionActivity;
import com.handybook.handybook.module.referral.ui.RedemptionEmailSignUpFragment;
import com.handybook.handybook.module.referral.ui.RedemptionFragment;
import com.handybook.handybook.module.referral.ui.RedemptionSignUpFragment;
import com.handybook.handybook.module.referral.ui.ReferralFragment;
import com.handybook.handybook.module.referral.ui.ReferralFragmentTest;
import com.handybook.handybook.ui.activity.BaseActivity;
import com.handybook.handybook.ui.activity.UpdatePaymentActivity;
import com.handybook.handybook.ui.fragment.UpdatePaymentFragment;
import com.handybook.handybook.ui.fragment.UpdatePaymentFragmentTest;
import com.squareup.otto.Bus;

import java.util.Properties;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Module(injects = {
        TestBaseApplication.class,
        BaseActivity.class,
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
        BookingRecurrenceActivity.class,
        BookingRecurrenceFragment.class,
        BookingRecurrenceFragmentTest.class,
        BookingHeaderFragment.class,
        BookingExtrasActivity.class,
        BookingExtrasFragment.class,
        BookingExtrasFragmentTest.class,
        BookingAddressActivity.class,
        BookingAddressFragment.class,
        BookingAddressFragmentTest.class,
        BookingPaymentActivity.class,
        BookingPaymentFragment.class,
        BookingPaymentFragmentTest.class,
        CancelRecurringBookingSelectionFragment.class,
        BookingEditAddressFragment.class,
        UpdatePaymentActivity.class,
        UpdatePaymentFragment.class,
        UpdatePaymentFragmentTest.class,
        ServiceCategoriesFragmentTest.class,
        ReferralFragment.class,
        ReferralFragmentTest.class,
        RedemptionActivity.class,
        RedemptionFragment.class,
        RedemptionSignUpFragment.class,
        RedemptionEmailSignUpFragment.class,
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
    final Properties providerProperties()
    {
        return mock(Properties.class);
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
    final StripeManager provideStripeManager()
    {
        return mock(StripeManager.class);
    }

    @Provides
    @Singleton
    final UrbanAirshipManager provideUrbanAirshipManager()
    {
        return mock(UrbanAirshipManager.class);
    }
}
