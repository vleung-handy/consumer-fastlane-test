package com.handybook.handybook.library.ui.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.manager.BookingManager;
import com.handybook.handybook.configuration.manager.ConfigurationManager;
import com.handybook.handybook.core.BaseApplication;
import com.handybook.handybook.core.NavigationManager;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.DataManagerErrorHandler;
import com.handybook.handybook.core.event.HandyEvent;
import com.handybook.handybook.core.manager.DefaultPreferencesManager;
import com.handybook.handybook.library.ui.view.ProgressDialog;
import com.handybook.handybook.library.util.ValidationUtils;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class InjectedFragment extends android.support.v4.app.Fragment {

    protected boolean allowCallbacks;
    protected ProgressDialog progressDialog; //TODO: we should take this out of this class
    protected Toast toast;

    @Inject
    protected BookingManager bookingManager;
    @Inject
    protected UserManager userManager;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected DataManagerErrorHandler dataManagerErrorHandler;
    @Inject
    protected NavigationManager navigationManager;
    @Inject
    protected ConfigurationManager mConfigurationManager;
    @Inject
    protected DefaultPreferencesManager mDefaultPreferencesManager;

    //TODO: acknowledged this is not ideal
    @Inject
    public Bus bus;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BaseApplication) getActivity().getApplication()).inject(this);

        toast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setDelay(400);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));
    }

    /**
     * This method will be used by those that actually have a toolbar
     *
     * @param title
     */
    public void setupToolbar(Toolbar toolbar, String title) {
        setupToolbar(toolbar, title, false);
    }

    public void setupToolbar(Toolbar toolbar, String title, boolean setDisplayHomeAsUp) {
        setupToolbar(toolbar, title, setDisplayHomeAsUp, Integer.MIN_VALUE);
    }

    /**
     *
     * @param toolbar
     * @param title
     * @param setDisplayHomeAsUp
     * @param navigationIcon use Integer.MIN_VALUE to use the default ic_x_white icon
     */
    public void setupToolbar(
            final Toolbar toolbar,
            final String title,
            final boolean setDisplayHomeAsUp,
            final @DrawableRes int navigationIcon
    ) {
        final AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        setToolbarTitle(title);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(true);
        if (setDisplayHomeAsUp) {
            if (navigationIcon == Integer.MIN_VALUE) {
                activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_x_white);
            }
            else {
                activity.getSupportActionBar().setHomeAsUpIndicator(navigationIcon);
            }
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    public void setToolbarTitle(String title) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
    }

    @Override
    public void onStart() {
        super.onStart();
        allowCallbacks = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        allowCallbacks = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        this.bus.register(this);
    }

    @Override
    public void onPause() {
        this.bus.unregister(this);
        super.onPause();
    }

    protected void disableInputs() {}

    protected void enableInputs() {}

    //Helpers
    protected void showToast(int stringId) {
        showToast(getString(stringId));
    }

    protected void showToast(String message) {
        showToast(message, Toast.LENGTH_SHORT);
    }

    protected void showToast(int stringId, int length) {
        showToast(getString(stringId), length);
    }

    protected void showToast(String message, int length) {
        toast = Toast.makeText(getActivity().getApplicationContext(), message, length);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    //Each fragment if it requires arguments from the bundles should override this list
    protected List<String> requiredArguments() {
        return new ArrayList<>();
    }

    protected boolean validateRequiredArguments() {
        boolean validated = true;

        Bundle suppliedArguments = this.getArguments();

        if (suppliedArguments == null) {
            return requiredArguments().size() == 0;
        }

        List<String> requiredArguments = requiredArguments();
        String errorDetails = "";
        for (String requiredArgument : requiredArguments) {
            //TODO: Is there a way we can validate without knowing the type in advance?
            if (!suppliedArguments.containsKey(requiredArgument)) {
                validated = false;

                if (!validated) {
                    errorDetails += "Missing required argument : " + requiredArgument + "\n";
                }
            }
        }

        try {
            if (!validated) {
                throw new Exception(errorDetails);
            }
        }
        catch (Exception e) {
            Crashlytics.logException(e);
        }

        return validated;
    }

    //TODO: why is the progress dialog in this class?
    protected void postBlockingEvent(HandyEvent event) {
        showUiBlockers();
        bus.post(event);
    }

    protected void showUiBlockers() {
        disableInputs();
        progressDialog.show();
    }

    protected void removeUiBlockers() {
        enableInputs();
        progressDialog.dismiss();
    }

    protected void showErrorDialog(final String errorMessage, final DialogCallback callback) {
        removeUiBlockers();
        String displayMessage = errorMessage;
        if (ValidationUtils.isNullOrEmpty(displayMessage)) {
            displayMessage = getString(R.string.an_error_has_occurred);
        }
        new AlertDialog.Builder(getActivity())
                .setTitle(displayMessage)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        dialog.dismiss();
                        callback.onCancel();
                    }
                })
                .setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        dialog.dismiss();
                        callback.onRetry();
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }

    protected interface DialogCallback {

        void onRetry();

        void onCancel();
    }
}
