package com.handybook.handybook.module.proteam.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.constant.RequestCode;
import com.handybook.handybook.core.BaseApplication;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.library.ui.view.EmptiableRecyclerView;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.ProTeamPageLog;
import com.handybook.handybook.module.proteam.event.ProTeamEvent;
import com.handybook.handybook.module.proteam.model.ProTeam;
import com.handybook.handybook.module.proteam.model.ProTeamCategoryType;
import com.handybook.handybook.module.proteam.model.ProviderMatchPreference;
import com.handybook.handybook.module.proteam.ui.activity.ProMessagesActivity;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.ui.view.SimpleDividerItemDecoration;
import com.handybook.shared.LayerConstants;
import com.handybook.shared.LayerHelper;
import com.layer.sdk.messaging.Conversation;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProTeamConversationsFragment extends InjectedFragment
{
    @Bind(R.id.pro_team_toolbar)
    Toolbar mToolbar;

    @Bind(R.id.pro_team_recycler_view)
    EmptiableRecyclerView mRecyclerView;

    @Bind(R.id.pro_team_empty_view)
    View mEmptyView;

    @Bind(R.id.pro_team_empty_view_title)
    TextView mEmptyViewTitle;

    @Bind(R.id.pro_team_empty_view_text)
    TextView mEmptyViewText;

    ProConversationAdapter mAdapter;

    private ProTeam mProTeam;
    private LayerHelper mLayerHelper;

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

        mLayerHelper = ((BaseApplication) getActivity()
                .getApplication())
                .getLayerHelper();

        initEmptyView();
        initRecyclerView();
        return view;
    }

    private void initRecyclerView()
    {
        if (mRecyclerView == null || mProTeam == null)
        {
            return;
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setEmptyView(mEmptyView);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));

        ProTeam.ProTeamCategory allCategories = mProTeam.getAllCategories();

        mAdapter = new ProConversationAdapter(
                allCategories,
                mLayerHelper,
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(final View v)
                    {
                        int pos = mRecyclerView.getChildAdapterPosition(v);
                        Conversation conversation = mAdapter.getItem(pos).getConversation();

                        if (conversation != null)
                        {
                            Intent intent = new Intent(getActivity(), ProMessagesActivity.class);
                            intent.putExtra(
                                    LayerConstants.LAYER_CONVERSATION_KEY,
                                    conversation.getId()
                            );
                            intent.putExtra(
                                    LayerConstants.LAYER_MESSAGE_TITLE,
                                    mAdapter.getItem(pos).getTitle()
                            );
                            intent.putExtra(
                                    BundleKeys.PROVIDER_ID,
                                    String.valueOf(mAdapter.getItem(pos).getProTeamPro().getId())
                            );
                            startActivity(intent);
                        }
                        else
                        {
                            Snackbar.make(
                                    mRecyclerView,
                                    "There isn't a conversation started with this pro yet.",
                                    Snackbar.LENGTH_SHORT
                            ).show();
                        }
                    }
                }
        );

        mAdapter.refreshConversations();
        mRecyclerView.setAdapter(mAdapter);

    }

    private void initEmptyView()
    {
        if (mEmptyViewTitle == null || mEmptyViewText == null)
        {
            return;
        }
        if (mProTeam == null)
        {
            mEmptyViewTitle.setText(R.string.pro_team_empty_card_title_loading);
            mEmptyViewText.setText(R.string.pro_team_empty_card_text_loading);
        }
        else
        {
            mEmptyViewTitle.setText(R.string.pro_team_empty_card_title);
            mEmptyViewText.setText(R.string.pro_team_empty_card_text);
        }
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

        initRecyclerView();
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
