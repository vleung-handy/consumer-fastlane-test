package com.handybook.handybook.manager;

import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.FacebookAuthorizationException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.event.UserEvent;
import com.handybook.handybook.model.request.CreateUserRequest;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.json.JSONObject;

import javax.inject.Inject;

public class UserDataManager
{
    private final UserManager mUserManager;
    private final DataManager mDataManager;
    private final Bus mBus;

    private static final String KEY_FACEBOOK_ID = "id";
    private static final String KEY_FACEBOOK_EMAIL = "email";
    private static final String KEY_FACEBOOK_FIRST_NAME = "first_name";
    private static final String KEY_FACEBOOK_LAST_NAME = "last_name";


    public enum AuthType
    {
        EMAIL, FACEBOOK
    }

    @Inject
    public UserDataManager(
            final UserManager userManager, final DataManager dataManager,
            final Bus bus
    )
    {
        mUserManager = userManager;
        mDataManager = dataManager;
        mBus = bus;
        mBus.register(this);
    }

    @Subscribe
    public void onRequestUpdatePayment(final HandyEvent.RequestUpdatePayment event)
    {
        mDataManager.updatePayment(mUserManager.getCurrentUser().getId(), event.token.getId(),
                new DataManager.Callback<Void>()
                {
                    @Override
                    public void onSuccess(final Void response)
                    {
                        mBus.post(new HandyEvent.ReceiveUpdatePaymentSuccess());
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error)
                    {
                        mBus.post(new HandyEvent.ReceiveUpdatePaymentError(error));
                    }
                });
    }

    @Subscribe
    public void onRequestAuthUser(final HandyEvent.RequestAuthUser event)
    {
        mDataManager.authUser(event.getEmail(), event.getPassword(),
                new DataManager.Callback<User>()
                {
                    @Override
                    public void onSuccess(final User user)
                    {
                        mUserManager.setCurrentUser(user);
                        mBus.post(new HandyEvent.ReceiveAuthUserSuccess(user, AuthType.EMAIL));
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error)
                    {
                        mBus.post(new HandyEvent.ReceiveAuthUserError(error, AuthType.EMAIL));
                    }
                });
    }

    @Subscribe
    public void onRequestCreateUser(final HandyEvent.RequestCreateUser event)
    {
        final CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setEmail(event.getEmail());
        createUserRequest.setPassword(event.getPassword());
        createUserRequest.setReferralPostGuid(event.getReferralGuid());
        mDataManager.createUser(createUserRequest,
                new DataManager.Callback<User>()
                {
                    @Override
                    public void onSuccess(final User user)
                    {
                        mUserManager.setCurrentUser(user);
                        mBus.post(new HandyEvent.ReceiveAuthUserSuccess(user, AuthType.EMAIL));
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error)
                    {
                        mBus.post(new HandyEvent.ReceiveAuthUserError(error, AuthType.EMAIL));
                    }
                });
    }

    @Subscribe
    public void onRequestAuthFacebookUser(final HandyEvent.RequestAuthFacebookUser event)
    {
        final AccessToken accessToken = event.getAccessToken();
        // Request user info from FB through a GraphRequest
        GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback()
        {
            @Override
            public void onCompleted(JSONObject user, GraphResponse response)
            {
                if (response.getError() != null)
                {
                    Crashlytics.logException(
                            new FacebookAuthorizationException(
                                    response.getError().toString()
                            )
                    );
                    //TODO: Handle error
                }
                else
                {
                    authFacebookUser(user, accessToken, event.getReferralGuid());
                }
            }
        }).executeAsync();
    }

    public void requestAndSetCurrentUser(
            @NonNull String userId,
            @NonNull String authToken,
            @NonNull final DataManager.Callback<User> callback
    )
    {
        mDataManager.getUser(userId, authToken, new DataManager.Callback<User>()
        {
            @Override
            public void onSuccess(final User response)
            {
                /*
                TODO investigate when we get current user and when we set it
                to find out what pages are using the cached user and see if we need to make them
                fetch updated user

                using an extra callback layer so that the user manager can set the current user
                to keep previous behavior
                 */
                mUserManager.setCurrentUser(response);
                callback.onSuccess(response);
            }

            @Override
            public void onError(final DataManager.DataManagerError error)
            {
                callback.onError(error);
            }
        });
    }

    /**
     * ugly to have both direct callback and bus event, but eventually we will probably remove this
     * and just use the direct callback
     *
     * @param event
     */
    @Subscribe
    public void onRequestUser(final HandyEvent.RequestUser event)
    {
        requestAndSetCurrentUser(
                event.getUserId(),
                event.getAuthToken(),
                new DataManager.Callback<User>()
                {
                    @Override
                    public void onSuccess(final User user)
                    {
                        mBus.post(new HandyEvent.ReceiveUserSuccess(
                                user,
                                event.getAuthType()
                        ));
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error)
                    {
                        mBus.post(new HandyEvent.ReceiveUserError(
                                error,
                                event.getAuthType()
                        ));
                    }
                }
        );
    }

    @Subscribe
    public void onRequestUserProfileUpdate(final UserEvent.RequestUserPasswordUpdate event)
    {
        mDataManager.updateUser(
                event.getUpdateUserRequest(),
                event.getAuthToken(),
                new DataManager.Callback<User>()
                {
                    @Override
                    public void onSuccess(final User response)
                    {
                        mBus.post(new UserEvent.ReceiveUserPasswordUpdateSuccess(response));
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error)
                    {
                        mBus.post(new UserEvent.ReceiveUserPasswordUpdateError(error));
                    }
                }
        );
    }

    private void authFacebookUser(
            final JSONObject user,
            final AccessToken accessToken,
            final String referralGuid
    )
    {
        final CreateUserRequest createUserRequest = getCreateUserRequestFromFacebookUser(user);
        createUserRequest.setFacebookAccessToken(accessToken.getToken());
        createUserRequest.setReferralPostGuid(referralGuid);
        mDataManager.authFBUser(createUserRequest, new DataManager.Callback<User>()
        {
            @Override
            public void onSuccess(final User user)
            {
                mUserManager.setCurrentUser(user);
                mBus.post(new HandyEvent.ReceiveAuthUserSuccess(user, AuthType.FACEBOOK));
            }

            @Override
            public void onError(final DataManager.DataManagerError error)
            {
                mBus.post(new HandyEvent.ReceiveAuthUserError(error, AuthType.FACEBOOK));
            }
        });
    }

    @NonNull
    private CreateUserRequest getCreateUserRequestFromFacebookUser(final JSONObject user)
    {
        final CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setFacebookUserId(user.optString(KEY_FACEBOOK_ID));
        createUserRequest.setEmail(user.optString(KEY_FACEBOOK_EMAIL));
        createUserRequest.setFirstName(user.optString(KEY_FACEBOOK_FIRST_NAME));
        createUserRequest.setLastName(user.optString(KEY_FACEBOOK_LAST_NAME));
        return createUserRequest;
    }
}
