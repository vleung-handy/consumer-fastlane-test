package com.handybook.handybook.core.data.callback;

import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.model.response.ErrorResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class HandyRetrofit2Callback<T extends ErrorResponse> implements Callback<T> {

    public abstract void onSuccess(T response);

    public abstract void onError(DataManager.DataManagerError error);

    protected abstract boolean areCallbacksEnabled();

    @Override
    public void onResponse(final Call<T> call, final Response<T> response) {
        if (!areCallbacksEnabled()) { return; }
        if (response.body() == null) {
            onError(new DataManager.DataManagerError(DataManager.Type.SERVER));
        }
        else if (response.body().isError()) {
            T body = response.body();
            DataManager.DataManagerError error = new DataManager.DataManagerError(
                    body.getErrorCode(),
                    DataManager.Type.CLIENT,
                    body.getMessages().length >= 1 ? body.getMessages()[0] : null
            );
            error.setInvalidInputs(body.getInvalidInputs());
            onError(error);
        }
        else {
            onSuccess(response.body());
        }
    }

    @Override
    public void onFailure(final Call<T> call, final Throwable t) {
        if (!areCallbacksEnabled()) { return; }
        onError(new DataManager.DataManagerError(DataManager.Type.NETWORK));
    }
}
