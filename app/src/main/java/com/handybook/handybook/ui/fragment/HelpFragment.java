package com.handybook.handybook.ui.fragment;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.core.HelpNode;
import com.handybook.handybook.core.User;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.ui.activity.HelpActivity;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.ui.widget.MenuButton;
import com.handybook.handybook.util.TextUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class HelpFragment extends InjectedFragment {
    static final String EXTRA_HELP_NODE = "com.handy.handy.EXTRA_HELP_NODE";

    private HelpNode node;

    @InjectView(R.id.menu_button_layout) ViewGroup menuButtonLayout;
    @InjectView(R.id.nav_text) TextView navText;
    @InjectView(R.id.help_header) View helpHeader;
    @InjectView(R.id.help_header_title) TextView headerTitle;
    @InjectView(R.id.info_text) TextView infoText;
    @InjectView(R.id.nav_options_layout) LinearLayout navList;
    @InjectView(R.id.info_layout) View infoLayout;

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
                layoutForRoot(container);
                break;

            case "navigation":
                layoutForNavigation(container);
                menuButtonLayout.setVisibility(View.GONE);
                ((MenuDrawerActivity) getActivity()).setDrawerDisabled(true);
                break;

            case "article":
                layoutForArticle();
                menuButtonLayout.setVisibility(View.GONE);
                ((MenuDrawerActivity) getActivity()).setDrawerDisabled(true);
                break;

            default:
                menuButtonLayout.setVisibility(View.GONE);
                ((MenuDrawerActivity) getActivity()).setDrawerDisabled(true);
                break;
        }

        return view;
    }

    private void layoutForRoot(final ViewGroup container) {
        headerTitle.setText(getResources().getString(R.string.what_need_help_with));
        setHeaderColor(getResources().getColor(R.color.handy_blue));
        layoutNavList(container);
    }

    private void layoutForNavigation(final ViewGroup container) {
        navText.setText(node.getLabel());
        layoutNavList(container);
    }

    private void layoutForArticle() {
        navText.setText(node.getLabel());
        setHeaderColor(getResources().getColor(R.color.handy_yellow));

        String info = node.getContent();

        for (final HelpNode child : node.getChildren()) {
            if (child.getType().equals("help-faq-container")) {
                info += "<br/><br/><b>" + getResources().getString(R.string.related_faq) + ":</b>";

                for (final HelpNode faqChild : child.getChildren()) {
                    info += "<br/><a href=" + faqChild.getContent() + ">" + faqChild.getLabel() + "</a>";
                }
            }
            else if (child.getType().equals("help-cta")) {
                info += "<br/><br/>HAS CTA BUTTONS";
            }
            else if (child.getType().equals("help-contact-form")) {
                info += "<br/><br/>HAS CONTACT FORM";
            }
        }

        infoText.setText(Html.fromHtml(info));
        infoText.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void layoutNavList(final ViewGroup container) {
        infoLayout.setVisibility(View.GONE);
        navList.setVisibility(View.VISIBLE);

        int count = 0;
        int size = node.getChildren().size();

        for (final HelpNode helpNode : node.getChildren()) {
            final View navView = getActivity().getLayoutInflater()
                    .inflate(R.layout.list_item_help_nav, container, false);

            final TextView textView = (TextView)navView.findViewById(R.id.nav_item_text);
            textView.setText(helpNode.getLabel());

            if (node.getType().equals("root")) {
                textView.setTextAppearance(getActivity(), R.style.TextView_Large);
                textView.setTypeface(TextUtils.get(getActivity(), "CircularStd-Book.otf"));
            }

            if (count == size - 1) navView.setBackgroundResource((R.drawable.cell_booking_last_rounded));

            navView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    displayNode(Integer.toString(helpNode.getId()));
                }
            });

            navList.addView(navView);
            count++;
        }
    }

    private void displayNode(final String nodeId) {
        progressDialog.show();

        final User user = userManager.getCurrentUser();
        final String authToken = user != null ? user.getAuthToken() : null;

        dataManager.getHelpInfo(nodeId, authToken, new DataManager.Callback<HelpNode>() {
            @Override
            public void onSuccess(final HelpNode helpNode) {
                if (!allowCallbacks) return;

                final Intent intent = new Intent(getActivity(), HelpActivity.class);
                intent.putExtra(HelpActivity.EXTRA_HELP_NODE, helpNode);
                startActivity(intent);

                progressDialog.dismiss();
            }

            @Override
            public void onError(final DataManager.DataManagerError error) {
                if (!allowCallbacks) return;
                progressDialog.dismiss();
                dataManagerErrorHandler.handleError(getActivity(), error);
            }
        });
    }

    private void setHeaderColor(final int color) {
        final Drawable header = getResources().getDrawable(R.drawable.help_header);
        header.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) helpHeader.setBackgroundDrawable(header);
        else helpHeader.setBackground(header);
    }
}
