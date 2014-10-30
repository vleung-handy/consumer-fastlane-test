package com.handybook.handybook;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class BookingRequestLocationFragment extends InjectedFragment {
    private static final String STATE_ZIP_HIGHLIGHT = "ZIP_HIGHLIGHT";

    private ProgressDialog progressDialog;
    private Toast toast;

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
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toast = Toast.makeText(getActivity(), null, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_bookreq_location, container, false);
        ButterKnife.inject(this, view);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setDelay(500);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));

        final User.Address address;
        final User user = userManager.getCurrentUser();
        if (user != null && (address = user.getAddress()) != null) {
            zipText.setText(address.getZip());
        }

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (validateFields()) {
                    disableInputs();
                    progressDialog.show();
                    progressDialog.dismiss();
                    enableInputs();
                }
            }
        });

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                zipText.unHighlight();
                updateZip();

                //TODO update state to blue if found otherwise set back to white
                //TODO if text typed, set back to white
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
    public final void onStop() {
        super.onStop();
        dataManager = null;
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
        locationButton.setVisibility(View.INVISIBLE);
        zipProgress.setVisibility(View.VISIBLE);

        toast.setText("LOCATION");
        toast.show();

        zipProgress.setVisibility(View.INVISIBLE);
        locationButton.setVisibility(View.VISIBLE);
    }
}
