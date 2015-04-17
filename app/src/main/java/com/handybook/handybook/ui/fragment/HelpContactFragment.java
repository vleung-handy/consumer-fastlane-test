package com.handybook.handybook.ui.fragment;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.core.HelpNode;
import com.handybook.handybook.core.User;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.ui.activity.HelpActivity;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.ui.widget.BasicInputTextView;
import com.handybook.handybook.ui.widget.EmailInputTextView;
import com.handybook.handybook.ui.widget.FirstNameInputTextView;
import com.handybook.handybook.ui.widget.InputTextField;
import com.handybook.handybook.ui.widget.MenuButton;
import com.handybook.handybook.util.TextUtils;
import com.simplealertdialog.SimpleAlertDialog;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;

public final class HelpContactFragment extends InjectedFragment {

    public static final String EXTRA_HELP_NODE = "com.handy.handy.EXTRA_HELP_NODE";
    private static final String HELP_CONTACT_FORM_DISPOSITION = "help-contact-form-disposition";
    private static final String HELP_CONTACT_FORM_NAME = "name";
    private static final String HELP_CONTACT_FORM_EMAIL = "email";
    private static final String HELP_CONTACT_FORM_DESCRIPTION = "description";
    private static final String SALESFORCE_DATA_WRAPPER_KEY = "salesforce_data";

    @InjectView(R.id.send_message_button)
    Button sendMessageButton;
    @InjectView(R.id.user_name_text)
    FirstNameInputTextView nameText;
    @InjectView(R.id.email_text)
    EmailInputTextView emailText;
    @InjectView(R.id.comment_text)
    BasicInputTextView commentText;
    @InjectView(R.id.close_img)
    ImageView closeImage;

    private HelpNode associatedNode;

    public static HelpContactFragment newInstance(final HelpNode node) {
        final HelpContactFragment fragment = new HelpContactFragment();
        final Bundle args = new Bundle();
        args.putParcelable(EXTRA_HELP_NODE, node);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        associatedNode = getArguments().getParcelable(EXTRA_HELP_NODE);
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_help_contact, container, false);

        ButterKnife.inject(this, view);

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSendMessageButtonClick();
            }
        });

        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCloseImageClick();
            }
        });


        return view;
    }

    private void onSendMessageButtonClick() {
        Boolean allValid = true;
        allValid &= nameText.validate();
        allValid &= emailText.validate();
        allValid &= commentText.validate();

        if (allValid) {
            progressDialog.show();

            //Generates params from the Help nodes
            HashMap<String, String> contactFormInfo = parseHelpNode(associatedNode);

            //add contact form information
            contactFormInfo.put(HELP_CONTACT_FORM_NAME, nameText.getText().toString());
            contactFormInfo.put(HELP_CONTACT_FORM_EMAIL, emailText.getText().toString());
            contactFormInfo.put(HELP_CONTACT_FORM_DESCRIPTION, commentText.getText().toString());

            JSONObject salesforceWrapper = new JSONObject();
            try {
                salesforceWrapper.put(SALESFORCE_DATA_WRAPPER_KEY, new JSONObject(contactFormInfo));
            } catch (Exception e) {
            }

            TypedInput body;
            try {
                body = new TypedByteArray("application/json", salesforceWrapper.toString().getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                body = null;
            }
            dataManager.createHelpCase(body, createCaseCallback);
        }
    }

    private void onCloseImageClick() {
        returnToHelpStartScreen();
    }

    private HashMap<String, String> parseHelpNode(HelpNode node) {
        HashMap<String, String> params = new HashMap<String, String>();
        for (HelpNode childNode : node.getChildren()) {
            if (childNode.getType().equals(HELP_CONTACT_FORM_DISPOSITION)) {
                params.put(childNode.getLabel(), childNode.getContent());
            }
        }
        return params;
    }

    private void returnToHomeScreen() {
        final Intent toHomeScreenIntent = new Intent(getActivity(), ServiceCategoriesActivity.class);
        toHomeScreenIntent.addFlags((Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        startActivity(toHomeScreenIntent);
    }

    private void returnToHelpStartScreen() {
        final Intent toHelpStartScreenIntent = new Intent(getActivity(), HelpActivity.class);
        toHelpStartScreenIntent.addFlags((Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        startActivity(toHelpStartScreenIntent);
    }

    private DataManager.Callback<Void> createCaseCallback = new DataManager.Callback<Void>() {
        @Override
        public void onSuccess(final Void v) {
            if (!allowCallbacks) return;
            progressDialog.dismiss();
            returnToHomeScreen();
        }

        @Override
        public void onError(final DataManager.DataManagerError error) {
            if (!allowCallbacks) return;
            progressDialog.dismiss();
            dataManagerErrorHandler.handleError(getActivity(), error);
        }
    };
}
