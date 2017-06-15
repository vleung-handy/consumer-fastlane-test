package com.handybook.handybook.helpcenter.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.core.ui.widget.MenuButton;
import com.handybook.handybook.helpcenter.model.HelpNode;
import com.handybook.handybook.library.ui.view.InjectedRelativeLayout;

import butterknife.BindView;

public final class HelpBannerView extends InjectedRelativeLayout {

    @BindView(R.id.menu_button_layout)
    ViewGroup menuButtonLayout;
    @BindView(R.id.back_img)
    public ImageView backImage;
    @BindView(R.id.close_img)
    public ImageView closeImage;
    @BindView(R.id.nav_text)
    public TextView navText;

    public HelpBannerView(final Context context) {
        super(context);
    }

    public HelpBannerView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public HelpBannerView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    public void updateDisplay(HelpNode helpNode) {
        if (helpNode == null) {
            Crashlytics.log("Tried to construct a help banner view for a null node");
            return;
        }

        //Add the menu drawer button
        final MenuButton menuButton = new MenuButton(getContext(), menuButtonLayout);
        menuButtonLayout.addView(menuButton);

        backImage.setVisibility(View.VISIBLE);
        navText.setText(R.string.contact_us);

        switch (helpNode.getType()) {
            case HelpNode.HelpNodeType.ROOT: {
                layoutForRoot();
                backImage.setVisibility(View.GONE);
                menuButtonLayout.setVisibility(View.VISIBLE);
            }
            break;

            case HelpNode.HelpNodeType.NAVIGATION:
            case HelpNode.HelpNodeType.BOOKINGS_NAV:
            case HelpNode.HelpNodeType.BOOKING: {
                layoutForNavigation(helpNode);
                backImage.setVisibility(View.VISIBLE);

                menuButtonLayout.setVisibility(View.GONE);

            }
            break;

            case HelpNode.HelpNodeType.ARTICLE: {
                layoutForArticle(helpNode);
                backImage.setVisibility(View.VISIBLE);

                menuButtonLayout.setVisibility(View.GONE);
            }
            break;

            default: {
                Crashlytics.log("Unrecognized node type : " + helpNode.getType());
                backImage.setVisibility(View.VISIBLE);

                menuButtonLayout.setVisibility(View.GONE);
            }
            break;
        }
    }

    private void layoutForNavigation(final HelpNode node) {
        if (node.getType().equals(HelpNode.HelpNodeType.BOOKING)) {
            navText.setText(getContext().getString(R.string.help));
        }
        else {
            navText.setText(node.getLabel());
        }
    }

    private void layoutForArticle(final HelpNode node) {
        navText.setText(R.string.help);
    }

    private void layoutForRoot() {
        navText.setText(R.string.help);
    }

}
