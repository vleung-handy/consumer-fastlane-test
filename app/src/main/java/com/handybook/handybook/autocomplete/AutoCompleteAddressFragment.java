package com.handybook.handybook.autocomplete;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v7.widget.ListPopupWindow;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.ZipValidationResponse;
import com.handybook.handybook.configuration.model.Configuration;
import com.handybook.handybook.core.ui.widget.StreetAddressInputTextView;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.library.util.UiUtils;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.booking.AddressAutocompleteLog;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * This is a fragment that supports autocomplete of address fields.
 */
public class AutoCompleteAddressFragment extends InjectedFragment {

    private static final int DEBOUNCE_DELAY = 250;
    //the delay before we fire a request to address autocomplete
    private static final String KEY_FILTER = "filter";
    private static final String KEY_ADDR1 = "address1";
    private static final String KEY_ADDR2 = "address2";
    private static final String KEY_CITY = "city";
    private static final String KEY_STATE = "state";
    private static final String KEY_CONFIGURATION = "configuration";

    @Bind(R.id.autocomplete_address_text_street)
    private StreetAddressInputTextView mStreet;
    @Bind(R.id.autocomplete_address_text_other)
    private EditText mApt;
    @Bind(R.id.autocomplete_address_text_city)
    private EditText mCity;
    @Bind(R.id.autocomplete_address_text_state)
    private EditText mState;

    @Inject
    AddressAutoCompleteManager mAutoCompleteManager;

    protected ListPopupWindow mListPopupWindow;
    Subscription mSubscription;

    List<String> mPredictionValues;
    List<PlacePrediction> mPredictions;

    ZipValidationResponse.ZipArea mZipFilter = null;
    Configuration mConfiguration;

    @NonNull
    public static AutoCompleteAddressFragment newInstance(
            final ZipValidationResponse.ZipArea filter,
            final String address1,
            final String address2,
            final String city,
            final String state,
            final Configuration config
    ) {
        Bundle args = new Bundle();
        args.putSerializable(KEY_FILTER, filter);
        args.putString(KEY_ADDR1, address1);
        args.putString(KEY_ADDR2, address2);
        args.putString(KEY_CITY, city);
        args.putString(KEY_STATE, state);
        args.putSerializable(KEY_CONFIGURATION, config);

        AutoCompleteAddressFragment fragment = new AutoCompleteAddressFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_auto_complete_address, container, false);
        ButterKnife.bind(this, view);

        if (getArguments() != null) {
            mZipFilter = (ZipValidationResponse.ZipArea) getArguments().getSerializable(KEY_FILTER);
            mStreet.setText(getArguments().getString(KEY_ADDR1));
            mApt.setText(getArguments().getString(KEY_ADDR2));
            mConfiguration = (Configuration) getArguments().getSerializable(KEY_CONFIGURATION);
        }

        if (mConfiguration != null && mConfiguration.isAddressAutoCompleteEnabled()) {
            mListPopupWindow = new ListPopupWindow(getActivity());
            mListPopupWindow.setAnchorView(mStreet);
            mListPopupWindow.setWidth(ListPopupWindow.MATCH_PARENT);
            mListPopupWindow.setModal(false);
            mListPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mSubscription.unsubscribe();

                    String prediction = mPredictions.get(position).getAddress();
                    bus.post(new LogEvent.AddLogEvent(
                            new AddressAutocompleteLog.AddressAutocompleteItemTappedLog(prediction)
                    ));

                    mStreet.setText(prediction);
                    mStreet.setSelection(mStreet.getText().length());
                    mListPopupWindow.dismiss();
                    hideKeyboard();

                    //skipping the first element fired, because it is triggered by the "setText" above.
                    subscribe();
                }
            });

            subscribe();
        }
        return view;
    }

    @NonNull
    public String getStreet() {
        return mStreet.getAddress();
    }

    @NonNull
    public String getApt() {
        return mApt.getText().toString();
    }

    @NonNull
    public String getCity() {
        return mCity.getText().toString();
    }

    @NonNull
    public String getState() {
        return mState.getText().toString();
    }

    private void subscribe() {
        mSubscription = RxTextView
                .textChanges(mStreet)
                .debounce(DEBOUNCE_DELAY, TimeUnit.MILLISECONDS)
                .skip(1)
                .flatMap(new Func1<CharSequence, Observable<List<String>>>() {
                    @Override
                    public Observable<List<String>> call(CharSequence charSequence) {
                        return Observable.just(makeApiCall(charSequence.toString()));
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<String>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(final Throwable e) {
                        //sometimes, we get an RetrofitError: thread interrupted, in which
                        //case this stream terminates. Fail silently and resubscribe.
                        mListPopupWindow.dismiss();
                        subscribe();
                    }

                    @Override
                    public void onNext(@NonNull final List<String> strings) {
                        onAutoCompleteResultsReceived(strings);
                    }
                });
    }

    @VisibleForTesting
    void onAutoCompleteResultsReceived(@NonNull final List<String> strings) {
        if (strings.isEmpty()) {
            mListPopupWindow.dismiss();
        }
        else {
            mListPopupWindow.setAdapter(new ArrayAdapter<>(
                    AutoCompleteAddressFragment.this.getActivity(),
                    android.R.layout.simple_list_item_1,
                    strings
            ));
            mListPopupWindow.show();
        }
    }

    @VisibleForTesting
    @NonNull
    List<String> makeApiCall(String string) {
        if (string == null || string.trim().length() < 3) {
            mPredictions = new ArrayList<>();
            mPredictionValues = new ArrayList<>();
            return mPredictionValues;
        }
        PlacePredictionResponse response = mAutoCompleteManager.getAddressPrediction(string);
        response.filter(mZipFilter);

        mPredictions = response.predictions;
        mPredictionValues = response.getFullAddresses();

        return mPredictionValues;
    }

    public void hideKeyboard() {
        if (getActivity() != null && getView() != null) {
            UiUtils.dismissKeyboard(getActivity());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }

    public boolean validateFields() {
        boolean validate = true;

        if (!mStreet.validate()) { validate = false; }

        return validate;
    }
}
