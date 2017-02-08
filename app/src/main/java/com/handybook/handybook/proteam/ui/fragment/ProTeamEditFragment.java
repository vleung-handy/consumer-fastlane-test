package com.handybook.handybook.proteam.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.ProTeamPageLog;
import com.handybook.handybook.proteam.event.ProTeamEvent;
import com.handybook.handybook.proteam.model.ProTeam;
import com.handybook.handybook.proteam.model.ProTeamCategoryType;
import com.handybook.handybook.proteam.model.ProTeamPro;
import com.handybook.handybook.proteam.model.ProviderMatchPreference;
import com.squareup.otto.Subscribe;

import java.security.InvalidParameterException;
import java.util.HashSet;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass. Use the {@link ProTeamEditFragment#newInstance} factory
 * method to create an instance of this fragment.
 */
public class ProTeamEditFragment extends InjectedFragment implements
        ProTeamProListFragment.OnProInteraction,
        RemoveProDialogFragment.RemoveProListener
{
    @Bind(R.id.pro_team_toolbar)
    Toolbar mToolbar;
    @Bind(R.id.pro_team_swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.pro_team_list_holder)
    ViewGroup mProTeamListHolder;
    @Bind(R.id.pro_team_toolbar_save_button)
    View mSaveButton;

    private ProTeam mProTeam;
    private HashSet<ProTeamPro> mCleanersToAdd = new HashSet<>();
    private HashSet<ProTeamPro> mCleanersToRemove = new HashSet<>();
    private HashSet<ProTeamPro> mHandymenToAdd = new HashSet<>();
    private HashSet<ProTeamPro> mHandymenToRemove = new HashSet<>();
    private ProTeamProListFragment mProTeamListFragment;

    public static ProTeamEditFragment newInstance()
    {
        return newInstance(null);
    }

    public static ProTeamEditFragment newInstance(@Nullable final ProTeam proTeam)
    {
        final ProTeamEditFragment fragment = new ProTeamEditFragment();
        if (proTeam != null)
        {
            final Bundle arguments = new Bundle();
            arguments.putParcelable(BundleKeys.PRO_TEAM, proTeam);
            fragment.setArguments(arguments);
        }
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        final Bundle arguments = getArguments();
        if (arguments != null)
        {
            mProTeam = arguments.getParcelable(BundleKeys.PRO_TEAM);
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    )
    {
        final View view = inflater.inflate(R.layout.fragment_pro_team_edit, container, false);
        ButterKnife.bind(this, view);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.handy_service_handyman,
                R.color.handy_service_electrician,
                R.color.handy_service_cleaner,
                R.color.handy_service_painter,
                R.color.handy_service_plumber
        );
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                requestProTeam();
            }
        });
        mSaveButton.setVisibility(isSettingFavoriteProEnabled() ? View.GONE : View.VISIBLE);
        if (mProTeam != null)
        {
            initialize();
        }
        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setupToolbar(mToolbar, getString(R.string.pro_team));
        if (mProTeam == null)
        {
            mSwipeRefreshLayout.setRefreshing(true);
            requestProTeam();
        }
    }

    private void requestProTeam()
    {
        bus.post(new ProTeamEvent.RequestProTeam());
    }

    @Subscribe
    public void onReceiveProTeamSuccess(final ProTeamEvent.ReceiveProTeamSuccess event)
    {
        mSwipeRefreshLayout.setRefreshing(false);
        mProTeam = event.getProTeam();
        initialize();
    }

    @Subscribe
    public void onReceiveProTeamError(final ProTeamEvent.ReceiveProTeamError event)
    {
        mSwipeRefreshLayout.setRefreshing(false);
        showToast(R.string.default_error_string);
    }

    private void initialize()
    {
        if (isSettingFavoriteProEnabled())
        {
            initNewProTeamListFragment();
        }
        else
        {
            initProTeamListFragment();
        }
    }

    private boolean isSettingFavoriteProEnabled()
    {
        return mConfigurationManager.getPersistentConfiguration().isSettingFavoriteProEnabled();
    }

    private void initProTeamListFragment()
    {
        mProTeamListFragment = ProTeamProListFragment.newInstance(mProTeam, null);
        mProTeamListFragment.setOnProInteraction(this);
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.pro_team_list_holder, mProTeamListFragment)
                .commit();
        mProTeamListFragment.setProTeam(mProTeam);
    }

    private void initNewProTeamListFragment()
    {
        if (mProTeam != null)
        {
            final NewProTeamProListFragment fragment = NewProTeamProListFragment.newInstance(
                    mProTeam.getCategory(ProTeamCategoryType.CLEANING),
                    ProTeamCategoryType.CLEANING
            );
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.pro_team_list_holder, fragment)
                    .commit();
        }
    }

    @Subscribe
    public void onReceiveProTeamEditSuccess(final ProTeamEvent.ReceiveProTeamEditSuccess event)
    {
        mProTeam = event.getProTeam();
        if (mProTeamListFragment != null)
        {
            mProTeamListFragment.setProTeam(mProTeam);
        }
        clearEditHolders();
        removeUiBlockers();
        showToast(R.string.pro_team_update_successful);
        updateTargetFragment();
        getActivity().onBackPressed();
    }

    private void updateTargetFragment()
    {
        final Fragment targetFragment = getTargetFragment();
        if (targetFragment != null)
        {
            final Intent data = new Intent();
            data.putExtra(BundleKeys.PRO_TEAM, mProTeam);
            targetFragment.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, data);
        }
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

    @OnClick(R.id.pro_team_toolbar_save_button)
    void onSaveButtonClicked()
    {
        if (mCleanersToAdd.isEmpty()
                && mCleanersToRemove.isEmpty()
                && mHandymenToAdd.isEmpty()
                && mHandymenToRemove.isEmpty())
        {
            //no changes were made, so we'll just tell them save successful
            showToast(R.string.pro_team_update_successful);
            return;
        }
        bus.post(
                new ProTeamEvent.RequestProTeamEdit(
                        mCleanersToAdd,
                        mHandymenToAdd,
                        mCleanersToRemove,
                        mHandymenToRemove,
                        ProTeamEvent.Source.PRO_MANAGEMENT
                )
        );
        bus.post(new LogEvent.AddLogEvent(new ProTeamPageLog.UpdateSubmitted(
                mCleanersToAdd.size() + mHandymenToAdd.size(),
                mCleanersToRemove.size() + mHandymenToRemove.size(),
                ProTeamPageLog.Context.MAIN_MANAGEMENT
        )));
        showUiBlockers();
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
        if (proTeamCategoryType == null || proTeamPro == null)
        {
            Crashlytics.logException(new InvalidParameterException("PTF.onYesPermanent invalid"));
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
                ProTeamPageLog.Context.MAIN_MANAGEMENT
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
                ProTeamPageLog.Context.MAIN_MANAGEMENT
        )));
    }

    @Override
    public void onDialogDisplayed(
            @Nullable ProTeamPro proTeamPro,
            @Nullable ProviderMatchPreference providerMatchPreference
    )
    {
        bus.post(new LogEvent.AddLogEvent(new ProTeamPageLog.BlockProvider.WarningDisplayed(
                proTeamPro == null ? null : String.valueOf(proTeamPro.getId()),
                providerMatchPreference,
                ProTeamPageLog.Context.MAIN_MANAGEMENT
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
        FragmentManager fm = getActivity().getSupportFragmentManager();
        RemoveProDialogFragment fragment = new RemoveProDialogFragment();
        final String title = getString(R.string.pro_team_remove_dialog_title, proTeamPro.getName());
        fragment.setTitle(title);
        fragment.setProTeamPro(proTeamPro);
        fragment.setProviderMatchPreference(providerMatchPreference);
        fragment.setProTeamCategoryType(proTeamPro.getCategoryType());
        fragment.setListener(this);
        fragment.show(fm, RemoveProDialogFragment.TAG);

        bus.post(new LogEvent.AddLogEvent(new ProTeamPageLog.BlockProvider.Tapped(
                String.valueOf(proTeamPro.getId()),
                providerMatchPreference,
                ProTeamPageLog.Context.MAIN_MANAGEMENT
        )));
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
                ProTeamPageLog.Context.MAIN_MANAGEMENT
        )));
    }
}
