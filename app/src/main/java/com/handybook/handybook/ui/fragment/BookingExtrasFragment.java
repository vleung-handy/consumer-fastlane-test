package com.handybook.handybook.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.handybook.handybook.R;
import com.handybook.handybook.core.BookingOption;
import com.handybook.handybook.core.BookingQuote;
import com.handybook.handybook.core.BookingTransaction;
import com.handybook.handybook.data.SecurePreferences;
import com.handybook.handybook.ui.widget.BookingOptionsSelectView;
import com.handybook.handybook.ui.widget.BookingOptionsView;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class BookingExtrasFragment extends BookingFlowFragment {
    private BookingTransaction bookingTransaction;
    private BookingQuote bookingQuote;

    @Inject SecurePreferences securePrefs;

    @Bind(R.id.options_layout)
    LinearLayout optionsLayout;
    @Bind(R.id.next_button)
    Button nextButton;

    public static BookingExtrasFragment newInstance() {
        final BookingExtrasFragment fragment = new BookingExtrasFragment();
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bookingTransaction = bookingManager.getCurrentTransaction();
        bookingQuote = bookingManager.getCurrentQuote();
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_booking_extras, container, false);

        ButterKnife.bind(this, view);

        final BookingHeaderFragment header = new BookingHeaderFragment();
        final FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.info_header_layout, header).commit();

        final BookingOptionsSelectView optionsView = new BookingOptionsSelectView(getActivity(),
                bookingQuote.getExtrasOptions(), optionUpdated);

        optionsView.hideTitle();

        final String selected = securePrefs.getString("STATE_BOOKING_CLEANING_EXTRAS_SEL");
        if (selected != null) {
            final String[] indexes = selected.split(",");
            final ArrayList<Integer> checked = new ArrayList<>();

            for (int i = 0; i < indexes.length; i++) {
                try { checked.add(Integer.parseInt(indexes[i])); }
                catch (final NumberFormatException e) {}
            }

            optionsView.setCheckedIndexes(checked.toArray(new Integer[checked.size()]));
        }

        optionsLayout.addView(optionsView, 0);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
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
            final BookingOption option = bookingQuote.getExtrasOptions();
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
            securePrefs.put("STATE_BOOKING_CLEANING_EXTRAS_SEL", selected);
            bookingTransaction.setExtraHours(extraHours);
            bookingTransaction.setExtraCleaningText(extraText.length() > 0 ? extraText : null);
        }

        @Override
        public void onShowChildren(final BookingOptionsView view,
                                   final String[] items) {
        }

        @Override
        public void onHideChildren(final BookingOptionsView view,
                                   final String[] items) {
        }
    };
}
