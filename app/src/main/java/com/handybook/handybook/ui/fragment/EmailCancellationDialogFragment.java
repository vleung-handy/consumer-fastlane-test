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

public class EmailCancellationDialogFragment extends BaseDialogFragment //TODO: give better name
{
    private static final String EXTRA_USER_EMAIL_ADDRESS = "EXTRA_USER_EMAIL_ADDRESS";

    @Bind(R.id.email_cancellation_info_text)
    TextView mInfoText;

    public static EmailCancellationDialogFragment newInstance(String userEmailAddress)
    {
        EmailCancellationDialogFragment tipDialogFragment = new EmailCancellationDialogFragment();
        tipDialogFragment.canDismiss = true;

        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_USER_EMAIL_ADDRESS, userEmailAddress);
        tipDialogFragment.setArguments(bundle);

        return tipDialogFragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             final ViewGroup container,
                             final Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        final View view = inflater.inflate(R.layout.dialog_email_cancellation, container, true);
        ButterKnife.bind(this, view);

        String userEmailAddress = getArguments().getString(EXTRA_USER_EMAIL_ADDRESS);
        mInfoText.setText(getString(R.string.email_cancellation_info_formatted, userEmailAddress));
        return view;
    }

    private void finish()
    {
        getActivity().finish();
        dismiss();
    }

    @OnClick(R.id.tip_dialog_container)
    public void onTipDialogContainerClicked()
    {
        finish();
    }

    @OnClick(R.id.submit_button)
    public void onSubmitButtonClicked(View view)
    {
        finish();
    }
}
