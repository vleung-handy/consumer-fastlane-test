package com.handybook.handybook.module.proteam.ui.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.handybook.handybook.R;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.constant.RequestCode;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.library.ui.view.EmptiableRecyclerView;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.AppLog;
import com.handybook.handybook.logger.handylogger.model.ProTeamPageLog;
import com.handybook.handybook.logger.handylogger.model.chat.ChatLog;
import com.handybook.handybook.module.proteam.event.ProTeamEvent;
import com.handybook.handybook.module.proteam.model.ProTeam;
import com.handybook.handybook.module.proteam.model.ProTeamCategoryType;
import com.handybook.handybook.module.proteam.model.ProTeamPro;
import com.handybook.handybook.module.proteam.model.ProviderMatchPreference;
import com.handybook.handybook.module.proteam.ui.activity.ProMessagesActivity;
import com.handybook.handybook.module.proteam.viewmodel.ProTeamProViewModel;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.ui.view.SimpleDividerItemDecoration;
import com.handybook.shared.CreateConversationResponse;
import com.handybook.shared.HandyLayer;
import com.handybook.shared.LayerConstants;
import com.handybook.shared.LayerHelper;
import com.handybook.shared.PushNotificationReceiver;
import com.layer.sdk.messaging.Conversation;
import com.squareup.otto.Subscribe;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.handybook.handybook.logger.handylogger.model.LogConstants.PRO_TEAM_CONVERSATIONS;

public class ProTeamConversationsFragment extends InjectedFragment implements SwipeRefreshLayout.OnRefreshListener
{
    @Bind(R.id.pro_team_toolbar)
    Toolbar mToolbar;

    @Bind(R.id.pro_team_swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

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

    private BroadcastReceiver mPushNotificationReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(final Context context, final Intent intent)
        {
            final Bundle extras = intent.getExtras();
            if (extras == null) { return; }
            final Uri conversationId = extras.getParcelable(LayerConstants.LAYER_CONVERSATION_KEY);
            if (conversationId != null)
            {
                // Assuming this receiver has a high system priority, this will prevent push
                // notifications regarding any conversation from being displayed.
                abortBroadcast();
            }
        }
    };

    @Inject
    LayerHelper mLayerHelper;

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

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.handy_service_handyman,
                R.color.handy_service_electrician,
                R.color.handy_service_cleaner,
                R.color.handy_service_painter,
                R.color.handy_service_plumber
        );

        mEmptyViewTitle.setText(R.string.pro_team_empty_card_title);
        mEmptyViewText.setText(R.string.pro_team_empty_card_text);

        initRecyclerView();

        bus.post(new LogEvent.AddLogEvent(new AppLog.AppNavigationLog(PRO_TEAM_CONVERSATIONS)));

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

        // Only allow SwipeRefresh when Recycler scrolled all the way up
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(final RecyclerView recyclerView, final int dx, final int dy)
            {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(-1))
                {
                    mSwipeRefreshLayout.setEnabled(true);
                }
                else
                {
                    mSwipeRefreshLayout.setEnabled(false);
                }
            }
        });
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

                        String providerId = String.valueOf(mSelectedProTeamMember.getProTeamPro()
                                                                                 .getId());
                        String conversationId = conversation == null ? null : conversation.getId()
                                                                                          .toString();
                        bus.post(new LogEvent.AddLogEvent(new ChatLog.ConversationSelectedLog(
                                providerId,
                                conversationId
                        )));

                        if (conversation != null)
                        {
                            startMessagesActivity(
                                    conversation.getId(),
                                    mSelectedProTeamMember.getTitle(),
                                    mSelectedProTeamMember.getProTeamPro()
                            );
                        }
                        else
                        {
                            createNewConversation(providerId);
                        }
                    }
                },
                bus
        );

        mAdapter.refreshConversations();
        mRecyclerView.setAdapter(mAdapter);
        clearNotifications();
    }

    private void startMessagesActivity(Uri conversationId, String title, ProTeamPro mPro)
    {
        Intent intent = new Intent(getActivity(), ProMessagesActivity.class);
        intent.putExtra(LayerConstants.LAYER_CONVERSATION_KEY, conversationId);
        intent.putExtra(LayerConstants.LAYER_MESSAGE_TITLE, title);
        intent.putExtra(BundleKeys.PRO_TEAM_PRO, mPro);
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
        bus.post(new LogEvent.AddLogEvent(new ChatLog.ConversationCreatedLog(String.valueOf(
                mSelectedProTeamMember.getProTeamPro().getId()), conversationId)));
        startMessagesActivity(
                Uri.parse(conversationId),
                mSelectedProTeamMember.getTitle(),
                mSelectedProTeamMember.getProTeamPro()
        );

        progressDialog.dismiss();
    }

    public void onError()
    {
        progressDialog.dismiss();
        Toast.makeText(getContext(), R.string.an_error_has_occurred, Toast.LENGTH_SHORT).show();
    }

    private void requestProTeam()
    {
        mSwipeRefreshLayout.setRefreshing(true);
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
        if (mAdapter != null)
        {
            clearNotifications();
        }
        registerPushNotificationReceiver();
    }

    private void registerPushNotificationReceiver()
    {
        final IntentFilter filter = new IntentFilter(LayerConstants.ACTION_SHOW_NOTIFICATION);
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        getActivity().registerReceiver(mPushNotificationReceiver, filter);
    }

    @Subscribe
    public void onReceiveProTeamSuccess(final ProTeamEvent.ReceiveProTeamSuccess event)
    {
        mProTeam = event.getProTeam();
        mSwipeRefreshLayout.setRefreshing(false);
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
        mSwipeRefreshLayout.setRefreshing(false);
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

    @Override
    public void onPause()
    {
        getActivity().unregisterReceiver(mPushNotificationReceiver);
        mSwipeRefreshLayout.setRefreshing(false);
        super.onPause();
    }

    @Override
    public void onRefresh()
    {
        requestProTeam();
    }

    public static class ConversationCallback implements Callback<CreateConversationResponse>
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

    private void clearNotifications()
    {
        if (mAdapter == null)
        {
            return;
        }
        for (int i = 0; i < mAdapter.getItemCount(); i++)
        {
            PushNotificationReceiver.getNotifications(getActivity())
                                    .clear(mAdapter.getItem(i).getConversation());
        }
    }
}
