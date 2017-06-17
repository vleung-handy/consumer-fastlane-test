package com.handybook.handybook.onboarding;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.handybook.handybook.BuildConfig;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.core.BaseApplication;
import com.handybook.handybook.core.EnvironmentModifier;
import com.handybook.handybook.core.constant.PrefsKey;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.manager.DefaultPreferencesManager;
import com.handybook.handybook.core.manager.ServicesManager;
import com.handybook.handybook.core.model.response.UserExistsResponse;
import com.handybook.handybook.core.ui.activity.LoginActivity;
import com.handybook.handybook.core.ui.activity.SplashActivity;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.library.util.TextWatcherAdapter;
import com.handybook.handybook.library.util.Utils;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.OnboardingLog;
import com.squareup.otto.Subscribe;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.handybook.handybook.core.constant.BundleKeys.ZIP;
import static com.handybook.handybook.core.constant.RequestCode.LOGIN_FROM_ONBOARDING;

/**
 * This is the new onboarding fragment that is supposed to prompt the user for zip and email.
 * If the email is an existing user, we prompt them to login. Otherwise, if we have services
 * supporting the user's zip, we display those. If we don't support that zip, the user will
 * be brought to {@link ServiceNotSupportedActivity}.
 *
 * The ZIP will always be stored locally in shared prefs. The email will only be stored if it's a
 * new user to Handy.
 */
public class OnboardV2Fragment extends InjectedFragment {

    private static final int ZIP_MIN_LENGTH = 5;

    // Threshold for minimal keyboard height.
    final int MIN_KEYBOARD_HEIGHT_PX = 150;

    @BindView(R.id.onboard_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.onboard_appbar)
    AppBarLayout mAppBar;

    @BindView(R.id.onboard_collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;

    @BindView(R.id.onboard_already_have_account)
    TextView mAlreadyHaveAccount;

    @BindView(R.id.onboard_edit_zip)
    TextInputEditText mEditZip;

    @BindView(R.id.onboard_edit_email)
    TextInputEditText mEditEmail;

    @BindView(R.id.onboard_view_switcher)
    ViewSwitcher mViewSwitcher;

    @BindView(R.id.pinned_layout)
    LinearLayout mPinnedLayout;

    @BindView(R.id.onboard_signin)
    Button mButtonSignIn;

    @BindView(R.id.onboard_button_next)
    Button mNextButton;

    @BindView(R.id.onboard_button_submit)
    Button mSubmitButton;

    @BindView(R.id.onboard_email)
    View mEmailView;

    @BindView(R.id.onboard_zip)
    View mZipView;

    @Inject
    protected ServicesManager mServicesManager;
    @Inject
    DefaultPreferencesManager mDefaultPreferencesManager;
    @Inject
    EnvironmentModifier mEnvironmentModifier;

    private Animation mSlideInFromRight;
    private Animation mSlideOutToLeft;
    private Animation mSlideOutToRight;
    private Animation mSlideInFromLeft;
    private boolean mExpanded = true;
    private List<Service> mServices;
    private String mZip;
    private String mEmail;
    private String mZipCodeString;
    private String mEmailString;
    private int mSignInOriginalLeft;
    private int mPadding;

    public static OnboardV2Fragment newInstance() {
        return new OnboardV2Fragment();
    }

    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_onboard_v2, container, false);
        ButterKnife.bind(this, view);
        mEditZip.clearFocus();
        registerKeyboardListener();

        mCollapsingToolbar.setTitleEnabled(false);
        disableAppBarDragging();
        mPadding = getResources().getDimensionPixelSize(R.dimen.default_padding);

        mSlideInFromRight = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right);
        mSlideOutToLeft = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_left);
        mSlideInFromLeft = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_left);
        mSlideOutToRight = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_right);
        mZipCodeString = getString(R.string.zip_code);
        mEmailString = getString(R.string.email);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mEditZip.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(final Editable s) {
                if (s.toString().trim().length() >= ZIP_MIN_LENGTH) {
                    mNextButton.setEnabled(true);
                }
                else {
                    mNextButton.setEnabled(false);
                }
            }
        });

        mEditZip.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(
                    final TextView v,
                    final int actionId,
                    final KeyEvent event
            ) {
                if (actionId == EditorInfo.IME_ACTION_DONE && mNextButton.isEnabled()) {
                    nextClicked();
                    return true;
                }
                return false;
            }
        });

        mEditEmail.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(final Editable s) {
                final String email = s.toString().trim();
                if (email.matches(Utils.EMAIL_VALIDATION_REGEX)) {
                    mSubmitButton.setEnabled(true);
                }
                else {
                    mSubmitButton.setEnabled(false);
                }
            }
        });

        mEditEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(
                    final TextView v,
                    final int actionId,
                    final KeyEvent event
            ) {
                if (actionId == EditorInfo.IME_ACTION_DONE && mSubmitButton.isEnabled()) {
                    emailSubmitClicked();
                    return true;
                }
                return false;
            }
        });

        /*
        Needs the toolbar to intercept the touch event
         */
        mToolbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, final MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (isWithinButtonBounds(event.getX(), event.getY())) {
                        mButtonSignIn.performClick();
                    }
                }
                return false;
            }
        });

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        //try to prepopulate the zip fields with any data we already have.
        mEditZip.setText(mDefaultPreferencesManager.getString(PrefsKey.ZIP));
        mEditEmail.setText(mDefaultPreferencesManager.getString(PrefsKey.EMAIL));

        return view;
    }

    @OnClick(R.id.onboard_image_logo)
    public void launchEnvironmentSelector() {
        if (!BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_STAGE)) {
            return;
        }
        final EditText input = new EditText(getContext());
        input.setText(mEnvironmentModifier.getEnvironmentPrefix());
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.set_environment)
                .setView(input)
                .setPositiveButton(R.string.set, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // change the environment and update the menu text
                        mEnvironmentModifier.setEnvironmentPrefix(input.getText().toString());
                        Intent intent = new Intent(getContext(), SplashActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show();
    }

    /**
     * Disables the app bar from letting the user drag to expand/collapse the toolbar
     */
    private void disableAppBarDragging() {
        // Disable "Drag" for AppBarLayout (i.e. User can't scroll appBarLayout by directly touching appBarLayout - User can only scroll appBarLayout by only using scrollContent)
        if (mAppBar.getLayoutParams() != null) {
            CoordinatorLayout.LayoutParams layoutParams
                    = (CoordinatorLayout.LayoutParams) mAppBar.getLayoutParams();
            AppBarLayout.Behavior appBarLayoutBehaviour = new AppBarLayout.Behavior();
            appBarLayoutBehaviour.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
                @Override
                public boolean canDrag(AppBarLayout appBarLayout) {
                    return false;
                }
            });
            layoutParams.setBehavior(appBarLayoutBehaviour);
        }

    }

    /**
     * Checks whether the x, y coordinates passed in are within the bounds of the "sign in" button.
     * The button moves to different locations depending on whether or not the keyboard is showing.
     *
     * @param x
     * @param y
     * @return
     */
    private boolean isWithinButtonBounds(float x, float y) {
        int[] location = new int[2];
        mButtonSignIn.getLocationOnScreen(location);

        return x > location[0]
               && x < location[0] + mButtonSignIn.getWidth()
               && y > Math.max(location[1] - mPadding, 0)
               && y < location[1] + mButtonSignIn.getHeight();

    }

    @OnClick(R.id.onboard_button_next)
    public void nextClicked() {
        mZip = mEditZip.getText().toString();
        mEmail = null;
        requestForServices(mZip);
        showNext();
    }

    @Subscribe
    public void onReceiveServicesSuccess(final BookingEvent.ReceiveServicesSuccess event) {
        onServicesReceived(event.getServices(), event.getZip());
    }

    @VisibleForTesting
    public void onServicesReceived(@NonNull final List<Service> services, final String zip) {
        mServices = services;

        if (!mServices.isEmpty()) {
            //only commit this to shared prefs when we know there are services available for this zip
            mDefaultPreferencesManager.setString(PrefsKey.ZIP, mZip);
        }

        bus.post(new LogEvent.AddLogEvent(new OnboardingLog.ZipSubmittedLog(
                zip, Locale.getDefault().toString()
        )));

        //it could be possible that this zip response comes after the user enters email.
        //if that is the case, we need to call userCreateLead();
        userCreateLead();
    }

    @Subscribe
    public void onReceiveServicesError(final BookingEvent.ReceiveServicesError error) {
        dataManagerErrorHandler.handleError(getActivity(), error.error);
    }

    private void requestForServices(final String zip) {
        mServices = null;

        mServicesManager.requestServices(zip, false);
    }

    private void showNext() {
        mToolbar.setTitle(mEmailString);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mViewSwitcher.setInAnimation(mSlideInFromRight);
        mViewSwitcher.setOutAnimation(mSlideOutToLeft);
        mViewSwitcher.showNext();
    }

    private void showPrevious() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mToolbar.setTitle(mZipCodeString);
        mViewSwitcher.setInAnimation(mSlideInFromLeft);
        mViewSwitcher.setOutAnimation(mSlideOutToRight);
        mViewSwitcher.showPrevious();
    }

    /**
     * Returns true if it's been handled.
     * @return
     */
    public boolean onBackPressed() {
        if (mViewSwitcher.getCurrentView() == mEmailView) {
            //we just want to go back to zip
            showPrevious();
            return true;
        }
        else {
            //we're backing out of the zip, which is the same as exiting the app.
            getActivity().finish();
            return false;
        }
    }

    @OnClick(R.id.onboard_signin)
    public void signinClicked() {
        redirectToLogin();
    }

    private void redirectToLogin() {
        final Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.putExtra(LoginActivity.EXTRA_FROM_ONBOARDING, true);
        startActivityForResult(intent, LOGIN_FROM_ONBOARDING);
    }

    @OnClick(R.id.onboard_button_submit)
    public void emailSubmitClicked() {
        //mark onboarding shown, so we don't show the old one, even if the config eventually gets turned off.
        mDefaultPreferencesManager.setBoolean(PrefsKey.APP_ONBOARD_SHOWN, true);
        mEmail = mEditEmail.getText().toString();
        mDefaultPreferencesManager.setString(PrefsKey.EMAIL, mEmail);
        userCreateLead();
    }

    private void userCreateLead() {
        //we only do this step if we have both the email and the zip response. If the zip
        //response is received, mServices will not equal to null
        if (mServices != null && !TextUtils.isEmpty(mEmail)) {
            dataManager.userCreateLead(
                    mEmail,
                    mZip,
                    new DataManager.Callback<UserExistsResponse>() {
                        @Override
                        public void onSuccess(final UserExistsResponse response) {
                            bus.post(new LogEvent.AddLogEvent(new OnboardingLog.EmailCollectedLog(
                                    mEmail
                            )));

                            handleEmailResponse(response);
                        }

                        @Override
                        public void onError(final DataManager.DataManagerError error) {
                            dataManagerErrorHandler.handleError(getActivity(), error);
                        }
                    }
            );
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (userManager.isUserLoggedIn()) {
            //don't need to show this screen if the user is already logged in.
            getActivity().finish();
        }
    }

    /**
     * IF the email already exists, redirect to login page
     * ELSE IF we don't support this zip, redirect to {@link ServiceNotSupportedActivity}
     * ELSE IF we support this zip, redirect to new home page
     *
     * @param emailResponse
     */
    @VisibleForTesting
    public void handleEmailResponse(UserExistsResponse emailResponse) {
        if (emailResponse.exists()) {
            //user exists, go to login
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.putExtra(LoginActivity.EXTRA_BOOKING_EMAIL, mEmail);
            intent.putExtra(LoginActivity.EXTRA_BOOKING_USER_NAME, emailResponse.getFirstName());
            intent.putExtra(LoginActivity.EXTRA_FROM_ONBOARDING, true);
            startActivityForResult(intent, LOGIN_FROM_ONBOARDING);
        }
        else {
            //mark onboarding shown, so we don't show the old one, even if the config eventually gets turned off.
            //we only store the email after we know this email doesn't already exist.
            mDefaultPreferencesManager.setBoolean(PrefsKey.APP_ONBOARD_SHOWN, true);
            mDefaultPreferencesManager.setString(PrefsKey.EMAIL, mEmail);

            if (mServices == null || mServices.isEmpty()) {
                //we don't support this zip
                Intent intent = new Intent(getActivity(), ServiceNotSupportedActivity.class);
                intent.putExtra(ZIP, mZip);
                startActivity(intent);
            }
            else {
                //we do support this zip, just go to home page without prompting to login
                startActivity(new Intent(getActivity(), ServiceCategoriesActivity.class));
            }
        }
    }

    /**
     * Android doesn't have an API for this, this is an approximation
     * https://pspdfkit.com/blog/2016/keyboard-handling-on-android/
     */
    private void registerKeyboardListener() {
        // Register global layout listener.
        final View decorView = getActivity().getWindow().getDecorView();
        decorView.getViewTreeObserver()
                 .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                     private final Rect windowVisibleDisplayFrame = new Rect();
                     private int lastVisibleDecorViewHeight;

                     @Override
                     public void onGlobalLayout() {
                         // Retrieve visible rectangle inside window.
                         decorView.getWindowVisibleDisplayFrame(windowVisibleDisplayFrame);
                         final int visibleDecorViewHeight = windowVisibleDisplayFrame.height();

                         // Decide whether keyboard is visible from changing decor view height.
                         if (lastVisibleDecorViewHeight != 0) {
                             if (lastVisibleDecorViewHeight >
                                 visibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX) {
                                 // Calculate current keyboard height (this includes also navigation bar height when in fullscreen mode).
                                 int currentKeyboardHeight = decorView.getHeight() -
                                                             windowVisibleDisplayFrame.bottom;
                                 // Notify listener about keyboard being shown.
                                 //                        listener.onKeyboardShown(currentKeyboardHeight);
                                 collapse();
                             }
                             else if (lastVisibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX <
                                      visibleDecorViewHeight) {
                                 // Notify listener about keyboard being hidden.
                                 expand();
                             }
                         }
                         // Save current decor view height for the next call.
                         lastVisibleDecorViewHeight = visibleDecorViewHeight;
                     }
                 });
    }

    private void expand() {
        if (!mExpanded) {
            mExpanded = true;
            mAppBar.setExpanded(true);

            mToolbar.setVisibility(View.INVISIBLE);
            mToolbar.setAlpha(0);

            mAlreadyHaveAccount.setVisibility(View.VISIBLE);

            mButtonSignIn.animate()
                         .translationX(mSignInOriginalLeft - mButtonSignIn.getLeft())
                         .start();

        }
    }

    private void collapse() {
        if (mExpanded) {
            mExpanded = false;

            mToolbar.setVisibility(View.VISIBLE);
            mToolbar.animate()
                    .alpha(1)
                    .start();

            mAlreadyHaveAccount.setVisibility(View.INVISIBLE);

            mAppBar.setExpanded(false);

            int newLeft = mPinnedLayout.getRight() - mButtonSignIn.getWidth();
            mSignInOriginalLeft = mButtonSignIn.getLeft();
            mButtonSignIn.animate()
                         .translationX(newLeft - mSignInOriginalLeft)
                         .start();

        }
    }
}
