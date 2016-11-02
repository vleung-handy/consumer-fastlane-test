package com.handybook.handybook.module.proteam.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.constant.RequestCode;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.ProTeamPageLog;
import com.handybook.handybook.module.proteam.event.ProTeamEvent;
import com.handybook.handybook.module.proteam.model.ProTeam;
import com.handybook.handybook.module.proteam.model.ProTeamCategoryType;
import com.handybook.handybook.module.proteam.model.ProviderMatchPreference;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProTeamConversationsFragment extends InjectedFragment
{
    @Bind(R.id.pro_team_toolbar)
    Toolbar mToolbar;

    private ProTeam mProTeam;

    public static ProTeamConversationsFragment newInstance()
    {
        return new ProTeamConversationsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState
    )
    {
        final View view = inflater.inflate(
                R.layout.fragment_pro_team_conversations,
                container,
                false
        );
        ButterKnife.bind(this, view);
        mToolbar.setNavigationIcon(R.drawable.ic_menu);
        return view;
    }

    private void requestProTeam()
    {
        showUiBlockers();
        bus.post(new ProTeamEvent.RequestProTeam());
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setupToolbar(mToolbar, getString(R.string.my_pro_team));
        ((MenuDrawerActivity) getActivity()).setupHamburgerMenu(mToolbar);
        if (mProTeam == null)
        {
            requestProTeam();
        }
    }

    @Subscribe
    public void onReceiveProTeamSuccess(final ProTeamEvent.ReceiveProTeamSuccess event)
    {
        mProTeam = event.getProTeam();
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

    @OnClick(R.id.pro_team_toolbar_edit_list_button)
    public void onEditListClicked()
    {
        final ProTeamEditFragment proTeamEditFragment = ProTeamEditFragment.newInstance(mProTeam);
        proTeamEditFragment.setTargetFragment(this, RequestCode.EDIT_PRO_TEAM);
        getFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, proTeamEditFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == RequestCode.EDIT_PRO_TEAM)
        {
            final ProTeam updatedProTeam = data.getParcelableExtra(BundleKeys.PRO_TEAM);
            if (updatedProTeam != null)
            {
                mProTeam = updatedProTeam;
            }
        }
    }
}
