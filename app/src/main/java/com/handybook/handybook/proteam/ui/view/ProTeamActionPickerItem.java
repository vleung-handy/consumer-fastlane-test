package com.handybook.handybook.proteam.ui.view;

import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handybook.handybook.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProTeamActionPickerItem extends FrameLayout {

    @Bind(R.id.action)
    TextView mAction;

    public ProTeamActionPickerItem(
            final Context context,
            @StringRes final int stringResId
    ) {
        super(context);
        init(stringResId);
    }

    private void init(final int stringResId) {
        inflate(getContext(), R.layout.view_pro_team_action_picker_item, this);
        ButterKnife.bind(this);
        mAction.setText(stringResId);
    }
}
