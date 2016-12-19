package com.handybook.handybook.module.proteam.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.helpcenter.ui.activity.HelpActivity;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.ProTeamPageLog;
import com.handybook.handybook.module.proteam.event.ProTeamEvent;
import com.handybook.handybook.module.proteam.model.ProTeam;
import com.handybook.handybook.module.proteam.model.ProTeamCategoryType;
import com.handybook.handybook.module.proteam.model.ProTeamPro;
import com.handybook.handybook.module.proteam.model.ProviderMatchPreference;
import com.handybook.handybook.core.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.core.ui.view.HandyTabLayout;
import com.squareup.otto.Subscribe;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashSet;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass. Use the {@link ProTeamFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProTeamFragment extends InjectedFragment implements
        ProTeamProListFragment.OnProInteraction,
        RemoveProDialogFragment.RemoveProListener
{
    @Bind(R.id.pro_team_coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;
    @Bind(R.id.pro_team_toolbar)
    Toolbar mToolbar;
    @Bind(R.id.pro_team_tab_layout)
    HandyTabLayout mTabLayout;
    @Bind(R.id.pro_team_pager)
    ViewPager mViewPager;
    @Bind(R.id.pro_team_bottom_button)
    Button mBottomButton;

    private TabAdapter mTabAdapter;
    private ProTeam mProTeam;
    private HashSet<ProTeamPro> mCleanersToAdd = new HashSet<>();
    private HashSet<ProTeamPro> mCleanersToRemove = new HashSet<>();
    private HashSet<ProTeamPro> mHandymenToAdd = new HashSet<>();
    private HashSet<ProTeamPro> mHandymenToRemove = new HashSet<>();

    public ProTeamFragment()
    {
        // Required empty public constructor
    }

    public static ProTeamFragment newInstance()
    {
        return new ProTeamFragment();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    )
    {
        final View view = inflater.inflate(R.layout.fragment_pro_team, container, false);
        ButterKnife.bind(this, view);

        setupToolbar(mToolbar, getString(R.string.title_activity_pro_team));
        if (mConfigurationManager.getPersistentConfiguration().isBottomNavEnabled())
        {
            mToolbar.setNavigationIcon(null);
        }
        else if (getActivity() instanceof MenuDrawerActivity)
        {
            mToolbar.setNavigationIcon(R.drawable.ic_menu);
            final MenuDrawerActivity activity = (MenuDrawerActivity) getActivity();
            activity.setupHamburgerMenu(mToolbar);
        }

        initialize();
        return view;
    }

    private void initialize()
    {
        mTabAdapter = new TabAdapter(
                getChildFragmentManager(),
                mProTeam,
                this
        );
        mViewPager.setAdapter(mTabAdapter);
        mViewPager.addOnPageChangeListener(
                new TabLayout.TabLayoutOnPageChangeListener(mTabLayout)
                {
                    @Override
                    public void onPageSelected(final int position)
                    {
                        super.onPageSelected(position);

                        //log when displayed service changed
                        ProTeamProListFragment proTeamProListFragment =
                                mTabAdapter.getItem(position);
                        ProTeamCategoryType proTeamCategoryType =
                                proTeamProListFragment.getProTeamCategoryType();
                        bus.post(new LogEvent.AddLogEvent(
                                new ProTeamPageLog.DisplayedServiceChanged(proTeamCategoryType)));
                    }
                }
        );
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabsFromPagerAdapter(mTabAdapter);
        initButtons();
    }


    private void initButtons()
    {
        final boolean proTeamChanged = !mCleanersToAdd.isEmpty()
                || !mCleanersToRemove.isEmpty()
                || !mHandymenToAdd.isEmpty()
                || !mHandymenToRemove.isEmpty();
        mBottomButton.setVisibility(proTeamChanged ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        requestProTeam();
    }

    @Override
    public void onPause()
    {
        removeUiBlockers();
        super.onPause();
    }

    private void requestProTeam()
    {
        showUiBlockers();
        bus.post(new ProTeamEvent.RequestProTeam());
    }


    @Subscribe
    public void onReceiveProTeamSuccess(final ProTeamEvent.ReceiveProTeamSuccess event)
    {
        mProTeam = event.getProTeam();
        mTabAdapter.setProTeam(mProTeam);
        clearEditHolders();
        initButtons();
        removeUiBlockers();

        bus.post(new LogEvent.AddLogEvent(new ProTeamPageLog.PageOpened(
                mProTeam.getCount(ProTeamCategoryType.CLEANING, ProviderMatchPreference.PREFERRED),
                mProTeam.getCount(
                        ProTeamCategoryType.CLEANING,
                        ProviderMatchPreference.INDIFFERENT
                ),
                mProTeam.getCount(ProTeamCategoryType.HANDYMEN, ProviderMatchPreference.PREFERRED),
                mProTeam.getCount(ProTeamCategoryType.CLEANING, ProviderMatchPreference.INDIFFERENT)
        )));
    }

    @Subscribe
    public void onReceiveProTeamError(final ProTeamEvent.ReceiveProTeamError event)
    {
        removeUiBlockers();
    }

    @Subscribe
    public void onReceiveProTeamEditSuccess(final ProTeamEvent.ReceiveProTeamEditSuccess event)
    {
        mProTeam = event.getProTeam();
        mTabAdapter.setProTeam(mProTeam);
        clearEditHolders();
        initButtons();
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

    @OnClick(R.id.pro_team_bottom_button)
    void onBottomButtomClicked()
    {
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
            initButtons();
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
            initButtons();
        }
        bus.post(new LogEvent.AddLogEvent(new ProTeamPageLog.EnableButtonTapped(
                String.valueOf(proTeamPro.getId()),
                isChecked,
                ProTeamPageLog.Context.MAIN_MANAGEMENT
        )));
    }


    @OnClick(R.id.pro_team_toolbar_questionmark)
    public void onMenuItemClick()
    {
        bus.post(new LogEvent.AddLogEvent(new ProTeamPageLog.HelpOpenTapped()));
        startActivity(HelpActivity.DeepLink.PRO_TEAM.getIntent(getActivity()));
    }


    private static class TabAdapter extends FragmentPagerAdapter
    {
        private ArrayList<ProTeamProListFragment> mFragments = new ArrayList<>();
        private ArrayList<String> mTitles = new ArrayList<>();

        TabAdapter(
                @NonNull final FragmentManager fm,
                @Nullable ProTeam proTeam,
                @Nullable ProTeamProListFragment.OnProInteraction listener
        )
        {
            super(fm);
            mTitles.add(ProTeamCategoryType.CLEANING.toString());
            final ProTeamProListFragment cleaning = ProTeamProListFragment.newInstance(
                    proTeam,
                    ProTeamCategoryType.CLEANING
            );
            mTitles.add(ProTeamCategoryType.HANDYMEN.toString());
            final ProTeamProListFragment handymen = ProTeamProListFragment.newInstance(
                    proTeam,
                    ProTeamCategoryType.HANDYMEN
            );
            cleaning.setOnProInteraction(listener);
            handymen.setOnProInteraction(listener);
            mFragments.add(cleaning);
            mFragments.add(handymen);
        }

        @Override
        public int getCount()
        {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return mTitles.get(position);
        }

        @Override
        public ProTeamProListFragment getItem(int position)
        {
            return mFragments.get(position);
        }

        void setProTeam(final ProTeam proTeam)
        {
            getItem(0).setProTeam(proTeam);
            getItem(1).setProTeam(proTeam);
        }
    }
}
