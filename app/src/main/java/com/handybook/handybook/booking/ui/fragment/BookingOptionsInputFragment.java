package com.handybook.handybook.booking.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.BookingOption;
import com.handybook.handybook.booking.ui.view.BookingOptionsIndexView;
import com.handybook.handybook.booking.ui.view.BookingOptionsSelectView;
import com.handybook.handybook.booking.ui.view.BookingOptionsSpinnerView;
import com.handybook.handybook.booking.ui.view.BookingOptionsTextView;
import com.handybook.handybook.booking.ui.view.BookingOptionsView;
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
 * NOT doing a complete refactor or consolidation at this point as it is too risky
 *
 * differences: no toolbar, next button, or next listener. also modified instructions text, quick-fix for saving state
 *
 */
public class BookingOptionsInputFragment extends BookingFlowFragment {

    public static final String TAG = BookingOptionsInputFragment.class.getName();
    public static final String EXTRA_OPTIONS = "com.handy.handy.EXTRA_OPTIONS";
    public static final String EXTRA_POST_OPTIONS = "com.handy.handy.EXTRA_POST_OPTIONS";
    public static final String EXTRA_CHILD_DISPLAY_MAP = "com.handy.handy.EXTRA_CHILD_DISPLAY_MAP";
    public static final String EXTRA_PAGE = "com.handy.handy.EXTRA_PAGE";
    public static final String EXTRA_IS_POST = "com.handy.handy.EXTRA_IS_POST";
    private static final int SERVICE_ID_CLEANING = 3;

    static final String STATE_CHILD_DISPLAY_MAP = "STATE_CHILD_DISPLAY_MAP";
    static final String STATE_OPTION_INDEX_MAP = "STATE_OPTION_INDEX_MAP";

    protected ArrayList<BookingOption> options;
    protected ArrayList<BookingOption> postOptions;
    protected HashMap<String, Boolean> childDisplayMap;
    protected HashMap<String, Integer> optionIndexMap;
    protected HashMap<String, BookingOptionsView> optionsViewMap;
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
        final View view = getActivity()
                .getLayoutInflater()
                .inflate(
                        R.layout.fragment_booking_options_input,
                        container,
                        false
                );

        ButterKnife.bind(this, view);

        if (bookingManager.getCurrentRequest().getServiceId() == SERVICE_ID_CLEANING) {
            mInstructionsText.setText(getString(R.string.fragment_booking_options_input_instructions_cleaning_service));
        }
        else {
            mInstructionsText.setText(getString(R.string.fragment_booking_options_input_instructions_noncleaning_service));
        }

        options = getArguments().getParcelableArrayList(EXTRA_OPTIONS);
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

        final ArrayList<BookingOption> displayOptions = new ArrayList<>();

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
            if (isPost || !option.isPost()) {
                displayOptions.add(option);
            }
        }

        int pos = 0;
        int previousOrFirstPage = 0; //used to determine if transitioning into new page
        for (final BookingOption option : displayOptions) {
            final BookingOptionsView optionsView;

            switch (option.getType()) {
                case "quantity":
                case "option_picker": {
                    optionsView = new BookingOptionsSpinnerView(
                            getActivity(), option,
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
                    optionsView = new BookingOptionsSelectView(
                            getActivity(), option,
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
                    optionsView = new BookingOptionsTextView(
                            getActivity(), option,
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
                        index
                                = Integer.parseInt(currentBookingRequestOptions.get(option.getUniq()));
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
                ((BookingOptionsTextView) optionsView).setValue(currentBookingRequestOptions.get(
                        option.getUniq()));
            }

            if (pos >= displayOptions.size() - 1) {
                optionsView.hideSeparator();
            }

            if (displayOptions.size() == 1 && option.getType().equals("text")) {
                optionsLayout.setBackgroundColor(0);
                ((BookingOptionsTextView) optionsView).enableSingleMode();
                bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingCommentsShownLog()));
            }
            else if (displayOptions.size() == 1 && option.getType().equals("option")
                     && option.getTitle().contains("professional")) {
                mInstructionsText.setText(option.getTitle());
                mInstructionsText.setVisibility(View.VISIBLE);
                ((BookingOptionsIndexView) optionsView).hideTitle();

                bus.post(new LogEvent.AddLogEvent(new BookingRequestProLog.BookingRequestProShownLog()));
            }

            //add spacing if new page detected
            if(option.getPage() > previousOrFirstPage)
            {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                int margin = getResources().getDimensionPixelSize(R.dimen.default_margin);
                layoutParams.setMargins(0, margin, 0, 0);
                optionsView.setLayoutParams(layoutParams);
            }
            optionsLayout.addView(optionsView, pos++);
            optionsViewMap.put(option.getUniq(), optionsView);

            final Boolean diplayOption = childDisplayMap.get(option.getUniq());
            if (diplayOption != null && !diplayOption) {
                optionsView.setVisibility(View.GONE);
            }
            previousOrFirstPage = option.getPage();
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
            BookingOptionsIndexView indexView = (BookingOptionsIndexView) view;
            int currentIndex = indexView.getCurrentIndex();
            String requestOptionValue;

            //If booking option is type quantity then return the value, otherwise return index
            if (BookingOption.TYPE_QUANTITY.equals(option.getType())) {
                requestOptionValue = indexView.getCurrentValue();
            }
            else {
                requestOptionValue = Integer.toString(currentIndex);
            }

            //Both the requestion options and option index map uses the current Index
            requestOptions.put(option.getUniq(), requestOptionValue);
            optionIndexMap.put(option.getUniq(), currentIndex);
        }
        else {
            requestOptions.put(option.getUniq(), view.getCurrentValue());
        }

        bookingManager.getCurrentRequest().setOptions(requestOptions);
    }
}
