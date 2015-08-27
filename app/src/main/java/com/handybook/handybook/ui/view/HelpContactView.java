package com.handybook.handybook.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.core.HelpNode;
import com.handybook.handybook.core.User;
import com.handybook.handybook.ui.widget.BasicInputTextView;
import com.handybook.handybook.ui.widget.EmailInputTextView;
import com.handybook.handybook.ui.widget.FirstNameInputTextView;

import butterknife.InjectView;

public final class HelpContactView extends InjectedRelativeLayout
{
    @InjectView(R.id.send_message_button)
    public Button sendMessageButton;

    @InjectView(R.id.subject_text)
    public TextView subjectText;
    @InjectView(R.id.help_contact_user_name_text)
    public FirstNameInputTextView nameText;
    @InjectView(R.id.help_contact_email_text)
    public EmailInputTextView emailText;
    @InjectView(R.id.help_contact_comment_text)
    public BasicInputTextView commentText;

    @InjectView(R.id.name_layout)
    ViewGroup nameLayout;
    @InjectView(R.id.email_layout)
    ViewGroup emailLayout;

    public HelpContactView(final Context context)
    {
        super(context);
    }

    public HelpContactView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public HelpContactView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public void updateDisplay(HelpNode node)
    {
        populateSubjectData(node);
    }

    private void populateSubjectData(HelpNode node)
    {
        subjectText.setText(node.getLabel());
    }

    public void prepopulateUserData(User user)
    {
        if (user != null)
        {
            this.nameText.setText(user.getFullName());
            this.emailText.setText(user.getEmail());

            //Hide the name and email fields if they are prepopulated so the user can not alter them
            //Validating them off prepopulated data, not hiding if the prepop data would prevent validation
            if (nameText.validate())
            {
                this.nameLayout.setVisibility(View.GONE);
            }
            if (emailText.validate())
            {
                this.emailLayout.setVisibility(View.GONE);
            }
        }
    }
}
