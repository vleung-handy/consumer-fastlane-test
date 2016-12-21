package com.handybook.handybook.helpcenter.helpcontact.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.helpcenter.helpcontact.ui.view.HelpContactView;
import com.handybook.handybook.helpcenter.model.HelpEvent;
import com.handybook.handybook.helpcenter.ui.view.HelpBannerView;
import com.handybook.handybook.helpcenter.model.HelpNode;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.squareup.otto.Subscribe;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;

public final class HelpContactFragment extends InjectedFragment
{
    private static final String HELP_CONTACT_FORM_DISPOSITION = "help-contact-form-disposition";
    private static final String HELP_CONTACT_FORM_NAME = "name";
    private static final String HELP_CONTACT_FORM_EMAIL = "email";
    private static final String HELP_CONTACT_FORM_DESCRIPTION = "description";
    private static final String HELP_CONTACT_FORM_PATH = "path";
    private static final String HELP_CONTACT_FORM_BOOKING_ID = "booking_id";

    private static final String SALESFORCE_DATA_WRAPPER_KEY = "salesforce_data";

    @Bind(R.id.help_contact_view)
    HelpContactView helpContactView;

    @Bind(R.id.help_banner_view)
    HelpBannerView helpBannerView;

    private HelpNode associatedNode;
    private String path;
    private String bookingId;

    public static HelpContactFragment newInstance(final HelpNode node, final String path, final String bookingId) {
        final HelpContactFragment fragment = new HelpContactFragment();
        final Bundle args = new Bundle();
        args.putParcelable(BundleKeys.HELP_NODE, node);
        args.putString(BundleKeys.PATH, path);
        args.putString(BundleKeys.BOOKING_ID, bookingId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState)
    {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_help_contact, container, false);

        ButterKnife.bind(this, view);

        //required arguments
        this.associatedNode = getArguments().getParcelable(BundleKeys.HELP_NODE);
        this.path = getArguments().getString(BundleKeys.PATH);

        //optional argument booking id
        if (getArguments() != null && getArguments().containsKey(BundleKeys.BOOKING_ID))
        {
            this.bookingId = getArguments().getString(BundleKeys.BOOKING_ID);
        }
        else
        {
            this.bookingId = "";
        }

        helpContactView.updateDisplay(this.associatedNode);
        if(this.userManager.getCurrentUser() != null)
        {
            helpContactView.prepopulateUserData(this.userManager.getCurrentUser());
        }
        helpBannerView.updateDisplay(this.associatedNode); //TODO: can we call this inside updateDisplay(HelpNode) instead?

        assignClickListeners(view);

        return view;
    }

    private void assignClickListeners(View view)
    {
        helpContactView.sendMessageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                onSendMessageButtonClick();
            }
        });

        final Activity activity = this.getActivity();
        helpBannerView.backImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                dismissKeyboard();
                activity.onBackPressed();
            }
        });
    }

    private void onSendMessageButtonClick()
    {
        Boolean allValid = true;

        allValid &= helpContactView.nameText.validate();
        allValid &= helpContactView.emailText.validate();
        allValid &= helpContactView.commentText.validate();

        if (allValid)
        {
            dismissKeyboard();
            sendContactFormData(helpContactView.nameText.getString(), helpContactView.emailText.getString(), helpContactView.commentText.getString(), this.associatedNode);
        }
        else
        {
            showToast(R.string.ensure_fields_valid);
        }
    }

    private void dismissKeyboard()
    {
        View currentFocus = getActivity().getCurrentFocus();
        if (currentFocus != null)
        {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }

    private void sendContactFormData(String name, String email, String comment, HelpNode associatedNode)
    {
        HashMap<String, String> contactFormInfo = extractDispositions(associatedNode);

        //add contact form information
        contactFormInfo.put(HELP_CONTACT_FORM_NAME, name);
        contactFormInfo.put(HELP_CONTACT_FORM_EMAIL, email);
        contactFormInfo.put(HELP_CONTACT_FORM_DESCRIPTION, comment);
        contactFormInfo.put(HELP_CONTACT_FORM_PATH, path);
        contactFormInfo.put(HELP_CONTACT_FORM_BOOKING_ID, bookingId);

        JSONObject salesforceWrapper = new JSONObject();
        try
        {
            salesforceWrapper.put(SALESFORCE_DATA_WRAPPER_KEY, new JSONObject(contactFormInfo));
        } catch (Exception e)
        {
            Crashlytics.logException(e);
        }

        TypedInput body;
        try
        {
            body = new TypedByteArray("application/json", salesforceWrapper.toString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e)
        {
            body = null;
        }

        progressDialog.show();

        bus.post(new HelpEvent.RequestNotifyHelpContact(body));
    }

    private HashMap<String, String> extractDispositions(HelpNode node)
    {
        HashMap<String, String> params = new HashMap<>();
        for (HelpNode childNode : node.getChildren())
        {
            if (childNode == null || childNode.getType() == null)
            {
                continue;
            }

            if (childNode.getType().equals(HELP_CONTACT_FORM_DISPOSITION))
            {
                params.put(childNode.getLabel(), childNode.getContent());
            }
        }
        return params;
    }

    private void returnToHomeScreen()
    {
        final Intent toHomeScreenIntent = new Intent(getActivity(), ServiceCategoriesActivity.class);
        toHomeScreenIntent.addFlags((Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        startActivity(toHomeScreenIntent);
    }

    //Event Listeners
    @Subscribe
    public void onReceiveNotifyHelpContactSuccess(HelpEvent.ReceiveNotifyHelpContactSuccess event)
    {
        progressDialog.dismiss();
//        if (bookingId == null || bookingId.isEmpty())
        {
            returnToHomeScreen();
        }
//        else
//        {
//            returnToBookingDetails(bookingId);
//        }

        showToast(getString(R.string.contact_received));
    }

    @Subscribe
    public void onReceiveNotifyHelpContactError(HelpEvent.ReceiveNotifyHelpContactError event)
    {
        progressDialog.dismiss();
        showToast(getString(R.string.an_error_has_occurred));
    }
}
