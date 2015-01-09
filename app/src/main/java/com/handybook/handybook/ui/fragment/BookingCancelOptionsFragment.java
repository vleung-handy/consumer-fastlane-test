package com.handybook.handybook.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.BookingOption;
import com.handybook.handybook.core.User;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.ui.activity.BookingCancelOptionsActivity;
import com.handybook.handybook.ui.widget.BookingOptionsSelectView;
import com.handybook.handybook.ui.widget.BookingOptionsView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class BookingCancelOptionsFragment extends BookingFlowFragment {
    public static final String EXTRA_OPTIONS = "com.handy.handy.EXTRA_OPTIONS";
    public static final String EXTRA_NOTICE = "com.handy.handy.EXTRA_NOTICE";
    public static final String EXTRA_BOOKING = "com.handy.handy.EXTRA_BOOKING";
    private static final String STATE_OPTION_INDEX = "OPTION_INDEX";

    private int optionIndex = -1;
    private String notice;
    private ArrayList<String> optionsList;
    private Booking booking;

    @InjectView(R.id.options_layout) FrameLayout optionsLayout;
    @InjectView(R.id.cancel_button) Button cancelButton;
    @InjectView(R.id.notice_text) TextView noticeText;

    public static BookingCancelOptionsFragment newInstance(final String notice,
                                                           final ArrayList<String> options,
                                                           final Booking booking) {
        final BookingCancelOptionsFragment fragment = new BookingCancelOptionsFragment();
        final Bundle args = new Bundle();

        args.putString(EXTRA_NOTICE, notice);
        args.putStringArrayList(EXTRA_OPTIONS, options);
        args.putParcelable(EXTRA_BOOKING, booking);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notice = getArguments().getString(EXTRA_NOTICE);
        optionsList = getArguments().getStringArrayList(EXTRA_OPTIONS);
        booking = getArguments().getParcelable(EXTRA_BOOKING);
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_booking_cancel_options, container, false);

        ButterKnife.inject(this, view);

        final BookingOption options = new BookingOption();
        options.setType("option");
        options.setOptions(optionsList.toArray(new String[optionsList.size()]));
        options.setDefaultValue(Integer.toString(optionIndex));

        final BookingOptionsSelectView optionsView = new BookingOptionsSelectView(getActivity(),
                options, optionUpdated);

        optionsView.hideTitle();
        optionsLayout.addView(optionsView, 0);

        if (notice != null && notice.length() > 0) {
            noticeText.setText(notice);
            noticeText.setVisibility(View.VISIBLE);
        }

        cancelButton.setOnClickListener(cancelClicked);

        return view;
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_OPTION_INDEX, optionIndex);
    }

    private final BookingOptionsView.OnUpdatedListener optionUpdated
            = new BookingOptionsView.OnUpdatedListener() {
        @Override
        public void onUpdate(final BookingOptionsView view) {
            optionIndex = ((BookingOptionsSelectView) view).getCurrentIndex();
        }

        @Override
        public void onShowChildren(final BookingOptionsView view,
                                   final String[] items) {
        }

        @Override
        public void onHideChildren(final BookingOptionsView view,
                                   final String[] items) {
        }
    };

    private View.OnClickListener cancelClicked = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            disableInputs();
            progressDialog.show();

            final User user = userManager.getCurrentUser();

            dataManager.cancelBooking(booking.getId(), optionIndex,  user.getId(), user.getAuthToken(),
                    new DataManager.Callback<String>() {
                @Override
                public void onSuccess(final String message) {
                    if (!allowCallbacks) return;
                    enableInputs();
                    progressDialog.dismiss();

                    if (message != null && !message.isEmpty()) {
                        toast.setText(message);
                        toast.show();
                    }

                    getActivity().setResult(BookingCancelOptionsActivity.RESULT_BOOKING_CANCELED, new Intent());
                    getActivity().finish();
                }

                @Override
                public void onError(final DataManager.DataManagerError error) {
                    if (!allowCallbacks) return;
                    enableInputs();
                    progressDialog.dismiss();
                    dataManagerErrorHandler.handleError(getActivity(), error);
                }
            });
        }
    };
}
