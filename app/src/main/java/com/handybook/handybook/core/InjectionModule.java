package com.handybook.handybook.core;

import com.handybook.handybook.account.ui.AccountFragment;
import com.handybook.handybook.account.ui.ContactFragment;
import com.handybook.handybook.account.ui.EditContactInfoActivity;
import com.handybook.handybook.account.ui.EditPasswordActivity;
import com.handybook.handybook.account.ui.EditPlanAddressActivity;
import com.handybook.handybook.account.ui.EditPlanAddressFragment;
import com.handybook.handybook.account.ui.EditPlanFragment;
import com.handybook.handybook.account.ui.EditPlanFrequencyActivity;
import com.handybook.handybook.account.ui.EditPlanFrequencyFragment;
import com.handybook.handybook.account.ui.PlansFragment;
import com.handybook.handybook.account.ui.ProfileActivity;
import com.handybook.handybook.account.ui.ProfilePasswordFragment;
import com.handybook.handybook.account.ui.UpdatePaymentFragment;
import com.handybook.handybook.autocomplete.AutoCompleteAddressFragment;
import com.handybook.handybook.booking.bookingedit.ui.activity.BookingEditAddressActivity;
import com.handybook.handybook.booking.bookingedit.ui.activity.BookingEditEntryInformationActivity;
import com.handybook.handybook.booking.bookingedit.ui.activity.BookingEditExtrasActivity;
import com.handybook.handybook.booking.bookingedit.ui.activity.BookingEditFrequencyActivity;
import com.handybook.handybook.booking.bookingedit.ui.activity.BookingEditHoursActivity;
import com.handybook.handybook.booking.bookingedit.ui.activity.BookingEditPreferencesActivity;
import com.handybook.handybook.booking.bookingedit.ui.fragment.BookingEditAddressFragment;
import com.handybook.handybook.booking.bookingedit.ui.fragment.BookingEditEntryInformationFragment;
import com.handybook.handybook.booking.bookingedit.ui.fragment.BookingEditExtrasFragment;
import com.handybook.handybook.booking.bookingedit.ui.fragment.BookingEditFrequencyFragment;
import com.handybook.handybook.booking.bookingedit.ui.fragment.BookingEditHoursFragment;
import com.handybook.handybook.booking.bookingedit.ui.fragment.BookingEditPreferencesFragment;
import com.handybook.handybook.booking.history.HistoryActivity;
import com.handybook.handybook.booking.history.HistoryFragment;
import com.handybook.handybook.booking.rating.RateImprovementConfirmationDialogFragment;
import com.handybook.handybook.booking.rating.RateImprovementDialogFragment;
import com.handybook.handybook.booking.rating.RatingsGridFragment;
import com.handybook.handybook.booking.rating.RatingsRadioFragment;
import com.handybook.handybook.booking.reschedule.RescheduleUpcomingActivity;
import com.handybook.handybook.booking.ui.activity.BookingAddressActivity;
import com.handybook.handybook.booking.ui.activity.BookingCancelOptionsActivity;
import com.handybook.handybook.booking.ui.activity.BookingDateActivity;
import com.handybook.handybook.booking.ui.activity.BookingDetailActivity;
import com.handybook.handybook.booking.ui.activity.BookingExtrasActivity;
import com.handybook.handybook.booking.ui.activity.BookingFinalizeActivity;
import com.handybook.handybook.booking.ui.activity.BookingGetQuoteActivity;
import com.handybook.handybook.booking.ui.activity.BookingLocationActivity;
import com.handybook.handybook.booking.ui.activity.BookingOptionsActivity;
import com.handybook.handybook.booking.ui.activity.BookingPaymentActivity;
import com.handybook.handybook.booking.ui.activity.BookingRecurrenceActivity;
import com.handybook.handybook.booking.ui.activity.BookingRescheduleOptionsActivity;
import com.handybook.handybook.booking.ui.activity.BookingsActivity;
import com.handybook.handybook.booking.ui.activity.CancelRecurringBookingActivity;
import com.handybook.handybook.booking.ui.activity.PeakPricingActivity;
import com.handybook.handybook.booking.ui.activity.PromosActivity;
import com.handybook.handybook.booking.ui.activity.ReportIssueActivity;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.booking.ui.activity.ServicesActivity;
import com.handybook.handybook.booking.ui.activity.ZipActivity;
import com.handybook.handybook.booking.ui.fragment.ActiveBookingFragment;
import com.handybook.handybook.booking.ui.fragment.AddLaundryDialogFragment;
import com.handybook.handybook.booking.ui.fragment.BookingAddressFragment;
import com.handybook.handybook.booking.ui.fragment.BookingCancelReasonFragment;
import com.handybook.handybook.booking.ui.fragment.BookingCancelWarningFragment;
import com.handybook.handybook.booking.ui.fragment.BookingDateFragmentV2;
import com.handybook.handybook.booking.ui.fragment.BookingDateTimeInputFragment;
import com.handybook.handybook.booking.ui.fragment.BookingDetailFragment;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragment;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentAddress;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentBookingActions;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentEntryInformation;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentExtras;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentLaundry;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentPayment;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentPreferences;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentProInformation;
import com.handybook.handybook.booking.ui.fragment.BookingEntryInfoFragment;
import com.handybook.handybook.booking.ui.fragment.BookingExtrasFragment;
import com.handybook.handybook.booking.ui.fragment.BookingGetQuoteFragment;
import com.handybook.handybook.booking.ui.fragment.BookingHeaderFragment;
import com.handybook.handybook.booking.ui.fragment.BookingLocationFragment;
import com.handybook.handybook.booking.ui.fragment.BookingOptionsFragment;
import com.handybook.handybook.booking.ui.fragment.BookingOptionsInputFragment;
import com.handybook.handybook.booking.ui.fragment.BookingPasswordPromptFragment;
import com.handybook.handybook.booking.ui.fragment.BookingPaymentFragment;
import com.handybook.handybook.booking.ui.fragment.BookingPreferencesFragment;
import com.handybook.handybook.booking.ui.fragment.BookingRecurrenceFragment;
import com.handybook.handybook.booking.ui.fragment.BookingRescheduleOptionsFragment;
import com.handybook.handybook.booking.ui.fragment.BookingSubscriptionFragment;
import com.handybook.handybook.booking.ui.fragment.CancelRecurringBookingFragment;
import com.handybook.handybook.booking.ui.fragment.CancelRecurringBookingSelectionFragment;
import com.handybook.handybook.booking.ui.fragment.EmailCancellationDialogFragment;
import com.handybook.handybook.booking.ui.fragment.LaundryDropOffDialogFragment;
import com.handybook.handybook.booking.ui.fragment.LaundryInfoDialogFragment;
import com.handybook.handybook.booking.ui.fragment.PeakPricingFragment;
import com.handybook.handybook.booking.ui.fragment.PeakPricingTableFragment;
import com.handybook.handybook.booking.ui.fragment.PromosFragment;
import com.handybook.handybook.booking.ui.fragment.RateProTeamFragment;
import com.handybook.handybook.booking.ui.fragment.RateServiceConfirmDialogFragment;
import com.handybook.handybook.booking.ui.fragment.RateServiceDialogFragment;
import com.handybook.handybook.booking.ui.fragment.ReferralDialogFragment;
import com.handybook.handybook.booking.ui.fragment.ReportIssueFragment;
import com.handybook.handybook.booking.ui.fragment.RescheduleDialogFragment;
import com.handybook.handybook.booking.ui.fragment.ServiceCategoriesFragment;
import com.handybook.handybook.booking.ui.fragment.ServiceCategoriesHomeFragment;
import com.handybook.handybook.booking.ui.fragment.ServicesFragment;
import com.handybook.handybook.booking.ui.fragment.TipDialogFragment;
import com.handybook.handybook.booking.ui.fragment.UpcomingBookingsFragment;
import com.handybook.handybook.booking.ui.fragment.ZipFragment;
import com.handybook.handybook.booking.ui.fragment.dialog.BookingTimeInputDialogFragment;
import com.handybook.handybook.booking.ui.view.ServiceCategoriesOverlayFragment;
import com.handybook.handybook.bottomnav.BottomNavActivity;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.manager.SessionManager;
import com.handybook.handybook.core.receiver.LayerPushReceiver;
import com.handybook.handybook.core.ui.activity.BlockingActivity;
import com.handybook.handybook.core.ui.activity.LoginActivity;
import com.handybook.handybook.core.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.core.ui.activity.OldDeeplinkSplashActivity;
import com.handybook.handybook.core.ui.activity.SplashActivity;
import com.handybook.handybook.core.ui.activity.UpdatePaymentActivity;
import com.handybook.handybook.core.ui.activity.WebViewActivity;
import com.handybook.handybook.core.ui.fragment.BlockingUpdateFragment;
import com.handybook.handybook.core.ui.fragment.LoginFragment;
import com.handybook.handybook.core.ui.fragment.NavbarWebViewDialogFragment;
import com.handybook.handybook.library.ui.fragment.WebViewFragment;
import com.handybook.handybook.proteam.ui.activity.ProMessagesActivity;
import com.handybook.handybook.proteam.ui.activity.ProTeamActivity;
import com.handybook.handybook.proteam.ui.activity.ProTeamEditActivity;
import com.handybook.handybook.proteam.ui.activity.ProTeamPerBookingActivity;
import com.handybook.handybook.proteam.ui.fragment.BookingProTeamConversationsFragment;
import com.handybook.handybook.proteam.ui.fragment.NewProTeamProListFragment;
import com.handybook.handybook.proteam.ui.fragment.ProTeamConversationsFragment;
import com.handybook.handybook.proteam.ui.fragment.ProTeamEditFragment;
import com.handybook.handybook.proteam.ui.fragment.ProTeamProListFragment;
import com.handybook.handybook.push.receiver.PushReceiver;
import com.handybook.handybook.yozio.YozioMetaDataCallback;

import dagger.Module;

@Module(
        library = true,
        complete = false,
        injects = {
                SplashActivity.class,
                ServiceCategoriesFragment.class,
                LoginFragment.class,
                UpcomingBookingsFragment.class,
                HistoryFragment.class,
                ActiveBookingFragment.class,
                AutoCompleteAddressFragment.class,
                BookingDetailFragment.class,
                ServiceCategoriesFragment.class,
                ServicesFragment.class,
                BookingLocationFragment.class,
                BookingOptionsFragment.class,
                BookingDateFragmentV2.class,
                BookingAddressFragment.class,
                BookingHeaderFragment.class,
                BookingPaymentFragment.class,
                PeakPricingFragment.class,
                PeakPricingTableFragment.class,
                PromosFragment.class,
                BookingExtrasFragment.class,
                BookingRecurrenceFragment.class,
                DataManager.class,
                ServiceCategoriesActivity.class,
                ServicesActivity.class, ProfileActivity.class,
                PeakPricingActivity.class,
                MenuDrawerActivity.class,
                LoginActivity.class,
                BookingsActivity.class,
                HistoryActivity.class,
                BookingRecurrenceActivity.class,
                BookingPaymentActivity.class,
                BookingOptionsActivity.class,
                BookingLocationActivity.class,
                BookingExtrasActivity.class,
                BookingDetailActivity.class,
                BookingDateActivity.class,
                BookingFinalizeActivity.class,
                BookingPreferencesFragment.class,
                BookingPasswordPromptFragment.class,
                BookingEntryInfoFragment.class,
                BookingAddressActivity.class,
                PromosActivity.class,
                BookingRescheduleOptionsActivity.class,
                BookingRescheduleOptionsFragment.class,
                BookingCancelOptionsActivity.class,
                BookingCancelReasonFragment.class,
                BookingCancelWarningFragment.class,
                YozioMetaDataCallback.class,
                RateServiceDialogFragment.class,
                RateServiceConfirmDialogFragment.class,
                LaundryDropOffDialogFragment.class,
                LaundryInfoDialogFragment.class,
                AddLaundryDialogFragment.class,
                OldDeeplinkSplashActivity.class,
                BookingDetailSectionFragment.class,
                BookingDetailSectionFragmentAddress.class,
                BookingDetailSectionFragmentEntryInformation.class,
                BookingDetailSectionFragmentExtras.class,
                BookingDetailSectionFragmentLaundry.class,
                BookingDetailSectionFragmentPreferences.class,
                BookingDetailSectionFragmentPayment.class,
                BookingDetailSectionFragmentProInformation.class,
                BookingDetailSectionFragmentBookingActions.class,
                BookingEditPreferencesActivity.class,
                BookingEditPreferencesFragment.class,
                BookingEditEntryInformationActivity.class,
                BookingEditEntryInformationFragment.class,
                BookingEditFrequencyActivity.class,
                BookingEditFrequencyFragment.class,
                BookingEditExtrasActivity.class,
                BookingEditExtrasFragment.class,
                BookingEditHoursActivity.class,
                BookingEditHoursFragment.class,
                BookingEditAddressActivity.class,
                BookingEditAddressFragment.class,
                BlockingActivity.class,
                BlockingUpdateFragment.class,
                TipDialogFragment.class,
                CancelRecurringBookingActivity.class,
                CancelRecurringBookingSelectionFragment.class,
                CancelRecurringBookingFragment.class,
                EmailCancellationDialogFragment.class,
                UpdatePaymentActivity.class,
                UpdatePaymentFragment.class,
                NavbarWebViewDialogFragment.class,
                ServiceCategoriesOverlayFragment.class,
                PushReceiver.class,
                ReferralDialogFragment.class,
                RateImprovementDialogFragment.class,
                RatingsGridFragment.class,
                RateProTeamFragment.class,
                RatingsRadioFragment.class,
                RateImprovementConfirmationDialogFragment.class,
                ProTeamActivity.class,
                ProMessagesActivity.class,
                ProTeamConversationsFragment.class,
                ProTeamEditFragment.class,
                ProTeamProListFragment.class,
                NewProTeamProListFragment.class,
                ReportIssueActivity.class,
                ReportIssueFragment.class,
                WebViewActivity.class,
                WebViewFragment.class,
                AccountFragment.class,
                ContactFragment.class,
                ProfilePasswordFragment.class,
                PlansFragment.class,
                EditPlanFragment.class,
                EditPlanAddressFragment.class,
                EditPlanFrequencyFragment.class,
                RescheduleUpcomingActivity.class,
                BottomNavActivity.class,
                LayerPushReceiver.class,
                BookingSubscriptionFragment.class,
                ProTeamPerBookingActivity.class,
                BookingProTeamConversationsFragment.class,
                RescheduleDialogFragment.class,
                EditContactInfoActivity.class,
                EditPasswordActivity.class,
                EditPlanFrequencyActivity.class,
                EditPlanAddressActivity.class,
                ProTeamEditActivity.class,
                ServiceCategoriesHomeFragment.class,
                SessionManager.class,
                ZipActivity.class,
                ZipFragment.class,
                BookingTimeInputDialogFragment.class,
                BookingDateTimeInputFragment.class,
                BookingGetQuoteActivity.class,
                BookingGetQuoteFragment.class,
                BookingOptionsInputFragment.class,
        })
public final class InjectionModule {}
