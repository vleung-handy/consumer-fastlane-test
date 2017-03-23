package com.handybook.handybook.ratingflow.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.rating.ReviewProRequest;
import com.handybook.handybook.core.BaseApplication;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.VoidDataManagerCallback;
import com.handybook.handybook.library.ui.fragment.SlideUpDialogFragment;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;

public class RatingFlowHelpDialogFragment extends SlideUpDialogFragment {

    public static final String TAG = RatingFlowHelpDialogFragment.class.getName();

    @Inject
    DataManager mDataManager;

    @Bind(R.id.rating_flow_help_text)
    EditText mHelpTextField;

    private Booking mBooking;

    public static RatingFlowHelpDialogFragment newInstance(@Nonnull final Booking booking) {
        RatingFlowHelpDialogFragment fragment = new RatingFlowHelpDialogFragment();
        final Bundle arguments = new Bundle();
        arguments.putParcelable(BundleKeys.BOOKING, booking);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BaseApplication) getActivity().getApplication()).inject(this);
        mBooking = getArguments().getParcelable(BundleKeys.BOOKING);
    }

    @NonNull
    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState
    ) {
        final View view = super.onCreateView(inflater, container, savedInstanceState);
        getDialog().getWindow()
                   .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return view;
    }

    @Override
    protected View inflateContentView(
            final LayoutInflater inflater, final ViewGroup container
    ) {
        return inflater.inflate(R.layout.fragment_rating_flow_help, container, false);
    }

    @OnClick(R.id.rating_flow_help_submit)
    void onSubmit() {
        final String message = mHelpTextField.getText().toString();
        if (!Strings.isNullOrEmpty(message)) {
            mDataManager.submitProRatingDetails(
                    Integer.parseInt(mBooking.getId()),
                    new ReviewProRequest(null, message),
                    new VoidDataManagerCallback()
            );
            Toast.makeText(
                    getActivity(),
                    R.string.rating_flow_post_feedback_message,
                    Toast.LENGTH_LONG
            ).show();
        }
        dismiss();
    }
}
