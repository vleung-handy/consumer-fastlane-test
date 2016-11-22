package com.handybook.handybook.module.proteam.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import com.handybook.handybook.module.proteam.viewmodel.ProTeamProViewModel;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.ui.view.SimpleDividerItemDecoration;
import com.handybook.shared.CreateConversationResponse;
import com.handybook.shared.HandyLayer;
import com.handybook.shared.LayerConstants;
import com.handybook.shared.LayerHelper;
import com.layer.sdk.messaging.Conversation;
import com.squareup.otto.Subscribe;

import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

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
    private ProTeamProViewModel mSelectedProTeamMember;
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
                        mSelectedProTeamMember = mAdapter.getItem(pos);
                        Conversation conversation = mAdapter.getItem(pos).getConversation();

                        if (conversation != null)
                        {
                            startMessagesActivity(
                                    conversation.getId(),
                                    mSelectedProTeamMember.getTitle(),
                                    String.valueOf(mSelectedProTeamMember.getProTeamPro().getId())
                            );
                        }
                        else
                        {
                            createNewConversation(String.valueOf(mSelectedProTeamMember.getProTeamPro()
                                                                                       .getId()));
                        }
                    }
                }
        );

        mAdapter.refreshConversations();
        mRecyclerView.setAdapter(mAdapter);
    }

    private void startMessagesActivity(Uri conversationId, String title, String providerId)
    {
        Intent intent = new Intent(getActivity(), ProMessagesActivity.class);
        intent.putExtra(LayerConstants.LAYER_CONVERSATION_KEY, conversationId);
        intent.putExtra(LayerConstants.LAYER_MESSAGE_TITLE, title);
        intent.putExtra(BundleKeys.PROVIDER_ID, providerId);
        startActivity(intent);
    }

    /**
     * Fires off a request to the server to create a new conversation, then once that's created, and
     * conversation synced, we will launch the ProMessagesActivity for the actual conversation to
     * happen
     */
    private void createNewConversation(String providerId)
    {
        progressDialog.show();

        HandyLayer.getInstance()
                  .getHandyService()
                  .createConversation(providerId,
                                      userManager.getCurrentUser().getAuthToken(),
                                      "",
                                      new ConversationCallback(this)
                  );
    }

    /**
     * Response successfully received from server in creating a new layer conversation
     *
     * @param conversationId
     */
    public void onConversationCreated(String conversationId)
    {
        startMessagesActivity(
                Uri.parse(LayerConstants.LAYER_CONVERSATION_URI_PREFIX + conversationId),
                mSelectedProTeamMember.getTitle(),
                String.valueOf(mSelectedProTeamMember.getProTeamPro().getId())
        );

        progressDialog.dismiss();
    }

    public void onError()
    {
        progressDialog.dismiss();
        Toast.makeText(getContext(), R.string.an_error_has_occurred, Toast.LENGTH_SHORT).show();
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
                initRecyclerView();
            }
        }
    }

    public class ConversationCallback implements Callback<CreateConversationResponse>
    {

        private WeakReference<ProTeamConversationsFragment> mFragment;

        public ConversationCallback(ProTeamConversationsFragment fragment)
        {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        public void success(
                final CreateConversationResponse createConversationResponse, final Response response
        )
        {
            if (mFragment.get() != null)
            {
                if (createConversationResponse.isSuccess())
                {
                    mFragment.get()
                             .onConversationCreated(createConversationResponse.getConversationId());
                }
                else
                {
                    mFragment.get().onError();
                }
            }
        }

        @Override
        public void failure(final RetrofitError error)
        {
            if (mFragment.get() != null)
            {
                mFragment.get().onError();
            }
        }
    }
}
