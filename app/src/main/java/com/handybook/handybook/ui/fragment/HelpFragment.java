package com.handybook.handybook.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.core.HelpNode;
import com.handybook.handybook.ui.widget.MenuButton;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class HelpFragment extends InjectedFragment {
    static final String EXTRA_HELP_NODE = "com.handy.handy.EXTRA_HELP_NODE";

    private HelpNode node;

    @InjectView(R.id.menu_button_layout) ViewGroup menuButtonLayout;
    @InjectView(R.id.info_text) TextView infoText;
    @InjectView(R.id.nav_options_layout) LinearLayout navList;

    public static HelpFragment newInstance(final HelpNode node) {
        final HelpFragment fragment = new HelpFragment();
        final Bundle args = new Bundle();
        args.putParcelable(EXTRA_HELP_NODE, node);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        node = getArguments().getParcelable(EXTRA_HELP_NODE);
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_help, container, false);

        ButterKnife.inject(this, view);

        final MenuButton menuButton = new MenuButton(getActivity(), menuButtonLayout);
        menuButtonLayout.addView(menuButton);

        switch (node.getType()) {
            case "root":
                layoutForRoot(inflater, container);
                break;

            default:
                break;
        }

        return view;
    }

    private void layoutForRoot(final LayoutInflater inflater, final ViewGroup container) {
        infoText.setVisibility(View.GONE);
        navList.setVisibility(View.VISIBLE);

        int count = 0;
        int size = node.getContent().size();

        for (final HelpNode helpNode : node.getContent()) {
            final View navView = inflater
                    .inflate(R.layout.list_item_help_nav, container, false);

            final TextView textView = (TextView)navView.findViewById(R.id.nav_item_text);
            textView.setText(helpNode.getLabel());

            if (count == size - 1) navView.setBackgroundResource((R.drawable.cell_booking_last_rounded));

            navView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                }
            });

            navList.addView(navView);
            count++;
        }
    }
}
