package com.handybook.handybook.module.autocomplete;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.ListPopupWindow;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.ZipValidationResponse;
import com.handybook.handybook.core.User;
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
    private static final String TAG = "AutoCompleteAddressFrag";

    private static final int DELAY = 500;   //the delay before we fire a request to address autocomplete

    @Bind(R.id.text_street)
    public StreetAddressInputTextView mStreet;

    @Bind(R.id.text_other)
    public EditText textOther;

    @Inject
    AddressAutoCompleteManager mAutoCompleteManager;

    private ListPopupWindow mListPopupWindow;
    Subscription subscription;

    List<String> mPredictionValues;
    List<PlacePrediction> mPredictions;

    ZipValidationResponse.ZipArea mZipFilter = null;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_auto_complete_address, container, false);
        ButterKnife.bind(this, view);


        if (bookingManager.getCurrentRequest() != null)
        {
            mZipFilter = bookingManager.getCurrentRequest().getZipArea();
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
                Log.d(TAG, "onItemClick: ");
                subscription.unsubscribe();
                mStreet.setText(mPredictions.get(position).getAddress());
                mStreet.setSelection(mStreet.getText().length());
                mListPopupWindow.dismiss();
                hideKeyboard();

                //skipping the first element fired, because it is triggered by the "setText" above.
                subscribe(1);
            }
        });

        subscribe(0);
        return view;
    }

    private void subscribe(int elementToSkip)
    {
        Log.d(TAG, "subscribe() called with: elementToSkip = [" + elementToSkip + "]");
        subscription = RxTextView.textChanges(mStreet)
                .debounce(DELAY, TimeUnit.MILLISECONDS)
                .skip(elementToSkip)
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
                        Log.d(TAG, "onCompleted: ");
                    }


                    @Override
                    public void onError(final Throwable e)
                    {
                        //sometimes, we get an RetrofitError: thread interrupted, in which
                        //case this stream terminates. Fail silently and resubscribe.
                        Log.e(TAG, "onError: " + e.getMessage(), e);
                        mListPopupWindow.dismiss();
                        subscribe(0);
                    }

                    @Override
                    public void onNext(final List<String> strings)
                    {
                        Log.d(TAG, "onNext:  beginning method");
                        if (strings == null)
                        {
                            Log.d(TAG, "onNext: String is NULL");
                        }

                        if (strings.isEmpty())
                        {
                            mListPopupWindow.dismiss();
                        }
                        else
                        {
                            mListPopupWindow.setAdapter(new ArrayAdapter<>(AutoCompleteAddressFragment.this.getActivity(), android.R.layout.simple_list_item_1, strings));
                            mListPopupWindow.show();
                        }

                        Log.d(TAG, "onNext: end method");
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

        Log.d(TAG, "makeApiCall: returning candidates");
        return mPredictionValues;
    }

    public void bindAddress(final User.Address address)
    {
        if (subscription != null)
        {
            subscription.unsubscribe();
        }
        mStreet.setText(address.getAddress1());
        textOther.setText(address.getAddress2());

        if (subscription != null)
        {
            subscribe(1);       //don't show autocomplete for the setText from this binding
        }
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
