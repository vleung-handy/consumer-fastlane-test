package com.handybook.handybook.account.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.handybook.handybook.R;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.callback.FragmentSafeCallback;
import com.handybook.handybook.core.model.request.UpdateUserRequest;
import com.handybook.handybook.core.ui.widget.EmailInputTextView;
import com.handybook.handybook.core.ui.widget.FullNameInputTextView;
import com.handybook.handybook.core.ui.widget.PhoneInputTextView;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.account.AccountLog;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContactFragment extends InjectedFragment
{
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.contact_name_text)
    FullNameInputTextView mFullNameText;
    @Bind(R.id.contact_email_text)
    EmailInputTextView mEmailText;
    @Bind(R.id.contact_phone_text)
    PhoneInputTextView mPhoneText;

    private User mUser;

    public static ContactFragment newInstance()
    {
        return new ContactFragment();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mUser = userManager.getCurrentUser();
    }

    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState
    )
    {
        final View view = getActivity().getLayoutInflater()
                                       .inflate(R.layout.fragment_update_contact, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState)
    {
        setupToolbar(mToolbar, getString(R.string.account_contact_info));
        updateDisplay();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        bus.post(new LogEvent.AddLogEvent(new AccountLog.ContactInfoShown()));

    }

    private void updateDisplay()
    {
        mFullNameText.setText(mUser.getFullName());
        mEmailText.setText(mUser.getEmail());
        mPhoneText.setText(mUser.getPhone());
        mPhoneText.setCountryCode(mUser.getPhonePrefix());
    }

    @OnClick(R.id.contact_update_button)
    public void updateContact()
    {
        bus.post(new LogEvent.AddLogEvent(new AccountLog.ContactInfoUpdateTapped()));

        if (validateFields())
        {
            disableInputs();
            progressDialog.show();

            UpdateUserRequest updateUserRequest = new UpdateUserRequest();
            updateUserRequest.setUserId(mUser.getId());
            updateUserRequest.setFirstName(mFullNameText.getFirstName());
            updateUserRequest.setLastName(mFullNameText.getLastName());
            updateUserRequest.setEmail(mEmailText.getEmail());
            updateUserRequest.setPhone(mPhoneText.getPhoneNumber());

            dataManager.updateUser(
                    updateUserRequest,
                    mUser.getAuthToken(),
                    new FragmentSafeCallback<User>(this)
                    {
                        @Override
                        public void onCallbackSuccess(final User user)
                        {
                            bus.post(new LogEvent.AddLogEvent(new AccountLog.ContactInfoUpdateSuccess()));

                            userManager.setCurrentUser(user);
                            mUser = userManager.getCurrentUser();
                            updateDisplay();
                            progressDialog.dismiss();
                            Toast.makeText(
                                    getContext(),
                                    R.string.info_updated,
                                    Toast.LENGTH_SHORT
                            ).show();
                        }

                        @Override
                        public void onCallbackError(final DataManager.DataManagerError error)
                        {
                            bus.post(new LogEvent.AddLogEvent(new AccountLog.ContactInfoUpdateError()));

                            progressDialog.dismiss();
                            dataManagerErrorHandler.handleError(
                                    getActivity(),
                                    error
                            );
                        }
                    }
            );
        }
    }

    private boolean validateFields()
    {
        return mFullNameText.validate() & mEmailText.validate() & mPhoneText.validate();
    }
}
