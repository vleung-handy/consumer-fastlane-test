package com.handybook.handybook.ui.fragment;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;

import com.handybook.handybook.R;
import com.handybook.handybook.core.BaseApplication;
import com.handybook.handybook.core.BookingManager;
import com.handybook.handybook.core.NavigationManager;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.data.DataManagerErrorHandler;
import com.handybook.handybook.data.Mixpanel;
import com.handybook.handybook.ui.widget.ProgressDialog;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class InjectedFragment extends android.support.v4.app.Fragment {
    protected boolean allowCallbacks;
    protected ProgressDialog progressDialog;
    protected Toast toast;

    //UPGRADE: Move away from direct calls to these and go through the bus
    @Inject BookingManager bookingManager;
    @Inject UserManager userManager;
    @Inject Mixpanel mixpanel;
    @Inject DataManager dataManager;
    @Inject DataManagerErrorHandler dataManagerErrorHandler;
    @Inject NavigationManager navigationManager;

    @Inject
    Bus bus;


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
    public final void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
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
}
