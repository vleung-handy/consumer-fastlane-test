package com.handybook.handybook;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import net.simonvt.menudrawer.MenuDrawer;

import java.util.ArrayList;

public final class NavigationFragment extends ListFragment {
    static final String ARG_SELECTED_ITEM = "com.handybook.handybook.ARG_SELECTED_ITEM";

    private final ArrayList<String> items = new ArrayList<String>();
    private String selectedItem;
    private MenuDrawer menuDrawer;

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
        else menuDrawer.closeMenu();
    }

    private void loadNavItems() {
        items.clear();
        items.add(getString(R.string.home));
        items.add(getString(R.string.help));
        items.add(getString(R.string.promotions));
        items.add(getString(R.string.log_in));
    }
}
