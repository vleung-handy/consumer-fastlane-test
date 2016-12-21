package com.handybook.handybook.booking.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.BookingOption;
import com.handybook.handybook.booking.ui.activity.BookingDateActivity;
import com.handybook.handybook.booking.ui.activity.BookingOptionsActivity;
import com.handybook.handybook.booking.ui.view.BookingOptionsIndexView;
import com.handybook.handybook.booking.ui.view.BookingOptionsSelectView;
import com.handybook.handybook.booking.ui.view.BookingOptionsSpinnerView;
import com.handybook.handybook.booking.ui.view.BookingOptionsTextView;
import com.handybook.handybook.booking.ui.view.BookingOptionsView;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.booking.BookingDetailsLog;
import com.handybook.handybook.logger.handylogger.model.booking.BookingFunnelLog;
import com.handybook.handybook.logger.handylogger.model.booking.BookingRequestProLog;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BookingOptionsFragment extends BookingFlowFragment
{
    static final String EXTRA_OPTIONS = "com.handy.handy.EXTRA_OPTIONS";
    static final String EXTRA_POST_OPTIONS = "com.handy.handy.EXTRA_POST_OPTIONS";
    static final String EXTRA_CHILD_DISPLAY_MAP = "com.handy.handy.EXTRA_CHILD_DISPLAY_MAP";
    static final String EXTRA_PAGE = "com.handy.handy.EXTRA_PAGE";
    static final String EXTRA_IS_POST = "com.handy.handy.EXTRA_IS_POST";
    static final String STATE_CHILD_DISPLAY_MAP = "STATE_CHILD_DISPLAY_MAP";
    static final String STATE_OPTION_INDEX_MAP = "STATE_OPTION_INDEX_MAP";

    protected ArrayList<BookingOption> options;
    protected ArrayList<BookingOption> postOptions;
    protected HashMap<String, Boolean> childDisplayMap;
    protected HashMap<String, Integer> optionIndexMap;
    protected HashMap<String, BookingOptionsView> optionsViewMap;
    private int page;
    protected boolean isPost;

    @Bind(R.id.options_layout)
    protected LinearLayout optionsLayout;
    @Bind(R.id.header_text)
    protected TextView headerText;
    @Bind(R.id.next_button)
    protected Button nextButton;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    public static BookingOptionsFragment newInstance(
            final ArrayList<BookingOption> options,
            final int page,
            final HashMap<String, Boolean> childDisplayMap,
            final ArrayList<BookingOption> postOptions,
            final boolean isPost
    )
    {
        final BookingOptionsFragment fragment = new BookingOptionsFragment();
        final Bundle args = new Bundle();

        args.putParcelableArrayList(EXTRA_OPTIONS, options);
        args.putParcelableArrayList(EXTRA_POST_OPTIONS, postOptions);
        args.putSerializable(EXTRA_CHILD_DISPLAY_MAP, childDisplayMap);
        args.putInt(EXTRA_PAGE, page);
        args.putBoolean(EXTRA_IS_POST, isPost);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null)
        {
            childDisplayMap = (HashMap) savedInstanceState.getSerializable(STATE_CHILD_DISPLAY_MAP);
            optionIndexMap = (HashMap) savedInstanceState.getSerializable(STATE_OPTION_INDEX_MAP);
        }
        else
        {
            optionIndexMap = new HashMap<>();
        }

        bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingServiceDetailsShownLog()));
        bus.post(new LogEvent.AddLogEvent(new BookingDetailsLog.BookingDetailsShownLog()));
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState
    )
    {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_booking_options, container, false);

        ButterKnife.bind(this, view);
        setupToolbar(mToolbar, getString(R.string.details));

        if (page != 0)
        {
            headerText.setVisibility(View.GONE);
        }
        else if (bookingManager.getCurrentRequest().getServiceId() == 3)
        {
            headerText.setText(getString(R.string.tell_us_place));
        }

        nextButton.setOnClickListener(nextClicked);

        options = getArguments().getParcelableArrayList(EXTRA_OPTIONS);
        page = getArguments().getInt(EXTRA_PAGE);
        childDisplayMap = (HashMap) getArguments().getSerializable(EXTRA_CHILD_DISPLAY_MAP);
        postOptions = getArguments().getParcelableArrayList(EXTRA_POST_OPTIONS);
        isPost = getArguments().getBoolean(EXTRA_IS_POST);
        displayOptions();

        return view;
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_CHILD_DISPLAY_MAP, childDisplayMap);
        outState.putSerializable(STATE_OPTION_INDEX_MAP, optionIndexMap);
    }

    @Override
    protected final void disableInputs()
    {
        super.disableInputs();
        nextButton.setClickable(false);
    }

    @Override
    protected final void enableInputs()
    {
        super.enableInputs();
        nextButton.setClickable(true);
    }

    protected void displayOptions()
    {
        optionsViewMap = new HashMap<>();
        optionsLayout.removeAllViews();

        final ArrayList<BookingOption> pageOptions = new ArrayList<>();

        if (childDisplayMap == null)
        {
            childDisplayMap = new HashMap<>();

            for (final BookingOption option : options)
            {
                if (option.getChildren() != null)
                {
                    for (final String[] s : option.getChildren())
                    {
                        childDisplayMap.put(s[0], false);
                    }
                }
            }
        }

        if (!isPost && postOptions == null)
        {
            postOptions = new ArrayList<>();

            for (final BookingOption option : options)
            {
                if (option.isPost())
                {
                    postOptions.add(option);
                }
            }
        }

        for (final BookingOption option : options)
        {
            if (isPost || (option.getPage() == page && !option.isPost()))
            {
                pageOptions.add(option);
            }
        }

        int pos = 0;
        for (final BookingOption option : pageOptions)
        {
            final BookingOptionsView optionsView;

            switch (option.getType())
            {
                case "quantity":
                case "option_picker":
                {
                    optionsView = new BookingOptionsSpinnerView(getActivity(), option,
                            new BookingOptionsView.OnUpdatedListener()
                            {
                                @Override
                                public void onUpdate(final BookingOptionsView view)
                                {
                                    handleOptionUpdate(view, option);
                                }

                                @Override
                                public void onShowChildren(
                                        final BookingOptionsView view,
                                        final String[] items
                                )
                                {
                                    handleChildViews(items, true);
                                }

                                @Override
                                public void onHideChildren(
                                        final BookingOptionsView view,
                                        final String[] items
                                )
                                {
                                    handleChildViews(items, false);
                                }
                            });
                    break;
                }
                case "option":
                {
                    optionsView = new BookingOptionsSelectView(getActivity(), option,
                            new BookingOptionsView.OnUpdatedListener()
                            {
                                @Override
                                public void onUpdate(final BookingOptionsView view)
                                {
                                    handleOptionUpdate(view, option);
                                }

                                @Override
                                public void onShowChildren(
                                        final BookingOptionsView view,
                                        final String[] items
                                )
                                {
                                    handleChildViews(items, true);
                                }

                                @Override
                                public void onHideChildren(
                                        final BookingOptionsView view,
                                        final String[] items
                                )
                                {
                                    handleChildViews(items, false);
                                }
                            });
                    break;

                }
                case "text":
                {
                    optionsView = new BookingOptionsTextView(getActivity(), option,
                            new BookingOptionsView.OnUpdatedListener()
                            {
                                @Override
                                public void onUpdate(final BookingOptionsView view)
                                {
                                    handleOptionUpdate(view, option);
                                }

                                @Override
                                public void onShowChildren(
                                        final BookingOptionsView view,
                                        final String[] items
                                )
                                {
                                    handleChildViews(items, true);
                                }

                                @Override
                                public void onHideChildren(
                                        final BookingOptionsView view,
                                        final String[] items
                                )
                                {
                                    handleChildViews(items, false);
                                }
                            });
                    break;

                }
                default:
                    continue;
            }

            // set default value for index based views
            if (optionsView instanceof BookingOptionsIndexView)
            {
                final Integer index = optionIndexMap.get(option.getUniq());
                final BookingOptionsIndexView indexView = (BookingOptionsIndexView) optionsView;
                if (index != null)
                {
                    indexView.setCurrentIndex(index);
                }
                else
                {
                    indexView.setCurrentIndex(indexView.getCurrentIndex());
                }
            }
            else if (optionsView instanceof BookingOptionsTextView)
            {
                final HashMap<String, String> requestOptions
                        = bookingManager.getCurrentRequest().getOptions();

                ((BookingOptionsTextView) optionsView).setValue(requestOptions.get(option.getUniq()));
            }

            if (pos >= pageOptions.size() - 1)
            {
                optionsView.hideSeparator();
            }

            if (pageOptions.size() == 1 && option.getType().equals("text"))
            {
                setToolbarTitle(getString(R.string.comments));
                optionsLayout.setBackgroundColor(0);
                ((BookingOptionsTextView) optionsView).enableSingleMode();

                bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingCommentsShownLog()));
            }
            else if (pageOptions.size() == 1 && option.getType().equals("option")
                    && option.getTitle().contains("professional"))
            {
                headerText.setText(option.getTitle());
                headerText.setVisibility(View.VISIBLE);
                setToolbarTitle(getString(R.string.request_pro));
                ((BookingOptionsIndexView) optionsView).hideTitle();

                bus.post(new LogEvent.AddLogEvent(new BookingRequestProLog.BookingRequestProShownLog()));
            }

            optionsLayout.addView(optionsView, pos++);
            optionsViewMap.put(option.getUniq(), optionsView);

            final Boolean diplayOption = childDisplayMap.get(option.getUniq());
            if (diplayOption != null && !diplayOption)
            {
                optionsView.setVisibility(View.GONE);
            }
        }
    }

    protected void handleChildViews(final String[] items, final boolean show)
    {
        for (final String item : items)
        {
            final View optionsView = optionsViewMap.get(item);
            if (optionsView != null)
            {
                if (show)
                {
                    optionsView.setVisibility(View.VISIBLE);
                }
                else
                {
                    optionsView.setVisibility(View.GONE);
                }
                childDisplayMap.put(item, show);
            }
        }
    }

    protected void handleOptionUpdate(
            final BookingOptionsView view,
            final BookingOption option
    )
    {
        final HashMap<String, String> requestOptions
                = bookingManager.getCurrentRequest().getOptions();

        if (view instanceof BookingOptionsIndexView)
        {
            int currentIndex = ((BookingOptionsIndexView) view).getCurrentIndex();
            //Both the requestion options and option index map uses the current Index
            requestOptions.put(option.getUniq(), Integer.toString(currentIndex));
            optionIndexMap.put(option.getUniq(), currentIndex);
        }
        else
        {
            requestOptions.put(option.getUniq(), view.getCurrentValue());
        }

        bookingManager.getCurrentRequest().setOptions(requestOptions);
    }

    protected final View.OnClickListener nextClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(final View v)
        {
            bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingServiceDetailsSubmittedLog()));

            final ArrayList<BookingOption> nextOptions = new ArrayList<>();
            for (final BookingOption option : options)
            {
                if (isPost || (option.getPage() > page && !option.isPost()))
                {
                    nextOptions.add(option);
                }
                if (!Strings.isNullOrEmpty(option.getType()) && option.getType().equals("text"))
                {
                    bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingCommentsSubmittedLog()));
                }
            }
            if (nextOptions.size() < 1 || nextOptions.get(nextOptions.size() - 1).getPage() <= page)
            {
                if (isPost)
                {
                    continueBookingFlow();
                    return;
                }

                final Intent intent = new Intent(getActivity(), BookingDateActivity.class);
                intent.putParcelableArrayListExtra(BundleKeys.POST_OPTIONS, new ArrayList<>(postOptions));
                startActivity(intent);
            }
            else
            {
                final Intent intent = new Intent(getActivity(), BookingOptionsActivity.class);
                intent.putParcelableArrayListExtra(BookingOptionsActivity.EXTRA_OPTIONS,
                        new ArrayList<>(nextOptions));

                intent.putParcelableArrayListExtra(BookingOptionsActivity.EXTRA_POST_OPTIONS,
                        new ArrayList<>(postOptions));

                if (isPost)
                {
                    intent.putExtra(BookingOptionsActivity.EXTRA_IS_POST, true);
                }

                intent.putExtra(BookingOptionsActivity.EXTRA_CHILD_DISPLAY_MAP, childDisplayMap);
                intent.putExtra(BookingOptionsActivity.EXTRA_PAGE, nextOptions.get(0).getPage());
                startActivity(intent);
            }
        }
    };
}
