package com.handybook.handybook;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import uk.co.chrisjenx.calligraphy.CalligraphyTypefaceSpan;

public final class BookingConfirmationFragment extends BookingFlowFragment
        implements BaseActivity.OnBackPressedListener {
    private static final String STATE_SELECTED_PRO_ENTRY_OPTION = "SELECTED_PRO_ENTRY_OPTION";

    private BookingOptionsSelectView optionsView;

    @InjectView(R.id.options_layout) LinearLayout optionsLayout;
    @InjectView(R.id.header_text) TextView headerText;
    @InjectView(R.id.next_button) Button nextButton;

    static BookingConfirmationFragment newInstance() {
        final BookingConfirmationFragment fragment = new BookingConfirmationFragment();
        return fragment;
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_booking_confirmation, container, false);

        ButterKnife.inject(this, view);

        final String text = getString(R.string.payment_confirmed);
        final SpannableString spanText = new SpannableString(text);

        spanText.setSpan(new CalligraphyTypefaceSpan(Typefaces.get(getActivity(),
                        "CircularStd-Medium.otf")), 0, text.indexOf("\n"),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        headerText.setText(spanText, TextView.BufferType.SPANNABLE);

        final BookingOption option = new BookingOption();
        option.setType("option");
        option.setDefaultValue("0");

        option.setOptions(new String[] { getString(R.string.will_be_home),
                getString(R.string.doorman), getString(R.string.will_hide_key)});

        optionsView = new BookingOptionsSelectView(getActivity(), option);
        optionsView.hideTitle();
        optionsLayout.addView(optionsView, 0);

        if (savedInstanceState != null) optionsView.setCurrentIndex(savedInstanceState
                .getInt(STATE_SELECTED_PRO_ENTRY_OPTION, 0));

        //TODO add next listener to show comments page
        //TODO add password page if new user
        //TODO clear booking manager after completeing booking

        return view;
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_PRO_ENTRY_OPTION, optionsView.getCurrentIndex());
    }

    @Override
    public final void onStart() {
        super.onStart();
        ((BaseActivity)getActivity()).setOnBackPressedListener(this);
    }

    @Override
    public final void onStop() {
        super.onStop();
        ((BaseActivity)getActivity()).setOnBackPressedListener(null);
    }

    @Override
    protected final void disableInputs() {
        super.disableInputs();
        nextButton.setClickable(false);
    }

    @Override
    protected final void enableInputs() {
        super.enableInputs();
        nextButton.setClickable(true);
    }

    @Override
    public final void onBack() {
        final Intent intent = new Intent(getActivity(), BookingsActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
    }
}
