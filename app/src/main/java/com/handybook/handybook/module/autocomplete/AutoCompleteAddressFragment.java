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

    @Bind(R.id.text_city)
    public StreetAddressInputTextView textCity;

    @Bind(R.id.text_state)
    public StreetAddressInputTextView textState;

    @Bind(R.id.text_postal)
    public StreetAddressInputTextView textPostal;

    @Inject
    AddressAutoCompleteManager mDataManager;

    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_auto_complete_address, container, false);
        ButterKnife.bind(this, view);

        mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(getActivity(), R.layout.auto_complete_list_item, mDataManager);
        textStreet.setAdapter(mAutoCompleteAdapter);
        textStreet.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                PlacePrediction prediction = mAutoCompleteAdapter.getPrediction(position);
                textStreet.setText(prediction.getAddress());
                textCity.setText(prediction.getCity());
                textState.setText(prediction.getState());

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
        textCity.setText(address.getCity());
        textState.setText(address.getState());
        textPostal.setText(address.getZip());
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
        if (!textCity.validate()) { validate = false; }
        if (!textState.validate()) { validate = false; }
        if (!textPostal.validate()) { validate = false; }

        return validate;
    }
}
