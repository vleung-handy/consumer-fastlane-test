package com.handybook.handybook.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.core.LaundryDropInfo;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.util.TextUtils;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LaundryDropOffDialogFragment extends BaseDialogFragment {
    static final String EXTRA_BOOKING = "com.handy.handy.EXTRA_BOOKING";
    static final String EXTRA_DROP_INFO = "com.handy.handy.EXTRA_DROP_INFO";

    private int booking;
    private LaundryDropInfo dropInfo;

    @Inject DataManager dataManager;

    @InjectView(R.id.title_text) TextView titleText;
    @InjectView(R.id.message_text) TextView messageText;
    @InjectView(R.id.date_spinner) Spinner dateSpinner;
    @InjectView(R.id.time_spinner) Spinner timeSpinner;
    @InjectView(R.id.submit_button) Button submitButton;
    @InjectView(R.id.submit_progress) ProgressBar submitProgress;

    public static LaundryDropOffDialogFragment newInstance(final int bookingId,
                                                           final LaundryDropInfo dropInfo) {
        final LaundryDropOffDialogFragment laundryDropOffDialogFragment
                = new LaundryDropOffDialogFragment();

        final Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_BOOKING, bookingId);
        bundle.putParcelable(EXTRA_DROP_INFO, dropInfo);

        laundryDropOffDialogFragment.setArguments(bundle);
        return laundryDropOffDialogFragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle args = getArguments();
        booking = args.getInt(EXTRA_BOOKING);
        dropInfo = args.getParcelable(EXTRA_DROP_INFO);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final View view = inflater.inflate(R.layout.dialog_laundry_drop_off, container, true);
        ButterKnife.inject(this, view);

        titleText.setText(dropInfo.getTitle());
        messageText.setText(dropInfo.getSubtitle());

        final List<Date> dropDates = dropInfo.getDates();
        dateSpinner.setAdapter(new DropDateAdapter(this.getActivity(), dropDates));

        timeSpinner.setAdapter(new DropTimeAdapter(this.getActivity(),
                dropInfo.getDropTimes(dropDates.get(0))));

        // update avail times by date selected
        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                timeSpinner.setAdapter(new DropTimeAdapter(LaundryDropOffDialogFragment.this.getActivity(),
                        dropInfo.getDropTimes(dropDates.get(position))));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        submitButton.setOnClickListener(submitListener);
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

    private View.OnClickListener submitListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            dismiss();
//            disableInputs();
//            submitProgress.setVisibility(View.VISIBLE);
//            submitButton.setText(null);
//
//            dataManager.ratePro(booking, finalRating, new DataManager.Callback<Void>() {
//                @Override
//                public void onSuccess(final Void response) {
//                    if (!allowCallbacks) return;
//                    dismiss();
//
//                    mixpanel.trackEventProRate(Mixpanel.ProRateEventType.SUBMIT, booking,
//                            proName, finalRating);
//
//                    RateServiceConfirmDialogFragment.newInstance(booking, finalRating).show(getActivity()
//                                .getSupportFragmentManager(), "RateServiceConfirmDialogFragment");
//                }
//
//                @Override
//                public void onError(final DataManager.DataManagerError error) {
//                    if (!allowCallbacks) return;
//                    submitProgress.setVisibility(View.GONE);
//                    submitButton.setText(R.string.submit);
//                    skipButton.setVisibility(View.VISIBLE);
//                    enableInputs();
//                    dataManagerErrorHandler.handleError(getActivity(), error);
//                }
//            });
        }
    };

    private static class DropDateAdapter extends ArrayAdapter<Date> {
        final Context context;
        final List<Date> dates;

        DropDateAdapter(final Context context, final List<Date> dates) {
            super(context, android.R.layout.simple_spinner_item, dates);
            this.context = context;
            this.dates = dates;
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            return buildRow(position, convertView, parent);
        }

        @Override
        public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
            return buildRow(position, convertView, parent);
        }

        private View buildRow(final int position, final View convertView, final ViewGroup parent) {
            TextView row = (TextView)convertView;

            if (row == null) {
                final LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                row = (TextView)inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
            }

            row.setText(TextUtils.formatDate(dates.get(position), "MM/dd/yy"));
            return row;
        }
    }

    private static class DropTimeAdapter extends ArrayAdapter<LaundryDropInfo.DropTime> {
        final Context context;
        final List<LaundryDropInfo.DropTime> times;

        DropTimeAdapter(final Context context, final List<LaundryDropInfo.DropTime> times) {
            super(context, android.R.layout.simple_spinner_item, times);
            this.context = context;
            this.times = times;
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            return buildRow(position, convertView, parent);
        }

        @Override
        public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
            return buildRow(position, convertView, parent);
        }

        private View buildRow(final int position, final View convertView, final ViewGroup parent) {
            TextView row = (TextView)convertView;

            if (row == null) {
                final LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                row = (TextView)inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
            }

            row.setText(times.get(position).getDisplayTime());
            return row;
        }
    }
}
