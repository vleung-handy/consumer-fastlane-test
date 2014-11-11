package com.handybook.handybook;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class BookingOptionsFragment extends InjectedFragment {
    static final String EXTRA_OPTIONS = "com.handy.handy.EXTRA_OPTIONS";
    static final String EXTRA_POST_OPTIONS = "com.handy.handy.EXTRA_POST_OPTIONS";
    static final String EXTRA_CHILD_DISPLAY_MAP = "com.handy.handy.EXTRA_CHILD_DISPLAY_MAP";
    static final String EXTRA_PAGE = "com.handy.handy.EXTRA_PAGE";
    static final String STATE_CHILD_DISPLAY_MAP = "STATE_CHILD_DISPLAY_MAP";
    static final String STATE_OPTION_INDEX_MAP = "STATE_OPTION_INDEX_MAP";

    private ArrayList<BookingOption> options;
    private ArrayList<BookingOption> postOptions;
    private HashMap<String, Boolean> childDisplayMap;
    private HashMap<String, Integer> optionIndexMap;
    private HashMap<String, BookingOptionsView> optionsViewMap;
    private int page;

    @Inject BookingRequestManager requestManager;

    @InjectView(R.id.options_layout) LinearLayout optionsLayout;
    @InjectView(R.id.nav_text) TextView navText;
    @InjectView(R.id.header_text) TextView headerText;
    @InjectView(R.id.next_button) Button nextButton;

    static BookingOptionsFragment newInstance(final ArrayList<BookingOption> options,
                                              final int page,
                                              final HashMap<String, Boolean> childDisplayMap,
                                              final ArrayList<BookingOption> postOptions) {
        final BookingOptionsFragment fragment = new BookingOptionsFragment();
        final Bundle args = new Bundle();

        args.putParcelableArrayList(EXTRA_OPTIONS, options);
        args.putParcelableArrayList(EXTRA_POST_OPTIONS, postOptions);
        args.putSerializable(EXTRA_CHILD_DISPLAY_MAP, childDisplayMap);
        args.putInt(EXTRA_PAGE, page);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        options = getArguments().getParcelableArrayList(EXTRA_OPTIONS);
        page = getArguments().getInt(EXTRA_PAGE);
        childDisplayMap = (HashMap) getArguments().getSerializable(EXTRA_CHILD_DISPLAY_MAP);
        postOptions = getArguments().getParcelableArrayList(EXTRA_POST_OPTIONS);

        //TODO if all options set invisible then prev view should have skipped this page

        if (savedInstanceState != null) {
            childDisplayMap = (HashMap)savedInstanceState.getSerializable(STATE_CHILD_DISPLAY_MAP);
            optionIndexMap = (HashMap)savedInstanceState.getSerializable(STATE_OPTION_INDEX_MAP);
        }
        else optionIndexMap = new HashMap<>();
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_booking_options, container, false);
        ButterKnife.inject(this, view);

        if (page != 0) {
            headerText.setVisibility(View.GONE);
        }
        else if (requestManager.getCurrentRequest().getServiceId() == 3)
            headerText.setText(getString(R.string.tell_us_place));

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final ArrayList<BookingOption> nextOptions = new ArrayList<>();
                for (final BookingOption option : options) {
                    if (option.getPage() > page && !option.isPost()) nextOptions.add(option);
                }

                if (nextOptions.size() < 1 || nextOptions.get(nextOptions.size() - 1).getPage() <= page) {
                    final Intent intent = new Intent(getActivity(), BookingDateActivity.class);
                    intent.putParcelableArrayListExtra(BookingDateActivity.EXTRA_POST_OPTIONS,
                            new ArrayList<>(postOptions));
                    startActivity(intent);
                }
                else {
                    final Intent intent = new Intent(getActivity(), BookingOptionsActivity.class);
                    intent.putParcelableArrayListExtra(BookingOptionsActivity.EXTRA_OPTIONS,
                            new ArrayList<>(nextOptions));

                    intent.putParcelableArrayListExtra(BookingOptionsActivity.EXTRA_POST_OPTIONS,
                            new ArrayList<>(postOptions));

                    intent.putExtra(BookingOptionsActivity.EXTRA_CHILD_DISPLAY_MAP, childDisplayMap);
                    intent.putExtra(BookingOptionsActivity.EXTRA_PAGE, nextOptions.get(0).getPage());
                    startActivity(intent);
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        displayOptions();
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_CHILD_DISPLAY_MAP, childDisplayMap);
        outState.putSerializable(STATE_OPTION_INDEX_MAP, optionIndexMap);
    }

    private void displayOptions() {
        optionsViewMap = new HashMap<>();
        optionsLayout.removeAllViews();

        final ArrayList<BookingOption> pageOptions = new ArrayList<>();

        if (childDisplayMap == null) {
            childDisplayMap = new HashMap<>();

            for (final BookingOption option : options) {
                if (option.getChildren() != null) {
                    for (final String[] s : option.getChildren()) {
                        childDisplayMap.put(s[0], false);
                    }
                }
            }
        }

        if (postOptions == null) {
            postOptions = new ArrayList<>();

            for (final BookingOption option : options) {
                if (option.isPost()) {
                    postOptions.add(option);
                }
            }
        }

        for (final BookingOption option : options) {
            if (option.getPage() == page && !option.isPost()) pageOptions.add(option);
        }

        int pos = 0;
        for (final BookingOption option : pageOptions) {
            final BookingOptionsView optionsView;

            switch (option.getType()) {
                case "quantity":
                case "option_picker": {
                    optionsView = new BookingOptionsSpinnerView(getActivity(), option,
                        new BookingOptionsView.OnUpdatedListener() {
                            @Override
                            public void onUpdate(final BookingOptionsView view) {
                                handleOptionUpdate(view, option);
                            }

                            @Override
                            public void onShowChildren(final BookingOptionsView view,
                                                           final String[] items) {
                                handleChildViews(items, true);
                            }

                            @Override
                            public void onHideChildren(final BookingOptionsView view,
                                                           final String[] items) {
                                handleChildViews(items, false);
                            }
                        });
                    break;
                }
                case "option": {
                    optionsView = new BookingOptionsSelectView(getActivity(), option,
                        new BookingOptionsView.OnUpdatedListener() {
                            @Override
                            public void onUpdate(final BookingOptionsView view) {
                                handleOptionUpdate(view, option);
                            }

                            @Override
                            public void onShowChildren(final BookingOptionsView view,
                                                           final String[] items) {
                                handleChildViews(items, true);
                            }

                            @Override
                            public void onHideChildren(final BookingOptionsView view,
                                                           final String[] items) {
                                handleChildViews(items, false);
                            }
                        });
                    break;

                }
                case "text": {
                    optionsView = new BookingOptionsTextView(getActivity(), option,
                        new BookingOptionsView.OnUpdatedListener() {
                            @Override
                            public void onUpdate(final BookingOptionsView view) {
                                handleOptionUpdate(view, option);
                            }

                            @Override
                            public void onShowChildren(final BookingOptionsView view,
                                                       final String[] items) {
                                handleChildViews(items, true);
                            }

                            @Override
                            public void onHideChildren(final BookingOptionsView view,
                                                       final String[] items) {
                                handleChildViews(items, false);
                            }
                        });
                    break;

                }
                default:
                    continue;
            }

            // set default value for index based views
            if (optionsView instanceof BookingOptionsIndexView) {
                final Integer index = optionIndexMap.get(option.getUniq());
                final BookingOptionsIndexView indexView = (BookingOptionsIndexView) optionsView;
                if (index != null) indexView.setCurrentIndex(index);
                else indexView.setCurrentIndex(indexView.getCurrentIndex());
            }
            else if (optionsView instanceof BookingOptionsTextView) {
                final HashMap<String, String> requestOptions
                        = requestManager.getCurrentRequest().getOptions();

                ((BookingOptionsTextView)optionsView).setValue(requestOptions.get(option.getUniq()));
            }

            if (pos >= pageOptions.size() - 1) optionsView.hideSeparator();
            if (pageOptions.size() == 1 && option.getType().equals("text")) {
                navText.setText(getString(R.string.comments));
                optionsLayout.setBackgroundColor(0);
                ((BookingOptionsTextView)optionsView).enableSingleMode();
            }

            optionsLayout.addView(optionsView, pos++);
            optionsViewMap.put(option.getUniq(), optionsView);

            final Boolean diplayOption = childDisplayMap.get(option.getUniq());
            if (diplayOption != null && !diplayOption) optionsView.setVisibility(View.GONE);
        }
    }

    private void handleChildViews(final String[] items, final boolean show) {
        for (final String item : items) {
            final View optionsView = optionsViewMap.get(item);
            if (optionsView != null) {
                if (show) optionsView.setVisibility(View.VISIBLE);
                else optionsView.setVisibility(View.GONE);
                childDisplayMap.put(item, show);
            }
        }
    }

    private void handleOptionUpdate(final BookingOptionsView view,
                                    final BookingOption option) {
        final HashMap<String, String> requestOptions
                = requestManager.getCurrentRequest().getOptions();

        requestOptions.put(option.getUniq(), view.getCurrentValue());
        requestManager.getCurrentRequest().setOptions(requestOptions);

        if (view instanceof BookingOptionsIndexView) optionIndexMap.put(option.getUniq(),
                ((BookingOptionsIndexView)view).getCurrentIndex());
    }
}
