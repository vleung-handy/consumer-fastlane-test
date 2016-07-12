package com.handybook.handybook.booking.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.Pair;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.BookingQuote;
import com.handybook.handybook.booking.model.BookingRequest;
import com.handybook.handybook.booking.model.BookingTransaction;
import com.handybook.handybook.booking.model.PeakPriceInfo;
import com.handybook.handybook.booking.model.PromoCode;
import com.handybook.handybook.booking.ui.activity.BookingAddressActivity;
import com.handybook.handybook.booking.ui.activity.BookingExtrasActivity;
import com.handybook.handybook.booking.ui.activity.BookingLocationActivity;
import com.handybook.handybook.booking.ui.activity.BookingRecurrenceActivity;
import com.handybook.handybook.booking.ui.activity.PeakPricingActivity;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.core.User;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.ui.activity.LoginActivity;
import com.handybook.handybook.ui.fragment.InjectedFragment;
import com.handybook.handybook.ui.fragment.LoginFragment;
import com.handybook.handybook.util.TextUtils;

import java.util.ArrayList;
import java.util.Date;

public class BookingFlowFragment extends InjectedFragment
{

    private boolean mUseCoupon = true;

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Crashlytics.log(getClass().getSimpleName() + ".onCreate with transaction " + bookingManager.getCurrentTransaction());
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Crashlytics.log(getClass().getSimpleName() + ".onResume with transaction " + bookingManager.getCurrentTransaction());
    }

    @Override
    public void onPause()
    {
        Crashlytics.log(getClass().getSimpleName() + ".onPause with transaction " + bookingManager.getCurrentTransaction());
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        Crashlytics.log(getClass().getSimpleName() + ".onDestroy with transaction " + bookingManager.getCurrentTransaction());
        super.onDestroy();
    }

    protected final void startBookingFlow(final int serviceId, final String uniq)
    {
        startBookingFlow(serviceId, uniq, null);
    }

    final void startBookingFlow(final int serviceId, final String uniq, final PromoCode promoCode)
    {
        final BookingRequest request = new BookingRequest();
        request.setServiceId(serviceId);
        request.setUniq(uniq);
        if (promoCode != null)
        {
            request.setPromoCode(promoCode.getCode());
            request.setPromoType(promoCode.getType());
        }
        request.setCoupon(bookingManager.getPromoTabCoupon());
        final User user = userManager.getCurrentUser();
        if (user != null)
        {
            request.setEmail(user.getEmail());
        }
        bookingManager.clear();
        bookingManager.setCurrentRequest(request);
        final Intent intent = new Intent(getActivity(), BookingLocationActivity.class);
        startActivity(intent);
    }

    public final void continueBookingFlow()
    {
        /*
          don't reload quote after recurrence selection, after extras selection,
          or if user skipped peak pricing
        */
        if (this instanceof BookingRecurrenceFragment
                || this instanceof PeakPricingFragment
                || this instanceof BookingExtrasFragment
                || this instanceof PeakPricingFragment)
        {
            continueFlow();
            return;
        }

        // user selected new time, reload quote
        if (this instanceof PeakPricingTableFragment)
        {
            disableInputs();
            progressDialog.show();

            final BookingQuote quote = bookingManager.getCurrentQuote();
            dataManager.updateQuoteDate(quote.getBookingId(), quote.getStartDate(),
                    bookingQuoteUpdateCallback);
            return;
        }

        final BookingRequest request = bookingManager.getCurrentRequest();
        final User user = userManager.getCurrentUser();

        if (user != null)
        {
            request.setUserId(user.getId());
            request.setEmail(user.getEmail());
        }
        else if (!(this instanceof LoginFragment))
        {
            final Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.putExtra(LoginActivity.EXTRA_FIND_USER, true);
            startActivity(intent);
            return;
        }

        disableInputs();
        progressDialog.show();
        dataManager.createQuote(request, bookingQuoteCallback);
    }

    protected void rescheduleBooking(final Booking booking, final Date date, final boolean rescheduleAll)
    {
        final String newDate = TextUtils.formatDate(date, "yyyy-MM-dd HH:mm");
        final User user = userManager.getCurrentUser();

        disableInputs();
        progressDialog.show();

        dataManager.rescheduleBooking(booking.getId(), newDate, rescheduleAll, user.getId(),
                user.getAuthToken(), new DataManager.Callback<Pair<String, BookingQuote>>()
                {
                    @Override
                    public void onSuccess(final Pair<String, BookingQuote> response)
                    {
                        if (!allowCallbacks)
                        {
                            return;
                        }
                        enableInputs();
                        progressDialog.dismiss();

                        final String message = response.first;
                        if (message != null)
                        {
                            toast.setText(message);
                            toast.show();
                        }

                        final BookingQuote quote = response.second;
                        final ArrayList<ArrayList<PeakPriceInfo>> peakTable
                                = quote != null ? quote.getPeakPriceTable() : null;

                        if (peakTable != null && !peakTable.isEmpty())
                        {
                            final Intent intent = new Intent(getActivity(), PeakPricingActivity.class);
                            intent.putExtra(BundleKeys.RESCHEDULE_BOOKING, booking);
                            intent.putExtra(BundleKeys.RESCHEDULE_PRICE_TABLE, peakTable);
                            intent.putExtra(BundleKeys.RESCHEDULE_ALL, rescheduleAll);
                            startActivityForResult(intent, ActivityResult.RESCHEDULE_NEW_DATE);
                            return;
                        }

                        final Intent intent = new Intent();

                        if (BookingFlowFragment.this instanceof PeakPricingTableFragment)
                        {
                            intent.putExtra(BundleKeys.RESCHEDULE_NEW_DATE, date.getTime());

                            getActivity().setResult(ActivityResult.RESCHEDULE_NEW_DATE, intent);
                        }
                        else if (BookingFlowFragment.this instanceof BookingDateFragment)
                        {
                            intent.putExtra(BundleKeys.RESCHEDULE_NEW_DATE, date.getTime());

                            getActivity().setResult(ActivityResult.RESCHEDULE_NEW_DATE, intent);
                        }
                        else if (BookingFlowFragment.this instanceof BookingRescheduleOptionsFragment)
                        {
                            intent.putExtra(BundleKeys.RESCHEDULE_NEW_DATE, date.getTime());

                            getActivity().setResult(ActivityResult.RESCHEDULE_NEW_DATE, intent);
                        }

                        getActivity().finish();
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error)
                    {
                        if (!allowCallbacks)
                        {
                            return;
                        }
                        enableInputs();
                        progressDialog.dismiss();
                        dataManagerErrorHandler.handleError(getActivity(), error);

                        // go back to date screen if error occurs on options screen
                        if (BookingFlowFragment.this instanceof BookingRescheduleOptionsFragment)
                        {
                            getActivity().finish();
                        }
                    }
                });
    }

    private void continueFlow()
    {
        final BookingRequest request = bookingManager.getCurrentRequest();
        final BookingQuote quote = bookingManager.getCurrentQuote();
        final User user = userManager.getCurrentUser();

        BookingTransaction transaction = bookingManager.getCurrentTransaction();
        if (transaction == null)
        {
            transaction = new BookingTransaction();
        }

        transaction.setBookingId(quote.getBookingId());
        transaction.setHours(quote.getHours());
        transaction.setStartDate(quote.getStartDate());
        transaction.setZipCode(quote.getAddress().getZip());
        transaction.setUserId(quote.getUserId());
        transaction.setServiceId(quote.getServiceId());
        transaction.setPromoApplied(bookingManager.getPromoTabCoupon());

        if (user != null)
        {
            transaction.setEmail(user.getEmail());
            transaction.setAuthToken(user.getAuthToken());
            transaction.setUserId(user.getId());
        }
        else
        {
            transaction.setEmail(request.getEmail());
        }

        bookingManager.setCurrentTransaction(transaction);

        final ArrayList<ArrayList<PeakPriceInfo>> peakTable
                = quote.getPeakPriceTable();

        boolean isVoucherFlow = request.getPromoType() == PromoCode.Type.VOUCHER;

        // show recurrence options if available (show first if regular flow)
        if (!isVoucherFlow && shouldShowRecurrenceOptions(request, false))
        {
            final Intent intent = new Intent(getActivity(), BookingRecurrenceActivity.class);
            startActivity(intent);
        }

        // show surge pricing options if necessary (show second if regular flow)
        else if (!isVoucherFlow && shouldShowSurgePricingOptions(peakTable, false))
        {
            final Intent intent = new Intent(getActivity(), PeakPricingActivity.class);
            startActivity(intent);
        }

        // show surge pricing options if necessary (show first if voucher flow)
        else if (isVoucherFlow && shouldShowSurgePricingOptions(peakTable, true))
        {
            final Intent intent = new Intent(getActivity(), PeakPricingActivity.class);
            intent.putExtra(BundleKeys.FOR_VOUCHER, true);
            startActivity(intent);
        }

        // show recurrence options if available (show second if voucher flow)
        else if (isVoucherFlow && shouldShowRecurrenceOptions(request, true))
        {
            final Intent intent = new Intent(getActivity(), BookingRecurrenceActivity.class);
            startActivity(intent);
        }

        // show extras for home cleaning
        else if (!(BookingFlowFragment.this instanceof BookingExtrasFragment)
                && request.getUniq().equals("home_cleaning"))
        {
            final Intent intent = new Intent(getActivity(), BookingExtrasActivity.class);
            startActivity(intent);
        }

        // show address info
        else
        {
            final Intent intent = new Intent(getActivity(), BookingAddressActivity.class);
            startActivity(intent);
        }

        // if user logged in, hide login view on back
        if (user != null && BookingFlowFragment.this instanceof LoginFragment)
        {
            getActivity().setResult(ActivityResult.LOGIN_FINISH);
            getActivity().finish();
        }

        enableInputs();
        progressDialog.dismiss();
    }

    private DataManager.Callback<BookingQuote> bookingQuoteCallback
            = new DataManager.Callback<BookingQuote>()
    {
        @Override
        public void onSuccess(final BookingQuote quote)
        {
            handleBookingQuoteSuccess(quote, false);
        }

        @Override
        public void onError(final DataManager.DataManagerError error)
        {
            handleBookingQuoteError(error);
        }
    };

    private DataManager.Callback<BookingQuote> bookingQuoteUpdateCallback
            = new DataManager.Callback<BookingQuote>()
    {
        @Override
        public void onSuccess(final BookingQuote quote)
        {
            handleBookingQuoteSuccess(quote, true);
        }

        @Override
        public void onError(final DataManager.DataManagerError error)
        {
            handleBookingQuoteError(error);
        }
    };

    private boolean shouldShowRecurrenceOptions(
            final BookingRequest request,
            final boolean isVoucherFlow
    )
    {
        return !(
                (BookingFlowFragment.this instanceof BookingRecurrenceFragment)
                || (BookingFlowFragment.this instanceof BookingExtrasFragment)
                        || !request.getUniq().equals("home_cleaning")
        ) && (
                isVoucherFlow || (
                        !(BookingFlowFragment.this instanceof PeakPricingFragment)
                                && !(BookingFlowFragment.this instanceof PeakPricingTableFragment)
                )
        );
    }

    private boolean shouldShowSurgePricingOptions(
            final ArrayList<ArrayList<PeakPriceInfo>> peakTable,
            final boolean isVoucherFlow
    )
    {
        return !(
                (BookingFlowFragment.this instanceof PeakPricingFragment)
                || (BookingFlowFragment.this instanceof PeakPricingTableFragment)
                || (BookingFlowFragment.this instanceof BookingExtrasFragment)
                        || peakTable == null
                        || peakTable.isEmpty()
        ) && (
                !isVoucherFlow
                || (!(BookingFlowFragment.this instanceof BookingRecurrenceFragment)));
    }

    private void handleBookingQuoteError(final DataManager.DataManagerError error)
    {
        if (!allowCallbacks)
        {
            return;
        }

        enableInputs();
        progressDialog.dismiss();
        if (isErrorCausedByInvalidCoupon(error))
        {
            informUserWeWillProceedWithoutCoupon(error);
            return;
        }
        dataManagerErrorHandler.handleError(getActivity(), error);

        if (BookingFlowFragment.this instanceof LoginFragment)
        {
            getActivity().setResult(ActivityResult.LOGIN_FINISH);
            getActivity().finish();
        }
    }

    private boolean isErrorCausedByInvalidCoupon(final DataManager.DataManagerError error)
    {
        return error != null
                && error.getMessage() != null
                && error.getMessage().toUpperCase().contains("CODE") // This is dirty but at least
                && error.getMessage().toUpperCase().contains("INVALID"); // they can finish booking
    }

    private void informUserWeWillProceedWithoutCoupon(final DataManager.DataManagerError error)
    {
        mUseCoupon = false;
        showToast(R.string.toast_error_booking_flow_coupon_invalid);
    }

    private void handleBookingQuoteSuccess(final BookingQuote quote, final boolean isUpdate)
    {
        if (BookingFlowFragment.this instanceof BookingDateFragment
                || BookingFlowFragment.this instanceof BookingOptionsFragment)
        {
            mixpanel.trackEventWhenPageSubmitted(bookingManager.getCurrentRequest());
        }

        if (!allowCallbacks)
        {
            return;
        }

        // persist extras since api doesn't return them on quote update calls
        final BookingQuote oldQuote = bookingManager.getCurrentQuote();
        if (isUpdate && oldQuote != null)
        {
            quote.setBookingOption(oldQuote.getBookingOption());
            quote.setSurgePriceTable(oldQuote.getSurgePriceTable());
        }

        // remove promo if new quote requested
        final BookingTransaction transaction = bookingManager.getCurrentTransaction();
        if (transaction != null && oldQuote != null && oldQuote.getBookingId()
                != quote.getBookingId())
        {
            transaction.setPromoApplied(null);
        }

        bookingManager.setCurrentQuote(quote);

/*
        final User user = userManager.getCurrentUser();
        // This can go, once confirmed that the coupon application on POST /quotes works.
        final String userId = user != null ? user.getId() : null;
        final String email = user != null ? user.getEmail() : null;
        final String authToken = user != null ? user.getAuthToken() : null;
        final String coupon = bookingManager.getPromoTabCoupon();
        if (coupon != null && !coupon.isEmpty() && mUseCoupon)
        {
            dataManager.applyPromo(coupon, quote.getBookingId(), userId, email, authToken,
                    new DataManager.Callback<BookingCoupon>()
                    {
                        @Override
                        public void onSuccess(final BookingCoupon response)
                        {
                            if (!allowCallbacks)
                            {
                                return;
                            }
                            quote.setPriceTable(response.getPriceTable());
                            if (transaction != null)
                            {
                                transaction.setPromoApplied(coupon);
                            }
                            continueFlow();
                        }

                        @Override
                        public void onError(final DataManager.DataManagerError error)
                        {
                            handleBookingQuoteError(error);
                        }
                    });
        }
        else
        {
            continueFlow();
        }
*/
        continueFlow();
    }

    protected void onReceiveErrorEvent(HandyEvent.ReceiveErrorEvent event)
    {
        removeUiBlockers();
        dataManagerErrorHandler.handleError(getActivity(), event.error);
    }
}
