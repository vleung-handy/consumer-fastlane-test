package com.handybook.handybook.ui.fragment;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.handybook.handybook.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RateServiceConfirmDialogFragment extends BaseDialogFragment {
    @InjectView(R.id.service_icon) ImageView serviceIcon;
    @InjectView(R.id.title_text) TextView titleText;
    @InjectView(R.id.message_text) TextView messageText;
    @InjectView(R.id.submit_button) Button submitButton;

    public static RateServiceConfirmDialogFragment newInstance() {
        return new RateServiceConfirmDialogFragment();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        final View view = inflater.inflate(R.layout.dialog_rate_service_confirm, container, true);
        ButterKnife.inject(this, view);

        serviceIcon.setColorFilter(getResources().getColor(R.color.handy_love),
                PorterDuff.Mode.SRC_ATOP);

        titleText.setText(getResources().getString(R.string.thanks_for_feedback));
        messageText.setText(getResources().getString(R.string.were_sorry_feedback));
        submitButton.setOnClickListener(submitListener);

        return view;
    }

    @Override
    protected void enableInputs() {
        super.enableInputs();
        submitButton.setClickable(true);
    }

    @Override
    protected void disableInputs() {
        super.disableInputs();
        submitButton.setClickable(false);
    }

    private View.OnClickListener submitListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            dismiss();
        }
    };
}
