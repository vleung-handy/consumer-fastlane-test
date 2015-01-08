package com.handybook.handybook.data;

import android.content.Context;

import com.handybook.handybook.ui.widget.InputTextField;

import java.util.Map;

public interface DataManagerErrorHandler {
    void handleError(final Context context, final DataManager.DataManagerError error,
                     final Map<String, InputTextField> inputMap);

    void handleError(final Context context, final DataManager.DataManagerError error);
}
