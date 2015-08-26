package com.handybook.handybook.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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
    @InjectView(R.id.help_webview)
    protected HandyWebView helpWebView;
    @InjectView(R.id.info_layout)
    protected RelativeLayout infoLayout;
    @InjectView(R.id.contact_button)
    public Button contactButton;
    @InjectView(R.id.nav_options_layout)
    public LinearLayout navOptionsLayout;

    @InjectView(R.id.cta_layout)
    public LinearLayout ctaLayout;


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

    public void updateDisplay(final HelpNode node)
    {
        //clear out the existing ctas and navigation buttons
        navOptionsLayout.removeAllViews();
        helpWebView.clearHtml();//prevent user from seeing previous article's content

        if (node == null)
        {
            Crashlytics.log("Trying to display a null help node");
            return;
        }

        switch (node.getType())
        {
            case HelpNode.HelpNodeType.ROOT:
            case HelpNode.HelpNodeType.NAVIGATION:
            case HelpNode.HelpNodeType.BOOKINGS_NAV:
            case HelpNode.HelpNodeType.BOOKING:
            {
                layoutNavList(node);
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

    private void layoutForArticle(final HelpNode node)
    {
        contactButton.setVisibility(GONE);
        //Turn these off, children nodes can turn them on

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
            if(childNode == null)
            {
                continue;
            }

            if (childNode.getType() == null)
            {
                Crashlytics.log("HelpNode " + childNode.getId() + " has null type data");
                continue;
            }

            //todo: info not being used anymore just showing webviews, does that work with the faq pages?
            String info = childNode.getContent();
            if (childNode.getType().equals("help-faq-container"))
            {
                info += "<br/><br/><b>" + getContext().getString(R.string.related_faq) + ":</b>";
                for (final HelpNode faqChild : childNode.getChildren())
                {
                    info += "<br/><a href=" + faqChild.getContent() + ">" + faqChild.getLabel() + "</a>";
                }
            }
            else if (childNode.getType().equals("help-cta"))
            {
                ctaLayout.setVisibility(View.VISIBLE);
                addCtaButton(childNode);
            }
            else if (childNode.getType().equals("help-contact-form"))
            {
                ctaLayout.setVisibility(View.VISIBLE);
                contactButton.setVisibility(View.VISIBLE);
            }

            if (childNode.getType().equals(HelpNode.HelpNodeType.CONTACT))
            {
                contactButton.setVisibility(VISIBLE);
            }

//            infoText.setText(TextUtils.trim(Html.fromHtml(info)));
//            infoText.setMovementMethod(LinkMovementMethod.getInstance());

        }
    }

    private void addCtaButton(HelpNode node)
    {
        String currentLoginToken = "foofwefefwefwefwefwewfewfefwwef";


        int newChildIndex = ctaLayout.getChildCount(); //new index is equal to the old count since the new count is +1
        final CTAButton ctaButton = (CTAButton) inflate(R.layout.fragment_cta_button_template, ctaLayout);
                //((ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.fragment_cta_button_template, ctaLayout)).getChildAt(newChildIndex);

        ctaButton.initFromHelpNode(node, currentLoginToken);

        //can't inject into buttons so need to set the on click listener here to take advantage of fragments injection
//        ctaButton.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(final View v)
//            {
//                HashMap<String, String> params = new HashMap<String, String>();
//                if (currentBookingId != null && !currentBookingId.isEmpty())
//                {
//                    params.put(NavigationManager.PARAM_BOOKING_ID, currentBookingId);
//                }
//                Boolean success = navigationManager.navigateTo(ctaButton.navigationData, params);
//                mixpanel.trackEventHelpCenterDeepLinkClicked(Integer.toString(ctaButton.nodeId), ctaButton.nodeLabel);
//            }
//        });
    }


    private void layoutNavList(final HelpNode node)
    {
        infoLayout.setVisibility(GONE);
        navOptionsLayout.setVisibility(VISIBLE);

        for (final HelpNode childNode : node.getChildren())
        {
            final View navView;

            if(childNode == null || childNode.getType() == null)
            {
                continue;
            }

            if (childNode.getType().equals(HelpNode.HelpNodeType.BOOKING))
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
                textView.setText(childNode.getLabel());
            }
        }
    }

}
