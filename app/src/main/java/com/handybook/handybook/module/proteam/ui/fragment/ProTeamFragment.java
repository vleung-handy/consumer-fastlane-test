package com.handybook.handybook.module.proteam.ui.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
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
        ProTeamProListFragment.OnRemoveProTeamProListener,
        RemoveProDialogFragment.RemoveProListener
{
    private static final String KEY_MODE = "ProTeamFragment:Mode";
    private static final String KEY_PRO_TEAM = "ProTeamFragment:ProTeam";

    @Bind(R.id.pro_team_toolbar)
    Toolbar mToolbar;
    @Bind(R.id.pro_team_tab_layout)
    HandyTabLayout mTabLayout;
    @Bind(R.id.pro_team_pager)
    ViewPager mViewPager;
    @Bind(R.id.pro_team_fab_button)
    FloatingActionButton mFab;
    @Bind(R.id.pro_team_bottom_button)
    Button mBottomButton;

    private Mode mMode = Mode.PRO_MANAGE;
    private TabAdapter mTabAdapter;
    private ProTeam mProTeam;

    public ProTeamFragment()
    {
        // Required empty public constructor
    }

    public static ProTeamFragment newInstance(@NonNull Mode mode)
    {
        ProTeamFragment fragment = new ProTeamFragment();
        return newInstance(null, mode);
    }

    public static ProTeamFragment newInstance(@Nullable ProTeam proTeam, @NonNull Mode mode)
    {
        ProTeamFragment fragment = new ProTeamFragment();
        final Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_PRO_TEAM, proTeam);
        bundle.putInt(KEY_MODE, mode.ordinal());
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        final Bundle bundle = getArguments();
        if (bundle != null)
        {
            mMode = Mode.values()[bundle.getInt(KEY_MODE, Mode.PRO_MANAGE.ordinal())];
            mProTeam = bundle.getParcelable(KEY_PRO_TEAM);
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
        mTabAdapter = new TabAdapter(getChildFragmentManager(), this, ProviderMatchPreference.PREFERRED);
        mViewPager.setAdapter(mTabAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabsFromPagerAdapter(mTabAdapter);
        setMode(mMode);
        return view;
    }

    private void setMode(final Mode mode)
    {
        mMode = mode;
        switch (mMode)
        {
            case PRO_MANAGE:
                mFab.setVisibility(View.VISIBLE);
                mBottomButton.setVisibility(View.GONE);
                break;
            case PRO_ADD:
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
            bus.post(new ProTeamEvent.RequestProTeam());
        }
        else
        {
            initialize();
        }
    }

    private void initialize()
    {
        mTabAdapter.setProTeam(mProTeam);
    }

    @Subscribe
    public void onReceiveProTeamSuccess(final ProTeamEvent.ReceiveProTeamSuccess event)
    {
        mProTeam = event.getProTeam();
        initialize();
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
    public void onRemoveProTeamProRequested(final ProTeamPro proTeamPro)
    {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        RemoveProDialogFragment fragment = new RemoveProDialogFragment();
        final String title = getString(R.string.pro_team_remove_dialog_title, proTeamPro.getName());
        fragment.setTitle(title);
        fragment.setProTeamPro(proTeamPro);
        fragment.setListener(this);
        fragment.show(fm, RemoveProDialogFragment.TAG);
    }

    private static class TabAdapter extends FragmentPagerAdapter
    {
        private ArrayList<ProTeamProListFragment> mFragments = new ArrayList<>();
        private ArrayList<String> mTitles = new ArrayList<>();

        TabAdapter(
                @NonNull final FragmentManager fm,
                ProTeamProListFragment.OnRemoveProTeamProListener listener,
                final ProviderMatchPreference preferred
        )
        {
            super(fm);
            mTitles.clear();
            mFragments.clear();
            mTitles.add(ProTeamCategoryType.CLEANING.toString());
            final ProTeamProListFragment cleaning = ProTeamProListFragment.newInstance(
                    null,
                    ProTeamCategoryType.CLEANING,
                    preferred
            );
            mTitles.add(ProTeamCategoryType.HANDYMEN.toString());
            final ProTeamProListFragment handymen = ProTeamProListFragment.newInstance(
                    null,
                    ProTeamCategoryType.HANDYMEN,
                    preferred
            );
            cleaning.setOnRemoveProTeamProListener(listener);
            handymen.setOnRemoveProTeamProListener(listener);
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
            getItem(0).update(proTeam);
            getItem(1).update(proTeam);
        }
    }


    public enum Mode
    {
        PRO_MANAGE,
        PRO_ADD
    }
}
