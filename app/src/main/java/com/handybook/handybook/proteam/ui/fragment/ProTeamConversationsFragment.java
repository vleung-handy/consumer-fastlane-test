package com.handybook.handybook.proteam.ui.fragment;

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
import android.widget.Toast;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Provider;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.constant.RequestCode;
import com.handybook.handybook.core.ui.view.SimpleDividerItemDecoration;
import com.handybook.handybook.library.ui.fragment.ProgressSpinnerFragment;
import com.handybook.handybook.library.ui.view.EmptiableRecyclerView;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.AppLog;
import com.handybook.handybook.logger.handylogger.model.ConversationsLog;
import com.handybook.handybook.logger.handylogger.model.ProTeamPageLog;
import com.handybook.handybook.logger.handylogger.model.chat.ChatLog;
import com.handybook.handybook.proteam.callback.ConversationCallback;
import com.handybook.handybook.proteam.callback.ConversationCallbackWrapper;
import com.handybook.handybook.proteam.event.ProTeamEvent;
import com.handybook.handybook.proteam.manager.ProTeamManager;
import com.handybook.handybook.proteam.model.ProTeam;
import com.handybook.handybook.proteam.model.ProTeamCategoryType;
import com.handybook.handybook.proteam.model.ProviderMatchPreference;
import com.handybook.handybook.proteam.ui.activity.ProMessagesActivity;
import com.handybook.handybook.proteam.ui.activity.ProTeamEditActivity;
import com.handybook.handybook.proteam.viewmodel.ProMessagesViewModel;
import com.handybook.handybook.proteam.viewmodel.ProTeamProViewModel;
import com.handybook.shared.core.HandyLibrary;
import com.handybook.shared.layer.LayerConstants;
import com.handybook.shared.layer.LayerHelper;
import com.handybook.shared.layer.receiver.PushNotificationReceiver;
import com.layer.sdk.messaging.Conversation;

import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProTeamConversationsFragment extends ProgressSpinnerFragment
        implements SwipeRefreshLayout.OnRefreshListener, ConversationCallback {

    private static final String BUNDLE_KEY_SHOW_TOOLBAR = "BUNDLE_KEY_SHOW_TOOLBAR";

    @BindView(R.id.pro_team_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.pro_team_swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.pro_team_recycler_view)
    EmptiableRecyclerView mRecyclerView;

    @BindView(R.id.messages_empty_view)
    View mEmptyView;

    @Inject
    ProTeamManager mProTeamManager;

    ProConversationAdapter mAdapter;

    private ProTeam mProTeam;
    private ProTeamProViewModel mSelectedProTeamMember;

    private long mProTeamResponseLastReceivedTimestampMs = 0;

    private BroadcastReceiver mPushNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final Bundle extras = intent.getExtras();
            if (extras == null) { return; }
            final Uri conversationId = extras.getParcelable(LayerConstants.LAYER_CONVERSATION_KEY);
            if (conversationId != null) {
                // Assuming this receiver has a high system priority, this will prevent push
                // notifications regarding any conversation from being displayed.
                abortBroadcast();
            }
        }
    };

    @Inject
    LayerHelper mLayerHelper;

    public static ProTeamConversationsFragment newInstance() {
        return new ProTeamConversationsFragment();
    }

    public static ProTeamConversationsFragment newInstance(boolean showToolbar) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(BUNDLE_KEY_SHOW_TOOLBAR, showToolbar);
        ProTeamConversationsFragment fragment = new ProTeamConversationsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        bus.register(this);
    }

    @Override
    public void onStop() {
        bus.unregister(this);
        super.onStop();
    }

    @Nullable
    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState
    ) {
        ViewGroup view = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        view.addView(inflater.inflate(R.layout.fragment_pro_team_conversations, container, false));
        ButterKnife.bind(this, view);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.handy_service_handyman,
                R.color.handy_service_electrician,
                R.color.handy_service_cleaner,
                R.color.handy_service_painter,
                R.color.handy_service_plumber
        );

        mEmptyView.setVisibility(View.GONE);
        initRecyclerView();

        bus.post(new LogEvent.AddLogEvent(new AppLog.AppNavigationLog(AppLog.AppNavigationLog.Page.PRO_TEAM_CONVERSATIONS)));

        return view;
    }

    private boolean hasPreferred() {
        return mProTeam != null
               && mProTeam.getAllCategories() != null
               && mProTeam.getAllCategories().getPreferred() != null
               && !mProTeam.getAllCategories().getPreferred().isEmpty();

    }

    private boolean hasIndifferent() {
        return mProTeam != null
               && mProTeam.getAllCategories() != null
               && mProTeam.getAllCategories().getIndifferent() != null
               && !mProTeam.getAllCategories().getIndifferent().isEmpty();

    }

    private void initRecyclerView() {
        if (mRecyclerView == null || mProTeam == null) {
            return;
        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setEmptyView(mEmptyView);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));

        // Only allow SwipeRefresh when Recycler scrolled all the way up
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(final RecyclerView recyclerView, final int dx, final int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(-1)) {
                    mSwipeRefreshLayout.setEnabled(true);
                }
                else {
                    mSwipeRefreshLayout.setEnabled(false);
                }
            }
        });
        ProTeam.ProTeamCategory allCategories = mProTeam.getAllCategories();

        mAdapter = new ProConversationAdapter(
                allCategories,
                mLayerHelper,
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        int pos = mRecyclerView.getChildAdapterPosition(v);
                        mSelectedProTeamMember = mAdapter.getItem(pos);
                        Conversation conversation = mAdapter.getItem(pos).getConversation();

                        String providerId =
                                String.valueOf(mSelectedProTeamMember.getProTeamPro().getId());
                        String conversationId =
                                conversation == null ? null : conversation.getId().toString();
                        bus.post(new LogEvent.AddLogEvent(new ChatLog.ConversationSelectedLog(
                                providerId,
                                conversationId
                        )));

                        if (conversation != null) {
                            startMessagesActivity(
                                    conversation.getId(),
                                    mSelectedProTeamMember.getTitle(),
                                    mSelectedProTeamMember.getProTeamPro(),
                                    mSelectedProTeamMember.getProviderMatchPreference()
                            );
                        }
                        else {
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

    private void startMessagesActivity(
            final Uri conversationId,
            final String title,
            final Provider mPro,
            final ProviderMatchPreference preference
    ) {
        Intent intent = new Intent(getActivity(), ProMessagesActivity.class);
        intent.putExtra(LayerConstants.LAYER_CONVERSATION_KEY, conversationId);
        intent.putExtra(LayerConstants.LAYER_MESSAGE_TITLE, title);
        intent.putExtra(BundleKeys.PRO_MESSAGES_VIEW_MODEL, new ProMessagesViewModel(mPro));
        startActivity(intent);
    }

    /**
     * Fires off a request to the server to create a new conversation, then once that's created, and
     * conversation synced, we will launch the ProMessagesActivity for the actual conversation to
     * happen
     */
    private void createNewConversation(String providerId) {
        showProgressSpinner(true);

        HandyLibrary.getInstance()
                    .getHandyService()
                    .createConversation(
                            providerId,
                            userManager.getCurrentUser().getAuthToken(),
                            "",
                            new ConversationCallbackWrapper(this)
                    );
    }

    /**
     * Response successfully received from server in creating a new layer conversation
     *
     * @param conversationId
     */
    @Override
    public void onCreateConversationSuccess(String conversationId) {
        bus.post(new LogEvent.AddLogEvent(new ChatLog.ConversationCreatedLog(String.valueOf(
                mSelectedProTeamMember.getProTeamPro().getId()), conversationId)));
        startMessagesActivity(
                Uri.parse(conversationId),
                mSelectedProTeamMember.getTitle(),
                mSelectedProTeamMember.getProTeamPro(),
                mSelectedProTeamMember.getProviderMatchPreference()
        );

        hideProgressSpinner();
    }

    @Override
    public void onCreateConversationError() {
        hideProgressSpinner();
        Toast.makeText(getContext(), R.string.an_error_has_occurred, Toast.LENGTH_SHORT).show();
    }

    private void requestProTeam() {
        bus.post(new ProTeamEvent.RequestProTeam());
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean showToolbar = getArguments() == null ||
                              getArguments().getBoolean(BUNDLE_KEY_SHOW_TOOLBAR, true);
        /*
        toolbar will be hidden when the my pros tab is enabled
        not bothering to break this fragment into one without a toolbar
        because we might just remove the toolbar if the my pros
        is supposed to be permanently on
         */
        if (showToolbar) {
            setupToolbar(mToolbar, getString(R.string.messages));
            mToolbar.setNavigationIcon(null);
            mToolbar.setVisibility(View.VISIBLE);
        }
        else {
            mToolbar.setVisibility(View.GONE);
        }

        //pro team has definitely been updated
        if (mProTeam == null
            || mProTeamManager.isProTeamResponseDefinitelyOutdated(
                mProTeamResponseLastReceivedTimestampMs)) {
            showProgressSpinner();
            requestProTeam();
        }

        if (mAdapter != null) {
            clearNotifications();
        }
        registerPushNotificationReceiver();
    }

    private void registerPushNotificationReceiver() {
        final IntentFilter filter = new IntentFilter(LayerConstants.ACTION_SHOW_NOTIFICATION);
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        getActivity().registerReceiver(mPushNotificationReceiver, filter);
    }

    @Subscribe
    public void onReceiveProTeamSuccess(final ProTeamEvent.ReceiveProTeamSuccess event) {
        mProTeamResponseLastReceivedTimestampMs = System.currentTimeMillis();

        mProTeam = event.getProTeam();
        hideProgressSpinner();
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

        ProTeam.ProTeamCategory category = mProTeam.getAllCategories();
        bus.post(new LogEvent.AddLogEvent(new ConversationsLog.Loaded(
                category.getIndifferent().size() + category.getPreferred().size())));

        initRecyclerView();
    }

    @Subscribe
    public void onReceiveProTeamError(final ProTeamEvent.ReceiveProTeamError event) {
        mSwipeRefreshLayout.setRefreshing(false);
        bus.post(new LogEvent.AddLogEvent(new ConversationsLog.LoadingError(event.error.getMessage())));
    }

    @OnClick(R.id.pro_team_toolbar_edit_pro_team_button)
    public void onEditListClicked() {
        startActivityForResult(
                new Intent(getContext(), ProTeamEditActivity.class),
                RequestCode.EDIT_PRO_TEAM
        );
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == RequestCode.EDIT_PRO_TEAM) {
            final ProTeam updatedProTeam = data.getParcelableExtra(BundleKeys.PRO_TEAM);
            if (updatedProTeam != null) {
                mProTeam = updatedProTeam;
                initRecyclerView();
            }
        }
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(mPushNotificationReceiver);
        hideProgressSpinner();
        mSwipeRefreshLayout.setRefreshing(false);
        super.onPause();
    }

    @Override
    public void onRefresh() {
        requestProTeam();
    }

    private void clearNotifications() {
        if (mAdapter == null) {
            return;
        }
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            PushNotificationReceiver.getNotifications(getActivity())
                                    .clear(mAdapter.getItem(i).getConversation());
        }
    }
}
