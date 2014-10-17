package com.handybook.handybook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class ProfileFragment extends InjectedFragment {
    private User user;
    private ProgressDialog progressDialog;

    @InjectView(R.id.credits_text) TextView creditsText;
    @Inject UserManager userManager;
    @Inject DataManager dataManager;
    @Inject DataManagerErrorHandler dataManagerErrorHandler;

    static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = userManager.getCurrentUser();
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.inject(this, view);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setDelay(500);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));

        creditsText.setText(null);
        loadUserInfo();

        return view;
    }

    private void updateUserInfo() {
        final DecimalFormat df = new DecimalFormat("#.##");
        creditsText.setText(df.format(user.getCredits()));
    }

    private void disableInputs() {
    }

    private void enableInputs() {
    }

    private void loadUserInfo() {
        updateUserInfo();
        disableInputs();
        progressDialog.show();
        dataManager.getUserInfo(user.getId(), user.getAuthToken(), new DataManager.Callback<User>() {
            @Override
            public void onSuccess(User user) {
                progressDialog.dismiss();
                enableInputs();
                userManager.setCurrentUser(user);
                updateUserInfo();
            }

            @Override
            public void onError(DataManager.DataManagerError error) {
                progressDialog.dismiss();
                enableInputs();
                dataManagerErrorHandler.handleError(getActivity(), error);
            }
        });
    }
}
