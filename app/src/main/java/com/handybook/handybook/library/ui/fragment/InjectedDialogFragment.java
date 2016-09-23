package com.handybook.handybook.library.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.manager.BookingManager;
import com.handybook.handybook.core.BaseApplication;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.data.DataManagerErrorHandler;
import com.handybook.handybook.library.ui.view.ProgressDialog;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class InjectedDialogFragment extends DialogFragment
{
    protected boolean allowCallbacks;
    protected ProgressDialog progressDialog;

    @Inject
    protected BookingManager bookingManager;
    @Inject
    protected UserManager userManager;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected DataManagerErrorHandler dataManagerErrorHandler;
    @Inject
    protected Bus mBus;

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ((BaseApplication) getActivity().getApplication()).inject(this);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setDelay(400);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));
    }

    @Override
    public final void onDestroyView()
    {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        allowCallbacks = true;
    }

    @Override
    public void onStop()
    {
        super.onStop();
        allowCallbacks = false;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mBus.register(this);
    }

    @Override
    public void onPause()
    {
        mBus.unregister(this);
        super.onPause();
    }

    protected void disableInputs() {}

    protected void enableInputs() {}
}
