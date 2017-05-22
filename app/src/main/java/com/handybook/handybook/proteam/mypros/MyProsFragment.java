package com.handybook.handybook.proteam.mypros;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.AppLog;
import com.handybook.handybook.logger.handylogger.model.MyProsLog;
import com.handybook.handybook.proteam.ui.activity.ProTeamEditActivity;
import com.handybook.handybook.proteam.ui.fragment.ProTeamConversationsFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * fragment that shows when the "my pros" tab menu item is clicked
 *
 * contains the following fragments:
 * - pro team horizontal recycler view, with each item showing a brief summary on the pro
 * - pro team conversations list
 */
public class MyProsFragment extends InjectedFragment {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    public static MyProsFragment newInstance() {
        return new MyProsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState
    ) {
        final View view = inflater.inflate(
                R.layout.fragment_my_pros,
                container,
                false
        );
        ButterKnife.bind(this, view);
        setupToolbar(mToolbar, getResources().getString(R.string.my_pros_tab_title));
        mToolbar.setNavigationIcon(null);

        initProTeamInfoFragment();
        initProTeamConversationsFragment();

        bus.post(new LogEvent.AddLogEvent(new AppLog.AppNavigationLog(AppLog.AppNavigationLog.Page.MY_PROS)));
        return view;
    }

    private void initProTeamInfoFragment() {
        //init the pro team info fragment
        ProTeamInfoFragment proTeamInfoFragment
                = ProTeamInfoFragment.newInstance();
        getChildFragmentManager().beginTransaction().replace(
                R.id.fragment_my_pros_pro_team_info_container,
                proTeamInfoFragment
        ).commit();
    }

    private void initProTeamConversationsFragment() {
        //init the conversations fragment
        ProTeamConversationsFragment proTeamConversationsFragment
                = ProTeamConversationsFragment.newInstance(false);
        getChildFragmentManager().beginTransaction().replace(
                R.id.fragment_my_pros_pro_team_conversations_container,
                proTeamConversationsFragment
        ).commit();
    }

    @OnClick(R.id.fragment_my_pros_edit_pro_team_button)
    public void onEditProTeamButtonClicked() {
        startActivity(new Intent(getActivity(), ProTeamEditActivity.class));
    }

    @OnClick(R.id.fragment_my_pros_pro_team_tooltip)
    public void onProTeamTooltipClicked() {

        bus.post(new LogEvent.AddLogEvent(new MyProsLog.ProTeamQuestionMarkButtonTapped()));

        final Context context = getContext();
        String title = getResources().getString(R.string.pro_team);
        String message
                = getString(R.string.my_pros_pro_team_info);
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

}
