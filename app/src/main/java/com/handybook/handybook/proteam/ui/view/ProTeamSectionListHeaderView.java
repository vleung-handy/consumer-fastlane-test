package com.handybook.handybook.proteam.ui.view;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.handybook.handybook.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProTeamSectionListHeaderView extends FrameLayout {

    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.help_icon)
    ImageView mHelpIcon;

    public ProTeamSectionListHeaderView(final Context context) {
        super(context);
        init();
    }

    private void init() {
        setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        inflate(getContext(), R.layout.view_pro_team_section_list_header, this);
        ButterKnife.bind(this);
    }

    public void setTitle(final String text) {
        mTitle.setText(text);
    }

    public void setHelpIconClickListener(final OnClickListener listener) {
        mHelpIcon.setOnClickListener(listener);
    }
}
