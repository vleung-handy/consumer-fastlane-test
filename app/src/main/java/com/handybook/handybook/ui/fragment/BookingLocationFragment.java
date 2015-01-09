package com.handybook.handybook.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.google.android.gms.location.LocationRequest;
import com.handybook.handybook.R;
import com.handybook.handybook.core.BookingOption;
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
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public final class BookingLocationFragment extends BookingFlowFragment
        implements BaseActivity.OnBackPressedListener {
    private static final String STATE_ZIP_HIGHLIGHT = "ZIP_HIGHLIGHT";

    private boolean isPromoFlow;

    @Inject ReactiveLocationProvider locationProvider;
    
    @InjectView(R.id.zip_text) ZipCodeInputTextView zipText;
    @InjectView(R.id.zip_progress) ProgressBar zipProgress;
    @InjectView(R.id.location_button) ImageButton locationButton;
    @InjectView(R.id.next_button) Button nextButton;

    public static BookingLocationFragment newInstance() {
        return new BookingLocationFragment();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final BookingRequest request = bookingManager.getCurrentRequest();
        if ((isPromoFlow = request.getPromoCode() != null)) {
            ((BaseActivity)getActivity()).setOnBackPressedListener(this);
        }
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_booking_location,container, false);

        ButterKnife.inject(this, view);

        final User.Address address;
        final User user = userManager.getCurrentUser();
        if (user != null && (address = user.getAddress()) != null) {
            zipText.setText(address.getZip());
        }

        zipText.addTextChangedListener(zipTextWatcher);
        nextButton.setOnClickListener(nextClicked);

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                zipText.unHighlight();
                updateZip();
            }
        });

        return view;
    }

    @Override
    public final void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(STATE_ZIP_HIGHLIGHT)) zipText.highlight();
        }
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_ZIP_HIGHLIGHT, zipText.isHighlighted());
    }

    private boolean validateFields() {
        boolean validate = true;
        if (!zipText.validate()) validate = false;
        return validate;
    }

    @Override
    protected void disableInputs() {
        super.disableInputs();
        nextButton.setClickable(false);
        locationButton.setClickable(false);
        final InputMethodManager imm = (InputMethodManager)getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(zipText.getWindowToken(), 0);
    }

    @Override
    protected final void enableInputs() {
        super.enableInputs();
        nextButton.setClickable(true);
        locationButton.setClickable(true);
    }

    @Override
    public final void onBack() {
        final Intent intent = new Intent(getActivity(), ServiceCategoriesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void showLocationProgress(final boolean display, final boolean showPressed) {
        if (display) {
            locationButton.setPressed(false);
            locationButton.setVisibility(View.INVISIBLE);
            zipProgress.setVisibility(View.VISIBLE);
        }
        else {
            zipProgress.setVisibility(View.INVISIBLE);
            locationButton.setVisibility(View.VISIBLE);
            locationButton.setPressed(showPressed);
        }
    }

    private void updateZip() {
        showLocationProgress(true, false);

        final Observable<Location> locationObservable = locationProvider
                .getUpdatedLocation(LocationRequest.create()
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .setExpirationDuration(TimeUnit.SECONDS.toMillis(5)).setInterval(100))
                .filter(new Func1<Location, Boolean>() {
                    @Override
                    public Boolean call(final Location location) {
                        return location.getAccuracy() <= 1000;
                    }
                })
                .timeout(5, TimeUnit.SECONDS, Observable.from((Location) null),
                        AndroidSchedulers.mainThread())
                .first().observeOn(AndroidSchedulers.mainThread());

        locationObservable.subscribe(new Action1<Location>() {
            @Override
            public void call(final Location location) {
                locationObservable.unsubscribeOn(Schedulers.io());
                if (!allowCallbacks) return;

                if (location != null) {
                    final Observable<List<Address>> geocodeObservable = locationProvider
                            .getGeocodeObservable(location.getLatitude(),
                                    location.getLongitude(), 1);

                    geocodeObservable
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<List<Address>>() {
                                @Override
                                public void call(final List<Address> addresses) {
                                    geocodeObservable.unsubscribeOn(Schedulers.io());
                                    if (!allowCallbacks) return;

                                    if (addresses.size() > 0) {
                                        final String zip = addresses.get(0).getPostalCode();
                                        zipText.setText(zip);
                                        zipText.setSelection(zip.length());
                                        showLocationProgress(false, true);
                                    }
                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(final Throwable throwable) {
                                    showLocationProgress(false, false);
                                }
                            });
                } else showLocationProgress(false, false);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(final Throwable throwable) { showLocationProgress(false, false); }
        }, Schedulers.io());
    }

    private final View.OnClickListener nextClicked = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            if (validateFields()) {
                disableInputs();
                progressDialog.show();

                final BookingRequest request = bookingManager.getCurrentRequest();
                final User user = userManager.getCurrentUser();
                final String userId = user != null ? user.getId() : null;
                final String authToken = user != null ? user.getAuthToken() : null;


                dataManager.validateBookingZip(request.getServiceId(), zipText.getZipCode(), userId,
                        authToken, request.getPromoCode(), new DataManager.Callback<Void>() {
                    @Override
                    public void onSuccess(Void v) {
                        final BookingRequest request = bookingManager.getCurrentRequest();
                        request.setZipCode(zipText.getZipCode());
                        mixpanel.trackEventWhenPage();

                        if (!allowCallbacks) return;
                        enableInputs();
                        progressDialog.dismiss();

                        if (!isPromoFlow) displayBookingOptions();
                        else {
                            final Intent intent = new Intent(getActivity(), BookingDateActivity.class);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error) {
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

        dataManager.getBookingOptions(request.getServiceId(), userId,
                new DataManager.Callback<List<BookingOption>>() {
            @Override
            public void onSuccess(final List<BookingOption> options) {
                if (!allowCallbacks) return;

                final Intent intent = new Intent(getActivity(), BookingOptionsActivity.class);
                intent.putParcelableArrayListExtra(BookingOptionsActivity.EXTRA_OPTIONS,
                        new ArrayList<>(options));
                intent.putExtra(BookingOptionsActivity.EXTRA_PAGE, 0);
                startActivity(intent);

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

    private final TextWatcher zipTextWatcher = new TextWatcher() {
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
            locationButton.setPressed(false);
        }
    };
}