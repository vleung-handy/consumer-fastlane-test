package com.handybook.handybook.core.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.BookingRequest;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.booking.ui.fragment.BookingFlowFragment;
import com.handybook.handybook.bottomnav.BottomNavActivity;
import com.handybook.handybook.core.MainNavTab;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.constant.PrefsKey;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.callback.FragmentSafeCallback;
import com.handybook.handybook.core.event.HandyEvent;
import com.handybook.handybook.core.manager.DefaultPreferencesManager;
import com.handybook.handybook.core.manager.UserDataManager;
import com.handybook.handybook.core.model.response.UserExistsResponse;
import com.handybook.handybook.core.ui.activity.LoginActivity;
import com.handybook.handybook.core.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.core.ui.widget.EmailInputTextView;
import com.handybook.handybook.core.ui.widget.MenuButton;
import com.handybook.handybook.core.ui.widget.PasswordInputTextView;
import com.handybook.handybook.library.util.ValidationUtils;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.booking.BookingFunnelLog;
import com.handybook.handybook.logger.handylogger.model.user.UserContactLog;
import com.handybook.handybook.logger.handylogger.model.user.UserLoginLog;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.handybook.handybook.core.constant.ActivityResult.LOGIN_FINISH;

public final class LoginFragment extends BookingFlowFragment
{
    public static final String EXTRA_FROM_BOOKING_FUNNEL = "com.handy.handy.EXTRA_FROM_BOOKING_FUNNEL";
    public static final String EXTRA_FROM_ONBOARDING = "com.handy.handy.EXTRA_FROM_ONBOARDING";
    static final String EXTRA_FIND_USER = "com.handy.handy.EXTRA_FIND_USER";
    static final String EXTRA_BOOKING_USER_NAME = "com.handy.handy.EXTRA_BOOKING_USER_NAME";
    static final String EXTRA_BOOKING_EMAIL = "com.handy.handy.EXTRA_BOOKING_EMAIL";
    private static final String STATE_EMAIL_HIGHLIGHT = "EMAIL_HIGHLIGHT";
    private static final String STATE_PASSWORD_HIGHLIGHT = "PASSWORD_HIGHLIGHT";

    CallbackManager callbackManager;
    //private UiLifecycleHelper mUiHelper;
    private boolean mHandleFBSessionUpdates = false;
    private boolean mFindUser;
    private boolean mIsFromBookingFunnel;
    private boolean mIsFromOnboarding;
    private String mBookingUserName, mBookingUserEmail;
    private BookingRequest mBookingRequest;

    Class<? extends Activity> mDestinationClass;

    @Bind(R.id.nav_text)
    TextView mNavText;
    @Bind(R.id.login_button)
    Button mLoginButton;
    @Bind(R.id.forgot_button)
    Button mForgotButton;
    @Bind(R.id.email_text)
    EmailInputTextView mEmailText;
    @Bind(R.id.password_text)
    PasswordInputTextView mPasswordText;
    //@Bind(R.id.fb_button)
    //LoginButton fbButton;
    @Bind(R.id.fb_login_button)
    LoginButton mFbLoginButton;
    @Bind(R.id.fb_layout)
    View mFbLayout;
    @Bind(R.id.or_text)
    TextView mOrText;
    @Bind(R.id.welcome_text)
    TextView mWelcomeText;
    @Bind(R.id.menu_button_layout)
    ViewGroup mMenuButtonLayout;
    @Bind(R.id.login_scroll_view)
    ScrollView mLoginScrollView;

    @Inject
    DefaultPreferencesManager mDefaultPreferencesManager;

    private ViewTreeObserver.OnGlobalLayoutListener mAutoScrollListener;

    private Bundle mDestinationExtras;

    public static LoginFragment newInstance(
            final boolean findUser,
            final String bookingUserName,
            final String bookingUserEmail,
            boolean fromBookingFunnel,
            boolean fromOnboarding
    )
    {
        final LoginFragment fragment = new LoginFragment();
        final Bundle args = new Bundle();

        args.putBoolean(EXTRA_FROM_BOOKING_FUNNEL, fromBookingFunnel);
        args.putBoolean(EXTRA_FROM_ONBOARDING, fromOnboarding);
        args.putBoolean(EXTRA_FIND_USER, findUser);
        args.putString(EXTRA_BOOKING_USER_NAME, bookingUserName);
        args.putString(EXTRA_BOOKING_EMAIL, bookingUserEmail);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        callbackManager = CallbackManager.Factory.create();

        mBookingRequest = bookingManager.getCurrentRequest();
        //mUiHelper = new UiLifecycleHelper(getActivity(), statusCallback);
        //mUiHelper.onCreate(savedInstanceState);
        mFindUser = getArguments().getBoolean(EXTRA_FIND_USER);
        mBookingUserName = getArguments().getString(EXTRA_BOOKING_USER_NAME);
        mBookingUserEmail = getArguments().getString(EXTRA_BOOKING_EMAIL);

        if (TextUtils.isEmpty(mBookingUserEmail))
        {
            //if there is no email passed in, look in shared prefs
            mBookingUserEmail = mDefaultPreferencesManager.getString(PrefsKey.EMAIL);
        }

        mIsFromBookingFunnel = getArguments().getBoolean(EXTRA_FROM_BOOKING_FUNNEL);
        mIsFromOnboarding = getArguments().getBoolean(EXTRA_FROM_ONBOARDING);

        String mDestinationActivity = getActivity().getIntent().getStringExtra(BundleKeys.ACTIVITY);
        mDestinationExtras = getActivity().getIntent().getExtras();
        if (!TextUtils.isEmpty(mDestinationActivity))
        {
            try
            {
                mDestinationClass = (Class<? extends Activity>) Class.forName(mDestinationActivity);
            }
            catch (ClassNotFoundException e)
            {
                e.printStackTrace();
            }
        }

        if (mIsFromBookingFunnel)
        {
            bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.UserContactShownLog()));
            bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.UserLoginShownLog(UserLoginLog.AUTH_TYPE_EMAIL)));
            bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.UserLoginShownLog(UserLoginLog.AUTH_TYPE_FACEBOOK)));
        }
        else
        {
            bus.post(new LogEvent.AddLogEvent(new UserContactLog.UserContactShownLog()));
            bus.post(new LogEvent.AddLogEvent(new UserLoginLog.UserLoginShownLog(UserLoginLog.AUTH_TYPE_EMAIL)));
            bus.post(new LogEvent.AddLogEvent(new UserLoginLog.UserLoginShownLog(UserLoginLog.AUTH_TYPE_FACEBOOK)));
        }
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState
    )
    {
        final View view = getActivity().getLayoutInflater()
                                       .inflate(R.layout.fragment_login, container, false);

        ButterKnife.bind(this, view);

        mEmailText.clearFocus();
        final MenuDrawerActivity activity = (MenuDrawerActivity) getActivity();

        if (mFindUser)
        {
            activity.setDrawerDisabled(true);
            mNavText.setText(getString(R.string.contact));
            mPasswordText.setVisibility(View.GONE);
            mForgotButton.setVisibility(View.GONE);
            mLoginButton.setText(getString(R.string.next));
            mEmailText.setText(mBookingRequest.getEmail());
        }
        else if (mBookingUserEmail != null)
        {
            activity.setDrawerDisabled(true);
            mFbLayout.setVisibility(View.GONE);
            mOrText.setVisibility(View.GONE);
            mEmailText.setText(mBookingUserEmail);
            if (ValidationUtils.isNullOrEmpty(mBookingUserName))
            {
                mWelcomeText.setVisibility(View.GONE);
            }
            else
            {
                mWelcomeText.setText(getString(R.string.welcome_back, mBookingUserName));
                mWelcomeText.setVisibility(View.VISIBLE);
            }
            //NOTE: mBookingRequest could be null if this is a login coming from the onboarding process
            if (mBookingRequest != null)
            {
                mBookingRequest.setEmail(mBookingUserEmail);
            }
        }
        else
        {
            final MenuButton menuButton = new MenuButton(getActivity(), mMenuButtonLayout);
            menuButton.setColor(ContextCompat.getColor(getContext(), R.color.white));
            mMenuButtonLayout.addView(menuButton);
        }

        mFbLoginButton.setFragment(this);
        mFbLoginButton.setReadPermissions("public_profile", "email", "user_friends");

        // Callback registration
        mFbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(final LoginResult loginResult)
            {
                final AccessToken accessToken = loginResult.getAccessToken();
                bus.post(new HandyEvent.RequestAuthFacebookUser(accessToken, null));
            }

            @Override
            public void onCancel()
            {
                assert true;
                //TODO: Handle case when user cancels Facebook login
            }

            @Override
            public void onError(FacebookException exception)
            {
                Crashlytics.logException(exception);
                progressDialog.dismiss();
                enableInputs();
                toast.setText(R.string.default_error_string);
                toast.show();
            }
        });

        mAutoScrollListener = new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                mLoginScrollView.smoothScrollTo(0, mLoginScrollView.getBottom());
            }
        };

        mLoginScrollView.getViewTreeObserver().addOnGlobalLayoutListener(mAutoScrollListener);

        return view;
    }

    @Override
    public final void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null)
        {
            if (savedInstanceState.getBoolean(STATE_EMAIL_HIGHLIGHT)) { mEmailText.highlight(); }
            if (savedInstanceState.getBoolean(STATE_PASSWORD_HIGHLIGHT))
            {
                mPasswordText.highlight();
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        mLoginButton.setOnClickListener(loginClicked);
        mForgotButton.setOnClickListener(forgotClicked);
    }

    @Override
    public final void onResume()
    {
        super.onResume();
        //mUiHelper.onResume();
        mHandleFBSessionUpdates = true;
    }

    @Override
    public final void onPause()
    {
        super.onPause();
        //mUiHelper.onPause();
        mHandleFBSessionUpdates = false;
    }

    @Override
    public void onDestroyView()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        {
            mLoginScrollView.getViewTreeObserver()
                            .removeOnGlobalLayoutListener(mAutoScrollListener);
        }
        else
        {
            mLoginScrollView.getViewTreeObserver()
                            .removeGlobalOnLayoutListener(mAutoScrollListener);
        }
        super.onDestroyView();
    }

    @Override
    public final void onDestroy()
    {
        super.onDestroy();
        //mUiHelper.onDestroy();
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState)
    {
        super.onSaveInstanceState(outState);
        //mUiHelper.onSaveInstanceState(outState);
        outState.putBoolean(STATE_EMAIL_HIGHLIGHT, mEmailText.isHighlighted());
        outState.putBoolean(STATE_PASSWORD_HIGHLIGHT, mPasswordText.isHighlighted());
    }

    @Override
    public final void onActivityResult(
            final int requestCode, final int resultCode,
            final Intent data
    )
    {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        //mUiHelper.onActivityResult(requestCode, resultCode, data);
    }

    private boolean validateFields()
    {
        if (!mEmailText.validate()) { return false; }
        return !(!mFindUser && !mPasswordText.validate());
    }

    @Override
    protected final void disableInputs()
    {
        super.disableInputs();
        mLoginButton.setClickable(false);
        mForgotButton.setClickable(false);

        final InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEmailText.getWindowToken(), 0);
    }

    @Override
    protected final void enableInputs()
    {
        super.enableInputs();
        mLoginButton.setClickable(true);
        mForgotButton.setClickable(true);
    }

    private final View.OnClickListener loginClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(final View view)
        {
            if (validateFields())
            {
                disableInputs();
                progressDialog.show();

                final String email = mEmailText.getEmail();
                if (mIsFromBookingFunnel)
                {
                    bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.UserLoginSubmittedLog(
                            email,
                            UserLoginLog.AUTH_TYPE_EMAIL
                    )));
                }
                else
                {
                    bus.post(new LogEvent.AddLogEvent(new UserLoginLog.UserLoginSubmittedLog(
                            email,
                            UserLoginLog.AUTH_TYPE_EMAIL
                    )));
                }

                if (mFindUser)
                {
                    dataManager.getUserExists(email, new FragmentSafeCallback<UserExistsResponse>(LoginFragment.this)
                    {
                        @Override
                        public void onCallbackSuccess(final UserExistsResponse userExistsResponse)
                        {
                            if (!allowCallbacks)
                            { return; }

                            if (userExistsResponse.exists())
                            {
                                final Intent intent = new Intent(
                                        getActivity(),
                                        LoginActivity.class
                                );
                                intent.putExtra(LoginActivity.EXTRA_BOOKING_EMAIL, email);
                                intent.putExtra(
                                        LoginActivity.EXTRA_BOOKING_USER_NAME,
                                        userExistsResponse.getFirstName()
                                );
                                startActivityForResult(intent, LOGIN_FINISH);

                                progressDialog.dismiss();
                                enableInputs();
                            }
                            else
                            {

                                //this is when the user doesn't already exist.
                                bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.EmailCollectedLog(
                                        email
                                )));

                                mBookingRequest.setEmail(email);
                                continueBookingFlow();
                            }
                        }

                        @Override
                        public void onCallbackError(DataManager.DataManagerError error)
                        {
                            if (!allowCallbacks)
                            {
                                return;
                            }
                            dataManagerErrorHandler.handleError(getActivity(), error);
                            enableInputs();
                            progressDialog.dismiss();

                        }
                    });
                }
                else
                {
                    bus.post(new HandyEvent.RequestAuthUser(email, mPasswordText.getPassword()));
                }
            }
        }
    };

    private final View.OnClickListener forgotClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(final View view)
        {
            mEmailText.unHighlight();
            mPasswordText.unHighlight();
            if (mEmailText.validate())
            {
                disableInputs();
                progressDialog.show();

                dataManager.requestPasswordReset(
                        mEmailText.getText().toString(),
                        new FragmentSafeCallback<String>(LoginFragment.this)
                        {
                            @Override
                            public void onCallbackSuccess(final String response)
                            {
                                if (!allowCallbacks) { return; }
                                progressDialog.dismiss();
                                enableInputs();

                                toast.setText(Html.fromHtml(response).toString());
                                toast.show();
                            }

                            @Override
                            public void onCallbackError(final DataManager.DataManagerError error)
                            {
                                if (!allowCallbacks) { return; }
                                progressDialog.dismiss();
                                enableInputs();
                                dataManagerErrorHandler.handleError(getActivity(), error);
                            }
                        }
                );
            }
        }
    };


    @Subscribe
    public void onReceiveAuthUserSuccess(final HandyEvent.ReceiveAuthUserSuccess event)
    {
        final User user = event.getUser();
        final UserDataManager.AuthType authType = event.getAuthType();
        bus.post(new HandyEvent.RequestUser(user.getId(), user.getAuthToken(), authType));
    }

    @Subscribe
    public void onReceiveAuthUserError(final HandyEvent.ReceiveAuthUserError event)
    {
        handleUserCallbackError(event.error, event.getAuthType());
    }

    private
    @Nullable
    String getUserZip(User user)
    {

        if (user != null && user.getAddress() != null)
        {
            return user.getAddress().getZip();
        }

        return null;
    }

    @Subscribe
    public void onReceiveUserSuccess(final HandyEvent.ReceiveUserSuccess event)
    {
        final UserDataManager.AuthType authType = event.getAuthType();
        String authTypeForLogger = getAuthTypeForLogger(authType);

        if (mIsFromBookingFunnel)
        {
            bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.UserLoginSuccessLog(
                    authTypeForLogger)));
            bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.UserLoginShownLog(
                    authTypeForLogger)));
        }
        else
        {
            bus.post(new LogEvent.AddLogEvent(new UserLoginLog.UserLoginSuccessLog(authTypeForLogger)));
            bus.post(new LogEvent.AddLogEvent(new UserLoginLog.UserLoginShownLog(authTypeForLogger)));
        }

        //the fact that the user is logged in guarantees at least email and zip information
        mDefaultPreferencesManager.setString(PrefsKey.ZIP, getUserZip(event.getUser()));

        //storing of EMAIl in shared prefs is only used for users that are not logged in. Now that
        //the user is logged in, it' safe to remove this.
        mDefaultPreferencesManager.removeValue(PrefsKey.EMAIL);

        mConfigurationManager.invalidateCache();

        if (!mIsFromOnboarding &&
                (mBookingUserName != null ||
                        authType == UserDataManager.AuthType.FACEBOOK && mFindUser))
        {
            continueBookingFlow();
            return;
        }

        progressDialog.dismiss();
        enableInputs();

        //TODO refactor
        if(mConfigurationManager.getPersistentConfiguration().isBottomNavEnabled()
                || !(getActivity() instanceof MenuDrawerActivity)
            //in case bottom nav config flag gets set to true after LoginFragment
            // already instantiated by LoginActivity
            // (which is currently a MenuDrawerActivity)
                )
        {
            //TODO consolidate this logic
            Intent intent;
            if(mDestinationClass != null)
            {
                //destination class could be BookingDetailActivity
                intent = new Intent(getActivity(), mDestinationClass);
                intent.putExtras(mDestinationExtras);
            }
            else
            {
                intent = new Intent(getActivity(), BottomNavActivity.class);
                intent.putExtra(BottomNavActivity.BUNDLE_KEY_TAB, MainNavTab.SERVICES);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            getActivity().startActivity(intent);
        }
        else if (hasStoredZip())
        {
            //This is a case of login from Onboarding v2 -- whether it's a successful login from
            //onboarding, or from the booking process, we direct the user to the home page for a
            //clean start.
            bookingManager.clear();
            getActivity().setResult(LOGIN_FINISH);
            Intent intent = new Intent(getActivity(), ServiceCategoriesActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            getActivity().startActivity(intent);
        }
        else
        {
            final MenuDrawerActivity activity = (MenuDrawerActivity) getActivity();

            //TODO want to consolidate with above, but keeping this here for now until we dig into what MenuDrawerActivity.navigateToActivity does
            if (mDestinationClass != null)
            {
                activity.navigateToActivity(mDestinationClass, getActivity().getIntent().getExtras(),
                                            null
                );
            }
            else
            {
                activity.navigateToActivity(ServiceCategoriesActivity.class, R.id.nav_menu_home);
            }
        }

    }

    @Subscribe
    public void onReceiveUserError(final HandyEvent.ReceiveUserError event)
    {
        handleUserCallbackError(event.error, event.getAuthType());
    }

    @UserLoginLog.AuthType
    private String getAuthTypeForLogger(UserDataManager.AuthType authType)
    {
        if (authType == null) { return null; }
        switch (authType)
        {
            case FACEBOOK:
                return UserLoginLog.AUTH_TYPE_FACEBOOK;
            case EMAIL:
                return UserLoginLog.AUTH_TYPE_EMAIL;
        }
        return null;
    }

    private void handleUserCallbackError(
            final DataManager.DataManagerError error,
            final UserDataManager.AuthType authType
    )
    {
        String authTypeForLogger = getAuthTypeForLogger(authType);
        if (mIsFromBookingFunnel)
        {
            bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.UserLoginErrorLog(
                    authTypeForLogger,
                    error == null ? null : error
                            .getMessage()
            )));
        }
        else
        {
            bus.post(new LogEvent.AddLogEvent(new UserLoginLog.UserLoginErrorLog(
                    authTypeForLogger,
                    error == null ? null : error
                            .getMessage()
            )));
        }
        progressDialog.dismiss();
        enableInputs();

/*
        final Session session = Session.getActiveSession();
        if (session != null) session.closeAndClearTokenInformation();
*/

        dataManagerErrorHandler.handleError(getActivity(), error, null);
    }
}
