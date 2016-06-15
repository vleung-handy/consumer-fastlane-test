package com.handybook.handybook.module.proteam.ui.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.helpcenter.ui.activity.HelpActivity;
import com.handybook.handybook.module.proteam.event.ProTeamEvent;
import com.handybook.handybook.module.proteam.model.ProTeam;
import com.handybook.handybook.module.proteam.model.ProTeamCategoryType;
import com.handybook.handybook.module.proteam.model.ProTeamPro;
import com.handybook.handybook.module.proteam.model.ProviderMatchPreference;
import com.handybook.handybook.module.proteam.ui.activity.ProTeamAddActivity;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.ui.fragment.InjectedFragment;
import com.handybook.handybook.ui.view.HandyTabLayout;
import com.handybook.handybook.ui.widget.ViewPager;
import com.squareup.otto.Subscribe;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashSet;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProTeamFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProTeamFragment extends InjectedFragment implements
        ProTeamProListFragment.OnProInteraction,
        RemoveProDialogFragment.RemoveProListener
{
    private static final String KEY_MODE = "ProTeamFragment:Mode";
    private static final String KEY_PRO_TEAM = "ProTeamFragment:ProTeam";

    @Bind(R.id.pro_team_toolbar)
    Toolbar mToolbar;
    @Bind(R.id.pro_team_tab_layout)
    HandyTabLayout mTabLayout;
    @Bind(R.id.pro_team_swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.pro_team_pager)
    ViewPager mViewPager;
    @Bind(R.id.pro_team_fab_button)
    FloatingActionButton mFab;
    @Bind(R.id.pro_team_bottom_button)
    Button mBottomButton;

    private Mode mMode;
    private TabAdapter mTabAdapter;
    private ProTeam mProTeam;
    private HashSet<ProTeamPro> mCleanersToAdd = new HashSet<>();
    private HashSet<ProTeamPro> mHandymenToAdd = new HashSet<>();


    public ProTeamFragment()
    {
        // Required empty public constructor
    }

    public static ProTeamFragment newInstance(@NonNull Mode mode)
    {
        return newInstance(mode, null);
    }

    public static ProTeamFragment newInstance(@NonNull Mode mode, @Nullable ProTeam proTeam)
    {
        ProTeamFragment fragment = new ProTeamFragment();
        final Bundle arguments = new Bundle();
        arguments.putInt(KEY_MODE, mode.ordinal());
        arguments.putParcelable(KEY_PRO_TEAM, proTeam);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        final Bundle arguments = getArguments();
        if (arguments != null)
        {
            mMode = Mode.values()[arguments.getInt(KEY_MODE, Mode.PRO_MANAGE.ordinal())];
            mProTeam = arguments.getParcelable(KEY_PRO_TEAM);
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    )
    {
        final View view = inflater.inflate(R.layout.fragment_pro_team, container, false);
        ButterKnife.bind(this, view);
        final MenuDrawerActivity activity = (MenuDrawerActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        activity.setupHamburgerMenu(mToolbar);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                requestProTeam();
                showUiBlockers();
            }
        });
        setMode(mMode);
        mViewPager.setAdapter(mTabAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabsFromPagerAdapter(mTabAdapter);
        initBottomButton();
        return view;
    }

    private void setMode(@NonNull final Mode mode)
    {
        mMode = mode;
        switch (mMode)
        {
            case PRO_MANAGE:
                mTabAdapter = new TabAdapter(
                        getChildFragmentManager(),
                        mProTeam,
                        this,
                        ProviderMatchPreference.PREFERRED
                );
                mFab.setVisibility(View.VISIBLE);
                mBottomButton.setVisibility(View.GONE);
                break;
            case PRO_ADD:
                mTabAdapter = new TabAdapter(
                        getChildFragmentManager(),
                        mProTeam,
                        this,
                        ProviderMatchPreference.INDIFFERENT
                );
                mFab.setVisibility(View.GONE);
                mBottomButton.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (mProTeam == null)
        {
            requestProTeam();
        }
    }

    private void requestProTeam() {bus.post(new ProTeamEvent.RequestProTeam());}


    @Subscribe
    public void onReceiveProTeamSuccess(final ProTeamEvent.ReceiveProTeamSuccess event)
    {
        mSwipeRefreshLayout.setRefreshing(false);
        mProTeam = event.getProTeam();
        mTabAdapter.setProTeam(mProTeam);
        showToast("Success receiving ProTeam");
        removeUiBlockers();

    }

    @Subscribe
    public void onReceiveProTeamError(final ProTeamEvent.ReceiveProTeamError event)
    {
        mSwipeRefreshLayout.setRefreshing(false);
        showToast("Error receiving ProTeam");
        removeUiBlockers();
    }

    @Subscribe
    public void onReceiveProTeamEditSuccess(final ProTeamEvent.ReceiveProTeamEditSuccess event)
    {
        mSwipeRefreshLayout.setRefreshing(false);
        mProTeam = event.getProTeam();
        mTabAdapter.setProTeam(mProTeam);
        showToast("ProTeam edited successfully");
        removeUiBlockers();
    }

    @Subscribe
    public void onReceiveProTeamEditError(final ProTeamEvent.ReceiveProTeamEditError event)
    {
        mSwipeRefreshLayout.setRefreshing(false);
        showToast("Error editing ProTeam");
        removeUiBlockers();
    }

    @OnClick(R.id.pro_team_fab_button)
    void onFabClicked()
    {
        //setMode(Mode.PRO_ADD);
        startActivity(ProTeamAddActivity.newIntent(getContext(), mProTeam));
    }

    @OnClick(R.id.pro_team_bottom_button)
    void onBottomButtomClicked()
    {
        showToast("Adding pros");
        bus.post(
                new ProTeamEvent.RequestProTeamEdit(
                        ProviderMatchPreference.PREFERRED,
                        mCleanersToAdd,
                        mHandymenToAdd
                )
        );
        showUiBlockers();
    }


    /**
     * Implementation of RemoveProDialogFragment listener
     */
    @Override
    public void onYesNotPermanent(
            @Nullable ProTeamCategoryType proTeamCategoryType,
            @Nullable ProTeamPro proTeamPro
    )
    {
        if (proTeamCategoryType == null || proTeamPro == null)
        {
            Crashlytics.logException(new InvalidParameterException("PTF.onYesNotPermanent invalid"));
            return;
        }
        showToast("Yes - NOT permanently! " + proTeamPro.getName());
        bus.post(new ProTeamEvent.RequestProTeamEdit(
                ProviderMatchPreference.INDIFFERENT,
                proTeamPro,
                proTeamCategoryType
        ));
        showUiBlockers();
    }

    /**
     * Implementation of RemoveProDialogFragment listener
     */
    @Override
    public void onYesPermanent(
            @Nullable ProTeamCategoryType proTeamCategoryType,
            @Nullable ProTeamPro proTeamPro
    )
    {
        if (proTeamCategoryType == null || proTeamPro == null)
        {
            Crashlytics.logException(new InvalidParameterException("PTF.onYesPermanent invalid"));
            return;
        }
        showToast("Yes - permanently " + proTeamPro.getName());
        bus.post(new ProTeamEvent.RequestProTeamEdit(
                ProviderMatchPreference.INDIFFERENT,
                proTeamPro,
                proTeamCategoryType
        ));
        showUiBlockers();
    }

    /**
     * Implementation of RemoveProDialogFragment listener
     */
    @Override
    public void onCancel(
            @Nullable ProTeamCategoryType proTeamCategoryType,
            @Nullable ProTeamPro proTeamPro
    )
    {
        showToast("Action cancelled " + proTeamPro.getName());
    }

    @Override
    public void onProRemovalRequested(
            final ProTeamCategoryType proTeamCategoryType,
            final ProTeamPro proTeamPro
    )
    {
        showToast("Pro X clicked" + proTeamPro.getName());
        FragmentManager fm = getActivity().getSupportFragmentManager();
        RemoveProDialogFragment fragment = new RemoveProDialogFragment();
        final String title = getString(R.string.pro_team_remove_dialog_title, proTeamPro.getName());
        fragment.setTitle(title);
        fragment.setProTeamPro(proTeamPro);
        fragment.setProTeamCategoryType(proTeamCategoryType);
        fragment.setListener(this);
        fragment.show(fm, RemoveProDialogFragment.TAG);
    }

    @Override
    public void onProCheckboxStateChanged(
            @NonNull final ProTeamCategoryType proTeamCategoryType,
            @NonNull final ProTeamPro proTeamPro,
            @NonNull final boolean isChecked
    )
    {
        showToast("Pro checkbox state changed" + proTeamPro.getName() + (isChecked ? "true" : "false"));
        if (isChecked)
        {
            switch (proTeamCategoryType)
            {
                case CLEANING:
                    mCleanersToAdd.add(proTeamPro);
                    break;
                case HANDYMEN:
                    mHandymenToAdd.add(proTeamPro);
                    break;
            }
            initBottomButton();
        }
        else
        {
            switch (proTeamCategoryType)
            {
                case CLEANING:
                    mCleanersToAdd.remove(proTeamPro);
                    break;
                case HANDYMEN:
                    mHandymenToAdd.remove(proTeamPro);
                    break;
            }
            initBottomButton();
            initBottomButton();
        }
    }

    private void initBottomButton()
    {
        final boolean haveProsToAdd = mCleanersToAdd.isEmpty() && mHandymenToAdd.isEmpty();
        final String countString = haveProsToAdd ?
                ""
                : Integer.toString(mCleanersToAdd.size() + mHandymenToAdd.size()).concat(" ");
        final String text = getString(R.string.pro_team_button_add_pros_template, countString);
        mBottomButton.setText(text);
        mBottomButton.setVisibility(haveProsToAdd ? View.GONE : View.VISIBLE);
    }

    @OnClick(R.id.pro_team_toolbar_questionmark)
    public void onMenuItemClick()
    {
        startActivity(HelpActivity.DeepLink.PRO_TEAM.getIntent(getActivity()));
    }


    private static class TabAdapter extends FragmentPagerAdapter
    {
        private ArrayList<ProTeamProListFragment> mFragments = new ArrayList<>();
        private ArrayList<String> mTitles = new ArrayList<>();

        TabAdapter(
                @NonNull final FragmentManager fm,
                @Nullable ProTeam proTeam,
                @Nullable ProTeamProListFragment.OnProInteraction listener,
                @NonNull final ProviderMatchPreference providerMatchPreference
        )
        {
            super(fm);
            mTitles.add(ProTeamCategoryType.CLEANING.toString());
            final ProTeamProListFragment cleaning = ProTeamProListFragment.newInstance(
                    proTeam,
                    ProTeamCategoryType.CLEANING,
                    providerMatchPreference
            );
            mTitles.add(ProTeamCategoryType.HANDYMEN.toString());
            final ProTeamProListFragment handymen = ProTeamProListFragment.newInstance(
                    proTeam,
                    ProTeamCategoryType.HANDYMEN,
                    providerMatchPreference
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

        void setProviderMatchPreference(final ProviderMatchPreference providerMatchPreference)
        {
            getItem(0).setProviderMatchPreference(providerMatchPreference);
            getItem(1).setProviderMatchPreference(providerMatchPreference);
        }
    }


    public enum Mode
    {
        PRO_MANAGE,
        PRO_ADD
    }
}
