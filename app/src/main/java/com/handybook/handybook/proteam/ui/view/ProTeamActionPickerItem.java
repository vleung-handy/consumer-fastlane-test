package com.handybook.handybook.proteam.ui.view;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handybook.handybook.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProTeamActionPickerItem extends FrameLayout
{
    @Bind(R.id.action)
    TextView mAction;

    public ProTeamActionPickerItem(
            final Context context,
            @StringRes final int stringResId,
            @ColorRes final int colorResId
    )
    {
        super(context);
        init(stringResId, colorResId);
    }

    private void init(final int stringResId, final int colorResId)
    {
        inflate(getContext(), R.layout.view_pro_team_action_picker_item, this);
        ButterKnife.bind(this);
        mAction.setText(stringResId);
        mAction.setTextColor(ContextCompat.getColor(getContext(), colorResId));
    }
}
