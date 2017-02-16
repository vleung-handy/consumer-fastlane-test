package com.handybook.handybook.booking.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.BookingOption;
import com.handybook.handybook.booking.model.BookingRequest;
import com.handybook.handybook.booking.model.BookingTransaction;
import com.handybook.handybook.booking.ui.fragment.dialog.BookingTimeInputDialogFragment;
import com.handybook.handybook.booking.ui.view.BookingOptionsTextView;
import com.handybook.handybook.booking.ui.view.BookingOptionsView;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.constant.PrefsKey;
import com.handybook.handybook.core.ui.widget.EmailInputTextView;
import com.handybook.handybook.core.ui.widget.ZipCodeInputTextView;
import com.handybook.handybook.library.util.TextUtils;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.booking.BookingDetailsLog;
import com.handybook.handybook.logger.handylogger.model.booking.BookingFunnelLog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.handybook.handybook.booking.ui.fragment.BookingOptionsFragment.EXTRA_PAGE;
import static com.handybook.handybook.booking.ui.fragment.BookingOptionsInputFragment.EXTRA_CHILD_DISPLAY_MAP;
import static com.handybook.handybook.booking.ui.fragment.BookingOptionsInputFragment.EXTRA_IS_POST;
import static com.handybook.handybook.booking.ui.fragment.BookingOptionsInputFragment.EXTRA_OPTIONS;
import static com.handybook.handybook.booking.ui.fragment.BookingOptionsInputFragment.EXTRA_POST_OPTIONS;

/**
 * NOTE: this is for the consolidated quote flow experiment
 * not bothering to refactor extensively because it is just an experiment
 *
 * if we decide we want to keep this screen we should refactor it more
 */
public class BookingGetQuoteFragment extends BookingFlowFragment implements
        BookingDateTimeInputFragment.OnSelectedDateTimeUpdatedListener {

    private static final String DEFAULT_DATE_DISPLAY_PATTERN = "EEE, MMM d";

    @Bind(R.id.booking_zipcode_input_text)
    ZipCodeInputTextView mZipCodeInputTextView;

    @Bind(R.id.booking_email_input)
    EmailInputTextView mEmailInputTextView;

    /**
     * hidden if user already logged in
     */
    @Bind(R.id.booking_email_input_container)
    ViewGroup mBookingEmailInputContainer;

    @Bind(R.id.next_button)
    Button mNextButton;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    /**
     * only exposed for logging purposes
     */
    private BookingOptionsInputFragment mBookingOptionsInputFragment;

    public static BookingGetQuoteFragment newInstance(
            final ArrayList<BookingOption> options,
            final int page,
            final HashMap<String, Boolean> childDisplayMap,
            final ArrayList<BookingOption> postOptions,
            final boolean isPost
    ) {
        final BookingGetQuoteFragment fragment = new BookingGetQuoteFragment();
        final Bundle args = new Bundle();

        args.putParcelableArrayList(EXTRA_OPTIONS, options);
        args.putParcelableArrayList(EXTRA_POST_OPTIONS, postOptions);
        args.putSerializable(EXTRA_CHILD_DISPLAY_MAP, childDisplayMap);
        args.putInt(EXTRA_PAGE, page);
        args.putBoolean(EXTRA_IS_POST, isPost);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingServiceDetailsShownLog()));
        bus.post(new LogEvent.AddLogEvent(new BookingDetailsLog.BookingDetailsShownLog()));
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState
    ) {
        final View view = getActivity().getLayoutInflater()
                                       .inflate(
                                               R.layout.fragment_booking_get_quote,
                                               container,
                                               false
                                       );

        ButterKnife.bind(this, view);
        setupToolbar(mToolbar, getString(R.string.details));
        updateOptionsInput();
        updateDateTimeInput();
        updateZipcodeInput();
        updateEmailInput();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        /*
        handle case in which logged-out user enters existing user email address, logs in,
        and then navigates back to this screen - it will hide the email address input.
        not supporting updating email for logged-in users in the booking flow right now
         */
        updateEmailInput();
        //account for timezone updates in the case user navigates back here from subsequent flow page
        updateDateTimeInput();
    }

    private void updateOptionsInput() {
        Bundle args = getArguments();
        final int page = args.getInt(EXTRA_PAGE, 0);
        final ArrayList<BookingOption> options = args.getParcelableArrayList(
                EXTRA_OPTIONS);
        if (options == null || options.isEmpty()) {
            //no options
            return;
        }

        final ArrayList<BookingOption> postOptions
                = args.getParcelableArrayList(EXTRA_POST_OPTIONS);

        final HashMap<String, Boolean> childDisplayMap
                = (HashMap) args.getSerializable(EXTRA_CHILD_DISPLAY_MAP);

        final boolean isPost = args.getBoolean(EXTRA_IS_POST, false);

        BookingOptionsInputFragment bookingOptionsInputFragment
                = BookingOptionsInputFragment.newInstance(
                options,
                page,
                childDisplayMap,
                postOptions,
                isPost
        );
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(
                R.id.booking_options_input_fragment_container,
                bookingOptionsInputFragment,
                BookingOptionsInputFragment.TAG
        );
        transaction.commit();
        mBookingOptionsInputFragment = bookingOptionsInputFragment;
    }

    private void updateZipcodeInput() {
        bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingZipShownLog()));
        final User user = userManager.getCurrentUser();
        final User.Address address;
        if (user != null && (address = user.getAddress()) != null) {
            mZipCodeInputTextView.setText(address.getZip());
        }
        else {
            String zipCode;
            if (bookingManager.getCurrentRequest() != null
                && !TextUtils.isBlank(bookingManager.getCurrentRequest().getZipCode())) {
                zipCode = bookingManager.getCurrentRequest().getZipCode();
            }
            else {
                zipCode = mDefaultPreferencesManager.getString(PrefsKey.ZIP);
            }
            mZipCodeInputTextView.setText(zipCode);
        }
    }

    private void updateEmailInput() {
        final User user = userManager.getCurrentUser();
        if (user != null && !TextUtils.isBlank(user.getEmail())) {
            mEmailInputTextView.setText(user.getEmail());
            mBookingEmailInputContainer.setVisibility(View.GONE);
            //user cannot modify their email in booking flow, currently
        }
        else {
            String email;
            if (bookingManager.getCurrentRequest() != null
                && !TextUtils.isBlank(bookingManager.getCurrentRequest().getEmail())) {
                email = bookingManager.getCurrentRequest().getEmail();
            }
            else {
                email = mDefaultPreferencesManager.getString(PrefsKey.EMAIL);
            }
            mEmailInputTextView.setText(email);
            mBookingEmailInputContainer.setVisibility(View.VISIBLE);

        }
    }

    private void updateDateTimeInput() {
        final Calendar startDateTime = getInitialStartDateTimeWithTimeZone();
        BookingDateTimeInputFragment bookingDateTimeInputFragment
                = BookingDateTimeInputFragment.newInstance(
                startDateTime,
                DEFAULT_DATE_DISPLAY_PATTERN
        );
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(
                R.id.booking_date_time_input_fragment_container,
                bookingDateTimeInputFragment,
                BookingTimeInputDialogFragment.TAG
        );
        transaction.commit();
        updateBookingRequestDateTime(startDateTime.getTime());
        bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingSchedulerShownLog()));
    }

    private boolean areAllInputsValid() {
        return
                mZipCodeInputTextView.validate()
                && mEmailInputTextView.validate();
    }

    @OnClick(R.id.next_button)
    public void onNextButtonClicked() {
        if (!areAllInputsValid()) {
            showToast(getString(R.string.invalid_inputs));
            return;
        }
        BookingRequest currentBookingRequest = bookingManager.getCurrentRequest();
        //assumes service id is already set at this point

        //booking options already set into booking request by the fragment. just log stuff
        bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingServiceDetailsSubmittedLog()));
        if (mBookingOptionsInputFragment != null) {
            HashMap<String, BookingOptionsView> bookingOptionIdToViewMap
                    = mBookingOptionsInputFragment.optionsViewMap;
            for (BookingOptionsView view : bookingOptionIdToViewMap.values()) {
                if (view instanceof BookingOptionsTextView) {
                    bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingCommentsSubmittedLog()));
                    //the logging logic in BookingOptionsFragment posts this event for every text option
                }
            }
        }

        //booking date time already set into booking request from the update listener. just log stuff
        String formattedStartDateForApi =
                BookingRequest.BookingRequestApiSerializer.getFormattedBookingStartDate(
                        currentBookingRequest);
        bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingSchedulerSubmittedLog(
                formattedStartDateForApi)));

        //set zip code
        bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingZipSubmittedLog(
                mZipCodeInputTextView.getZipCode())));
        currentBookingRequest.setZipCode(mZipCodeInputTextView.getZipCode());

        //set email
        bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.EmailCollectedLog(
                mEmailInputTextView.getEmail()
        )));
        currentBookingRequest.setEmail(mEmailInputTextView.getEmail());

        continueBookingFlow();
    }

    /**
     * TODO may consolidate everything date-related into date fragment later.
     * not doing now because date fragment also handles rescheduling
     * and is tightly coupled to a reschedule booking
     *
     * updates the current booking request and transaction
     * with the selected date time input
     * for the booking's timezone
     */
    private void updateBookingRequestDateTime(final Date selectedDateTime) {
        final BookingRequest request = bookingManager.getCurrentRequest();
        if (request != null) {
            request.setStartDate(selectedDateTime);
        }
        final BookingTransaction transaction = bookingManager.getCurrentTransaction();
        if (transaction != null) {
            transaction.setStartDate(selectedDateTime);
        }
    }

    /**
     * mostly copied from date fragment
     *
     * TODO may consolidate everything date-related into date fragment later.
     * currently, the date fragment also handles rescheduling and many of its methods
     * are dependent on a reschedule booking so not consolidating this with it now.
     * @return
     */
    private Calendar getInitialStartDateTimeWithTimeZone() {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getDefault());
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        /*
        just in case the device default time format that we use
        specifies to display seconds or milliseconds
         */

        final Date requestDate = bookingManager.getCurrentRequest().getStartDate();
        final BookingTransaction transaction = bookingManager.getCurrentTransaction();
        Date tranDate = null;
        if (transaction != null) {
            tranDate = transaction.getStartDate();
        }
        final Date startDate = tranDate != null ? tranDate : requestDate;
        //TODO fix issue when going back for surge and date changes to initial date
        if (startDate != null) {
            cal.setTime(startDate);
        }
        else {
            // initialize date 3 days ahead with random time between 10a - 5p
            final Random random = new Random();
            cal.set(Calendar.HOUR_OF_DAY, random.nextInt(8) + 10);
            cal.set(Calendar.MINUTE, 0);
            cal.add(Calendar.DATE, 3);
            // if suggested day is on a weekend, suggest new date during the following week
            final int day = cal.get(Calendar.DAY_OF_WEEK);
            if (day == Calendar.FRIDAY || day == Calendar.SATURDAY || day == Calendar.SUNDAY) {
                if (day != Calendar.SUNDAY) {
                    cal.add(Calendar.WEEK_OF_YEAR, 1);
                }
                cal.set(Calendar.DAY_OF_WEEK, random.nextInt(4) + 2);
            }
        }

        /*
        handle case in which user navigates back to this screen after they already got a quote
        (the start date that comes back from the server and is set
        in the booking request may be in a different timezone)
         */
        if (bookingManager.getCurrentRequest() != null
            && !TextUtils.isBlank(bookingManager.getCurrentRequest().getTimeZone())) {
            cal.setTimeZone(
                    TimeZone.getTimeZone(bookingManager.getCurrentRequest().getTimeZone()));
        }
        return cal;
    }

    @Override
    protected final void disableInputs() {
        super.disableInputs();
        mNextButton.setClickable(false);
    }

    @Override
    protected final void enableInputs() {
        super.enableInputs();
        mNextButton.setClickable(true);
    }

    @Override
    public void onSelectedDateTimeUpdatedListener(final Calendar selectedDateTime) {
        updateBookingRequestDateTime(selectedDateTime.getTime());
    }
}
