package com.handybook.handybook;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import net.simonvt.menudrawer.MenuDrawer;

import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;

public final class BookingDetailFragment extends InjectedFragment {

    static BookingDetailFragment newInstance() {
        return new BookingDetailFragment();
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_booking_detail, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public final void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final MenuDrawerActivity activity = (MenuDrawerActivity) getActivity();
        final MenuDrawer menuDrawer = activity.getMenuDrawer();
        menuDrawer.setOnInterceptMoveEventListener(new MenuDrawer.OnInterceptMoveEventListener() {
            @Override
            public boolean isViewDraggable(final View view, final int i, final int i2, final int i3) {
                return true;
            }
        });
    }
}
