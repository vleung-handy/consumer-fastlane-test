package com.handybook.handybook;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;

public final class BookingsFragment extends InjectedListFragment {
    private ProgressDialog progressDialog;
    private Toast toast;
    private final ArrayList<String> upBookings = new ArrayList<>();
    private final ArrayList<String> pastBookings = new ArrayList<>();

    @Inject UserManager userManager;
    @Inject DataManager dataManager;
    @Inject DataManagerErrorHandler dataManagerErrorHandler;

    static BookingsFragment newInstance() {
        return new BookingsFragment();
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toast = Toast.makeText(getActivity(), null, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_bookings, container, false);
        ButterKnife.inject(this, view);
        setListAdapter(new BookingsListAdapter());

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setDelay(500);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));

        upBookings.add("");
        upBookings.add("1");
        pastBookings.add("2");
        pastBookings.add("3");

        return view;
    }

    @Override
    public final void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final PinnedSectionListView listView = (PinnedSectionListView)getListView();
        listView.setShadowVisible(false);
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
                int offset = (upBookings.size() > 0 ? upBookings.size() + 1 : 2)
                        + (pastBookings.size() > 0 ? 1 : 0);

                if (position >= offset) return pastBookings.get(position - offset);
                else return upBookings.get(position - 1);
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
                        .inflate(R.layout.list_item_no_booking, null);
            }
            else if (position == 0 || position == (upBookings.size() > 0 ? upBookings.size() + 1 : 2)) {
                String header = (String)getItem(position);
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.list_item_booking_header, null);
                TextView textView = (TextView)convertView.findViewById(R.id.title);
                textView.setText(header);
            }
            else {
                String booking = (String)getItem(position);
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.list_item_booking, null);


                // remove cell separator from final item in list
                View layout = convertView.findViewById(R.id.cell_layout);
                int offset = (upBookings.size() > 0 ? upBookings.size() + 1 : 2)
                        + (pastBookings.size() > 0 ? 1 : 0);

                if ((position >= offset && booking.equals(pastBookings.get(pastBookings.size() - 1)))
                        || (position < offset && booking.equals(upBookings.get(upBookings.size() - 1)))) {
                    layout.setBackgroundColor(getResources().getColor(R.color.white));
                }
            }
            return convertView;
        }
    }
}
