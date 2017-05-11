package com.handybook.handybook.proteam.ui.fragment;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.handybook.handybook.proteam.ui.view.ProTeamProItemView;
import com.handybook.handybook.proteam.viewmodel.ProTeamProViewModel;

public class ConversationHolder extends RecyclerView.ViewHolder {

    private final ProTeamProItemView mProItemView;

    public ConversationHolder(final ProTeamProItemView itemView) {
        super(itemView);
        mProItemView = itemView;
    }

    public void bind(@NonNull final ProTeamProViewModel proTeamProViewModel) {
        mProItemView.bind(proTeamProViewModel);
    }
}
