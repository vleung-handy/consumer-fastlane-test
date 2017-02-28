package com.handybook.handybook.core.data;

import java.util.ArrayList;
import java.util.List;

public class DataSynchronizer {

    private int mRequestCount;
    private Callback mCallback;
    private List<DataManager.DataManagerError> mErrors;

    public DataSynchronizer(
            final int requestCount,
            final Callback callback
    ) {
        mRequestCount = requestCount;
        mCallback = callback;
        mErrors = new ArrayList<>();
    }

    public synchronized void countDownSuccess() {
        mRequestCount--;
        if (mRequestCount == 0) {
            finish();
        }
    }

    public synchronized void countDownError(final DataManager.DataManagerError error) {
        mRequestCount--;
        mErrors.add(error);
        if (mRequestCount == 0) {
            finish();
        }
    }

    private void finish() {
        if (mErrors.isEmpty()) {
            mCallback.onSuccess();
        }
        else {
            mCallback.onError(mErrors);
        }
    }

    public interface Callback {

        void onSuccess();

        void onError(final List<DataManager.DataManagerError> errors);
    }
}
