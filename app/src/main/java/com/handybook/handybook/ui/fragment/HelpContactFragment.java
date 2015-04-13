package com.handybook.handybook.ui.fragment;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
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
import com.handybook.handybook.ui.widget.BasicInputTextView;
import com.handybook.handybook.ui.widget.EmailInputTextView;
import com.handybook.handybook.ui.widget.FirstNameInputTextView;
import com.handybook.handybook.ui.widget.InputTextField;
import com.handybook.handybook.ui.widget.MenuButton;
import com.handybook.handybook.util.TextUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;

public final class HelpContactFragment extends InjectedFragment {

    @InjectView(R.id.send_message_button) Button sendMessageButton;
    @InjectView(R.id.user_name_text) FirstNameInputTextView nameText;
    @InjectView(R.id.email_text) EmailInputTextView emailText;
    @InjectView(R.id.comment_text) BasicInputTextView commentText;

    public static HelpContactFragment newInstance() {
        final HelpContactFragment fragment = new HelpContactFragment();
        return fragment;
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_help_contact, container, false);

        ButterKnife.inject(this, view);

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Boolean allValid = true;
                allValid &= nameText.validate();
                allValid &= emailText.validate();
                allValid &= commentText.validate();

                if(allValid)
                {
                    String json = "";
                    //TODO: Generate proper params from the Help nodes, the help_contact_form will have children indicating the required params
                    //String json = "{\"foo\":\"kit\",\"bar\":\"kat\"}"; //key value pairs
                    TypedInput body;
                    try {
                        body = new TypedByteArray("application/json", json.getBytes("UTF-8"));
                    }
                    catch (UnsupportedEncodingException e) {
                        body = null;
                    }
                    dataManager.createHelpCase(body, createCaseCallback);
                }
            }
        });

        return view;
    }

    private DataManager.Callback<Void> createCaseCallback = new DataManager.Callback<Void>() {
        @Override
        public void onSuccess(final Void v) {
            if (!allowCallbacks) return;
            //TODO: Inform user that we have received their feedback and offer navigation options
            progressDialog.dismiss();
        }

        @Override
        public void onError(final DataManager.DataManagerError error) {
            if (!allowCallbacks) return;
            progressDialog.dismiss();
            dataManagerErrorHandler.handleError(getActivity(), error);
        }
    };
}
