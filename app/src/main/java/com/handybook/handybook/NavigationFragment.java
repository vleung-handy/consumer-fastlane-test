package com.handybook.handybook;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public final class NavigationFragment extends ListFragment {

    static NavigationFragment newInstance() {
        return new NavigationFragment();
    }

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container,
                                   Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_navigation, container, false);
        return view;
    }

    @Override
    public final void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getListView().setAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, getNavItems()));
    }

    @Override
    public final void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        final Intent intent = new Intent(getActivity(), ServiceCategoriesActivity.class);
        intent.putExtra(MenuDrawerActivity.EXTRA_NAV_CHANGE, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().overridePendingTransition(0, 0);
    }

    private final String[] getNavItems() {
        return new String[]{"Navigation 1", "Navigation 1", "Navigation 1"};
    }
}
