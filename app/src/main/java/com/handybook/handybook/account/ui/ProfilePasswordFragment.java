package com.handybook.handybook.account.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.handybook.handybook.R;
import com.handybook.handybook.core.User;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.event.UserEvent;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.account.AccountLog;
import com.handybook.handybook.model.request.UpdateUserRequest;
import com.handybook.handybook.ui.widget.PasswordInputTextView;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProfilePasswordFragment extends InjectedFragment
        implements TextWatcher, View.OnClickListener
{
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.profile_old_password_text)
    PasswordInputTextView oldPasswordtext;
    @Bind(R.id.profile_new_password_text)
    PasswordInputTextView newPasswordtext;
    @Bind(R.id.profile_new_password_confirmation_text)
    PasswordInputTextView newPasswordConfirmationText;
    @Bind(R.id.profile_password_update_button)
    Button mPasswordUpdateButton;

    private static final String STATE_OLD_PWD_HIGHLIGHT = "OLD_PWD_HIGHLIGHT";
    private static final String STATE_NEW_PWD_HIGHLIGHT = "NEW_PWD_HIGHLIGHT";
    private static final String STATE_NEW_PWD_CONFIRMATION_HIGHLIGHT = "NEW_PWD_CONFIRMATION_HIGHLIGHT";
    private static final String STATE_LOADED_USER = "LOADED_USER";

    private User user;
    private boolean loadedUserInfo;
    private boolean updatingInfo;

    private static final int NEW_PASSWORD_MINIMUM_CHAR_LENGTH = 8;


    public static ProfilePasswordFragment newInstance()
    {
        return new ProfilePasswordFragment();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        user = userManager.getCurrentUser();
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState
    )
    {
        final View view =
                LayoutInflater.from(getContext()).inflate(
                        R.layout.fragment_profile_password,
                        container,
                        false
                );
        ButterKnife.bind(this, view);
        setupToolbar(mToolbar, getString(R.string.account_update_password));

        mPasswordUpdateButton.setOnClickListener(this);
        oldPasswordtext.addTextChangedListener(this);
        newPasswordtext.addTextChangedListener(this);
        newPasswordConfirmationText.addTextChangedListener(this);

        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null)
        {
            if (savedInstanceState.getBoolean(STATE_OLD_PWD_HIGHLIGHT))
            {
                oldPasswordtext.highlight();
            }
            if (savedInstanceState.getBoolean(STATE_NEW_PWD_HIGHLIGHT))
            {
                newPasswordtext.highlight();
            }
            if (savedInstanceState.getBoolean(STATE_NEW_PWD_CONFIRMATION_HIGHLIGHT))
            {
                newPasswordConfirmationText.highlight();
            }
            loadedUserInfo = savedInstanceState.getBoolean(STATE_LOADED_USER);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        bus.post(new LogEvent.AddLogEvent(new AccountLog.UpdatePasswordShown()));

    }

    @Override
    public void onSaveInstanceState(final Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_OLD_PWD_HIGHLIGHT, oldPasswordtext.isHighlighted());
        outState.putBoolean(STATE_NEW_PWD_HIGHLIGHT, newPasswordtext.isHighlighted());
        outState.putBoolean(
                STATE_NEW_PWD_CONFIRMATION_HIGHLIGHT,
                newPasswordConfirmationText.isHighlighted()
        );
        outState.putBoolean(STATE_LOADED_USER, loadedUserInfo);
    }

    //region TextWatcher
    @Override
    public void beforeTextChanged(
            final CharSequence s,
            final int start,
            final int count,
            final int after
    )
    { }

    @Override
    public void onTextChanged(
            final CharSequence s,
            final int start,
            final int before,
            final int count
    )
    { }

    @Override
    public void afterTextChanged(final Editable s)
    {
        //user changed the text, remove highlights
        oldPasswordtext.unHighlight();
        newPasswordtext.unHighlight();
        newPasswordConfirmationText.unHighlight();
    }
    //endregion

    //region View.OnClickListener
    @Override
    public void onClick(final View v)
    {
        bus.post(new LogEvent.AddLogEvent(new AccountLog.UpdatePasswordTapped()));

        if (validateFields())
        {
            disableInputs();
            progressDialog.show();

            UpdateUserRequest updateUserRequest = new UpdateUserRequest();
            updateUserRequest.setUserId(user.getId());
            updateUserRequest.setCurrentPassword(oldPasswordtext.getPassword());
            updateUserRequest.setPassword(newPasswordtext.getPassword());
            updateUserRequest.setPasswordConfirmation(newPasswordConfirmationText.getPassword());

            updatingInfo = true;
            bus.post(new UserEvent.RequestUserPasswordUpdate(
                    updateUserRequest, user.getAuthToken()));
        }
    }
    //endregion


    //region Bus events
    @Subscribe
    public void onReceiveUserPasswordUpdateSuccess(
            UserEvent.ReceiveUserPasswordUpdateSuccess event
    )
    {
        bus.post(new LogEvent.AddLogEvent(new AccountLog.UpdatePasswordSuccess()));
        userSuccessCallback();
    }

    @Subscribe
    public void onReceiveUserPasswordUpdateError(UserEvent.ReceiveUserPasswordUpdateError event)
    {
        bus.post(new LogEvent.AddLogEvent(new AccountLog.UpdatePasswordError()));
        userErrorCallback(event);
    }
    //endregion

    //region InjectedFragment
    @Override
    protected void disableInputs()
    {
        super.disableInputs();
        mPasswordUpdateButton.setClickable(false);

        final InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(oldPasswordtext.getWindowToken(), 0);
    }

    @Override
    protected void enableInputs()
    {
        super.enableInputs();
        mPasswordUpdateButton.setClickable(true);
    }
    //endregion

    //region Private Fragment methods
    private boolean validateFields()
    {
        final String oldPwd = oldPasswordtext.getPassword();
        final String newPwd = newPasswordtext.getPassword();
        final String newPwdConfirmation = newPasswordConfirmationText.getPassword();

        //if any of the password fields are empty
        if (oldPwd.isEmpty() || newPwd.isEmpty() || newPwdConfirmation.isEmpty())
        {
            //highlight the fields that are empty
            if (oldPwd.isEmpty()) { oldPasswordtext.highlight(); }
            if (newPwd.isEmpty()) { newPasswordtext.highlight(); }
            if (newPwdConfirmation.isEmpty()) { newPasswordConfirmationText.highlight(); }

            //show message that notifies user that all fields must be filled out
            showToast(R.string.update_pwd_missing_input_error);
        }
        //else if the new password is too short
        else if (newPwd.length() < NEW_PASSWORD_MINIMUM_CHAR_LENGTH)
        {
            newPasswordtext.highlight();
            showToast(R.string.pwd_length_error);
        }
        //else if the new password does not equal the confirmation
        else if (!newPwd.equals(newPwdConfirmation))
        {
            newPasswordtext.highlight();
            newPasswordConfirmationText.highlight();
            showToast(R.string.new_password_confirmation_error);
        }
        else
        {
            //no error
            return true;

        }
        return false;
    }

    private void clearPasswordFields()
    {
        oldPasswordtext.unHighlight();
        oldPasswordtext.setText("");
        newPasswordtext.unHighlight();
        newPasswordtext.setText("");
        newPasswordConfirmationText.unHighlight();
        newPasswordConfirmationText.setText("");
    }

    private void userSuccessCallback()
    {
        if (!allowCallbacks) { return; }

        loadedUserInfo = true;
        userManager.setCurrentUser(user);
        user = userManager.getCurrentUser();
        clearPasswordFields();
        progressDialog.dismiss();
        enableInputs();

        if (updatingInfo)
        {
            updatingInfo = false;
            toast.setText(getString(R.string.info_updated));
            toast.show();
        }
    }

    private void userErrorCallback(HandyEvent.ReceiveErrorEvent event)
    {
        if (!allowCallbacks) { return; }

        loadedUserInfo = true;
        progressDialog.dismiss();
        enableInputs();
        dataManagerErrorHandler.handleError(getActivity(), event.error);
    }
    //endregion
}
