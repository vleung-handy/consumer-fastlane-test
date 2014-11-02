package com.handybook.handybook;

import android.content.Context;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;

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

public final class BookingRequestLocationFragment extends InjectedFragment {
    private static final String STATE_ZIP_HIGHLIGHT = "ZIP_HIGHLIGHT";

    private ProgressDialog progressDialog;
    private Toast toast;
    private boolean allowCallbacks;


    @Inject ReactiveLocationProvider locationProvider;
    @Inject UserManager userManager;
    @Inject BookingRequestManager requestManager;
    @Inject DataManager dataManager;
    @Inject DataManagerErrorHandler dataManagerErrorHandler;

    @InjectView(R.id.zip_text) ZipCodeInputTextView zipText;
    @InjectView(R.id.zip_progress) ProgressBar zipProgress;
    @InjectView(R.id.location_button) ImageButton locationButton;
    @InjectView(R.id.next_button) Button nextButton;

    static BookingRequestLocationFragment newInstance() {
        return new BookingRequestLocationFragment();
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_bookreq_location, container, false);
        ButterKnife.inject(this, view);

        toast = Toast.makeText(getActivity(), null, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setDelay(500);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));

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
    public void onStart() {
        super.onStart();
        allowCallbacks = true;
    }

    @Override
    public final void onStop() {
        super.onStop();
        allowCallbacks = false;
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

    private void disableInputs() {
        nextButton.setClickable(false);
        locationButton.setClickable(false);
        final InputMethodManager imm = (InputMethodManager)getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(zipText.getWindowToken(), 0);
    }

    private void enableInputs() {
        nextButton.setClickable(true);
        locationButton.setClickable(true);
    }

    private void updateZip() {
        locationButton.setPressed(false);
        locationButton.setVisibility(View.INVISIBLE);
        zipProgress.setVisibility(View.VISIBLE);

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

                                        zipProgress.setVisibility(View.INVISIBLE);
                                        locationButton.setVisibility(View.VISIBLE);
                                        locationButton.setPressed(true);
                                    }
                                }
                            });
                } else {
                    zipProgress.setVisibility(View.INVISIBLE);
                    locationButton.setVisibility(View.VISIBLE);
                    locationButton.setPressed(false);
                }
            }
        }, Schedulers.io());
    }

    private final View.OnClickListener nextClicked = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            if (validateFields()) {
                disableInputs();
                progressDialog.show();

                final BookingRequest request = requestManager.getCurrentRequest();

                dataManager.validateBookingZip(request.getServiceId(), zipText.getZipCode(),
                        new DataManager.Callback<Void>() {
                    @Override
                    public void onSuccess(Void v) {
                        if (!allowCallbacks) return;

                        request.setZipCode(zipText.getZipCode());
                        enableInputs();
                        progressDialog.dismiss();
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
