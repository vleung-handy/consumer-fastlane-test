package com.handybook.handybook.module.autocomplete;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.ZipValidationResponse;
import com.handybook.handybook.core.User;
import com.handybook.handybook.ui.fragment.InjectedFragment;
import com.handybook.handybook.ui.widget.StreetAddressInputTextView;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This is a fragment that supports autocomplete of address fields.
 */
public class AutoCompleteAddressFragment extends InjectedFragment
{
    @Bind(R.id.text_street)
    public StreetAddressInputTextView textStreet;

    @Bind(R.id.text_other)
    public EditText textOther;

    @Inject
    AddressAutoCompleteManager mAutoCompleteManager;

    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_auto_complete_address, container, false);
        ButterKnife.bind(this, view);

        ZipValidationResponse.ZipArea filter = null;

        if (bookingManager.getCurrentRequest() != null)
        {
            filter = bookingManager.getCurrentRequest().getZipArea();
        }

        mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(getActivity(), R.layout.auto_complete_list_item, mAutoCompleteManager, filter);
        textStreet.setAdapter(mAutoCompleteAdapter);
        textStreet.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                PlacePrediction prediction = mAutoCompleteAdapter.getPrediction(position);
                textStreet.setText(prediction.getAddress());

//                TODO: JIA: test whether this is necessary
//                mMainContainer.requestFocus();

                hideKeyboard();
            }
        });

        return view;
    }

    public void bindAddress(final User.Address address)
    {
        textStreet.setText(address.getAddress1());
        textOther.setText(address.getAddress2());
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

        if (!textStreet.validate()) { validate = false; }

        return validate;
    }
}
