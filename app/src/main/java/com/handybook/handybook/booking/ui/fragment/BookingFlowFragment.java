package com.handybook.handybook.booking.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.BookingOption;
import com.handybook.handybook.booking.model.BookingOptionsWrapper;
import com.handybook.handybook.booking.model.BookingQuote;
import com.handybook.handybook.booking.model.BookingRequest;
import com.handybook.handybook.booking.model.BookingTransaction;
import com.handybook.handybook.booking.model.PeakPriceInfo;
import com.handybook.handybook.booking.model.PromoCode;
import com.handybook.handybook.booking.model.ZipValidationResponse;
import com.handybook.handybook.booking.ui.activity.BookingAddressActivity;
import com.handybook.handybook.booking.ui.activity.BookingDateActivity;
import com.handybook.handybook.booking.ui.activity.BookingExtrasActivity;
import com.handybook.handybook.booking.ui.activity.BookingGetQuoteActivity;
import com.handybook.handybook.booking.ui.activity.BookingLocationActivity;
import com.handybook.handybook.booking.ui.activity.BookingOptionsActivity;
import com.handybook.handybook.booking.ui.activity.BookingRecurrenceActivity;
import com.handybook.handybook.booking.ui.activity.PeakPricingActivity;
import com.handybook.handybook.configuration.model.Configuration;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.constant.ActivityResult;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.constant.PrefsKey;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.callback.FragmentSafeCallback;
import com.handybook.handybook.core.event.HandyEvent;
import com.handybook.handybook.core.manager.DefaultPreferencesManager;
import com.handybook.handybook.core.model.response.UserExistsResponse;
import com.handybook.handybook.core.ui.activity.LoginActivity;
import com.handybook.handybook.core.ui.fragment.LoginFragment;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.library.util.DateTimeUtils;
import com.handybook.handybook.library.util.TextUtils;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.booking.BookingDetailsLog;
import com.handybook.handybook.logger.handylogger.model.booking.BookingFunnelLog;
import com.handybook.handybook.logger.handylogger.model.chat.ChatLog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;

import static com.handybook.handybook.booking.ui.fragment.BookingOptionsInputFragment.EXTRA_OPTIONS;

public class BookingFlowFragment extends InjectedFragment {

    public static final int MAX_PROGRESS = 100;

    protected boolean mFlowStarted;
    protected boolean mShowProgress;
    protected int mProgress;

    @Bind(R.id.toolbar)
    protected Toolbar mToolbar;
    @Bind(R.id.horizontal_progress_bar)
    protected ProgressBar mProgressBar;

    @Inject
    protected DefaultPreferencesManager mDefaultPreferencesManager;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        if (getArguments() != null) {
            mShowProgress = getArguments().getBoolean(BundleKeys.SHOW_PROGRESS, false);
            mFlowStarted = getArguments().getBoolean(BundleKeys.BOOKING_FLOW_STARTED, false);
            mProgress = getArguments().getInt(BundleKeys.PROGRESS, 0);
        }
        super.onCreate(savedInstanceState);
        Crashlytics.log(getClass().getSimpleName() + ".onCreate with transaction "
                        + bookingManager.getCurrentTransaction());
    }

    @Override
    public void onResume() {
        super.onResume();
        Crashlytics.log(getClass().getSimpleName() + ".onResume with transaction "
                        + bookingManager.getCurrentTransaction());
    }

    @Override
    public void onPause() {
        Crashlytics.log(getClass().getSimpleName() + ".onPause with transaction "
                        + bookingManager.getCurrentTransaction());
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Crashlytics.log(getClass().getSimpleName() + ".onDestroy with transaction "
                        + bookingManager.getCurrentTransaction());
        super.onDestroy();
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        setupProgressBar();
        super.onViewCreated(view, savedInstanceState);
    }

    protected final void startBookingFlow(final int serviceId, final String uniq) {
        startBookingFlow(serviceId, uniq, null);
    }

    final void startBookingFlow(final int serviceId, final String uniq, final PromoCode promoCode) {
        final BookingRequest request = new BookingRequest();
        request.setServiceId(serviceId);
        request.setUniq(uniq);
        if (promoCode != null) {
            request.setPromoCode(promoCode.getCode());
            request.setPromoType(promoCode.getType());
        }
        request.setCoupon(bookingManager.getPromoTabCoupon());
        final User user = userManager.getCurrentUser();
        if (user != null) {
            request.setEmail(user.getEmail());
        }
        bookingManager.clear();
        bookingManager.setCurrentRequest(request);

        Configuration config = mConfigurationManager.getPersistentConfiguration();
        String zip = mDefaultPreferencesManager.getString(PrefsKey.ZIP, null);
        if (config.isConsolidateBookingGetQuoteFlowExperimentEnabled()) {
            startConsolidatedGetQuoteFlow();
        }
        else if (config.isOnboardingV2Enabled() && !TextUtils.isBlank(zip)) {
            validateZipAndProceed(zip);
        }
        else {
            final Intent intent = new Intent(getActivity(), BookingLocationActivity.class);
            intent.putExtras(createProgressBundle());
            startActivity(intent);
        }
    }

    protected void setupProgressBar() {
        mProgressBar.setVisibility(mShowProgress ? View.VISIBLE : View.GONE);
        mProgressBar.setProgress(mProgress);
    }

    protected Bundle createProgressBundle() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(BundleKeys.BOOKING_FLOW_STARTED, true);
        bundle.putBoolean(BundleKeys.SHOW_PROGRESS, mShowProgress);
        bundle.putInt(BundleKeys.PROGRESS, mProgress + 15);
        return bundle;
    }

    private Bundle createInitialProgressBundle() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(BundleKeys.BOOKING_FLOW_STARTED, true);
        bundle.putBoolean(BundleKeys.SHOW_PROGRESS, true);
        bundle.putInt(BundleKeys.PROGRESS, 10);
        return bundle;
    }

    private void startConsolidatedGetQuoteFlow() {
        showUiBlockers();
        final BookingRequest request = bookingManager.getCurrentRequest();
        final User user = userManager.getCurrentUser();
        final String userId = user != null ? user.getId() : null;
        dataManager.getQuoteOptions(
                request.getServiceId(),
                userId,
                new FragmentSafeCallback<BookingOptionsWrapper>(BookingFlowFragment.this) {
                    @Override
                    public void onCallbackSuccess(final BookingOptionsWrapper options) {
                        removeUiBlockers();

                        List<BookingOption> bookingOptions = options.getBookingOptions();
                        final Intent intent = new Intent(
                                getActivity(),
                                BookingGetQuoteActivity.class
                        );
                        intent.putExtras(createInitialProgressBundle());
                        intent.putParcelableArrayListExtra(
                                EXTRA_OPTIONS,
                                new ArrayList<>(bookingOptions)
                        );
                        startActivity(intent);
                    }

                    @Override
                    public void onCallbackError(final DataManager.DataManagerError error) {
                        if (!allowCallbacks) { return; }
                        removeUiBlockers();
                        dataManagerErrorHandler.handleError(getActivity(), error);
                    }
                }
        );

    }

    /**
     * updates the current booking request with the given ZipValidationResponse
     * @param response
     */
    private void updateCurrentBookingRequest(@Nullable ZipValidationResponse response) {
        if (response == null) {
            return; //if the response is not present, don't override these with null
        }
        bookingManager.getCurrentRequest().setZipCode(
                response.getZipArea() == null ?
                null : response.getZipArea().getZip());
        bookingManager.getCurrentRequest().setZipArea(response.getZipArea());
        bookingManager.getCurrentRequest().setTimeZone(response.getTimeZone());
    }

    /**
     * Even though we're not showing the "zip" page to the user we still have to call this
     * "zip validation" step, so we can have the proper time zone setup
     */
    private void validateZipAndProceed(final String zipCode) {
        showUiBlockers();
        final BookingRequest request = bookingManager.getCurrentRequest();
        final User user = userManager.getCurrentUser();
        final String userId = user != null ? user.getId() : null;

        dataManager.validateBookingZip(
                request.getServiceId(),
                zipCode,
                userId,
                request.getPromoCode(),
                new FragmentSafeCallback<ZipValidationResponse>(BookingFlowFragment.this) {
                    @Override
                    public void onCallbackSuccess(ZipValidationResponse response) {
                        //if we are in "onboarding" mode, and we have a zip, then skip the BookingLocationActivity
                        //and go directly to the Booking options (beds, bath)
                        updateCurrentBookingRequest(response);
                        if (TextUtils.isBlank(request.getPromoCode())) {
                            //we're not in a promotional flow, so we can display booking options
                            displayBookingOptions();
                        }
                        else {
                            removeUiBlockers();
                            //if we're in a promotional flow, we go straight to the date selection
                            final Intent intent = new Intent(
                                    getActivity(),
                                    BookingDateActivity.class
                            );
                            intent.putExtras(createProgressBundle());
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCallbackError(final DataManager.DataManagerError error) {
                        removeUiBlockers();
                        dataManagerErrorHandler.handleError(getActivity(), error);
                    }
                }
        );
    }

    /**
     * proceeds to display the Booking options (beds, baths)
     */
    protected void displayBookingOptions() {
        final BookingRequest request = bookingManager.getCurrentRequest();
        String userId = null;
        final User user = userManager.getCurrentUser();
        if (user != null) {
            userId = user.getId();
        }
        dataManager.getQuoteOptions(
                request.getServiceId(), userId,
                new FragmentSafeCallback<BookingOptionsWrapper>(this) {
                    @Override
                    public void onCallbackSuccess(final BookingOptionsWrapper options) {
                        if (!allowCallbacks) { return; }
                        removeUiBlockers();
                        List<BookingOption> bookingOptions = options.getBookingOptions();
                        final Intent intent = new Intent(
                                getActivity(),
                                BookingOptionsActivity.class
                        );
                        intent.putParcelableArrayListExtra(
                                BookingOptionsActivity.EXTRA_OPTIONS,
                                new ArrayList<>(bookingOptions)
                        );
                        if (mFlowStarted) {
                            intent.putExtras(createProgressBundle());
                        }
                        else {
                            intent.putExtras(createInitialProgressBundle());
                        }
                        intent.putExtra(BookingOptionsActivity.EXTRA_PAGE, 0);
                        startActivity(intent);
                    }

                    @Override
                    public void onCallbackError(final DataManager.DataManagerError error) {
                        if (!allowCallbacks) { return; }
                        removeUiBlockers();
                        dataManagerErrorHandler.handleError(getActivity(), error);
                    }
                }
        );
    }

    public final void continueBookingFlow() {
        /*
          don't reload quote after recurrence selection, after extras selection,
          or if user skipped peak pricing
        */
        if (this instanceof BookingRecurrenceFragment
            || this instanceof BookingSubscriptionFragment
            || this instanceof BookingExtrasFragment
            || this instanceof PeakPricingFragment) {
            continueFlow();
            return;
        }

        // user selected new time, reload quote
        if (this instanceof PeakPricingTableFragment) {
            disableInputs();
            progressDialog.show();

            final BookingQuote quote = bookingManager.getCurrentQuote();
            dataManager.updateQuoteDate(
                    quote.getBookingId(),
                    quote.getStartDate(),
                    bookingQuoteUpdateCallback
            );
            return;
        }
        final BookingRequest request = bookingManager.getCurrentRequest();
        final User user = userManager.getCurrentUser();
        if (user != null) {
            request.setUserId(user.getId());
            if (TextUtils.isBlank(request.getEmail())) {
                //don't override booking request object's email field if present
                request.setEmail(user.getEmail());
            }
        }
        else if (!(this instanceof BookingGetQuoteFragment
                   //login is handled after quote response which tells us if user exists
                   || this instanceof LoginFragment
                   || hasStoredEmailAndZip())) {
            //if we are not in the new onboarding flow (i.e., we don't have zip & email stored),
            //then we should prompt the user to login.
            final Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.putExtras(createProgressBundle());
            intent.putExtra(LoginActivity.EXTRA_FIND_USER, true);
            intent.putExtra(LoginActivity.EXTRA_FROM_BOOKING_FUNNEL, true);
            startActivity(intent);
            return;
        }
        else if (TextUtils.isBlank(request.getEmail()) && hasStoredEmail()) {
            request.setEmail(mDefaultPreferencesManager.getString(PrefsKey.EMAIL, null));
        }
        disableInputs();
        progressDialog.show();
        bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingQuoteRequestSubmitted()));
        dataManager.createQuote(request, bookingQuoteCallback);
    }

    /**
     * returns true if we already have zip and email stored in shared prefs.
     * @return
     */
    protected boolean hasStoredEmailAndZip() {
        return hasStoredEmail() && hasStoredZip();
    }

    protected boolean hasStoredZip() {
        String zip = mDefaultPreferencesManager.getString(PrefsKey.ZIP, null);
        return !TextUtils.isBlank(zip);
    }

    protected boolean hasStoredEmail() {
        String email = mDefaultPreferencesManager.getString(PrefsKey.EMAIL, null);
        return !TextUtils.isBlank(email);
    }

    protected void rescheduleBooking(
            final Booking booking,
            final Date date,
            final boolean rescheduleAll,
            @Nullable final String providerId,
            final BookingDetailFragment.RescheduleType rescheduleType,
            @Nullable final String recurringId
    ) {
        final String newDate = DateTimeUtils.formatDate(
                date,
                DateTimeUtils.SCHEDULE_DATE_TIME_FORMAT_NO_TIMEZONE,
                booking.getBookingTimezone()
        );
        final User user = userManager.getCurrentUser();
        disableInputs();
        progressDialog.show();

        //log submitted
        if (rescheduleType == BookingDetailFragment.RescheduleType.FROM_CHAT) {
            bus.post(new LogEvent.AddLogEvent(new ChatLog.RescheduleSubmittedLog(
                    providerId,
                    booking.getId(),
                    booking.getStartDate(),
                    date,
                    recurringId
                     ))
            );
        }
        else {
            bus.post(new LogEvent.AddLogEvent(new BookingDetailsLog.RescheduleBooking(
                             BookingDetailsLog.EventType.SUBMITTED,
                             booking.getId(),
                             booking.getStartDate(),
                             date
                     ))
            );
        }

        dataManager.rescheduleBooking(
                booking.getId(),
                newDate,
                rescheduleAll,
                user.getId(),
                providerId,
                new FragmentSafeCallback<Pair<String, BookingQuote>>(this) {
                    @Override
                    public void onCallbackSuccess(final Pair<String, BookingQuote> response) {
                        //log success
                        if (rescheduleType == BookingDetailFragment.RescheduleType.FROM_CHAT) {
                            bus.post(new LogEvent.AddLogEvent(new ChatLog.RescheduleSuccessLog(
                                             providerId,
                                             booking.getId(),
                                             booking.getStartDate(),
                                             date,
                                             recurringId
                                     ))
                            );
                        }
                        else {
                            bus.post(new LogEvent.AddLogEvent(new BookingDetailsLog.RescheduleBooking(
                                             BookingDetailsLog.EventType.SUCCESS,
                                             booking.getId(),
                                             booking.getStartDate(),
                                             date
                                     ))
                            );
                        }

                        if (!allowCallbacks) {
                            return;
                        }
                        enableInputs();
                        progressDialog.dismiss();
                        final String message = response.first;
                        if (message != null) {
                            toast.setText(message);
                            toast.show();
                        }
                        final BookingQuote quote = response.second;

                        final ArrayList<ArrayList<PeakPriceInfo>> peakTable
                                = quote != null ? quote.getPeakPriceTable() : null;
                        if (peakTable != null && !peakTable.isEmpty()) {
                            final Intent intent = new Intent(
                                    getActivity(),
                                    PeakPricingActivity.class
                            );
                            intent.putExtras(createProgressBundle());
                            intent.putExtra(BundleKeys.RESCHEDULE_BOOKING, booking);
                            intent.putExtra(BundleKeys.RESCHEDULE_PRICE_TABLE, peakTable);
                            intent.putExtra(BundleKeys.RESCHEDULE_ALL, rescheduleAll);
                            startActivityForResult(intent, ActivityResult.RESCHEDULE_NEW_DATE);
                            return;
                        }
                        final Intent intent = new Intent();
                        if (BookingFlowFragment.this instanceof PeakPricingTableFragment) {
                            intent.putExtra(BundleKeys.RESCHEDULE_NEW_DATE, date.getTime());
                            getActivity().setResult(ActivityResult.RESCHEDULE_NEW_DATE, intent);
                        }
                        else if (BookingFlowFragment.this instanceof BookingDateFragment) {
                            intent.putExtra(BundleKeys.RESCHEDULE_NEW_DATE, date.getTime());
                            getActivity().setResult(ActivityResult.RESCHEDULE_NEW_DATE, intent);
                        }
                        else if (BookingFlowFragment.this instanceof BookingRescheduleOptionsFragment) {
                            intent.putExtra(BundleKeys.RESCHEDULE_NEW_DATE, date.getTime());
                            getActivity().setResult(ActivityResult.RESCHEDULE_NEW_DATE, intent);
                        }
                        getActivity().finish();
                    }

                    @Override
                    public void onCallbackError(final DataManager.DataManagerError error) {
                        //log error
                        if (rescheduleType == BookingDetailFragment.RescheduleType.FROM_CHAT) {
                            bus.post(new LogEvent.AddLogEvent(new ChatLog.RescheduleErrorLog(
                                             providerId,
                                             booking.getId(),
                                             booking.getStartDate(),
                                             date,
                                             recurringId
                                     ))
                            );
                        }
                        else {
                            bus.post(new LogEvent.AddLogEvent(new BookingDetailsLog.RescheduleBooking(
                                             BookingDetailsLog.EventType.ERROR,
                                             booking.getId(),
                                             booking.getStartDate(),
                                             date
                                     ))
                            );
                        }

                        if (!allowCallbacks) {
                            return;
                        }
                        enableInputs();
                        progressDialog.dismiss();
                        dataManagerErrorHandler.handleError(getActivity(), error);
                        // go back to date screen if error occurs on options screen
                        if (BookingFlowFragment.this instanceof BookingRescheduleOptionsFragment) {
                            getActivity().finish();
                        }
                    }
                }
        );
    }

    private void continueFlow() {
        final BookingRequest request = bookingManager.getCurrentRequest();
        final BookingQuote quote = bookingManager.getCurrentQuote();
        final User user = userManager.getCurrentUser();
        BookingTransaction transaction = bookingManager.getCurrentTransaction();
        if (transaction == null) {
            transaction = new BookingTransaction();
        }
        transaction.setBookingId(quote.getBookingId());
        transaction.setHours(quote.getHours());
        transaction.setStartDate(quote.getStartDate());
        transaction.setZipCode(quote.getAddress().getZip());
        transaction.setUserId(quote.getUserId());
        transaction.setServiceId(quote.getServiceId());
        transaction.setProviderId(request.getProviderId());
        transaction.setPromoCode(
                bookingManager.getPromoTabCoupon(),
                transaction.shouldPromoCodeBeHidden()
        );
        if (user != null) {
            transaction.setEmail(user.getEmail());
            transaction.setAuthToken(user.getAuthToken());
            transaction.setUserId(user.getId());
        }
        else {
            transaction.setEmail(request.getEmail());
        }
        bookingManager.setCurrentTransaction(transaction);
        UserExistsResponse emailResponse = quote.getUserExistsResponse();
        if (emailResponse != null && emailResponse.exists()) {
            //user exists, go to login
            startLoginActivity(transaction.getEmail(), emailResponse.getFirstName());
        }
        //otherwise, ignore

        final ArrayList<ArrayList<PeakPriceInfo>> peakTable
                = quote.getPeakPriceTable();
        boolean isVoucherFlow = request.getPromoType() == PromoCode.Type.VOUCHER;
        // show recurrence options if available (show first if regular flow)
        if (!isVoucherFlow && shouldShowRecurrenceOptions(request, false)) {
            final Intent intent = new Intent(getActivity(), BookingRecurrenceActivity.class);
            intent.putExtras(createProgressBundle());
            startActivity(intent);
        }
        // show surge pricing options if necessary (show second if regular flow)
        else if (!isVoucherFlow && shouldShowSurgePricingOptions(peakTable, false)) {
            final Intent intent = new Intent(getActivity(), PeakPricingActivity.class);
            intent.putExtras(createProgressBundle());
            startActivity(intent);
        }
        // show surge pricing options if necessary (show first if voucher flow)
        else if (isVoucherFlow && shouldShowSurgePricingOptions(peakTable, true)) {
            final Intent intent = new Intent(getActivity(), PeakPricingActivity.class);
            intent.putExtras(createProgressBundle());
            intent.putExtra(BundleKeys.FOR_VOUCHER, true);
            startActivity(intent);
        }
        // show recurrence options if available (show second if voucher flow)
        else if (isVoucherFlow && shouldShowRecurrenceOptions(request, true)) {
            final Intent intent = new Intent(getActivity(), BookingRecurrenceActivity.class);
            intent.putExtras(createProgressBundle());
            startActivity(intent);
        }
        // show extras for home cleaning
        else if (!(BookingFlowFragment.this instanceof BookingExtrasFragment)
                 && request.getUniq().equals("home_cleaning")) {
            final Intent intent = new Intent(getActivity(), BookingExtrasActivity.class);
            intent.putExtras(createProgressBundle());
            startActivity(intent);
        }
        // show address info
        else {
            final Intent intent = new Intent(getActivity(), BookingAddressActivity.class);
            intent.putExtras(createProgressBundle());
            startActivity(intent);
        }
        // if user logged in, hide login view on back
        if (user != null && BookingFlowFragment.this instanceof LoginFragment) {
            getActivity().setResult(ActivityResult.LOGIN_FINISH);
            getActivity().finish();
        }
        enableInputs();
        progressDialog.dismiss();
    }

    private DataManager.Callback<BookingQuote> bookingQuoteCallback
            = new FragmentSafeCallback<BookingQuote>(this) {
        @Override
        public void onCallbackSuccess(final BookingQuote quote) {
            bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingQuoteRequestSuccess()));
            handleBookingQuoteSuccess(quote, false);
        }

        @Override
        public void onCallbackError(final DataManager.DataManagerError error) {
            bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingQuoteRequestError(error.getMessage())));
            handleBookingQuoteError(error);
        }
    };

    private DataManager.Callback<BookingQuote> bookingQuoteUpdateCallback
            = new FragmentSafeCallback<BookingQuote>(this) {
        @Override
        public void onCallbackSuccess(final BookingQuote quote) {
            handleBookingQuoteSuccess(quote, true);
        }

        @Override
        public void onCallbackError(final DataManager.DataManagerError error) {
            handleBookingQuoteError(error);
        }
    };

    private boolean shouldShowRecurrenceOptions(
            final BookingRequest request,
            final boolean isVoucherFlow
    ) {
        return !(
                (BookingFlowFragment.this instanceof BookingRecurrenceFragment)
                || (BookingFlowFragment.this instanceof BookingSubscriptionFragment)
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
    ) {
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

    private void startLoginActivity(@Nullable String email, @Nullable String bookingUserName) {
        final Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.putExtras(createProgressBundle());
        intent.putExtra(LoginActivity.EXTRA_BOOKING_EMAIL, email);
        intent.putExtra(LoginActivity.EXTRA_BOOKING_USER_NAME, bookingUserName);
        intent.putExtra(LoginActivity.EXTRA_FROM_BOOKING_FUNNEL, true);
        startActivity(intent);
    }

    private void handleBookingQuoteError(final DataManager.DataManagerError error) {
        if (!allowCallbacks) {
            return;
        }
        enableInputs();
        progressDialog.dismiss();

        /*
        even though this log is already in BookingLocationFragment,
        logging it here to cover the consolidated booking flow experiment.
        note this will be double-logged but logging department says that is OK,
        so not bothering to only log this if consolidated quote flow config is on
        because that would make this more confusing
        */
        if (isErrorCausedByInvalidZip(error)) {
            bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingZipErrorLog(
                    bookingManager.getCurrentRequest() == null
                    ? null
                    : bookingManager.getCurrentRequest().getZipCode(),
                    error.getMessage()
            )));
        }

        if (isErrorCausedByLoginRequired(error) && !(this instanceof LoginFragment)) {
            String email = bookingManager.getCurrentRequest() == null ?
                           null : bookingManager.getCurrentRequest().getEmail();
            startLoginActivity(email, null);
            //prompt user to log in
            return;
        }
        if (isErrorCausedByInvalidCoupon(error)) {
            informUserWeWillProceedWithoutCoupon(error);
            return;
        }
        dataManagerErrorHandler.handleError(getActivity(), error);
        if (BookingFlowFragment.this instanceof LoginFragment) {
            getActivity().setResult(ActivityResult.LOGIN_FINISH);
            getActivity().finish();
        }
    }

    /**
     * this is gross, but we need it for logging purposes for the consolidated quote flow experiment
     * @param error
     * @return
     */
    private boolean isErrorCausedByInvalidZip(final DataManager.DataManagerError error) {
        if (error == null || error.getInvalidInputs() == null) { return false; }
        for (int i = 0; i < error.getInvalidInputs().length; i++) {
            String invalidInput = error.getInvalidInputs()[i];
            if ("zipcode".equalsIgnoreCase(invalidInput)) {
                return true;
            }
        }
        return false;
    }

    /**
     * this is gross, but
     * see documentation for DataManagerError.getErrorCode for explanation
     */
    private boolean isErrorCausedByLoginRequired(final DataManager.DataManagerError error) {
        /*
        error.getErrorCode() refers to the payload "code": 401, not the HTTP code
         */
        return error.getErrorCode() != null
               && error.getErrorCode() == 401;
    }

    private boolean isErrorCausedByInvalidCoupon(final DataManager.DataManagerError error) {
        return error != null
               && error.getMessage() != null
               && error.getMessage().toUpperCase().contains("CODE") // This is dirty but at least
               && error.getMessage().toUpperCase().contains("INVALID"); // they can finish booking
    }

    private void informUserWeWillProceedWithoutCoupon(final DataManager.DataManagerError error) {
        showToast(R.string.toast_error_booking_flow_coupon_invalid);
    }

    private void handleBookingQuoteSuccess(final BookingQuote quote, final boolean isUpdate) {
        if (!allowCallbacks) {
            return;
        }

        //note that this response is currently only being used by the consolidated quote flow
        ZipValidationResponse zipValidationResponse = quote.getZipValidationResponse();
        if (zipValidationResponse != null && zipValidationResponse.getZipArea() != null) {
            /*
            note that this is logged in BookingLocationFragment
            but logging this here to cover the consolidated quote flow experiment.
            this will be double-logged in the case that
            consolidated quote flow is off but logging department says that is OK,
            so not bothering to only log this if consolidated quote flow config is on
            because that would make this more confusing
            */
            bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingZipSuccessLog(
                    zipValidationResponse.getZipArea().getZip()
            )));
        }
        updateCurrentBookingRequest(zipValidationResponse);

        // persist extras since api doesn't return them on quote update calls
        final BookingQuote oldQuote = bookingManager.getCurrentQuote();
        if (isUpdate && oldQuote != null) {
            quote.setBookingOption(oldQuote.getBookingOption());
            quote.setSurgePriceTable(oldQuote.getSurgePriceTable());
        }
        // remove promo and reset its hidden state if new quote requested
        final BookingTransaction transaction = bookingManager.getCurrentTransaction();
        if (transaction != null && oldQuote != null && oldQuote.getBookingId()
                                                       != quote.getBookingId()) {
            //TODO for ugly promo code hotfix
            transaction.setPromoCode(null, false);
        }
        bookingManager.setCurrentQuote(quote);

        /*
        if user currently not logged in, but entered an email for an existing user,
        prompt them to log in.

        in the case that the email was not for an existing user, they will be prompted to create
        their account (enter their password) later

        note that we currently do not support allowing the user to modify their email in the booking flow
         */
        if (!userManager.isUserLoggedIn()
            && quote.getUserExistsResponse() != null
            && quote.getUserExistsResponse().exists()) {
            final Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.putExtra(LoginActivity.EXTRA_FIND_USER, true);
            intent.putExtra(LoginActivity.EXTRA_FROM_BOOKING_FUNNEL, true);
            startActivity(intent);
            return;
        }

        continueFlow();
    }

    protected void onReceiveErrorEvent(HandyEvent.ReceiveErrorEvent event) {
        removeUiBlockers();
        dataManagerErrorHandler.handleError(getActivity(), event.error);
    }
}
