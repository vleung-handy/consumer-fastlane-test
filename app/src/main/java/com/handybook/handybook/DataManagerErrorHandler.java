package com.handybook.handybook;

import android.content.Context;

import java.util.Map;

interface DataManagerErrorHandler {
    void handleError(final Context context, final DataManager.DataManagerError error,
                     final Map<String, InputTextField> inputMap);

    void handleError(final Context context, final DataManager.DataManagerError error);
}
