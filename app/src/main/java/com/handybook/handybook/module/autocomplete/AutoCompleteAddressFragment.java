package com.handybook.handybook.module.autocomplete;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.ListPopupWindow;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.ZipValidationResponse;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.booking.AddressAutocompleteLog;
import com.handybook.handybook.ui.fragment.InjectedFragment;
import com.handybook.handybook.ui.widget.StreetAddressInputTextView;
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
public class AutoCompleteAddressFragment extends InjectedFragment
{
    private static final int DELAY = 500;   //the delay before we fire a request to address autocomplete
    private static final String KEY_FILTER = "filter";
    private static final String KEY_ADDR1 = "address1";
    private static final String KEY_ADDR2 = "address2";

    @Bind(R.id.text_street)
    public StreetAddressInputTextView mStreet;

    @Bind(R.id.text_other)
    public EditText mOther;

    @Inject
    AddressAutoCompleteManager mAutoCompleteManager;

    private ListPopupWindow mListPopupWindow;
    Subscription subscription;

    List<String> mPredictionValues;
    List<PlacePrediction> mPredictions;

    ZipValidationResponse.ZipArea mZipFilter = null;

    public static AutoCompleteAddressFragment newInstance(
            final ZipValidationResponse.ZipArea filter,
            final String address1,
            final String address2
    )
    {
        Bundle args = new Bundle();
        args.putSerializable(KEY_FILTER, filter);
        args.putString(KEY_ADDR1, address1);
        args.putString(KEY_ADDR2, address2);

        AutoCompleteAddressFragment fragment = new AutoCompleteAddressFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_auto_complete_address, container, false);
        ButterKnife.bind(this, view);

        if (getArguments() != null)
        {
            mZipFilter = (ZipValidationResponse.ZipArea) getArguments().getSerializable(KEY_FILTER);
            mStreet.setText(getArguments().getString(KEY_ADDR1));
            mOther.setText(getArguments().getString(KEY_ADDR2));
        }

        mListPopupWindow = new ListPopupWindow(getActivity());
        mListPopupWindow.setAnchorView(mStreet);
        mListPopupWindow.setWidth(ListPopupWindow.MATCH_PARENT);
        mListPopupWindow.setModal(false);
        mListPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                subscription.unsubscribe();

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
        return view;
    }

    private void subscribe()
    {
        subscription = RxTextView.textChanges(mStreet)
                .debounce(DELAY, TimeUnit.MILLISECONDS)
                .skip(1)
                .flatMap(new Func1<CharSequence, Observable<List<String>>>()
                {
                    @Override
                    public Observable<List<String>> call(CharSequence charSequence)
                    {
                        return Observable.just(makeApiCall(charSequence.toString()));
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<String>>()
                {
                    @Override
                    public void onCompleted()
                    {
                    }

                    @Override
                    public void onError(final Throwable e)
                    {
                        //sometimes, we get an RetrofitError: thread interrupted, in which
                        //case this stream terminates. Fail silently and resubscribe.
                        mListPopupWindow.dismiss();
                        subscribe();
                    }

                    @Override
                    public void onNext(final List<String> strings)
                    {
                        if (strings.isEmpty())
                        {
                            mListPopupWindow.dismiss();
                        }
                        else
                        {
                            mListPopupWindow.setAdapter(new ArrayAdapter<>(AutoCompleteAddressFragment.this.getActivity(), android.R.layout.simple_list_item_1, strings));
                            mListPopupWindow.show();
                        }
                    }
                });
    }

    private List<String> makeApiCall(String string)
    {
        if (string == null || string.trim().length() < 3)
        {
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

    public void hideKeyboard()
    {
        if (getActivity() != null && getView() != null)
        {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }
    }

    public boolean validateFields()
    {
        boolean validate = true;

        if (!mStreet.validate()) { validate = false; }

        return validate;
    }
}
