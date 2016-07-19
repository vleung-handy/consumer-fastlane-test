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
import com.handybook.handybook.logger.mixpanel.Mixpanel;
import com.handybook.handybook.logger.mixpanel.MixpanelEvent;
import com.handybook.handybook.ui.fragment.BaseDialogFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jtse on 4/8/16.
 */
public class RateImprovementConfirmationDialogFragment extends BaseDialogFragment
{

    public static final String EXTRA_BOOKING_ID = "booking_id";

    @Bind(R.id.title_text)
    TextView mTitleText;
    @Bind(R.id.message_text)
    TextView mMessageText;

    @Bind(R.id.submit_button)
    Button mSubmitButton;

    @Bind(R.id.skip_button)
    Button mSkipButton;

    @Bind(R.id.service_icon)
    ImageView mServiceIcon;

    @Bind(R.id.service_icon_img)
    ImageView mServiceIconImg;

    @Bind(R.id.img_icon_success)
    ImageView mSuccessIcon;

    private int mBookingId;

    public static RateImprovementConfirmationDialogFragment newInstance(final int bookingId)
    {
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
    )
    {

        super.onCreateView(inflater, container, savedInstanceState);

        final View view = inflater.inflate(R.layout.dialog_rate_service_confirm, container, true);
        ButterKnife.bind(this, view);

        final Bundle args = getArguments();
        mBookingId = args.getInt(EXTRA_BOOKING_ID);

        mSkipButton.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
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
    public void needHelp()
    {
        mBus.post(new MixpanelEvent.AppProRateHelp(Mixpanel.ProRateEventType.SHOW, mBookingId));
        startActivity(HelpActivity.DeepLink.PRO_ISSUES.getIntent(getActivity()));
        dismiss();
    }

    @OnClick(R.id.submit_button)
    public void close()
    {
        dismiss();
    }

}
