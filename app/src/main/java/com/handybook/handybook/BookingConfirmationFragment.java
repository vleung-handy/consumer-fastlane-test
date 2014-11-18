package com.handybook.handybook;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
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
    static final String EXTRA_IS_LAST = "com.handy.handy.EXTRA_IS_LAST";
    static final String EXTRA_ASK_PWD = "com.handy.handy.EXTRA_ASK_PWD";
    private static final String STATE_KEY_HIGHLIGHT = "KEY_HIGHLIGHT";

    private BookingOptionsView optionsView;
    private BookingPostInfo postInfo;
    private boolean isLast;

    @InjectView(R.id.options_layout) LinearLayout optionsLayout;
    @InjectView(R.id.header_text) TextView headerText;
    @InjectView(R.id.next_button) Button nextButton;
    @InjectView(R.id.keys_text) BasicInputTextView keysText;

    static BookingConfirmationFragment newInstance(final boolean isLast) {
        final BookingConfirmationFragment fragment = new BookingConfirmationFragment();
        final Bundle args = new Bundle();
        args.putBoolean(EXTRA_IS_LAST, isLast);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isLast = getArguments().getBoolean(EXTRA_IS_LAST, false);
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_booking_confirmation, container, false);

        ButterKnife.inject(this, view);

        keysText.setMinLength(2);
        keysText.setHint(getString(R.string.where_hide_key));
        keysText.addTextChangedListener(keyTextWatcher);

        postInfo = bookingManager.getCurrentPostInfo();

        if (isLast) {
            headerText.setText(getString(R.string.pro_to_know));
            nextButton.setText(getString(R.string.finish));

            final BookingOption option = new BookingOption();
            option.setType("text");
            option.setDefaultValue(getString(R.string.additional_pro_info));

            optionsView = new BookingOptionsTextView(getActivity(), option, textUpdated);
            ((BookingOptionsTextView)optionsView).setValue(postInfo.getExtraMessage());
        }
        else {
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

            optionsView = new BookingOptionsSelectView(getActivity(), option, optionUpdated);
            ((BookingOptionsSelectView)optionsView).hideTitle();
            ((BookingOptionsSelectView)optionsView).setCurrentIndex(postInfo.getGetInId());
        }

        optionsLayout.addView(optionsView, 0);
        nextButton.setOnClickListener(nextClicked);
        return view;
    }

    @Override
    public final void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(STATE_KEY_HIGHLIGHT)) keysText.highlight();
        }
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_KEY_HIGHLIGHT, keysText.isHighlighted());
    }

    @Override
    public final void onStart() {
        super.onStart();
        if (!isLast) ((BaseActivity)getActivity()).setOnBackPressedListener(this);
    }

    @Override
    public final void onStop() {
        super.onStop();
        if (!isLast) ((BaseActivity)getActivity()).setOnBackPressedListener(null);
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

    private boolean validateFields() {
        boolean validate = true;

        if (!isLast && ((BookingOptionsSelectView)optionsView).getCurrentIndex() == 2 && !keysText.validate())
            validate = false;

        return validate;
    }

    @Override
    public final void onBack() {
        if (!isLast) showBookings();
    }

    private final View.OnClickListener nextClicked = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            if (!validateFields()) return;

            if (!isLast) {
                final Intent intent = new Intent(getActivity(), BookingConfirmationActivity.class);
                intent.putExtra(BookingConfirmationActivity.EXTRA_IS_LAST, true);
                startActivity(intent);
            }
            else {
                //TODO show pwd if view new user otherwise show bookings after posting
                dataManager.addBookingPostInfo(bookingManager.getCurrentTransaction().getBookingId(),
                        postInfo, new DataManager.Callback<Void>() {
                    @Override
                    public void onSuccess(final Void response) {
                        if (!allowCallbacks) return;
                        showBookings();
                        enableInputs();
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error) {
                        if (!allowCallbacks) return;

                        enableInputs();
                        progressDialog.dismiss();
                        dataManagerErrorHandler.handleError(getActivity(), error);
                    }
                });
            }
        }
    };

    private void showBookings() {
        bookingManager.setCurrentRequest(null);

        final Intent intent = new Intent(getActivity(), BookingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private final BookingOptionsView.OnUpdatedListener textUpdated
            = new BookingOptionsView.OnUpdatedListener() {
        @Override
        public void onUpdate(final BookingOptionsView view) {
            postInfo.setExtraMessage(view.getCurrentValue());
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

    private final BookingOptionsView.OnUpdatedListener optionUpdated
            = new BookingOptionsView.OnUpdatedListener() {
        @Override
        public void onUpdate(final BookingOptionsView view) {
            final int index = ((BookingOptionsSelectView) view).getCurrentIndex();
            postInfo.setGetInId(index);

            if (index == 2) keysText.setVisibility(View.VISIBLE);
            else {
                keysText.unHighlight();
                keysText.setVisibility(View.GONE);
            }
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

    private final TextWatcher keyTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(final CharSequence charSequence, final int start,
                                      final int count, final int after) {

        }

        @Override
        public void onTextChanged(final CharSequence charSequence, final int start,
                                  final int before, final int count) {
        }

        @Override
        public void afterTextChanged(final Editable editable) {
            postInfo.setGetInText(keysText.getInput());
        }
    };
}
