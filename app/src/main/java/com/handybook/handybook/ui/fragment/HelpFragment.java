package com.handybook.handybook.ui.fragment;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.core.HelpNode;
import com.handybook.handybook.core.HelpNodeWrapper;
import com.handybook.handybook.core.NavigationManager;
import com.handybook.handybook.core.User;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.ui.activity.HelpActivity;
import com.handybook.handybook.ui.activity.HelpContactActivity;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.ui.view.HelpBannerView;
import com.handybook.handybook.ui.view.HelpNodeView;
import com.handybook.handybook.ui.widget.CTAButton;
import com.handybook.handybook.ui.widget.MenuButton;
import com.squareup.otto.Subscribe;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public final class HelpFragment extends InjectedFragment
{


    //private String currentBookingId; //optional param, if help request is associated with a booking
    //private String currentPathNodeLabels; //what nodes have we traversed to get to the current node
    private String nodeIdToRequest = null;


    private final static String PATH_SEPARATOR = " > ";

    @InjectView(R.id.help_node_view)
    HelpNodeView helpNodeView;
    @InjectView(R.id.help_banner_view)
    HelpBannerView helpBannerView;


//    @InjectView(R.id.scroll_view)
//    ScrollView scrollView;
    @InjectView(R.id.fetch_error_view)
    View errorView;
    @InjectView(R.id.fetch_error_text)
    TextView errorText;


//*******************


    private final String STATE_SCROLL_POSITION = "SCROLL_POSITION";
    static final String EXTRA_HELP_NODE = "com.handy.handy.EXTRA_HELP_NODE";
    private static String HELP_CONTACT_FORM_NODE_TYPE = "help-contact-form";
    static final String EXTRA_BOOKING_ID = "com.handy.handy.EXTRA_BOOKING_ID";
    static final String EXTRA_LOGIN_TOKEN = "com.handy.handy.EXTRA_LOGIN_TOKEN";
    static final String EXTRA_PATH = "com.handy.handy.EXTRA_PATH";

    //private HelpNode currentNode;
    //private static HelpNode rootNode;
    private String currentBookingId;
    private String currentLoginToken;
    private String path;

    @InjectView(R.id.menu_button_layout)
    ViewGroup menuButtonLayout;
    @InjectView(R.id.help_header)
    View helpHeader;

    /*
    @InjectView(R.id.help_header_title)
    TextView headerTitle;
    @InjectView(R.id.info_text)
    TextView infoText;
    @InjectView(R.id.nav_options_layout)
    LinearLayout navList;
    @InjectView(R.id.info_layout)
    View infoLayout;
    @InjectView(R.id.help_icon)
    ImageView helpIcon;
    @InjectView(R.id.help_triangle)
    ImageView helpTriangleView;
    @InjectView(R.id.cta_layout)
    ViewGroup ctaLayout;
    @InjectView(R.id.contact_button)
    Button contactButton;
    */

    @InjectView(R.id.scroll_view)
    ScrollView scrollView;
    @InjectView(R.id.close_img)
    ImageView closeImage;
    @InjectView(R.id.back_img)
    ImageView backImage;

    //@InjectView(R.id.cta_button_template_layout) ViewGroup ctaButtonTemplateLayout;

    public static HelpFragment newInstance(final String bookingId,
                                           final String loginToken,
                                           final String path,
                                           final String nodeId
    )
    {
        final HelpFragment fragment = new HelpFragment();
        final Bundle args = new Bundle();
        //args.putParcelable(EXTRA_HELP_NODE, node);
        args.putString(EXTRA_BOOKING_ID, bookingId);
        args.putString(EXTRA_LOGIN_TOKEN, loginToken);
        args.putString(EXTRA_PATH, path);
        args.putString(BundleKeys.HELP_NODE_ID, nodeId);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public final void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //NORTAL
        if (getArguments() != null && getArguments().containsKey(BundleKeys.BOOKING_ID))
        {
            currentBookingId = getArguments().getString(BundleKeys.BOOKING_ID);
        } else
        {
            currentBookingId = "";
        }

        if (getArguments() != null && getArguments().containsKey(BundleKeys.HELP_NODE_ID))
        {
            nodeIdToRequest = getArguments().getString(BundleKeys.HELP_NODE_ID);
        }


        System.out.println("ZZZZ Node id to request : " + nodeIdToRequest);

        //ORIGINAL


        //currentNode = getArguments().getParcelable(EXTRA_HELP_NODE);
        currentBookingId = getArguments().getString(EXTRA_BOOKING_ID);
        currentLoginToken = getArguments().getString(EXTRA_LOGIN_TOKEN);
        path = getArguments().getString(EXTRA_PATH, "");


        System.out.println("ZZZZ current path : " + path);

    }


    @Override
    public void onResume()
    {
        super.onResume();
        //if (!MainActivityFragment.clearingBackStack)
        {
            //bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));  //TODO: what is the relevant analogue?
            progressDialog.show();
            System.out.println("Requesting node : " + nodeIdToRequest);
            bus.post(new HandyEvent.RequestHelpNode(nodeIdToRequest, currentBookingId));
        }
    }


    //Event Listeners

    @Subscribe
    public void onReceiveHelpNodeSuccess(HandyEvent.ReceiveHelpNodeSuccess event)
    {
        //bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));

        System.out.println("Received help node " + (event != null ? event.helpNode.getId() : " is null"));

        progressDialog.dismiss();
        scrollView.setVisibility(View.VISIBLE);
        HelpNode helpNode = event.helpNode;
        trackPath(helpNode);
        updateDisplay(helpNode);



        //todo: move this out to its own function / mixpanel tracking shouold be wrangled into the navigation events
        //if (savedInstanceState == null)
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

    //TODO: Make this smarter and recognize back tracking
    private void trackPath(HelpNode node)
    {
        //Don't add the root node to the path as per CX spec
        if (!node.getType().equals(HelpNode.HelpNodeType.ROOT))
        {
            path += (!path.isEmpty() ? PATH_SEPARATOR : "") + node.getLabel();
        }
    }

    @Subscribe
    public void onReceiveHelpNodeError(HandyEvent.ReceiveHelpNodeError event)
    {
        // bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        progressDialog.dismiss();
        scrollView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
        errorText.setText(R.string.error_fetching_connectivity_issue);
    }

    @OnClick(R.id.try_again_button)
    public void doTryAgain()
    {
        errorView.setVisibility(View.GONE);
        //bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        progressDialog.show();
        bus.post(new HandyEvent.RequestHelpNode(nodeIdToRequest, currentBookingId));
    }


    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState)
    {
        //TODO: Should we bother inflating if we're popping the fragment stack?

        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_help, container, false);

        ButterKnife.inject(this, view);

        //Add the menu drawer button
        final MenuButton menuButton = new MenuButton(getActivity(), menuButtonLayout);
        menuButtonLayout.addView(menuButton);



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

        if (nodeIdToRequest == null)
        {
            helpBannerView.navText.setText(R.string.help);
        }

        if (getArguments() != null && getArguments().containsKey(BundleKeys.PATH))
        {
            path = getArguments().getString(BundleKeys.PATH);
        } else
        {
            path = "";
        }

        setupBackClickListeners();
        updateButtonAndDrawerVisiblity(null);

        return view;
    }

    private void updateDisplay(final HelpNode node)
    {
        helpBannerView.updateDisplay(node);
        helpNodeView.updateDisplay(node);
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
                        Bundle arguments = new Bundle();
                        arguments.putString(BundleKeys.HELP_NODE_ID, Integer.toString(childNode.getId()));
                        arguments.putString(BundleKeys.PATH, path);

                        //TODO: Replace with activity based navigation
                        //bus.post(new HandyEvent.NavigateToTab(MainViewTab.HELP, arguments));
                        navigateToHelpPage(childNode);
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

                String childNodeType = childNode.getType();
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
                            Bundle arguments = new Bundle();
                            arguments.putString(BundleKeys.PATH, path);
                            arguments.putParcelable(BundleKeys.HELP_NODE, childNode);

                            //TODO: Replace with activity based navigation
                            //HandyEvent.NavigateToTab navigateEvent = new HandyEvent.NavigateToTab(MainViewTab.HELP_CONTACT, arguments);
                            //bus.post(navigateEvent);
                            navigateToHelpContactPage(childNode);
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

        closeImage.setOnClickListener(new View.OnClickListener()
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

    private void updateButtonAndDrawerVisiblity(HelpNode node)
    {
        if (node == null)
        {
            helpBannerView.backImage.setVisibility(View.INVISIBLE);
            closeImage.setVisibility(View.INVISIBLE);
        } else
        {
            switch (node.getType())
            {
                case "root":
                {
                    backImage.setVisibility(View.GONE);
                    menuButtonLayout.setVisibility(View.VISIBLE);
                    ((MenuDrawerActivity) getActivity()).setDrawerDisabled(false);
                }
                break;

                case "navigation":
                case "dynamic-bookings-navigation":
                case "booking":
                case "article":
                default:
                {
                    backImage.setVisibility(View.VISIBLE);
                    menuButtonLayout.setVisibility(View.GONE);
                    ((MenuDrawerActivity) getActivity()).setDrawerDisabled(true);
                }
                break;
            }
        }
    }


    @Override
    public void onSaveInstanceState(final Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putIntArray(STATE_SCROLL_POSITION,
                new int[]{scrollView.getScrollX(), scrollView.getScrollY()});
    }




//    private void addCtaButton(HelpNode node)
//    {
//        int newChildIndex = ctaLayout.getChildCount(); //new index is equal to the old count since the new count is +1
//        final CTAButton ctaButton = (CTAButton) ((ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.fragment_cta_button_template, ctaLayout)).getChildAt(newChildIndex);
//        ctaButton.initFromHelpNode(node, currentLoginToken);
//        //can't inject into buttons so need to set the on click listener here to take advantage of fragments injection
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
//    }

    private void addCtaButtonListeners()
    {
        //int newChildIndex = ctaLayout.getChildCount(); //new index is equal to the old count since the new count is +1
          //     (CTAButton) ((ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.fragment_cta_button_template, ctaLayout)).getChildAt(newChildIndex);
        //ctaButton.initFromHelpNode(node, currentLoginToken);
        //can't inject into buttons so need to set the on click listener here to take advantage of fragments injection

        if(helpNodeView.ctaLayout.getVisibility() != View.VISIBLE)
        {
            return;
        }

        for(int i = 0; i < helpNodeView.ctaLayout.getChildCount(); i++)
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


//    private void layoutNavList(final ViewGroup container)
//    {
//        infoLayout.setVisibility(View.GONE);
//        navList.setVisibility(View.VISIBLE);
//
//        if (currentNode.getType().equals("dynamic-bookings-navigation"))
//        {
//            setHeaderColor(getResources().getColor(R.color.handy_teal));
//        }
//
//        int count = 0;
//        int size = currentNode.getChildren().size();
//
//        for (final HelpNode helpNode : currentNode.getChildren())
//        {
//            final View navView;
//
//            if (helpNode.getType().equals("booking"))
//            {
//                navView = getActivity().getLayoutInflater()
//                        .inflate(R.layout.list_item_help_booking_nav, container, false);
//
//                TextView textView = (TextView) navView.findViewById(R.id.service_text);
//                textView.setText(helpNode.getService());
//
//                textView = (TextView) navView.findViewById(R.id.date_text);
//                textView.setText(TextUtils.formatDate(helpNode.getStartDate(), "EEEE',' MMMM d"));
//
//                textView = (TextView) navView.findViewById(R.id.time_text);
//                textView.setText(TextUtils.formatDate(helpNode.getStartDate(), "h:mmaaa \u2013 ")
//                        + TextUtils.formatDecimal(helpNode.getHours(), "#.# ")
//                        + getResources().getQuantityString(R.plurals.hour, (int) helpNode.getHours()));
//            } else
//            {
//                if (currentNode.getType().equals("root"))
//                {
//                    navView = getActivity().getLayoutInflater()
//                            .inflate(R.layout.list_item_help_nav_main, container, false);
//                } else
//                {
//                    navView = getActivity().getLayoutInflater()
//                            .inflate(R.layout.list_item_help_nav, container, false);
//                }
//
//                final TextView textView = (TextView) navView.findViewById(R.id.nav_item_text);
//                textView.setText(helpNode.getLabel());
//
//                if (currentNode.getType().equals("root"))
//                {
//                    textView.setTextAppearance(getActivity(), R.style.TextView_Large);
//                    textView.setTypeface(TextUtils.get(getActivity(), "CircularStd-Book.otf"));
//                }
//            }
//
//            if (count == size - 1)
//            {
//                navView.setBackgroundResource((R.drawable.cell_booking_last_rounded));
//            }
//
//            navView.setOnClickListener(new View.OnClickListener()
//            {
//                @Override
//                public void onClick(final View v)
//                {
//                    if (helpNode.getType().equals("help-log-in-form"))
//                    {
//                        toast.setText(getString(R.string.please_login));
//                        toast.show();
//                    } else
//                    {
//                        displayNextNode(helpNode);
//                    }
//                }
//            });
//
//            navList.addView(navView);
//            count++;
//        }
//    }

    private void ZZZZ_displayNextNode(final HelpNode node)
    {

        progressDialog.show();

        final User user = userManager.getCurrentUser();
        final String authToken = user != null ? user.getAuthToken() : null;

        //Why are we assigning currentBookingId here but we need to wait on the CB for loginToken to come back correctly?
        if (node.getType().equals("booking"))
        {
            currentBookingId = Integer.toString(node.getId());
            dataManager.getHelpBookingsInfo(Integer.toString(node.getId()), authToken, currentBookingId, helpNodeCallback);
        } else
        {
            dataManager.getHelpInfo(Integer.toString(node.getId()), authToken, currentBookingId, helpNodeCallback);
        }
    }

    private void ZZZZZ_setHeaderColor(final int color)
    {
        final Drawable header = getResources().getDrawable(R.drawable.help_header_purple);
        header.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
            helpHeader.setBackgroundDrawable(header);
        else helpHeader.setBackground(header);
    }

    private DataManager.Callback<HelpNodeWrapper> helpNodeCallback = new DataManager.Callback<HelpNodeWrapper>()
    {
        @Override
        public void onSuccess(final HelpNodeWrapper helpNodeWrapper)
        {
            HelpNode helpNode = helpNodeWrapper.getHelpNode();

            if (helpNode == null)
            {
                return;
            }

            if (helpNode.getType() != null && helpNode.getType().equals("article"))
            {
                currentLoginToken = helpNode.getLoginToken();
            }

            if (!allowCallbacks) return;

            final Intent intent = new Intent(getActivity(), HelpActivity.class);
            //intent.putExtra(HelpActivity.EXTRA_HELP_NODE, helpNode);
            intent.putExtra(HelpActivity.EXTRA_BOOKING_ID, currentBookingId);
            intent.putExtra(HelpActivity.EXTRA_LOGIN_TOKEN, currentLoginToken);
            intent.putExtra(HelpActivity.EXTRA_PATH, path.length() > 0 ? path += " -> "
                    + helpNode.getLabel() : helpNode.getLabel());
            startActivity(intent);

            progressDialog.dismiss();
            mixpanel.trackEventHelpCenterNavigation(helpNode.getLabel());
        }

        @Override
        public void onError(final DataManager.DataManagerError error)
        {
            if (!allowCallbacks) return;
            progressDialog.dismiss();
            dataManagerErrorHandler.handleError(getActivity(), error);
        }
    };

    private void navigateToHelpPage(HelpNode helpNode)
    {
        final Intent intent = new Intent(getActivity(), HelpActivity.class);
        //intent.putExtra(HelpActivity.EXTRA_HELP_NODE, helpNode);
        intent.putExtra(HelpActivity.EXTRA_BOOKING_ID, currentBookingId);
        intent.putExtra(HelpActivity.EXTRA_LOGIN_TOKEN, currentLoginToken);
        intent.putExtra(HelpActivity.EXTRA_PATH, path.length() > 0 ? path += " -> "
                + helpNode.getLabel() : helpNode.getLabel());

        intent.putExtra(BundleKeys.HELP_NODE_ID, Integer.toString(helpNode.getId()));

        System.out.println("ZZZ navigate ot help page : " + helpNode.getId());

        startActivity(intent);
    }

    private void navigateToHelpContactPage(HelpNode helpNode)
    {
        final Intent intent = new Intent(getActivity(), HelpContactActivity.class);
        //intent.putExtra(HelpActivity.EXTRA_HELP_NODE, helpNode);
        intent.putExtra(HelpActivity.EXTRA_BOOKING_ID, currentBookingId);
        intent.putExtra(HelpActivity.EXTRA_LOGIN_TOKEN, currentLoginToken);
        intent.putExtra(HelpActivity.EXTRA_PATH, path.length() > 0 ? path += " -> "
                + helpNode.getLabel() : helpNode.getLabel());

        intent.putExtra(BundleKeys.HELP_NODE_ID, Integer.toString(helpNode.getId()));

        System.out.println("ZZZ navigate ot help page : " + helpNode.getId());

        startActivity(intent);
    }




}
