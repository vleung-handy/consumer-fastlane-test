package com.handybook.handybook.event;

import android.support.annotation.NonNull;

import com.facebook.AccessToken;
import com.handybook.handybook.analytics.annotation.Track;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.viewmodel.BookingCardViewModel;
import com.handybook.handybook.core.User;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.manager.UserDataManager;
import com.handybook.handybook.module.notifications.feed.model.HandyNotification;
import com.stripe.android.model.Token;

public abstract class HandyEvent
{
    public abstract static class RequestEvent extends HandyEvent
    {
        public static class BookingCardViewModelsEvent extends RequestEvent
        {
            private User mUser;
            @Booking.List.OnlyBookingValues
            private String mOnlyBookingValue;

            public BookingCardViewModelsEvent(
                    @NonNull final User user,
                    @NonNull @Booking.List.OnlyBookingValues final String onlyBookingsValue
            )
            {
                mUser = user;
                mOnlyBookingValue = onlyBookingsValue;
            }

            public BookingCardViewModelsEvent(@NonNull final User user)
            {
                mUser = user;
            }

            public User getUser()
            {
                return mUser;
            }

            @Booking.List.OnlyBookingValues
            public String getOnlyBookingValue()
            {
                return mOnlyBookingValue;
            }
        }


        public static class HandyNotificationsEvent extends RequestEvent
        {
            private static final long USER_ID_FOR_LOGGED_OUT_USERS = 0;

            final long mUserId;
            final Long mSinceId;
            final Long mUntilId;
            final Long mCount;

            public HandyNotificationsEvent(
                    final long userId,
                    final Long sinceId,
                    final Long untilId,
                    final Long count
            )
            {
                mUserId = userId;
                mSinceId = sinceId;
                mUntilId = untilId;
                mCount = count;
            }

            public HandyNotificationsEvent(final Long count, final Long untilId, final Long sinceId)
            {
                mUserId = USER_ID_FOR_LOGGED_OUT_USERS;
                mCount = count;
                mUntilId = untilId;
                mSinceId = sinceId;
            }

            public long getUserId()
            {
                return mUserId;
            }

            public Long getSinceId()
            {
                return mSinceId;
            }

            public Long getUntilId()
            {
                return mUntilId;
            }

            public Long getCount()
            {
                return mCount;
            }
        }
    }


    public abstract static class ResponseEvent<T> extends HandyEvent
    {
        private T mPayload;

        public ResponseEvent(T payload)
        {
            this.mPayload = payload;
        }

        public T getPayload()
        {
            return mPayload;
        }

        public static class BookingCardViewModels extends ResponseEvent<BookingCardViewModel.List>
        {

            public BookingCardViewModels(BookingCardViewModel.List payload)
            {
                super(payload);
            }
        }


        public static class BookingCardViewModelsError extends ResponseEvent<DataManager.DataManagerError>
        {
            public BookingCardViewModelsError(DataManager.DataManagerError payload)
            {
                super(payload);
            }
        }


        public static class HandyNotificationsSuccess extends ResponseEvent<HandyNotification.ResultSet>
        {
            public HandyNotificationsSuccess(final HandyNotification.ResultSet payload)
            {
                super(payload);
            }
        }


        public static class HandyNotificationsError extends ResponseEvent<DataManager.DataManagerError>
        {
            public HandyNotificationsError(final DataManager.DataManagerError payload)
            {
                super(payload);
            }
        }


    }


    public abstract static class ReceiveSuccessEvent extends HandyEvent
    {
    }


    public abstract static class ReceiveErrorEvent extends HandyEvent
    {
        public DataManager.DataManagerError error;
    }


    @Track("consumer app blocking screen displayed")
    public static class BlockingScreenDisplayed extends HandyEvent
    {
    }


    @Track("consumer app blocking screen button clicked")
    public static class BlockingScreenButtonPressed extends HandyEvent
    {
    }


    public static class StartBlockingAppEvent extends HandyEvent
    {

    }


    public static class StopBlockingAppEvent extends HandyEvent
    {

    }


    public static class RequestUpdatePayment extends RequestEvent
    {
        public final Token token;

        public RequestUpdatePayment(final Token token)
        {
            this.token = token;
        }
    }


    public static class ReceiveUpdatePaymentSuccess extends ReceiveSuccessEvent
    {
    }


    public static class ReceiveUpdatePaymentError extends ReceiveErrorEvent
    {
        public ReceiveUpdatePaymentError(final DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


    public static class RequestAuthUser
    {
        private final String mEmail;
        private final String mPassword;

        public RequestAuthUser(final String email, final String password)
        {
            mEmail = email;
            mPassword = password;
        }

        public String getEmail()
        {
            return mEmail;
        }

        public String getPassword()
        {
            return mPassword;
        }
    }


    public static class RequestAuthFacebookUser extends RequestEvent
    {
        private AccessToken mAccessToken;
        private String mReferralGuid;

        public RequestAuthFacebookUser(final AccessToken accessToken, final String referralGuid)
        {
            mAccessToken = accessToken;
            mReferralGuid = referralGuid;
        }

        public AccessToken getAccessToken()
        {
            return mAccessToken;
        }

        public String getReferralGuid()
        {
            return mReferralGuid;
        }
    }


    public static class ReceiveAuthUserSuccess extends ReceiveSuccessEvent
    {
        private final User mUser;
        private final UserDataManager.AuthType mAuthType;

        public ReceiveAuthUserSuccess(final User user, final UserDataManager.AuthType authType)
        {
            mUser = user;
            mAuthType = authType;
        }

        public User getUser()
        {
            return mUser;
        }

        public UserDataManager.AuthType getAuthType()
        {
            return mAuthType;
        }
    }


    public static class ReceiveAuthUserError extends ReceiveErrorEvent
    {
        private UserDataManager.AuthType mAuthType;

        public ReceiveAuthUserError(
                final DataManager.DataManagerError error,
                final UserDataManager.AuthType authType
        )
        {
            this.error = error;
            mAuthType = authType;
        }

        public UserDataManager.AuthType getAuthType()
        {
            return mAuthType;
        }
    }


    public static class RequestCreateUser extends RequestAuthUser
    {
        private final String mReferralGuid;

        public RequestCreateUser(
                final String email,
                final String password,
                final String referralGuid
        )
        {
            super(email, password);
            mReferralGuid = referralGuid;
        }

        public String getReferralGuid()
        {
            return mReferralGuid;
        }
    }
}
