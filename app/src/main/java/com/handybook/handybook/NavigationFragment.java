package com.handybook.handybook;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.simplealertdialog.SimpleAlertDialog;
import com.simplealertdialog.SimpleAlertDialogFragment;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import net.simonvt.menudrawer.MenuDrawer;

import java.util.ArrayList;

import javax.inject.Inject;

public final class NavigationFragment extends ListFragment implements SimpleAlertDialog.OnClickListener {
    static final String ARG_SELECTED_ITEM = "com.handybook.handybook.ARG_SELECTED_ITEM";

    private final ArrayList<String> items = new ArrayList<String>();
    private String selectedItem;
    private MenuDrawer menuDrawer;

    @Inject UserManager userManager;
    @Inject Bus bus;

    static NavigationFragment newInstance(final String selectedItem) {
        final Bundle args = new Bundle();
        args.putString(ARG_SELECTED_ITEM, selectedItem);

        final NavigationFragment fragment = new NavigationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BaseApplication)getActivity().getApplication()).inject(this);

        loadNavItems();

        final Bundle args;
        if ((args = getArguments()) != null) {
            selectedItem = args.getString(ARG_SELECTED_ITEM);
        }
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_navigation, container, false);
        return view;
    }

    @Override
    public final void onResume() {
        super.onResume();
        loadNavItems();
        bus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Override
    public final void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final MenuDrawerActivity activity = (MenuDrawerActivity)getActivity();
        menuDrawer = activity.getMenuDrawer();

        getListView().setAdapter(new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, items));
    }

    @Override
    public final void onListItemClick(final ListView l, final View v, final int position, final long id) {
        super.onListItemClick(l, v, position, id);

        final TextView textView = (TextView)v;
        final String item = textView.getText().toString();
        final MenuDrawerActivity activity = (MenuDrawerActivity)getActivity();

        if (item.equals(getString(R.string.home))
                && !(getString(R.string.home).equals(selectedItem))) {
            activity.navigateToActivity(ServiceCategoriesActivity.class);
        }
        else if (item.equals(getString(R.string.profile))
                && !(getString(R.string.profile).equals(selectedItem))) {
            activity.navigateToActivity(ProfileActivity.class);
        }
        else if (item.equals(getString(R.string.help))
                && !(getString(R.string.help).equals(selectedItem))) {

        }
        else if (item.equals(getString(R.string.promotions))
                && !(getString(R.string.promotions).equals(selectedItem))) {

        }
        else if (item.equals(getString(R.string.log_in))
                && !(getString(R.string.log_in).equals(selectedItem))) {
            activity.navigateToActivity(LoginActivity.class);
        }
        else if (item.equals(getString(R.string.log_out))) {
            new SimpleAlertDialogFragment.Builder()
                    .setMessage(getString(R.string.want_to_log_out))
                    .setPositiveButton(R.string.log_out)
                    .setNegativeButton(android.R.string.cancel)
                    .setRequestCode(1)
                    .create().show(getActivity().getFragmentManager(), "dialog");
        }
        else menuDrawer.closeMenu();
    }

    @Override
    public final void onDialogPositiveButtonClicked(final SimpleAlertDialog dialog,
                                              final int requestCode, final View view) {
        if (requestCode == 1) {
            userManager.setCurrentUser(null);
        }
    }

    @Override
    public final void onDialogNegativeButtonClicked(final SimpleAlertDialog dialog,
                                              final int requestCode, final View view) {}

    @Subscribe
    public final void userAuthUpdated(final UserLoggedInEvent event) {
        loadNavItems();
        BaseAdapter adapter = (BaseAdapter)getListView().getAdapter();
        adapter.notifyDataSetChanged();

        if (!event.isLoggedIn()) {
            final MenuDrawerActivity activity = (MenuDrawerActivity)getActivity();
            activity.navigateToActivity(ServiceCategoriesActivity.class);
        }
    }

    private void loadNavItems() {
        final boolean userLoggedIn = userManager.getCurrentUser() != null;

        items.clear();
        items.add(getString(R.string.home));

        if (userLoggedIn) {
            items.add(getString(R.string.profile));
            items.add(getString(R.string.my_bookings));
        }

        items.add(getString(R.string.help));

        if (userLoggedIn) items.add(getString(R.string.share));

        items.add(getString(R.string.promotions));

        if (userManager.getCurrentUser() != null) items.add(getString(R.string.log_out));
        else items.add(getString(R.string.log_in));
    }
}
