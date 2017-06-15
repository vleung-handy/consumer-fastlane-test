package com.handybook.handybook.proteam.ui.fragment;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import com.handybook.handybook.R;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.library.ui.fragment.InjectedDialogFragment;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.constants.EventContext;
import com.handybook.handybook.logger.handylogger.constants.EventType;
import com.handybook.handybook.proteam.ProTeamManagementLog;
import com.handybook.handybook.referral.model.ProReferral;
import com.handybook.handybook.referral.model.ReferralDescriptor;
import com.handybook.handybook.referral.ui.SimpleProReferralFragment;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * This is a standalone dialog fragment that allows a user to refer a specific pro.
 */
public class FavProReferralDialogFragment extends InjectedDialogFragment {

    @BindView(R.id.fav_pro_referral_avatar)
    CircleImageView mAvatar;

    private ProReferral mProReferral;
    private ReferralDescriptor mReferralDescriptor;
    private SimpleProReferralFragment mSimpleProReferralFragment;

    public static FavProReferralDialogFragment newInstance(
            @NonNull final ProReferral proReferral,
            @NonNull final ReferralDescriptor referralDescriptor
    ) {

        Bundle args = new Bundle();

        args.putSerializable(BundleKeys.PRO_REFERRAL, proReferral);
        args.putSerializable(BundleKeys.REFERRAL_DESCRIPTOR, referralDescriptor);

        FavProReferralDialogFragment fragment = new FavProReferralDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mProReferral = (ProReferral) getArguments().getSerializable(BundleKeys.PRO_REFERRAL);
            mReferralDescriptor
                    = (ReferralDescriptor) getArguments().getSerializable(BundleKeys.REFERRAL_DESCRIPTOR);
        }

        List<Integer> providerIds = Arrays.asList(Integer.parseInt(mProReferral.getProvider()
                                                                               .getId()));
        mBus.post(new LogEvent.AddLogEvent(new ProTeamManagementLog(
                          EventType.REFERRAL_PAGE_SHOWN,
                          EventContext.PROTEAM_MANAGEMENT,
                          providerIds
                  ))
        );
    }

    @Nullable
    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState
    ) {

        View view = inflater.inflate(R.layout.fragment_fav_pro_referral, container, false);
        ButterKnife.bind(this, view);

        mSimpleProReferralFragment
                = (SimpleProReferralFragment) getChildFragmentManager().findFragmentByTag(
                SimpleProReferralFragment.class.getName());

        Picasso.with(getContext())
               .load(mProReferral.getProvider().getImageUrl())
               .placeholder(R.drawable.img_pro_placeholder)
               .noFade()
               .into(mAvatar);

        if (mSimpleProReferralFragment == null) {
            mSimpleProReferralFragment = SimpleProReferralFragment.newInstance(
                    mProReferral,
                    mReferralDescriptor,
                    mProReferral.getProvider(),
                    EventContext.PROTEAM_MANAGEMENT
            );

            getChildFragmentManager().beginTransaction()
                                     .add(
                                             R.id.fav_pro_referral_referral_container,
                                             mSimpleProReferralFragment,
                                             SimpleProReferralFragment.class.getName()
                                     )
                                     .commit();
        }
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        Window window = dialog.getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        window.getAttributes().windowAnimations
                = R.style.dialog_animation_slide_up_down_from_bottom;

        return dialog;
    }

    @OnClick(R.id.exit_button)
    public void exitClicked() {
        dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        window.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(
                getContext(),
                R.color.white
        )));
        window.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
    }
}
