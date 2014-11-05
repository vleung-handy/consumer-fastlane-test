package com.handybook.handybook;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import antistatic.spinnerwheel.WheelHorizontalView;
import butterknife.ButterKnife;
import butterknife.InjectView;

public final class BookingOptionsFragment extends InjectedFragment {
    static final String EXTRA_OPTIONS = "com.handy.handy.EXTRA_OPTIONS";
    static final String EXTRA_CHILD_DISPLAY_MAP = "com.handy.handy.EXTRA_CHILD_DISPLAY_MAP";
    static final String EXTRA_PAGE = "com.handy.handy.EXTRA_PAGE";

    private ArrayList<BookingOption> options;
    private HashMap<String, Boolean> childDisplayMap;
    private int page;

    @Inject BookingRequestManager requestManager;

    @InjectView(R.id.options_layout) LinearLayout optionsLayout;
    @InjectView(R.id.header_text) TextView headerText;
    @InjectView(R.id.next_button) Button nextButton;

    static BookingOptionsFragment newInstance(final ArrayList<BookingOption> options,
                                              final int page) {
        final BookingOptionsFragment fragment = new BookingOptionsFragment();
        final Bundle args = new Bundle();

        args.putParcelableArrayList(EXTRA_OPTIONS, options);
        args.putInt(EXTRA_PAGE, page);
        fragment.setArguments(args);

        return fragment;
    }

    static BookingOptionsFragment newInstance(final ArrayList<BookingOption> options,
                                              final int page,
                                              HashMap<String, Boolean> childDisplayMap) {
        final BookingOptionsFragment fragment = new BookingOptionsFragment();
        final Bundle args = new Bundle();

        args.putParcelableArrayList(EXTRA_OPTIONS, options);
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

        displayOptions();

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (options.get(options.size() - 1).getPage() <= page) {
                    Toast.makeText(getActivity(), "SHOW DATE", Toast.LENGTH_SHORT).show();
                }
                else {
                    final ArrayList<BookingOption> nextOptions = new ArrayList<>();

                    for (final BookingOption option : options) {
                        if (option.getPage() > page) nextOptions.add(option);
                    }

                    final Intent intent = new Intent(getActivity(), BookingOptionsActivity.class);
                    intent.putParcelableArrayListExtra(BookingOptionsActivity.EXTRA_OPTIONS,
                            new ArrayList<>(nextOptions));
                    intent.putExtra(BookingOptionsActivity.EXTRA_CHILD_DISPLAY_MAP, childDisplayMap);
                    intent.putExtra(BookingOptionsActivity.EXTRA_PAGE, ++page);
                    startActivity(intent);
                }
            }
        });

        return view;
    }

    private void displayOptions() {
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

        int pos = 1;
        for (final BookingOption option : options) {
            if (option.getPage() != page) continue;

            final WheelHorizontalView optionsSpinner = new WheelHorizontalView(getActivity());
            optionsSpinner.setItemsDimmedAlpha(100);
            optionsSpinner.setItemsPadding(0);
            optionsSpinner.setViewAdapter(new OptionsAdapter<>(getActivity(),
                    new String[] {"No", "Yes", "Webster Ross"},
                    R.layout.view_spinner_option, R.id.text));

            final TextView temp = new TextView(getActivity());
            temp.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    200));
            temp.setText(option.getTitle());
            temp.setBackgroundColor(getResources().getColor(R.color.white));

            final Boolean diplayOption = childDisplayMap.get(option.getUniq());
            if (diplayOption != null && !diplayOption) temp.setVisibility(View.GONE);

            optionsLayout.addView(temp, pos++);
            optionsLayout.addView(optionsSpinner, pos++);

            //TODO if all options set invisible then prev view should have skipped this page
        }
    }
}
