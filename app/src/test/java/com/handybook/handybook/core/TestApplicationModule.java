package com.handybook.handybook.core;

import android.app.Application;
import android.content.Context;

import com.facebook.login.LoginManager;
import com.handybook.handybook.account.ui.AccountFragment;
import com.handybook.handybook.account.ui.AccountFragmentTest;
import com.handybook.handybook.account.ui.ContactFragment;
import com.handybook.handybook.account.ui.ContactFragmentTest;
import com.handybook.handybook.account.ui.EditPlanAddressFragment;
import com.handybook.handybook.account.ui.EditPlanFragment;
import com.handybook.handybook.account.ui.EditPlanFrequencyFragment;
import com.handybook.handybook.account.ui.PlansFragment;
import com.handybook.handybook.account.ui.ProfileActivity;
import com.handybook.handybook.account.ui.ProfilePasswordFragment;
import com.handybook.handybook.account.ui.UpdatePaymentFragment;
import com.handybook.handybook.account.ui.UpdatePaymentFragmentTest;
import com.handybook.handybook.autocomplete.AddressAutoCompleteManager;
import com.handybook.handybook.autocomplete.AutoCompleteAddressFragment;
import com.handybook.handybook.autocomplete.AutoCompleteAddressFragmentTest;
import com.handybook.handybook.autocomplete.PlacesService;
import com.handybook.handybook.booking.bookingedit.ui.activity.BookingEditExtrasActivity;
import com.handybook.handybook.booking.bookingedit.ui.activity.BookingEditHoursActivity;
import com.handybook.handybook.booking.bookingedit.ui.fragment.BookingEditAddressFragment;
import com.handybook.handybook.booking.bookingedit.ui.fragment.BookingEditExtrasFragment;
import com.handybook.handybook.booking.bookingedit.ui.fragment.BookingEditHoursFragment;
import com.handybook.handybook.booking.manager.BookingManager;
import com.handybook.handybook.booking.ui.activity.BookingAddressActivity;
import com.handybook.handybook.booking.ui.activity.BookingDateActivity;
import com.handybook.handybook.booking.ui.activity.BookingExtrasActivity;
import com.handybook.handybook.booking.ui.activity.BookingLocationActivity;
import com.handybook.handybook.booking.ui.activity.BookingOptionsActivity;
import com.handybook.handybook.booking.ui.activity.BookingPaymentActivity;
import com.handybook.handybook.booking.ui.activity.BookingRecurrenceActivity;
import com.handybook.handybook.booking.ui.activity.BookingsActivity;
import com.handybook.handybook.booking.ui.activity.ReportIssueActivity;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.booking.ui.fragment.ActiveBookingFragment;
import com.handybook.handybook.booking.ui.fragment.BookingAddressFragment;
import com.handybook.handybook.booking.ui.fragment.BookingAddressFragmentTest;
import com.handybook.handybook.booking.ui.fragment.BookingDateFragment;
import com.handybook.handybook.booking.ui.fragment.BookingDateFragmentTest;
import com.handybook.handybook.booking.ui.fragment.BookingDateTimeInputFragment;
import com.handybook.handybook.booking.ui.fragment.BookingDetailFragment;
import com.handybook.handybook.booking.ui.fragment.BookingDetailFragmentTest;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentAddress;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentBookingActions;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentEntryInformation;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentExtras;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentLaundry;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentPayment;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentPreferences;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentProInformation;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentProInformationTest;
import com.handybook.handybook.booking.ui.fragment.BookingExtrasFragment;
import com.handybook.handybook.booking.ui.fragment.BookingExtrasFragmentTest;
import com.handybook.handybook.booking.ui.fragment.BookingFlowFragment;
import com.handybook.handybook.booking.ui.fragment.BookingHeaderFragment;
import com.handybook.handybook.booking.ui.fragment.BookingHeaderFragmentTest;
import com.handybook.handybook.booking.ui.fragment.BookingLocationFragment;
import com.handybook.handybook.booking.ui.fragment.BookingLocationFragmentTest;
import com.handybook.handybook.booking.ui.fragment.BookingOptionsFragment;
import com.handybook.handybook.booking.ui.fragment.BookingOptionsFragmentTest;
import com.handybook.handybook.booking.ui.fragment.BookingPaymentFragment;
import com.handybook.handybook.booking.ui.fragment.BookingPaymentFragmentTest;
import com.handybook.handybook.booking.ui.fragment.BookingRecurrenceFragment;
import com.handybook.handybook.booking.ui.fragment.BookingRecurrenceFragmentTest;
import com.handybook.handybook.booking.ui.fragment.BookingSubscriptionFragment;
import com.handybook.handybook.booking.ui.fragment.BookingSubscriptionFragmentTest;
import com.handybook.handybook.booking.ui.fragment.CancelRecurringBookingSelectionFragment;
import com.handybook.handybook.booking.ui.fragment.OnboardingTest;
import com.handybook.handybook.booking.ui.fragment.PromosFragment;
import com.handybook.handybook.booking.ui.fragment.ReportIssueFragment;
import com.handybook.handybook.booking.ui.fragment.ServiceCategoriesHomeFragment;
import com.handybook.handybook.booking.ui.fragment.TipDialogFragment;
import com.handybook.handybook.booking.ui.fragment.UpcomingBookingsFragment;
import com.handybook.handybook.booking.ui.fragment.dialog.BookingTimeInputDialogFragment;
import com.handybook.handybook.bottomnav.BottomNavActivity;
import com.handybook.handybook.configuration.manager.ConfigurationManager;
import com.handybook.handybook.configuration.model.Configuration;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.DataManagerErrorHandler;
import com.handybook.handybook.core.data.HandyRetrofit2Service;
import com.handybook.handybook.core.data.HandyRetrofitEndpoint;
import com.handybook.handybook.core.data.HandyRetrofitService;
import com.handybook.handybook.core.manager.AppBlockManager;
import com.handybook.handybook.core.manager.AppseeManager;
import com.handybook.handybook.core.manager.DefaultPreferencesManager;
import com.handybook.handybook.core.manager.DefaultPreferencesManagerTest;
import com.handybook.handybook.core.manager.FileManager;
import com.handybook.handybook.core.manager.ReviewAppManager;
import com.handybook.handybook.core.manager.SecurePreferencesManager;
import com.handybook.handybook.core.manager.StripeManager;
import com.handybook.handybook.core.ui.activity.BaseActivity;
import com.handybook.handybook.core.ui.activity.SplashActivity;
import com.handybook.handybook.core.ui.activity.SplashActivityTest;
import com.handybook.handybook.core.ui.activity.UpdatePaymentActivity;
import com.handybook.handybook.core.ui.fragment.HelpCenterTest;
import com.handybook.handybook.core.ui.fragment.ReviewAppBannerFragment;
import com.handybook.handybook.helpcenter.ui.activity.HelpActivity;
import com.handybook.handybook.helpcenter.ui.fragment.HelpFragment;
import com.handybook.handybook.helpcenter.ui.fragment.HelpWebViewFragment;
import com.handybook.handybook.logger.handylogger.EventLogManager;
import com.handybook.handybook.logger.handylogger.EventLogManagerTest;
import com.handybook.handybook.onboarding.OnboardV2Fragment;
import com.handybook.handybook.proteam.ui.fragment.ProTeamConversationsFragment;
import com.handybook.handybook.push.manager.UrbanAirshipManager;
import com.handybook.handybook.referral.ui.BaseReferralFragment;
import com.handybook.handybook.referral.ui.ProReferralFragment;
import com.handybook.handybook.referral.ui.ProReferralFragmentTest;
import com.handybook.handybook.referral.ui.RedemptionActivity;
import com.handybook.handybook.referral.ui.RedemptionEmailSignUpFragment;
import com.handybook.handybook.referral.ui.RedemptionFragment;
import com.handybook.handybook.referral.ui.RedemptionSignUpFragment;
import com.handybook.handybook.referral.ui.ReferralFragment;
import com.handybook.handybook.referral.ui.ReferralFragmentTest;
import com.handybook.handybook.referral.ui.ReferralV2Fragment;
import com.handybook.handybook.referral.ui.ReferralV2FragmentTest;
import com.handybook.shared.layer.LayerHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.Properties;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@Module(injects = {
        TestBaseApplication.class,
        ActiveBookingFragment.class,
        AutoCompleteAddressFragment.class,
        AutoCompleteAddressFragmentTest.class,
        BaseActivity.class,
        BookingEditHoursActivity.class,
        BookingEditHoursFragment.class,
        BookingEditExtrasActivity.class,
        BookingEditExtrasFragment.class,
        ServiceCategoriesActivity.class,
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
        BookingSubscriptionFragment.class,
        BookingSubscriptionFragmentTest.class,
        BookingsActivity.class,
        CancelRecurringBookingSelectionFragment.class,
        BookingEditAddressFragment.class,
        UpdatePaymentActivity.class,
        UpdatePaymentFragment.class,
        UpdatePaymentFragmentTest.class,
        ReferralFragment.class,
        ReferralV2Fragment.class,
        ProReferralFragment.class,
        BaseReferralFragment.class,
        ReferralFragmentTest.class,
        ReferralV2FragmentTest.class,
        ProReferralFragmentTest.class,
        RedemptionActivity.class,
        RedemptionFragment.class,
        RedemptionSignUpFragment.class,
        RedemptionEmailSignUpFragment.class,
        ReportIssueActivity.class,
        ReportIssueFragment.class,
        UpcomingBookingsFragment.class,
        HelpActivity.class,
        HelpFragment.class,
        HelpWebViewFragment.class,
        HelpCenterTest.class,
        BookingDetailFragment.class,
        BookingDetailFragmentTest.class,
        BookingDetailSectionFragmentProInformation.class,
        BookingDetailSectionFragmentLaundry.class,
        BookingDetailSectionFragmentEntryInformation.class,
        BookingDetailSectionFragmentPreferences.class,
        BookingDetailSectionFragmentExtras.class,
        BookingDetailSectionFragmentAddress.class,
        BookingDetailSectionFragmentBookingActions.class,
        BookingDetailSectionFragmentPayment.class,
        BookingDetailSectionFragmentProInformationTest.class,
        TipDialogFragment.class,
        ContactFragment.class,
        ProfileActivity.class,
        AccountFragment.class,
        AccountFragmentTest.class,
        ProfilePasswordFragment.class,
        PromosFragment.class,
        PlansFragment.class,
        ContactFragmentTest.class,
        EditPlanFragment.class,
        EditPlanFrequencyFragment.class,
        EditPlanAddressFragment.class,
        EventLogManagerTest.class,
        TestActivity.class,
        OnboardingTest.class,
        OnboardV2Fragment.class,
        DefaultPreferencesManagerTest.class,
        SplashActivity.class,
        SplashActivityTest.class,
        ProTeamConversationsFragment.class,
        BookingHeaderFragmentTest.class,
        BookingDateTimeInputFragment.class,
        BookingTimeInputDialogFragment.class,
        ReviewAppBannerFragment.class,
        BottomNavActivity.class,
        BookingFlowFragment.class,
        ServiceCategoriesHomeFragment.class,

}, library = true)
public class TestApplicationModule {

    public final Context context;

    public TestApplicationModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    final Properties providerProperties() {
        return mock(Properties.class);
    }

    @Provides
    @Singleton
    final ReviewAppManager provideReviewAppManager() {
        return mock(ReviewAppManager.class);
    }

    @Provides
    @Singleton
    final AppseeManager provideAppseeManager() {
        return mock(AppseeManager.class);
    }

    @Provides
    @Singleton
    final DataManagerErrorHandler provideDataManagerHandler() {
        return mock(DataManagerErrorHandler.class);
    }

    @Provides
    @Singleton
    final EnvironmentModifier provideEnvironmentModifier() {
        EnvironmentModifier environmentModifier = mock(EnvironmentModifier.class);
        when(environmentModifier.getEnvironmentPrefix()).thenReturn("s");
        return environmentModifier;
    }

    @Provides
    @Singleton
    final HandyRetrofitEndpoint provideHandyEndpoint() {
        return mock(HandyRetrofitEndpoint.class);
    }

    @Provides
    @Singleton
    RestAdapter providesRestAdapter(
            final HandyRetrofitEndpoint endpoint,
            final UserManager userManager
    ) {
        return mock(RestAdapter.class);
    }

    @Provides
    @Singleton
    final HandyRetrofitService provideHandyService() {
        return mock(HandyRetrofitService.class);
    }

    @Provides
    @Singleton
    final HandyRetrofit2Service provideHandyService2() {
        return mock(HandyRetrofit2Service.class);
    }

    @Provides
    @Singleton
    final PlacesService providesPlacesService() {
        return mock(PlacesService.class);
    }

    @Provides
    @Singleton
    final AddressAutoCompleteManager providesAddressAutoCompleteManager() {
        return mock(AddressAutoCompleteManager.class);
    }

    @Provides
    @Singleton
    final DataManager provideDataManager(
            final HandyRetrofitService service,
            final HandyRetrofit2Service service2,
            final HandyRetrofitEndpoint endpoint
    ) {
        return spy(new TestDataManager(service, service2, endpoint));
    }

    @Provides
    @Singleton
    final EventLogManager provideLogEventsManager(
            final EventBus bus,
            final DataManager dataManager,
            final FileManager fileManager,
            final DefaultPreferencesManager defaultPreferencesManager,
            final UserManager userManager
    ) {
        return spy(new EventLogManager(
                context,
                bus,
                dataManager,
                fileManager,
                defaultPreferencesManager,
                userManager
        ));
    }

    @Provides
    @Singleton
    final UserManager provideUserManager(
            final EventBus bus,
            final SecurePreferencesManager securePreferencesManager,
            final DefaultPreferencesManager defaultPreferencesManager
    ) {
        return spy(new TestUserManager(
                context,
                bus,
                securePreferencesManager,
                defaultPreferencesManager
        ));
    }

    @Provides
    @Singleton
    final EventBus provideBus() {
        return mock(MainBus.class);
    }

    @Provides
    @Singleton
    final Application provideApplication() {
        return mock(Application.class);
    }

    @Provides
    @Singleton
    final BookingManager provideBookingManager() {
        return mock(BookingManager.class);
    }

    @Provides
    @Singleton
    final FileManager provideFileManager() {
        return new FileManager(context);
    }

    @Provides
    @Singleton
    final LoginManager provideLoginManager() {
        return mock(LoginManager.class);
    }

    @Provides
    @Singleton
    final SecurePreferencesManager provideSecurePreferencesManager() {
        return mock(SecurePreferencesManager.class);
    }

    @Provides
    @Singleton
    final DefaultPreferencesManager provideDefaultPreferencesManager() {
        return new DefaultPreferencesManager(context);
    }

    @Provides
    @Singleton
    final AppBlockManager provideAppBlockManager() {
        return mock(AppBlockManager.class);
    }

    @Provides
    @Singleton
    final Context provideContext() {
        return context;
    }

    @Provides
    @Singleton
    final StripeManager provideStripeManager() {
        return mock(StripeManager.class);
    }

    @Provides
    @Singleton
    final LayerHelper provideLayerHelper() {
        return mock(LayerHelper.class, RETURNS_DEEP_STUBS);
    }

    @Provides
    @Singleton
    final UrbanAirshipManager provideUrbanAirshipManager() {
        return mock(UrbanAirshipManager.class);
    }

    @Provides
    @Singleton
    final ConfigurationManager provideConfigurationManager(
            final EventBus bus,
            final DefaultPreferencesManager defaultPreferencesManager,
            final DataManager dataManager
    ) {
        TestConfigurationManager configManager = spy(new TestConfigurationManager(
                bus,
                defaultPreferencesManager,
                dataManager
        ));

        //this is so that we don't init the layer client when we run unit tests
        Configuration config = mock(Configuration.class);

        when(configManager.getPersistentConfiguration()).thenReturn(config);
        return configManager;
    }
}
