package com.handybook.handybook.proteam.ui.fragment;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.handybook.handybook.proteam.ui.view.ProTeamProConversationItemView;
import com.handybook.handybook.proteam.viewmodel.ProTeamProViewModel;

public class ConversationHolder extends RecyclerView.ViewHolder {

    private final ProTeamProConversationItemView mProItemView;
    private boolean mHideConversation;

    public ConversationHolder(final ProTeamProConversationItemView itemView) {
        super(itemView);
        mProItemView = itemView;
    }

    public void bind(@NonNull final ProTeamProViewModel proTeamProViewModel) {
        mProItemView.bind(proTeamProViewModel);
    }
}
