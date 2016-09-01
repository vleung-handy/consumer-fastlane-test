package com.handybook.handybook.ui.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.event.HandyEvent;

import javax.inject.Inject;

public class BaseDialogFragment extends InjectedDialogFragment
{
    //TODO: use getters and setters
    public boolean canDismiss;

    @Inject
    public DataManager dataManager;

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState)
    {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        this.setStyle(android.support.v4.app.DialogFragment.STYLE_NO_FRAME, 0);
        return dialog;
    }

    @Override
    public View onCreateView(
            final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState
    )
    {
        final View view = super.onCreateView(inflater, container, savedInstanceState);

        getDialog().getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));

        updateDialogState();

        return view;
    }

    protected void allowDialogDismissable()
    {
        canDismiss = true;
        updateDialogState();
    }

    private void updateDialogState()
    {
        getDialog().setCancelable(canDismiss);
        getDialog().setCanceledOnTouchOutside(canDismiss);
        applyDefaultKeyListener();
    }

    /**
     * Handles keypresses depending on whether this dialog is dismissable or not
     */
    protected void applyDefaultKeyListener()
    {
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener()
        {
            @Override
            public boolean onKey(
                    final DialogInterface dialog, final int keyCode,
                    final KeyEvent event
            )
            {
                // Disable the back key when cannot dismiss
                if (keyCode == KeyEvent.KEYCODE_BACK)
                {
                    return !canDismiss;
                }

                // Otherwise return false, do not consume the event
                return false;
            }
        });
    }

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
        Toast toast = Toast.makeText(getActivity().getApplicationContext(), message, length);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    protected void handleRequestError(HandyEvent.ReceiveErrorEvent error)
    {
        Crashlytics.logException(new RuntimeException(error.getClass().getName() + ":" + error.error.getMessage()));
        if (error.error.getType() == DataManager.Type.NETWORK)
        {
            showToast(R.string.error_fetching_connectivity_issue);
        }
        else
        {
            showToast(R.string.default_error_string);
        }
    }
}
