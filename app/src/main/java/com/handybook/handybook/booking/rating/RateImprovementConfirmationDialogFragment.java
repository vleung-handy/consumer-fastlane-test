package com.handybook.handybook.booking.rating;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.helpcenter.ui.activity.HelpActivity;
import com.handybook.handybook.library.ui.fragment.BaseDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jtse on 4/8/16.
 */
public class RateImprovementConfirmationDialogFragment extends BaseDialogFragment {

    public static final String EXTRA_BOOKING_ID = "booking_id";

    @BindView(R.id.title_text)
    TextView mTitleText;
    @BindView(R.id.message_text)
    TextView mMessageText;

    @BindView(R.id.submit_button)
    Button mSubmitButton;

    @BindView(R.id.skip_button)
    Button mSkipButton;

    @BindView(R.id.service_icon)
    ImageView mServiceIcon;

    @BindView(R.id.service_icon_img)
    ImageView mServiceIconImg;

    @BindView(R.id.img_icon_success)
    ImageView mSuccessIcon;

    private int mBookingId;

    public static RateImprovementConfirmationDialogFragment newInstance(final int bookingId) {
        final RateImprovementConfirmationDialogFragment fragment
                = new RateImprovementConfirmationDialogFragment();

        final Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_BOOKING_ID, bookingId);

        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(
            final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState
    ) {

        super.onCreateView(inflater, container, savedInstanceState);

        final View view = inflater.inflate(R.layout.dialog_rate_service_confirm, container, true);
        ButterKnife.bind(this, view);

        final Bundle args = getArguments();
        mBookingId = args.getInt(EXTRA_BOOKING_ID);

        mSkipButton.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f
        );
        mSkipButton.setLayoutParams(param);
        mSkipButton.setText(getString(R.string.need_help_question));
        mSubmitButton.setText(getString(R.string.close));
        mTitleText.setText(getResources().getString(R.string.thanks_for_feedback));
        mMessageText.setText(getResources().getString(R.string.were_sorry_feedback));

        mServiceIcon.setVisibility(View.GONE);
        mServiceIconImg.setVisibility(View.GONE);
        mSuccessIcon.setVisibility(View.VISIBLE);

        allowDialogDismissable();

        return view;
    }

    @OnClick(R.id.skip_button)
    public void needHelp() {
        startActivity(HelpActivity.DeepLink.PRO_ISSUES.getIntent(getActivity()));
        dismiss();
    }

    @OnClick(R.id.submit_button)
    public void close() {
        dismiss();
    }

}
