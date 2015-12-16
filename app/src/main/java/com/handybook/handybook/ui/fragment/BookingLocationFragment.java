package com.handybook.handybook.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.handybook.handybook.R;
import com.handybook.handybook.core.BookingOption;
import com.handybook.handybook.core.BookingOptionsWrapper;
import com.handybook.handybook.core.BookingRequest;
import com.handybook.handybook.core.User;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.ui.activity.BaseActivity;
import com.handybook.handybook.ui.activity.BookingDateActivity;
import com.handybook.handybook.ui.activity.BookingOptionsActivity;
import com.handybook.handybook.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.ui.widget.InputTextField;
import com.handybook.handybook.ui.widget.ZipCodeInputTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class BookingLocationFragment extends BookingFlowFragment
        implements BaseActivity.OnBackPressedListener {
    private static final String STATE_ZIP_HIGHLIGHT = "ZIP_HIGHLIGHT";

    private boolean isPromoFlow;

    @Bind(R.id.zip_text)
    ZipCodeInputTextView zipText;
    @Bind(R.id.next_button)
    Button nextButton;

    public static BookingLocationFragment newInstance() {
        return new BookingLocationFragment();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mixpanel.trackEventAppTrackLocation();

        final BookingRequest request = bookingManager.getCurrentRequest();
        if ((isPromoFlow = request.getPromoCode() != null))
        {
            ((BaseActivity) getActivity()).setOnBackPressedListener(this);
        }
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState)
    {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_booking_location, container, false);

        ButterKnife.bind(this, view);

        final User.Address address;
        final User user = userManager.getCurrentUser();
        if (user != null && (address = user.getAddress()) != null)
        {
            zipText.setText(address.getZip());
        }

        nextButton.setOnClickListener(nextClicked);

        return view;
    }

    @Override
    public final void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null)
        {
            if (savedInstanceState.getBoolean(STATE_ZIP_HIGHLIGHT)) zipText.highlight();
        }
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_ZIP_HIGHLIGHT, zipText.isHighlighted());
    }

    private boolean validateFields()
    {
        boolean validate = true;
        if (!zipText.validate()) validate = false;
        return validate;
    }

    @Override
    protected void disableInputs()
    {
        super.disableInputs();
        nextButton.setClickable(false);
        final InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(zipText.getWindowToken(), 0);
    }

    @Override
    protected final void enableInputs()
    {
        super.enableInputs();
        nextButton.setClickable(true);
    }

    @Override
    public final void onBack()
    {
        final Intent intent = new Intent(getActivity(), ServiceCategoriesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private final View.OnClickListener nextClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(final View view)
        {
            if (validateFields())
            {
                disableInputs();
                progressDialog.show();

                final BookingRequest request = bookingManager.getCurrentRequest();
                final User user = userManager.getCurrentUser();
                final String userId = user != null ? user.getId() : null;
                final String authToken = user != null ? user.getAuthToken() : null;


                dataManager.validateBookingZip(request.getServiceId(), zipText.getZipCode(), userId,
                        authToken, request.getPromoCode(), new DataManager.Callback<Void>()
                        {
                            @Override
                            public void onSuccess(Void v)
                            {
                                final BookingRequest request = bookingManager.getCurrentRequest();
                                request.setZipCode(zipText.getZipCode());
                                mixpanel.trackEventWhenPage(request);

                                if (!allowCallbacks) return;
                                enableInputs();
                                progressDialog.dismiss();

                                if (!isPromoFlow) displayBookingOptions();
                                else
                                {
                                    final Intent intent = new Intent(getActivity(), BookingDateActivity.class);
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onError(final DataManager.DataManagerError error)
                            {
                                if (!allowCallbacks) return;

                                enableInputs();
                                progressDialog.dismiss();

                                final HashMap<String, InputTextField> inputMap = new HashMap<>();
                                inputMap.put("zipcode", zipText);
                                dataManagerErrorHandler.handleError(getActivity(), error, inputMap);
                            }
                        });
            }
        }
    };

    private void displayBookingOptions() {
        final BookingRequest request = bookingManager.getCurrentRequest();

        String userId = null;
        final User user = userManager.getCurrentUser();
        if (user != null) userId = user.getId();

        dataManager.getQuoteOptions(request.getServiceId(), userId,
                new DataManager.Callback<BookingOptionsWrapper>()
                {
                    @Override
                    public void onSuccess(final BookingOptionsWrapper options)
                    {
                        if (!allowCallbacks) return;

                        List<BookingOption> bookingOptions = options.getBookingOptions();

                        final Intent intent = new Intent(getActivity(), BookingOptionsActivity.class);
                        intent.putParcelableArrayListExtra(BookingOptionsActivity.EXTRA_OPTIONS, new ArrayList<>(bookingOptions));
                        intent.putExtra(BookingOptionsActivity.EXTRA_PAGE, 0);
                        startActivity(intent);

                        enableInputs();
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error)
                    {
                        if (!allowCallbacks) return;
                        enableInputs();
                        progressDialog.dismiss();
                        dataManagerErrorHandler.handleError(getActivity(), error);
                    }
                });
    }

}
