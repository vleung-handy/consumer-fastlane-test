package com.handybook.handybook.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.ui.activity.CancelRecurringBookingActivity;
import com.handybook.handybook.core.User;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.model.request.UpdateUserRequest;
import com.handybook.handybook.module.proteam.ui.activity.ProTeamActivity;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.ui.widget.EmailInputTextView;
import com.handybook.handybook.ui.widget.FullNameInputTextView;
import com.handybook.handybook.ui.widget.PasswordInputTextView;
import com.handybook.handybook.ui.widget.PhoneInputTextView;
import com.handybook.handybook.ui.widget.ThinIconButton;
import com.handybook.handybook.util.TextUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyTypefaceSpan;

public final class ProfileFragment extends InjectedFragment {
    private static final String STATE_FULLNAME_HIGHLIGHT = "FULLNAME_HIGHLIGHT";
    private static final String STATE_EMAIL_HIGHLIGHT = "EMAIL_HIGHLIGHT";
    private static final String STATE_PHONE_HIGHLIGHT = "PHONE_HIGHLIGHT";
    private static final String STATE_OLD_PWD_HIGHLIGHT = "OLD_PWD_HIGHLIGHT";
    private static final String STATE_NEW_PWD_HIGHLIGHT = "NEW_PWD_HIGHLIGHT";
    private static final String STATE_LOADED_USER = "LOADED_USER";

    private User user;
    private boolean loadedUserInfo;
    private boolean updatingInfo;

    @Bind(R.id.credits_text)
    TextView creditsText;
    @Bind(R.id.update_button)
    Button updateButton;
    @Bind(R.id.fullname_text)
    FullNameInputTextView fullNameText;
    @Bind(R.id.email_text)
    EmailInputTextView emailText;
    @Bind(R.id.phone_prefix_text)
    TextView phonePrefixText;
    @Bind(R.id.phone_text)
    PhoneInputTextView phoneText;
    @Bind(R.id.old_password_text)
    PasswordInputTextView oldPasswordtext;
    @Bind(R.id.new_password_text)
    PasswordInputTextView newPasswordtext;
    @Bind(R.id.cancel_cleaning_plan_button)
    ThinIconButton mCancelCleaningPlanButton;
    @Bind(R.id.profile_pro_team_button)
    ThinIconButton mProTeamButton;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = userManager.getCurrentUser();
    }

    private void showRecurringBookingsToCancel()
    {
        final Intent intent = new Intent(getActivity(), CancelRecurringBookingActivity.class);
        getActivity().startActivity(intent);
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_profile,container, false);

        ButterKnife.bind(this, view);

        mToolbar.setNavigationIcon(R.drawable.ic_menu);
        setupToolbar(mToolbar, getString(R.string.account));
        ((MenuDrawerActivity) getActivity()).setupHamburgerMenu(mToolbar);

        phoneText.setCountryCode(user.getPhonePrefix());
        oldPasswordtext.addTextChangedListener(passwordTextWatcher);
        newPasswordtext.addTextChangedListener(passwordTextWatcher);

        return view;
    }

    @Override
    public final void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(STATE_FULLNAME_HIGHLIGHT)) fullNameText.highlight();
            if (savedInstanceState.getBoolean(STATE_EMAIL_HIGHLIGHT)) emailText.highlight();
            if (savedInstanceState.getBoolean(STATE_PHONE_HIGHLIGHT)) phoneText.highlight();
            if (savedInstanceState.getBoolean(STATE_OLD_PWD_HIGHLIGHT)) oldPasswordtext.highlight();
            if (savedInstanceState.getBoolean(STATE_NEW_PWD_HIGHLIGHT)) newPasswordtext.highlight();
            loadedUserInfo = savedInstanceState.getBoolean(STATE_LOADED_USER);
        }
    }

    @Override
    public final void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateButton.setOnClickListener(updateClicked);
    }

    @Override
    public final void onStart() {
        super.onStart();
        if (!loadedUserInfo)
        {
            loadUserInfo();
        }
        showCancelCleaningPlanButtonIfApplicable();
    }

    @Override
    public final void onStop() {
        super.onStop();
        progressDialog.dismiss();
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_FULLNAME_HIGHLIGHT, fullNameText.isHighlighted());
        outState.putBoolean(STATE_EMAIL_HIGHLIGHT, emailText.isHighlighted());
        outState.putBoolean(STATE_PHONE_HIGHLIGHT, phoneText.isHighlighted());
        outState.putBoolean(STATE_OLD_PWD_HIGHLIGHT, oldPasswordtext.isHighlighted());
        outState.putBoolean(STATE_NEW_PWD_HIGHLIGHT, newPasswordtext.isHighlighted());
        outState.putBoolean(STATE_LOADED_USER, loadedUserInfo);
    }

    private void updateUserInfo() {
        String text = getString(R.string.you_have_credits);
        final int replaceIndex = text.indexOf("#");
        final String amount = TextUtils.formatPrice(user.getCredits(), user.getCurrencyChar(), null);

        text = text.replace("#", amount);

        final SpannableString spanText = new SpannableString(text);
        spanText.setSpan(new TextAppearanceSpan(getActivity(), R.style.TextView_XXLarge_Bold),
                replaceIndex, replaceIndex + amount.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        spanText.setSpan(new CalligraphyTypefaceSpan(TextUtils.get(getActivity(),
                        "CircularStd-Bold.otf")), replaceIndex, replaceIndex + amount.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        creditsText.setText(spanText, TextView.BufferType.SPANNABLE);

        fullNameText.unHighlight();

        final String firstName = user.getFirstName();
        final String lastName = user.getLastName();
        if (firstName != null && lastName != null) {
            fullNameText.setText(user.getFirstName() + " " + user.getLastName());
            fullNameText.setSelection(fullNameText.getText().length());
        }

        emailText.unHighlight();
        emailText.setText(user.getEmail());
        emailText.setSelection(emailText.getText().length());

        phonePrefixText.setText(user.getPhonePrefix() != null ? user.getPhonePrefix() : "+1");

        phoneText.unHighlight();
        phoneText.setCountryCode(phonePrefixText.getText().toString());
        phoneText.setText(user.getPhone());
        phoneText.setSelection(phoneText.getText().length());

        oldPasswordtext.unHighlight();
        oldPasswordtext.setText("");

        newPasswordtext.unHighlight();
        newPasswordtext.setText("");

        showCancelCleaningPlanButtonIfApplicable();
    }

    private void showCancelCleaningPlanButtonIfApplicable()
    {
        //only show cancel cleaning plan button if user has recurring bookings + config params on
        if(
                user.isRecurringCancellationsEnabled()
                && user.isRecurringCancellationsEmailFlowEnabled()
                && user.getAnalytics() != null
                && user.getAnalytics().getRecurringBookings() > 0)
        {
            mCancelCleaningPlanButton.setVisibility(View.VISIBLE);
            mCancelCleaningPlanButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(final View v)
                {
                    showRecurringBookingsToCancel();
                }
            });
        }
        else
        {
            mCancelCleaningPlanButton.setVisibility(View.GONE);
        }
    }

    private boolean validateFields() {
        boolean validate = true;
        if (!fullNameText.validate()) validate = false;
        if (!emailText.validate()) validate = false;
        if (!phoneText.validate()) validate = false;

        final String oldPwd = oldPasswordtext.getPassword();
        final String newPwd = newPasswordtext.getPassword();

        if (oldPwd.length() > 0 || newPwd.length() > 0) {
            if (!oldPasswordtext.validate()) validate = false;
            if (!newPasswordtext.validate()) validate = false;

            if (oldPwd.length() < 1 || newPwd.length() < 1) {
                validate = false;
                toast.setText(getString(R.string.update_pwd_error));
                toast.show();
            }
            else if (newPwd.length() < 8) {
                validate = false;
                newPasswordtext.highlight();
                toast.setText(getString(R.string.pwd_length_error));
                toast.show();
            }
            else if (!oldPasswordtext.validate()) {
                validate = false;
                toast.setText(getString(R.string.update_pwd_error));
                toast.show();
            }
        }

        return validate;
    }


    @Override
    protected final void disableInputs() {
        super.disableInputs();
        updateButton.setClickable(false);

        final InputMethodManager imm = (InputMethodManager)getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(fullNameText.getWindowToken(), 0);
    }

    @Override
    protected final void enableInputs() {
        super.enableInputs();
        updateButton.setClickable(true);
    }

    private void loadUserInfo() {
        updateUserInfo();
        disableInputs();
        progressDialog.show();
        dataManager.getUser(user.getId(), user.getAuthToken(), userCallback);
    }

    private final View.OnClickListener updateClicked = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            if (validateFields()) {
                disableInputs();
                progressDialog.show();

                UpdateUserRequest updateUserRequest = new UpdateUserRequest();
                updateUserRequest.setUserId(user.getId());
                updateUserRequest.setFirstName(fullNameText.getFirstName());
                updateUserRequest.setLastName(fullNameText.getLastName());
                updateUserRequest.setEmail(emailText.getEmail());
                updateUserRequest.setPhone(phoneText.getPhoneNumber());

                if (oldPasswordtext.getPassword().length() > 0
                        && newPasswordtext.getPassword().length() > 0) {
                    updateUserRequest.setCurrentPassword(oldPasswordtext.getPassword());
                    updateUserRequest.setPassword(newPasswordtext.getPassword());
                    updateUserRequest.setPasswordConfirmation(newPasswordtext.getPassword());
                }

                updatingInfo = true;
                dataManager.updateUser(updateUserRequest, user.getAuthToken(), userCallback);
            }
        }
    };

    private final DataManager.Callback<User> userCallback = new DataManager.Callback<User>() {
        @Override
        public void onSuccess(final User user) {
            if (!allowCallbacks) return;

            loadedUserInfo = true;
            userManager.setCurrentUser(user);
            ProfileFragment.this.user = userManager.getCurrentUser();
            updateUserInfo();
            progressDialog.dismiss();
            enableInputs();

            if (updatingInfo) {
                updatingInfo = false;
                toast.setText(getString(R.string.info_updated));
                toast.show();
            }
        }

        @Override
        public void onError(final DataManager.DataManagerError error) {
            if (!allowCallbacks) return;

            loadedUserInfo = true;
            progressDialog.dismiss();
            enableInputs();
            dataManagerErrorHandler.handleError(getActivity(), error);
        }
    };

    private final TextWatcher passwordTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(final CharSequence charSequence, final int start,
        final int count, final int after) {

        }

        @Override
        public void onTextChanged(final CharSequence charSequence, final int start,
        final int before, final int count) {
        }

        @Override
        public void afterTextChanged(final Editable editable) {
            if (oldPasswordtext.getText().toString().length() < 1
                    && newPasswordtext.getText().toString().length() < 1) {
                oldPasswordtext.unHighlight();
                newPasswordtext.unHighlight();
            }
        }
    };


    @OnClick(R.id.profile_pro_team_button)
    void onProTeamClicked()
    {
        final Intent intent = new Intent(getContext(), ProTeamActivity.class);
        getContext().startActivity(intent);
    }
}
