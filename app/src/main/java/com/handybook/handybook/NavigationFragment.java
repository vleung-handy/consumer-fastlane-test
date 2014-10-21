package com.handybook.handybook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.simplealertdialog.SimpleAlertDialog;
import com.simplealertdialog.SimpleAlertDialogSupportFragment;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import net.simonvt.menudrawer.MenuDrawer;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class NavigationFragment extends InjectedListFragment
        implements SimpleAlertDialog.OnClickListener, SimpleAlertDialog.OnItemClickListener {
    static final String ARG_SELECTED_ITEM = "com.handybook.handybook.ARG_SELECTED_ITEM";
    static final int REQUEST_LOGOUT = 1;
    static final int REQUEST_ENV = 2;

    private final ArrayList<String> items = new ArrayList<String>();
    private final ArrayList<String> envs = new ArrayList<String>();
    private String selectedItem;
    private MenuDrawer menuDrawer;

    @InjectView(R.id.env_button) Button envButton;

    @Inject UserManager userManager;
    @Inject DataManager dataManager;
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

        final Bundle args;
        if ((args = getArguments()) != null) {
            selectedItem = args.getString(ARG_SELECTED_ITEM);
        }

        for (DataManager.Environment env : DataManager.Environment.values()) {
            if (env != DataManager.Environment.P) envs.add(env.toString());
        }
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_navigation, container, false);
        ButterKnife.inject(this, view);

        if (BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_PROD))
            envButton.setVisibility(View.GONE);

        envButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SimpleAlertDialogSupportFragment.Builder()
                        .setTitle("Select Environment")
                        .setItems(envs.toArray(new String[envs.size()]))
                        .setRequestCode(REQUEST_ENV)
                        .setTargetFragment(NavigationFragment.this)
                        .create().show(getActivity().getSupportFragmentManager(), "dialog");
            }
        });

        loadNavItems();
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
        else if (item.equals(getString(R.string.my_bookings))
                && !(getString(R.string.my_bookings).equals(selectedItem))) {
            activity.navigateToActivity(BookingsActivity.class);
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
            new SimpleAlertDialogSupportFragment.Builder()
                    .setMessage(getString(R.string.want_to_log_out))
                    .setPositiveButton(R.string.log_out)
                    .setNegativeButton(android.R.string.cancel)
                    .setRequestCode(REQUEST_LOGOUT)
                    .setTargetFragment(NavigationFragment.this)
                    .create().show(getActivity().getSupportFragmentManager(), "dialog");
        }
        else menuDrawer.closeMenu();
    }

    @Override
    public final void onDialogPositiveButtonClicked(final SimpleAlertDialog dialog,
                                              final int requestCode, final View view) {
        if (requestCode == REQUEST_LOGOUT) {
            userManager.setCurrentUser(null);
        }
    }

    @Override
    public final void onDialogNegativeButtonClicked(final SimpleAlertDialog dialog,
                                              final int requestCode, final View view) {}

    @Override
    public final void onItemClick(final SimpleAlertDialog dialog, final int requestCode, final int which) {
        if (requestCode == REQUEST_ENV) {
            dataManager.setEnvironment(DataManager.Environment.valueOf(envs.get(which)));
        }
    }

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

    @Subscribe
    public final void envUpdated(final EnvironmentUpdatedEvent event) {
        loadNavItems();
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

        envButton.setText(String.format(getString(R.string.env_format), dataManager.getEnvironment(),
                BuildConfig.VERSION_NAME, Integer.valueOf(BuildConfig.VERSION_CODE).toString()));
    }
}
