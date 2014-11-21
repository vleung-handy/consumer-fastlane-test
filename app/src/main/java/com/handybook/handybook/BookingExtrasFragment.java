package com.handybook.handybook;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class BookingExtrasFragment extends BookingFlowFragment {
    private BookingTransaction bookingTransaction;

    @Inject SecurePreferences securePrefs;

    @InjectView(R.id.options_layout) LinearLayout optionsLayout;
    @InjectView(R.id.next_button) Button nextButton;

    static BookingExtrasFragment newInstance() {
        final BookingExtrasFragment fragment = new BookingExtrasFragment();
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bookingTransaction = bookingManager.getCurrentTransaction();
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_booking_extras, container, false);

        ButterKnife.inject(this, view);

        final BookingHeaderFragment header = new BookingHeaderFragment();
        final FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.info_header_layout, header).commit();

        final BookingOptionsSelectView optionsView = new BookingOptionsSelectView(getActivity(),
                bookingManager.getCurrentQuote().getExtrasOptions(), optionUpdated);

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
//
        optionsLayout.addView(optionsView, 0);
//
//        nextButton.setOnClickListener(nextClicked);
        return view;
    }

//    @Override
//    protected final void disableInputs() {
//        super.disableInputs();
//        nextButton.setClickable(false);
//    }
//
//    @Override
//    protected final void enableInputs() {
//        super.enableInputs();
//        nextButton.setClickable(true);
//    }
//
//    private final View.OnClickListener nextClicked = new View.OnClickListener() {
//        @Override
//        public void onClick(final View view) {
//            continueBookingFlow();
//        }
//    };
//
    private final BookingOptionsView.OnUpdatedListener optionUpdated
            = new BookingOptionsView.OnUpdatedListener() {
        @Override
        public void onUpdate(final BookingOptionsView view) {
            final Integer[] indexes = ((BookingOptionsSelectView) view).getCheckedIndexes();
            String selected = "";

            for (final int i : indexes) selected += i + ",";
            securePrefs.put("STATE_BOOKING_CLEANING_EXTRAS_SEL", selected);
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
//
//    private String[] getSavingsInfo() {
//        final String[] info = new String[4];
//        final BookingQuote quote = bookingManager.getCurrentQuote();
//        final float hours = bookingTransaction.getHours();
//        final float prices[] = quote.getPricing(hours, 0);
//        final float price = prices[0];
//        final float discount = prices[1];
//
//        for (int i = 1; i < 4; i++) {
//            final float recurPrices[] = quote.getPricing(hours, i);
//            final float recurPrice = recurPrices[0];
//            final float recurDiscount = recurPrices[1];
//
//            int percent;
//            if (recurPrice != recurDiscount)
//                percent = Math.round((discount - recurDiscount) / discount * 100);
//            else percent = Math.round((price - recurPrice) / price * 100);
//
//            if (percent > 0) info[i - 1] = getString(R.string.save) + " " + percent + "%";
//        }
//        return info;
//    }
}
