package com.handybook.handybook.booking.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.BookingOption;
import com.handybook.handybook.booking.model.BookingQuote;
import com.handybook.handybook.booking.model.BookingTransaction;
import com.handybook.handybook.booking.ui.view.BookingOptionsSelectView;
import com.handybook.handybook.booking.ui.view.BookingOptionsView;
import com.handybook.handybook.core.constant.PrefsKey;
import com.handybook.handybook.core.manager.SecurePreferencesManager;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.booking.BookingFunnelLog;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class BookingExtrasFragment extends BookingFlowFragment {

    private BookingTransaction mBookingTransaction;
    private BookingQuote mBookingQuote;
    private String[] options;

    @Inject
    SecurePreferencesManager mSecurePreferencesManager;
    @Bind(R.id.options_layout)
    LinearLayout mOptionsLayout;
    @Bind(R.id.next_button)
    Button mNextButton;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    public static BookingExtrasFragment newInstance() {
        return new BookingExtrasFragment();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBookingTransaction = bookingManager.getCurrentTransaction();
        mBookingQuote = bookingManager.getCurrentQuote();
        options = mBookingQuote.getBookingOption().getOptions();
        if (options != null && options.length != 0) {
            bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingExtrasShownLog(options)));
        }
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState
    ) {
        final View view = getActivity().getLayoutInflater()
                                       .inflate(R.layout.fragment_booking_extras, container, false);

        ButterKnife.bind(this, view);
        setupToolbar(mToolbar, getString(R.string.extras));

        final BookingHeaderFragment header = new BookingHeaderFragment();
        final FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.info_header_layout, header).commit();

        final BookingOptionsSelectView optionsView = new BookingOptionsSelectView(
                getActivity(),
                mBookingQuote.getBookingOption(),
                optionUpdated
        );

        optionsView.hideTitle();

        final String selected = mSecurePreferencesManager.getString(
                PrefsKey.STATE_BOOKING_CLEANING_EXTRAS_SELECTION
        );
        if (selected != null) {
            final String[] indexes = selected.split(",");
            final ArrayList<Integer> checked = new ArrayList<>();
            for (String eachIndex : indexes) {
                try {
                    checked.add(Integer.parseInt(eachIndex));
                }
                catch (final NumberFormatException e) {
                    Crashlytics.logException(e);
                }
            }
            optionsView.setCheckedIndexes(checked.toArray(new Integer[checked.size()]));
        }

        mOptionsLayout.addView(optionsView, 0);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (options != null) {
                    bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingExtrasSubmittedLog(
                            options)));
                }
                continueBookingFlow();
            }
        });
        return view;
    }

    private final BookingOptionsView.OnUpdatedListener optionUpdated
            = new BookingOptionsView.OnUpdatedListener() {
        @Override
        public void onUpdate(final BookingOptionsView view) {
            final Integer[] indexes = ((BookingOptionsSelectView) view).getCheckedIndexes();
            final BookingOption option = mBookingQuote.getBookingOption();
            final float[] hoursMap = option.getHoursInfo();
            final String[] options = option.getOptions();

            float extraHours = 0;
            String selected = "";
            String extraText = "";

            int j = 0;
            for (final int i : indexes) {
                selected += i + ",";
                extraHours += hoursMap[i];
                extraText += options[i] + (j == indexes.length - 1 ? "" : ", ");
                j++;
            }

            //TODO integrate put call into booking manager like promo tab coupon
            mSecurePreferencesManager.setString(
                    PrefsKey.STATE_BOOKING_CLEANING_EXTRAS_SELECTION,
                    selected
            );
            mBookingTransaction.setExtraHours(extraHours);
            mBookingTransaction.setExtraCleaningText(extraText.length() > 0 ? extraText : null);
        }

        @Override
        public void onShowChildren(final BookingOptionsView view, final String[] items) {
        }

        @Override
        public void onHideChildren(final BookingOptionsView view, final String[] items) {
        }
    };
}
