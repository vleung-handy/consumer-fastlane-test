package com.handybook.handybook.ui.view;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.core.HelpNode;
import com.handybook.handybook.ui.widget.CTAButton;
import com.handybook.handybook.util.TextUtils;

import butterknife.InjectView;

public final class HelpNodeView extends InjectedRelativeLayout
{
    //Help header block
    @InjectView(R.id.help_header)
    View helpHeader;
    @InjectView(R.id.help_header_title)
    TextView headerTitle;
    @InjectView(R.id.help_icon)
    ImageView helpIcon;
    @InjectView(R.id.help_triangle)
    ImageView helpTriangleView;

    //Main content webview
    @InjectView(R.id.info_layout)
    protected RelativeLayout infoLayout;
    @InjectView(R.id.help_webview)
    protected HandyWebView helpWebView;

    //Contact Us
    @InjectView(R.id.contact_button)
    public Button contactButton;

    //Help Node Navigation Links
    @InjectView(R.id.nav_options_layout)
    public LinearLayout navOptionsLayout;

    //CTAs
    @InjectView(R.id.cta_layout)
    public LinearLayout ctaLayout;

    //TODO: Currently passing this around, could get it from a service if that would be cleaner
    private String currentLoginToken;

    public HelpNodeView(final Context context)
    {
        super(context);
    }

    public HelpNodeView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public HelpNodeView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public void updateDisplay(final HelpNode node, final String currentLoginToken)
    {
        //TODO: Ugh, this should not be here, ugh
        this.currentLoginToken = currentLoginToken;

        //clear out any existing ctas and navigation buttons
        navOptionsLayout.removeAllViews();
        ctaLayout.removeAllViews();

        helpWebView.clearHtml();//prevent user from seeing previous article's content

        if (node == null)
        {
            Crashlytics.log("Trying to display a null help node");
            return;
        }

        switch (node.getType())
        {
            case HelpNode.HelpNodeType.ROOT:
            {
                layoutForRoot(node);
            }
            break;

            case HelpNode.HelpNodeType.NAVIGATION:
            case HelpNode.HelpNodeType.BOOKINGS_NAV:
            case HelpNode.HelpNodeType.BOOKING:
            {
                layoutForNavigation(node);
            }
            break;

            case HelpNode.HelpNodeType.ARTICLE:
            {
                layoutForArticle(node);
            }
            break;

            default:
            {
                Crashlytics.log("Unrecognized node type : " + node.getType());
            }
            break;
        }
    }

    private void layoutForRoot(final HelpNode node)
    {
        headerTitle.setText(getResources().getString(R.string.what_need_help_with));
        setHeaderColor(getResources().getColor(R.color.handy_blue));
        helpIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_help_smiley));
        layoutNavList(node);
    }

    private void layoutForNavigation(final HelpNode node)
    {
        layoutNavList(node);
    }

    private void layoutForArticle(final HelpNode node)
    {
        headerTitle.setText(node.getLabel());

        setHeaderColor(getResources().getColor(R.color.handy_yellow));
        helpIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_help_bulb));
        helpTriangleView.setVisibility(View.VISIBLE);

        contactButton.setVisibility(GONE);

        helpWebView.loadHtml(node.getContent(), new HandyWebView.InvalidateCallback()
        {
            @Override
            public void invalidate()
            {
                if (infoLayout.getVisibility() != VISIBLE)
                {
                    infoLayout.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in));
                    infoLayout.setVisibility(VISIBLE);
                }
            }
        });

        for (final HelpNode childNode : node.getChildren())
        {
            if (childNode == null)
            {
                continue;
            }

            if (childNode.getType() == null)
            {
                Crashlytics.log("HelpNode " + childNode.getId() + " has null type data");
                continue;
            }

            if (childNode.getType().equals(HelpNode.HelpNodeType.CTA))
            {
                ctaLayout.setVisibility(View.VISIBLE);
                addCtaButton(childNode);
            }
            else if (childNode.getType().equals(HelpNode.HelpNodeType.CONTACT))
            {
                contactButton.setVisibility(View.VISIBLE);
            }
        }
    }

    private void addCtaButton(HelpNode node)
    {
        final CTAButton ctaButton = (CTAButton) inflate(R.layout.fragment_cta_button_template, ctaLayout);
        ctaButton.initFromHelpNode(node, this.currentLoginToken);
    }

    private void layoutNavList(final HelpNode node)
    {
        infoLayout.setVisibility(GONE);
        navOptionsLayout.setVisibility(VISIBLE);

        if (node.getType().equals(HelpNode.HelpNodeType.BOOKINGS_NAV))
        {
            setHeaderColor(getResources().getColor(R.color.handy_teal));
        }

        for (final HelpNode childNode : node.getChildren())
        {
            final View navView;

            if (childNode == null)
            {
                continue;
            }

            if (childNode.getType() != null && childNode.getType().equals(HelpNode.HelpNodeType.BOOKING))
            {
                navView = inflate(R.layout.list_item_help_booking_nav, navOptionsLayout);

                TextView textView = (TextView) navView.findViewById(R.id.service_text);
                textView.setText(childNode.getService());

                textView = (TextView) navView.findViewById(R.id.date_text);
                textView.setText(TextUtils.formatDate(childNode.getStartDate(), "EEEE',' MMMM d"));

                textView = (TextView) navView.findViewById(R.id.time_text);
                textView.setText(TextUtils.formatDate(childNode.getStartDate(), "h:mmaaa \u2013 ")
                        + TextUtils.formatDecimal(childNode.getHours(), "#.# ")
                        + getContext().getResources().getQuantityString(R.plurals.hour, (int) childNode.getHours()));
            }
            else
            {
                navView = inflate(R.layout.list_item_help_nav, navOptionsLayout);
                final TextView textView = (TextView) navView.findViewById(R.id.nav_item_text);

                //if the type/label is null we still inflate to keep our click listeners in sync but we hide the inflated view since it is empty
                textView.setText(childNode.getLabel());

                if(childNode.getLabel() == null)
                {
                    navView.setVisibility(View.GONE);
                }
            }
        }
    }

    private void setHeaderColor(final int color)
    {
        final Drawable header = getResources().getDrawable(R.drawable.help_header_purple);
        if(header != null)
        {
            header.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
        {
            helpHeader.setBackgroundDrawable(header);
        }
        else
        {
            helpHeader.setBackground(header);
        }
    }

}
