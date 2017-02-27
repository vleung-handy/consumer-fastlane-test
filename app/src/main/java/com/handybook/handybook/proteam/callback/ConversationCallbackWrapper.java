package com.handybook.handybook.proteam.callback;

import com.handybook.shared.layer.model.CreateConversationResponse;

import java.lang.ref.WeakReference;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ConversationCallbackWrapper implements Callback<CreateConversationResponse> {

    private WeakReference<ConversationCallback> mCallback;

    public ConversationCallbackWrapper(ConversationCallback callback) {
        mCallback = new WeakReference<>(callback);
    }

    @Override
    public void success(
            final CreateConversationResponse createConversationResponse, final Response response
    ) {
        if (mCallback.get() != null) {
            if (createConversationResponse.isSuccess()) {
                mCallback.get().onCreateConversationSuccess(
                        createConversationResponse.getConversationId());
            }
            else {
                mCallback.get().onCreateConversationError();
            }
        }
    }

    @Override
    public void failure(final RetrofitError error) {
        if (mCallback.get() != null) {
            mCallback.get().onCreateConversationError();
        }
    }
}
