package com.handybook.handybook.ui.fragment;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;

import com.handybook.handybook.R;
import com.handybook.handybook.core.BaseApplication;
import com.handybook.handybook.core.BookingManager;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.data.DataManagerErrorHandler;
import com.handybook.handybook.data.Mixpanel;
import com.handybook.handybook.ui.widget.ProgressDialog;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class InjectedFragment extends android.support.v4.app.Fragment {
    protected boolean allowCallbacks;
    protected ProgressDialog progressDialog;
    protected Toast toast;

    @Inject BookingManager bookingManager;
    @Inject UserManager userManager;
    @Inject Mixpanel mixpanel;
    @Inject DataManager dataManager;
    @Inject DataManagerErrorHandler dataManagerErrorHandler;

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

    protected void disableInputs() {}

    protected void enableInputs() {}
}
