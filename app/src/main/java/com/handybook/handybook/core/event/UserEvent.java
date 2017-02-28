package com.handybook.handybook.core.event;

import com.handybook.handybook.core.User;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.model.request.UpdateUserRequest;

public abstract class UserEvent {

    public static class RequestUserPasswordUpdate extends HandyEvent.RequestEvent {

        private UpdateUserRequest mUpdateUserRequest;
        private String mAuthToken;

        public RequestUserPasswordUpdate(UpdateUserRequest updateUserRequest, String authToken) {
            mUpdateUserRequest = updateUserRequest;
            mAuthToken = authToken;
        }

        public UpdateUserRequest getUpdateUserRequest() {
            return mUpdateUserRequest;
        }

        public String getAuthToken() {
            return mAuthToken;
        }
    }


    public static class ReceiveUserPasswordUpdateSuccess extends HandyEvent.ReceiveSuccessEvent {

        private User mUser;

        public ReceiveUserPasswordUpdateSuccess(User user) {
            mUser = user;
        }

        public User getUser() {
            return mUser;
        }
    }


    public static class ReceiveUserPasswordUpdateError extends HandyEvent.ReceiveErrorEvent {

        public ReceiveUserPasswordUpdateError(DataManager.DataManagerError error) {
            this.error = error;
        }
    }
}
