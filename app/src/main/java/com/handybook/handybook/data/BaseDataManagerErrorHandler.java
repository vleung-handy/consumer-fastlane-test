package com.handybook.handybook.data;

import android.content.Context;
import android.text.Html;
import android.view.Gravity;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.ui.widget.InputTextField;

import java.util.Map;

public class BaseDataManagerErrorHandler implements DataManagerErrorHandler
{
    private Toast toast;

    public final void handleError(
            final Context context,
            final DataManager.DataManagerError error,
            final Map<String, InputTextField> inputMap)
    {
        String message = context.getString(R.string.default_error_string);
        if (error == null)
        {
            Crashlytics.log("BaseDataManagerErrorHandler received a null error value.");
        }
        else
        {
            switch (error.getType())
            {
                case NETWORK:
                    message = "Unable to connect. Please try again.";
                    break;
                case CLIENT:
                    final String[] inputs = error.getInvalidInputs();
                    if (error.getMessage() != null) { message = error.getMessage(); }
                    if (inputs != null && inputMap != null)
                    {
                        for (String input : inputs)
                        {
                            final InputTextField textField;
                            if ((textField = inputMap.get(input)) != null)
                            {
                                textField.highlight();
                            }
                        }
                    }
                    break;
                case SERVER:
                case OTHER:
                default:
                    break;
            }
        }

        if (toast == null)
        {
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
        }
        toast.setText(Html.fromHtml(message).toString());
        toast.show();
    }

    public final void handleError(final Context context, final DataManager.DataManagerError error)
    {
        handleError(context, error, null);
    }
}
