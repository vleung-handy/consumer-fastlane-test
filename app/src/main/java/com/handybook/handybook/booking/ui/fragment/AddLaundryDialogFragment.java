package com.handybook.handybook.booking.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.core.User;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.analytics.Mixpanel;
import com.handybook.handybook.booking.ui.activity.BookingsActivity;
import com.handybook.handybook.ui.fragment.BaseDialogFragment;
import com.handybook.handybook.util.TextUtils;

import java.util.Calendar;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AddLaundryDialogFragment extends BaseDialogFragment
{
    static final String EXTRA_BOOKING = "com.handy.handy.EXTRA_BOOKING";

    private Booking booking;

    @Inject DataManager dataManager;

    @Bind(R.id.submit_button)
    Button submitButton;
    @Bind(R.id.close_img)
    ImageView closeImage;
    @Bind(R.id.submit_progress)
    ProgressBar submitProgress;
    @Bind(R.id.booking_info)
    TextView bookingInfo;

    public static AddLaundryDialogFragment newInstance(final Booking booking) {
        final AddLaundryDialogFragment addLaundryDialogFragment
                = new AddLaundryDialogFragment();

        final Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_BOOKING, booking);

        addLaundryDialogFragment.setArguments(bundle);
        return addLaundryDialogFragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle args = getArguments();
        booking = args.getParcelable(EXTRA_BOOKING);
        mixpanel.trackPageAddLaundryConfirm(Mixpanel.LaundryEventSource.APP_OPEN);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final View view = inflater.inflate(R.layout.dialog_laundry_add, container, true);
        ButterKnife.bind(this, view);

        final Calendar endDate = Calendar.getInstance();
        endDate.setTime(booking.getStartDate());
        endDate.add(Calendar.MINUTE, (int)(60 * booking.getHours()));

        bookingInfo.setText(TextUtils.formatDate(booking.getStartDate(),
                "EEEE',' MMM d',' yyyy\nh:mmaaa - ")
                + TextUtils.formatDate(endDate.getTime(), "h:mmaaa"));

        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                dismiss();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                disableInputs();
                submitProgress.setVisibility(View.VISIBLE);
                submitButton.setText(null);

                final User user = userManager.getCurrentUser();
                dataManager.addLaundry(Integer.parseInt(booking.getId()), user.getAuthToken(),
                        new DataManager.Callback<Void>() {
                    @Override
                    public void onSuccess(final Void response) {
                        mixpanel.trackEventLaundryAdded(Mixpanel.LaundryEventSource.APP_OPEN);

                        if (!allowCallbacks) return;
                        dismiss();

                        final Intent intent = new Intent(getActivity(), BookingsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error) {
                        if (!allowCallbacks) return;
                        submitProgress.setVisibility(View.GONE);
                        submitButton.setText(R.string.add_laundry);
                        enableInputs();
                        dataManagerErrorHandler.handleError(getActivity(), error);
                    }
                });
            }
        });

        return view;
    }

    @Override
    protected void enableInputs() {
        super.enableInputs();
        submitButton.setClickable(true);
    }

    @Override
    protected void disableInputs() {
        super.disableInputs();
        submitButton.setClickable(false);
    }
}
