package com.handybook.handybook.ui.fragment;

import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.view.Gravity;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.core.BaseApplication;
import com.handybook.handybook.booking.manager.BookingManager;
import com.handybook.handybook.core.NavigationManager;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.data.DataManagerErrorHandler;
import com.handybook.handybook.data.Mixpanel;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.ui.widget.ProgressDialog;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class InjectedFragment extends android.support.v4.app.Fragment {
    protected boolean allowCallbacks;
    protected ProgressDialog progressDialog; //TODO: we should take this out of this class
    protected Toast toast;

    //UPGRADE: Move away from direct calls to these and go through the bus
    @Inject protected BookingManager bookingManager;
    @Inject protected UserManager userManager;
    @Inject protected Mixpanel mixpanel;
    @Inject protected DataManager dataManager;
    @Inject protected DataManagerErrorHandler dataManagerErrorHandler;
    @Inject protected NavigationManager navigationManager;

    //TODO: acknowledged this is not ideal
    @VisibleForTesting
    @Inject
    public Bus bus;


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BaseApplication)getActivity().getApplication()).inject(this);

        toast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setDelay(400);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
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
    public void onResume()
    {
        super.onResume();
        this.bus.register(this);
    }

    @Override
    public void onPause()
    {
        this.bus.unregister(this);
        super.onPause();
    }

    protected void disableInputs() {}

    protected void enableInputs() {}

    //Helpers
    protected void showToast(int stringId)
    {
        showToast(getString(stringId));
    }

    protected void showToast(String message)
    {
        showToast(message, Toast.LENGTH_SHORT);
    }

    protected void showToast(int stringId, int length)
    {
        showToast(getString(stringId), length);
    }

    protected void showToast(String message, int length)
    {
        toast = Toast.makeText(getActivity().getApplicationContext(), message, length);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    //Each fragment if it requires arguments from the bundles should override this list
    protected List<String> requiredArguments()
    {
        return new ArrayList<String>();
    }

    protected boolean validateRequiredArguments()
    {
        boolean validated = true;

        Bundle suppliedArguments = this.getArguments();

        if(suppliedArguments == null)
        {
            if(requiredArguments().size() == 0)
            {
                return true;
            }
            else
            {
                return false;
            }
        }

        List<String> requiredArguments = requiredArguments();
        String errorDetails = "";
        for (String requiredArgument : requiredArguments)
        {
            //TODO: Is there a way we can validate without knowing the type in advance?
            if (!suppliedArguments.containsKey(requiredArgument))
            {
                validated = false;

                if (!validated)
                {
                    errorDetails += "Missing required argument : " + requiredArgument + "\n";
                }
            }
        }

        try
        {
            if (!validated)
            {
                throw new Exception(errorDetails);
            }
        } catch (Exception e)
        {
            Crashlytics.logException(e);
        }

        return validated;
    }

    //TODO: why is the progress dialog in this class?
    protected void postBlockingEvent(HandyEvent event)
    {
        showUiBlockers();
        bus.post(event);
    }

    protected void showUiBlockers()
    {
        disableInputs();
        progressDialog.show();
    }

    protected void removeUiBlockers()
    {
        enableInputs();
        progressDialog.dismiss();
    }
}
