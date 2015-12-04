package com.handybook.handybook.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handybook.handybook.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Displayed as confirmation when the user selects a recurring series to cancel
 */
public class EmailCancellationDialogFragment extends BaseDialogFragment
{
    private static final String BUNDLE_KEY_USER_EMAIL_ADDRESS = "USER_EMAIL_ADDRESS";

    @Bind(R.id.email_cancellation_info_text)
    TextView mInfoText;

    public static EmailCancellationDialogFragment newInstance(String userEmailAddress)
    {
        EmailCancellationDialogFragment emailCancellationFragment = new EmailCancellationDialogFragment();
        emailCancellationFragment.canDismiss = true;

        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_KEY_USER_EMAIL_ADDRESS, userEmailAddress);
        emailCancellationFragment.setArguments(bundle);

        return emailCancellationFragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             final ViewGroup container,
                             final Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        final View view = inflater.inflate(R.layout.dialog_email_cancellation, container, true);
        ButterKnife.bind(this, view);

        String userEmailAddress = getArguments().getString(BUNDLE_KEY_USER_EMAIL_ADDRESS);
        mInfoText.setText(getString(R.string.email_cancellation_info_formatted, userEmailAddress));
        return view;
    }

    @OnClick(R.id.next_button)
    public void onSubmitButtonClicked(View view)
    {
        getActivity().finish();
        dismiss();
    }
}
