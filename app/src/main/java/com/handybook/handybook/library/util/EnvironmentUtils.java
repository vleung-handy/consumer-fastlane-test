package com.handybook.handybook.library.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.handybook.handybook.R;
import com.handybook.handybook.core.EnvironmentModifier;

public class EnvironmentUtils {

    private static final String CURRENT = " (current)";

    public static void showEnvironmentModifierDialog(
            final EnvironmentModifier environmentModifier,
            final Context context,
            @Nullable final EnvironmentModifier.OnEnvironmentChangedListener callback
    ) {
        final String[] environmentNames = getEnvironmentNames(environmentModifier);
        final AlertDialog.Builder dialogBuilder =
                UiUtils.createDialogBuilderWithTitle(context, R.string.select_environment);

        dialogBuilder
                .setAdapter(
                        new ArrayAdapter<>(
                                context,
                                android.R.layout.simple_list_item_1,
                                environmentNames
                        ),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {
                                showEnvironmentPrefixDialog(
                                        context,
                                        environmentModifier,
                                        environmentNames[which].split(" ", 2)[0],
                                        // extract "namespace" from "namespace - s (current)"
                                        callback
                                );
                            }
                        }
                )
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show();
    }

    private static void showEnvironmentPrefixDialog(
            @NonNull final Context context,
            @NonNull final EnvironmentModifier environmentModifier,
            @NonNull final String environment,
            @Nullable final EnvironmentModifier.OnEnvironmentChangedListener callback
    ) {
        final EditText input = new EditText(context);
        input.setSingleLine();
        input.setGravity(Gravity.CENTER);
        int titleTextResId;
        if (environment.startsWith(EnvironmentModifier.Environment.NAMESPACE)) {
            titleTextResId = R.string.env_modifier_dialog_title_namespace;
            input.setHint(R.string.env_modifier_dialog_input_hint_namespace);
        }
        else if (environment.startsWith(EnvironmentModifier.Environment.LOCAL)) {
            titleTextResId = R.string.env_modifier_dialog_title_domain;
            input.setHint(R.string.env_modifier_dialog_input_hint_domain);
        }
        else {
            return;
        }

        final AlertDialog.Builder dialogBuilder =
                UiUtils.createDialogBuilderWithTitle(context, titleTextResId);
        dialogBuilder
                .setView(input)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        String prefix = input.getText().toString().trim().toLowerCase();
                        if (!android.text.TextUtils.isEmpty(prefix)) {
                            environmentModifier.setEnvironment(environment);
                            environmentModifier.setEnvironmentPrefix(prefix);
                            if (callback != null) {
                                callback.onEnvironmentChanged(environment, prefix);
                            }
                        }
                    }
                })
                .create()
                .show();
    }

    @NonNull
    private static String[] getEnvironmentNames(final EnvironmentModifier environmentModifier) {

        final String[] environmentNames = {
                EnvironmentModifier.Environment.NAMESPACE,
                EnvironmentModifier.Environment.LOCAL
        };

        if (environmentModifier.isNamespace()) {
            environmentNames[0] += " - " + environmentModifier.getEnvironmentPrefix() + CURRENT;
        }
        else {
            environmentNames[1] += " - " + environmentModifier.getEnvironmentPrefix() + CURRENT;
        }

        return environmentNames;
    }
}
