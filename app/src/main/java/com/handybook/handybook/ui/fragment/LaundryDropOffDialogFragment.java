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
import com.handybook.handybook.core.User;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.data.Mixpanel;
import com.handybook.handybook.util.TextUtils;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LaundryDropOffDialogFragment extends BaseDialogFragment {
    static final String EXTRA_BOOKING = "com.handy.handy.EXTRA_BOOKING";
    static final String EXTRA_DROP_INFO = "com.handy.handy.EXTRA_DROP_INFO";

    private int booking;
    private LaundryDropInfo dropInfo;

    @Inject DataManager dataManager;

    @Bind(R.id.title_text)
    TextView titleText;
    @Bind(R.id.message_text)
    TextView messageText;
    @Bind(R.id.date_spinner)
    Spinner dateSpinner;
    @Bind(R.id.time_spinner)
    Spinner timeSpinner;
    @Bind(R.id.submit_button)
    Button submitButton;
    @Bind(R.id.submit_progress)
    ProgressBar submitProgress;

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

        mixpanel.trackPageScheduleLaundry(Mixpanel.LaundryEventSource.APP_OPEN, dropInfo.getType());
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final View view = inflater.inflate(R.layout.dialog_laundry_drop_off, container, true);
        ButterKnife.bind(this, view);

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
            disableInputs();
            submitProgress.setVisibility(View.VISIBLE);
            submitButton.setText(null);

            final User user = userManager.getCurrentUser();
            final String date = TextUtils.formatDate((Date) dateSpinner.getSelectedItem(), "dd/MM/yyyy");

            final LaundryDropInfo.DropTime dropTime
                    = (LaundryDropInfo.DropTime)timeSpinner.getSelectedItem();

            dataManager.setLaundryDropOff(booking, user.getAuthToken(), date, dropTime.getHour(),
                    dropTime.getMinute(), dropInfo.getType(), new DataManager.Callback<Void>() {
                @Override
                public void onSuccess(final Void response) {
                    mixpanel.trackEventLaundryScheduled(Mixpanel.LaundryEventSource.APP_OPEN, dropInfo.getType());

                    if (!allowCallbacks) return;
                    dismiss();
                }

                @Override
                public void onError(final DataManager.DataManagerError error) {
                    if (!allowCallbacks) return;
                    submitProgress.setVisibility(View.GONE);
                    submitButton.setText(R.string.submit);
                    enableInputs();
                    dataManagerErrorHandler.handleError(getActivity(), error);
                }
            });
        }
    };

    private static class DropDateAdapter extends ArrayAdapter<Date> {
        final Context context;
        final List<Date> dates;

        DropDateAdapter(final Context context, final List<Date> dates) {
            super(context, R.layout.list_item_drop_down, dates);
            this.context = context;
            this.dates = dates;
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            return buildRow(position, convertView, parent, false);
        }

        @Override
        public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
            return buildRow(position, convertView, parent, true);
        }

        private View buildRow(final int position, final View convertView, final ViewGroup parent,
                              final boolean isDropDown) {
            final LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            View row = convertView;
            TextView textView;

            if (!isDropDown) {
                if (row == null) {
                    row = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
                }

                textView = (TextView)row;
                textView.setTextColor(context.getResources().getColor(R.color.handy_text_black));
            }
            else {
                if (row == null) {
                    row = inflater.inflate(R.layout.list_item_drop_down, parent, false);
                }

                textView = (TextView)row.findViewById(R.id.text);
            }

            textView.setText(TextUtils.formatDate(dates.get(position), "MM/dd/yy"));
            return row;
        }
    }

    private static class DropTimeAdapter extends ArrayAdapter<LaundryDropInfo.DropTime> {
        final Context context;
        final List<LaundryDropInfo.DropTime> times;

        DropTimeAdapter(final Context context, final List<LaundryDropInfo.DropTime> times) {
            super(context, R.layout.list_item_drop_down, times);
            this.context = context;
            this.times = times;
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            return buildRow(position, convertView, parent, false);
        }

        @Override
        public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
            return buildRow(position, convertView, parent, true);
        }

        private View buildRow(final int position, final View convertView, final ViewGroup parent,
                              final boolean isDropDown) {

            final LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            View row = convertView;
            TextView textView;

            if (!isDropDown) {
                if (row == null) {
                    row = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
                }

                textView = (TextView)row;
                textView.setTextColor(context.getResources().getColor(R.color.handy_text_black));
            }
            else {
                if (row == null) {
                    row = inflater.inflate(R.layout.list_item_drop_down, parent, false);
                }

                textView = (TextView)row.findViewById(R.id.text);
            }

            textView.setText(times.get(position).getDisplayTime());
            return row;
        }
    }
}
