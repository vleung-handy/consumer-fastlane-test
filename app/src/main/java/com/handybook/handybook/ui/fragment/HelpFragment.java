package com.handybook.handybook.ui.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.core.HelpNode;
import com.handybook.handybook.core.NavigationManager;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.manager.HelpManager;
import com.handybook.handybook.ui.activity.HelpActivity;
import com.handybook.handybook.ui.activity.HelpContactActivity;
import com.handybook.handybook.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.ui.view.HelpBannerView;
import com.handybook.handybook.ui.view.HelpNodeView;
import com.handybook.handybook.ui.widget.CTAButton;
import com.squareup.otto.Subscribe;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class HelpFragment extends InjectedFragment
{

    @Bind(R.id.help_node_view)
    HelpNodeView helpNodeView;
    @Bind(R.id.help_banner_view)
    HelpBannerView helpBannerView;
    @Bind(R.id.fetch_error_view)
    View errorView;
    @Bind(R.id.fetch_error_text)
    TextView errorText;
    @Bind(R.id.scroll_view)
    ScrollView scrollView;

    private final String STATE_SCROLL_POSITION = "SCROLL_POSITION";

    private String nodeIdToRequest = null;
    private boolean nodeIsBooking = false;
    private final static String PATH_SEPARATOR = " > ";
    private String currentBookingId = "";
    private String currentLoginToken = "";
    private String path = "";
    private HelpNode associatedNode = null;

    public static HelpFragment newInstance(final String bookingId,
                                           final String loginToken,
                                           final String path,
                                           final HelpNode helpNode,
                                           final String nodeId,
                                           final boolean nodeIsBooking
    )
    {
        final HelpFragment fragment = new HelpFragment();
        final Bundle args = new Bundle();

        args.putString(BundleKeys.BOOKING_ID, bookingId);
        args.putString(BundleKeys.LOGIN_TOKEN, loginToken);
        args.putString(BundleKeys.PATH, path);
        args.putString(BundleKeys.HELP_NODE_ID, nodeId);
        args.putParcelable(BundleKeys.HELP_NODE, helpNode);
        args.putBoolean(BundleKeys.HELP_NODE_IS_BOOKING, nodeIsBooking);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getArguments() == null)
        {
            return;
        }

        //optional parcelable param, can't pass a default value
        if (getArguments().containsKey(BundleKeys.HELP_NODE))
        {
            associatedNode = getArguments().getParcelable(BundleKeys.HELP_NODE);
        }

        nodeIdToRequest = getArguments().getString(BundleKeys.HELP_NODE_ID, HelpManager.ROOT_NODE_ID);
        nodeIsBooking = getArguments().getBoolean(BundleKeys.HELP_NODE_IS_BOOKING, false);
        currentBookingId = getArguments().getString(BundleKeys.BOOKING_ID, "");
        currentLoginToken = getArguments().getString(BundleKeys.LOGIN_TOKEN, "");
        path = getArguments().getString(BundleKeys.PATH, "");
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState)
    {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_help, container, false);

        ButterKnife.bind(this, view);

        //Restore saved scroll position
        if (savedInstanceState != null)
        {
            final int[] position = savedInstanceState.getIntArray(STATE_SCROLL_POSITION);
            if (position != null)
            {
                scrollView.post(new Runnable()
                {
                    public void run()
                    {
                        scrollView.scrollTo(position[0], position[1]);
                    }
                });
            }
        }

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) //needed to workaround a bug in android 4.4 that cause webview artifacts to show.
        {
            view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        setupBackClickListeners();

        return view;
    }

    @Override
    public void onSaveInstanceState(final Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putIntArray(STATE_SCROLL_POSITION,
                new int[]{scrollView.getScrollX(), scrollView.getScrollY()});
    }

    @Override
    public void onResume()
    {
        super.onResume();

        //If we don't have a node to display ask for one based on our requested node id
        //if we had node data passed in to us we can just use it
        if (this.associatedNode == null)
        {
            progressDialog.show();
            if (nodeIsBooking)
            {
                bus.post(new HandyEvent.RequestHelpBookingNode(nodeIdToRequest, currentBookingId));
            } else
            {
                bus.post(new HandyEvent.RequestHelpNode(nodeIdToRequest, currentBookingId));
            }
        } else
        {
            helpNodeReceivedForPage(this.associatedNode);
        }
    }

    //Event Listeners
    @Subscribe
    public void onReceiveHelpNodeSuccess(HandyEvent.ReceiveHelpNodeSuccess event)
    {
        onHelpNodeSuccess(event.helpNode);
    }

    @Subscribe
    public void onReceiveHelpNodeError(HandyEvent.ReceiveHelpNodeError event)
    {
        onHelpNodeError();
    }

    @Subscribe
    public void onReceiveHelpBookingNodeSuccess(HandyEvent.ReceiveHelpBookingNodeSuccess event)
    {
        onHelpNodeSuccess(event.helpNode);
    }

    @Subscribe
    public void onReceiveHelpBookingNodeError(HandyEvent.ReceiveHelpBookingNodeError event)
    {
        onHelpNodeError();
    }

    private void onHelpNodeSuccess(HelpNode helpNode)
    {
        progressDialog.dismiss();

        if (this.associatedNode == null)
        {
            //add to our arguments so if we get recreated we have it
            this.associatedNode = helpNode;
            getArguments().putParcelable(BundleKeys.HELP_NODE, helpNode);
            helpNodeReceivedForPage(helpNode);
        } else
        {
            helpNodeReceivedForNextPage(helpNode);
        }
    }

    private void helpNodeReceivedForPage(HelpNode helpNode)
    {
        scrollView.setVisibility(View.VISIBLE);
        trackPath(helpNode);
        updateDisplay(helpNode);

        //UPGRADE: move this out to its own function / mixpanel tracking shouold be wrangled into the navigation events
        if (helpNode != null)
        {
            switch (helpNode.getType())
            {
                case HelpNode.HelpNodeType.ROOT:
                    mixpanel.trackEventHelpCenterOpened();
                    break;

                case HelpNode.HelpNodeType.ARTICLE:
                    mixpanel.trackEventHelpCenterLeaf(Integer.toString(helpNode.getId()), helpNode.getLabel());
                    break;
            }
        }
    }

    private void helpNodeReceivedForNextPage(HelpNode helpNode)
    {
        if (helpNode.getType().equals(HelpNode.HelpNodeType.CONTACT))
        {
            navigateToHelpContactPage(helpNode);
        } else
        {
            navigateToHelpPage(helpNode);
        }
    }

    private void onHelpNodeError()
    {
        progressDialog.dismiss();
        scrollView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
        errorText.setText(R.string.error_fetching_connectivity_issue);
    }

    @OnClick(R.id.try_again_button)
    public void doTryAgain()
    {
        errorView.setVisibility(View.GONE);
        progressDialog.show();
        if (nodeIsBooking)
        {
            bus.post(new HandyEvent.RequestHelpBookingNode(nodeIdToRequest, currentBookingId));
        } else
        {
            bus.post(new HandyEvent.RequestHelpNode(nodeIdToRequest, currentBookingId));
        }
    }

    private void requestChildNodeDetails(HelpNode childNode)
    {
        progressDialog.show();
        if (childNode.getType().equals(HelpNode.HelpNodeType.BOOKING))
        {
            bus.post(new HandyEvent.RequestHelpBookingNode(Integer.toString(childNode.getId()), currentBookingId));
        } else
        {
            bus.post(new HandyEvent.RequestHelpNode(Integer.toString(childNode.getId()), currentBookingId));
        }
    }

    //TODO: Make this smarter and recognize back tracking
    private void trackPath(HelpNode node)
    {
        if (node == null)
        {
            return;
        }

        path = (path == null ? "" : path);

        //Don't add the root node to the path as per CX spec
        if (!node.getType().equals(HelpNode.HelpNodeType.ROOT))
        {
            path += (!path.isEmpty() ? PATH_SEPARATOR : "") + node.getLabel();
        }
    }

    private void updateDisplay(final HelpNode node)
    {
        helpBannerView.updateDisplay(node);
        helpNodeView.updateDisplay(node, currentLoginToken);
        setupClickListeners(node);
    }

    private void setupClickListeners(HelpNode helpNode)
    {
        switch (helpNode.getType())
        {
            case HelpNode.HelpNodeType.ROOT:
            case HelpNode.HelpNodeType.NAVIGATION:
            case HelpNode.HelpNodeType.BOOKINGS_NAV:
            case HelpNode.HelpNodeType.BOOKING:
            {
                setupNavigationListClickListeners(helpNode);
            }
            break;

            case HelpNode.HelpNodeType.ARTICLE:
            {
                setupArticleClickListeners(helpNode);
                addCtaButtonListeners();
            }
            break;
        }
    }

    private void setupNavigationListClickListeners(final HelpNode helpNode)
    {
        for (int i = 0; i < helpNode.getChildren().size(); i++)
        {
            final HelpNode childNode = helpNode.getChildren().get(i);
            if (childNode == null || childNode.getType() == null)
            {
                continue;
            }

            final View navView = helpNodeView.navOptionsLayout.getChildAt(i);

            navView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(final View v)
                {
                    if (childNode.getType().equals(HelpNode.HelpNodeType.LOG_IN_FORM))
                    {
                        showToast(R.string.please_login);
                    } else
                    {
                        requestChildNodeDetails(childNode);
                    }
                }
            });
        }
    }

    private void setupArticleClickListeners(final HelpNode helpNode)
    {
        if (helpNode.getChildren().size() > 0)
        {
            for (final HelpNode childNode : helpNode.getChildren())
            {
                if (childNode == null)
                {
                    continue;
                }

                final String childNodeType = childNode.getType();
                if (childNodeType == null)
                {
                    Crashlytics.log("HelpNode " + childNode.getId() + " has null data");
                    continue;
                }

                if (childNodeType.equals(HelpNode.HelpNodeType.CONTACT))
                {
                    helpNodeView.contactButton.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            requestChildNodeDetails(childNode);
                        }
                    });
                }
            }
        }
    }

    private void setupBackClickListeners()
    {
        helpBannerView.backImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                getActivity().onBackPressed();
            }
        });

        helpBannerView.closeImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                final Intent toHomeScreenIntent = new Intent(getActivity(), ServiceCategoriesActivity.class);
                toHomeScreenIntent.addFlags((Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                startActivity(toHomeScreenIntent);
            }
        });
    }

    private void addCtaButtonListeners()
    {
        if (helpNodeView.ctaLayout.getVisibility() != View.VISIBLE)
        {
            return;
        }

        for (int i = 0; i < helpNodeView.ctaLayout.getChildCount(); i++)
        {
            final CTAButton ctaButton = (CTAButton) helpNodeView.ctaLayout.getChildAt(i);
            ctaButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(final View v)
                {
                    HashMap<String, String> params = new HashMap<String, String>();
                    if (currentBookingId != null && !currentBookingId.isEmpty())
                    {
                        params.put(NavigationManager.PARAM_BOOKING_ID, currentBookingId);
                    }
                    Boolean success = navigationManager.navigateTo(ctaButton.navigationData, params);
                    mixpanel.trackEventHelpCenterDeepLinkClicked(Integer.toString(ctaButton.nodeId), ctaButton.nodeLabel);
                }
            });
        }
    }

    private void navigateToHelpPage(HelpNode helpNode)
    {
        final Intent intent = new Intent(getActivity(), HelpActivity.class);
        intent.putExtra(BundleKeys.HELP_NODE, helpNode);
        intent.putExtra(BundleKeys.HELP_NODE_ID, Integer.toString(helpNode.getId()));
        intent.putExtra(BundleKeys.HELP_NODE_IS_BOOKING, helpNode.getType().equals(HelpNode.HelpNodeType.BOOKING));
        intent.putExtra(BundleKeys.BOOKING_ID, currentBookingId);
        intent.putExtra(BundleKeys.LOGIN_TOKEN, currentLoginToken);
        intent.putExtra(BundleKeys.PATH, path);
        startActivity(intent);
    }

    private void navigateToHelpContactPage(HelpNode helpNode)
    {
        final Intent intent = new Intent(getActivity(), HelpContactActivity.class);
        intent.putExtra(BundleKeys.HELP_NODE, helpNode);
        intent.putExtra(BundleKeys.PATH, path);
        intent.putExtra(BundleKeys.BOOKING_ID, currentBookingId);
        startActivity(intent);
    }

}
