package com.handybook.handybook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;

import net.simonvt.menudrawer.MenuDrawer;

import java.util.HashMap;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class LoginActivityFragment extends InjectedFragment {
    private static final String STATE_EMAIL_HIGHLIGHT = "EMAIL_HIGHLIGHT";
    private static final String STATE_PASSWORD_HIGHLIGHT = "PASSWORD_HIGHLIGHT";

    @InjectView(R.id.login_button) Button loginButton;
    @InjectView(R.id.email_text) EmailInputTextView emailText;
    @InjectView(R.id.password_text) PasswordInputTextView passwordText;
    @Inject DataManager dataManager;
    @Inject DataManagerErrorHandler dataManagerErrorHandler;
    @Inject UserManager userManager;

    static LoginActivityFragment newInstance() {
        return new LoginActivityFragment();
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.inject(this, view);

        //TODO resotre spinner on orientation change
        //TODO add transparent layout to diable while spiinig
        //TODO replace old toast with new toast

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateFields()) {
                    final SuperActivityToast toast =
                            new SuperActivityToast(getActivity(), SuperToast.Type.PROGRESS);
                    toast.setText(getString(R.string.loading));
                    toast.setIndeterminate(true);
                    toast.setProgressIndeterminate(true);
                    toast.show();

                    dataManager.authUser(emailText.getText().toString(),
                            passwordText.getText().toString(), new DataManager.Callback<User>() {
                        @Override
                        public void onSuccess(final User user) {
                            userManager.setCurrentUser(user);
                            toast.dismiss();

                            final MenuDrawerActivity activty = (MenuDrawerActivity)getActivity();
                            final MenuDrawer menuDrawer = activty.getMenuDrawer();
                            menuDrawer.setOnDrawerStateChangeListener(new MenuDrawer.OnDrawerStateChangeListener() {
                                @Override
                                public void onDrawerStateChange(final int oldState, final int newState) {
                                    if (newState == MenuDrawer.STATE_OPEN) {
                                        activty.navigateToActivity(ServiceCategoriesActivity.class);
                                        menuDrawer.setOnDrawerStateChangeListener(null);
                                    }
                                }

                                @Override
                                public void onDrawerSlide(float openRatio, int offsetPixels) {
                                }
                            });
                            activty.getMenuDrawer().openMenu(true);
                        }

                        @Override
                        public void onError(final DataManager.DataManagerError error) {
                            toast.dismiss();
                            final HashMap<String, InputTextField> inputMap = new HashMap<>();
                            inputMap.put("password", passwordText);
                            inputMap.put("email", emailText);
                            dataManagerErrorHandler.handleError(getActivity(), error, inputMap);
                        }
                    });
                }
            }
        });

        return view;
    }

    @Override
    public final void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(STATE_EMAIL_HIGHLIGHT)) emailText.highlight();
            if (savedInstanceState.getBoolean(STATE_PASSWORD_HIGHLIGHT)) passwordText.highlight();
        }
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_EMAIL_HIGHLIGHT, emailText.isHighlighted());
        outState.putBoolean(STATE_PASSWORD_HIGHLIGHT, passwordText.isHighlighted());
    }

    final boolean validateFields() {
        boolean validate = true;
        if (!emailText.validate()) validate = false;
        if (!passwordText.validate()) validate = false;
        return validate;
    }
}
