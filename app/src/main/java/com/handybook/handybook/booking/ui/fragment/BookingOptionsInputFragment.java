package com.handybook.handybook.booking.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
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
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.booking.BookingDetailsLog;
import com.handybook.handybook.logger.handylogger.model.booking.BookingFunnelLog;
import com.handybook.handybook.logger.handylogger.model.booking.BookingRequestProLog;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * NOTE: almost entirely copied from {@link BookingOptionsFragment}
 * for purpose of consolidated booking experiment
 *
 * NOT doing a complete refactor at this point as it is too risky
 * note that {@link BookingOptionsFragment} was not saving the state of the options selected,
 * and since this is almost a full copy, this will not either.
 *
 * differences: no toolbar, next button, or next listener. also modified instructions text.
 *
 */
public class BookingOptionsInputFragment extends BookingFlowFragment {

    public static final String TAG = BookingOptionsInputFragment.class.getName();
    public static final String EXTRA_OPTIONS = "com.handy.handy.EXTRA_OPTIONS";
    public static final String EXTRA_POST_OPTIONS = "com.handy.handy.EXTRA_POST_OPTIONS";
    public static final String EXTRA_CHILD_DISPLAY_MAP = "com.handy.handy.EXTRA_CHILD_DISPLAY_MAP";
    public static final String EXTRA_PAGE = "com.handy.handy.EXTRA_PAGE";
    public static final String EXTRA_IS_POST = "com.handy.handy.EXTRA_IS_POST";

    static final String STATE_CHILD_DISPLAY_MAP = "STATE_CHILD_DISPLAY_MAP";
    static final String STATE_OPTION_INDEX_MAP = "STATE_OPTION_INDEX_MAP";

    protected ArrayList<BookingOption> options;
    protected ArrayList<BookingOption> postOptions;
    protected HashMap<String, Boolean> childDisplayMap;
    protected HashMap<String, Integer> optionIndexMap;
    protected HashMap<String, BookingOptionsView> optionsViewMap;
    private int page;
    protected boolean isPost;

    @Bind(R.id.booking_options_input)
    protected LinearLayout optionsLayout;
    @Bind(R.id.booking_options_input_instructions)
    protected TextView mInstructionsText;

    public static BookingOptionsInputFragment newInstance(
            final ArrayList<BookingOption> options,
            final int page,
            final HashMap<String, Boolean> childDisplayMap,
            final ArrayList<BookingOption> postOptions,
            final boolean isPost
    ) {
        final BookingOptionsInputFragment fragment = new BookingOptionsInputFragment();
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
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            childDisplayMap = (HashMap) savedInstanceState.getSerializable(STATE_CHILD_DISPLAY_MAP);
            optionIndexMap = (HashMap) savedInstanceState.getSerializable(STATE_OPTION_INDEX_MAP);
        }
        else {
            optionIndexMap = new HashMap<>();
        }

        bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingServiceDetailsShownLog()));
        bus.post(new LogEvent.AddLogEvent(new BookingDetailsLog.BookingDetailsShownLog()));
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState
    ) {
        final View view = getActivity().getLayoutInflater()
                                       .inflate(
                                               R.layout.fragment_booking_options_input,
                                               container,
                                               false
                                       );

        ButterKnife.bind(this, view);

        if (page != 0) {
            mInstructionsText.setVisibility(View.GONE);
        }
        else if (bookingManager.getCurrentRequest().getServiceId() == 3) {
            mInstructionsText.setText(getString(R.string.fragment_booking_options_input_instructions));
        }

        options = getArguments().getParcelableArrayList(EXTRA_OPTIONS);
        page = getArguments().getInt(EXTRA_PAGE);
        childDisplayMap = (HashMap) getArguments().getSerializable(EXTRA_CHILD_DISPLAY_MAP);
        postOptions = getArguments().getParcelableArrayList(EXTRA_POST_OPTIONS);
        isPost = getArguments().getBoolean(EXTRA_IS_POST);
        displayOptions();

        return view;
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_CHILD_DISPLAY_MAP, childDisplayMap);
        outState.putSerializable(STATE_OPTION_INDEX_MAP, optionIndexMap);
    }

    protected void displayOptions() {
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

        if (!isPost && postOptions == null) {
            postOptions = new ArrayList<>();

            for (final BookingOption option : options) {
                if (option.isPost()) {
                    postOptions.add(option);
                }
            }
        }

        for (final BookingOption option : options) {
            if (isPost || (option.getPage() == page && !option.isPost())) {
                pageOptions.add(option);
            }
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
                                                                        handleOptionUpdate(
                                                                                view,
                                                                                option
                                                                        );
                                                                    }

                                                                    @Override
                                                                    public void onShowChildren(
                                                                            final BookingOptionsView view,
                                                                            final String[] items
                                                                    ) {
                                                                        handleChildViews(
                                                                                items,
                                                                                true
                                                                        );
                                                                    }

                                                                    @Override
                                                                    public void onHideChildren(
                                                                            final BookingOptionsView view,
                                                                            final String[] items
                                                                    ) {
                                                                        handleChildViews(
                                                                                items,
                                                                                false
                                                                        );
                                                                    }
                                                                }
                    );
                    break;
                }
                case "option": {
                    optionsView = new BookingOptionsSelectView(getActivity(), option,
                                                               new BookingOptionsView.OnUpdatedListener() {
                                                                   @Override
                                                                   public void onUpdate(final BookingOptionsView view) {
                                                                       handleOptionUpdate(
                                                                               view,
                                                                               option
                                                                       );
                                                                   }

                                                                   @Override
                                                                   public void onShowChildren(
                                                                           final BookingOptionsView view,
                                                                           final String[] items
                                                                   ) {
                                                                       handleChildViews(
                                                                               items,
                                                                               true
                                                                       );
                                                                   }

                                                                   @Override
                                                                   public void onHideChildren(
                                                                           final BookingOptionsView view,
                                                                           final String[] items
                                                                   ) {
                                                                       handleChildViews(
                                                                               items,
                                                                               false
                                                                       );
                                                                   }
                                                               }
                    );
                    break;

                }
                case "text": {
                    optionsView = new BookingOptionsTextView(getActivity(), option,
                                                             new BookingOptionsView.OnUpdatedListener() {
                                                                 @Override
                                                                 public void onUpdate(final BookingOptionsView view) {
                                                                     handleOptionUpdate(
                                                                             view,
                                                                             option
                                                                     );
                                                                 }

                                                                 @Override
                                                                 public void onShowChildren(
                                                                         final BookingOptionsView view,
                                                                         final String[] items
                                                                 ) {
                                                                     handleChildViews(items, true);
                                                                 }

                                                                 @Override
                                                                 public void onHideChildren(
                                                                         final BookingOptionsView view,
                                                                         final String[] items
                                                                 ) {
                                                                     handleChildViews(items, false);
                                                                 }
                                                             }
                    );
                    break;

                }
                default:
                    continue;
            }

            final HashMap<String, String> currentBookingRequestOptions
                    = bookingManager.getCurrentRequest().getOptions();

            // set default value for index based views
            if (optionsView instanceof BookingOptionsIndexView) {
                Integer index = null;
                if (currentBookingRequestOptions == null) {
                    index = optionIndexMap.get(option.getUniq());
                }
                else {
                    try {
                        index = Integer.parseInt(currentBookingRequestOptions.get(option.getUniq()));
                    }
                    catch (Exception e) {
                        Crashlytics.logException(e);
                    }
                }
                final BookingOptionsIndexView indexView = (BookingOptionsIndexView) optionsView;
                if (index != null) {
                    indexView.setCurrentIndex(index);
                }
                else {
                    indexView.setCurrentIndex(indexView.getCurrentIndex());
                }
            }
            else if (optionsView instanceof BookingOptionsTextView) {
                ((BookingOptionsTextView) optionsView).setValue(currentBookingRequestOptions.get(option.getUniq()));
            }

            if (pos >= pageOptions.size() - 1) {
                optionsView.hideSeparator();
            }

            if (pageOptions.size() == 1 && option.getType().equals("text")) {
                setToolbarTitle(getString(R.string.comments));
                optionsLayout.setBackgroundColor(0);
                ((BookingOptionsTextView) optionsView).enableSingleMode();

                bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingCommentsShownLog()));
            }
            else if (pageOptions.size() == 1 && option.getType().equals("option")
                     && option.getTitle().contains("professional")) {
                mInstructionsText.setText(option.getTitle());
                mInstructionsText.setVisibility(View.VISIBLE);
                setToolbarTitle(getString(R.string.request_pro));
                ((BookingOptionsIndexView) optionsView).hideTitle();

                bus.post(new LogEvent.AddLogEvent(new BookingRequestProLog.BookingRequestProShownLog()));
            }

            optionsLayout.addView(optionsView, pos++);
            optionsViewMap.put(option.getUniq(), optionsView);

            final Boolean diplayOption = childDisplayMap.get(option.getUniq());
            if (diplayOption != null && !diplayOption) {
                optionsView.setVisibility(View.GONE);
            }
        }
    }

    protected void handleChildViews(final String[] items, final boolean show) {
        for (final String item : items) {
            final View optionsView = optionsViewMap.get(item);
            if (optionsView != null) {
                if (show) {
                    optionsView.setVisibility(View.VISIBLE);
                }
                else {
                    optionsView.setVisibility(View.GONE);
                }
                childDisplayMap.put(item, show);
            }
        }
    }

    protected void handleOptionUpdate(
            final BookingOptionsView view,
            final BookingOption option
    ) {
        final HashMap<String, String> requestOptions
                = bookingManager.getCurrentRequest().getOptions();

        if (view instanceof BookingOptionsIndexView) {
            int currentIndex = ((BookingOptionsIndexView) view).getCurrentIndex();
            //Both the requestion options and option index map uses the current Index
            requestOptions.put(option.getUniq(), Integer.toString(currentIndex));
            optionIndexMap.put(option.getUniq(), currentIndex);
        }
        else {
            requestOptions.put(option.getUniq(), view.getCurrentValue());
        }

        bookingManager.getCurrentRequest().setOptions(requestOptions);
    }

    protected final View.OnClickListener nextClicked = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingServiceDetailsSubmittedLog()));

            final ArrayList<BookingOption> nextOptions = new ArrayList<>();
            for (final BookingOption option : options) {
                if (isPost || (option.getPage() > page && !option.isPost())) {
                    nextOptions.add(option);
                }
                if (!Strings.isNullOrEmpty(option.getType()) && option.getType().equals("text")) {
                    bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingCommentsSubmittedLog()));
                }
            }
            if (nextOptions.size() < 1 ||
                nextOptions.get(nextOptions.size() - 1).getPage() <= page) {
                if (isPost) {
                    continueBookingFlow();
                    return;
                }

                final Intent intent = new Intent(getActivity(), BookingDateActivity.class);
                intent.putParcelableArrayListExtra(
                        BundleKeys.POST_OPTIONS,
                        new ArrayList<>(postOptions)
                );
                startActivity(intent);
            }
            else {
                final Intent intent = new Intent(getActivity(), BookingOptionsActivity.class);
                intent.putParcelableArrayListExtra(
                        BookingOptionsActivity.EXTRA_OPTIONS,
                        new ArrayList<>(nextOptions)
                );

                intent.putParcelableArrayListExtra(
                        BookingOptionsActivity.EXTRA_POST_OPTIONS,
                        new ArrayList<>(postOptions)
                );

                if (isPost) {
                    intent.putExtra(BookingOptionsActivity.EXTRA_IS_POST, true);
                }

                intent.putExtra(BookingOptionsActivity.EXTRA_CHILD_DISPLAY_MAP, childDisplayMap);
                intent.putExtra(BookingOptionsActivity.EXTRA_PAGE, nextOptions.get(0).getPage());
                startActivity(intent);
            }
        }
    };
}
