package com.handybook.handybook.booking.bookingedit.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.bookingedit.BookingEditEvent;
import com.handybook.handybook.booking.bookingedit.model.BookingUpdateEntryInformationTransaction;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.BookingOption;
import com.handybook.handybook.booking.ui.fragment.BookingFlowFragment;
import com.handybook.handybook.booking.ui.view.BookingOptionsSelectView;
import com.handybook.handybook.booking.ui.view.BookingOptionsView;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.ui.widget.BasicInputTextView;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class BookingEditEntryInformationFragment extends BookingFlowFragment
{
    private final static int ENTRY_INFORMATION_BE_HOME = 0;
    private final static int ENTRY_INFORMATION_DOORMAN = 1;
    private final static int ENTRY_INFORMATION_HIDE_KEY = 2;

    private BookingUpdateEntryInformationTransaction entryInformationTransaction;

    private Booking booking;

    @Bind(R.id.options_layout)
    LinearLayout optionsLayout;
    @Bind(R.id.header_text)
    TextView headerText;
    @Bind(R.id.next_button)
    Button nextButton;
    @Bind(R.id.keys_text)
    BasicInputTextView keysText;

    private BookingOptionsView optionsView;

    public static BookingEditEntryInformationFragment newInstance(final Booking booking)
    {
        final BookingEditEntryInformationFragment fragment = new BookingEditEntryInformationFragment();
        final Bundle args = new Bundle();
        args.putParcelable(BundleKeys.BOOKING, booking);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        booking = getArguments().getParcelable(BundleKeys.BOOKING);
        initTransaction();
    }

    private void initTransaction()
    {
        entryInformationTransaction = new BookingUpdateEntryInformationTransaction();
        entryInformationTransaction.setGetInId(booking.getEntryType());
        entryInformationTransaction.setGetInText(booking.getExtraEntryInfo());
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState)
    {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_booking_edit_entry_info, container, false);
        ButterKnife.bind(this, view);
        initHeader();
        initKeysText();
        initOptionsView();
        return view;
    }

    @OnClick(R.id.next_button)
    public void onNextButtonClicked()
    {
        if (entryInformationTransaction.getGetInId() == ENTRY_INFORMATION_HIDE_KEY
                && keysText.length() <= 0)
        {
            showToast(R.string.toast_error_missing_hidden_key_note);
            return;
        }
        disableInputs();
        progressDialog.show();
        int bookingId = Integer.parseInt(booking.getId());
        bus.post(new BookingEditEvent.RequestUpdateBookingEntryInformation(bookingId, entryInformationTransaction));
    }

    private void initHeader()
    {
        final String text = getString(R.string.pro_entry_information);
        final SpannableString spanText = new SpannableString(text);
        headerText.setText(spanText, TextView.BufferType.SPANNABLE);
    }

    private void initKeysText()
    {
        keysText.setMinLength(2);
        keysText.setHint(getString(R.string.where_hide_key));
        keysText.addTextChangedListener(keyTextWatcher);
        if (booking.getExtraEntryInfo() != null && !booking.getExtraEntryInfo().isEmpty())
        {
            keysText.setText(booking.getExtraEntryInfo());
        }
    }

    private void initOptionsView()
    {
        final BookingOption option = new BookingOption();
        option.setType(BookingOption.TYPE_OPTION);

        //todo: be better if these were retrieved from the server so we can support new options in the future
        option.setOptions(new String[]{getString(R.string.will_be_home),
                getString(R.string.doorman), getString(R.string.will_hide_key)});

        optionsView = new BookingOptionsSelectView(getActivity(), option, optionUpdated);
        ((BookingOptionsSelectView) optionsView).hideTitle();

        ((BookingOptionsSelectView) optionsView).setCurrentIndex(booking.getEntryType());

        optionsLayout.addView(optionsView, 0);
    }

    @Override
    protected final void disableInputs()
    {
        super.disableInputs();
        nextButton.setClickable(false);
    }

    @Override
    protected final void enableInputs()
    {
        super.enableInputs();
        nextButton.setClickable(true);
    }

    @Subscribe
    public final void onReceiveUpdateBookingEntryInformationSuccess(BookingEditEvent.ReceiveUpdateBookingEntryInformationSuccess event)
    {
        enableInputs();
        progressDialog.dismiss();
        showToast(R.string.updated_entry_information);
        getActivity().setResult(ActivityResult.BOOKING_UPDATED, new Intent());
        getActivity().finish();
    }

    @Subscribe
    public final void onReceiveUpdateBookingEntryInformationError(BookingEditEvent.ReceiveUpdateBookingEntryInformationError event)
    {
        enableInputs();
        progressDialog.dismiss();
        dataManagerErrorHandler.handleError(getActivity(), event.error);
    }

    private final BookingOptionsView.OnUpdatedListener optionUpdated;

    {
        optionUpdated = new BookingOptionsView.OnUpdatedListener()
        {
            @Override
            public void onUpdate(final BookingOptionsView view)
            {
                //this function can be called after butterknife unbinds the views
                //TODO: need to prevent listener from being called when view is unbound
                //below line is needed to prevent NPE caused by above issue
                if (keysText == null) return;

                final int index = ((BookingOptionsSelectView) view).getCurrentIndex();

                entryInformationTransaction.setGetInId(index);

                if (index == ENTRY_INFORMATION_HIDE_KEY)
                {
                    keysText.setVisibility(View.VISIBLE);
                }
                else
                {
                    keysText.unHighlight();
                    keysText.setVisibility(View.GONE);
                }

            }

            @Override
            public void onShowChildren(final BookingOptionsView view,
                    final String[] items)
            {
            }

            @Override
            public void onHideChildren(final BookingOptionsView view,
                    final String[] items)
            {
            }
        };
    }

    private final TextWatcher keyTextWatcher = new TextWatcher()
    {
        @Override
        public void beforeTextChanged(final CharSequence charSequence, final int start,
                final int count, final int after)
        {

        }

        @Override
        public void onTextChanged(final CharSequence charSequence, final int start,
                final int before, final int count)
        {
        }

        @Override
        public void afterTextChanged(final Editable editable)
        {
            entryInformationTransaction.setGetInText(keysText.getInput());
        }
    };
}
