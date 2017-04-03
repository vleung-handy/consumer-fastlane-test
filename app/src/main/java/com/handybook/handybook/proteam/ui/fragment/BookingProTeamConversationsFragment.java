package com.handybook.handybook.proteam.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.Provider;
import com.handybook.handybook.booking.ui.activity.BookingDateActivity;
import com.handybook.handybook.booking.ui.fragment.BookingDetailFragment;
import com.handybook.handybook.core.constant.ActivityResult;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.ui.view.SimpleDividerItemDecoration;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.booking.BookingDetailsLog;
import com.handybook.handybook.proteam.callback.ConversationCallback;
import com.handybook.handybook.proteam.callback.ConversationCallbackWrapper;
import com.handybook.handybook.proteam.manager.ProTeamManager;
import com.handybook.handybook.proteam.model.ProTeam;
import com.handybook.handybook.proteam.ui.activity.ProMessagesActivity;
import com.handybook.handybook.proteam.viewmodel.ProMessagesViewModel;
import com.handybook.handybook.proteam.viewmodel.ProTeamProViewModel;
import com.handybook.shared.core.HandyLibrary;
import com.handybook.shared.layer.LayerConstants;
import com.handybook.shared.layer.LayerHelper;
import com.layer.sdk.messaging.Conversation;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

// TODO: a lot of these code are duplicated from ProTeamConversationsFragment. Consider a refactor.
public class BookingProTeamConversationsFragment extends InjectedFragment
        implements ConversationCallback {

    @Inject
    ProTeamManager mProTeamManager;
    @Inject
    LayerHelper mLayerHelper;

    @Bind(R.id.pro_team_toolbar)
    Toolbar mToolbar;

    @Bind(R.id.pro_team_recycler_view)
    RecyclerView mRecyclerView;

    private ProTeam.ProTeamCategory mProTeamCategory;
    private ProTeamProViewModel mSelectedProTeamMember;
    private ProConversationAdapter mAdapter;
    private Booking mBooking;

    public static BookingProTeamConversationsFragment newInstance(
            ProTeam.ProTeamCategory category,
            Booking booking
    ) {
        Bundle args = new Bundle();
        args.putParcelable(BundleKeys.PRO_TEAM_CATEGORY, category);
        args.putParcelable(BundleKeys.BOOKING, booking);

        BookingProTeamConversationsFragment fragment = new BookingProTeamConversationsFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mProTeamCategory = getArguments().getParcelable(BundleKeys.PRO_TEAM_CATEGORY);
            mBooking = getArguments().getParcelable(BundleKeys.BOOKING);
        }
    }

    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState
    ) {
        final View view = inflater.inflate(
                R.layout.fragment_booking_pro_team_conversations,
                container,
                false
        );
        ButterKnife.bind(this, view);

        initRecyclerView();

        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        bus.post(new LogEvent.AddLogEvent(new BookingDetailsLog.RescheduleSelectedProShown(
                mProTeamCategory.getPreferred().size())));
    }

    @Subscribe
    public void onReceivePreRescheduleInfoSuccess(BookingEvent.ReceivePreRescheduleInfoSuccess event) {
        removeUiBlockers();

        final Intent intent = new Intent(getActivity(), BookingDateActivity.class);
        intent.putExtra(BundleKeys.RESCHEDULE_BOOKING, mBooking);
        intent.putExtra(BundleKeys.RESCHEDULE_NOTICE, event.notice);
        intent.putExtra(BundleKeys.RESCHEDULE_TYPE, BookingDetailFragment.RescheduleType.NORMAL);

        startActivityForResult(intent, ActivityResult.START_RESCHEDULE);
    }

    @Subscribe
    public void onReceivePreRescheduleInfoError(BookingEvent.ReceivePreRescheduleInfoError event) {
        removeUiBlockers();

        dataManagerErrorHandler.handleError(getActivity(), event.error);
    }

    private void initRecyclerView() {
        if (mRecyclerView == null || mProTeamCategory == null) { return; }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));

        mAdapter = new ProConversationAdapter(
                mProTeamCategory,
                mLayerHelper,
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        int pos = mRecyclerView.getChildAdapterPosition(v);

                        // ignore header
                        if (pos < mAdapter.getHeaderCount()) { return; }

                        mSelectedProTeamMember = mAdapter.getItem(pos - mAdapter.getHeaderCount());
                        Conversation conversation = mSelectedProTeamMember.getConversation();

                        String providerId =
                                String.valueOf(mSelectedProTeamMember.getProTeamPro().getId());
                        String conversationId =
                                conversation == null ? null : conversation.getId().toString();

                        bus.post(new LogEvent.AddLogEvent(
                                new BookingDetailsLog.RescheduleProviderSelected(
                                        providerId,
                                        conversationId
                                )));
                        if (conversation != null) {
                            startMessagesActivity(
                                    conversation.getId(),
                                    mSelectedProTeamMember.getTitle(),
                                    mSelectedProTeamMember.getProTeamPro()
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

        View header1 = LayoutInflater.from(getContext()).inflate(
                R.layout.list_item_conversation_header1, mRecyclerView, false);
        View header2 = LayoutInflater.from(getContext()).inflate(
                R.layout.list_item_conversation_header2, mRecyclerView, false);
        header2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                bus.post(new BookingEvent.RequestPreRescheduleInfo(mBooking.getId()));
                bus.post(new LogEvent.AddLogEvent(new BookingDetailsLog.RescheduleIndifferenceSelected()));

            }
        });
        View header3 = LayoutInflater.from(getContext()).inflate(
                R.layout.list_item_conversation_header3, mRecyclerView, false);
        mAdapter.addHeader(header1);
        mAdapter.addHeader(header2);
        mAdapter.addHeader(header3);
        mAdapter.setProviderId(mBooking.getProvider().getId());
        mAdapter.setHideConversation(true);

        mRecyclerView.setAdapter(mAdapter);
    }

    private void startMessagesActivity(
            final Uri conversationId,
            final String title,
            final Provider mPro
    ) {
        Intent intent = new Intent(getActivity(), ProMessagesActivity.class);
        intent.putExtra(LayerConstants.LAYER_CONVERSATION_KEY, conversationId);
        intent.putExtra(LayerConstants.LAYER_MESSAGE_TITLE, title);
        intent.putExtra(BundleKeys.PRO_MESSAGES_VIEW_MODEL, new ProMessagesViewModel(mPro));
        intent.putExtra(BundleKeys.SHOW_TIPS, true);
        intent.putExtra(BundleKeys.BOOKING, mBooking);
        startActivityForResult(intent, ActivityResult.RESCHEDULE_NEW_DATE);
    }

    /**
     * Fires off a request to the server to create a new conversation, then once that's created, and
     * conversation synced, we will launch the ProMessagesActivity for the actual conversation to
     * happen
     */
    private void createNewConversation(String providerId) {
        progressDialog.show();

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
        bus.post(new LogEvent.AddLogEvent(new BookingDetailsLog.ConversationCreatedLog(String.valueOf(
                mSelectedProTeamMember.getProTeamPro().getId()), conversationId)));
        startMessagesActivity(
                Uri.parse(conversationId),
                mSelectedProTeamMember.getTitle(),
                mSelectedProTeamMember.getProTeamPro()
        );

        progressDialog.dismiss();
    }

    @Override
    public void onCreateConversationError() {
        progressDialog.dismiss();
        Toast.makeText(getContext(), R.string.an_error_has_occurred, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        setupToolbar(mToolbar, getString(R.string.reschedule));
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ActivityResult.RESCHEDULE_NEW_DATE) {
            final long date = data.getLongExtra(BundleKeys.RESCHEDULE_NEW_DATE, 0);
            final Intent intent = new Intent();
            intent.putExtra(BundleKeys.RESCHEDULE_NEW_DATE, date);
            getActivity().setResult(ActivityResult.RESCHEDULE_NEW_DATE, intent);
            if (requestCode == ActivityResult.START_RESCHEDULE) {
                getActivity().finish();
            }
        }
    }
}
