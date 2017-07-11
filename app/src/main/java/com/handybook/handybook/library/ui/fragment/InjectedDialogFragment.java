package com.handybook.handybook.library.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.manager.BookingManager;
import com.handybook.handybook.core.BaseApplication;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.DataManagerErrorHandler;
import com.handybook.handybook.library.ui.view.ProgressDialog;
import com.handybook.handybook.logger.handylogger.EventLogManager;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

public class InjectedDialogFragment extends DialogFragment {

    protected boolean allowCallbacks;
    protected ProgressDialog progressDialog;

    @Inject
    protected BookingManager bookingManager;
    @Inject
    protected UserManager userManager;
    @Inject
    protected EventLogManager mEventLogManager;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected DataManagerErrorHandler dataManagerErrorHandler;
    @Inject
    protected EventBus mBus;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BaseApplication) getActivity().getApplication()).inject(this);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setDelay(400);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));
    }

    @Override
    public final void onDestroyView() {
        super.onDestroyView();
        //TODO: Look into if we need this and if so find a way to re-enable ButterKnife.unbind(this);
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

    protected void disableInputs() {}

    protected void enableInputs() {}
}
