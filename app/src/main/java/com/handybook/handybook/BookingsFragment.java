package com.handybook.handybook;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;

public final class BookingsFragment extends InjectedListFragment {
    private static final String STATE_LOADED_BOOKINGS = "LOADED_BOOKINGS";
    private static final String STATE_PAST_BOOKINGS = "STATE_PAST_BOOKINGS";
    private static final String STATE_UP_BOOKINGS = "STATE_UP_BOOKINGS";

    private boolean loadedBookings;
    private ProgressDialog progressDialog;
    private ArrayList<Booking> upBookings = new ArrayList<>();
    private ArrayList<Booking> pastBookings = new ArrayList<>();
    private SimpleDateFormat dateFormat, timeFormat;
    private DecimalFormat hoursFormat;

    @Inject UserManager userManager;
    @Inject DataManager dataManager;
    @Inject DataManagerErrorHandler dataManagerErrorHandler;

    static BookingsFragment newInstance() {
        return new BookingsFragment();
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dateFormat = new SimpleDateFormat("EEEE',' MMMM d");

        timeFormat = new SimpleDateFormat("h:mm aaa");
        final DateFormatSymbols symbols = new DateFormatSymbols();
        symbols.setAmPmStrings(new String[] { "am", "pm" });
        timeFormat.setDateFormatSymbols(symbols);

        hoursFormat = new DecimalFormat("#.#");
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_bookings,container, false);

        ButterKnife.inject(this, view);
        setListAdapter(new BookingsListAdapter());

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setDelay(500);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));

        return view;
    }

    @Override
    public final void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final PinnedSectionListView listView = (PinnedSectionListView)getListView();
        listView.setShadowVisible(false);

        if (savedInstanceState != null) {
            loadedBookings = savedInstanceState.getBoolean(STATE_LOADED_BOOKINGS);
            pastBookings = savedInstanceState.getParcelableArrayList(STATE_PAST_BOOKINGS);
            upBookings = savedInstanceState.getParcelableArrayList(STATE_UP_BOOKINGS);
        }

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, final View view,
                                    final int i, final long l) {
                final Intent intent = new Intent(getActivity(), BookingDetailActivity.class);
                intent.putExtra(BookingDetailActivity.EXTRA_BOOKING, getBooking(i));
                startActivity(intent);
            }
        });
    }

    @Override
    public final void onStart() {
        super.onStart();
        if (!loadedBookings) loadBookings();
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_LOADED_BOOKINGS, loadedBookings);
        outState.putParcelableArrayList(STATE_PAST_BOOKINGS, pastBookings);
        outState.putParcelableArrayList(STATE_UP_BOOKINGS, upBookings);
    }

    private void loadBookings() {
        progressDialog.show();
        dataManager.getBookings(userManager.getCurrentUser(), new DataManager.Callback<List<Booking>>() {
            @Override
            public void onSuccess(final List<Booking> bookings) {
                if (!allowCallbacks) return;
                pastBookings.clear();
                upBookings.clear();

                for (Booking booking : bookings) {
                    if (booking.isPast()) pastBookings.add(booking);
                    else upBookings.add(booking);
                }

                final BaseAdapter adapter = (BaseAdapter)getListView().getAdapter();
                adapter.notifyDataSetChanged();

                loadedBookings = true;
                progressDialog.dismiss();
            }

            @Override
            public void onError(final DataManager.DataManagerError error) {
                if (!allowCallbacks) return;
                loadedBookings = true;
                progressDialog.dismiss();
                dataManagerErrorHandler.handleError(getActivity(), error);
            }
        });
    }

    private final class BookingsListAdapter extends BaseAdapter
            implements PinnedSectionListView.PinnedSectionListAdapter {

        @Override
        public final int getCount() {
            if (upBookings.size() < 1 && pastBookings.size() < 1) return 2;
            else if (upBookings.size() < 1 && pastBookings.size() > 0) return 3 + pastBookings.size();
            return upBookings.size() + 1 + (pastBookings.size() > 0 ? pastBookings.size() + 1 : 0);
        }

        @Override
        public final Object getItem(final int position) {
            if (position == 0) {
                return getString(R.string.upcoming).toUpperCase();
            }
            else if (position == (upBookings.size() > 0 ? upBookings.size() + 1 : 2)) {
                return getActivity().getString(R.string.past).toUpperCase();
            }
            else {
                return getBooking(position);
            }
        }

        @Override
        public final long getItemId(final int position) {
            return position;
        }

        @Override
        public final int getViewTypeCount() {
            return 2;
        }

        @Override
        public final int getItemViewType(final int position) {
            if (position == 0 || (upBookings.size() > 0 && position == upBookings.size() + 1)
                    || upBookings.size() < 1 && position == upBookings.size() + 2) return 0;
            return 1;
        }

        @Override
        public final boolean isEnabled(final int position) {
            return !(position == 0 || (upBookings.size() > 0 && position == upBookings.size() + 1)
                    || (upBookings.size() < 1 && position == upBookings.size() + 2)
                    || (upBookings.size() < 1 && position == upBookings.size() + 1));
        }

        @Override
        public final boolean isItemViewTypePinned(final int viewType) {
            return viewType == 0;
        }

        @Override
        public final View getView(final int position, View convertView, final ViewGroup parent) {
            if (upBookings.size() < 1 && position == 1) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.list_item_no_booking, parent, false);
            }
            else if (position == 0 || position == (upBookings.size() > 0 ? upBookings.size() + 1 : 2)) {
                String header = (String)getItem(position);
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.list_item_booking_header, parent, false);
                TextView textView = (TextView)convertView.findViewById(R.id.title);
                textView.setText(header);
            }
            else {
                final Booking booking = (Booking)getItem(position);
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.list_item_booking, parent, false);
                configureCell(convertView, booking);

                // remove cell separator from final item in list
                final View layout = convertView.findViewById(R.id.cell_layout);
                int offset = (upBookings.size() > 0 ? upBookings.size() + 1 : 2)
                        + (pastBookings.size() > 0 ? 1 : 0);

                if ((position >= offset && booking == pastBookings.get(pastBookings.size() - 1))
                        || (position < offset && booking == upBookings.get(upBookings.size() - 1))) {
                    final int paddingBottom = layout.getPaddingBottom(),
                            paddingLeft = layout.getPaddingLeft(),
                            paddingRight = layout.getPaddingRight(),
                            paddingTop = layout.getPaddingTop();

                    layout.setBackgroundResource((R.drawable.booking_cell_last));
                    layout.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
                }
            }
            return convertView;
        }
    }

    private Booking getBooking(final int position) {
        final int offset = (upBookings.size() > 0 ? upBookings.size() + 1 : 2)
                + (pastBookings.size() > 0 ? 1 : 0);

        if (position >= offset) return pastBookings.get(position - offset);
        else return upBookings.get(position - 1);
    }

    private void configureCell(final View cell, final Booking booking) {
        final TextView serviceText = (TextView)cell.findViewById(R.id.service);
        serviceText.setText(booking.getService());

        final TextView dateText = (TextView)cell.findViewById(R.id.date);
        dateText.setText(dateFormat.format(booking.getStartDate()));

        final TextView timeText = (TextView)cell.findViewById(R.id.time);
        timeText.setText(timeFormat.format(booking.getStartDate()) + " - "
                + hoursFormat.format(booking.getHours()) + " " + getString(R.string.hours));
    }
}
