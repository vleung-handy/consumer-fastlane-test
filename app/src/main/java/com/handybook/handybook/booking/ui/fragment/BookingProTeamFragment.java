package com.handybook.handybook.booking.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.helpcenter.ui.activity.HelpActivity;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.ProTeamPageLog;
import com.handybook.handybook.logger.handylogger.model.booking.BookingFunnelLog;
import com.handybook.handybook.proteam.event.ProTeamEvent;
import com.handybook.handybook.proteam.model.ProTeam;
import com.handybook.handybook.proteam.model.ProTeamCategoryType;
import com.handybook.handybook.proteam.model.ProTeamPro;
import com.handybook.handybook.proteam.model.ProviderMatchPreference;
import com.handybook.handybook.proteam.ui.fragment.ProTeamProListFragment;
import com.handybook.handybook.proteam.ui.fragment.RemoveProDialogFragment;
import com.squareup.otto.Subscribe;

import java.security.InvalidParameterException;
import java.util.HashSet;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class BookingProTeamFragment extends BookingFlowFragment implements
        ProTeamProListFragment.OnProInteraction,
        RemoveProDialogFragment.RemoveProListener
{

    @Bind(R.id.booking_pro_team_toolbar)
    Toolbar mToolbar;
    @Bind(R.id.booking_pro_team_bottom_button)
    Button mBottomButton;
    @Bind(R.id.booking_pro_team_fragment_container)
    FrameLayout mContainer;

    private ProTeam mProTeam;
    private HashSet<ProTeamPro> mCleanersToAdd = new HashSet<>();
    private HashSet<ProTeamPro> mCleanersToRemove = new HashSet<>();
    private HashSet<ProTeamPro> mHandymenToAdd = new HashSet<>();
    private HashSet<ProTeamPro> mHandymenToRemove = new HashSet<>();
    private ProTeamCategoryType mProTeamCategoryType;
    private ProTeamProListFragment mProTeamProListFragment;


    public static BookingProTeamFragment newInstance()
    {
        return new BookingProTeamFragment();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mProTeam = bookingManager.getCurrentProTeam();
        if (mProTeam == null || mProTeam.isEmpty())
        {
            continueBookingFlow();
        }

        final ProTeam.ProTeamCategory cleaning = mProTeam.getCategory(ProTeamCategoryType.CLEANING);
        if (cleaning != null && !cleaning.isEmpty())
        {
            mProTeamCategoryType = ProTeamCategoryType.CLEANING;
        }
        else
        {
            mProTeamCategoryType = ProTeamCategoryType.HANDYMEN;
        }

        bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.ProTeamShownLog()));
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState
    )
    {
        final View view = getActivity().getLayoutInflater()
                                       .inflate(
                                               R.layout.fragment_booking_pro_team,
                                               container,
                                               false
                                       );
        ButterKnife.bind(this, view);
        setupToolbar(mToolbar, getString(R.string.title_activity_pro_team));
        final FragmentManager fragmentManager = getChildFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        mProTeamProListFragment = ProTeamProListFragment.newInstance(
                mProTeam,
                mProTeamCategoryType
        );
        mProTeamProListFragment.setOnProInteraction(this);
        fragmentTransaction.add(R.id.booking_pro_team_fragment_container, mProTeamProListFragment);
        fragmentTransaction.commit();
        return view;
    }

    @Override
    protected final void disableInputs()
    {
        super.disableInputs();
        mBottomButton.setClickable(false);
    }

    @Override
    protected final void enableInputs()
    {
        super.enableInputs();
        mBottomButton.setClickable(true);
    }

    @OnClick(R.id.booking_pro_team_toolbar_questionmark)
    public void onMenuItemClick()
    {
        startActivity(HelpActivity.DeepLink.PRO_TEAM.getIntent(getActivity()));
    }

    @OnClick(R.id.booking_pro_team_bottom_button)
    void onNextClicked()
    {
        if (!mCleanersToAdd.isEmpty()
                || !mCleanersToRemove.isEmpty()
                || !mHandymenToAdd.isEmpty()
                || !mHandymenToRemove.isEmpty())
        {
            bus.post(
                    new ProTeamEvent.RequestProTeamEdit(
                            mCleanersToAdd,
                            mHandymenToAdd,
                            mCleanersToRemove,
                            mHandymenToRemove,
                            ProTeamEvent.Source.BOOKING_FLOW
                    )
            );
        }
        bus.post(new LogEvent.AddLogEvent(new ProTeamPageLog.UpdateSubmitted(
                mCleanersToAdd.size() + mHandymenToAdd.size(), //added count
                mCleanersToRemove.size() + mHandymenToRemove.size(), //removed count
                ProTeamPageLog.Context.BOOKING_FLOW
        )));
        bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.ProTeamSubmittedLog(
                mCleanersToAdd.size(), mCleanersToRemove.size(),
                mHandymenToAdd.size(), mHandymenToRemove.size()
        )));
        showUiBlockers();
        continueBookingFlow();
    }

    @Subscribe
    public void onReceiveProTeamEditSuccess(final ProTeamEvent.ReceiveProTeamEditSuccess event)
    {
        mProTeam = event.getProTeam();
        mProTeamProListFragment.setProTeam(mProTeam);
        clearEditHolders();
        removeUiBlockers();
    }

    private void clearEditHolders()
    {
        mCleanersToAdd.clear();
        mCleanersToRemove.clear();
        mHandymenToAdd.clear();
        mHandymenToRemove.clear();
    }

    @Subscribe
    public void onReceiveProTeamEditError(final ProTeamEvent.ReceiveProTeamEditError event)
    {
        removeUiBlockers();
    }


    /**
     * Implementation of RemoveProDialogFragment listener
     */
    @Override
    public void onYesPermanent(
            @Nullable ProTeamCategoryType proTeamCategoryType,
            @Nullable ProTeamPro proTeamPro,
            @Nullable ProviderMatchPreference providerMatchPreference
    )
    {
        if (proTeamCategoryType == null || proTeamPro == null || providerMatchPreference == null)
        {
            Crashlytics.logException(
                    new InvalidParameterException("Booking Flow Pro Team onYesPermanent invalid")
            );
            return;
        }
        bus.post(new ProTeamEvent.RequestProTeamEdit(
                proTeamPro,
                proTeamCategoryType,
                ProviderMatchPreference.NEVER,
                ProTeamEvent.Source.PRO_MANAGEMENT
        ));
        bus.post(new ProTeamPageLog.BlockProvider.Submitted(
                String.valueOf(proTeamPro.getId()),
                providerMatchPreference,
                ProTeamPageLog.Context.BOOKING_FLOW
        ));
        showUiBlockers();
    }

    /**
     * Implementation of RemoveProDialogFragment listener
     */
    @Override
    public void onCancel(
            @Nullable ProTeamCategoryType proTeamCategoryType,
            @Nullable ProTeamPro proTeamPro,
            @Nullable ProviderMatchPreference providerMatchPreference
    )
    {
        bus.post(new LogEvent.AddLogEvent(new ProTeamPageLog.BlockProvider.Cancelled(
                proTeamPro == null ? null : String.valueOf(proTeamPro.getId()),
                providerMatchPreference,
                ProTeamPageLog.Context.BOOKING_FLOW
        )));
    }

    @Override
    public void onDialogDisplayed(
            @Nullable final ProTeamPro proTeamPro,
            @Nullable ProviderMatchPreference providerMatchPreference
    )
    {
        bus.post(new LogEvent.AddLogEvent(new ProTeamPageLog.BlockProvider.WarningDisplayed(
                proTeamPro == null ? null : String.valueOf(proTeamPro.getId()),
                providerMatchPreference,
                ProTeamPageLog.Context.BOOKING_FLOW
        )));
    }

    /**
     * Implementation of ProTeamProListFragment.OnProInteraction listener
     */
    @Override
    public void onProRemovalRequested(
            final ProTeamPro proTeamPro,
            final ProviderMatchPreference providerMatchPreference
    )
    {
        if (proTeamPro == null)
        {
            Crashlytics.logException(new InvalidParameterException(
                    "ProTeamPro cannot be null on pro removal requested"));
            return;
        }
        bus.post(new LogEvent.AddLogEvent(new ProTeamPageLog.BlockProvider.Tapped(
                String.valueOf(proTeamPro.getId()),
                providerMatchPreference,
                ProTeamPageLog.Context.BOOKING_FLOW
        )));

        FragmentManager fm = getActivity().getSupportFragmentManager();
        RemoveProDialogFragment fragment = new RemoveProDialogFragment();
        final String title = getString(R.string.pro_team_remove_dialog_title, proTeamPro.getName());
        fragment.setTitle(title);
        fragment.setProTeamPro(proTeamPro);
        fragment.setProviderMatchPreference(providerMatchPreference);
        fragment.setProTeamCategoryType(proTeamPro.getCategoryType());
        fragment.setListener(this);
        fragment.show(fm, RemoveProDialogFragment.TAG);
    }


    @Override
    public void onProCheckboxStateChanged(
            @NonNull final ProTeamPro proTeamPro,
            final boolean isChecked
    )
    {
        if (isChecked)
        {
            switch (proTeamPro.getCategoryType())
            {
                case CLEANING:
                    mCleanersToAdd.add(proTeamPro);
                    mCleanersToRemove.remove(proTeamPro);
                    break;
                case HANDYMEN:
                    mHandymenToAdd.add(proTeamPro);
                    mHandymenToRemove.remove(proTeamPro);
                    break;
            }
        }
        else
        {
            switch (proTeamPro.getCategoryType())
            {
                case CLEANING:
                    mCleanersToRemove.add(proTeamPro);
                    mCleanersToAdd.remove(proTeamPro);
                    break;
                case HANDYMEN:
                    mHandymenToRemove.add(proTeamPro);
                    mHandymenToAdd.remove(proTeamPro);
                    break;
            }
        }
        bus.post(new LogEvent.AddLogEvent(new ProTeamPageLog.EnableButtonTapped(
                String.valueOf(proTeamPro.getId()),
                isChecked,
                ProTeamPageLog.Context.BOOKING_FLOW
        )));
    }


}
