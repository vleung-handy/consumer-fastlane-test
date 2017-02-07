package com.handybook.handybook.onboarding;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.core.constant.PrefsKey;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.callback.FragmentSafeCallback;
import com.handybook.handybook.core.manager.DefaultPreferencesManager;
import com.handybook.handybook.core.model.response.UserExistsResponse;
import com.handybook.handybook.core.ui.activity.LoginActivity;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.library.util.TextWatcherAdapter;
import com.handybook.handybook.library.util.Utils;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.OnboardingLog;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.handybook.handybook.core.constant.BundleKeys.ZIP;
import static com.handybook.handybook.core.constant.RequestCode.LOGIN_FROM_ONBOARDING;

/**
 * This is the new onboarding fragment that is supposed to prompt the user for zip and email.
 * If the email is an existing user, we prompt them to login. Otherwise, if we have services
 * supporting the user's zip, we display those. If we don't support that zip, the user will
 * be brought to {@link NotSupportedActivity}.
 *
 * The ZIP will always be stored locally in shared prefs. The email will only be stored if it's a
 * new user to Handy.
 */
public class OnboardV2Fragment extends InjectedFragment implements AppBarLayout.OnOffsetChangedListener
{
    private static final String TAG = "OnboardV2Fragment";
    private static final int ZIP_MIN_LENGTH = 5;

    // Threshold for minimal keyboard height.
    final int MIN_KEYBOARD_HEIGHT_PX = 150;

    @Bind(R.id.onboard_toolbar)
    Toolbar mToolbar;

    @Bind(R.id.onboard_appbar)
    AppBarLayout mAppBar;

    @Bind(R.id.onboard_collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;

    @Bind(R.id.onboard_edit_zip)
    TextInputEditText mEditZip;

    @Bind(R.id.onboard_edit_email)
    TextInputEditText mEditEmail;

    @Bind(R.id.onboard_view_switcher)
    ViewSwitcher mViewSwitcher;

    @Bind(R.id.onboard_button_next)
    Button mNextButton;

    @Bind(R.id.onboard_button_submit)
    Button mSubmitButton;

    @Bind(R.id.onboard_email)
    View mEmailView;

    @Bind(R.id.onboard_zip)
    View mZipView;

    @Inject
    DefaultPreferencesManager mDefaultPreferencesManager;

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

    public static OnboardV2Fragment newInstance()
    {
        return new OnboardV2Fragment();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState
    )
    {
        View view = inflater.inflate(R.layout.fragment_onboard_v2, container, false);
        ButterKnife.bind(this, view);

        mEditZip.clearFocus();
        mAppBar.addOnOffsetChangedListener(this);
        registerKeyboardListener();

        mCollapsingToolbar.setTitleEnabled(false);

        mSlideInFromRight = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right);
        mSlideOutToLeft = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_left);
        mSlideInFromLeft = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_left);
        mSlideOutToRight = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_right);
        mZipCodeString = getString(R.string.zip_code);
        mEmailString = getString(R.string.email);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mEditZip.addTextChangedListener(new TextWatcherAdapter()
        {
            @Override
            public void afterTextChanged(final Editable s)
            {
                if (s.toString().trim().length() >= ZIP_MIN_LENGTH)
                {
                    mNextButton.setEnabled(true);
                }
                else
                {
                    mNextButton.setEnabled(false);
                }
            }
        });

        mEditZip.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(
                    final TextView v,
                    final int actionId,
                    final KeyEvent event
            )
            {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    nextClicked();
                    return true;
                }
                return false;
            }
        });

        mEditEmail.addTextChangedListener(new TextWatcherAdapter()
        {
            @Override
            public void afterTextChanged(final Editable s)
            {
                final String email = s.toString().trim();
                if (email.matches(Utils.EMAIL_VALIDATION_REGEX))
                {
                    mSubmitButton.setEnabled(true);
                }
                else
                {
                    mSubmitButton.setEnabled(false);
                }
            }
        });

        mEditEmail.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(
                    final TextView v,
                    final int actionId,
                    final KeyEvent event
            )
            {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    emailSubmitClicked();
                    return true;
                }
                return false;
            }
        });


        //try to prepopulate the zip fields with any data we already have.
        mEditZip.setText(mDefaultPreferencesManager.getString(PrefsKey.ZIP));
        mEditEmail.setText(mDefaultPreferencesManager.getString(PrefsKey.EMAIL));

        return view;
    }

    @OnClick(R.id.onboard_button_next)
    public void nextClicked()
    {
        mZip = mEditZip.getText().toString();
        mDefaultPreferencesManager.setString(PrefsKey.ZIP, mZip);
        mEmail = null;
        requestForServices(mZip);
        showNext();
    }

    private void requestForServices(final String zip)
    {
        mServices = null;
        dataManager.getServices(zip, new FragmentSafeCallback<List<Service>>(this)
        {
            @Override
            public void onCallbackSuccess(@NonNull final List<Service> services)
            {
                mServices = services;

                bus.post(new LogEvent.AddLogEvent(new OnboardingLog.ZipSubmittedLog(
                        zip, Locale.getDefault().toString()
                )));

                //it could be possible that this zip response comes after the user enters email.
                //if that is the case, we need to call userCreateLead();
                userCreateLead();
            }

            @Override
            public void onCallbackError(final DataManager.DataManagerError error)
            {
                dataManagerErrorHandler.handleError(getActivity(), error);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_onboarding, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item)
    {
        if (item.getItemId() == R.id.menu_sign_in)
        {
            redirectToLogin();
        }
        else if (item.getItemId() == android.R.id.home)
        {
            getActivity().onBackPressed();
        }

        return true;
    }

    private void showNext()
    {
        mToolbar.setTitle(mEmailString);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mViewSwitcher.setInAnimation(mSlideInFromRight);
        mViewSwitcher.setOutAnimation(mSlideOutToLeft);
        mViewSwitcher.showNext();
    }

    private void showPrevious()
    {
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
    public boolean onBackPressed()
    {
        if (mViewSwitcher.getCurrentView() == mEmailView)
        {
            //we just want to go back to zip
            showPrevious();
            return true;
        }
        else
        {
            //we're backing out of the zip, which is the same as exiting the app.
            getActivity().finish();
            return false;
        }
    }

    @OnClick(R.id.onboard_signin_1)
    public void signinClicked()
    {
        redirectToLogin();
    }

    private void redirectToLogin()
    {
        final Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivityForResult(intent, LOGIN_FROM_ONBOARDING);
    }

    @OnClick(R.id.onboard_button_submit)
    public void emailSubmitClicked()
    {
        mEmail = mEditEmail.getText().toString();
        userCreateLead();
    }

    private void userCreateLead()
    {
        //we only do this step if we have both the email and the zip response. If the zip
        //response is received, mServices will not equal to null
        if (mServices != null && !TextUtils.isEmpty(mEmail))
        {
            dataManager.userCreateLead(
                    mEmail,
                    mZip,
                    new DataManager.Callback<UserExistsResponse>()
                    {
                        @Override
                        public void onSuccess(final UserExistsResponse response)
                        {
                            bus.post(new LogEvent.AddLogEvent(new OnboardingLog.EmailCollectedLog(
                                    mEmail
                            )));

                            handleEmailResponse(response);
                        }

                        @Override
                        public void onError(final DataManager.DataManagerError error)
                        {
                            dataManagerErrorHandler.handleError(getActivity(), error);
                        }
                    }
            );
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (userManager.isUserLoggedIn())
        {
            //don't need to show this screen if the user is already logged in.
            getActivity().finish();
        }
    }

    /**
     * IF the email already exists, redirect to login page
     * ELSE IF we don't support this zip, redirect to {@link NotSupportedActivity}
     * ELSE IF we support this zip, redirect to new home page
     *
     * @param emailResponse
     */
    private void handleEmailResponse(UserExistsResponse emailResponse)
    {
        if (emailResponse.exists())
        {
            //user exists, go to login
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.putExtra(LoginActivity.EXTRA_BOOKING_EMAIL, mEmail);
            intent.putExtra(LoginActivity.EXTRA_BOOKING_USER_NAME, emailResponse.getFirstName());
            intent.putExtra(LoginActivity.EXTRA_FROM_ONBOARDING, true);
            startActivityForResult(intent, LOGIN_FROM_ONBOARDING);
        }
        else
        {
            //mark onboarding shown, so we don't show the old one, even if the config eventually gets turned off.
            //we only store the email after we know this email doesn't already exist.
            mDefaultPreferencesManager.setBoolean(PrefsKey.APP_ONBOARD_SHOWN, true);
            mDefaultPreferencesManager.setString(PrefsKey.EMAIL, mEmail);

            if (mServices.isEmpty())
            {
                //we don't support this zip
                Intent intent = new Intent(getActivity(), NotSupportedActivity.class);
                intent.putExtra(ZIP, mZip);
                startActivity(intent);
            }
            else
            {
                //we do support this zip, just go to home page without prompting to login
                startActivity(new Intent(getActivity(), ServiceCategoriesActivity.class));
            }
        }
    }

    /**
     * Android doesn't have an API for this, this is an approximation
     * https://pspdfkit.com/blog/2016/keyboard-handling-on-android/
     */
    private void registerKeyboardListener()
    {
        // Register global layout listener.
        final View decorView = getActivity().getWindow().getDecorView();
        decorView.getViewTreeObserver()
                 .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
                 {
                     private final Rect windowVisibleDisplayFrame = new Rect();
                     private int lastVisibleDecorViewHeight;

                     @Override
                     public void onGlobalLayout()
                     {
                         // Retrieve visible rectangle inside window.
                         decorView.getWindowVisibleDisplayFrame(windowVisibleDisplayFrame);
                         final int visibleDecorViewHeight = windowVisibleDisplayFrame.height();

                         // Decide whether keyboard is visible from changing decor view height.
                         if (lastVisibleDecorViewHeight != 0)
                         {
                             if (lastVisibleDecorViewHeight > visibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX)
                             {
                                 // Calculate current keyboard height (this includes also navigation bar height when in fullscreen mode).
                                 int currentKeyboardHeight = decorView.getHeight() - windowVisibleDisplayFrame.bottom;
                                 // Notify listener about keyboard being shown.
                                 //                        listener.onKeyboardShown(currentKeyboardHeight);
                                 collapse();
                             }
                             else if (lastVisibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX < visibleDecorViewHeight)
                             {
                                 // Notify listener about keyboard being hidden.
                                 expand();
                             }
                         }
                         // Save current decor view height for the next call.
                         lastVisibleDecorViewHeight = visibleDecorViewHeight;
                     }
                 });
    }

    private void expand()
    {
        if (!mExpanded)
        {
            mAppBar.setExpanded(true);
            mExpanded = true;
        }
    }

    private void collapse()
    {
        if (mExpanded)
        {
            mAppBar.setExpanded(false);
            mExpanded = false;
        }
    }

    /**
     * Determines when the toolbar is fully expanded and fully collapsed.
     * @param appBarLayout
     * @param verticalOffset
     */
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset)
    {
        Log.d(TAG, "onOffsetChanged: " + verticalOffset);
        if (verticalOffset == 0)
        {
            //fully expanded
            mToolbar.setVisibility(View.INVISIBLE);
            mToolbar.setAlpha(0);
        }
        else if (mToolbar.getHeight() - mCollapsingToolbar.getHeight() == verticalOffset)
        {
            //fully collapsed
            mToolbar.setVisibility(View.VISIBLE);
            mToolbar.animate()
                          .alpha(1)
                          .start();
        }
    }
}
