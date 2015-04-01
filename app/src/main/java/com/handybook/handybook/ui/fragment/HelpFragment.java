package com.handybook.handybook.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.core.HelpNode;
import com.handybook.handybook.ui.widget.MenuButton;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class HelpFragment extends InjectedFragment {
    static final String EXTRA_HELP_NODE = "com.handy.handy.EXTRA_HELP_NODE";

    private HelpNode node;

    @InjectView(R.id.menu_button_layout) ViewGroup menuButtonLayout;

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

        return view;
    }
}
