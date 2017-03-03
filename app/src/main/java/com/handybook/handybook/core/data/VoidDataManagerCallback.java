package com.handybook.handybook.core.data;

public class VoidDataManagerCallback implements DataManager.Callback<Void> {

    @Override
    public void onSuccess(final Void response) {
        // do nothing
    }

    @Override
    public void onError(final DataManager.DataManagerError error) {
        // do nothing
    }
}
