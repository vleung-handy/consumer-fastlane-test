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

import com.handybook.handybook.R;
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

import java.util.ArrayList;

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
            mMode = Mode.values()[arguments.getInt(KEY_MODE)];
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
            }
        });
        setMode(mMode);
        mViewPager.setAdapter(mTabAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabsFromPagerAdapter(mTabAdapter);
        return view;
    }

    private void setMode(final Mode mode)
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
        mProTeam = event.getProTeam();
        mTabAdapter.setProTeam(mProTeam);
    }

    @Subscribe
    public void onReceiveProTeamError(final ProTeamEvent.ReceiveProTeamError event)
    {
        showToast("Error receiving ProTeam");
    }

    @OnClick(R.id.pro_team_fab_button)
    void onFabClicked()
    {
        startActivity(ProTeamAddActivity.newIntent(getContext(), mProTeam));
    }

    /**
     * Implementation of RemoveProDialogFragment listener
     */
    @Override
    public void onYesNotPermanent(@Nullable ProTeamPro proTeamPro)
    {
        showToast("Yes - NOT permanently! " + proTeamPro.getName());
    }

    /**
     * Implementation of RemoveProDialogFragment listener
     */
    @Override
    public void onYesPermanent(@Nullable ProTeamPro proTeamPro)
    {
        showToast("Yes - permanently " + proTeamPro.getName());
    }

    /**
     * Implementation of RemoveProDialogFragment listener
     */
    @Override
    public void onCancel(@Nullable ProTeamPro proTeamPro)
    {
        showToast("Action cancelled " + proTeamPro.getName());
    }

    @Override
    public void onProRemovalRequested(final ProTeamPro proTeamPro)
    {
        showToast("Pro X clicked" + proTeamPro.getName());
        FragmentManager fm = getActivity().getSupportFragmentManager();
        RemoveProDialogFragment fragment = new RemoveProDialogFragment();
        final String title = getString(R.string.pro_team_remove_dialog_title, proTeamPro.getName());
        fragment.setTitle(title);
        fragment.setProTeamPro(proTeamPro);
        fragment.setListener(this);
        fragment.show(fm, RemoveProDialogFragment.TAG);
    }

    @Override
    public void onProCheckboxStateChanged(final ProTeamPro proTeamPro, final boolean state)
    {
        showToast("Pro checkbox state changed" + proTeamPro.getName() + (state ? "true" : "false"));
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
