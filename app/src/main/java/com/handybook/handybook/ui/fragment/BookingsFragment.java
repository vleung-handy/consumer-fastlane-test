package com.handybook.handybook.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.ui.activity.BookingDetailActivity;
import com.handybook.handybook.ui.widget.MenuButton;
import com.handybook.handybook.ui.widget.PinnedSectionListView;
import com.handybook.handybook.util.TextUtils;
import com.handybook.handybook.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class BookingsFragment extends InjectedFragment {
    private static final String STATE_LOADED_BOOKINGS = "LOADED_BOOKINGS";
    private static final String STATE_PAST_BOOKINGS = "STATE_PAST_BOOKINGS";
    private static final String STATE_UP_BOOKINGS = "STATE_UP_BOOKINGS";

    private boolean loadedBookings;
    private ArrayList<Booking> upBookings = new ArrayList<>();
    private ArrayList<Booking> pastBookings = new ArrayList<>();

    @InjectView(R.id.menu_button_layout) ViewGroup menuButtonLayout;
    @InjectView(android.R.id.list) PinnedSectionListView listView;

    public static BookingsFragment newInstance() {
        return new BookingsFragment();
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_bookings,container, false);

        ButterKnife.inject(this, view);

        listView.setAdapter(new BookingsListAdapter());

        final MenuButton menuButton = new MenuButton(getActivity());
        menuButton.setColor(getResources().getColor(R.color.black_pressed));
        Utils.extendHitArea(menuButton, menuButtonLayout, Utils.toDP(32, getActivity()));
        menuButtonLayout.addView(menuButton);

        return view;
    }

    @Override
    public final void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView.setShadowVisible(false);

        if (savedInstanceState != null) {
            loadedBookings = savedInstanceState.getBoolean(STATE_LOADED_BOOKINGS);
            pastBookings = savedInstanceState.getParcelableArrayList(STATE_PAST_BOOKINGS);
            upBookings = savedInstanceState.getParcelableArrayList(STATE_UP_BOOKINGS);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, final View view,
                                    final int i, final long l) {
                final Intent intent = new Intent(getActivity(), BookingDetailActivity.class);
                intent.putExtra(BookingDetailActivity.EXTRA_BOOKING, getBooking(i));
                startActivityForResult(intent, BookingDetailActivity.RESULT_BOOKING_UPDATED);
            }
        });
    }

    @Override
    public final void onStart() {
        super.onStart();
        if (!loadedBookings) loadBookings();
    }

    @Override
    public final void onStop() {
        super.onStop();
        progressDialog.dismiss();
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_LOADED_BOOKINGS, loadedBookings);
        outState.putParcelableArrayList(STATE_PAST_BOOKINGS, pastBookings);
        outState.putParcelableArrayList(STATE_UP_BOOKINGS, upBookings);
    }

    @Override
    public final void onActivityResult(final int requestCode, final int resultCode,
                                       final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == BookingDetailActivity.RESULT_BOOKING_UPDATED
                || resultCode == BookingDetailActivity.RESULT_BOOKING_CANCELED) {

            final boolean isCancel = resultCode == BookingDetailActivity.RESULT_BOOKING_CANCELED;
            final Booking booking;

            if (isCancel) {
                booking = data.getParcelableExtra(BookingDetailActivity.EXTRA_CANCELED_BOOKING);
            }
            else {
                booking = data.getParcelableExtra(BookingDetailActivity.EXTRA_UPDATED_BOOKING);
            }

            final String bookingId = booking.getId();

            for (int i = 0; i < upBookings.size(); i++) {
                final Booking upBooking = upBookings.get(i);

                if (upBooking.getId().equals(bookingId)) {
                    if (isCancel) upBookings.remove(i);
                    else upBookings.set(i, booking);

                    Collections.sort(upBookings, new Comparator<Booking>() {
                        @Override
                        public int compare(Booking lhs, Booking rhs) {
                            return lhs.getStartDate().compareTo(rhs.getStartDate());
                        }
                    });

                    final BaseAdapter adapter = (BaseAdapter)listView.getAdapter();
                    adapter.notifyDataSetChanged();
                    listView.setSelection(0);
                    break;
                }
            }

            // reloading all bookings here until there is a way to update recurring instances as well
            loadBookings();
        }
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

                final BaseAdapter adapter = (BaseAdapter)listView.getAdapter();
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

                    layout.setBackgroundResource((R.drawable.cell_booking_last));
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
        dateText.setText(TextUtils.formatDate(booking.getStartDate(), "EEEE',' MMMM d"));

        final TextView timeText = (TextView)cell.findViewById(R.id.time);
        timeText.setText(TextUtils.formatDate(booking.getStartDate(), "h:mm aaa") + " - "
                + TextUtils.formatDecimal(booking.getHours(), "#.#") + " " + getString(R.string.hours));
    }
}