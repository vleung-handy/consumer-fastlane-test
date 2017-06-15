package com.handybook.handybook.proteam.holder;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.handybook.handybook.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProTeamFacebookHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.pro_team_facebook_log_in_layout)
    ViewGroup mFacebookLogInLayout;
    @BindView(R.id.pro_team_dismiss_facebook_button)
    View mDismissFacebookButton;
    @BindView(R.id.pro_team_facebook_log_in_button)
    LoginButton mFacebookLogInButton;
    @BindView(R.id.pro_team_facebook_ok_button)
    Button mFacebookOkButton;
    @BindView(R.id.pro_team_facebook_title)
    TextView mFacebookTitleText;
    @BindView(R.id.pro_team_facebook_subtitle)
    TextView mFacebookSubtitleText;

    private Fragment mContainerFragment;
    private CallbackManager mFacebookCallbackManager;
    private View.OnClickListener mXButtonClickListener;
    private View.OnClickListener mLoginButtonClickListener;

    public ProTeamFacebookHolder(
            View itemView,
            Fragment containerFragment,
            CallbackManager facebookCallbackManager,
            View.OnClickListener loginButtonClickListener,
            View.OnClickListener xButtonClickListener
    ) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContainerFragment = containerFragment;
        mFacebookCallbackManager = facebookCallbackManager;
        mLoginButtonClickListener = loginButtonClickListener;
        mXButtonClickListener = xButtonClickListener;
        initFacebookLayout();
    }

    @OnClick({R.id.pro_team_dismiss_facebook_button, R.id.pro_team_facebook_ok_button})
    public void close() {
        mXButtonClickListener.onClick(itemView);
    }

    private void initFacebookLayout() {
        mFacebookLogInButton.setFragment(mContainerFragment);
        mFacebookLogInButton.setOnClickListener(mLoginButtonClickListener);
        mFacebookLogInButton.setReadPermissions("public_profile", "email", "user_friends");
        mFacebookLogInButton.registerCallback(
                mFacebookCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        showPostFacebookLoginMessage();
                    }

                    @Override
                    public void onCancel() { }

                    @Override
                    public void onError(final FacebookException error) {
                        Crashlytics.logException(error);
                        Toast.makeText(
                                mContainerFragment.getContext(),
                                R.string.default_error_string,
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );
        setFacebookLayout();
    }

    private void setFacebookLayout() {
        mFacebookLogInLayout.setVisibility(View.VISIBLE);
        mFacebookTitleText.setText(R.string.pro_team_find_pros_from_friends);
        mFacebookSubtitleText.setVisibility(View.GONE);
        mFacebookOkButton.setVisibility(View.GONE);
        mFacebookLogInButton.setVisibility(View.VISIBLE);
    }

    private void showPostFacebookLoginMessage() {
        mFacebookLogInLayout.setVisibility(View.VISIBLE);
        mFacebookTitleText.setText(R.string.pro_team_connect_to_facebook);
        mFacebookSubtitleText.setVisibility(View.VISIBLE);
        mFacebookOkButton.setVisibility(View.VISIBLE);
        mFacebookLogInButton.setVisibility(View.GONE);
    }
}
