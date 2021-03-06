package com.handybook.handybook.booking.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.google.common.base.Strings;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.BookingRequest;
import com.handybook.handybook.booking.model.ZipValidationResponse;
import com.handybook.handybook.booking.ui.activity.BookingDateActivity;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.callback.FragmentSafeCallback;
import com.handybook.handybook.core.ui.activity.BaseActivity;
import com.handybook.handybook.core.ui.widget.ZipCodeInputTextView;
import com.handybook.handybook.library.ui.view.InputTextField;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.booking.BookingFunnelLog;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class BookingLocationFragment extends BookingFlowFragment
        implements BaseActivity.OnBackPressedListener {

    private static final String STATE_ZIP_HIGHLIGHT = "ZIP_HIGHLIGHT";
    private static final String KEY_ZIPCODE = "zipcode";

    private boolean isPromoFlow;

    @BindView(R.id.zip_text)
    ZipCodeInputTextView mZipCodeInputTextView;
    @BindView(R.id.next_button)
    Button mNextButton;

    public static BookingLocationFragment newInstance(@Nullable final Bundle extras) {
        BookingLocationFragment fragment = new BookingLocationFragment();
        fragment.setArguments(extras);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final BookingRequest request = bookingManager.getCurrentRequest();
        if (isPromoFlow = request.getPromoCode() != null) {
            ((BaseActivity) getActivity()).setOnBackPressedListener(this);
        }

        bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingZipShownLog()));
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState
    ) {
        ViewGroup view = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        view.addView(inflater.inflate(R.layout.fragment_booking_location, container, false));

        ButterKnife.bind(this, view);

        setupToolbar(mToolbar, getString(R.string.location));
        final User user = userManager.getCurrentUser();
        final User.Address address;
        if (user != null && (address = user.getAddress()) != null) {
            mZipCodeInputTextView.setText(address.getZip());
        }
        mNextButton.setOnClickListener(mOnNextClickListener);
        return view;
    }

    @Override
    public final void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(STATE_ZIP_HIGHLIGHT)) {
                mZipCodeInputTextView.highlight();
            }
        }
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_ZIP_HIGHLIGHT, mZipCodeInputTextView.isHighlighted());
    }

    private boolean validateFields() {
        return mZipCodeInputTextView.validate();
    }

    @Override
    protected void disableInputs() {
        super.disableInputs();
        mNextButton.setClickable(false);
        final InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mZipCodeInputTextView.getWindowToken(), 0);
    }

    @Override
    protected final void enableInputs() {
        super.enableInputs();
        mNextButton.setClickable(true);
    }

    @Override
    public final void onBack() {
        final Intent intent = new Intent(getActivity(), ServiceCategoriesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private final View.OnClickListener mOnNextClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            if (validateFields()) {
                String zipCode = mZipCodeInputTextView.getZipCode();
                if (!Strings.isNullOrEmpty(zipCode)) {
                    bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingZipSubmittedLog(
                            zipCode)));
                }

                disableInputs();
                showProgressSpinner(true);

                final BookingRequest request = bookingManager.getCurrentRequest();
                final User user = userManager.getCurrentUser();
                final String userId = user != null ? user.getId() : null;

                dataManager.validateBookingZip(
                        request.getServiceId(),
                        zipCode,
                        userId,
                        request.getPromoCode(),
                        new FragmentSafeCallback<ZipValidationResponse>(BookingLocationFragment.this) {
                            @Override
                            public void onCallbackSuccess(ZipValidationResponse response) {
                                String zipCode = mZipCodeInputTextView.getZipCode();
                                if (!Strings.isNullOrEmpty(zipCode)) {
                                    bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingZipSuccessLog(
                                            zipCode)));
                                }

                                final BookingRequest request = bookingManager.getCurrentRequest();
                                request.setZipCode(zipCode);
                                request.setZipArea(response.getZipArea());
                                request.setTimeZone(response.getTimeZone());
                                if (!allowCallbacks) { return; }
                                enableInputs();
                                hideProgressSpinner();
                                if (!isPromoFlow) { displayBookingOptions(); }
                                else {
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
                                String zipCode = mZipCodeInputTextView.getZipCode();
                                if (!Strings.isNullOrEmpty(zipCode)) {
                                    bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingZipErrorLog(
                                            zipCode, error.getMessage())));
                                }

                                if (!allowCallbacks) { return; }
                                enableInputs();
                                hideProgressSpinner();
                                final HashMap<String, InputTextField> inputMap = new HashMap<>();
                                inputMap.put(KEY_ZIPCODE, mZipCodeInputTextView);
                                dataManagerErrorHandler.handleError(getActivity(), error, inputMap);
                            }
                        }
                );
            }
        }
    };


}
